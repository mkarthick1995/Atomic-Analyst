package com.atomicanalyst.domain.usecase

import com.atomicanalyst.domain.model.Result
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BaseUseCaseTest {
    private class IncrementUseCase : BaseUseCase<Int, Int>() {
        override suspend fun execute(params: Int): Result<Int> {
            return Result.Success(params + 1)
        }
    }

    @Test
    fun testInvoke_DelegatesToExecute() = runBlocking {
        val useCase = IncrementUseCase()
        val result = useCase(5)

        assertTrue(result is Result.Success)
        assertEquals(6, (result as Result.Success).data)
    }
}
