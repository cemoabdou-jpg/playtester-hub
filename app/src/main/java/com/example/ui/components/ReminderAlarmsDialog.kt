package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DangerRed
import com.example.ui.theme.PlayGreen
import com.example.ui.theme.SuccessGreen
import com.example.ui.util.AppLanguage
import com.example.ui.util.LocalAppLanguage
import com.example.util.NotificationHelper

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ReminderAlarmsDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val language = LocalAppLanguage.current
    val isArabic = language == AppLanguage.AR

    var isReminderEnabled by remember { mutableStateOf(NotificationHelper.isDailyReminderEnabled(context)) }
    var activeHours by remember { mutableStateOf(NotificationHelper.getReminderHours(context)) }
    var showCustomHourPicker by remember { mutableStateOf(false) }

    val presetSlots = remember(isArabic) {
        listOf(
            9 to (if (isArabic) "🌅 صباحاً (09:00 ص)" else "🌅 Morning (09:00 AM)"),
            14 to (if (isArabic) "☀️ ظهراً (02:00 م)" else "☀️ Midday (02:00 PM)"),
            20 to (if (isArabic) "🌇 مساءً (08:00 م)" else "🌇 Evening (08:00 PM)"),
            23 to (if (isArabic) "🌙 ليلاً (11:00 م)" else "🌙 Night (11:00 PM)")
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 16.dp)
                .testTag("dialog_reminder_alarms"),
            shape = RoundedCornerShape(22.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(AmberGold.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = AmberGold,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column {
                            Text(
                                text = if (isArabic) "منبهات ومواعيد التذكير" else "Multi-Schedule Reminders",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isArabic) "تنبيهات يومية في أكثر من موعد للفحص" else "Daily testing alarms across multiple slots",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Master Toggle Card
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isReminderEnabled) PlayGreen.copy(alpha = 0.10f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    border = BorderStroke(1.dp, if (isReminderEnabled) PlayGreen.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isArabic) "تفعيل المنبهات والتذكيرات اليومية" else "Enable Daily Alarms",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isReminderEnabled) PlayGreen else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isReminderEnabled)
                                    (if (isArabic) "المنبه نشط (${activeHours.size} مواعيد مجدولة)" else "Alarms active (${activeHours.size} schedules)")
                                else
                                    (if (isArabic) "التنبيهات متوقفة حالياً" else "Alarms are currently paused"),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Switch(
                            checked = isReminderEnabled,
                            onCheckedChange = { enabled ->
                                isReminderEnabled = enabled
                                NotificationHelper.setDailyReminderEnabled(context, enabled)
                                Toast.makeText(
                                    context,
                                    if (enabled) (if (isArabic) "تم تشغيل المنبهات اليومية ✓" else "Alarms enabled ✓")
                                    else (if (isArabic) "تم إيقاف المنبهات" else "Alarms paused"),
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = PlayGreen
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isReminderEnabled) {
                    // Quick Preset Schedules Section
                    Text(
                        text = if (isArabic) "مواعيد التنبيهات المقترحة (اختر أكثر من موعد):" else "Recommended Alarm Slots (Select multiple):",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isArabic) "انقر على أي موعد لتفعيله أو إلغائه مباشرة:" else "Tap any slot to toggle on/off directly:",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        presetSlots.forEach { (hour, slotLabel) ->
                            val isSelected = activeHours.contains(hour)
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) PlayGreen.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                border = BorderStroke(1.dp, if (isSelected) PlayGreen.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        val updated = NotificationHelper.toggleReminderHour(context, hour)
                                        activeHours = updated
                                        val activated = updated.contains(hour)
                                        Toast.makeText(
                                            context,
                                            if (activated) (if (isArabic) "تم تفعيل منبه $slotLabel ✓" else "Activated $slotLabel")
                                            else (if (isArabic) "تم إيقاف منبه $slotLabel" else "Removed $slotLabel"),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(if (isSelected) PlayGreen else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = if (isSelected) Icons.Default.Check else Icons.Default.AccessTime,
                                                contentDescription = null,
                                                tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }

                                        Text(
                                            text = slotLabel,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) PlayGreen else MaterialTheme.colorScheme.onSurface
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) PlayGreen.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
                                    ) {
                                        Text(
                                            text = if (isSelected) (if (isArabic) "مفعل 🟢" else "Active") else (if (isArabic) "معطل" else "Off"),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) PlayGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Custom Hour Picker Section
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isArabic) "ساعات إضافية مخصصة:" else "Custom Hourly Slots:",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                OutlinedButton(
                                    onClick = { showCustomHourPicker = !showCustomHourPicker },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Icon(
                                        imageVector = if (showCustomHourPicker) Icons.Default.Close else Icons.Default.Add,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (showCustomHourPicker) (if (isArabic) "إغلاق" else "Close") else (if (isArabic) "إضافة ساعة أخرى" else "Add Custom Hour"),
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            if (showCustomHourPicker) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = if (isArabic) "اختر أي ساعة لإضافتها إلى قائمة المنبهات:" else "Pick any hour to add to your reminders list:",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    for (h in 0..23) {
                                        val isHourActive = activeHours.contains(h)
                                        val hourStr = String.format("%02d:00", h)
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (isHourActive) PlayGreen else MaterialTheme.colorScheme.surface,
                                            border = BorderStroke(1.dp, if (isHourActive) PlayGreen else MaterialTheme.colorScheme.outlineVariant),
                                            modifier = Modifier.clickable {
                                                val updated = NotificationHelper.toggleReminderHour(context, h)
                                                activeHours = updated
                                            }
                                        ) {
                                            Text(
                                                text = hourStr,
                                                fontSize = 11.sp,
                                                fontWeight = if (isHourActive) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isHourActive) Color.White else MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Active Alarms summary chips
                            Text(
                                text = if (isArabic) "جميع المواعيد النشطة حالياً (${activeHours.size}):" else "Currently Active Schedules (${activeHours.size}):",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                activeHours.sorted().forEach { hour ->
                                    val formatted = when {
                                        hour == 0 -> if (isArabic) "12:00 منتصف الليل" else "12:00 AM"
                                        hour < 12 -> if (isArabic) "$hour:00 صباحاً" else "$hour:00 AM"
                                        hour == 12 -> if (isArabic) "12:00 ظهراً" else "12:00 PM"
                                        else -> if (isArabic) "${hour - 12}:00 مساءً" else "${hour - 12}:00 PM"
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(14.dp),
                                        color = PlayGreen.copy(alpha = 0.15f),
                                        border = BorderStroke(1.dp, PlayGreen.copy(alpha = 0.35f))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "⏰ $formatted",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = PlayGreen
                                            )
                                            if (activeHours.size > 1) {
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = "Remove",
                                                    tint = DangerRed,
                                                    modifier = Modifier
                                                        .size(14.dp)
                                                        .clickable {
                                                            val updated = NotificationHelper.toggleReminderHour(context, hour)
                                                            activeHours = updated
                                                        }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Instant Test Notification Button
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = AmberGold.copy(alpha = 0.10f),
                        border = BorderStroke(1.dp, AmberGold.copy(alpha = 0.35f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = null,
                                        tint = AmberGold,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isArabic) "تجربة الإشعار فوراً" else "Test Notification Now",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Text(
                                    text = if (isArabic) "تأكد من وصول الصوت والاهتزاز على جهازك" else "Verify notification sound and vibration",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Button(
                                onClick = {
                                    NotificationHelper.showSystemNotification(
                                        context = context,
                                        title = if (isArabic) "⏰ تجربة منبه PlayTester Hub بنجاح!" else "⏰ PlayTester Hub Reminder Working!",
                                        message = if (isArabic) "المنبه يعمل بشكل مثالي في جميع مواعيدك المحددة لتذكيرك بالفحص اليومي." else "Your testing reminders are active across all configured times.",
                                        notificationId = 8801
                                    )
                                    Toast.makeText(
                                        context,
                                        if (isArabic) "تم إرسال إشعار تجريبي للجهاز! تفقد شريط الإشعارات ✓" else "Test notification sent! Check your status bar ✓",
                                        Toast.LENGTH_LONG
                                    ).show()
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AmberGold)
                            ) {
                                Text(
                                    text = if (isArabic) "إرسال تجربة" else "Send Test",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Done Button
                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PlayGreen),
                    modifier = Modifier.fillMaxWidth().height(46.dp)
                ) {
                    Text(
                        text = if (isArabic) "تم وحفظ التنبيهات ✓" else "Save & Done ✓",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
