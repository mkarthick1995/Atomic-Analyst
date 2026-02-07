package com.atomicanalyst.domain.usecase

import com.atomicanalyst.domain.model.AccountBalanceSummary
import com.atomicanalyst.domain.model.Result
import com.atomicanalyst.domain.model.Transaction
import com.atomicanalyst.domain.service.AccountBalanceCalculator
import javax.inject.Inject

data class CalculateAccountBalanceParams(
    val openingBalanceCents: Long,
    val transactions: List<Transaction>,
    val fromEpochMs: Long? = null,
    val toEpochMs: Long? = null
)

class CalculateAccountBalanceUseCase @Inject constructor(
    private val calculator: AccountBalanceCalculator
) : BaseUseCase<CalculateAccountBalanceParams, AccountBalanceSummary>() {
    override suspend fun execute(params: CalculateAccountBalanceParams): Result<AccountBalanceSummary> {
        val summary = calculator.calculate(
            openingBalanceCents = params.openingBalanceCents,
            transactions = params.transactions,
            fromEpochMs = params.fromEpochMs,
            toEpochMs = params.toEpochMs
        )
        return Result.Success(summary)
    }
}
