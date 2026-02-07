package com.atomicanalyst.domain.model

data class StandingInstruction(
    val id: String,
    val description: String,
    val amountCents: Long,
    val frequency: Frequency,
    val nextExecutionEpochMs: Long
)
