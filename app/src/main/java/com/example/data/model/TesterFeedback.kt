package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tester_feedback")
data class TesterFeedback(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userAppId: Int = 0,
    val packageName: String,
    val appName: String,
    val testerName: String = "مختبر معتمد",
    val testerEmail: String = "tester@example.com",
    val rating: Int = 5,
    val feedbackType: String = "FEEDBACK", // "FEEDBACK", "BUG_REPORT", "FEATURE_REQUEST", "PERFORMANCE", "UI_UX"
    val content: String,
    val deviceModel: String = "",
    val androidVersion: String = "",
    val screenshotUrl: String = "",
    val developerReply: String? = null,
    val developerRepliedAt: Long? = null,
    val isVerifiedTester: Boolean = true,
    val isSyncedToCloud: Boolean = false,
    val submittedAt: Long = System.currentTimeMillis()
)
