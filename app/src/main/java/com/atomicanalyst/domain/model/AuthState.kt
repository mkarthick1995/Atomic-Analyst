package com.atomicanalyst.domain.model

data class AuthState(
    val isRegistered: Boolean,
    val isLoggedIn: Boolean,
    val biometricEnabled: Boolean,
    val userId: String?
)
