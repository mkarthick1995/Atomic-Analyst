package com.atomicanalyst.presentation.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.atomicanalyst.R
import com.atomicanalyst.presentation.backup.BackupViewModel
import java.text.DateFormat
import java.util.Date

@Suppress("FunctionNaming")
@Composable
fun HomeScreen(
    viewModel: BackupViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        viewModel.handleSignInResult(result.data)
    }
    var passphrase by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.backup_title),
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = stringResource(R.string.backup_status_sign_in, state.signedInEmail ?: "Not connected"),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = stringResource(
                    R.string.backup_status_passphrase,
                    if (state.passphraseSet) "Set" else "Not set"
                ),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = stringResource(
                    R.string.backup_status_last_local,
                    state.lastLocalBackupEpochMs?.let { formatTimestamp(it) } ?: "None"
                ),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = passphrase,
                onValueChange = { passphrase = it },
                label = { Text(stringResource(R.string.backup_passphrase_label)) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    viewModel.savePassphrase(passphrase)
                    passphrase = ""
                },
                enabled = passphrase.isNotBlank() && !state.isBusy
            ) {
                Text(text = stringResource(R.string.backup_passphrase_save))
            }
            Button(
                onClick = { viewModel.clearPassphrase() },
                enabled = state.passphraseSet && !state.isBusy
            ) {
                Text(text = stringResource(R.string.backup_passphrase_clear))
            }
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { launcher.launch(viewModel.signInIntent()) },
                enabled = !state.isSignedIn && !state.isBusy
            ) {
                Text(text = stringResource(R.string.backup_drive_sign_in))
            }
            Button(
                onClick = { viewModel.signOut() },
                enabled = state.isSignedIn && !state.isBusy
            ) {
                Text(text = stringResource(R.string.backup_drive_sign_out))
            }
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { viewModel.createLocalBackup() },
                enabled = !state.isBusy
            ) {
                Text(text = stringResource(R.string.backup_local_create))
            }
            Button(
                onClick = { viewModel.restoreLatestLocal() },
                enabled = !state.isBusy
            ) {
                Text(text = stringResource(R.string.backup_local_restore))
            }
            Button(
                onClick = { viewModel.uploadToDrive() },
                enabled = state.isSignedIn && !state.isBusy
            ) {
                Text(text = stringResource(R.string.backup_drive_upload))
            }
            Button(
                onClick = { viewModel.restoreLatestFromDrive() },
                enabled = state.isSignedIn && !state.isBusy
            ) {
                Text(text = stringResource(R.string.backup_drive_restore))
            }

            state.statusMessage?.let { message ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

private fun formatTimestamp(epochMs: Long): String {
    val formatter = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
    return formatter.format(Date(epochMs))
}
