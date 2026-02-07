package com.atomicanalyst.domain.repository

import com.atomicanalyst.domain.model.Result
import com.atomicanalyst.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    suspend fun upsert(transaction: Transaction): Result<Unit>
    suspend fun update(transaction: Transaction): Result<Unit>
    suspend fun delete(transaction: Transaction): Result<Unit>
    suspend fun getById(id: String): Result<Transaction?>
    fun observeAll(): Flow<List<Transaction>>
}
