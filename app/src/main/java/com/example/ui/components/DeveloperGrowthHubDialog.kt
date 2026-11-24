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
import com.example.ui.util.L

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
                        Icon(Icons.Default.Close, contentDescription = L.t("إغلاق", "Close"))
                    }
                    Text(
                        L.t("🚀 مركز جذب المطورين وشبكة التبادل", "🚀 Developer Growth & Referral Hub"),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                    IconButton(onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_SUBJECT,
                                L.t("انضم لشبكة مطوري منصة مختبري التطبيقات", "Join the PlayTester Hub Developer Network")
                            )
                            putExtra(
                                Intent.EXTRA_TEXT,
                                L.t(
                                    "انضم لمنصة مختبري التطبيقات واكسب 100 نقطة مجانية لاختبار تطبيقك عبر رابط الدعوة الخاص بي: https://playtesterhub.app/invite/dev_guest_01",
                                    "Join PlayTester Hub and earn 100 free coins to test your app via my invite link: https://playtesterhub.app/invite/dev_guest_01"
                                )
                            )
                        }
                        context.startActivity(Intent.createChooser(shareIntent, L.t("مشاركة دعوة المطورين", "Share Developer Invite")))
                    }) {
                        Icon(Icons.Default.Share, contentDescription = L.t("دعوة مطورين", "Invite Developers"))
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                TabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text(L.t("دعوة المطورين", "Invite Devs")) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text(L.t("عقود التبادل المباشرة", "1v1 Alliances")) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text(L.t("مجتمع المطورين", "Dev Guild")) }
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
                    Text(L.t("تم والعودة للرئيسية", "Done & Back Home"))
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
                        L.t("🎁 برنامج إحالة ودعوة المطورين", "🎁 Developer Referral Program"),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        L.t(
                            "لكل مطور جديد يسجل عبر رابطك ويضيف تطبيقه للاختبار، تحصل فوراً على +100 نقطة مجانية كافية لجلب مختبرين لتطبيقك!",
                            "For every developer who joins via your link and adds an app, you instantly earn +100 free coins!"
                        ),
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 13.sp
                    )
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(L.t("رابط الدعوة المخصص لك:", "Your Custom Invite Link:"), fontWeight = FontWeight.SemiBold)
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
                            val clip = android.content.ClipData.newPlainText(L.t("رابط الدعوة", "Invite Link"), "https://playtesterhub.app/invite/dev_guest_01")
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, L.t("تم نسخ رابط الدعوة للحافظة بنجاح!", "Invite link copied to clipboard!"), Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(L.t("نسخ رابط الدعوة", "Copy Invite Link"))
                    }
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(L.t("إحصائيات إحالاتك:", "Your Referral Stats:"), fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        StatBox("5", L.t("مطورين مدعوين", "Invited Devs"))
                        StatBox("+500", L.t("نقاط مكتسبة", "Coins Earned"))
                        StatBox(L.t("المستوى 2", "Level 2"), L.t("رتبة السفير", "Ambassador"))
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
                        L.t("🤝 عقود التبادل المتبادل المضمونة", "🤝 Guaranteed 1-on-1 Mutual Alliances"),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        L.t(
                            "اتفاقية ملزمة بينك وبين مطور آخر: يختبر تطبيقك لمدة 14 يوماً وتختبر تطبيقه بالتوازي مع نظام إشعار بالالتزام اليومي.",
                            "Mutual commitment with another developer: they test your app for 14 days while you test theirs with daily reminders."
                        ),
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 12.sp
                    )
                }
            }
        }

        val alliances = listOf(
            Triple(L.t("مطور تطبيق اللياقة البدنية", "GymMaster App Dev"), L.t("تطبيق اللياقة وبناء الأجسام", "Fitness & Workout Tracker"), L.t("متوفر للمبادلة الفورية", "Available for instant swap")),
            Triple(L.t("مطور تطبيق دفتر الوصفات", "ChefNotes App Dev"), L.t("مكتبة وصفات الطهي والمطاعم", "Cooking Recipes Library"), L.t("يبحث عن 5 مطورين للتبادل", "Looking for 5 testers")),
            Triple(L.t("مطور تطبيق الكتب الصوتية", "AudioBooker App Dev"), L.t("مشغل الكتب الصوتية والبودكاست", "Audiobooks & Podcast Player"), L.t("تبادل متبادل نشط", "Active mutual exchange"))
        )

        items(alliances.size) { index ->
            val item = alliances[index]
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(item.first, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(item.second, fontSize = 12.sp, color = Color.Gray)
                        Text(item.third, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
                    }
                    Button(
                        onClick = {
                            Toast.makeText(context, L.t("تم إرسال طلب عقد التبادل للمطور بنجاح!", "Alliance swap request sent!"), Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(L.t("عقد تبادل", "Request Swap"))
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
                    Text(L.t("🏆 لوحة شرف المطورين الأكثر تفاعلاً:", "🏆 Top Active Developers Honor Board:"), fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    DeveloperRow(L.t("1. م. أحمد خليل", "1. Eng. Ahmed Khalil"), L.t("32 تطبيقاً تم اعتماده", "32 Approved Apps"), L.t("1,850 نقطة", "1,850 pts"))
                    DeveloperRow(L.t("2. ستوديو السحاب للبرمجيات", "2. Cloud Soft Studio"), L.t("28 تطبيقاً تم اعتماده", "28 Approved Apps"), L.t("1,600 نقطة", "1,600 pts"))
                    DeveloperRow(L.t("3. د. عمر الشامي", "3. Dr. Omar Al-Shami"), L.t("21 تطبيقاً تم اعتماده", "21 Approved Apps"), L.t("1,240 نقطة", "1,240 pts"))
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
