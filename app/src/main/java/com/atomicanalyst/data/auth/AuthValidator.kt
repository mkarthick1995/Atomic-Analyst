package com.atomicanalyst.data.auth

import com.atomicanalyst.domain.error.AppException

object AuthValidator {
    private const val MIN_USER_ID = 3
    private const val MIN_PASSWORD = 8

    fun validateUserId(userId: String) {
        if (userId.isBlank()) {
            throw AppException.Validation("User ID cannot be empty")
        }
        if (userId.length < MIN_USER_ID) {
            throw AppException.Validation("User ID must be at least $MIN_USER_ID characters")
        }
    }

    fun validatePassword(password: CharArray) {
        if (password.size < MIN_PASSWORD) {
            throw AppException.Validation("Password must be at least $MIN_PASSWORD characters")
        }
        val hasDigit = password.any { it.isDigit() }
        val hasLetter = password.any { it.isLetter() }
        if (!hasDigit || !hasLetter) {
            throw AppException.Validation("Password must include letters and digits")
        }
    }
}
