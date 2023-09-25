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
package com.google.template.soy.jbcsrc.shared;

import com.google.errorprone.annotations.Keep;
import java.lang.invoke.MethodHandles;

/** Extra constant boostrap methods. */
public final class ExtraConstantBootstraps {
  @Keep
  public static boolean constantBoolean(
      MethodHandles.Lookup lookup, String name, Class<?> type, int v) {
    return v != 0;
  }

  /**
   * Returns a unique object. Useful for associating with a callsite to uniquely identify it at
   * runtime.
   */
  @Keep
  public static Object callSiteKey(MethodHandles.Lookup lookup, String name, Class<?> type, int v) {
    return new Object();
  }

  private ExtraConstantBootstraps() {}
}
