package com.atomicanalyst.data.backup

import org.junit.Assert.assertArrayEquals
import org.junit.Test

class BackupCryptoTest {
    @Test
    fun encryptDecrypt_RoundTrip() {
        val crypto = BackupCrypto()
        val payload = "sample".encodeToByteArray()
        val passphrase = "Password1".toCharArray()

        val envelope = crypto.encrypt(payload, passphrase)
        val restored = crypto.decrypt(envelope, passphrase)

        assertArrayEquals(payload, restored)
    }
}
