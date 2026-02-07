package com.atomicanalyst.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.atomicanalyst.domain.model.TransactionCategory

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val type: TransactionCategory?,
    val isSystem: Boolean,
    val createdAtEpochMs: Long,
    val updatedAtEpochMs: Long
)
