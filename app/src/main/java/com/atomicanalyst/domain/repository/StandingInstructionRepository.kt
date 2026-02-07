package com.atomicanalyst.domain.repository

import com.atomicanalyst.domain.model.Result
import com.atomicanalyst.domain.model.StandingInstruction
import kotlinx.coroutines.flow.Flow

interface StandingInstructionRepository {
    suspend fun upsert(instruction: StandingInstruction): Result<Unit>
    suspend fun update(instruction: StandingInstruction): Result<Unit>
    suspend fun delete(instruction: StandingInstruction): Result<Unit>
    suspend fun getById(id: String): Result<StandingInstruction?>
    fun observeByAccount(accountId: String): Flow<List<StandingInstruction>>
}
