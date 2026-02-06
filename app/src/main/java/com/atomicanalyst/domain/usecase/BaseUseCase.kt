package com.atomicanalyst.domain.usecase

import com.atomicanalyst.domain.model.Result

abstract class BaseUseCase<in P, R> {
    suspend operator fun invoke(params: P): Result<R> = execute(params)

    protected abstract suspend fun execute(params: P): Result<R>
}
