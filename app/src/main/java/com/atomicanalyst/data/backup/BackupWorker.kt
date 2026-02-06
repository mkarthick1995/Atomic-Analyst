package com.atomicanalyst.data.backup

import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.atomicanalyst.domain.error.AppException
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class BackupWorker @AssistedInject constructor(
    @Assisted appContext: android.content.Context,
    @Assisted workerParams: WorkerParameters,
    private val backupManager: BackupManager
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        return when (val result = backupManager.createBackup()) {
            is com.atomicanalyst.domain.model.Result.Success -> Result.success()
            is com.atomicanalyst.domain.model.Result.Error -> {
                if (result.exception is AppException.Security &&
                    result.exception.message == "Backup passphrase not set"
                ) {
                    Result.failure()
                } else {
                    Result.retry()
                }
            }
            com.atomicanalyst.domain.model.Result.Loading -> Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "atomic_analyst_backup"
    }
}
