package com.atomicanalyst.utils

import android.util.Log
import com.atomicanalyst.BuildConfig

object AppLogger {
    fun d(tag: String, message: String) {
        if (BuildConfig.DEBUG) {
            safeLog { Log.d(tag, message) }
        }
    }

    fun i(tag: String, message: String) {
        if (BuildConfig.DEBUG) {
            safeLog { Log.i(tag, message) }
        }
    }

    fun e(tag: String, message: String, throwable: Throwable? = null) {
        if (BuildConfig.DEBUG) {
            safeLog { Log.e(tag, message, throwable) }
        }
    }

    private fun safeLog(block: () -> Int) {
        try {
            block()
        } catch (_: RuntimeException) {
            // Ignore logging failures in local unit tests where Android Log is not available.
        }
    }
}
