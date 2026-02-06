package com.atomicanalyst.data.auth

import com.atomicanalyst.data.security.SecureStorage

data class StoredCredentials(
    val userId: String,
    val passwordHash: String
)

class AuthLocalStore(
    private val storage: SecureStorage
) {
    fun saveCredentials(userId: String, encodedHash: String) {
        storage.putString(KEY_USER_ID, userId)
        storage.putString(KEY_PASSWORD_HASH, encodedHash)
    }

    fun loadCredentials(): StoredCredentials? {
        val userId = storage.getString(KEY_USER_ID)
        val hash = storage.getString(KEY_PASSWORD_HASH)
        return if (userId == null || hash == null) {
            null
        } else {
            StoredCredentials(userId, hash)
        }
    }

    fun setBiometricEnabled(enabled: Boolean) {
        storage.putBoolean(KEY_BIOMETRIC_ENABLED, enabled)
    }

    fun isBiometricEnabled(): Boolean = storage.getBoolean(KEY_BIOMETRIC_ENABLED, false)

    fun clearCredentials() {
        storage.remove(KEY_USER_ID)
        storage.remove(KEY_PASSWORD_HASH)
        storage.remove(KEY_BIOMETRIC_ENABLED)
    }

    companion object {
        private const val KEY_USER_ID = "auth_user_id"
        private const val KEY_PASSWORD_HASH = "auth_password_hash"
        private const val KEY_BIOMETRIC_ENABLED = "auth_biometric_enabled"
    }
}
