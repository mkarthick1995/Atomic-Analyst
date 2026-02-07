package com.atomicanalyst.data.backup.cloud

import android.content.Context
import android.content.Intent
import com.atomicanalyst.domain.error.AppException
import com.atomicanalyst.domain.model.Result
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.api.services.drive.DriveScopes
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class DriveSignInManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val options: GoogleSignInOptions by lazy {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope(DriveScopes.DRIVE_APPDATA))
            .build()
    }

    private val signInClient by lazy { GoogleSignIn.getClient(context, options) }

    fun getSignInIntent(): Intent = signInClient.signInIntent

    fun isSignedIn(): Boolean = GoogleSignIn.getLastSignedInAccount(context) != null

    fun getSignedInEmail(): String? = GoogleSignIn.getLastSignedInAccount(context)?.email

    fun handleSignInResult(data: Intent?): Result<Unit> {
        return try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            task.getResult(ApiException::class.java)
            Result.Success(Unit)
        } catch (_: Exception) {
            Result.Error(AppException.Authentication("Google sign-in failed"))
        }
    }

    suspend fun signOut(): Result<Unit> {
        return suspendCancellableCoroutine { cont ->
            signInClient.signOut().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    cont.resume(Result.Success(Unit))
                } else {
                    cont.resume(Result.Error(AppException.Authentication("Google sign-out failed")))
                }
            }
        }
    }
}
