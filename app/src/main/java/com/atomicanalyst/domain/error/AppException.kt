package com.atomicanalyst.domain.error

sealed class AppException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {
    class Network(cause: Throwable? = null) : AppException("Network error", cause)
    class Database(cause: Throwable? = null) : AppException("Database error", cause)
    class Security(message: String = "Security error", cause: Throwable? = null) :
        AppException(message, cause)
    class Authentication(message: String = "Authentication error") : AppException(message)
    class Validation(message: String, cause: Throwable? = null) : AppException(message, cause)
    class Unknown(cause: Throwable? = null) : AppException("Unknown error", cause)
}
