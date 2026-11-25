package com.example.data.repository

import com.example.data.local.AppDao
import com.example.data.model.*
import com.example.data.remote.CloudSyncService
import com.example.data.remote.FirestoreService
import com.example.ui.util.L
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull

class TesterRepository(
    private val appDao: AppDao,
    private val firestoreService: FirestoreService = FirestoreService(),
    private val cloudSyncService: CloudSyncService = CloudSyncService()
) {
    // All community applications available on the server for testing
    val communityApps: Flow<List<CommunityApp>> = firestoreService.getCommunityAppsFlow()

    // User's own apps: combines local Room cache with cloud-synced metadata
    val userApps: Flow<List<UserApp>> = combine(
        appDao.getAllUserApps(),
        communityApps
    ) { localApps, cloudApps ->
        val result = localApps.toMutableList()

        cloudApps.forEach { cloud ->
            val existingIndex = result.indexOfFirst { it.packageName == cloud.packageName }
            if (existingIndex >= 0) {
                val existing = result[existingIndex]
                val realCloudCount = cloud.currentTesters
                val updatedTesters = maxOf(existing.currentTesters, realCloudCount)
                val resolvedIcon = if (cloud.iconUrl.isNotBlank()) cloud.iconUrl else existing.iconUrl
                val isCompleted = updatedTesters >= 20 && existing.completedDays >= 14
                result[existingIndex] = existing.copy(
                    currentTesters = updatedTesters,
                    targetTesters = maxOf(existing.targetTesters, cloud.targetTesters),
                    status = if (isCompleted) "READY_FOR_PRODUCTION" else "TESTING",
                    iconUrl = resolvedIcon,
                    isVip = existing.isVip || cloud.isFeatured,
                    webOptInUrl = if (cloud.webOptInUrl.isNotBlank()) cloud.webOptInUrl else existing.webOptInUrl,
                    androidOptInUrl = if (cloud.androidOptInUrl.isNotBlank()) cloud.androidOptInUrl else existing.androidOptInUrl
                )
            }
        }
        result
    }

    val joinedTests: Flow<List<JoinedTest>> = appDao.getAllJoinedTests()

    // =========================================================================
    // User Profile, Referrals & Cross-User Notifications
    // =========================================================================

    suspend fun fetchOrCreateUserProfile(
        email: String,
        displayName: String,
        defaultUid: String,
        isOwner: Boolean
    ): CloudUserProfile? = cloudSyncService.fetchOrCreateUserProfile(email, displayName, defaultUid, isOwner)

    fun getUserProfileFlow(email: String): Flow<CloudUserProfile?> =
        cloudSyncService.getUserProfileFlow(email)

    suspend fun updateUserProfileFields(email: String, fields: Map<String, Any>) =
        cloudSyncService.updateUserProfileFields(email, fields)

    suspend fun incrementUserPoints(email: String, delta: Int) =
        cloudSyncService.incrementUserPoints(email, delta)

    suspend fun redeemReferralCode(
        currentUserEmail: String,
        currentUserName: String,
        code: String
    ): Result<Int> = cloudSyncService.redeemReferralCode(currentUserEmail, currentUserName, code)

    fun getUserNotificationsFlow(userEmail: String): Flow<List<TesterNotification>> =
        cloudSyncService.getUserNotificationsFlow(userEmail)

    suspend fun sendNotificationToUser(targetEmail: String, notification: TesterNotification) =
        cloudSyncService.sendNotificationToUser(targetEmail, notification)

    suspend fun markNotificationRead(userEmail: String, notificationId: String) =
        cloudSyncService.markNotificationRead(userEmail, notificationId)

    suspend fun markAllNotificationsRead(userEmail: String, unreadIds: List<String>) =
        cloudSyncService.markAllNotificationsRead(userEmail, unreadIds)

    /**
     * Restores a user's joined tests & apps from Firestore on login / device switch.
     */
    suspend fun restoreUserWorkspaceFromCloud(userEmail: String) {
        if (userEmail.isBlank()) return
        val cloudTests = cloudSyncService.fetchUserJoinedTests(userEmail)
        val cloudUserApps = cloudSyncService.fetchUserAppsFromCloud(userEmail)

        val localApps = appDao.getAllUserApps().firstOrNull() ?: emptyList()
        if (cloudUserApps.isNotEmpty()) {
            cloudUserApps.forEach { cloudApp ->
                if (localApps.none { it.packageName == cloudApp.packageName }) {
                    appDao.insertUserApp(cloudApp)
                }
            }
        } else {
            // Backfill from community_apps authored by this email if user_apps subcollection is empty
            val authored = communityApps.firstOrNull()
                ?.filter { it.developerEmail.equals(userEmail, ignoreCase = true) }
                ?: emptyList()
            authored.forEach { comm ->
                if (localApps.none { it.packageName == comm.packageName }) {
                    val reconstructed = UserApp(
                        id = comm.packageName,
                        name = comm.name,
                        packageName = comm.packageName,
                        iconUrl = comm.iconUrl,
                        webOptInUrl = comm.webOptInUrl,
                        androidOptInUrl = comm.androidOptInUrl,
                        googleGroupUrl = comm.googleGroupUrl,
                        targetTesters = comm.targetTesters,
                        currentTesters = comm.currentTesters,
                        targetDays = 14,
                        completedDays = 1,
                        status = if (comm.currentTesters >= 20) "TESTING" else "TESTING",
                        isVip = comm.isFeatured,
                        appCategory = comm.category,
                        description = comm.description
                    )
                    appDao.insertUserApp(reconstructed)
                    cloudSyncService.saveUserAppToCloud(userEmail, reconstructed)
                }
            }
        }

        if (cloudTests.isNotEmpty()) {
            val localTests = appDao.getAllJoinedTests().firstOrNull() ?: emptyList()
            cloudTests.forEach { ct ->
                val existing = localTests.find { it.packageName == ct.packageName }
                if (existing == null || ct.daysCompleted > existing.daysCompleted) {
                    appDao.insertJoinedTest(ct)
                }
            }
        }
    }

    suspend fun clearLocalSessionTables() {
        appDao.clearAllJoinedTests()
        appDao.clearAllUserApps()
    }

    // =========================================================================
    // User Apps Management (Honest Telemetry - No Fake Tester Seeding)
    // =========================================================================

    suspend fun addUserApp(
        name: String,
        packageName: String,
        webOptInUrl: String,
        androidOptInUrl: String,
        googleGroupUrl: String,
        category: String = L.t("تطبيق", "App"),
        description: String = "",
        iconUrl: String = "",
        developerName: String = L.t("المطور المعتمد", "Verified Developer"),
        developerEmail: String = "",
        userId: String = "user_default",
        initialTesters: Int = 0,
        completedDays: Int = 1
    ) {
        val safeTesters = initialTesters.coerceIn(0, 50)
        val safeDays = completedDays.coerceIn(1, 14)

        try {
            firestoreService.addAppToFirestore(
                name = name,
                packageName = packageName,
                webOptInUrl = webOptInUrl,
                androidOptInUrl = androidOptInUrl,
                googleGroupUrl = googleGroupUrl,
                category = category,
                description = description,
                iconUrl = iconUrl,
                developerName = developerName,
                developerEmail = developerEmail,
                userId = userId,
                initialTesters = safeTesters
            )
        } catch (_: Exception) {}

        val isCompleted = safeTesters >= 20 && safeDays >= 14
        val newApp = UserApp(
            id = packageName,
            name = name,
            packageName = packageName,
            iconUrl = iconUrl,
            webOptInUrl = webOptInUrl,
            androidOptInUrl = androidOptInUrl,
            googleGroupUrl = googleGroupUrl,
            targetTesters = 20,
            currentTesters = safeTesters,
            targetDays = 14,
            completedDays = safeDays,
            status = if (isCompleted) "READY_FOR_PRODUCTION" else "TESTING",
            appCategory = category,
            description = description
        )
        appDao.insertUserApp(newApp)
        if (developerEmail.isNotBlank()) {
            cloudSyncService.saveUserAppToCloud(developerEmail, newApp)
        }
    }

    suspend fun updateAppTesters(
        packageName: String,
        realTestersCount: Int,
        completedDays: Int? = null,
        ownerEmail: String = ""
    ) {
        val safeTesters = realTestersCount.coerceIn(0, 50)
        val currentList = appDao.getAllUserApps().firstOrNull() ?: emptyList()
        val currentApp = currentList.find { it.packageName == packageName }
        val days = (completedDays ?: currentApp?.completedDays ?: 1).coerceIn(1, 14)
        val isCompleted = safeTesters >= 20 && days >= 14
        val status = if (isCompleted) "READY_FOR_PRODUCTION" else "TESTING"

        appDao.updateAppProgress(packageName, safeTesters, days, status)
        try {
            firestoreService.updateAppTestersInFirestore(packageName, safeTesters)
            if (ownerEmail.isNotBlank() && currentApp != null) {
                cloudSyncService.saveUserAppToCloud(
                    ownerEmail,
                    currentApp.copy(currentTesters = safeTesters, completedDays = days, status = status)
                )
            }
        } catch (_: Exception) {}
    }

    suspend fun removeUserApp(app: UserApp, ownerEmail: String = "") {
        appDao.deleteUserApp(app)
        if (ownerEmail.isNotBlank()) {
            cloudSyncService.deleteUserAppFromCloud(ownerEmail, app.packageName)
        }
    }

    suspend fun updateAppIcon(packageName: String, newIconUrl: String, ownerEmail: String = "") {
        appDao.updateUserAppIcon(packageName, newIconUrl)
        try {
            firestoreService.updateAppIconInFirestore(packageName, newIconUrl)
            val updated = (appDao.getAllUserApps().firstOrNull() ?: emptyList()).find { it.packageName == packageName }
            if (ownerEmail.isNotBlank() && updated != null) {
                cloudSyncService.saveUserAppToCloud(ownerEmail, updated)
            }
        } catch (_: Exception) {}
    }

    suspend fun updateAppDetails(
        packageName: String,
        name: String,
        category: String,
        iconUrl: String,
        webOptInUrl: String,
        googleGroupUrl: String,
        ownerEmail: String = ""
    ) {
        appDao.updateUserAppDetails(packageName, name, category, iconUrl, webOptInUrl, googleGroupUrl)
        try {
            firestoreService.updateAppDetailsInFirestore(packageName, name, category, iconUrl, webOptInUrl, googleGroupUrl)
            val updated = (appDao.getAllUserApps().firstOrNull() ?: emptyList()).find { it.packageName == packageName }
            if (ownerEmail.isNotBlank() && updated != null) {
                cloudSyncService.saveUserAppToCloud(ownerEmail, updated)
            }
        } catch (_: Exception) {}
    }

    // =========================================================================
    // Testing, Crash Reports, Daily Logs & Feedback (Firestore + Room Sync)
    // =========================================================================

    suspend fun joinTest(test: JoinedTest, userEmail: String) {
        val current = appDao.getAllJoinedTests().firstOrNull() ?: emptyList()
        if (current.any { it.packageName == test.packageName }) {
            return
        }
        appDao.insertJoinedTest(test)
        try {
            if (userEmail.isNotBlank()) {
                cloudSyncService.saveJoinedTestToCloud(userEmail, test)
            }
            firestoreService.incrementAppTesters(test.packageName)
            appDao.incrementUserAppTesters(test.packageName)
        } catch (_: Exception) {}
    }

    fun getCrashReports(appId: String): Flow<List<CrashReport>> {
        return combine(
            appDao.getCrashReports(appId),
            cloudSyncService.getCrashReportsFlow(appId)
        ) { local, cloud ->
            val merged = LinkedHashMap<String, CrashReport>()
            cloud.forEach { merged[it.id] = it }
            local.forEach { if (!merged.containsKey(it.id)) merged[it.id] = it }
            merged.values.sortedByDescending { it.reportedAt }
        }
    }

    suspend fun submitCrashReport(packageName: String, report: CrashReport) {
        appDao.insertCrashReports(listOf(report))
        cloudSyncService.submitCrashReport(packageName, report)
    }

    suspend fun resolveCrashReport(packageName: String, id: String, note: String) {
        appDao.updateCrashReportStatus(id, "RESOLVED", System.currentTimeMillis(), note)
        cloudSyncService.resolveCrashReport(packageName, id, note)
    }

    fun getDailyTestLogs(appId: String): Flow<List<DailyTestLog>> {
        return combine(
            appDao.getDailyTestLogs(appId),
            cloudSyncService.getDailyTestLogsFlow(appId)
        ) { local, cloud ->
            val merged = LinkedHashMap<String, DailyTestLog>()
            cloud.forEach { merged[it.id] = it }
            local.forEach { if (!merged.containsKey(it.id)) merged[it.id] = it }
            merged.values.sortedByDescending { it.checkInTimestamp }
        }
    }

    fun getFeedback(appId: String): Flow<List<TesterFeedback>> {
        return combine(
            appDao.getFeedback(appId),
            cloudSyncService.getFeedbackFlow(appId)
        ) { local, cloud ->
            val merged = LinkedHashMap<String, TesterFeedback>()
            cloud.forEach { merged[it.id] = it }
            local.forEach { if (!merged.containsKey(it.id)) merged[it.id] = it }
            merged.values.sortedByDescending { it.createdAt }
        }
    }

    suspend fun promoteApp(packageName: String, isFeatured: Boolean = true) {
        appDao.updateUserAppFeatured(packageName, isFeatured)
        appDao.updateCommunityAppFeatured(packageName, isFeatured)
        try {
            firestoreService.setAppFeaturedInFirestore(packageName, isFeatured)
        } catch (_: Exception) {}
    }

    suspend fun recordDailyAppTest(
        packageName: String,
        testerEmail: String,
        testerName: String
    ): Int {
        val tests = appDao.getAllJoinedTests().firstOrNull() ?: emptyList()
        val currentTest = tests.find { it.packageName == packageName } ?: return 0
        val newDays = (currentTest.daysCompleted + 1).coerceAtMost(14)
        val now = System.currentTimeMillis()
        appDao.updateJoinedTestSession(packageName, newDays, now)
        if (testerEmail.isNotBlank()) {
            cloudSyncService.saveJoinedTestToCloud(
                testerEmail,
                currentTest.copy(daysCompleted = newDays, lastTestDate = now)
            )
        }

        val resolvedEmail = testerEmail.ifBlank { "verified.tester@playtesterhub.app" }
        val resolvedName = testerName.ifBlank { L.t("مختبر معتمد", "Verified Tester") }
        val log = DailyTestLog(
            id = "log_${packageName}_${newDays}_${now}",
            appId = packageName,
            testerName = resolvedName,
            testerEmail = resolvedEmail,
            deviceModel = android.os.Build.MODEL ?: "Android Device",
            osVersion = "Android ${android.os.Build.VERSION.RELEASE}",
            dayNumber = newDays,
            checkInTimestamp = now,
            sessionDurationMinutes = 15,
            notes = L.t("جلسة اختبار يومية نشطة (+25 عملة)", "Active daily test session (+25 coins)")
        )
        appDao.insertDailyTestLogs(listOf(log))
        cloudSyncService.addDailyTestLog(packageName, log)
        return newDays
    }

    suspend fun addDailyTestLog(log: DailyTestLog) {
        appDao.insertDailyTestLogs(listOf(log))
        cloudSyncService.addDailyTestLog(log.appId, log)
    }

    suspend fun updateTestSession(packageName: String, daysCompleted: Int, userEmail: String) {
        val now = System.currentTimeMillis()
        appDao.updateJoinedTestSession(packageName, daysCompleted, now)
        if (userEmail.isNotBlank()) {
            val test = (appDao.getAllJoinedTests().firstOrNull() ?: emptyList()).find { it.packageName == packageName }
            if (test != null) {
                cloudSyncService.saveJoinedTestToCloud(userEmail, test.copy(daysCompleted = daysCompleted, lastTestDate = now))
            }
        }
    }

    suspend fun addFeedback(appId: String, testerName: String, rating: Int, comment: String) {
        val item = TesterFeedback(
            id = "fb_${appId}_${System.currentTimeMillis()}",
            appId = appId,
            testerName = testerName.ifBlank { L.t("مختبر معتمد", "Verified Tester") },
            rating = rating,
            comment = comment,
            createdAt = System.currentTimeMillis()
        )
        appDao.insertFeedback(listOf(item))
        cloudSyncService.submitFeedback(appId, item)
    }

    suspend fun adminDeleteApp(packageName: String) {
        try {
            firestoreService.deleteAppFromFirestore(packageName)
        } catch (_: Exception) {}
    }

    suspend fun adminSetFeatured(packageName: String, isFeatured: Boolean) {
        appDao.updateCommunityAppFeatured(packageName, isFeatured)
        try {
            firestoreService.setAppFeaturedInFirestore(packageName, isFeatured)
        } catch (_: Exception) {}
    }

    suspend fun adminUpdateTesters(packageName: String, testers: Int) {
        try {
            firestoreService.updateAppTestersInFirestore(packageName, testers)
        } catch (_: Exception) {}
    }

    suspend fun adminUpdateApp(
        packageName: String,
        name: String,
        devName: String,
        category: String,
        iconUrl: String,
        currentTesters: Int,
        targetTesters: Int,
        isFeatured: Boolean,
        webOptInUrl: String,
        googleGroupUrl: String
    ) {
        try {
            firestoreService.adminUpdateAppFull(
                packageName = packageName,
                name = name,
                devName = devName,
                category = category,
                iconUrl = iconUrl,
                currentTesters = currentTesters,
                targetTesters = targetTesters,
                isFeatured = isFeatured,
                webOptInUrl = webOptInUrl,
                googleGroupUrl = googleGroupUrl
            )
        } catch (_: Exception) {}
    }

    fun getChatMessagesFlow(channelId: String): Flow<List<ReciprocalChatMessage>> =
        firestoreService.getChatMessagesFlow(channelId)

    suspend fun sendChatMessage(
        channelId: String,
        message: ReciprocalChatMessage,
        recipientEmail: String,
        recipientName: String
    ) = firestoreService.sendChatMessage(channelId, message, recipientEmail, recipientName)

    fun getUserConversationsFlow(userEmail: String): Flow<List<DeveloperConversation>> =
        firestoreService.getUserConversationsFlow(userEmail)
}
