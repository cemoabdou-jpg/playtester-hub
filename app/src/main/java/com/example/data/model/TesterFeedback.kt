package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tester_feedback")
data class TesterFeedback(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userAppId: Int = 0,
    val packageName: String,
    val appName: String,
    val testerEmail: String = "tester@example.com",
    val rating: Int = 5,
    val feedbackType: String = "FEEDBACK", // "FEEDBACK", "BUG_REPORT", "FEATURE_REQUEST"
    val content: String,
    val submittedAt: Long = System.currentTimeMillis()
)
