package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Spellcheck
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserApp
import com.example.ui.theme.AmberGold
import com.example.ui.theme.DangerRed
import com.example.ui.theme.PlayGreen
import com.example.ui.theme.SuccessGreen
import com.example.ui.util.AppLanguage
import com.example.ui.util.LocalAppLanguage

@Composable
fun PlayPolicyLinterDialog(
    app: UserApp?,
    onDismiss: () -> Unit
) {
    val language = LocalAppLanguage.current
    val isArabic = language == AppLanguage.AR

    var titleInput by remember { mutableStateOf(app?.name ?: "") }
    var shortDescInput by remember { mutableStateOf("") }
    var fullDescInput by remember { mutableStateOf(app?.instructions ?: "") }

    // Prohibited words and patterns in Google Play Store policy
    val prohibitedPromoWords = listOf(
        "مجاني", "مجاناً", "free", "الأفضل", "الافضل", "best", "رقم 1", "#1", "top", "top 1",
        "تحميل الآن", "download now", "حصري", "exclusive", "تطبيق رقم واحد", "جديد 2026",
        "الأول في العالم", "fastest", "أسرع تطبيق"
    )

    // Emojis regex
    val emojiRegex = "[\\p{So}\\p{Cn}]".toRegex()

    // Title validation
    val titleViolations by remember(isArabic) {
        derivedStateOf {
            val list = mutableListOf<String>()
            val trimmed = titleInput.trim()
            if (trimmed.length > 30) {
                list.add(if (isArabic) "تجاوز الحد الأقصى: اسم التطبيق ${trimmed.length}/30 حرفاً (Google Play ترفض أكثر من 30 حرفاً)." else "Max character limit exceeded: Title is ${trimmed.length}/30 characters (Google Play maximum is 30).")
            }
            if (trimmed.isNotBlank() && trimmed.all { it.isUpperCase() || !it.isLetter() } && trimmed.any { it.isLetter() }) {
                list.add(if (isArabic) "استخدام الحروف الكبيرة بالكامل (ALL CAPS) ممنوع في عنوان التطبيق." else "ALL CAPS titles are strictly prohibited by Play Store policies.")
            }
            if (emojiRegex.containsMatchIn(trimmed)) {
                list.add(if (isArabic) "تحظر Google Play استخدام الرموز التعبيرية (Emojis) في عنوان التطبيق نهائياً." else "Emojis in app titles are disallowed on Google Play.")
            }
            prohibitedPromoWords.forEach { word ->
                if (trimmed.contains(word, ignoreCase = true)) {
                    list.add(if (isArabic) "كلمة ترويجية محظورة: \"$word\" ممنوعة صراحة في العنوان بموجب سياسات المتجر." else "Prohibited promotional keyword: \"$word\" in app title.")
                }
            }
            list
        }
    }

    // Short description validation
    val shortDescViolations by remember(isArabic) {
        derivedStateOf {
            val list = mutableListOf<String>()
            val trimmed = shortDescInput.trim()
            if (trimmed.length > 80) {
                list.add(if (isArabic) "تجاوز الحد الأقصى: الوصف القصير ${trimmed.length}/80 حرفاً." else "Max length exceeded: Short description is ${trimmed.length}/80 characters.")
            }
            prohibitedPromoWords.forEach { word ->
                if (trimmed.contains(word, ignoreCase = true)) {
                    list.add(if (isArabic) "كلمة تسويقية مضللة: \"$word\" قد تتسبب في تعليق فحص التطبيق." else "Misleading promotional keyword: \"$word\" can delay review.")
                }
            }
            list
        }
    }

    // Full description validation
    val fullDescViolations by remember(isArabic) {
        derivedStateOf {
            val list = mutableListOf<String>()
            val trimmed = fullDescInput.trim()
            if (trimmed.length > 4000) {
                list.add(if (isArabic) "تجاوز الحد الأقصى: الوصف الكامل ${trimmed.length}/4000 حرفاً." else "Max limit exceeded: Full description is ${trimmed.length}/4000 characters.")
            }
            if (trimmed.isNotBlank() && trimmed.length < 80) {
                list.add(if (isArabic) "الوصف الكامل قصير جداً (${trimmed.length} حرفاً) - يفضل كتابة شرح مفصل لمزايا التطبيق." else "Full description is too brief (${trimmed.length} chars) - provide detailed features description.")
            }
            if (trimmed.isNotBlank() && !trimmed.contains("@")) {
                list.add(if (isArabic) "يُنصح بإضافة بريد إلكتروني للدعم الفني والتواصل داخل الوصف." else "Recommended to include a contact support email inside description.")
            }
            list
        }
    }

    // Overall compliance score (0 - 100)
    val complianceScore by remember {
        derivedStateOf {
            var score = 100
            score -= (titleViolations.size * 25)
            score -= (shortDescViolations.size * 15)
            score -= (fullDescViolations.size * 10)
            score.coerceIn(0, 100)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Spellcheck,
                    contentDescription = null,
                    tint = if (complianceScore >= 80) SuccessGreen else if (complianceScore >= 50) AmberGold else DangerRed,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = if (isArabic) "مدقق نصوص المتجر وسياسات Google Play" else "Store Listing Policy Linter",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isArabic) "فحص الكلمات المحظورة وحدود الحروف لمنع رفض المراجعة" else "Audit metadata for prohibited keywords & character limits",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Score card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = BorderStroke(
                        1.dp,
                        if (complianceScore >= 80) SuccessGreen else if (complianceScore >= 50) AmberGold else DangerRed
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isArabic) "معدل الامتثال لسياسات المتجر:" else "Policy Compliance Score:",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "$complianceScore%",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (complianceScore >= 80) SuccessGreen else if (complianceScore >= 50) AmberGold else DangerRed
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { complianceScore / 100f },
                            modifier = Modifier.fillMaxWidth().height(8.dp),
                            color = if (complianceScore >= 80) SuccessGreen else if (complianceScore >= 50) AmberGold else DangerRed
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (complianceScore == 100)
                                (if (isArabic) "✅ نصوص متوافقة تماماً وجاهزة للنشر بـ Google Play Console دون أي مخالفة." else "✅ Fully compliant with Google Play metadata guidelines.")
                            else if (complianceScore >= 80)
                                (if (isArabic) "⚠️ هناك ملاحظات طفيفة يُفضل تعديلها لتسريع قبول المراجعة." else "⚠️ Minor suggestions to streamline review approval.")
                            else
                                (if (isArabic) "❌ تحذير: توجد كلمات أو أطوال تخالف سياسات Google Play الصريحة وستؤدي للرفض." else "❌ Warning: Policy violations detected that will trigger review rejections."),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Title Input
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isArabic) "عنوان التطبيق (App Title):" else "App Title:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${titleInput.length}/30",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (titleInput.length > 30) DangerRed else PlayGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = titleInput,
                        onValueChange = { titleInput = it },
                        placeholder = { Text(if (isArabic) "مثال: PlayTester Hub" else "e.g. PlayTester Hub") },
                        modifier = Modifier.fillMaxWidth().testTag("linter_title_input"),
                        singleLine = true,
                        isError = titleViolations.isNotEmpty()
                    )
                    titleViolations.forEach { violation ->
                        Text(
                            text = "• $violation",
                            style = MaterialTheme.typography.bodySmall,
                            color = DangerRed,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                // Short Description Input
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isArabic) "الوصف المختصر (Short Description):" else "Short Description:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${shortDescInput.length}/80",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (shortDescInput.length > 80) DangerRed else PlayGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = shortDescInput,
                        onValueChange = { shortDescInput = it },
                        placeholder = { Text(if (isArabic) "مثال: منصة متكاملة لاختبار تطبيقات أندرويد مع 20 مختبراً موثوقاً." else "e.g. Community platform for Android closed testing with verified testers.") },
                        modifier = Modifier.fillMaxWidth().testTag("linter_short_desc_input"),
                        singleLine = true,
                        isError = shortDescViolations.isNotEmpty()
                    )
                    shortDescViolations.forEach { violation ->
                        Text(
                            text = "• $violation",
                            style = MaterialTheme.typography.bodySmall,
                            color = DangerRed,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                // Full Description Input
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isArabic) "الوصف الكامل (Full Description):" else "Full Description:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${fullDescInput.length}/4000",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (fullDescInput.length > 4000) DangerRed else PlayGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = fullDescInput,
                        onValueChange = { fullDescInput = it },
                        placeholder = { Text(if (isArabic) "اكتب وصفاً مفصلاً يوضح مزايا التطبيق وميزاته للمستخدمين..." else "Detail your application features, workflows, and benefits...") },
                        modifier = Modifier.fillMaxWidth().testTag("linter_full_desc_input"),
                        minLines = 3,
                        maxLines = 6,
                        isError = fullDescViolations.isNotEmpty()
                    )
                    fullDescViolations.forEach { violation ->
                        Text(
                            text = "• $violation",
                            style = MaterialTheme.typography.bodySmall,
                            color = DangerRed,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = PlayGreen),
                modifier = Modifier.testTag("btn_close_linter_dialog")
            ) {
                Text(if (isArabic) "إغلاق" else "Close")
            }
        }
    )
}
