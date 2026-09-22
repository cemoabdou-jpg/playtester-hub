package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AppBuildArtifact
import com.example.data.model.AppNotification
import com.example.data.model.AuthUser
import com.example.data.model.CommunityApp
import com.example.data.model.DailyTestLog
import com.example.data.model.InvitedTester
import com.example.data.model.JoinedTest
import com.example.data.model.TesterFeedback
import com.example.data.model.UserApp
import com.example.data.model.UserWallet
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // Community Apps (Ordered by Owner Priority Score, then Featured, then ID)
    @Query("SELECT * FROM community_apps WHERE isHidden = 0 ORDER BY priorityScore DESC, isFeatured DESC, id DESC")
    fun getAllCommunityApps(): Flow<List<CommunityApp>>

    @Query("SELECT * FROM community_apps ORDER BY priorityScore DESC, isFeatured DESC, id DESC")
    fun getAllCommunityAppsAdmin(): Flow<List<CommunityApp>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommunityApp(app: CommunityApp): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommunityApps(apps: List<CommunityApp>)

    @Update
    suspend fun updateCommunityApp(app: CommunityApp)

    @Query("DELETE FROM community_apps WHERE id = :id")
    suspend fun deleteCommunityApp(id: Int)

    @Query("UPDATE community_apps SET priorityScore = :score WHERE id = :appId")
    suspend fun updateAppPriority(appId: Int, score: Int)

    @Query("UPDATE community_apps SET isFeatured = :isFeatured WHERE id = :appId")
    suspend fun toggleAppFeatured(appId: Int, isFeatured: Boolean)

    @Query("UPDATE community_apps SET isHidden = :isHidden WHERE id = :appId")
    suspend fun toggleAppHidden(appId: Int, isHidden: Boolean)

    @Query("UPDATE community_apps SET testersCurrent = testersCurrent + 1 WHERE id = :appId")
    suspend fun incrementCommunityAppTesters(appId: Int)

    // Joined Tests
    @Query("SELECT * FROM joined_tests ORDER BY isCompleted ASC, id DESC")
    fun getAllJoinedTests(): Flow<List<JoinedTest>>

    @Query("SELECT * FROM joined_tests WHERE communityAppId = :appId LIMIT 1")
    suspend fun getJoinedTestByCommunityId(appId: Int): JoinedTest?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJoinedTest(test: JoinedTest): Long

    @Update
    suspend fun updateJoinedTest(test: JoinedTest)

    @Query("DELETE FROM joined_tests WHERE id = :id")
    suspend fun deleteJoinedTest(id: Int)

    // User Published Apps
    @Query("SELECT * FROM user_apps ORDER BY id DESC")
    fun getAllUserApps(): Flow<List<UserApp>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserApp(app: UserApp): Long

    @Update
    suspend fun updateUserApp(app: UserApp)

    @Query("DELETE FROM user_apps WHERE id = :id")
    suspend fun deleteUserApp(id: Int)

    // User Wallet & Stats
    @Query("SELECT * FROM user_wallet WHERE id = 1 LIMIT 1")
    fun getUserWallet(): Flow<UserWallet?>

    @Query("SELECT * FROM user_wallet WHERE id = 1 LIMIT 1")
    suspend fun getWalletOnce(): UserWallet?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateWallet(wallet: UserWallet)

    // Auth Users
    @Query("SELECT * FROM auth_users WHERE isLoggedIn = 1 LIMIT 1")
    fun getCurrentUserFlow(): Flow<AuthUser?>

    @Query("SELECT * FROM auth_users WHERE isLoggedIn = 1 LIMIT 1")
    suspend fun getCurrentUserOnce(): AuthUser?

    @Query("SELECT * FROM auth_users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): AuthUser?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuthUser(user: AuthUser): Long

    @Update
    suspend fun updateAuthUser(user: AuthUser)

    @Query("UPDATE auth_users SET isLoggedIn = 0")
    suspend fun logoutAllUsers()

    // Build Artifacts (APKs/AABs)
    @Query("SELECT * FROM app_build_artifacts WHERE userAppId = :userAppId ORDER BY versionCode DESC")
    fun getArtifactsForApp(userAppId: Int): Flow<List<AppBuildArtifact>>

    @Query("SELECT * FROM app_build_artifacts ORDER BY uploadedAt DESC")
    fun getAllArtifacts(): Flow<List<AppBuildArtifact>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtifact(artifact: AppBuildArtifact): Long

    @Query("UPDATE app_build_artifacts SET isActiveForTesting = 0 WHERE userAppId = :userAppId")
    suspend fun deactivateAllArtifactsForApp(userAppId: Int)

    @Query("UPDATE app_build_artifacts SET isActiveForTesting = 1 WHERE id = :artifactId")
    suspend fun activateArtifact(artifactId: Int)

    @Query("DELETE FROM app_build_artifacts WHERE id = :artifactId")
    suspend fun deleteArtifact(artifactId: Int)

    // Invited Testers
    @Query("SELECT * FROM invited_testers WHERE userAppId = :userAppId ORDER BY invitedAt DESC")
    fun getInvitedTestersForApp(userAppId: Int): Flow<List<InvitedTester>>

    @Query("SELECT * FROM invited_testers ORDER BY invitedAt DESC")
    fun getAllInvitedTesters(): Flow<List<InvitedTester>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvitedTester(tester: InvitedTester): Long

    @Update
    suspend fun updateInvitedTester(tester: InvitedTester)

    @Query("DELETE FROM invited_testers WHERE id = :testerId")
    suspend fun deleteInvitedTester(testerId: Int)

    @Query("UPDATE invited_testers SET status = :status, acceptedAt = :acceptedAt WHERE id = :testerId")
    suspend fun updateTesterStatus(testerId: Int, status: String, acceptedAt: Long?)

    // Notifications
    @Query("SELECT * FROM app_notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<AppNotification>>

    @Query("SELECT COUNT(*) FROM app_notifications WHERE isRead = 0")
    fun getUnreadNotificationsCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: AppNotification): Long

    @Query("UPDATE app_notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationAsRead(id: Int)

    @Query("UPDATE app_notifications SET isRead = 1")
    suspend fun markAllNotificationsAsRead()

    @Query("DELETE FROM app_notifications WHERE id = :id")
    suspend fun deleteNotification(id: Int)

    @Query("DELETE FROM app_notifications")
    suspend fun clearAllNotifications()

    // Daily Test Logs (تأكيد التجربة اليومية)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyTestLog(log: DailyTestLog): Long

    @Query("SELECT * FROM daily_test_logs WHERE joinedTestId = :joinedTestId ORDER BY dayNumber ASC")
    fun getLogsForJoinedTest(joinedTestId: Int): Flow<List<DailyTestLog>>

    @Query("SELECT * FROM daily_test_logs ORDER BY verifiedAt DESC")
    fun getAllDailyTestLogs(): Flow<List<DailyTestLog>>

    @Query("SELECT * FROM daily_test_logs WHERE joinedTestId = :joinedTestId AND testDate = :testDate LIMIT 1")
    suspend fun getTodayLog(joinedTestId: Int, testDate: String): DailyTestLog?

    @Query("SELECT COUNT(*) FROM daily_test_logs WHERE testDate = :testDate")
    fun getTodayTestsCount(testDate: String): Flow<Int>

    // Tester Feedback & Bug Reports
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedback(feedback: TesterFeedback): Long

    @Query("SELECT * FROM tester_feedback ORDER BY submittedAt DESC")
    fun getAllFeedback(): Flow<List<TesterFeedback>>

    @Query("SELECT * FROM tester_feedback WHERE packageName = :packageName ORDER BY submittedAt DESC")
    fun getFeedbackForPackage(packageName: String): Flow<List<TesterFeedback>>

    @Query("DELETE FROM tester_feedback WHERE id = :id")
    suspend fun deleteFeedback(id: Int)
}
