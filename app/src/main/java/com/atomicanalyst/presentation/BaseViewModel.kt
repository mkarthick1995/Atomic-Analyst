package com.atomicanalyst.presentation

import androidx.lifecycle.ViewModel
import com.atomicanalyst.domain.error.AppException
import com.atomicanalyst.domain.model.Resource
import com.atomicanalyst.domain.model.Result

abstract class BaseViewModel : ViewModel() {
    protected fun <T> resultToResource(result: Result<T>): Resource<T> {
        return when (result) {
            is Result.Success -> Resource.Success(result.data)
            is Result.Error -> Resource.Error(result.exception.message ?: "Unknown error")
            Result.Loading -> Resource.Loading
        }
    }

    protected fun toAppException(throwable: Throwable): AppException {
        return if (throwable is AppException) {
            throwable
        } else {
            AppException.Unknown(throwable)
        }
    }
}
