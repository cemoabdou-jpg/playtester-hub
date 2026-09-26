package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Shop
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.PlayGreen

@Composable
fun AddAppDialog(
    userPoints: Int,
    officialGoogleGroupUrl: String = "https://groups.google.com/g/player-test-hup",
    officialGoogleGroupEmail: String = "player-test-hup@googlegroups.com",
    onDismiss: () -> Unit,
    onConfirm: (
        name: String,
        packageName: String,
        category: String,
        googleGroupUrl: String,
        webOptInUrl: String,
        androidOptInUrl: String,
        instructions: String,
        isFeatured: Boolean
    ) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var packageName by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("أدوات") }
    var googleGroupUrl by remember(officialGoogleGroupUrl) { mutableStateOf(officialGoogleGroupUrl) }
    var webOptInUrl by remember { mutableStateOf("") }
    var androidOptInUrl by remember { mutableStateOf("") }
    var instructions by remember { mutableStateOf("") }
    var isFeatured by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var testingMethod by remember { mutableStateOf(0) } // 0: Google Group, 1: Email List / Direct
    val clipboardManager = LocalClipboardManager.current
    var isGroupEmailCopied by remember { mutableStateOf(false) }

    fun extractPackageFromUrl(url: String): String? {
        val regexTesting = "play\\.google\\.com/apps/testing/([a-zA-Z0-9._]+)".toRegex()
        val matchTesting = regexTesting.find(url)
        if (matchTesting != null) return matchTesting.groupValues[1]

        val regexDetails = "play\\.google\\.com/store/apps/details\\?id=([a-zA-Z0-9._]+)".toRegex()
        val matchDetails = regexDetails.find(url)
        if (matchDetails != null) return matchDetails.groupValues[1]

        return null
    }

    val categories = listOf("أدوات", "إنتاجية", "مالية", "ألعاب", "تعليم", "نمط حياة")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = PlayGreen,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "إضافة تطبيقك للاختبار المغلق",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Free & Unconditional Upload Banner
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = PlayGreen.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, PlayGreen.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = PlayGreen,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "إضافة التطبيق لجمع 20 مختبراً",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "أضف قائمتك أو مجموعتك ليتمكن المطورون الآخرون من الانضمام لاختبار تطبيقك لمدة 14 يوماً.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Testing List Method Selector
                Text(
                    text = "طريقة إدارة المختبرين في Google Play Console:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = testingMethod == 0,
                        onClick = { testingMethod = 0 },
                        label = { Text("مجموعة Google Group") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = testingMethod == 1,
                        onClick = { testingMethod = 1 },
                        label = { Text("قائمة بريدية / رابط مباشر") },
                        modifier = Modifier.weight(1f)
                    )
                }

                if (errorMessage != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.errorContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = errorMessage ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

                val fieldColors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PlayGreen,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                    focusedLabelColor = PlayGreen,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
                val fieldShape = RoundedCornerShape(12.dp)

                // App Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; errorMessage = null },
                    label = { Text("اسم التطبيق *") },
                    placeholder = { Text("مثال: حاسبة المصاريف الذكية") },
                    leadingIcon = {
                        Icon(Icons.Default.Apps, contentDescription = null, tint = PlayGreen)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_app_name"),
                    shape = fieldShape,
                    colors = fieldColors,
                    singleLine = true
                )

                // Package Name
                OutlinedTextField(
                    value = packageName,
                    onValueChange = { packageName = it.trim(); errorMessage = null },
                    label = { Text("اسم الحزمة (Package Name) *") },
                    placeholder = { Text("مثال: com.developer.budgetapp") },
                    leadingIcon = {
                        Icon(Icons.Default.Android, contentDescription = null, tint = PlayGreen)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_package_name"),
                    shape = fieldShape,
                    colors = fieldColors,
                    singleLine = true
                )

                // Quick Auto-Generate Suggested Links
                if (packageName.contains(".") && (webOptInUrl.isBlank() || androidOptInUrl.isBlank())) {
                    OutlinedButton(
                        onClick = {
                            if (webOptInUrl.isBlank()) {
                                webOptInUrl = "https://play.google.com/apps/testing/$packageName"
                            }
                            if (androidOptInUrl.isBlank()) {
                                androidOptInUrl = "https://play.google.com/store/apps/details?id=$packageName"
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.fillMaxWidth().testTag("btn_auto_generate_links")
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = PlayGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "⚡ توليد روابط اختبار Google Play تلقائياً من اسم الحزمة",
                            style = MaterialTheme.typography.labelMedium,
                            color = PlayGreen
                        )
                    }
                }

                // Category selection
                Text(
                    text = "التصنيف:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.take(3).forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat) }
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.drop(3).forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat) }
                        )
                    }
                }

                // Automated Group Linking Info Card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = PlayGreen.copy(alpha = 0.08f),
                    border = BorderStroke(1.dp, PlayGreen.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = PlayGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "✅ ربط مجموعة Google Groups تلقائي بالكامل",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "رابط مجموعة المنصة مدرج تلقائياً في الأسفل. كل ما عليك في Google Play Console (المسار المغلق > المختبرون > Google Groups) هو إضافة بريد المجموعة:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = officialGoogleGroupEmail,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedButton(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(officialGoogleGroupEmail))
                                        isGroupEmailCopied = true
                                    },
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier
                                        .height(32.dp)
                                        .testTag("btn_copy_group_email_dialog")
                                ) {
                                    Icon(
                                        imageVector = if (isGroupEmailCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                                        contentDescription = "نسخ",
                                        modifier = Modifier.size(14.dp),
                                        tint = if (isGroupEmailCopied) PlayGreen else MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isGroupEmailCopied) "تم النسخ!" else "نسخ البريد",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isGroupEmailCopied) PlayGreen else MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }

                // Testers List / Group Link
                OutlinedTextField(
                    value = googleGroupUrl,
                    onValueChange = { googleGroupUrl = it.trim(); errorMessage = null },
                    label = { Text("رابط مجموعة Google Group للمختبرين (مدرج تلقائياً)") },
                    placeholder = { Text(officialGoogleGroupUrl) },
                    supportingText = {
                        Text(
                            text = if (googleGroupUrl == officialGoogleGroupUrl)
                                "⚡ تم ربط مجموعة المنصة الرسمية الموحدة تلقائياً"
                            else
                                "تم تعديل الرابط - يمكنك الضغط على زر الاستعادة للعودة للرابط الموحد",
                            color = PlayGreen
                        )
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Group, contentDescription = null, tint = PlayGreen)
                    },
                    trailingIcon = {
                        if (googleGroupUrl != officialGoogleGroupUrl) {
                            IconButton(
                                onClick = { googleGroupUrl = officialGoogleGroupUrl },
                                modifier = Modifier.testTag("btn_restore_default_group_url")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.RestartAlt,
                                    contentDescription = "استعادة رابط المجموعة الافتراضي",
                                    tint = PlayGreen
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_google_group"),
                    shape = fieldShape,
                    colors = fieldColors,
                    singleLine = true
                )

                // Web Opt-in URL
                OutlinedTextField(
                    value = webOptInUrl,
                    onValueChange = { input ->
                        val trimmed = input.trim()
                        webOptInUrl = trimmed
                        errorMessage = null
                        if (packageName.isBlank()) {
                            extractPackageFromUrl(trimmed)?.let { extracted ->
                                packageName = extracted
                            }
                        }
                    },
                    label = { Text("رابط اشتراك الويب (Web Opt-in Link)") },
                    placeholder = { Text("https://play.google.com/apps/testing/...") },
                    leadingIcon = {
                        Icon(Icons.Default.Language, contentDescription = null, tint = CyanAccent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_web_url"),
                    shape = fieldShape,
                    colors = fieldColors,
                    singleLine = true
                )

                // Android Opt-in URL
                OutlinedTextField(
                    value = androidOptInUrl,
                    onValueChange = { input ->
                        val trimmed = input.trim()
                        androidOptInUrl = trimmed
                        errorMessage = null
                        if (packageName.isBlank()) {
                            extractPackageFromUrl(trimmed)?.let { extracted ->
                                packageName = extracted
                            }
                        }
                    },
                    label = { Text("رابط التطبيق على متجر Play (Android link)") },
                    placeholder = { Text("https://play.google.com/store/apps/details?id=...") },
                    leadingIcon = {
                        Icon(Icons.Default.Shop, contentDescription = null, tint = PlayGreen)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_android_url"),
                    shape = fieldShape,
                    colors = fieldColors,
                    singleLine = true
                )

                // Testing instructions
                OutlinedTextField(
                    value = instructions,
                    onValueChange = { instructions = it },
                    label = { Text("تعليمات للمختبرين (اختياري)") },
                    placeholder = { Text("مثال: يرجى تجربة حفظ ملف والضغط على زر التقارير") },
                    leadingIcon = {
                        Icon(Icons.Default.Description, contentDescription = null, tint = AmberGold)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = fieldShape,
                    colors = fieldColors,
                    maxLines = 3
                )

                // Feature with points switch
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = AmberGold,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "تثبيت في أعلى القائمة (مميز)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "يمنح تطبيقك أولوية الظهور للمختبرين (متاح مجاناً لجميع المسجلين)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = isFeatured,
                            onCheckedChange = { isFeatured = it },
                            modifier = Modifier.testTag("switch_featured")
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank()) {
                        errorMessage = "يرجى إدخال اسم التطبيق"
                        return@Button
                    }
                    if (packageName.isBlank() || !packageName.contains(".")) {
                        errorMessage = "يرجى إدخال اسم حزمة صحيح (مثال: com.example.app)"
                        return@Button
                    }
                    if (googleGroupUrl.isBlank() && webOptInUrl.isBlank() && androidOptInUrl.isBlank()) {
                        errorMessage = "يرجى إدخال رابط واحد على الأقل (رابط المجموعة، رابط الويب، أو رابط متجر Google Play)"
                        return@Button
                    }

                    val effectiveGroupUrl = if (googleGroupUrl.isNotBlank()) {
                        googleGroupUrl
                    } else {
                        officialGoogleGroupUrl
                    }

                    onConfirm(
                        name,
                        packageName,
                        category,
                        effectiveGroupUrl,
                        webOptInUrl,
                        androidOptInUrl,
                        instructions,
                        isFeatured
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = PlayGreen),
                modifier = Modifier.testTag("confirm_publish_app")
            ) {
                Text("نشر وإضافة التطبيق")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_publish_app")
            ) {
                Text("إلغاء")
            }
        }
    )
}
