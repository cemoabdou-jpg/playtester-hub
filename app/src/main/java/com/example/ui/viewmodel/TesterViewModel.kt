package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.data.repository.TesterRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TesterViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TesterRepository

    val userApps: StateFlow<List<UserApp>>
    val communityApps: StateFlow<List<CommunityApp>>
    val joinedTests: StateFlow<List<JoinedTest>>

    private val _currentUser = MutableStateFlow(
        AuthUser(
            uid = "user_${System.currentTimeMillis() % 10000}",
            email = "cemoabdou@gmail.com",
            displayName = "المطور المعتمد",
            points = 750,
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

    private val _claimedBadgeIds = MutableStateFlow<Set<String>>(emptySet())
    val claimedBadgeIds: StateFlow<Set<String>> = _claimedBadgeIds.asStateFlow()

    private val _verificationCode = MutableStateFlow("")
    val verificationCode: StateFlow<String> = _verificationCode.asStateFlow()

    private val _pendingEmail = MutableStateFlow("cemoabdou@gmail.com")
    val pendingEmail: StateFlow<String> = _pendingEmail.asStateFlow()

    private val _pendingName = MutableStateFlow("المطور المعتمد")
    val pendingName: StateFlow<String> = _pendingName.asStateFlow()

    private val _lastSentCode = MutableStateFlow<String?>(null)
    val lastSentCode: StateFlow<String?> = _lastSentCode.asStateFlow()

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

    fun earnAdReward(points: Int = 50) {
        _currentUser.update { it.copy(points = it.points + points) }
    }

    fun claimDailyBonus(points: Int = 50) {
        _currentUser.update { it.copy(points = it.points + points) }
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
        if (enteredCode.trim() == _verificationCode.value) {
            _currentUser.update {
                it.copy(
                    email = _pendingEmail.value,
                    displayName = _pendingName.value,
                    isEmailVerified = true,
                    points = it.points + 100
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

    fun joinCommunityApp(app: CommunityApp) {
        viewModelScope.launch {
            val joined = JoinedTest(
                id = "joined_${System.currentTimeMillis()}",
                appId = app.id,
                appName = app.name,
                packageName = app.packageName,
                iconUrl = app.iconUrl,
                joinedAt = System.currentTimeMillis(),
                daysCompleted = 1,
                lastTestDate = System.currentTimeMillis(),
                status = "TESTING"
            )
            repository.joinTest(joined)
            _currentUser.update { it.copy(points = it.points + app.pointsReward, testsCount = it.testsCount + 1) }
        }
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

    fun removeUserApp(app: UserApp) {
        viewModelScope.launch {
            repository.removeUserApp(app)
            _currentUser.update { it.copy(appsCount = maxOf(0, it.appsCount - 1)) }
        }
    }

    fun promoteApp(app: UserApp, cost: Int = 100): Boolean {
        if (_currentUser.value.points >= cost) {
            _currentUser.update { it.copy(points = it.points - cost) }
            viewModelScope.launch {
                repository.promoteApp(app.packageName, true)
            }
            return true
        }
        return false
    }

    fun unpromoteApp(app: UserApp) {
        viewModelScope.launch {
            repository.promoteApp(app.packageName, false)
        }
    }

    fun openAndTestApp(context: android.content.Context, test: JoinedTest, onRewarded: (Int) -> Unit) {
        val reward = 25
        viewModelScope.launch {
            repository.recordDailyAppTest(test.packageName)
            _currentUser.update { it.copy(points = it.points + reward) }
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
            _currentUser.update { it.copy(points = it.points + rewardPoints) }
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
        val baseUsers = listOf(
            LeaderboardUser(
                rank = 1,
                name = "أحمد المنصوري (DevAhmed)",
                email = "ahmed.dev@gmail.com",
                testsCompleted = 19,
                points = 1420,
                badgeTitle = "المختبر الأسطوري 🥇"
            ),
            LeaderboardUser(
                rank = 2,
                name = "سارة التميمي (CodeWithSara)",
                email = "sara.code@gmail.com",
                testsCompleted = 16,
                points = 1180,
                badgeTitle = "صائدة الأخطاء المحترفة 🥈"
            ),
            LeaderboardUser(
                rank = 3,
                name = "طارق الخالدي (AppForge)",
                email = "tariq.kh@gmail.com",
                testsCompleted = 14,
                points = 990,
                badgeTitle = "مطور معتمد 🥉"
            ),
            LeaderboardUser(
                rank = 4,
                name = currentUser.displayName,
                email = currentUser.email,
                testsCompleted = maxOf(3, currentUser.testsCount),
                points = currentUser.points,
                badgeTitle = "مطور نشط ⭐",
                isCurrentUser = true
            ),
            LeaderboardUser(
                rank = 5,
                name = "عمر الزهراني (OmarDev)",
                email = "omar.zh@gmail.com",
                testsCompleted = 11,
                points = 680,
                badgeTitle = "مختبر متميز"
            ),
            LeaderboardUser(
                rank = 6,
                name = "ياسين الإدريسي (YassineApps)",
                email = "yassine.apps@gmail.com",
                testsCompleted = 9,
                points = 540,
                badgeTitle = "مختبر جديد"
            )
        )
        return baseUsers.sortedByDescending { it.points }.mapIndexed { index, user ->
            user.copy(rank = index + 1)
        }
    }
}
