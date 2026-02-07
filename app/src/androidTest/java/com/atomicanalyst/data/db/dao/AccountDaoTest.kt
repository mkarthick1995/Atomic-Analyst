package com.atomicanalyst.data.db.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.atomicanalyst.data.db.AtomicAnalystDatabase
import com.atomicanalyst.data.db.entity.AccountEntity
import com.atomicanalyst.domain.model.AccountType
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AccountDaoTest {
    private lateinit var db: AtomicAnalystDatabase
    private lateinit var dao: AccountDao

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AtomicAnalystDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        dao = db.accountDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun upsertUpdateDelete_WorksAsExpected() = runBlocking {
        val account = AccountEntity(
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
        dao.upsert(account)
        assertEquals(account, dao.getById("acct-1"))

        val updated = account.copy(name = "Updated")
        dao.update(updated)
        assertEquals("Updated", dao.getById("acct-1")?.name)

        dao.delete(updated)
        assertNull(dao.getById("acct-1"))
    }
}
