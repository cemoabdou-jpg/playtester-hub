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
    onOpenTheme: () -> Unit = {}
) {
    var showMenu by remember { mutableStateOf(false) }

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
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        "PlayTester Hub",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "منصة تبادل المختبرين الرسمية",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = Color(0xFFFFB300).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.clickable { onOpenMonetization() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.MonetizationOn,
                            contentDescription = null,
                            tint = Color(0xFFFF8F00),
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            "${user.points}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFFFF8F00)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Icon(
                            Icons.Default.AddCircle,
                            contentDescription = "كسب عملات",
                            tint = Color(0xFFFF8F00),
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                IconButton(
                    onClick = onOpenLeaderboard,
                    modifier = Modifier.size(36.dp)
                ) {
                    Text("🏆", fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.width(4.dp))

                IconButton(
                    onClick = onOpenMonetization,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = "VIP",
                        tint = Color(0xFFFFB300),
                        modifier = Modifier.size(20.dp)
                    )
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
                            text = { Text("🏆 لوحة الصدارة ومكافآت الأوسمة") },
                            onClick = {
                                showMenu = false
                                onOpenLeaderboard()
                            },
                            leadingIcon = { Text("🏆", fontSize = 16.sp) }
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
                    }
                }
            }
        }
    }
}
