package com.sweetbriarai.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.google.firebase.messaging.FirebaseMessaging
import com.sweetbriarai.mobile.data.api.RegisterDeviceRequest
import com.sweetbriarai.mobile.data.api.RetrofitClient
import com.sweetbriarai.mobile.data.auth.AuthManager
import com.sweetbriarai.mobile.data.repository.MessageRepository
import com.sweetbriarai.mobile.ui.navigation.AppNavigation
import com.sweetbriarai.mobile.ui.screens.SettingsScreen
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val authManager = AuthManager(this)
        val startDestination = if (authManager.isConfigured) "message_list" else "settings"

        // Handle deep link from notification
        val messageId = intent.getIntExtra("message_id", -1)

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    if (startDestination == "settings") {
                        val navController = rememberNavController()
                        SettingsScreen(navController)
                    } else {
                        AppNavigation()
                    }
                }
            }
        }

        // If configured, re-register FCM token
        if (authManager.isConfigured) {
            lifecycleScope.launch {
                try {
                    val fcmToken = FirebaseMessaging.getInstance().token.await()
                    val request = RegisterDeviceRequest(
                        device_name = android.os.Build.MODEL,
                        fcm_token = fcmToken,
                        app_version = "1.0"
                    )
                    val apiService = RetrofitClient.create(authManager)
                    val repository = MessageRepository(authManager, apiService)
                    repository.registerDevice(request)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}