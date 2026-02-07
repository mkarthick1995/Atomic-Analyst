package com.atomicanalyst.domain.service

import com.atomicanalyst.domain.model.AccountBalanceSummary
import com.atomicanalyst.domain.model.Transaction
import com.atomicanalyst.domain.model.TransactionSource
import org.junit.Assert.assertEquals
import org.junit.Test

class AccountBalanceCalculatorTest {
    @Test
    fun calculate_BuildsSnapshotsFromSignedTransactions() {
        val calculator = AccountBalanceCalculator()
        val transactions = listOf(
            Transaction(
                id = "txn-1",
                accountId = "acct-1",
                categoryId = null,
                amountCents = 200L,
                currency = "USD",
                description = "Income",
                timestampEpochMs = 1_000L,
                source = TransactionSource.MANUAL,
                notes = null,
                isReconciled = false,
                relatedTransactionId = null,
                tags = emptyList()
            ),
            Transaction(
                id = "txn-2",
                accountId = "acct-1",
                categoryId = null,
                amountCents = -50L,
                currency = "USD",
                description = "Expense",
                timestampEpochMs = 2_000L,
                source = TransactionSource.MANUAL,
                notes = null,
                isReconciled = false,
                relatedTransactionId = null,
                tags = emptyList()
            )
        )

        val summary = calculator.calculate(1_000L, transactions)

        assertEquals(1_000L, summary.openingBalanceCents)
        assertEquals(1_150L, summary.closingBalanceCents)
        assertEquals(listOf(1_200L, 1_150L), summary.snapshots.map { it.balanceCents })
    }
}
