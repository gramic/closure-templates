/*
 * Copyright 2016 Google Inc.
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

package com.google.template.soy.jssrc.dsl;

import com.google.template.soy.exprtree.Operator;
import com.google.template.soy.exprtree.Operator.Associativity;
import com.google.template.soy.jssrc.restricted.JsExpr;

/** Base class for representing a JavaScript operation. */
abstract class Operation extends Expression {

  public static String getOperatorToken(Operator soyOperator) {
    switch (soyOperator) {
      case NOT:
        return "!";
      case AND:
        return "&&";
      case OR:
        return "||";
      case NULL_COALESCING:
        return "??";
      case ASSERT_NON_NULL:
      case NEGATIVE:
      case TIMES:
      case DIVIDE_BY:
      case MOD:
      case PLUS:
      case MINUS:
      case SHIFT_LEFT:
      case SHIFT_RIGHT:
      case LESS_THAN:
      case GREATER_THAN:
      case LESS_THAN_OR_EQUAL:
      case GREATER_THAN_OR_EQUAL:
      case EQUAL:
      case NOT_EQUAL:
      case BITWISE_AND:
      case BITWISE_XOR:
      case BITWISE_OR:
        return soyOperator.getTokenString();
      case CONDITIONAL:
        throw new IllegalArgumentException("Not a single token.");
    }
    throw new AssertionError();
  }

  abstract Precedence precedence();

  abstract Associativity associativity();

  @Override
  public final JsExpr singleExprOrName(FormatOptions formatOptions) {
    FormattingContext ctx = new FormattingContext(formatOptions);
    ctx.appendOutputExpression(this);
    return new JsExpr(ctx.toString(), precedence().toInt());
  }

  /**
   * Surrounds the operand with parens if required by its {@link #precedence} or {@link
   * #associativity}. For subclasses to call from {@link #doFormatOutputExpr}.
   *
   * @param operandPosition The position of the operand with respect to this operation.
   */
  final void formatOperand(
      Expression operand, OperandPosition operandPosition, FormattingContext ctx) {
    boolean protect = shouldProtect(operand, operandPosition);
    if (protect) {
      ctx.append('(');
    }
    ctx.appendOutputExpression(operand);
    if (protect) {
      ctx.append(')');
    }
  }

  /**
   * An operand needs to be protected with parens if
   *
   * <ul>
   *   <li>its {@link #precedence} is lower than the operator's precedence, or
   *   <li>its precedence is the same as the operator's, it is {@link Associativity#LEFT left
   *       associative}, and it appears to the right of the operator, or
   *   <li>its precedence is the same as the operator's, it is {@link Associativity#RIGHT right
   *       associative}, and it appears to the left of the operator.
   * </ul>
   */
  private boolean shouldProtect(Expression operand, OperandPosition operandPosition) {
    if (operand instanceof Operation) {
      Operation operation = (Operation) operand;
      return operation.precedence().lessThan(this.precedence())
          || (operation.precedence() == this.precedence()
              && operandPosition.shouldParenthesize(operation.associativity()));
    } else if (operand instanceof Leaf) {
      // JsExprs have precedence info, but not associativity. So at least check the precedence.
      JsExpr expr = ((Leaf) operand).value();
      return expr.getPrecedence() < this.precedence().toInt();
    } else {
      return false;
    }
  }
}
