package com.atomicanalyst.presentation

import com.atomicanalyst.domain.error.AppException
import com.atomicanalyst.domain.model.Resource
import com.atomicanalyst.domain.model.Result
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BaseViewModelTest {
    private class TestViewModel : BaseViewModel() {
        fun map(result: Result<Int>): Resource<Int> = resultToResource(result)
        fun mapException(throwable: Throwable) = toAppException(throwable)
    }

    @Test
    fun testResultToResource_Success() {
        val viewModel = TestViewModel()
        val resource = viewModel.map(Result.Success(3))

        assertTrue(resource is Resource.Success)
        assertEquals(3, (resource as Resource.Success).data)
    }

    @Test
    fun testResultToResource_Error() {
        val viewModel = TestViewModel()
        val resource = viewModel.map(Result.Error(AppException.Validation("bad")))

        assertTrue(resource is Resource.Error)
        assertEquals("bad", (resource as Resource.Error).message)
    }

    @Test
    fun testToAppException_WrapsUnknown() {
        val viewModel = TestViewModel()
        val exception = viewModel.mapException(IllegalStateException("boom"))

        assertTrue(exception is AppException.Unknown)
    }
}
