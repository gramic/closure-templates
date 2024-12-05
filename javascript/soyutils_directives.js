/*
 * Copyright 2019 Google Inc.
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

/*
 * Copyright 2018 Google Inc.
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
/** @fileoverview @suppress {lintChecks} */

goog.module('google3.javascript.template.soy.soyutils_directives');
var module = module || { id: 'javascript/template/soy/soyutils_directives.js' };
const {SanitizedContentKind} = goog.require('goog.soy.data');
var soy = goog.require('soy'); // from //javascript/template/soy:soy_usegoog_js
var goog_soydata_VERY_UNSAFE_1 = goog.require('soydata.VERY_UNSAFE'); // from //javascript/template/soy:soy_usegoog_js
function isIdomFunctionType(
// tslint:disable-next-line:no-any
value, type) {
  return typeof value === 'function' && value.contentKind === type;
}
exports.$$isIdomFunctionType = isIdomFunctionType;
/**
 * Specialization of filterHtmlAttributes for Incremental DOM that can handle
 * attribute functions gracefully. In any other situation, this delegates to
 * the regular escaping directive.
 */
// tslint:disable-next-line:no-any
function filterHtmlAttributes(value) {
  if (isIdomFunctionType(value, SanitizedContentKind.ATTRIBUTES) ||
      soy.$$isAttribute(value)) {
    return value;
  }
    return soy.$$filterHtmlAttributes(value);
}
exports.$$filterHtmlAttributes = filterHtmlAttributes;
/**
 * Specialization of escapeHtml for Incremental DOM that can handle
 * html functions gracefully. In any other situation, this delegates to
 * the regular escaping directive.
 */
// tslint:disable-next-line:no-any
function escapeHtml(value, renderer) {
  if (isIdomFunctionType(value, SanitizedContentKind.HTML)) {
    return goog_soydata_VERY_UNSAFE_1.ordainSanitizedHtml(
        value.toString(renderer));
  }
    return soy.$$escapeHtml(value);
}
exports.$$escapeHtml = escapeHtml;
/**
 * Specialization of bidiUnicodeWrap for Incremental DOM that can handle
 * html functions gracefully. In any other situation, this delegates to
 * the regular escaping directive.
 */
function bidiUnicodeWrap(
// tslint:disable-next-line:no-any
bidiGlobalDir, value, renderer) {
  if (isIdomFunctionType(value, SanitizedContentKind.HTML)) {
    return soy.$$bidiUnicodeWrap(
        bidiGlobalDir,
        goog_soydata_VERY_UNSAFE_1.ordainSanitizedHtml(
            value.toString(renderer)));
  }
    return soy.$$bidiUnicodeWrap(bidiGlobalDir, value);
}
exports.$$bidiUnicodeWrap = bidiUnicodeWrap;
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoic295dXRpbHNfZGlyZWN0aXZlcy5qcyIsInNvdXJjZVJvb3QiOiIiLCJzb3VyY2VzIjpbIi4uLy4uLy4uLy4uLy4uLy4uL2phdmFzY3JpcHQvdGVtcGxhdGUvc295L3NveXV0aWxzX2RpcmVjdGl2ZXMudHMiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IkFBQUE7Ozs7Ozs7Ozs7Ozs7O0dBY0c7OztBQUVILG1HQUEyRSxDQUFDLHFDQUFxQztBQUNqSCw4QkFBZ0MsQ0FBRSxnREFBZ0Q7QUFDbEYsbURBQTRDLENBQUUsd0NBQXdDO0FBQ3RGLHFFQUE2RCxDQUFFLGdEQUFnRDtBQU0vRyxTQUFTLGtCQUFrQjtBQUMzQixrQ0FBa0M7QUFDOUIsS0FBVSxFQUFFLElBQTBCO0lBQ3hDLE9BQU8sSUFBSSxDQUFDLFVBQVUsQ0FBQyxLQUFLLENBQUMsSUFBSyxLQUFzQixDQUFDLFdBQVcsS0FBSyxJQUFJLENBQUM7QUFDaEYsQ0FBQztBQWdEdUIsa0RBQW9CO0FBOUM1Qzs7OztHQUlHO0FBQ0gsa0NBQWtDO0FBQ2xDLFNBQVMsb0JBQW9CLENBQUMsS0FBVTtJQUN0QyxJQUFJLGtCQUFrQixDQUFDLEtBQUssRUFBRSwwQ0FBcUIsVUFBVSxDQUFDO1FBQzFELDZCQUFXLENBQUMsS0FBSyxDQUFDLEVBQUU7UUFDdEIsT0FBTyxLQUFLLENBQUM7S0FDZDtJQUNELE9BQU8sR0FBRyxDQUFDLHNCQUFzQixDQUFDLEtBQUssQ0FBQyxDQUFDO0FBQzNDLENBQUM7QUErQnlCLHNEQUFzQjtBQTdCaEQ7Ozs7R0FJRztBQUNILGtDQUFrQztBQUNsQyxTQUFTLFVBQVUsQ0FBQyxLQUFVLEVBQUUsUUFBZ0M7SUFDOUQsSUFBSSxrQkFBa0IsQ0FBQyxLQUFLLEVBQUUsMENBQXFCLElBQUksQ0FBQyxFQUFFO1FBQ3hELE9BQU8sOENBQW1CLENBQUMsS0FBSyxDQUFDLFFBQVEsQ0FBQyxRQUFRLENBQUMsQ0FBQyxDQUFDO0tBQ3REO0lBQ0QsT0FBTyxHQUFHLENBQUMsWUFBWSxDQUFDLEtBQUssQ0FBQyxDQUFDO0FBQ2pDLENBQUM7QUFtQmUsa0NBQVk7QUFqQjVCOzs7O0dBSUc7QUFDSCxTQUFTLGVBQWU7QUFDcEIsa0NBQWtDO0FBQ2xDLGFBQXFCLEVBQUUsS0FBVSxFQUFFLFFBQWdDO0lBQ3JFLElBQUksa0JBQWtCLENBQUMsS0FBSyxFQUFFLDBDQUFxQixJQUFJLENBQUMsRUFBRTtRQUN4RCxPQUFPLEdBQUcsQ0FBQyxpQkFBaUIsQ0FDeEIsYUFBYSxFQUFFLDhDQUFtQixDQUFDLEtBQUssQ0FBQyxRQUFRLENBQUMsUUFBUSxDQUFDLENBQUMsQ0FBQyxDQUFDO0tBQ25FO0lBQ0QsT0FBTyxHQUFHLENBQUMsaUJBQWlCLENBQUMsYUFBYSxFQUFFLEtBQUssQ0FBQyxDQUFDO0FBQ3JELENBQUM7QUFLb0IsNENBQWlCIiwic291cmNlc0NvbnRlbnQiOlsiLypcbiAqIENvcHlyaWdodCAyMDE4IEdvb2dsZSBJbmMuXG4gKlxuICogTGljZW5zZWQgdW5kZXIgdGhlIEFwYWNoZSBMaWNlbnNlLCBWZXJzaW9uIDIuMCAodGhlIFwiTGljZW5zZVwiKTtcbiAqIHlvdSBtYXkgbm90IHVzZSB0aGlzIGZpbGUgZXhjZXB0IGluIGNvbXBsaWFuY2Ugd2l0aCB0aGUgTGljZW5zZS5cbiAqIFlvdSBtYXkgb2J0YWluIGEgY29weSBvZiB0aGUgTGljZW5zZSBhdFxuICpcbiAqICAgICBodHRwOi8vd3d3LmFwYWNoZS5vcmcvbGljZW5zZXMvTElDRU5TRS0yLjBcbiAqXG4gKiBVbmxlc3MgcmVxdWlyZWQgYnkgYXBwbGljYWJsZSBsYXcgb3IgYWdyZWVkIHRvIGluIHdyaXRpbmcsIHNvZnR3YXJlXG4gKiBkaXN0cmlidXRlZCB1bmRlciB0aGUgTGljZW5zZSBpcyBkaXN0cmlidXRlZCBvbiBhbiBcIkFTIElTXCIgQkFTSVMsXG4gKiBXSVRIT1VUIFdBUlJBTlRJRVMgT1IgQ09ORElUSU9OUyBPRiBBTlkgS0lORCwgZWl0aGVyIGV4cHJlc3Mgb3IgaW1wbGllZC5cbiAqIFNlZSB0aGUgTGljZW5zZSBmb3IgdGhlIHNwZWNpZmljIGxhbmd1YWdlIGdvdmVybmluZyBwZXJtaXNzaW9ucyBhbmRcbiAqIGxpbWl0YXRpb25zIHVuZGVyIHRoZSBMaWNlbnNlLlxuICovXG5cbmltcG9ydCBTYW5pdGl6ZWRDb250ZW50S2luZCBmcm9tICdnb29nOmdvb2cuc295LmRhdGEuU2FuaXRpemVkQ29udGVudEtpbmQnOyAvLyBmcm9tIC8vamF2YXNjcmlwdC9jbG9zdXJlL3NveTpkYXRhXG5pbXBvcnQgKiBhcyBzb3kgZnJvbSAnZ29vZzpzb3knOyAgLy8gZnJvbSAvL2phdmFzY3JpcHQvdGVtcGxhdGUvc295OnNveV91c2Vnb29nX2pzXG5pbXBvcnQge2lzQXR0cmlidXRlfSBmcm9tICdnb29nOnNveS5jaGVja3MnOyAgLy8gZnJvbSAvL2phdmFzY3JpcHQvdGVtcGxhdGUvc295OmNoZWNrc1xuaW1wb3J0IHtvcmRhaW5TYW5pdGl6ZWRIdG1sfSBmcm9tICdnb29nOnNveWRhdGEuVkVSWV9VTlNBRkUnOyAgLy8gZnJvbSAvL2phdmFzY3JpcHQvdGVtcGxhdGUvc295OnNveV91c2Vnb29nX2pzXG5cbmltcG9ydCB7SW5jcmVtZW50YWxEb21SZW5kZXJlcn0gZnJvbSAnLi9hcGlfaWRvbSc7XG5pbXBvcnQge0lkb21GdW5jdGlvbn0gZnJvbSAnLi9lbGVtZW50X2xpYl9pZG9tJztcblxuXG5mdW5jdGlvbiBpc0lkb21GdW5jdGlvblR5cGUoXG4vLyB0c2xpbnQ6ZGlzYWJsZS1uZXh0LWxpbmU6bm8tYW55XG4gICAgdmFsdWU6IGFueSwgdHlwZTogU2FuaXRpemVkQ29udGVudEtpbmQpOiB2YWx1ZSBpcyBJZG9tRnVuY3Rpb24ge1xuICByZXR1cm4gZ29vZy5pc0Z1bmN0aW9uKHZhbHVlKSAmJiAodmFsdWUgYXMgSWRvbUZ1bmN0aW9uKS5jb250ZW50S2luZCA9PT0gdHlwZTtcbn1cblxuLyoqXG4gKiBTcGVjaWFsaXphdGlvbiBvZiBmaWx0ZXJIdG1sQXR0cmlidXRlcyBmb3IgSW5jcmVtZW50YWwgRE9NIHRoYXQgY2FuIGhhbmRsZVxuICogYXR0cmlidXRlIGZ1bmN0aW9ucyBncmFjZWZ1bGx5LiBJbiBhbnkgb3RoZXIgc2l0dWF0aW9uLCB0aGlzIGRlbGVnYXRlcyB0b1xuICogdGhlIHJlZ3VsYXIgZXNjYXBpbmcgZGlyZWN0aXZlLlxuICovXG4vLyB0c2xpbnQ6ZGlzYWJsZS1uZXh0LWxpbmU6bm8tYW55XG5mdW5jdGlvbiBmaWx0ZXJIdG1sQXR0cmlidXRlcyh2YWx1ZTogYW55KSB7XG4gIGlmIChpc0lkb21GdW5jdGlvblR5cGUodmFsdWUsIFNhbml0aXplZENvbnRlbnRLaW5kLkFUVFJJQlVURVMpIHx8XG4gICAgICBpc0F0dHJpYnV0ZSh2YWx1ZSkpIHtcbiAgICByZXR1cm4gdmFsdWU7XG4gIH1cbiAgcmV0dXJuIHNveS4kJGZpbHRlckh0bWxBdHRyaWJ1dGVzKHZhbHVlKTtcbn1cblxuLyoqXG4gKiBTcGVjaWFsaXphdGlvbiBvZiBlc2NhcGVIdG1sIGZvciBJbmNyZW1lbnRhbCBET00gdGhhdCBjYW4gaGFuZGxlXG4gKiBodG1sIGZ1bmN0aW9ucyBncmFjZWZ1bGx5LiBJbiBhbnkgb3RoZXIgc2l0dWF0aW9uLCB0aGlzIGRlbGVnYXRlcyB0b1xuICogdGhlIHJlZ3VsYXIgZXNjYXBpbmcgZGlyZWN0aXZlLlxuICovXG4vLyB0c2xpbnQ6ZGlzYWJsZS1uZXh0LWxpbmU6bm8tYW55XG5mdW5jdGlvbiBlc2NhcGVIdG1sKHZhbHVlOiBhbnksIHJlbmRlcmVyOiBJbmNyZW1lbnRhbERvbVJlbmRlcmVyKSB7XG4gIGlmIChpc0lkb21GdW5jdGlvblR5cGUodmFsdWUsIFNhbml0aXplZENvbnRlbnRLaW5kLkhUTUwpKSB7XG4gICAgcmV0dXJuIG9yZGFpblNhbml0aXplZEh0bWwodmFsdWUudG9TdHJpbmcocmVuZGVyZXIpKTtcbiAgfVxuICByZXR1cm4gc295LiQkZXNjYXBlSHRtbCh2YWx1ZSk7XG59XG5cbi8qKlxuICogU3BlY2lhbGl6YXRpb24gb2YgYmlkaVVuaWNvZGVXcmFwIGZvciBJbmNyZW1lbnRhbCBET00gdGhhdCBjYW4gaGFuZGxlXG4gKiBodG1sIGZ1bmN0aW9ucyBncmFjZWZ1bGx5LiBJbiBhbnkgb3RoZXIgc2l0dWF0aW9uLCB0aGlzIGRlbGVnYXRlcyB0b1xuICogdGhlIHJlZ3VsYXIgZXNjYXBpbmcgZGlyZWN0aXZlLlxuICovXG5mdW5jdGlvbiBiaWRpVW5pY29kZVdyYXAoXG4gICAgLy8gdHNsaW50OmRpc2FibGUtbmV4dC1saW5lOm5vLWFueVxuICAgIGJpZGlHbG9iYWxEaXI6IG51bWJlciwgdmFsdWU6IGFueSwgcmVuZGVyZXI6IEluY3JlbWVudGFsRG9tUmVuZGVyZXIpIHtcbiAgaWYgKGlzSWRvbUZ1bmN0aW9uVHlwZSh2YWx1ZSwgU2FuaXRpemVkQ29udGVudEtpbmQuSFRNTCkpIHtcbiAgICByZXR1cm4gc295LiQkYmlkaVVuaWNvZGVXcmFwKFxuICAgICAgICBiaWRpR2xvYmFsRGlyLCBvcmRhaW5TYW5pdGl6ZWRIdG1sKHZhbHVlLnRvU3RyaW5nKHJlbmRlcmVyKSkpO1xuICB9XG4gIHJldHVybiBzb3kuJCRiaWRpVW5pY29kZVdyYXAoYmlkaUdsb2JhbERpciwgdmFsdWUpO1xufVxuXG5leHBvcnQge1xuICBmaWx0ZXJIdG1sQXR0cmlidXRlcyBhcyAkJGZpbHRlckh0bWxBdHRyaWJ1dGVzLFxuICBlc2NhcGVIdG1sIGFzICQkZXNjYXBlSHRtbCxcbiAgYmlkaVVuaWNvZGVXcmFwIGFzICQkYmlkaVVuaWNvZGVXcmFwLFxuICBpc0lkb21GdW5jdGlvblR5cGUgYXMgJCRpc0lkb21GdW5jdGlvblR5cGUsXG59O1xuIl19
