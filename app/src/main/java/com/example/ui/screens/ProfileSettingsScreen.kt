package com.example.ui.screens

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.model.AuthUser
import com.example.data.model.UserWallet
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DangerRed
import com.example.ui.theme.PlayGreen
import com.example.ui.theme.PlayGreenDark
import com.example.ui.theme.SuccessGreen
import com.example.util.NotificationHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProfileSettingsScreen(
    currentUser: AuthUser?,
    wallet: UserWallet,
    deviceModel: String,
    androidVersion: String,
    dailyReminderEnabled: Boolean,
    hapticEnabled: Boolean,
    isSimplifiedMode: Boolean,
    onUpdateProfile: (name: String) -> Unit,
    onChangePassword: (oldPass: String, newPass: String) -> Unit,
    onUpdatePreferences: (device: String, androidVer: String, reminder: Boolean, haptic: Boolean) -> Unit,
    onToggleSimplifiedMode: () -> Unit,
    onOpenOnboarding: () -> Unit,
    onOpenBadges: () -> Unit,
    onLogout: () -> Unit,
    onDeleteAccount: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showEditNameDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }

    var editDevice by remember(deviceModel) { mutableStateOf(deviceModel) }
    var editAndroidVer by remember(androidVersion) { mutableStateOf(androidVersion) }
    var editReminder by remember(dailyReminderEnabled) { mutableStateOf(dailyReminderEnabled) }
    var editHaptic by remember(hapticEnabled) { mutableStateOf(hapticEnabled) }

    val context = LocalContext.current
    var permissionGranted by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else true
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionGranted = isGranted
        if (isGranted) {
            Toast.makeText(context, "تم تفعيل إذن الإشعارات بنجاح!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "تم رفض الإذن، يرجى تفعيله من إعدادات النظام", Toast.LENGTH_LONG).show()
        }
    }

    var selectedReminderTime by remember { mutableStateOf("09:00 ص") }
    var notifyNewApps by remember { mutableStateOf(true) }
    var notifyNewTesters by remember { mutableStateOf(true) }
    var notifyReadinessReport by remember { mutableStateOf(true) }

    val registeredDateFormatted = remember(currentUser?.registeredAt) {
        val time = currentUser?.registeredAt ?: System.currentTimeMillis()
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale("ar"))
        sdf.format(Date(time))
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("screen_profile_settings"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- 1. Top Profile Hero Card ---
        item {
            ProfileHeroCard(
                user = currentUser,
                registeredDate = registeredDateFormatted,
                onEditNameClick = { showEditNameDialog = true }
            )
        }

        // --- 2. Stats & Rank Summary Card ---
        item {
            UserStatsOverviewCard(
                wallet = wallet,
                onOpenBadges = onOpenBadges
            )
        }

        // --- 3. Personal Account Settings ---
        item {
            SettingsSectionHeader(title = "الحساب والأمان", icon = Icons.Default.Security)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SettingsRowItem(
                        icon = Icons.Default.Person,
                        title = "الاسم الظاهر",
                        subtitle = currentUser?.displayName ?: "غير محدد",
                        trailingContent = {
                            IconButton(onClick = { showEditNameDialog = true }, modifier = Modifier.testTag("btn_edit_profile_name")) {
                                Icon(imageVector = Icons.Default.Edit, contentDescription = "تعديل الاسم", tint = PlayGreen)
                            }
                        }
                    )

                    Divider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    SettingsRowItem(
                        icon = Icons.Default.Lock,
                        title = "كلمة المرور",
                        subtitle = if (currentUser?.authProvider == "GOOGLE") "تم تسجيل الدخول عبر حساب Google" else "••••••••",
                        trailingContent = {
                            if (currentUser?.authProvider != "GOOGLE") {
                                OutlinedButton(
                                    onClick = { showChangePasswordDialog = true },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier.testTag("btn_change_password")
                                ) {
                                    Text("تغيير", style = MaterialTheme.typography.labelMedium)
                                }
                            }
                        }
                    )
                }
            }
        }

        // --- 4. Advanced Notification & Alerts Management ---
        item {
            SettingsSectionHeader(title = "إعدادات الإشعارات والتنبيهات المتقدمة", icon = Icons.Default.Notifications)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Permission Banner
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (permissionGranted) SuccessGreen.copy(alpha = 0.12f) else AmberGold.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (permissionGranted) SuccessGreen.copy(alpha = 0.4f) else AmberGold.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(
                                    imageVector = if (permissionGranted) Icons.Default.CheckCircle else Icons.Default.NotificationsActive,
                                    contentDescription = null,
                                    tint = if (permissionGranted) SuccessGreen else AmberGold,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (permissionGranted) "إذن إشعارات النظام: مفعّل وجاهز" else "إذن إشعارات النظام: يحتاج تفعيل",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (permissionGranted) SuccessGreen else AmberGold
                                    )
                                    Text(
                                        text = if (permissionGranted) "ستصلك تنبيهات الاختبار اليومي وتحديثات المختبرين." else "اضغط لمنح إذن الإشعارات لضمان عدم تفويت الستريك.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            if (!permissionGranted && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                Button(
                                    onClick = {
                                        permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = AmberGold, contentColor = Color.Black),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.testTag("btn_request_notif_perm")
                                ) {
                                    Text("تفعيل", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }

                    // Instant Test Notification Button
                    OutlinedButton(
                        onClick = {
                            val success = NotificationHelper.showSystemNotification(
                                context = context,
                                title = "🔔 إشعار تجريبي من PlayTester Hub",
                                message = "نظام الإشعارات يعمل بكفاءة تامة! ستتلقى التذكيرات اليومية وتحديثات الـ 20 مختبراً في موعدها."
                            )
                            if (success) {
                                Toast.makeText(context, "تم إرسال إشعار تجريبي للجهاز بنجاح!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "يرجى منح إذن الإشعارات أولاً لإظهار التنبيه", Toast.LENGTH_LONG).show()
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_send_test_notification")
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("إرسال إشعار تجريبي فوري للتأكد", fontWeight = FontWeight.SemiBold)
                    }

                    Divider(modifier = Modifier.padding(vertical = 2.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    // Daily Reminder Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(imageVector = Icons.Default.NotificationsActive, contentDescription = null, tint = AmberGold, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("تذكير الفحص اليومي (14-Day Streak)", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                Text("تلقي إشعار لتسجيل فتح وتجربة التطبيقات والحفاظ على الستريك", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Switch(
                            checked = editReminder,
                            onCheckedChange = {
                                editReminder = it
                                NotificationHelper.setDailyReminderEnabled(context, it)
                                onUpdatePreferences(editDevice, editAndroidVer, it, editHaptic)
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PlayGreen),
                            modifier = Modifier.testTag("switch_daily_reminder")
                        )
                    }

                    // Reminder Preferred Time Chips
                    if (editReminder) {
                        Column(modifier = Modifier.padding(start = 32.dp)) {
                            Text(
                                text = "توقيت التذكير اليومي المفضل:",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("09:00 ص", "03:00 م", "08:00 م").forEach { timeSlot ->
                                    FilterChip(
                                        selected = selectedReminderTime == timeSlot,
                                        onClick = { selectedReminderTime = timeSlot },
                                        label = { Text(timeSlot, fontSize = 12.sp) },
                                        leadingIcon = {
                                            Icon(imageVector = Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(14.dp))
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 2.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    // New Testers Joined Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(imageVector = Icons.Default.Group, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("تنبيهات انضمام المختبرين لتطبيقي", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                Text("إشعار فوري عند انضمام مطور جديد لاختبار أحد تطبيقاتك", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Switch(
                            checked = notifyNewTesters,
                            onCheckedChange = { notifyNewTesters = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PlayGreen),
                            modifier = Modifier.testTag("switch_notify_testers")
                        )
                    }

                    // New Community Apps Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(imageVector = Icons.Default.Bolt, contentDescription = null, tint = PlayGreen, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("تنبيهات التطبيقات الجديدة بالمجتمع", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                Text("إشعار عند نشر تطبيقات جديدة تحتاج لمختبرين لكسب نقاط سريعة", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Switch(
                            checked = notifyNewApps,
                            onCheckedChange = { notifyNewApps = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PlayGreen),
                            modifier = Modifier.testTag("switch_notify_apps")
                        )
                    }

                    // Readiness Report Alerts
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("تنبيهات تقرير الجاهزية واكتمال الاختبار", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                Text("إشعار عند وصول أي تطبيق لـ 20 مختبراً واكتمال 14 يوماً بنجاح", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Switch(
                            checked = notifyReadinessReport,
                            onCheckedChange = { notifyReadinessReport = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PlayGreen),
                            modifier = Modifier.testTag("switch_notify_readiness")
                        )
                    }
                }
            }
        }

        // --- 5. Testing Device & Environment Preferences ---
        item {
            SettingsSectionHeader(title = "بيئة ومواصفات جهاز الاختبار", icon = Icons.Default.PhoneAndroid)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "حدد مواصفات هاتفك لتسهيل توجيه التطبيقات المتوافقة وتأكيد تقارير الـ 14 يوماً:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = editDevice,
                        onValueChange = { editDevice = it },
                        label = { Text("طراز الجهاز (Device Model)") },
                        placeholder = { Text("مثال: Samsung S23, Xiaomi 13, Pixel 8") },
                        leadingIcon = { Icon(imageVector = Icons.Default.PhoneAndroid, contentDescription = null, tint = CyanAccent) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_device_model")
                    )

                    OutlinedTextField(
                        value = editAndroidVer,
                        onValueChange = { editAndroidVer = it },
                        label = { Text("إصدار الأندرويد (Android OS)") },
                        placeholder = { Text("مثال: Android 14 (API 34)") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Android, contentDescription = null, tint = PlayGreen) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_android_version")
                    )

                    Divider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(imageVector = Icons.Default.Vibration, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("الاستجابة اللمسية (Haptics)", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                Text("اهتزاز تفاعلي خفيف عند تأكيد الاختبار وإرسال الملاحظات", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Switch(
                            checked = editHaptic,
                            onCheckedChange = { editHaptic = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PlayGreen),
                            modifier = Modifier.testTag("switch_haptic")
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = {
                            onUpdatePreferences(editDevice, editAndroidVer, editReminder, editHaptic)
                            Toast.makeText(context, "تم حفظ بيانات بيئة الاختبار بنجاح!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PlayGreen),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_save_preferences")
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("حفظ بيانات بيئة الاختبار", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // --- 5. App Interface & System Options ---
        item {
            SettingsSectionHeader(title = "الواجهة والنظام", icon = Icons.Default.ViewAgenda)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(imageVector = Icons.Default.ViewAgenda, contentDescription = null, tint = PlayGreen, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("الوضع المبسط (Quick Hub)", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                Text("عرض بطاقة الإجراءات السريعة في أعلى الشاشة الرئيسية", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Switch(
                            checked = isSimplifiedMode,
                            onCheckedChange = { onToggleSimplifiedMode() },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PlayGreen),
                            modifier = Modifier.testTag("switch_simplified_mode")
                        )
                    }

                    Divider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    SettingsActionRow(
                        icon = Icons.Default.HelpOutline,
                        iconTint = CyanAccent,
                        title = "إعادة جولة الشرح التفاعلية",
                        subtitle = "تعرف على شروط الـ 20 مختبراً وطريقة نشر وقبول التطبيقات",
                        buttonText = "بدء الجولة",
                        onClick = onOpenOnboarding,
                        testTag = "btn_reopen_onboarding"
                    )
                }
            }
        }

        // --- 6. About & Google Play Compliance ---
        item {
            SettingsSectionHeader(title = "حول المنصة والشروط", icon = Icons.Default.Info)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("رقم الإصدار (App Version)", style = MaterialTheme.typography.bodyMedium)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "v1.3 (Build 4)",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("معيار الـ 14 يوماً لـ Google Play", style = MaterialTheme.typography.bodyMedium)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = PlayGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("متوافق 100%", style = MaterialTheme.typography.labelMedium, color = PlayGreen, fontWeight = FontWeight.Bold)
                        }
                    }

                    Text(
                        text = "المنصة تضمن تتبع واستمرارية 20 مختبراً حقيقياً عبر مجموعات Google Groups وروابط الاختبار المغلق الرسمية.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // --- 7. Danger Zone / Logout & Delete ---
        item {
            SettingsSectionHeader(title = "إجراءات الحساب", icon = Icons.Default.ExitToApp)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = onLogout,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_settings_logout")
                    ) {
                        Icon(imageVector = Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("تسجيل الخروج من الحساب", fontWeight = FontWeight.Bold)
                    }

                    TextButton(
                        onClick = { showDeleteAccountDialog = true },
                        colors = ButtonDefaults.textButtonColors(contentColor = DangerRed),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_settings_delete_account")
                    ) {
                        Icon(imageVector = Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("حذف الحساب نهائياً", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // --- Dialogs ---

    // 1. Edit Name Dialog
    if (showEditNameDialog) {
        var newName by remember { mutableStateOf(currentUser?.displayName ?: "") }
        AlertDialog(
            onDismissRequest = { showEditNameDialog = false },
            title = { Text("تعديل الاسم الشخصي", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("أدخل اسمك أو اسم فريق التطوير الذي سيظهر لباقي المختبرين:", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("الاسم") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dialog_input_new_name")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newName.isNotBlank()) {
                            onUpdateProfile(newName.trim())
                            showEditNameDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PlayGreen),
                    modifier = Modifier.testTag("dialog_btn_confirm_name")
                ) {
                    Text("حفظ التغيير", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditNameDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // 2. Change Password Dialog
    if (showChangePasswordDialog) {
        var oldPass by remember { mutableStateOf("") }
        var newPass by remember { mutableStateOf("") }
        var confirmPass by remember { mutableStateOf("") }
        var passError by remember { mutableStateOf<String?>(null) }
        var showPasswordText by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showChangePasswordDialog = false },
            title = { Text("تغيير كلمة المرور", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = oldPass,
                        onValueChange = { oldPass = it; passError = null },
                        label = { Text("كلمة المرور الحالية") },
                        visualTransformation = if (showPasswordText) VisualTransformation.None else PasswordVisualTransformation(),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("input_old_password")
                    )

                    OutlinedTextField(
                        value = newPass,
                        onValueChange = { newPass = it; passError = null },
                        label = { Text("كلمة المرور الجديدة (6 أحرف فأكثر)") },
                        visualTransformation = if (showPasswordText) VisualTransformation.None else PasswordVisualTransformation(),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("input_new_password")
                    )

                    OutlinedTextField(
                        value = confirmPass,
                        onValueChange = { confirmPass = it; passError = null },
                        label = { Text("تأكيد كلمة المرور الجديدة") },
                        visualTransformation = if (showPasswordText) VisualTransformation.None else PasswordVisualTransformation(),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("input_confirm_new_password")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { showPasswordText = !showPasswordText }) {
                            Icon(
                                imageVector = if (showPasswordText) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (showPasswordText) "إخفاء الرموز" else "إظهار الرموز", fontSize = 12.sp)
                        }
                    }

                    if (passError != null) {
                        Text(passError ?: "", color = DangerRed, style = MaterialTheme.typography.bodySmall)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPass.length < 6) {
                            passError = "كلمة المرور الجديدة يجب أن تكون 6 أحرف على الأقل"
                        } else if (newPass != confirmPass) {
                            passError = "كلمتا المرور غير متطابقتين"
                        } else {
                            onChangePassword(oldPass, newPass)
                            showChangePasswordDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PlayGreen),
                    modifier = Modifier.testTag("dialog_btn_confirm_password")
                ) {
                    Text("تحديث كلمة المرور", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showChangePasswordDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // 3. Delete Account Dialog
    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.DeleteForever, contentDescription = null, tint = DangerRed)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("تأكيد حذف الحساب", fontWeight = FontWeight.Bold, color = DangerRed)
                }
            },
            text = {
                Text(
                    text = "هل أنت متأكد من رغبتك في حذف حسابك؟ سيؤدي هذا الإجراء إلى مسح بيانات الدخول الخاصة بك والعودة لشاشة البدء.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteAccountDialog = false
                        onDeleteAccount()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed),
                    modifier = Modifier.testTag("dialog_btn_confirm_delete_account")
                ) {
                    Text("نعم، احذف الحساب", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAccountDialog = false }) {
                    Text("تراجع")
                }
            }
        )
    }
}

// --- Sub-components for Settings ---

@Composable
private fun ProfileHeroCard(
    user: AuthUser?,
    registeredDate: String,
    onEditNameClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(PlayGreen, CyanAccent)
                        )
                    )
                    .border(3.dp, MaterialTheme.colorScheme.surface, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = (user?.displayName?.take(1) ?: "U").uppercase(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = user?.displayName ?: "المطور المعتمد",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onEditNameClick, modifier = Modifier.size(28.dp)) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "تعديل", tint = PlayGreen, modifier = Modifier.size(16.dp))
                }
            }

            Text(
                text = user?.email ?: "developer@example.com",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Role Badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (user?.role == "ADMIN") AmberGold.copy(alpha = 0.2f) else PlayGreen.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (user?.role == "ADMIN") AmberGold else PlayGreen)
                ) {
                    Text(
                        text = if (user?.role == "ADMIN") "👑 صاحب المنصة (Admin)" else "⭐ مطور معتمد (Tester)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (user?.role == "ADMIN") AmberGold else PlayGreen,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                // Provider Badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = if (user?.authProvider == "GOOGLE") "Google Auth" else "Email Auth",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "عضو منذ: $registeredDate",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun UserStatsOverviewCard(
    wallet: UserWallet,
    onOpenBadges: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "سجل الإنجازات والرتب",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onOpenBadges, contentPadding = PaddingValues(0.dp)) {
                    Text("عرض الشارات 🏆", style = MaterialTheme.typography.labelMedium, color = AmberGold, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(icon = Icons.Default.MonetizationOn, tint = AmberGold, value = "${wallet.points}", label = "رصيد النقاط")
                StatItem(icon = Icons.Default.Bolt, tint = PlayGreen, value = "${wallet.currentStreak} أيام", label = "الستريك النشط")
                StatItem(icon = Icons.Default.CheckCircle, tint = CyanAccent, value = "${wallet.testsCompleted}", label = "اختبارات مكتملة")
            }
        }
    }
}

@Composable
private fun StatItem(
    icon: ImageVector,
    tint: Color,
    value: String,
    label: String
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.width(100.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = tint)
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
        }
    }
}

@Composable
private fun SettingsSectionHeader(
    title: String,
    icon: ImageVector
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = PlayGreen, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun SettingsRowItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    trailingContent: @Composable () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(imageVector = icon, contentDescription = null, tint = PlayGreen, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        trailingContent()
    }
}

@Composable
private fun SettingsActionRow(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    buttonText: String,
    onClick: () -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        OutlinedButton(
            onClick = onClick,
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            modifier = Modifier.testTag(testTag)
        ) {
            Text(buttonText, style = MaterialTheme.typography.labelMedium)
        }
    }
}
