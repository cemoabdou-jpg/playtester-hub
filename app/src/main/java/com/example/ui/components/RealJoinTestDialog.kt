package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.CommunityApp
import com.example.ui.util.L

@Composable
fun RealJoinTestDialog(
    app: CommunityApp,
    onDismiss: () -> Unit,
    onConfirmJoin: () -> Unit
) {
    val context = LocalContext.current
    var hasJoinedGroup by remember { mutableStateOf(false) }
    var hasOpenedOptIn by remember { mutableStateOf(false) }

    val optInUrl = remember(app) {
        when {
            app.webOptInUrl.isNotBlank() -> app.webOptInUrl.trim()
            app.androidOptInUrl.isNotBlank() -> app.androidOptInUrl.trim()
            else -> "https://play.google.com/apps/testing/${app.packageName.trim()}"
        }
    }

    val hasCustomGroup = app.googleGroupUrl.isNotBlank()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(androidx.compose.foundation.rememberScrollState())
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = L.t("إغلاق", "Close"))
                    }

                    Text(
                        L.t("الانضمام لاختبار التطبيق", "Join App Test"),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Surface(
                        color = Color(0xFFFFB300).copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            L.t("+${app.pointsReward} عملة", "+${app.pointsReward} coins"),
                            color = Color(0xFFE65100),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
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
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(app.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(
                                L.t(
                                    "المطور: ${L.translateKnown(app.developerName)} • ${L.localizeCategory(app.category)}",
                                    "Dev: ${L.translateKnown(app.developerName)} • ${L.localizeCategory(app.category)}"
                                ),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                app.packageName,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }

                Surface(
                    color = Color(0xFFE3F2FD),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF90CAF9)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFF1976D2),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            L.t(
                                "نظام متجر جوجل بلاي: يتطلب الانضمام للمجموعة أولاً بحسابك، ثم فتح رابط الاختبار لتظهر لك صفحة التحميل بنجاح.",
                                "Google Play requirement: Join the tester group with your Google account first, then open the opt-in link so the download page loads properly."
                            ),
                            fontSize = 10.5.sp,
                            color = Color(0xFF0D47A1),
                            lineHeight = 15.sp
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = if (hasJoinedGroup) Color(0xFF00875A) else MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                    ),
                    color = if (hasJoinedGroup) Color(0xFFE8F5E9).copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    L.t("الخطوة 1: الانضمام لمجموعة جوجل", "Step 1: Join Google Group"),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (hasJoinedGroup) Color(0xFF00875A) else MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = if (hasCustomGroup) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        if (hasCustomGroup) L.t("مجموعة مخصصة", "Custom Group") else L.t("المجموعة العامة", "Public Group"),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (hasCustomGroup) Color(0xFF2E7D32) else Color(0xFFE65100),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            if (hasJoinedGroup) {
                                Surface(
                                    color = Color(0xFF00875A),
                                    shape = CircleShape,
                                    modifier = Modifier.size(18.dp)
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            if (hasCustomGroup) {
                                L.t(
                                    "انضم لمجموعة جوجل الخاصة بالمطور لمنح حسابك صلاحية التحميل في المتجر",
                                    "Join the developer's Google Group to grant your account store download access"
                                )
                            } else {
                                L.t(
                                    "انضم لمجموعة المنصة العامة لاعتماد بريدك كمختبر معتمد",
                                    "Join the platform public Google Group to authorize your email as a tester"
                                )
                            },
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                com.example.util.AppInstallVerifier.openGoogleGroup(context, app.googleGroupUrl)
                                hasJoinedGroup = true
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (hasJoinedGroup) Color(0xFF00875A) else Color(0xFF2E7D32)
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.Groups, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                if (hasJoinedGroup) L.t("تم فتح المجموعة بنجاح ✓ (إعادة الفتح)", "Group Opened ✓ (Re-open)")
                                else L.t("1. الانضمام إلى مجموعة الاختبار 👥", "1. Join Google Group 👥"),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = if (hasOpenedOptIn) Color(0xFF00875A) else MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                    ),
                    color = if (hasOpenedOptIn) Color(0xFFE8F5E9).copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                L.t("الخطوة 2: قبول الدعوة في متجر جوجل بلاي", "Step 2: Accept Invite on Google Play"),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (hasOpenedOptIn) Color(0xFF00875A) else MaterialTheme.colorScheme.onSurface
                            )
                            if (hasOpenedOptIn) {
                                Surface(
                                    color = Color(0xFF00875A),
                                    shape = CircleShape,
                                    modifier = Modifier.size(18.dp)
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            L.t(
                                "اضغط على زر الانضمام للاختبار ثم زر التنزيل من متجر جوجل بلاي لتثبيت التطبيق",
                                "Tap 'Become a Tester' and then 'Download it on Google Play' to install the app"
                            ),
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(optInUrl)).apply {
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    }
                                    context.startActivity(intent)
                                    hasOpenedOptIn = true
                                } catch (_: Exception) {
                                    Toast.makeText(context, L.t("الرابط: $optInUrl", "Link: $optInUrl"), Toast.LENGTH_LONG).show()
                                    hasOpenedOptIn = true
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (hasOpenedOptIn) Color(0xFF00875A) else MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                if (hasOpenedOptIn) L.t("تم فتح الرابط بنجاح ✓ (إعادة الفتح)", "Link Opened ✓ (Re-open)")
                                else L.t("2. فتح رابط تجربة التطبيق 🚀", "2. Open App Test Link (Web Opt-In) 🚀"),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Button(
                    onClick = {
                        if (!hasJoinedGroup && !hasOpenedOptIn) {
                            Toast.makeText(
                                context,
                                L.t(
                                    "يرجى فتح رابط مجموعة جوجل ورابط الاشتراك أولاً لإتمام الانضمام الفعلي",
                                    "Please open the Google Group and opt-in link first to confirm joining"
                                ),
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            onConfirmJoin()
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (hasJoinedGroup || hasOpenedOptIn) Color(0xFF00875A) else MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 11.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        L.t(
                            "تأكيد الانضمام الفعلي والبدء (+${app.pointsReward} عملة)",
                            "Confirm Joining & Start (+${app.pointsReward} coins)"
                        ),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(L.t("إلغاء والعودة", "Cancel & Back"), fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                }
            }
        }
    }
}
