package com.atomicanalyst.data

import com.atomicanalyst.domain.error.AppException
import com.atomicanalyst.domain.model.Result
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BaseRepositoryTest {
    private class TestRepository : BaseRepository() {
        suspend fun successResult(): Result<Int> = safeCall { 10 }
        suspend fun errorResult(): Result<Int> = safeCall { throw IllegalStateException("boom") }
        suspend fun appErrorResult(): Result<Int> = safeCall { throw AppException.Validation("bad") }
    }

    @Test
    fun testSafeCall_ReturnsSuccess() = runBlocking {
        val repo = TestRepository()
        val result = repo.successResult()

        assertTrue(result is Result.Success)
        assertEquals(10, (result as Result.Success).data)
    }

    @Test
    fun testSafeCall_MapsUnknownError() = runBlocking {
        val repo = TestRepository()
        val result = repo.errorResult()

        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).exception is AppException.Unknown)
    }

    @Test
    fun testSafeCall_PreservesAppException() = runBlocking {
        val repo = TestRepository()
        val result = repo.appErrorResult()

        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).exception is AppException.Validation)
    }
}
