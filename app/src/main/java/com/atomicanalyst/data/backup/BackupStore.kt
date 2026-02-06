package com.atomicanalyst.data.backup

import java.io.File

class BackupStore(
    private val backupDir: File
) {
    fun ensureDir(): File {
        if (!backupDir.exists()) {
            backupDir.mkdirs()
        }
        return backupDir
    }

    fun createBackupFile(createdAtEpochMs: Long): File {
        ensureDir()
        return File(backupDir, "backup_${createdAtEpochMs}.aabk")
    }

    fun listBackups(): List<File> {
        return backupDir.listFiles { file -> file.extension == "aabk" }
            ?.sortedByDescending { it.lastModified() }
            ?: emptyList()
    }

    fun deleteBackupsOlderThan(cutoffEpochMs: Long) {
        listBackups().forEach { file ->
            val epoch = file.nameWithoutExtension.removePrefix("backup_").toLongOrNull()
            val timestamp = epoch ?: file.lastModified()
            if (timestamp < cutoffEpochMs) {
                file.delete()
            }
        }
    }
}
