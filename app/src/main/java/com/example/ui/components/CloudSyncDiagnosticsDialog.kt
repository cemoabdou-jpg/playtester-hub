package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Refresh
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
import com.example.ui.util.AppLanguage
import com.example.ui.util.LocalAppLanguage

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
    val language = LocalAppLanguage.current
    val isArabic = language == AppLanguage.AR

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
                        text = if (isArabic) "فاحص ومرشد المزامنة السحابية" else "Cloud Sync Diagnostics & Health",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isArabic) "تشخيص اتصال Firebase Firestore وحل مشكلة ظهور التطبيقات" else "Inspect Firebase connectivity and real-time app sync",
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
                            Text(if (isArabic) "جاري فحص الاتصال وقواعد الصلاحيات في Firebase..." else "Checking Firebase connectivity & security rules...", style = MaterialTheme.typography.bodyMedium)
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
                                    text = if (allGood)
                                        (if (isArabic) "السيرفر السحابي متصل ويعمل بكفاءة!" else "Cloud database is connected and active!")
                                    else
                                        (if (isArabic) "تم اكتشاف عائق في صلاحيات السحابة" else "Cloud permissions issue detected"),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (allGood) SuccessGreen else DangerRed
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Diagnostic items
                            DiagnosticStatusRow(
                                title = if (isArabic) "تسجيل الدخول في Firebase" else "Firebase Authentication",
                                isOk = diagnosticResult.authStatus == "AUTHENTICATED",
                                detail = diagnosticResult.authMessage
                            )

                            DiagnosticStatusRow(
                                title = if (isArabic) "صلاحيات قراءة التطبيقات (Read)" else "Read Permissions (Read)",
                                isOk = diagnosticResult.canRead,
                                detail = if (diagnosticResult.canRead)
                                    (if (isArabic) "مسموحة - تم العثور على (${diagnosticResult.remoteAppsCount}) تطبيق في السيرفر" else "Granted - Found (${diagnosticResult.remoteAppsCount}) apps in cloud")
                                else
                                    (if (isArabic) "محظورة: ${diagnosticResult.readError ?: "Permission Denied"}" else "Denied: ${diagnosticResult.readError ?: "Permission Denied"}")
                            )

                            DiagnosticStatusRow(
                                title = if (isArabic) "صلاحيات رفع التطبيقات (Write)" else "Write Permissions (Write)",
                                isOk = diagnosticResult.canWrite,
                                detail = if (diagnosticResult.canWrite)
                                    (if (isArabic) "مسموحة - يمكن للمطورين نشر تطبيقاتهم بنجاح" else "Granted - Developers can publish apps seamlessly")
                                else
                                    (if (isArabic) "محظورة: ${diagnosticResult.writeError ?: "Permission Denied"}" else "Denied: ${diagnosticResult.writeError ?: "Permission Denied"}")
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
                                        text = if (isArabic) "لماذا لم تظهر تطبيقات المطورين الآخرين؟" else "Why aren't apps showing up?",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (isArabic)
                                        "1. عندما يضيف أي مطور تطبيقه، إذا كانت قواعد فايربيس (Firestore Rules) تمنع الكتابة أو لم يتم تفعيل تسجيل الدخول المجهول، يُحفظ التطبيق على هاتفه محلياً فقط ولا يرتفع للسحابة.\n2. لحل المشكلة نهائياً ولتظهر تطبيقات جميع المطورين فوراً، اتبع الخطوات السريعة بالأسفل (تستغرق دقيقة واحدة في Firebase Console)."
                                    else
                                        "1. If Firestore Rules restrict writes or Anonymous Auth is inactive, apps remain in local cache only.\n2. To resolve this and sync all apps globally, enable Anonymous Authentication and apply open Firestore rules in Firebase Console.",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = if (isArabic) "🛠️ خطوات الإصلاح في Firebase Console (مشروع playtester-hub-61bd6):" else "🛠️ Quick Fix in Firebase Console (playtester-hub-61bd6):",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = PlayGreen
                                )

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (isArabic)
                                        "الخطوة 1: ادخل إلى Authentication ⬅️ تبويب Sign-in method ⬅️ فعّل خيار Anonymous (مجهول).\nالخطوة 2: ادخل إلى Firestore Database ⬅️ تبويب Rules ⬅️ الصق القواعد التالية واضغط Publish:"
                                    else
                                        "Step 1: Go to Authentication ➔ Sign-in method ➔ Enable 'Anonymous'.\nStep 2: Go to Firestore Database ➔ Rules ➔ Paste the rules snippet below and hit Publish:",
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
                                            Text(if (isArabic) "قواعد Firestore المفتوحة:" else "Open Firestore Rules:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                            IconButton(
                                                onClick = {
                                                    clipboardManager.setText(AnnotatedString(firestoreRulesSnippet))
                                                    isRulesCopied = true
                                                },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = if (isRulesCopied) Icons.Default.CheckCircle else Icons.Default.ContentCopy,
                                                    contentDescription = if (isArabic) "نسخ القواعد" else "Copy Rules",
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
                                text = if (isArabic) "مزامنة ورفع تطبيقاتي المحفوظة محلياً 🚀" else "Sync Local Apps to Cloud 🚀",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isArabic)
                                "إذا كنت قد أضفت تطبيقاتك سابقاً ولم تكن السحابة متصلة، اضغط هنا لرفع كل تطبيقاتك دفعة واحدة لتظهر فوراً للمطورين الآخرين!"
                            else
                                "If you published apps previously while offline, tap below to re-upload all local apps directly to the cloud market!",
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
                                Text(if (isArabic) "جاري الرفع للسيرفر السحابي..." else "Uploading to cloud database...", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            } else {
                                Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isArabic) "رفع تطبيقاتي المحلية إلى السحابة الآن" else "Upload Local Apps to Cloud Now", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        uploadStatusMessage?.let { msg ->
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = msg,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (msg.contains("نجاح") || msg.contains("success", ignoreCase = true)) SuccessGreen else DangerRed,
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
                    Text(if (isArabic) "إعادة الفحص الآن 🔄" else "Re-check Now 🔄", fontSize = 12.sp)
                }

                Button(
                    onClick = onSyncApps,
                    colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("btn_dialog_sync_apps")
                ) {
                    Text(if (isArabic) "مزامنة ☁️" else "Sync ☁️", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(if (isArabic) "إغلاق" else "Close")
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
