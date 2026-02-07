package com.atomicanalyst.domain.usecase

import com.atomicanalyst.domain.error.AppException
import com.atomicanalyst.domain.model.Result
import com.atomicanalyst.domain.model.StandingInstruction
import com.atomicanalyst.domain.repository.StandingInstructionRepository
import com.atomicanalyst.domain.validation.StandingInstructionValidator
import com.atomicanalyst.utils.Clock
import javax.inject.Inject

class UpdateStandingInstructionUseCase @Inject constructor(
    private val repository: StandingInstructionRepository,
    private val validator: StandingInstructionValidator,
    private val clock: Clock
) : BaseUseCase<StandingInstruction, Unit>() {
    override suspend fun execute(params: StandingInstruction): Result<Unit> {
        return try {
            val updated = params.copy(updatedAtEpochMs = clock.now())
            validator.validate(updated)
            repository.update(updated)
        } catch (exception: AppException) {
            Result.Error(exception)
        }
    }
}
