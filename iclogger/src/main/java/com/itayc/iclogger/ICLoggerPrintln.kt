package com.itayc.iclogger

import java.time.LocalTime
import java.time.format.DateTimeFormatter

class ICLoggerPrintln : ICLogger {

    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")
    private val processId = ProcessHandle.current().pid()

    private val systemLogger = object: Logger {
        override fun log(tag: String, logLevel: LogLevel, message: String, throwable: Throwable?, attributes: Map<String, Any?>?) {
            val timestamp = LocalTime.now().format(timeFormatter)
            val threadId = Thread.currentThread().id
            val levelChar = logLevel.tag

            // Format: timestamp pid-tid tag level message
            // Example: 11:36:36.067 21349-21349 ViewModelNavPanelLocationSource                              D  Found initial position
            // Field allocations (total chars including trailing whitespace):
            // - Timestamp: 13 chars (12 content + 1 space)
            // - PID-TID: 12 chars (11 content + 1 space)
            // - Tag: 61 chars (60 content + 1 space)
            // - Level: 3 chars (1 content + 2 spaces)
            // - Message: unlimited

            val formattedTimestamp = formatField(timestamp, totalWidth = 13, idealSpaces = 1)
            val formattedPidTid = formatField("$processId-$threadId", totalWidth = 12, idealSpaces = 1)
            val formattedTag = formatField(tag, totalWidth = 36, idealSpaces = 1)
            val formattedLevel = formatField(levelChar, totalWidth = 3, idealSpaces = 2)

            println("$formattedTimestamp$formattedPidTid$formattedTag$formattedLevel$message")

            // Print throwable if present
            throwable?.let {
                val stackTrace = it.stackTraceToString()
                stackTrace.lines().forEach { line ->
                    println("$formattedTimestamp$formattedPidTid$formattedTag$formattedLevel$line")
                }
            }
        }
    }

    override val consoleLogger: Logger
        get() = systemLogger

    override var allowLogs: AllowLogs = AllowLogs.Yes(LogLevel.DEBUG)

    override fun log(
        tag: String,
        level: LogLevel,
        message: String,
        throwable: Throwable?,
        attributes: Map<String, Any?>?,
        vararg extraLoggers: LoggerIdOwner
    ) {
        systemLogger.log(tag, level, message, throwable, attributes)
    }

    /**
     * Format a field with fixed total width, reducing whitespace before truncating content.
     *
     * @param content The actual content to display
     * @param totalWidth Total allocated width (content + trailing spaces)
     * @param idealSpaces Number of trailing spaces when content fits perfectly
     * @return Formatted string of exactly totalWidth characters
     */
    private fun formatField(content: String, totalWidth: Int, idealSpaces: Int): String {
        val minSpaces = 1
        val idealContentWidth = totalWidth - idealSpaces

        return when {
            // Content fits within ideal width: use full spacing
            content.length <= idealContentWidth -> {
                val remainingSpaces = totalWidth - content.length
                content + " ".repeat(remainingSpaces)
            }
            // Content overflows ideal but fits with reduced spacing
            content.length < totalWidth -> {
                val remainingSpaces = totalWidth - content.length
                content + " ".repeat(remainingSpaces.coerceAtLeast(minSpaces))
            }
            // Content too long even with minimum spacing: truncate
            else -> {
                val maxContentWidth = totalWidth - minSpaces
                content.take(maxContentWidth) + " ".repeat(minSpaces)
            }
        }
    }
}