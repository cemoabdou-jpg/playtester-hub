package com.example.data.remote

import android.util.Log
import com.example.data.model.*
import com.example.ui.util.L
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlin.math.abs

class CloudSyncService {
    private val firestore: FirebaseFirestore? by lazy {
        try {
            FirebaseFirestore.getInstance()
        } catch (e: Throwable) {
            Log.e("CloudSyncService", "FirebaseFirestore.getInstance() failed: ${e.message}")
            null
        }
    }

    companion object {
        fun emailKey(email: String): String {
            return email.trim().lowercase()
                .replace(".", "_dot_")
                .replace("@", "_at_")
                .replace("/", "_")
        }

        fun generateReferralCode(email: String): String {
            val clean = email.trim().lowercase()
            if (clean.isBlank()) return "PTH-000000"
            val hash = abs(clean.hashCode()).toString(36).uppercase().padStart(6, '0').take(6)
            return "PTH-$hash"
        }
    }

    // =========================================================================
    // 1. User Profile & Referral Cloud Sync (`users/{emailKey}`)
    // =========================================================================

    suspend fun fetchOrCreateUserProfile(
        email: String,
        displayName: String,
        defaultUid: String,
        isOwner: Boolean
    ): CloudUserProfile? {
        val db = firestore ?: return null
        val cleanEmail = email.trim().lowercase()
        if (cleanEmail.isBlank()) return null

        val key = emailKey(cleanEmail)
        val docRef = db.collection("users").document(key)

        return try {
            val snapshot = docRef.get().await()
            if (snapshot.exists()) {
                val existingCode = snapshot.getString("referralCode").orEmpty()
                    .ifBlank { generateReferralCode(cleanEmail) }
                if (snapshot.getString("referralCode").isNullOrBlank()) {
                    docRef.set(mapOf("referralCode" to existingCode), SetOptions.merge()).await()
                }
                if (displayName.isNotBlank()) {
                    docRef.set(
                        mapOf("displayName" to displayName, "lastSeenAt" to System.currentTimeMillis()),
                        SetOptions.merge()
                    )
                }
                parseCloudProfile(snapshot.data ?: emptyMap(), cleanEmail, defaultUid, isOwner, existingCode)
            } else {
                val code = generateReferralCode(cleanEmail)
                val initialPoints = if (isOwner) 9999 else 0
                val data = hashMapOf<String, Any>(
                    "uid" to defaultUid,
                    "email" to cleanEmail,
                    "displayName" to displayName,
                    "points" to initialPoints,
                    "streakDays" to 1,
                    "lastCheckInDate" to "",
                    "lastDailyBonusDate" to "",
                    "claimedBadgeIds" to emptyList<String>(),
                    "appsCount" to 0,
                    "testsCount" to 0,
                    "referralCode" to code,
                    "referredBy" to "",
                    "invitedDevsCount" to 0,
                    "isVip" to isOwner,
                    "createdAt" to System.currentTimeMillis(),
                    "lastSeenAt" to System.currentTimeMillis()
                )
                docRef.set(data, SetOptions.merge()).await()
                CloudUserProfile(
                    uid = defaultUid,
                    email = cleanEmail,
                    displayName = displayName,
                    points = initialPoints,
                    streakDays = 1,
                    referralCode = code,
                    isVip = isOwner
                )
            }
        } catch (e: Throwable) {
            Log.e("CloudSyncService", "fetchOrCreateUserProfile failed: ${e.message}")
            null
        }
    }

    fun getUserProfileFlow(email: String): Flow<CloudUserProfile?> = callbackFlow {
        val db = firestore
        val cleanEmail = email.trim().lowercase()
        if (db == null || cleanEmail.isBlank()) {
            trySend(null)
            awaitClose {}
            return@callbackFlow
        }
        val key = emailKey(cleanEmail)
        var listener: ListenerRegistration? = null
        try {
            listener = db.collection("users").document(key)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("CloudSyncService", "getUserProfileFlow error: ${error.message}")
                        return@addSnapshotListener
                    }
                    val data = snapshot?.data
                    if (snapshot != null && snapshot.exists() && data != null) {
                        val isOwner = cleanEmail.equals(PLATFORM_OWNER_EMAIL, ignoreCase = true)
                        trySend(
                            parseCloudProfile(
                                data = data,
                                email = cleanEmail,
                                fallbackUid = "user_${cleanEmail.hashCode()}",
                                isOwner = isOwner,
                                fallbackReferralCode = generateReferralCode(cleanEmail)
                            )
                        )
                    }
                }
        } catch (e: Throwable) {
            Log.e("CloudSyncService", "Failed profile listener: ${e.message}")
        }
        awaitClose { try { listener?.remove() } catch (_: Throwable) {} }
    }

    suspend fun updateUserProfileFields(email: String, fields: Map<String, Any>) {
        val db = firestore ?: return
        val cleanEmail = email.trim().lowercase()
        if (cleanEmail.isBlank()) return
        try {
            db.collection("users").document(emailKey(cleanEmail))
                .set(fields + ("lastSeenAt" to System.currentTimeMillis()), SetOptions.merge())
                .await()
        } catch (e: Throwable) {
            Log.e("CloudSyncService", "updateUserProfileFields failed: ${e.message}")
        }
    }

    suspend fun incrementUserPoints(email: String, delta: Int) {
        val db = firestore ?: return
        val cleanEmail = email.trim().lowercase()
        if (cleanEmail.isBlank() || delta == 0) return
        try {
            db.collection("users").document(emailKey(cleanEmail))
                .set(
                    mapOf(
                        "points" to FieldValue.increment(delta.toLong()),
                        "lastSeenAt" to System.currentTimeMillis()
                    ),
                    SetOptions.merge()
                ).await()
        } catch (e: Throwable) {
            Log.e("CloudSyncService", "incrementUserPoints failed: ${e.message}")
        }
    }

    suspend fun redeemReferralCode(
        currentUserEmail: String,
        currentUserName: String,
        rawCode: String
    ): Result<Int> {
        val db = firestore ?: return Result.failure(IllegalStateException("Offline"))
        val cleanMe = currentUserEmail.trim().lowercase()
        val normalizedCode = rawCode.trim().uppercase()
        if (cleanMe.isBlank()) return Result.failure(IllegalArgumentException(L.t("يجب تسجيل الدخول أولاً", "Please sign in first")))
        if (normalizedCode.isBlank()) return Result.failure(IllegalArgumentException(L.t("يرجى إدخال رمز الدعوة", "Please enter a referral code")))

        val myDocRef = db.collection("users").document(emailKey(cleanMe))
        return try {
            val mySnap = myDocRef.get().await()
            val myOwnCode = mySnap.getString("referralCode")?.uppercase() ?: generateReferralCode(cleanMe)
            val alreadyReferredBy = mySnap.getString("referredBy").orEmpty()

            if (alreadyReferredBy.isNotBlank()) {
                return Result.failure(IllegalStateException(L.t("تم استخدام رمز دعوة لهذا الحساب مسبقاً", "A referral code has already been redeemed for this account")))
            }
            if (normalizedCode == myOwnCode) {
                return Result.failure(IllegalArgumentException(L.t("لا يمكنك استخدام رمز الدعوة الخاص بك", "You cannot redeem your own referral code")))
            }

            val querySnap = db.collection("users")
                .whereEqualTo("referralCode", normalizedCode)
                .limit(1)
                .get()
                .await()

            val referrerDoc = querySnap.documents.firstOrNull()
                ?: return Result.failure(IllegalArgumentException(L.t("رمز الدعوة غير صحيح أو غير موجود", "Referral code not found")))

            val referrerEmail = referrerDoc.getString("email").orEmpty().trim().lowercase()
            if (referrerEmail == cleanMe) {
                return Result.failure(IllegalArgumentException(L.t("لا يمكنك استخدام رمز الدعوة الخاص بك", "Cannot redeem your own code")))
            }

            // Award +100 to referrer and +50 welcome bonus to invitee
            referrerDoc.reference.update(
                mapOf(
                    "invitedDevsCount" to FieldValue.increment(1),
                    "points" to FieldValue.increment(100)
                )
            ).await()

            myDocRef.set(
                mapOf(
                    "referredBy" to referrerEmail,
                    "points" to FieldValue.increment(50)
                ),
                SetOptions.merge()
            ).await()

            // Notify referrer in real time
            if (referrerEmail.isNotBlank()) {
                sendNotificationToUser(
                    targetEmail = referrerEmail,
                    notification = TesterNotification(
                        id = "notif_ref_${System.currentTimeMillis()}",
                        testerName = currentUserName.ifBlank { cleanMe.substringBefore("@") },
                        testerEmail = cleanMe,
                        testerBadge = L.t("دعوة مطور جديدة", "New Dev Referral"),
                        appName = L.t("شبكة المطورين", "Developer Network"),
                        appPackageName = "",
                        timestamp = System.currentTimeMillis(),
                        actionType = "REFERRAL",
                        deviceModel = android.os.Build.MODEL ?: "Android",
                        isRead = false,
                        message = L.t(
                            "انضم عبر رابط دعوتك! حصلت على +100 عملة مكافأة إحالة.",
                            "Joined using your referral code! You earned +100 referral bonus coins."
                        )
                    )
                )
            }

            Result.success(50)
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    private fun parseCloudProfile(
        data: Map<String, Any>,
        email: String,
        fallbackUid: String,
        isOwner: Boolean,
        fallbackReferralCode: String
    ): CloudUserProfile {
        val rawClaimed = (data["claimedBadgeIds"] as? List<*>)?.mapNotNull { it?.toString() } ?: emptyList()
        return CloudUserProfile(
            uid = (data["uid"] as? String)?.ifBlank { fallbackUid } ?: fallbackUid,
            email = email,
            displayName = (data["displayName"] as? String).orEmpty(),
            points = ((data["points"] as? Number)?.toInt() ?: 0).let { if (isOwner) maxOf(it, 9999) else it },
            streakDays = (data["streakDays"] as? Number)?.toInt() ?: 1,
            lastCheckInDate = (data["lastCheckInDate"] as? String).orEmpty(),
            lastDailyBonusDate = (data["lastDailyBonusDate"] as? String).orEmpty(),
            claimedBadgeIds = rawClaimed,
            appsCount = (data["appsCount"] as? Number)?.toInt() ?: 0,
            testsCount = (data["testsCount"] as? Number)?.toInt() ?: 0,
            referralCode = (data["referralCode"] as? String)?.ifBlank { fallbackReferralCode } ?: fallbackReferralCode,
            referredBy = (data["referredBy"] as? String).orEmpty(),
            invitedDevsCount = (data["invitedDevsCount"] as? Number)?.toInt() ?: 0,
            isVip = (data["isVip"] as? Boolean) ?: isOwner
        )
    }

    // =========================================================================
    // 2. Cross-User Notifications (`users/{emailKey}/notifications`)
    // =========================================================================

    suspend fun sendNotificationToUser(targetEmail: String, notification: TesterNotification) {
        val db = firestore ?: return
        val cleanTarget = targetEmail.trim().lowercase()
        if (cleanTarget.isBlank()) return
        try {
            val data = hashMapOf(
                "id" to notification.id,
                "testerName" to notification.testerName,
                "testerEmail" to notification.testerEmail,
                "testerBadge" to notification.testerBadge,
                "appName" to notification.appName,
                "appPackageName" to notification.appPackageName,
                "timestamp" to notification.timestamp,
                "actionType" to notification.actionType,
                "deviceModel" to notification.deviceModel,
                "isRead" to false,
                "message" to notification.message
            )
            db.collection("users").document(emailKey(cleanTarget))
                .collection("notifications").document(notification.id)
                .set(data).await()
        } catch (e: Throwable) {
            Log.e("CloudSyncService", "sendNotificationToUser failed: ${e.message}")
        }
    }

    fun getUserNotificationsFlow(userEmail: String): Flow<List<TesterNotification>> = callbackFlow {
        val db = firestore
        val cleanEmail = userEmail.trim().lowercase()
        if (db == null || cleanEmail.isBlank()) {
            trySend(emptyList())
            awaitClose {}
            return@callbackFlow
        }
        var listener: ListenerRegistration? = null
        try {
            listener = db.collection("users").document(emailKey(cleanEmail))
                .collection("notifications")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(50)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("CloudSyncService", "notifications error: ${error.message}")
                        return@addSnapshotListener
                    }
                    val list = snapshot?.documents?.mapNotNull { doc ->
                        try {
                            TesterNotification(
                                id = doc.id,
                                testerName = doc.getString("testerName") ?: L.t("مختبر معتمد", "Verified Tester"),
                                testerEmail = doc.getString("testerEmail") ?: "",
                                testerBadge = doc.getString("testerBadge") ?: L.t("مختبر معتمد", "Verified Tester"),
                                appName = doc.getString("appName") ?: "",
                                appPackageName = doc.getString("appPackageName") ?: "",
                                timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis(),
                                actionType = doc.getString("actionType") ?: "TESTED",
                                deviceModel = doc.getString("deviceModel") ?: "Android",
                                isRead = doc.getBoolean("isRead") ?: false,
                                message = doc.getString("message") ?: ""
                            )
                        } catch (_: Exception) { null }
                    } ?: emptyList()
                    trySend(list)
                }
        } catch (e: Throwable) {
            Log.e("CloudSyncService", "notifications listener failed: ${e.message}")
            trySend(emptyList())
        }
        awaitClose { try { listener?.remove() } catch (_: Throwable) {} }
    }

    suspend fun markNotificationRead(userEmail: String, notificationId: String) {
        val db = firestore ?: return
        val cleanEmail = userEmail.trim().lowercase()
        if (cleanEmail.isBlank()) return
        try {
            db.collection("users").document(emailKey(cleanEmail))
                .collection("notifications").document(notificationId)
                .update("isRead", true).await()
        } catch (_: Throwable) {}
    }

    suspend fun markAllNotificationsRead(userEmail: String, unreadIds: List<String>) {
        val db = firestore ?: return
        val cleanEmail = userEmail.trim().lowercase()
        if (cleanEmail.isBlank() || unreadIds.isEmpty()) return
        try {
            val batch = db.batch()
            val coll = db.collection("users").document(emailKey(cleanEmail)).collection("notifications")
            unreadIds.forEach { id ->
                batch.update(coll.document(id), "isRead", true)
            }
            batch.commit().await()
        } catch (_: Throwable) {}
    }

    // =========================================================================
    // 3. Cloud Sync for Joined Tests & User Apps
    // =========================================================================

    suspend fun saveJoinedTestToCloud(userEmail: String, test: JoinedTest) {
        val db = firestore ?: return
        val cleanEmail = userEmail.trim().lowercase()
        if (cleanEmail.isBlank()) return
        try {
            val data = hashMapOf(
                "id" to test.id,
                "appId" to test.appId,
                "appName" to test.appName,
                "packageName" to test.packageName,
                "iconUrl" to test.iconUrl,
                "joinedAt" to test.joinedAt,
                "daysCompleted" to test.daysCompleted,
                "lastTestDate" to test.lastTestDate,
                "status" to test.status
            )
            db.collection("users").document(emailKey(cleanEmail))
                .collection("joined_tests").document(test.packageName)
                .set(data, SetOptions.merge()).await()
        } catch (_: Throwable) {}
    }

    suspend fun fetchUserJoinedTests(userEmail: String): List<JoinedTest> {
        val db = firestore ?: return emptyList()
        val cleanEmail = userEmail.trim().lowercase()
        if (cleanEmail.isBlank()) return emptyList()
        return try {
            val snap = db.collection("users").document(emailKey(cleanEmail))
                .collection("joined_tests").get().await()
            snap.documents.mapNotNull { doc ->
                try {
                    JoinedTest(
                        id = doc.getString("id") ?: "joined_${doc.id}",
                        appId = doc.getString("appId") ?: doc.id,
                        appName = doc.getString("appName") ?: doc.id,
                        packageName = doc.getString("packageName") ?: doc.id,
                        iconUrl = doc.getString("iconUrl") ?: "",
                        joinedAt = doc.getLong("joinedAt") ?: System.currentTimeMillis(),
                        daysCompleted = (doc.getLong("daysCompleted") ?: 0L).toInt(),
                        lastTestDate = doc.getLong("lastTestDate") ?: 0L,
                        status = doc.getString("status") ?: "TESTING"
                    )
                } catch (_: Exception) { null }
            }
        } catch (_: Throwable) { emptyList() }
    }

    suspend fun saveUserAppToCloud(userEmail: String, app: UserApp) {
        val db = firestore ?: return
        val cleanEmail = userEmail.trim().lowercase()
        if (cleanEmail.isBlank()) return
        try {
            val data = hashMapOf(
                "id" to app.id,
                "name" to app.name,
                "packageName" to app.packageName,
                "iconUrl" to app.iconUrl,
                "playStoreUrl" to app.playStoreUrl,
                "webOptInUrl" to app.webOptInUrl,
                "androidOptInUrl" to app.androidOptInUrl,
                "googleGroupUrl" to app.googleGroupUrl,
                "targetTesters" to app.targetTesters,
                "currentTesters" to app.currentTesters,
                "targetDays" to app.targetDays,
                "completedDays" to app.completedDays,
                "status" to app.status,
                "isVip" to app.isVip,
                "createdAt" to app.createdAt,
                "appCategory" to app.appCategory,
                "description" to app.description
            )
            db.collection("users").document(emailKey(cleanEmail))
                .collection("user_apps").document(app.packageName)
                .set(data, SetOptions.merge()).await()
        } catch (_: Throwable) {}
    }

    suspend fun fetchUserAppsFromCloud(userEmail: String): List<UserApp> {
        val db = firestore ?: return emptyList()
        val cleanEmail = userEmail.trim().lowercase()
        if (cleanEmail.isBlank()) return emptyList()
        return try {
            val snap = db.collection("users").document(emailKey(cleanEmail))
                .collection("user_apps").get().await()
            snap.documents.mapNotNull { doc ->
                try {
                    UserApp(
                        id = doc.getString("id") ?: doc.id,
                        name = doc.getString("name") ?: doc.id,
                        packageName = doc.getString("packageName") ?: doc.id,
                        iconUrl = doc.getString("iconUrl") ?: "",
                        playStoreUrl = doc.getString("playStoreUrl") ?: "",
                        webOptInUrl = doc.getString("webOptInUrl") ?: "",
                        androidOptInUrl = doc.getString("androidOptInUrl") ?: "",
                        googleGroupUrl = doc.getString("googleGroupUrl") ?: "",
                        targetTesters = (doc.getLong("targetTesters") ?: 20L).toInt(),
                        currentTesters = (doc.getLong("currentTesters") ?: 0L).toInt(),
                        targetDays = (doc.getLong("targetDays") ?: 14L).toInt(),
                        completedDays = (doc.getLong("completedDays") ?: 1L).toInt(),
                        status = doc.getString("status") ?: "TESTING",
                        isVip = doc.getBoolean("isVip") ?: false,
                        createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis(),
                        appCategory = doc.getString("appCategory") ?: "Productivity",
                        description = doc.getString("description") ?: ""
                    )
                } catch (_: Exception) { null }
            }
        } catch (_: Throwable) { emptyList() }
    }

    suspend fun deleteUserAppFromCloud(userEmail: String, packageName: String) {
        val db = firestore ?: return
        val cleanEmail = userEmail.trim().lowercase()
        if (cleanEmail.isBlank()) return
        try {
            db.collection("users").document(emailKey(cleanEmail))
                .collection("user_apps").document(packageName)
                .delete().await()
        } catch (_: Throwable) {}
    }

    // =========================================================================
    // 4. Shared App Crashes, Feedback & Daily Test Logs
    // =========================================================================

    suspend fun submitCrashReport(packageName: String, report: CrashReport) {
        val db = firestore ?: return
        try {
            val data = hashMapOf(
                "id" to report.id,
                "appId" to packageName,
                "title" to report.title,
                "stackTrace" to report.stackTrace,
                "deviceModel" to report.deviceModel,
                "osVersion" to report.osVersion,
                "appVersion" to report.appVersion,
                "occurrences" to report.occurrences,
                "status" to report.status,
                "reportedAt" to report.reportedAt,
                "resolvedAt" to report.resolvedAt,
                "resolutionNote" to report.resolutionNote
            )
            db.collection("app_crashes").document(packageName)
                .collection("reports").document(report.id)
                .set(data, SetOptions.merge()).await()
        } catch (_: Throwable) {}
    }

    fun getCrashReportsFlow(packageName: String): Flow<List<CrashReport>> = callbackFlow {
        val db = firestore
        if (db == null || packageName.isBlank()) {
            trySend(emptyList())
            awaitClose {}
            return@callbackFlow
        }
        var listener: ListenerRegistration? = null
        try {
            listener = db.collection("app_crashes").document(packageName)
                .collection("reports")
                .orderBy("reportedAt", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, _ ->
                    val list = snapshot?.documents?.mapNotNull { doc ->
                        try {
                            CrashReport(
                                id = doc.id,
                                appId = doc.getString("appId") ?: packageName,
                                title = doc.getString("title") ?: "",
                                stackTrace = doc.getString("stackTrace") ?: "",
                                deviceModel = doc.getString("deviceModel") ?: "Android",
                                osVersion = doc.getString("osVersion") ?: "Android",
                                appVersion = doc.getString("appVersion") ?: "1.0",
                                occurrences = (doc.getLong("occurrences") ?: 1L).toInt(),
                                status = doc.getString("status") ?: "OPEN",
                                reportedAt = doc.getLong("reportedAt") ?: System.currentTimeMillis(),
                                resolvedAt = doc.getLong("resolvedAt"),
                                resolutionNote = doc.getString("resolutionNote") ?: ""
                            )
                        } catch (_: Exception) { null }
                    } ?: emptyList()
                    trySend(list)
                }
        } catch (_: Throwable) {
            trySend(emptyList())
        }
        awaitClose { try { listener?.remove() } catch (_: Throwable) {} }
    }

    suspend fun resolveCrashReport(packageName: String, reportId: String, note: String) {
        val db = firestore ?: return
        try {
            db.collection("app_crashes").document(packageName)
                .collection("reports").document(reportId)
                .update(
                    mapOf(
                        "status" to "RESOLVED",
                        "resolvedAt" to System.currentTimeMillis(),
                        "resolutionNote" to note
                    )
                ).await()
        } catch (_: Throwable) {}
    }

    suspend fun submitFeedback(packageName: String, feedback: TesterFeedback) {
        val db = firestore ?: return
        try {
            val data = hashMapOf(
                "id" to feedback.id,
                "appId" to packageName,
                "testerName" to feedback.testerName,
                "rating" to feedback.rating,
                "comment" to feedback.comment,
                "category" to feedback.category,
                "createdAt" to feedback.createdAt
            )
            db.collection("app_feedback").document(packageName)
                .collection("reviews").document(feedback.id)
                .set(data, SetOptions.merge()).await()
        } catch (_: Throwable) {}
    }

    fun getFeedbackFlow(packageName: String): Flow<List<TesterFeedback>> = callbackFlow {
        val db = firestore
        if (db == null || packageName.isBlank()) {
            trySend(emptyList())
            awaitClose {}
            return@callbackFlow
        }
        var listener: ListenerRegistration? = null
        try {
            listener = db.collection("app_feedback").document(packageName)
                .collection("reviews")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, _ ->
                    val list = snapshot?.documents?.mapNotNull { doc ->
                        try {
                            TesterFeedback(
                                id = doc.id,
                                appId = doc.getString("appId") ?: packageName,
                                testerName = doc.getString("testerName") ?: L.t("مختبر معتمد", "Verified Tester"),
                                rating = (doc.getLong("rating") ?: 5L).toInt(),
                                comment = doc.getString("comment") ?: "",
                                category = doc.getString("category") ?: "UX",
                                createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
                            )
                        } catch (_: Exception) { null }
                    } ?: emptyList()
                    trySend(list)
                }
        } catch (_: Throwable) {
            trySend(emptyList())
        }
        awaitClose { try { listener?.remove() } catch (_: Throwable) {} }
    }

    suspend fun addDailyTestLog(packageName: String, log: DailyTestLog) {
        val db = firestore ?: return
        try {
            val data = hashMapOf(
                "id" to log.id,
                "appId" to packageName,
                "testerName" to log.testerName,
                "testerEmail" to log.testerEmail,
                "deviceModel" to log.deviceModel,
                "osVersion" to log.osVersion,
                "dayNumber" to log.dayNumber,
                "checkInTimestamp" to log.checkInTimestamp,
                "sessionDurationMinutes" to log.sessionDurationMinutes,
                "notes" to log.notes
            )
            db.collection("daily_test_logs").document(packageName)
                .collection("logs").document(log.id)
                .set(data, SetOptions.merge()).await()
        } catch (_: Throwable) {}
    }

    fun getDailyTestLogsFlow(packageName: String): Flow<List<DailyTestLog>> = callbackFlow {
        val db = firestore
        if (db == null || packageName.isBlank()) {
            trySend(emptyList())
            awaitClose {}
            return@callbackFlow
        }
        var listener: ListenerRegistration? = null
        try {
            listener = db.collection("daily_test_logs").document(packageName)
                .collection("logs")
                .orderBy("checkInTimestamp", Query.Direction.DESCENDING)
                .limit(100)
                .addSnapshotListener { snapshot, _ ->
                    val list = snapshot?.documents?.mapNotNull { doc ->
                        try {
                            DailyTestLog(
                                id = doc.id,
                                appId = doc.getString("appId") ?: packageName,
                                testerName = doc.getString("testerName") ?: L.t("مختبر معتمد", "Verified Tester"),
                                testerEmail = doc.getString("testerEmail") ?: "",
                                deviceModel = doc.getString("deviceModel") ?: "Android",
                                osVersion = doc.getString("osVersion") ?: "Android",
                                dayNumber = (doc.getLong("dayNumber") ?: 1L).toInt(),
                                checkInTimestamp = doc.getLong("checkInTimestamp") ?: System.currentTimeMillis(),
                                sessionDurationMinutes = (doc.getLong("sessionDurationMinutes") ?: 15L).toInt(),
                                notes = doc.getString("notes") ?: ""
                            )
                        } catch (_: Exception) { null }
                    } ?: emptyList()
                    trySend(list)
                }
        } catch (_: Throwable) {
            trySend(emptyList())
        }
        awaitClose { try { listener?.remove() } catch (_: Throwable) {} }
    }
}
