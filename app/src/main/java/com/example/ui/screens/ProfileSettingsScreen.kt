package com.example.ui.screens

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.model.AuthUser
import com.example.data.model.UserWallet
import com.example.ui.components.JsonBackupDialog
import com.example.ui.components.ReminderAlarmsDialog
import com.example.ui.theme.AmberGold
import com.example.ui.theme.AppColorTheme
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DangerRed
import com.example.ui.theme.PlayGreen
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.ThemeMode
import com.example.ui.util.AppLanguage
import com.example.ui.util.L
import com.example.util.AppUpdateHelper
import com.example.util.EmailHelper
import com.example.util.NotificationHelper
import kotlinx.coroutines.launch
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
    themeMode: ThemeMode = ThemeMode.LIGHT,
    colorTheme: AppColorTheme = AppColorTheme.MINT_BEIGE,
    language: AppLanguage = AppLanguage.AR,
    onLanguageChange: (AppLanguage) -> Unit = {},
    onThemeModeChange: (ThemeMode) -> Unit = {},
    onColorThemeChange: (AppColorTheme) -> Unit = {},
    onUpdateProfile: (name: String) -> Unit,
    onChangePassword: (oldPass: String, newPass: String) -> Unit,
    onUpdatePreferences: (device: String, androidVer: String, reminder: Boolean, haptic: Boolean) -> Unit,
    onToggleSimplifiedMode: () -> Unit,
    onOpenOnboarding: (() -> Unit)? = null,
    onOpenBadges: () -> Unit,
    onOpenPointsCenter: (() -> Unit)? = null,
    onUpgradeGuestClick: (() -> Unit)? = null,
    onPushAllNotifications: (() -> Unit)? = null,
    onLogout: () -> Unit,
    onDeleteAccount: () -> Unit,
    onCheckForUpdates: () -> Unit = {},
    onExportBackup: suspend () -> String = { "{}" },
    onRestoreBackup: suspend (String) -> Pair<Boolean, String> = { Pair(false, "") },
    modifier: Modifier = Modifier
) {
    val s = L.strings()
    val isArabic = (language == AppLanguage.AR)
    val coroutineScope = rememberCoroutineScope()
    var showBackupDialog by remember { mutableStateOf(false) }
    var currentBackupJson by remember { mutableStateOf("") }

    var showEditNameDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }
    var showAlarmsDialog by remember { mutableStateOf(false) }
    var showPrivacyAndDataSafetyDialog by remember { mutableStateOf(false) }

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
            Toast.makeText(context, if (isArabic) "تم تفعيل إذن الإشعارات بنجاح!" else "Notifications enabled successfully!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, if (isArabic) "تم رفض الإذن، يرجى تفعيله من إعدادات النظام" else "Permission denied, please enable in system settings", Toast.LENGTH_LONG).show()
        }
    }

    var activeReminderHours by remember { mutableStateOf(NotificationHelper.getReminderHours(context)) }
    var notifyNewApps by remember { mutableStateOf(true) }
    var notifyNewTesters by remember { mutableStateOf(true) }
    var notifyReadinessReport by remember { mutableStateOf(true) }

    val registeredDateFormatted = remember(currentUser?.registeredAt, isArabic) {
        val time = currentUser?.registeredAt ?: System.currentTimeMillis()
        val locale = if (isArabic) Locale.forLanguageTag("ar") else Locale.ENGLISH
        val sdf = SimpleDateFormat("dd MMMM yyyy", locale)
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
                s = s,
                isArabic = isArabic,
                onEditNameClick = { showEditNameDialog = true }
            )
        }

        // --- 1.2. Guest Upgrade Card (When active user is Guest) ---
        if (currentUser?.role == "GUEST" || currentUser?.authProvider == "GUEST") {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, PlayGreen.copy(alpha = 0.35f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("card_guest_upgrade_banner")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = PlayGreen.copy(alpha = 0.12f),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.RocketLaunch, contentDescription = null, tint = PlayGreen, modifier = Modifier.size(18.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isArabic) "ترقية حساب الضيف وحفظ البيانات 🔒" else "Upgrade Guest & Save Data 🔒",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isArabic) "احفظ رصيدك (${wallet.points} نقطة) وتطبيقاتك وسجلاتك بشكل دائم." else "Save your ${wallet.points} points, apps & logs permanently.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { onUpgradeGuestClick?.invoke() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PlayGreen,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .testTag("btn_profile_upgrade_guest")
                        ) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isArabic) "ترقية الحساب الآن (بريد أو Google) ➔" else "Upgrade Account Now ➔",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // --- 1.5. App Language Selection Card ---
        item {
            SettingsSectionHeader(title = s.appLanguageTitle, icon = Icons.Default.Translate)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_language_selection")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = s.appLanguageDesc,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Arabic Option
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (language == AppLanguage.AR) PlayGreen.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface,
                            border = BorderStroke(if (language == AppLanguage.AR) 1.5.dp else 1.dp, if (language == AppLanguage.AR) PlayGreen else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onLanguageChange(AppLanguage.AR) }
                                .testTag("lang_select_ar")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(text = "🇸🇦", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "العربية",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = if (language == AppLanguage.AR) FontWeight.Bold else FontWeight.Medium,
                                    color = if (language == AppLanguage.AR) PlayGreen else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        // English Option
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (language == AppLanguage.EN) PlayGreen.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface,
                            border = BorderStroke(if (language == AppLanguage.EN) 1.5.dp else 1.dp, if (language == AppLanguage.EN) PlayGreen else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onLanguageChange(AppLanguage.EN) }
                                .testTag("lang_select_en")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(text = "🇬🇧", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "English",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = if (language == AppLanguage.EN) FontWeight.Bold else FontWeight.Medium,
                                    color = if (language == AppLanguage.EN) PlayGreen else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- 2. Stats & Rank Summary Card ---
        item {
            UserStatsOverviewCard(
                wallet = wallet,
                s = s,
                onOpenBadges = onOpenBadges,
                onOpenPointsCenter = onOpenPointsCenter
            )
        }

        // --- 3. Personal Account Settings ---
        item {
            SettingsSectionHeader(title = s.accountSection, icon = Icons.Default.Security)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SettingsRowItem(
                        icon = Icons.Default.Person,
                        title = s.displayNameLabel,
                        subtitle = currentUser?.displayName ?: (if (isArabic) "غير محدد" else "Not set"),
                        trailingContent = {
                            IconButton(onClick = { showEditNameDialog = true }, modifier = Modifier.testTag("btn_edit_profile_name")) {
                                Icon(imageVector = Icons.Default.Edit, contentDescription = s.editName, tint = PlayGreen)
                            }
                        }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    SettingsRowItem(
                        icon = Icons.Default.Lock,
                        title = s.passwordLabel,
                        subtitle = if (currentUser?.authProvider == "GOOGLE") s.googleSignedIn else "••••••••",
                        trailingContent = {
                            if (currentUser?.authProvider != "GOOGLE") {
                                OutlinedButton(
                                    onClick = { showChangePasswordDialog = true },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier.testTag("btn_change_password")
                                ) {
                                    Text(s.changeBtn, style = MaterialTheme.typography.labelMedium)
                                }
                            }
                        }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    SettingsRowItem(
                        icon = Icons.Default.Email,
                        title = s.verifiedEmail,
                        subtitle = currentUser?.email ?: (if (isArabic) "غير محدد" else "Not set"),
                        trailingContent = {
                            OutlinedButton(
                                onClick = {
                                    val targetEmail = currentUser?.email ?: ""
                                    EmailHelper.sendVerificationTestEmail(context, targetEmail)
                                },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("btn_test_email")
                            ) {
                                Icon(imageVector = Icons.Default.Email, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(s.sendTestEmail, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    )
                }
            }
        }

        // --- 4. Advanced Notification & Alerts Management ---
        item {
            SettingsSectionHeader(title = s.notificationsSettingsTitle, icon = Icons.Default.Notifications)
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
                        border = BorderStroke(
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
                                        text = if (permissionGranted) s.notifPermGranted else s.notifPermNeeded,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (permissionGranted) SuccessGreen else AmberGold
                                    )
                                    Text(
                                        text = if (permissionGranted) s.notifPermDescActive else s.notifPermDescInactive,
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
                                    Text(s.enableBtn, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }

                    // Notification Action Buttons (Push All + Test)
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                if (onPushAllNotifications != null) {
                                    onPushAllNotifications.invoke()
                                } else {
                                    val count = NotificationHelper.pushAllNotifications(
                                        context = context,
                                        notifications = emptyList()
                                    )
                                    Toast.makeText(
                                        context,
                                        if (isArabic) "تم تفعيل ودفع جميع التنبيهات في شريط الإشعارات بنجاح!" else "All notifications pushed and activated!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PlayGreen, contentColor = Color.White),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_push_all_notifications_profile")
                        ) {
                            Icon(imageVector = Icons.Default.NotificationsActive, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isArabic) "دفع وتفعيل كافة الإشعارات والتنبيهات 🔔" else "Push & Activate All Notifications 🔔",
                                fontWeight = FontWeight.Bold
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                val success = NotificationHelper.showSystemNotification(
                                    context = context,
                                    title = if (isArabic) "🔔 إشعار تجريبي من PlayTester Hub" else "🔔 Test Notification from PlayTester Hub",
                                    message = if (isArabic) "نظام الإشعارات يعمل بكفاءة تامة! ستتلقى التذكيرات اليومية وتحديثات الـ 20 مختبراً في موعدها." else "Notification system is active! You will receive daily test reminders on time."
                                )
                                if (success) {
                                    Toast.makeText(context, if (isArabic) "تم إرسال إشعار تجريبي للجهاز بنجاح!" else "Test notification sent successfully!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, if (isArabic) "يرجى منح إذن الإشعارات أولاً لإظهار التنبيه" else "Please grant notification permission first", Toast.LENGTH_LONG).show()
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_send_test_notification")
                        ) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(s.sendTestNotifBtn, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

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
                                Text(s.dailyStreakReminderTitle, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                Text(s.dailyStreakReminderDesc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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

                    // Reminder Preferred Time Chips (Multi-schedule alarms)
                    if (editReminder) {
                        val reminderSlots = remember(isArabic) {
                            listOf(
                                9 to (if (isArabic) "🌅 صباحاً (09:00 ص)" else "🌅 Morning (09:00 AM)"),
                                14 to (if (isArabic) "☀️ ظهراً (02:00 م)" else "☀️ Midday (02:00 PM)"),
                                20 to (if (isArabic) "🌇 مساءً (08:00 م)" else "🌇 Evening (08:00 PM)"),
                                23 to (if (isArabic) "🌙 ليلاً (11:00 م)" else "🌙 Night (11:00 PM)")
                            )
                        }

                        Column(modifier = Modifier.padding(start = 32.dp)) {
                            Text(
                                text = if (isArabic) "مواعيد التنبيهات اليومية (يمكنك تفعيل أكثر من موعد):" else "Daily Reminder Schedules (select one or multiple):",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isArabic) "ستصلك إشعارات وتنبيهات في كل موعد محدد لتذكيرك بإجراء الفحص اليومي." else "You will receive system notifications at each selected time to keep your streak.",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                reminderSlots.forEach { (hour, slotLabel) ->
                                    val isSlotActive = activeReminderHours.contains(hour)
                                    FilterChip(
                                        selected = isSlotActive,
                                        onClick = {
                                            val updated = NotificationHelper.toggleReminderHour(context, hour)
                                            activeReminderHours = updated
                                            val isNowActive = updated.contains(hour)
                                            val feedbackMsg = if (isNowActive) {
                                                if (isArabic) "تم تفعيل منبه $slotLabel ✓" else "Enabled $slotLabel ✓"
                                            } else {
                                                if (isArabic) "تم إيقاف منبه $slotLabel" else "Disabled $slotLabel"
                                            }
                                            Toast.makeText(context, feedbackMsg, Toast.LENGTH_SHORT).show()
                                        },
                                        label = {
                                            Text(
                                                text = slotLabel,
                                                fontSize = 12.sp,
                                                fontWeight = if (isSlotActive) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = if (isSlotActive) Icons.Default.NotificationsActive else Icons.Default.AccessTime,
                                                contentDescription = null,
                                                tint = if (isSlotActive) PlayGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(15.dp)
                                            )
                                        }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedButton(
                                onClick = { showAlarmsDialog = true },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(38.dp)
                                    .testTag("btn_manage_multi_alarms")
                            ) {
                                Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = AmberGold, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isArabic) "إدارة وتخصيص مواعيد المنبهات المتعددة ⏰" else "Manage Multi-Schedule Alarms ⏰",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

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
                                Text(s.newTestersAlertsTitle, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                Text(s.newTestersAlertsDesc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                                Text(s.newAppsAlertsTitle, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                Text(s.newAppsAlertsDesc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                                Text(s.readinessReportAlertsTitle, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                Text(s.readinessReportAlertsDesc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
            SettingsSectionHeader(title = s.testDeviceEnvTitle, icon = Icons.Default.PhoneAndroid)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = s.testDeviceEnvDesc,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = editDevice,
                        onValueChange = { editDevice = it },
                        label = { Text(s.deviceModelLabel) },
                        placeholder = { Text(if (isArabic) "مثال: Samsung S23, Xiaomi 13, Pixel 8" else "e.g. Pixel 8, Samsung S23") },
                        leadingIcon = { Icon(imageVector = Icons.Default.PhoneAndroid, contentDescription = null, tint = CyanAccent) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_device_model")
                    )

                    OutlinedTextField(
                        value = editAndroidVer,
                        onValueChange = { editAndroidVer = it },
                        label = { Text(s.androidVersionLabel) },
                        placeholder = { Text(if (isArabic) "مثال: Android 14 (API 34)" else "e.g. Android 14 (API 34)") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Android, contentDescription = null, tint = PlayGreen) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_android_version")
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(imageVector = Icons.Default.Vibration, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(s.hapticFeedbackTitle, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                Text(s.hapticFeedbackDesc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                            Toast.makeText(context, if (isArabic) "تم حفظ بيانات بيئة الاختبار بنجاح!" else "Testing device preferences saved successfully!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PlayGreen),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_save_preferences")
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(s.saveDeviceSpecsBtn, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // --- 5. Theme & Appearance (Dark / Light Mode) ---
        item {
            val isSystemDark = isSystemInDarkTheme()
            val effectiveDark = when (themeMode) {
                ThemeMode.DARK -> true
                ThemeMode.LIGHT -> false
                ThemeMode.SYSTEM -> isSystemDark
            }

            SettingsSectionHeader(title = s.themeModeTitle, icon = Icons.Default.BrightnessMedium)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_theme_settings")
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        text = s.themeModeDesc,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // 3 Mode selection pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ThemeOptionCard(
                            title = s.themeAuto,
                            subtitle = s.themeAutoSub,
                            icon = Icons.Default.BrightnessAuto,
                            isSelected = themeMode == ThemeMode.SYSTEM,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("chip_theme_system"),
                            onClick = { onThemeModeChange(ThemeMode.SYSTEM) }
                        )

                        ThemeOptionCard(
                            title = s.themeLight,
                            subtitle = s.themeLightSub,
                            icon = Icons.Default.LightMode,
                            isSelected = themeMode == ThemeMode.LIGHT,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("chip_theme_light"),
                            onClick = { onThemeModeChange(ThemeMode.LIGHT) }
                        )

                        ThemeOptionCard(
                            title = s.themeDark,
                            subtitle = s.themeDarkSub,
                            icon = Icons.Default.DarkMode,
                            isSelected = themeMode == ThemeMode.DARK,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("chip_theme_dark"),
                            onClick = { onThemeModeChange(ThemeMode.DARK) }
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    // Quick Switch toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(
                                imageVector = if (effectiveDark) Icons.Default.DarkMode else Icons.Default.LightMode,
                                contentDescription = null,
                                tint = if (effectiveDark) CyanAccent else AmberGold,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (effectiveDark) s.darkModeActive else s.lightModeActive,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (effectiveDark) (if (isArabic) "ألوان داكنة مريحة للعين في الأماكن المظلمة" else "Comfortable dark palette for low-light environments") else (if (isArabic) "ألوان واضحة ومتباينة في الإضاءة الخارجية" else "High-contrast bright theme for daylight use"),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Switch(
                            checked = effectiveDark,
                            onCheckedChange = { checked ->
                                onThemeModeChange(if (checked) ThemeMode.DARK else ThemeMode.LIGHT)
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PlayGreen),
                            modifier = Modifier.testTag("switch_dark_mode_quick")
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    // Color Palette Selector (Eye-Comfort & Custom Vibe)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isArabic) "درجات الألوان المريحة للعين (Eye Comfort)" else "Eye-Comfort Color Palette",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = if (isArabic) "اختر النمط اللوني الأنسب لراحتك البصرية لتقليل إجهاد العين:" else "Select your preferred color mood to eliminate visual glare and eye strain:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Palettes Grid / List
                        AppColorTheme.values().forEach { palette ->
                            val isSelected = colorTheme == palette
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surface,
                                border = BorderStroke(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onColorThemeChange(palette) }
                                    .testTag("palette_${palette.name.lowercase()}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(CircleShape)
                                                .background(palette.previewColor),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (isSelected) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = if (isArabic) palette.titleAr else palette.titleEn,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = if (isArabic) palette.descriptionAr else palette.descriptionEn,
                                                style = MaterialTheme.typography.bodySmall,
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- 6. App Interface & System Options ---
        item {
            SettingsSectionHeader(title = s.interfaceAndSystem, icon = Icons.Default.ViewAgenda)
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
                                Text(s.simplifiedModeHubTitle, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                Text(s.simplifiedModeHubDesc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Switch(
                            checked = isSimplifiedMode,
                            onCheckedChange = { onToggleSimplifiedMode() },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PlayGreen),
                            modifier = Modifier.testTag("switch_simplified_mode")
                        )
                    }

                    if (onOpenOnboarding != null) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                        SettingsActionRow(
                            icon = Icons.AutoMirrored.Filled.HelpOutline,
                            iconTint = CyanAccent,
                            title = s.replayOnboardingTitle,
                            subtitle = s.replayOnboardingDesc,
                            buttonText = s.startTourBtn,
                            onClick = onOpenOnboarding,
                            testTag = "btn_reopen_onboarding"
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    SettingsActionRow(
                        icon = Icons.Default.Backup,
                        iconTint = PlayGreen,
                        title = s.backupJsonTitle,
                        subtitle = s.backupJsonDesc,
                        buttonText = s.manageBackupBtn,
                        onClick = {
                            coroutineScope.launch {
                                currentBackupJson = onExportBackup()
                                showBackupDialog = true
                            }
                        },
                        testTag = "btn_open_json_backup"
                    )
                }
            }
        }

        // --- 7. About & Google Play Compliance ---
        item {
            SettingsSectionHeader(title = s.aboutAndComplianceTitle, icon = Icons.Default.Info)
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
                        Text(s.appVersionLabel, style = MaterialTheme.typography.bodyMedium)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "v${AppUpdateHelper.getInstalledVersionName(context)} (Build ${AppUpdateHelper.getInstalledVersionCode(context)})",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Check for updates button
                    OutlinedButton(
                        onClick = onCheckForUpdates,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_check_for_updates")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = PlayGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = s.checkForUpdatesBtn,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(s.googlePlay14DaysStandard, style = MaterialTheme.typography.bodyMedium)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = PlayGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(s.compliant100, style = MaterialTheme.typography.labelMedium, color = PlayGreen, fontWeight = FontWeight.Bold)
                        }
                    }

                    Text(
                        text = s.complianceNotice,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    // Google Play Data Safety & Privacy Policy Button
                    Button(
                        onClick = { showPrivacyAndDataSafetyDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_open_privacy_policy")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isArabic) "سياسة الخصوصية وأمان البيانات (Data Safety) 🔒" else "Privacy Policy & Data Safety 🔒",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // --- 8. Danger Zone / Logout & Delete ---
        item {
            SettingsSectionHeader(title = s.accountActionsTitle, icon = Icons.AutoMirrored.Filled.ExitToApp)
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
                        Icon(imageVector = Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(s.signOutBtn, fontWeight = FontWeight.Bold)
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
                        Text(s.deleteAccountPermBtn, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // --- Dialogs ---

    // 0. JSON Backup & Restore Dialog
    if (showBackupDialog) {
        JsonBackupDialog(
            exportedJson = currentBackupJson,
            onRestoreJson = { json ->
                var result = Pair(false, "")
                coroutineScope.launch {
                    result = onRestoreBackup(json)
                }
                result
            },
            onDismiss = { showBackupDialog = false }
        )
    }

    // 1. Edit Name Dialog
    if (showEditNameDialog) {
        var newName by remember { mutableStateOf(currentUser?.displayName ?: "") }
        AlertDialog(
            onDismissRequest = { showEditNameDialog = false },
            title = { Text(if (isArabic) "تعديل الاسم الشخصي" else "Edit Display Name", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(if (isArabic) "أدخل اسمك أو اسم فريق التطوير الذي سيظهر لباقي المختبرين:" else "Enter your name or dev team name that testers will see:", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text(if (isArabic) "الاسم" else "Name") },
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
                    Text(if (isArabic) "حفظ التغيير" else "Save Changes", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditNameDialog = false }) {
                    Text(if (isArabic) "إلغاء" else "Cancel")
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
            title = { Text(if (isArabic) "تغيير كلمة المرور" else "Change Password", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = oldPass,
                        onValueChange = { oldPass = it; passError = null },
                        label = { Text(if (isArabic) "كلمة المرور الحالية" else "Current Password") },
                        visualTransformation = if (showPasswordText) VisualTransformation.None else PasswordVisualTransformation(),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("input_old_password")
                    )

                    OutlinedTextField(
                        value = newPass,
                        onValueChange = { newPass = it; passError = null },
                        label = { Text(if (isArabic) "كلمة المرور الجديدة (6 أحرف فأكثر)" else "New Password (6+ characters)") },
                        visualTransformation = if (showPasswordText) VisualTransformation.None else PasswordVisualTransformation(),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("input_new_password")
                    )

                    OutlinedTextField(
                        value = confirmPass,
                        onValueChange = { confirmPass = it; passError = null },
                        label = { Text(if (isArabic) "تأكيد كلمة المرور الجديدة" else "Confirm New Password") },
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
                            Text(if (showPasswordText) (if (isArabic) "إخفاء الرموز" else "Hide Password") else (if (isArabic) "إظهار الرموز" else "Show Password"), fontSize = 12.sp)
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
                            passError = if (isArabic) "كلمة المرور الجديدة يجب أن تكون 6 أحرف على الأقل" else "New password must be at least 6 characters"
                        } else if (newPass != confirmPass) {
                            passError = if (isArabic) "كلمتا المرور غير متطابقتين" else "Passwords do not match"
                        } else {
                            onChangePassword(oldPass, newPass)
                            showChangePasswordDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PlayGreen),
                    modifier = Modifier.testTag("dialog_btn_confirm_password")
                ) {
                    Text(if (isArabic) "تحديث كلمة المرور" else "Update Password", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showChangePasswordDialog = false }) {
                    Text(if (isArabic) "إلغاء" else "Cancel")
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
                    Text(if (isArabic) "تأكيد حذف الحساب" else "Confirm Delete Account", fontWeight = FontWeight.Bold, color = DangerRed)
                }
            },
            text = {
                Text(
                    text = if (isArabic) "هل أنت متأكد من رغبتك في حذف حسابك؟ سيؤدي هذا الإجراء إلى مسح بيانات الدخول الخاصة بك والعودة لشاشة البدء." else "Are you sure you want to permanently delete your account? This will wipe your login credentials and return to welcome screen.",
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
                    Text(if (isArabic) "نعم، احذف الحساب" else "Yes, Delete Account", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAccountDialog = false }) {
                    Text(if (isArabic) "تراجع" else "Cancel")
                }
            }
        )
    }

    if (showAlarmsDialog) {
        ReminderAlarmsDialog(
            onDismiss = { showAlarmsDialog = false }
        )
    }

    if (showPrivacyAndDataSafetyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyAndDataSafetyDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isArabic) "سياسة الخصوصية وأمان البيانات 🔒" else "Privacy Policy & Data Safety 🔒",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (isArabic)
                                    "نلتزم بأعلى معايير الخصوصية وسياسات مطوري Google Play لضمان حماية بياناتك بنسبة 100%."
                                else
                                    "We strictly adhere to Google Play Developer Policies and top privacy standards ensuring 100% data protection.",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    item {
                        Text(
                            text = if (isArabic) "1. البيانات التي نقوم بجمعها (Data Collection):" else "1. Data We Collect:",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = if (isArabic)
                                "• البريد الإلكتروني والاسم المعروض لإنشاء الحساب ومزامنة نقاط المكافآت.\n• سجل فحوصات التطبيقات اليومية لتوثيق فترة الـ 14 يوماً.\n• نوع وموديل الجهاز وإصدار أندرويد لتقييم مصفوفة تنوع الأجهزة لمراجعي Google Play."
                            else
                                "• Email and display name for authentication and points synchronization.\n• Daily testing log timestamps to verify the 14-day testing period.\n• Device model and OS version to calculate device diversity metrics for Google Play reviewers.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    item {
                        Text(
                            text = if (isArabic) "2. التشفير والحماية (Security & Encryption):" else "2. Security & Encryption:",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = if (isArabic)
                                "• تشفير كامل لجميع الاتصالات عبر بروتوكول HTTPS / TLS 1.3.\n• حفظ آمن محلياً عبر قاعدة بيانات SQLite المشفرة وسحابياً عبر Google Cloud Firestore."
                            else
                                "• End-to-end HTTPS/TLS 1.3 transit encryption for all communications.\n• Secure local storage via SQLite Room and cloud backend via Google Cloud Firestore.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    item {
                        Text(
                            text = if (isArabic) "3. عدم بيع أو مشاركة البيانات (No Third-Party Sharing):" else "3. Zero Third-Party Sharing:",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = if (isArabic)
                                "• لا نقوم إطلاقاً ببيع أو تأجير أي بيانات شخصية أو تسويقية لأي طرف ثالث."
                            else
                                "• We never sell, rent, or monetize your personal or testing data to any third party.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    item {
                        Text(
                            text = if (isArabic) "4. حق الحذف والنسخ الاحتياطي (Right to Erasure & Backup):" else "4. Data Erasure & Export:",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = if (isArabic)
                                "• يمكنك تصدير نسخة احتياطية من كافة بياناتك بصيغة JSON بضغطة زر واحدة.\n• يمكنك حذف حسابك وكافة سجلاتك نهائياً وفورياً من خيار (حذف الحساب) في هذه الشاشة."
                            else
                                "• You can export your full data in portable JSON format anytime.\n• You can permanently and immediately delete your account and all records via the Delete Account option.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showPrivacyAndDataSafetyDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(if (isArabic) "فهمت وموافق ✓" else "I Understand ✓", fontWeight = FontWeight.Bold)
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
    s: com.example.ui.util.AppStrings,
    isArabic: Boolean = true,
    onEditNameClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
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
                    text = user?.displayName ?: s.roleTester,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onEditNameClick, modifier = Modifier.size(28.dp)) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = s.editName, tint = PlayGreen, modifier = Modifier.size(16.dp))
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
                    color = when (user?.role) {
                        "ADMIN" -> AmberGold.copy(alpha = 0.2f)
                        "GUEST" -> CyanAccent.copy(alpha = 0.15f)
                        else -> PlayGreen.copy(alpha = 0.15f)
                    },
                    border = BorderStroke(1.dp, when (user?.role) {
                        "ADMIN" -> AmberGold
                        "GUEST" -> CyanAccent
                        else -> PlayGreen
                    })
                ) {
                    Text(
                        text = when (user?.role) {
                            "ADMIN" -> s.roleAdmin
                            "GUEST" -> if (isArabic) "حساب ضيف (زائر)" else "Guest Mode"
                            else -> s.roleTester
                        },
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = when (user?.role) {
                            "ADMIN" -> AmberGold
                            "GUEST" -> CyanAccent
                            else -> PlayGreen
                        },
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                // Provider Badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = when (user?.authProvider) {
                            "GOOGLE" -> "Google Auth"
                            "GUEST" -> if (isArabic) "دخول كضيف" else "Guest"
                            else -> "Email Auth"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${s.memberSinceLabel} $registeredDate",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun UserStatsOverviewCard(
    wallet: UserWallet,
    s: com.example.ui.util.AppStrings,
    onOpenBadges: () -> Unit,
    onOpenPointsCenter: (() -> Unit)? = null
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = s.achievementsTitle,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onOpenBadges, contentPadding = PaddingValues(0.dp)) {
                    Text(s.viewBadgesBtn, style = MaterialTheme.typography.labelMedium, color = AmberGold, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    icon = Icons.Default.MonetizationOn,
                    tint = AmberGold,
                    value = "${wallet.points}",
                    label = s.pointsBalanceLabel,
                    onClick = onOpenPointsCenter
                )
                StatItem(
                    icon = Icons.Default.Bolt,
                    tint = PlayGreen,
                    value = "${wallet.currentStreak}",
                    label = s.activeStreakLabel,
                    onClick = onOpenBadges
                )
                StatItem(
                    icon = Icons.Default.CheckCircle,
                    tint = CyanAccent,
                    value = "${wallet.testsCompleted}",
                    label = s.testsCompletedLabel
                )
            }
        }
    }
}

@Composable
private fun StatItem(
    icon: ImageVector,
    tint: Color,
    value: String,
    label: String,
    onClick: (() -> Unit)? = null
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = if (onClick != null) BorderStroke(1.dp, tint.copy(alpha = 0.35f)) else null,
        modifier = Modifier
            .width(100.dp)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
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

@Composable
private fun ThemeOptionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
