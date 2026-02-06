package com.atomicanalyst.di

import com.atomicanalyst.utils.Clock
import com.atomicanalyst.utils.DefaultDispatcherProvider
import com.atomicanalyst.utils.DispatcherProvider
import com.atomicanalyst.utils.SystemClock
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDispatcherProvider(): DispatcherProvider = DefaultDispatcherProvider()

    @Provides
    @Singleton
    fun provideClock(): Clock = SystemClock
}
