package com.atomicanalyst.domain.usecase

import com.atomicanalyst.domain.model.AuthSession
import com.atomicanalyst.domain.model.Result
import com.atomicanalyst.domain.repository.AuthRepository
import javax.inject.Inject

data class RegisterUserParams(
    val userId: String,
    val password: CharArray
)

class RegisterUserUseCase @Inject constructor(
    private val repository: AuthRepository
) : BaseUseCase<RegisterUserParams, AuthSession>() {
    override suspend fun execute(params: RegisterUserParams): Result<AuthSession> {
        return repository.register(params.userId, params.password)
    }
}
