package com.atomicanalyst.domain.usecase

import com.atomicanalyst.domain.model.Frequency
import com.atomicanalyst.domain.model.Result
import com.atomicanalyst.domain.model.StandingInstruction
import com.atomicanalyst.domain.repository.StandingInstructionRepository
import com.atomicanalyst.domain.validation.StandingInstructionValidator
import com.atomicanalyst.utils.FixedClock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StandingInstructionUseCasesTest {
    @Test
    fun createAndObserveInstruction_WorksAsExpected() = runBlocking {
        val repository = FakeStandingInstructionRepository()
        val clock = FixedClock(1_000L)
        val validator = StandingInstructionValidator(clock)
        val create = CreateStandingInstructionUseCase(repository, validator, clock)
        val observe = ObserveStandingInstructionsUseCase(repository)

        val result = create(
            CreateStandingInstructionParams(
                accountId = "acct-1",
                description = "Rent",
                amountCents = 20_00L,
                frequency = Frequency.MONTHLY,
                nextExecutionEpochMs = 9_000L
            )
        )
        assertTrue(result is Result.Success)

        val flowResult = observe("acct-1") as Result.Success
        val items = flowResult.data.first()
        assertEquals(1, items.size)
        assertEquals("acct-1", items.first().accountId)
    }
}

private class FakeStandingInstructionRepository : StandingInstructionRepository {
    private val items = LinkedHashMap<String, StandingInstruction>()
    private val flows = mutableMapOf<String, MutableStateFlow<List<StandingInstruction>>>()

    override suspend fun upsert(instruction: StandingInstruction): Result<Unit> {
        items[instruction.id] = instruction
        emit(instruction.accountId)
        return Result.Success(Unit)
    }

    override suspend fun update(instruction: StandingInstruction): Result<Unit> {
        items[instruction.id] = instruction
        emit(instruction.accountId)
        return Result.Success(Unit)
    }

    override suspend fun delete(instruction: StandingInstruction): Result<Unit> {
        items.remove(instruction.id)
        emit(instruction.accountId)
        return Result.Success(Unit)
    }

    override suspend fun getById(id: String): Result<StandingInstruction?> =
        Result.Success(items[id])

    override fun observeByAccount(accountId: String): Flow<List<StandingInstruction>> =
        flows.getOrPut(accountId) { MutableStateFlow(emptyList()) }

    private fun emit(accountId: String) {
        val list = items.values.filter { it.accountId == accountId }
        flows.getOrPut(accountId) { MutableStateFlow(emptyList()) }.value = list
    }
}
