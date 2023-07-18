package com.example.checkin

import android.app.Activity
import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity


object AuthenticationManager {
    fun generate(): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder().setTitle("Biometric login").setSubtitle("Login using your biometric crednetials").setAllowedAuthenticators(BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL).build()

    }

    fun canAuthenticate(context: Context): Boolean {
        val biometricManager = BiometricManager.from(context)
        return when(biometricManager.canAuthenticate(BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS ->
                 true
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                false
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
            false
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                //can enroll but meh lazy
false
            }

            else -> {false }
        }
    }

    fun biometricPrompt(context: Context, onAuthError: () -> Unit, onAuthSucceeded: () -> Unit, onAuthFailed: () -> Unit): BiometricPrompt {
        return BiometricPrompt(context as FragmentActivity, ContextCompat.getMainExecutor(context), object: BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int,
                                               errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                onAuthError()
            }


            override fun onAuthenticationSucceeded(
                result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)

                onAuthSucceeded()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
               onAuthFailed()
            }
        })
    }
}