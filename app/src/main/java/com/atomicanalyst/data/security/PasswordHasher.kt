package com.atomicanalyst.data.security

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

private const val PASSWORD_HASH_DEFAULT_ALGORITHM = "PBKDF2WithHmacSHA256"
private const val PASSWORD_HASH_DEFAULT_ITERATIONS = 120_000
private const val PASSWORD_HASH_KEY_LENGTH_BITS = 256
private const val PASSWORD_HASH_SALT_BYTES = 16
private const val PASSWORD_HASH_SEPARATOR = "|"
private const val PASSWORD_HASH_PARTS_COUNT = 5
private const val PASSWORD_HASH_INDEX_ALGORITHM = 0
private const val PASSWORD_HASH_INDEX_ITERATIONS = 1
private const val PASSWORD_HASH_INDEX_KEY_LENGTH = 2
private const val PASSWORD_HASH_INDEX_SALT = 3
private const val PASSWORD_HASH_INDEX_HASH = 4

data class PasswordHash(
    val hash: ByteArray,
    val salt: ByteArray,
    val iterations: Int,
    val keyLengthBits: Int = PASSWORD_HASH_KEY_LENGTH_BITS,
    val algorithm: String = PASSWORD_HASH_DEFAULT_ALGORITHM
)

class PasswordHasher(
    private val secureRandom: SecureRandom = SecureRandom()
) {
    fun hash(password: CharArray, iterations: Int = PASSWORD_HASH_DEFAULT_ITERATIONS): PasswordHash {
        val salt = ByteArray(PASSWORD_HASH_SALT_BYTES).also { secureRandom.nextBytes(it) }
        val hash = deriveKey(password, salt, iterations, PASSWORD_HASH_KEY_LENGTH_BITS)
        return PasswordHash(hash, salt, iterations)
    }

    fun verify(password: CharArray, stored: PasswordHash): Boolean {
        val hash = deriveKey(password, stored.salt, stored.iterations, stored.keyLengthBits)
        return MessageDigest.isEqual(hash, stored.hash)
    }

    fun encode(stored: PasswordHash): String {
        val encoder = Base64.getEncoder()
        val saltB64 = encoder.encodeToString(stored.salt)
        val hashB64 = encoder.encodeToString(stored.hash)
        return listOf(
            stored.algorithm,
            stored.iterations.toString(),
            stored.keyLengthBits.toString(),
            saltB64,
            hashB64
        ).joinToString(PASSWORD_HASH_SEPARATOR)
    }

    fun decode(encoded: String): PasswordHash {
        val parts = encoded.split(PASSWORD_HASH_SEPARATOR)
        require(parts.size == PASSWORD_HASH_PARTS_COUNT) { "Invalid password hash format" }
        val algorithm = parts[PASSWORD_HASH_INDEX_ALGORITHM]
        val iterations = parts[PASSWORD_HASH_INDEX_ITERATIONS].toInt()
        val keyLength = parts[PASSWORD_HASH_INDEX_KEY_LENGTH].toInt()
        val decoder = Base64.getDecoder()
        val salt = decoder.decode(parts[PASSWORD_HASH_INDEX_SALT])
        val hash = decoder.decode(parts[PASSWORD_HASH_INDEX_HASH])
        return PasswordHash(hash, salt, iterations, keyLength, algorithm)
    }

    private fun deriveKey(
        password: CharArray,
        salt: ByteArray,
        iterations: Int,
        keyLengthBits: Int
    ): ByteArray {
        val spec = PBEKeySpec(password, salt, iterations, keyLengthBits)
        val factory = SecretKeyFactory.getInstance(PASSWORD_HASH_DEFAULT_ALGORITHM)
        return factory.generateSecret(spec).encoded
    }
}
