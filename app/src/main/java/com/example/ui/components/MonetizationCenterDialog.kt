package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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

@Composable
fun MonetizationCenterDialog(
    onDismiss: () -> Unit,
    onWatchAd: (() -> Unit)? = null
) {
    val context = LocalContext.current

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
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                    Text(
                        "💎 باقات الـ VIP والتسريع ومصادر الدخل",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300))
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF311B92))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "⚡ باقات التسريع وضمان استيفاء الـ 20 مختبراً",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "احصل على 20 مختبراً موثقاً وفعالاً بنقرة واحدة دون الحاجة لقضاء ساعات في اختبار تطبيقات الآخرين يدوياً.",
                                    color = Color(0xFFD1C4E9),
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    item {
                        PricingCard(
                            title = "باقة التسريع الماسي (VIP Express)",
                            price = "$29.99",
                            badge = "الأكثر طلباً للمطورين المستعجلين ⭐",
                            badgeColor = Color(0xFFFFB300),
                            features = listOf(
                                "توفير 20 مختبراً معتمداً خلال أقل من 12 ساعة",
                                "فحص يومي مؤكد ومضمون لـ 14 يوماً مع سجلات Check-in",
                                "تثبيت تطبيقك في أعلى القائمة الرئيسية (Featured Sticky)",
                                "توليد تقرير تدقيق رسمي PDF/CSV معتمد لـ Google Play",
                                "ضمان استبدال فوري لأي مختبر غير نشط تلقائياً"
                            ),
                            onSelect = {
                                Toast.makeText(context, "تم تفعيل باقة التسريع الماسي بنجاح على مشروعك!", Toast.LENGTH_LONG).show()
                            }
                        )
                    }

                    item {
                        PricingCard(
                            title = "باقة النمو السريع (Fast Booster)",
                            price = "$14.99",
                            badge = "توفير 50%",
                            badgeColor = Color(0xFF00875A),
                            features = listOf(
                                "10 مختبرين إضافيين معتمدين فوراً",
                                "مضاعفة ظهور التطبيق في المجتمع (2x Visibility)",
                                "فحص تنوع الأجهزة وإصدارات أندرويد",
                                "تنبيهات يومية عبر الإشعارات للمختبرين"
                            ),
                            onSelect = {
                                Toast.makeText(context, "تم تفعيل باقة النمو السريع بنجاح!", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }

                    item {
                        PricingCard(
                            title = "خدمة الفحص والتدقيق البشري الشامل (Human QA Audit)",
                            price = "$19.99",
                            badge = "خدمة احترافية",
                            badgeColor = Color(0xFF1E88E5),
                            features = listOf(
                                "تقرير شامل مكتوب من مهندس جودة وتجربة مستخدم (QA)",
                                "فحص امتثال سياسات متجر Google Play لتجنب الرفض",
                                "مراجعة ملف الخصوصية وصلاحيات الأذونات",
                                "تجهيز مسودة إجابات استبيان الإنتاج 14 يوماً"
                            ),
                            onSelect = {
                                Toast.makeText(context, "تم حجز جلسة التدقيق الشامل لمهندس الجودة!", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }

                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("🎬 كسب عملات بمشاهدة الإعلانات", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    Surface(color = Color(0xFFE8F5E9), shape = RoundedCornerShape(6.dp)) {
                                        Text("+50 نقطة", color = Color(0xFF2E7D32), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("شاهد إعلاناً قصيراً واكسب 50 نقطة فورية لمساعدتك في ترقية وتثبيت تطبيقك لجذب 20 مختبراً مجاناً!", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    modifier = Modifier.fillMaxWidth(),
                                    onClick = { onWatchAd?.invoke() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("مشاهدة إعلان مكافأة الآن (+50 نقطة)", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    item {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("🪙 شحن رصيد النقاط الفوري (Points Recharge)", fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("اشترِ نقاطاً فورية لمكافأة المختبرين دون انتظار:", fontSize = 12.sp, color = Color.Gray)
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        modifier = Modifier.weight(1f),
                                        onClick = { Toast.makeText(context, "تم شحن +250 نقطة بنجاح!", Toast.LENGTH_SHORT).show() }
                                    ) {
                                        Text("+250 نقطة ($4.99)", fontSize = 11.sp)
                                    }
                                    OutlinedButton(
                                        modifier = Modifier.weight(1f),
                                        onClick = { Toast.makeText(context, "تم شحن +600 نقطة بنجاح!", Toast.LENGTH_SHORT).show() }
                                    ) {
                                        Text("+600 نقطة ($8.99)", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("إغلاق والعودة")
                }
            }
        }
    }
}

@Composable
private fun PricingCard(
    title: String,
    price: String,
    badge: String,
    badgeColor: Color,
    features: List<String>,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(1.5.dp, badgeColor.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(price, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = badgeColor)
            }
            Surface(
                color = badgeColor.copy(alpha = 0.15f),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Text(
                    badge,
                    color = badgeColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            features.forEach { feat ->
                Row(modifier = Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF00875A), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(feat, fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onSelect,
                colors = ButtonDefaults.buttonColors(containerColor = badgeColor)
            ) {
                Text("تفعيل الآن ($price)", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}
