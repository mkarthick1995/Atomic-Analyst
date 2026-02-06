package com.atomicanalyst.data.backup

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test
import java.nio.file.Files

class BackupFileCodecTest {
    @Test
    fun writeRead_RoundTrip() {
        val tempDir = Files.createTempDirectory("backupcodec").toFile()
        val file = tempDir.resolve("backup.aabk")
        val envelope = BackupEnvelope(
            version = 1,
            createdAtEpochMs = 1234L,
            salt = byteArrayOf(1, 2, 3),
            iv = byteArrayOf(4, 5, 6),
            cipherText = byteArrayOf(7, 8, 9),
            checksum = byteArrayOf(10, 11)
        )

        BackupFileCodec.write(file, envelope)
        val read = BackupFileCodec.read(file)

        assertEquals(envelope.version, read.version)
        assertEquals(envelope.createdAtEpochMs, read.createdAtEpochMs)
        assertArrayEquals(envelope.salt, read.salt)
        assertArrayEquals(envelope.iv, read.iv)
        assertArrayEquals(envelope.cipherText, read.cipherText)
        assertArrayEquals(envelope.checksum, read.checksum)
    }
}
