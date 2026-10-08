package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.AppBuildArtifact
import com.example.data.model.AppNotification
import com.example.data.model.AuthUser
import com.example.data.model.CloudDiagnosticResult
import com.example.data.model.CommunityApp
import com.example.data.model.DailyTestLog
import com.example.data.model.InvitedTester
import com.example.data.model.JoinedTest
import com.example.data.model.TesterFeedback
import com.example.data.model.UserApp
import com.example.data.model.UserWallet
import com.example.data.repository.TesterRepository
import com.example.ui.theme.AppColorTheme
import com.example.ui.theme.ThemeMode
import com.example.ui.util.AppLanguage
import com.example.util.AppUpdateHelper
import com.example.util.AppUpdateStatus
import com.example.util.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TesterViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TesterRepository
    private val prefs = application.getSharedPreferences("playtester_prefs", Context.MODE_PRIVATE)

    private val _themeMode = MutableStateFlow(
        try {
            ThemeMode.valueOf(prefs.getString("theme_mode", ThemeMode.LIGHT.name) ?: ThemeMode.LIGHT.name)
        } catch (e: Exception) {
            ThemeMode.LIGHT
        }
    )
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _colorTheme = MutableStateFlow(
        try {
            AppColorTheme.valueOf(prefs.getString("color_theme_mode", AppColorTheme.MINT_BEIGE.name) ?: AppColorTheme.MINT_BEIGE.name)
        } catch (e: Exception) {
            AppColorTheme.MINT_BEIGE
        }
    )
    val colorTheme: StateFlow<AppColorTheme> = _colorTheme.asStateFlow()

    fun setColorTheme(palette: AppColorTheme) {
        prefs.edit().putString("color_theme_mode", palette.name).apply()
        _colorTheme.value = palette
        _userMessage.value = if (_language.value == AppLanguage.AR) "تم تغيير نمط الألوان إلى: ${palette.titleAr}" else "Theme palette set to: ${palette.titleEn}"
    }

    private val _language = MutableStateFlow(
        try {
            AppLanguage.valueOf(prefs.getString("app_language", AppLanguage.AR.name) ?: AppLanguage.AR.name)
        } catch (e: Exception) {
            AppLanguage.AR
        }
    )
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    fun setLanguage(lang: AppLanguage) {
        prefs.edit().putString("app_language", lang.name).apply()
        _language.value = lang
        _userMessage.value = if (lang == AppLanguage.AR) "تم تغيير لغة التطبيق إلى العربية" else "Language changed to English"
    }

    fun toggleLanguage() {
        val next = if (_language.value == AppLanguage.AR) AppLanguage.EN else AppLanguage.AR
        setLanguage(next)
    }

    fun setThemeMode(mode: ThemeMode) {
        prefs.edit().putString("theme_mode", mode.name).apply()
        _themeMode.value = mode
        _userMessage.value = when (mode) {
            ThemeMode.DARK -> "تم تفعيل الوضع الداكن (ليلي مريح)"
            ThemeMode.LIGHT -> "تم تفعيل الوضع الفاتح (نهاري)"
            ThemeMode.SYSTEM -> "تم تفعيل الوضع التلقائي (حسب النظام)"
        }
    }

    fun toggleDarkLightMode(isCurrentlyDark: Boolean) {
        val newMode = if (isCurrentlyDark) ThemeMode.LIGHT else ThemeMode.DARK
        setThemeMode(newMode)
    }

    private val _isOnboardingCompleted = MutableStateFlow(true)
    val isOnboardingCompleted: StateFlow<Boolean> = _isOnboardingCompleted.asStateFlow()

    fun setOnboardingCompleted(completed: Boolean) {
        prefs.edit().putBoolean("onboarding_completed", completed).apply()
        _isOnboardingCompleted.value = completed
    }

    init {
        val db = AppDatabase.getInstance(application)
        repository = TesterRepository(db.appDao())
        NotificationHelper.createNotificationChannel(application)
        
        // Automatic Realtime Cloud Sync & Background Auto-Upload (No manual clicks needed)
        viewModelScope.launch {
            // 1. Continuous real-time listener for any new or updated apps from other developers
            launch {
                try {
                    repository.observeAndSyncRemoteApps()
                } catch (e: Exception) {
                    android.util.Log.w("TesterViewModel", "Observe apps note: ${e.message}")
                }
            }
            // 2. Automatic one-time fetch & automatic background upload of local apps
            launch {
                try {
                    repository.backfillMissingAppIcons()
                    repository.syncRemoteAppsNow()
                    repository.reuploadAllUserAppsToCloud()
                    repository.backfillMissingAppIcons()
                } catch (e: Exception) {
                    android.util.Log.w("TesterViewModel", "Auto startup sync note: ${e.message}")
                }
            }
        }
    }

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("الكل")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    private val _isSimplifiedMode = MutableStateFlow(false)
    val isSimplifiedMode: StateFlow<Boolean> = _isSimplifiedMode.asStateFlow()

    private val _isSyncingApps = MutableStateFlow(false)
    val isSyncingApps: StateFlow<Boolean> = _isSyncingApps.asStateFlow()

    private val _syncStatusMessage = MutableStateFlow<String?>(null)
    val syncStatusMessage: StateFlow<String?> = _syncStatusMessage.asStateFlow()

    private val _cloudDiagnostic = MutableStateFlow<CloudDiagnosticResult?>(null)
    val cloudDiagnostic: StateFlow<CloudDiagnosticResult?> = _cloudDiagnostic.asStateFlow()

    private val _isDiagnosingCloud = MutableStateFlow(false)
    val isDiagnosingCloud: StateFlow<Boolean> = _isDiagnosingCloud.asStateFlow()

    private val _isUploadingLocalApps = MutableStateFlow(false)
    val isUploadingLocalApps: StateFlow<Boolean> = _isUploadingLocalApps.asStateFlow()

    private val _appUpdateStatus = MutableStateFlow<AppUpdateStatus?>(null)
    val appUpdateStatus: StateFlow<AppUpdateStatus?> = _appUpdateStatus.asStateFlow()

    private val _showUpdateDialog = MutableStateFlow(true)
    val showUpdateDialog: StateFlow<Boolean> = _showUpdateDialog.asStateFlow()

    fun checkForAppUpdate(context: Context, showToastIfLatest: Boolean = false) {
        AppUpdateHelper.checkPlayInAppUpdate(context) { status ->
            _appUpdateStatus.value = status
            if (status.isUpdateAvailable) {
                _showUpdateDialog.value = true
            }
            if (!status.isUpdateAvailable && showToastIfLatest) {
                val isAr = _language.value == AppLanguage.AR
                _userMessage.value = if (isAr)
                    "أنت تستخدم أحدث إصدار من التطبيق v${AppUpdateHelper.getInstalledVersionName(context)} (${AppUpdateHelper.getInstalledVersionCode(context)})"
                else
                    "You are using the latest version of the app v${AppUpdateHelper.getInstalledVersionName(context)} (${AppUpdateHelper.getInstalledVersionCode(context)})"
            }
        }
    }

    fun dismissAppUpdatePrompt() {
        _showUpdateDialog.value = false
    }

    fun promptUpdateDialog() {
        _showUpdateDialog.value = true
    }

    val currentUser: StateFlow<AuthUser?> = repository.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val adminCommunityApps: StateFlow<List<CommunityApp>> = repository.allCommunityAppsAdmin
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allArtifacts: StateFlow<List<AppBuildArtifact>> = repository.allArtifacts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allInvitedTesters: StateFlow<List<InvitedTester>> = repository.allInvitedTesters
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<AppNotification>> = repository.notifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadNotificationsCount: StateFlow<Int> = repository.unreadNotificationsCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val allDailyTestLogs: StateFlow<List<DailyTestLog>> = repository.allDailyTestLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allFeedback: StateFlow<List<TesterFeedback>> = repository.allFeedback
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unsyncedItemsCount: StateFlow<Int> = repository.unsyncedItemsCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val allTesterActivities: StateFlow<List<com.example.data.model.AppTesterActivity>> = repository.allTesterActivities
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val officialGoogleGroupUrl: StateFlow<String> = repository.officialGoogleGroupUrl
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TesterRepository.DEFAULT_GOOGLE_GROUP_URL)

    val officialGoogleGroupEmail: StateFlow<String> = repository.officialGoogleGroupEmail
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TesterRepository.DEFAULT_GOOGLE_GROUP_EMAIL)

    val rawCommunityApps = repository.communityApps
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredCommunityApps: StateFlow<List<CommunityApp>> = combine(
        rawCommunityApps,
        _searchQuery,
        _selectedCategory
    ) { apps, query, category ->
        val distinctApps = apps.distinctBy { it.packageName.trim().lowercase() }
        val categoryFiltered = if (category == "الكل" || category.isBlank()) {
            distinctApps
        } else {
            distinctApps.filter { it.category == category }
        }
        if (query.isBlank()) {
            categoryFiltered
        } else {
            categoryFiltered.filter { app ->
                app.name.contains(query, ignoreCase = true) ||
                app.developerName.contains(query, ignoreCase = true) ||
                app.packageName.contains(query, ignoreCase = true) ||
                app.description.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val joinedTests: StateFlow<List<JoinedTest>> = repository.joinedTests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userApps: StateFlow<List<UserApp>> = repository.userApps
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val wallet: StateFlow<UserWallet> = repository.wallet
        .combine(MutableStateFlow(Unit)) { w, _ ->
            w ?: UserWallet(id = 1, points = 50, testsCompleted = 0, currentStreak = 0, lastActiveDate = "")
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserWallet(id = 1, points = 50, testsCompleted = 0, currentStreak = 0, lastActiveDate = ""))

    private val _claimedBadgeIds = MutableStateFlow<Set<String>>(emptySet())
    val claimedBadgeIds: StateFlow<Set<String>> = _claimedBadgeIds

    init {
        viewModelScope.launch {
            _claimedBadgeIds.value = repository.getClaimedBadgeIds()
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun clearMessage() {
        _userMessage.value = null
    }

    // --- Authentication Actions ---
    fun register(name: String, email: String, pass: String) {
        viewModelScope.launch {
            val res = repository.registerUser(name, email, pass)
            res.onSuccess { user ->
                _userMessage.value = "مرحباً بك ${user.displayName}! تم إنشاء الحساب بنجاح."
            }.onFailure { err ->
                _userMessage.value = err.message ?: "فشل إنشاء الحساب"
            }
        }
    }

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            val res = repository.loginWithEmail(email, pass)
            res.onSuccess { user ->
                _userMessage.value = "تم تسجيل الدخول بنجاح. أهلاً بك مجدداً ${user.displayName}!"
            }.onFailure { err ->
                _userMessage.value = err.message ?: "فشل تسجيل الدخول"
            }
        }
    }

    fun loginWithGoogle(email: String, displayName: String) {
        viewModelScope.launch {
            val res = repository.loginWithGoogle(email, displayName)
            res.onSuccess { user ->
                _userMessage.value = "تم تسجيل الدخول بحساب Google (${user.email}) بنجاح!"
            }.onFailure { err ->
                _userMessage.value = err.message ?: "فشل تسجيل الدخول عبر Google"
            }
        }
    }

    fun loginAsGuest() {
        viewModelScope.launch {
            val res = repository.loginAsGuest()
            res.onSuccess { user ->
                _userMessage.value = "تم الدخول كضيف بنجاح! يمكنك الآن تجربة التطبيق وتصفح الميزات بحرية."
            }.onFailure { err ->
                _userMessage.value = err.message ?: "فشل الدخول كضيف"
            }
        }
    }

    fun upgradeGuestAccountWithEmail(
        displayName: String,
        email: String,
        password: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val res = repository.upgradeGuestAccountWithEmail(displayName, email, password)
            res.onSuccess { user ->
                val msg = "تهانينا ${user.displayName}! تم ترقية حسابك بنجاح وحفظ كافة نقاطك وتطبيقاتك بشكل دائم 🚀"
                _userMessage.value = msg
                onResult(true, msg)
            }.onFailure { err ->
                val msg = err.message ?: "فشلت ترقية الحساب"
                _userMessage.value = msg
                onResult(false, msg)
            }
        }
    }

    fun upgradeGuestAccountWithGoogle(
        displayName: String,
        email: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val res = repository.upgradeGuestAccountWithGoogle(displayName, email)
            res.onSuccess { user ->
                val msg = "تهانينا ${user.displayName}! تم ربط الحساب بـ Google (${user.email}) وحفظ جميع بياناتك بنجاح 🚀"
                _userMessage.value = msg
                onResult(true, msg)
            }.onFailure { err ->
                val msg = err.message ?: "فشل الربط بحساب Google"
                _userMessage.value = msg
                onResult(false, msg)
            }
        }
    }

    fun resetPassword(email: String, newPass: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val res = repository.resetPassword(email, newPass)
            res.onSuccess {
                _userMessage.value = "تمت استعادة وتحديث كلمة المرور بنجاح!"
                onResult(true, "تم تحديث كلمة المرور بنجاح. يمكنك الآن تسجيل الدخول بها.")
            }.onFailure { err ->
                val errMsg = err.message ?: "فشلت استعادة كلمة المرور"
                _userMessage.value = errMsg
                onResult(false, errMsg)
            }
        }
    }

    suspend fun checkEmailRegistered(email: String): Boolean {
        return repository.isEmailRegistered(email)
    }

    private val _deviceModel = MutableStateFlow(prefs.getString("device_model", "Samsung Galaxy S23 / Pixel 8") ?: "Samsung Galaxy S23 / Pixel 8")
    val deviceModel: StateFlow<String> = _deviceModel.asStateFlow()

    private val _androidVersion = MutableStateFlow(prefs.getString("android_version", "Android 14 (API 34)") ?: "Android 14 (API 34)")
    val androidVersion: StateFlow<String> = _androidVersion.asStateFlow()

    private val _dailyReminderEnabled = MutableStateFlow(prefs.getBoolean("daily_reminder_enabled", true))
    val dailyReminderEnabled: StateFlow<Boolean> = _dailyReminderEnabled.asStateFlow()

    private val _hapticEnabled = MutableStateFlow(prefs.getBoolean("haptic_enabled", true))
    val hapticEnabled: StateFlow<Boolean> = _hapticEnabled.asStateFlow()

    fun updatePreferences(
        device: String,
        androidVer: String,
        dailyReminder: Boolean,
        haptic: Boolean
    ) {
        prefs.edit()
            .putString("device_model", device)
            .putString("android_version", androidVer)
            .putBoolean("daily_reminder_enabled", dailyReminder)
            .putBoolean("haptic_enabled", haptic)
            .apply()

        _deviceModel.value = device
        _androidVersion.value = androidVer
        _dailyReminderEnabled.value = dailyReminder
        _hapticEnabled.value = haptic
        _userMessage.value = "تم حفظ تفضيلاتك الشخصية بنجاح"
    }

    fun updateProfile(displayName: String) {
        viewModelScope.launch {
            val res = repository.updateUserProfile(displayName)
            res.onSuccess {
                _userMessage.value = "تم تحديث الاسم الشخصي بنجاح"
            }.onFailure { err ->
                _userMessage.value = err.message ?: "فشل تحديث البيانات"
            }
        }
    }

    fun changePassword(oldPass: String, newPass: String) {
        viewModelScope.launch {
            val res = repository.changePassword(oldPass, newPass)
            res.onSuccess {
                _userMessage.value = "تم تغيير كلمة المرور بنجاح"
            }.onFailure { err ->
                _userMessage.value = err.message ?: "فشل تغيير كلمة المرور"
            }
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            val res = repository.deleteAccount()
            res.onSuccess {
                _userMessage.value = "تم حذف الحساب بنجاح"
            }.onFailure { err ->
                _userMessage.value = err.message ?: "فشل حذف الحساب"
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _userMessage.value = "تم تسجيل الخروج بنجاح"
        }
    }

    // --- Version Artifact Management ---
    fun uploadArtifact(
        userAppId: Int,
        versionName: String,
        versionCode: Int,
        artifactType: String,
        fileName: String,
        fileSizeMb: Double,
        releaseNotes: String,
        setAsActive: Boolean
    ) {
        viewModelScope.launch {
            val res = repository.uploadArtifact(
                userAppId, versionName, versionCode, artifactType,
                fileName, fileSizeMb, releaseNotes, setAsActive
            )
            res.onSuccess { msg ->
                _userMessage.value = msg
            }.onFailure { err ->
                _userMessage.value = err.message ?: "فشل رفع الإصدار"
            }
        }
    }

    fun setActiveVersion(userAppId: Int, artifactId: Int) {
        viewModelScope.launch {
            val res = repository.setActiveVersion(userAppId, artifactId)
            res.onSuccess { msg ->
                _userMessage.value = msg
            }.onFailure { err ->
                _userMessage.value = err.message ?: "فشل تعيين الإصدار النشط"
            }
        }
    }

    fun deleteArtifact(artifactId: Int) {
        viewModelScope.launch {
            val res = repository.deleteArtifact(artifactId)
            res.onSuccess { msg ->
                _userMessage.value = msg
            }
        }
    }

    // --- Tester Invitation Management ---
    fun inviteTester(userAppId: Int, email: String, notes: String) {
        viewModelScope.launch {
            val res = repository.inviteTesterByEmail(userAppId, email, notes)
            res.onSuccess { msg ->
                _userMessage.value = msg
            }.onFailure { err ->
                _userMessage.value = err.message ?: "فشل إرسال الدعوة للمختبر"
            }
        }
    }

    fun updateTesterStatus(testerId: Int, status: String) {
        viewModelScope.launch {
            val res = repository.updateTesterStatus(testerId, status)
            res.onSuccess { msg ->
                _userMessage.value = msg
            }
        }
    }

    fun removeTester(testerId: Int) {
        viewModelScope.launch {
            val res = repository.removeInvitedTester(testerId)
            res.onSuccess { msg ->
                _userMessage.value = msg
            }
        }
    }

    fun awardEarnedPoints(points: Int, reason: String, incrementStreak: Boolean = false) {
        viewModelScope.launch {
            val res = repository.awardEarnedPoints(points, reason, incrementStreak)
            res.onSuccess { msg ->
                _userMessage.value = msg
            }.onFailure { err ->
                _userMessage.value = err.message ?: "فشل إضافة النقاط"
            }
        }
    }

    fun claimBadgeReward(badgeId: String, pointsReward: Int) {
        viewModelScope.launch {
            val res = repository.claimBadgeReward(badgeId, pointsReward)
            res.onSuccess { msg ->
                _claimedBadgeIds.value = _claimedBadgeIds.value + badgeId
                _userMessage.value = msg
            }.onFailure { err ->
                _userMessage.value = err.message ?: "تعذر استلام مكافأة الشارة"
            }
        }
    }

    fun sendTesterTip(testerId: Int, points: Int, note: String) {
        viewModelScope.launch {
            val currentWallet = wallet.value
            if (currentWallet.points < points) {
                _userMessage.value = "رصيد نقاطك غير كافٍ لتمرير البقشيش"
                return@launch
            }
            val updatedWallet = currentWallet.copy(points = currentWallet.points - points)
            repository.updateWallet(updatedWallet)
            repository.addNotification(
                title = "🎁 تم إرسال مكافأة بقشيش ($points نقطة)!",
                message = "تم منح البقشيش بنجاح للمختبر. الملاحظة: $note",
                type = "MILESTONE"
            )
            _userMessage.value = "تم إرسال مكافأة قدرها $points نقطة بنجاح للمختبر 💎"
        }
    }

    fun joinApp(app: CommunityApp) {
        viewModelScope.launch {
            val result = repository.joinCommunityApp(app)
            result.onSuccess { msg ->
                _userMessage.value = msg
            }.onFailure { err ->
                _userMessage.value = err.message ?: "حدث خطأ أثناء الانضمام للاختبار"
            }
        }
    }

    fun checkIn(test: JoinedTest) {
        viewModelScope.launch {
            val result = repository.recordCheckIn(test)
            result.onSuccess { msg ->
                _userMessage.value = msg
            }.onFailure { err ->
                _userMessage.value = err.message ?: "حدث خطأ في تسجيل الحضور"
            }
        }
    }

    fun confirmDailyTest(
        test: JoinedTest,
        durationMinutes: Int = 10,
        rating: Int = 5,
        notes: String = "",
        proofScreenshotUrl: String = ""
    ) {
        viewModelScope.launch {
            val result = repository.confirmDailyTest(test, durationMinutes, rating, notes, proofScreenshotUrl)
            result.onSuccess { msg ->
                _userMessage.value = msg
            }.onFailure { err ->
                _userMessage.value = err.message ?: "حدث خطأ أثناء تأكيد التجربة اليومية"
            }
        }
    }

    fun sendDailyTestingReminder(app: UserApp) {
        viewModelScope.launch {
            val res = repository.sendDailyTestingReminder(app)
            res.onSuccess { msg ->
                _userMessage.value = msg
                NotificationHelper.showSystemNotification(
                    getApplication(),
                    "تذكير يومي عاجل للمختبرين ⏰",
                    "يرجى فتح تطبيق '${app.name}' وتأكيد تجربة اليوم لضمان استيفاء شرط الـ 14 يوماً في Google Play."
                )
            }
        }
    }

    fun sendFeedback(
        test: JoinedTest,
        content: String = "التطبيق يعمل بشكل جيد ومستقر.",
        rating: Int = 5,
        feedbackType: String = "FEEDBACK",
        deviceModel: String = "",
        androidVersion: String = "",
        screenshotUrl: String = ""
    ) {
        viewModelScope.launch {
            val userEmail = currentUser.value?.email ?: "tester@example.com"
            val userName = currentUser.value?.displayName ?: "مختبر معتمد"
            val res = repository.submitFeedback(
                test = test,
                content = content,
                rating = rating,
                feedbackType = feedbackType,
                testerEmail = userEmail,
                testerName = userName,
                deviceModel = deviceModel,
                androidVersion = androidVersion,
                screenshotUrl = screenshotUrl
            )
            res.onSuccess { msg ->
                _userMessage.value = msg
            }.onFailure { err ->
                _userMessage.value = err.message ?: "فشل إرسال التقييم"
            }
        }
    }

    fun submitAppReview(
        packageName: String,
        appName: String,
        content: String,
        rating: Int = 5,
        feedbackType: String = "FEEDBACK",
        deviceModel: String = "",
        androidVersion: String = "",
        screenshotUrl: String = ""
    ) {
        viewModelScope.launch {
            val res = repository.submitAppReview(
                packageName = packageName,
                appName = appName,
                content = content,
                rating = rating,
                feedbackType = feedbackType,
                deviceModel = deviceModel,
                androidVersion = androidVersion,
                screenshotUrl = screenshotUrl
            )
            res.onSuccess { msg ->
                _userMessage.value = msg
            }.onFailure { err ->
                _userMessage.value = err.message ?: "فشل إرسال المراجعة"
            }
        }
    }

    fun syncOfflinePendingData(showToast: Boolean = true) {
        viewModelScope.launch {
            val res = repository.syncOfflinePendingData()
            res.onSuccess { (uploaded, fetched) ->
                if (showToast) {
                    if (uploaded > 0 || fetched > 0) {
                        _userMessage.value = "تمت المزامنة السحابية بنجاح ☁️: تم رفع $uploaded واستلام $fetched تقييم جديد."
                    } else {
                        _userMessage.value = "جميع الفحوصات والملاحظات متزامنة مع السحاب بالفعل ✓"
                    }
                }
            }.onFailure { err ->
                if (showToast) {
                    _userMessage.value = "تعذر إتمام المزامنة السحابية: ${err.message}"
                }
            }
        }
    }

    fun replyToFeedback(feedbackId: Int, replyText: String) {
        viewModelScope.launch {
            val res = repository.replyToFeedback(feedbackId, replyText)
            res.onSuccess { msg ->
                _userMessage.value = msg
            }.onFailure { err ->
                _userMessage.value = err.message ?: "فشل إرسال الرد"
            }
        }
    }

    fun deleteFeedback(id: Int) {
        viewModelScope.launch {
            repository.deleteFeedback(id)
            _userMessage.value = "تم حذف المراجعة/التقييم"
        }
    }

    fun publishApp(
        name: String,
        packageName: String,
        category: String = "تطبيق",
        iconUrl: String = "",
        googleGroupUrl: String,
        webOptInUrl: String,
        androidOptInUrl: String,
        instructions: String,
        isFeatured: Boolean
    ) {
        viewModelScope.launch {
            val newApp = UserApp(
                name = name,
                packageName = packageName,
                category = category,
                iconUrl = iconUrl,
                googleGroupUrl = googleGroupUrl,
                webOptInUrl = webOptInUrl,
                androidOptInUrl = androidOptInUrl,
                instructions = instructions,
                testersCount = 1,
                targetTesters = 20,
                daysElapsed = 1,
                targetDays = 14,
                status = "TESTING"
            )
            val result = repository.publishApp(newApp, isFeatured)
            result.onSuccess { msg ->
                _userMessage.value = msg
            }.onFailure { err ->
                _userMessage.value = err.message ?: "فشل في نشر التطبيق"
            }
        }
    }

    fun recordAppOpened(packageName: String, appName: String) {
        viewModelScope.launch {
            val res = repository.recordAppOpened(packageName, appName)
            res.onSuccess { msg ->
                _userMessage.value = msg
            }.onFailure { err ->
                _userMessage.value = err.message ?: "تم فتح التطبيق"
            }
        }
    }

    fun updateUserAppGroupUrl(appId: Int, groupUrl: String) {
        viewModelScope.launch {
            val res = repository.updateUserAppGroupUrl(appId, groupUrl)
            res.onSuccess { msg ->
                _userMessage.value = msg
            }.onFailure { err ->
                _userMessage.value = err.message ?: "فشل تحديث رابط المجموعة"
            }
        }
    }

    fun syncTesterActivitiesForApp(packageName: String) {
        viewModelScope.launch {
            repository.syncTesterActivitiesForPackage(packageName)
        }
    }

    fun syncAppIconFromGooglePlay(packageName: String, userAppId: Int? = null) {
        viewModelScope.launch {
            _userMessage.value = "جاري سحب أيقونة التطبيق من Google Play..."
            val res = repository.syncAppIconFromGooglePlay(packageName, userAppId)
            res.onSuccess { msg ->
                _userMessage.value = msg
            }.onFailure { err ->
                _userMessage.value = err.message ?: "فشل سحب أيقونة التطبيق"
            }
        }
    }

    fun updateAppIcon(packageName: String, iconUrl: String, userAppId: Int? = null) {
        viewModelScope.launch {
            val res = repository.updateAppIconDirectly(packageName, iconUrl, userAppId)
            _userMessage.value = if (res.isSuccess) res.getOrNull() ?: "تم حفظ وتحديث الأيقونة بنجاح" else res.exceptionOrNull()?.message ?: "فشل تحديث الأيقونة"
        }
    }

    fun deleteUserApp(id: Int) {
        viewModelScope.launch {
            repository.deleteUserApp(id)
            _userMessage.value = "تم إزالة التطبيق من قائمتك"
        }
    }

    fun deleteJoinedTest(id: Int) {
        viewModelScope.launch {
            repository.deleteJoinedTest(id)
            _userMessage.value = "تم إلغاء متابعة الاختبار"
        }
    }

    // --- Screen Simplification Mode ---
    fun toggleSimplifiedMode() {
        _isSimplifiedMode.value = !_isSimplifiedMode.value
        _userMessage.value = if (_isSimplifiedMode.value) "تم تفعيل الواجهة المبسطة لسهولة الاستخدام" else "تم العودة للواجهة القياسية"
    }

    // --- Notifications Management ---
    fun markNotificationAsRead(id: Int) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }

    fun markAllNotificationsAsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsAsRead()
            _userMessage.value = "تم تعيين جميع الإشعارات كمقروءة"
        }
    }

    fun deleteNotification(id: Int) {
        viewModelScope.launch {
            repository.deleteNotification(id)
        }
    }

    fun clearAllNotifications() {
        viewModelScope.launch {
            repository.clearAllNotifications()
            _userMessage.value = "تم مسح جميع الإشعارات"
        }
    }

    fun triggerTestNotification(context: Context) {
        viewModelScope.launch {
            val title = "تنبيه اختبار جديد من PlayTester Hub"
            val message = "انضم مختبر جديد لتطبيقك وهو نشط الآن في فترة الـ 14 يوماً! حافظ على تحديثاتك مستمرة."
            repository.addNotification(
                title = title,
                message = message,
                type = "TESTER_JOINED",
                actionRoute = "my_apps"
            )
            val delivered = NotificationHelper.showSystemNotification(context, title, message)
            _userMessage.value = if (delivered) {
                "تم إرسال إشعار النظام بنجاح إلى شريط الإشعارات!"
            } else {
                "تم إضافة الإشعار في مركز الإشعارات (تأكد من تفعيل إذن الإشعارات لإظهارها في شريط النظام)"
            }
        }
    }

    /**
     * Pushes all current/unread notifications directly to the Android notification drawer.
     */
    fun pushAllNotifications(context: Context) {
        viewModelScope.launch {
            var list = repository.getAllNotificationsOnce()
            if (list.isEmpty()) {
                repository.addNotification(
                    title = "🚀 مرحباً بك في PlayTester Hub",
                    message = "منظومتك المتكاملة لتخطي متطلب الـ 14 يوماً و20 مختبراً في متجر Google Play!",
                    type = "WELCOME"
                )
                repository.addNotification(
                    title = "⏰ تذكير الفحص اليومي النشط",
                    message = "تم تفعيل دفع وتوصيل كافة التنبيهات لضمان عدم تفويت فحص التطبيقات اليومي.",
                    type = "DAILY_REMINDER"
                )
                list = repository.getAllNotificationsOnce()
            }
            val pushedCount = NotificationHelper.pushAllNotifications(context, list)
            _userMessage.value = if (pushedCount > 0) {
                "تم دفع وتفعيل جميع الإشعارات بنجاح ($pushedCount إشعار) في شريط أندرويد! 🔔"
            } else {
                "تم تجهيز الإشعارات (يرجى التأكد من منح إذن الإشعارات في النظام)"
            }
        }
    }

    // --- Admin / Owner Controls ---
    fun updateAppPriority(appId: Int, priorityScore: Int) {
        viewModelScope.launch {
            val res = repository.updateAppPriority(appId, priorityScore)
            res.onSuccess { msg -> _userMessage.value = msg }
        }
    }

    fun toggleAppFeatured(appId: Int, isFeatured: Boolean) {
        viewModelScope.launch {
            val res = repository.toggleAppFeatured(appId, isFeatured)
            res.onSuccess { msg -> _userMessage.value = msg }
        }
    }

    fun toggleAppVisibility(appId: Int, isHidden: Boolean) {
        viewModelScope.launch {
            val res = repository.toggleAppVisibility(appId, isHidden)
            res.onSuccess { msg -> _userMessage.value = msg }
        }
    }

    fun deleteCommunityAppByAdmin(appId: Int) {
        viewModelScope.launch {
            val res = repository.deleteCommunityAppByAdmin(appId)
            res.onSuccess { msg -> _userMessage.value = msg }
        }
    }

    fun broadcastNotification(title: String, message: String, context: Context?) {
        viewModelScope.launch {
            val res = repository.broadcastNotification(title, message, type = "MILESTONE")
            context?.let { ctx ->
                NotificationHelper.showSystemNotification(ctx, title, message)
            }
            res.onSuccess { msg -> _userMessage.value = msg }
        }
    }

    fun updateOfficialGoogleGroup(url: String, email: String) {
        viewModelScope.launch {
            val res = repository.updateOfficialGoogleGroup(url, email)
            res.onSuccess { msg -> _userMessage.value = msg }
            res.onFailure { err -> _userMessage.value = err.message ?: "حدث خطأ أثناء تحديث رابط المجموعة" }
        }
    }

    fun resetOfficialGoogleGroup() {
        viewModelScope.launch {
            val res = repository.resetOfficialGoogleGroup()
            res.onSuccess { msg -> _userMessage.value = msg }
        }
    }

    suspend fun getExportedBackupJson(): String {
        return repository.exportBackupJson()
    }

    suspend fun restoreBackupJson(jsonString: String): Pair<Boolean, String> {
        val res = repository.restoreBackupJson(jsonString)
        return if (res.isSuccess) {
            val msg = res.getOrNull() ?: "تمت الاستعادة بنجاح"
            _userMessage.value = msg
            Pair(true, msg)
        } else {
            val err = res.exceptionOrNull()?.message ?: "فشلت الاستعادة"
            _userMessage.value = err
            Pair(false, err)
        }
    }

    fun syncRemoteAppsNow(showFeedbackToast: Boolean = true) {
        viewModelScope.launch {
            _isSyncingApps.value = true
            _syncStatusMessage.value = "جاري الاتصال بالسيرفر السحابي ومزامنة التطبيقات..."
            val result = repository.syncRemoteAppsNow()
            _isSyncingApps.value = false
            if (result.isSuccess) {
                val count = result.getOrDefault(0)
                val msg = if (count > 0) "تمت المزامنة بنجاح! تم تحديث $count تطبيق من السيرفر." else "السيرفر متصل، ولا توجد تطبيقات جديدة حالياً."
                _syncStatusMessage.value = msg
                if (showFeedbackToast) {
                    _userMessage.value = msg
                }
            } else {
                val err = result.exceptionOrNull()?.message ?: "فشلت المزامنة"
                _syncStatusMessage.value = "تنبيه المزامنة: $err"
                if (showFeedbackToast) {
                    _userMessage.value = "تنبيه: $err (تأكد من اتصال الإنترنت أو استخدم ميزة الإضافة بالرابط)"
                }
            }
        }
    }

    fun runCloudDiagnostics() {
        viewModelScope.launch {
            _isDiagnosingCloud.value = true
            val diag = repository.checkCloudConnection()
            _cloudDiagnostic.value = diag
            _isDiagnosingCloud.value = false
        }
    }

    fun clearCloudDiagnostics() {
        _cloudDiagnostic.value = null
    }

    fun reuploadLocalAppsToCloud(onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            _isUploadingLocalApps.value = true
            val res = repository.reuploadAllUserAppsToCloud()
            _isUploadingLocalApps.value = false
            if (res.isSuccess) {
                val (success, fail) = res.getOrDefault(Pair(0, 0))
                val msg = if (fail == 0) {
                    "تم رفع جميع تطبيقاتك ($success تطبيق) إلى السيرفر السحابي بنجاح! ستظهر الآن لجميع المطورين."
                } else {
                    "تم رفع $success تطبيق بنجاح، وتعذر رفع $fail (تحقق من الصلاحيات والإنترنت)."
                }
                _userMessage.value = msg
                onComplete(true, msg)
                // Refresh list
                syncRemoteAppsNow(showFeedbackToast = false)
            } else {
                val err = res.exceptionOrNull()?.message ?: "فشل الرفع للسحابة"
                _userMessage.value = err
                onComplete(false, err)
            }
        }
    }

    fun importCommunityAppDirectly(
        input: String,
        customName: String,
        developerName: String,
        category: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val res = repository.importCommunityAppDirectly(input, customName, developerName, category)
            if (res.isSuccess) {
                val app = res.getOrNull()
                val msg = "تمت إضافة تطبيق \"${app?.name ?: ""}\" بنجاح إلى قائمة الاختبار!"
                _userMessage.value = msg
                onResult(true, msg)
            } else {
                val err = res.exceptionOrNull()?.message ?: "فشلت الإضافة"
                _userMessage.value = err
                onResult(false, err)
            }
        }
    }
}
