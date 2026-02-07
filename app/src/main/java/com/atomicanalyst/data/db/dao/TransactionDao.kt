package com.atomicanalyst.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.atomicanalyst.data.db.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(transaction: TransactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(transactions: List<TransactionEntity>)

    @Update
    suspend fun update(transaction: TransactionEntity)

    @Delete
    suspend fun delete(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getById(id: String): TransactionEntity?

    @Query("SELECT * FROM transactions")
    suspend fun getAll(): List<TransactionEntity>

    @Query("SELECT * FROM transactions ORDER BY timestampEpochMs DESC")
    fun observeAll(): Flow<List<TransactionEntity>>

    @Query("DELETE FROM transactions")
    suspend fun clearAll()
}
