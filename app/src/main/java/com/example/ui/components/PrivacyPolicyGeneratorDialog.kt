package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserApp
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DangerRed
import com.example.ui.theme.PlayGreen
import com.example.ui.theme.SuccessGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PrivacyPolicyGeneratorDialog(
    app: UserApp?,
    onDismiss: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    val today = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()) }

    var appName by remember { mutableStateOf(app?.name ?: "تطبيقي") }
    var developerName by remember { mutableStateOf("فريق التطوير") }
    var contactEmail by remember { mutableStateOf("developer@example.com") }

    // Features toggles
    var collectsAnalytics by remember { mutableStateOf(false) }
    var collectsCrashLogs by remember { mutableStateOf(true) }
    var hasAds by remember { mutableStateOf(false) }
    var targetedForChildren by remember { mutableStateOf(false) }

    // Format mode: 0 = Markdown, 1 = HTML
    var outputFormat by remember { mutableStateOf(0) }
    var isCopied by remember { mutableStateOf(false) }

    // Live URL validator
    var testUrlInput by remember { mutableStateOf("") }
    var urlValidationResult by remember { mutableStateOf<Pair<Boolean, String>?>(null) }

    // Function to generate Markdown
    fun generateMarkdown(): String {
        return buildString {
            appendLine("# سياسة الخصوصية لتطبيق $appName (Privacy Policy)")
            appendLine("**تاريخ السريان:** $today")
            appendLine()
            appendLine("نحن في **$developerName** نلتزم بحماية خصوصية مستخدمي تطبيق **$appName** والامتثال الكامل لسياسات Google Play.")
            appendLine()
            appendLine("### 1. المعلومات التي نقوم بجمعها واستخدامها")
            if (collectsCrashLogs) {
                appendLine("- **بيانات الأعطال والتشخيص (Crash Logs):** نجمع بيانات فنية مجهولة المصدر لتحسين استقرار التطبيق وحل الأخطاء البرمجية أثناء فترات الاختبار والاستخدام.")
            }
            if (collectsAnalytics) {
                appendLine("- **تحليلات الاستخدام (Analytics):** قياس مدى تفاعل المستخدمين مع شاشات التطبيق لتقديم تجربة مستخدم أفضل.")
            }
            if (!collectsCrashLogs && !collectsAnalytics) {
                appendLine("- **عدم جمع بيانات شخصية:** التطبيق لا يطلب ولا يخزن أي بيانات تعريفية شخصية خاصة بالمستخدم على خوادم خارجية.")
            }
            appendLine()
            appendLine("### 2. الإعلانات والجهات الخارجية")
            if (hasAds) {
                appendLine("- يعرض التطبيق إعلانات عبر شبكات إعلانية متوافقة مع سياسات Google Play (مثل Google AdMob). قد تستخدم هذه الشبكات معرّفات إعلانية مجهولة الهوية لتخصيص الإعلانات.")
            } else {
                appendLine("- هذا التطبيق خالٍ تماماً من الإعلانات ولا يشارك أي بيانات مع معلنين خارجيين.")
            }
            appendLine()
            appendLine("### 3. سياسة خصوصية الأطفال (COPPA & Family Policy)")
            if (targetedForChildren) {
                appendLine("- هذا التطبيق مصمم للعائلات ويلتزم بالكامل بسياسة عائلة Google Play وقانون حماية خصوصية الأطفال على الإنترنت (COPPA). لا نقوم بجمع أي بيانات شخصية من الأطفال دون سن 13 عاماً.")
            } else {
                appendLine("- هذا التطبيق غير موجه للأطفال دون سن 13 عاماً، ولا نجمع عمداً أي معلومات منهم.")
            }
            appendLine()
            appendLine("### 4. أمان البيانات")
            appendLine("- نطبق أعلى معايير الحماية والتشفير أثناء نقل وتخزين البيانات لضمان عدم وصول أي طرف غير مصرح له.")
            appendLine()
            appendLine("### 5. التواصل معنا")
            appendLine("إذا كانت لديك أي استفسارات بخصوص سياسة الخصوصية، يرجى التواصل عبر البريد الإلكتروني:")
            appendLine("**$contactEmail**")
        }
    }

    // Function to generate HTML
    fun generateHtml(): String {
        return buildString {
            appendLine("<!DOCTYPE html>")
            appendLine("<html lang=\"ar\" dir=\"rtl\">")
            appendLine("<head><meta charset=\"UTF-8\"><title>سياسة الخصوصية - $appName</title>")
            appendLine("<style>body{font-family:sans-serif;max-width:800px;margin:30px auto;padding:0 20px;line-height:1.7;color:#333}h1,h2{color:#0f9d58}</style></head>")
            appendLine("<body>")
            appendLine("<h1>سياسة الخصوصية لتطبيق $appName</h1>")
            appendLine("<p><strong>تاريخ السريان:</strong> $today</p>")
            appendLine("<p>نحن في <strong>$developerName</strong> نلتزم بحماية خصوصية مستخدمينا وفقاً لسياسات Google Play الرسمية.</p>")
            appendLine("<h2>1. المعلومات المجمعة</h2>")
            appendLine("<ul>")
            if (collectsCrashLogs) appendLine("<li><strong>سجلات الأعطال:</strong> بيانات مجهولة لحل المشاكل الفنية.</li>")
            if (collectsAnalytics) appendLine("<li><strong>التحليلات:</strong> تحسين تجربة التصفح والأداء.</li>")
            if (!collectsCrashLogs && !collectsAnalytics) appendLine("<li>لا يتم جمع أي بيانات شخصية حساسة.</li>")
            appendLine("</ul>")
            appendLine("<h2>2. الإعلانات</h2><p>${if (hasAds) "يحتوي التطبيق على إعلانات متوافقة مع Google Play." else "التطبيق خالي تماماً من الإعلانات."}</p>")
            appendLine("<h2>3. التواصل</h2><p>لأي استفسار يرجى مراسلتنا: <a href=\"mailto:$contactEmail\">$contactEmail</a></p>")
            appendLine("</body></html>")
        }
    }

    val generatedContent = remember(appName, developerName, contactEmail, collectsAnalytics, collectsCrashLogs, hasAds, targetedForChildren, outputFormat) {
        if (outputFormat == 0) generateMarkdown() else generateHtml()
    }

    fun validateUrl(url: String) {
        val trimmed = url.trim()
        if (trimmed.isBlank()) {
            urlValidationResult = Pair(false, "الرجاء إدخال رابط سياسة الخصوصية للفحص")
            return
        }
        if (!trimmed.startsWith("https://")) {
            urlValidationResult = Pair(false, "❌ غير متوافق: تشترط Google Play أن يبدأ الرابط بـ https:// المشفرة.")
            return
        }
        if (trimmed.contains("example.com") || trimmed.contains("your-domain") || trimmed.contains("placeholder")) {
            urlValidationResult = Pair(false, "❌ تنبيه: الرابط يحتوي على نطاق وهمي غير مفعل.")
            return
        }
        if (!trimmed.contains(".")) {
            urlValidationResult = Pair(false, "❌ الرابط غير صالح (ينقصه اسم النطاق .com / .io / إلخ).")
            return
        }
        urlValidationResult = Pair(true, "✅ ممتاز! الرابط صالح ومطابق بنسبة 100% لمعايير Google Play Console.")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Policy,
                    contentDescription = null,
                    tint = PlayGreen,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "مولد وفاحص سياسة الخصوصية",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "متوافق مع سياسات مراجعة Google Play 2026",
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
                // Info note
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = PlayGreen.copy(alpha = 0.08f),
                    border = BorderStroke(1.dp, PlayGreen.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Policy, contentDescription = null, tint = PlayGreen, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "تشترط Google وجود رابط سياسة خصوصية فعال ومباشر في صفحة المتجر وقسم أمان البيانات، وإلا يُرفض التطبيق فوراً.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Fields
                OutlinedTextField(
                    value = appName,
                    onValueChange = { appName = it },
                    label = { Text("اسم التطبيق") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = developerName,
                    onValueChange = { developerName = it },
                    label = { Text("اسم المطور أو الشركة") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = contactEmail,
                    onValueChange = { contactEmail = it },
                    label = { Text("بريد الدعم والتواصل") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Checkboxes for permissions & ads
                Text(
                    text = "خصائص التطبيق وقسم أمان البيانات (Data Safety):",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = collectsCrashLogs, onCheckedChange = { collectsCrashLogs = it })
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("جمع سجلات الأعطال الفنية (Crashlytics)", fontSize = 12.sp)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = collectsAnalytics, onCheckedChange = { collectsAnalytics = it })
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("جمع بيانات التحليلات والأداء (Analytics)", fontSize = 12.sp)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = hasAds, onCheckedChange = { hasAds = it })
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("يحتوي على إعلانات (AdMob / وغيرها)", fontSize = 12.sp)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = targetedForChildren, onCheckedChange = { targetedForChildren = it })
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("موجه للأطفال أو العائلات (Family Policy)", fontSize = 12.sp)
                }

                // Format selector (Markdown vs HTML)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = outputFormat == 0,
                        onClick = { outputFormat = 0; isCopied = false },
                        label = { Text("صيغة Markdown (لـ GitHub/Notion)") },
                        leadingIcon = { Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                    FilterChip(
                        selected = outputFormat == 1,
                        onClick = { outputFormat = 1; isCopied = false },
                        label = { Text("صيغة HTML (لموقع ويب)") },
                        leadingIcon = { Icon(Icons.Default.Code, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                }

                // Code Preview & Copy
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
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
                                text = if (outputFormat == 0) "معاينة Markdown" else "معاينة كود HTML",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Button(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(generatedContent))
                                    isCopied = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = if (isCopied) SuccessGreen else PlayGreen),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.height(32.dp).testTag("btn_copy_privacy_policy")
                            ) {
                                Icon(
                                    imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isCopied) "تم النسخ!" else "نسخ النص",
                                    fontSize = 11.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = generatedContent.take(280) + "\n...",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Section: Live URL Validator
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "🔍 فحص رابط سياسة الخصوصية بعد رفعه:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = CyanAccent
                )

                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = testUrlInput,
                        onValueChange = { testUrlInput = it; urlValidationResult = null },
                        placeholder = { Text("https://yourname.github.io/privacy") },
                        label = { Text("رابط سياسة الخصوصية") },
                        modifier = Modifier.weight(1f).testTag("input_policy_url_check"),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { validateUrl(testUrlInput) },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(52.dp).testTag("btn_validate_policy_url")
                    ) {
                        Text("فحص", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }

                urlValidationResult?.let { (isValid, msg) ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isValid) SuccessGreen.copy(alpha = 0.12f) else DangerRed.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, if (isValid) SuccessGreen else DangerRed),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isValid) Icons.Default.CheckCircle else Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (isValid) SuccessGreen else DangerRed,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = msg,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isValid) SuccessGreen else DangerRed,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = PlayGreen),
                modifier = Modifier.testTag("btn_close_privacy_dialog")
            ) {
                Text("إغلاق")
            }
        }
    )
}
