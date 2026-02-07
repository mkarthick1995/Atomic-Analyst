package com.atomicanalyst.data.repository

import com.atomicanalyst.data.BaseRepository
import com.atomicanalyst.data.db.dao.ReconciliationLogDao
import com.atomicanalyst.data.db.mapper.toDomain
import com.atomicanalyst.data.db.mapper.toEntity
import com.atomicanalyst.domain.model.ReconciliationLog
import com.atomicanalyst.domain.model.Result
import com.atomicanalyst.domain.repository.ReconciliationLogRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ReconciliationLogRepositoryImpl @Inject constructor(
    private val logDao: ReconciliationLogDao
) : BaseRepository(), ReconciliationLogRepository {
    override suspend fun upsert(log: ReconciliationLog): Result<Unit> = safeCall {
        logDao.upsert(log.toEntity())
    }

    override fun observeLogs(transactionId: String): Flow<List<ReconciliationLog>> =
        logDao.observeLogs(transactionId).map { logs -> logs.map { it.toDomain() } }
}
