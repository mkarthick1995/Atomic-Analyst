package com.atomicanalyst.presentation.backup

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atomicanalyst.data.backup.BackupManager
import com.atomicanalyst.data.backup.BackupPassphraseStore
import com.atomicanalyst.data.backup.cloud.CloudBackupClient
import com.atomicanalyst.data.backup.cloud.DriveSignInManager
import com.atomicanalyst.domain.model.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BackupUiState(
    val isSignedIn: Boolean = false,
    val signedInEmail: String? = null,
    val passphraseSet: Boolean = false,
    val lastLocalBackupEpochMs: Long? = null,
    val isBusy: Boolean = false,
    val statusMessage: String? = null
)

@HiltViewModel
class BackupViewModel @Inject constructor(
    private val backupManager: BackupManager,
    private val cloudClient: CloudBackupClient,
    private val passphraseStore: BackupPassphraseStore,
    private val driveSignInManager: DriveSignInManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(BackupUiState())
    val uiState: StateFlow<BackupUiState> = _uiState

    init {
        refresh()
    }

    fun refresh() {
        val latest = latestLocalBackup()
        _uiState.update {
            it.copy(
                isSignedIn = driveSignInManager.isSignedIn(),
                signedInEmail = driveSignInManager.getSignedInEmail(),
                passphraseSet = passphraseStore.isSet(),
                lastLocalBackupEpochMs = latest?.epochMs,
                statusMessage = it.statusMessage
            )
        }
    }

    fun signInIntent(): Intent = driveSignInManager.getSignInIntent()

    fun handleSignInResult(data: Intent?) {
        when (val result = driveSignInManager.handleSignInResult(data)) {
            is Result.Success -> {
                _uiState.update { it.copy(statusMessage = "Google Drive connected.") }
            }
            is Result.Error -> {
                _uiState.update { it.copy(statusMessage = result.exception.message) }
            }
            Result.Loading -> Unit
        }
        refresh()
    }

    fun signOut() {
        viewModelScope.launch {
            setBusy(true)
            when (val result = driveSignInManager.signOut()) {
                is Result.Success -> _uiState.update {
                    it.copy(statusMessage = "Signed out from Google Drive.")
                }
                is Result.Error -> _uiState.update {
                    it.copy(statusMessage = result.exception.message)
                }
                Result.Loading -> Unit
            }
            setBusy(false)
            refresh()
        }
    }

    fun savePassphrase(passphrase: String) {
        if (passphrase.isBlank()) {
            _uiState.update { it.copy(statusMessage = "Passphrase cannot be empty.") }
            return
        }
        passphraseStore.save(passphrase.toCharArray())
        _uiState.update { it.copy(statusMessage = "Backup passphrase saved.", passphraseSet = true) }
    }

    fun clearPassphrase() {
        passphraseStore.clear()
        _uiState.update { it.copy(statusMessage = "Backup passphrase cleared.", passphraseSet = false) }
    }

    fun createLocalBackup() {
        viewModelScope.launch {
            setBusy(true)
            val result = backupManager.createBackup()
            handleFileResult(result, "Local backup created.")
            setBusy(false)
            refresh()
        }
    }

    fun restoreLatestLocal() {
        viewModelScope.launch {
            val latest = latestLocalBackup()
            if (latest == null) {
                _uiState.update { it.copy(statusMessage = "No local backups found.") }
                return@launch
            }
            setBusy(true)
            val result = backupManager.restoreBackup(latest.file)
            handleUnitResult(result, "Local backup restored.")
            setBusy(false)
        }
    }

    fun uploadToDrive() {
        viewModelScope.launch {
            setBusy(true)
            val result = backupManager.uploadToCloud(cloudClient)
            when (result) {
                is Result.Success -> _uiState.update {
                    it.copy(statusMessage = "Uploaded to Drive: ${result.data.name}")
                }
                is Result.Error -> _uiState.update {
                    it.copy(statusMessage = result.exception.message)
                }
                Result.Loading -> Unit
            }
            setBusy(false)
        }
    }

    fun restoreLatestFromDrive() {
        viewModelScope.launch {
            setBusy(true)
            val result = backupManager.restoreLatestFromCloud(cloudClient)
            handleUnitResult(result, "Restored latest backup from Drive.")
            setBusy(false)
        }
    }

    private fun handleFileResult(result: Result<File>, successMessage: String) {
        when (result) {
            is Result.Success -> _uiState.update {
                it.copy(statusMessage = successMessage)
            }
            is Result.Error -> _uiState.update {
                it.copy(statusMessage = result.exception.message)
            }
            Result.Loading -> Unit
        }
    }

    private fun handleUnitResult(result: Result<Unit>, successMessage: String) {
        when (result) {
            is Result.Success -> _uiState.update {
                it.copy(statusMessage = successMessage)
            }
            is Result.Error -> _uiState.update {
                it.copy(statusMessage = result.exception.message)
            }
            Result.Loading -> Unit
        }
    }

    private fun setBusy(busy: Boolean) {
        _uiState.update { it.copy(isBusy = busy) }
    }

    private data class LatestBackup(
        val file: File,
        val epochMs: Long
    )

    private fun latestLocalBackup(): LatestBackup? {
        val latest = backupManager.listBackups().firstOrNull() ?: return null
        val epoch = latest.nameWithoutExtension.removePrefix("backup_").toLongOrNull()
        val resolved = epoch ?: latest.lastModified()
        return LatestBackup(latest, resolved)
    }
}
