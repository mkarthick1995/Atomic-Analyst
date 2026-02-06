package com.atomicanalyst.utils

import org.junit.Test

class AppLoggerTest {
    @Test
    fun testLogger_DoesNotThrow() {
        AppLogger.d("Test", "debug")
        AppLogger.i("Test", "info")
        AppLogger.e("Test", "error", null)
    }
}
