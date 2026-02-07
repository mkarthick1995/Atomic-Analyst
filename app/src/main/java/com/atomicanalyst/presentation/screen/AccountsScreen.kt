package com.atomicanalyst.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.atomicanalyst.domain.model.AccountType
import com.atomicanalyst.presentation.account.AccountViewModel

@Suppress("FunctionNaming")
@Composable
fun AccountsScreen(
    viewModel: AccountViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var name by rememberSaveable { mutableStateOf("") }
    var number by rememberSaveable { mutableStateOf("") }
    var institution by rememberSaveable { mutableStateOf("") }
    var balance by rememberSaveable { mutableStateOf("") }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Accounts",
                style = MaterialTheme.typography.headlineSmall
            )
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Account name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = number,
                onValueChange = { number = it },
                label = { Text("Account number") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = institution,
                onValueChange = { institution = it },
                label = { Text("Institution") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = balance,
                onValueChange = { balance = it },
                label = { Text("Opening balance (cents)") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    val opening = balance.toLongOrNull() ?: 0L
                    viewModel.createAccount(
                        name = name,
                        accountNumber = number,
                        institution = institution,
                        openingBalanceCents = opening,
                        type = AccountType.SAVINGS_ACCOUNT,
                        currency = "USD"
                    )
                    name = ""
                    number = ""
                    institution = ""
                    balance = ""
                },
                enabled = !state.isBusy
            ) {
                Text(text = "Add Account")
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Existing accounts",
                style = MaterialTheme.typography.titleMedium
            )
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(state.accounts) { account ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = account.name, style = MaterialTheme.typography.bodyLarge)
                            Text(
                                text = "${account.currency} ${account.balanceCents}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = if (account.isActive) "Active" else "Inactive",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(
                            onClick = { viewModel.setActive(account.id, !account.isActive) },
                            enabled = !state.isBusy
                        ) {
                            Text(text = if (account.isActive) "Deactivate" else "Activate")
                        }
                    }
                }
            }

            state.statusMessage?.let { message ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = message, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
