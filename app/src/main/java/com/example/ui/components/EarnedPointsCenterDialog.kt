package com.example.ui.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserWallet
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.PlayGreen
import com.example.ui.theme.SuccessGreen
import com.example.ui.util.AppLanguage
import com.example.ui.util.LocalAppLanguage

data class PointsEarningChannel(
    val titleAr: String,
    val titleEn: String,
    val rewardText: String,
    val descriptionAr: String,
    val descriptionEn: String,
    val icon: ImageVector,
    val iconTint: Color
)

@Composable
fun EarnedPointsCenterDialog(
    wallet: UserWallet,
    onDismiss: () -> Unit,
    onOpenBadges: () -> Unit = {}
) {
    val language = LocalAppLanguage.current
    val isArabic = language == AppLanguage.AR

    val earningChannels = listOf(
        PointsEarningChannel(
            titleAr = "فحص التطبيقات اليومي (14 يوماً)",
            titleEn = "Daily 14-Day App Testing",
            rewardText = "+25 إلى +60 نقطة",
            descriptionAr = "افتح تطبيق منضم إليه وأكّد تجربتك اليومية لكسب +25 نقطة يومياً، ومكافأة كبرى +60 نقطة عند إتمام اليوم الـ 14.",
            descriptionEn = "Open joined apps and confirm your daily test for +25 pts daily, plus +60 pts bonus on day 14.",
            icon = Icons.Default.CheckCircle,
            iconTint = SuccessGreen
        ),
        PointsEarningChannel(
            titleAr = "شراكات التبادل (1-on-1)",
            titleEn = "Reciprocal Exchange Partnerships",
            rewardText = "+25 إلى +200 نقطة",
            descriptionAr = "تبادل الاختبار مع مطور شريك يومياً لكسب +25 نقطة يومياً ومكافأة إتمام ضخمة +200 نقطة.",
            descriptionEn = "Exchange daily tests with partner developers for +25 pts/day and +200 pts upon 14-day completion.",
            icon = Icons.Default.Handshake,
            iconTint = CyanAccent
        ),
        PointsEarningChannel(
            titleAr = "كتابة المراجعات والتقييمات",
            titleEn = "Reviews & Detailed Feedback",
            rewardText = "+25 نقطة",
            descriptionAr = "اكتب تقييمات بناءة وملاحظات ومقترحات لتحسين تطبيقات المطورين لكسب +25 نقطة عن كل تقييم.",
            descriptionEn = "Submit constructive feedback, bug reports, and ratings to earn +25 points per review.",
            icon = Icons.Default.RateReview,
            iconTint = AmberGold
        ),
        PointsEarningChannel(
            titleAr = "الانضمام لاختبار التطبيقات الجديدة",
            titleEn = "Joining New App Tests",
            rewardText = "+10 نقاط",
            descriptionAr = "احصل على +10 نقاط مكافأة فورية بمجرد الانضمام لاختبار أي تطبيق جديد في المجتمع.",
            descriptionEn = "Earn an instant +10 bonus points whenever you join a new community app test.",
            icon = Icons.Default.Stars,
            iconTint = AmberGold
        ),
        PointsEarningChannel(
            titleAr = "فتح الرتب والشارات التقديرية",
            titleEn = "Unlocking Rank Badges",
            rewardText = "+25 إلى +100 نقطة",
            descriptionAr = "كل شارة إنجاز تفتحها تمنحك مكافأة نقاط مباشرة قابلة للاستلام.",
            descriptionEn = "Every unlocked achievement badge awards direct claimable bonus points.",
            icon = Icons.Default.EmojiEvents,
            iconTint = AmberGold
        ),
        PointsEarningChannel(
            titleAr = "سلسلة الأيام والاستمرار (Streak)",
            titleEn = "Daily Streak Continuity",
            rewardText = "+مكافآت مضاعفة",
            descriptionAr = "المحافظة على سلسلة الفحص اليومي المتواصل ترفع رتبتك وتضاعف نقاطك ومكافآتك.",
            descriptionEn = "Maintaining your consecutive testing streak boosts your rank and multiplies rewards.",
            icon = Icons.Default.Bolt,
            iconTint = PlayGreen
        )
    )

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
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = null,
                                tint = AmberGold,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isArabic) "مركز النقاط المكتسبة 💎" else "Earned Points Center 💎",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isArabic) "كيفية تجميع النقاط والاستفادة منها" else "How to earn & use testing points",
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
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Total Points Header Card
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
                    border = BorderStroke(1.dp, AmberGold.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isArabic) "رصيدك الحالي من النقاط:" else "Your Current Points Balance:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${wallet.points}",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AmberGold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isArabic) "نقطة مكتسبة" else "Earned Pts",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = AmberGold
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = PlayGreen.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, PlayGreen.copy(alpha = 0.4f))
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "${wallet.currentStreak}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = PlayGreen
                                )
                                Text(
                                    text = if (isArabic) "أيام استمرار" else "Days Streak",
                                    fontSize = 10.sp,
                                    color = PlayGreen
                                )
                            }
                        }
                    }
                }

                Text(
                    text = if (isArabic) "طرق كسب النقاط المتاحة في المنصة:" else "Ways to Earn Points in the Hub:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                // Earning Channels List
                earningChannels.forEach { channel ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = channel.iconTint.copy(alpha = 0.15f),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = channel.icon,
                                        contentDescription = null,
                                        tint = channel.iconTint,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (isArabic) channel.titleAr else channel.titleEn,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = channel.iconTint.copy(alpha = 0.12f)
                                    ) {
                                        Text(
                                            text = channel.rewardText,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = channel.iconTint,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(3.dp))

                                Text(
                                    text = if (isArabic) channel.descriptionAr else channel.descriptionEn,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onDismiss()
                    onOpenBadges()
                },
                colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (isArabic) "عرض الشارات واستلام المكافآت" else "View Badges & Rewards", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(10.dp)) {
                Text(if (isArabic) "إغلاق" else "Close")
            }
        }
    )
}
