package com.atomicanalyst.di

import com.atomicanalyst.data.auth.AuthLocalStore
import com.atomicanalyst.data.security.SecureStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {
    @Provides
    @Singleton
    fun provideAuthLocalStore(storage: SecureStorage): AuthLocalStore = AuthLocalStore(storage)
}
