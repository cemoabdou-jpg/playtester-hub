package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Spellcheck
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import com.example.ui.components.ChangelogGeneratorDialog
import com.example.ui.components.DeviceDiversityMatrixDialog
import com.example.ui.components.PlayPolicyLinterDialog
import com.example.ui.components.PreFlightChecklistDialog
import com.example.ui.components.PrivacyPolicyGeneratorDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import coil.compose.AsyncImage
import com.example.data.model.AppBuildArtifact
import com.example.data.model.AppTesterActivity
import com.example.data.model.DailyTestLog
import com.example.data.model.InvitedTester
import com.example.data.model.TesterFeedback
import com.example.data.model.UserApp
import java.io.File
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
    allTesterActivities: List<AppTesterActivity> = emptyList(),
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
    onUploadLocalApps: (((Boolean, String) -> Unit) -> Unit)? = null,
    isUploadingLocalApps: Boolean = false,
    onUpdateGroupUrl: (appId: Int, groupUrl: String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val todayStr = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()) }
    var uploadStatusMsg by remember { mutableStateOf<String?>(null) }
    var appToShare by remember { mutableStateOf<UserApp?>(null) }
    var appForVersions by remember { mutableStateOf<UserApp?>(null) }
    var appForTesters by remember { mutableStateOf<UserApp?>(null) }
    var appForDailyLogs by remember { mutableStateOf<UserApp?>(null) }
    var appForEditGroupUrl by remember { mutableStateOf<UserApp?>(null) }
    var appForReadinessReport by remember { mutableStateOf<UserApp?>(null) }
    var appForValidator by remember { mutableStateOf<UserApp?>(null) }
    var appForPrivacyPolicy by remember { mutableStateOf<UserApp?>(null) }
    var appForPolicyLinter by remember { mutableStateOf<UserApp?>(null) }
    var appForChangelog by remember { mutableStateOf<UserApp?>(null) }
    var appForPreFlight by remember { mutableStateOf<UserApp?>(null) }
    var appForDiversity by remember { mutableStateOf<UserApp?>(null) }

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
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
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

                            if (userApps.isNotEmpty() && onUploadLocalApps != null) {
                                OutlinedButton(
                                    onClick = {
                                        onUploadLocalApps.invoke { _, msg ->
                                            uploadStatusMsg = msg
                                        }
                                    },
                                    enabled = !isUploadingLocalApps,
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.testTag("btn_my_apps_upload_cloud")
                                ) {
                                    if (isUploadingLocalApps) {
                                        CircularProgressIndicator(modifier = Modifier.size(14.dp), color = PlayGreen, strokeWidth = 2.dp)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("جاري الرفع...", fontSize = 11.sp, color = PlayGreen)
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.CloudSync,
                                            contentDescription = "رفع للسحابة",
                                            tint = PlayGreen,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("رفع للسحابة ☁️", fontSize = 11.sp, color = PlayGreen)
                                    }
                                }
                            } else {
                                Icon(
                                    imageVector = Icons.Default.RocketLaunch,
                                    contentDescription = null,
                                    tint = PlayGreen,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }

                        uploadStatusMsg?.let { msg ->
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = msg,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (msg.contains("نجاح")) SuccessGreen else DangerRed,
                                fontWeight = FontWeight.Bold
                            )
                        }
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
                var isToolkitExpanded by remember { mutableStateOf(false) }
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

                        // App Row: Icon + Title & Package
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                if (app.iconUrl.isNotBlank()) {
                                    AsyncImage(
                                        model = if (app.iconUrl.startsWith("/")) File(app.iconUrl) else app.iconUrl,
                                        contentDescription = "أيقونة ${app.name}",
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(12.dp)),
                                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                    )
                                } else {
                                    Text(
                                        text = app.name.take(2).uppercase(),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = PlayGreen
                                    )
                                }
                            }

                            Column(modifier = Modifier.weight(1f)) {
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
                            }
                        }

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

                        // Safety Buffer & Risk Calculator
                        val safetyBuffer = app.testersCount - 20
                        val isBufferSafe = safetyBuffer >= 3
                        val isBufferCritical = safetyBuffer in 0..2
                        val isBufferDanger = safetyBuffer < 0

                        val bufferBgColor = if (isBufferSafe) SuccessGreen else if (isBufferCritical) AmberGold else DangerRed

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = bufferBgColor.copy(alpha = 0.10f),
                            border = BorderStroke(1.dp, bufferBgColor.copy(alpha = 0.35f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("card_safety_buffer_${app.id}")
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = if (isBufferSafe) Icons.Default.Security else Icons.Default.Warning,
                                            contentDescription = null,
                                            tint = bufferBgColor,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "معامل أمان الـ 20 مختبراً (Buffer):",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = bufferBgColor.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = if (isBufferSafe) "آمن (+${safetyBuffer} احتياطي) 🟢"
                                            else if (isBufferCritical) "حرج (+${safetyBuffer} فقط) 🟡"
                                            else "خطر (${safetyBuffer} تحت الـ20) 🔴",
                                            color = bufferBgColor,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = if (isBufferSafe)
                                        "لديك هامش أمان ممتاز يحميك من توقف عداد جوجل بلاي إذا انسحب أي مختبر."
                                    else if (isBufferCritical)
                                        "تحذير: إذا قام مختبر واحد بحذف التطبيق أو مغادرة المجموعة، سيتوقف عداد الـ 14 يوماً فوراً! ينصح بزيادة المختبرين إلى 23+."
                                    else
                                        "تحذير شديد: عدد المختبرين أقل من 20، لن تحتسب Google Play أيام الاختبار حتى تكتمل الـ 20 مختبراً.",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
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

                        Spacer(modifier = Modifier.height(10.dp))

                        // App Open Tracking & Tester Activity (سجل المطورين الذين فتحوا التطبيق للاختبار)
                        val appActivities = allTesterActivities.filter { it.packageName == app.packageName }
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = PlayGreen.copy(alpha = 0.08f),
                            border = BorderStroke(1.dp, PlayGreen.copy(alpha = 0.35f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("app_tester_activities_${app.id}")
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.RocketLaunch,
                                            contentDescription = null,
                                            tint = PlayGreen,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "المطورون الذين فتحوا تطبيقك للاختبار (${appActivities.size}):",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = PlayGreen
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (appActivities.isNotEmpty()) SuccessGreen.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
                                    ) {
                                        Text(
                                            text = if (appActivities.isNotEmpty()) "تم الفتح بنجاح ✓" else "بانتظار الفتح",
                                            color = if (appActivities.isNotEmpty()) SuccessGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                if (appActivities.isEmpty()) {
                                    Text(
                                        text = "لم يقم أي مطور بفتح التطبيق بعد. عند قيام المطورين بالضغط على أيقونة تطبيقك لتجربته، سيظهر اسم كل مطور فوراً هنا!",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.sp
                                    )
                                } else {
                                    val timeFmt = remember { SimpleDateFormat("MM/dd HH:mm", Locale.getDefault()) }
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        appActivities.take(5).forEach { act ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f), RoundedCornerShape(6.dp))
                                                    .padding(horizontal = 8.dp, vertical = 5.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                                    Icon(
                                                        imageVector = Icons.Default.CheckCircle,
                                                        contentDescription = null,
                                                        tint = SuccessGreen,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = "المطور: ${act.testerName}",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                    Text(
                                                        text = " فتح التطبيق للاختبار",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = PlayGreen,
                                                        fontSize = 11.sp
                                                    )
                                                }
                                                Text(
                                                    text = timeFmt.format(Date(act.timestamp)),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    fontSize = 10.sp
                                                )
                                            }
                                        }
                                        if (appActivities.size > 5) {
                                            Text(
                                                text = "+ ${appActivities.size - 5} مطورين إضافيين قاموا بفتح التطبيق",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = PlayGreen,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp,
                                                modifier = Modifier.padding(top = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { appToShare = app },
                                modifier = Modifier
                                    .weight(1.2f)
                                    .testTag("share_app_btn_${app.id}"),
                                colors = ButtonDefaults.buttonColors(containerColor = PlayGreen),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("مشاركة الدعوة", fontSize = 12.sp)
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
                                Text("المجموعة", fontSize = 12.sp)
                            }

                            // Edit Group Link Button
                            IconButton(
                                onClick = { appForEditGroupUrl = app },
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(PlayGreen.copy(alpha = 0.1f), RoundedCornerShape(10.dp))
                                    .testTag("btn_edit_group_url_${app.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "تعديل رابط المجموعة",
                                    tint = PlayGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            OutlinedButton(
                                onClick = {
                                    val url = if (app.androidOptInUrl.isNotBlank()) app.androidOptInUrl else app.webOptInUrl
                                    openUrl(context, url)
                                },
                                modifier = Modifier.weight(0.9f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Android,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("المتجر", fontSize = 12.sp)
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

                        Spacer(modifier = Modifier.height(10.dp))

                        // Google Play Developer Toolkit Suite (5 Essential Tools - Collapsible to prevent clutter)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("toolkit_suite_${app.id}")
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { isToolkitExpanded = !isToolkitExpanded }
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Build,
                                            contentDescription = null,
                                            tint = PlayGreen,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "مجموعة أدوات كونسول Google Play (5 أدوات)",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    Icon(
                                        imageVector = if (isToolkitExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                        contentDescription = if (isToolkitExpanded) "طي" else "توسيع",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (isToolkitExpanded) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = { appForPrivacyPolicy = app },
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
                                            modifier = Modifier.weight(1f).testTag("btn_policy_gen_${app.id}")
                                        ) {
                                            Icon(Icons.Default.Policy, contentDescription = null, modifier = Modifier.size(14.dp), tint = PlayGreen)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("سياسة الخصوصية", fontSize = 10.sp)
                                        }

                                        OutlinedButton(
                                            onClick = { appForPolicyLinter = app },
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
                                            modifier = Modifier.weight(1f).testTag("btn_policy_linter_${app.id}")
                                        ) {
                                            Icon(Icons.Default.Spellcheck, contentDescription = null, modifier = Modifier.size(14.dp), tint = AmberGold)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("مدقق الكلمات", fontSize = 10.sp)
                                        }

                                        OutlinedButton(
                                            onClick = { appForChangelog = app },
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
                                            modifier = Modifier.weight(1f).testTag("btn_changelog_${app.id}")
                                        ) {
                                            Icon(Icons.Default.Update, contentDescription = null, modifier = Modifier.size(14.dp), tint = CyanAccent)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("سجل التحديث", fontSize = 10.sp)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = { appForPreFlight = app },
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
                                            modifier = Modifier.weight(1f).testTag("btn_preflight_${app.id}")
                                        ) {
                                            Icon(Icons.Default.FactCheck, contentDescription = null, modifier = Modifier.size(14.dp), tint = SuccessGreen)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("فحص الامتثال", fontSize = 10.sp)
                                        }

                                        OutlinedButton(
                                            onClick = { appForDiversity = app },
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
                                            modifier = Modifier.weight(1f).testTag("btn_diversity_${app.id}")
                                        ) {
                                            Icon(Icons.Default.Devices, contentDescription = null, modifier = Modifier.size(14.dp), tint = CyanAccent)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("تنوع الأجهزة", fontSize = 10.sp)
                                        }
                                    }
                                }
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

    // Privacy Policy Generator & Validator dialog
    appForPrivacyPolicy?.let { app ->
        PrivacyPolicyGeneratorDialog(
            app = app,
            onDismiss = { appForPrivacyPolicy = null }
        )
    }

    // Play Policy Linter dialog
    appForPolicyLinter?.let { app ->
        PlayPolicyLinterDialog(
            app = app,
            onDismiss = { appForPolicyLinter = null }
        )
    }

    // Changelog Generator dialog
    appForChangelog?.let { app ->
        ChangelogGeneratorDialog(
            app = app,
            onDismiss = { appForChangelog = null }
        )
    }

    // Pre-Flight Review Checklist dialog
    appForPreFlight?.let { app ->
        PreFlightChecklistDialog(
            app = app,
            onDismiss = { appForPreFlight = null }
        )
    }

    // Device Diversity Matrix dialog
    appForDiversity?.let { app ->
        DeviceDiversityMatrixDialog(
            app = app,
            onDismiss = { appForDiversity = null }
        )
    }

    // Edit Google Group URL Dialog (تعديل رابط مجموعة الاختبار)
    appForEditGroupUrl?.let { app ->
        var newGroupUrl by remember(app) { mutableStateOf(app.googleGroupUrl) }
        AlertDialog(
            onDismissRequest = { appForEditGroupUrl = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = null,
                        tint = PlayGreen,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "تعديل رابط مجموعة Google Group",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "تطبيق: ${app.name}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = PlayGreen
                    )
                    Text(
                        text = "يمكنك تعديل رابط مجموعة الاختبار الخاصة بك في أي وقت. سيتم تحديث الرابط لجميع المختبرين الراغبين بالانضمام فوراً:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = newGroupUrl,
                        onValueChange = { newGroupUrl = it.trim() },
                        label = { Text("رابط Google Group الجديد *") },
                        placeholder = { Text("https://groups.google.com/g/your-testers-group") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_edit_group_url"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newGroupUrl.isNotBlank()) {
                            onUpdateGroupUrl(app.id, newGroupUrl)
                            appForEditGroupUrl = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PlayGreen),
                    modifier = Modifier.testTag("btn_save_edit_group_url")
                ) {
                    Text("حفظ التعديل")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { appForEditGroupUrl = null }) {
                    Text("إلغاء")
                }
            }
        )
    }
}
