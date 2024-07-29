package com.itay.loggers_android.disklogger

import com.itay.iclogger.LoggerIdOwner

internal const val LoggerDiskId = "com.trekace.common.LoggerDisk"

data object LoggerDisk : LoggerIdOwner {
    override val id: String
        get() = LoggerDiskId
}