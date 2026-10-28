package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_apps")
data class UserApp(
    @PrimaryKey val id: String,
    val name: String,
    val packageName: String,
    val iconUrl: String = "",
    val playStoreUrl: String = "",
    val webOptInUrl: String = "",
    val androidOptInUrl: String = "",
    val googleGroupUrl: String = "",
    val targetTesters: Int = 20,
    val currentTesters: Int = 0,
    val targetDays: Int = 14,
    val completedDays: Int = 1,
    val status: String = "TESTING",
    val isVip: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val appCategory: String = "Productivity",
    val description: String = ""
)

@Entity(tableName = "community_apps")
data class CommunityApp(
    @PrimaryKey val id: String,
    val name: String,
    val packageName: String,
    val iconUrl: String = "",
    val developerName: String = "",
    val developerEmail: String = "",
    val category: String = "General",
    val pointsReward: Int = 50,
    val requiredDays: Int = 14,
    val currentTesters: Int = 0,
    val targetTesters: Int = 20,
    val webOptInUrl: String = "",
    val androidOptInUrl: String = "",
    val googleGroupUrl: String = "",
    val isFeatured: Boolean = false,
    val description: String = ""
)

@Entity(tableName = "joined_tests")
data class JoinedTest(
    @PrimaryKey val id: String,
    val appId: String,
    val appName: String,
    val packageName: String,
    val iconUrl: String = "",
    val joinedAt: Long = System.currentTimeMillis(),
    val daysCompleted: Int = 0,
    val lastTestDate: Long = 0,
    val status: String = "TESTING"
)

@Entity(tableName = "crash_reports")
data class CrashReport(
    @PrimaryKey val id: String,
    val appId: String,
    val title: String,
    val stackTrace: String,
    val deviceModel: String,
    val osVersion: String,
    val appVersion: String,
    val occurrences: Int = 1,
    val status: String = "RESOLVED",
    val reportedAt: Long = System.currentTimeMillis(),
    val resolvedAt: Long? = null,
    val resolutionNote: String = ""
)

@Entity(tableName = "tester_feedback")
data class TesterFeedback(
    @PrimaryKey val id: String,
    val appId: String,
    val testerName: String,
    val rating: Int,
    val comment: String,
    val category: String = "UX",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "daily_test_logs")
data class DailyTestLog(
    @PrimaryKey val id: String,
    val appId: String,
    val testerName: String,
    val testerEmail: String,
    val deviceModel: String,
    val osVersion: String,
    val dayNumber: Int,
    val checkInTimestamp: Long,
    val sessionDurationMinutes: Int = 15,
    val notes: String = "جلسة اختبار تفاعلية ناجحة"
)

const val PLATFORM_OWNER_EMAIL = "cemoabdou@gmail.com"

data class AuthUser(
    val uid: String = "dev_guest_01",
    val email: String = "developer@testerhub.com",
    val displayName: String = "مطور تجريبي",
    val points: Int = 0,
    val isVip: Boolean = false,
    val isEmailVerified: Boolean = false,
    val appsCount: Int = 1,
    val testsCount: Int = 3
) {
    val isPlatformOwner: Boolean
        get() = email.trim().equals(PLATFORM_OWNER_EMAIL, ignoreCase = true)
}

data class AchievementBadge(
    val id: String,
    val title: String,
    val description: String,
    val emoji: String,
    val coinReward: Int,
    val currentProgress: Int,
    val targetProgress: Int,
    val isUnlocked: Boolean,
    val isClaimed: Boolean
)

data class LeaderboardUser(
    val rank: Int,
    val name: String,
    val email: String,
    val testsCompleted: Int,
    val points: Int,
    val badgeTitle: String,
    val isCurrentUser: Boolean = false
)

data class TesterNotification(
    val id: String,
    val testerName: String,
    val testerEmail: String,
    val testerBadge: String = "مختبر معتمد ⭐",
    val appName: String,
    val appPackageName: String,
    val timestamp: Long = System.currentTimeMillis(),
    val actionType: String = "TESTED", // "TESTED", "JOINED"
    val deviceModel: String = "Samsung Galaxy S23",
    val isRead: Boolean = false,
    val message: String = ""
)

data class TesterProfileData(
    val name: String,
    val email: String,
    val badge: String = "مختبر معتمد ⭐",
    val rating: Float = 4.9f,
    val testsCompleted: Int = 18,
    val reputationPoints: Int = 980,
    val deviceModel: String = "Samsung Galaxy S23",
    val bio: String = "مطور ومختبر أندرويد معتمد في مجتمع PlayTester Hub، متفاعل يومياً لدعم تطبيقات الزملاء المتبادلة.",
    val apps: List<CommunityApp> = emptyList()
)

data class ComplianceDay(
    val dayNumber: Int,
    val dateLabel: String,
    val activeTestersCount: Int,
    val targetTesters: Int = 20,
    val avgSessionMinutes: Float,
    val retentionRate: Float,
    val isCompleted: Boolean,
    val isToday: Boolean = false,
    val notes: String = ""
)

data class GooglePlayQuestionnaireAnswer(
    val questionNumber: Int,
    val questionTitleAr: String,
    val questionTitleEn: String,
    val recommendedAnswerAr: String,
    val recommendedAnswerEn: String
)

data class ReciprocalChatMessage(
    val id: String,
    val senderName: String,
    val senderEmail: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isFromMe: Boolean = false,
    val attachedAppPackage: String? = null,
    val attachedAppName: String? = null
)

data class QAInspectionCheck(
    val id: String,
    val title: String,
    val description: String,
    val isChecked: Boolean = false
)

data class DeveloperConversation(
    val channelId: String,
    val otherParticipantName: String,
    val otherParticipantEmail: String,
    val lastMessage: String,
    val lastTimestamp: Long,
    val lastSenderEmail: String,
    val appName: String? = null
)

