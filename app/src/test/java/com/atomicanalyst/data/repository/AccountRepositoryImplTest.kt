package com.atomicanalyst.data.repository

import com.atomicanalyst.data.db.dao.AccountDao
import com.atomicanalyst.data.db.entity.AccountEntity
import com.atomicanalyst.domain.model.Account
import com.atomicanalyst.domain.model.AccountType
import com.atomicanalyst.domain.model.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AccountRepositoryImplTest {
    private class FakeAccountDao : AccountDao {
        private val items = mutableMapOf<String, AccountEntity>()
        private val flow = MutableStateFlow<List<AccountEntity>>(emptyList())

        override suspend fun upsert(account: AccountEntity) {
            items[account.id] = account
            emit()
        }

        override suspend fun update(account: AccountEntity) {
            items[account.id] = account
            emit()
        }

        override suspend fun delete(account: AccountEntity) {
            items.remove(account.id)
            emit()
        }

        override suspend fun getById(id: String): AccountEntity? = items[id]

        override fun observeAll(): Flow<List<AccountEntity>> = flow

        private fun emit() {
            flow.value = items.values.sortedBy { it.name }
        }
    }

    @Test
    fun testUpsertAndObserveAll_ReturnsDomainAccount() = runBlocking {
        val dao = FakeAccountDao()
        val repository = AccountRepositoryImpl(dao)
        val account = Account(
            id = "acct-1",
            name = "Primary",
            accountNumber = "1234",
            type = AccountType.SAVINGS_ACCOUNT,
            institution = "Bank",
            balanceCents = 100_00L,
            currency = "USD",
            isActive = true,
            createdAtEpochMs = 1700000000000L,
            updatedAtEpochMs = 1700000000000L
        )

        val result = repository.upsert(account)

        assertTrue(result is Result.Success)
        assertEquals(listOf(account), repository.observeAll().first())
        assertEquals(account, (repository.getById("acct-1") as Result.Success).data)
    }
}
