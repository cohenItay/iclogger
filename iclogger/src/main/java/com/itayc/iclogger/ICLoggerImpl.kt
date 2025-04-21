package com.itayc.iclogger

@Suppress("unused")
internal class ICLoggerImpl(
    override val consoleLogger: Logger?,
    allowLogsInitial: AllowLogs,
    private val loggersMap: Map<String, Logger>
) : ICLogger {

    override var allowLogs: AllowLogs = allowLogsInitial

    override fun log(
        tag: String,
        level: LogLevel,
        message: String,
        throwable: Throwable?,
        attributes: Map<String, Any?>?,
        vararg extraLoggers: LoggerIdOwner
    ) {
        val allowedLevel = allowLogs.getLogLevelOrNull() ?: return
        if (level >= allowedLevel) {
            val (extra, _) = extraLoggers.partition { loggersMap.containsKey(it.id) }
            consoleLogger?.log(tag, level, message, throwable, attributes)
            extra.forEach { loggerId ->
                loggersMap[loggerId.id]!!.log(tag, level, message, throwable, attributes)
            }
        }
    }

    override fun releaseResources() {
        loggersMap.values.forEach {
            it.releaseResources()
        }
    }

    private fun AllowLogs.getLogLevelOrNull() = when (this) {
        is AllowLogs.No -> null
        is AllowLogs.Yes -> level
    }
}


/**
 * Creates an instance of the ICLogger interface.
 *
 * This function initializes and returns a concrete implementation of the ICLogger,
 * which is used for logging within the system. It allows for configuring the logger
 * with an optional console logger, initial log filtering rules, and a map of
 * named loggers.
 *
 * @param consoleLogger An optional Logger instance to which log messages will
 * also be directed. If null, no console logging will occur.
 * This is useful for capturing logs in standard output/error streams.
 * @param allowLogsInitial An instance of AllowLogs, defining the initial set of
 * rules for filtering log messages. This determines which
 * log levels and categories are initially allowed. can be modified later on.
 * @param loggersMap A map where keys are logger ids (String) and values are
 * corresponding Logger instances. This allows logs to be
 * directed to specific, named loggers.
 * Note that the key for this loggers map is equivalent to your [LoggerIdOwner.id]
 * Example:
 * ```
 * object DiskLoggerId : LoggerIdOwner {
 *     override val id: String = "com.example.disk.id"
 * }
 * // then you can call
 * icLogger.i(TAG, "log message", ..., DiskLoggerId)
 * ```
 * @return An instance of ICLogger, configured with the provided parameters.
 */
@Suppress("unused")
fun createIcLoggerInstance(
    consoleLogger: Logger?,
    allowLogsInitial: AllowLogs,
    loggersMap: Map<String, Logger>
) : ICLogger = ICLoggerImpl(consoleLogger, allowLogsInitial, loggersMap)