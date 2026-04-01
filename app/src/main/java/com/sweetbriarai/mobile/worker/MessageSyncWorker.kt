package com.sweetbriarai.mobile.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.sweetbriarai.mobile.data.api.RetrofitClient
import com.sweetbriarai.mobile.data.auth.AuthManager
import com.sweetbriarai.mobile.data.repository.MessageRepository
import com.sweetbriarai.mobile.notification.NotificationHelper

class MessageSyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val authManager = AuthManager(applicationContext)
            if (!authManager.isConfigured) return Result.success()

            val apiService = RetrofitClient.create(authManager)
            val repository = MessageRepository(authManager, apiService)
            val response = repository.getMessages("pending,delivered")

            val notificationHelper = NotificationHelper(applicationContext)
            response.messages.forEach { message ->
                // TODO: Check if already shown, for now show all
                notificationHelper.show(
                    message.id,
                    message.message_type,
                    message.sender_label,
                    message.message_text,
                    message.response_options
                )
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}