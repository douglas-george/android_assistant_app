package com.sweetbriarai.mobile.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import com.sweetbriarai.mobile.data.api.RegisterDeviceRequest
import com.sweetbriarai.mobile.data.api.RetrofitClient
import com.sweetbriarai.mobile.data.auth.AuthManager
import com.sweetbriarai.mobile.data.repository.MessageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    val authManager = AuthManager(application)

    private val _isRegistering = MutableStateFlow(false)
    val isRegistering: StateFlow<Boolean> = _isRegistering

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage

    private val _registrationComplete = MutableStateFlow(false)
    val registrationComplete: StateFlow<Boolean> = _registrationComplete

    fun testConnection(apiUrl: String, bearerToken: String) {
        viewModelScope.launch {
            try {
                _statusMessage.value = "Testing connection..."
                authManager.apiUrl = apiUrl
                authManager.bearerToken = bearerToken
                val apiService = RetrofitClient.create(authManager)
                val repository = MessageRepository(authManager, apiService)
                repository.health()
                _statusMessage.value = "Connection successful!"
            } catch (e: Exception) {
                _statusMessage.value = "Connection failed: ${e.message}"
            }
        }
    }

    fun registerDevice(apiUrl: String, bearerToken: String) {
        viewModelScope.launch {
            _isRegistering.value = true
            _statusMessage.value = null
            try {
                authManager.apiUrl = apiUrl
                authManager.bearerToken = bearerToken
                val fcmToken = FirebaseMessaging.getInstance().token.await()
                val request = RegisterDeviceRequest(
                    device_name = android.os.Build.MODEL,
                    fcm_token = fcmToken,
                    app_version = "1.0"
                )
                val apiService = RetrofitClient.create(authManager)
                val repository = MessageRepository(authManager, apiService)
                val response = repository.registerDevice(request)
                authManager.deviceId = response.device_id
                _statusMessage.value = "Device registered successfully!"
                _registrationComplete.value = true
            } catch (e: Exception) {
                _statusMessage.value = "Registration failed: ${e.message}"
            } finally {
                _isRegistering.value = false
            }
        }
    }
}
