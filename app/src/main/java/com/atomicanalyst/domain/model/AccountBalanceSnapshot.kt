package com.atomicanalyst.domain.model

data class AccountBalanceSnapshot(
    val timestampEpochMs: Long,
    val balanceCents: Long
)
