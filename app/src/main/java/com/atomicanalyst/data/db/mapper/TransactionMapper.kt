package com.atomicanalyst.data.db.mapper

import com.atomicanalyst.data.db.entity.TransactionEntity
import com.atomicanalyst.data.db.entity.TransactionWithTags
import com.atomicanalyst.domain.model.Transaction

fun TransactionWithTags.toDomain(): Transaction = Transaction(
    id = transaction.id,
    accountId = transaction.accountId,
    categoryId = transaction.categoryId,
    amountCents = transaction.amountCents,
    currency = transaction.currency,
    description = transaction.description,
    timestampEpochMs = transaction.timestampEpochMs,
    source = transaction.source,
    notes = transaction.notes,
    isReconciled = transaction.isReconciled,
    relatedTransactionId = transaction.relatedTransactionId,
    tags = tags.map { it.id }
)

fun Transaction.toEntity(): TransactionEntity = TransactionEntity(
    id = id,
    accountId = accountId,
    categoryId = categoryId,
    amountCents = amountCents,
    currency = currency,
    description = description,
    timestampEpochMs = timestampEpochMs,
    source = source,
    notes = notes,
    isReconciled = isReconciled,
    relatedTransactionId = relatedTransactionId
)
