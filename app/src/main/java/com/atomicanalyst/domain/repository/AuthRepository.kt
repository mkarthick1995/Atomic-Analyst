package com.atomicanalyst.domain.repository

import com.atomicanalyst.domain.model.AuthSession
import com.atomicanalyst.domain.model.AuthState
import com.atomicanalyst.domain.model.Result

interface AuthRepository {
    suspend fun register(userId: String, password: CharArray): Result<AuthSession>
    suspend fun login(userId: String, password: CharArray): Result<AuthSession>
    suspend fun logout(): Result<Unit>
    suspend fun authState(): Result<AuthState>
    suspend fun setBiometricEnabled(enabled: Boolean): Result<Unit>
}
