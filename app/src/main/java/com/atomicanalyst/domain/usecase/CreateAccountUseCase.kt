package com.atomicanalyst.domain.usecase

import com.atomicanalyst.domain.error.AppException
import com.atomicanalyst.domain.model.Account
import com.atomicanalyst.domain.model.AccountType
import com.atomicanalyst.domain.model.Result
import com.atomicanalyst.domain.repository.AccountRepository
import com.atomicanalyst.domain.validation.AccountValidator
import com.atomicanalyst.utils.Clock
import java.util.UUID
import javax.inject.Inject

data class CreateAccountParams(
    val id: String? = null,
    val name: String,
    val accountNumber: String,
    val type: AccountType,
    val institution: String,
    val openingBalanceCents: Long,
    val currency: String,
    val isActive: Boolean = true
)

class CreateAccountUseCase @Inject constructor(
    private val repository: AccountRepository,
    private val clock: Clock
) : BaseUseCase<CreateAccountParams, Account>() {
    override suspend fun execute(params: CreateAccountParams): Result<Account> {
        return try {
            AccountValidator.validateName(params.name)
            AccountValidator.validateAccountNumber(params.accountNumber)
            AccountValidator.validateInstitution(params.institution)
            AccountValidator.validateCurrency(params.currency)
            val now = clock.now()
            val account = Account(
                id = params.id ?: UUID.randomUUID().toString(),
                name = params.name.trim(),
                accountNumber = params.accountNumber.trim(),
                type = params.type,
                institution = params.institution.trim(),
                balanceCents = params.openingBalanceCents,
                currency = params.currency.uppercase(),
                isActive = params.isActive,
                createdAtEpochMs = now,
                updatedAtEpochMs = now
            )
            when (val saved = repository.upsert(account)) {
                is Result.Success -> Result.Success(account)
                is Result.Error -> Result.Error(saved.exception)
                Result.Loading -> Result.Loading
            }
        } catch (exception: AppException) {
            Result.Error(exception)
        }
    }
}
