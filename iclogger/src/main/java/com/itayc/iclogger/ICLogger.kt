package com.itayc.iclogger

@Suppress("unused")
interface ICLogger {

    /**
     * The logger which the logs would always be logged into. usually the console such as System.out or logcat
     */
    val consoleLogger: Logger?

    /**
     * Info log
     * @param tag - The tag of the log
     * @param log - The message of the log
     */
    fun i(tag: String, log: String) =
        i(tag, log, null, null)

    /**
     * Info log
     * @param tag - The tag of the log
     * @param log - The message of the log
     * @param extraLoggers Extra extraLogger  then the [consoleLogger] which you would like to send the log to.
     */
    fun i(tag: String, log: String,  vararg extraLoggers: LoggerIdOwner) =
        i(tag, log, null, null, *extraLoggers)

    /**
     * Info log
     * @param tag - The tag of the log
     * @param log - The message of the log
     * @param throwable - If there is error of exception
     */
    fun i(tag: String, log: String, throwable: Throwable?) =
        i(tag, log, throwable, null)

    /**
     * Info log
     * @param tag - The tag of the log
     * @param log - The message of the log
     * @param attributes - Extra key-value pairs to log.
     */
    fun i(tag: String, log: String, attributes: Map<String, Any>) =
        i(tag, log, null, attributes)

    /**
     * Info log
     * @param tag - The tag of the log
     * @param message - The message of the log
     * @param throwable - If there is error of exception
     * @param attributes - Extra key-value pairs to log.
     * @param extraLoggers - .
     * @param extraLoggers Extra extraLogger  then the [consoleLogger] which you would like to send the log to.
     */
    fun i(
        tag: String,
        message: String,
        throwable: Throwable?,
        attributes: Map<String, Any?>?,
        vararg extraLoggers: LoggerIdOwner
    )= log(tag, LogLevel.INFO, message, throwable, attributes, *extraLoggers)




    /**
     * Debug log
     * @param tag - The tag of the log
     * @param log - The message of the log
     */
    fun d(tag: String, log: String) =
        d(tag, log, null, null)

    /**
     * Debug log
     * @param tag - The tag of the log
     * @param log - The message of the log
     */
    fun d(tag: String, log: String,  vararg extraLoggers: LoggerIdOwner) =
        d(tag, log, null, null, *extraLoggers)

    /**
     * Debug log
     * @param tag - The tag of the log
     * @param log - The message of the log
     * @param throwable - If there is error of exception
     */
    fun d(tag: String, log: String, throwable: Throwable?) =
        d(tag, log, throwable, null)

    /**
     * Debug log
     * @param tag - The tag of the log
     * @param log - The message of the log
     * @param attributes - Extra key-value pairs to log.
     */
    fun d(tag: String, log: String, attributes: Map<String, Any>) =
        d(tag, log, null, attributes)

    /**
     * Debug log
     * @param tag - The tag of the log
     * @param message - The message of the log
     * @param throwable - If there is error of exception
     * @param attributes - Extra key-value pairs to log.
     * @param extraLoggers Extra extraLogger  then the [consoleLogger] which you would like to send the log to.
     */
    fun d(
        tag: String,
        message: String,
        throwable: Throwable?,
        attributes: Map<String, Any?>?,
        vararg extraLoggers: LoggerIdOwner
    )= log(tag, LogLevel.DEBUG, message, throwable, attributes, *extraLoggers)



    /**
     * Error log
     * @param tag - The tag of the log
     * @param log - The message of the log
     * @param extraLoggers Extra extraLogger  then the [consoleLogger] which you would like to send the log to.
     */
    fun e(tag: String, log: String,  vararg extraLoggers: LoggerIdOwner) =
        e(tag, log, null, null, *extraLoggers)

    /**
     * Error log
     * @param tag - The tag of the log
     * @param log - The message of the log
     * @param throwable - If there is error of exception
     */
    fun e(tag: String, log: String, throwable: Throwable?) =
        e(tag, log, throwable, null)

    /**
     * Error log
     * @param tag - The tag of the log
     * @param log - The message of the log
     * @param attributes - Extra key-value pairs to log.
     */
    fun e(tag: String, log: String, attributes: Map<String, Any>) =
        e(tag, log, null, attributes)

    /**
     * Error log
     * @param tag - The tag of the log
     * @param message - The message of the log
     * @param throwable - If there is error of exception
     * @param attributes - Extra key-value pairs to log.
     * @param extraLoggers - Extra extraLogger which you would like to send the log to.
     */
    fun e(
        tag: String,
        message: String,
        throwable: Throwable?,
        attributes: Map<String, Any?>?,
        vararg extraLoggers: LoggerIdOwner
    ) = log(tag, LogLevel.ERROR, message, throwable, attributes, *extraLoggers)




    /**
     * Warning log
     * @param tag - The tag of the log
     * @param log - The message of the log
     */
    fun w(tag: String, log: String) =
        w(tag, log, null, null)

    /**
     * Warning log
     * @param tag - The tag of the log
     * @param log - The message of the log
     * @param extraLoggers Extra extraLogger  then the [consoleLogger] which you would like to send the log to.
     */
    fun w(tag: String, log: String,  vararg extraLoggers: LoggerIdOwner) =
        w(tag, log, null, null, *extraLoggers)

    /**
     * Warning log
     * @param tag - The tag of the log
     * @param log - The message of the log
     * @param throwable - If there is error of exception
     */
    fun w(tag: String, log: String, throwable: Throwable?) =
        w(tag, log, throwable, null)

    /**
     * Warning log
     * @param tag - The tag of the log
     * @param log - The message of the log
     * @param attributes - Extra key-value pairs to log.
     */
    fun w(tag: String, log: String, attributes: Map<String, Any>) =
        w(tag, log, null, attributes)

    /**
     * Warning log
     * @param tag - The tag of the log
     * @param message - The message of the log
     * @param throwable - If there is error of exception
     * @param attributes - Extra key-value pairs to log.
     * @param extraLoggers Extra extraLogger  then the [consoleLogger] which you would like to send the log to.
     */
    fun w(
        tag: String,
        message: String,
        throwable: Throwable?,
        attributes: Map<String, Any?>?,
        vararg extraLoggers: LoggerIdOwner
    ) = log(tag, LogLevel.WARNING, message, throwable, attributes, *extraLoggers)

    /**
     * logs the message
     * @param tag - The tag of the log
     * @param message - The message of the log
     * @param throwable - If there is error of exception
     * @param attributes - Extra key-value pairs to log.
     * @param extraLoggers Extra extraLogger  then the [consoleLogger] which you would like to send the log to.
     */
    fun log(
        tag: String,
        level: LogLevel,
        message: String,
        throwable: Throwable?,
        attributes: Map<String, Any?>?,
        vararg extraLoggers: LoggerIdOwner
    )

    /**
     * Release any open resources for the logger
     */
    suspend fun releaseResources()

    companion object {

        fun createStackTrace(linesLimit: Int) : String =
            Throwable("Stack trace print with lines limit of $linesLimit").stackTrace.let {
                if (it.isNotEmpty()) {
                    val limit = if (it.size > linesLimit) linesLimit else it.size
                    it.copyOfRange(0, limit)
                } else {
                    arrayOf("Stack trace isn't available")
                }
            }.joinToString(separator = System.lineSeparator())
    }
}