package com.example.ui.components

import android.content.Context
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.material.icons.filled.Download
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
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.ui.util.AppLanguage
import com.example.ui.util.L
import com.example.ui.util.LocalAppLanguage
import com.example.util.GooglePlayScraperHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    val currentLang = LocalAppLanguage.current
    val isArabic = (currentLang == AppLanguage.AR)
    val s = L.strings()

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
    var isFetchingPlayInfo by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val clipboardManager = LocalClipboardManager.current
    var isGroupEmailCopied by remember { mutableStateOf(false) }

    var isProcessingImage by remember { mutableStateOf(false) }

    // Standard zero-permission photo picker for app icon
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri: Uri? ->
            if (uri != null) {
                isProcessingImage = true
                coroutineScope.launch(Dispatchers.IO) {
                    try {
                        val processed = AppIconProcessor.processPickedImage(context, uri, packageName)
                        withContext(Dispatchers.Main) {
                            iconUrl = processed
                            isProcessingImage = false
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            iconUrl = uri.toString()
                            isProcessingImage = false
                        }
                    }
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
                    text = if (isArabic) "إضافة تطبيقك للاختبار المغلق" else "Publish App for Closed Testing",
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
                                if (isProcessingImage) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        strokeWidth = 2.dp,
                                        color = PlayGreen
                                    )
                                } else if (iconUrl.isNotBlank()) {
                                    AppIconImage(
                                        iconUrl = iconUrl,
                                        appName = name.ifBlank { "App" },
                                        size = 48.dp,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.AddPhotoAlternate,
                                        contentDescription = if (isArabic) "تصفح المعرض" else "Pick photo",
                                        tint = PlayGreen,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Text(
                                    text = if (iconUrl.isNotBlank()) (if (isArabic) "تم تحديد أيقونة التطبيق ✓" else "App Icon Selected ✓") else (if (isArabic) "أيقونة التطبيق (اضغط لاختيار صورة)" else "App Icon (Tap to select)"),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (iconUrl.isNotBlank()) PlayGreen else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (iconUrl.isNotBlank()) (if (isArabic) "انقر لتغيير الصورة من المعرض" else "Tap to change photo") else (if (isArabic) "تصفح صور ومعرض الجهاز" else "Choose from device gallery"),
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
                                    Icon(Icons.Default.Close, contentDescription = if (isArabic) "حذف الصورة" else "Remove image", tint = DangerRed, modifier = Modifier.size(16.dp))
                                }
                            }
                            IconButton(
                                onClick = { showUrlIconInput = !showUrlIconInput },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Language, contentDescription = if (isArabic) "إدخال رابط صورة" else "Icon URL", tint = CyanAccent, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }

                if (showUrlIconInput) {
                    OutlinedTextField(
                        value = iconUrl,
                        onValueChange = { iconUrl = it.trim() },
                        label = { Text(if (isArabic) "رابط صورة الأيقونة (URL)" else "Icon URL") },
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
                    label = { Text(if (isArabic) "اسم التطبيق *" else "App Name *") },
                    placeholder = { Text(if (isArabic) "مثال: حاسبة المصاريف الذكية" else "e.g. Smart Expense Tracker") },
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
                    label = { Text(if (isArabic) "اسم الحزمة (Package Name) *" else "Package Name *") },
                    placeholder = { Text("com.developer.myapp") },
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
                            text = if (isArabic) "⚡ إنشاء روابط اختبار Google Play من اسم الحزمة" else "⚡ Auto-generate Play links from package name",
                            style = MaterialTheme.typography.labelMedium,
                            color = PlayGreen
                        )
                    }
                }

                // --- Automatic Fetch App Icon & Title from Google Play ---
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = PlayGreen.copy(alpha = 0.08f),
                    border = BorderStroke(1.dp, PlayGreen.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isArabic) "سحب صورة وبيانات التطبيق من Google Play" else "Fetch Icon & Info from Google Play",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = PlayGreen
                                )
                                Text(
                                    text = if (isArabic) "جلب أيقونة المتجر واسم التطبيق تلقائياً" else "Auto-extract the real app icon and title",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Button(
                                onClick = {
                                    val targetQuery = packageName.ifBlank { androidOptInUrl }.ifBlank { webOptInUrl }
                                    if (targetQuery.isBlank()) {
                                        errorMessage = if (isArabic) "يرجى كتابة اسم الحزمة أو رابط التطبيق أولاً" else "Please enter package name or Play URL first"
                                        return@Button
                                    }
                                    isFetchingPlayInfo = true
                                    errorMessage = null
                                    coroutineScope.launch {
                                        try {
                                            val info = GooglePlayScraperHelper.fetchAppInfo(targetQuery)
                                            if (info != null) {
                                                if (info.iconUrl.isNotBlank()) {
                                                    iconUrl = info.iconUrl
                                                }
                                                if (name.isBlank() && info.title.isNotBlank()) {
                                                    name = info.title
                                                }
                                                if (packageName.isBlank() && info.packageName.isNotBlank()) {
                                                    packageName = info.packageName
                                                }
                                                if (webOptInUrl.isBlank()) {
                                                    webOptInUrl = "https://play.google.com/apps/testing/${info.packageName}"
                                                }
                                                if (androidOptInUrl.isBlank()) {
                                                    androidOptInUrl = "https://play.google.com/store/apps/details?id=${info.packageName}"
                                                }
                                                Toast.makeText(
                                                    context,
                                                    if (isArabic) "تم سحب الأيقونة وبيانات التطبيق من متجر Google Play بنجاح! ✓" else "Fetched icon & details from Google Play! ✓",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    if (isArabic) "تعذر سحب الأيقونة تلقائياً (قد يكون التطبيق غير معلن بعد). يمكنك اختيار صورة من المعرض." else "Could not fetch public icon. You can pick an image manually.",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        } catch (e: Exception) {
                                            errorMessage = if (isArabic) "خطأ أثناء الاتصال بـ Google Play" else "Error connecting to Google Play"
                                        } finally {
                                            isFetchingPlayInfo = false
                                        }
                                    }
                                },
                                enabled = !isFetchingPlayInfo,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PlayGreen),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(34.dp).testTag("btn_fetch_play_info")
                            ) {
                                if (isFetchingPlayInfo) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(14.dp),
                                        strokeWidth = 2.dp,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (isArabic) "جاري السحب..." else "Fetching...", fontSize = 11.sp, color = Color.White)
                                } else {
                                    Icon(Icons.Default.Download, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (isArabic) "سحب من Play" else "Fetch", fontSize = 11.sp, color = Color.White)
                                }
                            }
                        }
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
                                text = if (isArabic) "مجموعة اختبار Google Group الخاصة بك:" else "Your Testing Google Group URL:",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isArabic) "أدخل رابط مجموعتك التي أنشأتها، أو اضغط زر «المجموعة المشتركة» لاستخدام المجموعة الجاهزة للمنصة:" else "Enter your custom Google Group URL or tap 'Use Shared Group' to use the hub's community group:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = googleGroupUrl,
                            onValueChange = { googleGroupUrl = it.trim(); errorMessage = null },
                            label = { Text(if (isArabic) "رابط مجموعة Google Group الخاصة بك *" else "Google Group URL *") },
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
                                Text(if (isArabic) "استخدام المجموعة المشتركة الجاهزة" else "Use Shared Group", fontSize = 11.sp, color = PlayGreen)
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
                                    Text(if (isGroupEmailCopied) (if (isArabic) "تم نسخ بريد المجموعة" else "Copied!") else (if (isArabic) "نسخ بريد المجموعة" else "Copy Email"), fontSize = 11.sp)
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
                    label = { Text(if (isArabic) "رابط اشتراك الويب (Web Opt-in Link)" else "Web Opt-in Link") },
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
                    label = { Text(if (isArabic) "رابط التطبيق على متجر Play (Android link)" else "Google Play Store Link") },
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
                    label = { Text(if (isArabic) "تعليمات للمختبرين (اختياري)" else "Instructions for Testers (Optional)") },
                    placeholder = { Text(if (isArabic) "مثال: يرجى فتح التطبيق يومياً وتجربة الميزات" else "e.g. Please test login and core features daily") },
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
                                    text = if (isArabic) "تثبيت في أعلى القائمة (مميز)" else "Pin as Featured App",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = if (isArabic) "يمنح تطبيقك أولوية الظهور للمختبرين (متاح مجاناً للجميع)" else "Gives your app higher visibility in the community market",
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
                        errorMessage = if (isArabic) "يرجى إدخال اسم التطبيق" else "Please enter app name"
                        return@Button
                    }
                    if (packageName.isBlank() || !packageName.contains(".")) {
                        errorMessage = if (isArabic) "يرجى إدخال اسم حزمة صحيح (مثال: com.example.app)" else "Please enter a valid package name (e.g. com.example.app)"
                        return@Button
                    }
                    if (googleGroupUrl.isBlank() && webOptInUrl.isBlank() && androidOptInUrl.isBlank()) {
                        errorMessage = if (isArabic) "يرجى إدخال رابط واحد على الأقل (رابط المجموعة، رابط الويب، أو رابط متجر Google Play)" else "Please provide at least one link (Group, Web Opt-in, or Store link)"
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
                Text(if (isArabic) "نشر وإضافة التطبيق" else "Publish App")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_publish_app")
            ) {
                Text(if (isArabic) "إلغاء" else "Cancel")
            }
        }
    )
}
