package com.atomicanalyst.data.security

interface SecureStorage {
    fun putString(key: String, value: String)
    fun getString(key: String): String?
    fun putBoolean(key: String, value: Boolean)
    fun getBoolean(key: String, default: Boolean = false): Boolean
    fun putLong(key: String, value: Long)
    fun getLong(key: String, default: Long = 0L): Long
    fun remove(key: String)
    fun clear()
}
