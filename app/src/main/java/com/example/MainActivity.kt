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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.PlayTesterTheme
import com.example.ui.util.L
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

enum class NavigationTab(val titleAr: String, val titleEn: String, val icon: ImageVector) {
    COMMUNITY("المجتمع", "Community", Icons.Default.Public),
    MY_TESTING("اختباراتي", "My Tests", Icons.Default.Science),
    MY_APPS("تطبيقاتي", "My Apps", Icons.Default.Apps),
    GUIDE("الدليل", "Guide", Icons.Default.Book),
    PROFILE("حسابي", "Profile", Icons.Default.Person);

    @Composable
    fun getTitle(): String = if (L.isArabic()) titleAr else titleEn
}

@Composable
fun MainScreen(viewModel: TesterViewModel) {
    val user by viewModel.currentUser.collectAsState()

    CompositionLocalProvider(
        LocalLayoutDirection provides (if (L.isArabic()) LayoutDirection.Rtl else LayoutDirection.Ltr)
    ) {
        // Mandatory Authentication Gate
        if (!user.isEmailVerified) {
            MandatoryAuthScreen(viewModel = viewModel)
            return@CompositionLocalProvider
        }

        val context = LocalContext.current
        val activity = context as? Activity

        LaunchedEffect(Unit) {
            AdMobConfig.loadInterstitialAd(context)
        }

        var selectedTab by remember { mutableStateOf(NavigationTab.COMMUNITY) }
        val requestedTabIndex by viewModel.requestedTabIndex.collectAsState()
        LaunchedEffect(requestedTabIndex) {
            requestedTabIndex?.let { index ->
                NavigationTab.values().getOrNull(index)?.let { targetTab ->
                    selectedTab = targetTab
                }
                viewModel.consumeRequestedTab()
            }
        }

        val userApps by viewModel.userApps.collectAsState()
        val selectedApp = viewModel.selectedAppForReport.collectAsState().value ?: userApps.firstOrNull()

        val showAudit by viewModel.showAuditReportDialog.collectAsState()
        val showGrowth by viewModel.showDeveloperGrowthDialog.collectAsState()
        val showMonetization by viewModel.showMonetizationDialog.collectAsState()
        val showCrashes by viewModel.showCrashReportsDialog.collectAsState()
        val showHealth by viewModel.showTesterHealthDialog.collectAsState()
        val showChangelog by viewModel.showChangelogDialog.collectAsState()
        val showEmailVerification by viewModel.showEmailVerificationDialog.collectAsState()
        val pendingEmail by viewModel.pendingEmail.collectAsState()
        val pendingName by viewModel.pendingName.collectAsState()
        val showCommandCenter by viewModel.showAppCommandCenter.collectAsState()
        val commandCenterApp by viewModel.commandCenterApp.collectAsState()

        val logs by remember(selectedApp?.packageName) {
            if (selectedApp != null) viewModel.getDailyTestLogs(selectedApp.packageName)
            else kotlinx.coroutines.flow.flowOf(emptyList())
        }.collectAsState(initial = emptyList())

        val crashes by remember(selectedApp?.packageName) {
            if (selectedApp != null) viewModel.getCrashReports(selectedApp.packageName)
            else kotlinx.coroutines.flow.flowOf(emptyList())
        }.collectAsState(initial = emptyList())

        if (selectedTab != NavigationTab.COMMUNITY) {
            BackHandler {
                selectedTab = NavigationTab.COMMUNITY
            }
        }

        val showLeaderboard by viewModel.showLeaderboardDialog.collectAsState()
        var showThemeDialog by remember { mutableStateOf(false) }
        var showLogoEventsDialog by remember { mutableStateOf(false) }

        val notifications by viewModel.testerNotifications.collectAsState()
        val unreadNotificationsCount = remember(notifications) { notifications.count { !it.isRead } }

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val useNavigationRail = maxWidth >= 600.dp

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
                        onOpenComplianceMatrix = { selectedApp?.let { viewModel.openComplianceMatrix(it) } },
                        onOpenLogoEvents = { showLogoEventsDialog = true }
                    )
                },
                bottomBar = {
                    if (!useNavigationRail) {
                        NavigationBar {
                            NavigationTab.values().forEach { tab ->
                                NavigationBarItem(
                                    selected = selectedTab == tab,
                                    onClick = { selectedTab = tab },
                                    icon = { Icon(tab.icon, contentDescription = tab.getTitle()) },
                                    label = { Text(tab.getTitle(), maxLines = 1) },
                                    modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                                )
                            }
                        }
                    }
                }
            ) { paddingValues ->
                if (useNavigationRail) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        NavigationRail {
                            Spacer(modifier = Modifier.height(8.dp))
                            NavigationTab.values().forEach { tab ->
                                NavigationRailItem(
                                    selected = selectedTab == tab,
                                    onClick = { selectedTab = tab },
                                    icon = { Icon(tab.icon, contentDescription = tab.getTitle()) },
                                    label = { Text(tab.getTitle(), maxLines = 1) },
                                    modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            Column(
                                modifier = Modifier
                                    .widthIn(max = 840.dp)
                                    .fillMaxWidth()
                                    .fillMaxHeight()
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
                                        NavigationTab.GUIDE -> GuideScreen(
                                            userApps = userApps,
                                            onNavigateToMyApps = { selectedTab = NavigationTab.MY_APPS }
                                        )
                                        NavigationTab.PROFILE -> ProfileSettingsScreen(viewModel = viewModel)
                                    }
                                }
                                AdBannerView()
                            }
                        }
                    }
                } else {
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
                                NavigationTab.GUIDE -> GuideScreen(
                                    userApps = userApps,
                                    onNavigateToMyApps = { selectedTab = NavigationTab.MY_APPS }
                                )
                                NavigationTab.PROFILE -> ProfileSettingsScreen(viewModel = viewModel)
                            }
                        }
                        AdBannerView()
                    }
                }
            }
        }

        if (showCommandCenter && commandCenterApp != null) {
            val targetApp = commandCenterApp!!
            AppCommandCenterDialog(
                app = targetApp,
                viewModel = viewModel,
                onOpenComplianceMatrix = { viewModel.openComplianceMatrix(targetApp) },
                onOpenAuditReport = { viewModel.openAuditReport(targetApp) },
                onOpenCrashManager = { viewModel.openCrashReports(targetApp) },
                onOpenTesterHealth = { viewModel.openTesterHealth(targetApp) },
                onOpenChangelog = { viewModel.openChangelog(targetApp) },
                onDismiss = { viewModel.closeAppCommandCenter() }
            )
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
                viewModel = viewModel,
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
                onResolveCrash = { id, note -> viewModel.resolveCrash(id, note, selectedApp.packageName) },
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
                onConfirmAccount = { email, name -> viewModel.completeQuickSignIn(email, name) },
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

        if (showLogoEventsDialog) {
            LogoEventSelectionDialog(
                onDismiss = { showLogoEventsDialog = false }
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
}
