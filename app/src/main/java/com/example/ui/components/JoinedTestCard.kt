package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.data.model.JoinedTest
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DangerRed
import com.example.ui.theme.PlayGreen
import com.example.ui.theme.SuccessGreen
import com.example.ui.util.AppLanguage
import com.example.ui.util.L
import com.example.ui.util.LocalAppLanguage
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun JoinedTestCard(
    test: JoinedTest,
    onCheckInClick: () -> Unit,
    onFeedbackClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onRecordOpened: (packageName: String, appName: String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val language = LocalAppLanguage.current
    val isArabic = language == AppLanguage.AR
    val s = L.strings()
    val todayStr = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()) }
    val isCheckedToday = test.lastCheckInDate == todayStr
    val checkedDaysList = remember(test.checkedDaysCsv) {
        test.checkedDaysCsv.split(",").filter { it.isNotEmpty() }.mapNotNull { it.toIntOrNull() }.toSet()
    }
    val progress = (test.daysCompleted.toFloat() / 14f).coerceIn(0f, 1f)
    var showCalendarGrid by remember { mutableStateOf(false) }

    // Check if target app is installed on this device
    var isAppInstalled by remember(test.packageName) {
        mutableStateOf(
            try {
                context.packageManager.getLaunchIntentForPackage(test.packageName) != null
            } catch (_: Exception) {
                false
            }
        )
    }

    var appLaunchStartTime by remember { mutableLongStateOf(0L) }
    var measuredDwellSeconds by remember { mutableIntStateOf(0) }

    // Auto-track dwell time when returning from the tested app
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                // Refresh installation state
                isAppInstalled = try {
                    context.packageManager.getLaunchIntentForPackage(test.packageName) != null
                } catch (_: Exception) {
                    false
                }

                if (appLaunchStartTime > 0L) {
                    val duration = ((System.currentTimeMillis() - appLaunchStartTime) / 1000).toInt()
                    appLaunchStartTime = 0L
                    if (duration > 0) {
                        measuredDwellSeconds = duration
                        if (duration >= 30) {
                            Toast.makeText(
                                context,
                                if (isArabic) "أحسنت! قضيت $duration ثانية في تجربة ${test.appName} ⏱️. اضغط 'فحص اليوم' لتوثيق اليوم وكسب النقاط!"
                                else "Great! You spent $duration seconds testing ${test.appName} ⏱️. Tap 'Daily Check-in' to record progress!",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(
                                context,
                                if (isArabic) "قضيت $duration ثانية داخل التطبيق. توصي Google بقضاء 30 ثانية على الأقل لتوثيق الاستخدام الحقيقي."
                                else "You spent $duration seconds in app. Google recommends at least 30 seconds for genuine usage.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("joined_test_card_${test.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            1.dp,
            if (test.isCompleted) PlayGreen.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Header: Status badge & Quick Calendar toggle & Delete icon
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
                        shape = RoundedCornerShape(8.dp),
                        color = if (test.isCompleted) SuccessGreen.copy(alpha = 0.15f) else CyanAccent.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, if (test.isCompleted) SuccessGreen.copy(alpha = 0.35f) else CyanAccent.copy(alpha = 0.35f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (test.isCompleted) Icons.Default.CheckCircle else Icons.Default.Schedule,
                                contentDescription = null,
                                tint = if (test.isCompleted) SuccessGreen else CyanAccent,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (test.isCompleted) s.testingCompletedCount else s.testingActiveCount,
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (test.isCompleted) SuccessGreen else CyanAccent
                            )
                        }
                    }

                    // Device Installation Status Badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isAppInstalled) PlayGreen.copy(alpha = 0.12f) else AmberGold.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, if (isAppInstalled) PlayGreen.copy(alpha = 0.3f) else AmberGold.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (isAppInstalled) PlayGreen else AmberGold)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isAppInstalled) (if (isArabic) "مثبت ✓" else "Installed ✓") else (if (isArabic) "غير مثبت" else "Not Installed"),
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isAppInstalled) PlayGreen else AmberGold
                            )
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.clickable { showCalendarGrid = !showCalendarGrid }
                    ) {
                        Text(
                            text = if (showCalendarGrid) (if (isArabic) "إخفاء التقويم ▴" else "Hide ▴") else (if (isArabic) "تفاصيل الأيام ▾" else "Days ▾"),
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }

                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier
                            .size(28.dp)
                            .testTag("delete_test_${test.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = s.deleteJoinedTest,
                            tint = DangerRed.copy(alpha = 0.6f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // App row: Clickable icon + title & package + progress bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable {
                            appLaunchStartTime = System.currentTimeMillis()
                            launchOrOpenApp(
                                context = context,
                                packageName = test.packageName,
                                fallbackUrl = test.androidOptInUrl,
                                appName = test.appName,
                                isArabic = isArabic,
                                onRecordOpened = onRecordOpened
                            )
                        }
                        .testTag("joined_app_icon_${test.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    AppIconImage(
                        iconUrl = test.iconUrl,
                        appName = test.appName,
                        packageName = test.packageName,
                        size = 48.dp,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Surface(
                        shape = CircleShape,
                        color = PlayGreen,
                        shadowElevation = 1.dp,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(10.dp).padding(1.dp)
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = test.appName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(1.dp))
                    Text(
                        text = test.packageName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .weight(1f)
                                .height(5.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (test.isCompleted) SuccessGreen else PlayGreen,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                        Text(
                            text = "${test.daysCompleted}/14 يوم",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (test.isCompleted) SuccessGreen else PlayGreen
                        )
                    }
                }
            }

            // Measured Dwell Time Pill (if tester launched app recently)
            if (measuredDwellSeconds > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PlayGreen.copy(alpha = 0.1f),
                    border = BorderStroke(1.dp, PlayGreen.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Timer, contentDescription = null, tint = PlayGreen, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isArabic) "⏱️ مدة التجربة المحسوبة: $measuredDwellSeconds ثانية (استخدام حقيقي موثق)" else "⏱️ Dwell time: ${measuredDwellSeconds}s (Genuine usage logged)",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = PlayGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Expandable 14-Day Calendar Circles Grid (1..14)
            AnimatedVisibility(visible = showCalendarGrid) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    Text(
                        text = if (isArabic) "تفاصيل الأيام المكتملة:" else "Completed Days:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        maxItemsInEachRow = 7
                    ) {
                        for (day in 1..14) {
                            val isChecked = checkedDaysList.contains(day)
                            val isCurrentDay = day == test.daysCompleted + 1 && !isCheckedToday && !test.isCompleted

                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isChecked -> PlayGreen
                                            isCurrentDay -> AmberGold.copy(alpha = 0.2f)
                                            else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                        }
                                    )
                                    .then(
                                        if (isCurrentDay) {
                                            Modifier.border(
                                                width = 1.5.dp,
                                                color = AmberGold,
                                                shape = CircleShape
                                            )
                                        } else Modifier
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isChecked) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(15.dp)
                                    )
                                } else {
                                    Text(
                                        text = "$day",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 10.sp,
                                        fontWeight = if (isCurrentDay) FontWeight.ExtraBold else FontWeight.Medium,
                                        color = if (isCurrentDay) AmberGold else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons (Direct App Launcher + Daily Check-in + Feedback)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Direct App Launcher Button with Auto-Duration Tracking
                Button(
                    onClick = {
                        appLaunchStartTime = System.currentTimeMillis()
                        launchOrOpenApp(
                            context = context,
                            packageName = test.packageName,
                            fallbackUrl = test.androidOptInUrl,
                            appName = test.appName,
                            isArabic = isArabic,
                            onRecordOpened = onRecordOpened
                        )
                    },
                    modifier = Modifier
                        .weight(1.1f)
                        .height(34.dp)
                        .testTag("direct_launch_btn_${test.id}"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isAppInstalled) PlayGreen else CyanAccent
                    ),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = if (isAppInstalled) Icons.Default.RocketLaunch else Icons.Default.Download,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isAppInstalled) (if (isArabic) "تشغيل التطبيق" else "Launch App") else (if (isArabic) "تثبيت التطبيق" else "Install App"),
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 10.sp,
                        maxLines = 1
                    )
                }

                // Daily Check-in CTA Button
                if (!test.isCompleted) {
                    Button(
                        onClick = onCheckInClick,
                        enabled = !isCheckedToday,
                        modifier = Modifier
                            .weight(1.1f)
                            .height(34.dp)
                            .testTag("check_in_btn_${test.id}"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        if (isCheckedToday) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = PlayGreen,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = if (isArabic) "تم الفحص ✓" else "Tested ✓",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.FactCheck,
                                contentDescription = null,
                                modifier = Modifier.size(13.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = if (isArabic) "تأكيد الفحص" else "Check-in",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                // Send feedback button
                OutlinedButton(
                    onClick = onFeedbackClick,
                    modifier = Modifier
                        .height(34.dp)
                        .testTag("feedback_btn_${test.id}"),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Chat,
                        contentDescription = null,
                        tint = if (test.feedbackSent) SuccessGreen else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = if (test.feedbackSent) (if (isArabic) "تم التقييم ✓" else "Rated ✓") else (if (isArabic) "تقييم" else "Review"),
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

fun launchOrOpenApp(
    context: Context,
    packageName: String,
    fallbackUrl: String,
    appName: String = "",
    isArabic: Boolean = true,
    onRecordOpened: (String, String) -> Unit = { _, _ -> }
): Boolean {
    val pm = context.packageManager
    val launchIntent = pm.getLaunchIntentForPackage(packageName)
    return if (launchIntent != null) {
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(launchIntent)
        Toast.makeText(context, if (isArabic) "جاري تشغيل $appName واحتساب وقت التجربة تلقائياً... ⏱️" else "Launching $appName and auto-tracking test duration... ⏱️", Toast.LENGTH_SHORT).show()
        onRecordOpened(packageName, appName.ifBlank { packageName })
        true
    } else {
        // Not installed, open Play Store test link
        val url = if (fallbackUrl.isNotBlank()) fallbackUrl else "https://play.google.com/store/apps/details?id=$packageName"
        openUrl(context, url)
        Toast.makeText(context, if (isArabic) "جاري فتح صفحة التثبيت والاختبار في متجر Google Play..." else "Opening Play Store testing page...", Toast.LENGTH_SHORT).show()
        onRecordOpened(packageName, appName.ifBlank { packageName })
        false
    }
}
