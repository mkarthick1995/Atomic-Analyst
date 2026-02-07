package com.atomicanalyst.domain.usecase

import com.atomicanalyst.domain.error.AppException
import com.atomicanalyst.domain.model.Account
import com.atomicanalyst.domain.model.Result
import com.atomicanalyst.domain.repository.AccountRepository
import com.atomicanalyst.utils.Clock
import javax.inject.Inject

data class SetAccountActiveParams(
    val accountId: String,
    val isActive: Boolean
)

class SetAccountActiveUseCase @Inject constructor(
    private val repository: AccountRepository,
    private val clock: Clock
) : BaseUseCase<SetAccountActiveParams, Account>() {
    override suspend fun execute(params: SetAccountActiveParams): Result<Account> {
        return when (val existing = repository.getById(params.accountId)) {
            is Result.Success -> {
                val account = existing.data
                    ?: return Result.Error(AppException.Validation("Account not found"))
                val updated = account.copy(
                    isActive = params.isActive,
                    updatedAtEpochMs = clock.now()
                )
                when (val result = repository.update(updated)) {
                    is Result.Success -> Result.Success(updated)
                    is Result.Error -> Result.Error(result.exception)
                    Result.Loading -> Result.Loading
                }
            }
            is Result.Error -> Result.Error(existing.exception)
            Result.Loading -> Result.Loading
        }
    }
}
