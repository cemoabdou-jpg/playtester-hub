package com.example.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserApp
import com.example.ui.util.L
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

@Composable
fun EditAppDetailsDialog(
    app: UserApp,
    onDismiss: () -> Unit,
    onSave: (name: String, category: String, iconUrl: String, webOptInUrl: String, googleGroupUrl: String) -> Unit
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf(app.name) }
    var category by remember { mutableStateOf(if (app.appCategory.isNotBlank()) L.localizeCategory(app.appCategory) else L.t("أدوات", "Tools")) }
    var iconUrl by remember { mutableStateOf(app.iconUrl) }
    var webOptInUrl by remember { mutableStateOf(app.webOptInUrl) }
    var googleGroupUrl by remember { mutableStateOf(app.googleGroupUrl) }
    var isSaving by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val originalBitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                if (originalBitmap != null) {
                    val scaled = Bitmap.createScaledBitmap(originalBitmap, 140, 140, true)

                    // 1. Save to local internal app files for permanent local access
                    try {
                        val iconsDir = File(context.filesDir, "app_icons").apply { mkdirs() }
                        val safePkg = app.packageName.replace(".", "_")
                        val iconFile = File(iconsDir, "$safePkg.png")
                        FileOutputStream(iconFile).use { fos ->
                            scaled.compress(Bitmap.CompressFormat.PNG, 90, fos)
                        }
                    } catch (_: Exception) {}

                    // 2. Base64 for Cloud Firestore synchronization
                    val outputStream = ByteArrayOutputStream()
                    scaled.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
                    val bytes = outputStream.toByteArray()
                    iconUrl = "data:image/jpeg;base64," + Base64.encodeToString(bytes, Base64.NO_WRAP)
                    Toast.makeText(context, L.t("تم تحديد الأيقونة بنجاح من المعرض!", "Icon selected from gallery!"), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                iconUrl = uri.toString()
                Toast.makeText(context, L.t("تم حفظ مسار الصورة", "Image path saved"), Toast.LENGTH_SHORT).show()
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.PhotoCamera,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(L.t("تعديل صورة وبيانات التطبيق", "Edit App Icon & Details"), fontWeight = FontWeight.Bold, fontSize = 17.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    L.t(
                        "يمكنك تغيير أيقونة التطبيق برفع صورة حقيقية من هاتفك أو اختيار أيقونة متجر جاهزة لتظهر لكافة المطورين في المجتمع.",
                        "Update your app icon from your phone gallery or pick a 3D store preset visible to everyone."
                    ),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Current Icon Preview & App Name
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppIconImage(
                            iconUrl = iconUrl,
                            appName = if (name.isNotBlank()) name else app.name,
                            category = category,
                            packageName = app.packageName,
                            modifier = Modifier.size(56.dp),
                            shape = RoundedCornerShape(14.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                L.t("معاينة أيقونة التطبيق الحالية", "Current App Icon Preview"),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                if (name.isNotBlank()) name else app.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                app.packageName,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // Gallery Upload Button
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(vertical = 10.dp)
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(L.t("رفع صورة جديدة من معرض الهاتف", "Upload New Image from Gallery"), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                // Store Icon Presets
                Text(L.t("أو اختر أيقونة متجر جاهزة ثلاثية الأبعاد:", "Or choose a 3D Store preset icon:"), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 2.dp)
                ) {
                    items(PRESET_APP_ICONS) { presetItem ->
                        val isSelected = iconUrl == presetItem.id
                        Surface(
                            onClick = {
                                iconUrl = presetItem.id
                                category = presetItem.getTitle()
                            },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(
                                            Brush.linearGradient(
                                                colors = listOf(presetItem.gradient[0], presetItem.gradient[1])
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        presetItem.icon,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    presetItem.getTitle(),
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                // Custom URL Input Option
                OutlinedTextField(
                    value = if (iconUrl.startsWith("data:") || iconUrl.startsWith("preset:")) "" else iconUrl,
                    onValueChange = {
                        if (it.isNotBlank()) iconUrl = it.trim()
                    },
                    label = { Text(L.t("أو رابط صورة مباشر", "Or Direct Image URL")) },
                    placeholder = { Text("https://example.com/icon.png") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                // App Name Field
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(L.t("اسم التطبيق", "App Name")) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Category Field
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text(L.t("تصنيف التطبيق", "App Category")) },
                    placeholder = { Text(L.t("مثال: ألعاب، أدوات، إنتاجية", "e.g., Games, Tools, Productivity")) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Google Play Opt-in URL
                OutlinedTextField(
                    value = webOptInUrl,
                    onValueChange = { webOptInUrl = it },
                    label = { Text(L.t("رابط الانضمام في جوجل بلاي", "Google Play Opt-in Link")) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Google Group URL
                OutlinedTextField(
                    value = googleGroupUrl,
                    onValueChange = { googleGroupUrl = it },
                    label = { Text(L.t("رابط مجموعة جوجل للمختبرين", "Testers Google Group Link")) },
                    placeholder = { Text("https://groups.google.com/g/...") },
                    supportingText = {
                        Text(
                            L.t(
                                "ضروري ليتمكن المختبرون من الانضمام بحساباتهم وتحميل التطبيق بدون أخطاء المتجر",
                                "Required so testers can join with their accounts and install without Play Store errors"
                            ),
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank()) {
                        Toast.makeText(context, L.t("يرجى إدخال اسم التطبيق", "Please enter app name"), Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    isSaving = true
                    onSave(name.trim(), category.trim(), iconUrl.trim(), webOptInUrl.trim(), googleGroupUrl.trim())
                    Toast.makeText(context, L.t("🎉 تم تحديث بيانات وأيقونة التطبيق ومزامنتها بنجاح!", "🎉 App icon & details updated & synced!"), Toast.LENGTH_LONG).show()
                },
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(L.t("حفظ التعديلات ومزامنة الأيقونة", "Save & Sync Icon"), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(L.t("إلغاء", "Cancel"))
            }
        }
    )
}
