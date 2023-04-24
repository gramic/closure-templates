"""Top level extension to download repos.
"""

load("@bazel_tools//tools/build_defs/repo:jvm.bzl", "jvm_maven_import_external")

_annotation_tag = tag_class(
    attrs = {
        "name": attr.string(default = DEFAULT_NAME),
    },
)

jvm_maven_import_external(
    name = "com_google_auto_value_auto_value",
    artifact = "com.google.auto.value:auto-value:1.9",
    artifact_sha256 = "fd39087fa111da2b12b14675fee740043f0e78e4bfc7055cf3443bfffa3f572b",
    extra_build_file_content = """
java_plugin(
  name = "AutoAnnotationProcessor",
  output_licenses = ["unencumbered"],
  processor_class = "com.google.auto.value.processor.AutoAnnotationProcessor",
  tags = ["annotation=com.google.auto.value.AutoAnnotation;genclass=${package}.AutoAnnotation_${outerclasses}${classname}_${methodname}"],
  deps = [":processor"],
)
java_plugin(
  name = "AutoOneOfProcessor",
  output_licenses = ["unencumbered"],
  processor_class = "com.google.auto.value.processor.AutoOneOfProcessor",
  tags = ["annotation=com.google.auto.value.AutoValue;genclass=${package}.AutoOneOf_${outerclasses}${classname}"],
  deps = [":processor"],
)
java_plugin(
  name = "AutoValueProcessor",
  output_licenses = ["unencumbered"],
  processor_class = "com.google.auto.value.processor.AutoValueProcessor",
  tags = ["annotation=com.google.auto.value.AutoValue;genclass=${package}.AutoValue_${outerclasses}${classname}"],
  deps = [":processor"],
)
java_plugin(
  name = "MemoizedValidator",
  output_licenses = ["unencumbered"],
  processor_class = "com.google.auto.value.extension.memoized.processor.MemoizedValidator",
  deps = [":processor"],
)
java_library(
  name = "com_google_auto_value_auto_value",
  exported_plugins = [
      ":AutoAnnotationProcessor",
      ":AutoOneOfProcessor",
      ":AutoValueProcessor",
      ":MemoizedValidator",
  ],
  exports = ["@com_google_auto_value_auto_value_annotations"],
)
""",
    generated_rule_name = "processor",
    server_urls = SERVER_URLS,
    exports = ["@com_google_auto_value_auto_value_annotations"],
)

# This isn't part of the maven_install above so we can set a custom visibility.
jvm_maven_import_external(
    name = "com_google_auto_value_auto_value_annotations",
    artifact = "com.google.auto.value:auto-value-annotations:1.9",
    artifact_sha256 = "fa5469f4c44ee598a2d8f033ab0a9dcbc6498a0c5e0c998dfa0c2adf51358044",
    default_visibility = [
        "@com_google_auto_value_auto_value//:__pkg__",
        "@maven//:__pkg__",
    ],
    neverlink = True,
    server_urls = SERVER_URLS,
)

def _impl(ctx):
    pass

soy = module_extension(
    implementation = _impl,
    tag_classes = {
        "annotate": _annotation_tag,
    },
)
