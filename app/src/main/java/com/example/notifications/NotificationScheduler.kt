package com.example.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.data.local.AppSettingsEntity

object NotificationScheduler {

    const val TEST_NOTIFICATION_ID = 9999
    const val DEFAULT_TEST_DELAY_SECONDS = 180L // 3 minutes

    fun schedule3MinuteTestNotification(
        context: Context,
        settings: AppSettingsEntity?,
        delaySeconds: Long = DEFAULT_TEST_DELAY_SECONDS,
        forceNewSession: Boolean = false
    ): String {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Prevent duplicate test scheduling if session is already PENDING and valid
        if (!forceNewSession && settings != null && settings.testSessionState == "PENDING") {
            val now = System.currentTimeMillis()
            if (now < settings.testSessionScheduledTime + 300_000L) {
                return settings.lastTestSessionId ?: "existing_pending_session"
            }
        }

        val sessionId = "test_session_${System.currentTimeMillis()}"
        val triggerTimeMs = System.currentTimeMillis() + (delaySeconds * 1000L)

        val intent = Intent(context, StudyHubNotificationReceiver::class.java).apply {
            action = StudyHubNotificationReceiver.ACTION_TRIGGER_NOTIFICATION
            putExtra(StudyHubNotificationReceiver.EXTRA_NOTIFICATION_ID, TEST_NOTIFICATION_ID)
            putExtra(StudyHubNotificationReceiver.EXTRA_TITLE, "StudyHub 3-Min Test: Motivation Check! 🚀")
            putExtra(StudyHubNotificationReceiver.EXTRA_BODY, "3 minutes of study time passed! Time to tackle a chapter or start a focus session.")
            putExtra(StudyHubNotificationReceiver.EXTRA_ACTION_ROUTE, "FOCUS")
            putExtra(StudyHubNotificationReceiver.EXTRA_CATEGORY, "STUDY")
            putExtra(StudyHubNotificationReceiver.EXTRA_IS_TEST, true)
        }

        val pendingFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            TEST_NOTIFICATION_ID,
            intent,
            pendingFlags
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTimeMs,
                    pendingIntent
                )
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    triggerTimeMs,
                    pendingIntent
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return sessionId
    }

    fun sendTestNotificationNow(context: Context) {
        val intent = Intent(context, StudyHubNotificationReceiver::class.java).apply {
            action = StudyHubNotificationReceiver.ACTION_TRIGGER_NOTIFICATION
            putExtra(StudyHubNotificationReceiver.EXTRA_NOTIFICATION_ID, TEST_NOTIFICATION_ID)
            putExtra(StudyHubNotificationReceiver.EXTRA_TITLE, "StudyHub Instant Test! ⚡")
            putExtra(StudyHubNotificationReceiver.EXTRA_BODY, "Bhai, syllabus ki tor ex? 💀 Eto din dhore avoid kortesis! Ajke ekta chapter settle kor.")
            putExtra(StudyHubNotificationReceiver.EXTRA_ACTION_ROUTE, "SYLLABUS")
            putExtra(StudyHubNotificationReceiver.EXTRA_CATEGORY, "STUDY")
            putExtra(StudyHubNotificationReceiver.EXTRA_IS_TEST, true)
        }
        context.sendBroadcast(intent)
    }

    fun scheduleAdaptiveNotification(
        context: Context,
        template: NotificationTemplate,
        delayMinutes: Long = 180L
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val triggerTimeMs = System.currentTimeMillis() + (delayMinutes * 60_000L)
        val notifId = template.id.hashCode()

        val intent = Intent(context, StudyHubNotificationReceiver::class.java).apply {
            action = StudyHubNotificationReceiver.ACTION_TRIGGER_NOTIFICATION
            putExtra(StudyHubNotificationReceiver.EXTRA_NOTIFICATION_ID, notifId)
            putExtra(StudyHubNotificationReceiver.EXTRA_TITLE, template.title)
            putExtra(StudyHubNotificationReceiver.EXTRA_BODY, template.body)
            putExtra(StudyHubNotificationReceiver.EXTRA_ACTION_ROUTE, template.actionRoute.name)
            putExtra(StudyHubNotificationReceiver.EXTRA_CATEGORY, template.category.name)
            putExtra(StudyHubNotificationReceiver.EXTRA_IS_TEST, false)
        }

        val pendingFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notifId,
            intent,
            pendingFlags
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTimeMs,
                    pendingIntent
                )
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    triggerTimeMs,
                    pendingIntent
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
