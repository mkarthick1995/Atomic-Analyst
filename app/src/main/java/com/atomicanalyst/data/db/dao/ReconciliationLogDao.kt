package com.atomicanalyst.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.atomicanalyst.data.db.entity.ReconciliationLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReconciliationLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(log: ReconciliationLogEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(logs: List<ReconciliationLogEntity>)

    @Query("SELECT * FROM reconciliation_logs")
    suspend fun getAll(): List<ReconciliationLogEntity>

    @Query("SELECT * FROM reconciliation_logs WHERE primaryTransactionId = :transactionId")
    fun observeLogs(transactionId: String): Flow<List<ReconciliationLogEntity>>

    @Query("DELETE FROM reconciliation_logs")
    suspend fun clearAll()
}
