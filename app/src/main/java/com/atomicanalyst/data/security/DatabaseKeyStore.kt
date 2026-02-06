package com.atomicanalyst.data.security

import java.security.SecureRandom
import java.util.Base64

class DatabaseKeyStore(
    private val storage: SecureStorage,
    private val secureRandom: SecureRandom = SecureRandom()
) {
    fun getOrCreateKey(): ByteArray {
        val existing = storage.getString(KEY_DB_KEY)
        if (existing != null) {
            return Base64.getDecoder().decode(existing)
        }
        val key = ByteArray(KEY_BYTES).also { secureRandom.nextBytes(it) }
        storage.putString(KEY_DB_KEY, Base64.getEncoder().encodeToString(key))
        return key
    }

    fun rotateKey() {
        storage.remove(KEY_DB_KEY)
    }

    companion object {
        private const val KEY_DB_KEY = "db_cipher_key"
        private const val KEY_BYTES = 32
    }
}
