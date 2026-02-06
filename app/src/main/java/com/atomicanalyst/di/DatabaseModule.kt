package com.atomicanalyst.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    private const val DATABASE_NAME = "atomic_analyst.db"

    @Provides
    @Singleton
    fun provideDatabaseName(): String = DATABASE_NAME
}
