package com.atomicanalyst.domain.error

sealed class AppException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {
    class Network(cause: Throwable? = null) : AppException("Network error", cause)
    class Database(cause: Throwable? = null) : AppException("Database error", cause)
    class Security(cause: Throwable? = null) : AppException("Security error", cause)
    class Validation(message: String) : AppException(message)
    class Unknown(cause: Throwable? = null) : AppException("Unknown error", cause)
}
