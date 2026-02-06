package com.atomicanalyst.domain.model

import com.atomicanalyst.domain.error.AppException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ResultTest {
    @Test
    fun testSuccess_HoldsData() {
        val result: Result<Int> = Result.Success(42)

        assertTrue(result is Result.Success)
        assertEquals(42, (result as Result.Success).data)
    }

    @Test
    fun testError_HoldsException() {
        val exception = AppException.Validation("Invalid input")
        val result: Result<Int> = Result.Error(exception)

        assertTrue(result is Result.Error)
        assertEquals(exception, (result as Result.Error).exception)
    }

    @Test
    fun testLoading_IsSingleton() {
        val result: Result<Int> = Result.Loading

        assertTrue(result is Result.Loading)
    }
}
