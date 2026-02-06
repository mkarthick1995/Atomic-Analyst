package com.atomicanalyst.di

import com.atomicanalyst.data.auth.AndroidBiometricCapability
import com.atomicanalyst.data.auth.BiometricCapability
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthBindingsModule {
    @Binds
    @Singleton
    abstract fun bindBiometricCapability(
        impl: AndroidBiometricCapability
    ): BiometricCapability
}
