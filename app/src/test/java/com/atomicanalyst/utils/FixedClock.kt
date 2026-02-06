package com.atomicanalyst.utils

class FixedClock(
    private var current: Long
) : Clock {
    override fun now(): Long = current

    fun advance(millis: Long) {
        current += millis
    }
}
