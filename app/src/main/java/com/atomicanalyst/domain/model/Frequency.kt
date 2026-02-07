package com.atomicanalyst.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class Frequency {
    DAILY,
    WEEKLY,
    BIWEEKLY,
    MONTHLY,
    QUARTERLY,
    YEARLY
}
