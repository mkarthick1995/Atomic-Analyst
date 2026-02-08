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
import androidx.compose.material3.Checkbox
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
import com.atomicanalyst.domain.model.Frequency
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
    var editId by rememberSaveable { mutableStateOf("") }
    var editName by rememberSaveable { mutableStateOf("") }
    var editNumber by rememberSaveable { mutableStateOf("") }
    var editInstitution by rememberSaveable { mutableStateOf("") }
    var editBalance by rememberSaveable { mutableStateOf("") }
    var editCurrency by rememberSaveable { mutableStateOf("USD") }
    var editActive by rememberSaveable { mutableStateOf(true) }
    var detailsAccountId by rememberSaveable { mutableStateOf("") }
    var liabilityIds by rememberSaveable { mutableStateOf("") }
    var instructionAccountId by rememberSaveable { mutableStateOf("") }
    var instructionDescription by rememberSaveable { mutableStateOf("") }
    var instructionAmount by rememberSaveable { mutableStateOf("") }
    var instructionFrequency by rememberSaveable { mutableStateOf("MONTHLY") }
    var instructionNextExecution by rememberSaveable { mutableStateOf("") }

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
                text = "Edit account",
                style = MaterialTheme.typography.titleMedium
            )
            OutlinedTextField(
                value = editId,
                onValueChange = { editId = it },
                label = { Text("Account ID") },
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        val account = state.accounts.firstOrNull { it.id == editId }
                        if (account == null) {
                            viewModel.showMessage("Account not found.")
                        } else {
                            editName = account.name
                            editNumber = account.accountNumber
                            editInstitution = account.institution
                            editBalance = account.balanceCents.toString()
                            editCurrency = account.currency
                            editActive = account.isActive
                        }
                    },
                    enabled = editId.isNotBlank() && !state.isBusy
                ) {
                    Text(text = "Load")
                }
                Button(
                    onClick = { viewModel.deleteAccount(editId) },
                    enabled = editId.isNotBlank() && !state.isBusy
                ) {
                    Text(text = "Delete")
                }
            }
            OutlinedTextField(
                value = editName,
                onValueChange = { editName = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = editNumber,
                onValueChange = { editNumber = it },
                label = { Text("Account number") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = editInstitution,
                onValueChange = { editInstitution = it },
                label = { Text("Institution") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = editBalance,
                onValueChange = { editBalance = it },
                label = { Text("Balance (cents)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = editCurrency,
                onValueChange = { editCurrency = it },
                label = { Text("Currency") },
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Checkbox(
                    checked = editActive,
                    onCheckedChange = { editActive = it }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = if (editActive) "Active" else "Inactive")
            }
            Button(
                onClick = {
                    val updatedBalance = editBalance.toLongOrNull() ?: 0L
                    viewModel.updateAccount(
                        accountId = editId,
                        name = editName,
                        accountNumber = editNumber,
                        institution = editInstitution,
                        balanceCents = updatedBalance,
                        type = AccountType.SAVINGS_ACCOUNT,
                        currency = editCurrency,
                        isActive = editActive
                    )
                },
                enabled = editId.isNotBlank() && !state.isBusy
            ) {
                Text(text = "Update")
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Account details",
                style = MaterialTheme.typography.titleMedium
            )
            OutlinedTextField(
                value = detailsAccountId,
                onValueChange = { detailsAccountId = it },
                label = { Text("Details account ID") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = { viewModel.observeDetails(detailsAccountId) },
                enabled = detailsAccountId.isNotBlank() && !state.isBusy
            ) {
                Text(text = "Load details")
            }
            Text(
                text = "Liabilities: ${state.liabilities.joinToString()}",
                style = MaterialTheme.typography.bodyMedium
            )
            OutlinedTextField(
                value = liabilityIds,
                onValueChange = { liabilityIds = it },
                label = { Text("Liability account IDs (comma-separated)") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    val ids = liabilityIds.split(",").map { it.trim() }.filter { it.isNotBlank() }
                    viewModel.linkLiabilities(detailsAccountId, ids)
                },
                enabled = detailsAccountId.isNotBlank() && !state.isBusy
            ) {
                Text(text = "Link liabilities")
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Standing instructions",
                style = MaterialTheme.typography.titleMedium
            )
            OutlinedTextField(
                value = instructionAccountId,
                onValueChange = { instructionAccountId = it },
                label = { Text("Instruction account ID") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = instructionDescription,
                onValueChange = { instructionDescription = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = instructionAmount,
                onValueChange = { instructionAmount = it },
                label = { Text("Amount (cents)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = instructionFrequency,
                onValueChange = { instructionFrequency = it },
                label = { Text("Frequency (DAILY, WEEKLY, MONTHLY, ...)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = instructionNextExecution,
                onValueChange = { instructionNextExecution = it },
                label = { Text("Next execution epoch ms") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    val accountId = if (instructionAccountId.isBlank()) {
                        detailsAccountId
                    } else {
                        instructionAccountId
                    }
                    val amount = instructionAmount.toLongOrNull()
                    val next = instructionNextExecution.toLongOrNull()
                    val freq = runCatching {
                        Frequency.valueOf(instructionFrequency.trim().uppercase())
                    }.getOrNull()
                    when {
                        accountId.isBlank() -> viewModel.showMessage("Account ID required.")
                        amount == null -> viewModel.showMessage("Invalid amount.")
                        next == null -> viewModel.showMessage("Invalid next execution time.")
                        freq == null -> viewModel.showMessage("Invalid frequency.")
                        else -> {
                            viewModel.createStandingInstruction(
                                accountId = accountId,
                                description = instructionDescription,
                                amountCents = amount,
                                frequency = freq,
                                nextExecutionEpochMs = next
                            )
                            instructionDescription = ""
                            instructionAmount = ""
                            instructionNextExecution = ""
                        }
                    }
                },
                enabled = !state.isBusy
            ) {
                Text(text = "Create instruction")
            }
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(state.standingInstructions) { instruction ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = instruction.description, style = MaterialTheme.typography.bodyLarge)
                            Text(
                                text = "${instruction.amountCents} @ ${instruction.frequency}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = if (instruction.isActive) "Active" else "Inactive",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Button(
                                onClick = { viewModel.toggleInstructionActive(instruction.id) },
                                enabled = !state.isBusy
                            ) {
                                Text(text = "Toggle")
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Button(
                                onClick = { viewModel.deleteStandingInstruction(instruction.id) },
                                enabled = !state.isBusy
                            ) {
                                Text(text = "Delete")
                            }
                        }
                    }
                }
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
