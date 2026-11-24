package com.example.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.util.L

@Composable
fun FeedbackDialog(
    appName: String,
    iconUrl: String,
    onDismiss: () -> Unit,
    onSubmit: (rating: Int, comment: String) -> Unit
) {
    var rating by remember { mutableStateOf(5) }
    var selectedTagIndex by remember { mutableIntStateOf(0) }
    var comment by remember { mutableStateOf("") }

    val quickTags = listOf(
        L.t("يعمل بسلاسة وبدون أي أخطاء ✓", "Runs smoothly with zero crashes ✓"),
        L.t("تصميم جذاب وسهل الاستخدام 🎨", "Attractive UI & easy to use 🎨"),
        L.t("متوافق تماماً مع أندرويد الحديث 📱", "Fully compatible with modern Android 📱"),
        L.t("لوحظ بطء طفيف في التحميل ⚡", "Slight loading delay noticed ⚡"),
        L.t("تطبيق رائع وواعد في المتجر 🌟", "Great and promising app 🌟")
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AppIconImage(
                    iconUrl = iconUrl,
                    appName = appName,
                    modifier = Modifier.size(36.dp),
                    shape = RoundedCornerShape(8.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(L.t("تقييم $appName", "Rate $appName"), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(
                        L.t("ملاحظات حقيقية للمطور لاجتياز جوجل بلاي", "Real feedback for developer to pass Google Play"),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Interactive Star Rating
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    (1..5).forEach { starIndex ->
                        IconButton(
                            onClick = { rating = starIndex },
                            modifier = Modifier.size(38.dp)
                        ) {
                            Icon(
                                if (starIndex <= rating) Icons.Default.Star else Icons.Outlined.Star,
                                contentDescription = L.t("$starIndex نجوم", "$starIndex stars"),
                                tint = if (starIndex <= rating) Color(0xFFFFB300) else MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                }

                Text(L.t("اختر ملاحظة سريعة:", "Choose quick feedback:"), fontWeight = FontWeight.Bold, fontSize = 11.sp)

                // Quick tags
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    quickTags.forEachIndexed { index, tag ->
                        val isSelected = selectedTagIndex == index
                        Surface(
                            onClick = { selectedTagIndex = index },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                tag,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text(L.t("ملاحظة إضافية (اختياري)", "Extra comment (optional)")) },
                    placeholder = { Text(L.t("اكتب رأيك للمطور هنا...", "Write your feedback here...")) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Surface(
                    color = Color(0xFFE8F5E9),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        L.t("🎁 ستحصل على +15 عملة تشجيعية فور إرسال تقييمك!", "🎁 You'll earn +15 bonus coins upon submitting feedback!"),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32),
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val chosenTag = quickTags.getOrElse(selectedTagIndex) { quickTags.first() }
                    val fullComment = if (comment.isNotBlank()) "$chosenTag - $comment" else chosenTag
                    onSubmit(rating, fullComment)
                },
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(L.t("إرسال التقييم (+15 عملة)", "Submit Rating (+15 Coins)"))
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(L.t("إلغاء", "Cancel"))
            }
        }
    )
}
