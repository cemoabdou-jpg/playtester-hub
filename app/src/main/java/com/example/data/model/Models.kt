package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "auth_users")
data class AuthUser(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val email: String,
    val passwordHash: String = "",
    val displayName: String,
    val authProvider: String = "EMAIL", // "EMAIL", "GOOGLE", "GITHUB"
    val avatarUrl: String = "",
    val role: String = "DEVELOPER", // "ADMIN" (صاحب التطبيق), "DEVELOPER"
    val isLoggedIn: Boolean = true,
    val registeredAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "app_build_artifacts")
data class AppBuildArtifact(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userAppId: Int,
    val versionName: String,
    val versionCode: Int,
    val artifactType: String, // "AAB" or "APK"
    val fileName: String,
    val fileSizeMb: Double,
    val releaseNotes: String = "",
    val uploadedAt: Long = System.currentTimeMillis(),
    val isActiveForTesting: Boolean = false,
    val track: String = "CLOSED_TESTING" // "CLOSED_TESTING", "INTERNAL"
)

@Entity(tableName = "invited_testers")
data class InvitedTester(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userAppId: Int,
    val email: String,
    val status: String = "INVITED", // "INVITED", "ACCEPTED", "TESTING", "DECLINED"
    val invitedAt: Long = System.currentTimeMillis(),
    val acceptedAt: Long? = null,
    val notes: String = ""
)

@Entity(tableName = "community_apps")
data class CommunityApp(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val developerName: String,
    val packageName: String,
    val description: String,
    val category: String,
    val googleGroupUrl: String,
    val webOptInUrl: String,
    val androidOptInUrl: String,
    val testersCurrent: Int = 12,
    val testersTarget: Int = 20,
    val rewardPoints: Int = 50,
    val isFeatured: Boolean = false,
    val priorityScore: Int = 0, // 0 = standard, positive = higher rank/top display
    val isHidden: Boolean = false, // Owner can hide/disable an app
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "joined_tests")
data class JoinedTest(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val communityAppId: Int,
    val appName: String,
    val packageName: String,
    val category: String = "تطبيقات",
    val googleGroupUrl: String,
    val webOptInUrl: String,
    val androidOptInUrl: String,
    val joinTimestamp: Long = System.currentTimeMillis(),
    val daysCompleted: Int = 0,
    val lastCheckInDate: String = "",
    val checkedDaysCsv: String = "",
    val isCompleted: Boolean = false,
    val feedbackSent: Boolean = false
)

@Entity(tableName = "user_apps")
data class UserApp(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val packageName: String,
    val category: String,
    val googleGroupUrl: String,
    val webOptInUrl: String,
    val androidOptInUrl: String,
    val instructions: String = "",
    val testersCount: Int = 1,
    val targetTesters: Int = 20,
    val daysElapsed: Int = 1,
    val targetDays: Int = 14,
    val createdAt: Long = System.currentTimeMillis(),
    val status: String = "TESTING" // "TESTING", "READY_FOR_PRODUCTION", "PAUSED"
)

@Entity(tableName = "user_wallet")
data class UserWallet(
    @PrimaryKey val id: Int = 1,
    val points: Int = 150,
    val testsCompleted: Int = 2,
    val currentStreak: Int = 4,
    val lastActiveDate: String = ""
)

@Entity(tableName = "app_notifications")
data class AppNotification(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val message: String,
    val type: String = "INFO", // "TESTER_JOINED", "DAILY_REMINDER", "MILESTONE", "INFO"
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val actionRoute: String = ""
)

@Entity(tableName = "daily_test_logs")
data class DailyTestLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val joinedTestId: Int,
    val appName: String,
    val packageName: String,
    val dayNumber: Int,
    val testDate: String, // "yyyy-MM-dd"
    val durationMinutes: Int = 10,
    val rating: Int = 5,
    val notes: String = "",
    val verifiedAt: Long = System.currentTimeMillis()
)
