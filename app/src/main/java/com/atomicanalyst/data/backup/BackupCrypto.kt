package com.atomicanalyst.data.backup

import com.atomicanalyst.utils.Clock
import com.atomicanalyst.utils.SystemClock
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

class BackupCrypto(
    private val secureRandom: SecureRandom = SecureRandom(),
    private val clock: Clock = SystemClock
) {
    fun encrypt(plain: ByteArray, passphrase: CharArray): BackupEnvelope {
        val salt = ByteArray(SALT_BYTES).also { secureRandom.nextBytes(it) }
        val key = deriveKey(passphrase, salt)
        val iv = ByteArray(IV_BYTES).also { secureRandom.nextBytes(it) }
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(TAG_BITS, iv))
        val cipherText = cipher.doFinal(plain)
        val checksum = MessageDigest.getInstance(CHECKSUM_ALGO).digest(plain)
        return BackupEnvelope(
            version = VERSION,
            createdAtEpochMs = clock.now(),
            salt = salt,
            iv = iv,
            cipherText = cipherText,
            checksum = checksum
        )
    }

    fun decrypt(envelope: BackupEnvelope, passphrase: CharArray): ByteArray {
        val key = deriveKey(passphrase, envelope.salt)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(TAG_BITS, envelope.iv))
        val plain = cipher.doFinal(envelope.cipherText)
        val checksum = MessageDigest.getInstance(CHECKSUM_ALGO).digest(plain)
        if (!MessageDigest.isEqual(checksum, envelope.checksum)) {
            throw SecurityException("Backup checksum mismatch")
        }
        return plain
    }

    private fun deriveKey(passphrase: CharArray, salt: ByteArray): SecretKeySpec {
        val spec = PBEKeySpec(passphrase, salt, ITERATIONS, KEY_LENGTH_BITS)
        val factory = SecretKeyFactory.getInstance(KEY_DERIVATION)
        val keyBytes = factory.generateSecret(spec).encoded
        return SecretKeySpec(keyBytes, KEY_ALGORITHM)
    }

    companion object {
        private const val VERSION = 1
        private const val SALT_BYTES = 16
        private const val IV_BYTES = 12
        private const val TAG_BITS = 128
        private const val ITERATIONS = 150_000
        private const val KEY_LENGTH_BITS = 256
        private const val KEY_DERIVATION = "PBKDF2WithHmacSHA256"
        private const val KEY_ALGORITHM = "AES"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val CHECKSUM_ALGO = "SHA-256"
    }
}
