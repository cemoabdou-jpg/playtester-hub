package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Query("SELECT * FROM user_apps ORDER BY createdAt DESC")
    fun getAllUserApps(): Flow<List<UserApp>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserApp(app: UserApp)

    @Delete
    suspend fun deleteUserApp(app: UserApp)

    @Query("UPDATE user_apps SET currentTesters = currentTesters + 1 WHERE packageName = :packageName")
    suspend fun incrementUserAppTesters(packageName: String)

    @Query("UPDATE user_apps SET currentTesters = :testers, completedDays = :days, status = :status WHERE packageName = :packageName")
    suspend fun updateAppProgress(packageName: String, testers: Int, days: Int, status: String)

    @Query("UPDATE user_apps SET isVip = :isFeatured WHERE packageName = :packageName")
    suspend fun updateUserAppFeatured(packageName: String, isFeatured: Boolean)

    @Query("UPDATE user_apps SET iconUrl = :iconUrl WHERE packageName = :packageName")
    suspend fun updateUserAppIcon(packageName: String, iconUrl: String)

    @Query("UPDATE user_apps SET name = :name, appCategory = :category, iconUrl = :iconUrl, webOptInUrl = :webOptInUrl, googleGroupUrl = :googleGroupUrl WHERE packageName = :packageName")
    suspend fun updateUserAppDetails(packageName: String, name: String, category: String, iconUrl: String, webOptInUrl: String, googleGroupUrl: String)

    @Query("UPDATE community_apps SET isFeatured = :isFeatured WHERE packageName = :packageName")
    suspend fun updateCommunityAppFeatured(packageName: String, isFeatured: Boolean)

    @Query("UPDATE joined_tests SET daysCompleted = :days, lastTestDate = :lastTestDate WHERE packageName = :packageName")
    suspend fun updateJoinedTestSession(packageName: String, days: Int, lastTestDate: Long)

    @Query("SELECT * FROM community_apps ORDER BY isFeatured DESC, currentTesters ASC")
    fun getAllCommunityApps(): Flow<List<CommunityApp>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommunityApps(apps: List<CommunityApp>)

    @Query("SELECT * FROM joined_tests ORDER BY joinedAt DESC")
    fun getAllJoinedTests(): Flow<List<JoinedTest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJoinedTest(test: JoinedTest)

    @Query("SELECT * FROM crash_reports WHERE appId = :appId ORDER BY reportedAt DESC")
    fun getCrashReports(appId: String): Flow<List<CrashReport>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrashReports(reports: List<CrashReport>)

    @Query("UPDATE crash_reports SET status = :status, resolvedAt = :resolvedAt, resolutionNote = :note WHERE id = :id")
    suspend fun updateCrashReportStatus(id: String, status: String, resolvedAt: Long, note: String)

    @Query("SELECT * FROM daily_test_logs WHERE appId = :appId ORDER BY checkInTimestamp DESC")
    fun getDailyTestLogs(appId: String): Flow<List<DailyTestLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyTestLogs(logs: List<DailyTestLog>)

    @Query("SELECT * FROM tester_feedback WHERE appId = :appId ORDER BY createdAt DESC")
    fun getFeedback(appId: String): Flow<List<TesterFeedback>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedback(feedback: List<TesterFeedback>)
}
