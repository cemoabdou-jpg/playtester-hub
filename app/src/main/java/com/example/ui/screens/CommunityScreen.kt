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
import com.example.ui.components.CloudSyncDiagnosticsDialog
import com.example.ui.components.HeroBannerCard
import com.example.ui.components.ImportExternalAppDialog
import com.example.ui.components.ReciprocalPartnershipDialog
import com.example.ui.components.TesterCard
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DangerRed
import com.example.ui.theme.PlayGreen
import com.example.ui.theme.SuccessGreen

@Composable
fun CommunityScreen(
    communityApps: List<CommunityApp>,
    joinedTests: List<JoinedTest>,
    searchQuery: String,
    selectedCategory: String,
    currentUser: AuthUser? = null,
    isSyncing: Boolean = false,
    syncStatusMessage: String? = null,
    diagnosticResult: CloudDiagnosticResult? = null,
    isDiagnosingCloud: Boolean = false,
    isUploadingLocalApps: Boolean = false,
    onSearchChange: (String) -> Unit,
    onCategorySelect: (String) -> Unit,
    onJoinApp: (CommunityApp) -> Unit,
    onPublishClick: () -> Unit,
    onSyncClick: () -> Unit = {},
    onRunCloudDiagnostics: () -> Unit = {},
    onUploadLocalApps: (((Boolean, String) -> Unit) -> Unit)? = null,
    onImportApp: ((input: String, appName: String, devName: String, category: String, onComplete: (Boolean, String) -> Unit) -> Unit)? = null,
    onOpenAdmin: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val joinedAppIds = joinedTests.map { it.communityAppId }.toSet()
    val categories = listOf("الكل", "أدوات", "إنتاجية", "مالية", "ألعاب", "تعليم", "نمط حياة")

    var sortBy by remember { mutableStateOf("PRIORITY") } // "PRIORITY", "URGENT", "REWARD"
    var showReciprocalDialog by remember { mutableStateOf(false) }
    var showImportAppDialog by remember { mutableStateOf(false) }
    var showCloudDiagnosticsDialog by remember { mutableStateOf(false) }

    val sortedCommunityApps: List<CommunityApp> = remember(communityApps, sortBy) {
        when (sortBy) {
            "URGENT" -> communityApps.sortedByDescending { (it.testersTarget - it.testersCurrent).coerceAtLeast(0) }
            "REWARD" -> communityApps.sortedByDescending { it.rewardPoints }
            else -> communityApps.sortedWith(
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
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Hero Banner
            item {
                HeroBannerCard(onPublishClick = onPublishClick)
            }

            // 1-on-1 Reciprocal Matching Banner
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = AmberGold.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, AmberGold.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showReciprocalDialog = true }
                        .testTag("banner_reciprocal_matching")
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = AmberGold,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Handshake, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "شراكات التبادل والالتزام المتبادل (1-on-1)",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "اختبر تطبيق مطور آخر يومياً لـ 14 يوماً مقابل اختبار تطبيقك + نقاط مضاعفة!",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Button(
                            onClick = { showReciprocalDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("فتح", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }

            // Search bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_field"),
                    placeholder = { Text("ابحث عن تطبيق أو مطور...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "بحث",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "مسح")
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            // Categories horizontal filter
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { cat ->
                        ElevatedFilterChip(
                            selected = selectedCategory == cat,
                            onClick = { onCategorySelect(cat) },
                            label = { Text(cat) },
                            modifier = Modifier.testTag("filter_chip_$cat")
                        )
                    }
                }
            }

            // Sorting Options Row
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ترتيب حسب:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (sortBy == "PRIORITY") PlayGreen.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(1.dp, if (sortBy == "PRIORITY") PlayGreen else MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier
                            .testTag("sort_chip_priority")
                            .clickable { sortBy = "PRIORITY" }
                    ) {
                        Text(
                            text = "⭐ الأولوية",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (sortBy == "PRIORITY") PlayGreen else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (sortBy == "URGENT") AmberGold.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(1.dp, if (sortBy == "URGENT") AmberGold else MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier
                            .testTag("sort_chip_urgent")
                            .clickable { sortBy = "URGENT" }
                    ) {
                        Text(
                            text = "🔥 الأكثر احتياجاً",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (sortBy == "URGENT") AmberGold else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (sortBy == "REWARD") CyanAccent.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(1.dp, if (sortBy == "REWARD") CyanAccent else MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier
                            .testTag("sort_chip_reward")
                            .clickable { sortBy = "REWARD" }
                    ) {
                        Text(
                            text = "💎 الأعلى مكافأة",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (sortBy == "REWARD") CyanAccent else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
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
                            text = "تطبيقات بحاجة لمختبرين (${communityApps.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Manual Cloud Sync Button
                            OutlinedButton(
                                onClick = onSyncClick,
                                enabled = !isSyncing,
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
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
                                    Text("مزامنة...", fontSize = 11.sp, color = PlayGreen)
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "مزامنة السحابة",
                                        tint = PlayGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("مزامنة", fontSize = 11.sp, color = PlayGreen)
                                }
                            }

                            // Cloud Diagnostics / Fix Button
                            OutlinedButton(
                                onClick = {
                                    showCloudDiagnosticsDialog = true
                                    onRunCloudDiagnostics()
                                },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier
                                    .height(34.dp)
                                    .testTag("btn_open_cloud_diagnostics")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudSync,
                                    contentDescription = "حالة السيرفر",
                                    tint = AmberGold,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("حالة السحابة", fontSize = 11.sp, color = AmberGold)
                            }

                            // Quick Import by Link / Package Button
                            OutlinedButton(
                                onClick = { showImportAppDialog = true },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier
                                    .height(34.dp)
                                    .testTag("btn_open_import_app")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddLink,
                                    contentDescription = "إضافة بالرابط",
                                    tint = CyanAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("إضافة بالرابط", fontSize = 11.sp, color = CyanAccent)
                            }

                            if (currentUser?.role == "ADMIN" && onOpenAdmin != null) {
                                Surface(
                                    onClick = onOpenAdmin,
                                    shape = RoundedCornerShape(8.dp),
                                    color = AmberGold.copy(alpha = 0.2f),
                                    border = BorderStroke(1.dp, AmberGold),
                                    modifier = Modifier.testTag("btn_open_admin_panel")
                                ) {
                                    Text(
                                        text = "👑 لوحة الأدمن",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = AmberGold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                    )
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
                                    imageVector = if (msg.contains("نجاح") || msg.contains("تحديث")) Icons.Default.CloudDone else Icons.Default.CloudSync,
                                    contentDescription = null,
                                    tint = if (msg.contains("نجاح") || msg.contains("تحديث")) SuccessGreen else if (msg.contains("تنبيه") || msg.contains("فشل")) DangerRed else PlayGreen,
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
                                if (msg.contains("تنبيه") || msg.contains("فشل") || msg.contains("صلاحيات")) {
                                    Text(
                                        text = "عرض الحل 🛠️",
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
                                imageVector = if (searchQuery.isBlank() && selectedCategory == "الكل") Icons.Default.SwapHoriz else Icons.Default.Search,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = if (searchQuery.isBlank() && selectedCategory == "الكل")
                                    "لم تظهر تطبيقات بعد من السيرفر السحابي"
                                else
                                    "لا توجد نتائج مطابقة للبحث",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (searchQuery.isBlank() && selectedCategory == "الكل")
                                    "إذا أضاف مطورون آخرون تطبيقاتهم ولم تظهر، اضغط على «فاحص السحابة» للتأكد من صلاحيات Firebase أو استخدم «إضافة بالرابط» المباشر!"
                                else
                                    "جرب تغيير كلمات البحث أو اختيار تصنيف آخر",
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
                                    Text("مزامنة السيرفر 🔄", fontSize = 12.sp)
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
                                    Text("فاحص السحابة 🌐", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                OutlinedButton(
                                    onClick = { showImportAppDialog = true },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.AddLink, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("إضافة بالرابط 🔗", fontSize = 12.sp)
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
                    onJoinClick = { onJoinApp(app) }
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
                Icon(Icons.Default.Add, contentDescription = "أضف تطبيقك")
                Spacer(modifier = Modifier.size(6.dp))
                Text("أضف تطبيقك", fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showReciprocalDialog) {
        val userAppNames = communityApps.filter { it.developerName == (currentUser?.displayName ?: "") }.map { it.name }
        ReciprocalPartnershipDialog(
            userAppsNames = userAppNames,
            onDismiss = { showReciprocalDialog = false }
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
