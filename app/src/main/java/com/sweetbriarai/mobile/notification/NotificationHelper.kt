package com.sweetbriarai.mobile.notification

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.sweetbriarai.mobile.MainActivity
import com.sweetbriarai.mobile.R

class NotificationHelper(private val context: Context) {

    fun show(messageId: Int, messageType: String, sender: String, text: String, options: List<String>?) {
        val notificationId = messageId

        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("message_id", messageId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, "advisor_messages")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // TODO: replace with proper icon
            .setContentTitle(sender)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        when (messageType) {
            "yes_no" -> {
                val yesIntent = Intent(context, ResponseReceiver::class.java).apply {
                    action = "RESPONSE"
                    putExtra("message_id", messageId)
                    putExtra("response_value", "yes")
                }
                val yesPending = PendingIntent.getBroadcast(
                    context, messageId * 2, yesIntent, PendingIntent.FLAG_IMMUTABLE
                )

                val noIntent = Intent(context, ResponseReceiver::class.java).apply {
                    action = "RESPONSE"
                    putExtra("message_id", messageId)
                    putExtra("response_value", "no")
                }
                val noPending = PendingIntent.getBroadcast(
                    context, messageId * 2 + 1, noIntent, PendingIntent.FLAG_IMMUTABLE
                )

                builder.addAction(R.drawable.ic_launcher_foreground, "Yes", yesPending)
                    .addAction(R.drawable.ic_launcher_foreground, "No", noPending)
            }
            "choice" -> {
                if (options != null && options.size <= 3) {
                    options.forEachIndexed { index, option ->
                        val choiceIntent = Intent(context, ResponseReceiver::class.java).apply {
                            action = "RESPONSE"
                            putExtra("message_id", messageId)
                            putExtra("response_value", option)
                        }
                        val choicePending = PendingIntent.getBroadcast(
                            context, messageId * 10 + index, choiceIntent, PendingIntent.FLAG_IMMUTABLE
                        )
                        builder.addAction(R.drawable.ic_launcher_foreground, option, choicePending)
                    }
                }
                // If more than 3 options, just tap to open app
            }
            // "info" just shows the notification with tap to open
        }

        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, builder.build())
        }
    }
}