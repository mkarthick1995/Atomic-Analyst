package com.atomicanalyst.data.backup

import com.atomicanalyst.data.auth.AuthLocalStore
import com.atomicanalyst.domain.error.AppException
import com.atomicanalyst.utils.Clock
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class BackupPayload(
    val version: Int,
    val createdAtEpochMs: Long,
    val auth: AuthBackup?
)

@Serializable
data class AuthBackup(
    val userId: String,
    val passwordHash: String,
    val biometricEnabled: Boolean
)

class AppBackupDataSource(
    private val authLocalStore: AuthLocalStore,
    private val clock: Clock
) : BackupDataSource {
    override suspend fun exportData(): ByteArray {
        val credentials = authLocalStore.loadCredentials()
        val auth = credentials?.let {
            AuthBackup(
                userId = it.userId,
                passwordHash = it.passwordHash,
                biometricEnabled = authLocalStore.isBiometricEnabled()
            )
        }
        val payload = BackupPayload(
            version = CURRENT_VERSION,
            createdAtEpochMs = clock.now(),
            auth = auth
        )
        return Json.encodeToString(payload).encodeToByteArray()
    }

    override suspend fun importData(data: ByteArray) {
        val payload = try {
            Json.decodeFromString<BackupPayload>(data.decodeToString())
        } catch (exception: SerializationException) {
            throw AppException.Validation("Invalid backup payload", exception)
        } catch (exception: IllegalArgumentException) {
            throw AppException.Validation("Invalid backup payload", exception)
        }
        if (payload.version != CURRENT_VERSION) {
            throw AppException.Validation("Unsupported backup version")
        }
        val auth = payload.auth ?: return
        authLocalStore.saveCredentials(auth.userId, auth.passwordHash)
        authLocalStore.setBiometricEnabled(auth.biometricEnabled)
    }

    companion object {
        private const val CURRENT_VERSION = 1
    }
}
