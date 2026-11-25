package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.AuthUser
import com.example.data.model.CommunityApp
import com.example.data.model.LeaderboardUser
import com.example.ui.util.L
import com.example.ui.viewmodel.TesterViewModel

@Composable
fun DeveloperGrowthHubDialog(
    viewModel: TesterViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()
    val communityApps by viewModel.communityApps.collectAsState()
    val leaderboard = remember(communityApps, currentUser) {
        viewModel.getWeeklyLeaderboard(currentUser).take(10)
    }

    val myCode = currentUser.referralCode.ifBlank { "PTH-HUB" }
    val inviteLink = "https://playtesterhub.app/invite/$myCode"

    val peerApps = remember(communityApps, currentUser.email) {
        communityApps.filter {
            it.developerEmail.isBlank() || !it.developerEmail.equals(currentUser.email, ignoreCase = true)
        }
    }

    var selectedTab by remember { mutableIntStateOf(0) }

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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_growth_hub_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = L.t("إغلاق", "Close"))
                    }
                    Text(
                        L.t("🚀 مركز جذب المطورين وشبكة التبادل", "🚀 Developer Growth & Referral Hub"),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(
                                    Intent.EXTRA_SUBJECT,
                                    L.t("انضم لشبكة مطوري منصة مختبري التطبيقات", "Join the PlayTester Hub Developer Network")
                                )
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    L.t(
                                        "انضم لمنصة مختبري التطبيقات واستخدم رمز الدعوة $myCode لكسب +50 عملة مجانية واختبار تطبيقك: $inviteLink",
                                        "Join PlayTester Hub with referral code $myCode to earn +50 bonus coins and test your app: $inviteLink"
                                    )
                                )
                            }
                            context.startActivity(
                                Intent.createChooser(shareIntent, L.t("مشاركة دعوة المطورين", "Share Developer Invite"))
                            )
                        },
                        modifier = Modifier.testTag("share_referral_button")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = L.t("دعوة مطورين", "Invite Developers"))
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                TabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        modifier = Modifier.testTag("growth_tab_referrals"),
                        text = { Text(L.t("دعوة المطورين", "Invite Devs"), fontSize = 12.sp) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        modifier = Modifier.testTag("growth_tab_alliances"),
                        text = { Text(L.t("التبادل المباشر", "1v1 Swap"), fontSize = 12.sp) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        modifier = Modifier.testTag("growth_tab_leaderboard"),
                        text = { Text(L.t("مجتمع المطورين", "Dev Guild"), fontSize = 12.sp) }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Box(modifier = Modifier.weight(1f)) {
                    when (selectedTab) {
                        0 -> ReferralSection(
                            context = context,
                            user = currentUser,
                            myCode = myCode,
                            inviteLink = inviteLink,
                            onRedeemReferral = { codeInput, onFeedback ->
                                viewModel.redeemReferralCode(codeInput, onFeedback)
                            }
                        )
                        1 -> MutualTestingSection(
                            peerApps = peerApps,
                            onRequestSwap = { app ->
                                val devEmail = app.developerEmail.ifBlank {
                                    "${app.developerName.lowercase().replace(" ", "").replace("[^a-zA-Z0-9]".toRegex(), "")}@developer.com"
                                }
                                onDismiss()
                                viewModel.openDeveloperChat(devEmail, app.developerName, app.name)
                            }
                        )
                        2 -> CommunityGuildSection(
                            leaderboard = leaderboard,
                            onSelectDeveloper = { entry ->
                                onDismiss()
                                viewModel.openTesterProfile(entry.email, entry.name)
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("growth_hub_done_button"),
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(L.t("تم والعودة للرئيسية", "Done & Back Home"), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ReferralSection(
    context: Context,
    user: AuthUser,
    myCode: String,
    inviteLink: String,
    onRedeemReferral: (String, (Boolean, String) -> Unit) -> Unit
) {
    var inputCode by remember { mutableStateOf("") }
    var redeemStatus by remember { mutableStateOf<Pair<Boolean, String>?>(null) }
    var isRedeeming by remember { mutableStateOf(false) }

    val ambassadorRank = when {
        user.invitedDevsCount >= 10 -> L.t("المستوى 3", "Level 3")
        user.invitedDevsCount >= 3 -> L.t("المستوى 2", "Level 2")
        else -> L.t("المستوى 1", "Level 1")
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
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
                            "لكل مطور جديد يسجل برمز دعوتك يحصل على +50 عملة ترحيبية وتكسب أنت فوراً +100 عملة مجانية!",
                            "When a new developer redeems your referral code, they receive +50 welcome coins and you instantly earn +100 free coins!"
                        ),
                        color = Color.White.copy(alpha = 0.92f),
                        fontSize = 13.sp
                    )
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(L.t("رمز الدعوة الخاص بك:", "Your Personal Referral Code:"), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            myCode,
                            modifier = Modifier.padding(12.dp),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            inviteLink,
                            modifier = Modifier.padding(10.dp),
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    }
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("copy_referral_link_button"),
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText(L.t("رابط الدعوة", "Invite Link"), inviteLink)
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

        if (user.referredBy.isBlank()) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            L.t("هل لديك رمز دعوة من مطور آخر؟", "Have a referral code from another developer?"),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        OutlinedTextField(
                            value = inputCode,
                            onValueChange = {
                                inputCode = it.uppercase()
                                redeemStatus = null
                            },
                            label = { Text(L.t("رمز الدعوة (مثال: PTH-AB12CD)", "Referral Code (e.g. PTH-AB12CD)")) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("redeem_referral_input")
                        )
                        if (redeemStatus != null) {
                            Text(
                                redeemStatus!!.second,
                                fontSize = 12.sp,
                                color = if (redeemStatus!!.first) Color(0xFF00875A) else MaterialTheme.colorScheme.error
                            )
                        }
                        OutlinedButton(
                            onClick = {
                                if (inputCode.isBlank()) {
                                    redeemStatus = false to L.t("أدخل رمز الدعوة أولاً", "Please enter a referral code")
                                    return@OutlinedButton
                                }
                                isRedeeming = true
                                onRedeemReferral(inputCode.trim()) { ok, msg ->
                                    isRedeeming = false
                                    redeemStatus = ok to msg
                                    if (ok) inputCode = ""
                                }
                            },
                            enabled = !isRedeeming,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("redeem_referral_button"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                if (isRedeeming) L.t("جارٍ التحقق...", "Redeeming...") else L.t("تفعيل رمز الدعوة (+50 عملة)", "Redeem Referral Code (+50 Coins)"),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        } else {
            item {
                Surface(
                    color = Color(0xFFE8F5E9),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        L.t("تم ربط حسابك بدعوة من: ${user.referredBy} ✓", "Referred by: ${user.referredBy} ✓"),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20),
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(L.t("إحصائيات إحالاتك الفعلية:", "Your Live Referral Stats:"), fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        StatBox("${user.invitedDevsCount}", L.t("مطورين مدعوين", "Invited Devs"))
                        StatBox("+${user.invitedDevsCount * 100}", L.t("عملات مكتسبة", "Coins Earned"))
                        StatBox(ambassadorRank, L.t("رتبة السفير", "Ambassador"))
                    }
                }
            }
        }
    }
}

@Composable
private fun MutualTestingSection(
    peerApps: List<CommunityApp>,
    onRequestSwap: (CommunityApp) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF00796B))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        L.t("🤝 التبادل المباشر 1 ضد 1 مع المطورين", "🤝 Direct 1-on-1 Developer Swap"),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        L.t(
                            "تواصل مباشرة مع مطورين حقيقيين مسجلين على المنصة للاتفاق على اختبار متبادل لمدة 14 يوماً.",
                            "Connect directly with registered peer developers to agree on mutual 14-day closed testing."
                        ),
                        color = Color.White.copy(alpha = 0.92f),
                        fontSize = 12.sp
                    )
                }
            }
        }

        if (peerApps.isEmpty()) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Groups, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            L.t("لا توجد تطبيقات زملاء متاحة للتبادل حالياً", "No peer developer apps available for swap right now"),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        } else {
            items(peerApps, key = { it.id }) { app ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(app.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(
                                L.translateKnown(app.developerName),
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                L.t("${app.currentTesters}/${app.targetTesters} مختبراً • +${app.pointsReward} عملة", "${app.currentTesters}/${app.targetTesters} testers • +${app.pointsReward} coins"),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { onRequestSwap(app) },
                            modifier = Modifier.height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text(L.t("بدء محادثة تبادل", "Request Swap"), fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CommunityGuildSection(
    leaderboard: List<LeaderboardUser>,
    onSelectDeveloper: (LeaderboardUser) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        L.t("🏆 المطورون النشطون على المنصة:", "🏆 Active Developers on PlayTester Hub:"),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    if (leaderboard.isEmpty()) {
                        Text(
                            L.t("لا توجد بيانات مطورين مسجلة بعد.", "No registered developer stats yet."),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        leaderboard.forEach { dev ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "${dev.rank}. ${dev.name}",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        "${dev.badgeTitle} • ${L.t("${dev.testsCompleted} اختبار/تطبيق", "${dev.testsCompleted} tests/apps")}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        L.t("${dev.points} عملة", "${dev.points} pts"),
                                        color = Color(0xFF00875A),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                    TextButton(onClick = { onSelectDeveloper(dev) }) {
                                        Text(L.t("عرض", "View"), fontSize = 11.sp)
                                    }
                                }
                            }
                            HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatBox(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
        Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
