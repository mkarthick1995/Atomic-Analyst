package com.atomicanalyst.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class AccountType {
    SAVINGS_ACCOUNT,
    CURRENT_ACCOUNT,
    CREDIT_CARD,
    DEBIT_CARD,
    WALLET,
    LOAN,
    OTHER
}
