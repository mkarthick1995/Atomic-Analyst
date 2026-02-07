@file:Suppress("TooManyFunctions", "LongMethod")

package com.atomicanalyst.data.backup

import com.atomicanalyst.data.auth.AuthLocalStore
import com.atomicanalyst.data.db.dao.AccountDao
import com.atomicanalyst.data.db.dao.CategoryDao
import com.atomicanalyst.data.db.dao.ReconciliationLogDao
import com.atomicanalyst.data.db.dao.TagDao
import com.atomicanalyst.data.db.dao.TransactionDao
import com.atomicanalyst.data.db.dao.TransactionTagDao
import com.atomicanalyst.data.db.entity.AccountEntity
import com.atomicanalyst.data.db.entity.CategoryEntity
import com.atomicanalyst.data.db.entity.ReconciliationLogEntity
import com.atomicanalyst.data.db.entity.TagEntity
import com.atomicanalyst.data.db.entity.TagWithTransactions
import com.atomicanalyst.data.db.entity.TransactionEntity
import com.atomicanalyst.data.db.entity.TransactionTagCrossRef
import com.atomicanalyst.data.db.entity.TransactionWithTags
import com.atomicanalyst.data.security.InMemorySecureStorage
import com.atomicanalyst.domain.model.AccountType
import com.atomicanalyst.domain.model.ReconciliationAction
import com.atomicanalyst.domain.model.TransactionCategory
import com.atomicanalyst.domain.model.TransactionSource
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
    fun exportData_IncludesAuthAndData() = runBlocking {
        val storage = InMemorySecureStorage()
        val authStore = AuthLocalStore(storage)
        authStore.saveCredentials("user1", "hash123")
        authStore.setBiometricEnabled(true)
        val accountDao = FakeAccountDao()
        val categoryDao = FakeCategoryDao()
        val tagDao = FakeTagDao()
        val transactionDao = FakeTransactionDao()
        val logDao = FakeReconciliationLogDao()
        val transactionTagDao = FakeTransactionTagDao(transactionDao)
        accountDao.upsertAll(
            listOf(
                AccountEntity(
                    id = "acct-1",
                    name = "Primary",
                    accountNumber = "1234",
                    type = AccountType.SAVINGS_ACCOUNT,
                    institution = "Bank",
                    balanceCents = 100_00L,
                    currency = "USD",
                    isActive = true,
                    createdAtEpochMs = 1_000L,
                    updatedAtEpochMs = 2_000L
                )
            )
        )
        categoryDao.upsertAll(
            listOf(
                CategoryEntity(
                    id = "cat-1",
                    name = "Food",
                    type = TransactionCategory.FOOD,
                    isSystem = true,
                    createdAtEpochMs = 1_000L,
                    updatedAtEpochMs = 2_000L
                )
            )
        )
        tagDao.upsertAll(
            listOf(
                TagEntity(
                    id = "tag-1",
                    name = "Groceries",
                    createdAtEpochMs = 1_000L
                )
            )
        )
        transactionDao.upsertAll(
            listOf(
                TransactionEntity(
                    id = "txn-1",
                    accountId = "acct-1",
                    categoryId = "cat-1",
                    amountCents = 5000L,
                    currency = "USD",
                    description = "Market",
                    timestampEpochMs = 3_000L,
                    source = TransactionSource.MANUAL,
                    notes = "weekly",
                    isReconciled = false,
                    relatedTransactionId = null
                )
            )
        )
        transactionTagDao.upsertTagCrossRefs(
            listOf(
                TransactionTagCrossRef(
                    transactionId = "txn-1",
                    tagId = "tag-1"
                )
            )
        )
        logDao.upsertAll(
            listOf(
                ReconciliationLogEntity(
                    id = "log-1",
                    primaryTransactionId = "txn-1",
                    relatedTransactionIdsJson = "[\"txn-2\"]",
                    action = ReconciliationAction.MERGED,
                    timestampEpochMs = 4_000L,
                    userId = "user1",
                    reason = "merge"
                )
            )
        )
        val dataSource = AppBackupDataSource(
            authLocalStore = authStore,
            daos = BackupDaos(
                accountDao = accountDao,
                categoryDao = categoryDao,
                tagDao = tagDao,
                transactionDao = transactionDao,
                transactionTagDao = transactionTagDao,
                reconciliationLogDao = logDao
            ),
            runInTransaction = { block -> block() },
            clock = FixedClock(1_000L)
        )

        val bytes = dataSource.exportData()
        val payload = Json.decodeFromString<BackupPayload>(bytes.decodeToString())

        assertEquals(2, payload.version)
        assertEquals(1_000L, payload.createdAtEpochMs)
        assertNotNull(payload.auth)
        assertEquals("user1", payload.auth?.userId)
        assertEquals("hash123", payload.auth?.passwordHash)
        assertTrue(payload.auth?.biometricEnabled == true)
        assertEquals(1, payload.accounts.size)
        assertEquals(1, payload.categories.size)
        assertEquals(1, payload.tags.size)
        assertEquals(1, payload.transactions.size)
        assertEquals(1, payload.transactionTags.size)
        assertEquals(1, payload.reconciliationLogs.size)
    }

    @Test
    fun importData_RestoresAuthAndData() = runBlocking {
        val storage = InMemorySecureStorage()
        val authStore = AuthLocalStore(storage)
        val accountDao = FakeAccountDao()
        val categoryDao = FakeCategoryDao()
        val tagDao = FakeTagDao()
        val transactionDao = FakeTransactionDao()
        val logDao = FakeReconciliationLogDao()
        val transactionTagDao = FakeTransactionTagDao(transactionDao)
        val dataSource = AppBackupDataSource(
            authLocalStore = authStore,
            daos = BackupDaos(
                accountDao = accountDao,
                categoryDao = categoryDao,
                tagDao = tagDao,
                transactionDao = transactionDao,
                transactionTagDao = transactionTagDao,
                reconciliationLogDao = logDao
            ),
            runInTransaction = { block -> block() },
            clock = FixedClock(1_000L)
        )
        val payload = BackupPayload(
            version = 2,
            createdAtEpochMs = 1_000L,
            auth = AuthBackup(
                userId = "user1",
                passwordHash = "hash123",
                biometricEnabled = true
            ),
            accounts = listOf(
                AccountBackup(
                    id = "acct-1",
                    name = "Primary",
                    accountNumber = "1234",
                    type = AccountType.SAVINGS_ACCOUNT,
                    institution = "Bank",
                    balanceCents = 100_00L,
                    currency = "USD",
                    isActive = true,
                    createdAtEpochMs = 1_000L,
                    updatedAtEpochMs = 2_000L
                )
            ),
            categories = listOf(
                CategoryBackup(
                    id = "cat-1",
                    name = "Food",
                    type = TransactionCategory.FOOD,
                    isSystem = true,
                    createdAtEpochMs = 1_000L,
                    updatedAtEpochMs = 2_000L
                )
            ),
            tags = listOf(
                TagBackup(
                    id = "tag-1",
                    name = "Groceries",
                    createdAtEpochMs = 1_000L
                )
            ),
            transactions = listOf(
                TransactionBackup(
                    id = "txn-1",
                    accountId = "acct-1",
                    categoryId = "cat-1",
                    amountCents = 5000L,
                    currency = "USD",
                    description = "Market",
                    timestampEpochMs = 3_000L,
                    source = TransactionSource.MANUAL,
                    notes = "weekly",
                    isReconciled = false,
                    relatedTransactionId = null
                )
            ),
            transactionTags = listOf(
                TransactionTagBackup(
                    transactionId = "txn-1",
                    tagId = "tag-1"
                )
            ),
            reconciliationLogs = listOf(
                ReconciliationLogBackup(
                    id = "log-1",
                    primaryTransactionId = "txn-1",
                    relatedTransactionIds = listOf("txn-2"),
                    action = ReconciliationAction.MERGED,
                    timestampEpochMs = 4_000L,
                    userId = "user1",
                    reason = "merge"
                )
            )
        )

        dataSource.importData(Json.encodeToString(payload).encodeToByteArray())

        val creds = authStore.loadCredentials()
        assertNotNull(creds)
        assertEquals("user1", creds?.userId)
        assertEquals("hash123", creds?.passwordHash)
        assertTrue(authStore.isBiometricEnabled())
        assertEquals(1, accountDao.getAll().size)
        assertEquals(1, categoryDao.getAll().size)
        assertEquals(1, tagDao.getAll().size)
        assertEquals(1, transactionDao.getAll().size)
        assertEquals(1, transactionTagDao.getAllTagCrossRefs().size)
        assertEquals(1, logDao.getAll().size)
    }
}

private class FakeAccountDao : AccountDao {
    private val items = linkedMapOf<String, AccountEntity>()

    override suspend fun upsert(account: AccountEntity) {
        items[account.id] = account
    }

    override suspend fun upsertAll(accounts: List<AccountEntity>) {
        accounts.forEach { items[it.id] = it }
    }

    override suspend fun update(account: AccountEntity) {
        items[account.id] = account
    }

    override suspend fun delete(account: AccountEntity) {
        items.remove(account.id)
    }

    override suspend fun getById(id: String): AccountEntity? = items[id]

    override suspend fun getAll(): List<AccountEntity> = items.values.toList()

    override fun observeAll() = throw UnsupportedOperationException("Not needed in test")

    override suspend fun clearAll() {
        items.clear()
    }
}

private class FakeCategoryDao : CategoryDao {
    private val items = linkedMapOf<String, CategoryEntity>()

    override suspend fun upsert(category: CategoryEntity) {
        items[category.id] = category
    }

    override suspend fun upsertAll(categories: List<CategoryEntity>) {
        categories.forEach { items[it.id] = it }
    }

    override suspend fun update(category: CategoryEntity) {
        items[category.id] = category
    }

    override suspend fun delete(category: CategoryEntity) {
        items.remove(category.id)
    }

    override suspend fun getById(id: String): CategoryEntity? = items[id]

    override suspend fun getAll(): List<CategoryEntity> = items.values.toList()

    override fun observeAll() = throw UnsupportedOperationException("Not needed in test")

    override suspend fun clearAll() {
        items.clear()
    }
}

private class FakeTagDao : TagDao {
    private val items = linkedMapOf<String, TagEntity>()

    override suspend fun upsert(tag: TagEntity) {
        items[tag.id] = tag
    }

    override suspend fun upsertAll(tags: List<TagEntity>) {
        tags.forEach { items[it.id] = it }
    }

    override suspend fun update(tag: TagEntity) {
        items[tag.id] = tag
    }

    override suspend fun delete(tag: TagEntity) {
        items.remove(tag.id)
    }

    override suspend fun getById(id: String): TagEntity? = items[id]

    override suspend fun getAll(): List<TagEntity> = items.values.toList()

    override fun observeAll() = throw UnsupportedOperationException("Not needed in test")

    override suspend fun clearAll() {
        items.clear()
    }
}

private class FakeTransactionDao : TransactionDao {
    private val items = linkedMapOf<String, TransactionEntity>()

    override suspend fun upsert(transaction: TransactionEntity) {
        items[transaction.id] = transaction
    }

    override suspend fun upsertAll(transactions: List<TransactionEntity>) {
        transactions.forEach { items[it.id] = it }
    }

    override suspend fun update(transaction: TransactionEntity) {
        items[transaction.id] = transaction
    }

    override suspend fun delete(transaction: TransactionEntity) {
        items.remove(transaction.id)
    }

    override suspend fun getById(id: String): TransactionEntity? = items[id]

    override suspend fun getAll(): List<TransactionEntity> = items.values.toList()

    override fun observeAll() = throw UnsupportedOperationException("Not needed in test")

    override suspend fun clearAll() {
        items.clear()
    }
}

private class FakeTransactionTagDao(
    private val transactionDao: FakeTransactionDao
) : TransactionTagDao {
    private val crossRefs = mutableListOf<TransactionTagCrossRef>()

    override suspend fun upsertTagCrossRefs(refs: List<TransactionTagCrossRef>) {
        crossRefs.addAll(refs)
    }

    override suspend fun clearTagsForTransaction(transactionId: String) {
        crossRefs.removeAll { it.transactionId == transactionId }
    }

    override suspend fun getAllTagCrossRefs(): List<TransactionTagCrossRef> = crossRefs.toList()

    override suspend fun clearAllTagCrossRefs() {
        crossRefs.clear()
    }

    override suspend fun getWithTags(id: String): TransactionWithTags? {
        val transaction = transactionDao.getById(id) ?: return null
        val tags = emptyList<TagEntity>()
        return TransactionWithTags(
            transaction = transaction,
            tags = tags
        )
    }

    override fun observeAllWithTags() =
        throw UnsupportedOperationException("Not needed in test")

    override suspend fun getTagWithTransactions(id: String): TagWithTransactions? =
        throw UnsupportedOperationException("Not needed in test")

    override fun observeAllTagsWithTransactions() =
        throw UnsupportedOperationException("Not needed in test")
}

private class FakeReconciliationLogDao : ReconciliationLogDao {
    private val items = linkedMapOf<String, ReconciliationLogEntity>()

    override suspend fun upsert(log: ReconciliationLogEntity) {
        items[log.id] = log
    }

    override suspend fun upsertAll(logs: List<ReconciliationLogEntity>) {
        logs.forEach { items[it.id] = it }
    }

    override suspend fun getAll(): List<ReconciliationLogEntity> = items.values.toList()

    override fun observeLogs(transactionId: String) =
        throw UnsupportedOperationException("Not needed in test")

    override suspend fun clearAll() {
        items.clear()
    }
}
