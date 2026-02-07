package com.atomicanalyst.domain.validation

import com.atomicanalyst.domain.error.AppException
import org.junit.Assert.assertThrows
import org.junit.Test

class AccountValidatorTest {
    @Test
    fun validateCurrency_RejectsInvalidCodes() {
        assertThrows(AppException.Validation::class.java) {
            AccountValidator.validateCurrency("US")
        }
    }

    @Test
    fun validateName_RejectsBlank() {
        assertThrows(AppException.Validation::class.java) {
            AccountValidator.validateName("")
        }
    }
}
