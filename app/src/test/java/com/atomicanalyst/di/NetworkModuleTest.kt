package com.atomicanalyst.di

import com.atomicanalyst.data.network.AuthHeaderInterceptor
import com.atomicanalyst.data.network.NetworkSecurityConfig
import com.atomicanalyst.data.security.InMemorySecureStorage
import com.atomicanalyst.data.security.SessionManager
import com.atomicanalyst.utils.FixedClock
import org.junit.Assert.assertEquals
import org.junit.Test

class NetworkModuleTest {
    @Test
    fun testRetrofit_BaseUrlIsStable() {
        val clock = FixedClock(0L)
        val sessionManager = SessionManager(InMemorySecureStorage(), clock)
        val interceptor = AuthHeaderInterceptor(sessionManager, clock)
        val config = NetworkSecurityConfig("https://example.com/")
        val client = NetworkModule.provideOkHttpClient(interceptor, config)
        val retrofit = NetworkModule.provideRetrofit(client, config)

        assertEquals("https://example.com/", retrofit.baseUrl().toString())
    }
}
