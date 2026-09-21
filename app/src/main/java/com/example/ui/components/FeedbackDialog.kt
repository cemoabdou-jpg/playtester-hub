package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.JoinedTest
import com.example.ui.theme.AmberGold
import com.example.ui.theme.DangerRed
import com.example.ui.theme.PlayGreen

@Composable
fun FeedbackDialog(
    test: JoinedTest,
    onDismiss: () -> Unit,
    onSubmitFeedback: (content: String, rating: Int, feedbackType: String) -> Unit
) {
    var feedbackText by remember { mutableStateOf("") }
    var rating by remember { mutableIntStateOf(5) }
    var feedbackType by remember { mutableStateOf("FEEDBACK") } // "FEEDBACK" or "BUG_REPORT"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (feedbackType == "BUG_REPORT") Icons.Default.BugReport else Icons.AutoMirrored.Filled.Chat,
                    contentDescription = null,
                    tint = if (feedbackType == "BUG_REPORT") DangerRed else PlayGreen,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "تقييم وملاحظات المختبر",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "تطبيق: ${test.appName}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "يطلب Google Play وجود ملاحظات فعلية من المختبرين كدليل على اختبار التطبيق. ملاحظتك ستُسجل للمطور في تقرير الجاهزية:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Feedback Type Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = feedbackType == "FEEDBACK",
                        onClick = { feedbackType = "FEEDBACK" },
                        label = { Text("ملاحظة عامة", fontSize = 12.sp) }
                    )
                    FilterChip(
                        selected = feedbackType == "BUG_REPORT",
                        onClick = { feedbackType = "BUG_REPORT" },
                        label = { Text("إبلاغ عن عطل (Bug)", fontSize = 12.sp) }
                    )
                }

                // Star Rating
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "تقييم الاستقرار:",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Row {
                            (1..5).forEach { star ->
                                Icon(
                                    imageVector = if (star <= rating) Icons.Default.Star else Icons.Default.StarOutline,
                                    contentDescription = "$star نجوم",
                                    tint = if (star <= rating) AmberGold else MaterialTheme.colorScheme.outline,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clickable { rating = star }
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = feedbackText,
                    onValueChange = { feedbackText = it },
                    label = {
                        Text(if (feedbackType == "BUG_REPORT") "وصف المشكلة وكيفية تكرارها" else "الملاحظات أو المقترحات")
                    },
                    placeholder = {
                        Text(
                            if (feedbackType == "BUG_REPORT") "مثال: التطبيق يتوقف عند الضغط على زر التقرير في شاشة البداية..."
                            else "مثال: التطبيق سريع وواضح، التصميم ممتاز، أقترح إضافة الوضع الليلي..."
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("feedback_text_input"),
                    minLines = 3,
                    maxLines = 5
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalContent = feedbackText.ifBlank { "تطبيق ممتاز ومستقر بعد التجربة." }
                    onSubmitFeedback(finalContent, rating, feedbackType)
                },
                colors = ButtonDefaults.buttonColors(containerColor = PlayGreen),
                modifier = Modifier.testTag("submit_feedback_btn")
            ) {
                Text("إرسال وكسب +20 نقطة")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}
