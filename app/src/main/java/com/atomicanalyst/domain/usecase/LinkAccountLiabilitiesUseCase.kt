package com.atomicanalyst.domain.usecase

import com.atomicanalyst.domain.model.Result
import com.atomicanalyst.domain.repository.AccountLiabilityRepository
import javax.inject.Inject

data class LinkAccountLiabilitiesParams(
    val accountId: String,
    val liabilityAccountIds: List<String>
)

class LinkAccountLiabilitiesUseCase @Inject constructor(
    private val repository: AccountLiabilityRepository
) : BaseUseCase<LinkAccountLiabilitiesParams, Unit>() {
    override suspend fun execute(params: LinkAccountLiabilitiesParams): Result<Unit> {
        return repository.setLiabilities(params.accountId, params.liabilityAccountIds)
    }
}
