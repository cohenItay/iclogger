package com.itayc.iclogger

interface TagOwner {
    val TAG: String
        get() = this::class.simpleName!!
}