package com.atomicanalyst.data.backup

import com.atomicanalyst.data.security.InMemorySecureStorage
import com.atomicanalyst.domain.model.Result
import com.atomicanalyst.utils.FixedClock
import com.atomicanalyst.utils.TestDispatcherProvider
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test
import java.nio.file.Files

class BackupManagerTest {
    @Test
    fun createBackup_WritesFile() = runBlocking {
        val tempDir = Files.createTempDirectory("backupmanager").toFile()
        val storage = InMemorySecureStorage()
        val passphraseStore = BackupPassphraseStore(storage)
        passphraseStore.save("Password1".toCharArray())

        val manager = BackupManager(
            dataSource = InMemoryBackupDataSource(),
            crypto = BackupCrypto(clock = FixedClock(1_000L)),
            store = BackupStore(tempDir),
            passphraseStore = passphraseStore,
            dispatcherProvider = TestDispatcherProvider(),
            clock = FixedClock(1_000L)
        )

        val result = manager.createBackup()
        assertTrue(result is Result.Success)
        assertTrue(tempDir.listFiles()?.isNotEmpty() == true)
    }

    @Test
    fun restoreBackup_RoundTrip() = runBlocking {
        val tempDir = Files.createTempDirectory("backuprestore").toFile()
        val storage = InMemorySecureStorage()
        val passphraseStore = BackupPassphraseStore(storage)
        passphraseStore.save("Password1".toCharArray())
        val dataSource = InMemoryBackupDataSource("payload".encodeToByteArray())

        val manager = BackupManager(
            dataSource = dataSource,
            crypto = BackupCrypto(clock = FixedClock(1_000L)),
            store = BackupStore(tempDir),
            passphraseStore = passphraseStore,
            dispatcherProvider = TestDispatcherProvider(),
            clock = FixedClock(1_000L)
        )

        val created = manager.createBackup()
        assertTrue(created is Result.Success)
        dataSource.data = "changed".encodeToByteArray()

        val restore = manager.restoreBackup((created as Result.Success).data)
        assertTrue(restore is Result.Success)
        assertTrue(dataSource.data.contentEquals("payload".encodeToByteArray()))
    }
}

private class InMemoryBackupDataSource(
    initial: ByteArray = ByteArray(0)
) : BackupDataSource {
    var data: ByteArray = initial

    override suspend fun exportData(): ByteArray = data

    override suspend fun importData(data: ByteArray) {
        this.data = data
    }
}
