package com.atomicanalyst.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.atomicanalyst.domain.model.ReconciliationAction

@Entity(
    tableName = "reconciliation_logs",
    indices = [Index("primaryTransactionId")]
)
data class ReconciliationLogEntity(
    @PrimaryKey
    val id: String,
    val primaryTransactionId: String,
    val relatedTransactionIdsJson: String,
    val action: ReconciliationAction,
    val timestampEpochMs: Long,
    val userId: String,
    val reason: String?
)
