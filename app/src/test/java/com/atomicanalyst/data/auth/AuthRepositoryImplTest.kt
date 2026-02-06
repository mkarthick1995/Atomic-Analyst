package com.atomicanalyst.data.auth

import com.atomicanalyst.data.security.InMemorySecureStorage
import com.atomicanalyst.data.security.PasswordHasher
import com.atomicanalyst.data.security.SessionManager
import com.atomicanalyst.domain.model.Result
import com.atomicanalyst.utils.FixedClock
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthRepositoryImplTest {
    @Test
    fun registerAndLogin_Succeeds() = runBlocking {
        val storage = InMemorySecureStorage()
        val repository = AuthRepositoryImpl(
            store = AuthLocalStore(storage),
            passwordHasher = PasswordHasher(),
            sessionManager = SessionManager(storage, FixedClock(1_000L)),
            biometricCapability = FakeBiometricCapability(true)
        )

        val register = repository.register("user1", "Password1".toCharArray())
        assertTrue(register is Result.Success)

        val login = repository.login("user1", "Password1".toCharArray())
        assertTrue(login is Result.Success)
    }

    @Test
    fun login_FailsForWrongPassword() = runBlocking {
        val storage = InMemorySecureStorage()
        val repository = AuthRepositoryImpl(
            store = AuthLocalStore(storage),
            passwordHasher = PasswordHasher(),
            sessionManager = SessionManager(storage, FixedClock(1_000L)),
            biometricCapability = FakeBiometricCapability(true)
        )

        repository.register("user1", "Password1".toCharArray())
        val login = repository.login("user1", "WrongPass1".toCharArray())

        assertTrue(login is Result.Error)
    }

    @Test
    fun biometricEnablement_RequiresCapability() = runBlocking {
        val storage = InMemorySecureStorage()
        val repository = AuthRepositoryImpl(
            store = AuthLocalStore(storage),
            passwordHasher = PasswordHasher(),
            sessionManager = SessionManager(storage, FixedClock(1_000L)),
            biometricCapability = FakeBiometricCapability(false)
        )

        repository.register("user1", "Password1".toCharArray())
        val result = repository.setBiometricEnabled(true)

        assertTrue(result is Result.Error)
    }
}

private class FakeBiometricCapability(
    private val available: Boolean
) : BiometricCapability {
    override fun isAvailable(): Boolean = available
}
