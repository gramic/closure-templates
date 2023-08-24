/*
 * Copyright 2023 Google Inc.
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

package com.google.template.soy.types;

import com.google.template.soy.soytree.SoyTypeP;

/** The "undefined" type. */
public final class UndefinedType extends PrimitiveType {

  private static final UndefinedType INSTANCE = new UndefinedType();

  private UndefinedType() {}

  @Override
  public Kind getKind() {
    return Kind.UNDEFINED;
  }

  @Override
  public boolean isNullOrUndefined() {
    return true;
  }

  @Override
  public String toString() {
    return "undefined";
  }

  @Override
  void doToProto(SoyTypeP.Builder builder) {
    builder.setPrimitive(SoyTypeP.PrimitiveTypeP.UNDEFINED);
  }

  /** Return the single instance of this type. */
  public static UndefinedType getInstance() {
    return INSTANCE;
  }
}
