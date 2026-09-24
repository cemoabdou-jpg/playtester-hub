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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.SilverAccent
import com.example.ui.theme.SuccessGreen

data class DeveloperRankInfo(
    val level: Int,
    val title: String,
    val currentXp: Int,
    val maxXpForNextLevel: Int,
    val badgeIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val accentColor: Color
)

fun calculateRankInfo(points: Int, streakDays: Int): DeveloperRankInfo {
    val totalXp = points + (streakDays * 25)

    return when {
        totalXp >= 2000 -> DeveloperRankInfo(
            level = 5,
            title = "خبير Google Play الماسى 💎",
            currentXp = totalXp,
            maxXpForNextLevel = 5000,
            badgeIcon = Icons.Default.Diamond,
            accentColor = CyanAccent
        )
        totalXp >= 1000 -> DeveloperRankInfo(
            level = 4,
            title = "قائد مجتمع المطورين 👑",
            currentXp = totalXp,
            maxXpForNextLevel = 2000,
            badgeIcon = Icons.Default.WorkspacePremium,
            accentColor = CrimsonRed
        )
        totalXp >= 500 -> DeveloperRankInfo(
            level = 3,
            title = "مهندس جودة واختبارات 🥇",
            currentXp = totalXp,
            maxXpForNextLevel = 1000,
            badgeIcon = Icons.Default.EmojiEvents,
            accentColor = AmberGold
        )
        totalXp >= 200 -> DeveloperRankInfo(
            level = 2,
            title = "فاحص معتمد 🥈",
            currentXp = totalXp,
            maxXpForNextLevel = 500,
            badgeIcon = Icons.Default.MilitaryTech,
            accentColor = SilverAccent
        )
        else -> DeveloperRankInfo(
            level = 1,
            title = "مطور واعد 🥉",
            currentXp = totalXp,
            maxXpForNextLevel = 200,
            badgeIcon = Icons.Default.AutoAwesome,
            accentColor = SuccessGreen
        )
    }
}

@Composable
fun DeveloperXpRankCard(
    points: Int,
    streakDays: Int,
    modifier: Modifier = Modifier
) {
    val rankInfo = calculateRankInfo(points, streakDays)
    val progress = (rankInfo.currentXp.toFloat() / rankInfo.maxXpForNextLevel.toFloat()).coerceIn(0f, 1f)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        border = BorderStroke(1.dp, rankInfo.accentColor.copy(alpha = 0.4f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("developer_xp_rank_card")
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = rankInfo.accentColor.copy(alpha = 0.18f),
                border = BorderStroke(1.5.dp, rankInfo.accentColor),
                modifier = Modifier.size(52.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = rankInfo.badgeIcon,
                        contentDescription = null,
                        tint = rankInfo.accentColor,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = rankInfo.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "المستوى ${rankInfo.level} • ${rankInfo.currentXp} XP",
                            style = MaterialTheme.typography.bodySmall,
                            color = rankInfo.accentColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = rankInfo.accentColor.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "Lvl ${rankInfo.level}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = rankInfo.accentColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = rankInfo.accentColor,
                    trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "متبقي ${rankInfo.maxXpForNextLevel - rankInfo.currentXp} XP للترقية التالية 🚀",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
