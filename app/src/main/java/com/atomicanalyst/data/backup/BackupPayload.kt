package com.atomicanalyst.data.backup

import kotlinx.serialization.Serializable

@Serializable
data class BackupPayload(
    val version: Int,
    val createdAtEpochMs: Long,
    val auth: AuthBackup?,
    val accounts: List<AccountBackup> = emptyList(),
    val accountLiabilities: List<AccountLiabilityBackup> = emptyList(),
    val categories: List<CategoryBackup> = emptyList(),
    val standingInstructions: List<StandingInstructionBackup> = emptyList(),
    val tags: List<TagBackup> = emptyList(),
    val transactions: List<TransactionBackup> = emptyList(),
    val transactionTags: List<TransactionTagBackup> = emptyList(),
    val reconciliationLogs: List<ReconciliationLogBackup> = emptyList()
)

@Serializable
data class AuthBackup(
    val userId: String,
    val passwordHash: String,
    val biometricEnabled: Boolean
)

@Serializable
data class AccountBackup(
    val id: String,
    val name: String,
    val accountNumber: String,
    val type: com.atomicanalyst.domain.model.AccountType,
    val institution: String,
    val balanceCents: Long,
    val currency: String,
    val isActive: Boolean,
    val createdAtEpochMs: Long,
    val updatedAtEpochMs: Long
)

@Serializable
data class AccountLiabilityBackup(
    val accountId: String,
    val liabilityAccountId: String
)

@Serializable
data class CategoryBackup(
    val id: String,
    val name: String,
    val type: com.atomicanalyst.domain.model.TransactionCategory?,
    val isSystem: Boolean,
    val createdAtEpochMs: Long,
    val updatedAtEpochMs: Long
)

@Serializable
data class StandingInstructionBackup(
    val id: String,
    val accountId: String,
    val description: String,
    val amountCents: Long,
    val frequency: com.atomicanalyst.domain.model.Frequency,
    val nextExecutionEpochMs: Long,
    val createdAtEpochMs: Long,
    val updatedAtEpochMs: Long,
    val isActive: Boolean
)

@Serializable
data class TagBackup(
    val id: String,
    val name: String,
    val createdAtEpochMs: Long
)

@Serializable
data class TransactionBackup(
    val id: String,
    val accountId: String,
    val categoryId: String?,
    val amountCents: Long,
    val currency: String,
    val description: String,
    val timestampEpochMs: Long,
    val source: com.atomicanalyst.domain.model.TransactionSource,
    val notes: String?,
    val isReconciled: Boolean,
    val relatedTransactionId: String?
)

@Serializable
data class TransactionTagBackup(
    val transactionId: String,
    val tagId: String
)

@Serializable
data class ReconciliationLogBackup(
    val id: String,
    val primaryTransactionId: String,
    val relatedTransactionIds: List<String>,
    val action: com.atomicanalyst.domain.model.ReconciliationAction,
    val timestampEpochMs: Long,
    val userId: String,
    val reason: String?
)
