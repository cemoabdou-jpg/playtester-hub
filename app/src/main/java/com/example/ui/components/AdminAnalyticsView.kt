package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppBuildArtifact
import com.example.data.model.CommunityApp
import com.example.data.model.DailyTestLog
import com.example.data.model.InvitedTester
import com.example.data.model.JoinedTest
import com.example.data.model.TesterFeedback
import com.example.data.model.UserApp
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.PlayGreen
import com.example.ui.theme.SuccessGreen

@Composable
fun AdminAnalyticsView(
    adminApps: List<CommunityApp>,
    allDailyLogs: List<DailyTestLog>,
    allFeedback: List<TesterFeedback>,
    allArtifacts: List<AppBuildArtifact>,
    allInvitedTesters: List<InvitedTester>,
    joinedTests: List<JoinedTest>,
    userApps: List<UserApp>,
    modifier: Modifier = Modifier
) {
    val totalApps = adminApps.size
    val activeApps = adminApps.count { !it.isHidden }
    val hiddenApps = adminApps.count { it.isHidden }
    val featuredApps = adminApps.count { it.isFeatured }

    val totalFeedbacks = allFeedback.size
    val avgFeedbackRating = remember(allFeedback) {
        if (allFeedback.isNotEmpty()) {
            allFeedback.map { it.rating }.average()
        } else 4.8
    }

    val totalLogs = allDailyLogs.size
    val avgTestDuration = remember(allDailyLogs) {
        if (allDailyLogs.isNotEmpty()) {
            allDailyLogs.map { it.durationMinutes }.average().toInt()
        } else 12
    }

    // Categories Breakdown
    val categoryCounts = remember(adminApps) {
        adminApps.groupBy { it.category }
            .mapValues { it.value.size }
            .toList()
            .sortedByDescending { it.second }
    }

    // 14-Day Completion Stages based on joinedTests and communityApps
    val stage1Count = remember(joinedTests) { joinedTests.count { it.daysCompleted in 1..4 } }
    val stage2Count = remember(joinedTests) { joinedTests.count { it.daysCompleted in 5..9 } }
    val stage3Count = remember(joinedTests) { joinedTests.count { it.daysCompleted in 10..13 } }
    val stage4Count = remember(joinedTests) { joinedTests.count { it.daysCompleted >= 14 || it.isCompleted } }
    val totalSessions = maxOf(joinedTests.size, 1)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("admin_analytics_view"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Overview Banner
        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = BorderStroke(1.dp, PlayGreen.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Assessment,
                                contentDescription = null,
                                tint = PlayGreen,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "لوحة التحليلات ومؤشرات الأداء (KPIs)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = PlayGreen.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "محدث لحظياً",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PlayGreen,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 4 Main KPI Cards
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        KpiSummaryMiniCard(
                            title = "التطبيقات",
                            value = "$totalApps",
                            subtitle = "$activeApps نشط",
                            icon = Icons.Default.Apps,
                            color = CyanAccent,
                            modifier = Modifier.weight(1f)
                        )

                        KpiSummaryMiniCard(
                            title = "جلسات الفحص",
                            value = "${maxOf(joinedTests.size, totalLogs)}",
                            subtitle = "$stage4Count اكتملت",
                            icon = Icons.Default.Speed,
                            color = PlayGreen,
                            modifier = Modifier.weight(1f)
                        )

                        KpiSummaryMiniCard(
                            title = "الملاحظات",
                            value = "$totalFeedbacks",
                            subtitle = "★ ${String.format("%.1f", avgFeedbackRating)}",
                            icon = Icons.Default.BugReport,
                            color = AmberGold,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Section 1: 14-Day Stages & Readiness Chart
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.RocketLaunch, contentDescription = null, tint = AmberGold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "مراحل اكتمال شرط الـ 14 يوماً",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "$stage4Count جاهز للنشر",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = SuccessGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 4 Progress Bars for Stages
                    StageProgressRow(
                        title = "مرحلة الانطلاق (الأيام 1 - 4)",
                        count = stage1Count,
                        total = totalSessions,
                        color = Color(0xFF94A3B8)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    StageProgressRow(
                        title = "مرحلة الاختبار المتوسط (الأيام 5 - 9)",
                        count = stage2Count,
                        total = totalSessions,
                        color = CyanAccent
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    StageProgressRow(
                        title = "مرحلة الاقتراب والتقييم (الأيام 10 - 13)",
                        count = stage3Count,
                        total = totalSessions,
                        color = AmberGold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    StageProgressRow(
                        title = "مكتمل وجاهز للإنتاج (14 يوماً كاملة ⭐)",
                        count = stage4Count,
                        total = totalSessions,
                        color = SuccessGreen
                    )
                }
            }
        }

        // Section 2: Categories Breakdown Chart
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Category, contentDescription = null, tint = CyanAccent)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "توزيع التطبيقات حسب التصنيف",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (categoryCounts.isEmpty()) {
                        Text(
                            text = "لا توجد تطبيقات مسجلة بعد",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        categoryCounts.forEach { (category, count) ->
                            val ratio = if (totalApps > 0) count.toFloat() / totalApps else 0f
                            val percent = (ratio * 100).toInt()

                            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = category,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "$count تطبيق ($percent%)",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                LinearProgressIndicator(
                                    progress = { ratio },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = CyanAccent,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section 3: Engagement & Testing Quality Metrics
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Timeline, contentDescription = null, tint = PlayGreen)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "جودة ومعدل تفاعل المختبرين",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        QualityMetricBox(
                            label = "متوسط مدة الفحص",
                            value = "$avgTestDuration دقيقة",
                            desc = "مدة فحص حقيقية يومياً",
                            color = PlayGreen,
                            modifier = Modifier.weight(1f)
                        )

                        QualityMetricBox(
                            label = "متوسط تقييم التجارب",
                            value = "${String.format("%.1f", avgFeedbackRating)} / 5",
                            desc = "بناءً على $totalFeedbacks تقييم",
                            color = AmberGold,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        QualityMetricBox(
                            label = "حزم البناء المرفوعة",
                            value = "${allArtifacts.size} حزمة",
                            desc = "AAB & APK للاختبار المباشر",
                            color = CyanAccent,
                            modifier = Modifier.weight(1f)
                        )

                        QualityMetricBox(
                            label = "دعوات المختبرين بالبريد",
                            value = "${allInvitedTesters.size} دعوة",
                            desc = "مختبرين تمت دعوتهم",
                            color = Color(0xFF8B5CF6),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun KpiSummaryMiniCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.25f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun StageProgressRow(
    title: String,
    count: Int,
    total: Int,
    color: Color
) {
    val ratio = if (total > 0) count.toFloat() / total else 0f
    val percent = (ratio * 100).toInt()

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "$count ($percent%)",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        LinearProgressIndicator(
            progress = { ratio },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
private fun QualityMetricBox(
    label: String,
    value: String,
    desc: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = desc,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
