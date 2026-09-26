package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppBuildArtifact
import com.example.data.model.DailyTestLog
import com.example.data.model.InvitedTester
import com.example.data.model.TesterFeedback
import com.example.data.model.UserApp
import com.example.ui.components.AppHealthDiagnosticsCard
import com.example.ui.components.DailyTestLogsHistoryDialog
import com.example.ui.components.OptInLinkValidatorDialog
import com.example.ui.components.ProductionReadinessReportDialog
import com.example.ui.components.ShareInviteDialog
import com.example.ui.components.SmartFeedbackClassifierCard
import com.example.ui.components.TesterInvitationDialog
import com.example.ui.components.VersionManagerDialog
import com.example.ui.components.openUrl
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DangerRed
import com.example.ui.theme.PlayGreen
import com.example.ui.theme.SuccessGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MyAppsScreen(
    userApps: List<UserApp>,
    allArtifacts: List<AppBuildArtifact>,
    allInvitedTesters: List<InvitedTester>,
    allDailyTestLogs: List<DailyTestLog> = emptyList(),
    allFeedback: List<TesterFeedback> = emptyList(),
    onAddAppClick: () -> Unit,
    onDeleteApp: (Int) -> Unit,
    onUploadArtifact: (
        userAppId: Int,
        versionName: String,
        versionCode: Int,
        artifactType: String,
        fileName: String,
        fileSizeMb: Double,
        releaseNotes: String,
        setAsActive: Boolean
    ) -> Unit,
    onSetActiveVersion: (userAppId: Int, artifactId: Int) -> Unit,
    onDeleteArtifact: (artifactId: Int) -> Unit,
    onInviteTester: (userAppId: Int, email: String, notes: String) -> Unit,
    onUpdateTesterStatus: (testerId: Int, status: String) -> Unit,
    onRemoveTester: (testerId: Int) -> Unit,
    onSendDailyReminder: (UserApp) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val todayStr = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()) }
    var appToShare by remember { mutableStateOf<UserApp?>(null) }
    var appForVersions by remember { mutableStateOf<UserApp?>(null) }
    var appForTesters by remember { mutableStateOf<UserApp?>(null) }
    var appForDailyLogs by remember { mutableStateOf<UserApp?>(null) }
    var appForReadinessReport by remember { mutableStateOf<UserApp?>(null) }
    var appForValidator by remember { mutableStateOf<UserApp?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("my_apps_list"),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Top CTA button
            item {
                Button(
                    onClick = onAddAppClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("btn_add_my_app"),
                    colors = ButtonDefaults.buttonColors(containerColor = PlayGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "نشر تطبيق جديد لطلب 20 مختبراً",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Summary banner
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "تطبيقاتك المنشورة (${userApps.size})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "نشر التطبيقات متاح مجاناً وبدون أي شروط لجميع المطورين المسجلين",
                                style = MaterialTheme.typography.bodySmall,
                                color = PlayGreen
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.RocketLaunch,
                            contentDescription = null,
                            tint = PlayGreen,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            // Empty state
            if (userApps.isEmpty()) {
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
                                imageVector = Icons.Default.RocketLaunch,
                                contentDescription = null,
                                modifier = Modifier.size(54.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "لم تقم بإضافة أي تطبيق بعد",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "أضف رابط مجموعتك ورابط الاشتراك لتستقبل 20 مختبراً من مجتمع المطورين بسهولة.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                            )
                        }
                    }
                }
            }

            // User Apps
            items(userApps, key = { it.id }) { app ->
                val testerProgress = (app.testersCount.toFloat() / app.targetTesters.toFloat()).coerceIn(0f, 1f)
                val daysProgress = (app.daysElapsed.toFloat() / app.targetDays.toFloat()).coerceIn(0f, 1f)
                val isReadyForProduction = app.testersCount >= app.targetTesters && app.daysElapsed >= app.targetDays

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("user_app_card_${app.id}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(
                        1.dp,
                        if (isReadyForProduction) SuccessGreen.copy(alpha = 0.7f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isReadyForProduction) SuccessGreen.copy(alpha = 0.15f) else CyanAccent.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = if (isReadyForProduction) "جاهز لطلب الإنتاج! ✓" else "قيد الاختبار (14 يوماً)",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isReadyForProduction) SuccessGreen else CyanAccent,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            IconButton(
                                onClick = { onDeleteApp(app.id) },
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("delete_user_app_${app.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "حذف",
                                    tint = DangerRed.copy(alpha = 0.7f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = app.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = app.packageName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Testers Progress
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "المختبرين المنضمين:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${app.testersCount} من ${app.targetTesters} مختبر",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (app.testersCount >= app.targetTesters) SuccessGreen else CyanAccent
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { testerProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (app.testersCount >= app.targetTesters) SuccessGreen else CyanAccent,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Days Progress
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "الأيام المنقضية في الاختبار:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "اليوم ${app.daysElapsed} من 14 يوماً",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (app.daysElapsed >= 14) SuccessGreen else MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { daysProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (app.daysElapsed >= 14) SuccessGreen else PlayGreen,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Developer Management Tools: Versions & Invited Testers
                        val appArtifacts = allArtifacts.filter { it.userAppId == app.id }
                        val appTesters = allInvitedTesters.filter { it.userAppId == app.id }
                        val activeArtifact = appArtifacts.find { it.isActiveForTesting }

                        // Health & Google Play Compliance Diagnostics Card
                        AppHealthDiagnosticsCard(
                            app = app,
                            artifacts = appArtifacts,
                            testers = appTesters,
                            onOpenReadinessReport = { appForReadinessReport = app },
                            onInviteTesterClick = { appForTesters = app },
                            onManageVersionsClick = { appForVersions = app }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "الإصدار النشط: ${activeArtifact?.let { "v${it.versionName} (${it.artifactType})" } ?: "لا يوجد إصدار محدد"}",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (activeArtifact != null) PlayGreen else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "المختبرين المدعوين: ${appTesters.size}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { appForVersions = app },
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("manage_versions_btn_${app.id}"),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Default.Layers, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("إدارة الحزم (${appArtifacts.size})", fontSize = 12.sp)
                                    }

                                    OutlinedButton(
                                        onClick = { appForTesters = app },
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("manage_testers_btn_${app.id}"),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Default.GroupAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("دعوة المختبرين (${appTesters.size})", fontSize = 12.sp)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Daily Test Verification Status for Google Play compliance
                        val appLogs = allDailyTestLogs.filter { it.packageName == app.packageName }
                        val todayLogsCount = appLogs.count { it.testDate == todayStr }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (todayLogsCount > 0) SuccessGreen.copy(alpha = 0.12f) else AmberGold.copy(alpha = 0.12f),
                            border = BorderStroke(
                                1.dp,
                                if (todayLogsCount > 0) SuccessGreen.copy(alpha = 0.35f) else AmberGold.copy(alpha = 0.4f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("app_daily_testing_status_${app.id}")
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.FactCheck,
                                            contentDescription = null,
                                            tint = if (todayLogsCount > 0) SuccessGreen else AmberGold,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "توثيق التجربة اليومية (إلزامي):",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "$todayLogsCount تأكيد اليوم • إجمالي ${appLogs.size} تأكيد مسجل",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                OutlinedButton(
                                    onClick = { appForDailyLogs = app },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("btn_view_daily_logs_${app.id}")
                                ) {
                                    Text("عرض السجل", fontSize = 12.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { appToShare = app },
                                modifier = Modifier
                                    .weight(1.3f)
                                    .testTag("share_app_btn_${app.id}"),
                                colors = ButtonDefaults.buttonColors(containerColor = PlayGreen),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("مشاركة الدعوة")
                            }

                            OutlinedButton(
                                onClick = { openUrl(context, app.googleGroupUrl) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Group,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("المجموعة")
                            }

                            OutlinedButton(
                                onClick = {
                                    val url = if (app.androidOptInUrl.isNotBlank()) app.androidOptInUrl else app.webOptInUrl
                                    openUrl(context, url)
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Android,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("المتجر")
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { appForValidator = app },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_validate_links_${app.id}"),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.QrCode2,
                                    contentDescription = null,
                                    tint = CyanAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("فحص الروابط وQR", fontSize = 12.sp)
                            }

                            // Production Readiness Report Button
                            OutlinedButton(
                                onClick = { appForReadinessReport = app },
                                modifier = Modifier
                                    .weight(1.5f)
                                    .testTag("btn_readiness_report_${app.id}"),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, if (isReadyForProduction) SuccessGreen else MaterialTheme.colorScheme.outline)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Assignment,
                                    contentDescription = null,
                                    tint = if (isReadyForProduction) SuccessGreen else CyanAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "تقرير الجاهزية",
                                    color = if (isReadyForProduction) SuccessGreen else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            // Smart Feedback Classifier Card
            if (allFeedback.isNotEmpty()) {
                item {
                    SmartFeedbackClassifierCard(
                        feedbacks = allFeedback
                    )
                }
            }
        }
    }

    // Share dialog
    appToShare?.let { app ->
        ShareInviteDialog(
            app = app,
            onDismiss = { appToShare = null }
        )
    }

    // Version Manager dialog
    appForVersions?.let { app ->
        val artifacts = allArtifacts.filter { it.userAppId == app.id }
        VersionManagerDialog(
            app = app,
            artifacts = artifacts,
            onUploadArtifact = { vName, vCode, aType, fName, fSize, rNotes, active ->
                onUploadArtifact(app.id, vName, vCode, aType, fName, fSize, rNotes, active)
            },
            onSetActive = { artifactId ->
                onSetActiveVersion(app.id, artifactId)
            },
            onDeleteArtifact = onDeleteArtifact,
            onDismiss = { appForVersions = null }
        )
    }

    // Tester Invitation dialog
    appForTesters?.let { app ->
        val testers = allInvitedTesters.filter { it.userAppId == app.id }
        TesterInvitationDialog(
            app = app,
            invitedTesters = testers,
            onInviteTester = { email, notes ->
                onInviteTester(app.id, email, notes)
            },
            onUpdateStatus = onUpdateTesterStatus,
            onRemoveTester = onRemoveTester,
            onDismiss = { appForTesters = null }
        )
    }

    // Daily Test Logs History dialog
    appForDailyLogs?.let { app ->
        val logs = allDailyTestLogs.filter { it.packageName == app.packageName }
        DailyTestLogsHistoryDialog(
            app = app,
            logs = logs,
            onSendReminder = {
                onSendDailyReminder(app)
                appForDailyLogs = null
            },
            onDismiss = { appForDailyLogs = null }
        )
    }

    // Production Readiness Report dialog
    appForReadinessReport?.let { app ->
        val artifacts = allArtifacts.filter { it.userAppId == app.id }
        val testers = allInvitedTesters.filter { it.userAppId == app.id }
        val logs = allDailyTestLogs.filter { it.packageName == app.packageName }
        val feedback = allFeedback.filter { it.packageName == app.packageName }
        ProductionReadinessReportDialog(
            app = app,
            artifacts = artifacts,
            invitedTesters = testers,
            dailyLogs = logs,
            feedbackList = feedback,
            onDismiss = { appForReadinessReport = null }
        )
    }

    // Opt-In Link Validator & QR Generator dialog
    appForValidator?.let { app ->
        OptInLinkValidatorDialog(
            app = app,
            onDismiss = { appForValidator = null }
        )
    }
}
