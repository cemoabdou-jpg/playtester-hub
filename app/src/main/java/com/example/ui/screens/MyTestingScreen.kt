package com.example.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.JoinedTest
import com.example.ui.components.AppIconImage
import com.example.ui.components.FeedbackDialog
import com.example.ui.components.ReportBugDialog
import com.example.ui.util.L
import com.example.ui.viewmodel.TesterViewModel
import com.example.util.AdMobConfig
import java.util.Calendar

private fun isCheckedToday(lastTestDate: Long): Boolean {
    if (lastTestDate <= 0L) return false
    val nowCal = Calendar.getInstance()
    val testCal = Calendar.getInstance().apply { timeInMillis = lastTestDate }
    return nowCal.get(Calendar.YEAR) == testCal.get(Calendar.YEAR) &&
        nowCal.get(Calendar.DAY_OF_YEAR) == testCal.get(Calendar.DAY_OF_YEAR)
}

@Composable
fun MyTestingScreen(
    viewModel: TesterViewModel
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val joinedTests by viewModel.joinedTests.collectAsState()

    var selectedSubTab by remember { mutableStateOf("ACTIVE") }
    var lastTestedAppForDouble by remember { mutableStateOf<JoinedTest?>(null) }
    var appForFeedback by remember { mutableStateOf<JoinedTest?>(null) }
    var appForBugReport by remember { mutableStateOf<JoinedTest?>(null) }

    val activeTests = remember(joinedTests) {
        joinedTests
            .filter { it.daysCompleted < 14 }
            .sortedBy { isCheckedToday(it.lastTestDate) } // Unchecked today first!
    }
    val completedTests = remember(joinedTests) {
        joinedTests.filter { it.daysCompleted >= 14 }
    }

    val checkedTodayActiveCount = remember(activeTests) {
        activeTests.count { isCheckedToday(it.lastTestDate) }
    }

    val displayList = if (selectedSubTab == "ACTIVE") activeTests else completedTests

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    L.t("التطبيقات التي أقوم باختبارها", "Apps I am Testing"),
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    L.t("افتح التطبيقات يومياً للحفاظ على نقاطك وتأهيل المطورين", "Open apps daily to maintain points and qualify developers"),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Daily Check Progress Summary Card
        if (activeTests.isNotEmpty()) {
            item {
                val allDoneToday = checkedTodayActiveCount == activeTests.size
                val progress = checkedTodayActiveCount.toFloat() / activeTests.size.coerceAtLeast(1)
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = if (allDoneToday) Color(0xFF00875A).copy(alpha = 0.10f)
                        else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.30f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                L.t(
                                    "فحصت $checkedTodayActiveCount من ${activeTests.size} تطبيق اليوم",
                                    "Checked $checkedTodayActiveCount of ${activeTests.size} apps today"
                                ),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (allDoneToday) Color(0xFF00875A) else MaterialTheme.colorScheme.onSurface
                            )
                            Surface(
                                color = if (allDoneToday) Color(0xFF00875A) else Color(0xFFFF8F00),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    if (allDoneToday) L.t("اكتمل اليوم ✓", "All Done ✓")
                                    else L.t("متبقي ${activeTests.size - checkedTodayActiveCount}", "${activeTests.size - checkedTodayActiveCount} left"),
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (allDoneToday) Color(0xFF00875A) else MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }
            }
        }

        // Sub Tabs Selector (Active vs Completed)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                FilterChip(
                    selected = selectedSubTab == "ACTIVE",
                    onClick = { selectedSubTab = "ACTIVE" },
                    label = {
                        Text(
                            L.t("قيد الاختبار اليومي (${activeTests.size})", "Daily Testing (${activeTests.size})"),
                            fontSize = 12.sp,
                            fontWeight = if (selectedSubTab == "ACTIVE") FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("my_testing_tab_active"),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                    )
                )

                FilterChip(
                    selected = selectedSubTab == "COMPLETED",
                    onClick = { selectedSubTab = "COMPLETED" },
                    label = {
                        Text(
                            L.t("المكتملة 14 يوماً (${completedTests.size}) ✓", "Completed 14 Days (${completedTests.size}) ✓"),
                            fontSize = 12.sp,
                            fontWeight = if (selectedSubTab == "COMPLETED") FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("my_testing_tab_completed"),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF00875A),
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        // Double Rewards Banner (if an app was just tested)
        if (lastTestedAppForDouble != null) {
            val app = lastTestedAppForDouble!!
            item {
                Surface(
                    color = Color(0xFFFFF8E1),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Color(0xFFFFB300)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Celebration, contentDescription = null, tint = Color(0xFFE65100), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    L.t(
                                        "🎉 أحسنت! كسبت +25 عملة لاختبار ${app.appName}",
                                        "🎉 Well done! Earned +25 coins testing ${app.appName}"
                                    ),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color(0xFFE65100)
                                )
                            }
                            IconButton(
                                onClick = { lastTestedAppForDouble = null },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = L.t("إغلاق", "Close"), tint = Color.Gray, modifier = Modifier.size(16.dp))
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    if (activity != null) {
                                        AdMobConfig.showRewardedAd(
                                            activity = activity,
                                            onRewardEarned = {
                                                viewModel.doubleTestReward(25)
                                                Toast.makeText(
                                                    context,
                                                    L.t(
                                                        "🌟 رائع! تمت مضاعفة مكافأتك إلى 50 عملة!",
                                                        "🌟 Awesome! Your reward was doubled to 50 coins!"
                                                    ),
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                lastTestedAppForDouble = null
                                            }
                                        )
                                    } else {
                                        viewModel.doubleTestReward(25)
                                        lastTestedAppForDouble = null
                                    }
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00875A)),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("double_reward_ad_button"),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                Icon(Icons.Default.PlayCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    L.t("ضاعفها إلى 50 عملة 🎬", "Double to 50 Coins 🎬"),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            OutlinedButton(
                                onClick = {
                                    appForFeedback = app
                                    lastTestedAppForDouble = null
                                },
                                modifier = Modifier.height(48.dp),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp)
                            ) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFF8F00), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(L.t("تقييم (+15)", "Rate (+15)"), fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // Empty state
        if (displayList.isEmpty()) {
            item {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            if (selectedSubTab == "ACTIVE") Icons.Default.HourglassEmpty else Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(44.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            if (selectedSubTab == "ACTIVE") {
                                L.t("لا توجد تطبيقات قيد الاختبار حالياً", "No active tests right now")
                            } else {
                                L.t("لم تكمل أي تطبيق بعد (14 يوماً)", "No completed 14-day tests yet")
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            if (selectedSubTab == "ACTIVE") {
                                L.t(
                                    "تصفح شاشة المجتمع وانضم لاختبار تطبيقات المطورين لكسب العملات",
                                    "Browse the Community tab and join developers' apps to earn coins"
                                )
                            } else {
                                L.t(
                                    "استمر في فتح تطبيقاتك الحالية يومياً حتى تصل إلى 14 يوماً مكتملة",
                                    "Keep opening your active apps daily until you reach 14 completed days"
                                )
                            },
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(displayList, key = { it.id }) { test ->
                val isCompleted = test.daysCompleted >= 14
                val checkedToday = remember(test.lastTestDate) { isCheckedToday(test.lastTestDate) }
                val isInstalled = remember(test.packageName) {
                    com.example.util.AppInstallVerifier.isAppInstalled(context, test.packageName)
                }
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // 1. Top Section: App Icon + Name & Package + Daily & Install Badges
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AppIconImage(
                                iconUrl = test.iconUrl,
                                appName = test.appName,
                                packageName = test.packageName,
                                modifier = Modifier.size(52.dp),
                                shape = RoundedCornerShape(14.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    test.appName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    maxLines = 1,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    test.packageName,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(5.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Daily check badge
                                    Surface(
                                        color = if (checkedToday) Color(0xFFE8F5E9) else Color(0xFFFFF8E1),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            if (checkedToday) L.t("تم الفحص اليوم ✓", "Checked Today ✓")
                                            else L.t("يحتاج فحص اليوم", "Needs Check Today"),
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (checkedToday) Color(0xFF2E7D32) else Color(0xFFE65100),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }

                                    // Installed status badge
                                    Surface(
                                        color = if (isInstalled) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                if (isInstalled) Icons.Default.CheckCircle else Icons.Default.Download,
                                                contentDescription = null,
                                                tint = if (isInstalled) Color(0xFF2E7D32) else Color(0xFFE65100),
                                                modifier = Modifier.size(10.dp)
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(
                                                if (isInstalled) L.t("مثبت ✓", "Installed ✓") else L.t("يحتاج تثبيت", "Needs Install"),
                                                fontSize = 9.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isInstalled) Color(0xFF2E7D32) else Color(0xFFE65100)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // 2. Middle Section: 14-Day Testing Progress
                        val progressFraction = (test.daysCompleted.toFloat() / 14f).coerceIn(0f, 1f)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                if (isCompleted) L.t("مكتمل 14 يوماً بنجاح ✓", "Completed 14 Days ✓") else L.t("تقدم الاختبار: اليوم ${test.daysCompleted} من 14", "Progress: Day ${test.daysCompleted} of 14"),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isCompleted) Color(0xFF00875A) else MaterialTheme.colorScheme.primary
                            )
                            Text(
                                if (isCompleted) L.t("100% مكتمل 🎉", "100% Done 🎉") else L.t("متبقي ${maxOf(0, 14 - test.daysCompleted)} يوماً", "${maxOf(0, 14 - test.daysCompleted)} days left"),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { progressFraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (isCompleted) Color(0xFF00875A) else MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // 3. Primary Action Buttons Section
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                modifier = Modifier
                                    .weight(1.25f)
                                    .height(48.dp)
                                    .testTag("log_test_button_${test.packageName}"),
                                onClick = {
                                    viewModel.openAndTestApp(context, test) {
                                        lastTestedAppForDouble = test
                                        activity?.let { AdMobConfig.showInterstitialAfterAction(it) }
                                    }
                                },
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(vertical = 8.dp, horizontal = 8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isCompleted) Color(0xFF00875A) else MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Icon(
                                    if (isCompleted) Icons.Default.CheckCircle else Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    if (isCompleted) L.t("إعادة الفحص", "Re-check") else L.t("تسجيل الفحص (+25)", "Log Test (+25)"),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            OutlinedButton(
                                modifier = Modifier
                                    .weight(1.05f)
                                    .height(48.dp)
                                    .testTag("open_session_button_${test.packageName}"),
                                onClick = { viewModel.openActiveTestSession(test) },
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(vertical = 8.dp, horizontal = 6.dp)
                            ) {
                                Icon(Icons.Default.HourglassBottom, contentDescription = null, modifier = Modifier.size(15.dp), tint = Color(0xFF00875A))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(L.t("جلسة 15د (+50)", "15m (+50)"), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00875A))
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Secondary Actions Row: Launch (if installed), Rate (+15), Report Bug (+15)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isInstalled) {
                                FilledTonalButton(
                                    onClick = { com.example.util.AppInstallVerifier.launchApp(context, test.packageName) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp)
                                ) {
                                    Icon(Icons.Default.Launch, contentDescription = null, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(L.t("تشغيل", "Launch"), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            OutlinedButton(
                                onClick = { appForFeedback = test },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("rate_app_button_${test.packageName}"),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFF8F00), modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(L.t("تقييم (+15)", "Rate (+15)"), fontSize = 11.sp)
                            }

                            OutlinedButton(
                                onClick = { appForBugReport = test },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("report_bug_button_${test.packageName}"),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                Icon(Icons.Default.BugReport, contentDescription = null, tint = Color(0xFFE53935), modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(L.t("بلاغ عطل", "Report Bug"), fontSize = 11.sp, color = Color(0xFFE53935))
                            }
                        }
                    }
                }
            }
        }
    }

    if (appForFeedback != null) {
        val feedbackTarget = appForFeedback!!
        FeedbackDialog(
            appName = feedbackTarget.appName,
            iconUrl = feedbackTarget.iconUrl,
            onDismiss = { appForFeedback = null },
            onSubmit = { rating, comment ->
                viewModel.submitFeedback(feedbackTarget.packageName, rating, comment)
                Toast.makeText(
                    context,
                    L.t(
                        "🌟 شكراً لك! تم إرسال تقييمك وإضافة +15 عملة لرصيدك!",
                        "🌟 Thank you! Your feedback was submitted (+15 coins)!"
                    ),
                    Toast.LENGTH_LONG
                ).show()
                appForFeedback = null
            }
        )
    }

    if (appForBugReport != null) {
        val targetTest = appForBugReport!!
        ReportBugDialog(
            test = targetTest,
            onDismiss = { appForBugReport = null },
            onSubmitBug = { title, details ->
                viewModel.reportAppCrash(
                    packageName = targetTest.packageName,
                    appName = targetTest.appName,
                    title = title,
                    details = details
                ) {
                    appForBugReport = null
                }
            }
        )
    }
}
