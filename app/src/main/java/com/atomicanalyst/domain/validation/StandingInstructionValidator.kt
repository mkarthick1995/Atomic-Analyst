package com.atomicanalyst.domain.validation

import com.atomicanalyst.domain.error.AppException
import com.atomicanalyst.domain.model.StandingInstruction
import com.atomicanalyst.utils.Clock

class StandingInstructionValidator @javax.inject.Inject constructor(
    private val clock: Clock
) {
    fun validate(instruction: StandingInstruction) {
        if (instruction.description.isBlank()) {
            throw AppException.Validation("Instruction description is required")
        }
        if (instruction.amountCents <= 0L) {
            throw AppException.Validation("Instruction amount must be positive")
        }
        if (instruction.nextExecutionEpochMs <= clock.now()) {
            throw AppException.Validation("Instruction execution time must be in the future")
        }
    }
}
