package com.atomicanalyst.utils

import org.junit.Assert.assertNotNull
import org.junit.Test

class DispatcherProviderTest {
    @Test
    fun testDefaultDispatchers_AreAvailable() {
        val provider = DefaultDispatcherProvider()

        assertNotNull(provider.main)
        assertNotNull(provider.io)
        assertNotNull(provider.default)
    }
}
