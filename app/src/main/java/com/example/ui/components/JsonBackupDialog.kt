package com.example.ui.components

import android.content.Intent
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
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DangerRed
import com.example.ui.theme.PlayGreen
import com.example.ui.theme.SuccessGreen
import com.example.ui.util.AppLanguage
import com.example.ui.util.LocalAppLanguage

@Composable
fun JsonBackupDialog(
    exportedJson: String,
    onRestoreJson: (String) -> Pair<Boolean, String>,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val language = LocalAppLanguage.current
    val isArabic = language == AppLanguage.AR

    var selectedTab by remember { mutableStateOf(0) } // 0 = Export, 1 = Restore
    var isCopied by remember { mutableStateOf(false) }
    var restoreInputJson by remember { mutableStateOf("") }
    var restoreResult by remember { mutableStateOf<Pair<Boolean, String>?>(null) }

    fun shareJsonFile() {
        try {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, exportedJson)
                putExtra(Intent.EXTRA_SUBJECT, "PlayTester_Hub_Backup.json")
                type = "text/plain"
            }
            context.startActivity(Intent.createChooser(sendIntent, if (isArabic) "مشاركة النسخة الاحتياطية" else "Share Backup File"))
        } catch (_: Exception) {}
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Backup,
                    contentDescription = null,
                    tint = PlayGreen,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = if (isArabic) "النسخ الاحتياطي واستعادة البيانات (JSON)" else "Data Backup & Restore (JSON)",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isArabic) "حفظ رصيد النقاط، التطبيقات المنشورة، وسجلات الـ 14 يوماً" else "Export or restore apps, points, and 14-day logs",
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
                // Tabs: Export vs Restore
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        label = { Text(if (isArabic) "تصدير نسخة (Export)" else "Export Backup") },
                        leadingIcon = { Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                    FilterChip(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        label = { Text(if (isArabic) "استعادة نسخة (Restore)" else "Restore Backup") },
                        leadingIcon = { Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                }

                if (selectedTab == 0) {
                    // Export Section
                    Text(
                        text = if (isArabic) "النسخة الاحتياطية الحالية جاهزة للحفظ والتصدير:" else "Current snapshot is ready to copy or share:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = exportedJson.take(300) + "\n...",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                lineHeight = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(exportedJson))
                                isCopied = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = if (isCopied) SuccessGreen else PlayGreen),
                            modifier = Modifier.weight(1f).testTag("btn_copy_json_backup"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isCopied) (if (isArabic) "تم النسخ!" else "Copied!") else (if (isArabic) "نسخ النص" else "Copy JSON"))
                        }

                        OutlinedButton(
                            onClick = { shareJsonFile() },
                            modifier = Modifier.weight(1f).testTag("btn_share_json_backup"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isArabic) "مشاركة ملف" else "Share File")
                        }
                    }
                } else {
                    // Restore Section
                    Text(
                        text = if (isArabic) "الصق محتوى ملف النسخة الاحتياطية (JSON) هنا لاستعادة بياناتك فوراً:" else "Paste your backup JSON snippet below to restore your state:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = restoreInputJson,
                        onValueChange = { restoreInputJson = it; restoreResult = null },
                        placeholder = { Text("{\"version\": 1, \"apps\": [...], \"wallet\": {...}}") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .testTag("input_restore_json"),
                        minLines = 4,
                        maxLines = 8
                    )

                    Button(
                        onClick = {
                            if (restoreInputJson.isNotBlank()) {
                                restoreResult = onRestoreJson(restoreInputJson)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                        modifier = Modifier.fillMaxWidth().testTag("btn_execute_restore"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.FileUpload, contentDescription = null, tint = androidx.compose.ui.graphics.Color.Black)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isArabic) "بدء استعادة البيانات الآن" else "Restore Data Now", color = androidx.compose.ui.graphics.Color.Black, fontWeight = FontWeight.Bold)
                    }

                    restoreResult?.let { (success, message) ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (success) SuccessGreen.copy(alpha = 0.12f) else DangerRed.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, if (success) SuccessGreen else DangerRed),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (success) Icons.Default.CheckCircle else Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = if (success) SuccessGreen else DangerRed,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = message,
                                    color = if (success) SuccessGreen else DangerRed,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = PlayGreen),
                modifier = Modifier.testTag("btn_close_backup_dialog")
            ) {
                Text(if (isArabic) "إغلاق" else "Close")
            }
        }
    )
}
