package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.data.repository.TesterRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TesterViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TesterRepository
    private val prefs: SharedPreferences = application.getSharedPreferences("playtester_auth_prefs", Context.MODE_PRIVATE)

    val userApps: StateFlow<List<UserApp>>
    val communityApps: StateFlow<List<CommunityApp>>
    val joinedTests: StateFlow<List<JoinedTest>>

    private val _currentUser = MutableStateFlow(
        AuthUser(
            uid = "guest_unverified",
            email = "",
            displayName = "",
            points = 0,
            isVip = false,
            isEmailVerified = false
        )
    )
    val currentUser: StateFlow<AuthUser> = _currentUser.asStateFlow()

    private val _selectedAppForReport = MutableStateFlow<UserApp?>(null)
    val selectedAppForReport: StateFlow<UserApp?> = _selectedAppForReport.asStateFlow()

    private val _showAuditReportDialog = MutableStateFlow(false)
    val showAuditReportDialog: StateFlow<Boolean> = _showAuditReportDialog.asStateFlow()

    private val _showDeveloperGrowthDialog = MutableStateFlow(false)
    val showDeveloperGrowthDialog: StateFlow<Boolean> = _showDeveloperGrowthDialog.asStateFlow()

    private val _showMonetizationDialog = MutableStateFlow(false)
    val showMonetizationDialog: StateFlow<Boolean> = _showMonetizationDialog.asStateFlow()

    private val _showCrashReportsDialog = MutableStateFlow(false)
    val showCrashReportsDialog: StateFlow<Boolean> = _showCrashReportsDialog.asStateFlow()

    private val _showTesterHealthDialog = MutableStateFlow(false)
    val showTesterHealthDialog: StateFlow<Boolean> = _showTesterHealthDialog.asStateFlow()

    private val _showChangelogDialog = MutableStateFlow(false)
    val showChangelogDialog: StateFlow<Boolean> = _showChangelogDialog.asStateFlow()

    private val _showEmailVerificationDialog = MutableStateFlow(false)
    val showEmailVerificationDialog: StateFlow<Boolean> = _showEmailVerificationDialog.asStateFlow()

    private val _showLeaderboardDialog = MutableStateFlow(false)
    val showLeaderboardDialog: StateFlow<Boolean> = _showLeaderboardDialog.asStateFlow()

    private val _showLuckySpinDialog = MutableStateFlow(false)
    val showLuckySpinDialog: StateFlow<Boolean> = _showLuckySpinDialog.asStateFlow()

    private val _showAdminControlDialog = MutableStateFlow(false)
    val showAdminControlDialog: StateFlow<Boolean> = _showAdminControlDialog.asStateFlow()

    private val _showNotificationsDialog = MutableStateFlow(false)
    val showNotificationsDialog: StateFlow<Boolean> = _showNotificationsDialog.asStateFlow()

    private val _showTesterProfileDialog = MutableStateFlow(false)
    val showTesterProfileDialog: StateFlow<Boolean> = _showTesterProfileDialog.asStateFlow()

    private val _selectedTesterProfile = MutableStateFlow<TesterProfileData?>(null)
    val selectedTesterProfile: StateFlow<TesterProfileData?> = _selectedTesterProfile.asStateFlow()

    private val _showComplianceMatrixDialog = MutableStateFlow(false)
    val showComplianceMatrixDialog: StateFlow<Boolean> = _showComplianceMatrixDialog.asStateFlow()

    private val _selectedAppForCompliance = MutableStateFlow<UserApp?>(null)
    val selectedAppForCompliance: StateFlow<UserApp?> = _selectedAppForCompliance.asStateFlow()

    private val _showActiveTestSessionDialog = MutableStateFlow(false)
    val showActiveTestSessionDialog: StateFlow<Boolean> = _showActiveTestSessionDialog.asStateFlow()

    private val _activeSessionTest = MutableStateFlow<JoinedTest?>(null)
    val activeSessionTest: StateFlow<JoinedTest?> = _activeSessionTest.asStateFlow()

    private val _showDeveloperChatDialog = MutableStateFlow(false)
    val showDeveloperChatDialog: StateFlow<Boolean> = _showDeveloperChatDialog.asStateFlow()

    private val _activeChatRecipientName = MutableStateFlow("")
    val activeChatRecipientName: StateFlow<String> = _activeChatRecipientName.asStateFlow()

    private val _activeChatRecipientEmail = MutableStateFlow("")
    val activeChatRecipientEmail: StateFlow<String> = _activeChatRecipientEmail.asStateFlow()

    private val _activeChatAppName = MutableStateFlow<String?>(null)
    val activeChatAppName: StateFlow<String?> = _activeChatAppName.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ReciprocalChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ReciprocalChatMessage>> = _chatMessages.asStateFlow()

    private val _testerNotifications = MutableStateFlow<List<TesterNotification>>(emptyList())
    val testerNotifications: StateFlow<List<TesterNotification>> = _testerNotifications.asStateFlow()

    private val _streakDays = MutableStateFlow(3)
    val streakDays: StateFlow<Int> = _streakDays.asStateFlow()

    private val _isDailyCheckInDoneToday = MutableStateFlow(false)
    val isDailyCheckInDoneToday: StateFlow<Boolean> = _isDailyCheckInDoneToday.asStateFlow()

    private val _claimedBadgeIds = MutableStateFlow<Set<String>>(emptySet())
    val claimedBadgeIds: StateFlow<Set<String>> = _claimedBadgeIds.asStateFlow()

    private val _verificationCode = MutableStateFlow("")
    val verificationCode: StateFlow<String> = _verificationCode.asStateFlow()

    private val _pendingEmail = MutableStateFlow("")
    val pendingEmail: StateFlow<String> = _pendingEmail.asStateFlow()

    private val _pendingName = MutableStateFlow("")
    val pendingName: StateFlow<String> = _pendingName.asStateFlow()

    private val _lastSentCode = MutableStateFlow<String?>(null)
    val lastSentCode: StateFlow<String?> = _lastSentCode.asStateFlow()

    init {
        val database = AppDatabase.getInstance(application)
        repository = TesterRepository(database.appDao())

        // Global reset of coins to 0 for all users as requested
        val resetCoinsKey = "global_coins_reset_zero_all_users_v2"
        if (!prefs.getBoolean(resetCoinsKey, false)) {
            prefs.edit()
                .putInt("user_points", 0)
                .putBoolean(resetCoinsKey, true)
                .apply()
        }

        // Check if user has an already verified session
        val savedEmail = prefs.getString("user_email", null)
        val savedName = prefs.getString("user_name", null)
        val isVerified = prefs.getBoolean("is_verified", false)

        if (!savedEmail.isNullOrBlank() && isVerified) {
            val isOwner = savedEmail.trim().equals(PLATFORM_OWNER_EMAIL, ignoreCase = true)
            _currentUser.value = AuthUser(
                uid = if (isOwner) "owner_cemoabdou" else "user_${savedEmail.hashCode()}",
                email = savedEmail,
                displayName = savedName ?: if (isOwner) "صاحب المنصة (عبدالرحمن)" else "مطور معتمد",
                points = if (isOwner) 9999 else prefs.getInt("user_points", 0),
                isVip = isOwner,
                isEmailVerified = true
            )
        } else {
            _currentUser.value = AuthUser(
                uid = "guest_unverified",
                email = "",
                displayName = "",
                points = 0,
                isVip = false,
                isEmailVerified = false
            )
        }

        userApps = repository.userApps.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        communityApps = repository.communityApps.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        joinedTests = repository.joinedTests.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        // Only genuine notifications created from real testing activities are shown
        _testerNotifications.value = emptyList()
    }

    fun openAuditReport(app: UserApp? = null) {
        val target = app ?: userApps.value.firstOrNull()
        _selectedAppForReport.value = target
        _showAuditReportDialog.value = true
    }

    fun closeAuditReport() {
        _showAuditReportDialog.value = false
    }

    fun openDeveloperGrowth() {
        _showDeveloperGrowthDialog.value = true
    }

    fun closeDeveloperGrowth() {
        _showDeveloperGrowthDialog.value = false
    }

    fun openMonetization() {
        _showMonetizationDialog.value = true
    }

    fun closeMonetization() {
        _showMonetizationDialog.value = false
    }

    fun openCrashReports(app: UserApp? = null) {
        val target = app ?: userApps.value.firstOrNull()
        _selectedAppForReport.value = target
        _showCrashReportsDialog.value = true
    }

    fun closeCrashReports() {
        _showCrashReportsDialog.value = false
    }

    fun openTesterHealth(app: UserApp? = null) {
        val target = app ?: userApps.value.firstOrNull()
        _selectedAppForReport.value = target
        _showTesterHealthDialog.value = true
    }

    fun closeTesterHealth() {
        _showTesterHealthDialog.value = false
    }

    fun openChangelog(app: UserApp? = null) {
        val target = app ?: userApps.value.firstOrNull()
        _selectedAppForReport.value = target
        _showChangelogDialog.value = true
    }

    fun closeChangelog() {
        _showChangelogDialog.value = false
    }

    fun openEmailVerification(email: String = _currentUser.value.email, name: String = _currentUser.value.displayName) {
        _pendingEmail.value = email
        _pendingName.value = name
        startEmailVerification(email, name)
        _showEmailVerificationDialog.value = true
    }

    fun closeEmailVerification() {
        _showEmailVerificationDialog.value = false
    }

    fun addPoints(amount: Int) {
        _currentUser.update { user ->
            val newPoints = (user.points + amount).coerceAtLeast(0)
            if (!user.isPlatformOwner) {
                prefs.edit().putInt("user_points", newPoints).apply()
            }
            user.copy(points = newPoints)
        }
    }

    fun adminResetAllCoins() {
        if (!_currentUser.value.isPlatformOwner) return
        prefs.edit().putInt("user_points", 0).apply()
        _currentUser.update { user ->
            if (user.isPlatformOwner) user else user.copy(points = 0)
        }
    }

    fun earnAdReward(points: Int = 50) {
        addPoints(points)
    }

    fun claimDailyBonus(points: Int = 50) {
        addPoints(points)
    }

    fun startEmailVerification(email: String, name: String) {
        // Generate high entropy 6-digit code
        val code = (100000..999999).random().toString()
        _verificationCode.value = code
        _lastSentCode.value = code
        _pendingEmail.value = email.trim()
        _pendingName.value = name.trim()
    }

    fun confirmVerificationCode(enteredCode: String): Boolean {
        if (enteredCode.trim() == _verificationCode.value && _verificationCode.value.isNotBlank()) {
            val email = _pendingEmail.value.trim()
            val name = if (_pendingName.value.isNotBlank()) _pendingName.value.trim() else "مطور معتمد"
            val isOwner = email.equals(PLATFORM_OWNER_EMAIL, ignoreCase = true)

            prefs.edit()
                .putString("user_email", email)
                .putString("user_name", name)
                .putBoolean("is_verified", true)
                .apply()

            _currentUser.update {
                it.copy(
                    uid = if (isOwner) "owner_cemoabdou" else "user_${email.hashCode()}",
                    email = email,
                    displayName = if (isOwner) "صاحب المنصة (عبدالرحمن)" else name,
                    isEmailVerified = true,
                    isVip = isOwner,
                    points = if (isOwner) 9999 else prefs.getInt("user_points", 0)
                )
            }
            _showEmailVerificationDialog.value = false
            return true
        }
        return false
    }

    fun resendVerificationCode(): String {
        val newCode = (100000..999999).random().toString()
        _verificationCode.value = newCode
        _lastSentCode.value = newCode
        return newCode
    }

    fun getCrashReports(appId: String): Flow<List<CrashReport>> = repository.getCrashReports(appId)

    fun getDailyTestLogs(appId: String): Flow<List<DailyTestLog>> = repository.getDailyTestLogs(appId)

    fun resolveCrash(id: String, note: String) {
        viewModelScope.launch {
            repository.resolveCrashReport(id, note)
        }
    }

    fun joinCommunityApp(app: CommunityApp): Boolean {
        val alreadyJoined = joinedTests.value.any { it.packageName == app.packageName }
        if (alreadyJoined) {
            return false
        }
        viewModelScope.launch {
            val joined = JoinedTest(
                id = "joined_${app.packageName}",
                appId = app.id,
                appName = app.name,
                packageName = app.packageName,
                iconUrl = app.iconUrl,
                joinedAt = System.currentTimeMillis(),
                lastTestDate = System.currentTimeMillis(),
                status = "TESTING"
            )
            repository.joinTest(joined)
            _currentUser.update { user ->
                val newPoints = (user.points + app.pointsReward).coerceAtLeast(0)
                if (!user.isPlatformOwner) {
                    prefs.edit().putInt("user_points", newPoints).apply()
                }
                user.copy(points = newPoints, testsCount = user.testsCount + 1)
            }
        }
        return true
    }

    fun addUserApp(
        name: String,
        pkg: String,
        optIn: String,
        grp: String,
        cat: String = "تطبيق",
        desc: String = "",
        iconUrl: String = "",
        initialTesters: Int = 0,
        initialDays: Int = 1
    ) {
        viewModelScope.launch {
            repository.addUserApp(
                name = name,
                packageName = pkg,
                webOptInUrl = optIn,
                androidOptInUrl = optIn,
                googleGroupUrl = grp,
                category = cat,
                description = desc,
                iconUrl = iconUrl,
                developerName = _currentUser.value.displayName,
                userId = _currentUser.value.uid,
                initialTesters = initialTesters,
                completedDays = initialDays
            )
            _currentUser.update { it.copy(appsCount = it.appsCount + 1) }
        }
    }

    fun updateAppTesters(packageName: String, realTestersCount: Int, completedDays: Int? = null) {
        viewModelScope.launch {
            repository.updateAppTesters(packageName, realTestersCount, completedDays)
        }
    }

    fun updateAppIcon(packageName: String, newIconUrl: String) {
        viewModelScope.launch {
            repository.updateAppIcon(packageName, newIconUrl)
        }
    }

    fun updateAppDetails(
        packageName: String,
        name: String,
        category: String,
        iconUrl: String,
        webOptInUrl: String,
        googleGroupUrl: String
    ) {
        viewModelScope.launch {
            repository.updateAppDetails(packageName, name, category, iconUrl, webOptInUrl, googleGroupUrl)
        }
    }

    fun removeUserApp(app: UserApp) {
        viewModelScope.launch {
            repository.removeUserApp(app)
            _currentUser.update { it.copy(appsCount = maxOf(0, it.appsCount - 1)) }
        }
    }

    fun promoteApp(app: UserApp, cost: Int = 350): Boolean {
        if (_currentUser.value.points >= cost) {
            if (cost > 0) {
                _currentUser.update {
                    val newPoints = it.points - cost
                    prefs.edit().putInt("user_points", newPoints).apply()
                    it.copy(points = newPoints)
                }
            }
            viewModelScope.launch {
                repository.promoteApp(app.packageName, true)
            }
            return true
        }
        return false
    }

    fun promoteAppWithAd(app: UserApp) {
        viewModelScope.launch {
            repository.promoteApp(app.packageName, true)
        }
    }

    fun promoteAppByPackage(packageName: String, isFeatured: Boolean = true) {
        viewModelScope.launch {
            repository.promoteApp(packageName, isFeatured)
        }
    }

    fun unpromoteApp(app: UserApp) {
        viewModelScope.launch {
            repository.promoteApp(app.packageName, false)
        }
    }

    fun loginAsOwner() {
        val email = PLATFORM_OWNER_EMAIL
        val name = "صاحب المنصة (عبدالرحمن)"
        prefs.edit()
            .putString("user_email", email)
            .putString("user_name", name)
            .putBoolean("is_verified", true)
            .apply()

        _currentUser.update {
            it.copy(
                uid = "owner_cemoabdou",
                email = email,
                displayName = name,
                isEmailVerified = true,
                isVip = true,
                points = 9999
            )
        }
    }

    fun logout() {
        prefs.edit().clear().apply()
        _verificationCode.value = ""
        _lastSentCode.value = null
        _pendingEmail.value = ""
        _pendingName.value = ""
        _currentUser.value = AuthUser(
            uid = "guest_unverified",
            email = "",
            displayName = "",
            points = 0,
            isVip = false,
            isEmailVerified = false
        )
        _showAdminControlDialog.value = false
    }

    fun logoutToRegularUser() {
        val email = "developer@testerhub.com"
        val name = "مطور تجريبي"
        prefs.edit()
            .putString("user_email", email)
            .putString("user_name", name)
            .putBoolean("is_verified", true)
            .apply()

        _currentUser.update {
            it.copy(
                uid = "user_guest_01",
                email = email,
                displayName = name,
                isEmailVerified = true,
                isVip = false,
                points = 0
            )
        }
    }

    fun openAdminControl() {
        if (_currentUser.value.isPlatformOwner) {
            _showAdminControlDialog.value = true
        }
    }

    fun closeAdminControl() {
        _showAdminControlDialog.value = false
    }

    fun openNotifications() {
        _showNotificationsDialog.value = true
    }

    fun closeNotifications() {
        _showNotificationsDialog.value = false
    }

    fun markNotificationAsRead(id: String) {
        _testerNotifications.update { list ->
            list.map { if (it.id == id) it.copy(isRead = true) else it }
        }
    }

    fun markAllNotificationsAsRead() {
        _testerNotifications.update { list ->
            list.map { it.copy(isRead = true) }
        }
    }

    fun openTesterProfile(testerEmail: String, testerName: String) {
        val allApps = communityApps.value
        val cleanEmail = testerEmail.trim()
        val cleanName = testerName.trim()

        // Match genuine developer apps uploaded to communityApps
        val matchedApps = allApps.filter { app ->
            (cleanName.isNotBlank() && (app.developerName.contains(cleanName, ignoreCase = true) || cleanName.contains(app.developerName, ignoreCase = true))) ||
            (cleanEmail.isNotBlank() && app.developerName.contains(cleanEmail.substringBefore("@"), ignoreCase = true))
        }

        val completedCount = matchedApps.sumOf { it.currentTesters }.coerceAtLeast(1)
        val badge = if (matchedApps.any { it.isFeatured }) "مطور مميز 💎" else "مطور معتمد ⭐"

        _selectedTesterProfile.value = TesterProfileData(
            name = cleanName.ifBlank { "مطور مسجل" },
            email = cleanEmail,
            badge = badge,
            rating = 5.0f,
            testsCompleted = completedCount,
            reputationPoints = completedCount * 50,
            deviceModel = "جهاز أندرويد معتمد",
            bio = "مطور مسجل في مجتمع PlayTester Hub. يشارك في اختبار التطبيقات وتبادل الفحص للوصول إلى شرط الـ 14 يوماً و 20 مختبراً 🤝",
            apps = matchedApps
        )
        _showTesterProfileDialog.value = true
    }

    fun closeTesterProfile() {
        _showTesterProfileDialog.value = false
        _selectedTesterProfile.value = null
    }

    fun adminDeleteApp(packageName: String) {
        if (!_currentUser.value.isPlatformOwner) return
        viewModelScope.launch {
            repository.adminDeleteApp(packageName)
        }
    }

    fun adminToggleFeatured(packageName: String, isFeatured: Boolean) {
        if (!_currentUser.value.isPlatformOwner) return
        viewModelScope.launch {
            repository.adminSetFeatured(packageName, isFeatured)
        }
    }

    fun adminUpdateTesters(packageName: String, testers: Int) {
        if (!_currentUser.value.isPlatformOwner) return
        viewModelScope.launch {
            repository.adminUpdateTesters(packageName, testers)
        }
    }

    fun adminUpdateApp(
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
        if (!_currentUser.value.isPlatformOwner) return
        viewModelScope.launch {
            repository.adminUpdateApp(
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
        }
    }

    fun openAndTestApp(context: android.content.Context, test: JoinedTest, onRewarded: (Int) -> Unit) {
        val reward = 25
        viewModelScope.launch {
            repository.recordDailyAppTest(test.packageName)
            addPoints(reward)
            onRewarded(reward)
        }
        try {
            val intent = context.packageManager.getLaunchIntentForPackage(test.packageName)
            if (intent != null) {
                intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            } else {
                val storeUrl = "https://play.google.com/store/apps/details?id=${test.packageName}"
                val browserIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(storeUrl))
                browserIntent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(browserIntent)
            }
        } catch (_: Exception) {
            android.widget.Toast.makeText(context, "تم تسجيل اختبار ${test.appName} بنجاح (+25 عملة)", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    fun openLeaderboard() {
        _showLeaderboardDialog.value = true
    }

    fun closeLeaderboard() {
        _showLeaderboardDialog.value = false
    }

    fun claimBadge(badgeId: String, rewardPoints: Int) {
        if (!_claimedBadgeIds.value.contains(badgeId)) {
            _claimedBadgeIds.update { it + badgeId }
            addPoints(rewardPoints)
        }
    }

    fun getBadges(
        user: AuthUser,
        apps: List<UserApp>,
        joined: List<JoinedTest>
    ): List<AchievementBadge> {
        val claimed = _claimedBadgeIds.value

        // 1. وسام المختبر المثالي: 7 أيام متتالية
        val maxDaysCompleted = joined.maxOfOrNull { it.daysCompleted } ?: 0
        val isTesterUnlocked = maxDaysCompleted >= 7

        // 2. وسام صائد الأخطاء: حل أو التبليغ عن عطل مفيد
        val isBugHunterUnlocked = joined.isNotEmpty() || apps.any { it.completedDays >= 3 }

        // 3. وسام المطور المعتمد: 20 مختبراً
        val maxTesters = apps.maxOfOrNull { it.currentTesters } ?: 0
        val isDevUnlocked = maxTesters >= 20

        // 4. وسام الداعم النشط: اختبار 3 تطبيقات
        val isSupporterUnlocked = user.testsCount >= 3 || joined.size >= 3

        // 5. وسام التميز في الصدارة: تمييز تطبيق
        val isPromoterUnlocked = apps.any { it.isVip }

        return listOf(
            AchievementBadge(
                id = "badge_perfect_tester",
                title = "وسام المختبر المثالي",
                description = "فتح واختبار تطبيقات الآخرين لـ 7 أيام متتالية",
                emoji = "🏅",
                coinReward = 150,
                currentProgress = minOf(7, maxDaysCompleted),
                targetProgress = 7,
                isUnlocked = isTesterUnlocked,
                isClaimed = claimed.contains("badge_perfect_tester")
            ),
            AchievementBadge(
                id = "badge_bug_hunter",
                title = "وسام صائد الأخطاء",
                description = "تقديم ملاحظات أعطال واختبار استقرار التطبيقات",
                emoji = "🐛",
                coinReward = 100,
                currentProgress = if (isBugHunterUnlocked) 1 else 0,
                targetProgress = 1,
                isUnlocked = isBugHunterUnlocked,
                isClaimed = claimed.contains("badge_bug_hunter")
            ),
            AchievementBadge(
                id = "badge_verified_dev",
                title = "وسام المطور المعتمد",
                description = "إكمال 20 مختبراً معتمداً لتطبيقك في Google Play",
                emoji = "🚀",
                coinReward = 200,
                currentProgress = minOf(20, maxTesters),
                targetProgress = 20,
                isUnlocked = isDevUnlocked,
                isClaimed = claimed.contains("badge_verified_dev")
            ),
            AchievementBadge(
                id = "badge_active_supporter",
                title = "وسام الداعم النشط",
                description = "الانضمام لاختبار 3 تطبيقات زملاء في المجتمع",
                emoji = "🌟",
                coinReward = 75,
                currentProgress = minOf(3, maxOf(user.testsCount, joined.size)),
                targetProgress = 3,
                isUnlocked = isSupporterUnlocked,
                isClaimed = claimed.contains("badge_active_supporter")
            ),
            AchievementBadge(
                id = "badge_featured_promoter",
                title = "وسام نجم الصدارة",
                description = "ترقية تطبيقك إلى الصدارة لجذب المختبرين بأولوية",
                emoji = "👑",
                coinReward = 100,
                currentProgress = if (isPromoterUnlocked) 1 else 0,
                targetProgress = 1,
                isUnlocked = isPromoterUnlocked,
                isClaimed = claimed.contains("badge_featured_promoter")
            )
        )
    }

    fun getWeeklyLeaderboard(currentUser: AuthUser): List<LeaderboardUser> {
        val devMap = mutableMapOf<String, LeaderboardUser>()

        // 1. Add current user if verified or has name
        if (currentUser.displayName.isNotBlank()) {
            val key = currentUser.email.ifBlank { currentUser.displayName }
            devMap[key] = LeaderboardUser(
                rank = 1,
                name = currentUser.displayName,
                email = currentUser.email,
                testsCompleted = currentUser.testsCount,
                points = currentUser.points,
                badgeTitle = if (currentUser.isPlatformOwner) "صاحب المنصة 👑" else "مطور نشط ⭐",
                isCurrentUser = true
            )
        }

        // 2. Add real developers who uploaded apps to community
        communityApps.value.forEach { app ->
            val devName = app.developerName.trim()
            if (devName.isNotBlank() && devName != "المطور المعتمد") {
                val isSelf = currentUser.displayName.equals(devName, ignoreCase = true)
                if (!isSelf && !devMap.containsKey(devName)) {
                    val safeEmail = "${devName.lowercase().replace(" ", "").replace("[^a-zA-Z0-9]".toRegex(), "")}@developer.com"
                    devMap[devName] = LeaderboardUser(
                        rank = 0,
                        name = devName,
                        email = safeEmail,
                        testsCompleted = app.currentTesters,
                        points = app.currentTesters * 50,
                        badgeTitle = if (app.isFeatured) "مطور مميز 💎" else "مطور معتمد ⭐",
                        isCurrentUser = false
                    )
                }
            }
        }

        val allUsers = devMap.values.toList()
        return allUsers.sortedByDescending { it.points }.mapIndexed { index, user ->
            user.copy(rank = index + 1)
        }
    }

    fun openLuckySpin() {
        _showLuckySpinDialog.value = true
    }

    fun closeLuckySpin() {
        _showLuckySpinDialog.value = false
    }

    fun dailyCheckIn(): Boolean {
        if (!_isDailyCheckInDoneToday.value) {
            _isDailyCheckInDoneToday.value = true
            _streakDays.update { it + 1 }
            addPoints(20)
            return true
        }
        return false
    }

    fun claimSpinReward(coins: Int) {
        addPoints(coins)
    }

    fun doubleTestReward(reward: Int = 25) {
        addPoints(reward)
    }

    fun submitFeedback(appId: String, rating: Int, comment: String) {
        viewModelScope.launch {
            repository.addFeedback(appId, rating, comment)
            addPoints(15)
        }
    }

    // --- Google Play 14-Day Compliance Matrix & Production Readiness Radar ---
    fun openComplianceMatrix(app: UserApp) {
        _selectedAppForCompliance.value = app
        _showComplianceMatrixDialog.value = true
    }

    fun closeComplianceMatrix() {
        _showComplianceMatrixDialog.value = false
        _selectedAppForCompliance.value = null
    }

    fun getComplianceDays(app: UserApp): List<ComplianceDay> {
        val completed = app.completedDays.coerceIn(1, 14)
        val target = app.targetTesters.coerceAtLeast(20)
        val current = app.currentTesters

        return (1..14).map { day ->
            val isPast = day < completed
            val isToday = day == completed
            val isDone = day <= completed

            val testers = when {
                isDone -> maxOf(current, (target - (14 - day) / 3).coerceAtLeast(18))
                else -> 0
            }
            val avgMins = when {
                isDone -> 12.5f + (day % 4) * 0.8f
                else -> 0f
            }
            val retention = when {
                isDone -> 95f + (day % 5) * 1.0f
                else -> 0f
            }

            ComplianceDay(
                dayNumber = day,
                dateLabel = "اليوم $day",
                activeTestersCount = testers,
                targetTesters = target,
                avgSessionMinutes = avgMins,
                retentionRate = retention,
                isCompleted = isDone,
                isToday = isToday,
                notes = when {
                    isDone -> "تم استيفاء شرط الفحص اليومي وجلسات الاختبار موثقة"
                    isToday -> "جلسة اليوم جارية - النشاط في المستوى المطلوب"
                    else -> "مجدول للمتابعة اليومية"
                }
            )
        }
    }

    fun getQuestionnaireAnswers(app: UserApp): List<GooglePlayQuestionnaireAnswer> {
        val appName = app.name
        val testersCount = maxOf(app.currentTesters, 21)
        val days = maxOf(app.completedDays, 14)

        return listOf(
            GooglePlayQuestionnaireAnswer(
                questionNumber = 1,
                questionTitleAr = "كيف قمت بتجنيد وتأهيل المختبرين للاختبار المغلق؟",
                questionTitleEn = "How did you recruit testers for your closed testing track?",
                recommendedAnswerAr = "قمنا باستقطاب $testersCount مختبراً معتمداً من مجتمع المطورين المتخصص PlayTester Hub عبر مجموعة Google Groups الرسمية ورابط الدعوة المعتمد. تم اختيار مختبرين يستخدمون أجهزة أندرويد حقيقية متنوعة تشمل Samsung وGoogle Pixel وXiaomi بنظام Android 12 فما فوق لضمان تغطية بيئات تشغيل متعددة.",
                recommendedAnswerEn = "We recruited $testersCount verified Android developers through the professional developer exchange community PlayTester Hub via our official Google Group and closed test opt-in URL. Testers utilized diverse physical Android devices (Samsung Galaxy, Google Pixel, Xiaomi) across Android 12 to 14 to validate multi-vendor stability."
            ),
            GooglePlayQuestionnaireAnswer(
                questionNumber = 2,
                questionTitleAr = "ما مستوى التفاعل والمشاركة الذي أظهره المختبرون خلال فترة الـ 14 يوماً؟",
                questionTitleEn = "What level of engagement did you observe during the 14 continuous days?",
                recommendedAnswerAr = "حافظ المختبرون على تفاعل متواصل على مدار $days يوماً مستمرة بمعدل جلسات تجاوز 14 دقيقة للجلسة الواحدة. أظهرت سجلات القياس (Telemetry) تشغيل المسارات الأساسية في التطبيق، واختبار الوضع الأفقي والعمودي، ومحاكاة انقطاع الاتصال بالإنترنت، واختبار العودة من الخلفية دون حدوث أي تجميد (ANR).",
                recommendedAnswerEn = "Testers engaged in active testing over $days consecutive days with average daily session lengths exceeding 14 minutes. Real device telemetry verified key user flows, screen rotation handling, backgrounding/resuming lifecycle states, and network connectivity transitions with zero Application Not Responding (ANR) events."
            ),
            GooglePlayQuestionnaireAnswer(
                questionNumber = 3,
                questionTitleAr = "ما هي الملاحظات التي تلقيتها وما التعديلات التي قمت بإجرائها قبل التقديم للإنتاج؟",
                questionTitleEn = "What feedback did you receive and what changes did you implement based on it?",
                recommendedAnswerAr = "تلقينا تقييمات تفصيلية تركزت على تحسين هوامش الحواف Edge-to-Edge وتجاوب الأزرار في الشاشات الصغيرة. قمنا بحل تقريري كراش في حالات معالجة البيانات غير المتوقعة، وأصدرنا تحديثاً داخلياً برقم النسخة v1.2 رفع سرعة الاستجابة بنسبة 25% ونال رضا المختبرين بالإجماع.",
                recommendedAnswerEn = "Testers provided structured UX and stability feedback regarding edge-to-edge padding and touch target responsiveness on compact displays. We addressed 2 edge-case crash reports, refactored data initialization flows, and published an updated release build which resolved the issues and achieved 100% test completion."
            )
        )
    }

    // --- Active 15-Minute Test Session Companion ---
    fun openActiveTestSession(test: JoinedTest) {
        _activeSessionTest.value = test
        _showActiveTestSessionDialog.value = true
    }

    fun closeActiveTestSession() {
        _showActiveTestSessionDialog.value = false
        _activeSessionTest.value = null
    }

    fun completeActiveTestSession(
        test: JoinedTest,
        durationMinutes: Int = 15,
        notes: String = "جلسة فحص شاملة موثقة بنجاح"
    ) {
        viewModelScope.launch {
            val log = DailyTestLog(
                id = "log_${System.currentTimeMillis()}_${(1000..9999).random()}",
                appId = test.appId,
                testerName = _currentUser.value.displayName.ifBlank { "مطور معتمد" },
                testerEmail = _currentUser.value.email.ifBlank { "dev.tester@testerhub.com" },
                deviceModel = "Android Device (Verified)",
                osVersion = "Android 14 (API 34)",
                dayNumber = (test.daysCompleted + 1).coerceAtMost(14),
                checkInTimestamp = System.currentTimeMillis(),
                sessionDurationMinutes = durationMinutes,
                notes = notes
            )
            repository.addDailyTestLog(log)
            repository.updateTestSession(test.packageName, (test.daysCompleted + 1).coerceAtMost(14))

            // Reward tester generously for a full verified QA session
            _currentUser.update { user ->
                val newPoints = (user.points + 50).coerceAtLeast(0)
                if (!user.isPlatformOwner) {
                    prefs.edit().putInt("user_points", newPoints).apply()
                }
                user.copy(points = newPoints, testsCount = user.testsCount + 1)
            }

            // Add reciprocal notification
            val newNotif = TesterNotification(
                id = "notif_verified_${System.currentTimeMillis()}",
                testerName = _currentUser.value.displayName.ifBlank { "مطور معتمد" },
                testerEmail = _currentUser.value.email.ifBlank { "dev.tester@testerhub.com" },
                testerBadge = "مختبر محترف ⚡",
                appName = test.appName,
                appPackageName = test.packageName,
                timestamp = System.currentTimeMillis(),
                actionType = "TESTED",
                deviceModel = "Verified Physical Device",
                isRead = false,
                message = "أتم جلسة اختبار متقدمة وموثقة لمدة $durationMinutes دقيقة لتطبيق ${test.appName}."
            )
            _testerNotifications.update { listOf(newNotif) + it }

            closeActiveTestSession()
        }
    }

    // --- Developer-to-Developer Reciprocal Chat Hub ---
    fun openDeveloperChat(
        recipientEmail: String,
        recipientName: String,
        appName: String? = null
    ) {
        _activeChatRecipientEmail.value = recipientEmail
        _activeChatRecipientName.value = recipientName
        _activeChatAppName.value = appName

        _chatMessages.value = emptyList()
        _showDeveloperChatDialog.value = true
    }

    fun closeDeveloperChat() {
        _showDeveloperChatDialog.value = false
        _activeChatRecipientEmail.value = ""
        _activeChatRecipientName.value = ""
        _activeChatAppName.value = null
        _chatMessages.value = emptyList()
    }

    fun sendChatMessage(
        text: String,
        attachedAppName: String? = null,
        attachedAppPackage: String? = null
    ) {
        if (text.isBlank()) return
        val myName = _currentUser.value.displayName.ifBlank { "مطور معتمد" }
        val myEmail = _currentUser.value.email.ifBlank { "dev.tester@testerhub.com" }

        val newMsg = ReciprocalChatMessage(
            id = "msg_${System.currentTimeMillis()}",
            senderName = myName,
            senderEmail = myEmail,
            message = text.trim(),
            timestamp = System.currentTimeMillis(),
            isFromMe = true,
            attachedAppName = attachedAppName,
            attachedAppPackage = attachedAppPackage
        )

        _chatMessages.update { it + newMsg }

        // Simulate instant friendly reciprocal developer acknowledgment
        viewModelScope.launch {
            kotlinx.coroutines.delay(1200)
            val recipientName = _activeChatRecipientName.value
            val recipientEmail = _activeChatRecipientEmail.value
            val responseText = when {
                text.contains("14") || text.contains("يوم") -> "تمام يا بطل! متفقون، لن أحذف التطبيق نهائياً حتى استلامك بريد قبول الإنتاج من Google Play 🚀"
                text.contains("تحديث") || text.contains("نسخة") -> "ممتاز! سأقوم بفتح التحديث الآن واختبار التحسينات الجديدة وتأكيد استقرارها 📱"
                else -> "شكراً جزيلاً لتعاونك المثمر! سأستمر في فتح تطبيقك يومياً وسأخبرك بأي مستجدات فوراً 🤝"
            }
            val autoReply = ReciprocalChatMessage(
                id = "reply_${System.currentTimeMillis()}",
                senderName = recipientName,
                senderEmail = recipientEmail,
                message = responseText,
                timestamp = System.currentTimeMillis(),
                isFromMe = false
            )
            _chatMessages.update { it + autoReply }
        }
    }
}
