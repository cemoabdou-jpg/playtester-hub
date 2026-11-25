package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.clickable
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
import com.example.data.model.DailyTestLog
import com.example.data.model.UserApp
import com.example.ui.util.L

@Composable
fun TesterActivityHealthDialog(
    app: UserApp,
    logs: List<DailyTestLog>,
    onOpenTesterProfile: (email: String, name: String) -> Unit = { _, _ -> },
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val uniqueTesters = remember(logs) {
        logs.distinctBy { it.testerEmail.ifBlank { it.testerName } }
    }
    val consoleExternalGap = maxOf(0, app.currentTesters - uniqueTesters.size)

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
                        L.t("👥 صحة نشاط المختبرين والاستبدال التلقائي", "👥 Tester Activity Health & Verification"),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = {
                        val msg = if (app.currentTesters >= 20) {
                            L.t("إجمالي المختبرين المسجلين: ${app.currentTesters}/20 (${uniqueTesters.size} جلسة موثقة)", "Enrolled: ${app.currentTesters}/20 (${uniqueTesters.size} verified in-app)")
                        } else {
                            L.t("تم تسجيل ${app.currentTesters} مختبر من 20", "Logged ${app.currentTesters} of 20 active testers")
                        }
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.CheckCircle, contentDescription = L.t("فحص الحالة", "Check Status"), tint = Color(0xFF00875A))
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = if (app.currentTesters >= 20) Color(0xFF005A3C) else Color(0xFF1E3A5F))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            if (app.currentTesters >= 20) {
                                L.t(
                                    "لوحة جوجل بلاي: ${app.currentTesters}/20 • الموثقون بالمنصة: ${uniqueTesters.size}",
                                    "Play Console: ${app.currentTesters}/20 • In-App Verified: ${uniqueTesters.size}"
                                )
                            } else {
                                L.t(
                                    "المختبرون في لوحة جوجل: ${app.currentTesters} من ${app.targetTesters} (${uniqueTesters.size} موثق بالمنصة)",
                                    "Play Console Testers: ${app.currentTesters} of ${app.targetTesters} (${uniqueTesters.size} verified in-app)"
                                )
                            },
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            L.t(
                                "يعرض هذا السجل المختبرين الحقيقيين الذين قاموا بفتح التطبيق وتسجيل جلسات نشطة عبر المنصة.",
                                "Shows real testers who launched your app and logged active sessions through PlayTester Hub."
                            ),
                            color = Color(0xFFE8F5E9),
                            fontSize = 12.sp
                        )
                    }
                }

                if (consoleExternalGap > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = Color(0xFFFFF3E0),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            L.t(
                                "ملاحظة: يوجد $consoleExternalGap مختبراً إضافياً في عداد لوحة جوجل بلاي اليدوي لم يسجلوا جلساتهم عبر التطبيق بعد.",
                                "Note: $consoleExternalGap tester(s) counted from your manual Play Console entry have not logged an in-app session yet."
                            ),
                            fontSize = 11.sp,
                            color = Color(0xFFE65100),
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (uniqueTesters.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.PeopleOutline,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                L.t("لا توجد جلسات مختبرين مسجلة عبر المنصة بعد", "No in-app tester logs recorded yet"),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                L.t(
                                    "سيظهر هنا كل مختبر ينضم ويسجل جلسته اليومية لتطبيقك.",
                                    "Each tester who joins and logs a daily test session will appear here."
                                ),
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                } else {
                    LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(uniqueTesters) { tester ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onOpenTesterProfile(tester.testerEmail, tester.testerName)
                                    }
                            ) {
                                Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(L.translateKnown(tester.testerName), fontWeight = FontWeight.Bold)
                                            Text(L.t("الجهاز: ${tester.deviceModel}", "Device: ${tester.deviceModel}"), fontSize = 12.sp, color = Color.Gray)
                                            Text(
                                                L.t("آخر نشاط: اليوم (جلسة اختبار فعالة)", "Last Active: Today (Active test session)"),
                                                fontSize = 11.sp,
                                                color = Color(0xFF00875A)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Surface(
                                            color = Color(0xFFE8F5E9),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Text(
                                                L.t("نشط يومياً ✓", "Daily Active ✓"),
                                                color = Color(0xFF00796B),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Button(
                                        modifier = Modifier.fillMaxWidth(),
                                        onClick = {
                                            onOpenTesterProfile(tester.testerEmail, tester.testerName)
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(vertical = 6.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f))
                                    ) {
                                        Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            L.t("عرض حساب المختبر وتطبيقاته للاختبار المتبادل ↗", "View Tester Profile & Reciprocal Apps ↗"),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
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
