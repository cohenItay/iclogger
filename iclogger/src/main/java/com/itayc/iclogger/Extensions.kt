package com.itayc.iclogger

fun String?.appendLevelTagThrowable(
    logLevel: LogLevel,
    tag: String,
    throwable: Throwable? = null
) = "${logLevel.tag}/$tag: ${this ?: ""} ${throwable?.let {"\t$it"} ?: ""}"

fun String.appendAttrsToLog(
    attributes: Map<String, Any?>? = null
) = "$this ${attributes?.let { "\n\tattrs:\n${convertAttributesFromMapToString(it)}" } ?: ""}"

fun convertAttributesFromMapToString(attributes: Map<String, Any?>): String {
    val attributesStr = StringBuilder()
    for ((key, value) in attributes) {
        attributesStr.append("\t\t").append(key).append(" → ").append(value).append("\n")
    }
    return attributesStr.toString()
}