package com.example.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.local.AppDatabase
import com.example.data.local.NotificationItemEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StudyHubNotificationReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_TRIGGER_NOTIFICATION = "com.example.studyhub.TRIGGER_NOTIFICATION"
        const val EXTRA_NOTIFICATION_ID = "extra_notification_id"
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_BODY = "extra_body"
        const val EXTRA_ACTION_ROUTE = "extra_action_route"
        const val EXTRA_CATEGORY = "extra_category"
        const val EXTRA_IS_TEST = "extra_is_test"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_TRIGGER_NOTIFICATION) return

        val title = intent.getStringExtra(EXTRA_TITLE) ?: "StudyHub Reminder"
        val body = intent.getStringExtra(EXTRA_BODY) ?: "Time to check your study goals!"
        val actionRoute = intent.getStringExtra(EXTRA_ACTION_ROUTE) ?: "FOCUS"
        val category = intent.getStringExtra(EXTRA_CATEGORY) ?: "STUDY"
        val isTest = intent.getBooleanExtra(EXTRA_IS_TEST, false)
        val notifId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, 1001)

        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getDatabase(context)

                // 1. Save to Room database history
                val notificationItem = NotificationItemEntity(
                    title = title,
                    body = body,
                    category = category,
                    isRead = false,
                    createdAt = System.currentTimeMillis(),
                    actionRoute = actionRoute
                )
                db.notificationDao().insertNotification(notificationItem)

                // 2. Update settings: lastNotificationTime for cooldown tracking
                val currentSettings = db.settingsDao().getSettingsSync() ?: com.example.data.local.AppSettingsEntity()
                db.settingsDao().upsertSettings(
                    currentSettings.copy(
                        lastNotificationTime = System.currentTimeMillis(),
                        testSessionState = if (isTest) "DELIVERED" else currentSettings.testSessionState
                    )
                )

                // 3. Post system notification
                val notificationManager = StudyHubNotificationManager(context)
                val channelId = when (category) {
                    "ADMISSION" -> StudyHubNotificationManager.CHANNEL_ADMISSION
                    "DAILY_GOAL" -> StudyHubNotificationManager.CHANNEL_DAILY_GOAL
                    else -> StudyHubNotificationManager.CHANNEL_STUDY
                }
                notificationManager.postNotification(
                    id = notifId,
                    channelId = channelId,
                    title = title,
                    message = body,
                    actionRoute = actionRoute
                )

                // 4. Emit foreground banner
                InAppBannerManager.showBanner(
                    InAppBannerData(
                        title = title,
                        body = body,
                        actionRoute = actionRoute
                    )
                )

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                pendingResult.finish()
            }
        }
    }
}
