package com.atomicanalyst.data.db.mapper

import com.atomicanalyst.data.db.entity.ReconciliationLogEntity
import com.atomicanalyst.domain.model.ReconciliationLog

fun ReconciliationLogEntity.toDomain(): ReconciliationLog = ReconciliationLog(
    id = id,
    primaryTransactionId = primaryTransactionId,
    relatedTransactionIds = decodeStringList(relatedTransactionIdsJson),
    action = action,
    timestampEpochMs = timestampEpochMs,
    userId = userId,
    reason = reason
)

fun ReconciliationLog.toEntity(): ReconciliationLogEntity = ReconciliationLogEntity(
    id = id,
    primaryTransactionId = primaryTransactionId,
    relatedTransactionIdsJson = encodeStringList(relatedTransactionIds),
    action = action,
    timestampEpochMs = timestampEpochMs,
    userId = userId,
    reason = reason
)
