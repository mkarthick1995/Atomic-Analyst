package com.atomicanalyst.domain.usecase

import com.atomicanalyst.domain.error.AppException
import com.atomicanalyst.domain.model.Account
import com.atomicanalyst.domain.model.Result
import com.atomicanalyst.domain.repository.AccountRepository
import com.atomicanalyst.domain.validation.AccountValidator
import com.atomicanalyst.utils.Clock
import javax.inject.Inject

class UpdateAccountUseCase @Inject constructor(
    private val repository: AccountRepository,
    private val clock: Clock
) : BaseUseCase<Account, Unit>() {
    override suspend fun execute(params: Account): Result<Unit> {
        return try {
            AccountValidator.validateName(params.name)
            AccountValidator.validateAccountNumber(params.accountNumber)
            AccountValidator.validateInstitution(params.institution)
            AccountValidator.validateCurrency(params.currency)
            val updated = params.copy(updatedAtEpochMs = clock.now())
            repository.update(updated)
        } catch (exception: AppException) {
            Result.Error(exception)
        }
    }
}
