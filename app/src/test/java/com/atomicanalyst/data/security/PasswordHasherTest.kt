package com.atomicanalyst.data.security

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PasswordHasherTest {
    @Test
    fun hashAndVerify_Succeeds() {
        val hasher = PasswordHasher()
        val password = "Password1".toCharArray()
        val hash = hasher.hash(password)

        assertTrue(hasher.verify("Password1".toCharArray(), hash))
    }

    @Test
    fun verify_FailsForWrongPassword() {
        val hasher = PasswordHasher()
        val hash = hasher.hash("Password1".toCharArray())

        assertFalse(hasher.verify("WrongPass1".toCharArray(), hash))
    }

    @Test
    fun encodeDecode_RoundTrip() {
        val hasher = PasswordHasher()
        val hash = hasher.hash("Password1".toCharArray())
        val encoded = hasher.encode(hash)
        val decoded = hasher.decode(encoded)

        assertTrue(hasher.verify("Password1".toCharArray(), decoded))
    }
}
