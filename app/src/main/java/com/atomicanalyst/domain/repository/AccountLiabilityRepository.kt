package com.atomicanalyst.domain.repository

import com.atomicanalyst.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface AccountLiabilityRepository {
    suspend fun setLiabilities(accountId: String, liabilityIds: List<String>): Result<Unit>
    suspend fun getLiabilities(accountId: String): Result<List<String>>
    suspend fun getLinkedAccounts(liabilityAccountId: String): Result<List<String>>
    fun observeLiabilities(accountId: String): Flow<List<String>>
}
