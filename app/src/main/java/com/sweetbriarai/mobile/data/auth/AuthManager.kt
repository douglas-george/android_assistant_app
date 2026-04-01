package com.sweetbriarai.mobile.data.auth

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class AuthManager(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "auth_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    var apiUrl: String
        get() = sharedPreferences.getString("api_url", "https://mobile.sweetbriarai.com") ?: "https://mobile.sweetbriarai.com"
        set(value) = sharedPreferences.edit().putString("api_url", value).apply()

    var bearerToken: String
        get() = sharedPreferences.getString("bearer_token", "") ?: ""
        set(value) = sharedPreferences.edit().putString("bearer_token", value).apply()

    var deviceId: Int
        get() = sharedPreferences.getInt("device_id", -1)
        set(value) = sharedPreferences.edit().putInt("device_id", value).apply()

    val isConfigured: Boolean
        get() = apiUrl.isNotBlank() && bearerToken.isNotBlank()
}