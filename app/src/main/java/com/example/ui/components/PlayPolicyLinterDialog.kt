package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Spellcheck
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserApp
import com.example.ui.theme.AmberGold
import com.example.ui.theme.DangerRed
import com.example.ui.theme.PlayGreen
import com.example.ui.theme.SuccessGreen

@Composable
fun PlayPolicyLinterDialog(
    app: UserApp?,
    onDismiss: () -> Unit
) {
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
    val titleViolations by remember {
        derivedStateOf {
            val list = mutableListOf<String>()
            val trimmed = titleInput.trim()
            if (trimmed.length > 30) {
                list.add("تجاوز الحد الأقصى: اسم التطبيق ${trimmed.length}/30 حرفاً (Google Play ترفض أكثر من 30 حرفاً).")
            }
            if (trimmed.isNotBlank() && trimmed.all { it.isUpperCase() || !it.isLetter() } && trimmed.any { it.isLetter() }) {
                list.add("استخدام الحروف الكبيرة بالكامل (ALL CAPS) ممنوع في عنوان التطبيق.")
            }
            if (emojiRegex.containsMatchIn(trimmed)) {
                list.add("تحظر Google Play استخدام الرموز التعبيرية (Emojis) في عنوان التطبيق نهائياً.")
            }
            prohibitedPromoWords.forEach { word ->
                if (trimmed.contains(word, ignoreCase = true)) {
                    list.add("كلمة ترويجية محظورة: \"$word\" ممنوعة صراحة في العنوان بموجب سياسات المتجر.")
                }
            }
            list
        }
    }

    // Short description validation
    val shortDescViolations by remember {
        derivedStateOf {
            val list = mutableListOf<String>()
            val trimmed = shortDescInput.trim()
            if (trimmed.length > 80) {
                list.add("تجاوز الحد الأقصى: الوصف القصير ${trimmed.length}/80 حرفاً.")
            }
            prohibitedPromoWords.forEach { word ->
                if (trimmed.contains(word, ignoreCase = true)) {
                    list.add("كلمة تسويقية مضللة: \"$word\" قد تتسبب في تعليق فحص التطبيق.")
                }
            }
            list
        }
    }

    // Full description validation
    val fullDescViolations by remember {
        derivedStateOf {
            val list = mutableListOf<String>()
            val trimmed = fullDescInput.trim()
            if (trimmed.length > 4000) {
                list.add("تجاوز الحد الأقصى: الوصف الكامل ${trimmed.length}/4000 حرفاً.")
            }
            if (trimmed.isNotBlank() && trimmed.length < 80) {
                list.add("الوصف الكامل قصير جداً (${trimmed.length} حرفاً) - يفضل كتابة شرح مفصل لمزايا التطبيق.")
            }
            if (trimmed.isNotBlank() && !trimmed.contains("@")) {
                list.add("يُنصح بإضافة بريد إلكتروني للدعم الفني والتواصل داخل الوصف.")
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
                        text = "مدقق نصوص المتجر وسياسات Google Play",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "فحص الكلمات المحظورة وحدود الحروف لمنع رفض المراجعة",
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
                                text = "معدل الامتثال لسياسات المتجر:",
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
                                "✅ نصوص متوافقة تماماً وجاهزة للنشر بـ Google Play Console دون أي مخالفة."
                            else if (complianceScore >= 80)
                                "⚠️ هناك ملاحظات طفيفة يُفضل تعديلها لتسريع قبول المراجعة."
                            else
                                "❌ تحذير: توجد كلمات أو أطوال تخالف سياسات Google Play الصريحة وستؤدي للرفض.",
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
                            text = "عنوان التطبيق (App Title):",
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
                        placeholder = { Text("مثال: PlayTester Hub") },
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
                            text = "الوصف المختصر (Short Description):",
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
                        placeholder = { Text("مثال: منصة متكاملة لاختبار تطبيقات أندرويد مع 20 مختبراً موثوقاً.") },
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
                            text = "الوصف الكامل (Full Description):",
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
                        placeholder = { Text("اكتب وصفاً مفصلاً يوضح مزايا التطبيق وميزاته للمستخدمين...") },
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
                Text("إغلاق")
            }
        }
    )
}
