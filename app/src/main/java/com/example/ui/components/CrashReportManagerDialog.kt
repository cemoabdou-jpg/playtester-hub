package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.CrashReport
import com.example.data.model.UserApp
import com.example.ui.util.L

@Composable
fun CrashReportManagerDialog(
    app: UserApp,
    crashes: List<CrashReport>,
    onResolveCrash: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var resolutionText by remember { mutableStateOf("") }
    var resolvingCrashId by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = L.t("إغلاق", "Close"))
                    }
                    Text(
                        L.t("🛠️ مركز تتبع الأعطال والانهيارات", "🛠️ Crash & ANR Tracker"),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                    IconButton(onClick = {
                        Toast.makeText(
                            context,
                            L.t("سجل الأخطاء محدث ومتزامن مع تقارير الاختبار", "Crash logs synced with test reports"),
                            Toast.LENGTH_SHORT
                        ).show()
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = L.t("تحديث", "Refresh"))
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${crashes.size}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text(L.t("إجمالي التقارير", "Total Reports"), fontSize = 11.sp, color = Color.Gray)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${crashes.count { it.status == "RESOLVED" }}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF00875A))
                            Text(L.t("تم حلها وموثقة", "Resolved & Logged"), fontSize = 11.sp, color = Color.Gray)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${crashes.count { it.status != "RESOLVED" }}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFFE53935))
                            Text(L.t("قيد الانتظار", "Pending"), fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(crashes) { crash ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(crash.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Surface(
                                        color = if (crash.status == "RESOLVED") Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            if (crash.status == "RESOLVED") L.t("تم الحل", "Resolved") else L.t("نشط", "Active"),
                                            color = if (crash.status == "RESOLVED") Color(0xFF2E7D32) else Color(0xFFC62828),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    L.t(
                                        "الجهاز: ${crash.deviceModel} | النظام: ${crash.osVersion} | النسخة: ${crash.appVersion}",
                                        "Device: ${crash.deviceModel} | OS: ${crash.osVersion} | Version: ${crash.appVersion}"
                                    ),
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(4.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        crash.stackTrace,
                                        modifier = Modifier.padding(8.dp),
                                        fontSize = 10.sp,
                                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                    )
                                }

                                if (crash.status == "RESOLVED") {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        L.t("ملاحظة الحل الموثقة: ${crash.resolutionNote}", "Resolution Note: ${crash.resolutionNote}"),
                                        fontSize = 12.sp,
                                        color = Color(0xFF00796B),
                                        fontWeight = FontWeight.Medium
                                    )
                                } else {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    if (resolvingCrashId == crash.id) {
                                        OutlinedTextField(
                                            value = resolutionText,
                                            onValueChange = { resolutionText = it },
                                            label = { Text(L.t("اكتب سبب الحل وخطوات الإصلاح", "Write resolution & fix notes")) },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Button(
                                                onClick = {
                                                    if (resolutionText.isNotBlank()) {
                                                        onResolveCrash(crash.id, resolutionText)
                                                        resolvingCrashId = null
                                                        resolutionText = ""
                                                        Toast.makeText(context, L.t("تم توثيق حل المشكلة بنجاح!", "Crash fix logged successfully!"), Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                            ) {
                                                Text(L.t("حفظ التوثيق", "Save Log"))
                                            }
                                            OutlinedButton(onClick = { resolvingCrashId = null }) {
                                                Text(L.t("إلغاء", "Cancel"))
                                            }
                                        }
                                    } else {
                                        Button(
                                            onClick = { resolvingCrashId = crash.id },
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                        ) {
                                            Icon(Icons.Default.Done, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(L.t("توثيق الحل في تقرير التدقيق", "Log Fix in Audit Report"))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onDismiss
                ) {
                    Text(L.t("إغلاق", "Close"))
                }
            }
        }
    }
}
