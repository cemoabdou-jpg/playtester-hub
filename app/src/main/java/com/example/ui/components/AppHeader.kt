package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AuthUser
import com.example.ui.util.L

@Composable
fun AppHeader(
    user: AuthUser,
    onOpenMonetization: () -> Unit,
    onOpenGrowth: () -> Unit,
    onOpenAudit: () -> Unit,
    onOpenLeaderboard: () -> Unit,
    onOpenTheme: () -> Unit = {},
    onOpenLuckySpin: () -> Unit = {},
    onOpenAdmin: () -> Unit = {},
    unreadNotificationsCount: Int = 0,
    onOpenNotifications: () -> Unit = {},
    onOpenDeveloperInbox: () -> Unit = {},
    onOpenComplianceMatrix: () -> Unit = {},
    onOpenLogoEvents: () -> Unit = {}
) {
    var showMenu by remember { mutableStateOf(false) }
    val isOwner = user.isPlatformOwner

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                DynamicEventLogo(
                    size = 38.dp,
                    isOwner = isOwner,
                    showBadge = true,
                    onClick = { onOpenLogoEvents() }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            L.t("منصة مختبري التطبيقات", "PlayTester Hub"),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            maxLines = 1,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (isOwner) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Surface(
                                color = Color(0xFF1E88E5),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    L.t("صاحب المنصة", "Owner"),
                                    color = Color.White,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }
                    Text(
                        L.t("تبادل المختبرين الحقيقيين 14 يوماً", "14-Day Real Tester Exchange"),
                        fontSize = 10.sp,
                        maxLines = 1,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // Coins Pill (min 48dp accessible height wrapper)
                Surface(
                    color = Color(0xFFFFB300).copy(alpha = 0.16f),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .heightIn(min = 36.dp)
                        .clickable { onOpenMonetization() }
                        .testTag("header_coins_pill")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.MonetizationOn,
                            contentDescription = L.t("رصيد العملات", "Coins Balance"),
                            tint = Color(0xFFFF8F00),
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "${user.points}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFFE65100)
                        )
                    }
                }

                // Notifications Bell with Badge (48dp touch target)
                IconButton(
                    onClick = onOpenNotifications,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("header_notifications_button")
                ) {
                    BadgedBox(
                        badge = {
                            if (unreadNotificationsCount > 0) {
                                Badge(
                                    containerColor = Color(0xFFE53935),
                                    contentColor = Color.White
                                ) {
                                    Text("$unreadNotificationsCount")
                                }
                            }
                        }
                    ) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = L.t("إشعارات المختبرين", "Tester Notifications"),
                            tint = if (unreadNotificationsCount > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(21.dp)
                        )
                    }
                }

                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("header_more_menu_button")
                    ) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = L.t("أدوات إضافية", "More Tools"),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(L.t("🔔 إشعارات نشاط المختبرين", "🔔 Tester Activity Notifications"))
                                    if (unreadNotificationsCount > 0) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            color = Color(0xFFE53935),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Text(
                                                L.t("$unreadNotificationsCount جديد", "$unreadNotificationsCount New"),
                                                color = Color.White,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                }
                            },
                            onClick = {
                                showMenu = false
                                onOpenNotifications()
                            },
                            leadingIcon = { Icon(Icons.Default.Notifications, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
                        )
                        DropdownMenuItem(
                            text = { Text(L.t("💬 محادثاتي مع المطورين", "💬 Developer Chats")) },
                            onClick = {
                                showMenu = false
                                onOpenDeveloperInbox()
                            },
                            leadingIcon = { Icon(Icons.Default.Chat, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
                        )
                        DropdownMenuItem(
                            text = { Text(L.t("🎡 عجلة الحظ والمكافأة اليومية", "🎡 Lucky Spin & Daily Wheel")) },
                            onClick = {
                                showMenu = false
                                onOpenLuckySpin()
                            },
                            leadingIcon = { Icon(Icons.Default.Casino, contentDescription = null, tint = Color(0xFF7C3AED)) }
                        )
                        DropdownMenuItem(
                            text = { Text(L.t("🏆 لوحة الصدارة ومكافآت الأوسمة", "🏆 Leaderboard & Badges")) },
                            onClick = {
                                showMenu = false
                                onOpenLeaderboard()
                            },
                            leadingIcon = { Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color(0xFFFF8F00)) }
                        )
                        DropdownMenuItem(
                            text = { Text(L.t("🛡️ مصفوفة الـ 14 يوماً ورادار الامتثال", "🛡️ 14-Day Matrix & Compliance Radar")) },
                            onClick = {
                                showMenu = false
                                onOpenComplianceMatrix()
                            },
                            leadingIcon = { Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF00875A)) }
                        )
                        DropdownMenuItem(
                            text = { Text(L.t("📄 تقرير التدقيق الرسمي", "📄 Official Audit Report")) },
                            onClick = {
                                showMenu = false
                                onOpenAudit()
                            },
                            leadingIcon = { Icon(Icons.Default.Assessment, contentDescription = null, tint = Color(0xFF00875A)) }
                        )
                        DropdownMenuItem(
                            text = { Text(L.t("🚀 جذب المطورين وشبكة التبادل", "🚀 Developer Growth & Referral Hub")) },
                            onClick = {
                                showMenu = false
                                onOpenGrowth()
                            },
                            leadingIcon = { Icon(Icons.Default.GroupAdd, contentDescription = null, tint = Color(0xFF1E88E5)) }
                        )
                        DropdownMenuItem(
                            text = { Text(L.t("💎 الباقات المميزة والتسريع", "💎 VIP Plans & Boosts")) },
                            onClick = {
                                showMenu = false
                                onOpenMonetization()
                            },
                            leadingIcon = { Icon(Icons.Default.Speed, contentDescription = null, tint = Color(0xFFFFB300)) }
                        )
                        DropdownMenuItem(
                            text = { Text(L.t("🎨 ثيمات وألوان التطبيق", "🎨 App Themes & Colors")) },
                            onClick = {
                                showMenu = false
                                onOpenTheme()
                            },
                            leadingIcon = { Icon(Icons.Default.Palette, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
                        )
                        DropdownMenuItem(
                            text = { Text(L.t("🎭 فعاليات وأجواء الشعار", "🎭 Logo Events & Effects")) },
                            onClick = {
                                showMenu = false
                                onOpenLogoEvents()
                            },
                            leadingIcon = { Icon(Icons.Default.Celebration, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
                        )
                        if (isOwner) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        L.t("🛡️ لوحة تحكم صاحب المنصة", "🛡️ Platform Owner Admin Panel"),
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E88E5)
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    onOpenAdmin()
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = Color(0xFF1E88E5))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
