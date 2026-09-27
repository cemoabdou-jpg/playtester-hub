package com.example.ui.components

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Shop
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DangerRed
import com.example.ui.theme.PlayGreen
import java.io.File

@Composable
fun AddAppDialog(
    userPoints: Int,
    officialGoogleGroupUrl: String = "https://groups.google.com/g/player-test-hup",
    officialGoogleGroupEmail: String = "player-test-hup@googlegroups.com",
    onDismiss: () -> Unit,
    onConfirm: (
        name: String,
        packageName: String,
        iconUrl: String,
        googleGroupUrl: String,
        webOptInUrl: String,
        androidOptInUrl: String,
        instructions: String,
        isFeatured: Boolean
    ) -> Unit
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var packageName by remember { mutableStateOf("") }
    var iconUrl by remember { mutableStateOf("") }
    var googleGroupUrl by remember { mutableStateOf("") }
    var webOptInUrl by remember { mutableStateOf("") }
    var androidOptInUrl by remember { mutableStateOf("") }
    var instructions by remember { mutableStateOf("") }
    var isFeatured by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showUrlIconInput by remember { mutableStateOf(false) }

    val clipboardManager = LocalClipboardManager.current
    var isGroupEmailCopied by remember { mutableStateOf(false) }

    // Standard zero-permission photo picker for app icon
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri: Uri? ->
            if (uri != null) {
                try {
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val outputFile = File(context.filesDir, "app_icon_${System.currentTimeMillis()}.png")
                    outputFile.outputStream().use { out ->
                        inputStream?.copyTo(out)
                    }
                    iconUrl = outputFile.absolutePath
                } catch (e: Exception) {
                    iconUrl = uri.toString()
                }
            }
        }
    )

    fun extractPackageFromUrl(url: String): String? {
        val regexTesting = "play\\.google\\.com/apps/testing/([a-zA-Z0-9._]+)".toRegex()
        val matchTesting = regexTesting.find(url)
        if (matchTesting != null) return matchTesting.groupValues[1]

        val regexDetails = "play\\.google\\.com/store/apps/details\\?id=([a-zA-Z0-9._]+)".toRegex()
        val matchDetails = regexDetails.find(url)
        if (matchDetails != null) return matchDetails.groupValues[1]

        return null
    }

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

                // 1. Compact App Icon / Gallery Picker (Space-efficient)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (iconUrl.isNotBlank()) MaterialTheme.colorScheme.surface else PlayGreen.copy(alpha = 0.15f))
                                    .testTag("btn_pick_app_icon"),
                                contentAlignment = Alignment.Center
                            ) {
                                if (iconUrl.isNotBlank()) {
                                    AsyncImage(
                                        model = if (iconUrl.startsWith("/")) File(iconUrl) else iconUrl,
                                        contentDescription = "أيقونة التطبيق",
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(RoundedCornerShape(10.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.AddPhotoAlternate,
                                        contentDescription = "تصفح المعرض",
                                        tint = PlayGreen,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Text(
                                    text = if (iconUrl.isNotBlank()) "تم تحديد أيقونة التطبيق ✓" else "أيقونة التطبيق (اضغط لاختيار صورة)",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (iconUrl.isNotBlank()) PlayGreen else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (iconUrl.isNotBlank()) "انقر لتغيير الصورة من المعرض" else "تصفح صور ومعرض الجهاز",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (iconUrl.isNotBlank()) {
                                IconButton(
                                    onClick = { iconUrl = "" },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "حذف الصورة", tint = DangerRed, modifier = Modifier.size(16.dp))
                                }
                            }
                            IconButton(
                                onClick = { showUrlIconInput = !showUrlIconInput },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Language, contentDescription = "إدخال رابط صورة", tint = CyanAccent, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }

                if (showUrlIconInput) {
                    OutlinedTextField(
                        value = iconUrl,
                        onValueChange = { iconUrl = it.trim() },
                        label = { Text("رابط صورة الأيقونة (URL)") },
                        placeholder = { Text("https://example.com/icon.png") },
                        singleLine = true,
                        shape = fieldShape,
                        colors = fieldColors,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // 2. App Name
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

                // 3. Package Name
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
                        Icon(Icons.Default.Link, contentDescription = null, tint = PlayGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "⚡ إنشاء روابط اختبار Google Play من اسم الحزمة",
                            style = MaterialTheme.typography.labelMedium,
                            color = PlayGreen
                        )
                    }
                }

                // 4. Testing Google Group URL (Customizable per Developer)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Group, contentDescription = null, tint = PlayGreen, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "مجموعة اختبار Google Group الخاصة بك:",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "أدخل رابط مجموعتك التي أنشأتها، أو اضغط زر «المجموعة المشتركة» لاستخدام المجموعة الجاهزة للمنصة:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = googleGroupUrl,
                            onValueChange = { googleGroupUrl = it.trim(); errorMessage = null },
                            label = { Text("رابط مجموعة Google Group الخاصة بك *") },
                            placeholder = { Text("https://groups.google.com/g/your-testers-group") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_google_group"),
                            shape = fieldShape,
                            colors = fieldColors,
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = { googleGroupUrl = officialGoogleGroupUrl },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Icon(Icons.Default.Group, contentDescription = null, tint = PlayGreen, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("استخدام المجموعة المشتركة الجاهزة", fontSize = 11.sp, color = PlayGreen)
                            }

                            if (googleGroupUrl == officialGoogleGroupUrl) {
                                OutlinedButton(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(officialGoogleGroupEmail))
                                        isGroupEmailCopied = true
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isGroupEmailCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                                        contentDescription = null,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (isGroupEmailCopied) "تم نسخ بريد المجموعة" else "نسخ بريد المجموعة", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }

                // 5. Web Opt-in URL
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

                // 6. Android Opt-in URL
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

                // 7. Testing instructions
                OutlinedTextField(
                    value = instructions,
                    onValueChange = { instructions = it },
                    label = { Text("تعليمات للمختبرين (اختياري)") },
                    placeholder = { Text("مثال: يرجى فتح التطبيق يومياً وتجربة الميزات") },
                    leadingIcon = {
                        Icon(Icons.Default.Description, contentDescription = null, tint = AmberGold)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = fieldShape,
                    colors = fieldColors,
                    maxLines = 3
                )

                // 8. Feature switch
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
                                text = "يمنح تطبيقك أولوية الظهور للمختبرين (متاح مجاناً للجميع)",
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
                        iconUrl,
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
