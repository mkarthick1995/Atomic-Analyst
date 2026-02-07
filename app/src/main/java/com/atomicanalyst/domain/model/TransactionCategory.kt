package com.atomicanalyst.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class TransactionCategory {
    FOOD,
    TRANSPORT,
    UTILITIES,
    ENTERTAINMENT,
    SHOPPING,
    HEALTHCARE,
    EDUCATION,
    INVESTMENTS,
    LOANS,
    EMI,
    INCOME,
    TRANSFER,
    OTHER
}
