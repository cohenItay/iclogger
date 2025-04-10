package com.itayc.iclogger

@Suppress("unused")
internal class ICLoggerImpl(
    override val consoleLogger: Logger?,
    allowLogsInitial: AllowLogs,
    private val loggersMap: Map<String, Logger>
) : ICLogger {

    private var allowedLevel:LogLevel? = null

    override var allowLogs: AllowLogs = allowLogsInitial
        set(value) {
            field = value
            allowedLevel = when (value) {
                is AllowLogs.No -> null
                is AllowLogs.Yes -> value.level
            }
        }

    override fun log(
        tag: String,
        level: LogLevel,
        message: String,
        throwable: Throwable?,
        attributes: Map<String, Any?>?,
        vararg extraLoggers: LoggerIdOwner
    ) {
        val allowedLevel = this.allowedLevel ?: return
        if (level >= allowedLevel) {
            val (extra, _) = extraLoggers.partition { loggersMap.containsKey(it.id) }
            consoleLogger?.log(tag, level, message, throwable, attributes)
            extra.forEach { loggerId ->
                loggersMap[loggerId.id]!!.log(tag, level, message, throwable, attributes)
            }
        }
    }

    override suspend fun releaseResources() {
        loggersMap.values.forEach {
            it.releaseResources()
        }
    }
}

@Suppress("unused")
fun createIcLoggerInstance(
    consoleLogger: Logger?,
    allowLogsInitial: AllowLogs,
    loggersMap: Map<String, Logger>
) : ICLogger = ICLoggerImpl(consoleLogger, allowLogsInitial, loggersMap)