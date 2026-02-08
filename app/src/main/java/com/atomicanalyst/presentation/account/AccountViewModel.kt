package com.atomicanalyst.presentation.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atomicanalyst.domain.model.Account
import com.atomicanalyst.domain.model.AccountType
import com.atomicanalyst.domain.model.Result
import com.atomicanalyst.domain.model.StandingInstruction
import com.atomicanalyst.domain.usecase.CreateAccountParams
import com.atomicanalyst.domain.usecase.CreateAccountUseCase
import com.atomicanalyst.domain.usecase.CreateStandingInstructionParams
import com.atomicanalyst.domain.usecase.CreateStandingInstructionUseCase
import com.atomicanalyst.domain.usecase.DeleteAccountUseCase
import com.atomicanalyst.domain.usecase.DeleteStandingInstructionUseCase
import com.atomicanalyst.domain.usecase.LinkAccountLiabilitiesParams
import com.atomicanalyst.domain.usecase.LinkAccountLiabilitiesUseCase
import com.atomicanalyst.domain.usecase.ObserveAccountsUseCase
import com.atomicanalyst.domain.usecase.ObserveAccountLiabilitiesUseCase
import com.atomicanalyst.domain.usecase.ObserveStandingInstructionsUseCase
import com.atomicanalyst.domain.usecase.SetAccountActiveParams
import com.atomicanalyst.domain.usecase.SetAccountActiveUseCase
import com.atomicanalyst.domain.usecase.UpdateAccountUseCase
import com.atomicanalyst.domain.usecase.UpdateStandingInstructionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

data class AccountUiState(
    val accounts: List<Account> = emptyList(),
    val liabilities: List<String> = emptyList(),
    val standingInstructions: List<StandingInstruction> = emptyList(),
    val detailsAccountId: String? = null,
    val statusMessage: String? = null,
    val isBusy: Boolean = false
)

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val observeAccountsUseCase: ObserveAccountsUseCase,
    private val createAccountUseCase: CreateAccountUseCase,
    private val updateAccountUseCase: UpdateAccountUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val setAccountActiveUseCase: SetAccountActiveUseCase,
    private val linkAccountLiabilitiesUseCase: LinkAccountLiabilitiesUseCase,
    private val observeAccountLiabilitiesUseCase: ObserveAccountLiabilitiesUseCase,
    private val createStandingInstructionUseCase: CreateStandingInstructionUseCase,
    private val updateStandingInstructionUseCase: UpdateStandingInstructionUseCase,
    private val deleteStandingInstructionUseCase: DeleteStandingInstructionUseCase,
    private val observeStandingInstructionsUseCase: ObserveStandingInstructionsUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(AccountUiState())
    val uiState: StateFlow<AccountUiState> = _uiState
    private var liabilitiesJob: Job? = null
    private var standingInstructionJob: Job? = null

    init {
        observeAccounts()
    }

    fun createAccount(
        name: String,
        accountNumber: String,
        institution: String,
        openingBalanceCents: Long,
        type: AccountType,
        currency: String
    ) {
        viewModelScope.launch {
            setBusy(true)
            val result = createAccountUseCase(
                CreateAccountParams(
                    name = name,
                    accountNumber = accountNumber,
                    type = type,
                    institution = institution,
                    openingBalanceCents = openingBalanceCents,
                    currency = currency
                )
            )
            when (result) {
                is Result.Success -> _uiState.update {
                    it.copy(statusMessage = "Account created.")
                }
                is Result.Error -> _uiState.update {
                    it.copy(statusMessage = result.exception.message)
                }
                Result.Loading -> Unit
            }
            setBusy(false)
        }
    }

    fun setActive(accountId: String, isActive: Boolean) {
        viewModelScope.launch {
            setBusy(true)
            val result = setAccountActiveUseCase(
                SetAccountActiveParams(accountId, isActive)
            )
            when (result) {
                is Result.Success -> _uiState.update {
                    it.copy(statusMessage = "Account updated.")
                }
                is Result.Error -> _uiState.update {
                    it.copy(statusMessage = result.exception.message)
                }
                Result.Loading -> Unit
            }
            setBusy(false)
        }
    }

    fun updateAccount(
        accountId: String,
        name: String,
        accountNumber: String,
        institution: String,
        balanceCents: Long,
        type: AccountType,
        currency: String,
        isActive: Boolean
    ) {
        viewModelScope.launch {
            val existing = _uiState.value.accounts.firstOrNull { it.id == accountId }
                ?: return@launch _uiState.update {
                    it.copy(statusMessage = "Account not found.")
                }
            setBusy(true)
            val result = updateAccountUseCase(
                existing.copy(
                    name = name,
                    accountNumber = accountNumber,
                    institution = institution,
                    balanceCents = balanceCents,
                    type = type,
                    currency = currency,
                    isActive = isActive
                )
            )
            when (result) {
                is Result.Success -> _uiState.update {
                    it.copy(statusMessage = "Account updated.")
                }
                is Result.Error -> _uiState.update {
                    it.copy(statusMessage = result.exception.message)
                }
                Result.Loading -> Unit
            }
            setBusy(false)
        }
    }

    fun deleteAccount(accountId: String) {
        viewModelScope.launch {
            val existing = _uiState.value.accounts.firstOrNull { it.id == accountId }
                ?: return@launch _uiState.update {
                    it.copy(statusMessage = "Account not found.")
                }
            setBusy(true)
            val result = deleteAccountUseCase(existing)
            when (result) {
                is Result.Success -> _uiState.update {
                    it.copy(statusMessage = "Account deleted.")
                }
                is Result.Error -> _uiState.update {
                    it.copy(statusMessage = result.exception.message)
                }
                Result.Loading -> Unit
            }
            setBusy(false)
        }
    }

    fun linkLiabilities(accountId: String, liabilityIds: List<String>) {
        viewModelScope.launch {
            setBusy(true)
            val result = linkAccountLiabilitiesUseCase(
                LinkAccountLiabilitiesParams(accountId, liabilityIds)
            )
            when (result) {
                is Result.Success -> _uiState.update {
                    it.copy(statusMessage = "Liabilities linked.")
                }
                is Result.Error -> _uiState.update {
                    it.copy(statusMessage = result.exception.message)
                }
                Result.Loading -> Unit
            }
            setBusy(false)
        }
    }

    fun observeDetails(accountId: String) {
        _uiState.update { it.copy(detailsAccountId = accountId) }
        liabilitiesJob?.cancel()
        standingInstructionJob?.cancel()
        liabilitiesJob = viewModelScope.launch {
            when (val result = observeAccountLiabilitiesUseCase(accountId)) {
                is Result.Success -> result.data.collect { list ->
                    _uiState.update { it.copy(liabilities = list) }
                }
                is Result.Error -> _uiState.update {
                    it.copy(statusMessage = result.exception.message)
                }
                Result.Loading -> Unit
            }
        }
        standingInstructionJob = viewModelScope.launch {
            when (val result = observeStandingInstructionsUseCase(accountId)) {
                is Result.Success -> result.data.collect { list ->
                    _uiState.update { it.copy(standingInstructions = list) }
                }
                is Result.Error -> _uiState.update {
                    it.copy(statusMessage = result.exception.message)
                }
                Result.Loading -> Unit
            }
        }
    }

    fun createStandingInstruction(
        accountId: String,
        description: String,
        amountCents: Long,
        frequency: com.atomicanalyst.domain.model.Frequency,
        nextExecutionEpochMs: Long
    ) {
        viewModelScope.launch {
            setBusy(true)
            val result = createStandingInstructionUseCase(
                CreateStandingInstructionParams(
                    accountId = accountId,
                    description = description,
                    amountCents = amountCents,
                    frequency = frequency,
                    nextExecutionEpochMs = nextExecutionEpochMs
                )
            )
            when (result) {
                is Result.Success -> _uiState.update {
                    it.copy(statusMessage = "Standing instruction created.")
                }
                is Result.Error -> _uiState.update {
                    it.copy(statusMessage = result.exception.message)
                }
                Result.Loading -> Unit
            }
            setBusy(false)
        }
    }

    fun toggleInstructionActive(id: String) {
        viewModelScope.launch {
            val instruction = _uiState.value.standingInstructions.firstOrNull { it.id == id }
                ?: return@launch _uiState.update {
                    it.copy(statusMessage = "Instruction not found.")
                }
            setBusy(true)
            val updated = instruction.copy(isActive = !instruction.isActive)
            val result = updateStandingInstructionUseCase(updated)
            when (result) {
                is Result.Success -> _uiState.update {
                    it.copy(statusMessage = "Instruction updated.")
                }
                is Result.Error -> _uiState.update {
                    it.copy(statusMessage = result.exception.message)
                }
                Result.Loading -> Unit
            }
            setBusy(false)
        }
    }

    fun deleteStandingInstruction(id: String) {
        viewModelScope.launch {
            val instruction = _uiState.value.standingInstructions.firstOrNull { it.id == id }
                ?: return@launch _uiState.update {
                    it.copy(statusMessage = "Instruction not found.")
                }
            setBusy(true)
            val result = deleteStandingInstructionUseCase(instruction)
            when (result) {
                is Result.Success -> _uiState.update {
                    it.copy(statusMessage = "Instruction deleted.")
                }
                is Result.Error -> _uiState.update {
                    it.copy(statusMessage = result.exception.message)
                }
                Result.Loading -> Unit
            }
            setBusy(false)
        }
    }

    private fun observeAccounts() {
        viewModelScope.launch {
            when (val result = observeAccountsUseCase(Unit)) {
                is Result.Success -> {
                    result.data.collect { accounts ->
                        _uiState.update { it.copy(accounts = accounts) }
                    }
                }
                is Result.Error -> _uiState.update {
                    it.copy(statusMessage = result.exception.message)
                }
                Result.Loading -> Unit
            }
        }
    }

    private fun setBusy(busy: Boolean) {
        _uiState.update { it.copy(isBusy = busy) }
    }

    fun showMessage(message: String) {
        _uiState.update { it.copy(statusMessage = message) }
    }
}
