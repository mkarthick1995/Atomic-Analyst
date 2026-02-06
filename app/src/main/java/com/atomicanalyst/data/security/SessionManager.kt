package com.atomicanalyst.data.security

import com.atomicanalyst.domain.model.AuthSession
import com.atomicanalyst.utils.Clock
import java.util.UUID

class SessionManager(
    private val storage: SecureStorage,
    private val clock: Clock,
    private val sessionTtlMinutes: Long = DEFAULT_TTL_MINUTES
) {
    fun createSession(userId: String): AuthSession {
        val token = UUID.randomUUID().toString()
        val expiresAt = clock.now() + sessionTtlMinutes * MILLIS_PER_MINUTE
        storage.putString(KEY_USER_ID, userId)
        storage.putString(KEY_TOKEN, token)
        storage.putLong(KEY_EXPIRES_AT, expiresAt)
        return AuthSession(userId, token, expiresAt)
    }

    fun readSession(): AuthSession? {
        val userId = storage.getString(KEY_USER_ID)
        val token = storage.getString(KEY_TOKEN)
        val expiresAt = storage.getLong(KEY_EXPIRES_AT, 0L)
        return if (userId == null || token == null || expiresAt == 0L) {
            null
        } else {
            AuthSession(userId, token, expiresAt)
        }
    }

    fun isSessionValid(): Boolean {
        val session = readSession() ?: return false
        return session.expiresAtEpochMs > clock.now()
    }

    fun clearSession() {
        storage.remove(KEY_USER_ID)
        storage.remove(KEY_TOKEN)
        storage.remove(KEY_EXPIRES_AT)
    }

    companion object {
        private const val DEFAULT_TTL_MINUTES = 30L
        private const val MILLIS_PER_MINUTE = 60_000L
        private const val KEY_USER_ID = "auth_session_user_id"
        private const val KEY_TOKEN = "auth_session_token"
        private const val KEY_EXPIRES_AT = "auth_session_expires_at"
    }
}
