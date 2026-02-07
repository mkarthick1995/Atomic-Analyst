package com.atomicanalyst.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.atomicanalyst.domain.model.AccountType

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val accountNumber: String,
    val type: AccountType,
    val institution: String,
    val balanceCents: Long,
    val currency: String,
    val isActive: Boolean,
    val createdAtEpochMs: Long,
    val updatedAtEpochMs: Long
)
