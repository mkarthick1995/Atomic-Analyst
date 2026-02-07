package com.atomicanalyst.data.repository

import com.atomicanalyst.data.BaseRepository
import com.atomicanalyst.data.db.dao.StandingInstructionDao
import com.atomicanalyst.data.db.mapper.toDomain
import com.atomicanalyst.data.db.mapper.toEntity
import com.atomicanalyst.domain.model.Result
import com.atomicanalyst.domain.model.StandingInstruction
import com.atomicanalyst.domain.repository.StandingInstructionRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StandingInstructionRepositoryImpl @Inject constructor(
    private val dao: StandingInstructionDao
) : BaseRepository(), StandingInstructionRepository {
    override suspend fun upsert(instruction: StandingInstruction): Result<Unit> = safeCall {
        dao.upsert(instruction.toEntity())
    }

    override suspend fun update(instruction: StandingInstruction): Result<Unit> = safeCall {
        dao.update(instruction.toEntity())
    }

    override suspend fun delete(instruction: StandingInstruction): Result<Unit> = safeCall {
        dao.delete(instruction.toEntity())
    }

    override suspend fun getById(id: String): Result<StandingInstruction?> = safeCall {
        dao.getById(id)?.toDomain()
    }

    override fun observeByAccount(accountId: String): Flow<List<StandingInstruction>> =
        dao.observeByAccountId(accountId).map { list -> list.map { it.toDomain() } }
}
