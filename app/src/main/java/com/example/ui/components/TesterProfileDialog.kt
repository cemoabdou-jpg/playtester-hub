package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import com.example.data.model.CommunityApp
import com.example.data.model.TesterProfileData
import com.example.ui.util.L
import com.example.ui.viewmodel.TesterViewModel

@Composable
fun TesterProfileDialog(
    profile: TesterProfileData,
    viewModel: TesterViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val joinedTests by viewModel.joinedTests.collectAsState()
    val displayName = L.translateKnown(profile.name)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.90f),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            shadowElevation = 10.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Close, contentDescription = L.t("إغلاق", "Close"))
                    }
                    Text(
                        L.t("الملف الشخصي للمختبر", "Tester Profile"),
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Surface(
                        color = Color(0xFF00875A).copy(alpha = 0.12f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            L.t("تبادل معتمد 🤝", "Verified Swap 🤝"),
                            color = Color(0xFF00875A),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(contentAlignment = Alignment.BottomEnd) {
                                    Surface(
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(64.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = displayName.firstOrNull()?.toString() ?: L.t("م", "T"),
                                                color = Color.White,
                                                fontSize = 26.sp,
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                        }
                                    }
                                    Surface(
                                        color = Color(0xFF00C853),
                                        shape = CircleShape,
                                        border = BorderStroke(2.dp, MaterialTheme.colorScheme.surface),
                                        modifier = Modifier.size(18.dp)
                                    ) {}
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    displayName,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Text(
                                    profile.email,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Surface(
                                    color = Color(0xFFFFB300).copy(alpha = 0.18f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(
                                        L.translateKnown(profile.badge),
                                        color = Color(0xFFD97706),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("${profile.testsCompleted}", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                                        Text(L.t("اختبار مكتمل", "Tests Done"), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("★ ${profile.rating}", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = Color(0xFFFF8F00))
                                        Text(L.t("تقييم النشاط", "Activity Rating"), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("${profile.reputationPoints}", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = Color(0xFF00875A))
                                        Text(L.t("نقاط السمعة", "Reputation"), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Surface(
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            L.t("جهاز الاختبار المسجل: ${profile.deviceModel}", "Registered Test Device: ${profile.deviceModel}"),
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedButton(
                                    onClick = {
                                        viewModel.openDeveloperChat(
                                            recipientEmail = profile.email,
                                            recipientName = profile.name,
                                            appName = profile.apps.firstOrNull()?.name
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                                    contentPadding = PaddingValues(vertical = 8.dp)
                                ) {
                                    Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        L.t("💬 فتح محادثة تنسيق التبادل المباشر", "💬 Open Direct Test Swap Chat"),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Apps, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    L.t("تطبيقات هذا المطور (المتاحة للاختبار):", "Developer's Apps (Available to Test):"),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Text(
                                L.t("${profile.apps.size} تطبيق", "${profile.apps.size} apps"),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            L.t(
                                "بادر باختبار تطبيقه بالمثل لإكمال شرط الـ 14 يوماً معاً وكسب نقاط إضافية 🤝",
                                "Test their app in return to complete the 14-day requirement together & earn bonus coins 🤝"
                            ),
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (profile.apps.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF00875A), modifier = Modifier.size(36.dp))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        L.t(
                                            "أكمل هذا المطور متطلبات تطبيقاته أو لا توجد لديه تطبيقات معلقة حالياً",
                                            "This developer has no pending apps requiring tests right now"
                                        ),
                                        fontSize = 12.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    } else {
                        items(profile.apps) { app ->
                            val isAlreadyJoined = joinedTests.any { it.packageName == app.packageName }

                            ElevatedCard(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            modifier = Modifier.weight(1f),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            AppIconImage(
                                                iconUrl = app.iconUrl,
                                                appName = app.name,
                                                category = app.category,
                                                packageName = app.packageName,
                                                modifier = Modifier.size(46.dp),
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(
                                                    app.name,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 14.sp,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Text(
                                                    "${L.localizeCategory(app.category)} • ${app.packageName}",
                                                    fontSize = 10.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }

                                        Surface(
                                            color = Color(0xFFFFB300).copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                L.t("+${app.pointsReward} عملة", "+${app.pointsReward} coins"),
                                                color = Color(0xFFFF8F00),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            L.t("المختبرون الحاليون: ${app.currentTesters} / ${app.targetTesters}", "Current Testers: ${app.currentTesters} / ${app.targetTesters}"),
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            L.t("المدة المطلوبة: ${app.requiredDays} يوماً", "Required: ${app.requiredDays} days"),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    if (isAlreadyJoined) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Surface(
                                                color = Color(0xFFE8F5E9),
                                                shape = RoundedCornerShape(10.dp),
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(40.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF00796B), modifier = Modifier.size(16.dp))
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text(
                                                            L.t("أنت منضم لاختباره بالفعل ✓", "Already Joined ✓"),
                                                            color = Color(0xFF00796B),
                                                            fontSize = 12.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }
                                                }
                                            }

                                            OutlinedButton(
                                                onClick = {
                                                    val url = app.androidOptInUrl.ifBlank { app.webOptInUrl }
                                                    if (url.isNotBlank()) {
                                                        try {
                                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                                                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                            }
                                                            context.startActivity(intent)
                                                        } catch (_: Exception) {
                                                            Toast.makeText(context, L.t("الرابط: $url", "Link: $url"), Toast.LENGTH_SHORT).show()
                                                        }
                                                    }
                                                },
                                                shape = RoundedCornerShape(10.dp),
                                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                                modifier = Modifier.height(40.dp)
                                            ) {
                                                Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(L.t("فتح الرابط", "Open Link"), fontSize = 11.sp)
                                            }
                                        }
                                    } else {
                                        Button(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(42.dp),
                                            onClick = {
                                                val joined = viewModel.joinCommunityApp(app)
                                                if (joined) {
                                                    Toast.makeText(
                                                        context,
                                                        L.t(
                                                            "🎉 تم الانضمام لاختبار تطبيق ${app.name} بنجاح! شكراً على تبادل الدعم (+${app.pointsReward} عملة)",
                                                            "🎉 Joined ${app.name} test! Thanks for mutual support (+${app.pointsReward} coins)"
                                                        ),
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                    val url = app.androidOptInUrl.ifBlank { app.webOptInUrl }
                                                    if (url.isNotBlank()) {
                                                        try {
                                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                                                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                            }
                                                            context.startActivity(intent)
                                                        } catch (_: Exception) {}
                                                    }
                                                } else {
                                                    Toast.makeText(
                                                        context,
                                                        L.t("أنت منضم بالفعل لاختبار هذا التطبيق", "You have already joined this app test"),
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            },
                                            shape = RoundedCornerShape(10.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00875A))
                                        ) {
                                            Icon(Icons.Default.Handshake, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                L.t("🚀 انضم لاختبار تطبيقه الآن (تبادل الاختبار)", "🚀 Join Their App Test Now (Swap)"),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(L.t("إغلاق الملف الشخصي", "Close Profile"))
                }
            }
        }
    }
}
