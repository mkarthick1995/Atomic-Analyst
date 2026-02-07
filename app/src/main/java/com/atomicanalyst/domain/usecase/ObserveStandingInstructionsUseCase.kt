package com.atomicanalyst.domain.usecase

import com.atomicanalyst.domain.model.Result
import com.atomicanalyst.domain.model.StandingInstruction
import com.atomicanalyst.domain.repository.StandingInstructionRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveStandingInstructionsUseCase @Inject constructor(
    private val repository: StandingInstructionRepository
) : BaseUseCase<String, Flow<List<StandingInstruction>>>() {
    override suspend fun execute(params: String): Result<Flow<List<StandingInstruction>>> {
        return Result.Success(repository.observeByAccount(params))
    }
}
