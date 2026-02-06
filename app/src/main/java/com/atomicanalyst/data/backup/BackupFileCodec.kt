package com.atomicanalyst.data.backup

import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File

object BackupFileCodec {
    private val MAGIC = byteArrayOf('A'.code.toByte(), 'A'.code.toByte(), 'B'.code.toByte(), 'K'.code.toByte())

    fun write(file: File, envelope: BackupEnvelope) {
        file.parentFile?.mkdirs()
        DataOutputStream(file.outputStream().buffered()).use { out ->
            out.write(MAGIC)
            out.writeInt(envelope.version)
            out.writeLong(envelope.createdAtEpochMs)
            out.writeInt(envelope.salt.size)
            out.writeInt(envelope.iv.size)
            out.writeInt(envelope.checksum.size)
            out.writeInt(envelope.cipherText.size)
            out.write(envelope.salt)
            out.write(envelope.iv)
            out.write(envelope.checksum)
            out.write(envelope.cipherText)
        }
    }

    fun read(file: File): BackupEnvelope {
        DataInputStream(file.inputStream().buffered()).use { input ->
            val magic = ByteArray(MAGIC.size)
            input.readFully(magic)
            require(magic.contentEquals(MAGIC)) { "Invalid backup file" }
            val version = input.readInt()
            val createdAt = input.readLong()
            val saltSize = input.readInt()
            val ivSize = input.readInt()
            val checksumSize = input.readInt()
            val cipherSize = input.readInt()
            val salt = ByteArray(saltSize)
            val iv = ByteArray(ivSize)
            val checksum = ByteArray(checksumSize)
            val cipherText = ByteArray(cipherSize)
            input.readFully(salt)
            input.readFully(iv)
            input.readFully(checksum)
            input.readFully(cipherText)
            return BackupEnvelope(
                version = version,
                createdAtEpochMs = createdAt,
                salt = salt,
                iv = iv,
                cipherText = cipherText,
                checksum = checksum
            )
        }
    }
}
