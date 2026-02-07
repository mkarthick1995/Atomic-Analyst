package com.atomicanalyst.di

import com.atomicanalyst.data.auth.AuthRepositoryImpl
import com.atomicanalyst.data.repository.AccountLiabilityRepositoryImpl
import com.atomicanalyst.data.repository.AccountRepositoryImpl
import com.atomicanalyst.data.repository.CategoryRepositoryImpl
import com.atomicanalyst.data.repository.ReconciliationLogRepositoryImpl
import com.atomicanalyst.data.repository.StandingInstructionRepositoryImpl
import com.atomicanalyst.data.repository.TagRepositoryImpl
import com.atomicanalyst.data.repository.TransactionRepositoryImpl
import com.atomicanalyst.domain.repository.AccountLiabilityRepository
import com.atomicanalyst.domain.repository.AccountRepository
import com.atomicanalyst.domain.repository.AuthRepository
import com.atomicanalyst.domain.repository.CategoryRepository
import com.atomicanalyst.domain.repository.ReconciliationLogRepository
import com.atomicanalyst.domain.repository.StandingInstructionRepository
import com.atomicanalyst.domain.repository.TagRepository
import com.atomicanalyst.domain.repository.TransactionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindAccountRepository(
        impl: AccountRepositoryImpl
    ): AccountRepository

    @Binds
    @Singleton
    abstract fun bindAccountLiabilityRepository(
        impl: AccountLiabilityRepositoryImpl
    ): AccountLiabilityRepository

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(
        impl: CategoryRepositoryImpl
    ): CategoryRepository

    @Binds
    @Singleton
    abstract fun bindTagRepository(
        impl: TagRepositoryImpl
    ): TagRepository

    @Binds
    @Singleton
    abstract fun bindStandingInstructionRepository(
        impl: StandingInstructionRepositoryImpl
    ): StandingInstructionRepository

    @Binds
    @Singleton
    abstract fun bindTransactionRepository(
        impl: TransactionRepositoryImpl
    ): TransactionRepository

    @Binds
    @Singleton
    abstract fun bindReconciliationLogRepository(
        impl: ReconciliationLogRepositoryImpl
    ): ReconciliationLogRepository
}
