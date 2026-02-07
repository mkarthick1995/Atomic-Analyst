package com.atomicanalyst.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.atomicanalyst.data.db.entity.TransactionTagCrossRef
import com.atomicanalyst.data.db.entity.TagWithTransactions
import com.atomicanalyst.data.db.entity.TransactionWithTags
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionTagDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTagCrossRefs(refs: List<TransactionTagCrossRef>)

    @Query("DELETE FROM transaction_tag_cross_ref WHERE transactionId = :transactionId")
    suspend fun clearTagsForTransaction(transactionId: String)

    @Query("SELECT * FROM transaction_tag_cross_ref")
    suspend fun getAllTagCrossRefs(): List<TransactionTagCrossRef>

    @Query("DELETE FROM transaction_tag_cross_ref")
    suspend fun clearAllTagCrossRefs()

    @Transaction
    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getWithTags(id: String): TransactionWithTags?

    @Transaction
    @Query("SELECT * FROM transactions ORDER BY timestampEpochMs DESC")
    fun observeAllWithTags(): Flow<List<TransactionWithTags>>

    @Transaction
    @Query("SELECT * FROM tags WHERE id = :id")
    suspend fun getTagWithTransactions(id: String): TagWithTransactions?

    @Transaction
    @Query("SELECT * FROM tags ORDER BY name")
    fun observeAllTagsWithTransactions(): Flow<List<TagWithTransactions>>
}
