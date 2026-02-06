package com.atomicanalyst.domain.model

data class AuthSession(
    val userId: String,
    val token: String,
    val expiresAtEpochMs: Long
) {
    fun isExpired(nowEpochMs: Long): Boolean = nowEpochMs >= expiresAtEpochMs
}
