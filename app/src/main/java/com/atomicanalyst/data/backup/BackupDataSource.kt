package com.atomicanalyst.data.backup

interface BackupDataSource {
    suspend fun exportData(): ByteArray
    suspend fun importData(data: ByteArray)
}
