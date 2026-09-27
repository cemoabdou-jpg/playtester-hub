package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.JoinedTest
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.PlayGreen
import com.example.ui.theme.SuccessGreen

@Composable
fun DailyMissionsCard(
    joinedTests: List<JoinedTest>,
    currentStreak: Int,
    todayStr: String,
    onNavigateToCommunity: () -> Unit,
    onQuickFeedbackClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    val totalApps = joinedTests.size
    val checkedTodayCount = joinedTests.count { it.lastCheckInDate == todayStr }
    val isAllAppsChecked = totalApps > 0 && checkedTodayCount == totalApps

    // Calculate completed missions (out of 4)
    val mission1Done = isAllAppsChecked || (totalApps == 0)
    val mission2Done = currentStreak > 0
    val mission3Done = joinedTests.any { it.feedbackSent }
    val mission4Done = joinedTests.isNotEmpty()

    val completedMissionsCount = listOf(mission1Done, mission2Done, mission3Done, mission4Done).count { it }
    val missionsProgress = completedMissionsCount / 4f

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("daily_missions_card")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Compact Header Row (Clickable to toggle)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = CrimsonRed.copy(alpha = 0.15f),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.TaskAlt,
                                contentDescription = null,
                                tint = CrimsonRed,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "مهام المختبر اليومية 🎯",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (completedMissionsCount == 4) SuccessGreen.copy(alpha = 0.2f) else CrimsonRed.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "$completedMissionsCount/4",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (completedMissionsCount == 4) SuccessGreen else CrimsonRed,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = if (isExpanded) "انقر للطي وتوفير المساحة" else "أكمل المهام اليومية لكسب رصيد نقاط مضاعف (انقر للتفاصيل)",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(
                    onClick = { isExpanded = !isExpanded },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "توسيع/طي المهام",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Missions Overall Progress Bar
            LinearProgressIndicator(
                progress = { missionsProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = if (completedMissionsCount == 4) SuccessGreen else CrimsonRed,
                trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
            )

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier.padding(top = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Mission Item 1: Daily App Check-ins
                    CompactMissionItem(
                        icon = Icons.Default.FactCheck,
                        title = "تأكيد فحص التطبيقات",
                        rewardText = "+25 ن",
                        isDone = mission1Done,
                        badgeColor = if (mission1Done) SuccessGreen else CrimsonRed
                    )

                    // Mission Item 2: Daily Streak
                    CompactMissionItem(
                        icon = Icons.Default.Bolt,
                        title = "الستريك النشط ($currentStreak أيام)",
                        rewardText = "x1.2 ⚡",
                        isDone = mission2Done,
                        badgeColor = AmberGold
                    )

                    // Mission Item 3: Quality Feedback / Bug Report (Streamlined Compact Button)
                    CompactMissionItem(
                        icon = Icons.Default.RateReview,
                        title = "إرسال تقرير أو ملاحظة",
                        rewardText = "+50 ن",
                        isDone = mission3Done,
                        badgeColor = CyanAccent,
                        actionButton = if (!mission3Done && onQuickFeedbackClick != null) {
                            {
                                OutlinedButton(
                                    onClick = onQuickFeedbackClick,
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.height(28.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text("إرسال تقرير", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        } else null
                    )

                    // Mission Item 4: Explore & Join New Community App (Streamlined Compact Button)
                    CompactMissionItem(
                        icon = Icons.Default.GroupAdd,
                        title = "تصفح واختبار تطبيق جديد",
                        rewardText = "+100 ن",
                        isDone = mission4Done,
                        badgeColor = CrimsonRed,
                        actionButton = {
                            Button(
                                onClick = onNavigateToCommunity,
                                colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.height(28.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text("تصفح المعرض", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CompactMissionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    rewardText: String,
    isDone: Boolean,
    badgeColor: Color,
    actionButton: (@Composable () -> Unit)? = null
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            0.5.dp,
            if (isDone) SuccessGreen.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = if (isDone) Icons.Default.CheckCircle else icon,
                    contentDescription = null,
                    tint = if (isDone) SuccessGreen else badgeColor,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.width(6.dp))

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = badgeColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = rewardText,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
            }

            if (actionButton != null) {
                Spacer(modifier = Modifier.width(6.dp))
                actionButton()
            }
        }
    }
}
