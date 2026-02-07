package com.atomicanalyst.data.backup.cloud

import com.atomicanalyst.domain.model.Result
import java.io.File

data class CloudBackupEntry(
    val id: String,
    val name: String,
    val modifiedTimeEpochMs: Long
)

interface CloudBackupClient {
    suspend fun uploadBackup(file: File): Result<CloudBackupEntry>
    suspend fun listBackups(): Result<List<CloudBackupEntry>>
    suspend fun downloadBackup(fileId: String, destination: File): Result<File>
}
