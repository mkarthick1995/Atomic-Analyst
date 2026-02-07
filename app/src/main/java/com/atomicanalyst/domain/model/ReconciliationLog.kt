package com.atomicanalyst.domain.model

data class ReconciliationLog(
    val id: String,
    val primaryTransactionId: String,
    val relatedTransactionIds: List<String>,
    val action: ReconciliationAction,
    val timestampEpochMs: Long,
    val userId: String,
    val reason: String?
)
