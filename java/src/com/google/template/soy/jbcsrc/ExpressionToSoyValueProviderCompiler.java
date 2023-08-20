/*
 * Copyright 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.template.soy.jbcsrc;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.template.soy.jbcsrc.restricted.BytecodeUtils.isDefinitelyAssignableFrom;

import com.google.template.soy.data.SoyValueProvider;
import com.google.template.soy.exprtree.DataAccessNode;
import com.google.template.soy.exprtree.ExprNode;
import com.google.template.soy.exprtree.ExprRootNode;
import com.google.template.soy.exprtree.NullNode;
import com.google.template.soy.exprtree.OperatorNodes.ConditionalOpNode;
import com.google.template.soy.exprtree.OperatorNodes.NullCoalescingOpNode;
import com.google.template.soy.exprtree.VarRefNode;
import com.google.template.soy.jbcsrc.ExpressionCompiler.BasicExpressionCompiler;
import com.google.template.soy.jbcsrc.restricted.Branch;
import com.google.template.soy.jbcsrc.restricted.BytecodeUtils;
import com.google.template.soy.jbcsrc.restricted.Expression;
import com.google.template.soy.jbcsrc.restricted.FieldRef;
import com.google.template.soy.jbcsrc.restricted.MethodRef;
import com.google.template.soy.jbcsrc.restricted.SoyExpression;
import com.google.template.soy.soytree.defn.LocalVar;
import com.google.template.soy.soytree.defn.TemplateParam;
import java.util.Optional;
import javax.annotation.Nullable;
import org.objectweb.asm.Type;

/**
 * Attempts to compile an {@link ExprNode} to an {@link Expression} for a {@link SoyValueProvider}
 * in order to preserve laziness.
 *
 * <p>There are two ways to use this depending on the specific requirements of the caller
 *
 * <ul>
 *   <li>{@link #compileToSoyValueProviderIfUsefulToPreserveStreaming(ExprNode, ExpressionDetacher)}
 *       attempts to compile the expression to a {@link SoyValueProvider} but without introducing
 *       any unnecessary boxing operations. Generating detach logic is OK. This case is for print
 *       operations, where callers may want to call {@link SoyValueProvider#renderAndResolve} to
 *       incrementally print the value. However, this is only desirable if the expression is
 *       naturally a {@link SoyValueProvider}.
 *   <li>{@link #compileAvoidingDetaches(ExprNode)} attempts to compile the expression to a {@link
 *       SoyValueProvider} with no detach logic. This is for passing data to templates or defining
 *       variables with {@code let} statements. In these cases boxing operations are fine (because
 *       the alternative is to use the {@link LazyClosureCompiler} which necessarily boxes the
 *       expression into a custom SoyValueProvider.
 * </ul>
 *
 * <p>This is used as a basic optimization and as a necessary tool to implement template
 * transclusions. If a template has a parameter {@code foo} then we want to be able to render it via
 * {@link SoyValueProvider#renderAndResolve} so that we can render it incrementally.
 */
final class ExpressionToSoyValueProviderCompiler {
  /** Create an expression compiler that can implement complex detaching logic. */
  static ExpressionToSoyValueProviderCompiler create(
      TemplateAnalysis analysis,
      ExpressionCompiler exprCompiler,
      TemplateParameterLookup variables) {
    return new ExpressionToSoyValueProviderCompiler(analysis, exprCompiler, variables);
  }

  private final TemplateAnalysis analysis;
  private final TemplateParameterLookup variables;
  private final ExpressionCompiler exprCompiler;

  private ExpressionToSoyValueProviderCompiler(
      TemplateAnalysis analysis,
      ExpressionCompiler exprCompiler,
      TemplateParameterLookup variables) {
    this.analysis = analysis;
    this.exprCompiler = exprCompiler;
    this.variables = variables;
  }

  /**
   * Compile the given expression to a {@link SoyValueProvider} even if it requires boxing or
   * detaches.
   */
  Expression compile(ExprNode node, ExpressionDetacher detacher) {
    checkNotNull(node);
    checkNotNull(detacher);
    // This mode always works so we can unconditionally dereference this
    return createVisitor(exprCompiler, detacher).exec(node).get();
  }

  /**
   * Compiles the given expression tree to a sequence of bytecode in the current method visitor.
   *
   * <p>If successful, the generated bytecode will resolve to a {@link SoyValueProvider} if it can
   * be done without introducing unnecessary boxing operations. This is intended for situations
   * (like print operations) where calling {@link SoyValueProvider#renderAndResolve} would be better
   * than calling {@link #toString()} and passing directly to the output.
   */
  Optional<Expression> compileToSoyValueProviderIfUsefulToPreserveStreaming(
      ExprNode node, ExpressionDetacher detacher) {
    checkNotNull(node);
    checkNotNull(detacher);
    return createVisitor(/* exprCompiler= */ null, detacher).exec(node);
  }

  /**
   * Compiles the given expression tree to a sequence of bytecode in the current method visitor.
   *
   * <p>If successful, the generated bytecode will resolve to a {@link SoyValueProvider} if it can
   * be done without introducing any detach operations. This is intended for situations where we
   * need to model the expression as a SoyValueProvider to satisfy a contract (e.g. let nodes and
   * params), but we also want to preserve any laziness. So boxing is fine, but detaches are not.
   */
  Optional<Expression> compileAvoidingDetaches(ExprNode node) {
    checkNotNull(node);
    return createVisitor(exprCompiler, /* detacher= */ null).exec(node);
  }

  private CompilerVisitor createVisitor(
      @Nullable ExpressionCompiler exprCompiler, @Nullable ExpressionDetacher detacher) {
    return new CompilerVisitor(
        analysis,
        variables,
        exprCompiler,
        detacher == null ? null : this.exprCompiler.asBasicCompiler(detacher),
        detacher);
  }

  private static final class CompilerVisitor
      extends EnhancedAbstractExprNodeVisitor<Optional<Expression>> {
    final TemplateAnalysis analysis;
    final TemplateParameterLookup variables;

    // depending on the mode at most one of exprCompiler and detachingExprCompiler will be null
    @Nullable final ExpressionCompiler exprCompiler;
    @Nullable final BasicExpressionCompiler detachingExprCompiler;
    @Nullable final ExpressionDetacher detacher;

    CompilerVisitor(
        TemplateAnalysis analysis,
        TemplateParameterLookup variables,
        @Nullable ExpressionCompiler exprCompiler,
        @Nullable BasicExpressionCompiler detachingExprCompiler,
        @Nullable ExpressionDetacher detacher) {
      this.analysis = analysis;
      this.variables = variables;
      // at least one must be non-null
      checkArgument((exprCompiler != null) || (detachingExprCompiler != null));
      this.exprCompiler = exprCompiler;
      this.detachingExprCompiler = detachingExprCompiler;
      this.detacher = detacher;
    }

    private boolean allowsBoxing() {
      return exprCompiler != null;
    }

    private boolean allowsDetaches() {
      return detachingExprCompiler != null;
    }

    @Override
    protected Optional<Expression> visitExprRootNode(ExprRootNode node) {
      return visit(node.getRoot());
    }

    // Primitive value constants

    @Override
    protected Optional<Expression> visitNullNode(NullNode node) {
      // unlike other primitives, this doesn't really count as boxing, just a read of a static
      // constant field. so we always do it
      return Optional.of(FieldRef.NULL_PROVIDER.accessor());
    }

    @Override
    protected Optional<Expression> visitNullCoalescingOpNode(NullCoalescingOpNode node) {
      // All non-trivial ?: will require detaches for the left hand side.
      if (allowsDetaches()) {
        Optional<Expression> maybeLeft = visit(node.getLeftChild());
        Optional<Expression> maybeRight = visit(node.getRightChild());
        // Logging statements get dropped when a value is converted to a SoyValue. If at least one
        // side can be compiled to a SoyValueProvider, there could be logging statements in it, so
        // we need to compile the whole expression to a SoyValueProvider.
        if (maybeLeft.isPresent() || maybeRight.isPresent()) {
          // Get the SoyValueProviders, or box so both left and right are SoyValueProviders.
          Expression right =
              maybeRight.orElseGet(
                  () -> compileToSoyValueProviderWithDetaching(node.getRightChild()));
          Expression left;
          if (maybeLeft.isPresent()) {
            // If left can be compiled to a SoyValueProvider, resolve it to check if it's null.
            Expression leftSvp = maybeLeft.get();
            left =
                ExpressionCompiler.requiresDetach(analysis, node.getLeftChild())
                    ? detacher.waitForSoyValueProvider(leftSvp)
                    : leftSvp;
          } else {
            // If left cannot be compiled to a SoyValueProvider, compile it to a SoyValue and box it
            // into a SoyValueProvider.
            left = compileToSoyValueProviderWithDetaching(node.getLeftChild());
          }

          left = MethodRef.SOY_VALUE_PROVIDER_OR_NULLISH.invoke(left);
          return Optional.of(BytecodeUtils.firstSoyNonNullish(left, right));
        }
      }
      return visitExprNode(node);
    }

    private Expression compileToSoyValueProviderWithDetaching(ExprNode expr) {
      return detachingExprCompiler.compile(expr).box();
    }

    @Override
    protected Optional<Expression> visitConditionalOpNode(ConditionalOpNode node) {
      if (allowsDetaches()) {
        Optional<Expression> trueBranchOpt = visit(node.getChild(1));
        Optional<Expression> falseBranchOpt = visit(node.getChild(2));
        // Compile to a SoyValueProvider if either side can be compiled to a SoyValueProvider. The
        // SoyValueProvider side(s) may have logging statements in them, so need to stay
        // SoyValueProviders, otherwise the logging statements will get dropped.
        if (trueBranchOpt.isPresent() || falseBranchOpt.isPresent()) {
          Branch condition = detachingExprCompiler.compile(node.getChild(0)).compileToBranch();
          Expression trueBranch =
              trueBranchOpt.orElseGet(
                  () -> compileToSoyValueProviderWithDetaching(node.getChild(1)));
          Expression falseBranch =
              falseBranchOpt.orElseGet(
                  () -> compileToSoyValueProviderWithDetaching(node.getChild(2)));
          // Use the actual types, SoyValue or SoyValueProvider
          Type resultType =
              trueBranch.resultType().equals(falseBranch.resultType())
                  ? trueBranch.resultType()
                  : (isDefinitelyAssignableFrom(
                              BytecodeUtils.SOY_VALUE_TYPE, trueBranch.resultType())
                          && isDefinitelyAssignableFrom(
                              BytecodeUtils.SOY_VALUE_TYPE, falseBranch.resultType())
                      ? BytecodeUtils.SOY_VALUE_TYPE
                      : BytecodeUtils.SOY_VALUE_PROVIDER_TYPE);
          return Optional.of(condition.ternary(resultType, trueBranch, falseBranch));
        } else {
          return Optional.empty();
        }
      }
      return visitExprNode(node);
    }

    @Override
    Optional<Expression> visitForLoopVar(VarRefNode varRef, LocalVar local) {
      Expression loopVar = variables.getLocal(local);
      if (loopVar.resultType().equals(Type.LONG_TYPE)) {
        // this happens in foreach loops over ranges
        if (allowsBoxing()) {
          return Optional.of(SoyExpression.forInt(loopVar).box());
        }
        return Optional.empty();
      } else {
        return Optional.of(loopVar);
      }
    }

    @Override
    Optional<Expression> visitParam(VarRefNode varRef, TemplateParam param) {
      return Optional.of(variables.getParam(param));
    }

    @Override
    Optional<Expression> visitLetNodeVar(VarRefNode varRef, LocalVar local) {
      return Optional.of(variables.getLocal(local));
    }

    @Override
    protected Optional<Expression> visitDataAccessNode(DataAccessNode node) {
      // TODO(lukes): implement special case for allowsDetaches().  The complex part will be sharing
      // null safety access logic with the ExpressionCompiler
      return visitExprNode(node);
    }

    @Override
    protected Optional<Expression> visitExprNode(ExprNode node) {
      if (allowsBoxing()) {
        Optional<SoyExpression> compileWithNoDetaches = exprCompiler.compileWithNoDetaches(node);
        if (compileWithNoDetaches.isPresent()) {
          return Optional.of(compileWithNoDetaches.get().box());
        }
        if (allowsDetaches()) {
          return Optional.of(compileToSoyValueProviderWithDetaching(node));
        }
      }
      return Optional.empty();
    }
  }
}
