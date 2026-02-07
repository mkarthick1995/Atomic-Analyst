package com.atomicanalyst.data.backup

import com.atomicanalyst.data.security.InMemorySecureStorage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BackupPassphraseStoreTest {
    @Test
    fun saveLoadClear_WorksAsExpected() {
        val store = BackupPassphraseStore(InMemorySecureStorage())
        assertFalse(store.isSet())

        store.save("secret".toCharArray())
        assertTrue(store.isSet())
        assertEquals("secret", store.load()?.concatToString())

        store.clear()
        assertFalse(store.isSet())
        assertNull(store.load())
    }
}
