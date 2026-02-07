package com.atomicanalyst.data.repository

import com.atomicanalyst.data.db.dao.TransactionDao
import com.atomicanalyst.data.db.entity.TagEntity
import com.atomicanalyst.data.db.entity.TransactionEntity
import com.atomicanalyst.data.db.entity.TransactionTagCrossRef
import com.atomicanalyst.data.db.entity.TransactionWithTags
import com.atomicanalyst.domain.model.Result
import com.atomicanalyst.domain.model.Transaction
import com.atomicanalyst.domain.model.TransactionSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TransactionRepositoryImplTest {
    private class FakeTransactionDao : TransactionDao {
        private val items = mutableMapOf<String, TransactionEntity>()
        private val crossRefs = mutableSetOf<TransactionTagCrossRef>()
        private val tags = mutableMapOf<String, TagEntity>()
        private val flow = MutableStateFlow<List<TransactionEntity>>(emptyList())

        override suspend fun upsert(transaction: TransactionEntity) {
            items[transaction.id] = transaction
            emit()
        }

        override suspend fun update(transaction: TransactionEntity) {
            items[transaction.id] = transaction
            emit()
        }

        override suspend fun delete(transaction: TransactionEntity) {
            items.remove(transaction.id)
            crossRefs.removeAll { it.transactionId == transaction.id }
            emit()
        }

        override suspend fun getById(id: String): TransactionEntity? = items[id]

        override fun observeAll(): Flow<List<TransactionEntity>> = flow

        override suspend fun upsertTagCrossRefs(refs: List<TransactionTagCrossRef>) {
            crossRefs.addAll(refs)
        }

        override suspend fun clearTagsForTransaction(transactionId: String) {
            crossRefs.removeAll { it.transactionId == transactionId }
        }

        override suspend fun getWithTags(id: String): TransactionWithTags? {
            val transaction = items[id] ?: return null
            return TransactionWithTags(
                transaction = transaction,
                tags = resolveTags(id)
            )
        }

        override fun observeAllWithTags(): Flow<List<TransactionWithTags>> =
            flow.map { list ->
                list.map { transaction ->
                    TransactionWithTags(
                        transaction = transaction,
                        tags = resolveTags(transaction.id)
                    )
                }
            }

        fun seedTags(values: List<TagEntity>) {
            values.forEach { tags[it.id] = it }
        }

        private fun resolveTags(transactionId: String): List<TagEntity> {
            val tagIds = crossRefs.filter { it.transactionId == transactionId }
                .map { it.tagId }
            return tagIds.mapNotNull { tags[it] }
        }

        private fun emit() {
            flow.value = items.values.sortedByDescending { it.timestampEpochMs }
        }
    }

    @Test
    fun testUpsertAndObserveAll_ReturnsDomainTransaction() = runBlocking {
        val dao = FakeTransactionDao()
        dao.seedTags(listOf(TagEntity("tag-1", "Bills", 1700000000000L)))
        val repository = TransactionRepositoryImpl(dao)
        val transaction = Transaction(
            id = "txn-1",
            accountId = "acct-1",
            categoryId = null,
            amountCents = 5000L,
            currency = "USD",
            description = "Groceries",
            timestampEpochMs = 1700000000000L,
            source = TransactionSource.MANUAL,
            notes = null,
            isReconciled = false,
            relatedTransactionId = null,
            tags = listOf("tag-1")
        )

        val result = repository.upsert(transaction)

        assertTrue(result is Result.Success)
        assertEquals(listOf(transaction), repository.observeAll().first())
        assertEquals(transaction, (repository.getById("txn-1") as Result.Success).data)
    }
}
