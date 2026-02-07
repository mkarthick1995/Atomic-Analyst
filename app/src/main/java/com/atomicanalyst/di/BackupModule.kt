package com.atomicanalyst.di

import android.content.Context
import com.atomicanalyst.data.auth.AuthLocalStore
import com.atomicanalyst.data.backup.AppBackupDataSource
import com.atomicanalyst.data.backup.BackupCrypto
import com.atomicanalyst.data.backup.BackupDataSource
import com.atomicanalyst.data.backup.BackupManager
import com.atomicanalyst.data.backup.BackupPassphraseStore
import com.atomicanalyst.data.backup.BackupStore
import com.atomicanalyst.utils.Clock
import com.atomicanalyst.utils.DispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BackupModule {
    @Provides
    @Singleton
    fun provideBackupStore(
        @ApplicationContext context: Context
    ): BackupStore = BackupStore(File(context.filesDir, "backups"))

    @Provides
    @Singleton
    fun provideBackupCrypto(clock: Clock): BackupCrypto = BackupCrypto(clock = clock)

    @Provides
    @Singleton
    fun provideBackupDataSource(
        authLocalStore: AuthLocalStore,
        clock: Clock
    ): BackupDataSource = AppBackupDataSource(authLocalStore, clock)

    @Provides
    @Singleton
    @Suppress("LongParameterList")
    fun provideBackupManager(
        dataSource: BackupDataSource,
        crypto: BackupCrypto,
        store: BackupStore,
        passphraseStore: BackupPassphraseStore,
        dispatcherProvider: DispatcherProvider,
        clock: Clock
    ): BackupManager = BackupManager(
        dataSource = dataSource,
        crypto = crypto,
        store = store,
        passphraseStore = passphraseStore,
        dispatcherProvider = dispatcherProvider,
        clock = clock
    )
}
