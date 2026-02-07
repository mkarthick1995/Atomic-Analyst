package com.atomicanalyst.data.network

import com.atomicanalyst.data.security.InMemorySecureStorage
import com.atomicanalyst.data.security.SessionManager
import com.atomicanalyst.utils.FixedClock
import java.util.concurrent.TimeUnit
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AuthHeaderInterceptorTest {
    @Test
    fun intercept_AddsAuthorizationHeaderWhenSessionValid() {
        val clock = FixedClock(0L)
        val storage = InMemorySecureStorage()
        val sessionManager = SessionManager(storage, clock, sessionTtlMinutes = 1)
        val interceptor = AuthHeaderInterceptor(sessionManager, clock)
        val request = Request.Builder().url("https://example.com/").build()
        val chain = FakeChain(request)

        interceptor.intercept(chain)
        assertNull(chain.lastRequest.header("Authorization"))

        val session = sessionManager.createSession("user1")
        interceptor.intercept(chain)
        assertEquals("Bearer ${session.token}", chain.lastRequest.header("Authorization"))
    }
}

private class FakeChain(initial: Request) : Interceptor.Chain {
    private var request: Request = initial
    val lastRequest: Request get() = request

    override fun request(): Request = request

    override fun proceed(request: Request): Response {
        this.request = request
        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .body("".toResponseBody())
            .build()
    }

    override fun connection() = null

    override fun call() = throw UnsupportedOperationException("Not needed in test")

    override fun connectTimeoutMillis(): Int = 0

    override fun withConnectTimeout(timeout: Int, unit: TimeUnit): Interceptor.Chain = this

    override fun readTimeoutMillis(): Int = 0

    override fun withReadTimeout(timeout: Int, unit: TimeUnit): Interceptor.Chain = this

    override fun writeTimeoutMillis(): Int = 0

    override fun withWriteTimeout(timeout: Int, unit: TimeUnit): Interceptor.Chain = this
}
