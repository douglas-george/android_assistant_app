package com.sweetbriarai.mobile.fcm

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.sweetbriarai.mobile.data.api.RegisterDeviceRequest
import com.sweetbriarai.mobile.data.api.RetrofitClient
import com.sweetbriarai.mobile.data.auth.AuthManager
import com.sweetbriarai.mobile.data.repository.MessageRepository
import com.sweetbriarai.mobile.notification.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PushService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Re-register if configured
        val authManager = AuthManager(this)
        if (authManager.isConfigured) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val apiService = RetrofitClient.create(authManager)
                    val repository = MessageRepository(authManager, apiService)
                    val request = RegisterDeviceRequest(
                        device_name = android.os.Build.MODEL,
                        fcm_token = token,
                        app_version = "1.0" // TODO: get from BuildConfig
                    )
                    repository.registerDevice(request)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val data = remoteMessage.data
        val messageId = data["message_id"]?.toIntOrNull() ?: return
        val messageType = data["msg_type"] ?: "info"
        val sender = data["sender_label"] ?: "Advisor"
        val text = data["message_text"] ?: ""
        val options = data["response_options"]?.split(",")?.map { it.trim() }

        val notificationHelper = NotificationHelper(this)
        notificationHelper.show(messageId, messageType, sender, text, options)
    }
}