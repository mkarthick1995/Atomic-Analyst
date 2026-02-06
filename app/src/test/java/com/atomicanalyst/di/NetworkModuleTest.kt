package com.atomicanalyst.di

import org.junit.Assert.assertEquals
import org.junit.Test

class NetworkModuleTest {
    @Test
    fun testRetrofit_BaseUrlIsStable() {
        val client = NetworkModule.provideOkHttpClient()
        val retrofit = NetworkModule.provideRetrofit(client)

        assertEquals("https://example.com/", retrofit.baseUrl().toString())
    }
}
