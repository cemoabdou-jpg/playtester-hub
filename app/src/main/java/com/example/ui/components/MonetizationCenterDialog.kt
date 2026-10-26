package com.example.ui.components

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import com.example.data.model.UserApp
import com.example.ui.viewmodel.TesterViewModel
import com.example.util.AdMobConfig

@Composable
fun MonetizationCenterDialog(
    viewModel: TesterViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val currentUser by viewModel.currentUser.collectAsState()
    val userApps by viewModel.userApps.collectAsState()
    var selectedPackageName by remember {
        mutableStateOf(userApps.firstOrNull()?.packageName ?: "")
    }

    fun sendPurchaseOrderEmail(packageName: String, price: String) {
        val app = userApps.find { it.packageName == selectedPackageName }
        val appName = app?.name ?: "مشروعي"
        val subject = "طلب شراء باقة VIP: $packageName - تطبيق $appName"
        val body = """
مرحباً إدارة PlayTester Hub،

أرغب في شراء وتفعيل باقة VIP التالية لتطبيقي:
- اسم الباقة: $packageName
- السعر: $price
- اسم التطبيق: $appName
- اسم الحزمة (Package Name): ${app?.packageName ?: "غير محدد"}
- البريد الإلكتروني لحسابي: ${currentUser.email}
- اسم المطور: ${currentUser.displayName}

يرجى تزويدي ببيانات الدفع والتحويل (PayPal أو تحويل بنكي) لإتمام التفعيل رسمياً.
        """.trimIndent()

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:cemoabdou@gmail.com")
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
        try {
            context.startActivity(intent)
            Toast.makeText(context, "جارٍ فتح البريد لإرسال طلب الشراء إلى الإدارة...", Toast.LENGTH_LONG).show()
        } catch (_: Exception) {
            Toast.makeText(context, "تواصل معنا عبر البريد: cemoabdou@gmail.com", Toast.LENGTH_LONG).show()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                // Top Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق")
                    }
                    Text(
                        "💎 باقات الـ VIP والتسريع المعتمدة",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Surface(
                        color = Color(0xFFFFB300).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "${currentUser.points} عملة 🪙",
                            color = Color(0xFFE65100),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Header Banner
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    "⚡ باقات حقيقية لتسريع جمع الـ 20 مختبراً",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "يمكنك تفعيل الباقات إما باستخدام رصيد عملاتك المكتسبة من اختبار التطبيقات، أو بطلب الشراء المالي الرسمي، أو مجاناً بمشاهدة الإعلانات لدعم المنصة.",
                                    color = Color(0xFFC7D2FE),
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp
                                )
                            }
                        }
                    }

                    // Target App Selector (if user has apps)
                    if (userApps.isNotEmpty()) {
                        item {
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        "اختر التطبيق المراد ترقيته وتسريعه:",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    userApps.forEach { app ->
                                        val isSelected = selectedPackageName == app.packageName
                                        Surface(
                                            onClick = { selectedPackageName = app.packageName },
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                                            border = if (isSelected) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
                                            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                RadioButton(
                                                    selected = isSelected,
                                                    onClick = { selectedPackageName = app.packageName }
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Column {
                                                    Text(app.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                    Text(
                                                        "${app.currentTesters}/20 مختبراً • ${app.packageName}",
                                                        fontSize = 11.sp,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 1. VIP Express Package
                    item {
                        RealPricingCard(
                            title = "باقة التسريع الماسي (VIP Express)",
                            price = "$29.99",
                            coinCost = 1200,
                            currentCoins = currentUser.points,
                            badge = "الأكثر طلباً لتأهيل الـ 20 مختبراً ⭐",
                            badgeColor = Color(0xFFFFB300),
                            features = listOf(
                                "تثبيت في صدارة شريط Spotlight لمدة أسبوع كامل",
                                "أولوية مطلقة في جذب الـ 20 مختبراً المعتمدين",
                                "توليد تقرير تدقيق رسمي PDF/CSV معتمد لـ Google Play",
                                "متابعة يومية لسجلات الـ 14 يوماً مع ضمان عدم انقطاع المختبرين"
                            ),
                            onPayWithCoins = {
                                val targetPkg = selectedPackageName.ifBlank { userApps.firstOrNull()?.packageName.orEmpty() }
                                val app = userApps.find { it.packageName == targetPkg }
                                if (app != null) {
                                    val success = viewModel.promoteApp(app, cost = 1200)
                                    if (success) {
                                        Toast.makeText(context, "🌟 تهانينا! تم خصم 1200 عملة وترقية تطبيق ${app.name} للصدارة!", Toast.LENGTH_LONG).show()
                                    } else {
                                        Toast.makeText(context, "رصيد عملاتك غير كافٍ (تحتاج 1200 عملة)! يمكنك شحن العملات بمشاهدة الإعلانات أدناه 📺", Toast.LENGTH_LONG).show()
                                    }
                                } else {
                                    Toast.makeText(context, "يرجى تسجيل تطبيقك أولاً من تبويب 'تطبيقاتي'", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onRequestCashPurchase = {
                                sendPurchaseOrderEmail("باقة التسريع الماسي VIP Express", "$29.99")
                            }
                        )
                    }

                    // 2. Fast Booster Package
                    item {
                        RealPricingCard(
                            title = "باقة النمو السريع (Fast Booster)",
                            price = "$14.99",
                            coinCost = 650,
                            currentCoins = currentUser.points,
                            badge = "توفير 50%",
                            badgeColor = Color(0xFF00875A),
                            features = listOf(
                                "مضاعفة ظهور التطبيق في المجتمع (2x Visibility)",
                                "جذب 10 مختبرين إضافيين معتمدين فوراً",
                                "إشعار وتنبيه يومي للمختبرين لفتح التطبيق",
                                "فحص تنوع الأجهزة وإصدارات أندرويد"
                            ),
                            onPayWithCoins = {
                                val targetPkg = selectedPackageName.ifBlank { userApps.firstOrNull()?.packageName.orEmpty() }
                                val app = userApps.find { it.packageName == targetPkg }
                                if (app != null) {
                                    val success = viewModel.promoteApp(app, cost = 650)
                                    if (success) {
                                        Toast.makeText(context, "🌟 تهانينا! تم خصم 650 عملة وتفعيل باقة النمو السريع على ${app.name}!", Toast.LENGTH_LONG).show()
                                    } else {
                                        Toast.makeText(context, "رصيد عملاتك غير كافٍ (تحتاج 650 عملة)! يمكنك كسب العملات بمشاهدة الإعلانات 🎬", Toast.LENGTH_LONG).show()
                                    }
                                } else {
                                    Toast.makeText(context, "يرجى إضافة تطبيقك أولاً من تبويب 'تطبيقاتي'", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onRequestCashPurchase = {
                                sendPurchaseOrderEmail("باقة النمو السريع Fast Booster", "$14.99")
                            }
                        )
                    }

                    // 3. Free Instant Ad-Supported Boost & Rewarded Ads Hub
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("🎬 مركز العملات المجانية والترقية بالإعلانات", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
                                    Surface(color = Color(0xFFE8F5E9), shape = RoundedCornerShape(6.dp)) {
                                        Text("+50 عملة / إعلان", color = Color(0xFF2E7D32), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "نظراً لزيادة تكلفة الترقية بالعملات، يمكنك مشاهدة إعلانات الفيديو لدعم المنصة وجمع العملات بسرعة أو ترقية تطبيقك للصدارة فوراً مجاناً!",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 16.sp
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Button 1: Watch ad for direct +50 coins
                                    OutlinedButton(
                                        modifier = Modifier.weight(1f),
                                        onClick = {
                                            if (activity != null) {
                                                AdMobConfig.showRewardedAd(
                                                    activity = activity,
                                                    onRewardEarned = { coins ->
                                                        viewModel.earnAdReward(coins)
                                                        Toast.makeText(context, "🎉 مبروك! تمت إضافة +$coins عملة إلى رصيدك بنجاح!", Toast.LENGTH_LONG).show()
                                                    }
                                                )
                                            } else {
                                                viewModel.earnAdReward(50)
                                                Toast.makeText(context, "🎉 كسبت +50 عملة بنجاح!", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(vertical = 8.dp)
                                    ) {
                                        Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = Color(0xFFFF8F00), modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("شحن +50 عملة 🪙", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    // Button 2: Direct promotion via ad
                                    Button(
                                        modifier = Modifier.weight(1.2f),
                                        onClick = {
                                            if (activity != null) {
                                                AdMobConfig.showRewardedAd(
                                                    activity = activity,
                                                    onRewardEarned = { coins ->
                                                        viewModel.earnAdReward(coins)
                                                        val targetPkg = selectedPackageName.ifBlank { userApps.firstOrNull()?.packageName.orEmpty() }
                                                        if (targetPkg.isNotBlank()) {
                                                            viewModel.promoteAppByPackage(targetPkg, true)
                                                            Toast.makeText(context, "🌟 تهانينا! تمت مشاهدة الإعلان وترقية تطبيقك إلى الصدارة فوراً (+50 عملة)!", Toast.LENGTH_LONG).show()
                                                        } else {
                                                            Toast.makeText(context, "🎉 كسبت +$coins عملة! سجّل تطبيقك لترقيته للصدارة.", Toast.LENGTH_SHORT).show()
                                                        }
                                                    }
                                                )
                                            } else {
                                                viewModel.earnAdReward(50)
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8F00)),
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(vertical = 8.dp)
                                    ) {
                                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("ترقية للصدارة بإعلان 🎬", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
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
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("إغلاق والعودة")
                }
            }
        }
    }
}

@Composable
private fun RealPricingCard(
    title: String,
    price: String,
    coinCost: Int,
    currentCoins: Int,
    badge: String,
    badgeColor: Color,
    features: List<String>,
    onPayWithCoins: () -> Unit,
    onRequestCashPurchase: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(1.5.dp, badgeColor.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(price, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp, color = badgeColor)
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
            Spacer(modifier = Modifier.height(6.dp))
            features.forEach { feat ->
                Row(modifier = Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF00875A), modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(feat, fontSize = 11.sp)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))

            // Two Real Action Options (Coins vs Direct Cash Order)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Option 1: Coins
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = onPayWithCoins,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (currentCoins >= coinCost) Color(0xFF00875A) else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    Icon(Icons.Default.MonetizationOn, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        if (currentCoins >= coinCost) "تفعيل بـ $coinCost عملة 🪙" else "ينقصك ${coinCost - currentCoins} عملة",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Option 2: Real Order via Email / Contact
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = onRequestCashPurchase,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("طلب الشراء ($price) 💳", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
