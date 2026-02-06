package com.atomicanalyst.data.auth

import android.content.Context
import androidx.biometric.BiometricManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

interface BiometricCapability {
    fun isAvailable(): Boolean
}

class AndroidBiometricCapability @Inject constructor(
    @param:ApplicationContext private val context: Context
) : BiometricCapability {
    override fun isAvailable(): Boolean {
        val manager = BiometricManager.from(context)
        val authenticators =
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
                BiometricManager.Authenticators.DEVICE_CREDENTIAL
        return manager.canAuthenticate(authenticators) == BiometricManager.BIOMETRIC_SUCCESS
    }
}
