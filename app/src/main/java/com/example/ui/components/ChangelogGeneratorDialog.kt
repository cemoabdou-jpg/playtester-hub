package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.CrashReport
import com.example.data.model.UserApp

@Composable
fun ChangelogGeneratorDialog(
    app: UserApp,
    crashes: List<CrashReport>,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var isArabic by remember { mutableStateOf(true) }

    val resolvedCrashesText = remember(crashes) {
        crashes.filter { it.status == "RESOLVED" }
            .joinToString("\n") { "• تم حل مشكلة: ${it.title} على أجهزة ${it.deviceModel}" }
    }

    val arabicChangelog = remember(app, resolvedCrashesText) {
        """
        🚀 تحديث جديد لتطبيق ${app.name} (الإصدار v1.0.3):
        
        ✨ أبرز التحسينات والمميزات:
        - تحسين استقرار التطبيق وسرعة الاستجابة أثناء تصفح الواجهات.
        - توافق كامل وتجربة مستخدم محسنة على أحدث أجهزة أندرويد 14.
        
        🛠️ الإصلاحات البرمجية المبنية على ملاحظات المختبرين:
        ${if (resolvedCrashesText.isNotBlank()) resolvedCrashesText else "• إصلاح أعطال طارئة وتحسين استهلاك الذاكرة"}
        - تقليل زمن التحميل بنسبة 25%.
        
        ❤️ نشكر مجتمع مختبري PlayTester Hub على مساهماتهم القيمة!
        """.trimIndent()
    }

    val englishChangelog = remember(app, crashes) {
        """
        🚀 What's New in ${app.name} (v1.0.3):
        
        ✨ Improvements & Features:
        - Enhanced overall app performance and smoother UI transitions.
        - Full compatibility and optimization for Android 14.
        
        🛠️ Bug Fixes & Tester Feedback:
        - Resolved null pointer exceptions on specific devices.
        - Fixed background processing timeouts during checkout.
        - Optimized memory footprint and reduced startup latency by 25%.
        
        ❤️ Special thanks to our dedicated PlayTester Hub testers community!
        """.trimIndent()
    }

    val currentText = if (isArabic) arabicChangelog else englishChangelog

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                    Text(
                        "📝 مولّد سجل التغييرات الذكي (Release Notes)",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                    IconButton(onClick = { isArabic = !isArabic }) {
                        Icon(Icons.Default.Translate, contentDescription = "تغيير اللغة")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        if (isArabic) "النص العربي لـ Google Play Console" else "English Text for Play Console",
                        fontWeight = FontWeight.SemiBold
                    )
                    OutlinedButton(onClick = { isArabic = !isArabic }) {
                        Text(if (isArabic) "تبديل للإنجليزية (EN)" else "تبديل للعربية (AR)")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        currentText,
                        modifier = Modifier.padding(14.dp),
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Play Console Release Notes", currentText)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "تم نسخ النص لتقديمه في Google Play Console!", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("نسخ لـ Play Console")
                    }
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = onDismiss
                    ) {
                        Text("إغلاق")
                    }
                }
            }
        }
    }
}
