package com.itay.iclogger

sealed class LogLevel(val tag: String) {
    data object INFO: LogLevel("I")
    data object DEBUG: LogLevel("D")
    data object WARNING: LogLevel("W")
    data object ERROR: LogLevel("E")
}