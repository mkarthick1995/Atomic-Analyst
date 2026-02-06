package com.atomicanalyst.di

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.atomicanalyst.data.backup.BackupPassphraseStore
import com.atomicanalyst.data.security.EncryptedPrefsSecureStorage
import com.atomicanalyst.data.security.PasswordHasher
import com.atomicanalyst.data.security.SecureStorage
import com.atomicanalyst.data.security.SessionManager
import com.atomicanalyst.utils.Clock
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SecurityModule {
    private const val SECURE_PREFS_FILE = "atomic_analyst_secure_prefs"

    @Provides
    @Singleton
    fun provideMasterKey(@ApplicationContext context: Context): MasterKey {
        return MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    @Provides
    @Singleton
    fun provideEncryptedSharedPreferences(
        @ApplicationContext context: Context,
        masterKey: MasterKey
    ): SharedPreferences = EncryptedSharedPreferences.create(
        context,
        SECURE_PREFS_FILE,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    @Provides
    @Singleton
    fun provideSecureStorage(
        prefs: SharedPreferences
    ): SecureStorage = EncryptedPrefsSecureStorage(prefs)

    @Provides
    @Singleton
    fun providePasswordHasher(): PasswordHasher = PasswordHasher()

    @Provides
    @Singleton
    fun provideSessionManager(
        storage: SecureStorage,
        clock: Clock
    ): SessionManager = SessionManager(storage, clock)

    @Provides
    @Singleton
    fun provideBackupPassphraseStore(
        storage: SecureStorage
    ): BackupPassphraseStore = BackupPassphraseStore(storage)
}
