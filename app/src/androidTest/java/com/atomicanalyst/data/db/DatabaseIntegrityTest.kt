package com.atomicanalyst.data.db

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.atomicanalyst.data.db.entity.AccountEntity
import com.atomicanalyst.data.db.entity.AccountLiabilityCrossRef
import com.atomicanalyst.data.db.entity.CategoryEntity
import com.atomicanalyst.data.db.entity.StandingInstructionEntity
import com.atomicanalyst.data.db.entity.TagEntity
import com.atomicanalyst.data.db.entity.TransactionEntity
import com.atomicanalyst.data.db.entity.TransactionTagCrossRef
import com.atomicanalyst.domain.model.AccountType
import com.atomicanalyst.domain.model.Frequency
import com.atomicanalyst.domain.model.TransactionCategory
import com.atomicanalyst.domain.model.TransactionSource
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatabaseIntegrityTest {
    private lateinit var db: AtomicAnalystDatabase

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AtomicAnalystDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun deletingAccount_CascadesToTransactionsAndTags() = runBlocking {
        val accountDao = db.accountDao()
        val accountLiabilityDao = db.accountLiabilityDao()
        val standingInstructionDao = db.standingInstructionDao()
        val transactionDao = db.transactionDao()
        val tagDao = db.tagDao()
        val transactionTagDao = db.transactionTagDao()

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
        accountDao.upsert(
            AccountEntity(
                id = "acct-2",
                name = "Loan",
                accountNumber = "5678",
                type = AccountType.LOAN,
                institution = "Bank",
                balanceCents = -100_00L,
                currency = "USD",
                isActive = true,
                createdAtEpochMs = 1_500L,
                updatedAtEpochMs = 2_500L
            )
        )
        val transaction = TransactionEntity(
            id = "txn-1",
            accountId = "acct-1",
            categoryId = null,
            amountCents = 1000L,
            currency = "USD",
            description = "Test",
            timestampEpochMs = 1_000L,
            source = TransactionSource.MANUAL,
            notes = null,
            isReconciled = false,
            relatedTransactionId = null
        )
        val instruction = StandingInstructionEntity(
            id = "si-1",
            accountId = "acct-1",
            description = "Rent",
            amountCents = 20_00L,
            frequency = Frequency.MONTHLY,
            nextExecutionEpochMs = 9_000L,
            createdAtEpochMs = 1_000L,
            updatedAtEpochMs = 1_500L,
            isActive = true
        )
        val tag = TagEntity(
            id = "tag-1",
            name = "Bills",
            createdAtEpochMs = 1_000L
        )
        accountLiabilityDao.upsertAll(
            listOf(
                AccountLiabilityCrossRef(
                    accountId = "acct-1",
                    liabilityAccountId = "acct-2"
                )
            )
        )
        standingInstructionDao.upsert(instruction)
        transactionDao.upsert(transaction)
        tagDao.upsert(tag)
        transactionTagDao.upsertTagCrossRefs(
            listOf(TransactionTagCrossRef(transactionId = "txn-1", tagId = "tag-1"))
        )

        accountDao.delete(accountDao.getById("acct-1")!!)

        assertNull(transactionDao.getById("txn-1"))
        assertEquals(0, transactionTagDao.getAllTagCrossRefs().size)
        assertEquals(0, accountLiabilityDao.getAll().size)
        assertEquals(0, standingInstructionDao.getAll().size)
    }

    @Test
    fun deletingCategory_SetsTransactionCategoryToNull() = runBlocking {
        val accountDao = db.accountDao()
        val categoryDao = db.categoryDao()
        val transactionDao = db.transactionDao()

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
            amountCents = 1000L,
            currency = "USD",
            description = "Test",
            timestampEpochMs = 1_000L,
            source = TransactionSource.MANUAL,
            notes = null,
            isReconciled = false,
            relatedTransactionId = null
        )
        transactionDao.upsert(transaction)

        categoryDao.delete(categoryDao.getById("cat-1")!!)

        assertNull(transactionDao.getById("txn-1")?.categoryId)
    }

    @Test
    fun deletingTag_RemovesCrossRefs() = runBlocking {
        val accountDao = db.accountDao()
        val transactionDao = db.transactionDao()
        val tagDao = db.tagDao()
        val transactionTagDao = db.transactionTagDao()

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
            amountCents = 1000L,
            currency = "USD",
            description = "Test",
            timestampEpochMs = 1_000L,
            source = TransactionSource.MANUAL,
            notes = null,
            isReconciled = false,
            relatedTransactionId = null
        )
        val tag = TagEntity(
            id = "tag-1",
            name = "Bills",
            createdAtEpochMs = 1_000L
        )
        transactionDao.upsert(transaction)
        tagDao.upsert(tag)
        transactionTagDao.upsertTagCrossRefs(
            listOf(TransactionTagCrossRef(transactionId = "txn-1", tagId = "tag-1"))
        )

        tagDao.delete(tagDao.getById("tag-1")!!)

        assertEquals(0, transactionTagDao.getAllTagCrossRefs().size)
    }
}
