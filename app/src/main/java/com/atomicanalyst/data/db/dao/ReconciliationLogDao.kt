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

    @Query("SELECT * FROM reconciliation_logs WHERE primaryTransactionId = :transactionId")
    fun observeLogs(transactionId: String): Flow<List<ReconciliationLogEntity>>
}
