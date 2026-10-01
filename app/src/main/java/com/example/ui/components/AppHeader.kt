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
import androidx.compose.material.icons.filled.RocketLaunch
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
import com.example.ui.theme.EmeraldGradientStart
import com.example.ui.theme.PlayGreen
import com.example.ui.util.AppLanguage
import com.example.ui.util.L

@Composable
fun AppHeader(
    wallet: UserWallet,
    currentUser: AuthUser? = null,
    unreadNotificationsCount: Int = 0,
    isSimplifiedMode: Boolean = false,
    isDarkTheme: Boolean = false,
    language: AppLanguage = AppLanguage.AR,
    onToggleLanguage: (() -> Unit)? = null,
    onToggleTheme: (() -> Unit)? = null,
    onNotificationsClick: (() -> Unit)? = null,
    onBadgesClick: (() -> Unit)? = null,
    onPointsClick: (() -> Unit)? = null,
    onProfileClick: (() -> Unit)? = null,
    onOpenOnboarding: (() -> Unit)? = null,
    onToggleSimplifiedMode: (() -> Unit)? = null,
    onUpgradeGuestClick: (() -> Unit)? = null,
    onLogout: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val s = L.strings()
    val isArabic = language == AppLanguage.AR

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .testTag("app_header"),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
        ),
        tonalElevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left branding & User Identity
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(enabled = onProfileClick != null) { onProfileClick?.invoke() }
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(EmeraldGradientStart, CyanAccent)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = if (isArabic) "شعار التطبيق" else "App Logo",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "PlayTester Hub",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            letterSpacing = 0.3.sp
                        )
                        if (currentUser?.role == "ADMIN") {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = AmberGold
                            ) {
                                Text(
                                    text = if (isArabic) "👑 مالك" else "👑 Owner",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        } else if (currentUser?.role == "GUEST" || currentUser?.authProvider == "GUEST") {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = PlayGreen.copy(alpha = 0.18f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, PlayGreen.copy(alpha = 0.6f)),
                                modifier = Modifier
                                    .clickable(enabled = onUpgradeGuestClick != null) { onUpgradeGuestClick?.invoke() }
                                    .testTag("btn_header_guest_upgrade")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.RocketLaunch,
                                        contentDescription = null,
                                        tint = PlayGreen,
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = if (isArabic) "ترقية الحساب ⚡" else "Upgrade ⚡",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = PlayGreen,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                    Text(
                        text = currentUser?.displayName ?: if (isArabic) "تبادل المختبرين" else "Tester Exchange",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
            }

            // Right Action Controls & Stats Pill
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Points pill
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = AmberGold.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AmberGold.copy(alpha = 0.35f)),
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable(enabled = onPointsClick != null || onBadgesClick != null) {
                            if (onPointsClick != null) onPointsClick.invoke() else onBadgesClick?.invoke()
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = if (isArabic) "النقاط" else "Points",
                            tint = AmberGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
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
                    shape = RoundedCornerShape(20.dp),
                    color = PlayGreen.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PlayGreen.copy(alpha = 0.35f)),
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable(enabled = onBadgesClick != null) { onBadgesClick?.invoke() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = if (isArabic) "أيام الاستمرار" else "Daily Streak",
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

                // Language Switcher Quick Pill Button
                if (onToggleLanguage != null) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onToggleLanguage.invoke() }
                            .testTag("btn_language_toggle")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (language == AppLanguage.AR) "EN" else "عربي",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                // Dark / Light Theme Quick Toggle
                if (onToggleTheme != null) {
                    IconButton(
                        onClick = onToggleTheme,
                        modifier = Modifier
                            .size(34.dp)
                            .testTag("btn_theme_toggle")
                    ) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = if (isDarkTheme) {
                                if (isArabic) "التبديل إلى الوضع الفاتح" else "Switch to Light Mode"
                            } else {
                                if (isArabic) "التبديل إلى الوضع الداكن" else "Switch to Dark Mode"
                            },
                            tint = if (isDarkTheme) AmberGold else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Notification Bell with unread badge
                if (onNotificationsClick != null) {
                    IconButton(
                        onClick = onNotificationsClick,
                        modifier = Modifier
                            .size(34.dp)
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
                                contentDescription = if (isArabic) "الإشعارات" else "Notifications",
                                tint = if (unreadNotificationsCount > 0) CyanAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
