package com.atomicanalyst.domain.usecase

import com.atomicanalyst.domain.model.Account
import com.atomicanalyst.domain.model.Result
import com.atomicanalyst.domain.repository.AccountRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveAccountsUseCase @Inject constructor(
    private val repository: AccountRepository
) : BaseUseCase<Unit, Flow<List<Account>>>() {
    override suspend fun execute(params: Unit): Result<Flow<List<Account>>> {
        return Result.Success(repository.observeAll())
    }
}
