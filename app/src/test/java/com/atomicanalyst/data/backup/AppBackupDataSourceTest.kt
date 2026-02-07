package com.atomicanalyst.data.backup

import com.atomicanalyst.data.auth.AuthLocalStore
import com.atomicanalyst.data.security.InMemorySecureStorage
import com.atomicanalyst.utils.FixedClock
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AppBackupDataSourceTest {
    @Test
    fun exportData_IncludesAuthWhenPresent() = runBlocking {
        val storage = InMemorySecureStorage()
        val authStore = AuthLocalStore(storage)
        authStore.saveCredentials("user1", "hash123")
        authStore.setBiometricEnabled(true)
        val dataSource = AppBackupDataSource(authStore, FixedClock(1_000L))

        val bytes = dataSource.exportData()
        val payload = Json.decodeFromString<BackupPayload>(bytes.decodeToString())

        assertEquals(1, payload.version)
        assertEquals(1_000L, payload.createdAtEpochMs)
        assertNotNull(payload.auth)
        assertEquals("user1", payload.auth?.userId)
        assertEquals("hash123", payload.auth?.passwordHash)
        assertTrue(payload.auth?.biometricEnabled == true)
    }

    @Test
    fun importData_RestoresAuth() = runBlocking {
        val storage = InMemorySecureStorage()
        val authStore = AuthLocalStore(storage)
        val dataSource = AppBackupDataSource(authStore, FixedClock(1_000L))
        val payload = BackupPayload(
            version = 1,
            createdAtEpochMs = 1_000L,
            auth = AuthBackup(
                userId = "user1",
                passwordHash = "hash123",
                biometricEnabled = true
            )
        )

        dataSource.importData(Json.encodeToString(payload).encodeToByteArray())

        val creds = authStore.loadCredentials()
        assertNotNull(creds)
        assertEquals("user1", creds?.userId)
        assertEquals("hash123", creds?.passwordHash)
        assertTrue(authStore.isBiometricEnabled())
    }
}
