package com.atomicanalyst.di

import android.content.Context
import androidx.room.Room
import com.atomicanalyst.data.db.AtomicAnalystDatabase
import com.atomicanalyst.data.db.DatabaseMigrations
import com.atomicanalyst.data.db.dao.AccountDao
import com.atomicanalyst.data.db.dao.CategoryDao
import com.atomicanalyst.data.db.dao.ReconciliationLogDao
import com.atomicanalyst.data.db.dao.TagDao
import com.atomicanalyst.data.db.dao.TransactionDao
import com.atomicanalyst.data.security.DatabaseKeyStore
import com.atomicanalyst.data.security.SecureStorage
import com.atomicanalyst.utils.Clock
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import net.sqlcipher.database.SupportFactory
import net.sqlcipher.database.SQLiteDatabase

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    private const val DATABASE_NAME = "atomic_analyst.db"

    @Provides
    @Singleton
    fun provideDatabaseName(): String = DATABASE_NAME

    @Provides
    @Singleton
    fun provideDatabaseKeyStore(
        storage: SecureStorage,
        clock: Clock
    ): DatabaseKeyStore = DatabaseKeyStore(storage, clock)

    @Provides
    @Singleton
    fun provideSqlCipherFactory(
        keyStore: DatabaseKeyStore
    ): SupportFactory = SupportFactory(keyStore.getOrCreateKey())

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        supportFactory: SupportFactory
    ): AtomicAnalystDatabase {
        SQLiteDatabase.loadLibs(context)
        val builder = Room.databaseBuilder(
            context,
            AtomicAnalystDatabase::class.java,
            DATABASE_NAME
        )
            .openHelperFactory(supportFactory)
        DatabaseMigrations.MIGRATIONS.forEach { builder.addMigrations(it) }
        return builder.build()
    }

    @Provides
    fun provideAccountDao(database: AtomicAnalystDatabase): AccountDao = database.accountDao()

    @Provides
    fun provideCategoryDao(database: AtomicAnalystDatabase): CategoryDao = database.categoryDao()

    @Provides
    fun provideTagDao(database: AtomicAnalystDatabase): TagDao = database.tagDao()

    @Provides
    fun provideTransactionDao(database: AtomicAnalystDatabase): TransactionDao =
        database.transactionDao()

    @Provides
    fun provideReconciliationLogDao(database: AtomicAnalystDatabase): ReconciliationLogDao =
        database.reconciliationLogDao()
}
