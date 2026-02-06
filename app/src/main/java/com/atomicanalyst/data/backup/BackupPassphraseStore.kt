package com.atomicanalyst.data.backup

import com.atomicanalyst.data.security.SecureStorage

class BackupPassphraseStore(
    private val storage: SecureStorage
) {
    fun save(passphrase: CharArray) {
        storage.putString(KEY_PASSPHRASE, String(passphrase))
    }

    fun load(): CharArray? = storage.getString(KEY_PASSPHRASE)?.toCharArray()

    fun clear() {
        storage.remove(KEY_PASSPHRASE)
    }

    fun isSet(): Boolean = storage.getString(KEY_PASSPHRASE) != null

    companion object {
        private const val KEY_PASSPHRASE = "backup_passphrase"
    }
}
