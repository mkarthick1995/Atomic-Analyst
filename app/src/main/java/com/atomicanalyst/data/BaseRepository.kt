package com.atomicanalyst.data

import com.atomicanalyst.domain.error.AppException
import com.atomicanalyst.domain.model.Result

abstract class BaseRepository {
    @Suppress("TooGenericExceptionCaught")
    protected suspend fun <T> safeCall(block: suspend () -> T): Result<T> {
        return try {
            Result.Success(block())
        } catch (exception: AppException) {
            Result.Error(exception)
        } catch (exception: Exception) {
            Result.Error(AppException.Unknown(exception))
        }
    }
}
