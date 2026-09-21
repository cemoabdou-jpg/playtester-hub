package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.AppBuildArtifact
import com.example.data.model.UserApp
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DangerRed
import com.example.ui.theme.PlayGreen
import com.example.ui.theme.SuccessGreen

@Composable
fun VersionManagerDialog(
    app: UserApp,
    artifacts: List<AppBuildArtifact>,
    onUploadArtifact: (
        versionName: String,
        versionCode: Int,
        artifactType: String,
        fileName: String,
        fileSizeMb: Double,
        releaseNotes: String,
        setAsActive: Boolean
    ) -> Unit,
    onSetActive: (artifactId: Int) -> Unit,
    onDeleteArtifact: (artifactId: Int) -> Unit,
    onDismiss: () -> Unit
) {
    var showUploadForm by remember { mutableStateOf(false) }

    // Upload Form State
    var versionName by remember { mutableStateOf("1.0.2") }
    var versionCodeText by remember { mutableStateOf("3") }
    var artifactType by remember { mutableStateOf("AAB") } // AAB or APK
    var fileName by remember { mutableStateOf("app-release-1.0.2.aab") }
    var fileSizeText by remember { mutableStateOf("21.5") }
    var releaseNotes by remember { mutableStateOf("تحسين أداء الواجهة ودعم الشاشات اللوحية") }
    var setAsActive by remember { mutableStateOf(true) }
    var formError by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 24.dp)
                .testTag("version_manager_dialog"),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Dialog Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(PlayGreen.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Layers,
                                contentDescription = null,
                                tint = PlayGreen,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "إدارة إصدارات التطبيق",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = app.name,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("btn_close_version_dialog")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Toggle between List and Upload Form
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (showUploadForm) "رفع حزمة جديدة (AAB / APK)" else "سجل الإصدارات (${artifacts.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Button(
                        onClick = {
                            showUploadForm = !showUploadForm
                            formError = null
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (showUploadForm) MaterialTheme.colorScheme.surfaceVariant else PlayGreen,
                            contentColor = if (showUploadForm) MaterialTheme.colorScheme.onSurfaceVariant else Color.White
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("btn_toggle_upload_form")
                    ) {
                        Icon(
                            imageVector = if (showUploadForm) Icons.Default.Layers else Icons.Default.CloudUpload,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (showUploadForm) "عرض السجل" else "رفع حزمة جديدة")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (showUploadForm) {
                    // Upload Build Artifact Form
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("upload_artifact_form")
                    ) {
                        // Artifact Type Selection (AAB recommended by Play Store, APK allowed)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "نوع الحزمة:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                            FilterChip(
                                selected = artifactType == "AAB",
                                onClick = {
                                    artifactType = "AAB"
                                    fileName = fileName.replace(".apk", ".aab")
                                },
                                label = { Text("Android App Bundle (.aab)") },
                                modifier = Modifier.testTag("chip_aab")
                            )
                            FilterChip(
                                selected = artifactType == "APK",
                                onClick = {
                                    artifactType = "APK"
                                    fileName = fileName.replace(".aab", ".apk")
                                },
                                label = { Text("APK (.apk)") },
                                modifier = Modifier.testTag("chip_apk")
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = versionName,
                                onValueChange = {
                                    versionName = it
                                    fileName = "app-release-$it.${artifactType.lowercase()}"
                                },
                                label = { Text("اسم الإصدار (مثال: 1.0.2)") },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("input_version_name"),
                                shape = RoundedCornerShape(10.dp)
                            )
                            OutlinedTextField(
                                value = versionCodeText,
                                onValueChange = { versionCodeText = it },
                                label = { Text("رقم البناء (Version Code)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("input_version_code"),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = fileName,
                                onValueChange = { fileName = it },
                                label = { Text("اسم ملف الحزمة") },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1.3f)
                                    .testTag("input_artifact_filename"),
                                shape = RoundedCornerShape(10.dp)
                            )
                            OutlinedTextField(
                                value = fileSizeText,
                                onValueChange = { fileSizeText = it },
                                label = { Text("الحجم (MB)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true,
                                modifier = Modifier
                                    .weight(0.7f)
                                    .testTag("input_artifact_size"),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = releaseNotes,
                            onValueChange = { releaseNotes = it },
                            label = { Text("ملاحظات الإصدار وميزات الاختبار (Release Notes)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_release_notes"),
                            maxLines = 3,
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "تفعيل هذا الإصدار للمختبرين فوراً",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "سيصل هذا الإصدار لجميع المختبرين في مسار الاختبار المغلق",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = setAsActive,
                                onCheckedChange = { setAsActive = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = PlayGreen
                                ),
                                modifier = Modifier.testTag("switch_set_active")
                            )
                        }

                        formError?.let { err ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = err,
                                color = DangerRed,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                val vCode = versionCodeText.toIntOrNull()
                                if (versionName.isBlank() || vCode == null || vCode <= 0) {
                                    formError = "يرجى كتابة اسم إصدار ورقم بناء صحيحين"
                                    return@Button
                                }
                                val sizeMb = fileSizeText.toDoubleOrNull() ?: 18.0
                                onUploadArtifact(
                                    versionName.trim(),
                                    vCode,
                                    artifactType,
                                    fileName.trim(),
                                    sizeMb,
                                    releaseNotes.trim(),
                                    setAsActive
                                )
                                showUploadForm = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("btn_submit_artifact_upload"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PlayGreen)
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("تأكيد ورفع الحزمة لمسار الاختبار", fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    // Version History List
                    if (artifacts.isEmpty()) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Layers,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "لم يتم رفع أي حزم أو إصدارات بعد",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "اضغط على زر 'رفع حزمة جديدة' لإضافة ملف AAB أو APK وتعيينه للمختبرين.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(320.dp)
                                .testTag("artifacts_list"),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(artifacts, key = { it.id }) { artifact ->
                                ArtifactItemCard(
                                    artifact = artifact,
                                    onSetActive = { onSetActive(artifact.id) },
                                    onDelete = { onDeleteArtifact(artifact.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ArtifactItemCard(
    artifact: AppBuildArtifact,
    onSetActive: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("artifact_item_${artifact.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (artifact.isActiveForTesting) {
                PlayGreen.copy(alpha = 0.08f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            }
        ),
        border = if (artifact.isActiveForTesting) BorderStroke(1.5.dp, PlayGreen) else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (artifact.artifactType == "AAB") CyanAccent.copy(alpha = 0.2f) else AmberGold.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = artifact.artifactType,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (artifact.artifactType == "AAB") MaterialTheme.colorScheme.primary else AmberGold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "الإصدار v${artifact.versionName} (${artifact.versionCode})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (artifact.isActiveForTesting) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = PlayGreen
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "الإصدار النشط للمختبرين",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "${artifact.fileName} • ${artifact.fileSizeMb} MB • المسار: ${if (artifact.track == "CLOSED_TESTING") "الاختبار المغلق" else "الداخلي"}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (artifact.releaseNotes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "الملاحظات: ${artifact.releaseNotes}",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(8.dp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!artifact.isActiveForTesting) {
                    Button(
                        onClick = onSetActive,
                        colors = ButtonDefaults.buttonColors(containerColor = PlayGreen),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("btn_set_active_${artifact.id}")
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("تعيين كإصدار نشط", fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("btn_delete_artifact_${artifact.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "حذف الإصدار",
                        tint = DangerRed.copy(alpha = 0.8f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
