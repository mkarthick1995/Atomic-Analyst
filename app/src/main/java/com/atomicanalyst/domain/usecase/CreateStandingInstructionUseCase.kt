package com.atomicanalyst.domain.usecase

import com.atomicanalyst.domain.error.AppException
import com.atomicanalyst.domain.model.Frequency
import com.atomicanalyst.domain.model.Result
import com.atomicanalyst.domain.model.StandingInstruction
import com.atomicanalyst.domain.repository.StandingInstructionRepository
import com.atomicanalyst.domain.validation.StandingInstructionValidator
import com.atomicanalyst.utils.Clock
import java.util.UUID
import javax.inject.Inject

data class CreateStandingInstructionParams(
    val id: String? = null,
    val accountId: String,
    val description: String,
    val amountCents: Long,
    val frequency: Frequency,
    val nextExecutionEpochMs: Long,
    val isActive: Boolean = true
)

class CreateStandingInstructionUseCase @Inject constructor(
    private val repository: StandingInstructionRepository,
    private val validator: StandingInstructionValidator,
    private val clock: Clock
) : BaseUseCase<CreateStandingInstructionParams, StandingInstruction>() {
    override suspend fun execute(params: CreateStandingInstructionParams): Result<StandingInstruction> {
        return try {
            val now = clock.now()
            val instruction = StandingInstruction(
                id = params.id ?: UUID.randomUUID().toString(),
                accountId = params.accountId,
                description = params.description.trim(),
                amountCents = params.amountCents,
                frequency = params.frequency,
                nextExecutionEpochMs = params.nextExecutionEpochMs,
                createdAtEpochMs = now,
                updatedAtEpochMs = now,
                isActive = params.isActive
            )
            validator.validate(instruction)
            when (val result = repository.upsert(instruction)) {
                is Result.Success -> Result.Success(instruction)
                is Result.Error -> Result.Error(result.exception)
                Result.Loading -> Result.Loading
            }
        } catch (exception: AppException) {
            Result.Error(exception)
        }
    }
}
