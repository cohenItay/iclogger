package com.itayc.iclogger


/**
 * Represents whether logging is allowed and, if so, at what level.
 *
 * This sealed interface provides two possible states:
 * - [Yes]: Logging is allowed at a specified [LogLevel].
 * - [No]: Logging is not allowed.
 */
sealed interface AllowLogs {
    data class Yes(val level: LogLevel) : AllowLogs
    data object No: AllowLogs
}