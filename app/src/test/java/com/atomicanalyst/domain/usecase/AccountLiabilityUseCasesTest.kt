package com.atomicanalyst.domain.usecase

import com.atomicanalyst.domain.model.Result
import com.atomicanalyst.domain.repository.AccountLiabilityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AccountLiabilityUseCasesTest {
    @Test
    fun linkAndObserveLiabilities_WorksAsExpected() = runBlocking {
        val repository = FakeAccountLiabilityRepository()
        val linkUseCase = LinkAccountLiabilitiesUseCase(repository)
        val observeUseCase = ObserveAccountLiabilitiesUseCase(repository)

        val linkResult = linkUseCase(
            LinkAccountLiabilitiesParams(
                accountId = "acct-1",
                liabilityAccountIds = listOf("acct-2")
            )
        )
        assertTrue(linkResult is Result.Success)

        val flowResult = observeUseCase("acct-1") as Result.Success
        val liabilities = flowResult.data.first()
        assertEquals(listOf("acct-2"), liabilities)
    }
}

private class FakeAccountLiabilityRepository : AccountLiabilityRepository {
    private val data = mutableMapOf<String, List<String>>()
    private val flows = mutableMapOf<String, MutableStateFlow<List<String>>>()

    override suspend fun setLiabilities(
        accountId: String,
        liabilityIds: List<String>
    ): Result<Unit> {
        data[accountId] = liabilityIds
        flows.getOrPut(accountId) { MutableStateFlow(emptyList()) }.value = liabilityIds
        return Result.Success(Unit)
    }

    override suspend fun getLiabilities(accountId: String): Result<List<String>> =
        Result.Success(data[accountId] ?: emptyList())

    override suspend fun getLinkedAccounts(liabilityAccountId: String): Result<List<String>> {
        val accounts = data.filterValues { it.contains(liabilityAccountId) }.keys.toList()
        return Result.Success(accounts)
    }

    override fun observeLiabilities(accountId: String): Flow<List<String>> =
        flows.getOrPut(accountId) { MutableStateFlow(emptyList()) }
}
