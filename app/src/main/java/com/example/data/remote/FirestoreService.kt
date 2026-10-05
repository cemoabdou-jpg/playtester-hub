package com.example.data.remote

import android.util.Log
import com.example.data.model.CloudDiagnosticResult
import com.example.data.model.CommunityApp
import com.example.data.model.DailyTestLog
import com.example.data.model.TesterFeedback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreService {

    private fun getFirestore(): FirebaseFirestore? {
        return try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.w("FirestoreService", "Firebase Firestore default init note: ${e.message}")
            try {
                val app = try {
                    com.google.firebase.FirebaseApp.getInstance()
                } catch (ex: Exception) {
                    val context = com.example.PlayTesterApplication.instance
                    val options = com.google.firebase.FirebaseOptions.Builder()
                        .setProjectId("playtester-hub-61bd6")
                        .setApplicationId("1:170455936881:android:29bcec6a7e7d0f03c410d0")
                        .setApiKey("AIzaSyCAjx_Z-syoM_CfDnlQqzdgYcAvmqe7Y0o")
                        .setStorageBucket("playtester-hub-61bd6.firebasestorage.app")
                        .build()
                    com.google.firebase.FirebaseApp.initializeApp(context, options)
                }
                FirebaseFirestore.getInstance(app)
            } catch (e2: Exception) {
                Log.e("FirestoreService", "Firebase Firestore fallback init error: ${e2.message}")
                null
            }
        }
    }

    suspend fun ensureFirebaseAuth(): Result<String> {
        return try {
            val auth = try {
                FirebaseAuth.getInstance()
            } catch (e: Exception) {
                val app = com.google.firebase.FirebaseApp.getInstance()
                FirebaseAuth.getInstance(app)
            }
            val current = auth.currentUser
            if (current != null) {
                Result.success(current.uid)
            } else {
                val res = auth.signInAnonymously().await()
                val uid = res.user?.uid ?: "anonymous_user"
                Log.d("FirestoreService", "Signed in anonymously to Firebase: $uid")
                Result.success(uid)
            }
        } catch (e: Exception) {
            Log.w("FirestoreService", "Firebase Anonymous Auth note: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun publishAppToFirestore(app: CommunityApp): Result<Boolean> {
        val db = getFirestore() ?: return Result.failure(Exception("خدمة Firestore غير مهيأة أو غير متوفرة."))
        return try {
            ensureFirebaseAuth()
            val docData = hashMapOf(
                "name" to app.name,
                "developerName" to app.developerName,
                "packageName" to app.packageName,
                "description" to app.description,
                "category" to app.category,
                "iconUrl" to app.iconUrl,
                "googleGroupUrl" to app.googleGroupUrl,
                "webOptInUrl" to app.webOptInUrl,
                "androidOptInUrl" to app.androidOptInUrl,
                "testersCurrent" to app.testersCurrent,
                "testersTarget" to app.testersTarget,
                "rewardPoints" to app.rewardPoints,
                "isFeatured" to app.isFeatured,
                "priorityScore" to app.priorityScore,
                "isHidden" to app.isHidden,
                "updatedAt" to System.currentTimeMillis()
            )
            val docRef = if (app.packageName.isNotBlank()) {
                db.collection("community_apps").document(app.packageName)
            } else {
                db.collection("community_apps").document()
            }
            docRef.set(docData).await()
            Result.success(true)
        } catch (e: FirebaseFirestoreException) {
            Log.e("FirestoreService", "Firestore exception publishing app: ${e.code} - ${e.message}")
            val msg = when (e.code) {
                FirebaseFirestoreException.Code.PERMISSION_DENIED ->
                    "خطأ صلاحيات السيرفر (PERMISSION_DENIED): تأكد من تفعيل صلاحيات القراءة والكتابة في Firebase Firestore Rules وتفعيل Anonymous Auth في لوحة تحكم Firebase."
                FirebaseFirestoreException.Code.UNAVAILABLE ->
                    "تعذر الاتصال بسيرفر فايربيس: تأكد من اتصال الإنترنت."
                else -> "خطأ في السيرفر السحابي (${e.code}): ${e.message}"
            }
            Result.failure(Exception(msg))
        } catch (e: Exception) {
            Log.e("FirestoreService", "Error publishing app to Firestore: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun publishAllAppsToFirestore(apps: List<CommunityApp>): Result<Pair<Int, Int>> {
        var successCount = 0
        var failCount = 0
        for (app in apps) {
            val res = publishAppToFirestore(app)
            if (res.isSuccess) {
                successCount++
            } else {
                failCount++
            }
        }
        return Result.success(Pair(successCount, failCount))
    }

    suspend fun fetchCommunityAppsOnce(): Result<List<CommunityApp>> {
        val db = getFirestore() ?: return Result.failure(Exception("قاعدة بيانات Firestore غير متوفرة."))
        return try {
            ensureFirebaseAuth()
            val snapshot = db.collection("community_apps").get().await()
            val apps = snapshot.documents.mapNotNull { doc ->
                try {
                    val pkg = doc.getString("packageName")?.takeIf { it.isNotBlank() } ?: doc.id
                    CommunityApp(
                        name = doc.getString("name") ?: "تطبيق",
                        developerName = doc.getString("developerName") ?: "مطور معتمد",
                        packageName = pkg,
                        description = doc.getString("description") ?: "",
                        category = doc.getString("category") ?: "تطبيق",
                        iconUrl = doc.getString("iconUrl") ?: "",
                        googleGroupUrl = doc.getString("googleGroupUrl") ?: "",
                        webOptInUrl = doc.getString("webOptInUrl") ?: "",
                        androidOptInUrl = doc.getString("androidOptInUrl") ?: "",
                        testersCurrent = (doc.getLong("testersCurrent") ?: 0L).toInt(),
                        testersTarget = (doc.getLong("testersTarget") ?: 20L).toInt(),
                        rewardPoints = (doc.getLong("rewardPoints") ?: 40L).toInt(),
                        isFeatured = doc.getBoolean("isFeatured") ?: false,
                        priorityScore = (doc.getLong("priorityScore") ?: 0L).toInt(),
                        isHidden = doc.getBoolean("isHidden") ?: false
                    )
                } catch (e: Exception) {
                    null
                }
            }
            Result.success(apps)
        } catch (e: FirebaseFirestoreException) {
            Log.e("FirestoreService", "Firestore error fetching apps: ${e.code} - ${e.message}")
            val msg = when (e.code) {
                FirebaseFirestoreException.Code.PERMISSION_DENIED ->
                    "خطأ في الصلاحيات (PERMISSION_DENIED): قاعدة بيانات Firebase تمنع قراءة التطبيقات. يرجى تفعيل قواعد القراءة (Rules) وتفعيل Anonymous Auth في Firebase Console."
                FirebaseFirestoreException.Code.UNAVAILABLE ->
                    "تعذر الاتصال بالسيرفر السحابي (UNAVAILABLE): يرجى التأكد من اتصال الإنترنت."
                else -> "فشل جلب التطبيقات من السيرفر السحابي (${e.code})"
            }
            Result.failure(Exception(msg))
        } catch (e: Exception) {
            Log.e("FirestoreService", "Error fetching remote apps: ${e.message}")
            Result.failure(e)
        }
    }

    fun observeRemoteCommunityApps(): Flow<List<CommunityApp>> = callbackFlow {
        val db = getFirestore()
        if (db == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listenerRegistration = db.collection("community_apps")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("FirestoreService", "Listen error: ${error.message}")
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val apps = snapshot.documents.mapNotNull { doc ->
                        try {
                            val pkg = doc.getString("packageName")?.takeIf { it.isNotBlank() } ?: doc.id
                            CommunityApp(
                                name = doc.getString("name") ?: "",
                                developerName = doc.getString("developerName") ?: "مطور معتمد",
                                packageName = pkg,
                                description = doc.getString("description") ?: "",
                                category = doc.getString("category") ?: "تطبيق",
                                iconUrl = doc.getString("iconUrl") ?: "",
                                googleGroupUrl = doc.getString("googleGroupUrl") ?: "",
                                webOptInUrl = doc.getString("webOptInUrl") ?: "",
                                androidOptInUrl = doc.getString("androidOptInUrl") ?: "",
                                testersCurrent = (doc.getLong("testersCurrent") ?: 0L).toInt(),
                                testersTarget = (doc.getLong("testersTarget") ?: 20L).toInt(),
                                rewardPoints = (doc.getLong("rewardPoints") ?: 40L).toInt(),
                                isFeatured = doc.getBoolean("isFeatured") ?: false,
                                priorityScore = (doc.getLong("priorityScore") ?: 0L).toInt(),
                                isHidden = doc.getBoolean("isHidden") ?: false
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                    trySend(apps)
                }
            }

        awaitClose { listenerRegistration.remove() }
    }

    suspend fun recordTesterActivity(activity: com.example.data.model.AppTesterActivity): Result<Boolean> {
        val db = getFirestore() ?: return Result.failure(Exception("قاعدة بيانات Firestore غير متوفرة."))
        return try {
            ensureFirebaseAuth()
            val data = hashMapOf(
                "packageName" to activity.packageName,
                "appName" to activity.appName,
                "testerName" to activity.testerName,
                "testerEmail" to activity.testerEmail,
                "action" to activity.action,
                "timestamp" to activity.timestamp
            )
            db.collection("tester_activities").add(data).await()
            Result.success(true)
        } catch (e: Exception) {
            Log.w("FirestoreService", "Record tester activity error: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun fetchTesterActivitiesForPackage(packageName: String): Result<List<com.example.data.model.AppTesterActivity>> {
        val db = getFirestore() ?: return Result.failure(Exception("قاعدة بيانات Firestore غير متوفرة."))
        return try {
            ensureFirebaseAuth()
            val snapshot = db.collection("tester_activities")
                .whereEqualTo("packageName", packageName)
                .limit(50)
                .get()
                .await()
            val list = snapshot.documents.mapNotNull { doc ->
                try {
                    com.example.data.model.AppTesterActivity(
                        id = 0,
                        packageName = doc.getString("packageName") ?: packageName,
                        appName = doc.getString("appName") ?: "",
                        testerName = doc.getString("testerName") ?: "مختبر معتمد",
                        testerEmail = doc.getString("testerEmail") ?: "",
                        action = doc.getString("action") ?: "OPENED_APP",
                        timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()
                    )
                } catch (e: Exception) {
                    null
                }
            }.sortedByDescending { it.timestamp }
            Result.success(list)
        } catch (e: Exception) {
            Log.w("FirestoreService", "Fetch tester activities error: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun updateAppTestersInFirestore(packageName: String, newTestersCount: Int) {
        val db = getFirestore() ?: return
        try {
            ensureFirebaseAuth()
            db.collection("community_apps").document(packageName)
                .update("testersCurrent", newTestersCount)
                .await()
        } catch (e: Exception) {
            Log.e("FirestoreService", "Error updating testers count: ${e.message}")
        }
    }

    suspend fun publishDailyLogToFirestore(log: DailyTestLog): Result<Boolean> {
        val db = getFirestore() ?: return Result.failure(Exception("Firestore غير متوفر"))
        return try {
            ensureFirebaseAuth()
            val data = hashMapOf(
                "packageName" to log.packageName,
                "appName" to log.appName,
                "dayNumber" to log.dayNumber,
                "testDate" to log.testDate,
                "durationMinutes" to log.durationMinutes,
                "rating" to log.rating,
                "notes" to log.notes,
                "proofScreenshotUrl" to log.proofScreenshotUrl,
                "deviceModel" to log.deviceModel,
                "androidVersion" to log.androidVersion,
                "verifiedAt" to log.verifiedAt
            )
            val docId = "${log.packageName}_${log.testDate}_${log.dayNumber}_${log.verifiedAt}"
            db.collection("daily_test_logs").document(docId).set(data).await()
            Result.success(true)
        } catch (e: Exception) {
            Log.w("FirestoreService", "Error publishing daily log: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun fetchDailyLogsForPackage(packageName: String): Result<List<DailyTestLog>> {
        val db = getFirestore() ?: return Result.failure(Exception("Firestore غير متوفر"))
        return try {
            ensureFirebaseAuth()
            val snapshot = db.collection("daily_test_logs")
                .whereEqualTo("packageName", packageName)
                .limit(100)
                .get()
                .await()
            val logs = snapshot.documents.mapNotNull { doc ->
                try {
                    DailyTestLog(
                        id = 0,
                        joinedTestId = 0,
                        appName = doc.getString("appName") ?: "",
                        packageName = doc.getString("packageName") ?: packageName,
                        dayNumber = (doc.getLong("dayNumber") ?: 1L).toInt(),
                        testDate = doc.getString("testDate") ?: "",
                        durationMinutes = (doc.getLong("durationMinutes") ?: 10L).toInt(),
                        rating = (doc.getLong("rating") ?: 5L).toInt(),
                        notes = doc.getString("notes") ?: "",
                        proofScreenshotUrl = doc.getString("proofScreenshotUrl") ?: "",
                        deviceModel = doc.getString("deviceModel") ?: "",
                        androidVersion = doc.getString("androidVersion") ?: "",
                        isSyncedToCloud = true,
                        verifiedAt = doc.getLong("verifiedAt") ?: System.currentTimeMillis()
                    )
                } catch (e: Exception) {
                    null
                }
            }.sortedByDescending { it.verifiedAt }
            Result.success(logs)
        } catch (e: Exception) {
            Log.w("FirestoreService", "Error fetching daily logs: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun publishFeedbackToFirestore(feedback: TesterFeedback): Result<Boolean> {
        val db = getFirestore() ?: return Result.failure(Exception("Firestore غير متوفر"))
        return try {
            ensureFirebaseAuth()
            val data = hashMapOf(
                "packageName" to feedback.packageName,
                "appName" to feedback.appName,
                "testerName" to feedback.testerName,
                "testerEmail" to feedback.testerEmail,
                "rating" to feedback.rating,
                "feedbackType" to feedback.feedbackType,
                "content" to feedback.content,
                "deviceModel" to feedback.deviceModel,
                "androidVersion" to feedback.androidVersion,
                "screenshotUrl" to feedback.screenshotUrl,
                "developerReply" to feedback.developerReply,
                "developerRepliedAt" to feedback.developerRepliedAt,
                "isVerifiedTester" to feedback.isVerifiedTester,
                "submittedAt" to feedback.submittedAt
            )
            val docId = "${feedback.packageName}_${feedback.submittedAt}"
            db.collection("tester_feedback").document(docId).set(data).await()
            Result.success(true)
        } catch (e: Exception) {
            Log.w("FirestoreService", "Error publishing feedback: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun fetchFeedbackForPackage(packageName: String): Result<List<TesterFeedback>> {
        val db = getFirestore() ?: return Result.failure(Exception("Firestore غير متوفر"))
        return try {
            ensureFirebaseAuth()
            val snapshot = db.collection("tester_feedback")
                .whereEqualTo("packageName", packageName)
                .limit(100)
                .get()
                .await()
            val list = snapshot.documents.mapNotNull { doc ->
                try {
                    TesterFeedback(
                        id = 0,
                        userAppId = 0,
                        packageName = doc.getString("packageName") ?: packageName,
                        appName = doc.getString("appName") ?: "",
                        testerName = doc.getString("testerName") ?: "مختبر معتمد",
                        testerEmail = doc.getString("testerEmail") ?: "",
                        rating = (doc.getLong("rating") ?: 5L).toInt(),
                        feedbackType = doc.getString("feedbackType") ?: "FEEDBACK",
                        content = doc.getString("content") ?: "",
                        deviceModel = doc.getString("deviceModel") ?: "",
                        androidVersion = doc.getString("androidVersion") ?: "",
                        screenshotUrl = doc.getString("screenshotUrl") ?: "",
                        developerReply = doc.getString("developerReply"),
                        developerRepliedAt = doc.getLong("developerRepliedAt"),
                        isVerifiedTester = doc.getBoolean("isVerifiedTester") ?: true,
                        isSyncedToCloud = true,
                        submittedAt = doc.getLong("submittedAt") ?: System.currentTimeMillis()
                    )
                } catch (e: Exception) {
                    null
                }
            }.sortedByDescending { it.submittedAt }
            Result.success(list)
        } catch (e: Exception) {
            Log.w("FirestoreService", "Error fetching feedback: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun checkCloudConnectionStatus(): CloudDiagnosticResult {
        val db = getFirestore()
        if (db == null) {
            return CloudDiagnosticResult(
                isInitialized = false,
                authStatus = "NOT_INITIALIZED",
                authMessage = "حزمة Firebase Firestore غير مهيأة أو ملف google-services.json مفقود.",
                canRead = false,
                readError = "Firestore Instance is null",
                remoteAppsCount = 0,
                canWrite = false,
                writeError = "Firestore Instance is null",
                suggestedFix = "تأكد من وجود google-services.json وتفعيل Firebase في المشروع."
            )
        }

        // Test Auth
        val authRes = ensureFirebaseAuth()
        val authStatus = if (authRes.isSuccess) "AUTHENTICATED" else "ANONYMOUS_FAILED"
        val authMessage = if (authRes.isSuccess) {
            "تم تسجيل الدخول في فايربيس بنجاح (UID: ${authRes.getOrNull()?.take(8)}...)"
        } else {
            "فشل تسجيل الدخول المجهول: ${authRes.exceptionOrNull()?.message ?: "Anonymous Auth Provider is disabled"}"
        }

        // Test Read
        var canRead = false
        var readError: String? = null
        var appsCount = 0
        try {
            val snapshot = db.collection("community_apps").limit(50).get().await()
            canRead = true
            appsCount = snapshot.size()
        } catch (e: Exception) {
            canRead = false
            readError = e.message
        }

        // Test Write
        var canWrite = false
        var writeError: String? = null
        try {
            val testDoc = hashMapOf(
                "testPing" to System.currentTimeMillis(),
                "status" to "OK"
            )
            db.collection("_diagnostics").document("ping").set(testDoc).await()
            canWrite = true
        } catch (e: Exception) {
            canWrite = false
            writeError = e.message
        }

        val suggestedFix = when {
            !canRead || !canWrite ->
                "يرجى فتح Firebase Console لمشروع playtester-hub-61bd6 والقيام بالآتي:\n1) الدخول إلى Firestore Database -> Rules وتفعيل: allow read, write: if true;\n2) الدخول إلى Authentication -> Sign-in method وتفعيل Anonymous (مجهول)."
            appsCount == 0 ->
                "السيرفر متصل وصحي تماماً، ولكن قاعدة البيانات فارغة حالياً (لم يرفع المطورون تطبيقاتهم بعد، أو تم تخزينها محلياً). اضغط زر «رفع جميع تطبيقاتي المحلية للسيرفر» لرفعها فوراً!"
            else ->
                "الاتصال السحابي ممتاز ومتصل بقاعدة بيانات التطبيقات الحية بنجاح!"
        }

        return CloudDiagnosticResult(
            isInitialized = true,
            authStatus = authStatus,
            authMessage = authMessage,
            canRead = canRead,
            readError = readError,
            remoteAppsCount = appsCount,
            canWrite = canWrite,
            writeError = writeError,
            suggestedFix = suggestedFix
        )
    }
}
