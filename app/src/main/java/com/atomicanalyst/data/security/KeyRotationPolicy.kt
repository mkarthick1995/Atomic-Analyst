package com.atomicanalyst.data.security

data class KeyRotationPolicy(
    val rotationIntervalDays: Long = DEFAULT_ROTATION_DAYS
) {
    fun isRotationDue(lastRotatedEpochMs: Long, nowEpochMs: Long): Boolean {
        if (lastRotatedEpochMs == 0L) return false
        return nowEpochMs >= nextRotationEpochMs(lastRotatedEpochMs)
    }

    fun nextRotationEpochMs(lastRotatedEpochMs: Long): Long {
        return lastRotatedEpochMs + rotationIntervalDays * MILLIS_PER_DAY
    }

    companion object {
        private const val DEFAULT_ROTATION_DAYS = 90L
        private const val MILLIS_PER_DAY = 24L * 60L * 60L * 1000L
    }
}

data class RotationStatus(
    val lastRotatedEpochMs: Long,
    val nextRotationEpochMs: Long,
    val isDue: Boolean
)
