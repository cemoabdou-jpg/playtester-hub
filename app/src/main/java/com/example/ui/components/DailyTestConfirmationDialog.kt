package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.ui.util.LocalAppLanguage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DailyTestConfirmationDialog(
    test: JoinedTest,
    onConfirm: (durationMinutes: Int, rating: Int, notes: String, proofScreenshotUrl: String) -> Unit,
    onDismiss: () -> Unit
) {
    val language = LocalAppLanguage.current
    val isArabic = language == AppLanguage.AR

    val nextDayNumber = (test.daysCompleted + 1).coerceAtMost(14)
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Check if the target app is actually installed on this device
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

    var appLaunchStartTime by remember { mutableLongStateOf(0L) }
    var totalAppUsageSeconds by remember { mutableIntStateOf(0) }
    var isWaitingForAppReturn by remember { mutableStateOf(false) }

    var isSimulationMode by remember { mutableStateOf(false) }
    var simulationSecondsRemaining by remember { mutableIntStateOf(30) }
    var isSimulationRunning by remember { mutableStateOf(false) }

    var isTimerCompleted by remember { mutableStateOf(false) }
    var hasConfirmedTesting by remember { mutableStateOf(false) }
    var installErrorMessage by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()
    var proofScreenshotUrl by remember { mutableStateOf("") }
    var isProcessingScreenshot by remember { mutableStateOf(false) }

    val screenshotPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri: Uri? ->
            if (uri != null) {
                isProcessingScreenshot = true
                coroutineScope.launch(Dispatchers.IO) {
                    try {
                        val processed = AppIconProcessor.processPickedImage(context, uri, test.packageName)
                        withContext(Dispatchers.Main) {
                            proofScreenshotUrl = processed
                            isProcessingScreenshot = false
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            proofScreenshotUrl = uri.toString()
                            isProcessingScreenshot = false
                        }
                    }
                }
            }
        }
    )

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

    // Monitor real app departure and return to measure genuine usage time
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (appLaunchStartTime > 0L) {
                    val duration = ((System.currentTimeMillis() - appLaunchStartTime) / 1000).toInt()
                    appLaunchStartTime = 0L
                    isWaitingForAppReturn = false
                    if (duration > 0) {
                        totalAppUsageSeconds += duration
                    }
                    if (totalAppUsageSeconds >= 25) {
                        isTimerCompleted = true
                        hasConfirmedTesting = true
                        installErrorMessage = null
                    } else {
                        isTimerCompleted = false
                        installErrorMessage = if (isArabic) {
                            "⚠️ لقد قضيت $totalAppUsageSeconds ثانية فقط داخل التطبيق المستهدف. يجب قضاء 30 ثانية على الأقل لتوثيق الاستخدام الفعلي في Google Play."
                        } else {
                            "⚠️ You spent only $totalAppUsageSeconds seconds in the target app. You need at least 30 seconds to document genuine usage for Google Play."
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

    // Simulation timer effect for demo
    LaunchedEffect(isSimulationRunning) {
        if (isSimulationRunning && !isTimerCompleted) {
            while (simulationSecondsRemaining > 0) {
                delay(1000L)
                simulationSecondsRemaining--
            }
            isTimerCompleted = true
            isSimulationRunning = false
            hasConfirmedTesting = true
        }
    }

    var selectedDuration by remember { mutableIntStateOf(10) }
    var selectedRating by remember { mutableIntStateOf(5) }
    var customNotes by remember { mutableStateOf("") }
    
    val presetTags = remember(isArabic) {
        if (isArabic) {
            listOf(
                "الواجهات والميزات تعمل بسلاسة",
                "تم اختبار الميزات الأساسية بنجاح",
                "التصميم جذاب وسهل التصفح",
                "لوحظ بطء طفيف عند التحميل",
                "تطبيق رائع وبدون أية أخطاء"
            )
        } else {
            listOf(
                "UI and features run smoothly",
                "Core functionality tested successfully",
                "Great design and easy navigation",
                "Minor delay noticed during loading",
                "Excellent app, zero errors found"
            )
        }
    }

    var selectedTag by remember(presetTags) { mutableStateOf(presetTags.first()) }
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
                                imageVector = Icons.AutoMirrored.Filled.FactCheck,
                                contentDescription = null,
                                tint = PlayGreen,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Text(
                                text = if (isArabic) "تأكيد التجربة اليومية الإلزامية" else "Mandatory Daily Test Confirmation",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isArabic) "${test.appName} • اليوم $nextDayNumber من 14" else "${test.appName} • Day $nextDayNumber of 14",
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
                            contentDescription = if (isArabic) "إغلاق" else "Close",
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
                                text = if (isArabic) "شرط سياسة Google Play للاختبار المغلق" else "Google Play Closed Testing Policy Requirement",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = CyanAccent
                            )
                            Text(
                                text = if (isArabic) {
                                    "تشترط Google فتح واستخدام التطبيق فعلياً يومياً لمدة 14 يوماً متواصلة. يتم قياس مدة الاستخدام لضمان التفاعل الحقيقي."
                                } else {
                                    "Google requires real active usage every day for 14 continuous days. Usage duration is measured to ensure genuine engagement."
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Real Testing Verification Card
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            isTimerCompleted -> SuccessGreen.copy(alpha = 0.12f)
                            isWaitingForAppReturn || isSimulationRunning -> AmberGold.copy(alpha = 0.12f)
                            else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        }
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(
                        1.5.dp,
                        when {
                            isTimerCompleted -> SuccessGreen
                            isWaitingForAppReturn || isSimulationRunning -> AmberGold
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
                                        isWaitingForAppReturn || isSimulationRunning -> Icons.Default.HourglassTop
                                        else -> Icons.Default.Schedule
                                    },
                                    contentDescription = null,
                                    tint = when {
                                        isTimerCompleted -> SuccessGreen
                                        isWaitingForAppReturn || isSimulationRunning -> AmberGold
                                        else -> MaterialTheme.colorScheme.primary
                                    },
                                    modifier = Modifier.size(22.dp)
                                )
                                Text(
                                    text = if (isArabic) {
                                        if (isSimulationMode) "محاكاة الفحص (Demo)" else "التحقق من فتح التطبيق الفعلي"
                                    } else {
                                        if (isSimulationMode) "Inspection Simulation (Demo)" else "App Launch & Usage Verification"
                                    },
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
                                    isWaitingForAppReturn -> CyanAccent.copy(alpha = 0.2f)
                                    isSimulationRunning -> AmberGold.copy(alpha = 0.2f)
                                    else -> MaterialTheme.colorScheme.surface
                                }
                            ) {
                                Text(
                                    text = when {
                                        isTimerCompleted -> if (isArabic) "مكتمل ✓ ($totalAppUsageSeconds ث)" else "Done ✓ (${totalAppUsageSeconds}s)"
                                        isWaitingForAppReturn -> if (isArabic) "قيد الاستخدام الآن" else "In Use Now"
                                        isSimulationRunning -> if (isArabic) "$simulationSecondsRemaining ث" else "${simulationSecondsRemaining}s"
                                        else -> if (isArabic) "مطلوب فتح التطبيق" else "Open App Required"
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = when {
                                        isTimerCompleted -> SuccessGreen
                                        isWaitingForAppReturn -> CyanAccent
                                        isSimulationRunning -> AmberGold
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
                                            if (isArabic) "التطبيق مثبت على جهازك ✓ (${test.packageName})" else "App is installed on your device ✓ (${test.packageName})"
                                        } else {
                                            if (isArabic) "تنبيه: التطبيق غير مثبت على هاتفك! يجب تثبيته أولاً للبدء" else "Warning: Target app not installed! Install from Play Store first"
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
                                        installErrorMessage = if (nowInstalled) null else if (isArabic) "لم يتم العثور على حزمة '${test.packageName}' مثبتة بعد." else "Package '${test.packageName}' not found."
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = if (isArabic) "إعادة فحص التثبيت" else "Recheck install",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        installErrorMessage?.let { errMsg ->
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = errMsg,
                                style = MaterialTheme.typography.labelSmall,
                                color = DangerRed,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (isTimerCompleted) {
                            Text(
                                text = if (isArabic) {
                                    "أحسنت! تم التحقق بنجاح من فتح وتجربة التطبيق لمدة كافية ($totalAppUsageSeconds ثانية). يمكنك الآن إرسال الملاحظات وتأكيد الحضور اليومي لاستلام النقاط."
                                } else {
                                    "Great! App usage verified for sufficient time ($totalAppUsageSeconds seconds). You can now submit feedback and claim your daily streak points."
                                },
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        } else if (isWaitingForAppReturn) {
                            Text(
                                text = if (isArabic) {
                                    "تم تشغيل التطبيق المستهدف! تجول داخل شاشاته لمدة 30 ثانية على الأقل، وعند عودتك لهنا سيتم قياس وتوثيق وقت استخدامك الفعلي تلقائياً."
                                } else {
                                    "Target app launched! Interact with it for at least 30 seconds. When you return here, your time will be recorded automatically."
                                },
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = CyanAccent
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
                                Text(if (isArabic) "العودة للتطبيق المستهدف" else "Return to Target App", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        } else if (isSimulationRunning) {
                            Text(
                                text = if (isArabic) "وضع المحاكاة يعمل: متبقي $simulationSecondsRemaining ثانية..." else "Simulation running: $simulationSecondsRemaining seconds left...",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = AmberGold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            val timerProgress = (30 - simulationSecondsRemaining) / 30f
                            LinearProgressIndicator(
                                progress = { timerProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = AmberGold,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        } else if (!isAppInstalled && !isSimulationMode) {
                            Text(
                                text = if (isArabic) {
                                    "وفقاً لسياسة Google Play الصارمة، لا يمكن احتساب التجربة أو تسجيل الحضور بمجرد الضغط، بل يجب تثبيت التطبيق الفعلي على جهازك وفتحه يومياً."
                                } else {
                                    "Under Google Play's strict policy, daily participation requires the actual app to be installed and opened on your physical device."
                                },
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
                                    text = if (isArabic) "الانتقال لـ Google Play لتثبيت التطبيق" else "Go to Google Play to Install App",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(
                                    onClick = {
                                        val nowInstalled = refreshInstallStatus()
                                        if (nowInstalled) {
                                            installErrorMessage = null
                                        } else {
                                            installErrorMessage = if (isArabic) "لم يتم تثبيت حزمة التطبيق '${test.packageName}' بعد." else "App package '${test.packageName}' not yet installed."
                                        }
                                    }
                                ) {
                                    Text(if (isArabic) "تحديث الفحص" else "Recheck", style = MaterialTheme.typography.labelSmall)
                                }

                                TextButton(
                                    onClick = { isSimulationMode = true }
                                ) {
                                    Icon(Icons.Default.Science, contentDescription = null, modifier = Modifier.size(14.dp), tint = AmberGold)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (isArabic) "محاكاة الاختبار (Sandbox)" else "Test Simulation (Sandbox)", style = MaterialTheme.typography.labelSmall, color = AmberGold)
                                }
                            }
                        } else if (isSimulationMode) {
                            Text(
                                text = if (isArabic) "🧪 وضع المحاكاة للتطوير: يمكنك استخدام هذا الوضع لاختبار تدفق التطبيق دون الحاجة لتثبيت الحزمة الفعلية." else "🧪 Sandbox Mode: Simulate the 30-second testing timer without installing the actual package.",
                                style = MaterialTheme.typography.bodySmall,
                                color = AmberGold
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = {
                                    isSimulationRunning = true
                                    installErrorMessage = null
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Timer, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isArabic) "بدء محاكاة الـ 30 ثانية" else "Start 30-second Simulation", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            // Installed state
                            Text(
                                text = if (isArabic) {
                                    "لضمان مصداقية التجربة أمام Google Play، اضغط أدناه لفتح التطبيق واستخدامه لمدة 30 ثانية. عند عودتك سيتم توثيق الاستخدام."
                                } else {
                                    "To ensure valid test logs for Google Play, tap below to open the app and use it for 30 seconds. Your usage will be verified upon return."
                                },
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
                                            appLaunchStartTime = System.currentTimeMillis()
                                            isWaitingForAppReturn = true
                                            installErrorMessage = null
                                        } else {
                                            installErrorMessage = if (isArabic) "تعذر تشغيل التطبيق تلقائياً. تأكد من اكتمال التثبيت." else "Failed to launch app automatically. Ensure installation completed."
                                        }
                                    } else {
                                        installErrorMessage = if (isArabic) "التطبيق غير مثبت على جهازك بعد!" else "App is not installed on this device!"
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
                                    text = if (isArabic) "تشغيل التطبيق وبدء تجربة الـ 30 ثانية" else "Launch App & Start 30s Test",
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
                    text = if (isArabic) "1. مدة تجربة واستخدام التطبيق اليوم:" else "1. Testing Duration Today:",
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
                                    text = if (isArabic) "$minutes د" else "${minutes}m",
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
                    text = if (isArabic) "2. تقييم أداء التطبيق اليوم:" else "2. App Performance Rating Today:",
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
                                contentDescription = "$star stars",
                                tint = if (star <= selectedRating) AmberGold else MaterialTheme.colorScheme.outlineVariant,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when (selectedRating) {
                            5 -> if (isArabic) "ممتاز ومستقر جداً" else "Excellent & Very Stable"
                            4 -> if (isArabic) "جيد جداً وسلس" else "Very Good & Smooth"
                            3 -> if (isArabic) "مقبول مع ملاحظات بسيطة" else "Fair with minor feedback"
                            2 -> if (isArabic) "يوجد بطء أو أخطاء" else "Slow or contains bugs"
                            else -> if (isArabic) "يحتاج تحسين جذري" else "Needs major improvement"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 3: Preset Feedback & Notes
                Text(
                    text = if (isArabic) "3. ملاحظات الفحص السريع:" else "3. Quick Feedback Tags:",
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
                    label = { Text(if (isArabic) "تفاصيل أو ملاحظات إضافية للمطور (اختياري)" else "Additional Feedback Details (Optional)") },
                    placeholder = { Text(if (isArabic) "مثال: تم تجربة تسجيل الدخول والتنقل بنجاح..." else "e.g. Login flow tested successfully...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_daily_notes"),
                    minLines = 2,
                    maxLines = 3,
                    shape = RoundedCornerShape(12.dp)
                )

                // --- Screenshot Proof Card (Verified Tester Proof for Google Play) ---
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (proofScreenshotUrl.isNotBlank()) SuccessGreen.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    border = BorderStroke(
                        1.dp,
                        if (proofScreenshotUrl.isNotBlank()) SuccessGreen.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhotoCamera,
                                    contentDescription = null,
                                    tint = if (proofScreenshotUrl.isNotBlank()) SuccessGreen else CyanAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = if (isArabic) "إثبات الفحص بلقطة شاشة (Screenshot)" else "Testing Proof Screenshot",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            if (proofScreenshotUrl.isNotBlank()) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = SuccessGreen.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = if (isArabic) "إثبات موثق ✓" else "Verified Proof ✓",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = SuccessGreen,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Text(
                            text = if (isArabic) {
                                "إرفاق لقطة شاشة من داخل التطبيق يرفع مصداقية الفحص لدى مراجعي Google Play بنسبة 100%."
                            } else {
                                "Attaching an in-app screenshot provides rock-solid proof for Google Play closed-testing reviewers."
                            },
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                        )

                        if (proofScreenshotUrl.isNotBlank()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    AppIconImage(
                                        iconUrl = proofScreenshotUrl,
                                        appName = "Screenshot",
                                        size = 54.dp,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    Column {
                                        Text(
                                            text = if (isArabic) "تم إرفاق صورة الإثبات" else "Proof attached",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = SuccessGreen
                                        )
                                        Text(
                                            text = if (isArabic) "جاهز للإرسال مع التقرير" else "Ready to submit",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    IconButton(
                                        onClick = {
                                            screenshotPickerLauncher.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            )
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Refresh, contentDescription = "تغيير", tint = CyanAccent, modifier = Modifier.size(16.dp))
                                    }
                                    IconButton(
                                        onClick = { proofScreenshotUrl = "" },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "حذف", tint = DangerRed, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        } else {
                            OutlinedButton(
                                onClick = {
                                    screenshotPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                enabled = !isProcessingScreenshot,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth().height(38.dp)
                            ) {
                                if (isProcessingScreenshot) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = CyanAccent)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(if (isArabic) "جاري معالجة الصورة..." else "Processing image...", fontSize = 11.sp)
                                } else {
                                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(if (isArabic) "إرفاق لقطة شاشة من المعرض 📸" else "Pick Screenshot from Gallery 📸", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                                }
                            }
                        }
                    }
                }

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
                                if (isArabic) {
                                    "أقر وأؤكد بأنني قمت بفتح تطبيق '${test.appName}' وتجربته اليوم لمدة 30 ثانية على الأقل وفق إرشادات الاختبار المغلق."
                                } else {
                                    "I confirm that I opened and tested '${test.appName}' for at least 30 seconds today in accordance with closed testing guidelines."
                                }
                            } else {
                                if (isArabic) {
                                    "يرجى تشغيل واختبار التطبيق لمدة 30 ثانية لتفعيل إقرار وتأكيد اليوم."
                                } else {
                                    "Please launch and test the app for 30 seconds to enable daily confirmation."
                                }
                            },
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = if (isCheckAllowed) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Confirm Action Button
                val isSubmitEnabled = hasConfirmedTesting && isTimerCompleted && (isAppInstalled || isSimulationMode)
                Button(
                    onClick = {
                        val finalNotes = if (customNotes.isNotBlank()) customNotes else selectedTag
                        onConfirm(selectedDuration, selectedRating, finalNotes, proofScreenshotUrl)
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
                            !isAppInstalled && !isSimulationMode -> if (isArabic) "التطبيق غير مثبت على هاتفك - ثبته أولاً" else "App not installed on device - Install first"
                            isWaitingForAppReturn -> if (isArabic) "بانتظار إكمال الـ 30 ثانية داخل التطبيق..." else "Waiting for 30 seconds inside app..."
                            isSimulationRunning -> if (isArabic) "جاري المحاكاة: $simulationSecondsRemaining ث..." else "Simulating: ${simulationSecondsRemaining}s..."
                            !isTimerCompleted -> if (isArabic) "يلزم فتح واختبار التطبيق 30 ثانية أولاً" else "30-second app test required first"
                            !hasConfirmedTesting -> if (isArabic) "يرجى تأكيد الإقرار أعلاه للمتابعة" else "Please check confirmation box above"
                            else -> if (isArabic) "توثيق وتأكيد تجربة اليوم (+25 نقطة) ✓" else "Confirm Daily Test (+25 pts) ✓"
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

private fun launchOrOpenApp(context: Context, packageName: String, optInUrl: String): Boolean {
    val pm = context.packageManager
    val launchIntent = pm.getLaunchIntentForPackage(packageName)
    return if (launchIntent != null) {
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(launchIntent)
        true
    } else {
        try {
            val url = if (optInUrl.isNotBlank()) optInUrl else "https://play.google.com/store/apps/details?id=$packageName"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            false
        }
    }
}
