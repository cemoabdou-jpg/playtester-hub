package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.data.remote.CloudSyncService
import com.example.data.remote.FirestoreService
import com.example.data.repository.TesterRepository
import com.example.ui.util.L
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TesterViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TesterRepository
    private val prefs: SharedPreferences =
        application.getSharedPreferences("playtester_auth_prefs", Context.MODE_PRIVATE)

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

    private val _showAppCommandCenter = MutableStateFlow(false)
    val showAppCommandCenter: StateFlow<Boolean> = _showAppCommandCenter.asStateFlow()

    private val _commandCenterApp = MutableStateFlow<UserApp?>(null)
    val commandCenterApp: StateFlow<UserApp?> = _commandCenterApp.asStateFlow()

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

    private val _showDeveloperInboxDialog = MutableStateFlow(false)
    val showDeveloperInboxDialog: StateFlow<Boolean> = _showDeveloperInboxDialog.asStateFlow()

    private val _userConversations = MutableStateFlow<List<DeveloperConversation>>(emptyList())
    val userConversations: StateFlow<List<DeveloperConversation>> = _userConversations.asStateFlow()

    private val _testerNotifications = MutableStateFlow<List<TesterNotification>>(emptyList())
    val testerNotifications: StateFlow<List<TesterNotification>> = _testerNotifications.asStateFlow()

    private val _requestedTabIndex = MutableStateFlow<Int?>(null)
    val requestedTabIndex: StateFlow<Int?> = _requestedTabIndex.asStateFlow()

    private val _streakDays = MutableStateFlow(1)
    val streakDays: StateFlow<Int> = _streakDays.asStateFlow()

    private val _isDailyCheckInDoneToday = MutableStateFlow(false)
    val isDailyCheckInDoneToday: StateFlow<Boolean> = _isDailyCheckInDoneToday.asStateFlow()

    private val _isDailyBonusClaimedToday = MutableStateFlow(false)
    val isDailyBonusClaimedToday: StateFlow<Boolean> = _isDailyBonusClaimedToday.asStateFlow()

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

    private var profileSyncJob: Job? = null
    private var notificationsSyncJob: Job? = null
    private var conversationsSyncJob: Job? = null
    private var chatMessagesJob: Job? = null

    init {
        val database = AppDatabase.getInstance(application)
        repository = TesterRepository(database.appDao())

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

        val today = getTodayDateString()
        _isDailyCheckInDoneToday.value = (prefs.getString("last_check_in_date", "") == today)
        _isDailyBonusClaimedToday.value = (prefs.getString("last_daily_bonus_date", "") == today)
        _streakDays.value = prefs.getInt("streak_days_count", 1)

        // Check saved verified session
        val savedEmail = prefs.getString("user_email", null)
        val savedName = prefs.getString("user_name", null)
        val isVerified = prefs.getBoolean("is_verified", false)

        if (!savedEmail.isNullOrBlank() && isVerified) {
            val cleanEmail = savedEmail.trim().lowercase()
            val isOwner = cleanEmail.equals(PLATFORM_OWNER_EMAIL, ignoreCase = true)
            val resolvedName = savedName?.let { L.translateKnown(it) }
                ?: if (isOwner) L.t("صاحب المنصة (عبدالرحمن)", "Platform Owner (Abdelrahman)")
                else L.t("مطور معتمد", "Verified Developer")

            _currentUser.value = AuthUser(
                uid = if (isOwner) "owner_cemoabdou" else "user_${cleanEmail.hashCode()}",
                email = cleanEmail,
                displayName = resolvedName,
                points = if (isOwner) 9999 else prefs.getInt("user_points", 0),
                isVip = isOwner,
                isEmailVerified = true,
                referralCode = CloudSyncService.generateReferralCode(cleanEmail),
                streakDays = _streakDays.value
            )
            restoreCloudSession(cleanEmail, resolvedName)
        }
    }

    private fun getTodayDateString(): String {
        return java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date())
    }

    private fun restoreCloudSession(
        email: String,
        displayName: String,
        referralCodeInput: String = ""
    ) {
        val cleanEmail = email.trim().lowercase()
        if (cleanEmail.isBlank()) return
        val isOwner = cleanEmail.equals(PLATFORM_OWNER_EMAIL, ignoreCase = true)
        val defaultUid = if (isOwner) "owner_cemoabdou" else "user_${cleanEmail.hashCode()}"

        profileSyncJob?.cancel()
        notificationsSyncJob?.cancel()
        conversationsSyncJob?.cancel()

        viewModelScope.launch {
            val profile = repository.fetchOrCreateUserProfile(
                email = cleanEmail,
                displayName = displayName,
                defaultUid = defaultUid,
                isOwner = isOwner
            )
            if (profile != null) {
                applyCloudProfile(profile)
            }
            repository.restoreUserWorkspaceFromCloud(cleanEmail)

            if (referralCodeInput.isNotBlank()) {
                repository.redeemReferralCode(cleanEmail, displayName, referralCodeInput)
            }
        }

        profileSyncJob = viewModelScope.launch {
            repository.getUserProfileFlow(cleanEmail).collect { profile ->
                if (profile != null) {
                    applyCloudProfile(profile)
                }
            }
        }

        notificationsSyncJob = viewModelScope.launch {
            repository.getUserNotificationsFlow(cleanEmail).collect { notifList ->
                _testerNotifications.value = notifList
            }
        }

        conversationsSyncJob = viewModelScope.launch {
            repository.getUserConversationsFlow(cleanEmail).collect { convList ->
                _userConversations.value = convList
            }
        }
    }

    private fun applyCloudProfile(profile: CloudUserProfile) {
        val today = getTodayDateString()
        val isOwner = profile.email.equals(PLATFORM_OWNER_EMAIL, ignoreCase = true)
        val resolvedName = profile.displayName.ifBlank {
            if (isOwner) L.t("صاحب المنصة (عبدالرحمن)", "Platform Owner (Abdelrahman)")
            else L.t("مطور معتمد", "Verified Developer")
        }

        _currentUser.update { current ->
            current.copy(
                uid = profile.uid.ifBlank { current.uid },
                email = profile.email,
                displayName = resolvedName,
                points = if (isOwner) maxOf(profile.points, 9999) else profile.points,
                isVip = profile.isVip || isOwner,
                isEmailVerified = true,
                appsCount = maxOf(profile.appsCount, userApps.value.size),
                testsCount = maxOf(profile.testsCount, joinedTests.value.size),
                referralCode = profile.referralCode.ifBlank { CloudSyncService.generateReferralCode(profile.email) },
                referredBy = profile.referredBy,
                invitedDevsCount = profile.invitedDevsCount,
                streakDays = maxOf(1, profile.streakDays)
            )
        }
        _streakDays.value = maxOf(1, profile.streakDays)
        _isDailyCheckInDoneToday.value = (profile.lastCheckInDate == today)
        _isDailyBonusClaimedToday.value = (profile.lastDailyBonusDate == today)
        if (profile.claimedBadgeIds.isNotEmpty()) {
            _claimedBadgeIds.value = profile.claimedBadgeIds.toSet()
        }

        if (!isOwner) {
            prefs.edit()
                .putInt("user_points", profile.points)
                .putInt("streak_days_count", maxOf(1, profile.streakDays))
                .putString("last_check_in_date", profile.lastCheckInDate)
                .putString("last_daily_bonus_date", profile.lastDailyBonusDate)
                .apply()
        }
    }

    fun navigateToTab(index: Int) {
        _requestedTabIndex.value = index
    }

    fun consumeRequestedTab() {
        _requestedTabIndex.value = null
    }

    fun openAppCommandCenter(app: UserApp) {
        _commandCenterApp.value = app
        _selectedAppForReport.value = app
        _showAppCommandCenter.value = true
    }

    fun closeAppCommandCenter() {
        _showAppCommandCenter.value = false
        _commandCenterApp.value = null
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

    fun openEmailVerification(
        email: String = _currentUser.value.email,
        name: String = _currentUser.value.displayName
    ) {
        _pendingEmail.value = email
        _pendingName.value = name
        _showEmailVerificationDialog.value = true
    }

    fun closeEmailVerification() {
        _showEmailVerificationDialog.value = false
    }

    fun addPoints(amount: Int) {
        val email = _currentUser.value.email.trim()
        _currentUser.update { user ->
            val newPoints = (user.points + amount).coerceAtLeast(0)
            if (!user.isPlatformOwner) {
                prefs.edit().putInt("user_points", newPoints).apply()
            }
            user.copy(points = newPoints)
        }
        if (email.isNotBlank() && amount != 0) {
            viewModelScope.launch {
                repository.incrementUserPoints(email, amount)
            }
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

    @Synchronized
    fun claimDailyBonus(): Boolean {
        val today = getTodayDateString()
        val lastDate = prefs.getString("last_daily_bonus_date", "")
        if (lastDate == today || _isDailyBonusClaimedToday.value) {
            return false
        }
        _isDailyBonusClaimedToday.value = true
        prefs.edit().putString("last_daily_bonus_date", today).apply()
        addPoints(50)

        val email = _currentUser.value.email.trim()
        if (email.isNotBlank()) {
            viewModelScope.launch {
                repository.updateUserProfileFields(email, mapOf("lastDailyBonusDate" to today))
            }
        }
        return true
    }

    /**
     * Quick Verified Session Sign-In & Cloud Workspace Restoration
     */
    fun completeQuickSignIn(
        email: String,
        name: String,
        referralCode: String = "",
        onResult: (Boolean, String?) -> Unit = { _, _ -> }
    ) {
        val cleanEmail = email.trim().lowercase()
        val cleanName = name.trim()
        if (cleanEmail.isBlank() || !cleanEmail.contains("@") || !cleanEmail.contains(".")) {
            onResult(false, L.t("يرجى إدخال بريد إلكتروني صالح", "Please enter a valid email address"))
            return
        }
        if (cleanName.isBlank()) {
            onResult(false, L.t("يرجى كتابة اسمك أو اسم المطور", "Please enter your name or developer studio name"))
            return
        }

        val isOwner = cleanEmail.equals(PLATFORM_OWNER_EMAIL, ignoreCase = true)
        val displayName = if (isOwner) L.t("صاحب المنصة (عبدالرحمن)", "Platform Owner (Abdelrahman)") else cleanName

        prefs.edit()
            .putString("user_email", cleanEmail)
            .putString("user_name", displayName)
            .putBoolean("is_verified", true)
            .apply()

        _verificationCode.value = ""
        _lastSentCode.value = null
        _pendingEmail.value = cleanEmail
        _pendingName.value = displayName

        _currentUser.value = AuthUser(
            uid = if (isOwner) "owner_cemoabdou" else "user_${cleanEmail.hashCode()}",
            email = cleanEmail,
            displayName = displayName,
            points = if (isOwner) 9999 else 0,
            isVip = isOwner,
            isEmailVerified = true,
            referralCode = CloudSyncService.generateReferralCode(cleanEmail),
            streakDays = _streakDays.value
        )

        _showEmailVerificationDialog.value = false
        restoreCloudSession(cleanEmail, displayName, referralCode.trim())
        onResult(true, null)
    }

    fun startEmailVerification(email: String, name: String) {
        _pendingEmail.value = email.trim()
        _pendingName.value = name.trim()
        val code = (100000..999999).random().toString()
        _verificationCode.value = code
        _lastSentCode.value = code
    }

    fun confirmVerificationCode(enteredCode: String): Boolean {
        if (enteredCode.trim() == _verificationCode.value && _verificationCode.value.isNotBlank()) {
            val email = _pendingEmail.value.trim()
            val name = if (_pendingName.value.isNotBlank()) _pendingName.value.trim() else L.t("مطور معتمد", "Verified Developer")
            completeQuickSignIn(email, name)
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

    fun redeemReferralCode(code: String, onResult: (Boolean, String) -> Unit) {
        val user = _currentUser.value
        if (user.email.isBlank()) {
            onResult(false, L.t("يرجى تسجيل الدخول أولاً", "Please sign in first"))
            return
        }
        viewModelScope.launch {
            val result = repository.redeemReferralCode(user.email, user.displayName, code)
            result.fold(
                onSuccess = { bonus ->
                    onResult(
                        true,
                        L.t("تم تفعيل رمز الدعوة بنجاح! حصلت على +$bonus عملة ترحيبية", "Referral redeemed! You earned +$bonus bonus coins")
                    )
                },
                onFailure = { err ->
                    onResult(
                        false,
                        err.message ?: L.t("تعذر تفعيل رمز الدعوة", "Could not redeem referral code")
                    )
                }
            )
        }
    }

    fun getCrashReports(appId: String): Flow<List<CrashReport>> = repository.getCrashReports(appId)

    fun getDailyTestLogs(appId: String): Flow<List<DailyTestLog>> = repository.getDailyTestLogs(appId)

    fun getFeedback(appId: String): Flow<List<TesterFeedback>> = repository.getFeedback(appId)

    fun resolveCrash(id: String, note: String, packageName: String? = null) {
        val resolvedPkg = packageName
            ?: _selectedAppForReport.value?.packageName
            ?: userApps.value.firstOrNull()?.packageName
            ?: return
        viewModelScope.launch {
            repository.resolveCrashReport(resolvedPkg, id, note)
        }
    }

    fun reportAppCrash(
        packageName: String,
        appName: String,
        title: String,
        details: String,
        onResult: (Boolean) -> Unit = {}
    ) {
        if (title.isBlank()) {
            onResult(false)
            return
        }
        val user = _currentUser.value
        val now = System.currentTimeMillis()
        val report = CrashReport(
            id = "crash_${now}_${(100..999).random()}",
            appId = packageName,
            title = title.trim(),
            stackTrace = details.trim().ifBlank { L.t("تم الإبلاغ من خلال جلسة الاختبار المغلق", "Reported during closed testing session") },
            deviceModel = Build.MODEL ?: "Android",
            osVersion = "Android ${Build.VERSION.RELEASE}",
            appVersion = "Closed Track",
            occurrences = 1,
            status = "OPEN",
            reportedAt = now
        )
        viewModelScope.launch {
            repository.submitCrashReport(packageName, report)
            addPoints(15)
            dispatchAppOwnerNotification(
                packageName = packageName,
                appName = appName,
                actionType = "CRASH_REPORT",
                message = L.t(
                    "أبلغ عن مشكلة تقنية في تطبيق $appName: ${title.trim()}",
                    "Reported an issue in $appName: ${title.trim()}"
                )
            )
            onResult(true)
        }
    }

    fun joinCommunityApp(app: CommunityApp): Boolean {
        val alreadyJoined = joinedTests.value.any { it.packageName == app.packageName }
        if (alreadyJoined) return false

        val user = _currentUser.value
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
            repository.joinTest(joined, user.email)
            addPoints(app.pointsReward)
            _currentUser.update { it.copy(testsCount = it.testsCount + 1) }

            if (user.email.isNotBlank()) {
                repository.updateUserProfileFields(
                    user.email,
                    mapOf("testsCount" to (_currentUser.value.testsCount))
                )
            }

            dispatchAppOwnerNotification(
                packageName = app.packageName,
                appName = app.name,
                fallbackTargetEmail = app.developerEmail,
                actionType = "JOINED",
                message = L.t(
                    "انضم إلى فريق مختبري تطبيق ${app.name} وبدأ مسار الـ 14 يوماً.",
                    "Joined closed testing for ${app.name} and started the 14-day track."
                )
            )
        }
        return true
    }

    fun addUserApp(
        name: String,
        pkg: String,
        optIn: String,
        grp: String,
        cat: String = L.t("تطبيق", "App"),
        desc: String = "",
        iconUrl: String = "",
        initialTesters: Int = 0,
        initialDays: Int = 1
    ) {
        val user = _currentUser.value
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
                developerName = user.displayName,
                developerEmail = user.email,
                userId = user.uid,
                initialTesters = initialTesters,
                completedDays = initialDays
            )
            val newAppsCount = user.appsCount + 1
            _currentUser.update { it.copy(appsCount = newAppsCount) }
            if (user.email.isNotBlank()) {
                repository.updateUserProfileFields(user.email, mapOf("appsCount" to newAppsCount))
            }
        }
    }

    fun updateAppTesters(packageName: String, realTestersCount: Int, completedDays: Int? = null) {
        val ownerEmail = _currentUser.value.email
        viewModelScope.launch {
            repository.updateAppTesters(packageName, realTestersCount, completedDays, ownerEmail)
        }
    }

    fun updateAppIcon(packageName: String, newIconUrl: String) {
        val ownerEmail = _currentUser.value.email
        viewModelScope.launch {
            repository.updateAppIcon(packageName, newIconUrl, ownerEmail)
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
        val ownerEmail = _currentUser.value.email
        viewModelScope.launch {
            repository.updateAppDetails(packageName, name, category, iconUrl, webOptInUrl, googleGroupUrl, ownerEmail)
        }
    }

    fun removeUserApp(app: UserApp) {
        val ownerEmail = _currentUser.value.email
        viewModelScope.launch {
            repository.removeUserApp(app, ownerEmail)
            val updatedCount = maxOf(0, _currentUser.value.appsCount - 1)
            _currentUser.update { it.copy(appsCount = updatedCount) }
            if (ownerEmail.isNotBlank()) {
                repository.updateUserProfileFields(ownerEmail, mapOf("appsCount" to updatedCount))
            }
        }
    }

    fun promoteApp(app: UserApp, cost: Int = 350): Boolean {
        val user = _currentUser.value
        if (user.points >= cost) {
            if (cost > 0) {
                addPoints(-cost)
            }
            viewModelScope.launch {
                repository.promoteApp(app.packageName, true)
                if (user.email.isNotBlank()) {
                    val savedApp = app.copy(isVip = true)
                    repository.updateAppTesters(savedApp.packageName, savedApp.currentTesters, savedApp.completedDays, user.email)
                }
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
        val name = L.t("صاحب المنصة (عبدالرحمن)", "Platform Owner (Abdelrahman)")
        completeQuickSignIn(email, name)
    }

    fun logout() {
        profileSyncJob?.cancel()
        notificationsSyncJob?.cancel()
        conversationsSyncJob?.cancel()
        chatMessagesJob?.cancel()

        // Preserve non-session app preferences while clearing only session credentials
        prefs.edit()
            .remove("user_email")
            .remove("user_name")
            .remove("is_verified")
            .remove("user_points")
            .remove("last_check_in_date")
            .remove("last_daily_bonus_date")
            .remove("streak_days_count")
            .apply()

        _verificationCode.value = ""
        _lastSentCode.value = null
        _pendingEmail.value = ""
        _pendingName.value = ""
        _testerNotifications.value = emptyList()
        _userConversations.value = emptyList()
        _claimedBadgeIds.value = emptySet()
        _streakDays.value = 1
        _isDailyCheckInDoneToday.value = false
        _isDailyBonusClaimedToday.value = false
        _showAdminControlDialog.value = false

        _currentUser.value = AuthUser(
            uid = "guest_unverified",
            email = "",
            displayName = "",
            points = 0,
            isVip = false,
            isEmailVerified = false
        )

        viewModelScope.launch {
            repository.clearLocalSessionTables()
        }
    }

    fun logoutToRegularUser() {
        val email = "developer@testerhub.com"
        val name = L.t("مطور تجريبي", "Beta Developer")
        viewModelScope.launch {
            repository.clearLocalSessionTables()
            completeQuickSignIn(email, name)
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
        val email = _currentUser.value.email.trim()
        if (email.isNotBlank()) {
            viewModelScope.launch {
                repository.markNotificationRead(email, id)
            }
        }
    }

    fun markAllNotificationsAsRead() {
        val unreadIds = _testerNotifications.value.filter { !it.isRead }.map { it.id }
        _testerNotifications.update { list ->
            list.map { it.copy(isRead = true) }
        }
        val email = _currentUser.value.email.trim()
        if (email.isNotBlank() && unreadIds.isNotEmpty()) {
            viewModelScope.launch {
                repository.markAllNotificationsRead(email, unreadIds)
            }
        }
    }

    private suspend fun dispatchAppOwnerNotification(
        packageName: String,
        appName: String,
        fallbackTargetEmail: String = "",
        actionType: String,
        message: String
    ) {
        val sender = _currentUser.value
        val senderName = sender.displayName.ifBlank { L.t("مطور معتمد", "Verified Developer") }
        val senderEmail = sender.email.trim()
        val resolvedTarget = fallbackTargetEmail.ifBlank {
            communityApps.value.find { it.packageName == packageName }?.developerEmail.orEmpty()
        }.trim()

        if (resolvedTarget.isBlank() || resolvedTarget.equals(senderEmail, ignoreCase = true)) {
            return
        }

        val notif = TesterNotification(
            id = "notif_${actionType.lowercase()}_${System.currentTimeMillis()}_${(100..999).random()}",
            testerName = senderName,
            testerEmail = senderEmail,
            testerBadge = if (sender.isPlatformOwner) L.t("صاحب المنصة", "Platform Owner") else L.t("مختبر معتمد", "Verified Tester"),
            appName = appName,
            appPackageName = packageName,
            timestamp = System.currentTimeMillis(),
            actionType = actionType,
            deviceModel = Build.MODEL ?: "Android",
            isRead = false,
            message = message
        )
        repository.sendNotificationToUser(resolvedTarget, notif)
    }

    fun openTesterProfile(testerEmail: String, testerName: String) {
        val allApps = communityApps.value
        val cleanEmail = testerEmail.trim()
        val cleanName = testerName.trim()

        val matchedApps = allApps.filter { app ->
            (cleanEmail.isNotBlank() && app.developerEmail.equals(cleanEmail, ignoreCase = true)) ||
                (cleanName.isNotBlank() && (app.developerName.contains(cleanName, ignoreCase = true) || cleanName.contains(app.developerName, ignoreCase = true)))
        }

        val completedCount = matchedApps.sumOf { it.currentTesters }.coerceAtLeast(1)
        val badge = if (matchedApps.any { it.isFeatured }) {
            L.t("مطور مميز 💎", "Featured Developer 💎")
        } else {
            L.t("مطور معتمد ⭐", "Verified Developer ⭐")
        }

        _selectedTesterProfile.value = TesterProfileData(
            name = L.translateKnown(cleanName.ifBlank { L.t("مطور مسجل", "Registered Developer") }),
            email = cleanEmail,
            badge = badge,
            rating = 5.0f,
            testsCompleted = completedCount,
            reputationPoints = completedCount * 50,
            deviceModel = L.t("جهاز أندرويد معتمد", "Verified Android Device"),
            bio = L.t(
                "مطور مسجل في مجتمع منصة المختبرين، يشارك في اختبار التطبيقات وتبادل الفحص للوصول إلى شرط 14 يوماً و20 مختبراً.",
                "Registered developer in PlayTester Hub, actively exchanging tests to reach the 14-day / 20-tester requirement."
            ),
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

    fun openAndTestApp(context: Context, test: JoinedTest, onRewarded: (Int) -> Unit) {
        val reward = 25
        val user = _currentUser.value
        viewModelScope.launch {
            repository.recordDailyAppTest(
                packageName = test.packageName,
                testerEmail = user.email,
                testerName = user.displayName
            )
            addPoints(reward)
            dispatchAppOwnerNotification(
                packageName = test.packageName,
                appName = test.appName,
                actionType = "TESTED",
                message = L.t(
                    "قام باختبار تطبيق ${test.appName} اليوم وسجل جلسة نشطة.",
                    "Tested ${test.appName} today and logged an active session."
                )
            )
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
            android.widget.Toast.makeText(
                context,
                L.t("تم تسجيل اختبار ${test.appName} بنجاح (+25 عملة)", "Test recorded for ${test.appName} (+25 coins)"),
                android.widget.Toast.LENGTH_SHORT
            ).show()
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
            val updatedBadges = _claimedBadgeIds.value + badgeId
            _claimedBadgeIds.value = updatedBadges
            addPoints(rewardPoints)
            val email = _currentUser.value.email.trim()
            if (email.isNotBlank()) {
                viewModelScope.launch {
                    repository.updateUserProfileFields(
                        email,
                        mapOf("claimedBadgeIds" to updatedBadges.toList())
                    )
                }
            }
        }
    }

    fun getBadges(
        user: AuthUser,
        apps: List<UserApp>,
        joined: List<JoinedTest>
    ): List<AchievementBadge> {
        val claimed = _claimedBadgeIds.value

        val maxDaysCompleted = joined.maxOfOrNull { it.daysCompleted } ?: 0
        val isTesterUnlocked = maxDaysCompleted >= 7
        val isBugHunterUnlocked = joined.isNotEmpty() || apps.any { it.completedDays >= 3 }
        val maxTesters = apps.maxOfOrNull { it.currentTesters } ?: 0
        val isDevUnlocked = maxTesters >= 20
        val isSupporterUnlocked = user.testsCount >= 3 || joined.size >= 3
        val isPromoterUnlocked = apps.any { it.isVip }

        return listOf(
            AchievementBadge(
                id = "badge_perfect_tester",
                title = L.t("وسام المختبر المثالي", "Perfect Tester Badge"),
                description = L.t("فتح واختبار تطبيقات الآخرين لـ 7 أيام متتالية", "Open and test peers' apps for 7 consecutive days"),
                emoji = "🏅",
                coinReward = 150,
                currentProgress = minOf(7, maxDaysCompleted),
                targetProgress = 7,
                isUnlocked = isTesterUnlocked,
                isClaimed = claimed.contains("badge_perfect_tester")
            ),
            AchievementBadge(
                id = "badge_bug_hunter",
                title = L.t("وسام صائد الأخطاء", "Bug Hunter Badge"),
                description = L.t("تقديم ملاحظات أعطال واختبار استقرار التطبيقات", "Submit stability feedback and report app issues"),
                emoji = "🐛",
                coinReward = 100,
                currentProgress = if (isBugHunterUnlocked) 1 else 0,
                targetProgress = 1,
                isUnlocked = isBugHunterUnlocked,
                isClaimed = claimed.contains("badge_bug_hunter")
            ),
            AchievementBadge(
                id = "badge_verified_dev",
                title = L.t("وسام المطور المعتمد", "Verified Developer Badge"),
                description = L.t("إكمال 20 مختبراً معتمداً لتطبيقك في متجر جوجل بلاي", "Reach 20 verified testers for your app on Google Play"),
                emoji = "🚀",
                coinReward = 200,
                currentProgress = minOf(20, maxTesters),
                targetProgress = 20,
                isUnlocked = isDevUnlocked,
                isClaimed = claimed.contains("badge_verified_dev")
            ),
            AchievementBadge(
                id = "badge_active_supporter",
                title = L.t("وسام الداعم النشط", "Active Supporter Badge"),
                description = L.t("الانضمام لاختبار 3 تطبيقات زملاء في المجتمع", "Join closed testing for 3 community peer apps"),
                emoji = "🌟",
                coinReward = 75,
                currentProgress = minOf(3, maxOf(user.testsCount, joined.size)),
                targetProgress = 3,
                isUnlocked = isSupporterUnlocked,
                isClaimed = claimed.contains("badge_active_supporter")
            ),
            AchievementBadge(
                id = "badge_featured_promoter",
                title = L.t("وسام نجم الصدارة", "Spotlight Star Badge"),
                description = L.t("ترقية تطبيقك إلى الصدارة لجذب المختبرين بأولوية", "Promote your app to Spotlight to attract priority testers"),
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

        if (currentUser.displayName.isNotBlank()) {
            val key = currentUser.email.ifBlank { currentUser.displayName }
            devMap[key] = LeaderboardUser(
                rank = 1,
                name = L.translateKnown(currentUser.displayName),
                email = currentUser.email,
                testsCompleted = currentUser.testsCount,
                points = currentUser.points,
                badgeTitle = if (currentUser.isPlatformOwner) {
                    L.t("صاحب المنصة 👑", "Platform Owner 👑")
                } else {
                    L.t("مطور نشط ⭐", "Active Developer ⭐")
                },
                isCurrentUser = true
            )
        }

        communityApps.value.forEach { app ->
            val devName = app.developerName.trim()
            if (devName.isNotBlank() && devName != "المطور المعتمد" && devName != "Verified Developer") {
                val isSelf = (app.developerEmail.isNotBlank() && app.developerEmail.equals(currentUser.email, ignoreCase = true)) ||
                    currentUser.displayName.equals(devName, ignoreCase = true)
                if (!isSelf && !devMap.containsKey(devName)) {
                    val safeEmail = app.developerEmail.ifBlank {
                        "${devName.lowercase().replace(" ", "").replace("[^a-zA-Z0-9]".toRegex(), "")}@developer.com"
                    }
                    devMap[devName] = LeaderboardUser(
                        rank = 0,
                        name = L.translateKnown(devName),
                        email = safeEmail,
                        testsCompleted = app.currentTesters,
                        points = app.currentTesters * 50,
                        badgeTitle = if (app.isFeatured) {
                            L.t("مطور مميز 💎", "Featured Developer 💎")
                        } else {
                            L.t("مطور معتمد ⭐", "Verified Developer ⭐")
                        },
                        isCurrentUser = false
                    )
                }
            }
        }

        return devMap.values
            .sortedByDescending { it.points }
            .mapIndexed { index, user -> user.copy(rank = index + 1) }
    }

    fun openLuckySpin() {
        _showLuckySpinDialog.value = true
    }

    fun closeLuckySpin() {
        _showLuckySpinDialog.value = false
    }

    @Synchronized
    fun dailyCheckIn(): Boolean {
        val today = getTodayDateString()
        val lastDate = prefs.getString("last_check_in_date", "")
        if (lastDate == today || _isDailyCheckInDoneToday.value) {
            return false
        }
        _isDailyCheckInDoneToday.value = true
        val newStreak = _streakDays.value + 1
        _streakDays.value = newStreak
        prefs.edit()
            .putString("last_check_in_date", today)
            .putInt("streak_days_count", newStreak)
            .apply()

        _currentUser.update { it.copy(streakDays = newStreak) }
        addPoints(20)

        val email = _currentUser.value.email.trim()
        if (email.isNotBlank()) {
            viewModelScope.launch {
                repository.updateUserProfileFields(
                    email,
                    mapOf("lastCheckInDate" to today, "streakDays" to newStreak)
                )
            }
        }
        return true
    }

    fun claimSpinReward(coins: Int) {
        addPoints(coins)
    }

    fun doubleTestReward(reward: Int = 25) {
        addPoints(reward)
    }

    fun submitFeedback(appId: String, rating: Int, comment: String) {
        val user = _currentUser.value
        val testerName = user.displayName.ifBlank { L.t("مختبر معتمد", "Verified Tester") }
        viewModelScope.launch {
            repository.addFeedback(appId, testerName, rating, comment)
            addPoints(15)
            val targetAppName = communityApps.value.find { it.packageName == appId }?.name ?: appId
            dispatchAppOwnerNotification(
                packageName = appId,
                appName = targetAppName,
                actionType = "FEEDBACK",
                message = L.t(
                    "أرسل تقييماً ($rating/5) لتطبيق $targetAppName: $comment",
                    "Submitted a $rating/5 review for $targetAppName: $comment"
                )
            )
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

    fun getComplianceDays(
        app: UserApp,
        realLogs: List<DailyTestLog> = emptyList()
    ): List<ComplianceDay> {
        val completed = app.completedDays.coerceIn(1, 14)
        val target = app.targetTesters.coerceAtLeast(20)
        val logsByDay = realLogs.groupBy { it.dayNumber }

        return (1..14).map { day ->
            val dayLogs = logsByDay[day].orEmpty()
            val isToday = day == completed
            val isDone = day <= completed || dayLogs.isNotEmpty()

            val loggedTestersCount = dayLogs.map { it.testerEmail.ifBlank { it.testerName } }.distinct().size
            val testers = when {
                loggedTestersCount > 0 -> maxOf(loggedTestersCount, app.currentTesters)
                isDone -> app.currentTesters
                else -> 0
            }
            val avgMins = when {
                dayLogs.isNotEmpty() -> dayLogs.map { it.sessionDurationMinutes }.average().toFloat()
                isDone && app.currentTesters > 0 -> 15.0f
                else -> 0f
            }
            val retention = if (isDone && target > 0) {
                ((testers.toFloat() / target.toFloat()) * 100f).coerceIn(0f, 100f)
            } else {
                0f
            }

            ComplianceDay(
                dayNumber = day,
                dateLabel = L.t("اليوم $day", "Day $day"),
                activeTestersCount = testers,
                targetTesters = target,
                avgSessionMinutes = avgMins,
                retentionRate = retention,
                isCompleted = isDone,
                isToday = isToday,
                notes = when {
                    dayLogs.isNotEmpty() -> L.t(
                        "تم توثيق ${dayLogs.size} جلسة اختبار حقيقية عبر المنصة",
                        "${dayLogs.size} verified platform session(s) recorded"
                    )
                    isDone -> L.t(
                        "مسجل ضمن فترة الـ 14 يوماً في لوحة مطوري جوجل بلاي",
                        "Tracked as part of your 14-day Play Console window"
                    )
                    else -> L.t(
                        "مجدول للمتابعة اليومية",
                        "Scheduled for daily follow-up"
                    )
                }
            )
        }
    }

    fun getQuestionnaireAnswers(app: UserApp): List<GooglePlayQuestionnaireAnswer> {
        val testersCount = maxOf(app.currentTesters, 20)
        val days = maxOf(app.completedDays, 14)

        return listOf(
            GooglePlayQuestionnaireAnswer(
                questionNumber = 1,
                questionTitleAr = "كيف قمت بتجنيد وتأهيل المختبرين للاختبار المغلق؟",
                questionTitleEn = "How did you recruit testers for your closed testing track?",
                recommendedAnswerAr = "قمنا بدعوة $testersCount مختبراً من مجتمع المطورين عبر مجموعة جوجل الرسمية ورابط الانضمام للاختبار المغلق، باستخدام أجهزة أندرويد حقيقية ومتنوعة عبر إصدارات النظام المختلفة للتحقق من التوافق والاستقرار.",
                recommendedAnswerEn = "We recruited $testersCount Android developers through our developer testing group and closed track opt-in link, testing across diverse physical Android devices and OS versions to validate compatibility."
            ),
            GooglePlayQuestionnaireAnswer(
                questionNumber = 2,
                questionTitleAr = "ما مستوى التفاعل والمشاركة الذي أظهره المختبرون خلال فترة الـ 14 يوماً؟",
                questionTitleEn = "What level of engagement did you observe during the 14 continuous days?",
                recommendedAnswerAr = "شارك المختبرون في جلسات فحص منتظمة على مدار $days يوماً متصلة، شملت اختبار الشاشات الرئيسية وتدوير الشاشة وحالات انقطاع الشبكة والعودة من الخلفية لضمان سلاسة الأداء.",
                recommendedAnswerEn = "Testers engaged consistently over $days consecutive days, exercising core user journeys, screen rotation, background/resume transitions, and connectivity changes without hangs or ANR events."
            ),
            GooglePlayQuestionnaireAnswer(
                questionNumber = 3,
                questionTitleAr = "ما هي الملاحظات التي تلقيتها وما التعديلات التي قمت بإجرائها قبل التقديم للإنتاج؟",
                questionTitleEn = "What feedback did you receive and what changes did you implement based on it?",
                recommendedAnswerAr = "استناداً إلى ملاحظات المختبرين حول وضوح الأزرار وتجربة المستخدم على الشاشات الصغيرة، قمنا بتحسين التجاوب وإصلاح الأعطال المبلغ عنها وتجهيز نسخة نهائية مستقرة للإنتاج.",
                recommendedAnswerEn = "Based on tester feedback regarding touch responsiveness and layout spacing on compact displays, we refined the UI, resolved reported issues, and prepared a stable release build for production."
            )
        )
    }

    // --- Active Test Session Companion ---
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
        notes: String = L.t("جلسة فحص شاملة موثقة بنجاح", "Comprehensive QA inspection verified successfully")
    ) {
        val user = _currentUser.value
        viewModelScope.launch {
            val resolvedTesterName = user.displayName.ifBlank {
                L.t("مطور معتمد", "Verified Developer")
            }
            val resolvedEmail = user.email.ifBlank { "verified.tester@playtesterhub.app" }
            val nextDay = (test.daysCompleted + 1).coerceAtMost(14)

            val log = DailyTestLog(
                id = "log_${System.currentTimeMillis()}_${(1000..9999).random()}",
                appId = test.packageName,
                testerName = resolvedTesterName,
                testerEmail = resolvedEmail,
                deviceModel = Build.MODEL ?: "Android Device",
                osVersion = "Android ${Build.VERSION.RELEASE}",
                dayNumber = nextDay,
                checkInTimestamp = System.currentTimeMillis(),
                sessionDurationMinutes = durationMinutes,
                notes = notes
            )
            repository.addDailyTestLog(log)
            repository.updateTestSession(test.packageName, nextDay, user.email)

            val reward = if (durationMinutes >= 15) 50 else 30
            addPoints(reward)
            _currentUser.update { it.copy(testsCount = it.testsCount + 1) }

            dispatchAppOwnerNotification(
                packageName = test.packageName,
                appName = test.appName,
                actionType = "TESTED",
                message = L.t(
                    "أتم جلسة اختبار موثقة لمدة $durationMinutes دقيقة لتطبيق ${test.appName}.",
                    "Completed a verified $durationMinutes-minute QA session for ${test.appName}."
                )
            )

            closeActiveTestSession()
        }
    }

    // --- Developer-to-Developer Real Reciprocal Chat Hub ---
    fun openDeveloperChat(
        recipientEmail: String,
        recipientName: String,
        appName: String? = null
    ) {
        val myEmail = _currentUser.value.email.ifBlank { "dev.tester@testerhub.com" }
        _activeChatRecipientEmail.value = recipientEmail
        _activeChatRecipientName.value = recipientName
        _activeChatAppName.value = appName

        chatMessagesJob?.cancel()
        val channelId = FirestoreService.getChatChannelId(myEmail, recipientEmail)

        chatMessagesJob = viewModelScope.launch {
            repository.getChatMessagesFlow(channelId).collect { rawList ->
                val resolvedList = rawList.map { msg ->
                    msg.copy(isFromMe = msg.senderEmail.equals(myEmail, ignoreCase = true))
                }
                _chatMessages.value = resolvedList
            }
        }

        _showDeveloperChatDialog.value = true
    }

    fun closeDeveloperChat() {
        chatMessagesJob?.cancel()
        chatMessagesJob = null
        _showDeveloperChatDialog.value = false
        _activeChatRecipientEmail.value = ""
        _activeChatRecipientName.value = ""
        _activeChatAppName.value = null
        _chatMessages.value = emptyList()
    }

    fun openDeveloperInbox() {
        _showDeveloperInboxDialog.value = true
    }

    fun closeDeveloperInbox() {
        _showDeveloperInboxDialog.value = false
    }

    fun sendChatMessage(
        text: String,
        attachedAppName: String? = null,
        attachedAppPackage: String? = null
    ) {
        if (text.isBlank()) return
        val myName = _currentUser.value.displayName.ifBlank { L.t("مطور معتمد", "Verified Developer") }
        val myEmail = _currentUser.value.email.ifBlank { "dev.tester@testerhub.com" }
        val recipientEmail = _activeChatRecipientEmail.value
        val recipientName = _activeChatRecipientName.value

        if (recipientEmail.isBlank()) return

        val channelId = FirestoreService.getChatChannelId(myEmail, recipientEmail)

        val newMsg = ReciprocalChatMessage(
            id = "msg_${System.currentTimeMillis()}_${(1000..9999).random()}",
            senderName = myName,
            senderEmail = myEmail,
            message = text.trim(),
            timestamp = System.currentTimeMillis(),
            isFromMe = true,
            attachedAppName = attachedAppName ?: _activeChatAppName.value,
            attachedAppPackage = attachedAppPackage
        )

        viewModelScope.launch {
            repository.sendChatMessage(
                channelId = channelId,
                message = newMsg,
                recipientEmail = recipientEmail,
                recipientName = recipientName
            )
        }
    }
}
