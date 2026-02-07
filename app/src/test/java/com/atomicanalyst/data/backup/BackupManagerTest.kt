package com.atomicanalyst.data.backup

import com.atomicanalyst.data.security.InMemorySecureStorage
import com.atomicanalyst.data.backup.cloud.CloudBackupClient
import com.atomicanalyst.data.backup.cloud.CloudBackupEntry
import com.atomicanalyst.domain.model.Result
import com.atomicanalyst.utils.FixedClock
import com.atomicanalyst.utils.TestDispatcherProvider
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
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

    @Test
    fun uploadToCloud_UploadsLatestBackup() = runBlocking {
        val tempDir = Files.createTempDirectory("backupcloud").toFile()
        val storage = InMemorySecureStorage()
        val passphraseStore = BackupPassphraseStore(storage)
        passphraseStore.save("Password1".toCharArray())

        val manager = BackupManager(
            dataSource = InMemoryBackupDataSource("payload".encodeToByteArray()),
            crypto = BackupCrypto(clock = FixedClock(1_000L)),
            store = BackupStore(tempDir),
            passphraseStore = passphraseStore,
            dispatcherProvider = TestDispatcherProvider(),
            clock = FixedClock(1_000L)
        )
        val client = FakeCloudBackupClient()

        val result = manager.uploadToCloud(client)

        assertTrue(result is Result.Success)
        assertTrue(client.uploadedFile != null)
    }

    @Test
    fun restoreLatestFromCloud_RestoresBackup() = runBlocking {
        val tempDir = Files.createTempDirectory("backupcloudrestore").toFile()
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
        val backupFile = (created as Result.Success).data
        val client = FakeCloudBackupClient().apply {
            val entry = CloudBackupEntry("cloud-1", backupFile.name, 1_000L)
            entries = listOf(entry)
            downloads[entry.id] = backupFile.readBytes()
        }

        dataSource.data = "changed".encodeToByteArray()
        val restore = manager.restoreLatestFromCloud(client)

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

private class FakeCloudBackupClient : CloudBackupClient {
    var entries: List<CloudBackupEntry> = emptyList()
    val downloads: MutableMap<String, ByteArray> = mutableMapOf()
    var uploadedFile: File? = null

    override suspend fun uploadBackup(file: File): Result<CloudBackupEntry> {
        uploadedFile = file
        val entry = CloudBackupEntry("cloud-upload", file.name, 1_000L)
        return Result.Success(entry)
    }

    override suspend fun listBackups(): Result<List<CloudBackupEntry>> =
        Result.Success(entries)

    override suspend fun downloadBackup(fileId: String, destination: File): Result<File> {
        val bytes = downloads[fileId] ?: return Result.Error(
            com.atomicanalyst.domain.error.AppException.Validation("Missing cloud backup")
        )
        destination.writeBytes(bytes)
        return Result.Success(destination)
    }
}
