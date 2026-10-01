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
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Spellcheck
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import com.example.ui.components.ChangelogGeneratorDialog
import com.example.ui.components.ConsoleShortcutsAndBundleHelperDialog
import com.example.ui.components.DeviceDiversityMatrixDialog
import com.example.ui.components.PlayApprovalGuideDialog
import com.example.ui.components.PlayPolicyLinterDialog
import com.example.ui.components.PreFlightChecklistDialog
import com.example.ui.components.PrivacyPolicyGeneratorDialog
import com.example.ui.components.ProductionReadinessCertificateDialog
import com.example.ui.components.TrackProgressTimelineDialog
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.ui.components.AppReviewsDialog
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
import com.example.ui.util.L
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
    onSyncPlayIcon: ((packageName: String, appId: Int) -> Unit)? = null,
    onReplyToReview: (feedbackId: Int, replyText: String) -> Unit = { _, _ -> },
    onDeleteFeedback: (feedbackId: Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val s = L.strings()
    val todayStr = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()) }
    var uploadStatusMsg by remember { mutableStateOf<String?>(null) }
    var appToShare by remember { mutableStateOf<UserApp?>(null) }
    var appForVersions by remember { mutableStateOf<UserApp?>(null) }
    var appForTesters by remember { mutableStateOf<UserApp?>(null) }
    var appForDailyLogs by remember { mutableStateOf<UserApp?>(null) }
    var appForEditGroupUrl by remember { mutableStateOf<UserApp?>(null) }
    var appForReadinessReport by remember { mutableStateOf<UserApp?>(null) }
    var appForTimeline by remember { mutableStateOf<UserApp?>(null) }
    var appForConsoleHelper by remember { mutableStateOf<UserApp?>(null) }
    var appForCertificate by remember { mutableStateOf<UserApp?>(null) }
    var showApprovalGuide by remember { mutableStateOf(false) }
    var appForValidator by remember { mutableStateOf<UserApp?>(null) }
    var appForPrivacyPolicy by remember { mutableStateOf<UserApp?>(null) }
    var appForPolicyLinter by remember { mutableStateOf<UserApp?>(null) }
    var appForChangelog by remember { mutableStateOf<UserApp?>(null) }
    var appForPreFlight by remember { mutableStateOf<UserApp?>(null) }
    var appForDiversity by remember { mutableStateOf<UserApp?>(null) }
    var appForReviews by remember { mutableStateOf<UserApp?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("my_apps_list"),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Sleek Top Action Bar (Compact & Professional)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onAddAppClick,
                        modifier = Modifier
                            .weight(1.3f)
                            .height(36.dp)
                            .testTag("btn_add_my_app"),
                        colors = ButtonDefaults.buttonColors(containerColor = PlayGreen),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = s.postAppHeroCta,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            maxLines = 1
                        )
                    }

                    // Play Approval Secrets Pill
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = AmberGold.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, AmberGold.copy(alpha = 0.35f)),
                        modifier = Modifier
                            .height(36.dp)
                            .clickable { showApprovalGuide = true }
                            .testTag("banner_play_approval_secrets")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = AmberGold, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(s.viewSecretsBtn, style = MaterialTheme.typography.labelSmall, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }

                    // Cloud Sync Pill if user has apps
                    if (userApps.isNotEmpty() && onUploadLocalApps != null) {
                        OutlinedButton(
                            onClick = {
                                onUploadLocalApps.invoke { _, msg ->
                                    uploadStatusMsg = msg
                                }
                            },
                            enabled = !isUploadingLocalApps,
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(36.dp).testTag("btn_my_apps_upload_cloud")
                        ) {
                            if (isUploadingLocalApps) {
                                CircularProgressIndicator(modifier = Modifier.size(12.dp), color = PlayGreen, strokeWidth = 2.dp)
                            } else {
                                Icon(
                                    imageVector = Icons.Default.CloudSync,
                                    contentDescription = "رفع للسحابة",
                                    tint = PlayGreen,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("مزامنة ☁️", fontSize = 11.sp, color = PlayGreen)
                        }
                    }
                }
            }

            uploadStatusMsg?.let { msg ->
                item {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = (if (msg.contains("نجاح")) SuccessGreen else DangerRed).copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, (if (msg.contains("نجاح")) SuccessGreen else DangerRed).copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = msg,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (msg.contains("نجاح")) SuccessGreen else DangerRed,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(onClick = { uploadStatusMsg = null }, modifier = Modifier.size(20.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Close", modifier = Modifier.size(12.dp))
                            }
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

                val appArtifacts = allArtifacts.filter { it.userAppId == app.id }
                val appTesters = allInvitedTesters.filter { it.userAppId == app.id }
                val activeArtifact = appArtifacts.find { it.isActiveForTesting }

                val safetyBuffer = app.testersCount - 20
                val isBufferSafe = safetyBuffer >= 3
                val isBufferCritical = safetyBuffer in 0..2
                val isBufferDanger = safetyBuffer < 0
                val bufferBgColor = if (isBufferSafe) SuccessGreen else if (isBufferCritical) AmberGold else DangerRed

                val appLogs = allDailyTestLogs.filter { it.packageName == app.packageName }
                val todayLogsCount = appLogs.count { it.testDate == todayStr }
                val appActivities = allTesterActivities.filter { it.packageName == app.packageName }

                LaunchedEffect(app.packageName, app.iconUrl) {
                    if (app.iconUrl.isBlank() && app.packageName.isNotBlank()) {
                        onSyncPlayIcon?.invoke(app.packageName, app.id)
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("user_app_card_${app.id}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(
                        1.dp,
                        if (isReadyForProduction) SuccessGreen.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        // 1. Header: Status badge, Buffer pill, Sync Play icon button, Delete button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isReadyForProduction) SuccessGreen.copy(alpha = 0.15f) else CyanAccent.copy(alpha = 0.15f),
                                    border = BorderStroke(1.dp, if (isReadyForProduction) SuccessGreen.copy(alpha = 0.35f) else CyanAccent.copy(alpha = 0.35f))
                                ) {
                                    Text(
                                        text = if (isReadyForProduction) "جاهز للإنتاج 🏆" else "قيد الاختبار 🚀",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isReadyForProduction) SuccessGreen else CyanAccent,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = bufferBgColor.copy(alpha = 0.15f),
                                    border = BorderStroke(1.dp, bufferBgColor.copy(alpha = 0.35f))
                                ) {
                                    Text(
                                        text = if (isBufferSafe) "+${safetyBuffer} آمن 🟢"
                                        else if (isBufferCritical) "+${safetyBuffer} حرج 🟡"
                                        else "${safetyBuffer} خطر 🔴",
                                        color = bufferBgColor,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                // Sync Google Play Icon Action
                                OutlinedButton(
                                    onClick = {
                                        onSyncPlayIcon?.invoke(app.packageName, app.id)
                                    },
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 1.dp),
                                    modifier = Modifier.height(26.dp).testTag("btn_sync_play_icon_${app.id}")
                                ) {
                                    Icon(Icons.Default.CloudSync, contentDescription = null, tint = PlayGreen, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("سحب أيقونة Play", fontSize = 9.sp, color = PlayGreen, fontWeight = FontWeight.Bold)
                                }

                                IconButton(
                                    onClick = { onDeleteApp(app.id) },
                                    modifier = Modifier
                                        .size(26.dp)
                                        .testTag("delete_user_app_${app.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "حذف",
                                        tint = DangerRed.copy(alpha = 0.6f),
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // 2. App Row: Icon + Title & Package + Version badge
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable {
                                        onSyncPlayIcon?.invoke(app.packageName, app.id)
                                    },
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
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = app.name.take(2).uppercase(),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = PlayGreen
                                        )
                                        Text(
                                            text = "Play",
                                            fontSize = 7.sp,
                                            color = PlayGreen
                                        )
                                    }
                                }
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = app.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                )
                                Text(
                                    text = app.packageName,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                )
                                activeArtifact?.let { art ->
                                    Text(
                                        text = "v${art.versionName} (${art.artifactType})",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = PlayGreen,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // 3. Compact Dual Metrics Summary
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
                            ) {
                                Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("👥 المختبرين", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("${app.testersCount}/${app.targetTesters}", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (app.testersCount >= app.targetTesters) SuccessGreen else CyanAccent)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    LinearProgressIndicator(
                                        progress = { testerProgress },
                                        modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                                        color = if (app.testersCount >= app.targetTesters) SuccessGreen else CyanAccent,
                                        trackColor = MaterialTheme.colorScheme.surface
                                    )
                                }
                            }

                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
                            ) {
                                Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("📅 الأيام", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("${app.daysElapsed}/14 يوم", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (app.daysElapsed >= 14) SuccessGreen else PlayGreen)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    LinearProgressIndicator(
                                        progress = { daysProgress },
                                        modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                                        color = if (app.daysElapsed >= 14) SuccessGreen else PlayGreen,
                                        trackColor = MaterialTheme.colorScheme.surface
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // 4. Core Action Buttons (Compact & Sleek)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = { appForVersions = app },
                                modifier = Modifier.weight(1f).height(32.dp).testTag("manage_versions_btn_${app.id}"),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Icon(Icons.Default.Layers, contentDescription = null, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("الحزم (${appArtifacts.size})", fontSize = 10.5.sp, maxLines = 1)
                            }

                            OutlinedButton(
                                onClick = { appForTesters = app },
                                modifier = Modifier.weight(1f).height(32.dp).testTag("manage_testers_btn_${app.id}"),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Icon(Icons.Default.GroupAdd, contentDescription = null, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("دعوة (${appTesters.size})", fontSize = 10.5.sp, maxLines = 1)
                            }

                            OutlinedButton(
                                onClick = { appForDailyLogs = app },
                                modifier = Modifier.weight(1.1f).height(32.dp).testTag("btn_view_daily_logs_${app.id}"),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.FactCheck, contentDescription = null, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("التوثيق (${todayLogsCount})", fontSize = 10.5.sp, maxLines = 1)
                            }

                            Button(
                                onClick = { appToShare = app },
                                modifier = Modifier.height(32.dp).testTag("share_app_btn_${app.id}"),
                                colors = ButtonDefaults.buttonColors(containerColor = PlayGreen),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(13.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // --- 2. Advanced Console, Diagnostics & Review Tools (Clean Accordion) ---
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
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
                                        .padding(vertical = 4.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Build,
                                            contentDescription = null,
                                            tint = PlayGreen,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = s.devToolsSectionTitle,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Text(
                                        text = if (isToolkitExpanded) s.collapseTools else s.expandTools,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = PlayGreen,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                if (isToolkitExpanded) {
                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Diagnostics & Compliance
                                    AppHealthDiagnosticsCard(
                                        app = app,
                                        artifacts = appArtifacts,
                                        testers = appTesters,
                                        onOpenReadinessReport = { appForReadinessReport = app },
                                        onInviteTesterClick = { appForTesters = app },
                                        onManageVersionsClick = { appForVersions = app }
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Detailed Safety Buffer Card
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

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Daily Test Verification Status
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

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // App Open Tracking & Tester Activity
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

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Store Link Button
                                    OutlinedButton(
                                        onClick = {
                                            val url = if (app.androidOptInUrl.isNotBlank()) app.androidOptInUrl else app.webOptInUrl
                                            openUrl(context, url)
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("btn_store_link_${app.id}"),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Android,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(s.storeLinkBtn, fontSize = 11.sp)
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Reviews & Ratings Button for Developer
                                    val appFeedbacks = remember(allFeedback, app.packageName) {
                                        allFeedback.filter { it.packageName == app.packageName }
                                    }
                                    val avgRating = if (appFeedbacks.isNotEmpty()) appFeedbacks.map { it.rating }.average() else 5.0

                                    OutlinedButton(
                                        onClick = { appForReviews = app },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("btn_reviews_${app.id}"),
                                        shape = RoundedCornerShape(10.dp),
                                        border = BorderStroke(1.dp, AmberGold.copy(alpha = 0.6f)),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                                    ) {
                                        Icon(Icons.Default.Star, contentDescription = null, tint = AmberGold, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "تقييمات ومراجعات المختبرين ⭐ ${String.format(Locale.US, "%.1f", avgRating)} (${appFeedbacks.size} تقييم)",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AmberGold
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Row 1: 14-Day Timeline & Console Helper
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = { appForTimeline = app },
                                            modifier = Modifier.weight(1f).testTag("btn_timeline_${app.id}"),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                                        ) {
                                            Icon(Icons.Default.Timeline, contentDescription = null, tint = PlayGreen, modifier = Modifier.size(15.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(s.timeline14DaysBtn, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PlayGreen)
                                        }

                                        OutlinedButton(
                                            onClick = { appForConsoleHelper = app },
                                            modifier = Modifier.weight(1f).testTag("btn_console_helper_${app.id}"),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                                        ) {
                                            Icon(Icons.Default.RocketLaunch, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(15.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(s.consoleHelperBtn, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CyanAccent)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    // Row 2: Manage Versions & Pre-Flight Checklist
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = { appForVersions = app },
                                            modifier = Modifier.weight(1f).testTag("btn_versions_${app.id}"),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                                        ) {
                                            Icon(Icons.Default.Layers, contentDescription = null, tint = PlayGreen, modifier = Modifier.size(15.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(s.manageVersionsBtn, fontSize = 11.sp)
                                        }

                                        OutlinedButton(
                                            onClick = { appForPreFlight = app },
                                            modifier = Modifier.weight(1f).testTag("btn_preflight_${app.id}"),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                                        ) {
                                            Icon(Icons.Default.FactCheck, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(15.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(s.checklistBtn, fontSize = 11.sp)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    // Row 3: Policy Linter & Link Validator
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = { appForPolicyLinter = app },
                                            modifier = Modifier.weight(1f).testTag("btn_policy_linter_${app.id}"),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                                        ) {
                                            Icon(Icons.Default.Spellcheck, contentDescription = null, tint = AmberGold, modifier = Modifier.size(15.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(s.policyLinterBtn, fontSize = 11.sp)
                                        }

                                        OutlinedButton(
                                            onClick = { appForValidator = app },
                                            modifier = Modifier.weight(1f).testTag("btn_validate_links_${app.id}"),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                                        ) {
                                            Icon(Icons.Default.QrCode2, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(15.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(s.linkValidatorBtn, fontSize = 11.sp)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    // Row 4: Changelog, Device Diversity & Privacy Policy
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = { appForChangelog = app },
                                            modifier = Modifier.weight(1f).testTag("btn_changelog_${app.id}"),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
                                        ) {
                                            Icon(Icons.Default.Update, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(s.changelogBtn, fontSize = 10.sp)
                                        }

                                        OutlinedButton(
                                            onClick = { appForDiversity = app },
                                            modifier = Modifier.weight(1f).testTag("btn_diversity_${app.id}"),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
                                        ) {
                                            Icon(Icons.Default.Devices, contentDescription = null, tint = PlayGreen, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(s.deviceDiversityBtn, fontSize = 10.sp)
                                        }

                                        OutlinedButton(
                                            onClick = { appForPrivacyPolicy = app },
                                            modifier = Modifier.weight(1f).testTag("btn_policy_gen_${app.id}"),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
                                        ) {
                                            Icon(Icons.Default.Policy, contentDescription = null, tint = AmberGold, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(s.privacyPolicyBtn, fontSize = 10.sp)
                                        }
                                    }

                                    if (isReadyForProduction || app.daysElapsed >= 14) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Button(
                                            onClick = { appForCertificate = app },
                                            modifier = Modifier.fillMaxWidth().testTag("btn_certificate_${app.id}"),
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = AmberGold)
                                        ) {
                                            Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(s.readinessCertificateBtn, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    // Secondary Actions: Edit Group URL & Delete App
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        TextButton(
                                            onClick = { appForEditGroupUrl = app },
                                            modifier = Modifier.testTag("btn_edit_group_url_${app.id}")
                                        ) {
                                            Icon(Icons.Default.Edit, contentDescription = null, tint = PlayGreen, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(s.editGroupLink, fontSize = 11.sp, color = PlayGreen)
                                        }

                                        TextButton(
                                            onClick = { onDeleteApp(app.id) },
                                            modifier = Modifier.testTag("btn_delete_app_${app.id}")
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = null, tint = DangerRed, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(s.deleteAppBtn, fontSize = 11.sp, color = DangerRed)
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

    // 14-Day Track Progress Timeline Dialog
    appForTimeline?.let { app ->
        TrackProgressTimelineDialog(
            app = app,
            onDismiss = { appForTimeline = null },
            onOpenReadinessReport = { appForReadinessReport = it }
        )
    }

    // Console Shortcuts and Bundle/Mapping Helper Dialog
    appForConsoleHelper?.let { app ->
        ConsoleShortcutsAndBundleHelperDialog(
            app = app,
            onDismiss = { appForConsoleHelper = null }
        )
    }

    // Production Readiness Certificate Dialog
    appForCertificate?.let { app ->
        ProductionReadinessCertificateDialog(
            app = app,
            onDismiss = { appForCertificate = null }
        )
    }

    // Google Play Approval Secrets Guide Dialog
    if (showApprovalGuide) {
        PlayApprovalGuideDialog(
            onDismiss = { showApprovalGuide = false }
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

    // App Reviews & Ratings Dialog for Developer
    appForReviews?.let { app ->
        AppReviewsDialog(
            appName = app.name,
            packageName = app.packageName,
            feedbacks = allFeedback,
            isDeveloperOrAdmin = true,
            onWriteReviewClick = {
                // Developers can view reviews & reply
            },
            onReplyToReview = { feedbackId, replyText ->
                onReplyToReview(feedbackId, replyText)
            },
            onDeleteReview = { feedbackId ->
                onDeleteFeedback(feedbackId)
            },
            onDismiss = { appForReviews = null }
        )
    }
}
