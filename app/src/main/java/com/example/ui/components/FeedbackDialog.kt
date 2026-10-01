package com.example.ui.components

import android.os.Build
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.JoinedTest
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DangerRed
import com.example.ui.theme.PlayGreen
import com.example.ui.util.AppLanguage
import com.example.ui.util.LocalAppLanguage

@Composable
fun FeedbackDialog(
    test: JoinedTest? = null,
    appName: String = test?.appName ?: "",
    packageName: String = test?.packageName ?: "",
    onDismiss: () -> Unit,
    onSubmitFeedback: (content: String, rating: Int, feedbackType: String, deviceModel: String, androidVersion: String) -> Unit
) {
    val language = LocalAppLanguage.current
    val isArabic = language == AppLanguage.AR

    var feedbackText by remember { mutableStateOf("") }
    var rating by remember { mutableIntStateOf(5) }
    var feedbackType by remember { mutableStateOf("FEEDBACK") } // "FEEDBACK", "BUG_REPORT", "UI_UX", "PERFORMANCE"
    var attachDeviceSpecs by remember { mutableStateOf(true) }

    val deviceModelName = remember {
        "${Build.MANUFACTURER.replaceFirstChar { it.uppercase() }} ${Build.MODEL}"
    }
    val androidVersionName = remember {
        "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})"
    }
    val deviceSpecSummary = remember {
        "$deviceModelName ($androidVersionName)"
    }

    val ratingLabels = remember(isArabic) {
        mapOf(
            1 to if (isArabic) "ضعيف / يحتاج تحسينات جذرية" else "Poor / Needs major fixes",
            2 to if (isArabic) "مقبول مع وجود مشاكل" else "Fair / Has several issues",
            3 to if (isArabic) "جيد ومناسب للاختبار" else "Good / Decent for testing",
            4 to if (isArabic) "ممتاز جداً وسلس" else "Very Good / Smooth",
            5 to if (isArabic) "استثنائي وجاهز للإنتاج 🌟" else "Exceptional / Production Ready 🌟"
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (feedbackType == "BUG_REPORT") DangerRed.copy(alpha = 0.15f) else AmberGold.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (feedbackType == "BUG_REPORT") Icons.Default.BugReport else Icons.Default.Star,
                        contentDescription = null,
                        tint = if (feedbackType == "BUG_REPORT") DangerRed else AmberGold,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = if (isArabic) "تقييم ومراجعة التطبيق ⭐" else "Rate & Review App ⭐",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    if (appName.isNotBlank()) {
                        Text(
                            text = appName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .imePadding(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Interactive Star Rating Selector
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isArabic) "حدد تقييمك العام للتطبيق:" else "Select overall star rating:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            (1..5).forEach { star ->
                                Icon(
                                    imageVector = if (star <= rating) Icons.Default.Star else Icons.Default.StarOutline,
                                    contentDescription = "$star stars",
                                    tint = if (star <= rating) AmberGold else MaterialTheme.colorScheme.outline,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clickable { rating = star }
                                        .padding(2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = ratingLabels[rating] ?: "",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (rating >= 4) PlayGreen else if (rating == 3) AmberGold else DangerRed
                        )
                    }
                }

                // Feedback Category Selector
                Text(
                    text = if (isArabic) "نوع الملاحظة / التصنيف:" else "Category:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = feedbackType == "FEEDBACK",
                        onClick = { feedbackType = "FEEDBACK" },
                        label = { Text(if (isArabic) "عام" else "General", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = feedbackType == "BUG_REPORT",
                        onClick = { feedbackType = "BUG_REPORT" },
                        label = { Text(if (isArabic) "عطل 🐛" else "Bug 🐛", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = DangerRed,
                            selectedLabelColor = Color.White
                        )
                    )
                    FilterChip(
                        selected = feedbackType == "UI_UX",
                        onClick = { feedbackType = "UI_UX" },
                        label = { Text(if (isArabic) "تصميم 🎨" else "UI/UX", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = feedbackType == "PERFORMANCE",
                        onClick = { feedbackType = "PERFORMANCE" },
                        label = { Text(if (isArabic) "أداء ⚡" else "Speed", fontSize = 11.sp) }
                    )
                }

                // Quick Review Templates
                Text(
                    text = if (isArabic) "قوالب تقييم وملاحظات سريعة:" else "Quick Review Presets:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                rating = 5
                                feedbackType = "FEEDBACK"
                                feedbackText = if (isArabic)
                                    "التطبيق ممتاز وسلس جداً، ولم أواجه أي توقف طوال فترة الفحص اليومي."
                                else
                                    "Smooth and responsive app; no crashes encountered during testing."
                            }
                    ) {
                        Row(modifier = Modifier.padding(6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PlayGreen, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isArabic) "سلس ومستقر" else "Stable & Fast", fontSize = 10.sp, fontWeight = FontWeight.Medium)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                feedbackType = "UI_UX"
                                feedbackText = if (isArabic)
                                    "التصميم أنيق وواضح، الخطوط مريحة للعين وتوزيع العناصر ممتاز."
                                else
                                    "UI design is clean and pleasant; typography and spacing are great."
                            }
                    ) {
                        Row(modifier = Modifier.padding(6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Palette, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isArabic) "واجهة أنيقة" else "Clean UI", fontSize = 10.sp, fontWeight = FontWeight.Medium)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                rating = 2
                                feedbackType = "BUG_REPORT"
                                feedbackText = if (isArabic)
                                    "واجهت بطء ملحوظ أو إغلاقاً مفاجئاً عند الانتقال بين الشاشات الرئيسية."
                                else
                                    "Experienced slight stutter or unexpected lag when navigating screens."
                            }
                    ) {
                        Row(modifier = Modifier.padding(6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.BugReport, contentDescription = null, tint = DangerRed, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isArabic) "إبلاغ عطل" else "Report Bug", fontSize = 10.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }

                // Text Input
                OutlinedTextField(
                    value = feedbackText,
                    onValueChange = { feedbackText = it },
                    label = {
                        Text(
                            if (feedbackType == "BUG_REPORT")
                                (if (isArabic) "وصف المشكلة بالتفصيل" else "Bug details")
                            else
                                (if (isArabic) "اكتب مراجعتك أو تقييمك للمطور..." else "Write review / feedback...")
                        )
                    },
                    placeholder = {
                        Text(
                            if (isArabic)
                                "اكتب تجربتك مع التطبيق، الميزات التي أعجبتك، أو أي اقتراحات للتحسين..."
                            else
                                "Share your testing experience, features you liked, or improvements..."
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("feedback_text_input"),
                    minLines = 3,
                    maxLines = 5
                )

                // Device Specs Snapshot Checkbox
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = CyanAccent.copy(alpha = 0.08f),
                    border = BorderStroke(1.dp, CyanAccent.copy(alpha = 0.25f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = attachDeviceSpecs,
                            onCheckedChange = { attachDeviceSpecs = it },
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Devices, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isArabic) "توثيق مواصفات جهازي مع التقييم:" else "Attach device info with review:",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = deviceSpecSummary,
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalContent = feedbackText.ifBlank {
                        if (isArabic) "تطبيق ممتاز ومستقر بعد التجربة." else "Stable and smooth application experience."
                    }
                    val devModel = if (attachDeviceSpecs) deviceModelName else ""
                    val androidVer = if (attachDeviceSpecs) androidVersionName else ""
                    onSubmitFeedback(finalContent, rating, feedbackType, devModel, androidVer)
                },
                colors = ButtonDefaults.buttonColors(containerColor = PlayGreen),
                modifier = Modifier.testTag("submit_feedback_btn")
            ) {
                Text(if (isArabic) "إرسال التقييم (+25 نقطة) ⭐" else "Submit (+25 XP) ⭐", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(if (isArabic) "إلغاء" else "Cancel")
            }
        }
    )
}
