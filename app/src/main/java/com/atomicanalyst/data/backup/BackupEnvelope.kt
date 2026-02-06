package com.atomicanalyst.data.backup

data class BackupEnvelope(
    val version: Int,
    val createdAtEpochMs: Long,
    val salt: ByteArray,
    val iv: ByteArray,
    val cipherText: ByteArray,
    val checksum: ByteArray
)
