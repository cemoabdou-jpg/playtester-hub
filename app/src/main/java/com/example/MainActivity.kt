package com.example

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.BadgeCalculator
import com.example.data.model.JoinedTest
import com.example.data.model.UserApp
import com.example.ui.components.AddAppDialog
import com.example.ui.components.AppHeader
import com.example.ui.components.BadgesDialog
import com.example.ui.components.DailyTestConfirmationDialog
import com.example.ui.components.NotificationCenterDialog
import com.example.ui.components.SimplifiedDashboardCard
import com.example.ui.components.TesterInvitationDialog
import com.example.ui.screens.AdminControlScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.CommunityScreen
import com.example.ui.screens.GuideScreen
import com.example.ui.screens.MyAppsScreen
import com.example.ui.screens.MyTestingScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.TesterViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: TesterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                PlayTesterApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun PlayTesterApp(viewModel: TesterViewModel) {
    val context = LocalContext.current
    var currentTab by remember { mutableIntStateOf(0) }
    var showAddAppDialog by remember { mutableStateOf(false) }
    var showNotificationsDialog by remember { mutableStateOf(false) }
    var showBadgesDialog by remember { mutableStateOf(false) }
    var showOnboardingExplicitly by remember { mutableStateOf(false) }
    var quickInviteApp by remember { mutableStateOf<UserApp?>(null) }
    var selectedTestForDailyConfirmation by remember { mutableStateOf<JoinedTest?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

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

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.triggerTestNotification(context)
        } else {
            viewModel.triggerTestNotification(context)
        }
    }

    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearMessage()
        }
    }

    // If no user is logged in, present the secure Authentication screen
    if (currentUser == null) {
        AuthScreen(
            onLogin = { email, pass -> viewModel.login(email, pass) },
            onRegister = { name, email, pass -> viewModel.register(name, email, pass) },
            onGoogleSignIn = { email, name -> viewModel.loginWithGoogle(email, name) }
        )
        return
    }

    // If onboarding is not completed or user explicitly reopened it, show OnboardingScreen
    if (!isOnboardingCompleted || showOnboardingExplicitly) {
        OnboardingScreen(
            onFinishOnboarding = {
                viewModel.setOnboardingCompleted(true)
                showOnboardingExplicitly = false
            }
        )
        return
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
                onNotificationsClick = { showNotificationsDialog = true },
                onBadgesClick = { showBadgesDialog = true },
                onOpenOnboarding = { showOnboardingExplicitly = true },
                onToggleSimplifiedMode = { viewModel.toggleSimplifiedMode() },
                onLogout = { viewModel.logout() }
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier.testTag("bottom_navigation")
            ) {
                NavigationBarItem(
                    selected = currentTab == 0,
                    onClick = { currentTab = 0 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = "سوق التبادل"
                        )
                    },
                    label = { Text("سوق التبادل") },
                    modifier = Modifier.testTag("tab_community")
                )

                NavigationBarItem(
                    selected = currentTab == 1,
                    onClick = { currentTab = 1 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "اختباراتي"
                        )
                    },
                    label = { Text("اختباراتي") },
                    modifier = Modifier.testTag("tab_my_testing")
                )

                NavigationBarItem(
                    selected = currentTab == 2,
                    onClick = { currentTab = 2 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.RocketLaunch,
                            contentDescription = "تطبيقاتي"
                        )
                    },
                    label = { Text("تطبيقاتي") },
                    modifier = Modifier.testTag("tab_my_apps")
                )

                NavigationBarItem(
                    selected = currentTab == 3,
                    onClick = { currentTab = 3 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = "دليل الـ 14 يوم"
                        )
                    },
                    label = { Text("دليل 14 يوم") },
                    modifier = Modifier.testTag("tab_guide")
                )

                if (currentUser?.role == "ADMIN") {
                    NavigationBarItem(
                        selected = currentTab == 4,
                        onClick = { currentTab = 4 },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = "لوحة المالك"
                            )
                        },
                        label = { Text("لوحة المالك") },
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
            // Screen facilitation: In simplified mode, show the quick action hub
            if (isSimplifiedMode) {
                SimplifiedDashboardCard(
                    userApps = userApps,
                    joinedTests = joinedTests,
                    communityApps = communityApps,
                    onQuickCheckIn = { test -> selectedTestForDailyConfirmation = test },
                    onQuickInvite = { app -> quickInviteApp = app },
                    onQuickJoinApp = { viewModel.joinApp(it) },
                    onSwitchTab = { currentTab = it },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
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
                        currentUser = currentUser,
                        onSearchChange = { viewModel.setSearchQuery(it) },
                        onCategorySelect = { viewModel.setSelectedCategory(it) },
                        onJoinApp = { viewModel.joinApp(it) },
                        onPublishClick = { showAddAppDialog = true },
                        onOpenAdmin = if (currentUser?.role == "ADMIN") { { currentTab = 4 } } else null
                    )
                    1 -> MyTestingScreen(
                        joinedTests = joinedTests,
                        currentStreak = wallet.currentStreak,
                        onCheckIn = { viewModel.checkIn(it) },
                        onConfirmDailyTest = { test, duration, rating, notes ->
                            viewModel.confirmDailyTest(test, duration, rating, notes)
                        },
                        onSendFeedback = { test, content, rating, type ->
                            viewModel.sendFeedback(test, content, rating, type)
                        },
                        onDeleteTest = { viewModel.deleteJoinedTest(it) },
                        onNavigateToCommunity = { currentTab = 0 }
                    )
                    2 -> MyAppsScreen(
                        userApps = userApps,
                        allArtifacts = allArtifacts,
                        allInvitedTesters = allInvitedTesters,
                        allDailyTestLogs = allDailyTestLogs,
                        allFeedback = allFeedback,
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
                        }
                    )
                    3 -> GuideScreen(
                        onOpenOnboarding = { showOnboardingExplicitly = true }
                    )
                    4 -> AdminControlScreen(
                        adminApps = adminCommunityApps,
                        notifications = notifications,
                        isAdmin = currentUser?.role == "ADMIN",
                        allDailyLogs = allDailyTestLogs,
                        allFeedback = allFeedback,
                        allArtifacts = allArtifacts,
                        allInvitedTesters = allInvitedTesters,
                        joinedTests = joinedTests,
                        userApps = userApps,
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
            onDismiss = { showBadgesDialog = false }
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
            onDismiss = { quickInviteApp = null }
        )
    }

    if (showAddAppDialog) {
        AddAppDialog(
            userPoints = wallet.points,
            onDismiss = { showAddAppDialog = false },
            onConfirm = { name, pkg, cat, groupUrl, webUrl, androidUrl, instructions, isFeatured ->
                viewModel.publishApp(
                    name = name,
                    packageName = pkg,
                    category = cat,
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
            onConfirm = { durationMinutes, rating, notes ->
                viewModel.confirmDailyTest(test, durationMinutes, rating, notes)
                selectedTestForDailyConfirmation = null
            }
        )
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
