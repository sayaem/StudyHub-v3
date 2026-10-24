package com.example

import com.example.data.local.AdmissionTargetEntity
import com.example.data.local.AppSettingsEntity
import com.example.data.local.ChapterEntity
import com.example.data.local.NotificationItemEntity
import com.example.data.local.StudyNoteEntity
import com.example.data.local.StudyTaskEntity
import com.example.data.local.SubjectEntity
import com.example.data.local.UserProfile
import com.example.notifications.AdaptiveNotificationEngine
import com.example.notifications.NotificationCategory
import com.example.notifications.NotificationContentLibrary
import com.example.notifications.NotificationEligibilityEvaluator
import com.example.notifications.NotificationTemplate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AdaptiveNotificationEngineTest {

    @Test
    fun testNotificationLibraryContains120Items() {
        val allTemplates = NotificationContentLibrary.ALL_NOTIFICATIONS
        assertEquals(120, allTemplates.size)

        // Verify notification IDs span N001 to N120
        val ids = allTemplates.map { it.id }.toSet()
        for (i in 1..120) {
            val expectedId = "N" + String.format("%03d", i)
            assertTrue("Library missing notification ID $expectedId", ids.contains(expectedId))
        }
    }

    @Test
    fun testNotificationLibraryCategories() {
        val allTemplates = NotificationContentLibrary.ALL_NOTIFICATIONS
        val categories = allTemplates.map { it.category }.toSet()

        assertTrue(categories.contains(NotificationCategory.FUNNY_ROASTS))
        assertTrue(categories.contains(NotificationCategory.DISTRACTION_NUDGES))
        assertTrue(categories.contains(NotificationCategory.MOTIVATION))
        assertTrue(categories.contains(NotificationCategory.SYLLABUS_CHAPTERS))
        assertTrue(categories.contains(NotificationCategory.TASKS_PLANNING))
        assertTrue(categories.contains(NotificationCategory.EXAM_ALERTS))
        assertTrue(categories.contains(NotificationCategory.FOCUS_BREAKS))
        assertTrue(categories.contains(NotificationCategory.STREAKS_PROGRESS))
        assertTrue(categories.contains(NotificationCategory.HEALTHY_HABITS))
        assertTrue(categories.contains(NotificationCategory.GENTLE_COMEBACK))
        assertTrue(categories.contains(NotificationCategory.NOTES_LEARNING))
    }

    @Test
    fun testExamAlertsDataRequirementRestriction() {
        val examTemplates = NotificationContentLibrary.ALL_NOTIFICATIONS.filter {
            it.category == NotificationCategory.EXAM_ALERTS
        }
        assertTrue(examTemplates.isNotEmpty())

        for (template in examTemplates) {
            assertTrue(
                "Exam alert template ${template.id} must require exam data",
                template.requiresExamData
            )
        }

        val settings = AppSettingsEntity(
            funnyRoastsEnabled = true,
            distractionNudgesEnabled = true,
            admissionUpdatesEnabled = true,
            quietHoursStartHour = 23,
            quietHoursEndHour = 6,
            maxDailyNotifications = 10
        )

        val target = AdmissionTargetEntity(
            id = 1L,
            institutionName = "BUET",
            programUnit = "A Unit",
            applicationDeadline = "2026-11-15",
            examDate = "2026-12-01"
        )

        // Evaluate eligibility WITHOUT admission targets
        for (template in examTemplates) {
            val eligibleNoData = NotificationEligibilityEvaluator.isEligible(
                template = template,
                settings = settings,
                userProfile = null,
                subjects = emptyList(),
                chapters = emptyList(),
                tasks = emptyList(),
                admissionTargets = emptyList(),
                notes = emptyList(),
                isFocusTimerActive = false,
                recentNotifications = emptyList(),
                todayNotificationCount = 0,
                currentHour = 14
            )
            assertFalse(
                "Exam alert ${template.id} must NOT be eligible without admission targets",
                eligibleNoData
            )

            val eligibleWithData = NotificationEligibilityEvaluator.isEligible(
                template = template,
                settings = settings,
                userProfile = null,
                subjects = emptyList(),
                chapters = emptyList(),
                tasks = emptyList(),
                admissionTargets = listOf(target),
                notes = emptyList(),
                isFocusTimerActive = false,
                recentNotifications = emptyList(),
                todayNotificationCount = 0,
                currentHour = 14
            )
            assertTrue(
                "Exam alert ${template.id} MUST be eligible when admission targets exist",
                eligibleWithData
            )
        }
    }

    @Test
    fun testQuietHoursSuppression() {
        val template = NotificationContentLibrary.ALL_NOTIFICATIONS.first { it.id == "N031" } // Motivation
        val settings = AppSettingsEntity(
            quietHoursStartHour = 23,
            quietHoursEndHour = 6,
            maxDailyNotifications = 10
        )

        // Hour 2 = Quiet hour (2 AM)
        val eligibleQuiet = NotificationEligibilityEvaluator.isEligible(
            template = template,
            settings = settings,
            userProfile = null,
            subjects = emptyList(),
            chapters = emptyList(),
            tasks = emptyList(),
            admissionTargets = emptyList(),
            notes = emptyList(),
            isFocusTimerActive = false,
            recentNotifications = emptyList(),
            todayNotificationCount = 0,
            currentHour = 2
        )
        assertFalse("Notifications must be suppressed during quiet hours (2 AM)", eligibleQuiet)

        // Hour 15 = Active hour (3 PM)
        val eligibleActive = NotificationEligibilityEvaluator.isEligible(
            template = template,
            settings = settings,
            userProfile = null,
            subjects = emptyList(),
            chapters = emptyList(),
            tasks = emptyList(),
            admissionTargets = emptyList(),
            notes = emptyList(),
            isFocusTimerActive = false,
            recentNotifications = emptyList(),
            todayNotificationCount = 0,
            currentHour = 15
        )
        assertTrue("Notifications should be allowed during active hours (3 PM)", eligibleActive)
    }

    @Test
    fun testFocusTimerSuppression() {
        val template = NotificationContentLibrary.ALL_NOTIFICATIONS.first { it.id == "N001" } // Funny Roast
        val settings = AppSettingsEntity(
            funnyRoastsEnabled = true,
            maxDailyNotifications = 10
        )

        val eligibleDuringFocus = NotificationEligibilityEvaluator.isEligible(
            template = template,
            settings = settings,
            userProfile = null,
            subjects = emptyList(),
            chapters = emptyList(),
            tasks = emptyList(),
            admissionTargets = emptyList(),
            notes = emptyList(),
            isFocusTimerActive = true,
            recentNotifications = emptyList(),
            todayNotificationCount = 0,
            currentHour = 14
        )
        assertFalse("Non-focus notifications must be suppressed while timer is active", eligibleDuringFocus)
    }

    @Test
    fun testDailyCapLimit() {
        val template = NotificationContentLibrary.ALL_NOTIFICATIONS.first { it.id == "N031" } // Motivation
        val settings = AppSettingsEntity(maxDailyNotifications = 3)

        // Under cap (2 < 3)
        val underCap = NotificationEligibilityEvaluator.isEligible(
            template = template,
            settings = settings,
            userProfile = null,
            subjects = emptyList(),
            chapters = emptyList(),
            tasks = emptyList(),
            admissionTargets = emptyList(),
            notes = emptyList(),
            isFocusTimerActive = false,
            recentNotifications = emptyList(),
            todayNotificationCount = 2,
            currentHour = 14
        )
        assertTrue("Should be eligible under daily cap", underCap)

        // At or over cap (3 >= 3)
        val atCap = NotificationEligibilityEvaluator.isEligible(
            template = template,
            settings = settings,
            userProfile = null,
            subjects = emptyList(),
            chapters = emptyList(),
            tasks = emptyList(),
            admissionTargets = emptyList(),
            notes = emptyList(),
            isFocusTimerActive = false,
            recentNotifications = emptyList(),
            todayNotificationCount = 3,
            currentHour = 14
        )
        assertFalse("Must be suppressed at daily cap limit", atCap)
    }

    @Test
    fun testRepeatAvoidanceHistory() {
        val template = NotificationContentLibrary.ALL_NOTIFICATIONS.first { it.id == "N005" }
        val settings = AppSettingsEntity(funnyRoastsEnabled = true, maxDailyNotifications = 10)

        val recentNotifications = listOf(
            NotificationItemEntity(
                title = template.title,
                body = template.body,
                category = "FUNNY_ROASTS",
                createdAt = System.currentTimeMillis() - 1000
            )
        )

        val eligibleInHistory = NotificationEligibilityEvaluator.isEligible(
            template = template,
            settings = settings,
            userProfile = null,
            subjects = emptyList(),
            chapters = emptyList(),
            tasks = emptyList(),
            admissionTargets = emptyList(),
            notes = emptyList(),
            isFocusTimerActive = false,
            recentNotifications = recentNotifications,
            todayNotificationCount = 0,
            currentHour = 14
        )
        assertFalse("Template presented recently must be suppressed to avoid repeats", eligibleInHistory)
    }

    @Test
    fun testAdaptiveNotificationEngineSelection() {
        val userProfile = UserProfile(displayName = "Rahim", academicLevel = "HSC")

        val eligibleTemplates = NotificationContentLibrary.ALL_NOTIFICATIONS.filter { !it.requiresExamData }

        val selected = AdaptiveNotificationEngine.selectBestCandidate(
            templates = eligibleTemplates,
            userProfile = userProfile,
            subjects = emptyList(),
            chapters = listOf(ChapterEntity(subjectId = 1L, title = "Physics Ch1", status = "NOT_STARTED")),
            tasks = listOf(StudyTaskEntity(title = "Math Prep", date = "2026-10-25", priority = "HIGH")),
            admissionTargets = emptyList(),
            notes = emptyList(),
            recentNotifications = emptyList(),
            currentHour = 15
        )

        assertNotNull("Engine must select an eligible notification candidate", selected)
        assertTrue(selected!!.body.isNotBlank())
    }
}
