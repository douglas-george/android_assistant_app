package com.sweetbriarai.mobile

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.sweetbriarai.mobile.worker.MessageSyncWorker
import java.util.concurrent.TimeUnit

class SweetbriarApp : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        scheduleMessageSync()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "advisor_messages",
            "Advisor Messages",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifications from AI advisors"
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun scheduleMessageSync() {
        val syncRequest = PeriodicWorkRequestBuilder<MessageSyncWorker>(
            15, TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "message_sync",
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }
}
