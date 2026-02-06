package com.atomicanalyst.domain.usecase

import com.atomicanalyst.domain.model.Result
import com.atomicanalyst.domain.repository.AuthRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val repository: AuthRepository
) : BaseUseCase<Unit, Unit>() {
    override suspend fun execute(params: Unit): Result<Unit> {
        return repository.logout()
    }
}
