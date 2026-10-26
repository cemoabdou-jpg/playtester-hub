package com.example.data.repository

import com.example.data.local.AppDao
import com.example.data.model.*
import com.example.data.remote.FirestoreService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull

class TesterRepository(
    private val appDao: AppDao,
    private val firestoreService: FirestoreService = FirestoreService()
) {
    // All community applications available on the server for testing
    val communityApps: Flow<List<CommunityApp>> = firestoreService.getCommunityAppsFlow()

    // User's own apps: combines local Room database apps with any cloud-synced user apps
    val userApps: Flow<List<UserApp>> = combine(
        appDao.getAllUserApps(),
        communityApps
    ) { localApps, cloudApps ->
        val result = mutableListOf<UserApp>()
        
        // 1. Add all local apps (the user's genuine created apps on this device)
        result.addAll(localApps)

        // 2. Sync tester count and cloud metadata for matching user apps
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
                    webOptInUrl = if (cloud.webOptInUrl.isNotBlank()) cloud.webOptInUrl else existing.webOptInUrl,
                    androidOptInUrl = if (cloud.androidOptInUrl.isNotBlank()) cloud.androidOptInUrl else existing.androidOptInUrl
                )
            }
        }
        result
    }

    val joinedTests: Flow<List<JoinedTest>> = appDao.getAllJoinedTests()

    suspend fun addUserApp(
        name: String,
        packageName: String,
        webOptInUrl: String,
        androidOptInUrl: String,
        googleGroupUrl: String,
        category: String = "تطبيق",
        description: String = "",
        iconUrl: String = "",
        developerName: String = "المطور المعتمد",
        userId: String = "user_default",
        initialTesters: Int = 0,
        completedDays: Int = 1
    ) {
        val safeTesters = initialTesters.coerceIn(0, 50)
        val safeDays = completedDays.coerceIn(1, 14)

        // Save to cloud Firestore so it is accessible in the community
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
                userId = userId,
                initialTesters = safeTesters
            )
        } catch (_: Exception) {
            // Local persistence remains intact even if network is offline
        }

        // Save to local Room database with real initial values
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

        // Seed logs matching the actual initial testers ONLY if testers > 0
        if (safeTesters > 0) {
            val count = safeTesters.coerceAtMost(20)
            val logs = (1..count).map { testerIndex ->
                DailyTestLog(
                    id = "log_${packageName}_1_$testerIndex",
                    appId = packageName,
                    testerName = "مختبر معتمد #$testerIndex",
                    testerEmail = "tester$testerIndex@playtesterhub.com",
                    deviceModel = listOf("Pixel 8", "Galaxy S23", "Xiaomi 13", "OnePlus 11")[testerIndex % 4],
                    osVersion = "Android 14 (API 34)",
                    dayNumber = safeDays,
                    checkInTimestamp = System.currentTimeMillis() - ((count - testerIndex) * 1800000L),
                    sessionDurationMinutes = 15,
                    notes = "جلسة اختبار تفاعلية ناجحة وموثقة"
                )
            }
            appDao.insertDailyTestLogs(logs)
        }
    }

    suspend fun updateAppTesters(packageName: String, realTestersCount: Int, completedDays: Int? = null) {
        val safeTesters = realTestersCount.coerceIn(0, 50)
        val currentList = appDao.getAllUserApps().firstOrNull() ?: emptyList()
        val currentApp = currentList.find { it.packageName == packageName }
        val days = (completedDays ?: currentApp?.completedDays ?: 1).coerceIn(1, 14)
        val isCompleted = safeTesters >= 20 && days >= 14
        val status = if (isCompleted) "READY_FOR_PRODUCTION" else "TESTING"

        appDao.updateAppProgress(packageName, safeTesters, days, status)
        try {
            firestoreService.updateAppTestersInFirestore(packageName, safeTesters)
        } catch (_: Exception) {}

        // Keep test logs in sync with the real tester count
        if (safeTesters > 0) {
            val count = safeTesters.coerceAtMost(20)
            val logs = (1..count).map { testerIndex ->
                DailyTestLog(
                    id = "log_${packageName}_${days}_$testerIndex",
                    appId = packageName,
                    testerName = "مختبر معتمد #$testerIndex",
                    testerEmail = "tester$testerIndex@playtesterhub.com",
                    deviceModel = listOf("Pixel 8", "Galaxy S23", "Xiaomi 13", "OnePlus 11")[testerIndex % 4],
                    osVersion = "Android 14 (API 34)",
                    dayNumber = days,
                    checkInTimestamp = System.currentTimeMillis() - ((count - testerIndex) * 3600000L),
                    sessionDurationMinutes = 15,
                    notes = "جلسة اختبار معتمدة ونشطة في Google Play"
                )
            }
            appDao.insertDailyTestLogs(logs)
        }
    }

    suspend fun removeUserApp(app: UserApp) {
        appDao.deleteUserApp(app)
    }

    suspend fun updateAppIcon(packageName: String, newIconUrl: String) {
        appDao.updateUserAppIcon(packageName, newIconUrl)
        try {
            firestoreService.updateAppIconInFirestore(packageName, newIconUrl)
        } catch (_: Exception) {}
    }

    suspend fun updateAppDetails(
        packageName: String,
        name: String,
        category: String,
        iconUrl: String,
        webOptInUrl: String,
        googleGroupUrl: String
    ) {
        appDao.updateUserAppDetails(packageName, name, category, iconUrl, webOptInUrl, googleGroupUrl)
        try {
            firestoreService.updateAppDetailsInFirestore(packageName, name, category, iconUrl, webOptInUrl, googleGroupUrl)
        } catch (_: Exception) {}
    }

    suspend fun joinTest(test: JoinedTest) {
        val current = appDao.getAllJoinedTests().firstOrNull() ?: emptyList()
        if (current.any { it.packageName == test.packageName }) {
            return
        }
        appDao.insertJoinedTest(test)
        try {
            firestoreService.incrementAppTesters(test.packageName)
            appDao.incrementUserAppTesters(test.packageName)
        } catch (_: Exception) {}
    }

    fun getCrashReports(appId: String): Flow<List<CrashReport>> {
        return appDao.getCrashReports(appId)
    }

    suspend fun resolveCrashReport(id: String, note: String) {
        appDao.updateCrashReportStatus(id, "RESOLVED", System.currentTimeMillis(), note)
    }

    fun getDailyTestLogs(appId: String): Flow<List<DailyTestLog>> {
        return appDao.getDailyTestLogs(appId)
    }

    fun getFeedback(appId: String): Flow<List<TesterFeedback>> {
        return appDao.getFeedback(appId)
    }

    suspend fun promoteApp(packageName: String, isFeatured: Boolean = true) {
        appDao.updateUserAppFeatured(packageName, isFeatured)
        appDao.updateCommunityAppFeatured(packageName, isFeatured)
        try {
            firestoreService.setAppFeaturedInFirestore(packageName, isFeatured)
        } catch (_: Exception) {}
    }

    suspend fun recordDailyAppTest(packageName: String): Int {
        val tests = appDao.getAllJoinedTests().firstOrNull() ?: emptyList()
        val currentTest = tests.find { it.packageName == packageName } ?: return 0
        val newDays = (currentTest.daysCompleted + 1).coerceAtMost(14)
        val now = System.currentTimeMillis()
        appDao.updateJoinedTestSession(packageName, newDays, now)

        val log = DailyTestLog(
            id = "log_${packageName}_${newDays}_${System.currentTimeMillis()}",
            appId = packageName,
            testerName = "المطور المعتمد",
            testerEmail = "cemoabdou@gmail.com",
            deviceModel = android.os.Build.MODEL ?: "Android Device",
            osVersion = "Android ${android.os.Build.VERSION.RELEASE}",
            dayNumber = newDays,
            checkInTimestamp = now,
            sessionDurationMinutes = 15,
            notes = "جلسة اختبار يومية نشطة (+25 عملة)"
        )
        appDao.insertDailyTestLogs(listOf(log))
        return newDays
    }

    suspend fun addDailyTestLog(log: DailyTestLog) {
        appDao.insertDailyTestLogs(listOf(log))
    }

    suspend fun updateTestSession(packageName: String, daysCompleted: Int) {
        appDao.updateJoinedTestSession(packageName, daysCompleted, System.currentTimeMillis())
    }

    suspend fun addFeedback(appId: String, rating: Int, comment: String) {
        val item = TesterFeedback(
            id = "fb_${appId}_${System.currentTimeMillis()}",
            appId = appId,
            testerName = "مختبر معتمد",
            rating = rating,
            comment = comment,
            createdAt = System.currentTimeMillis()
        )
        appDao.insertFeedback(listOf(item))
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
}
