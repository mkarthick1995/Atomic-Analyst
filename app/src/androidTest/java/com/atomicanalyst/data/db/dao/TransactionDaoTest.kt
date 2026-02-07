package com.atomicanalyst.data.db.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.atomicanalyst.data.db.AtomicAnalystDatabase
import com.atomicanalyst.data.db.entity.AccountEntity
import com.atomicanalyst.data.db.entity.CategoryEntity
import com.atomicanalyst.data.db.entity.TransactionEntity
import com.atomicanalyst.domain.model.AccountType
import com.atomicanalyst.domain.model.TransactionCategory
import com.atomicanalyst.domain.model.TransactionSource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TransactionDaoTest {
    private lateinit var db: AtomicAnalystDatabase
    private lateinit var dao: TransactionDao
    private lateinit var accountDao: AccountDao
    private lateinit var categoryDao: CategoryDao

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AtomicAnalystDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        dao = db.transactionDao()
        accountDao = db.accountDao()
        categoryDao = db.categoryDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun observeAll_ReturnsTransactionsSortedByTimestamp() = runBlocking {
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
        val older = TransactionEntity(
            id = "txn-1",
            accountId = "acct-1",
            categoryId = "cat-1",
            amountCents = 1000L,
            currency = "USD",
            description = "Old",
            timestampEpochMs = 1_000L,
            source = TransactionSource.MANUAL,
            notes = null,
            isReconciled = false,
            relatedTransactionId = null
        )
        val newer = older.copy(id = "txn-2", description = "New", timestampEpochMs = 2_000L)

        dao.upsert(older)
        dao.upsert(newer)

        val observed = dao.observeAll().first()
        assertEquals(listOf("txn-2", "txn-1"), observed.map { it.id })
    }
}
