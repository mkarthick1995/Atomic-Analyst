package com.atomicanalyst.data.db.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.atomicanalyst.data.db.AtomicAnalystDatabase
import com.atomicanalyst.data.db.entity.AccountEntity
import com.atomicanalyst.data.db.entity.AccountLiabilityCrossRef
import com.atomicanalyst.domain.model.AccountType
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AccountLiabilityDaoTest {
    private lateinit var db: AtomicAnalystDatabase
    private lateinit var dao: AccountLiabilityDao
    private lateinit var accountDao: AccountDao

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AtomicAnalystDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        dao = db.accountLiabilityDao()
        accountDao = db.accountDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun upsertAndQuery_WorksAsExpected() = runBlocking {
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
                balanceCents = -50_00L,
                currency = "USD",
                isActive = true,
                createdAtEpochMs = 1_500L,
                updatedAtEpochMs = 2_500L
            )
        )
        dao.upsertAll(
            listOf(AccountLiabilityCrossRef("acct-1", "acct-2"))
        )

        assertEquals(listOf("acct-2"), dao.getLiabilityIds("acct-1"))
        assertEquals(listOf("acct-1"), dao.getAccountIds("acct-2"))
    }
}
