package com.atomicanalyst.data.db.mapper

import com.atomicanalyst.data.db.entity.AccountEntity
import com.atomicanalyst.domain.model.Account

fun AccountEntity.toDomain(): Account = Account(
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

fun Account.toEntity(): AccountEntity = AccountEntity(
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
