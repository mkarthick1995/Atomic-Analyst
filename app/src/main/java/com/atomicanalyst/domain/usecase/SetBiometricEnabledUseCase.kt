package com.atomicanalyst.domain.usecase

import com.atomicanalyst.domain.model.Result
import com.atomicanalyst.domain.repository.AuthRepository
import javax.inject.Inject

data class SetBiometricEnabledParams(
    val enabled: Boolean
)

class SetBiometricEnabledUseCase @Inject constructor(
    private val repository: AuthRepository
) : BaseUseCase<SetBiometricEnabledParams, Unit>() {
    override suspend fun execute(params: SetBiometricEnabledParams): Result<Unit> {
        return repository.setBiometricEnabled(params.enabled)
    }
}
