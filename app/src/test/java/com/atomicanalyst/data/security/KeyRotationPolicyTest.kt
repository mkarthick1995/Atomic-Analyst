package com.atomicanalyst.data.security

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class KeyRotationPolicyTest {
    @Test
    fun isRotationDue_ReturnsFalseWhenNeverRotated() {
        val policy = KeyRotationPolicy(rotationIntervalDays = 1)

        assertFalse(policy.isRotationDue(0L, DAY_MS))
    }

    @Test
    fun isRotationDue_ReturnsTrueAfterInterval() {
        val policy = KeyRotationPolicy(rotationIntervalDays = 1)
        val lastRotated = 1_000L
        val now = lastRotated + DAY_MS + 1

        assertTrue(policy.isRotationDue(lastRotated, now))
    }

    companion object {
        private const val DAY_MS = 24L * 60L * 60L * 1000L
    }
}
