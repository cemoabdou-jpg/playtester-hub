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

data class AuthUser(
    val uid: String = "dev_guest_01",
    val email: String = "cemoabdou@gmail.com",
    val displayName: String = "المطور المعتمد",
    val points: Int = 750,
    val isVip: Boolean = true,
    val isEmailVerified: Boolean = false,
    val appsCount: Int = 1,
    val testsCount: Int = 3
)

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
