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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.data.model.TesterFeedback
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.PlayGreen
import com.example.ui.util.AppLanguage
import com.example.ui.util.L
import com.example.ui.util.LocalAppLanguage
import com.example.util.GooglePlayScraperHelper
import java.io.File

@Composable
fun TesterCard(
    app: CommunityApp,
    isAlreadyJoined: Boolean,
    feedbacks: List<TesterFeedback> = emptyList(),
    onJoinClick: () -> Unit,
    onViewReviewsClick: (() -> Unit)? = null,
    onRecordOpened: (packageName: String, appName: String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val s = L.strings()
    val language = LocalAppLanguage.current
    val isArabic = language == AppLanguage.AR
    val progress = (app.testersCurrent.toFloat() / app.testersTarget.toFloat()).coerceIn(0f, 1f)

    var effectiveIconUrl by remember(app.iconUrl) { mutableStateOf(app.iconUrl) }

    LaunchedEffect(app.packageName, app.iconUrl) {
        if (effectiveIconUrl.isBlank() && app.packageName.isNotBlank()) {
            try {
                val info = GooglePlayScraperHelper.fetchAppInfo(app.packageName)
                if (info != null && info.iconUrl.isNotBlank()) {
                    effectiveIconUrl = info.iconUrl
                }
            } catch (_: Exception) {}
        }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("tester_card_${app.id}"),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            1.dp,
            if (app.isFeatured) AmberGold.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
        ),
        tonalElevation = if (app.isFeatured) 2.dp else 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Main App Row: Icon + Title/Dev/Progress + Points pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Clickable App Icon
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(12.dp))
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
                    val displayIcon = effectiveIconUrl.ifBlank { app.iconUrl }
                    AppIconImage(
                        iconUrl = displayIcon,
                        appName = app.name,
                        packageName = app.packageName,
                        size = 48.dp,
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Little play overlay indicator
                    Surface(
                        shape = CircleShape,
                        color = PlayGreen,
                        shadowElevation = 1.dp,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(18.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Open",
                            tint = Color.White,
                            modifier = Modifier.size(12.dp).padding(1.dp)
                        )
                    }
                }

                // App Title + Developer + Compact Progress Bar
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = app.name,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        if (app.isFeatured) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = AmberGold.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = if (isArabic) "مميز ⭐" else "Pinned ⭐",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AmberGold,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    val appReviews = remember(feedbacks, app.packageName) {
                        feedbacks.filter { it.packageName == app.packageName }
                    }
                    val avgRating = if (appReviews.isNotEmpty()) appReviews.map { it.rating }.average() else 5.0
                    val reviewCount = appReviews.size

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (isArabic) "بواسطة: ${app.developerName}" else "By: ${app.developerName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )

                        // Rating badge
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = AmberGold.copy(alpha = 0.12f),
                            border = BorderStroke(0.5.dp, AmberGold.copy(alpha = 0.3f)),
                            modifier = Modifier
                                .clickable(enabled = onViewReviewsClick != null) { onViewReviewsClick?.invoke() }
                                .testTag("rating_badge_${app.id}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = AmberGold, modifier = Modifier.size(11.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = if (reviewCount > 0) {
                                        String.format(java.util.Locale.US, "%.1f (%d)", avgRating, reviewCount)
                                    } else {
                                        "5.0 (0)"
                                    },
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AmberGold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Compact progress bar + count
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .weight(1f)
                                .height(5.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (app.testersCurrent >= app.testersTarget) PlayGreen else CyanAccent,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                        Text(
                            text = "${app.testersCurrent}/${app.testersTarget}",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (app.testersCurrent >= app.testersTarget) PlayGreen else CyanAccent
                        )
                    }
                }

                // Reward pill
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = AmberGold.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, AmberGold.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = null,
                            tint = AmberGold,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "+${app.rewardPoints}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = AmberGold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons Row (Compact, Clean & Direct)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Group button (compact)
                OutlinedButton(
                    onClick = { openUrl(context, app.googleGroupUrl) },
                    modifier = Modifier
                        .weight(1f)
                        .height(34.dp)
                        .testTag("open_group_${app.id}"),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(s.groupLinkBtn, maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 11.sp)
                }

                // Reviews Button
                OutlinedButton(
                    onClick = { onViewReviewsClick?.invoke() },
                    modifier = Modifier
                        .height(34.dp)
                        .testTag("btn_reviews_${app.id}"),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = AmberGold,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(if (isArabic) "التقييمات" else "Reviews", fontSize = 11.sp)
                }

                // Join or Launch button
                if (!isAlreadyJoined) {
                    Button(
                        onClick = onJoinClick,
                        modifier = Modifier
                            .weight(1.3f)
                            .height(34.dp)
                            .testTag("join_button_${app.id}"),
                        colors = ButtonDefaults.buttonColors(containerColor = PlayGreen),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(s.joinTestBtn, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 11.sp)
                    }
                } else {
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
                            .height(34.dp)
                            .testTag("open_optin_${app.id}"),
                        colors = ButtonDefaults.buttonColors(containerColor = PlayGreen),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Android,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(s.openAppBtn, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 11.sp)
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
        Toast.makeText(context, "Opening $appName...", Toast.LENGTH_SHORT).show()
        onRecordOpened(packageName, appName)
    } else {
        val targetUrl = when {
            androidOptInUrl.isNotBlank() -> androidOptInUrl
            webOptInUrl.isNotBlank() -> webOptInUrl
            googleGroupUrl.isNotBlank() -> googleGroupUrl
            else -> "https://play.google.com/store/apps/details?id=$packageName"
        }
        openUrl(context, targetUrl)
        Toast.makeText(context, "Opening Google Play opt-in for $appName...", Toast.LENGTH_SHORT).show()
        onRecordOpened(packageName, appName)
    }
}

fun openUrl(context: Context, url: String) {
    if (url.isBlank()) {
        Toast.makeText(context, "Link is currently unavailable", Toast.LENGTH_SHORT).show()
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
        Toast.makeText(context, "Could not open link", Toast.LENGTH_SHORT).show()
    }
}
