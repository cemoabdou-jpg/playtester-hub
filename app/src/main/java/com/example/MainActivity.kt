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
    // عداد ذكي لمرات التنقل حتى لا يظهر الإعلان في
