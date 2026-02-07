package com.atomicanalyst.data.repository

import com.atomicanalyst.data.db.dao.ReconciliationLogDao
import com.atomicanalyst.data.db.entity.ReconciliationLogEntity
import com.atomicanalyst.domain.model.ReconciliationAction
import com.atomicanalyst.domain.model.ReconciliationLog
import com.atomicanalyst.domain.model.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReconciliationLogRepositoryImplTest {
    private class FakeReconciliationLogDao : ReconciliationLogDao {
        private val items = mutableListOf<ReconciliationLogEntity>()
        private val flow = MutableStateFlow<List<ReconciliationLogEntity>>(emptyList())

        override suspend fun upsert(log: ReconciliationLogEntity) {
            items.removeAll { it.id == log.id }
            items.add(log)
            emit()
        }

        override fun observeLogs(transactionId: String): Flow<List<ReconciliationLogEntity>> =
            flow.map { logs -> logs.filter { it.primaryTransactionId == transactionId } }

        private fun emit() {
            flow.value = items.toList()
        }
    }

    @Test
    fun testUpsertAndObserveLogs_ReturnsDomainLogs() = runBlocking {
        val dao = FakeReconciliationLogDao()
        val repository = ReconciliationLogRepositoryImpl(dao)
        val log = ReconciliationLog(
            id = "log-1",
            primaryTransactionId = "txn-1",
            relatedTransactionIds = listOf("txn-2"),
            action = ReconciliationAction.MERGED,
            timestampEpochMs = 1700000000000L,
            userId = "user-1",
            reason = "manual"
        )

        val result = repository.upsert(log)

        assertTrue(result is Result.Success)
        assertEquals(listOf(log), repository.observeLogs("txn-1").first())
    }
}
