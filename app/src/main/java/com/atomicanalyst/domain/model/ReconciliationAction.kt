package com.atomicanalyst.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class ReconciliationAction {
    MERGED,
    SPLIT,
    TRANSFER_TAGGED
}
