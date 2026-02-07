package com.atomicanalyst.data.db.mapper

import com.atomicanalyst.data.db.entity.StandingInstructionEntity
import com.atomicanalyst.domain.model.StandingInstruction

fun StandingInstructionEntity.toDomain(): StandingInstruction = StandingInstruction(
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

fun StandingInstruction.toEntity(): StandingInstructionEntity = StandingInstructionEntity(
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
