package com.atomicanalyst.presentation.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atomicanalyst.domain.model.Account
import com.atomicanalyst.domain.model.AccountType
import com.atomicanalyst.domain.model.Result
import com.atomicanalyst.domain.usecase.CreateAccountParams
import com.atomicanalyst.domain.usecase.CreateAccountUseCase
import com.atomicanalyst.domain.usecase.ObserveAccountsUseCase
import com.atomicanalyst.domain.usecase.SetAccountActiveParams
import com.atomicanalyst.domain.usecase.SetAccountActiveUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AccountUiState(
    val accounts: List<Account> = emptyList(),
    val statusMessage: String? = null,
    val isBusy: Boolean = false
)

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val observeAccountsUseCase: ObserveAccountsUseCase,
    private val createAccountUseCase: CreateAccountUseCase,
    private val setAccountActiveUseCase: SetAccountActiveUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(AccountUiState())
    val uiState: StateFlow<AccountUiState> = _uiState

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
}
