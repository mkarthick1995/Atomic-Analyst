package com.atomicanalyst.domain.repository

import com.atomicanalyst.domain.model.Account
import com.atomicanalyst.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    suspend fun upsert(account: Account): Result<Unit>
    suspend fun update(account: Account): Result<Unit>
    suspend fun delete(account: Account): Result<Unit>
    suspend fun getById(id: String): Result<Account?>
    fun observeAll(): Flow<List<Account>>
}
