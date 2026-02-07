package com.atomicanalyst.data.network

object PinConfigParser {
    fun parse(raw: String): List<Pin> {
        if (raw.isBlank()) return emptyList()
        return raw.split(ENTRY_SEPARATOR)
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .map { entry ->
                val parts = entry.split(KEY_VALUE_SEPARATOR, limit = LIMIT)
                    .map { it.trim() }
                require(parts.size == LIMIT) { "Invalid pin entry: $entry" }
                val host = parts[INDEX_HOST]
                val pin = parts[INDEX_PIN]
                require(host.isNotBlank()) { "Pin host cannot be blank" }
                require(pin.startsWith(SHA256_PREFIX)) { "Pin must start with sha256/" }
                Pin(host, pin)
            }
    }

    private const val ENTRY_SEPARATOR = ","
    private const val KEY_VALUE_SEPARATOR = "="
    private const val SHA256_PREFIX = "sha256/"
    private const val LIMIT = 2
    private const val INDEX_HOST = 0
    private const val INDEX_PIN = 1
}
