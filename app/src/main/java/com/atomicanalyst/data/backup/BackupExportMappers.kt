package com.atomicanalyst.data.backup

import com.atomicanalyst.data.db.entity.AccountEntity
import com.atomicanalyst.data.db.entity.AccountLiabilityCrossRef
import com.atomicanalyst.data.db.entity.CategoryEntity
import com.atomicanalyst.data.db.entity.ReconciliationLogEntity
import com.atomicanalyst.data.db.entity.StandingInstructionEntity
import com.atomicanalyst.data.db.entity.TagEntity
import com.atomicanalyst.data.db.entity.TransactionEntity
import com.atomicanalyst.data.db.entity.TransactionTagCrossRef
import com.atomicanalyst.data.db.mapper.decodeStringList

fun AccountEntity.toBackup(): AccountBackup = AccountBackup(
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

fun AccountLiabilityCrossRef.toBackup(): AccountLiabilityBackup = AccountLiabilityBackup(
    accountId = accountId,
    liabilityAccountId = liabilityAccountId
)

fun CategoryEntity.toBackup(): CategoryBackup = CategoryBackup(
    id = id,
    name = name,
    type = type,
    isSystem = isSystem,
    createdAtEpochMs = createdAtEpochMs,
    updatedAtEpochMs = updatedAtEpochMs
)

fun StandingInstructionEntity.toBackup(): StandingInstructionBackup = StandingInstructionBackup(
    id = id,
    accountId = accountId,
    description = description,
    amountCents = amountCents,
    frequency = frequency,
    nextExecutionEpochMs = nextExecutionEpochMs,
    createdAtEpochMs = createdAtEpochMs,
    updatedAtEpochMs = updatedAtEpochMs,
    isActive = isActive
)

fun TagEntity.toBackup(): TagBackup = TagBackup(
    id = id,
    name = name,
    createdAtEpochMs = createdAtEpochMs
)

fun TransactionEntity.toBackup(): TransactionBackup = TransactionBackup(
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

fun TransactionTagCrossRef.toBackup(): TransactionTagBackup = TransactionTagBackup(
    transactionId = transactionId,
    tagId = tagId
)

fun ReconciliationLogEntity.toBackup(): ReconciliationLogBackup = ReconciliationLogBackup(
    id = id,
    primaryTransactionId = primaryTransactionId,
    relatedTransactionIds = decodeStringList(relatedTransactionIdsJson),
    action = action,
    timestampEpochMs = timestampEpochMs,
    userId = userId,
    reason = reason
)
