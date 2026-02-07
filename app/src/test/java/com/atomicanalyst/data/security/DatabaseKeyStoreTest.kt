package com.atomicanalyst.data.security

import com.atomicanalyst.utils.FixedClock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.security.SecureRandom

class DatabaseKeyStoreTest {
    @Test
    fun getOrCreateKey_PersistsTimestamp() {
        val storage = InMemorySecureStorage()
        val clock = FixedClock(1_000L)
        val store = DatabaseKeyStore(storage, clock, TestSecureRandom())

        val key = store.getOrCreateKey()
        val status = store.rotationStatus(KeyRotationPolicy(rotationIntervalDays = 1))

        assertEquals(32, key.size)
        assertEquals(1_000L, status.lastRotatedEpochMs)
    }

    @Test
    fun rotateKey_ChangesKeyAndUpdatesTimestamp() {
        val storage = InMemorySecureStorage()
        val clock = FixedClock(1_000L)
        val store = DatabaseKeyStore(storage, clock, TestSecureRandom())

        val first = store.getOrCreateKey()
        clock.advance(DAY_MS + 1)
        val rotated = store.rotateKey()
        val status = store.rotationStatus(KeyRotationPolicy(rotationIntervalDays = 1))

        assertFalse(first.contentEquals(rotated))
        assertEquals(clock.now(), status.lastRotatedEpochMs)
        assertFalse(status.isDue)
    }

    @Test
    fun rotationStatus_ReportsDueWhenIntervalPassed() {
        val storage = InMemorySecureStorage()
        val clock = FixedClock(1_000L)
        val store = DatabaseKeyStore(storage, clock, TestSecureRandom())
        val policy = KeyRotationPolicy(rotationIntervalDays = 1)

        store.getOrCreateKey()
        clock.advance(DAY_MS + 1)

        val status = store.rotationStatus(policy)

        assertTrue(status.isDue)
    }

    companion object {
        private const val DAY_MS = 24L * 60L * 60L * 1000L
    }
}

private class TestSecureRandom : SecureRandom() {
    private var counter = 0

    override fun nextBytes(bytes: ByteArray) {
        for (i in bytes.indices) {
            bytes[i] = counter.toByte()
            counter++
        }
    }
}
