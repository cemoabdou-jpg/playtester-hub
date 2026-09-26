package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TabletAndroid
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserApp
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.PlayGreen
import com.example.ui.theme.SuccessGreen

@Composable
fun ChangelogGeneratorDialog(
    app: UserApp?,
    onDismiss: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current

    var fixCrashes by remember { mutableStateOf(true) }
    var speedBoost by remember { mutableStateOf(true) }
    var uiPolish by remember { mutableStateOf(true) }
    var darkModeSupport by remember { mutableStateOf(false) }
    var localizationPolish by remember { mutableStateOf(true) }
    var tabletSupport by remember { mutableStateOf(false) }
    var customNote by remember { mutableStateOf("") }

    var selectedLang by remember { mutableStateOf(0) } // 0 = Arabic, 1 = English, 2 = Bilingual
    var isCopied by remember { mutableStateOf(false) }

    fun buildArabicNotes(): String {
        return buildString {
            appendLine("🌟 ما الجديد في هذا التحديث (الإصدار التجريبي المغلق):")
            if (fixCrashes) appendLine("• معالجة الأخطاء والإغلاقات المفاجئة بناءً على ملاحظات المختبرين.")
            if (speedBoost) appendLine("• تحسين سرعة استجابة الشاشات وتقليل استهلاك موارد الذاكرة والبطارية.")
            if (uiPolish) appendLine("• تحديثات في واجهة المستخدم وتحسين تباين الألوان وأبعاد أزرار التحكم.")
            if (darkModeSupport) appendLine("• دعم الوضع الليلي الداكن التلقائي لراحة العين وتقليل إجهاد الشاشة.")
            if (localizationPolish) appendLine("• تحسين النصوص والترجمة العربية وتنسيق اتجاه الشاشة (RTL).")
            if (tabletSupport) appendLine("• تحسين التوافق مع الأجهزة اللوحية (Tablets) والهواتف القابلة للطي.")
            if (customNote.isNotBlank()) appendLine("• ${customNote.trim()}")
            appendLine("نشكر جميع المختبرين على ملاحظاتهم القيمة المستمرة!")
        }
    }

    fun buildEnglishNotes(): String {
        return buildString {
            appendLine("🌟 What's New in this Closed Testing Release:")
            if (fixCrashes) appendLine("• Fixed crashes and stability issues reported by closed testers.")
            if (speedBoost) appendLine("• Performance optimizations and reduced memory consumption.")
            if (uiPolish) appendLine("• Polished UI components and enhanced touch target accessibility.")
            if (darkModeSupport) appendLine("• Added full Dark Mode support with smooth theme transitions.")
            if (localizationPolish) appendLine("• Enhanced bilingual localization and RTL layout formatting.")
            if (tabletSupport) appendLine("• Improved responsiveness for tablets and foldable devices.")
            if (customNote.isNotBlank()) appendLine("• ${customNote.trim()}")
            appendLine("Thank you to all our testers for your continued feedback!")
        }
    }

    val generatedText = remember(fixCrashes, speedBoost, uiPolish, darkModeSupport, localizationPolish, tabletSupport, customNote, selectedLang) {
        when (selectedLang) {
            0 -> buildArabicNotes()
            1 -> buildEnglishNotes()
            else -> buildArabicNotes() + "\n---\n" + buildEnglishNotes()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Update,
                    contentDescription = null,
                    tint = PlayGreen,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "منشئ سجل التحديثات (Release Notes)",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "إثبات تفاعل وتحديث دوري لـ Google Play Console",
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
                // Info banner
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = PlayGreen.copy(alpha = 0.08f),
                    border = BorderStroke(1.dp, PlayGreen.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = PlayGreen, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "💡 توصي Google بإطلاق تحديث واحد على الأقل أثناء الـ 14 يوماً. يساعد هذا النص المقنع لجان المراجعة في التحقق من أنك قمت بحل مشاكل المختبرين فعلياً.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Text(
                    text = "حدد التحسينات التي قمت بها في هذا الإصدار:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = fixCrashes, onCheckedChange = { fixCrashes = it; isCopied = false })
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("إصلاح الأعطال والإغلاقات (Bug Fixes)", fontSize = 12.sp)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = speedBoost, onCheckedChange = { speedBoost = it; isCopied = false })
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("تحسين السرعة واستهلاك البطارية (Performance)", fontSize = 12.sp)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = uiPolish, onCheckedChange = { uiPolish = it; isCopied = false })
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("تحسينات الواجهة والتصميم (UI & Accessibility)", fontSize = 12.sp)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = darkModeSupport, onCheckedChange = { darkModeSupport = it; isCopied = false })
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("دعم الوضع الليلي الداكن (Dark Mode)", fontSize = 12.sp)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = localizationPolish, onCheckedChange = { localizationPolish = it; isCopied = false })
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("تحسين النصوص ودعم اللغة العربية (Localization)", fontSize = 12.sp)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = tabletSupport, onCheckedChange = { tabletSupport = it; isCopied = false })
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("دعم الأجهزة اللوحية (Tablets & Foldables)", fontSize = 12.sp)
                }

                OutlinedTextField(
                    value = customNote,
                    onValueChange = { customNote = it; isCopied = false },
                    label = { Text("إضافة ملاحظة إضافية مخصصة (اختياري)") },
                    placeholder = { Text("مثال: إضافة ميزة تصدير البيانات إلى ملف PDF...") },
                    modifier = Modifier.fillMaxWidth().testTag("input_custom_changelog"),
                    singleLine = true
                )

                // Language Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedLang == 0,
                        onClick = { selectedLang = 0; isCopied = false },
                        label = { Text("العربية") }
                    )
                    FilterChip(
                        selected = selectedLang == 1,
                        onClick = { selectedLang = 1; isCopied = false },
                        label = { Text("English") }
                    )
                    FilterChip(
                        selected = selectedLang == 2,
                        onClick = { selectedLang = 2; isCopied = false },
                        label = { Text("ثنائي اللغة (Bilingual)") }
                    )
                }

                // Output preview
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "النص المولد لـ Google Play Console:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Button(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(generatedText))
                                    isCopied = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = if (isCopied) SuccessGreen else PlayGreen),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.height(32.dp).testTag("btn_copy_changelog")
                            ) {
                                Icon(
                                    imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isCopied) "تم النسخ!" else "نسخ",
                                    fontSize = 11.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = generatedText,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = PlayGreen),
                modifier = Modifier.testTag("btn_close_changelog_dialog")
            ) {
                Text("إغلاق")
            }
        }
    )
}
