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

        // 2. Category toggles
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

        // 3. Quiet hours check
        if (isQuietHour(currentHour, settings.quietHoursStartHour, settings.quietHoursEndHour)) {
            return false
        }

        // 4. Suppress during active focus session
        if (isFocusTimerActive) {
            return false
        }

        // 5. Daily cap check
        if (!ignoreDailyCapForTesting && todayNotificationCount >= settings.maxDailyNotifications) {
            return false
        }

        // 6. Data prerequisites
        if (template.requiresExamData && admissionTargets.none { !it.isArchived }) {
            return false
        }
        if (template.requiresTasks && tasks.isEmpty()) {
            return false
        }
        if (template.requiresChapters && chapters.isEmpty()) {
            return false
        }
        if (template.requiresNotes && notes.isEmpty()) {
            return false
        }

        // 7. Repeat avoidance: check if title or body was shown in recent notifications (last 15)
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
