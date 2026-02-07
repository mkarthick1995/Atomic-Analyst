package com.atomicanalyst.di

import com.atomicanalyst.data.security.DatabaseKeyStore
import com.atomicanalyst.data.security.SecureStorage
import com.atomicanalyst.utils.Clock
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import net.sqlcipher.database.SupportFactory

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
}
