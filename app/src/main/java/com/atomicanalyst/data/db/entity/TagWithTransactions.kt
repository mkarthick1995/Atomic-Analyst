package com.atomicanalyst.data.db.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class TagWithTransactions(
    @Embedded
    val tag: TagEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = TransactionTagCrossRef::class,
            parentColumn = "tagId",
            entityColumn = "transactionId"
        )
    )
    val transactions: List<TransactionEntity>
)
