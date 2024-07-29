package com.itayc.iclogger

@Suppress("unused")
internal class ICLoggerImpl(
    override val consoleLogger: Logger?,
    private val isDebuggable: Boolean,
    private val loggersMap: Map<String, Logger>
) : ICLogger {


    override fun log(
        tag: String,
        level: LogLevel,
        message: String,
        throwable: Throwable?,
        attributes: Map<String, Any?>?,
        vararg extraLoggers: LoggerIdOwner
    ) {
        if (isDebuggable) {
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

fun createIcloggerInstance(
    consoleLogger: Logger?,
    isInDebug: Boolean,
    loggersMap: Map<String, Logger>
) : ICLogger = ICLoggerImpl(consoleLogger, isInDebug, loggersMap)