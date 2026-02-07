package com.atomicanalyst.data.db.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.atomicanalyst.data.db.AtomicAnalystDatabase
import com.atomicanalyst.data.db.entity.AccountEntity
import com.atomicanalyst.data.db.entity.StandingInstructionEntity
import com.atomicanalyst.domain.model.AccountType
import com.atomicanalyst.domain.model.Frequency
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StandingInstructionDaoTest {
    private lateinit var db: AtomicAnalystDatabase
    private lateinit var dao: StandingInstructionDao
    private lateinit var accountDao: AccountDao

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AtomicAnalystDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        dao = db.standingInstructionDao()
        accountDao = db.accountDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun upsertAndQueryByAccount_WorksAsExpected() = runBlocking {
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
        dao.upsert(instruction)

        val items = dao.getByAccountId("acct-1")
        assertEquals(1, items.size)
        assertEquals("si-1", items.first().id)
    }
}
