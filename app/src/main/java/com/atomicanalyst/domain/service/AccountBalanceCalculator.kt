package com.atomicanalyst.domain.service

import com.atomicanalyst.domain.model.AccountBalanceSnapshot
import com.atomicanalyst.domain.model.AccountBalanceSummary
import com.atomicanalyst.domain.model.Transaction

class AccountBalanceCalculator @javax.inject.Inject constructor() {
    fun calculate(
        openingBalanceCents: Long,
        transactions: List<Transaction>,
        fromEpochMs: Long? = null,
        toEpochMs: Long? = null
    ): AccountBalanceSummary {
        val filtered = transactions
            .filter { tx ->
                val afterStart = fromEpochMs?.let { tx.timestampEpochMs >= it } ?: true
                val beforeEnd = toEpochMs?.let { tx.timestampEpochMs <= it } ?: true
                afterStart && beforeEnd
            }
            .sortedBy { it.timestampEpochMs }
        var running = openingBalanceCents
        val snapshots = mutableListOf<AccountBalanceSnapshot>()
        filtered.forEach { tx ->
            // Amounts are expected to be signed: credits positive, debits negative.
            running += tx.amountCents
            snapshots.add(
                AccountBalanceSnapshot(
                    timestampEpochMs = tx.timestampEpochMs,
                    balanceCents = running
                )
            )
        }
        return AccountBalanceSummary(
            openingBalanceCents = openingBalanceCents,
            closingBalanceCents = running,
            snapshots = snapshots
        )
    }
}
