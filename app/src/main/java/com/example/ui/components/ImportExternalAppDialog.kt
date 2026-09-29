package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.AddLink
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DangerRed
import com.example.ui.theme.PlayGreen
import com.example.ui.theme.SuccessGreen
import com.example.ui.util.AppLanguage
import com.example.ui.util.LocalAppLanguage

@Composable
fun ImportExternalAppDialog(
    onImport: (input: String, appName: String, devName: String, category: String, onComplete: (Boolean, String) -> Unit) -> Unit,
    onDismiss: () -> Unit
) {
    val language = LocalAppLanguage.current
    val isArabic = language == AppLanguage.AR

    var packageOrUrlInput by remember { mutableStateOf("") }
    var customAppName by remember { mutableStateOf("") }
    var developerName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(if (isArabic) "أدوات" else "Tools") }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val categories = if (isArabic) {
        listOf("أدوات", "إنتاجية", "مالية", "ألعاب", "تعليم", "نمط حياة")
    } else {
        listOf("Tools", "Productivity", "Finance", "Games", "Education", "Lifestyle")
    }

    fun extractPackageName(text: String): String {
        val trimmed = text.trim()
        return when {
            trimmed.contains("id=") -> trimmed.substringAfter("id=").substringBefore("&")
            trimmed.contains("play.google.com/apps/testing/") -> trimmed.substringAfter("play.google.com/apps/testing/").substringBefore("?").substringBefore("/")
            trimmed.contains(".") -> trimmed
            else -> ""
        }
    }

    val extractedPkg = remember(packageOrUrlInput) {
        extractPackageName(packageOrUrlInput)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AddLink,
                    contentDescription = null,
                    tint = PlayGreen,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = if (isArabic) "إضافة تطبيق مطور آخر بالرابط" else "Import Peer App by Link/Package",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isArabic) "إضافة أي تطبيق بالرابط أو اسم الحزمة ليظهر في قائمة الاختبار فوراً" else "Import via Play Store test URL or package name",
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
                        Icon(Icons.Default.Info, contentDescription = null, tint = PlayGreen, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isArabic)
                                "💡 إذا أرسل لك مطور صديق رابط تطبيقه التجريبي أو اسم الحزمة ولم يظهر تلقائياً بالسحابة، يمكنك لصقه هنا ليُضاف فوراً لقائمتك وتبدأ اختباره وحساب الـ 14 يوماً له!"
                            else
                                "💡 Paste any peer developer's opt-in link or package name to manually add it to your testing list and start counting 14-day testing days!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Input
                OutlinedTextField(
                    value = packageOrUrlInput,
                    onValueChange = {
                        packageOrUrlInput = it
                        errorMessage = null
                        if (customAppName.isBlank() && it.contains(".")) {
                            val pkg = extractPackageName(it)
                            if (pkg.isNotBlank()) {
                                customAppName = pkg.substringAfterLast(".").replaceFirstChar { char -> char.uppercase() }
                            }
                        }
                    },
                    label = { Text(if (isArabic) "رابط Google Play أو اسم الحزمة (Package Name)" else "Play Store Link or Package Name") },
                    placeholder = { Text("com.developer.app or https://play.google.com/...") },
                    modifier = Modifier.fillMaxWidth().testTag("input_import_package_url"),
                    singleLine = true
                )

                if (extractedPkg.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isArabic) "تم التعرف على الحزمة: $extractedPkg" else "Package Identified: $extractedPkg",
                                style = MaterialTheme.typography.labelSmall,
                                color = SuccessGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = customAppName,
                    onValueChange = { customAppName = it; errorMessage = null },
                    label = { Text(if (isArabic) "اسم التطبيق" else "App Name") },
                    placeholder = { Text(if (isArabic) "مثال: تطبيق الملاحظات السريعة" else "e.g. Quick Notes Pro") },
                    modifier = Modifier.fillMaxWidth().testTag("input_import_app_name"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = developerName,
                    onValueChange = { developerName = it },
                    label = { Text(if (isArabic) "اسم المطور (اختياري)" else "Developer Name (Optional)") },
                    placeholder = { Text(if (isArabic) "مثال: م. عمر خالد" else "e.g. John Doe") },
                    modifier = Modifier.fillMaxWidth().testTag("input_import_dev_name"),
                    singleLine = true
                )

                Text(
                    text = if (isArabic) "التصنيف:" else "Category:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.take(4).forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat, fontSize = 11.sp) }
                        )
                    }
                }

                errorMessage?.let { err ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = DangerRed.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, DangerRed),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = DangerRed, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = err, color = DangerRed, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(if (isArabic) "إلغاء" else "Cancel")
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (extractedPkg.isBlank()) {
                        errorMessage = if (isArabic)
                            "يرجى إدخال اسم حزمة صالح (مثل com.example.app) أو رابط اختبار Play Store"
                        else
                            "Please enter a valid package name (e.g. com.example.app) or Play Store test link"
                        return@Button
                    }
                    val finalAppName = if (customAppName.isNotBlank()) customAppName.trim() else (if (isArabic) "تطبيق $extractedPkg" else "App $extractedPkg")
                    isLoading = true
                    errorMessage = null
                    onImport(packageOrUrlInput, finalAppName, developerName, selectedCategory) { success, msg ->
                        isLoading = false
                        if (success) {
                            onDismiss()
                        } else {
                            errorMessage = msg
                        }
                    }
                },
                enabled = !isLoading && packageOrUrlInput.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = PlayGreen),
                modifier = Modifier.testTag("btn_confirm_import_app")
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Text(if (isArabic) "إضافة وبدء الاختبار" else "Import & Start Test")
            }
        }
    )
}
