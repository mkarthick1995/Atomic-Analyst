package com.atomicanalyst.domain.validation

import com.atomicanalyst.domain.error.AppException
import com.atomicanalyst.domain.model.Frequency
import com.atomicanalyst.domain.model.StandingInstruction
import com.atomicanalyst.utils.FixedClock
import org.junit.Assert.assertThrows
import org.junit.Test

class StandingInstructionValidatorTest {
    @Test
    fun validate_RejectsPastExecution() {
        val validator = StandingInstructionValidator(FixedClock(1_000L))
        val instruction = StandingInstruction(
            id = "si-1",
            description = "Rent",
            amountCents = 10_00L,
            frequency = Frequency.MONTHLY,
            nextExecutionEpochMs = 500L
        )

        assertThrows(AppException.Validation::class.java) {
            validator.validate(instruction)
        }
    }
}
