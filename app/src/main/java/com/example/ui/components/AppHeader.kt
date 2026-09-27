package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AuthUser
import com.example.data.model.UserWallet
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.PlayGreen

@Composable
fun AppHeader(
    wallet: UserWallet,
    currentUser: AuthUser? = null,
    unreadNotificationsCount: Int = 0,
    isSimplifiedMode: Boolean = false,
    isDarkTheme: Boolean = false,
    onToggleTheme: (() -> Unit)? = null,
    onNotificationsClick: (() -> Unit)? = null,
    onBadgesClick: (() -> Unit)? = null,
    onProfileClick: (() -> Unit)? = null,
    onOpenOnboarding: (() -> Unit)? = null,
    onToggleSimplifiedMode: (() -> Unit)? = null,
    onLogout: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .testTag("app_header"),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left branding & User Identity
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable(enabled = onProfileClick != null) { onProfileClick?.invoke() }
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(PlayGreen, CyanAccent)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "شعار التطبيق",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "PlayTester Hub",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (currentUser?.role == "ADMIN") {
                            Spacer(modifier = Modifier.width(4.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = AmberGold
                            ) {
                                Text(
                                    text = "👑 مالك",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = currentUser?.displayName ?: "تبادل المختبرين",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
            }

            // Right Action Controls & Stats Pill
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Points pill
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = AmberGold.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AmberGold.copy(alpha = 0.3f)),
                    modifier = Modifier.clickable(enabled = onBadgesClick != null) { onBadgesClick?.invoke() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = "النقاط",
                            tint = AmberGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${wallet.points}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = AmberGold
                        )
                    }
                }

                // Streak pill
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = PlayGreen.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PlayGreen.copy(alpha = 0.3f)),
                    modifier = Modifier.clickable(enabled = onBadgesClick != null) { onBadgesClick?.invoke() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = "أيام الاستمرار",
                            tint = PlayGreen,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${wallet.currentStreak}d",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = PlayGreen
                        )
                    }
                }

                // Dark / Light Theme Quick Toggle
                if (onToggleTheme != null) {
                    IconButton(
                        onClick = onToggleTheme,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("btn_theme_toggle")
                    ) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = if (isDarkTheme) "التبديل إلى الوضع الفاتح" else "التبديل إلى الوضع الداكن",
                            tint = if (isDarkTheme) AmberGold else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                }

                // Notification Bell with unread badge
                if (onNotificationsClick != null) {
                    IconButton(
                        onClick = onNotificationsClick,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("btn_notifications_bell")
                    ) {
                        BadgedBox(
                            badge = {
                                if (unreadNotificationsCount > 0) {
                                    Badge(
                                        containerColor = CyanAccent,
                                        contentColor = Color.White
                                    ) {
                                        Text(
                                            text = if (unreadNotificationsCount > 9) "9+" else "$unreadNotificationsCount",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "الإشعارات",
                                tint = if (unreadNotificationsCount > 0) CyanAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(19.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
