package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserApp
import com.example.ui.theme.AmberGold
import com.example.ui.theme.PlayGreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ChangeAppIconDialog(
    app: UserApp,
    isArabic: Boolean = true,
    onDismiss: () -> Unit,
    onSaveIcon: (newIconUrl: String) -> Unit,
    onPullFromGooglePlay: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var currentIconUrl by remember { mutableStateOf(app.iconUrl) }
    var selectedPresetId by remember { mutableStateOf<String?>(null) }
    var showUrlField by remember { mutableStateOf(false) }
    var urlInput by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri: Uri? ->
            if (uri != null) {
                isProcessing = true
                coroutineScope.launch(Dispatchers.IO) {
                    try {
                        val processed = AppIconProcessor.processPickedImage(context, uri, app.packageName)
                        withContext(Dispatchers.Main) {
                            currentIconUrl = processed
                            selectedPresetId = null
                            isProcessing = false
                        }
                    } catch (_: Exception) {
                        withContext(Dispatchers.Main) {
                            currentIconUrl = uri.toString()
                            selectedPresetId = null
                            isProcessing = false
                        }
                    }
                }
            }
        }
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AddPhotoAlternate,
                    contentDescription = null,
                    tint = PlayGreen,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isArabic) "تخصيص أيقونة: ${app.name}" else "Customize Icon: ${app.name}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Current Preview
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(64.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isProcessing) {
                                CircularProgressIndicator(modifier = Modifier.size(32.dp), color = PlayGreen)
                            } else {
                                AppIconImage(
                                    iconUrl = currentIconUrl,
                                    appName = app.name,
                                    packageName = app.packageName,
                                    size = 64.dp,
                                    shape = RoundedCornerShape(14.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isArabic) "المعاينة الحالية" else "Current Preview",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = PlayGreen
                            )
                            Text(
                                text = if (currentIconUrl.isNotBlank())
                                    (if (isArabic) "تم تعيين صورة مخصصة بنجاح" else "Custom icon active")
                                else
                                    (if (isArabic) "أيقونة تلقائية ذكية مدمجة" else "Smart vector identity"),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                // Option 1: Gallery Photo Picker
                Button(
                    onClick = {
                        photoPickerLauncher.launch(
                            androidx.activity.result.PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PlayGreen),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_pick_gallery_icon")
                ) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isArabic) "اختيار صورة من معرض الهاتف 🖼️" else "Pick photo from gallery 🖼️",
                        fontWeight = FontWeight.Bold
                    )
                }

                // Option 2: Pull from Google Play Store
                OutlinedButton(
                    onClick = {
                        onPullFromGooglePlay()
                        onDismiss()
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudDownload,
                        contentDescription = null,
                        tint = PlayGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isArabic) "سحب الأيقونة من Google Play 🌐" else "Fetch from Google Play 🌐",
                        fontWeight = FontWeight.Bold
                    )
                }

                // Option 3: Reset to Default Badge
                if (currentIconUrl.isNotBlank()) {
                    OutlinedButton(
                        onClick = {
                            currentIconUrl = ""
                            selectedPresetId = null
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Apps,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isArabic) "استخدام الشعار الافتراضي النظيف" else "Use default clean app badge",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Option 4: URL Input Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isArabic) "إدخال رابط صورة مباشرة" else "Enter image URL",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    IconButton(onClick = { showUrlField = !showUrlField }) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = null,
                            tint = PlayGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                if (showUrlField) {
                    OutlinedTextField(
                        value = urlInput,
                        onValueChange = {
                            urlInput = it.trim()
                            if (it.isNotBlank()) {
                                currentIconUrl = it.trim()
                            }
                        },
                        label = { Text(if (isArabic) "رابط الصورة (HTTPS)" else "Image URL (HTTPS)") },
                        placeholder = { Text("https://example.com/icon.png") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSaveIcon(currentIconUrl)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = PlayGreen)
            ) {
                Text(if (isArabic) "حفظ الأيقونة ✓" else "Save Icon ✓", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(if (isArabic) "إلغاء" else "Cancel")
            }
        }
    )
}
