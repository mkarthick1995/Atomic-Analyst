package com.atomicanalyst.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.atomicanalyst.domain.model.TransactionSource

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["accountId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("accountId"),
        Index("categoryId"),
        Index("timestampEpochMs")
    ]
)
data class TransactionEntity(
    @PrimaryKey
    val id: String,
    val accountId: String,
    val categoryId: String?,
    val amountCents: Long,
    val currency: String,
    val description: String,
    val timestampEpochMs: Long,
    val source: TransactionSource,
    val notes: String?,
    val isReconciled: Boolean,
    val relatedTransactionId: String?
)
