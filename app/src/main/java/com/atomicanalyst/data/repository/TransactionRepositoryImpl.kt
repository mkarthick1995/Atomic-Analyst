package com.atomicanalyst.data.repository

import com.atomicanalyst.data.BaseRepository
import com.atomicanalyst.data.db.dao.TransactionDao
import com.atomicanalyst.data.db.dao.TransactionTagDao
import com.atomicanalyst.data.db.mapper.toDomain
import com.atomicanalyst.data.db.mapper.toEntity
import com.atomicanalyst.data.db.entity.TransactionTagCrossRef
import com.atomicanalyst.domain.model.Result
import com.atomicanalyst.domain.model.Transaction
import com.atomicanalyst.domain.repository.TransactionRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao,
    private val transactionTagDao: TransactionTagDao
) : BaseRepository(), TransactionRepository {
    override suspend fun upsert(transaction: Transaction): Result<Unit> = safeCall {
        transactionDao.upsert(transaction.toEntity())
        updateTags(transaction)
    }

    override suspend fun update(transaction: Transaction): Result<Unit> = safeCall {
        transactionDao.update(transaction.toEntity())
        updateTags(transaction)
    }

    override suspend fun delete(transaction: Transaction): Result<Unit> = safeCall {
        transactionDao.delete(transaction.toEntity())
    }

    override suspend fun getById(id: String): Result<Transaction?> = safeCall {
        transactionTagDao.getWithTags(id)?.toDomain()
    }

    override fun observeAll(): Flow<List<Transaction>> = transactionTagDao.observeAllWithTags()
        .map { transactions -> transactions.map { it.toDomain() } }

    private suspend fun updateTags(transaction: Transaction) {
        transactionTagDao.clearTagsForTransaction(transaction.id)
        val refs = transaction.tags.distinct().map { tagId ->
            TransactionTagCrossRef(
                transactionId = transaction.id,
                tagId = tagId
            )
        }
        if (refs.isNotEmpty()) {
            transactionTagDao.upsertTagCrossRefs(refs)
        }
    }
}
