package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
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
import android.app.Activity
import com.example.ui.components.ThemeSelectionCard
import com.example.ui.util.AppLanguage
import com.example.ui.util.L
import com.example.ui.viewmodel.TesterViewModel

@Composable
fun ProfileSettingsScreen(
    viewModel: TesterViewModel
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // User Profile Card
        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.size(46.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                L.translateKnown(currentUser.displayName).firstOrNull()?.toString() ?: L.t("م", "D"),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            L.translateKnown(currentUser.displayName),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                currentUser.email,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            if (currentUser.isEmailVerified) {
                                Surface(
                                    color = Color(0xFFE8F5E9),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        L.t("موثق ✓", "Verified ✓"),
                                        color = Color(0xFF2E7D32),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        if (currentUser.isPlatformOwner) {
                            Surface(
                                color = Color(0xFF1E88E5).copy(alpha = 0.15f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    L.t("صاحب المنصة 👑", "Platform Owner 👑"),
                                    color = Color(0xFF1565C0),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        if (currentUser.isVip) {
                            Surface(
                                color = Color(0xFFFFB300).copy(alpha = 0.15f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    L.t("عضوية مميزة نشطة", "VIP Active"),
                                    color = Color(0xFFFF8F00),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Platform Owner Admin Control Banner - ONLY visible for platform owner (cemoabdou@gmail.com)
        if (currentUser.isPlatformOwner) {
            item {
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.openAdminControl() },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFF1E88E5).copy(alpha = 0.1f)),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = Color(0xFF1E88E5),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.AdminPanelSettings,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        L.t("لوحة تحكم صاحب المنصة", "Platform Owner Control Panel"),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = Color(0xFF1565C0)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        color = Color(0xFF00875A),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            L.t("مسؤول", "Admin"),
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                                Text(
                                    L.t(
                                        "إدارة وتعديل وحذف وتمييز جميع تطبيقات السيرفر",
                                        "Manage, edit, feature, or remove all server apps"
                                    ),
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForwardIos,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color(0xFF1E88E5)
                        )
                    }
                }
            }
        }

        // Verified Account Status Card
        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFFE8F5E9)),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                L.t(
                                    "الحساب مؤكد بالبريد الإلكتروني ✓",
                                    "Account Verified via Email (OTP) ✓"
                                ),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1B5E20)
                            )
                            Text(
                                currentUser.email,
                                fontSize = 11.sp,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }
                    OutlinedButton(
                        onClick = { viewModel.logout() },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(L.t("تسجيل خروج", "Sign Out"), fontSize = 11.sp)
                    }
                }
            }
        }

        // Account Actions: Manage or logout
        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        L.t("إدارة الحساب والجلسة:", "Account & Session Management:"),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    if (currentUser.isPlatformOwner) {
                        Surface(
                            color = Color(0xFFE3F2FD),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color(0xFF1976D2), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        L.t("أنت مسجل حالياً بصفتك: صاحب المنصة 👑", "Signed in as: Platform Owner 👑"),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color(0xFF0D47A1)
                                    )
                                    Text(
                                        L.t("البريد الإلكتروني المعتمد: ${currentUser.email}", "Verified email: ${currentUser.email}"),
                                        fontSize = 11.sp,
                                        color = Color(0xFF1565C0)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { viewModel.logoutToRegularUser() },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                L.t("معاينة مظهر المستخدم العادي (حساب تجريبي)", "Preview Standard User View (Demo Account)"),
                                fontSize = 12.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { viewModel.logout() },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                L.t("تسجيل الخروج والعودة لشاشة الدخول الإلزامية", "Sign Out & Return to Sign-In Gate"),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { viewModel.logout() },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                L.t("تسجيل الخروج والعودة لشاشة الدخول الإلزامية", "Sign Out & Return to Sign-In Gate"),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Executive Services Section
        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        L.t("الخدمات التنفيذية والأدوات:", "Executive Services & Tools:"),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.openAuditReport() },
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.Assessment, contentDescription = null, modifier = Modifier.size(15.dp), tint = Color(0xFF00875A))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(L.t("تقرير التدقيق", "Audit Report"), fontSize = 11.sp)
                        }
                        OutlinedButton(
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.openMonetization() },
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(15.dp), tint = Color(0xFFFFB300))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(L.t("الباقات المميزة", "VIP Plans"), fontSize = 11.sp)
                        }
                        OutlinedButton(
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.openDeveloperGrowth() },
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.GroupAdd, contentDescription = null, modifier = Modifier.size(15.dp), tint = Color(0xFF1E88E5))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(L.t("دعوة مطورين", "Invite Devs"), fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Leaderboard & Badges Card
        item {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.openLeaderboard() },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFFFFD54F).copy(alpha = 0.15f)),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = Color(0xFFFFB300).copy(alpha = 0.3f),
                            shape = androidx.compose.foundation.shape.CircleShape,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🏆", fontSize = 22.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    L.t("لوحة الصدارة ومكافآت الأوسمة", "Leaderboard & Badge Rewards"),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = Color(0xFFFF8F00),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        L.t("+مكافآت فورية", "+Instant Rewards"),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                L.t(
                                    "تحقق من رتبتك الأسبوعية واستلم مكافآت أوسمة الإنجاز",
                                    "Check your weekly rank and claim achievement badge bonuses"
                                ),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Text(
                        L.t("‹", "›"),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }

        // Ads & Free Points Hub
        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = Color(0xFFFFA000).copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.PlayCircle,
                                        contentDescription = null,
                                        tint = Color(0xFFFFA000),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                L.t("مكافأة الحضور والنشاط اليومي:", "Daily Attendance & Activity Bonus:"),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Surface(
                            color = Color(0xFFE8F5E9),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                L.t("+50 نقطة", "+50 Coins"),
                                color = Color(0xFF2E7D32),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        L.t(
                            "سجّل حضورك اليومي وشارك في اختبار التطبيقات لكسب عملات مجانية لترويج تطبيقاتك واستقطاب 20 مختبراً حقيقياً.",
                            "Check in daily and test peer apps to earn free coins for promoting your apps and attracting 20 real testers."
                        ),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val isBonusClaimed by viewModel.isDailyBonusClaimedToday.collectAsState()
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isBonusClaimed,
                        onClick = {
                            val success = viewModel.claimDailyBonus()
                            if (success) {
                                Toast.makeText(
                                    context,
                                    L.t(
                                        "🎉 مبروك! حصلت على +50 نقطة مكافأة نشاط يومي!",
                                        "🎉 Congrats! You received +50 daily activity coins!"
                                    ),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    context,
                                    L.t(
                                        "لقد استلمت مكافأة اليوم مسبقاً! عد غداً للمزيد.",
                                        "You already claimed today's bonus! Come back tomorrow."
                                    ),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFB300),
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        )
                    ) {
                        Icon(
                            if (isBonusClaimed) Icons.Default.CheckCircle else Icons.Default.CardGiftcard,
                            contentDescription = null,
                            tint = if (isBonusClaimed) Color(0xFF00875A) else Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            if (isBonusClaimed) {
                                L.t("تم استلام مكافأة اليوم بنجاح ✓ (عد غداً)", "Today's Bonus Claimed ✓ (Come back tomorrow)")
                            } else {
                                L.t("استلام مكافأة اليوم (+50 نقطة)", "Claim Daily Bonus (+50 Coins)")
                            },
                            color = if (isBonusClaimed) Color(0xFF00875A) else Color.Black,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Theme & Appearance Selection
        item {
            ThemeSelectionCard()
        }

        // App Preferences
        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        L.t("تفضيلات التطبيق:", "App Preferences:"),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(L.t("لغة التطبيق:", "App Language:"), fontSize = 13.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            FilterChip(
                                selected = L.isArabic(),
                                onClick = { L.setLanguage(AppLanguage.ARABIC) },
                                label = { Text("العربية", fontSize = 11.sp) },
                                shape = RoundedCornerShape(8.dp)
                            )
                            FilterChip(
                                selected = !L.isArabic(),
                                onClick = { L.setLanguage(AppLanguage.ENGLISH) },
                                label = { Text("English", fontSize = 11.sp) },
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                    // Daily Testing Streak Reminder Setting
                    var isReminderOn by remember {
                        mutableStateOf(com.example.util.DailyReminderScheduler.isReminderEnabled(context))
                    }
                    val notificationPermissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
                        contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
                    ) { granted ->
                        if (granted) {
                            com.example.util.DailyReminderScheduler.setReminderEnabled(context, true)
                            isReminderOn = true
                            Toast.makeText(
                                context,
                                L.t("تم تفعيل الإشعارات بنجاح 🔔", "Notifications enabled successfully 🔔"),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                context,
                                L.t(
                                    "يرجى السماح بالإشعارات من إعدادات الهاتف لتلقي التذكير اليومي",
                                    "Please allow notifications in device settings to receive daily reminders"
                                ),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = Color(0xFF00875A), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    L.t("تذكير الاختبار اليومي (8:00 م)", "Daily Testing Reminder (8:00 PM)"),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                            Text(
                                L.t(
                                    "تنبيه يومي للحفاظ على سلسلة 14 يوماً ومساعدة المطورين",
                                    "Daily alert to keep your 14-day streak and support developers"
                                ),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = isReminderOn,
                            onCheckedChange = { enable ->
                                if (enable && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                                    notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                                } else {
                                    com.example.util.DailyReminderScheduler.setReminderEnabled(context, enable)
                                    isReminderOn = enable
                                    Toast.makeText(
                                        context,
                                        if (enable) {
                                            L.t("تم تفعيل التذكير اليومي (8:00 مساءً) ⏰", "Daily reminder enabled (8:00 PM) ⏰")
                                        } else {
                                            L.t("تم إيقاف التذكير", "Daily reminder disabled")
                                        },
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = {
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                                notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                            }
                            com.example.util.DailyReminderScheduler.triggerTestReminderNow(context)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Notifications, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            L.t("إرسال إشعار تجريبي الآن للتأكد 🔔", "Send Test Notification Now 🔔"),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
