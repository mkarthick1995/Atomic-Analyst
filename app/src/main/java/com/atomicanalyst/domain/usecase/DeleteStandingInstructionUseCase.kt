package com.atomicanalyst.domain.usecase

import com.atomicanalyst.domain.model.Result
import com.atomicanalyst.domain.model.StandingInstruction
import com.atomicanalyst.domain.repository.StandingInstructionRepository
import javax.inject.Inject

class DeleteStandingInstructionUseCase @Inject constructor(
    private val repository: StandingInstructionRepository
) : BaseUseCase<StandingInstruction, Unit>() {
    override suspend fun execute(params: StandingInstruction): Result<Unit> {
        return repository.delete(params)
    }
}
