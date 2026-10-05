package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddLink
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.data.model.AuthUser
import com.example.data.model.CloudDiagnosticResult
import com.example.data.model.CommunityApp
import com.example.data.model.JoinedTest
import com.example.data.model.TesterFeedback
import com.example.data.model.UserApp
import com.example.ui.components.AppReviewsDialog
import com.example.ui.components.CloudSyncDiagnosticsDialog
import com.example.ui.components.FeedbackDialog
import com.example.ui.components.HeroBannerCard
import com.example.ui.components.ImportExternalAppDialog
import com.example.ui.components.ReciprocalPartnershipDialog
import com.example.ui.components.TesterCard
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DangerRed
import com.example.ui.theme.PlayGreen
import com.example.ui.theme.SuccessGreen
import com.example.ui.util.AppLanguage
import com.example.ui.util.L
import com.example.ui.util.LocalAppLanguage
import com.example.util.AppUpdateStatus

@Composable
fun CommunityScreen(
    communityApps: List<CommunityApp>,
    joinedTests: List<JoinedTest>,
    searchQuery: String,
    selectedCategory: String,
    userApps: List<UserApp> = emptyList(),
    allFeedback: List<TesterFeedback> = emptyList(),
    currentUser: AuthUser? = null,
    appUpdateStatus: AppUpdateStatus? = null,
    onUpdateClick: (() -> Unit)? = null,
    isSyncing: Boolean = false,
    syncStatusMessage: String? = null,
    diagnosticResult: CloudDiagnosticResult? = null,
    isDiagnosingCloud: Boolean = false,
    isUploadingLocalApps: Boolean = false,
    onSearchChange: (String) -> Unit,
    onCategorySelect: (String) -> Unit,
    onJoinApp: (CommunityApp) -> Unit,
    onPublishClick: () -> Unit,
    onAwardPoints: (Int, String) -> Unit = { _, _ -> },
    onSyncClick: () -> Unit = {},
    onRunCloudDiagnostics: () -> Unit = {},
    onUploadLocalApps: (((Boolean, String) -> Unit) -> Unit)? = null,
    onImportApp: ((input: String, appName: String, devName: String, category: String, onComplete: (Boolean, String) -> Unit) -> Unit)? = null,
    onOpenAdmin: (() -> Unit)? = null,
    onReplyToReview: (feedbackId: Int, replyText: String) -> Unit = { _, _ -> },
    onSubmitReview: (packageName: String, appName: String, content: String, rating: Int, type: String, devModel: String, androidVer: String, screenshotUrl: String) -> Unit = { _, _, _, _, _, _, _, _ -> },
    onRecordAppOpened: (packageName: String, appName: String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val joinedAppIds = joinedTests.map { it.communityAppId }.toSet()
    val s = L.strings()
    val language = LocalAppLanguage.current
    val isArabic = language == AppLanguage.AR

    var sortBy by remember { mutableStateOf("PRIORITY") } // "PRIORITY", "URGENT", "REWARD"
    var showReciprocalDialog by remember { mutableStateOf(false) }
    var showImportAppDialog by remember { mutableStateOf(false) }
    var showCloudDiagnosticsDialog by remember { mutableStateOf(false) }
    var appForReviews by remember { mutableStateOf<CommunityApp?>(null) }
    var appForWritingReview by remember { mutableStateOf<CommunityApp?>(null) }

    val sortedCommunityApps: List<CommunityApp> = remember(communityApps, sortBy) {
        val uniqueApps = communityApps.distinctBy { it.packageName.trim().lowercase() }
        when (sortBy) {
            "URGENT" -> uniqueApps.sortedByDescending { (it.testersTarget - it.testersCurrent).coerceAtLeast(0) }
            "REWARD" -> uniqueApps.sortedByDescending { it.rewardPoints }
            else -> uniqueApps.sortedWith(
                compareByDescending<CommunityApp> { it.priorityScore }
                    .thenByDescending { it.isFeatured }
                    .thenByDescending { it.id }
            )
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("community_apps_list"),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // --- 0. Update Notice Banner ---
            if (appUpdateStatus?.isUpdateAvailable == true) {
                item {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = AmberGold.copy(alpha = 0.15f),
                        border = BorderStroke(1.5.dp, AmberGold),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onUpdateClick?.invoke() }
                            .testTag("banner_app_update_available")
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = AmberGold,
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.SystemUpdate,
                                            contentDescription = null,
                                            tint = Color.Black,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = s.updateBannerTitle,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = s.updateBannerSubtitleFmt(appUpdateStatus.latestVersionName),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = { onUpdateClick?.invoke() },
                                colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("btn_banner_update_now")
                            ) {
                                Text(
                                    text = s.updateNowBtn,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            // --- Top Quick Action Bar (Compact & Sleek) ---
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Publish Button
                    Button(
                        onClick = onPublishClick,
                        colors = ButtonDefaults.buttonColors(containerColor = PlayGreen),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.height(34.dp).testTag("btn_quick_publish")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(s.publishNewAppBtn, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    // Reciprocal 1-on-1 Matching
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = AmberGold.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, AmberGold.copy(alpha = 0.35f)),
                        modifier = Modifier
                            .height(34.dp)
                            .clickable { showReciprocalDialog = true }
                            .testTag("chip_reciprocal_matching")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Handshake, contentDescription = null, tint = AmberGold, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(s.reciprocalTitle, style = MaterialTheme.typography.labelSmall, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }

                    // Import direct link
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = CyanAccent.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, CyanAccent.copy(alpha = 0.35f)),
                        modifier = Modifier
                            .height(34.dp)
                            .clickable { showImportAppDialog = true }
                            .testTag("chip_import_app")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.AddLink, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(s.importAppBtn, style = MaterialTheme.typography.labelSmall, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }

                    // Cloud Health / Sync
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .height(34.dp)
                            .clickable {
                                showCloudDiagnosticsDialog = true
                                onRunCloudDiagnostics()
                            }
                            .testTag("chip_cloud_diagnostics")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CloudSync, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(s.cloudSyncTitle, style = MaterialTheme.typography.labelSmall, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }

            // Search bar (Modernized Pill Style)
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_field"),
                    placeholder = { Text(s.searchPlaceholder, fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(18.dp))
                            }
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PlayGreen,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    singleLine = true
                )
            }

            // Sorting Options Row (Sleek Pills)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = s.sortLabel,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (sortBy == "PRIORITY") PlayGreen.copy(alpha = 0.16f) else MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(1.dp, if (sortBy == "PRIORITY") PlayGreen else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .testTag("sort_chip_priority")
                            .clickable { sortBy = "PRIORITY" }
                    ) {
                        Text(
                            text = s.sortPriority,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (sortBy == "PRIORITY") FontWeight.Bold else FontWeight.Normal,
                            color = if (sortBy == "PRIORITY") PlayGreen else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (sortBy == "URGENT") AmberGold.copy(alpha = 0.16f) else MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(1.dp, if (sortBy == "URGENT") AmberGold else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .testTag("sort_chip_urgent")
                            .clickable { sortBy = "URGENT" }
                    ) {
                        Text(
                            text = s.sortUrgent,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (sortBy == "URGENT") FontWeight.Bold else FontWeight.Normal,
                            color = if (sortBy == "URGENT") AmberGold else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (sortBy == "REWARD") CyanAccent.copy(alpha = 0.16f) else MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(1.dp, if (sortBy == "REWARD") CyanAccent else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .testTag("sort_chip_reward")
                            .clickable { sortBy = "REWARD" }
                    ) {
                        Text(
                            text = s.sortReward,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (sortBy == "REWARD") FontWeight.Bold else FontWeight.Normal,
                            color = if (sortBy == "REWARD") CyanAccent else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            // Section Header & Cloud Sync Controls
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${s.marketTitle} (${communityApps.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Cloud Diagnostics / Fix Button
                            IconButton(
                                onClick = {
                                    showCloudDiagnosticsDialog = true
                                    onRunCloudDiagnostics()
                                },
                                modifier = Modifier
                                    .size(34.dp)
                                    .testTag("btn_open_cloud_diagnostics")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudSync,
                                    contentDescription = if (isArabic) "حالة السحابة" else "Cloud Status",
                                    tint = AmberGold,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Manual Cloud Sync Button
                            OutlinedButton(
                                onClick = onSyncClick,
                                enabled = !isSyncing,
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier
                                    .height(34.dp)
                                    .testTag("btn_sync_community_apps")
                            ) {
                                if (isSyncing) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(14.dp),
                                        strokeWidth = 2.dp,
                                        color = PlayGreen
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (isArabic) "مزامنة..." else "Syncing...", fontSize = 11.sp, color = PlayGreen)
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = if (isArabic) "مزامنة السحابة" else "Cloud Sync",
                                        tint = PlayGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (isArabic) "تحديث" else "Refresh", fontSize = 11.sp, color = PlayGreen, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }

                    // Sync status note if available
                    syncStatusMessage?.let { msg ->
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showCloudDiagnosticsDialog = true
                                    onRunCloudDiagnostics()
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (msg.contains("نجاح") || msg.contains("Success") || msg.contains("up to date")) Icons.Default.CloudDone else Icons.Default.CloudSync,
                                    contentDescription = null,
                                    tint = if (msg.contains("نجاح") || msg.contains("Success")) SuccessGreen else if (msg.contains("تنبيه") || msg.contains("Error") || msg.contains("fail")) DangerRed else PlayGreen,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = msg,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 10.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                if (msg.contains("تنبيه") || msg.contains("فشل") || msg.contains("Error") || msg.contains("Permission")) {
                                    Text(
                                        text = if (isArabic) "عرض الحل 🛠️" else "View Fix 🛠️",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = AmberGold,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Empty state
            if (communityApps.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = if (searchQuery.isBlank()) Icons.Default.SwapHoriz else Icons.Default.Search,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = s.noAppsFound,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (searchQuery.isBlank()) {
                                    if (isArabic) {
                                        "إذا أضاف مطورون آخرون تطبيقاتهم ولم تظهر، اضغط على «فاحص السحابة» للتأكد من صلاحيات Firebase أو استخدم «إضافة بالرابط» المباشر!"
                                    } else {
                                        "If other developers added apps and they don't show, tap 'Cloud Diagnostics' to verify Firebase sync or use direct 'Import by Link'!"
                                    }
                                } else {
                                    if (isArabic) "جرب تغيير كلمات البحث للوصول لتطبيقات أخرى" else "Try changing search terms to find other applications"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = onSyncClick,
                                    colors = ButtonDefaults.buttonColors(containerColor = PlayGreen),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (isArabic) "مزامنة السيرفر 🔄" else "Sync Server 🔄", fontSize = 12.sp)
                                }
                                Button(
                                    onClick = {
                                        showCloudDiagnosticsDialog = true
                                        onRunCloudDiagnostics()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.CloudSync, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (isArabic) "فاحص السحابة 🌐" else "Cloud Inspector 🌐", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                OutlinedButton(
                                    onClick = { showImportAppDialog = true },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.AddLink, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (isArabic) "إضافة بالرابط 🔗" else "Import Link 🔗", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Community App Cards
            items(sortedCommunityApps, key = { it.id }) { app ->
                TesterCard(
                    app = app,
                    isAlreadyJoined = joinedAppIds.contains(app.id),
                    feedbacks = allFeedback,
                    onJoinClick = { onJoinApp(app) },
                    onViewReviewsClick = { appForReviews = app },
                    onRecordOpened = onRecordAppOpened
                )
            }
        }

        // Floating Action Button to add app
        FloatingActionButton(
            onClick = onPublishClick,
            containerColor = PlayGreen,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("fab_publish_app")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Add, contentDescription = if (isArabic) "أضف تطبيقك" else "Add Your App")
                Spacer(modifier = Modifier.size(6.dp))
                Text(if (isArabic) "أضف تطبيقك" else "Add App", fontWeight = FontWeight.Bold)
            }
        }
    }

    // App Reviews & Ratings Dialog
    appForReviews?.let { app ->
        val isOwner = (currentUser != null && (
            (currentUser.displayName.isNotBlank() && app.developerName.equals(currentUser.displayName, ignoreCase = true)) ||
            (currentUser.email.isNotBlank() && app.developerName.equals(currentUser.email.substringBefore("@"), ignoreCase = true)) ||
            userApps.any { it.packageName.equals(app.packageName, ignoreCase = true) }
        )) || currentUser?.role == "ADMIN"
        AppReviewsDialog(
            appName = app.name,
            packageName = app.packageName,
            feedbacks = allFeedback,
            isDeveloperOrAdmin = isOwner,
            onWriteReviewClick = {
                appForWritingReview = app
            },
            onReplyToReview = { feedbackId, replyText ->
                onReplyToReview(feedbackId, replyText)
            },
            onDismiss = { appForReviews = null }
        )
    }

    // Write App Review Dialog
    appForWritingReview?.let { app ->
        FeedbackDialog(
            appName = app.name,
            packageName = app.packageName,
            onDismiss = { appForWritingReview = null },
            onSubmitFeedback = { content, rating, type, devModel, androidVer, screenshotUrl ->
                onSubmitReview(app.packageName, app.name, content, rating, type, devModel, androidVer, screenshotUrl)
                appForWritingReview = null
            }
        )
    }

    if (showReciprocalDialog) {
        val userAppNames = userApps.map { it.name }.ifEmpty {
            communityApps.filter { it.developerName == (currentUser?.displayName ?: "") }.map { it.name }
        }
        ReciprocalPartnershipDialog(
            userAppsNames = userAppNames,
            onDismiss = { showReciprocalDialog = false },
            onBonusClaimed = { pts, reason -> onAwardPoints(pts, reason) }
        )
    }

    if (showImportAppDialog && onImportApp != null) {
        ImportExternalAppDialog(
            onImport = onImportApp,
            onDismiss = { showImportAppDialog = false }
        )
    }

    if (showCloudDiagnosticsDialog) {
        CloudSyncDiagnosticsDialog(
            diagnosticResult = diagnosticResult,
            isLoading = isDiagnosingCloud,
            isUploadingLocalApps = isUploadingLocalApps,
            onRunDiagnostic = onRunCloudDiagnostics,
            onSyncApps = {
                onSyncClick()
                showCloudDiagnosticsDialog = false
            },
            onUploadLocalApps = { cb ->
                onUploadLocalApps?.invoke(cb)
            },
            onDismiss = { showCloudDiagnosticsDialog = false }
        )
    }
}
