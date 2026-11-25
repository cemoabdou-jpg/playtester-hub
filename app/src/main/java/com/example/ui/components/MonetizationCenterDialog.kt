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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.UserApp
import com.example.ui.util.L
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
        val appName = app?.name ?: L.t("مشروعي", "My Project")
        val subject = L.t(
            "طلب شراء باقة مميزة: $packageName - تطبيق $appName",
            "VIP Package Purchase Request: $packageName - $appName"
        )
        val body = L.t(
            """
مرحباً إدارة منصة مختبري التطبيقات،

أرغب في شراء وتفعيل الباقة المميزة التالية لتطبيقي:
- اسم الباقة: $packageName
- السعر: $price
- اسم التطبيق: $appName
- اسم الحزمة: ${app?.packageName ?: "غير محدد"}
- البريد الإلكتروني لحسابي: ${currentUser.email}
- اسم المطور: ${L.translateKnown(currentUser.displayName)}

يرجى تزويدي ببيانات الدفع والتحويل (بايبال أو تحويل بنكي) لإتمام التفعيل رسمياً.
            """.trimIndent(),
            """
Hello PlayTester Hub Team,

I would like to purchase and activate the following VIP package for my app:
- Package Name: $packageName
- Price: $price
- App Name: $appName
- Application ID: ${app?.packageName ?: "Not specified"}
- Account Email: ${currentUser.email}
- Developer Name: ${L.translateKnown(currentUser.displayName)}

Please share payment details (PayPal or bank transfer) to complete official activation.
            """.trimIndent()
        )

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:cemoabdou@gmail.com")
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
        try {
            context.startActivity(intent)
            Toast.makeText(
                context,
                L.t("جارٍ فتح البريد لإرسال طلب الشراء إلى الإدارة...", "Opening email app to send purchase request..."),
                Toast.LENGTH_LONG
            ).show()
        } catch (_: Exception) {
            Toast.makeText(
                context,
                L.t("تواصل معنا عبر البريد: cemoabdou@gmail.com", "Contact us via email: cemoabdou@gmail.com"),
                Toast.LENGTH_LONG
            ).show()
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
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_monetization_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = L.t("إغلاق", "Close"))
                    }
                    Text(
                        L.t("💎 الباقات المميزة والتسريع المعتمدة", "💎 Official VIP & Boost Packages"),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Surface(
                        color = Color(0xFFFFB300).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            L.t("${currentUser.points} عملة 🪙", "${currentUser.points} coins 🪙"),
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
                                    L.t("⚡ باقات حقيقية لتسريع جمع الـ 20 مختبراً", "⚡ Real Boost Packages to Reach 20 Testers Faster"),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    L.t(
                                        "يمكنك تفعيل الباقات إما باستخدام رصيد عملاتك المكتسبة من اختبار التطبيقات، أو بطلب الشراء المالي الرسمي، أو مجاناً بمشاهدة الإعلانات لدعم المنصة.",
                                        "Activate packages using coins earned from testing apps, by requesting an official purchase, or for free by watching ads to support the platform."
                                    ),
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
                                        L.t("اختر التطبيق المراد ترقيته وتسريعه:", "Select the app to promote & boost:"),
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
                                                        L.t("${app.currentTesters}/20 مختبراً • ${app.packageName}", "${app.currentTesters}/20 testers • ${app.packageName}"),
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
                            title = L.t("باقة التسريع الماسي السريعة", "VIP Express Package"),
                            price = "$29.99",
                            coinCost = 1200,
                            currentCoins = currentUser.points,
                            badge = L.t("الأكثر طلباً لتأهيل الـ 20 مختبراً ⭐", "Most Popular for 20 Testers ⭐"),
                            badgeColor = Color(0xFFFFB300),
                            features = listOf(
                                L.t("تثبيت في شريط الصدارة المميز لمدة أسبوع لزيادة معدل الانضمام", "Pinned in Featured Spotlight bar for a full week to boost sign-ups"),
                                L.t("أولوية عرض متقدمة أمام مجتمع المطورين النشطين", "Top placement priority across active community developers"),
                                L.t("توليد تقرير تدقيق رسمي مطبوع أو جدول بيانات لمتجر جوجل بلاي", "Generate official PDF/CSV audit report for Google Play"),
                                L.t("متابعة يومية لسجلات الـ 14 يوماً مع تنبيهات التفاعل", "Daily 14-day retention tracking with engagement notifications")
                            ),
                            onPayWithCoins = {
                                val targetPkg = selectedPackageName.ifBlank { userApps.firstOrNull()?.packageName.orEmpty() }
                                val app = userApps.find { it.packageName == targetPkg }
                                if (app != null) {
                                    val success = viewModel.promoteApp(app, cost = 1200)
                                    if (success) {
                                        Toast.makeText(
                                            context,
                                            L.t(
                                                "🌟 تهانينا! تم خصم 1200 عملة وترقية تطبيق ${app.name} للصدارة!",
                                                "🌟 Congrats! Deducted 1200 coins and promoted ${app.name}!"
                                            ),
                                            Toast.LENGTH_LONG
                                        ).show()
                                    } else {
                                        Toast.makeText(
                                            context,
                                            L.t(
                                                "رصيد عملاتك غير كافٍ (تحتاج 1200 عملة)! يمكنك شحن العملات بمشاهدة الإعلانات أدناه 📺",
                                                "Insufficient coins (need 1200 coins)! Earn more coins by watching ads below 📺"
                                            ),
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                } else {
                                    Toast.makeText(
                                        context,
                                        L.t("يرجى تسجيل تطبيقك أولاً من تبويب 'تطبيقاتي'", "Please add your app first from 'My Apps' tab"),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            onRequestCashPurchase = {
                                sendPurchaseOrderEmail(L.t("باقة التسريع الماسي السريعة", "VIP Express Package"), "$29.99")
                            }
                        )
                    }

                    // 2. Fast Booster Package
                    item {
                        RealPricingCard(
                            title = L.t("باقة النمو السريع", "Fast Booster Package"),
                            price = "$14.99",
                            coinCost = 650,
                            currentCoins = currentUser.points,
                            badge = L.t("توفير 50%", "Save 50%"),
                            badgeColor = Color(0xFF00875A),
                            features = listOf(
                                L.t("مضاعفة ظهور التطبيق في قائمة المجتمع مرتين", "2x app visibility boost across the community list"),
                                L.t("أولوية عرض عالية لزيادة سرعة انضمام المختبرين الحقيقيين", "High visibility priority to speed up real tester sign-ups"),
                                L.t("تنبيهات متابعة يومية للمختبرين لفتح التطبيق", "Daily reminder alerts for testers to open your app"),
                                L.t("فحص تنوع الأجهزة وإصدارات النظام", "Device diversity & OS version coverage checks")
                            ),
                            onPayWithCoins = {
                                val targetPkg = selectedPackageName.ifBlank { userApps.firstOrNull()?.packageName.orEmpty() }
                                val app = userApps.find { it.packageName == targetPkg }
                                if (app != null) {
                                    val success = viewModel.promoteApp(app, cost = 650)
                                    if (success) {
                                        Toast.makeText(
                                            context,
                                            L.t(
                                                "🌟 تهانينا! تم خصم 650 عملة وتفعيل باقة النمو السريع على ${app.name}!",
                                                "🌟 Congrats! Deducted 650 coins and activated Fast Booster for ${app.name}!"
                                            ),
                                            Toast.LENGTH_LONG
                                        ).show()
                                    } else {
                                        Toast.makeText(
                                            context,
                                            L.t(
                                                "رصيد عملاتك غير كافٍ (تحتاج 650 عملة)! يمكنك كسب العملات بمشاهدة الإعلانات 🎬",
                                                "Insufficient coins (need 650 coins)! Earn coins by watching ads 🎬"
                                            ),
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                } else {
                                    Toast.makeText(
                                        context,
                                        L.t("يرجى إضافة تطبيقك أولاً من تبويب 'تطبيقاتي'", "Please add your app first from 'My Apps' tab"),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            onRequestCashPurchase = {
                                sendPurchaseOrderEmail(L.t("باقة النمو السريع", "Fast Booster Package"), "$14.99")
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
                                    Text(
                                        L.t("🎬 مركز العملات المجانية والترقية بالإعلانات", "🎬 Free Coins & Ad-Supported Boost Hub"),
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = 14.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(color = Color(0xFFE8F5E9), shape = RoundedCornerShape(6.dp)) {
                                        Text(
                                            L.t("+50 عملة / إعلان", "+50 coins / ad"),
                                            color = Color(0xFF2E7D32),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    L.t(
                                        "نظراً لزيادة تكلفة الترقية بالعملات، يمكنك مشاهدة إعلانات الفيديو لدعم المنصة وجمع العملات بسرعة أو ترقية تطبيقك للصدارة فوراً مجاناً!",
                                        "Watch rewarded video ads to support the platform, earn coins fast, or instantly promote your app to the spotlight for free!"
                                    ),
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
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("monetization_watch_ad_coins_button"),
                                        onClick = {
                                            if (activity != null) {
                                                AdMobConfig.showRewardedAd(
                                                    activity = activity,
                                                    onRewardEarned = { coins ->
                                                        viewModel.earnAdReward(coins)
                                                        Toast.makeText(
                                                            context,
                                                            L.t("🎉 مبروك! تمت إضافة +$coins عملة إلى رصيدك بنجاح!", "🎉 Congrats! Added +$coins coins to your balance!"),
                                                            Toast.LENGTH_LONG
                                                        ).show()
                                                    }
                                                )
                                            } else {
                                                viewModel.earnAdReward(50)
                                                Toast.makeText(
                                                    context,
                                                    L.t("🎉 كسبت +50 عملة بنجاح!", "🎉 Earned +50 coins!"),
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(vertical = 8.dp)
                                    ) {
                                        Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = Color(0xFFFF8F00), modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(L.t("شحن +50 عملة 🪙", "Get +50 Coins 🪙"), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    // Button 2: Direct promotion via ad
                                    Button(
                                        modifier = Modifier
                                            .weight(1.2f)
                                            .testTag("monetization_feature_ad_button"),
                                        onClick = {
                                            if (activity != null) {
                                                AdMobConfig.showRewardedAd(
                                                    activity = activity,
                                                    onRewardEarned = { coins ->
                                                        viewModel.earnAdReward(coins)
                                                        val targetPkg = selectedPackageName.ifBlank { userApps.firstOrNull()?.packageName.orEmpty() }
                                                        if (targetPkg.isNotBlank()) {
                                                            viewModel.promoteAppByPackage(targetPkg, true)
                                                            Toast.makeText(
                                                                context,
                                                                L.t(
                                                                    "🌟 تهانينا! تمت مشاهدة الإعلان وترقية تطبيقك إلى الصدارة فوراً (+50 عملة)!",
                                                                    "🌟 Congrats! Ad watched and your app is now featured (+50 coins)!"
                                                                ),
                                                                Toast.LENGTH_LONG
                                                            ).show()
                                                        } else {
                                                            Toast.makeText(
                                                                context,
                                                                L.t(
                                                                    "🎉 كسبت +$coins عملة! سجّل تطبيقك لترقيته للصدارة.",
                                                                    "🎉 Earned +$coins coins! Register your app to feature it."
                                                                ),
                                                                Toast.LENGTH_SHORT
                                                            ).show()
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
                                        Text(L.t("ترقية للصدارة بإعلان 🎬", "Feature via Ad 🎬"), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("monetization_close_back_button"),
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(L.t("إغلاق والعودة", "Close & Back"))
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
                        if (currentCoins >= coinCost) L.t("تفعيل بـ $coinCost عملة 🪙", "Activate ($coinCost 🪙)")
                        else L.t("ينقصك ${coinCost - currentCoins} عملة", "Need ${coinCost - currentCoins} more"),
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
                    Text(L.t("طلب الشراء ($price) 💳", "Buy Now ($price) 💳"), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
