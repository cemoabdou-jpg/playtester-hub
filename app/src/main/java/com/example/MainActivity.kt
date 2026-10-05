package com.example

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.BadgeCalculator
import com.example.data.model.InvitedTester
import com.example.data.model.JoinedTest
import com.example.data.model.UserApp
import com.example.ui.util.AppLanguage
import com.example.ui.util.L
import com.example.ui.util.LocalAppLanguage
import com.example.ui.components.AddAppDialog
import com.example.ui.components.AppHeader
import com.example.ui.components.BadgesDialog
import com.example.ui.components.DailyTestConfirmationDialog
import com.example.ui.components.EarnedPointsCenterDialog
import com.example.ui.components.InAppUpdateDialog
import com.example.ui.components.NotificationCenterDialog
import com.example.ui.components.SimplifiedDashboardCard
import com.example.ui.components.TesterInvitationDialog
import com.example.ui.components.TipTesterDialog
import com.example.ui.components.UpgradeGuestAccountDialog
import com.example.ui.screens.AdminControlScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.CommunityScreen
import com.example.ui.screens.GuideScreen
import com.example.ui.screens.MyAppsScreen
import com.example.ui.screens.MyTestingScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.ProfileSettingsScreen
import com.example.ui.theme.AppColorTheme
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PlayGreen
import com.example.ui.theme.ThemeMode
import com.example.ui.viewmodel.TesterViewModel
import com.example.util.FcmService
import com.example.util.NotificationHelper

class MainActivity : ComponentActivity() {
    private val viewModel: TesterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            com.google.firebase.FirebaseApp.initializeApp(this)
        } catch (e: Exception) {
            android.util.Log.w("MainActivity", "FirebaseApp startup: ${e.message}")
        }
        enableEdgeToEdge()
        FcmService.initializeFcm(applicationContext)
        setContent {
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
            val colorTheme by viewModel.colorTheme.collectAsStateWithLifecycle()
            val isDarkTheme = when (themeMode) {
                ThemeMode.DARK -> true
                ThemeMode.LIGHT -> false
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }
            MyApplicationTheme(darkTheme = isDarkTheme, colorTheme = colorTheme) {
                PlayTesterApp(
                    viewModel = viewModel,
                    themeMode = themeMode,
                    colorTheme = colorTheme,
                    isDarkTheme = isDarkTheme
                )
            }
        }
    }
}

@Composable
fun PlayTesterApp(
    viewModel: TesterViewModel,
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    colorTheme: AppColorTheme = AppColorTheme.CALM_BLUE,
    isDarkTheme: Boolean = false
) {
    val context = LocalContext.current
    var currentTab by remember { mutableIntStateOf(0) }
    var showAddAppDialog by remember { mutableStateOf(false) }
    var showNotificationsDialog by remember { mutableStateOf(false) }
    var showBadgesDialog by remember { mutableStateOf(false) }
    var showPointsCenterDialog by remember { mutableStateOf(false) }
    var quickInviteApp by remember { mutableStateOf<UserApp?>(null) }
    var selectedTestForDailyConfirmation by remember { mutableStateOf<JoinedTest?>(null) }
    var testerForTip by remember { mutableStateOf<InvitedTester?>(null) }
    var showUpgradeGuestDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    val language by viewModel.language.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val isOnboardingCompleted by viewModel.isOnboardingCompleted.collectAsStateWithLifecycle()
    val allArtifacts by viewModel.allArtifacts.collectAsStateWithLifecycle()
    val allInvitedTesters by viewModel.allInvitedTesters.collectAsStateWithLifecycle()

    val communityApps by viewModel.filteredCommunityApps.collectAsStateWithLifecycle()
    val adminCommunityApps by viewModel.adminCommunityApps.collectAsStateWithLifecycle()
    val joinedTests by viewModel.joinedTests.collectAsStateWithLifecycle()
    val userApps by viewModel.userApps.collectAsStateWithLifecycle()
    val wallet by viewModel.wallet.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()

    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val unreadNotificationsCount by viewModel.unreadNotificationsCount.collectAsStateWithLifecycle()
    val isSimplifiedMode by viewModel.isSimplifiedMode.collectAsStateWithLifecycle()
    val allDailyTestLogs by viewModel.allDailyTestLogs.collectAsStateWithLifecycle()
    val allFeedback by viewModel.allFeedback.collectAsStateWithLifecycle()
    val allTesterActivities by viewModel.allTesterActivities.collectAsStateWithLifecycle()
    val claimedBadgeIds by viewModel.claimedBadgeIds.collectAsStateWithLifecycle()

    val deviceModel by viewModel.deviceModel.collectAsStateWithLifecycle()
    val androidVersion by viewModel.androidVersion.collectAsStateWithLifecycle()
    val dailyReminderEnabled by viewModel.dailyReminderEnabled.collectAsStateWithLifecycle()
    val hapticEnabled by viewModel.hapticEnabled.collectAsStateWithLifecycle()
    val officialGoogleGroupUrl by viewModel.officialGoogleGroupUrl.collectAsStateWithLifecycle()
    val officialGoogleGroupEmail by viewModel.officialGoogleGroupEmail.collectAsStateWithLifecycle()
    val appUpdateStatus by viewModel.appUpdateStatus.collectAsStateWithLifecycle()
    val showUpdateDialog by viewModel.showUpdateDialog.collectAsStateWithLifecycle()
    val isSyncingApps by viewModel.isSyncingApps.collectAsStateWithLifecycle()
    val syncStatusMessage by viewModel.syncStatusMessage.collectAsStateWithLifecycle()
    val unsyncedItemsCount by viewModel.unsyncedItemsCount.collectAsStateWithLifecycle()
    val cloudDiagnostic by viewModel.cloudDiagnostic.collectAsStateWithLifecycle()
    val isDiagnosingCloud by viewModel.isDiagnosingCloud.collectAsStateWithLifecycle()
    val isUploadingLocalApps by viewModel.isUploadingLocalApps.collectAsStateWithLifecycle()

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            NotificationHelper.scheduleDailyReminderAlarm(context)
            viewModel.triggerTestNotification(context)
        } else {
            viewModel.triggerTestNotification(context)
        }
    }

    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            NotificationHelper.createNotificationChannel(context)
            NotificationHelper.scheduleDailyReminderAlarm(context)
            FcmService.initializeFcm(context)
            viewModel.checkForAppUpdate(context)
            viewModel.syncRemoteAppsNow(showFeedbackToast = false)
            viewModel.syncOfflinePendingData(showToast = false)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (androidx.core.content.ContextCompat.checkSelfPermission(
                        context,
                        android.Manifest.permission.POST_NOTIFICATIONS
                    ) != android.content.pm.PackageManager.PERMISSION_GRANTED
                ) {
                    notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearMessage()
        }
    }

    val layoutDirection = if (language == AppLanguage.AR) LayoutDirection.Rtl else LayoutDirection.Ltr
    CompositionLocalProvider(
        LocalLayoutDirection provides layoutDirection,
        LocalAppLanguage provides language
    ) {
        val s = L.strings()

        // If no user is logged in, present the secure Authentication screen
        if (currentUser == null) {
            AuthScreen(
                language = language,
                onToggleLanguage = { viewModel.toggleLanguage() },
                onLogin = { email, pass -> viewModel.login(email, pass) },
                onRegister = { name, email, pass -> viewModel.register(name, email, pass) },
                onGoogleSignIn = { email, name -> viewModel.loginWithGoogle(email, name) },
                onGuestLogin = { viewModel.loginAsGuest() },
                onResetPassword = { email, newPass, onResult ->
                    viewModel.resetPassword(email, newPass, onResult)
                }
            )
            return@CompositionLocalProvider
        }

        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .imePadding(),
            topBar = {
                AppHeader(
                    wallet = wallet,
                    currentUser = currentUser,
                    unreadNotificationsCount = unreadNotificationsCount,
                    isSimplifiedMode = isSimplifiedMode,
                    isDarkTheme = isDarkTheme,
                    language = language,
                    onToggleLanguage = { viewModel.toggleLanguage() },
                    onToggleTheme = { viewModel.toggleDarkLightMode(isDarkTheme) },
                    onNotificationsClick = { showNotificationsDialog = true },
                    onBadgesClick = { showBadgesDialog = true },
                    onPointsClick = { showPointsCenterDialog = true },
                    onProfileClick = { currentTab = 4 },
                    onToggleSimplifiedMode = { viewModel.toggleSimplifiedMode() },
                    onUpgradeGuestClick = { showUpgradeGuestDialog = true },
                    onLogout = { viewModel.logout() }
                )
            },
            bottomBar = {
                NavigationBar(
                    modifier = Modifier.testTag("bottom_navigation"),
                    tonalElevation = 4.dp
                ) {
                    val navItemColors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PlayGreen,
                        selectedTextColor = PlayGreen,
                        indicatorColor = PlayGreen.copy(alpha = 0.16f),
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
                    )

                    NavigationBarItem(
                        selected = currentTab == 0,
                        onClick = { currentTab = 0 },
                        colors = navItemColors,
                        icon = {
                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = s.tabCommunity
                            )
                        },
                        label = {
                            Text(
                                text = s.tabCommunity,
                                fontWeight = if (currentTab == 0) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        modifier = Modifier.testTag("tab_community")
                    )

                    NavigationBarItem(
                        selected = currentTab == 1,
                        onClick = { currentTab = 1 },
                        colors = navItemColors,
                        icon = {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = s.tabMyTesting
                            )
                        },
                        label = {
                            Text(
                                text = s.tabMyTesting,
                                fontWeight = if (currentTab == 1) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        modifier = Modifier.testTag("tab_my_testing")
                    )

                    NavigationBarItem(
                        selected = currentTab == 2,
                        onClick = { currentTab = 2 },
                        colors = navItemColors,
                        icon = {
                            Icon(
                                imageVector = Icons.Default.RocketLaunch,
                                contentDescription = s.tabMyApps
                            )
                        },
                        label = {
                            Text(
                                text = s.tabMyApps,
                                fontWeight = if (currentTab == 2) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        modifier = Modifier.testTag("tab_my_apps")
                    )

                    NavigationBarItem(
                        selected = currentTab == 3,
                        onClick = { currentTab = 3 },
                        colors = navItemColors,
                        icon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                contentDescription = s.tabGuide
                            )
                        },
                        label = {
                            Text(
                                text = s.tabGuide,
                                fontWeight = if (currentTab == 3) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        modifier = Modifier.testTag("tab_guide")
                    )

                    NavigationBarItem(
                        selected = currentTab == 4,
                        onClick = { currentTab = 4 },
                        colors = navItemColors,
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = s.tabSettings
                            )
                        },
                        label = {
                            Text(
                                text = s.tabSettings,
                                fontWeight = if (currentTab == 4) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        modifier = Modifier.testTag("tab_settings")
                    )

                    if (currentUser?.role == "ADMIN") {
                        NavigationBarItem(
                            selected = currentTab == 5,
                            onClick = { currentTab = 5 },
                            colors = navItemColors,
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = s.tabAdmin
                                )
                            },
                            label = {
                                Text(
                                    text = s.tabAdmin,
                                    fontWeight = if (currentTab == 5) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            modifier = Modifier.testTag("tab_admin_owner")
                        )
                    }
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Offline-First Cache Sync Banner (if there are unsynced offline checks/feedbacks)
            if (unsyncedItemsCount > 0) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PlayGreen.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, PlayGreen.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .clickable { viewModel.syncOfflinePendingData(showToast = true) }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CloudSync, contentDescription = null, tint = PlayGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "💾 لديك $unsyncedItemsCount فحص مسجل دون اتصال - اضغط للمزامنة ☁️",
                                style = MaterialTheme.typography.labelSmall,
                                color = PlayGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text("مزامنة الآن", fontSize = 10.sp, color = PlayGreen, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }

            // Screen facilitation: In simplified mode, show the quick action hub on the main market screen
            if (isSimplifiedMode && currentTab == 0) {
                SimplifiedDashboardCard(
                    userApps = userApps,
                    joinedTests = joinedTests,
                    communityApps = communityApps,
                    onQuickCheckIn = { test -> selectedTestForDailyConfirmation = test },
                    onQuickInvite = { app -> quickInviteApp = app },
                    onQuickJoinApp = { viewModel.joinApp(it) },
                    onSwitchTab = { currentTab = it },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when (currentTab) {
                    0 -> CommunityScreen(
                        communityApps = communityApps,
                        joinedTests = joinedTests,
                        searchQuery = searchQuery,
                        selectedCategory = selectedCategory,
                        userApps = userApps,
                        allFeedback = allFeedback,
                        currentUser = currentUser,
                        appUpdateStatus = appUpdateStatus,
                        onUpdateClick = {
                            viewModel.promptUpdateDialog()
                        },
                        isSyncing = isSyncingApps,
                        syncStatusMessage = syncStatusMessage,
                        diagnosticResult = cloudDiagnostic,
                        isDiagnosingCloud = isDiagnosingCloud,
                        isUploadingLocalApps = isUploadingLocalApps,
                        onSearchChange = { viewModel.setSearchQuery(it) },
                        onCategorySelect = { viewModel.setSelectedCategory(it) },
                        onJoinApp = { viewModel.joinApp(it) },
                        onPublishClick = { showAddAppDialog = true },
                        onAwardPoints = { pts, reason -> viewModel.awardEarnedPoints(pts, reason) },
                        onSyncClick = { viewModel.syncRemoteAppsNow(showFeedbackToast = true) },
                        onRunCloudDiagnostics = { viewModel.runCloudDiagnostics() },
                        onUploadLocalApps = { cb -> viewModel.reuploadLocalAppsToCloud(cb) },
                        onImportApp = { input, appName, devName, category, onComplete ->
                            viewModel.importCommunityAppDirectly(input, appName, devName, category, onComplete)
                        },
                        onOpenAdmin = if (currentUser?.role == "ADMIN") { { currentTab = 5 } } else null,
                        onReplyToReview = { feedbackId, replyText ->
                            viewModel.replyToFeedback(feedbackId, replyText)
                        },
                        onSubmitReview = { pkg, name, content, rating, type, devModel, androidVer, screenshotUrl ->
                            viewModel.submitAppReview(pkg, name, content, rating, type, devModel, androidVer, screenshotUrl)
                        },
                        onRecordAppOpened = { pkg, name -> viewModel.recordAppOpened(pkg, name) }
                    )
                    1 -> MyTestingScreen(
                        joinedTests = joinedTests,
                        currentStreak = wallet.currentStreak,
                        onCheckIn = { viewModel.checkIn(it) },
                        onConfirmDailyTest = { test, duration, rating, notes, proofUrl ->
                            viewModel.confirmDailyTest(test, duration, rating, notes, proofUrl)
                        },
                        onSendFeedback = { test, content, rating, type, devModel, androidVer, screenshotUrl ->
                            viewModel.sendFeedback(test, content, rating, type, devModel, androidVer, screenshotUrl)
                        },
                        onDeleteTest = { viewModel.deleteJoinedTest(it) },
                        onNavigateToCommunity = { currentTab = 0 },
                        onRecordAppOpened = { pkg, name -> viewModel.recordAppOpened(pkg, name) }
                    )
                    2 -> MyAppsScreen(
                        userApps = userApps,
                        allArtifacts = allArtifacts,
                        allInvitedTesters = allInvitedTesters,
                        allDailyTestLogs = allDailyTestLogs,
                        allFeedback = allFeedback,
                        allTesterActivities = allTesterActivities,
                        onAddAppClick = { showAddAppDialog = true },
                        onDeleteApp = { viewModel.deleteUserApp(it) },
                        onUploadArtifact = { appId, vName, vCode, aType, fName, fSize, rNotes, active ->
                            viewModel.uploadArtifact(appId, vName, vCode, aType, fName, fSize, rNotes, active)
                        },
                        onSetActiveVersion = { appId, artifactId ->
                            viewModel.setActiveVersion(appId, artifactId)
                        },
                        onDeleteArtifact = { artifactId ->
                            viewModel.deleteArtifact(artifactId)
                        },
                        onInviteTester = { appId, email, notes ->
                            viewModel.inviteTester(appId, email, notes)
                        },
                        onUpdateTesterStatus = { testerId, status ->
                            viewModel.updateTesterStatus(testerId, status)
                        },
                        onRemoveTester = { testerId ->
                            viewModel.removeTester(testerId)
                        },
                        onSendDailyReminder = { app ->
                            viewModel.sendDailyTestingReminder(app)
                        },
                        onUploadLocalApps = { cb -> viewModel.reuploadLocalAppsToCloud(cb) },
                        isUploadingLocalApps = isUploadingLocalApps,
                        onUpdateGroupUrl = { appId, url -> viewModel.updateUserAppGroupUrl(appId, url) },
                        onSyncPlayIcon = { pkg, appId -> viewModel.syncAppIconFromGooglePlay(pkg, appId) },
                        onReplyToReview = { feedbackId, replyText ->
                            viewModel.replyToFeedback(feedbackId, replyText)
                        },
                        onDeleteFeedback = { feedbackId ->
                            viewModel.deleteFeedback(feedbackId)
                        }
                    )
                    3 -> GuideScreen()
                    4 -> ProfileSettingsScreen(
                        currentUser = currentUser,
                        wallet = wallet,
                        deviceModel = deviceModel,
                        androidVersion = androidVersion,
                        dailyReminderEnabled = dailyReminderEnabled,
                        hapticEnabled = hapticEnabled,
                        isSimplifiedMode = isSimplifiedMode,
                        themeMode = themeMode,
                        colorTheme = colorTheme,
                        language = language,
                        onLanguageChange = { viewModel.setLanguage(it) },
                        onThemeModeChange = { viewModel.setThemeMode(it) },
                        onColorThemeChange = { viewModel.setColorTheme(it) },
                        onUpdateProfile = { viewModel.updateProfile(it) },
                        onChangePassword = { oldPass, newPass ->
                            viewModel.changePassword(oldPass, newPass)
                        },
                        onUpdatePreferences = { device, androidVer, reminder, haptic ->
                            viewModel.updatePreferences(device, androidVer, reminder, haptic)
                        },
                        onToggleSimplifiedMode = { viewModel.toggleSimplifiedMode() },
                        onOpenBadges = { showBadgesDialog = true },
                        onOpenPointsCenter = { showPointsCenterDialog = true },
                        onUpgradeGuestClick = { showUpgradeGuestDialog = true },
                        onPushAllNotifications = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                            }
                            viewModel.pushAllNotifications(context)
                        },
                        onLogout = { viewModel.logout() },
                        onDeleteAccount = { viewModel.deleteAccount() },
                        onCheckForUpdates = { viewModel.checkForAppUpdate(context, showToastIfLatest = true) },
                        onExportBackup = { viewModel.getExportedBackupJson() },
                        onRestoreBackup = { json -> viewModel.restoreBackupJson(json) }
                    )
                    5 -> AdminControlScreen(
                        adminApps = adminCommunityApps,
                        notifications = notifications,
                        isAdmin = currentUser?.role == "ADMIN",
                        allDailyLogs = allDailyTestLogs,
                        allFeedback = allFeedback,
                        allArtifacts = allArtifacts,
                        allInvitedTesters = allInvitedTesters,
                        joinedTests = joinedTests,
                        userApps = userApps,
                        officialGoogleGroupUrl = officialGoogleGroupUrl,
                        officialGoogleGroupEmail = officialGoogleGroupEmail,
                        onUpdateGoogleGroup = { u, e ->
                            viewModel.updateOfficialGoogleGroup(u, e)
                        },
                        onResetGoogleGroup = {
                            viewModel.resetOfficialGoogleGroup()
                        },
                        onUpdatePriority = { appId, score ->
                            viewModel.updateAppPriority(appId, score)
                        },
                        onToggleFeatured = { appId, isFeatured ->
                            viewModel.toggleAppFeatured(appId, isFeatured)
                        },
                        onToggleVisibility = { appId, isHidden ->
                            viewModel.toggleAppVisibility(appId, isHidden)
                        },
                        onDeleteApp = { appId ->
                            viewModel.deleteCommunityAppByAdmin(appId)
                        },
                        onBroadcastNotification = { title, message, ctx ->
                            viewModel.broadcastNotification(title, message, ctx)
                        },
                        onDeleteNotification = { notifId ->
                            viewModel.deleteNotification(notifId)
                        }
                    )
                }
            }
        }
    }

    if (showBadgesDialog) {
        val badges = remember(wallet, joinedTests, allDailyTestLogs, allFeedback, userApps) {
            BadgeCalculator.calculateBadges(
                wallet = wallet,
                joinedTests = joinedTests,
                userApps = userApps,
                feedbacks = allFeedback,
                dailyLogs = allDailyTestLogs
            )
        }
        BadgesDialog(
            badges = badges,
            claimedBadgeIds = claimedBadgeIds,
            onDismiss = { showBadgesDialog = false },
            onClaimBadgeReward = { id, pts -> viewModel.claimBadgeReward(id, pts) }
        )
    }

    if (showPointsCenterDialog) {
        EarnedPointsCenterDialog(
            wallet = wallet,
            onDismiss = { showPointsCenterDialog = false },
            onOpenBadges = { showBadgesDialog = true }
        )
    }

    if (showNotificationsDialog) {
        NotificationCenterDialog(
            notifications = notifications,
            onMarkAsRead = { viewModel.markNotificationAsRead(it) },
            onMarkAllAsRead = { viewModel.markAllNotificationsAsRead() },
            onDeleteNotification = { viewModel.deleteNotification(it) },
            onClearAll = { viewModel.clearAllNotifications() },
            onSendTestNotification = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    viewModel.triggerTestNotification(context)
                }
            },
            onPushAllNotifications = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
                viewModel.pushAllNotifications(context)
            },
            onDismiss = { showNotificationsDialog = false }
        )
    }

    if (quickInviteApp != null) {
        val targetApp = quickInviteApp!!
        val appTesters = allInvitedTesters.filter { it.userAppId == targetApp.id }
        TesterInvitationDialog(
            app = targetApp,
            invitedTesters = appTesters,
            onInviteTester = { email, notes ->
                viewModel.inviteTester(targetApp.id, email, notes)
            },
            onUpdateStatus = { testerId, status ->
                viewModel.updateTesterStatus(testerId, status)
            },
            onRemoveTester = { testerId ->
                viewModel.removeTester(testerId)
            },
            onTipTester = { tester ->
                testerForTip = tester
            },
            onDismiss = { quickInviteApp = null }
        )
    }

    testerForTip?.let { tester ->
        TipTesterDialog(
            tester = tester,
            userPoints = wallet.points,
            onSendTip = { testerId, points, note ->
                viewModel.sendTesterTip(testerId, points, note)
            },
            onDismiss = { testerForTip = null }
        )
    }

    if (showAddAppDialog) {
        AddAppDialog(
            userPoints = wallet.points,
            officialGoogleGroupUrl = officialGoogleGroupUrl,
            officialGoogleGroupEmail = officialGoogleGroupEmail,
            onDismiss = { showAddAppDialog = false },
            onConfirm = { name, pkg, iconUrl, groupUrl, webUrl, androidUrl, instructions, isFeatured ->
                viewModel.publishApp(
                    name = name,
                    packageName = pkg,
                    category = "تطبيق",
                    iconUrl = iconUrl,
                    googleGroupUrl = groupUrl,
                    webOptInUrl = webUrl,
                    androidOptInUrl = androidUrl,
                    instructions = instructions,
                    isFeatured = isFeatured
                )
                showAddAppDialog = false
                currentTab = 2 // Switch to My Apps
            }
        )
    }

    selectedTestForDailyConfirmation?.let { test ->
        DailyTestConfirmationDialog(
            test = test,
            onDismiss = { selectedTestForDailyConfirmation = null },
            onConfirm = { durationMinutes, rating, notes, proofScreenshotUrl ->
                viewModel.confirmDailyTest(test, durationMinutes, rating, notes, proofScreenshotUrl)
                selectedTestForDailyConfirmation = null
            }
        )
    }

    appUpdateStatus?.let { status ->
        if (status.isUpdateAvailable && showUpdateDialog) {
            InAppUpdateDialog(
                updateStatus = status,
                onDismiss = { viewModel.dismissAppUpdatePrompt() }
            )
        }
    }

    if (showUpgradeGuestDialog) {
        UpgradeGuestAccountDialog(
            wallet = wallet,
            onUpgradeWithEmail = { name, email, pass, onResult ->
                viewModel.upgradeGuestAccountWithEmail(name, email, pass, onResult)
            },
            onUpgradeWithGoogle = { name, email, onResult ->
                viewModel.upgradeGuestAccountWithGoogle(name, email, onResult)
            },
            onDismiss = { showUpgradeGuestDialog = false }
        )
    }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme { Greeting("PlayTester") }
}
