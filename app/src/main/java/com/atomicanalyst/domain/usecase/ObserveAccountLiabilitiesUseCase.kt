package com.atomicanalyst.domain.usecase

import com.atomicanalyst.domain.model.Result
import com.atomicanalyst.domain.repository.AccountLiabilityRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveAccountLiabilitiesUseCase @Inject constructor(
    private val repository: AccountLiabilityRepository
) : BaseUseCase<String, Flow<List<String>>>() {
    override suspend fun execute(params: String): Result<Flow<List<String>>> {
        return Result.Success(repository.observeLiabilities(params))
    }
}
