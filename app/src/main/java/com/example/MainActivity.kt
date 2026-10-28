package com.example

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.PlayTesterTheme
import com.example.ui.viewmodel.TesterViewModel
import com.example.util.AdBannerView
import com.example.util.AdMobConfig

class MainActivity : ComponentActivity() {
    private val viewModel: TesterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            enableEdgeToEdge()
        } catch (_: Throwable) {}

        try {
            com.example.util.DailyReminderScheduler.scheduleDailyReminder(this)
        } catch (_: Throwable) {}

        setContent {
            PlayTesterTheme {
                MainScreen(viewModel = viewModel)
            }
        }
    }
}

enum class NavigationTab(val title: String, val icon: ImageVector) {
    COMMUNITY("المجتمع", Icons.Default.Public),
    MY_TESTING("اختباراتي", Icons.Default.Science),
    MY_APPS("تطبيقاتي", Icons.Default.Apps),
    GUIDE("الدليل", Icons.Default.Book),
    PROFILE("حسابي", Icons.Default.Person)
}

@Composable
fun MainScreen(viewModel: TesterViewModel) {
    val user by viewModel.currentUser.collectAsState()

    // Mandatory Authentication Gate: Must log in and verify email via OTP to access the platform
    if (!user.isEmailVerified) {
        MandatoryAuthScreen(viewModel = viewModel)
        return
    }

    val context = LocalContext.current
    val activity = context as? Activity

    // 1. تحميل الإعلان البيني مسبقاً في الخلفية فور تشغيل الشاشة
    LaunchedEffect(Unit) {
        AdMobConfig.loadInterstitialAd(context)
    }

    var selectedTab by remember { mutableStateOf(NavigationTab.COMMUNITY) }
    // عداد ذكي لمرات التنقل حتى لا يظهر الإعلان في كل نقرة ويزعج المستخدم (يظهر كل 3 تنقلات مثلاً)
    var tabSwitchCount by remember { mutableIntStateOf(0) }

    val userApps by viewModel.userApps.collectAsState()
    val selectedApp = viewModel.selectedAppForReport.collectAsState().value ?: userApps.firstOrNull()

    val showAudit by viewModel.showAuditReportDialog.collectAsState()
    val showGrowth by viewModel.showDeveloperGrowthDialog.collectAsState()
    val showMonetization by viewModel.showMonetizationDialog.collectAsState()
    val showCrashes by viewModel.showCrashReportsDialog.collectAsState()
    val showHealth by viewModel.showTesterHealthDialog.collectAsState()
    val showChangelog by viewModel.showChangelogDialog.collectAsState()
    val showEmailVerification by viewModel.showEmailVerificationDialog.collectAsState()
    val verificationCode by viewModel.verificationCode.collectAsState()
    val pendingEmail by viewModel.pendingEmail.collectAsState()
    val pendingName by viewModel.pendingName.collectAsState()

    val logs by remember(selectedApp) {
        if (selectedApp != null) viewModel.getDailyTestLogs(selectedApp.id) else kotlinx.coroutines.flow.flowOf(emptyList())
    }.collectAsState(initial = emptyList())

    val crashes by remember(selectedApp) {
        if (selectedApp != null) viewModel.getCrashReports(selectedApp.id) else kotlinx.coroutines.flow.flowOf(emptyList())
    }.collectAsState(initial = emptyList())

    // BackHandler to navigate back to Community Tab if user presses back on another tab
    if (selectedTab != NavigationTab.COMMUNITY) {
        BackHandler {
            selectedTab = NavigationTab.COMMUNITY
        }
    }

    val showLeaderboard by viewModel.showLeaderboardDialog.collectAsState()
    var showThemeDialog by remember { mutableStateOf(false) }

    val notifications by viewModel.testerNotifications.collectAsState()
    val unreadNotificationsCount = remember(notifications) { notifications.count { !it.isRead } }

    Scaffold(
        topBar = {
            AppHeader(
                user = user,
                onOpenMonetization = { viewModel.openMonetization() },
                onOpenGrowth = { viewModel.openDeveloperGrowth() },
                onOpenAudit = { viewModel.openAuditReport() },
                onOpenLeaderboard = { viewModel.openLeaderboard() },
                onOpenTheme = { showThemeDialog = true },
                onOpenLuckySpin = { viewModel.openLuckySpin() },
                onOpenAdmin = { viewModel.openAdminControl() },
                unreadNotificationsCount = unreadNotificationsCount,
                onOpenNotifications = { viewModel.openNotifications() },
                onOpenDeveloperInbox = { viewModel.openDeveloperInbox() },
                onOpenComplianceMatrix = { selectedApp?.let { viewModel.openComplianceMatrix(it) } }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationTab.values().forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = {
                            if (selectedTab != tab) {
                                tabSwitchCount++
                                // كل 3 تنقلات بين الصفحات يتم عرض الإعلان البيني
                                if (tabSwitchCount % 3 == 0 && activity != null) {
                                    AdMobConfig.showInterstitialAd(activity) {
                                        selectedTab = tab
                                    }
                                } else {
                                    selectedTab = tab
                                }
                            }
                        },
                        icon = { Icon(tab.icon, contentDescription = tab.title) },
                        label = { Text(tab.title) }
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (selectedTab) {
                    NavigationTab.COMMUNITY -> CommunityScreen(viewModel = viewModel)
                    NavigationTab.MY_TESTING -> MyTestingScreen(viewModel = viewModel)
                    NavigationTab.MY_APPS -> MyAppsScreen(viewModel = viewModel)
                    NavigationTab.GUIDE -> GuideScreen()
                    NavigationTab.PROFILE -> ProfileSettingsScreen(viewModel = viewModel)
                }
            }
            AdBannerView()
        }
    }

    if (showAudit && selectedApp != null) {
        OfficialAuditReportDialog(
            app = selectedApp,
            logs = logs,
            crashes = crashes,
            onDismiss = { viewModel.closeAuditReport() }
        )
    }

    if (showGrowth) {
        DeveloperGrowthHubDialog(
            onDismiss = { viewModel.closeDeveloperGrowth() }
        )
    }

    if (showMonetization) {
        MonetizationCenterDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.closeMonetization() }
        )
    }

    if (showCrashes && selectedApp != null) {
        CrashReportManagerDialog(
            app = selectedApp,
            crashes = crashes,
            onResolveCrash = { id, note -> viewModel.resolveCrash(id, note) },
            onDismiss = { viewModel.closeCrashReports() }
        )
    }

    if (showHealth && selectedApp != null) {
        TesterActivityHealthDialog(
            app = selectedApp,
            logs = logs,
            onOpenTesterProfile = { email, name ->
                viewModel.openTesterProfile(email, name)
            },
            onDismiss = { viewModel.closeTesterHealth() }
        )
    }

    if (showChangelog && selectedApp != null) {
        ChangelogGeneratorDialog(
            app = selectedApp,
            crashes = crashes,
            onDismiss = { viewModel.closeChangelog() }
        )
    }

    if (showEmailVerification) {
        EmailVerificationDialog(
            initialEmail = pendingEmail,
            initialName = pendingName,
            currentCode = verificationCode,
            onSendCode = { email, name -> viewModel.startEmailVerification(email, name) },
            onConfirmCode = { code -> viewModel.confirmVerificationCode(code) },
            onDismiss = { viewModel.closeEmailVerification() }
        )
    }

    if (showLeaderboard) {
        LeaderboardBadgesDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.closeLeaderboard() },
            onWatchAd = {
                if (activity != null) {
                    AdMobConfig.showRewardedAd(
                        activity = activity,
                        onRewardEarned = { coins ->
                            viewModel.earnAdReward(coins)
                        }
                    )
                } else {
                    viewModel.earnAdReward(50)
                }
            }
        )
    }

    if (showThemeDialog) {
        ThemeSelectionDialog(
            onDismissRequest = { showThemeDialog = false }
        )
    }

    val showLuckySpin by viewModel.showLuckySpinDialog.collectAsState()
    if (showLuckySpin) {
        DailyLuckySpinDialog(
            onDismiss = { viewModel.closeLuckySpin() },
            onRewardWon = { coins ->
                viewModel.claimSpinReward(coins)
            }
        )
    }

    val showAdminControl by viewModel.showAdminControlDialog.collectAsState()
    if (showAdminControl && user.isPlatformOwner) {
        PlatformOwnerAdminDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.closeAdminControl() }
        )
    }

    val showNotifications by viewModel.showNotificationsDialog.collectAsState()
    if (showNotifications) {
        NotificationsCenterDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.closeNotifications() }
        )
    }

    val showTesterProfile by viewModel.showTesterProfileDialog.collectAsState()
    val selectedTesterProfile by viewModel.selectedTesterProfile.collectAsState()
    if (showTesterProfile && selectedTesterProfile != null) {
        TesterProfileDialog(
            profile = selectedTesterProfile!!,
            viewModel = viewModel,
            onDismiss = { viewModel.closeTesterProfile() }
        )
    }

    val showComplianceMatrix by viewModel.showComplianceMatrixDialog.collectAsState()
    val selectedAppForCompliance by viewModel.selectedAppForCompliance.collectAsState()
    if (showComplianceMatrix && selectedAppForCompliance != null) {
        GooglePlayComplianceMatrixDialog(
            app = selectedAppForCompliance!!,
            viewModel = viewModel,
            onDismiss = { viewModel.closeComplianceMatrix() }
        )
    }

    val showActiveSession by viewModel.showActiveTestSessionDialog.collectAsState()
    val activeSessionTest by viewModel.activeSessionTest.collectAsState()
    if (showActiveSession && activeSessionTest != null) {
        ActiveTestSessionDialog(
            test = activeSessionTest!!,
            viewModel = viewModel,
            onDismiss = { viewModel.closeActiveTestSession() }
        )
    }

    val showDeveloperInbox by viewModel.showDeveloperInboxDialog.collectAsState()
    if (showDeveloperInbox) {
        DeveloperInboxDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.closeDeveloperInbox() }
        )
    }

    val showDeveloperChat by viewModel.showDeveloperChatDialog.collectAsState()
    val activeChatRecipientName by viewModel.activeChatRecipientName.collectAsState()
    val activeChatRecipientEmail by viewModel.activeChatRecipientEmail.collectAsState()
    val activeChatAppName by viewModel.activeChatAppName.collectAsState()
    if (showDeveloperChat) {
        DeveloperChatDialog(
            recipientName = activeChatRecipientName,
            recipientEmail = activeChatRecipientEmail,
            appName = activeChatAppName,
            viewModel = viewModel,
            onDismiss = { viewModel.closeDeveloperChat() }
        )
    }
}
