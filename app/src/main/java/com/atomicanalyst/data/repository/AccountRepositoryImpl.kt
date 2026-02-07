package com.atomicanalyst.data.repository

import com.atomicanalyst.data.BaseRepository
import com.atomicanalyst.data.db.dao.AccountDao
import com.atomicanalyst.data.db.mapper.toDomain
import com.atomicanalyst.data.db.mapper.toEntity
import com.atomicanalyst.domain.model.Account
import com.atomicanalyst.domain.model.Result
import com.atomicanalyst.domain.repository.AccountRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AccountRepositoryImpl @Inject constructor(
    private val accountDao: AccountDao
) : BaseRepository(), AccountRepository {
    override suspend fun upsert(account: Account): Result<Unit> = safeCall {
        accountDao.upsert(account.toEntity())
    }

    override suspend fun update(account: Account): Result<Unit> = safeCall {
        accountDao.update(account.toEntity())
    }

    override suspend fun delete(account: Account): Result<Unit> = safeCall {
        accountDao.delete(account.toEntity())
    }

    override suspend fun getById(id: String): Result<Account?> = safeCall {
        accountDao.getById(id)?.toDomain()
    }

    override fun observeAll(): Flow<List<Account>> = accountDao.observeAll()
        .map { accounts -> accounts.map { it.toDomain() } }
}
