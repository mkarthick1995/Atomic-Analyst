package com.atomicanalyst.data.repository

import com.atomicanalyst.data.BaseRepository
import com.atomicanalyst.data.db.dao.AccountLiabilityDao
import com.atomicanalyst.data.db.entity.AccountLiabilityCrossRef
import com.atomicanalyst.domain.model.Result
import com.atomicanalyst.domain.repository.AccountLiabilityRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AccountLiabilityRepositoryImpl @Inject constructor(
    private val dao: AccountLiabilityDao
) : BaseRepository(), AccountLiabilityRepository {
    override suspend fun setLiabilities(
        accountId: String,
        liabilityIds: List<String>
    ): Result<Unit> = safeCall {
        dao.clearForAccount(accountId)
        val filtered = liabilityIds.distinct().filter { it.isNotBlank() && it != accountId }
        if (filtered.isNotEmpty()) {
            val refs = filtered.map { liabilityId ->
                AccountLiabilityCrossRef(
                    accountId = accountId,
                    liabilityAccountId = liabilityId
                )
            }
            dao.upsertAll(refs)
        }
    }

    override suspend fun getLiabilities(accountId: String): Result<List<String>> = safeCall {
        dao.getLiabilityIds(accountId)
    }

    override suspend fun getLinkedAccounts(liabilityAccountId: String): Result<List<String>> = safeCall {
        dao.getAccountIds(liabilityAccountId)
    }

    override fun observeLiabilities(accountId: String): Flow<List<String>> =
        dao.observeLiabilityRefs(accountId).map { refs -> refs.map { it.liabilityAccountId } }
}
