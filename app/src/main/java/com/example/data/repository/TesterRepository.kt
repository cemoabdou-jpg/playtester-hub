package com.example.data.repository

import com.example.data.local.AppDao
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TesterRepository(private val dao: AppDao) {

    val communityApps: Flow<List<CommunityApp>> = dao.getAllCommunityApps()
    val joinedTests: Flow<List<JoinedTest>> = dao.getAllJoinedTests()
    val userApps: Flow<List<UserApp>> = dao.getAllUserApps()
    val wallet: Flow<UserWallet?> = dao.getUserWallet()
    val currentUser: Flow<AuthUser?> = dao.getCurrentUserFlow()
    val allArtifacts: Flow<List<AppBuildArtifact>> = dao.getAllArtifacts()
    val allInvitedTesters: Flow<List<InvitedTester>> = dao.getAllInvitedTesters()
    val notifications: Flow<List<AppNotification>> = dao.getAllNotifications()
    val unreadNotificationsCount: Flow<Int> = dao.getUnreadNotificationsCount()
    val allDailyTestLogs: Flow<List<DailyTestLog>> = dao.getAllDailyTestLogs()
    val allFeedback: Flow<List<TesterFeedback>> = dao.getAllFeedback()

    fun getArtifactsForApp(appId: Int): Flow<List<AppBuildArtifact>> = dao.getArtifactsForApp(appId)
    fun getInvitedTestersForApp(appId: Int): Flow<List<InvitedTester>> = dao.getInvitedTestersForApp(appId)
    fun getLogsForJoinedTest(joinedTestId: Int): Flow<List<DailyTestLog>> = dao.getLogsForJoinedTest(joinedTestId)

    companion object {
        // Designated admin emails that get automatically assigned the ADMIN role
        val ADMIN_EMAILS = setOf(
            "cemoabdou@gmail.com",
            "admin@playtesterhub.com"
        )

        fun isAdminEmail(email: String): Boolean {
            return ADMIN_EMAILS.contains(email.trim().lowercase())
        }
    }

    // Auth methods
    suspend fun registerUser(name: String, email: String, password: String): Result<AuthUser> {
        val cleanEmail = email.trim().lowercase()
        if (cleanEmail.isBlank() || !cleanEmail.contains("@")) {
            return Result.failure(Exception("يرجى إدخال بريد إلكتروني صالح"))
        }
        if (password.length < 6) {
            return Result.failure(Exception("كلمة المرور يجب أن لا تقل عن 6 أحرف"))
        }
        val existing = dao.getUserByEmail(cleanEmail)
        if (existing != null) {
            return Result.failure(Exception("البريد الإلكتروني مسجل بالفعل، يرجى تسجيل الدخول"))
        }
        dao.logoutAllUsers()
        val assignedRole = if (isAdminEmail(cleanEmail)) "ADMIN" else "DEVELOPER"
        val newUser = AuthUser(
            email = cleanEmail,
            passwordHash = password, // In client DB model
            displayName = if (name.isNotBlank()) name.trim() else cleanEmail.substringBefore("@"),
            authProvider = "EMAIL",
            role = assignedRole,
            isLoggedIn = true
        )
        val id = dao.insertAuthUser(newUser)
        return Result.success(newUser.copy(id = id.toInt()))
    }

    suspend fun loginWithEmail(email: String, password: String): Result<AuthUser> {
        val cleanEmail = email.trim().lowercase()
        val user = dao.getUserByEmail(cleanEmail)
        if (user == null) {
            // Auto register default developer or user seamlessly
            dao.logoutAllUsers()
            val assignedRole = if (isAdminEmail(cleanEmail)) "ADMIN" else "DEVELOPER"
            val newUser = AuthUser(
                email = cleanEmail,
                passwordHash = password,
                displayName = cleanEmail.substringBefore("@").replace(".", " "),
                authProvider = "EMAIL",
                role = assignedRole,
                isLoggedIn = true
            )
            val id = dao.insertAuthUser(newUser)
            return Result.success(newUser.copy(id = id.toInt()))
        }
        if (user.passwordHash.isNotEmpty() && user.passwordHash != password) {
            return Result.failure(Exception("كلمة المرور غير صحيحة"))
        }
        dao.logoutAllUsers()
        val assignedRole = if (isAdminEmail(cleanEmail)) "ADMIN" else user.role
        val loggedInUser = user.copy(isLoggedIn = true, role = assignedRole)
        dao.updateAuthUser(loggedInUser)
        return Result.success(loggedInUser)
    }

    suspend fun loginWithGoogle(email: String, displayName: String): Result<AuthUser> {
        val cleanEmail = email.trim().lowercase()
        dao.logoutAllUsers()
        val assignedRole = if (isAdminEmail(cleanEmail)) "ADMIN" else "DEVELOPER"
        val existing = dao.getUserByEmail(cleanEmail)
        val userToLogin = if (existing != null) {
            val role = if (isAdminEmail(cleanEmail)) "ADMIN" else existing.role
            existing.copy(isLoggedIn = true, displayName = displayName, role = role)
        } else {
            AuthUser(
                email = cleanEmail,
                displayName = displayName,
                authProvider = "GOOGLE",
                role = assignedRole,
                isLoggedIn = true
            )
        }
        dao.insertAuthUser(userToLogin)
        return Result.success(userToLogin)
    }

    suspend fun logout(): Result<Unit> {
        dao.logoutAllUsers()
        return Result.success(Unit)
    }

    // Version Management (Artifacts)
    suspend fun uploadArtifact(
        userAppId: Int,
        versionName: String,
        versionCode: Int,
        artifactType: String,
        fileName: String,
        fileSizeMb: Double,
        releaseNotes: String,
        setAsActive: Boolean
    ): Result<String> {
        if (setAsActive) {
            dao.deactivateAllArtifactsForApp(userAppId)
        }
        val artifact = AppBuildArtifact(
            userAppId = userAppId,
            versionName = versionName,
            versionCode = versionCode,
            artifactType = artifactType,
            fileName = fileName,
            fileSizeMb = fileSizeMb,
            releaseNotes = releaseNotes,
            isActiveForTesting = setAsActive,
            track = "CLOSED_TESTING"
        )
        dao.insertArtifact(artifact)
        return Result.success("تم رفع إصدار الحزمة بنجاح ($versionName)!")
    }

    suspend fun setActiveVersion(userAppId: Int, artifactId: Int): Result<String> {
        dao.deactivateAllArtifactsForApp(userAppId)
        dao.activateArtifact(artifactId)
        return Result.success("تم تعيين الإصدار كإصدار نشط للاختبار المغلق!")
    }

    suspend fun deleteArtifact(artifactId: Int): Result<String> {
        dao.deleteArtifact(artifactId)
        return Result.success("تم حذف إصدار الحزمة بنجاح")
    }

    // Tester Invitations
    suspend fun inviteTesterByEmail(userAppId: Int, email: String, notes: String): Result<String> {
        val cleanEmail = email.trim().lowercase()
        if (cleanEmail.isBlank() || !cleanEmail.contains("@")) {
            return Result.failure(Exception("يرجى إدخال بريد إلكتروني صالح للمختبر"))
        }
        val newTester = InvitedTester(
            userAppId = userAppId,
            email = cleanEmail,
            status = "INVITED",
            notes = notes
        )
        dao.insertInvitedTester(newTester)
        return Result.success("تم إرسال دعوة الاختبار إلى $cleanEmail بنجاح!")
    }

    suspend fun updateTesterStatus(testerId: Int, status: String): Result<String> {
        val acceptedAt = if (status == "ACCEPTED" || status == "TESTING") System.currentTimeMillis() else null
        dao.updateTesterStatus(testerId, status, acceptedAt)
        return Result.success("تم تحديث حالة المختبر بنجاح")
    }

    suspend fun removeInvitedTester(testerId: Int): Result<String> {
        dao.deleteInvitedTester(testerId)
        return Result.success("تم إزالة المختبر من القائمة")
    }

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    suspend fun joinCommunityApp(app: CommunityApp): Result<String> {
        val existing = dao.getJoinedTestByCommunityId(app.id)
        if (existing != null) {
            return Result.failure(Exception("أنت منضم بالفعل لاختبار هذا التطبيق!"))
        }

        val newTest = JoinedTest(
            communityAppId = app.id,
            appName = app.name,
            packageName = app.packageName,
            category = app.category,
            googleGroupUrl = app.googleGroupUrl,
            webOptInUrl = app.webOptInUrl,
            androidOptInUrl = app.androidOptInUrl,
            joinTimestamp = System.currentTimeMillis(),
            daysCompleted = 1,
            checkedDaysCsv = "1",
            lastCheckInDate = dateFormat.format(Date()),
            isCompleted = false
        )
        dao.insertJoinedTest(newTest)
        dao.incrementCommunityAppTesters(app.id)

        // Award join reward points (+10 bonus)
        updateWalletPoints(10, incrementStreak = false)

        return Result.success("تم الانضمام للاختبار بنجاح وحصلت على +10 نقاط!")
    }

    suspend fun confirmDailyTest(
        test: JoinedTest,
        durationMinutes: Int = 10,
        rating: Int = 5,
        notes: String = ""
    ): Result<String> {
        val today = dateFormat.format(Date())
        if (test.lastCheckInDate == today) {
            return Result.failure(Exception("تم تأكيد تجربة اليوم مسبقاً لهذا التطبيق! عُد غداً لإكمال اليوم التالي."))
        }

        val newDays = (test.daysCompleted + 1).coerceAtMost(14)
        val currentDaysSet = test.checkedDaysCsv.split(",")
            .filter { it.isNotEmpty() }
            .mapNotNull { it.toIntOrNull() }
            .toMutableSet()
        currentDaysSet.add(newDays)
        val updatedCsv = currentDaysSet.sorted().joinToString(",")

        val isNowCompleted = newDays >= 14

        val updatedTest = test.copy(
            daysCompleted = newDays,
            lastCheckInDate = today,
            checkedDaysCsv = updatedCsv,
            isCompleted = isNowCompleted
        )
        dao.updateJoinedTest(updatedTest)

        // Record official Daily Test Log entry for Google Play verification compliance
        val dailyLog = DailyTestLog(
            joinedTestId = test.id,
            appName = test.appName,
            packageName = test.packageName,
            dayNumber = newDays,
            testDate = today,
            durationMinutes = durationMinutes.coerceAtLeast(1),
            rating = rating.coerceIn(1, 5),
            notes = notes.ifBlank { "تم فتح التطبيق وتجربة الميزات الرئيسية بنجاح لمدة ${durationMinutes} دقائق." },
            verifiedAt = System.currentTimeMillis()
        )
        dao.insertDailyTestLog(dailyLog)

        // Notify user of successful daily testing verification
        val notif = AppNotification(
            title = "تأكيد التجربة اليومية (اليوم $newDays من 14)",
            message = "تم تأكيد وتوثيق تجربة تطبيق ${test.appName} بنجاح! حصلت على +25 نقطة.",
            type = "DAILY_CONFIRMATION",
            actionRoute = "joined"
        )
        dao.insertNotification(notif)

        val pointsGained = if (isNowCompleted) 60 else 25
        updateWalletPoints(pointsGained, incrementStreak = true, isTestComplete = isNowCompleted)

        val msg = if (isNowCompleted) {
            "تهانينا! أتممت بنجاح متطلب الـ 14 يوماً الكامل لتطبيق '${test.appName}'! حصلت على +60 نقطة وبات جاهزاً لشهادة جوجل بلاي."
        } else {
            "تم تأكيد تجربة اليوم $newDays/14 بنجاح! تم تسجيل مدة $durationMinutes دقيقة وكسبت +25 نقطة."
        }
        return Result.success(msg)
    }

    suspend fun recordCheckIn(test: JoinedTest): Result<String> {
        return confirmDailyTest(test, durationMinutes = 10, rating = 5, notes = "تجربة يومية سريعة")
    }

    suspend fun sendDailyTestingReminder(app: UserApp): Result<String> {
        val notif = AppNotification(
            title = "تذكير يومي عاجل للمختبرين",
            message = "مطلوب تأكيد تجربة تطبيق '${app.name}' لليوم لضمان استيفاء شرط الـ 14 يوماً المتواصلة في Google Play.",
            type = "DAILY_REMINDER",
            actionRoute = "joined"
        )
        dao.insertNotification(notif)
        return Result.success("تم إرسال تذكير تأكيد التجربة اليومية للمختبرين بنجاح!")
    }

    suspend fun markFeedbackSent(test: JoinedTest) {
        dao.updateJoinedTest(test.copy(feedbackSent = true))
        updateWalletPoints(15, incrementStreak = false)
    }

    suspend fun submitFeedback(
        test: JoinedTest,
        content: String,
        rating: Int = 5,
        feedbackType: String = "FEEDBACK",
        testerEmail: String = "tester@example.com"
    ): Result<String> {
        if (content.isBlank()) {
            return Result.failure(Exception("يرجى كتابة ملاحظتك أو تقرير المشكلة أولاً"))
        }
        val feedback = TesterFeedback(
            userAppId = test.communityAppId,
            packageName = test.packageName,
            appName = test.appName,
            testerEmail = testerEmail,
            rating = rating,
            feedbackType = feedbackType,
            content = content.trim(),
            submittedAt = System.currentTimeMillis()
        )
        dao.insertFeedback(feedback)
        dao.updateJoinedTest(test.copy(feedbackSent = true))

        // Notify developer of new feedback
        dao.insertNotification(
            AppNotification(
                title = "ملاحظة وتقييم جديد من مختبر",
                message = "قدم مختبر ملاحظات لتطبيق '${test.appName}' بتقييم $rating نجوم.",
                type = "FEEDBACK",
                actionRoute = "my_apps"
            )
        )

        updateWalletPoints(20, incrementStreak = false)
        return Result.success("تم إرسال الملاحظات بنجاح للمطور وحصلت على +20 نقطة!")
    }

    suspend fun deleteFeedback(id: Int) {
        dao.deleteFeedback(id)
    }

    suspend fun publishApp(app: UserApp, featureWithPoints: Boolean): Result<String> {
        // Upload is free and unconditional for all registered users (cost = 0)
        // Can be linked with points in future updates as requested
        dao.insertUserApp(app)

        // Also add to community exchange so others can test it
        val communityItem = CommunityApp(
            name = app.name,
            developerName = "أنا (مطور التطبيق)",
            packageName = app.packageName,
            description = if (app.instructions.isNotEmpty()) app.instructions else "تطبيق قيد الاختبار المغلق لـ 14 يوماً",
            category = app.category,
            googleGroupUrl = app.googleGroupUrl,
            webOptInUrl = app.webOptInUrl,
            androidOptInUrl = app.androidOptInUrl,
            testersCurrent = app.testersCount,
            testersTarget = app.targetTesters,
            rewardPoints = 40,
            isFeatured = featureWithPoints
        )
        dao.insertCommunityApp(communityItem)

        // Free upload for all registered users without point deductions or restrictions
        return Result.success("تم نشر التطبيق بنجاح! متاح مجاناً لجميع المسجلين بدون أي شروط.")
    }

    suspend fun deleteUserApp(id: Int) {
        dao.deleteUserApp(id)
    }

    suspend fun deleteJoinedTest(id: Int) {
        dao.deleteJoinedTest(id)
    }

    // Notifications operations
    suspend fun addNotification(
        title: String,
        message: String,
        type: String = "INFO",
        actionRoute: String = ""
    ): Long {
        val notification = AppNotification(
            title = title,
            message = message,
            type = type,
            actionRoute = actionRoute
        )
        return dao.insertNotification(notification)
    }

    suspend fun markNotificationAsRead(id: Int) {
        dao.markNotificationAsRead(id)
    }

    suspend fun markAllNotificationsAsRead() {
        dao.markAllNotificationsAsRead()
    }

    suspend fun deleteNotification(id: Int) {
        dao.deleteNotification(id)
    }

    suspend fun clearAllNotifications() {
        dao.clearAllNotifications()
    }

    private suspend fun updateWalletPoints(pointsToAdd: Int, incrementStreak: Boolean, isTestComplete: Boolean = false) {
        val current = dao.getWalletOnce() ?: UserWallet(id = 1)
        dao.insertOrUpdateWallet(
            current.copy(
                points = current.points + pointsToAdd,
                currentStreak = if (incrementStreak) current.currentStreak + 1 else current.currentStreak,
                testsCompleted = if (isTestComplete) current.testsCompleted + 1 else current.testsCompleted
            )
        )
    }

    private suspend fun deductPoints(pointsToDeduct: Int) {
        val current = dao.getWalletOnce() ?: UserWallet(id = 1)
        val newPoints = (current.points - pointsToDeduct).coerceAtLeast(0)
        dao.insertOrUpdateWallet(current.copy(points = newPoints))
    }

    // --- Admin / Owner Control Methods ---
    val allCommunityAppsAdmin: Flow<List<CommunityApp>> = dao.getAllCommunityAppsAdmin()

    suspend fun updateAppPriority(appId: Int, priorityScore: Int): Result<String> {
        dao.updateAppPriority(appId, priorityScore)
        return Result.success("تم تحديث أولوية ظهور التطبيق بنجاح")
    }

    suspend fun toggleAppFeatured(appId: Int, isFeatured: Boolean): Result<String> {
        dao.toggleAppFeatured(appId, isFeatured)
        val msg = if (isFeatured) "تم تثبيت التطبيق في الصدارة (مميز)" else "تم إلغاء تثبيت التطبيق من الصدارة"
        return Result.success(msg)
    }

    suspend fun toggleAppVisibility(appId: Int, isHidden: Boolean): Result<String> {
        dao.toggleAppHidden(appId, isHidden)
        val msg = if (isHidden) "تم إخفاء التطبيق من سوق التبادل" else "تم إظهار التطبيق في سوق التبادل"
        return Result.success(msg)
    }

    suspend fun deleteCommunityAppByAdmin(appId: Int): Result<String> {
        dao.deleteCommunityApp(appId)
        return Result.success("تم حذف التطبيق نهائياً من قبل صاحب المنصة")
    }

    suspend fun broadcastNotification(title: String, message: String, type: String = "INFO"): Result<String> {
        addNotification(title = title, message = message, type = type)
        return Result.success("تم إرسال الإشعار العام لجميع المطورين بنجاح!")
    }
}
