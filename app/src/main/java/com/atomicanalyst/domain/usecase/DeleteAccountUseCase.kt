package com.atomicanalyst.domain.usecase

import com.atomicanalyst.domain.model.Account
import com.atomicanalyst.domain.model.Result
import com.atomicanalyst.domain.repository.AccountRepository
import javax.inject.Inject

class DeleteAccountUseCase @Inject constructor(
    private val repository: AccountRepository
) : BaseUseCase<Account, Unit>() {
    override suspend fun execute(params: Account): Result<Unit> {
        return repository.delete(params)
    }
}
