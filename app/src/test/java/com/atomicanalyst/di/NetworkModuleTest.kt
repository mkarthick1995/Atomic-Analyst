package com.atomicanalyst.di

import com.atomicanalyst.data.network.AuthHeaderInterceptor
import com.atomicanalyst.data.network.NetworkSecurityConfig
import com.atomicanalyst.data.network.Pin
import com.atomicanalyst.data.security.InMemorySecureStorage
import com.atomicanalyst.data.security.SessionManager
import com.atomicanalyst.utils.FixedClock
import okhttp3.CertificatePinner
import okhttp3.TlsVersion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertTrue
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

    @Test
    fun okHttpClient_AddsCertificatePinnerWhenPinsProvided() {
        val clock = FixedClock(0L)
        val sessionManager = SessionManager(InMemorySecureStorage(), clock)
        val interceptor = AuthHeaderInterceptor(sessionManager, clock)
        val config = NetworkSecurityConfig(
            baseUrl = "https://example.com/",
            pins = listOf(Pin("example.com", "sha256/abc123"))
        )

        val client = NetworkModule.provideOkHttpClient(interceptor, config)

        assertNotSame(CertificatePinner.DEFAULT, client.certificatePinner)
    }

    @Test
    fun okHttpClient_UsesModernTls() {
        val clock = FixedClock(0L)
        val sessionManager = SessionManager(InMemorySecureStorage(), clock)
        val interceptor = AuthHeaderInterceptor(sessionManager, clock)
        val config = NetworkSecurityConfig("https://example.com/")

        val client = NetworkModule.provideOkHttpClient(interceptor, config)

        val hasTls13 = client.connectionSpecs.any { spec ->
            spec.tlsVersions?.contains(TlsVersion.TLS_1_3) == true
        }
        val hasTls12 = client.connectionSpecs.any { spec ->
            spec.tlsVersions?.contains(TlsVersion.TLS_1_2) == true
        }
        val allTls = client.connectionSpecs.all { spec -> spec.isTls }
        assertTrue(hasTls13 && hasTls12 && allTls)
    }
}
