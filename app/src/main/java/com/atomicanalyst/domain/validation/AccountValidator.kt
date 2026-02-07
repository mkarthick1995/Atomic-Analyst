package com.atomicanalyst.domain.validation

import com.atomicanalyst.domain.error.AppException

object AccountValidator {
    fun validateName(name: String) {
        if (name.isBlank()) {
            throw AppException.Validation("Account name is required")
        }
    }

    fun validateAccountNumber(accountNumber: String) {
        if (accountNumber.isBlank()) {
            throw AppException.Validation("Account number is required")
        }
    }

    fun validateInstitution(institution: String) {
        if (institution.isBlank()) {
            throw AppException.Validation("Institution is required")
        }
    }

    fun validateCurrency(currency: String) {
        if (currency.length != 3) {
            throw AppException.Validation("Currency must be a 3-letter code")
        }
    }
}
