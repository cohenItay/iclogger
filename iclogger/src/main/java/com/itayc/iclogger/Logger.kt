package com.itayc.iclogger

interface Logger {

    fun log(
        tag: String,
        logLevel: LogLevel,
        message: String,
        throwable: Throwable?,
        attributes: Map<String, Any?>?
    )

    /**
     * Release any open resources (if any)
     */
    suspend fun releaseResources()
}