package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyTestLog
import com.example.data.model.JoinedTest
import com.example.data.model.TesterFeedback
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.PlayGreen
import com.example.ui.util.AppLanguage
import com.example.ui.util.LocalAppLanguage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DailyMissionsCard(
    joinedTests: List<JoinedTest>,
    currentStreak: Int,
    feedbacks: List<TesterFeedback> = emptyList(),
    dailyLogs: List<DailyTestLog> = emptyList(),
    todayStr: String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()),
    onNavigateToCommunity: () -> Unit = {},
    onQuickFeedbackClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val currentLang = LocalAppLanguage.current
    val isArabic = (currentLang == AppLanguage.AR)

    var isExpanded by remember { mutableStateOf(false) }

    // Mission 1: Checked in at least 1 app today
    val mission1Done = joinedTests.any { it.lastCheckInDate == todayStr }

    // Mission 2: Active Streak > 0
    val mission2Done = currentStreak > 0

    // Mission 3: Sent feedback today or in test history
    val mission3Done = feedbacks.isNotEmpty() || dailyLogs.any { it.notes.isNotBlank() }

    // Mission 4: Joined community tests
    val mission4Done = joinedTests.isNotEmpty()

    val completedMissionsCount = listOf(mission1Done, mission2Done, mission3Done, mission4Done).count { it }
    val missionsProgress = completedMissionsCount / 4f

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("card_daily_missions"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        ),
        border = BorderStroke(
            1.dp,
            if (completedMissionsCount == 4) PlayGreen.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.clickable { isExpanded = !isExpanded }
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (completedMissionsCount == 4) PlayGreen.copy(alpha = 0.15f) else AmberGold.copy(alpha = 0.15f),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (completedMissionsCount == 4) Icons.Default.TaskAlt else Icons.Default.Star,
                                contentDescription = null,
                                tint = if (completedMissionsCount == 4) PlayGreen else AmberGold,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Column {
                        Text(
                            text = if (isArabic) "المهام اليومية السريعة 🎯" else "Daily Missions 🎯",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isArabic) {
                                if (completedMissionsCount == 4) "🎉 اكتملت جميع مهام اليوم (+100 XP)!"
                                else "$completedMissionsCount من 4 مهام مكتملة اليوم"
                            } else {
                                if (completedMissionsCount == 4) "🎉 All missions completed (+100 XP)!"
                                else "$completedMissionsCount of 4 missions completed"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = if (completedMissionsCount == 4) PlayGreen else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = "${(missionsProgress * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (completedMissionsCount == 4) PlayGreen else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    IconButton(
                        onClick = { isExpanded = !isExpanded },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (isExpanded) "Collapse" else "Expand",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress Bar
            LinearProgressIndicator(
                progress = { missionsProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (completedMissionsCount == 4) PlayGreen else AmberGold,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            // Expanded Missions List
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MissionRow(
                        title = if (isArabic) "تجربة تطبيق واحد على الأقل" else "Test at least 1 app today",
                        subtitle = if (isArabic) "افتح تطبيقاً واختبره لمدة 30 ثانية" else "Launch & test an app for 30s",
                        isDone = mission1Done,
                        rewardXp = "+25 XP",
                        icon = Icons.Default.CheckCircle,
                        onClick = null
                    )

                    MissionRow(
                        title = if (isArabic) "الحفاظ على ستريك الاستمرار" else "Maintain Daily Testing Streak",
                        subtitle = if (isArabic) "الستريك الحالي: $currentStreak يوم" else "Current streak: $currentStreak days",
                        isDone = mission2Done,
                        rewardXp = "+25 XP",
                        icon = Icons.Default.LocalFireDepartment,
                        onClick = null
                    )

                    MissionRow(
                        title = if (isArabic) "تقديم ملاحظات ومراجعة لمطور" else "Submit feedback for a developer",
                        subtitle = if (isArabic) "تقييم أداء التطبيق وملاحظات التحسين" else "Rate performance & share suggestions",
                        isDone = mission3Done,
                        rewardXp = "+25 XP",
                        icon = Icons.Default.Feedback,
                        onClick = onQuickFeedbackClick
                    )

                    MissionRow(
                        title = if (isArabic) "الانضمام لتطبيق مجتمعي جديد" else "Join a new community test",
                        subtitle = if (isArabic) "استكشف تطبيقات المطورين الجدد" else "Explore peer apps in the market",
                        isDone = mission4Done,
                        rewardXp = "+25 XP",
                        icon = Icons.Default.Group,
                        onClick = onNavigateToCommunity
                    )
                }
            }
        }
    }
}

@Composable
private fun MissionRow(
    title: String,
    subtitle: String,
    isDone: Boolean,
    rewardXp: String,
    icon: ImageVector,
    onClick: (() -> Unit)?
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isDone) PlayGreen.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
        border = BorderStroke(
            0.5.dp,
            if (isDone) PlayGreen.copy(alpha = 0.3f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = if (isDone) PlayGreen else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(18.dp)
                )

                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = if (isDone) FontWeight.Bold else FontWeight.Medium,
                        color = if (isDone) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (isDone) PlayGreen.copy(alpha = 0.15f) else AmberGold.copy(alpha = 0.12f)
            ) {
                Text(
                    text = rewardXp,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDone) PlayGreen else AmberGold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}
