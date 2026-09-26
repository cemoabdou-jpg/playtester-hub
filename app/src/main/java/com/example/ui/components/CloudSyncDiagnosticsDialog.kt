package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CloudDiagnosticResult
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DangerRed
import com.example.ui.theme.PlayGreen
import com.example.ui.theme.SuccessGreen

@Composable
fun CloudSyncDiagnosticsDialog(
    diagnosticResult: CloudDiagnosticResult?,
    isLoading: Boolean,
    isUploadingLocalApps: Boolean,
    onRunDiagnostic: () -> Unit,
    onSyncApps: () -> Unit,
    onUploadLocalApps: ((Boolean, String) -> Unit) -> Unit,
    onDismiss: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    var isRulesCopied by remember { mutableStateOf(false) }
    var uploadStatusMessage by remember { mutableStateOf<String?>(null) }

    val firestoreRulesSnippet = """rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if true;
    }
  }
}"""

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CloudSync,
                    contentDescription = null,
                    tint = PlayGreen,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "فاحص ومرشد المزامنة السحابية",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "تشخيص اتصال Firebase Firestore وحل مشكلة ظهور التطبيقات",
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
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Status Box
                if (isLoading) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = PlayGreen, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("جاري فحص الاتصال وقواعد الصلاحيات في Firebase...", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                } else if (diagnosticResult != null) {
                    val allGood = diagnosticResult.canRead && diagnosticResult.canWrite && diagnosticResult.authStatus == "AUTHENTICATED"

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (allGood) SuccessGreen.copy(alpha = 0.1f) else DangerRed.copy(alpha = 0.1f),
                        border = BorderStroke(1.dp, if (allGood) SuccessGreen else DangerRed),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (allGood) Icons.Default.CheckCircle else Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = if (allGood) SuccessGreen else DangerRed,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (allGood) "السيرفر السحابي متصل ويعمل بكفاءة!" else "تم اكتشاف عائق في صلاحيات السحابة",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (allGood) SuccessGreen else DangerRed
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Diagnostic items
                            DiagnosticStatusRow(
                                title = "تسجيل الدخول في Firebase",
                                isOk = diagnosticResult.authStatus == "AUTHENTICATED",
                                detail = diagnosticResult.authMessage
                            )

                            DiagnosticStatusRow(
                                title = "صلاحيات قراءة التطبيقات (Read)",
                                isOk = diagnosticResult.canRead,
                                detail = if (diagnosticResult.canRead) "مسموحة - تم العثور على (${diagnosticResult.remoteAppsCount}) تطبيق في السيرفر" else "محظورة: ${diagnosticResult.readError ?: "Permission Denied"}"
                            )

                            DiagnosticStatusRow(
                                title = "صلاحيات رفع التطبيقات (Write)",
                                isOk = diagnosticResult.canWrite,
                                detail = if (diagnosticResult.canWrite) "مسموحة - يمكن للمطورين نشر تطبيقاتهم بنجاح" else "محظورة: ${diagnosticResult.writeError ?: "Permission Denied"}"
                            )
                        }
                    }

                    // Explanation & Solution Card
                    if (!allGood || diagnosticResult.remoteAppsCount == 0) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.HelpOutline, contentDescription = null, tint = AmberGold, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "لماذا لم تظهر تطبيقات المطورين الآخرين؟",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "1. عندما يضيف أي مطور تطبيقه، إذا كانت قواعد فايربيس (Firestore Rules) تمنع الكتابة أو لم يتم تفعيل تسجيل الدخول المجهول، يُحفظ التطبيق على هاتفه محلياً فقط ولا يرتفع للسحابة.\n2. لحل المشكلة نهائياً ولتظهر تطبيقات جميع المطورين فوراً، اتبع الخطوات السريعة بالأسفل (تستغرق دقيقة واحدة في Firebase Console).",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "🛠️ خطوات الإصلاح في Firebase Console (مشروع playtester-hub-61bd6):",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = PlayGreen
                                )

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "الخطوة 1: ادخل إلى Authentication ⬅️ تبويب Sign-in method ⬅️ فعّل خيار Anonymous (مجهول).\nالخطوة 2: ادخل إلى Firestore Database ⬅️ تبويب Rules ⬅️ الصق القواعد التالية واضغط Publish:",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 11.sp
                                )

                                Spacer(modifier = Modifier.height(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("قواعد Firestore المفتوحة:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                            IconButton(
                                                onClick = {
                                                    clipboardManager.setText(AnnotatedString(firestoreRulesSnippet))
                                                    isRulesCopied = true
                                                },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = if (isRulesCopied) Icons.Default.CheckCircle else Icons.Default.ContentCopy,
                                                    contentDescription = "نسخ القواعد",
                                                    tint = if (isRulesCopied) SuccessGreen else PlayGreen,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                        Text(
                                            text = firestoreRulesSnippet,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.horizontalScroll(rememberScrollState())
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Bulk Re-upload Section
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = CyanAccent.copy(alpha = 0.08f),
                    border = BorderStroke(1.dp, CyanAccent.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "مزامنة ورفع تطبيقاتي المحفوظة محلياً 🚀",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "إذا كنت قد أضفت تطبيقاتك سابقاً ولم تكن السحابة متصلة، اضغط هنا لرفع كل تطبيقاتك دفعة واحدة لتظهر فوراً للمطورين الآخرين!",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                onUploadLocalApps { _, msg ->
                                    uploadStatusMessage = msg
                                }
                            },
                            enabled = !isUploadingLocalApps,
                            colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().testTag("btn_reupload_local_apps")
                        ) {
                            if (isUploadingLocalApps) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.Black, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("جاري الرفع للسيرفر السحابي...", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            } else {
                                Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("رفع تطبيقاتي المحلية إلى السحابة الآن", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        uploadStatusMessage?.let { msg ->
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = msg,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (msg.contains("نجاح")) SuccessGreen else DangerRed,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onRunDiagnostic,
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = PlayGreen),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("btn_rerun_cloud_diagnostics")
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("إعادة الفحص الآن 🔄", fontSize = 12.sp)
                }

                Button(
                    onClick = onSyncApps,
                    colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("btn_dialog_sync_apps")
                ) {
                    Text("مزامنة ☁️", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("إغلاق")
            }
        }
    )
}

@Composable
private fun DiagnosticStatusRow(
    title: String,
    isOk: Boolean,
    detail: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = if (isOk) Icons.Default.CheckCircle else Icons.Default.Error,
            contentDescription = null,
            tint = if (isOk) SuccessGreen else DangerRed,
            modifier = Modifier.size(16.dp).padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = detail,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
