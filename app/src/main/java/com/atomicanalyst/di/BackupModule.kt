package com.atomicanalyst.di

import android.content.Context
import com.atomicanalyst.data.auth.AuthLocalStore
import androidx.room.withTransaction
import com.atomicanalyst.data.backup.AppBackupDataSource
import com.atomicanalyst.data.backup.BackupDaos
import com.atomicanalyst.data.backup.BackupCrypto
import com.atomicanalyst.data.backup.BackupDataSource
import com.atomicanalyst.data.backup.BackupManager
import com.atomicanalyst.data.backup.BackupPassphraseStore
import com.atomicanalyst.data.backup.BackupStore
import com.atomicanalyst.data.backup.cloud.CloudBackupClient
import com.atomicanalyst.data.backup.cloud.DefaultDriveServiceFactory
import com.atomicanalyst.data.backup.cloud.DefaultGoogleAccountProvider
import com.atomicanalyst.data.backup.cloud.DriveSignInManager
import com.atomicanalyst.data.backup.cloud.DriveServiceFactory
import com.atomicanalyst.data.backup.cloud.GoogleDriveBackupClient
import com.atomicanalyst.data.backup.cloud.GoogleAccountProvider
import com.atomicanalyst.data.db.AtomicAnalystDatabase
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
        database: AtomicAnalystDatabase,
        clock: Clock
    ): BackupDataSource = AppBackupDataSource(
        authLocalStore = authLocalStore,
        daos = BackupDaos(
            accountDao = database.accountDao(),
            categoryDao = database.categoryDao(),
            tagDao = database.tagDao(),
            transactionDao = database.transactionDao(),
            transactionTagDao = database.transactionTagDao(),
            reconciliationLogDao = database.reconciliationLogDao()
        ),
        runInTransaction = { block -> database.withTransaction { block() } },
        clock = clock
    )

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

    @Provides
    @Singleton
    fun provideCloudBackupClient(
        client: GoogleDriveBackupClient
    ): CloudBackupClient = client

    @Provides
    @Singleton
    fun provideGoogleAccountProvider(
        @ApplicationContext context: Context
    ): GoogleAccountProvider = DefaultGoogleAccountProvider(context)

    @Provides
    @Singleton
    fun provideDriveServiceFactory(
        @ApplicationContext context: Context
    ): DriveServiceFactory = DefaultDriveServiceFactory(context)

    @Provides
    @Singleton
    fun provideDriveSignInManager(
        @ApplicationContext context: Context
    ): DriveSignInManager = DriveSignInManager(context)
}
