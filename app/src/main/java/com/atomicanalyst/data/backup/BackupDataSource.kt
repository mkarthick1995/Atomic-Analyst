package com.atomicanalyst.data.backup

interface BackupDataSource {
    suspend fun exportData(): ByteArray
    suspend fun importData(data: ByteArray)
}

class PlaceholderBackupDataSource : BackupDataSource {
    override suspend fun exportData(): ByteArray = ByteArray(0)

    override suspend fun importData(data: ByteArray) {
        // No-op until real data sources are wired (Room database, secure prefs, etc.)
    }
}
