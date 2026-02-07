package com.atomicanalyst.domain.model

data class StandingInstruction(
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
