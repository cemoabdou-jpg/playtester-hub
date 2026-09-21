package com.example.ui.components

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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.model.JoinedTest
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DangerRed
import com.example.ui.theme.PlayGreen
import com.example.ui.theme.SuccessGreen
import kotlinx.coroutines.delay

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DailyTestConfirmationDialog(
    test: JoinedTest,
    onConfirm: (durationMinutes: Int, rating: Int, notes: String) -> Unit,
    onDismiss: () -> Unit
) {
    val nextDayNumber = (test.daysCompleted + 1).coerceAtMost(14)
    val context = LocalContext.current

    // Check if the app is actually installed on this device
    var isAppInstalled by remember {
        mutableStateOf(
            try {
                context.packageManager.getPackageInfo(test.packageName, 0)
                true
            } catch (e: Exception) {
                false
            }
        )
    }

    var appWasLaunched by remember { mutableStateOf(false) }
    var secondsRemaining by remember { mutableIntStateOf(30) }
    var isTimerRunning by remember { mutableStateOf(false) }
    var isTimerCompleted by remember { mutableStateOf(false) }
    var hasConfirmedTesting by remember { mutableStateOf(false) }
    var installErrorMessage by remember { mutableStateOf<String?>(null) }

    fun refreshInstallStatus(): Boolean {
        val installed = try {
            context.packageManager.getPackageInfo(test.packageName, 0)
            true
        } catch (e: Exception) {
            false
        }
        isAppInstalled = installed
        return installed
    }

    LaunchedEffect(isTimerRunning) {
        if (isTimerRunning && !isTimerCompleted) {
            while (secondsRemaining > 0) {
                delay(1000L)
                secondsRemaining--
            }
            isTimerCompleted = true
            isTimerRunning = false
            hasConfirmedTesting = true
        }
    }

    var selectedDuration by remember { mutableIntStateOf(10) }
    var selectedRating by remember { mutableIntStateOf(5) }
    var customNotes by remember { mutableStateOf("") }
    var selectedTag by remember { mutableStateOf("الواجهات والميزات تعمل بسلاسة") }

    val presetTags = listOf(
        "الواجهات والميزات تعمل بسلاسة",
        "تم اختبار الميزات الأساسية بنجاح",
        "التصميم جذاب وسهل التصفح",
        "لوحظ بطء طفيف عند التحميل",
        "تطبيق رائع وبدون أية أخطاء"
    )

    val durationOptions = listOf(5, 10, 15, 30)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .imePadding()
                .padding(vertical = 24.dp)
                .testTag("daily_test_confirmation_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Header with Close
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
                                .background(PlayGreen.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FactCheck,
                                contentDescription = null,
                                tint = PlayGreen,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "تأكيد التجربة اليومية الإلزامية",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${test.appName} • اليوم $nextDayNumber من 14",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("btn_close_daily_confirm")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "إغلاق",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Google Play Policy Notice Card
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = CyanAccent.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, CyanAccent.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = CyanAccent,
                            modifier = Modifier.size(26.dp)
                        )
                        Column {
                            Text(
                                text = "شرط سياسة Google Play للاختبار المغلق",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = CyanAccent
                            )
                            Text(
                                text = "تشترط Google فتح واستخدام التطبيق يومياً لمدة 14 يوماً متواصلة مع 20 مختبراً على الأقل لتأهيل التطبيق للإطلاق في الإنتاج.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 30-Second Real Testing Verification Card
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            isTimerCompleted -> SuccessGreen.copy(alpha = 0.12f)
                            isTimerRunning -> AmberGold.copy(alpha = 0.12f)
                            else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        }
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(
                        1.5.dp,
                        when {
                            isTimerCompleted -> SuccessGreen
                            isTimerRunning -> AmberGold
                            else -> MaterialTheme.colorScheme.outlineVariant
                        }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("verification_30s_card")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = when {
                                        isTimerCompleted -> Icons.Default.CheckCircle
                                        isTimerRunning -> Icons.Default.HourglassTop
                                        else -> Icons.Default.Schedule
                                    },
                                    contentDescription = null,
                                    tint = when {
                                        isTimerCompleted -> SuccessGreen
                                        isTimerRunning -> AmberGold
                                        else -> MaterialTheme.colorScheme.primary
                                    },
                                    modifier = Modifier.size(22.dp)
                                )
                                Text(
                                    text = "التحقق من فتح التطبيق (30 ثانية)",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            // Status badge
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = when {
                                    isTimerCompleted -> SuccessGreen.copy(alpha = 0.2f)
                                    isTimerRunning -> AmberGold.copy(alpha = 0.2f)
                                    else -> MaterialTheme.colorScheme.surface
                                }
                            ) {
                                Text(
                                    text = when {
                                        isTimerCompleted -> "مكتمل ✓"
                                        isTimerRunning -> "$secondsRemaining ث"
                                        else -> "مطلوب"
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = when {
                                        isTimerCompleted -> SuccessGreen
                                        isTimerRunning -> AmberGold
                                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // App install status note
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isAppInstalled) SuccessGreen.copy(alpha = 0.12f) else DangerRed.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, if (isAppInstalled) SuccessGreen.copy(alpha = 0.4f) else DangerRed.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isAppInstalled) Icons.Default.CheckCircle else Icons.Default.WarningAmber,
                                        contentDescription = null,
                                        tint = if (isAppInstalled) SuccessGreen else DangerRed,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = if (isAppInstalled) {
                                            "التطبيق مثبت على جهازك ✓ (${test.packageName})"
                                        } else {
                                            "تنبيه: التطبيق غير مثبت على هاتفك! يجب تثبيته أولاً للبدء"
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isAppInstalled) SuccessGreen else DangerRed
                                    )
                                }

                                // Refresh check button
                                IconButton(
                                    onClick = {
                                        val nowInstalled = refreshInstallStatus()
                                        installErrorMessage = if (nowInstalled) null else "لم يتم العثور على حزمة '${test.packageName}' مثبتة بعد."
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "إعادة فحص التثبيت",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        installErrorMessage?.let { errMsg ->
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "⚠ $errMsg",
                                style = MaterialTheme.typography.labelSmall,
                                color = DangerRed,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (isTimerCompleted) {
                            Text(
                                text = "أحسنت! تم التحقق بنجاح من فتح وتجربة التطبيق لمدة 30 ثانية كاملة. يمكنك الآن إرسال الملاحظات وتأكيد الحضور اليومي لاستلام النقاط.",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        } else if (isTimerRunning) {
                            Text(
                                text = "جاري احتساب وقت الاستخدام الحقيقي للتطبيق: متبقي $secondsRemaining ثانية...",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            val timerProgress = (30 - secondsRemaining) / 30f
                            LinearProgressIndicator(
                                progress = { timerProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = AmberGold,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "ابقَ في التطبيق وتصفح شاشاته حتى ينتهي العداد لضمان احتساب التجربة في سياسات Google Play.",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { launchOrOpenApp(context, test.packageName, test.androidOptInUrl) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("العودة للتطبيق المستهدف", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        } else if (!isAppInstalled) {
                            // Strictly uninstalled state: user MUST install from Google Play first!
                            Text(
                                text = "وفقاً لسياسة Google Play الصارمة، لا يمكن احتساب العداد أو تسجيل الحضور إلا بعد تثبيت التطبيق الفعلي على جهازك. انقر بالأسفل لتثبيته من Google Play.",
                                style = MaterialTheme.typography.bodySmall,
                                color = DangerRed
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = {
                                    installErrorMessage = null
                                    launchOrOpenApp(context, test.packageName, test.androidOptInUrl)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("btn_install_from_play_store"),
                                colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Download,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "الانتقال لـ Google Play لتثبيت التطبيق",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            TextButton(
                                onClick = {
                                    val nowInstalled = refreshInstallStatus()
                                    if (nowInstalled) {
                                        installErrorMessage = null
                                    } else {
                                        installErrorMessage = "لم يتم تثبيت حزمة التطبيق '${test.packageName}' بعد. يرجى تثبيته من متجر Play أولاً."
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "تم التثبيت بالفعل؟ اضغط هنا للتحقق وتحديث الحالة",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        } else {
                            // Installed state: App must be launched to start timer
                            Text(
                                text = "لضمان مصداقية التجربة أمام Google Play، يلزم فتح التطبيق واستخدامه لمدة 30 ثانية على الأقل قبل تسجيل الحضور اليومي.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = {
                                    val installed = refreshInstallStatus()
                                    if (installed) {
                                        val launched = launchOrOpenApp(context, test.packageName, test.androidOptInUrl)
                                        if (launched) {
                                            appWasLaunched = true
                                            isTimerRunning = true
                                            installErrorMessage = null
                                        } else {
                                            installErrorMessage = "تعذر تشغيل التطبيق تلقائياً. تأكد من اكتمال التثبيت."
                                        }
                                    } else {
                                        installErrorMessage = "التطبيق غير مثبت على جهازك بعد!"
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("btn_launch_and_verify_30s"),
                                colors = ButtonDefaults.buttonColors(containerColor = PlayGreen),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "تشغيل التطبيق وبدء اختبار الـ 30 ثانية",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 1: Duration
                Text(
                    text = "1. مدة تجربة واستخدام التطبيق اليوم:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    durationOptions.forEach { minutes ->
                        val isSelected = selectedDuration == minutes
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) PlayGreen else MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) PlayGreen else MaterialTheme.colorScheme.outlineVariant
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedDuration = minutes }
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "$minutes د",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 2: Rating
                Text(
                    text = "2. تقييم أداء التطبيق اليوم:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    for (star in 1..5) {
                        IconButton(
                            onClick = { selectedRating = star },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (star <= selectedRating) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = "تقييم $star نجوم",
                                tint = if (star <= selectedRating) AmberGold else MaterialTheme.colorScheme.outlineVariant,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when (selectedRating) {
                            5 -> "ممتاز ومستقر جداً"
                            4 -> "جيد جداً وسلس"
                            3 -> "مقبول مع ملاحظات بسيطة"
                            2 -> "يوجد بطء أو أخطاء"
                            else -> "يحتاج تحسين جذري"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 3: Preset Feedback & Notes
                Text(
                    text = "3. ملاحظات الفحص السريع:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    presetTags.forEach { tag ->
                        val isSelected = selectedTag == tag
                        Surface(
                            shape = CircleShape,
                            color = if (isSelected) PlayGreen.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) PlayGreen else MaterialTheme.colorScheme.outlineVariant
                            ),
                            modifier = Modifier.clickable {
                                selectedTag = tag
                                if (customNotes.isBlank()) {
                                    customNotes = tag
                                }
                            }
                        ) {
                            Text(
                                text = tag,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) PlayGreen else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = customNotes,
                    onValueChange = { customNotes = it },
                    label = { Text("تفاصيل أو ملاحظات إضافية للمطور (اختياري)") },
                    placeholder = { Text("مثال: تم تجربة تسجيل الدخول والتنقل بين الصفحات بنجاح...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_daily_notes"),
                    minLines = 2,
                    maxLines = 3,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Mandatory Tester Confirmation Checkbox
                val isCheckAllowed = isTimerCompleted
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = if (isCheckAllowed) 0.6f else 0.3f),
                    border = BorderStroke(
                        1.dp,
                        if (hasConfirmedTesting && isCheckAllowed) PlayGreen else MaterialTheme.colorScheme.outlineVariant
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = isCheckAllowed) { hasConfirmedTesting = !hasConfirmedTesting }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = hasConfirmedTesting && isCheckAllowed,
                            onCheckedChange = { if (isCheckAllowed) hasConfirmedTesting = it },
                            enabled = isCheckAllowed,
                            colors = CheckboxDefaults.colors(checkedColor = PlayGreen),
                            modifier = Modifier.testTag("checkbox_confirm_daily_test")
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isTimerCompleted) {
                                "أقر وأؤكد بأنني قمت بفتح تطبيق '${test.appName}' وتجربته اليوم لمدة 30 ثانية على الأقل وفق إرشادات الاختبار المغلق."
                            } else {
                                "يرجى تشغيل واختبار التطبيق لمدة 30 ثانية لتفعيل إقرار وتأكيد اليوم."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = if (isCheckAllowed) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Confirm Action Button
                val isSubmitEnabled = hasConfirmedTesting && isTimerCompleted
                Button(
                    onClick = {
                        val finalNotes = if (customNotes.isNotBlank()) customNotes else selectedTag
                        onConfirm(selectedDuration, selectedRating, finalNotes)
                    },
                    enabled = isSubmitEnabled,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btn_submit_daily_confirmation"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PlayGreen,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (isSubmitEnabled) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when {
                            !isTimerCompleted && isTimerRunning -> "متبقي $secondsRemaining ثانية لاكتمال الاختبار..."
                            !isTimerCompleted -> "يلزم فتح واختبار التطبيق 30 ثانية أولاً"
                            !hasConfirmedTesting -> "يرجى تأكيد الإقرار أعلاه للمتابعة"
                            else -> "توثيق وتأكيد تجربة اليوم (+25 نقطة) ✓"
                        },
                        color = if (isSubmitEnabled) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
