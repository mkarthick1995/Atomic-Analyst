package com.atomicanalyst.domain.usecase

import com.atomicanalyst.domain.model.Account
import com.atomicanalyst.domain.model.AccountType
import com.atomicanalyst.domain.model.Result
import com.atomicanalyst.domain.repository.AccountRepository
import com.atomicanalyst.utils.FixedClock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AccountUseCasesTest {
    @Test
    fun createAccount_PersistsAndReturnsAccount() = runBlocking {
        val repository = FakeAccountRepository()
        val clock = FixedClock(1_000L)
        val useCase = CreateAccountUseCase(repository, clock)

        val result = useCase(
            CreateAccountParams(
                id = "acct-1",
                name = "Primary",
                accountNumber = "1234",
                type = AccountType.SAVINGS_ACCOUNT,
                institution = "Bank",
                openingBalanceCents = 5000L,
                currency = "USD"
            )
        )

        assertTrue(result is Result.Success)
        val account = (result as Result.Success).data
        assertEquals("acct-1", account.id)
        assertEquals(1_000L, account.createdAtEpochMs)
        assertEquals(1_000L, account.updatedAtEpochMs)
    }

    @Test
    fun setAccountActive_UpdatesStatus() = runBlocking {
        val repository = FakeAccountRepository()
        val clock = FixedClock(1_000L)
        val create = CreateAccountUseCase(repository, clock)
        val setActive = SetAccountActiveUseCase(repository, clock)
        create(
            CreateAccountParams(
                id = "acct-1",
                name = "Primary",
                accountNumber = "1234",
                type = AccountType.SAVINGS_ACCOUNT,
                institution = "Bank",
                openingBalanceCents = 5000L,
                currency = "USD"
            )
        )

        val result = setActive(SetAccountActiveParams("acct-1", false))

        assertTrue(result is Result.Success)
        val account = (result as Result.Success).data
        assertEquals(false, account.isActive)
    }

    @Test
    fun observeAccounts_ReturnsFlow() = runBlocking {
        val repository = FakeAccountRepository()
        val clock = FixedClock(1_000L)
        val create = CreateAccountUseCase(repository, clock)
        val observe = ObserveAccountsUseCase(repository)
        create(
            CreateAccountParams(
                id = "acct-1",
                name = "Primary",
                accountNumber = "1234",
                type = AccountType.SAVINGS_ACCOUNT,
                institution = "Bank",
                openingBalanceCents = 5000L,
                currency = "USD"
            )
        )

        val flowResult = observe(Unit) as Result.Success
        val accounts = flowResult.data.first()

        assertEquals(1, accounts.size)
        assertEquals("acct-1", accounts.first().id)
    }
}

private class FakeAccountRepository : AccountRepository {
    private val items = LinkedHashMap<String, Account>()
    private val flow = MutableStateFlow<List<Account>>(emptyList())

    override suspend fun upsert(account: Account): Result<Unit> {
        items[account.id] = account
        emit()
        return Result.Success(Unit)
    }

    override suspend fun update(account: Account): Result<Unit> {
        items[account.id] = account
        emit()
        return Result.Success(Unit)
    }

    override suspend fun delete(account: Account): Result<Unit> {
        items.remove(account.id)
        emit()
        return Result.Success(Unit)
    }

    override suspend fun getById(id: String): Result<Account?> =
        Result.Success(items[id])

    override fun observeAll(): Flow<List<Account>> = flow

    private fun emit() {
        flow.value = items.values.toList()
    }
}
