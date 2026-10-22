package com.example.ui.components

import android.content.Intent
import android.widget.Toast
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
fun DeveloperGrowthHubDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) }

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
                        "🚀 مركز جذب المطورين وشبكة التبادل",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                    IconButton(onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "انضم لشبكة مطوري PlayTester Hub")
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "انضم لمنصة PlayTester Hub واكسب 100 نقطة مجانية لاختبار تطبيقك عبر رابط الدعوة الخاص بي: https://playtesterhub.app/invite/dev_guest_01"
                            )
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "مشاركة دعوة المطورين"))
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "دعوة مطورين")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                TabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("دعوة المطورين") }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("عقود التبادل 1v1") }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("مجتمع المطورين") }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                when (selectedTab) {
                    0 -> ReferralSection(context)
                    1 -> MutualTestingSection(context)
                    2 -> CommunityGuildSection()
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("تم والعودة للرئيسية")
                }
            }
        }
    }
}

@Composable
private fun ReferralSection(context: android.content.Context) {
    LazyColumn(modifier = Modifier.fillMaxHeight(0.75f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E88E5))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "🎁 برنامج إحالة المطورين (Developer Referral)",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "لكل مطور جديد يسجل عبر رابطك ويضيف تطبيقه للاختبار، تحصل فوراً على +100 نقطة مجانية كافية لجلب مختبرين لتطبيقك!",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 13.sp
                    )
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("رابط الدعوة المخصص لك:", fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "https://playtesterhub.app/invite/dev_guest_01",
                            modifier = Modifier.padding(12.dp),
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                            val clip = android.content.ClipData.newPlainText("Invite Link", "https://playtesterhub.app/invite/dev_guest_01")
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "تم نسخ رابط الدعوة للحافظة بنجاح!", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("نسخ رابط الدعوة")
                    }
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("إحصائيات إحالاتك:", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        StatBox("5", "مطورين مدعوين")
                        StatBox("+500", "نقاط مكتسبة")
                        StatBox("المستوى 2", "رتبة السفير")
                    }
                }
            }
        }
    }
}

@Composable
private fun MutualTestingSection(context: android.content.Context) {
    LazyColumn(modifier = Modifier.fillMaxHeight(0.75f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF00796B))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        "🤝 عقود التبادل المتبادل المضمونة (1-to-1 Guaranteed Alliance)",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "اتفاقية ملزمة بينك وبين مطور آخر: يختبر تطبيقك لمدة 14 يوماً وتختبر تطبيقه بالتوازي مع نظام إشعار بالالتزام اليومي.",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 12.sp
                    )
                }
            }
        }

        val alliances = listOf(
            Triple("مطور تطبيق GymMaster", "تطبيق اللياقة وبناء الأجسام", "متوفر للمبادلة الفورية"),
            Triple("مطور تطبيق ChefNotes", "مكتبة وصفات الطهي والمطاعم", "يبحث عن 5 مطورين للتبادل"),
            Triple("مطور تطبيق AudioBooker", "مشغل الكتب الصوتية والبودكاست", "تبادل متبادل نشط")
        )

        items(alliances.size) { index ->
            val item = alliances[index]
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(item.first, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(item.second, fontSize = 12.sp, color = Color.Gray)
                        Text(item.third, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
                    }
                    Button(
                        onClick = {
                            Toast.makeText(context, "تم إرسال طلب عقد التبادل للمطور بنجاح!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("عقد تبادل")
                    }
                }
            }
        }
    }
}

@Composable
private fun CommunityGuildSection() {
    LazyColumn(modifier = Modifier.fillMaxHeight(0.75f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("🏆 لوحة شرف المطورين الأكثر تفاعلاً (Top Developers):", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    DeveloperRow("1. م. أحمد خليل", "32 تطبيقاً تم اعتماده", "1,850 نقطة")
                    DeveloperRow("2. ستوديو السحاب للبرمجيات", "28 تطبيقاً تم اعتماده", "1,600 نقطة")
                    DeveloperRow("3. د. عمر الشامي", "21 تطبيقاً تم اعتماده", "1,240 نقطة")
                }
            }
        }
    }
}

@Composable
private fun DeveloperRow(name: String, apps: String, points: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(name, fontWeight = FontWeight.Medium)
            Text(apps, fontSize = 11.sp, color = Color.Gray)
        }
        Text(points, color = Color(0xFF00875A), fontWeight = FontWeight.Bold)
    }
    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
}

@Composable
private fun StatBox(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
        Text(label, fontSize = 11.sp, color = Color.Gray)
    }
}
