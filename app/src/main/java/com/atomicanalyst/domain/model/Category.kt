package com.atomicanalyst.domain.model

data class Category(
    val id: String,
    val name: String,
    val type: TransactionCategory?,
    val isSystem: Boolean,
    val createdAtEpochMs: Long,
    val updatedAtEpochMs: Long
)
