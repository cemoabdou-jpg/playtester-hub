package com.example.ui.components

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.CommunityApp
import com.example.ui.viewmodel.TesterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlatformOwnerAdminDialog(
    viewModel: TesterViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()
    if (!currentUser.isPlatformOwner) {
        LaunchedEffect(Unit) {
            onDismiss()
        }
        return
    }

    val communityApps by viewModel.communityApps.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var filterFeaturedOnly by remember { mutableStateOf(false) }

    // Dialog state for editing a specific app
    var appToEdit by remember { mutableStateOf<CommunityApp?>(null) }
    var appToDelete by remember { mutableStateOf<CommunityApp?>(null) }

    val filteredApps = remember(communityApps, searchQuery, filterFeaturedOnly) {
        communityApps.filter { app ->
            val matchesFilter = if (filterFeaturedOnly) app.isFeatured else true
            val matchesSearch = if (searchQuery.isBlank()) true else {
                app.name.contains(searchQuery, ignoreCase = true) ||
                app.packageName.contains(searchQuery, ignoreCase = true) ||
                app.developerName.contains(searchQuery, ignoreCase = true)
            }
            matchesFilter && matchesSearch
        }
    }

    val totalTesters = remember(communityApps) {
        communityApps.sumOf { it.currentTesters }
    }
    val featuredCount = remember(communityApps) {
        communityApps.count { it.isFeatured }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.background,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Admin Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF1E88E5).copy(alpha = 0.15f),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Security,
                                    contentDescription = null,
                                    tint = Color(0xFF1E88E5),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "لوحة تحكم صاحب المنصة",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = Color(0xFF00875A).copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        "مسؤول معتمد",
                                        color = Color(0xFF00875A),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                "تحكم كامل وفوري في جميع التطبيقات بالسيرفر والمجتمع",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Stats Dashboard Cards Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AdminStatCard(
                        title = "إجمالي التطبيقات",
                        value = "${communityApps.size}",
                        icon = Icons.Default.Apps,
                        accentColor = Color(0xFF1E88E5),
                        modifier = Modifier.weight(1f)
                    )
                    AdminStatCard(
                        title = "تطبيقات VIP مميزة",
                        value = "$featuredCount",
                        icon = Icons.Default.Star,
                        accentColor = Color(0xFFFF8F00),
                        modifier = Modifier.weight(1f)
                    )
                    AdminStatCard(
                        title = "المختبرون الفعليون",
                        value = "$totalTesters",
                        icon = Icons.Default.Group,
                        accentColor = Color(0xFF00875A),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Search & Filter Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("بحث باسم التطبيق، اسم الحزمة، أو اسم المطور...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "مسح")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Filter Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterChip(
                        selected = !filterFeaturedOnly,
                        onClick = { filterFeaturedOnly = false },
                        label = { Text("جميع التطبيقات (${communityApps.size})") }
                    )
                    FilterChip(
                        selected = filterFeaturedOnly,
                        onClick = { filterFeaturedOnly = true },
                        leadingIcon = { Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        label = { Text("المميزة فقط ($featuredCount)") }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Apps List
                if (filteredApps.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "لا توجد تطبيقات مطابقة لبحثك في السيرفر",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredApps, key = { it.packageName }) { app ->
                            AdminAppControlCard(
                                app = app,
                                onToggleFeatured = {
                                    val newFeaturedState = !app.isFeatured
                                    viewModel.adminToggleFeatured(app.packageName, newFeaturedState)
                                    Toast.makeText(
                                        context,
                                        if (newFeaturedState) "⭐ تم ترقية ${app.name} إلى تطبيق مميز في صدارة السيرفر!" else "تم إلغاء تمييز ${app.name}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                onUpdateTesters = { newCount ->
                                    val safe = newCount.coerceIn(0, 100)
                                    viewModel.adminUpdateTesters(app.packageName, safe)
                                    Toast.makeText(context, "تم ضبط عدد مختبري ${app.name} إلى $safe", Toast.LENGTH_SHORT).show()
                                },
                                onEdit = { appToEdit = app },
                                onDelete = { appToDelete = app },
                                onOpenUrl = { url ->
                                    try {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                        context.startActivity(intent)
                                    } catch (_: Exception) {
                                        Toast.makeText(context, "الرابط: $url", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Bottom Admin Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            viewModel.adminResetAllCoins()
                            Toast.makeText(context, "✅ تم تصفير عملات جميع المستخدمين بنجاح (0 عملة)!", Toast.LENGTH_LONG).show()
                        },
                        modifier = Modifier.weight(1.1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("تصفير عملات الجميع", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(0.9f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("إغلاق", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Edit App Dialog
    if (appToEdit != null) {
        val target = appToEdit!!
        AdminEditAppDialog(
            app = target,
            onDismiss = { appToEdit = null },
            onSave = { updatedName, updatedDev, updatedCat, updatedIcon, updatedTesters, updatedTarget, isFeatured, webUrl, grpUrl ->
                viewModel.adminUpdateApp(
                    packageName = target.packageName,
                    name = updatedName,
                    devName = updatedDev,
                    category = updatedCat,
                    iconUrl = updatedIcon,
                    currentTesters = updatedTesters,
                    targetTesters = updatedTarget,
                    isFeatured = isFeatured,
                    webOptInUrl = webUrl,
                    googleGroupUrl = grpUrl
                )
                Toast.makeText(context, "🎉 تم حفظ كافة التعديلات في السيرفر بنجاح!", Toast.LENGTH_SHORT).show()
                appToEdit = null
            }
        )
    }

    // Delete Confirmation Dialog
    if (appToDelete != null) {
        val target = appToDelete!!
        AlertDialog(
            onDismissRequest = { appToDelete = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("حذف التطبيق نهائياً من المنصة", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Text("هل أنت متأكد من حذف تطبيق '${target.name}' (${target.packageName}) من السيرفر نهائياً؟ سيختفي التطبيق من قائمة جميع المطورين والمختبرين فوراً.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.adminDeleteApp(target.packageName)
                        Toast.makeText(context, "🗑️ تم حذف ${target.name} من السيرفر نهائياً", Toast.LENGTH_SHORT).show()
                        appToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("حذف نهائي", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { appToDelete = null }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@Composable
private fun AdminStatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = accentColor.copy(alpha = 0.08f),
        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontWeight = FontWeight.Black, fontSize = 16.sp, color = accentColor)
            Text(title, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
        }
    }
}

@Composable
private fun AdminAppControlCard(
    app: CommunityApp,
    onToggleFeatured: () -> Unit,
    onUpdateTesters: (Int) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onOpenUrl: (String) -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (app.isFeatured) Color(0xFFFFF8E1) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Top Row: Icon, App Details, Badges
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
                        category = app.category,
                        packageName = app.packageName,
                        modifier = Modifier.size(44.dp),
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                app.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                maxLines = 1,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (app.isFeatured) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Surface(
                                    color = Color(0xFFFF8F00).copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        "مميز ⭐",
                                        color = Color(0xFFE65100),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            app.packageName,
                            fontSize = 10.sp,
                            maxLines = 1,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "المطور: ${app.developerName} • الفئة: ${app.category}",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Delete & Edit Icon Buttons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "تعديل", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(17.dp))
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "حذف من السيرفر", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(17.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Testers Control Row & Featured Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Quick Adjust Testers
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "المختبرون: ",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    FilledTonalIconButton(
                        onClick = { onUpdateTesters(app.currentTesters - 1) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "إنقاص", modifier = Modifier.size(12.dp))
                    }
                    Text(
                        "${app.currentTesters}/${app.targetTesters}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 6.dp),
                        color = if (app.currentTesters >= app.targetTesters) Color(0xFF00875A) else MaterialTheme.colorScheme.primary
                    )
                    FilledTonalIconButton(
                        onClick = { onUpdateTesters(app.currentTesters + 1) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "زيادة", modifier = Modifier.size(12.dp))
                    }
                }

                // Featured Toggle Button
                FilledTonalButton(
                    onClick = onToggleFeatured,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.height(28.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = if (app.isFeatured) Color(0xFFFF8F00).copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Icon(
                        if (app.isFeatured) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = null,
                        tint = if (app.isFeatured) Color(0xFFFF8F00) else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        if (app.isFeatured) "إلغاء التمييز" else "تمييز كـ VIP ⭐",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (app.isFeatured) Color(0xFFE65100) else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Link Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (app.webOptInUrl.isNotBlank()) {
                    OutlinedButton(
                        onClick = { onOpenUrl(app.webOptInUrl) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("رابط Opt-in", fontSize = 10.sp)
                    }
                }
                if (app.googleGroupUrl.isNotBlank()) {
                    OutlinedButton(
                        onClick = { onOpenUrl(app.googleGroupUrl) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Groups, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("مجموعة Google Group", fontSize = 10.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminEditAppDialog(
    app: CommunityApp,
    onDismiss: () -> Unit,
    onSave: (name: String, dev: String, cat: String, icon: String, testers: Int, target: Int, featured: Boolean, web: String, grp: String) -> Unit
) {
    var name by remember { mutableStateOf(app.name) }
    var devName by remember { mutableStateOf(app.developerName) }
    var category by remember { mutableStateOf(app.category) }
    var iconUrl by remember { mutableStateOf(app.iconUrl) }
    var testers by remember { mutableStateOf(app.currentTesters.toString()) }
    var targetTesters by remember { mutableStateOf(app.targetTesters.toString()) }
    var isFeatured by remember { mutableStateOf(app.isFeatured) }
    var webOptInUrl by remember { mutableStateOf(app.webOptInUrl) }
    var googleGroupUrl by remember { mutableStateOf(app.googleGroupUrl) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("تعديل التطبيق كمسؤول", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp)
                    .verticalScroll(androidx.compose.foundation.rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppIconImage(
                        iconUrl = iconUrl,
                        appName = name,
                        category = category,
                        packageName = app.packageName,
                        modifier = Modifier.size(50.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(app.packageName, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text("التحكم الكامل ببيانات السيرفر", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("اسم التطبيق") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = devName,
                    onValueChange = { devName = it },
                    label = { Text("اسم المطور") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("التصنيف") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = iconUrl,
                    onValueChange = { iconUrl = it },
                    label = { Text("رابط الأيقونة (URL / Base64 / Preset)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = testers,
                        onValueChange = { testers = it.filter { ch -> ch.isDigit() } },
                        label = { Text("المختبرون الحاليون") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = targetTesters,
                        onValueChange = { targetTesters = it.filter { ch -> ch.isDigit() } },
                        label = { Text("الهدف (20)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isFeatured = !isFeatured }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = isFeatured, onCheckedChange = { isFeatured = it })
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("تمييز كتطبيق VIP في صدارة المجتمع ⭐", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                OutlinedTextField(
                    value = webOptInUrl,
                    onValueChange = { webOptInUrl = it },
                    label = { Text("رابط Google Play Opt-in") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = googleGroupUrl,
                    onValueChange = { googleGroupUrl = it },
                    label = { Text("رابط Google Group") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val safeTesters = testers.toIntOrNull() ?: 0
                    val safeTarget = targetTesters.toIntOrNull() ?: 20
                    onSave(name.trim(), devName.trim(), category.trim(), iconUrl.trim(), safeTesters, safeTarget, isFeatured, webOptInUrl.trim(), googleGroupUrl.trim())
                }
            ) {
                Text("حفظ في السيرفر", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}
