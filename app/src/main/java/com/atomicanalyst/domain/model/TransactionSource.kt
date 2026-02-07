package com.atomicanalyst.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class TransactionSource {
    GPAY,
    PHONEPE,
    UPI,
    CREDIT_CARD,
    DEBIT_CARD,
    WALLET,
    CASH,
    MANUAL,
    IMPORT
}
