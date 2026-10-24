package com.example.notifications

import com.example.data.local.AdmissionTargetEntity
import com.example.data.local.AppSettingsEntity
import com.example.data.local.ChapterEntity
import com.example.data.local.NotificationItemEntity
import com.example.data.local.StudyNoteEntity
import com.example.data.local.StudyTaskEntity
import com.example.data.local.SubjectEntity
import com.example.data.local.UserProfile
import java.util.Calendar

object NotificationEligibilityEvaluator {

    fun isEligible(
        template: NotificationTemplate,
        settings: AppSettingsEntity,
        userProfile: UserProfile?,
        subjects: List<SubjectEntity>,
        chapters: List<ChapterEntity>,
        tasks: List<StudyTaskEntity>,
        admissionTargets: List<AdmissionTargetEntity>,
        notes: List<StudyNoteEntity>,
        isFocusTimerActive: Boolean,
        recentNotifications: List<NotificationItemEntity>,
        todayNotificationCount: Int,
        currentHour: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
        ignoreDailyCapForTesting: Boolean = false
    ): Boolean {
        // 1. Master study reminders switch
        if (!settings.studyRemindersEnabled) {
            return false
        }

        // 2. Active Snooze check
        val currentTimeMs = System.currentTimeMillis()
        if (currentTimeMs < settings.snoozeUntilTime) {
            return false
        }

        // 3. Category toggles
        val categoryEnabled = when (template.category) {
            NotificationCategory.FUNNY_ROASTS -> settings.funnyRoastsEnabled
            NotificationCategory.DISTRACTION_NUDGES -> settings.distractionNudgesEnabled
            NotificationCategory.EXAM_ALERTS -> settings.admissionUpdatesEnabled
            NotificationCategory.STREAKS_PROGRESS, NotificationCategory.HEALTHY_HABITS -> settings.dailyGoalRemindersEnabled
            else -> settings.studyRemindersEnabled
        }
        if (!categoryEnabled) {
            return false
        }

        // 4. Quiet hours check (10:00 PM to 7:00 AM local time default)
        if (isQuietHour(currentHour, settings.quietHoursStartHour, settings.quietHoursEndHour)) {
            return false
        }

        // 5. Suppress during active focus session or concentration
        if (isFocusTimerActive) {
            return false
        }

        // 6. Daily cap check: Default max 2, hard cap 3 in any local calendar day
        if (!ignoreDailyCapForTesting) {
            val maxAllowedByLevel = when (settings.frequencyLevel.uppercase()) {
                "MINIMAL" -> 1
                "LOW" -> 2
                "BALANCED" -> 3
                else -> minOf(settings.maxDailyNotifications, 3)
            }
            val hardLimit = minOf(maxAllowedByLevel, 3)
            if (todayNotificationCount >= hardLimit) {
                return false
            }
        }

        // 7. Minimum Cooldown Check (Default 4-hour cooldown between proactive notifications)
        if (!ignoreDailyCapForTesting && settings.lastNotificationTime > 0) {
            var baseCooldownHours = when (settings.frequencyLevel.uppercase()) {
                "MINIMAL" -> 8
                "BALANCED" -> 3
                else -> 4 // LOW default is 4 hours
            }

            // Adaptive frequency decay: if notifications are repeatedly ignored, increase cooldown
            if (settings.consecutiveIgnoredCount >= 3) {
                baseCooldownHours += 2
            }

            val minCooldownMs = baseCooldownHours * 3600_000L
            val elapsedSinceLast = currentTimeMs - settings.lastNotificationTime
            if (elapsedSinceLast < minCooldownMs) {
                return false
            }
        }

        // 8. Category Tone Rotation: Avoid repeating exact same category consecutively
        val lastNotif = recentNotifications.firstOrNull()
        if (lastNotif != null && lastNotif.category == template.category.name) {
            val timeSinceLastMs = currentTimeMs - lastNotif.createdAt
            if (timeSinceLastMs < 2 * 3600_000L) { // Within 2 hours
                return false
            }
        }

        // 9. Data prerequisites check
        if (template.requiresExamData && admissionTargets.none { !it.isArchived }) {
            return false
        }
        if (template.requiresTasks && tasks.none { it.status == "PENDING" }) {
            return false
        }
        if (template.requiresChapters && chapters.none { it.status != "COMPLETED" }) {
            return false
        }
        if (template.requiresNotes && notes.isEmpty()) {
            return false
        }

        // 10. Repeat avoidance: check if title was shown in recent notifications (last 15)
        val recentTitles = recentNotifications.take(15).map { it.title }
        if (recentTitles.contains(template.title)) {
            return false
        }

        return true
    }

    private fun isQuietHour(currentHour: Int, startHour: Int, endHour: Int): Boolean {
        if (startHour == endHour) return false
        return if (startHour > endHour) {
            // e.g. 22 to 7 -> 22, 23, 0, 1, 2, 3, 4, 5, 6
            currentHour >= startHour || currentHour < endHour
        } else {
            // e.g. 1 to 6 -> 1, 2, 3, 4, 5
            currentHour in startHour until endHour
        }
    }
}
