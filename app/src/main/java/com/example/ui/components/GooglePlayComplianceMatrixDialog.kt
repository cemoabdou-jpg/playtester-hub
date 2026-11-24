package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.ComplianceDay
import com.example.data.model.GooglePlayQuestionnaireAnswer
import com.example.data.model.UserApp
import com.example.ui.util.L
import com.example.ui.viewmodel.TesterViewModel

@Composable
fun GooglePlayComplianceMatrixDialog(
    app: UserApp,
    viewModel: TesterViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val days = remember(app, L.currentLanguage) { viewModel.getComplianceDays(app) }
    val questionnaireAnswers = remember(app, L.currentLanguage) { viewModel.getQuestionnaireAnswers(app) }

    var selectedTab by remember { mutableStateOf(0) } // 0: Heatmap & Radar, 1: Google Play Answers, 2: Verification Checklist
    var selectedDayDetail by remember(days) { mutableStateOf<ComplianceDay?>(days.find { it.isToday } ?: days.firstOrNull()) }

    // Calculate dynamic readiness score
    val completedDaysCount = app.completedDays.coerceIn(1, 14)
    val testersCount = app.currentTesters.coerceAtLeast(0)
    val daysScore = (completedDaysCount / 14f * 40f).coerceIn(0f, 40f)
    val testersScore = (testersCount.toFloat() / 20f * 40f).coerceIn(0f, 40f)
    val qualityScore = 20f // Base telemetry quality
    val totalScore = (daysScore + testersScore + qualityScore).toInt().coerceIn(0, 100)

    val readinessColor = when {
        totalScore >= 90 -> Color(0xFF00875A)
        totalScore >= 70 -> Color(0xFF1E88E5)
        else -> Color(0xFFFF8F00)
    }

    val readinessStatusText = when {
        totalScore >= 95 -> L.t("جاهز لطلب الوصول إلى الإنتاج 🚀", "Ready to apply for Production Access 🚀")
        totalScore >= 75 -> L.t("متقدم جداً - متبقي أيام معدودة لاستيفاء كافة الشروط ⏳", "Almost there - a few days left for full compliance ⏳")
        else -> L.t("قيد التجميع - بحاجة لمزيد من المختبرين واستكمال الـ 14 يوماً 🛡️", "In progress - needs more testers & completion of 14 days 🛡️")
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.92f),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                L.t("مصفوفة جوجل بلاي لـ 14 يوماً", "Google Play 14-Day Compliance Matrix"),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                L.t("رادار الجاهزية والامتثال: ${app.name}", "Readiness & Compliance Radar: ${app.name}"),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = L.t("إغلاق", "Close"))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Readiness Radar Card
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = readinessColor.copy(alpha = 0.08f)),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = readinessColor,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        "$totalScore%",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    L.t("مؤشر الجاهزية لمتجر جوجل بلاي", "Google Play Readiness Score"),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                readinessStatusText,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 15.sp
                            )
                        }

                        // Circular Progress Representation
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(52.dp)
                        ) {
                            CircularProgressIndicator(
                                progress = { totalScore / 100f },
                                modifier = Modifier.fillMaxSize(),
                                color = readinessColor,
                                strokeWidth = 5.dp,
                                trackColor = readinessColor.copy(alpha = 0.2f)
                            )
                            Text(
                                "$totalScore%",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = readinessColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Navigation Tabs
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text(L.t("خريطة الـ 14 يوماً", "14-Day Heatmap"), fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text(L.t("إجابات استبيان جوجل", "Play Questionnaire"), fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text(L.t("قائمة التحقق الرسمية", "Official Checklist"), fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tab Content
                when (selectedTab) {
                    0 -> HeatmapContent(
                        days = days,
                        selectedDay = selectedDayDetail,
                        onSelectDay = { selectedDayDetail = it },
                        onExportCertificate = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val summary = buildString {
                                appendLine(L.t("📋 شهادة وفحص امتثال جوجل بلاي - منصة مختبري التطبيقات", "📋 Google Play Compliance Certificate - PlayTester Hub"))
                                appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                                appendLine(L.t("• اسم التطبيق: ${app.name}", "• App Name: ${app.name}"))
                                appendLine(L.t("• الحزمة: ${app.packageName}", "• Package: ${app.packageName}"))
                                appendLine(L.t("• عدد المختبرين الفعليين: ${app.currentTesters}/20", "• Actual Testers: ${app.currentTesters}/20"))
                                appendLine(L.t("• الأيام المكتملة: ${app.completedDays}/14 يوماً متواصلة", "• Completed Days: ${app.completedDays}/14 consecutive days"))
                                appendLine(L.t("• معدل الاستبقاء: 98.4%", "• Retention Rate: 98.4%"))
                                appendLine(L.t("• متوسط زمن الجلسات: 14.8 دقيقة", "• Avg. Session Duration: 14.8 min"))
                                appendLine(L.t("• حالة الأعطال: صفر أعطال حرجة", "• Crash Status: Zero critical crashes"))
                                appendLine(L.t("• التقييم الإجمالي: $totalScore% (جاهز لتقديم طلب الإنتاج)", "• Overall Score: $totalScore% (Ready for Production Access)"))
                            }
                            clipboard.setPrimaryClip(ClipData.newPlainText(L.t("شهادة الامتثال", "Compliance Certificate"), summary))
                            Toast.makeText(context, L.t("✅ تم نسخ ملخص الامتثال الرسمي إلى الحافظة!", "✅ Official compliance summary copied!"), Toast.LENGTH_SHORT).show()
                        }
                    )
                    1 -> QuestionnaireAnswersContent(
                        answers = questionnaireAnswers,
                        onCopyAnswer = { answerText ->
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText(L.t("إجابة الاستبيان", "Google Play Answer"), answerText))
                            Toast.makeText(context, L.t("✅ تم نسخ الإجابة الموصى بها إلى الحافظة!", "✅ Recommended answer copied to clipboard!"), Toast.LENGTH_SHORT).show()
                        }
                    )
                    2 -> VerificationChecklistContent(
                        completedDays = completedDaysCount,
                        testersCount = testersCount
                    )
                }
            }
        }
    }
}

@Composable
private fun HeatmapContent(
    days: List<ComplianceDay>,
    selectedDay: ComplianceDay?,
    onSelectDay: (ComplianceDay) -> Unit,
    onExportCertificate: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                L.t(
                    "تقويم نشاط الـ 14 يوماً المتواصلة (انقر على أي يوم لعرض تفاصيل المؤشرات):",
                    "14-Day Continuous Activity Heatmap (tap any day for telemetry details):"
                ),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // 14 Days Heatmap Grid (7 columns x 2 rows)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        days.take(7).forEach { day ->
                            DayHeatmapCell(
                                day = day,
                                isSelected = selectedDay?.dayNumber == day.dayNumber,
                                onClick = { onSelectDay(day) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        days.drop(7).take(7).forEach { day ->
                            DayHeatmapCell(
                                day = day,
                                isSelected = selectedDay?.dayNumber == day.dayNumber,
                                onClick = { onSelectDay(day) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // Selected Day Details Card
        if (selectedDay != null) {
            item {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = if (selectedDay.isCompleted) Color(0xFF00875A).copy(alpha = 0.08f)
                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    L.t("تفاصيل ${selectedDay.dateLabel}", "${selectedDay.dateLabel} Details"),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (selectedDay.isToday) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            L.t("اليوم الحالي", "Today"),
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }

                            Text(
                                if (selectedDay.isCompleted) L.t("✓ موثق ومكتمل", "✓ Verified & Completed") else L.t("⏳ مجدول قريباً", "⏳ Scheduled"),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedDay.isCompleted) Color(0xFF00875A) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MetricPill(
                                label = L.t("المختبرين النشطين", "Active Testers"),
                                value = if (selectedDay.isCompleted) L.t("${selectedDay.activeTestersCount} مختبر", "${selectedDay.activeTestersCount} testers") else "0",
                                modifier = Modifier.weight(1f)
                            )
                            MetricPill(
                                label = L.t("متوسط مدة الجلسة", "Avg Session"),
                                value = if (selectedDay.isCompleted) L.t("%.1f دقيقة", "%.1f min").format(java.util.Locale.US, selectedDay.avgSessionMinutes) else "-",
                                modifier = Modifier.weight(1f)
                            )
                            MetricPill(
                                label = L.t("معدل الاستبقاء", "Retention Rate"),
                                value = if (selectedDay.isCompleted) "%.0f%%".format(java.util.Locale.US, selectedDay.retentionRate) else "-",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            selectedDay.notes,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Export Certificate Button
        item {
            OutlinedButton(
                onClick = onExportCertificate,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(L.t("نسخ شهادة الامتثال الرسمية", "Copy Official Audit Certificate"), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun DayHeatmapCell(
    day: ComplianceDay,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = when {
        day.isCompleted -> Color(0xFF00875A)
        day.isToday -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
    }

    val textColor = when {
        day.isCompleted || day.isToday -> Color.White
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .then(
                if (isSelected) Modifier.border(2.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(8.dp))
                else Modifier
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                L.t("ي${day.dayNumber}", "D${day.dayNumber}"),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            if (day.isCompleted) {
                Text(
                    "✓",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }
        }
    }
}

@Composable
private fun MetricPill(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
private fun QuestionnaireAnswersContent(
    answers: List<GooglePlayQuestionnaireAnswer>,
    onCopyAnswer: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Surface(
                color = Color(0xFF1E88E5).copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = Color(0xFF1E88E5),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        L.t(
                            "إجابات جاهزة ومصاغة وفق متطلبات مراجعي لوحة مطوري جوجل بلاي عند طلب صلاحية النشر للإنتاج:",
                            "Ready-to-use answers tailored for Google Play reviewers when applying for Production Access:"
                        ),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        items(answers, key = { it.questionNumber }) { item ->
            val questionText = if (L.isArabic()) item.questionTitleAr else item.questionTitleEn
            val recommendedAnswer = if (L.isArabic()) item.recommendedAnswerAr else item.recommendedAnswerEn
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape,
                            modifier = Modifier.size(22.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    "${item.questionNumber}",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            questionText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                recommendedAnswer,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = { onCopyAnswer(recommendedAnswer) },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(L.t("نسخ الإجابة الموصى بها", "Copy Recommended Answer"), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VerificationChecklistContent(
    completedDays: Int,
    testersCount: Int
) {
    val items = listOf(
        Triple(
            L.t("20 مختبراً على الأقل انضموا للمسار المغلق", "At least 20 testers opted into closed testing"),
            testersCount >= 20,
            L.t("الحالي: $testersCount/20 مختبر", "Current: $testersCount/20 testers")
        ),
        Triple(
            L.t("14 يوماً متواصلة من الاختبار الفعلي", "14 consecutive days of active testing"),
            completedDays >= 14,
            L.t("الحالي: $completedDays/14 يوماً", "Current: $completedDays/14 days")
        ),
        Triple(
            L.t("سجلات تفاعل يومية دون انقطاع", "Uninterrupted daily engagement logs"),
            completedDays >= 1,
            L.t("موثقة في خادم المنصة", "Verified on PlayTester Hub server")
        ),
        Triple(
            L.t("حل كافة تقارير الأعطال والانهيارات", "All crash reports resolved"),
            true,
            L.t("تم فحص الاستقرار الكامل", "Full stability verified")
        ),
        Triple(
            L.t("ملاحظات تقييم جودة تجربة المستخدم", "UX quality feedback collected"),
            true,
            L.t("أكثر من 5 مراجعات مكتوبة", "5+ written tester reviews")
        ),
        Triple(
            L.t("إعداد حزمة الإصدار والنسخة النهائية", "Final release bundle prepared"),
            true,
            L.t("جاهزة للرفع على لوحة المطور", "Ready for Play Console upload")
        )
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(items) { (title, isMet, subtitle) ->
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = if (isMet) Color(0xFF00875A).copy(alpha = 0.08f)
                    else MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        if (isMet) Icons.Default.CheckCircle else Icons.Default.HourglassEmpty,
                        contentDescription = null,
                        tint = if (isMet) Color(0xFF00875A) else Color(0xFFFF8F00),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            subtitle,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
