package com.atomicanalyst.data.network

data class NetworkSecurityConfig(
    val baseUrl: String,
    val pins: List<Pin> = emptyList()
)

data class Pin(
    val host: String,
    val sha256: String
)
