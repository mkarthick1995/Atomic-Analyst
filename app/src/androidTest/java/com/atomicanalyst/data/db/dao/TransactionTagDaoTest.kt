package com.atomicanalyst.data.db.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.atomicanalyst.data.db.AtomicAnalystDatabase
import com.atomicanalyst.data.db.entity.AccountEntity
import com.atomicanalyst.data.db.entity.CategoryEntity
import com.atomicanalyst.data.db.entity.TagEntity
import com.atomicanalyst.data.db.entity.TransactionEntity
import com.atomicanalyst.data.db.entity.TransactionTagCrossRef
import com.atomicanalyst.domain.model.AccountType
import com.atomicanalyst.domain.model.TransactionCategory
import com.atomicanalyst.domain.model.TransactionSource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TransactionTagDaoTest {
    private lateinit var db: AtomicAnalystDatabase
    private lateinit var transactionDao: TransactionDao
    private lateinit var tagDao: TagDao
    private lateinit var categoryDao: CategoryDao
    private lateinit var accountDao: AccountDao
    private lateinit var dao: TransactionTagDao

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AtomicAnalystDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        transactionDao = db.transactionDao()
        tagDao = db.tagDao()
        categoryDao = db.categoryDao()
        accountDao = db.accountDao()
        dao = db.transactionTagDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun getWithTags_ReturnsLinkedTags() = runBlocking {
        accountDao.upsert(
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
        categoryDao.upsert(
            CategoryEntity(
                id = "cat-1",
                name = "Food",
                type = TransactionCategory.FOOD,
                isSystem = true,
                createdAtEpochMs = 1_000L,
                updatedAtEpochMs = 2_000L
            )
        )
        val transaction = TransactionEntity(
            id = "txn-1",
            accountId = "acct-1",
            categoryId = "cat-1",
            amountCents = 5000L,
            currency = "USD",
            description = "Market",
            timestampEpochMs = 3_000L,
            source = TransactionSource.MANUAL,
            notes = null,
            isReconciled = false,
            relatedTransactionId = null
        )
        val tag = TagEntity(
            id = "tag-1",
            name = "Groceries",
            createdAtEpochMs = 1_000L
        )
        transactionDao.upsert(transaction)
        tagDao.upsert(tag)
        dao.upsertTagCrossRefs(
            listOf(TransactionTagCrossRef(transactionId = "txn-1", tagId = "tag-1"))
        )

        val result = dao.getWithTags("txn-1")

        assertNotNull(result)
        assertEquals(listOf("tag-1"), result?.tags?.map { it.id })
    }

    @Test
    fun observeAllWithTags_ReturnsTransactions() = runBlocking {
        accountDao.upsert(
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
        val transaction = TransactionEntity(
            id = "txn-1",
            accountId = "acct-1",
            categoryId = null,
            amountCents = 5000L,
            currency = "USD",
            description = "Market",
            timestampEpochMs = 3_000L,
            source = TransactionSource.MANUAL,
            notes = null,
            isReconciled = false,
            relatedTransactionId = null
        )
        transactionDao.upsert(transaction)

        val observed = dao.observeAllWithTags().first()

        assertEquals(1, observed.size)
        assertEquals("txn-1", observed.first().transaction.id)
    }
}
