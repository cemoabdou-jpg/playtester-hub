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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AuthUser

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
    onOpenComplianceMatrix: () -> Unit = {}
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
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = if (isOwner) Modifier.clickable { onOpenAdmin() } else Modifier
            ) {
                Surface(
                    color = if (isOwner) Color(0xFF1E88E5).copy(alpha = 0.15f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.size(34.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            if (isOwner) Icons.Default.AdminPanelSettings else Icons.Default.VerifiedUser,
                            contentDescription = if (isOwner) "لوحة تحكم صاحب المنصة" else "PlayTester Hub",
                            tint = if (isOwner) Color(0xFF1E88E5) else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "PlayTester Hub",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (isOwner) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Surface(
                                color = Color(0xFF1E88E5),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    "صاحب المنصة",
                                    color = Color.White,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }
                    Text(
                        "منصة تبادل المختبرين الرسمية",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Points Pill
                Surface(
                    color = Color(0xFFFFB300).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.clickable { onOpenMonetization() }
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
                            "${user.points}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color(0xFFFF8F00)
                        )
                    }
                }

                // Quick Action: Lucky Spin
                IconButton(
                    onClick = onOpenLuckySpin,
                    modifier = Modifier.size(34.dp)
                ) {
                    Text("🎡", fontSize = 16.sp)
                }

                // Notifications Bell with Badge
                IconButton(
                    onClick = onOpenNotifications,
                    modifier = Modifier.size(34.dp)
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
                            contentDescription = "إشعارات المختبرين",
                            tint = if (unreadNotificationsCount > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "أدوات إضافية",
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
                                    Text("🔔 إشعارات نشاط المختبرين")
                                    if (unreadNotificationsCount > 0) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            color = Color(0xFFE53935),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Text(
                                                "$unreadNotificationsCount جديد",
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
                            text = { Text("💬 محادثاتي مع المطورين") },
                            onClick = {
                                showMenu = false
                                onOpenDeveloperInbox()
                            },
                            leadingIcon = { Icon(Icons.Default.Chat, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
                        )
                        DropdownMenuItem(
                            text = { Text("🏆 لوحة الصدارة ومكافآت الأوسمة") },
                            onClick = {
                                showMenu = false
                                onOpenLeaderboard()
                            },
                            leadingIcon = { Text("🏆", fontSize = 16.sp) }
                        )
                        DropdownMenuItem(
                            text = { Text("🛡️ مصفوفة الـ 14 يوماً ورادار الامتثال") },
                            onClick = {
                                showMenu = false
                                onOpenComplianceMatrix()
                            },
                            leadingIcon = { Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF00875A)) }
                        )
                        DropdownMenuItem(
                            text = { Text("📄 تقرير التدقيق الرسمي (Audit)") },
                            onClick = {
                                showMenu = false
                                onOpenAudit()
                            },
                            leadingIcon = { Icon(Icons.Default.Assessment, contentDescription = null, tint = Color(0xFF00875A)) }
                        )
                        DropdownMenuItem(
                            text = { Text("🚀 جذب المطورين وشبكة التبادل") },
                            onClick = {
                                showMenu = false
                                onOpenGrowth()
                            },
                            leadingIcon = { Icon(Icons.Default.GroupAdd, contentDescription = null, tint = Color(0xFF1E88E5)) }
                        )
                        DropdownMenuItem(
                            text = { Text("💎 باقات الـ VIP والتسريع") },
                            onClick = {
                                showMenu = false
                                onOpenMonetization()
                            },
                            leadingIcon = { Icon(Icons.Default.Speed, contentDescription = null, tint = Color(0xFFFFB300)) }
                        )
                        DropdownMenuItem(
                            text = { Text("🎨 ثيمات وألوان التطبيق") },
                            onClick = {
                                showMenu = false
                                onOpenTheme()
                            },
                            leadingIcon = { Icon(Icons.Default.Palette, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
                        )
                        if (isOwner) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                            DropdownMenuItem(
                                text = { 
                                    Text(
                                        "🛡️ لوحة تحكم صاحب المنصة",
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
