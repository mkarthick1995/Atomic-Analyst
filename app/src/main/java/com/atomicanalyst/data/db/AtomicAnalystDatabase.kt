package com.atomicanalyst.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.atomicanalyst.data.db.converter.EnumConverters
import com.atomicanalyst.data.db.dao.AccountDao
import com.atomicanalyst.data.db.dao.CategoryDao
import com.atomicanalyst.data.db.dao.ReconciliationLogDao
import com.atomicanalyst.data.db.dao.TagDao
import com.atomicanalyst.data.db.dao.TransactionDao
import com.atomicanalyst.data.db.dao.TransactionTagDao
import com.atomicanalyst.data.db.entity.AccountEntity
import com.atomicanalyst.data.db.entity.CategoryEntity
import com.atomicanalyst.data.db.entity.ReconciliationLogEntity
import com.atomicanalyst.data.db.entity.TagEntity
import com.atomicanalyst.data.db.entity.TransactionEntity
import com.atomicanalyst.data.db.entity.TransactionTagCrossRef

@Database(
    entities = [
        AccountEntity::class,
        CategoryEntity::class,
        TagEntity::class,
        TransactionEntity::class,
        TransactionTagCrossRef::class,
        ReconciliationLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(EnumConverters::class)
abstract class AtomicAnalystDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun categoryDao(): CategoryDao
    abstract fun tagDao(): TagDao
    abstract fun transactionDao(): TransactionDao
    abstract fun transactionTagDao(): TransactionTagDao
    abstract fun reconciliationLogDao(): ReconciliationLogDao
}
