package com.example.ui.screens

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.widget.Toast
import com.example.util.AdMobConfig
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
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
import com.example.data.model.UserApp
import com.example.ui.components.AppIconImage
import com.example.ui.components.EditAppDetailsDialog
import com.example.ui.components.PRESET_APP_ICONS
import com.example.ui.util.L
import com.example.ui.viewmodel.TesterViewModel
import java.io.ByteArrayOutputStream

@Composable
fun MyAppsScreen(
    viewModel: TesterViewModel
) {
    val context = LocalContext.current
    val userApps by viewModel.userApps.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val notifications by viewModel.testerNotifications.collectAsState()
    var showAddAppDialog by remember { mutableStateOf(false) }
    var appToPromote by remember { mutableStateOf<UserApp?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        L.t("مشاريعي قيد الاختبار", "My Testing Projects"),
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        L.t("${userApps.size} تطبيقات مسجلة ومزامنة مع السيرفر", "${userApps.size} apps registered & synced with server"),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = { showAddAppDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(L.t("إضافة تطبيق", "Add App"), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Reciprocal Tester Alert Banner
        val latestNotif = notifications.firstOrNull()
        if (latestNotif != null) {
            item {
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.markNotificationAsRead(latestNotif.id)
                            viewModel.openTesterProfile(latestNotif.testerEmail, latestNotif.testerName)
                        },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.09f)),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.primary,
                                shape = androidx.compose.foundation.shape.CircleShape,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.NotificationsActive,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        L.t("قام ${L.translateKnown(latestNotif.testerName)} بتجربة تطبيقك", "${L.translateKnown(latestNotif.testerName)} tested your app"),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(L.t("• جديد", "• New"), fontSize = 10.sp, color = Color(0xFF00875A), fontWeight = FontWeight.Bold)
                                }
                                Text(
                                    L.t("اضغط هنا لفتح حسابه وعرض تطبيقاته والمشاركة في اختبارها 🤝", "Tap to open their profile, view their apps, and test in return 🤝"),
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForwardIos,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        if (userApps.isEmpty()) {
            item {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Apps,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            L.t("لم تسجل أي تطبيق بعد", "No apps registered yet"),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            L.t(
                                "أضف تطبيقك الآن لتتبع الـ 20 مختبراً واستخراج تقرير التدقيق الرسمي لاجتياز فحص جوجل بلاي بنجاح",
                                "Add your app now to track 20 testers and export your official audit report for Google Play review"
                            ),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { showAddAppDialog = true },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(L.t("إضافة تطبيقي الأول", "Add My First App"))
                        }
                    }
                }
            }
        } else {
            items(userApps, key = { it.id }) { app ->
                CleanUserAppCard(
                    app = app,
                    onOpenComplianceMatrix = { viewModel.openComplianceMatrix(app) },
                    onOpenAudit = { viewModel.openAuditReport(app) },
                    onOpenCrashes = { viewModel.openCrashReports(app) },
                    onOpenHealth = { viewModel.openTesterHealth(app) },
                    onOpenChangelog = { viewModel.openChangelog(app) },
                    onUpdateTesters = { testers, days ->
                        viewModel.updateAppTesters(app.packageName, testers, days)
                        Toast.makeText(context, L.t("تم تحديث بيانات المختبرين بنجاح", "Tester data updated successfully"), Toast.LENGTH_SHORT).show()
                    },
                    onPromote = {
                        appToPromote = app
                    },
                    onUnpromote = {
                        viewModel.unpromoteApp(app)
                        Toast.makeText(context, L.t("تم إلغاء تمييز التطبيق", "App spotlight removed"), Toast.LENGTH_SHORT).show()
                    },
                    onUpdateDetails = { name, cat, iconUrl, web, grp ->
                        viewModel.updateAppDetails(app.packageName, name, cat, iconUrl, web, grp)
                    },
                    onDelete = {
                        viewModel.removeUserApp(app)
                        Toast.makeText(context, L.t("تم حذف التطبيق", "App deleted"), Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }

    if (showAddAppDialog) {
        AddAppSimpleDialog(
            onDismiss = { showAddAppDialog = false },
            onAdd = { name, pkg, optIn, grp, cat, desc, iconUrl, testersCount, daysCount, shouldPromote ->
                viewModel.addUserApp(name, pkg, optIn, grp, cat, desc, iconUrl, testersCount, daysCount)
                if (shouldPromote) {
                    viewModel.promoteAppByPackage(pkg, true)
                    Toast.makeText(context, L.t("🌟 تمت إضافة $name وترقيته إلى صدارة المجتمع!", "🌟 $name added and promoted to Community Spotlight!"), Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, L.t("تمت إضافة $name بنجاح!", "$name added successfully!"), Toast.LENGTH_SHORT).show()
                }
                showAddAppDialog = false
            }
        )
    }

    if (appToPromote != null) {
        val targetApp = appToPromote!!
        PromoteAppFeaturedDialog(
            app = targetApp,
            currentCoins = currentUser.points,
            onDismiss = { appToPromote = null },
            onWatchAdToPromote = {
                val activity = context as? Activity
                if (activity != null) {
                    AdMobConfig.showRewardedAd(
                        activity = activity,
                        onRewardEarned = {
                            viewModel.promoteAppWithAd(targetApp)
                            Toast.makeText(
                                context,
                                L.t("🌟 تهانينا! تمت ترقية تطبيق ${targetApp.name} إلى صدارة المجتمع بنجاح!", "🌟 Congrats! ${targetApp.name} promoted to Community Spotlight!"),
                                Toast.LENGTH_LONG
                            ).show()
                            appToPromote = null
                        }
                    )
                } else {
                    viewModel.promoteAppWithAd(targetApp)
                    Toast.makeText(context, L.t("🌟 تمت ترقية التطبيق بنجاح!", "🌟 App promoted successfully!"), Toast.LENGTH_SHORT).show()
                    appToPromote = null
                }
            },
            onPayCoinsToPromote = {
                val success = viewModel.promoteApp(targetApp, cost = 350)
                if (success) {
                    Toast.makeText(
                        context,
                        L.t("🌟 تهانينا! تم تمييز تطبيق ${targetApp.name} وخصم 350 عملة!", "🌟 Congrats! ${targetApp.name} featured (350 coins spent)!"),
                        Toast.LENGTH_LONG
                    ).show()
                    appToPromote = null
                }
            },
            onWatchAdForCoins = {
                val activity = context as? Activity
                if (activity != null) {
                    AdMobConfig.showRewardedAd(
                        activity = activity,
                        onRewardEarned = { coins ->
                            viewModel.earnAdReward(coins)
                        }
                    )
                } else {
                    viewModel.earnAdReward(50)
                }
            }
        )
    }
}

@Composable
private fun CleanUserAppCard(
    app: UserApp,
    onOpenComplianceMatrix: () -> Unit = {},
    onOpenAudit: () -> Unit,
    onOpenCrashes: () -> Unit,
    onOpenHealth: () -> Unit,
    onOpenChangelog: () -> Unit,
    onUpdateTesters: (Int, Int) -> Unit,
    onPromote: () -> Unit,
    onUnpromote: () -> Unit,
    onUpdateDetails: (name: String, category: String, iconUrl: String, webOptInUrl: String, googleGroupUrl: String) -> Unit = { _, _, _, _, _ -> },
    onDelete: () -> Unit
) {
    var showConfirmDelete by remember { mutableStateOf(false) }
    var showEditProgressDialog by remember { mutableStateOf(false) }
    var showEditDetailsDialog by remember { mutableStateOf(false) }

    val isCompleted = app.currentTesters >= 20 && app.completedDays >= 14
    val compliancePercent = ((app.currentTesters.toFloat() / app.targetTesters.coerceAtLeast(1)) * 100).toInt().coerceIn(0, 100)
    val testersColor = if (app.currentTesters >= 20) Color(0xFF00875A) else Color(0xFFE65100)
    val daysColor = if (app.completedDays >= 14) Color(0xFF00875A) else Color(0xFF1E88E5)

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // App Header Row
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
                        modifier = Modifier.size(46.dp),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            app.name,
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

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = if (isCompleted) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            if (isCompleted) L.t("مستوفٍ للشروط ✓", "Compliant ✓") else L.t("متبقي ${maxOf(0, 20 - app.currentTesters)} مختبرين", "${maxOf(0, 20 - app.currentTesters)} testers left"),
                            color = if (isCompleted) Color(0xFF00796B) else Color(0xFFE65100),
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }

                    IconButton(
                        onClick = { showEditDetailsDialog = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = L.t("تعديل صورة وبيانات التطبيق", "Edit App Icon & Details"),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = { showConfirmDelete = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.DeleteOutline,
                            contentDescription = L.t("حذف التطبيق", "Delete App"),
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Real Metrics Box
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        CompactMetricItem("${app.currentTesters}/${app.targetTesters}", L.t("مختبر معتمد", "Verified Testers"), testersColor)
                        CompactMetricItem("${app.completedDays}/${app.targetDays}", L.t("يوماً مكتملة", "Days Completed"), daysColor)
                        CompactMetricItem("$compliancePercent%", L.t("نسبة الامتثال", "Compliance"), if (isCompleted) Color(0xFF00875A) else Color(0xFFE65100))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { (app.currentTesters.toFloat() / app.targetTesters.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth().height(5.dp).clip(RoundedCornerShape(3.dp)),
                        color = if (isCompleted) Color(0xFF00875A) else Color(0xFFFFA000),
                        trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Real-time Testers Adjuster Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            L.t("الرقم الحقيقي من لوحة المطور:", "Real Play Console count:"),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            FilledTonalIconButton(
                                onClick = {
                                    val newCount = maxOf(0, app.currentTesters - 1)
                                    onUpdateTesters(newCount, app.completedDays)
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = L.t("إنقاص مختبر", "Decrease tester"), modifier = Modifier.size(14.dp))
                            }
                            Text(
                                "${app.currentTesters}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = testersColor,
                                modifier = Modifier.padding(horizontal = 6.dp)
                            )
                            FilledTonalIconButton(
                                onClick = {
                                    val newCount = minOf(50, app.currentTesters + 1)
                                    onUpdateTesters(newCount, app.completedDays)
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = L.t("زيادة مختبر", "Increase tester"), modifier = Modifier.size(14.dp))
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            OutlinedButton(
                                onClick = { showEditProgressDialog = true },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(L.t("تعديل", "Edit"), fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Featured Promotion Section
            if (app.isVip) {
                Surface(
                    color = Color(0xFFFFD54F).copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFB300)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 7.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFF8F00), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(L.t("تطبيق مميز في صدارة المجتمع ⭐", "Featured in Community Spotlight ⭐"), fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFFE65100))
                        }
                        TextButton(
                            onClick = onUnpromote,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                            modifier = Modifier.height(24.dp)
                        ) {
                            Text(L.t("إلغاء التمييز", "Remove"), fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                        }
                    }
                }
            } else {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onPromote,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8F00)),
                    contentPadding = PaddingValues(vertical = 9.dp)
                ) {
                    Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(L.t("ترقية لتطبيق مميز في الصدارة ⭐ (350 عملة أو بإعلان)", "Promote to Spotlight ⭐ (350 coins or Ad)"), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                }
            }

            // 🌟 14-Day Google Play Compliance & Production Readiness Radar Button
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onOpenComplianceMatrix,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                contentPadding = PaddingValues(vertical = 10.dp)
            ) {
                Icon(Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    L.t("مصفوفة الـ 14 يوماً ورادار جاهزية جوجل بلاي 🛡️", "14-Day Matrix & Google Play Readiness Radar 🛡️"),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Primary Audit Report Button
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onOpenAudit,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00875A)),
                contentPadding = PaddingValues(vertical = 10.dp)
            ) {
                Icon(Icons.Default.Assessment, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    L.t("استخراج تقرير التدقيق الرسمي المطبوع والجدولي", "Export Official Audit Report (PDF / CSV)"),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Secondary Quick Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = onOpenCrashes,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(vertical = 6.dp)
                ) {
                    Icon(Icons.Default.BugReport, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFFE53935))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(L.t("الأعطال", "Crashes"), fontSize = 11.sp)
                }

                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = onOpenHealth,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(vertical = 6.dp)
                ) {
                    Icon(Icons.Default.People, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFF00875A))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(L.t("المختبرون", "Testers"), fontSize = 11.sp)
                }

                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = onOpenChangelog,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(vertical = 6.dp)
                ) {
                    Icon(Icons.Default.EditNote, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(L.t("التغييرات", "Changelog"), fontSize = 11.sp)
                }
            }
        }
    }

    if (showEditProgressDialog) {
        var editTesters by remember { mutableStateOf(app.currentTesters.toString()) }
        var editDays by remember { mutableStateOf(app.completedDays.toString()) }

        AlertDialog(
            onDismissRequest = { showEditProgressDialog = false },
            title = { Text(L.t("تحديث إحصائيات لوحة مطوري جوجل بلاي", "Update Google Play Console Stats"), fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        L.t(
                            "أدخل الرقم الحقيقي للمختبرين وعدد الأيام المسجلة في حسابك بلوحة مطوري جوجل بلاي لضمان دقة التقرير.",
                            "Enter the real tester count and days logged in your Google Play Console for an accurate report."
                        ),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = editTesters,
                        onValueChange = { editTesters = it.filter { ch -> ch.isDigit() } },
                        label = { Text(L.t("عدد المختبرين الفعليين (0 - 50)", "Actual Testers (0 - 50)")) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editDays,
                        onValueChange = { editDays = it.filter { ch -> ch.isDigit() } },
                        label = { Text(L.t("عدد الأيام المكتملة (1 - 14)", "Completed Days (1 - 14)")) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val count = editTesters.toIntOrNull()?.coerceIn(0, 50) ?: 0
                        val days = editDays.toIntOrNull()?.coerceIn(1, 14) ?: 1
                        onUpdateTesters(count, days)
                        showEditProgressDialog = false
                    }
                ) {
                    Text(L.t("حفظ وتحديث", "Save & Update"))
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showEditProgressDialog = false }) {
                    Text(L.t("إلغاء", "Cancel"))
                }
            }
        )
    }

    if (showConfirmDelete) {
        AlertDialog(
            onDismissRequest = { showConfirmDelete = false },
            title = { Text(L.t("حذف التطبيق", "Delete App")) },
            text = { Text(L.t("هل أنت متأكد من حذف ${app.name} من مشاريعك؟", "Are you sure you want to delete ${app.name} from your projects?")) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmDelete = false
                        onDelete()
                    }
                ) {
                    Text(L.t("حذف", "Delete"), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDelete = false }) {
                    Text(L.t("إلغاء", "Cancel"))
                }
            }
        )
    }

    if (showEditDetailsDialog) {
        EditAppDetailsDialog(
            app = app,
            onDismiss = { showEditDetailsDialog = false },
            onSave = { name, cat, iconUrl, web, grp ->
                onUpdateDetails(name, cat, iconUrl, web, grp)
                showEditDetailsDialog = false
            }
        )
    }
}

@Composable
private fun CompactMetricItem(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = color)
        Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun PromoteAppFeaturedDialog(
    app: UserApp,
    currentCoins: Int,
    onDismiss: () -> Unit,
    onWatchAdToPromote: () -> Unit,
    onPayCoinsToPromote: () -> Unit,
    onWatchAdForCoins: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFB300),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(L.t("ترقية لتطبيق مميز في الصدارة ⭐", "Promote to Spotlight ⭐"), fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // App Preview Header
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppIconImage(
                            iconUrl = app.iconUrl,
                            appName = app.name,
                            category = app.appCategory,
                            modifier = Modifier.size(46.dp),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(app.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(
                                L.t("سيتم تثبيت التطبيق في الواجهة العلوية لصفحة المجتمع لجذب 20 مختبراً", "App will be pinned at the top of the Community tab to attract 20 testers"),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Spotlight Benefits
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFF8E1), RoundedCornerShape(10.dp))
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(L.t("✨ مميزات التطبيق المميز:", "✨ Spotlight Benefits:"), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFFB78103))
                    Text(L.t("• تثبيت في شريط الصدارة الدائري في مقدمة المجتمع", "• Pinned in the top Community Spotlight carousel"), fontSize = 11.sp, color = Color(0xFF5D4037))
                    Text(L.t("• إشارة نجمة ذهبية تلفت انتباه جميع المطورين والمختبرين", "• Golden star badge catching every developer's eye"), fontSize = 11.sp, color = Color(0xFF5D4037))
                    Text(L.t("• جمع الـ 20 مختبراً بأضعاف السرعة لاجتياز شرط الـ 14 يوماً", "• Gather 20 testers much faster to pass the 14-day rule"), fontSize = 11.sp, color = Color(0xFF5D4037))
                }

                Spacer(modifier = Modifier.height(2.dp))
                Text(L.t("اختر طريقة الترقية:", "Choose Promotion Method:"), fontWeight = FontWeight.Bold, fontSize = 12.sp)

                // Option 1: Rewarded Ad
                Button(
                    onClick = onWatchAdToPromote,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00875A)),
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 10.dp)
                ) {
                    Icon(Icons.Default.PlayCircle, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(L.t("مشاهدة إعلان وترقية مجاناً 🎬", "Watch Ad & Promote Free 🎬"), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(L.t("مشاهدة فيديو سريع لترقية تطبيقك فوراً في الصدارة", "Watch a quick video to feature your app immediately"), fontSize = 10.sp, color = Color.White.copy(alpha = 0.9f))
                    }
                }

                // Option 2: Pay Coins
                if (currentCoins >= 350) {
                    OutlinedButton(
                        onClick = onPayCoinsToPromote,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = Color(0xFFFF8F00), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(L.t("ترقية مقابل 350 عملة (رصيدك: $currentCoins 🪙)", "Promote for 350 Coins (Balance: $currentCoins 🪙)"), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                } else {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(L.t("رصيدك الحالي: $currentCoins عملة (تكلفة الترقية: 350)", "Current Balance: $currentCoins coins (Cost: 350)"), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF8F00))
                                Text(L.t("ينقصك ${350 - currentCoins} عملة • شاهد إعلانات لجمعها سريعاً", "Need ${350 - currentCoins} more coins • Watch ads to earn fast"), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Button(
                                onClick = onWatchAdForCoins,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00875A)),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.PlayCircle, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(L.t("+50 عملة 🎬", "+50 Coins 🎬"), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(L.t("إلغاء", "Cancel"))
            }
        }
    )
}

@Composable
private fun AddAppSimpleDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, String, String, String, String, String, Int, Int, Boolean) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var pkg by remember { mutableStateOf("") }
    var iconUrl by remember { mutableStateOf("preset:tools") }
    var optIn by remember { mutableStateOf("") }
    var grp by remember { mutableStateOf("") }
    var cat by remember { mutableStateOf(L.t("أدوات", "Tools")) }
    var desc by remember { mutableStateOf("") }
    var initialTesters by remember { mutableStateOf("0") }
    var initialDays by remember { mutableStateOf("1") }
    var promoteWithAd by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val originalBitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                if (originalBitmap != null) {
                    val scaled = Bitmap.createScaledBitmap(originalBitmap, 140, 140, true)

                    try {
                        val iconsDir = java.io.File(context.filesDir, "app_icons").apply { mkdirs() }
                        val safePkg = if (pkg.isNotBlank()) pkg.replace(".", "_") else "custom_${System.currentTimeMillis()}"
                        val iconFile = java.io.File(iconsDir, "$safePkg.png")
                        java.io.FileOutputStream(iconFile).use { fos ->
                            scaled.compress(Bitmap.CompressFormat.PNG, 90, fos)
                        }
                    } catch (_: Exception) {}

                    val outputStream = ByteArrayOutputStream()
                    scaled.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
                    val bytes = outputStream.toByteArray()
                    iconUrl = "data:image/jpeg;base64," + Base64.encodeToString(bytes, Base64.NO_WRAP)
                }
            } catch (_: Exception) {
                iconUrl = uri.toString()
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(L.t("إضافة تطبيق جديد للاختبار", "Add New App for Testing"), fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    L.t("أدخل تفاصيل تطبيقك لمزامنته مع السيرفر وبدء جمع الـ 20 مختبراً", "Enter your app details to sync with server and gather 20 testers"),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Live App Icon Preview + Name Field
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppIconImage(
                        iconUrl = iconUrl,
                        appName = if (name.isNotBlank()) name else L.t("تطبيقي", "My App"),
                        category = cat,
                        modifier = Modifier.size(54.dp),
                        shape = RoundedCornerShape(14.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it; errorText = null },
                        label = { Text(L.t("اسم التطبيق", "App Name")) },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Store Icon Presets Carousel
                Text(L.t("اختر أيقونة متجر جاهزة ثلاثية الأبعاد:", "Choose a 3D Store Preset Icon:"), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(vertical = 2.dp)
                ) {
                    items(PRESET_APP_ICONS) { presetItem ->
                        val isSelected = iconUrl == presetItem.id
                        Surface(
                            onClick = {
                                iconUrl = presetItem.id
                                cat = presetItem.getTitle()
                            },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(
                                            Brush.linearGradient(
                                                colors = listOf(presetItem.gradient[0], presetItem.gradient[1])
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        presetItem.icon,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    presetItem.getTitle(),
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                // Photo Selection Row: Gallery Picker + Clear Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(L.t("أو ارفع صورتك من المعرض", "Or upload from Gallery"), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }

                    if (iconUrl.startsWith("data:") || iconUrl.startsWith("http")) {
                        IconButton(
                            onClick = { iconUrl = "preset:tools" },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = L.t("استعادة الأيقونة الافتراضية", "Restore default icon"), tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }

                // Smart Package Name / Play Store URL input
                OutlinedTextField(
                    value = pkg,
                    onValueChange = { input ->
                        val extracted = com.example.util.AppInstallVerifier.extractPackageName(input)
                        pkg = extracted
                        errorText = null
                        if (extracted.isNotBlank() && optIn.isBlank()) {
                            optIn = "https://play.google.com/apps/testing/$extracted"
                        }
                    },
                    label = { Text(L.t("اسم الحزمة أو رابط متجر جوجل بلاي", "Package Name or Play Store Link")) },
                    placeholder = { Text(L.t("مثال: com.company.app أو الصق رابط المتجر", "e.g., com.company.app or paste Store link")) },
                    trailingIcon = {
                        if (pkg.isNotBlank()) {
                            val isValid = com.example.util.AppInstallVerifier.isValidPackageName(pkg)
                            val isInstalled = com.example.util.AppInstallVerifier.isAppInstalled(context, pkg)
                            Icon(
                                if (isValid) Icons.Default.CheckCircle else Icons.Default.Info,
                                contentDescription = null,
                                tint = if (isInstalled) Color(0xFF00875A) else if (isValid) Color(0xFF0288D1) else Color(0xFFF57C00),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Smart Detection & Installation Status Hint
                if (pkg.isNotBlank()) {
                    val isValid = com.example.util.AppInstallVerifier.isValidPackageName(pkg)
                    val isInstalled = com.example.util.AppInstallVerifier.isAppInstalled(context, pkg)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isInstalled) L.t("✓ مثبت على جهازك الحالي", "✓ Installed on your device") else if (isValid) L.t("معرف الحزمة صالح ✓", "Valid package name ✓") else L.t("يرجى التأكد من صيغة الحزمة", "Check package format"),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isInstalled) Color(0xFF00875A) else if (isValid) Color(0xFF0288D1) else MaterialTheme.colorScheme.error
                        )
                        if (optIn.isNotBlank()) {
                            Text(
                                L.t("تم توليد رابط الانضمام 🚀", "Opt-in link generated 🚀"),
                                fontSize = 10.sp,
                                color = Color(0xFF00875A),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = optIn,
                    onValueChange = { input ->
                        optIn = input
                        if (pkg.isBlank()) {
                            val extracted = com.example.util.AppInstallVerifier.extractPackageName(input)
                            if (com.example.util.AppInstallVerifier.isValidPackageName(extracted)) {
                                pkg = extracted
                            }
                        }
                    },
                    label = { Text(L.t("رابط الانضمام في جوجل بلاي", "Google Play Opt-in Link")) },
                    placeholder = { Text("https://play.google.com/apps/testing/...") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = grp,
                    onValueChange = { grp = it },
                    label = { Text(L.t("رابط مجموعة جوجل للمختبرين", "Testers Google Group Link")) },
                    placeholder = { Text("https://groups.google.com/g/...") },
                    supportingText = {
                        Text(
                            L.t(
                                "ضروري ليتمكن المختبرون من الانضمام بحساباتهم وتحميل التطبيق بدون أخطاء متجر بلاي",
                                "Required so testers can join with their accounts and install without Play Store errors"
                            ),
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = initialTesters,
                        onValueChange = { initialTesters = it.filter { ch -> ch.isDigit() } },
                        label = { Text(L.t("المختبرون الفعليون", "Actual Testers")) },
                        placeholder = { Text("0") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = initialDays,
                        onValueChange = { initialDays = it.filter { ch -> ch.isDigit() } },
                        label = { Text(L.t("اليوم الحالي (1-14)", "Current Day (1-14)")) },
                        placeholder = { Text("1") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Promote Instantly Checkbox with Rewarded Video Ad
                Surface(
                    color = if (promoteWithAd) Color(0xFFFFF8E1) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    shape = RoundedCornerShape(12.dp),
                    border = if (promoteWithAd) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFB300)) else null,
                    modifier = Modifier.fillMaxWidth().clickable { promoteWithAd = !promoteWithAd }
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = promoteWithAd,
                            onCheckedChange = { promoteWithAd = it },
                            colors = CheckboxDefaults.colors(checkedColor = Color(0xFFFF8F00))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(L.t("ترقية مميزة فورية في صدارة المجتمع ⭐", "Instant Community Spotlight Promotion ⭐"), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = if (promoteWithAd) Color(0xFFE65100) else MaterialTheme.colorScheme.onSurface)
                            Text(L.t("مشاهدة إعلان فيديو سريع لدعم المنصة وتثبيت تطبيقك في الصدارة فور النشر 🎬", "Watch a quick ad to support the hub and pin your app upon publish 🎬"), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                if (errorText != null) {
                    Text(errorText!!, color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank() || pkg.isBlank()) {
                        errorText = L.t("يرجى كتابة اسم التطبيق واسم الحزمة", "Please enter app name and package name")
                        return@Button
                    }
                    val formattedOptIn = if (optIn.isNotBlank()) optIn.trim() else "https://play.google.com/apps/testing/${pkg.trim()}"
                    val realCount = initialTesters.toIntOrNull()?.coerceIn(0, 50) ?: 0
                    val realDays = initialDays.toIntOrNull()?.coerceIn(1, 14) ?: 1
                    val resolvedIcon = if (iconUrl.isNotBlank()) iconUrl.trim() else "preset:tools"

                    if (promoteWithAd) {
                        val activity = context as? Activity
                        if (activity != null) {
                            AdMobConfig.showRewardedAd(
                                activity = activity,
                                onRewardEarned = {
                                    onAdd(name.trim(), pkg.trim(), formattedOptIn, grp.trim(), cat, desc.trim(), resolvedIcon, realCount, realDays, true)
                                }
                            )
                        } else {
                            onAdd(name.trim(), pkg.trim(), formattedOptIn, grp.trim(), cat, desc.trim(), resolvedIcon, realCount, realDays, true)
                        }
                    } else {
                        onAdd(name.trim(), pkg.trim(), formattedOptIn, grp.trim(), cat, desc.trim(), resolvedIcon, realCount, realDays, false)
                    }
                }
            ) {
                if (promoteWithAd) {
                    Icon(Icons.Default.PlayCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(L.t("نشر مع إعلان وترقية مميزة 🎬", "Publish with Ad & Feature 🎬"))
                } else {
                    Text(L.t("إضافة ومزامنة السيرفر", "Add & Sync to Server"))
                }
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(L.t("إلغاء", "Cancel"))
            }
        }
    )
}
