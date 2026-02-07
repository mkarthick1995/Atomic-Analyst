package com.atomicanalyst.data.backup

import com.atomicanalyst.data.auth.AuthLocalStore
import com.atomicanalyst.data.db.dao.AccountDao
import com.atomicanalyst.data.db.dao.CategoryDao
import com.atomicanalyst.data.db.dao.ReconciliationLogDao
import com.atomicanalyst.data.db.dao.TagDao
import com.atomicanalyst.data.db.dao.TransactionDao
import com.atomicanalyst.data.db.dao.TransactionTagDao
import com.atomicanalyst.domain.error.AppException
import com.atomicanalyst.utils.Clock
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

data class BackupDaos(
    val accountDao: AccountDao,
    val categoryDao: CategoryDao,
    val tagDao: TagDao,
    val transactionDao: TransactionDao,
    val transactionTagDao: TransactionTagDao,
    val reconciliationLogDao: ReconciliationLogDao
)

class AppBackupDataSource(
    private val authLocalStore: AuthLocalStore,
    private val daos: BackupDaos,
    private val runInTransaction: suspend (suspend () -> Unit) -> Unit,
    private val clock: Clock
) : BackupDataSource {
    override suspend fun exportData(): ByteArray {
        val credentials = authLocalStore.loadCredentials()
        val auth = credentials?.let {
            AuthBackup(
                userId = it.userId,
                passwordHash = it.passwordHash,
                biometricEnabled = authLocalStore.isBiometricEnabled()
            )
        }
        val payload = BackupPayload(
            version = CURRENT_VERSION,
            createdAtEpochMs = clock.now(),
            auth = auth,
            accounts = daos.accountDao.getAll().map { it.toBackup() },
            categories = daos.categoryDao.getAll().map { it.toBackup() },
            tags = daos.tagDao.getAll().map { it.toBackup() },
            transactions = daos.transactionDao.getAll().map { it.toBackup() },
            transactionTags = daos.transactionTagDao.getAllTagCrossRefs().map { it.toBackup() },
            reconciliationLogs = daos.reconciliationLogDao.getAll().map { it.toBackup() }
        )
        return Json.encodeToString(payload).encodeToByteArray()
    }

    override suspend fun importData(data: ByteArray) {
        val payload = try {
            Json.decodeFromString<BackupPayload>(data.decodeToString())
        } catch (exception: SerializationException) {
            throw AppException.Validation("Invalid backup payload", exception)
        } catch (exception: IllegalArgumentException) {
            throw AppException.Validation("Invalid backup payload", exception)
        }
        if (payload.version != CURRENT_VERSION) {
            throw AppException.Validation("Unsupported backup version")
        }
        runInTransaction {
            daos.reconciliationLogDao.clearAll()
            daos.transactionTagDao.clearAllTagCrossRefs()
            daos.transactionDao.clearAll()
            daos.tagDao.clearAll()
            daos.categoryDao.clearAll()
            daos.accountDao.clearAll()

            daos.accountDao.upsertAll(payload.accounts.map { it.toEntity() })
            daos.categoryDao.upsertAll(payload.categories.map { it.toEntity() })
            daos.tagDao.upsertAll(payload.tags.map { it.toEntity() })
            daos.transactionDao.upsertAll(payload.transactions.map { it.toEntity() })
            daos.transactionTagDao.upsertTagCrossRefs(
                payload.transactionTags.map { it.toEntity() }
            )
            daos.reconciliationLogDao.upsertAll(
                payload.reconciliationLogs.map { it.toEntity() }
            )
        }
        val auth = payload.auth ?: return
        authLocalStore.saveCredentials(auth.userId, auth.passwordHash)
        authLocalStore.setBiometricEnabled(auth.biometricEnabled)
    }

    companion object {
        private const val CURRENT_VERSION = 2
    }
}
