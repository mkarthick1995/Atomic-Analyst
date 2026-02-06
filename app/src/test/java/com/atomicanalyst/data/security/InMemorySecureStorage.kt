package com.atomicanalyst.data.security

class InMemorySecureStorage : SecureStorage {
    private val stringMap = mutableMapOf<String, String>()
    private val booleanMap = mutableMapOf<String, Boolean>()
    private val longMap = mutableMapOf<String, Long>()

    override fun putString(key: String, value: String) {
        stringMap[key] = value
    }

    override fun getString(key: String): String? = stringMap[key]

    override fun putBoolean(key: String, value: Boolean) {
        booleanMap[key] = value
    }

    override fun getBoolean(key: String, default: Boolean): Boolean = booleanMap[key] ?: default

    override fun putLong(key: String, value: Long) {
        longMap[key] = value
    }

    override fun getLong(key: String, default: Long): Long = longMap[key] ?: default

    override fun remove(key: String) {
        stringMap.remove(key)
        booleanMap.remove(key)
        longMap.remove(key)
    }

    override fun clear() {
        stringMap.clear()
        booleanMap.clear()
        longMap.clear()
    }
}
