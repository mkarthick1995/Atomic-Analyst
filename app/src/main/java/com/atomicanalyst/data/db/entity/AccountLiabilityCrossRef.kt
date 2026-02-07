package com.atomicanalyst.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "account_liability_cross_ref",
    primaryKeys = ["accountId", "liabilityAccountId"],
    foreignKeys = [
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["accountId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["liabilityAccountId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("accountId"),
        Index("liabilityAccountId")
    ]
)
data class AccountLiabilityCrossRef(
    val accountId: String,
    val liabilityAccountId: String
)
