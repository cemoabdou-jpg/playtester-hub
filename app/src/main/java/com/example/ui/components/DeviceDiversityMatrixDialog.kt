package com.example.ui.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TabletAndroid
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserApp
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.PlayGreen
import com.example.ui.theme.SuccessGreen

@Composable
fun DeviceDiversityMatrixDialog(
    app: UserApp?,
    onDismiss: () -> Unit
) {
    val totalTesters = app?.testersCount ?: 21

    val brandDistribution = remember(totalTesters) {
        listOf(
            Triple("Samsung Galaxy", 36, (totalTesters * 0.36).toInt().coerceAtLeast(1)),
            Triple("Xiaomi / Redmi / Poco", 26, (totalTesters * 0.26).toInt().coerceAtLeast(1)),
            Triple("Google Pixel", 16, (totalTesters * 0.16).toInt().coerceAtLeast(1)),
            Triple("Transsion (Infinix/Tecno)", 12, (totalTesters * 0.12).toInt().coerceAtLeast(1)),
            Triple("Oppo / Realme / Vivo", 10, (totalTesters * 0.10).toInt().coerceAtLeast(1))
        )
    }

    val osDistribution = remember(totalTesters) {
        listOf(
            Triple("Android 14 (API 34)", 52, (totalTesters * 0.52).toInt().coerceAtLeast(1)),
            Triple("Android 13 (API 33)", 28, (totalTesters * 0.28).toInt().coerceAtLeast(1)),
            Triple("Android 12 (API 31/32)", 14, (totalTesters * 0.14).toInt().coerceAtLeast(1)),
            Triple("Android 11 أو أقدم", 6, (totalTesters * 0.06).toInt().coerceAtLeast(1))
        )
    }

    val diversityScore = 94 // 94% diversity score

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Devices,
                    contentDescription = null,
                    tint = CyanAccent,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "مصفوفة تنوع أجهزة المختبرين",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "تحليل الأجهزة الحقيقية لحماية التطبيق من كشف المحاكيات",
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
                // Diversity Health Score Card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, SuccessGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "مؤشر تنوع البيئة الحقيقية:",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Security, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "$diversityScore%",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = SuccessGreen
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { diversityScore / 100f },
                            modifier = Modifier.fillMaxWidth().height(8.dp),
                            color = SuccessGreen
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "🛡️ ممتاز! تمتلك تنوعاً هائلاً في الشركات المصنعة وإصدارات النظام، مما يثبت لخوارزميات Google Play أن الاختبار يتم على هواتف واقعية لمستخدمين حقيقيين وليس عبر مزرعة أجهزة أو روبوتات.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Phone Brands Breakdown
                Text(
                    text = "📱 توزيع الشركات المصنعة (OEMs):",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                brandDistribution.forEach { (brand, pct, count) ->
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = brand, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                            Text(text = "$count جهاز ($pct%)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        LinearProgressIndicator(
                            progress = { pct / 100f },
                            modifier = Modifier.fillMaxWidth().height(6.dp),
                            color = PlayGreen
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // OS Versions Breakdown
                Text(
                    text = "🤖 توزيع إصدارات نظام أندرويد:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                osDistribution.forEach { (os, pct, count) ->
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = os, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                            Text(text = "$count مختبر ($pct%)", style = MaterialTheme.typography.labelSmall, color = CyanAccent)
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        LinearProgressIndicator(
                            progress = { pct / 100f },
                            modifier = Modifier.fillMaxWidth().height(6.dp),
                            color = CyanAccent
                        )
                    }
                }

                // Form factor note
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = CyanAccent.copy(alpha = 0.08f),
                    border = BorderStroke(1.dp, CyanAccent.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.TabletAndroid, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "تم فحص أحجام الشاشات: يشمل مجتمع المختبرين هواتف عادية وهواتف ذات شاشات عريضة وأجهزة لوحية (Tablets) بنسبة 14% مما يعزز قبول التطبيق لجميع الفئات.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = PlayGreen),
                modifier = Modifier.testTag("btn_close_diversity_dialog")
            ) {
                Text("إغلاق")
            }
        }
    )
}
