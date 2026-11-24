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
import com.example.ui.util.L

@Composable
fun ChangelogGeneratorDialog(
    app: UserApp,
    crashes: List<CrashReport>,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var isArabicNote by remember(L.currentLanguage) { mutableStateOf(L.isArabic()) }

    val resolvedCrashesTextAr = remember(crashes) {
        crashes.filter { it.status == "RESOLVED" }
            .joinToString("\n") { "• تم حل مشكلة: ${it.title} على أجهزة ${it.deviceModel}" }
    }

    val resolvedCrashesTextEn = remember(crashes) {
        crashes.filter { it.status == "RESOLVED" }
            .joinToString("\n") { "• Resolved: ${it.title} on ${it.deviceModel}" }
    }

    val arabicChangelog = remember(app, resolvedCrashesTextAr) {
        """
        🚀 تحديث جديد لتطبيق ${app.name} (الإصدار 1.0.3):
        
        ✨ أبرز التحسينات والمميزات:
        - تحسين استقرار التطبيق وسرعة الاستجابة أثناء تصفح الواجهات.
        - توافق كامل وتجربة مستخدم محسنة على أحدث أجهزة أندرويد.
        
        🛠️ الإصلاحات البرمجية المبنية على ملاحظات المختبرين:
        ${if (resolvedCrashesTextAr.isNotBlank()) resolvedCrashesTextAr else "• إصلاح أعطال طارئة وتحسين استهلاك الذاكرة"}
        - تقليل زمن التحميل بنسبة 25%.
        
        ❤️ نشكر مجتمع منصة مختبري التطبيقات على مساهماتهم القيمة!
        """.trimIndent()
    }

    val englishChangelog = remember(app, resolvedCrashesTextEn) {
        """
        🚀 What's New in ${app.name} (v1.0.3):
        
        ✨ Improvements & Features:
        - Enhanced overall app performance and smoother UI transitions.
        - Full compatibility and optimization for modern Android devices.
        
        🛠️ Bug Fixes & Tester Feedback:
        ${if (resolvedCrashesTextEn.isNotBlank()) resolvedCrashesTextEn else "• Resolved edge-case crashes and improved memory footprint"}
        - Reduced startup latency by 25%.
        
        ❤️ Special thanks to our dedicated PlayTester Hub testers community!
        """.trimIndent()
    }

    val currentText = if (isArabicNote) arabicChangelog else englishChangelog

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
                        Icon(Icons.Default.Close, contentDescription = L.t("إغلاق", "Close"))
                    }
                    Text(
                        L.t("📝 مولّد سجل التغييرات الذكي", "📝 Smart Release Notes Generator"),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                    IconButton(onClick = { isArabicNote = !isArabicNote }) {
                        Icon(Icons.Default.Translate, contentDescription = L.t("تبديل لغة السجل", "Switch Note Language"))
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        if (isArabicNote)
                            L.t("النص العربي للوحة مطوري جوجل بلاي", "Arabic Release Notes for Play Console")
                        else
                            L.t("النص الإنجليزي للوحة مطوري جوجل بلاي", "English Release Notes for Play Console"),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedButton(onClick = { isArabicNote = !isArabicNote }) {
                        Text(
                            if (isArabicNote) L.t("تبديل للإنجليزية", "Switch to English")
                            else L.t("تبديل للعربية", "Switch to Arabic"),
                            fontSize = 11.sp
                        )
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
                            val clip = ClipData.newPlainText(L.t("سجل التغييرات", "Play Console Release Notes"), currentText)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(
                                context,
                                L.t("تم نسخ النص لتقديمه في لوحة مطوري جوجل بلاي!", "Release notes copied for Play Console!"),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(L.t("نسخ للوحة المطور", "Copy Notes"))
                    }
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = onDismiss
                    ) {
                        Text(L.t("إغلاق", "Close"))
                    }
                }
            }
        }
    }
}
