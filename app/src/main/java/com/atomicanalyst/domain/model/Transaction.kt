package com.atomicanalyst.domain.model

data class Transaction(
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
    val relatedTransactionId: String?,
    val tags: List<String>
)
