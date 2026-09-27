package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
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
import com.example.data.model.AppBuildArtifact
import com.example.data.model.InvitedTester
import com.example.data.model.UserApp
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.PlayGreen
import com.example.ui.theme.SilverAccent
import com.example.ui.theme.SuccessGreen

data class HealthCheckItem(
    val title: String,
    val description: String,
    val isPassed: Boolean,
    val weight: Int
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AppHealthDiagnosticsCard(
    app: UserApp,
    artifacts: List<AppBuildArtifact>,
    testers: List<InvitedTester>,
    onOpenReadinessReport: () -> Unit,
    onInviteTesterClick: () -> Unit,
    onManageVersionsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    // Diagnostic rules
    val checkTesters = HealthCheckItem(
        title = "المختبرين (20/20)",
        description = "توفير 20 مختبراً حقيقياً في قائمة الاختبار المغلق",
        isPassed = app.testersCount >= 20 || testers.size >= 20,
        weight = 30
    )
    val checkDays = HealthCheckItem(
        title = "مدة الـ 14 يوماً",
        description = "مرور 14 يوماً متواصلة من الاختبار الفعلي",
        isPassed = app.daysElapsed >= 14,
        weight = 30
    )
    val checkArtifact = HealthCheckItem(
        title = "ملف الإصدار (AAB/APK)",
        description = "رفع واستبدال إصدار فعال على متجر Play",
        isPassed = artifacts.any { it.isActiveForTesting },
        weight = 15
    )
    val checkGoogleGroup = HealthCheckItem(
        title = "رابط مجموعات Google Groups",
        description = "ربط رابط مجموعة Google لتمكين انضمام المختبرين",
        isPassed = app.googleGroupUrl.isNotBlank() && app.googleGroupUrl.contains("groups.google.com"),
        weight = 15
    )
    val checkOptInLink = HealthCheckItem(
        title = "رابط الانضمام للتجربة Opt-In Link",
        description = "توفير رابط الويب أو الأندرويد لقبول الاختبار",
        isPassed = app.androidOptInUrl.isNotBlank() || app.webOptInUrl.isNotBlank(),
        weight = 10
    )

    val allChecks = listOf(checkTesters, checkDays, checkArtifact, checkGoogleGroup, checkOptInLink)
    val totalScore = allChecks.filter { it.isPassed }.sumOf { it.weight }
    val passedCount = allChecks.count { it.isPassed }

    val scoreColor = when {
        totalScore >= 90 -> SuccessGreen
        totalScore >= 60 -> AmberGold
        else -> CrimsonRed
    }

    val scoreLabel = when {
        totalScore >= 90 -> "جاهز للنشر على Google Play 🏆"
        totalScore >= 60 -> "قريب من الاكتمال - استكمل الشروط ⚡"
        else -> "يحتاج لاستكمال متطلبات الاختبار المغلق ⚠️"
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        border = BorderStroke(1.dp, scoreColor.copy(alpha = 0.3f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("app_health_diagnostics_card_${app.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Title Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = CircleShape,
                        color = scoreColor.copy(alpha = 0.15f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.HealthAndSafety,
                                contentDescription = null,
                                tint = scoreColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "مؤشر صحة وقبول التطبيق: $totalScore%",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = scoreColor
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = scoreColor.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "$passedCount/5 معايير",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = scoreColor,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = scoreLabel,
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
                        contentDescription = "تفاصيل التشخيص",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Score Progress Bar
            LinearProgressIndicator(
                progress = { totalScore / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = scoreColor,
                trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Diagnostic Chips Row
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                DiagnosticStatusChip(
                    label = "20 مختبراً: ${app.testersCount}/20",
                    isPassed = checkTesters.isPassed
                )
                DiagnosticStatusChip(
                    label = "الـ 14 يوماً: ${app.daysElapsed}/14",
                    isPassed = checkDays.isPassed
                )
                DiagnosticStatusChip(
                    label = if (checkArtifact.isPassed) "إصدار مرفوع ✓" else "بدون AAB/APK",
                    isPassed = checkArtifact.isPassed
                )
                DiagnosticStatusChip(
                    label = if (checkGoogleGroup.isPassed) "Google Group ✓" else "رابط المجموعات مفقود",
                    isPassed = checkGoogleGroup.isPassed
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier.padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "قائمة فحص معايير Google Play التفصيلية:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    allChecks.forEach { check ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(
                                0.5.dp,
                                if (check.isPassed) SuccessGreen.copy(alpha = 0.4f) else CrimsonRed.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Icon(
                                        imageVector = if (check.isPassed) Icons.Default.CheckCircle else Icons.Default.ReportProblem,
                                        contentDescription = null,
                                        tint = if (check.isPassed) SuccessGreen else CrimsonRed,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = check.title,
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = check.description,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Text(
                                    text = if (check.isPassed) "+${check.weight}%" else "0%",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (check.isPassed) SuccessGreen else CrimsonRed
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))
                }
            }
        }
    }
}

@Composable
private fun DiagnosticStatusChip(
    label: String,
    isPassed: Boolean
) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = if (isPassed) SuccessGreen.copy(alpha = 0.12f) else CrimsonRed.copy(alpha = 0.12f),
        border = BorderStroke(0.5.dp, if (isPassed) SuccessGreen.copy(alpha = 0.4f) else CrimsonRed.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isPassed) Icons.Default.CheckCircle else Icons.Default.ReportProblem,
                contentDescription = null,
                tint = if (isPassed) SuccessGreen else CrimsonRed,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isPassed) SuccessGreen else CrimsonRed
            )
        }
    }
}
