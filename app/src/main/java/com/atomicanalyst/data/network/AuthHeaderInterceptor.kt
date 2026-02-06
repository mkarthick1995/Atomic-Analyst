package com.atomicanalyst.data.network

import com.atomicanalyst.data.security.SessionManager
import com.atomicanalyst.utils.Clock
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthHeaderInterceptor @Inject constructor(
    private val sessionManager: SessionManager,
    private val clock: Clock
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val session = sessionManager.readSession()
        val updatedRequest = if (session != null && !session.isExpired(clock.now())) {
            request.newBuilder()
                .addHeader("Authorization", "Bearer ${session.token}")
                .build()
        } else {
            request
        }
        return chain.proceed(updatedRequest)
    }
}
