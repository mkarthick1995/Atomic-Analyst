package com.atomicanalyst.domain.usecase

import com.atomicanalyst.data.auth.AuthLocalStore
import com.atomicanalyst.data.auth.AuthRepositoryImpl
import com.atomicanalyst.data.auth.BiometricCapability
import com.atomicanalyst.data.security.InMemorySecureStorage
import com.atomicanalyst.data.security.PasswordHasher
import com.atomicanalyst.data.security.SessionManager
import com.atomicanalyst.domain.model.Result
import com.atomicanalyst.utils.FixedClock
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test

class SetBiometricEnabledUseCaseTest {
    @Test
    fun enableBiometric_SucceedsWhenCapabilityAvailable() = runBlocking {
        val storage = InMemorySecureStorage()
        val repository = AuthRepositoryImpl(
            store = AuthLocalStore(storage),
            passwordHasher = PasswordHasher(),
            sessionManager = SessionManager(storage, FixedClock(0L)),
            biometricCapability = FakeBiometricCapability(true)
        )
        repository.register("user1", "Password1".toCharArray())
        val useCase = SetBiometricEnabledUseCase(repository)

        val result = useCase(SetBiometricEnabledParams(true))

        assertTrue(result is Result.Success)
    }
}

private class FakeBiometricCapability(
    private val available: Boolean
) : BiometricCapability {
    override fun isAvailable(): Boolean = available
}
