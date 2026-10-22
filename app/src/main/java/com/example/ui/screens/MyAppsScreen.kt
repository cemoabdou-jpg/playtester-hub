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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserApp
import com.example.ui.components.AppIconImage
import com.example.ui.viewmodel.TesterViewModel
import java.io.ByteArrayOutputStream

@Composable
fun MyAppsScreen(
    viewModel: TesterViewModel
) {
    val context = LocalContext.current
    val userApps by viewModel.userApps.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    var showAddAppDialog by remember { mutableStateOf(false) }
    var showInsufficientCoinsDialog by remember { mutableStateOf(false) }

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
                        "مشاريعي قيد الاختبار",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "${userApps.size} تطبيقات مسجلة ومزامنة مع السيرفر",
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
                    Text("إضافة تطبيق", fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
                            "لم تسجل أي تطبيق بعد",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "أضف تطبيقك الآن لتتبع الـ 20 مختبراً واستخراج تقرير التدقيق الرسمي لاجتياز فحص Google Play بنجاح",
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
                            Text("إضافة تطبيقي الأول")
                        }
                    }
                }
            }
        } else {
            items(userApps, key = { it.id }) { app ->
                CleanUserAppCard(
                    app = app,
                    onOpenAudit = { viewModel.openAuditReport(app) },
                    onOpenCrashes = { viewModel.openCrashReports(app) },
                    onOpenHealth = { viewModel.openTesterHealth(app) },
                    onOpenChangelog = { viewModel.openChangelog(app) },
                    onUpdateTesters = { testers, days ->
                        viewModel.updateAppTesters(app.packageName, testers, days)
                        Toast.makeText(context, "تم تحديث بيانات المختبرين بنجاح", Toast.LENGTH_SHORT).show()
                    },
                    onPromote = {
                        val success = viewModel.promoteApp(app, cost = 100)
                        if (success) {
                            Toast.makeText(
                                context,
                                "🌟 تهانينا! تم تمييز تطبيق ${app.name} ووضعه في صدارة قائمة المجتمع لجذب المختبرين بسرعة!",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            showInsufficientCoinsDialog = true
                        }
                    },
                    onUnpromote = {
                        viewModel.unpromoteApp(app)
                        Toast.makeText(context, "تم إلغاء تمييز التطبيق", Toast.LENGTH_SHORT).show()
                    },
                    onDelete = {
                        viewModel.removeUserApp(app)
                        Toast.makeText(context, "تم حذف التطبيق", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }

    if (showAddAppDialog) {
        AddAppSimpleDialog(
            onDismiss = { showAddAppDialog = false },
            onAdd = { name, pkg, optIn, grp, cat, desc, iconUrl, testersCount, daysCount ->
                viewModel.addUserApp(name, pkg, optIn, grp, cat, desc, iconUrl, testersCount, daysCount)
                showAddAppDialog = false
                Toast.makeText(context, "تمت إضافة $name بنجاح!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showInsufficientCoinsDialog) {
        AlertDialog(
            onDismissRequest = { showInsufficientCoinsDialog = false },
            icon = {
                Icon(
                    Icons.Default.MonetizationOn,
                    contentDescription = null,
                    tint = Color(0xFFFF8F00),
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text("رصيد العملات غير كافٍ", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "تحتاج إلى 100 عملة لترقية تطبيقك إلى تطبيق مميز مثبت في صدارة المجتمع لجذب 20 مختبراً بأسرع وقت.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Text(
                            "رصيدك الحالي: ${currentUser.points} عملة (ينقصك ${maxOf(0, 100 - currentUser.points)} عملة)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFFFF8F00),
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    Text("💡 كيف تكسب عملات إضافية مجاناً؟", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text("1. مشاهدة إعلان فيديو سريع (+50 عملة فوراً)", fontSize = 11.sp)
                    Text("2. فتح واختبار تطبيقات المطورين الآخرين (+25 عملة يومياً لكل تطبيق)", fontSize = 11.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showInsufficientCoinsDialog = false
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
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00875A))
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("مشاهدة إعلان مكافأة (+50 عملة)")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showInsufficientCoinsDialog = false }) {
                    Text("إغلاق")
                }
            }
        )
    }
}

@Composable
private fun CleanUserAppCard(
    app: UserApp,
    onOpenAudit: () -> Unit,
    onOpenCrashes: () -> Unit,
    onOpenHealth: () -> Unit,
    onOpenChangelog: () -> Unit,
    onUpdateTesters: (Int, Int) -> Unit,
    onPromote: () -> Unit,
    onUnpromote: () -> Unit,
    onDelete: () -> Unit
) {
    var showConfirmDelete by remember { mutableStateOf(false) }
    var showEditProgressDialog by remember { mutableStateOf(false) }

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
                            if (isCompleted) "مستوفٍ للشروط ✓" else "متبقي ${maxOf(0, 20 - app.currentTesters)} مختبرين",
                            color = if (isCompleted) Color(0xFF00796B) else Color(0xFFE65100),
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }

                    IconButton(
                        onClick = { showConfirmDelete = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.DeleteOutline,
                            contentDescription = "حذف التطبيق",
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
                        CompactMetricItem("${app.currentTesters}/${app.targetTesters}", "مختبر معتمد", testersColor)
                        CompactMetricItem("${app.completedDays}/${app.targetDays}", "يوماً مكتملة", daysColor)
                        CompactMetricItem("$compliancePercent%", "نسبة الامتثال", if (isCompleted) Color(0xFF00875A) else Color(0xFFE65100))
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
                            "الرقم الحقيقي من Play Console:",
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
                                Icon(Icons.Default.Remove, contentDescription = "إنقاص مختبر", modifier = Modifier.size(14.dp))
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
                                Icon(Icons.Default.Add, contentDescription = "زيادة مختبر", modifier = Modifier.size(14.dp))
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
                                Text("تعديل", fontSize = 11.sp)
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
                            Text("تطبيق مميز في صدارة المجتمع ⭐", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFFE65100))
                        }
                        TextButton(
                            onClick = onUnpromote,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                            modifier = Modifier.height(24.dp)
                        ) {
                            Text("إلغاء التمييز", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
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
                    Text("ترقية لتطبيق مميز في الصدارة ⭐ (100 عملة)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

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
                    "استخراج تقرير التدقيق الرسمي (PDF / CSV)",
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
                    Text("الأعطال", fontSize = 11.sp)
                }

                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = onOpenHealth,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(vertical = 6.dp)
                ) {
                    Icon(Icons.Default.People, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFF00875A))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("المختبرون", fontSize = 11.sp)
                }

                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = onOpenChangelog,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(vertical = 6.dp)
                ) {
                    Icon(Icons.Default.EditNote, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("التغييرات", fontSize = 11.sp)
                }
            }
        }
    }

    if (showEditProgressDialog) {
        var editTesters by remember { mutableStateOf(app.currentTesters.toString()) }
        var editDays by remember { mutableStateOf(app.completedDays.toString()) }

        AlertDialog(
            onDismissRequest = { showEditProgressDialog = false },
            title = { Text("تحديث إحصائيات Google Play Console", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "أدخل الرقم الحقيقي للمختبرين وعدد الأيام المسجلة في حسابك بـ Google Play Console لضمان دقة التقرير.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = editTesters,
                        onValueChange = { editTesters = it.filter { ch -> ch.isDigit() } },
                        label = { Text("عدد المختبرين الفعليين (0 - 50)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editDays,
                        onValueChange = { editDays = it.filter { ch -> ch.isDigit() } },
                        label = { Text("عدد الأيام المكتملة (1 - 14)") },
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
                    Text("حفظ وتحديث")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showEditProgressDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    if (showConfirmDelete) {
        AlertDialog(
            onDismissRequest = { showConfirmDelete = false },
            title = { Text("حذف التطبيق") },
            text = { Text("هل أنت متأكد من حذف ${app.name} من مشاريعك؟") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmDelete = false
                        onDelete()
                    }
                ) {
                    Text("حذف", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDelete = false }) {
                    Text("إلغاء")
                }
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
private fun AddAppSimpleDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, String, String, String, String, String, Int, Int) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var pkg by remember { mutableStateOf("") }
    var iconUrl by remember { mutableStateOf("") }
    var optIn by remember { mutableStateOf("") }
    var grp by remember { mutableStateOf("") }
    var cat by remember { mutableStateOf("إنتاجية") }
    var desc by remember { mutableStateOf("") }
    var initialTesters by remember { mutableStateOf("0") }
    var initialDays by remember { mutableStateOf("1") }
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
                    
                    // Save to local internal app files for permanent local access
                    try {
                        val iconsDir = java.io.File(context.filesDir, "app_icons").apply { mkdirs() }
                        val safePkg = if (pkg.isNotBlank()) pkg.replace(".", "_") else "custom_${System.currentTimeMillis()}"
                        val iconFile = java.io.File(iconsDir, "$safePkg.png")
                        java.io.FileOutputStream(iconFile).use { fos ->
                            scaled.compress(Bitmap.CompressFormat.PNG, 90, fos)
                        }
                    } catch (_: Exception) {}

                    // Efficient Base64 for Firestore cloud sync
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
        title = { Text("إضافة تطبيق جديد للاختبار", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "أدخل تفاصيل تطبيقك لمزامنته مع السيرفر وبدء جمع الـ 20 مختبراً",
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
                        appName = if (name.isNotBlank()) name else "تطبيقي",
                        category = cat,
                        modifier = Modifier.size(52.dp),
                        shape = RoundedCornerShape(14.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it; errorText = null },
                        label = { Text("اسم التطبيق") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
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
                        Text("اختيار صورة من المعرض", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }

                    if (iconUrl.isNotBlank()) {
                        IconButton(
                            onClick = { iconUrl = "" },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "مسح الصورة", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }

                OutlinedTextField(
                    value = iconUrl,
                    onValueChange = { iconUrl = it },
                    label = { Text("أو أدخل رابط صورة / أيقونة (URL)") },
                    placeholder = { Text("https://example.com/icon.png") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = pkg,
                    onValueChange = { pkg = it; errorText = null },
                    label = { Text("اسم الحزمة (Package Name)") },
                    placeholder = { Text("مثال: com.company.app") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = optIn,
                    onValueChange = { optIn = it },
                    label = { Text("رابط Google Play Opt-in") },
                    placeholder = { Text("https://play.google.com/apps/testing/...") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = grp,
                    onValueChange = { grp = it },
                    label = { Text("رابط مجموعة Google Group للمختبرين") },
                    placeholder = { Text("https://groups.google.com/g/...") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = initialTesters,
                        onValueChange = { initialTesters = it.filter { ch -> ch.isDigit() } },
                        label = { Text("المختبرون الفعليون") },
                        placeholder = { Text("0") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = initialDays,
                        onValueChange = { initialDays = it.filter { ch -> ch.isDigit() } },
                        label = { Text("اليوم الحالي (1-14)") },
                        placeholder = { Text("1") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
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
                        errorText = "يرجى كتابة اسم التطبيق واسم الحزمة"
                        return@Button
                    }
                    val formattedOptIn = if (optIn.isNotBlank()) optIn.trim() else "https://play.google.com/apps/testing/${pkg.trim()}"
                    val realCount = initialTesters.toIntOrNull()?.coerceIn(0, 50) ?: 0
                    val realDays = initialDays.toIntOrNull()?.coerceIn(1, 14) ?: 1
                    onAdd(name.trim(), pkg.trim(), formattedOptIn, grp.trim(), cat, desc.trim(), iconUrl.trim(), realCount, realDays)
                }
            ) {
                Text("إضافة ومزامنة السيرفر")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}
