package com.atomicanalyst.domain.model

data class AccountBalanceSummary(
    val openingBalanceCents: Long,
    val closingBalanceCents: Long,
    val snapshots: List<AccountBalanceSnapshot>
)
