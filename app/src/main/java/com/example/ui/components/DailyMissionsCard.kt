package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.MonetizationOn
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
import com.example.ui.theme.SilverAccent
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
    var isExpanded by remember { mutableStateOf(true) }

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
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        border = BorderStroke(1.dp, CrimsonRed.copy(alpha = 0.4f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("daily_missions_card")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.TaskAlt,
                                contentDescription = null,
                                tint = CrimsonRed,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "مركز مهام المختبر اليومية 🎯",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = CrimsonRed.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "$completedMissionsCount/4 مكتمل",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CrimsonRed,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "أكمل المهام لكسب رصيد نقاط إضافي وتنشيط شارات الإنجاز",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(
                    onClick = { isExpanded = !isExpanded },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandMore else Icons.Default.ChevronRight,
                        contentDescription = "توسيع/طي",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Missions Overall Progress Bar
            LinearProgressIndicator(
                progress = { missionsProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = CrimsonRed,
                trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
            )

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier.padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Mission Item 1: Daily App Check-ins
                    MissionRowItem(
                        icon = Icons.Default.FactCheck,
                        title = "تأكيد فحص التطبيقات اليومي",
                        subtitle = if (totalApps == 0) "انضم لتطبيقات من المجتمع لبدء الفحص اليومي"
                        else "تم فحص $checkedTodayCount من أصل $totalApps تطبيق اليوم",
                        rewardText = "+25 نقطة / تطبيق",
                        isDone = mission1Done,
                        badgeColor = if (mission1Done) SuccessGreen else CrimsonRed
                    )

                    // Mission Item 2: Daily Streak
                    MissionRowItem(
                        icon = Icons.Default.Bolt,
                        title = "المحافظة على الستريك النشط",
                        subtitle = "سلسلة الفحص متواصلة لـ $currentStreak أيام على التوالي",
                        rewardText = "مضاعف النقاط x1.2 ⚡",
                        isDone = mission2Done,
                        badgeColor = AmberGold
                    )

                    // Mission Item 3: Quality Feedback
                    MissionRowItem(
                        icon = Icons.Default.RateReview,
                        title = "إرسال تقرير عطل أو ملاحظة جودة",
                        subtitle = "ساعد المطور بتوجيه ملاحظة واكسب نقاط الجودة",
                        rewardText = "+50 نقطة",
                        isDone = mission3Done,
                        badgeColor = CyanAccent,
                        actionButton = if (!mission3Done && onQuickFeedbackClick != null) {
                            {
                                OutlinedButton(
                                    onClick = onQuickFeedbackClick,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text("إرسال تقرير", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        } else null
                    )

                    // Mission Item 4: Explore & Join New Community App
                    MissionRowItem(
                        icon = Icons.Default.GroupAdd,
                        title = "استكشاف والانضمام لتطبيق جديد",
                        subtitle = "تبادل التجربة مع مطور آخر لكسب نقاط ترفع أولوية تطبيقاتك",
                        rewardText = "+100 نقطة",
                        isDone = mission4Done,
                        badgeColor = CrimsonRed,
                        actionButton = {
                            Button(
                                onClick = onNavigateToCommunity,
                                colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text("تصفح المعرض", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun MissionRowItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    rewardText: String,
    isDone: Boolean,
    badgeColor: Color,
    actionButton: (@Composable () -> Unit)? = null
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            0.5.dp,
            if (isDone) SuccessGreen.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
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
                    modifier = Modifier.size(22.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isDone) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = badgeColor.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = rewardText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = badgeColor,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }

                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }

            if (actionButton != null) {
                Spacer(modifier = Modifier.width(8.dp))
                actionButton()
            }
        }
    }
}
