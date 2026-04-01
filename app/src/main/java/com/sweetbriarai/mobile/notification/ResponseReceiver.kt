package com.sweetbriarai.mobile.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.sweetbriarai.mobile.data.api.RetrofitClient
import com.sweetbriarai.mobile.data.auth.AuthManager
import com.sweetbriarai.mobile.data.repository.MessageRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ResponseReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val messageId = intent.getIntExtra("message_id", -1)
        val responseValue = intent.getStringExtra("response_value") ?: return

        if (messageId == -1) return

        // Dismiss notification
        NotificationManagerCompat.from(context).cancel(messageId)

        // Respond via API
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val authManager = AuthManager(context)
                val apiService = RetrofitClient.create(authManager)
                val repository = MessageRepository(authManager, apiService)
                repository.respondToMessage(messageId, responseValue)
            } catch (e: Exception) {
                // TODO: Log error and queue for retry
                e.printStackTrace()
            }
        }
    }
}