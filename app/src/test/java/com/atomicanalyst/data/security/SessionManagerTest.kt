package com.atomicanalyst.data.security

import com.atomicanalyst.utils.FixedClock
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SessionManagerTest {
    @Test
    fun createSession_PersistsAndValidates() {
        val storage = InMemorySecureStorage()
        val clock = FixedClock(1_000L)
        val manager = SessionManager(storage, clock, sessionTtlMinutes = 1)

        val session = manager.createSession("user")

        assertNotNull(manager.readSession())
        assertTrue(manager.isSessionValid())
        assertTrue(session.expiresAtEpochMs > clock.now())
    }

    @Test
    fun session_Expires() {
        val storage = InMemorySecureStorage()
        val clock = FixedClock(1_000L)
        val manager = SessionManager(storage, clock, sessionTtlMinutes = 1)

        manager.createSession("user")
        clock.advance(61_000L)

        assertFalse(manager.isSessionValid())
    }
}
