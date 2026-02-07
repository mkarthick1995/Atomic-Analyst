package com.atomicanalyst.domain.model

data class Account(
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
