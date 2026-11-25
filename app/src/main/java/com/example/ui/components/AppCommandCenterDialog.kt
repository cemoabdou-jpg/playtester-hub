package com.example.ui.components

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.UserApp
import com.example.ui.util.L
import com.example.ui.viewmodel.TesterViewModel

@Composable
fun AppCommandCenterDialog(
    app: UserApp,
    viewModel: TesterViewModel,
    onOpenComplianceMatrix: () -> Unit,
    onOpenAuditReport: () -> Unit,
    onOpenCrashManager: () -> Unit,
    onOpenTesterHealth: () -> Unit,
    onOpenChangelog: () -> Unit,
    onDismiss: () -> Unit
) {
    val logs by remember(app.packageName) {
        viewModel.getDailyTestLogs(app.packageName)
    }.collectAsState(initial = emptyList())

    val crashes by remember(app.packageName) {
        viewModel.getCrashReports(app.packageName)
    }.collectAsState(initial = emptyList())

    val reviews by remember(app.packageName) {
        viewModel.getFeedback(app.packageName)
    }.collectAsState(initial = emptyList())

    var selectedTab by remember { mutableIntStateOf(0) }

    val uniqueLoggedTesters = remember(logs) {
        logs.map { it.testerEmail.ifBlank { it.testerName } }.distinct().size
    }
    val openCrashesCount = remember(crashes) { crashes.count { it.status != "RESOLVED" } }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            shape = RoundedCornerShape(22.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppIconImage(
                            iconUrl = app.iconUrl,
                            appName = app.name,
                            category = app.appCategory,
                            packageName = app.packageName,
                            modifier = Modifier.size(44.dp),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                L.t("مركز إدارة ${app.name}", "${app.name} Command Center"),
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                maxLines = 1,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                app.packageName,
                                fontSize = 11.sp,
                                maxLines = 1,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_command_center_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = L.t("إغلاق", "Close"))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    edgePadding = 4.dp,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text(L.t("نظرة عامة", "Overview"), fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text(L.t("الأعطال (${crashes.size})", "Crashes (${crashes.size})"), fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text(L.t("المراجعات (${reviews.size})", "Reviews (${reviews.size})"), fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        text = { Text(L.t("الأدوات والتقارير", "Tools & Reports"), fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    when (selectedTab) {
                        0 -> {
                            item {
                                ElevatedCard(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.elevatedCardColors(
                                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(14.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            L.t("مقارنة إحصائيات الامتثال الحقيقية", "Real Compliance Breakdown"),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            CommandMetricBox(
                                                value = "${app.currentTesters}/${app.targetTesters}",
                                                label = L.t("لوحة مطوري جوجل", "Play Console Count"),
                                                modifier = Modifier.weight(1f)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            CommandMetricBox(
                                                value = "$uniqueLoggedTesters",
                                                label = L.t("جلسات موثقة بالمنصة", "Verified App Logs"),
                                                modifier = Modifier.weight(1f)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            CommandMetricBox(
                                                value = "${app.completedDays}/14",
                                                label = L.t("الأيام المكتملة", "Days Completed"),
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                    }
                                }
                            }

                            item {
                                Button(
                                    onClick = {
                                        onDismiss()
                                        onOpenComplianceMatrix()
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("cmd_open_compliance_matrix"),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        L.t("مصفوفة الـ 14 يوماً واستبيان جوجل بلاي", "14-Day Matrix & Play Questionnaire"),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }

                            item {
                                Button(
                                    onClick = {
                                        onDismiss()
                                        onOpenAuditReport()
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("cmd_open_audit_report"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00875A))
                                ) {
                                    Icon(Icons.Default.Assessment, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        L.t("استخراج تقرير التدقيق الرسمي (PDF / CSV)", "Export Official Audit Report (PDF / CSV)"),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }

                        1 -> {
                            item {
                                Surface(
                                    color = if (openCrashesCount == 0) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            if (openCrashesCount == 0)
                                                L.t("سجل الأعطال مستقر حالياً ($openCrashesCount مفتوح)", "Stability clear ($openCrashesCount open)")
                                            else
                                                L.t("يوجد $openCrashesCount تقارير أعطال تحتاج مراجعة", "$openCrashesCount crash report(s) need review"),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = if (openCrashesCount == 0) Color(0xFF1B5E20) else Color(0xFFC62828),
                                            modifier = Modifier.weight(1f)
                                        )
                                        TextButton(
                                            onClick = {
                                                onDismiss()
                                                onOpenCrashManager()
                                            }
                                        ) {
                                            Text(L.t("إدارة كاملة", "Manage All"), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }

                            if (crashes.isEmpty()) {
                                item {
                                    Card(modifier = Modifier.fillMaxWidth()) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(24.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF00875A), modifier = Modifier.size(36.dp))
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                L.t("لا توجد تقارير أعطال مسجلة لهذا التطبيق", "No crash reports logged for this app"),
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            } else {
                                items(crashes, key = { it.id }) { crash ->
                                    Card(modifier = Modifier.fillMaxWidth()) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(crash.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.weight(1f))
                                                Text(
                                                    if (crash.status == "RESOLVED") L.t("محلول ✓", "Resolved ✓") else L.t("مفتوح", "Open"),
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (crash.status == "RESOLVED") Color(0xFF00875A) else Color(0xFFE53935)
                                                )
                                            }
                                            Text("${crash.deviceModel} • ${crash.osVersion}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            if (crash.stackTrace.isNotBlank()) {
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(crash.stackTrace, fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        2 -> {
                            if (reviews.isEmpty()) {
                                item {
                                    Card(modifier = Modifier.fillMaxWidth()) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(24.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Icon(Icons.Default.RateReview, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(36.dp))
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                L.t("لا توجد مراجعات مختبرين بعد لهذا التطبيق", "No tester reviews submitted yet for this app"),
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            } else {
                                items(reviews, key = { it.id }) { fb ->
                                    Card(modifier = Modifier.fillMaxWidth()) {
                                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(L.translateKnown(fb.testerName), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                Text("★ ${fb.rating}/5", color = Color(0xFFFF8F00), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            }
                                            Text(fb.comment, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }
                            }
                        }

                        3 -> {
                            item {
                                OutlinedButton(
                                    onClick = {
                                        onDismiss()
                                        onOpenTesterHealth()
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.People, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color(0xFF00875A))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(L.t("سجل نشاط المختبرين (${logs.size} جلسة)", "Tester Activity Health (${logs.size} logs)"), fontWeight = FontWeight.Bold)
                                }
                            }
                            item {
                                OutlinedButton(
                                    onClick = {
                                        onDismiss()
                                        onOpenChangelog()
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.EditNote, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(L.t("منشئ ملاحظات الإصدار للمتجر", "Play Console Release Notes Builder"), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(L.t("إغلاق", "Close"), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun CommandMetricBox(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontWeight = FontWeight.Black, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
            Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
        }
    }
}
