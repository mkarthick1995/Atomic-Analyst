package com.atomicanalyst.data.backup

import com.atomicanalyst.data.BaseRepository
import com.atomicanalyst.data.backup.cloud.CloudBackupClient
import com.atomicanalyst.data.backup.cloud.CloudBackupEntry
import com.atomicanalyst.domain.error.AppException
import com.atomicanalyst.domain.model.Result
import com.atomicanalyst.utils.Clock
import com.atomicanalyst.utils.DispatcherProvider
import kotlinx.coroutines.withContext
import java.io.File

class BackupManager(
    private val dataSource: BackupDataSource,
    private val crypto: BackupCrypto,
    private val store: BackupStore,
    private val passphraseStore: BackupPassphraseStore,
    private val dispatcherProvider: DispatcherProvider,
    private val clock: Clock
) : BaseRepository() {
    suspend fun createBackup(passphrase: CharArray? = null): Result<File> = safeCall {
        val resolved = resolvePassphrase(passphrase)
        try {
            withContext(dispatcherProvider.io) {
                val payload = dataSource.exportData()
                val envelope = crypto.encrypt(payload, resolved)
                val file = store.createBackupFile(envelope.createdAtEpochMs)
                BackupFileCodec.write(file, envelope)
                store.deleteBackupsOlderThan(clock.now() - RETENTION_DAYS * MILLIS_PER_DAY)
                file
            }
        } finally {
            resolved.fill('\u0000')
        }
    }.also { passphrase?.fill('\u0000') }

    suspend fun restoreBackup(file: File, passphrase: CharArray? = null): Result<Unit> = safeCall {
        val resolved = resolvePassphrase(passphrase)
        try {
            withContext(dispatcherProvider.io) {
                val envelope = BackupFileCodec.read(file)
                val plain = crypto.decrypt(envelope, resolved)
                dataSource.importData(plain)
            }
        } finally {
            resolved.fill('\u0000')
        }
    }.also { passphrase?.fill('\u0000') }

    suspend fun verifyBackup(file: File, passphrase: CharArray? = null): Result<Boolean> = safeCall {
        val resolved = resolvePassphrase(passphrase)
        try {
            withContext(dispatcherProvider.io) {
                val envelope = BackupFileCodec.read(file)
                crypto.decrypt(envelope, resolved)
                true
            }
        } finally {
            resolved.fill('\u0000')
        }
    }.also { passphrase?.fill('\u0000') }

    fun listBackups(): List<File> = store.listBackups()

    suspend fun uploadToCloud(
        client: CloudBackupClient,
        passphrase: CharArray? = null
    ): Result<CloudBackupEntry> {
        return when (val local = createBackup(passphrase)) {
            is Result.Success -> client.uploadBackup(local.data)
            is Result.Error -> Result.Error(local.exception)
            Result.Loading -> Result.Loading
        }
    }

    suspend fun restoreLatestFromCloud(
        client: CloudBackupClient,
        passphrase: CharArray? = null
    ): Result<Unit> {
        return when (val cloud = client.listBackups()) {
            is Result.Success -> {
                val entry = cloud.data.maxByOrNull { it.modifiedTimeEpochMs }
                    ?: return Result.Error(AppException.Validation("No cloud backups found"))
                val destination = store.createBackupFile(entry.modifiedTimeEpochMs)
                when (val download = client.downloadBackup(entry.id, destination)) {
                    is Result.Success -> restoreBackup(download.data, passphrase)
                    is Result.Error -> Result.Error(download.exception)
                    Result.Loading -> Result.Loading
                }
            }
            is Result.Error -> Result.Error(cloud.exception)
            Result.Loading -> Result.Loading
        }
    }

    private fun resolvePassphrase(passphrase: CharArray?): CharArray {
        return passphrase
            ?: passphraseStore.load()
            ?: throw AppException.Security("Backup passphrase not set")
    }

    companion object {
        private const val RETENTION_DAYS = 30L
        private const val MILLIS_PER_DAY = 24L * 60L * 60L * 1000L
    }
}
