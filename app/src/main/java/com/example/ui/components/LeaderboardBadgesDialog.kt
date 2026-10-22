package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.AchievementBadge
import com.example.data.model.LeaderboardUser
import com.example.ui.viewmodel.TesterViewModel

@Composable
fun LeaderboardBadgesDialog(
    viewModel: TesterViewModel,
    onDismiss: () -> Unit,
    onWatchAd: () -> Unit
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()
    val userApps by viewModel.userApps.collectAsState()
    val joinedTests by viewModel.joinedTests.collectAsState()
    val claimedBadges by viewModel.claimedBadgeIds.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }

    val badges = remember(currentUser, userApps, joinedTests, claimedBadges) {
        viewModel.getBadges(currentUser, userApps, joinedTests)
    }

    val leaderboard = remember(currentUser) {
        viewModel.getWeeklyLeaderboard(currentUser)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Top Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق")
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFB300),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "لوحة الصدارة والأوسمة",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // User coins pill
                    Surface(
                        color = Color(0xFFFFB300).copy(alpha = 0.15f),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.MonetizationOn,
                                contentDescription = null,
                                tint = Color(0xFFFF8F00),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "${currentUser.points}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = Color(0xFFFF8F00)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Segmented Tabs
                TabRow(
                    selectedTabIndex = selectedTab,
                    modifier = Modifier.clip(RoundedCornerShape(12.dp)),
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    indicator = {},
                    divider = {}
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                "🏆 لوحة الصدارة الأسبوعية",
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp,
                                color = if (selectedTab == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                "🏅 الأوسمة والمكافآت",
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp,
                                color = if (selectedTab == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Content View
                if (selectedTab == 0) {
                    LeaderboardTabContent(
                        leaderboard = leaderboard,
                        currentUser = currentUser,
                        onWatchAd = onWatchAd
                    )
                } else {
                    BadgesTabContent(
                        badges = badges,
                        onClaimBadge = { badge ->
                            viewModel.claimBadge(badge.id, badge.coinReward)
                            Toast.makeText(
                                context,
                                "🎉 مبارك! تم استلام ${badge.title} وإضافة +${badge.coinReward} عملة!",
                                Toast.LENGTH_LONG
                            ).show()
                        },
                        onWatchAd = onWatchAd
                    )
                }
            }
        }
    }
}

@Composable
private fun LeaderboardTabContent(
    leaderboard: List<LeaderboardUser>,
    currentUser: com.example.data.model.AuthUser,
    onWatchAd: () -> Unit
) {
    val topThree = leaderboard.take(3)
    val remaining = leaderboard.drop(3)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Podium Header
        item {
            PodiumCard(topThree = topThree)
        }

        item {
            Text(
                "الترتيب الأسبوعي لمطوري المجتمع",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )
        }

        // Leaderboard List
        items(leaderboard) { user ->
            LeaderboardUserCard(user = user)
        }

        // Boost Coins Action Banner
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 12.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF1E88E5).copy(alpha = 0.1f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E88E5).copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "🚀 ارتقِ في لوحة الصدارة الآن!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFF1565C0)
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            "اكسب المزيد من العملات فوراً عبر تحصيل مكافأة المطور اليومية أو اختبار تطبيقات الزملاء",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = onWatchAd,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+50 عملة", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun PodiumCard(topThree: List<LeaderboardUser>) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "🥇 منصة التتويج الأسبوعية",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                // 2nd Place (Silver)
                if (topThree.size >= 2) {
                    PodiumColumn(
                        user = topThree[1],
                        medal = "🥈",
                        rankNumber = "2",
                        color = Color(0xFFB0BEC5),
                        height = 80
                    )
                }

                // 1st Place (Gold)
                if (topThree.isNotEmpty()) {
                    PodiumColumn(
                        user = topThree[0],
                        medal = "👑",
                        rankNumber = "1",
                        color = Color(0xFFFFB300),
                        height = 105
                    )
                }

                // 3rd Place (Bronze)
                if (topThree.size >= 3) {
                    PodiumColumn(
                        user = topThree[2],
                        medal = "🥉",
                        rankNumber = "3",
                        color = Color(0xFFCD7F32),
                        height = 65
                    )
                }
            }
        }
    }
}

@Composable
private fun PodiumColumn(
    user: LeaderboardUser,
    medal: String,
    rankNumber: String,
    color: Color,
    height: Int
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(95.dp)
    ) {
        Text(medal, fontSize = 22.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            user.name.substringBefore(" "),
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            maxLines = 1,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            "${user.points} عملة",
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = color
        )
        Spacer(modifier = Modifier.height(6.dp))

        Surface(
            modifier = Modifier
                .width(70.dp)
                .height(height.dp),
            shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
            color = color.copy(alpha = 0.25f),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, color)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    "#$rankNumber",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = color
                )
            }
        }
    }
}

@Composable
private fun LeaderboardUserCard(user: LeaderboardUser) {
    val isMe = user.isCurrentUser
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = if (isMe) Color(0xFFFFB300).copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        border = if (isMe) androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFFB300)) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Rank Circle
                Surface(
                    shape = CircleShape,
                    color = when (user.rank) {
                        1 -> Color(0xFFFFB300)
                        2 -> Color(0xFF90A4AE)
                        3 -> Color(0xFFCD7F32)
                        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    },
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            "${user.rank}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (user.rank <= 3) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            user.name,
                            fontWeight = if (isMe) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (isMe) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = Color(0xFF00875A),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    "أنت",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }
                    Text(
                        "${user.badgeTitle} • ${user.testsCompleted} اختبارات مكتملة",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Points
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.MonetizationOn,
                    contentDescription = null,
                    tint = Color(0xFFFF8F00),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "${user.points}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFFFF8F00)
                )
            }
        }
    }
}

@Composable
private fun BadgesTabContent(
    badges: List<AchievementBadge>,
    onClaimBadge: (AchievementBadge) -> Unit,
    onWatchAd: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF00875A).copy(alpha = 0.1f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00875A).copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("💡", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        "أكمل مهام الاختبار ومساعدة المطورين لفتح الأوسمة وكسب مئات العملات المجانية فوراً!",
                        fontSize = 11.sp,
                        color = Color(0xFF00796B),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        items(badges, key = { it.id }) { badge ->
            BadgeCard(badge = badge, onClaim = { onClaimBadge(badge) })
        }
    }
}

@Composable
private fun BadgeCard(
    badge: AchievementBadge,
    onClaim: () -> Unit
) {
    val isComplete = badge.isUnlocked
    val isClaimed = badge.isClaimed

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = when {
            isClaimed -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
            isComplete -> Color(0xFFFFD54F).copy(alpha = 0.15f)
            else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        },
        border = if (isComplete && !isClaimed) androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFFB300)) else null
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = if (isComplete) Color(0xFFFFD54F).copy(alpha = 0.3f) else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.size(46.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(badge.emoji, fontSize = 22.sp)
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                badge.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = Color(0xFFFF8F00).copy(alpha = 0.15f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    "+${badge.coinReward} عملة",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFF8F00),
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            badge.description,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Progress Bar & Action Button Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                    val progressFraction = (badge.currentProgress.toFloat() / badge.targetProgress.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "التقدم: ${badge.currentProgress}/${badge.targetProgress}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "${(progressFraction * 100).toInt()}%",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isComplete) Color(0xFF00875A) else MaterialTheme.colorScheme.outline
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { progressFraction },
                        modifier = Modifier.fillMaxWidth().height(5.dp).clip(RoundedCornerShape(3.dp)),
                        color = if (isComplete) Color(0xFF00875A) else MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                    )
                }

                when {
                    isClaimed -> {
                        FilledTonalButton(
                            onClick = {},
                            enabled = false,
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("تم الاستلام", fontSize = 11.sp)
                        }
                    }
                    isComplete -> {
                        Button(
                            onClick = onClaim,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00875A)),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("استلام +${badge.coinReward} عملة 🎉", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    else -> {
                        OutlinedButton(
                            onClick = {},
                            enabled = false,
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("قيد الإنجاز", fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}
