package com.atomicanalyst.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.atomicanalyst.domain.model.Frequency

@Entity(
    tableName = "standing_instructions",
    foreignKeys = [
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["accountId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("accountId")
    ]
)
data class StandingInstructionEntity(
    @PrimaryKey
    val id: String,
    val accountId: String,
    val description: String,
    val amountCents: Long,
    val frequency: Frequency,
    val nextExecutionEpochMs: Long,
    val createdAtEpochMs: Long,
    val updatedAtEpochMs: Long,
    val isActive: Boolean
)
