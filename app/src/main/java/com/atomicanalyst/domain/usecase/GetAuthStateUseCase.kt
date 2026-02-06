package com.atomicanalyst.domain.usecase

import com.atomicanalyst.domain.model.AuthState
import com.atomicanalyst.domain.model.Result
import com.atomicanalyst.domain.repository.AuthRepository
import javax.inject.Inject

class GetAuthStateUseCase @Inject constructor(
    private val repository: AuthRepository
) : BaseUseCase<Unit, AuthState>() {
    override suspend fun execute(params: Unit): Result<AuthState> {
        return repository.authState()
    }
}
