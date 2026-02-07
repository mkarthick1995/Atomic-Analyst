package com.atomicanalyst.data.backup

import com.atomicanalyst.data.db.entity.AccountEntity
import com.atomicanalyst.data.db.entity.CategoryEntity
import com.atomicanalyst.data.db.entity.ReconciliationLogEntity
import com.atomicanalyst.data.db.entity.TagEntity
import com.atomicanalyst.data.db.entity.TransactionEntity
import com.atomicanalyst.data.db.entity.TransactionTagCrossRef
import com.atomicanalyst.data.db.mapper.encodeStringList

fun AccountBackup.toEntity(): AccountEntity = AccountEntity(
    id = id,
    name = name,
    accountNumber = accountNumber,
    type = type,
    institution = institution,
    balanceCents = balanceCents,
    currency = currency,
    isActive = isActive,
    createdAtEpochMs = createdAtEpochMs,
    updatedAtEpochMs = updatedAtEpochMs
)

fun CategoryBackup.toEntity(): CategoryEntity = CategoryEntity(
    id = id,
    name = name,
    type = type,
    isSystem = isSystem,
    createdAtEpochMs = createdAtEpochMs,
    updatedAtEpochMs = updatedAtEpochMs
)

fun TagBackup.toEntity(): TagEntity = TagEntity(
    id = id,
    name = name,
    createdAtEpochMs = createdAtEpochMs
)

fun TransactionBackup.toEntity(): TransactionEntity = TransactionEntity(
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

fun TransactionTagBackup.toEntity(): TransactionTagCrossRef = TransactionTagCrossRef(
    transactionId = transactionId,
    tagId = tagId
)

fun ReconciliationLogBackup.toEntity(): ReconciliationLogEntity = ReconciliationLogEntity(
    id = id,
    primaryTransactionId = primaryTransactionId,
    relatedTransactionIdsJson = encodeStringList(relatedTransactionIds),
    action = action,
    timestampEpochMs = timestampEpochMs,
    userId = userId,
    reason = reason
)
