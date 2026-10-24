package com.example.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

class StudyHubNotificationManager(private val context: Context) {

    companion object {
        const val CHANNEL_STUDY = "studyhub_study"
        const val CHANNEL_ADMISSION = "studyhub_admission"
        const val CHANNEL_DAILY_GOAL = "studyhub_daily_goal"
    }

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createChannels()
    }

    private fun createChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channels = listOf(
                NotificationChannel(
                    CHANNEL_STUDY,
                    "Study Reminders",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Reminders for scheduled study sessions and focus intervals"
                    enableVibration(true)
                },
                NotificationChannel(
                    CHANNEL_ADMISSION,
                    "Admission Deadlines",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Milestone alerts for application deadlines, admit cards and exams"
                    enableVibration(true)
                },
                NotificationChannel(
                    CHANNEL_DAILY_GOAL,
                    "Daily Study Goals",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Updates on daily study progress and target milestones"
                }
            )
            channels.forEach { notificationManager.createNotificationChannel(it) }
        }
    }

    fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun postNotification(
        id: Int,
        channelId: String,
        title: String,
        message: String
    ): Boolean {
        if (!hasNotificationPermission()) {
            return false
        }

        try {
            val builder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)

            notificationManager.notify(id, builder.build())
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
}
