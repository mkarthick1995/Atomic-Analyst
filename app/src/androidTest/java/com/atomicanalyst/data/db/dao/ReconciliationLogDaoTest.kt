package com.atomicanalyst.data.db.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.atomicanalyst.data.db.AtomicAnalystDatabase
import com.atomicanalyst.data.db.entity.ReconciliationLogEntity
import com.atomicanalyst.domain.model.ReconciliationAction
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReconciliationLogDaoTest {
    private lateinit var db: AtomicAnalystDatabase
    private lateinit var dao: ReconciliationLogDao

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AtomicAnalystDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        dao = db.reconciliationLogDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun observeLogs_FiltersByTransactionId() = runBlocking {
        val log1 = ReconciliationLogEntity(
            id = "log-1",
            primaryTransactionId = "txn-1",
            relatedTransactionIdsJson = "[\"txn-2\"]",
            action = ReconciliationAction.MERGED,
            timestampEpochMs = 1_000L,
            userId = "user1",
            reason = "merge"
        )
        val log2 = log1.copy(id = "log-2", primaryTransactionId = "txn-2")
        dao.upsertAll(listOf(log1, log2))

        val logs = dao.observeLogs("txn-1").first()

        assertEquals(listOf("log-1"), logs.map { it.id })
    }
}
