package com.atomicanalyst.data.db.mapper

import com.atomicanalyst.data.db.entity.TagEntity
import com.atomicanalyst.data.db.entity.TransactionWithTags
import com.atomicanalyst.domain.model.ReconciliationAction
import com.atomicanalyst.domain.model.ReconciliationLog
import com.atomicanalyst.domain.model.Transaction
import com.atomicanalyst.domain.model.TransactionSource
import org.junit.Assert.assertEquals
import org.junit.Test

class DbMappersTest {
    @Test
    fun testTransactionMapping_RoundTrip() {
        val transaction = Transaction(
            id = "txn-1",
            accountId = "acct-1",
            categoryId = "cat-1",
            amountCents = 1250L,
            currency = "USD",
            description = "Coffee",
            timestampEpochMs = 1700000000000L,
            source = TransactionSource.MANUAL,
            notes = "morning",
            isReconciled = false,
            relatedTransactionId = null,
            tags = listOf("tag-1", "tag-2")
        )

        val transactionEntity = transaction.toEntity()
        val relation = TransactionWithTags(
            transaction = transactionEntity,
            tags = listOf(
                TagEntity("tag-1", "Bills", 1700000000000L),
                TagEntity("tag-2", "Food", 1700000000000L)
            )
        )
        val mapped = relation.toDomain()

        assertEquals(transaction, mapped)
    }

    @Test
    fun testReconciliationLogMapping_RoundTrip() {
        val log = ReconciliationLog(
            id = "log-1",
            primaryTransactionId = "txn-1",
            relatedTransactionIds = listOf("txn-2", "txn-3"),
            action = ReconciliationAction.MERGED,
            timestampEpochMs = 1700000005000L,
            userId = "user-1",
            reason = "manual merge"
        )

        val mapped = log.toEntity().toDomain()

        assertEquals(log, mapped)
    }
}
