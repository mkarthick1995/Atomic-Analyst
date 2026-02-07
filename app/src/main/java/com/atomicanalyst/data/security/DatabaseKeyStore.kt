package com.atomicanalyst.data.security

import com.atomicanalyst.utils.Clock
import java.security.SecureRandom
import java.util.Base64

class DatabaseKeyStore(
    private val storage: SecureStorage,
    private val clock: Clock,
    private val secureRandom: SecureRandom = SecureRandom()
) {
    fun getOrCreateKey(): ByteArray {
        val existing = storage.getString(KEY_DB_KEY)
        if (existing != null) {
            ensureRotationTimestamp()
            return Base64.getDecoder().decode(existing)
        }
        return rotateKey()
    }

    fun rotateKey(): ByteArray {
        val key = ByteArray(KEY_BYTES).also { secureRandom.nextBytes(it) }
        storage.putString(KEY_DB_KEY, Base64.getEncoder().encodeToString(key))
        storage.putLong(KEY_DB_KEY_ROTATED_AT, clock.now())
        return key
    }

    fun rotationStatus(policy: KeyRotationPolicy): RotationStatus {
        val lastRotated = storage.getLong(KEY_DB_KEY_ROTATED_AT, 0L)
        val nextRotation = if (lastRotated == 0L) 0L else policy.nextRotationEpochMs(lastRotated)
        val due = policy.isRotationDue(lastRotated, clock.now())
        return RotationStatus(lastRotated, nextRotation, due)
    }

    private fun ensureRotationTimestamp() {
        val lastRotated = storage.getLong(KEY_DB_KEY_ROTATED_AT, 0L)
        if (lastRotated == 0L) {
            storage.putLong(KEY_DB_KEY_ROTATED_AT, clock.now())
        }
    }

    companion object {
        private const val KEY_DB_KEY = "db_cipher_key"
        private const val KEY_DB_KEY_ROTATED_AT = "db_cipher_key_rotated_at"
        private const val KEY_BYTES = 32
    }
}
