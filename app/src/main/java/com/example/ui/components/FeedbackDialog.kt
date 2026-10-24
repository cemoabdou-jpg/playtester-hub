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

@Composable
fun FeedbackDialog(
    appName: String,
    iconUrl: String,
    onDismiss: () -> Unit,
    onSubmit: (rating: Int, comment: String) -> Unit
) {
    var rating by remember { mutableStateOf(5) }
    var selectedTag by remember { mutableStateOf("يعمل بسلاسة وبدون أي أخطاء ✓") }
    var comment by remember { mutableStateOf("") }

    val quickTags = listOf(
        "يعمل بسلاسة وبدون أي أخطاء ✓",
        "تصميم جذاب وسهل الاستخدام 🎨",
        "متوافق تماماً مع أندرويد 14 📱",
        "لوحظ بطء طفيف في التحميل ⚡",
        "تطبيق رائع وواعد في المتجر 🌟"
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
                    Text("تقييم $appName", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("ملاحظات حقيقية للمطور لاجتياز Google Play", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                                contentDescription = "$starIndex نجوم",
                                tint = if (starIndex <= rating) Color(0xFFFFB300) else MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                }

                Text("اختر ملاحظة سريعة:", fontWeight = FontWeight.Bold, fontSize = 11.sp)

                // Quick tags
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    quickTags.forEach { tag ->
                        val isSelected = selectedTag == tag
                        Surface(
                            onClick = { selectedTag = tag },
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
                    label = { Text("ملاحظة إضافية (اختياري)") },
                    placeholder = { Text("اكتب رأيك للمطور هنا...") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Surface(
                    color = Color(0xFFE8F5E9),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "🎁 ستحصل على +15 عملة تشجيعية فور إرسال تقييمك!",
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
                    val fullComment = if (comment.isNotBlank()) "$selectedTag - $comment" else selectedTag
                    onSubmit(rating, fullComment)
                },
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("إرسال التقييم (+15 عملة)")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}
