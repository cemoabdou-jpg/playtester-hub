package com.example.ui.components

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BadgeCalculator
import com.example.data.model.BadgeCategory
import com.example.data.model.UserBadge
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.PlayGreen
import com.example.ui.theme.SuccessGreen
import com.example.ui.util.AppLanguage
import com.example.ui.util.LocalAppLanguage

@Composable
fun BadgesDialog(
    badges: List<UserBadge>,
    claimedBadgeIds: Set<String> = emptySet(),
    onDismiss: () -> Unit,
    onClaimBadgeReward: (badgeId: String, points: Int) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    val language = LocalAppLanguage.current
    val isArabic = language == AppLanguage.AR

    var selectedCategory by remember { mutableStateOf(BadgeCategory.ALL) }

    val unlockedCount = badges.count { it.isUnlocked }
    val totalCount = badges.size
    val (rankTitleAr, rankDescAr) = remember(unlockedCount) {
        BadgeCalculator.getUserRankTitle(unlockedCount)
    }

    val rankTitle = if (isArabic) rankTitleAr else when {
        unlockedCount >= 10 -> "Master Google Play Pioneer 👑"
        unlockedCount >= 7 -> "Professional Play Tester ⭐"
        unlockedCount >= 4 -> "Active Developer 🚀"
        unlockedCount >= 2 -> "Promising Tester 🛠️"
        else -> "New Tester 🌱"
    }

    val rankDesc = if (isArabic) rankDescAr else when {
        unlockedCount >= 10 -> "Top trusted developer in the community"
        unlockedCount >= 7 -> "Strong ongoing testing contributions"
        unlockedCount >= 4 -> "Consistent daily tester & reviewer"
        unlockedCount >= 2 -> "Good progress on 14-day tests"
        else -> "Just getting started on your testing journey"
    }

    val filteredBadges = remember(selectedCategory, badges) {
        if (selectedCategory == BadgeCategory.ALL) badges
        else badges.filter { it.category == selectedCategory }
    }

    val overallProgress = if (totalCount > 0) unlockedCount.toFloat() / totalCount else 0f
    val animatedProgress by animateFloatAsState(targetValue = overallProgress, label = "badge_progress")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = AmberGold.copy(alpha = 0.2f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = AmberGold,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isArabic) "نظام الرتب والشارات 🏅" else "Badges & Ranks System 🏅",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isArabic) "$unlockedCount من $totalCount شارات مفتوحة" else "$unlockedCount of $totalCount badges unlocked",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = if (isArabic) "إغلاق" else "Close")
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("badges_dialog_content")
            ) {
                // Rank Card
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (isArabic) "رتبتك الحالية:" else "Current Rank:",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = rankTitle,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AmberGold
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = AmberGold.copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, AmberGold.copy(alpha = 0.4f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Stars,
                                        contentDescription = null,
                                        tint = AmberGold,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "$unlockedCount/$totalCount",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = AmberGold
                                    )
                                }
                            }
                        }

                        Text(
                            text = rankDesc,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Progress to next rank
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (isArabic) "التقدم الإجمالي للشارات" else "Overall Badges Progress",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${(animatedProgress * 100).toInt()}%",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = AmberGold
                                )
                            }
                            LinearProgressIndicator(
                                progress = { animatedProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = AmberGold,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Category Filter Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(BadgeCategory.values()) { category ->
                        val isSelected = selectedCategory == category
                        val label = when (category) {
                            BadgeCategory.ALL -> if (isArabic) "الكل" else "All"
                            BadgeCategory.TESTING -> if (isArabic) "الاختبار" else "Testing"
                            BadgeCategory.DEVELOPER -> if (isArabic) "المطور" else "Developer"
                            BadgeCategory.QUALITY -> if (isArabic) "الجودة" else "Quality"
                            BadgeCategory.STREAK -> if (isArabic) "الاستمرار" else "Streak"
                        }
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = category },
                            label = { Text(label, fontSize = 12.sp) },
                            modifier = Modifier.testTag("badge_filter_${category.name}")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Badges List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredBadges, key = { it.id }) { badge ->
                        val isClaimed = claimedBadgeIds.contains(badge.id)
                        BadgeItemCard(
                            badge = badge,
                            isClaimed = isClaimed,
                            isArabic = isArabic,
                            onClaim = { onClaimBadgeReward(badge.id, badge.pointsReward) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val shareText = if (isArabic) {
                        "🏆 إنجازاتي في منصة PlayTester Hub:\n" +
                                "رتبتي: $rankTitle\n" +
                                "حققت $unlockedCount شارة تقديرية لاختبار تطبيقات أندرويد لمدة 14 يوماً بنجاح!\n" +
                                "انضم الآن لتبادل المختبرين والحصول على 20 مختبراً لتطبيقك."
                    } else {
                        "🏆 My Achievements on PlayTester Hub:\n" +
                                "Rank: $rankTitle\n" +
                                "Unlocked $unlockedCount badges testing Android apps for 14 continuous days!\n" +
                                "Join the developer network to get 20 active testers for your app."
                    }
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, shareText)
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(sendIntent, if (isArabic) "مشاركة إنجازاتك" else "Share Achievements"))
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PlayGreen),
                modifier = Modifier.testTag("btn_share_badges")
            ) {
                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (isArabic) "مشاركة إنجازاتي" else "Share Achievements")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(if (isArabic) "إغلاق" else "Close")
            }
        }
    )
}

@Composable
fun BadgeItemCard(
    badge: UserBadge,
    isClaimed: Boolean = false,
    isArabic: Boolean = true,
    onClaim: () -> Unit = {}
) {
    val rarityColor = Color(badge.rarity.colorHex)
    val progressRatio = if (badge.maxProgress > 0) badge.currentProgress.toFloat() / badge.maxProgress else 0f

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (badge.isUnlocked) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
        border = BorderStroke(
            1.dp,
            if (badge.isUnlocked) rarityColor.copy(alpha = 0.6f)
            else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("badge_card_${badge.id}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Badge Emoji / Icon
                Surface(
                    shape = CircleShape,
                    color = if (badge.isUnlocked) rarityColor.copy(alpha = 0.18f)
                    else MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, if (badge.isUnlocked) rarityColor else Color.Transparent),
                    modifier = Modifier.size(46.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (badge.isUnlocked) {
                            Text(
                                text = badge.iconEmoji,
                                fontSize = 24.sp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = if (isArabic) "مقفل" else "Locked",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = badge.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (badge.isUnlocked) MaterialTheme.colorScheme.onSurface
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = PlayGreen.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = "+${badge.pointsReward} نقطة",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PlayGreen,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = rarityColor.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = if (isArabic) badge.rarity.label else badge.rarity.name.lowercase().capitalize(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = rarityColor,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = badge.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        LinearProgressIndicator(
                            progress = { progressRatio },
                            modifier = Modifier
                                .weight(1f)
                                .height(5.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (badge.isUnlocked) PlayGreen else CyanAccent,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = if (badge.isUnlocked) (if (isArabic) "مكتمل ✓" else "Unlocked ✓") else "${badge.currentProgress}/${badge.maxProgress}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (badge.isUnlocked) PlayGreen else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Claim Reward Button if Unlocked
            if (badge.isUnlocked) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    if (isClaimed) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = SuccessGreen.copy(alpha = 0.12f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isArabic) "تم استلام +${badge.pointsReward} نقطة ✓" else "Claimed +${badge.pointsReward} pts ✓",
                                    color = SuccessGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else {
                        Button(
                            onClick = onClaim,
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isArabic) "استلام المكافأة (+${badge.pointsReward} نقطة)" else "Claim (+${badge.pointsReward} pts)",
                                color = Color.Black,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
