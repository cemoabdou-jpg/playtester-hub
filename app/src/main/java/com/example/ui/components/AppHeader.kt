package com.example.ui.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AuthUser
import com.example.data.model.UserWallet
import com.example.ui.theme.AmberGold
import com.example.ui.theme.PlayGreen
import com.example.ui.util.AppLanguage
import com.example.ui.util.L

/**
 * Streamlined, calm, and uncluttered top application bar.
 * Keeps only essential glanceable items (Logo, Points, Notifications, Profile Menu).
 * Consolidates secondary quick switches inside a sleek popup menu to prevent visual crowding.
 */
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
    val isArabic = language == AppLanguage.AR
    var showMenu by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .testTag("app_header"),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
        ),
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 7.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Clean Identity & Points Pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(enabled = onProfileClick != null) { onProfileClick?.invoke() }
                    .padding(vertical = 2.dp)
            ) {
                // Sleek App Emblem
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = PlayGreen.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, PlayGreen.copy(alpha = 0.3f)),
                    modifier = Modifier.size(34.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = if (isArabic) "شعار التطبيق" else "App Logo",
                            tint = PlayGreen,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "PlayTester",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            letterSpacing = 0.2.sp
                        )
                        if (currentUser?.role == "ADMIN") {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = AmberGold.copy(alpha = 0.18f),
                                border = BorderStroke(1.dp, AmberGold.copy(alpha = 0.5f))
                            ) {
                                Text(
                                    text = if (isArabic) "👑 مالك" else "👑 Admin",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = AmberGold,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = currentUser?.displayName?.ifBlank { null }
                            ?: if (isArabic) "تبادل المختبرين" else "Tester Hub",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
            }

            // Right: Glanceable points + Notification bell + Consolidated Profile & Quick Menu
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Points pill (Tap to view points center)
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = PlayGreen.copy(alpha = 0.10f),
                    border = BorderStroke(1.dp, PlayGreen.copy(alpha = 0.28f)),
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .clickable(enabled = onPointsClick != null || onBadgesClick != null) {
                            if (onPointsClick != null) onPointsClick.invoke() else onBadgesClick?.invoke()
                        }
                        .testTag("header_points_pill")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = if (isArabic) "النقاط" else "Points",
                            tint = PlayGreen,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${wallet.points}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = PlayGreen
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
                                        containerColor = PlayGreen,
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
                                tint = if (unreadNotificationsCount > 0) PlayGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Consolidated Options / Profile Menu (eliminates header clutter)
                Box {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .clickable { showMenu = true }
                            .testTag("btn_header_menu")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            val initial = currentUser?.displayName?.firstOrNull()?.uppercase() ?: ""
                            if (initial.isNotBlank()) {
                                Text(
                                    text = initial,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "Options",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    // Sleek, organized dropdown popup
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        shape = RoundedCornerShape(14.dp),
                        containerColor = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                        modifier = Modifier.width(220.dp)
                    ) {
                        // Profile / Settings
                        if (onProfileClick != null) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = if (isArabic) "الملف الشخصي والإعدادات" else "Profile & Settings",
                                        fontWeight = FontWeight.Medium
                                    )
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(18.dp))
                                },
                                onClick = {
                                    showMenu = false
                                    onProfileClick.invoke()
                                }
                            )
                        }

                        // Theme Mode Toggle
                        if (onToggleTheme != null) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = if (isDarkTheme) (if (isArabic) "الوضع الفاتح ☀️" else "Light Mode ☀️")
                                        else (if (isArabic) "الوضع الداكن 🌙" else "Dark Mode 🌙"),
                                        fontWeight = FontWeight.Medium
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    onToggleTheme.invoke()
                                }
                            )
                        }

                        // Language Switcher
                        if (onToggleLanguage != null) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = if (language == AppLanguage.AR) "Switch to English" else "التبديل إلى العربية",
                                        fontWeight = FontWeight.Medium
                                    )
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Translate, contentDescription = null, modifier = Modifier.size(18.dp))
                                },
                                onClick = {
                                    showMenu = false
                                    onToggleLanguage.invoke()
                                }
                            )
                        }

                        // Simplified Mode Switch
                        if (onToggleSimplifiedMode != null) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = if (isSimplifiedMode) (if (isArabic) "الوضع المتقدم" else "Full Mode")
                                        else (if (isArabic) "الوضع المبسط ⚡" else "Simplified Mode ⚡"),
                                        fontWeight = FontWeight.Medium
                                    )
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.ViewAgenda, contentDescription = null, modifier = Modifier.size(18.dp))
                                },
                                onClick = {
                                    showMenu = false
                                    onToggleSimplifiedMode.invoke()
                                }
                            )
                        }

                        // Upgrade guest if applicable
                        if (currentUser?.role == "GUEST" || currentUser?.authProvider == "GUEST") {
                            if (onUpgradeGuestClick != null) {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = if (isArabic) "ترقية الحساب وحفظ البيانات" else "Upgrade Account",
                                            fontWeight = FontWeight.Bold,
                                            color = PlayGreen
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Default.Shield, contentDescription = null, tint = PlayGreen, modifier = Modifier.size(18.dp))
                                    },
                                    onClick = {
                                        showMenu = false
                                        onUpgradeGuestClick.invoke()
                                    }
                                )
                            }
                        }

                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        // Logout
                        if (onLogout != null) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = if (isArabic) "تسجيل الخروج" else "Sign Out",
                                        color = MaterialTheme.colorScheme.error,
                                        fontWeight = FontWeight.Medium
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Logout,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    onLogout.invoke()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
