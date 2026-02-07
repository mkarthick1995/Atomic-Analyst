package com.atomicanalyst.data.backup.cloud

import com.atomicanalyst.data.BaseRepository
import com.atomicanalyst.domain.error.AppException
import com.atomicanalyst.domain.model.Result
import com.atomicanalyst.utils.DispatcherProvider
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.FileContent
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File as DriveFile
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import kotlinx.coroutines.withContext

class GoogleDriveBackupClient @Inject constructor(
    private val accountProvider: GoogleAccountProvider,
    private val driveServiceFactory: DriveServiceFactory,
    private val dispatcherProvider: DispatcherProvider
) : BaseRepository(), CloudBackupClient {
    override suspend fun uploadBackup(file: File): Result<CloudBackupEntry> = safeCall {
        withContext(dispatcherProvider.io) {
            val drive = driveServiceFactory.create(requireAccount())
            val metadata = DriveFile().apply {
                name = file.name
                parents = listOf(APP_DATA_FOLDER)
            }
            val content = FileContent(MIME_TYPE, file)
            val created = drive.files()
                .create(metadata, content)
                .setFields("id,name,modifiedTime")
                .execute()
            created.toEntry()
        }
    }

    override suspend fun listBackups(): Result<List<CloudBackupEntry>> = safeCall {
        withContext(dispatcherProvider.io) {
            val drive = driveServiceFactory.create(requireAccount())
            val result = drive.files().list()
                .setSpaces(APP_DATA_FOLDER)
                .setQ("trashed = false")
                .setFields("files(id,name,modifiedTime)")
                .execute()
            result.files?.map { it.toEntry() } ?: emptyList()
        }
    }

    override suspend fun downloadBackup(
        fileId: String,
        destination: File
    ): Result<File> = safeCall {
        withContext(dispatcherProvider.io) {
            val drive = driveServiceFactory.create(requireAccount())
            FileOutputStream(destination).use { output ->
                drive.files().get(fileId).executeMediaAndDownloadTo(output)
            }
            destination
        }
    }

    private fun requireAccount(): GoogleSignInAccount {
        return accountProvider.getAccount()
            ?: throw AppException.Authentication("Google account not signed in")
    }

    private fun DriveFile.toEntry(): CloudBackupEntry = CloudBackupEntry(
        id = id ?: throw AppException.Validation("Missing Drive file id"),
        name = name ?: DEFAULT_BACKUP_NAME,
        modifiedTimeEpochMs = modifiedTime?.value ?: 0L
    )

    companion object {
        private const val APP_DATA_FOLDER = "appDataFolder"
        private const val MIME_TYPE = "application/octet-stream"
        private const val DEFAULT_BACKUP_NAME = "backup.aabk"
    }
}

interface GoogleAccountProvider {
    fun getAccount(): GoogleSignInAccount?
}

interface DriveServiceFactory {
    fun create(account: GoogleSignInAccount): Drive
}

class DefaultGoogleAccountProvider @Inject constructor(
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: android.content.Context
) : GoogleAccountProvider {
    override fun getAccount(): GoogleSignInAccount? =
        com.google.android.gms.auth.api.signin.GoogleSignIn.getLastSignedInAccount(context)
}

class DefaultDriveServiceFactory @Inject constructor(
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: android.content.Context
) : DriveServiceFactory {
    override fun create(account: GoogleSignInAccount): Drive {
        val credential = GoogleAccountCredential.usingOAuth2(
            context,
            listOf(DriveScopes.DRIVE_APPDATA)
        )
        credential.selectedAccount = account.account
        return Drive.Builder(
            NetHttpTransport(),
            GsonFactory.getDefaultInstance(),
            credential
        )
            .setApplicationName(APP_NAME)
            .build()
    }

    companion object {
        private const val APP_NAME = "Atomic Analyst"
    }
}
