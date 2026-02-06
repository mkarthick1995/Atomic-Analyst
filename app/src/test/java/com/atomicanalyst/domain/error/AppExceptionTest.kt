package com.atomicanalyst.domain.error

import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class AppExceptionTest {
    @Test
    fun testValidation_UsesMessage() {
        val exception = AppException.Validation("Invalid value")

        assertEquals("Invalid value", exception.message)
    }

    @Test
    fun testUnknown_PreservesCause() {
        val cause = IllegalStateException("boom")
        val exception = AppException.Unknown(cause)

        assertSame(cause, exception.cause)
    }
}
