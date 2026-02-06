package com.atomicanalyst.domain.usecase

import com.atomicanalyst.domain.model.AuthSession
import com.atomicanalyst.domain.model.Result
import com.atomicanalyst.domain.repository.AuthRepository
import javax.inject.Inject

data class LoginParams(
    val userId: String,
    val password: CharArray
)

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) : BaseUseCase<LoginParams, AuthSession>() {
    override suspend fun execute(params: LoginParams): Result<AuthSession> {
        return repository.login(params.userId, params.password)
    }
}
