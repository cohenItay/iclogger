package com.itay.iclogger

class ICLoggerPrintln : ICLogger {

    private val systemLogger = object: Logger {
        override fun log(tag: String, logLevel: LogLevel, message: String, throwable: Throwable?, attributes: Map<String, Any?>?) {
            println("$tag: $message" + (throwable?.message ?: ""))
        }

        override suspend fun releaseResources() {}
    }

    override val consoleLogger: Logger
        get() = systemLogger

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

    override suspend fun releaseResources() {

    }
}