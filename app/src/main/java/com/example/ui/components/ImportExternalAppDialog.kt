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
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DangerRed
import com.example.ui.theme.PlayGreen
import com.example.ui.theme.SuccessGreen

@Composable
fun ImportExternalAppDialog(
    onImport: (input: String, appName: String, devName: String, category: String, onComplete: (Boolean, String) -> Unit) -> Unit,
    onDismiss: () -> Unit
) {
    var packageOrUrlInput by remember { mutableStateOf("") }
    var customAppName by remember { mutableStateOf("") }
    var developerName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("أدوات") }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val categories = listOf("أدوات", "إنتاجية", "مالية", "ألعاب", "تعليم", "نمط حياة")

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
                        text = "إضافة تطبيق مطور آخر بالرابط",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "إضافة أي تطبيق بالرابط أو اسم الحزمة ليظهر في قائمة الاختبار فوراً",
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
                            text = "💡 إذا أرسل لك مطور صديق رابط تطبيقه التجريبي أو اسم الحزمة ولم يظهر تلقائياً بالسحابة، يمكنك لصقه هنا ليُضاف فوراً لقائمتك وتبدأ اختباره وحساب الـ 14 يوماً له!",
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
                    label = { Text("رابط Google Play أو اسم الحزمة (Package Name)") },
                    placeholder = { Text("com.developer.app أو https://play.google.com/...") },
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
                                text = "تم التعرف على الحزمة: $extractedPkg",
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
                    label = { Text("اسم التطبيق") },
                    placeholder = { Text("مثال: تطبيق الملاحظات السريعة") },
                    modifier = Modifier.fillMaxWidth().testTag("input_import_app_name"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = developerName,
                    onValueChange = { developerName = it },
                    label = { Text("اسم المطور (اختياري)") },
                    placeholder = { Text("مثال: م. عمر خالد") },
                    modifier = Modifier.fillMaxWidth().testTag("input_import_dev_name"),
                    singleLine = true
                )

                Text(
                    text = "التصنيف:",
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
                Text("إلغاء")
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (extractedPkg.isBlank()) {
                        errorMessage = "يرجى إدخال اسم حزمة صالح (مثل com.example.app) أو رابط اختبار Play Store"
                        return@Button
                    }
                    val finalAppName = if (customAppName.isNotBlank()) customAppName.trim() else "تطبيق $extractedPkg"
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
                Text("إضافة وبدء الاختبار")
            }
        }
    )
}
