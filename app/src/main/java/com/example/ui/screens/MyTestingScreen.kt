package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.JoinedTest
import com.example.ui.components.DailyMissionsCard
import com.example.ui.components.DeveloperXpRankCard
import com.example.ui.components.DailyTestConfirmationDialog
import com.example.ui.components.FeedbackDialog
import com.example.ui.components.JoinedTestCard
import com.example.ui.components.TestingHeatmapCard
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.PlayGreen
import com.example.ui.theme.SuccessGreen
import com.example.util.NotificationHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MyTestingScreen(
    joinedTests: List<JoinedTest>,
    currentStreak: Int = 0,
    onCheckIn: (JoinedTest) -> Unit = {},
    onConfirmDailyTest: ((JoinedTest, Int, Int, String) -> Unit)? = null,
    onSendFeedback: (JoinedTest, String, Int, String) -> Unit,
    onDeleteTest: (Int) -> Unit,
    onNavigateToCommunity: () -> Unit,
    onRecordAppOpened: (packageName: String, appName: String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTestForFeedback by remember { mutableStateOf<JoinedTest?>(null) }
    var selectedTestForDailyConfirmation by remember { mutableStateOf<JoinedTest?>(null) }
    var reminderEnabled by remember { mutableStateOf(NotificationHelper.isDailyReminderEnabled(context)) }
    var reminderSentNotice by remember { mutableStateOf(false) }

    val todayStr = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()) }
    val activeCount = joinedTests.count { !it.isCompleted }
    val completedCount = joinedTests.count { it.isCompleted }
    val pendingDailyConfirmCount = joinedTests.count { !it.isCompleted && it.lastCheckInDate != todayStr }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("my_testing_list"),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Stats Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "$activeCount",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = CyanAccent
                            )
                            Text(
                                text = "تطبيقات قيد الاختبار",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "$completedCount",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = SuccessGreen
                            )
                            Text(
                                text = "أتممت 14 يوماً",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Developer XP Level & Rank
            item {
                val totalPoints = joinedTests.sumOf { it.daysCompleted * 25 } + (currentStreak * 25)
                DeveloperXpRankCard(
                    points = totalPoints,
                    streakDays = currentStreak
                )
            }

            // Daily Developer Missions Card
            item {
                DailyMissionsCard(
                    joinedTests = joinedTests,
                    currentStreak = currentStreak,
                    todayStr = todayStr,
                    onNavigateToCommunity = onNavigateToCommunity,
                    onQuickFeedbackClick = if (joinedTests.isNotEmpty()) {
                        { selectedTestForFeedback = joinedTests.first() }
                    } else null
                )
            }

            // Empty state
            if (joinedTests.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                modifier = Modifier.size(54.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "لم تنضم لأي اختبار بعد!",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "انضم لتطبيقات المطورين وافتحها يومياً لتبادل الاختبار، ويمكنك نشر تطبيقاتك مجاناً وبدون أي شروط!",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                            )
                            Button(
                                onClick = onNavigateToCommunity,
                                colors = ButtonDefaults.buttonColors(containerColor = PlayGreen),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.size(6.dp))
                                Text("تصفح سوق التطبيقات")
                            }
                        }
                    }
                }
            }

            // List of joined tests
            items(joinedTests, key = { it.id }) { test ->
                JoinedTestCard(
                    test = test,
                    onCheckInClick = { selectedTestForDailyConfirmation = test },
                    onFeedbackClick = { selectedTestForFeedback = test },
                    onDeleteClick = { onDeleteTest(test.id) },
                    onRecordOpened = onRecordAppOpened
                )
            }
        }
    }

    // Daily test confirmation dialog
    selectedTestForDailyConfirmation?.let { test ->
        DailyTestConfirmationDialog(
            test = test,
            onDismiss = { selectedTestForDailyConfirmation = null },
            onConfirm = { durationMinutes, rating, notes ->
                if (onConfirmDailyTest != null) {
                    onConfirmDailyTest(test, durationMinutes, rating, notes)
                } else {
                    onCheckIn(test)
                }
                selectedTestForDailyConfirmation = null
            }
        )
    }

    // Feedback dialog
    selectedTestForFeedback?.let { test ->
        FeedbackDialog(
            test = test,
            onDismiss = { selectedTestForFeedback = null },
            onSubmitFeedback = { content, rating, type ->
                onSendFeedback(test, content, rating, type)
                selectedTestForFeedback = null
            }
        )
    }
}
