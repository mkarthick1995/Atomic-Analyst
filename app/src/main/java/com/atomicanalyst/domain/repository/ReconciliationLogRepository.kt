package com.atomicanalyst.domain.repository

import com.atomicanalyst.domain.model.ReconciliationLog
import com.atomicanalyst.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface ReconciliationLogRepository {
    suspend fun upsert(log: ReconciliationLog): Result<Unit>
    fun observeLogs(transactionId: String): Flow<List<ReconciliationLog>>
}
