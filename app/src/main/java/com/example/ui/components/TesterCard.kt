package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.CommunityApp
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.PlayGreen
import java.io.File

@Composable
fun TesterCard(
    app: CommunityApp,
    isAlreadyJoined: Boolean,
    onJoinClick: () -> Unit,
    onRecordOpened: (packageName: String, appName: String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val progress = (app.testersCurrent.toFloat() / app.testersTarget.toFloat()).coerceIn(0f, 1f)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("tester_card_${app.id}"),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            1.dp,
            if (app.isFeatured) CyanAccent.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
        ),
        tonalElevation = if (app.isFeatured) 3.dp else 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row: Featured badge, Priority score, Points Reward (Category removed)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (app.isFeatured) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = AmberGold.copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "مميز",
                                    tint = AmberGold,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "مثبت في القمة",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AmberGold,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    if (app.priorityScore > 0) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = PlayGreen.copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "⚡ أولوية #${app.priorityScore}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PlayGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Reward points
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = "نقاط",
                        tint = AmberGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "+${app.rewardPoints} نقطة",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = AmberGold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main App Info Row: Clickable App Icon (Opens app for testing) + Name & Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Clickable App Icon (Launches app & logs testing activity)
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable {
                            launchAppOrTestLink(
                                context = context,
                                packageName = app.packageName,
                                appName = app.name,
                                androidOptInUrl = app.androidOptInUrl,
                                webOptInUrl = app.webOptInUrl,
                                googleGroupUrl = app.googleGroupUrl,
                                onRecordOpened = onRecordOpened
                            )
                        }
                        .testTag("app_icon_clickable_${app.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    if (app.iconUrl.isNotBlank()) {
                        AsyncImage(
                            model = if (app.iconUrl.startsWith("/")) File(app.iconUrl) else app.iconUrl,
                            contentDescription = "أيقونة ${app.name}",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        // Styled fallback with app's initial letter
                        Text(
                            text = app.name.take(2).uppercase(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = PlayGreen
                        )
                    }

                    // Little overlay indicator that clicking opens the app
                    Surface(
                        shape = CircleShape,
                        color = PlayGreen.copy(alpha = 0.85f),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(18.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "فتح",
                            tint = Color.White,
                            modifier = Modifier.size(12.dp).padding(2.dp)
                        )
                    }
                }

                // App Title, Developer Name, and Package
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = app.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "بواسطة: ${app.developerName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "⚡ اضغط على الأيقونة للفتح والاختبار اليومي",
                        style = MaterialTheme.typography.labelSmall,
                        color = PlayGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Description
            if (app.description.isNotBlank()) {
                Text(
                    text = app.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Testers Progress
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "المختبرين المنضمين:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${app.testersCurrent} من ${app.testersTarget} مختبر",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (app.testersCurrent >= app.testersTarget) PlayGreen else CyanAccent
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (app.testersCurrent >= app.testersTarget) PlayGreen else CyanAccent,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons (Streamlined to 2 clear, spacious actions)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Group link
                OutlinedButton(
                    onClick = { openUrl(context, app.googleGroupUrl) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("open_group_${app.id}"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = "مجموعة الاختبار",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("المجموعة", maxLines = 1, overflow = TextOverflow.Ellipsis)
                }

                if (!isAlreadyJoined) {
                    // Join button (Primary action when not yet joined)
                    Button(
                        onClick = onJoinClick,
                        modifier = Modifier
                            .weight(1.3f)
                            .testTag("join_button_${app.id}"),
                        colors = ButtonDefaults.buttonColors(containerColor = PlayGreen),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("انضم للاختبار", fontWeight = FontWeight.Bold)
                    }
                } else {
                    // Launch & Test button (Primary action once joined)
                    Button(
                        onClick = {
                            launchAppOrTestLink(
                                context = context,
                                packageName = app.packageName,
                                appName = app.name,
                                androidOptInUrl = app.androidOptInUrl,
                                webOptInUrl = app.webOptInUrl,
                                googleGroupUrl = app.googleGroupUrl,
                                onRecordOpened = onRecordOpened
                            )
                        },
                        modifier = Modifier
                            .weight(1.3f)
                            .testTag("open_optin_${app.id}"),
                        colors = ButtonDefaults.buttonColors(containerColor = PlayGreen),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Android,
                            contentDescription = "فتح واختبار",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("فتح واختبار", maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }
    }
}

fun launchAppOrTestLink(
    context: Context,
    packageName: String,
    appName: String,
    androidOptInUrl: String,
    webOptInUrl: String,
    googleGroupUrl: String,
    onRecordOpened: (String, String) -> Unit
) {
    val pm = context.packageManager
    val launchIntent = pm.getLaunchIntentForPackage(packageName)
    if (launchIntent != null) {
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(launchIntent)
        Toast.makeText(context, "جاري فتح تطبيق $appName وتسجيل مشاركتك...", Toast.LENGTH_SHORT).show()
        onRecordOpened(packageName, appName)
    } else {
        val targetUrl = when {
            androidOptInUrl.isNotBlank() -> androidOptInUrl
            webOptInUrl.isNotBlank() -> webOptInUrl
            googleGroupUrl.isNotBlank() -> googleGroupUrl
            else -> "https://play.google.com/store/apps/details?id=$packageName"
        }
        openUrl(context, targetUrl)
        Toast.makeText(context, "جاري فتح صفحة تثبيت واختبار $appName وتسجيل تجربتك...", Toast.LENGTH_SHORT).show()
        onRecordOpened(packageName, appName)
    }
}

fun openUrl(context: Context, url: String) {
    if (url.isBlank()) {
        Toast.makeText(context, "الرابط غير متوفر حالياً", Toast.LENGTH_SHORT).show()
        return
    }
    try {
        val finalUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) {
            "https://$url"
        } else {
            url
        }
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(finalUrl)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "تعذر فتح الرابط", Toast.LENGTH_SHORT).show()
    }
}
