package com.atomicanalyst.data.auth

import com.atomicanalyst.data.security.InMemorySecureStorage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthLocalStoreTest {
    @Test
    fun saveAndLoadCredentials_PersistsValues() {
        val storage = InMemorySecureStorage()
        val store = AuthLocalStore(storage)

        store.saveCredentials("user1", "hash123")

        val creds = store.loadCredentials()
        assertNotNull(creds)
        assertEquals("user1", creds?.userId)
        assertEquals("hash123", creds?.passwordHash)
    }

    @Test
    fun biometricFlag_DefaultsToFalseAndPersists() {
        val storage = InMemorySecureStorage()
        val store = AuthLocalStore(storage)

        assertFalse(store.isBiometricEnabled())
        store.setBiometricEnabled(true)
        assertTrue(store.isBiometricEnabled())
    }
}
