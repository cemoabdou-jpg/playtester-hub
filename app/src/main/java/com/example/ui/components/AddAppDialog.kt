package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.PlayGreen

@Composable
fun AddAppDialog(
    userPoints: Int,
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
    var googleGroupUrl by remember { mutableStateOf("") }
    var webOptInUrl by remember { mutableStateOf("") }
    var androidOptInUrl by remember { mutableStateOf("") }
    var instructions by remember { mutableStateOf("") }
    var isFeatured by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

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
                    text = "نشر تطبيق للاختبار المغلق",
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
                                text = "رفع التطبيقات متاح مجاناً وبدون أي شروط",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "يمكن لجميع الأعضاء المسجلين نشر تطبيقاتهم وطلب مختبرين فوراً بدون خصم أي نقاط أو اشتراطات مسبقة.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Text(
                    text = "أدخل بيانات تطبيقك من Google Play Console ليتمكن المطورون الآخرون من الانضمام لاختباره لمدة 14 يوماً.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

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

                // App Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; errorMessage = null },
                    label = { Text("اسم التطبيق *") },
                    placeholder = { Text("مثال: حاسبة المصاريف الذكية") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_app_name"),
                    singleLine = true
                )

                // Package Name
                OutlinedTextField(
                    value = packageName,
                    onValueChange = { packageName = it.trim(); errorMessage = null },
                    label = { Text("اسم الحزمة (Package Name) *") },
                    placeholder = { Text("مثال: com.developer.budgetapp") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_package_name"),
                    singleLine = true
                )

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

                // Google Group URL
                OutlinedTextField(
                    value = googleGroupUrl,
                    onValueChange = { googleGroupUrl = it.trim(); errorMessage = null },
                    label = { Text("رابط مجموعة Google Group *") },
                    placeholder = { Text("https://groups.google.com/g/...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_google_group"),
                    singleLine = true
                )

                // Web Opt-in URL
                OutlinedTextField(
                    value = webOptInUrl,
                    onValueChange = { webOptInUrl = it.trim(); errorMessage = null },
                    label = { Text("رابط الانضمام عبر الويب (Web Opt-in)") },
                    placeholder = { Text("https://play.google.com/apps/testing/...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_web_url"),
                    singleLine = true
                )

                // Android Opt-in URL
                OutlinedTextField(
                    value = androidOptInUrl,
                    onValueChange = { androidOptInUrl = it.trim(); errorMessage = null },
                    label = { Text("رابط التطبيق على متجر Play (Android link)") },
                    placeholder = { Text("https://play.google.com/store/apps/details?id=...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_android_url"),
                    singleLine = true
                )

                // Testing instructions
                OutlinedTextField(
                    value = instructions,
                    onValueChange = { instructions = it },
                    label = { Text("تعليمات للمختبرين (اختياري)") },
                    placeholder = { Text("مثال: يرجى تجربة حفظ ملف والضغط على زر التقارير") },
                    modifier = Modifier.fillMaxWidth(),
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
                    if (googleGroupUrl.isBlank()) {
                        errorMessage = "يرجى إدخال رابط مجموعة Google Group للمختبرين"
                        return@Button
                    }
                    if (webOptInUrl.isBlank() && androidOptInUrl.isBlank()) {
                        errorMessage = "يرجى إدخال أحد رابطي الاختبار (Web أو Android)"
                        return@Button
                    }

                    onConfirm(
                        name,
                        packageName,
                        category,
                        googleGroupUrl,
                        webOptInUrl,
                        androidOptInUrl,
                        instructions,
                        isFeatured
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = PlayGreen),
                modifier = Modifier.testTag("confirm_publish_app")
            ) {
                Text("نشر التطبيق (مجاناً)")
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
