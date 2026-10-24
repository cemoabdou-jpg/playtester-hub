package com.example.ui.screens

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playtesterhub.app.R
import com.example.data.model.CommunityApp
import com.example.ui.components.AppIconImage
import com.example.ui.viewmodel.TesterViewModel
import com.example.util.AdMobConfig

@Composable
fun CommunityScreen(
    viewModel: TesterViewModel
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val communityApps by viewModel.communityApps.collectAsState()
    val userApps by viewModel.userApps.collectAsState()
    val joinedTests by viewModel.joinedTests.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val streakDays by viewModel.streakDays.collectAsState()
    val isDailyCheckInDone by viewModel.isDailyCheckInDoneToday.collectAsState()

    var selectedCategory by remember { mutableStateOf("الكل") }
    var searchQuery by remember { mutableStateOf("") }

    val categories = listOf("الكل", "الأكثر احتياجاً", "أدوات", "ألعاب", "إنتاجية", "تعليم", "تواصل")

    val featuredApps = remember(communityApps) {
        communityApps.filter { it.isFeatured }
    }

    val regularApps = remember(communityApps, selectedCategory, searchQuery) {
        communityApps.filter { app ->
            val matchesCategory = when (selectedCategory) {
                "الكل" -> true
                "الأكثر احتياجاً" -> app.currentTesters < 20
                else -> app.category.contains(selectedCategory, ignoreCase = true)
            }
            val matchesSearch = searchQuery.isBlank() ||
                    app.name.contains(searchQuery, ignoreCase = true) ||
                    app.packageName.contains(searchQuery, ignoreCase = true) ||
                    app.developerName.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }.sortedWith(
            if (selectedCategory == "الأكثر احتياجاً") {
                compareBy<CommunityApp> { it.currentTesters }
            } else {
                compareByDescending<CommunityApp> { it.isFeatured }.thenBy { it.currentTesters }
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Structured Hero Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(165.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.img_community_hero),
                        contentDescription = "مجتمع المطورين",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    // Dark luxury gradient overlay for readability
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF0F172A).copy(alpha = 0.4f),
                                        Color(0xFF0F172A).copy(alpha = 0.90f)
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = Color(0xFF00875A),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        "منصة معتمدة",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "Google Play 14-Days Closed Testing",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                "تبادل المختبرين الحقيقيين",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 17.sp
                            )
                            Text(
                                "اختبر تطبيقات المطورين واجمع 20 مختبراً لتطبيقك بنجاح",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 11.sp
                            )
                        }

                        // Hero Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Button(
                                onClick = { viewModel.openLuckySpin() },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text("عجلة الحظ 🎡", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    if (activity != null) {
                                        AdMobConfig.showRewardedAd(
                                            activity = activity,
                                            onRewardEarned = { coins ->
                                                viewModel.earnAdReward(coins)
                                            }
                                        )
                                    } else {
                                        viewModel.earnAdReward(50)
                                    }
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300)),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("+50 عملة 🎬", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            FilledTonalButton(
                                onClick = { viewModel.openMonetization() },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = Color.White.copy(alpha = 0.2f),
                                    contentColor = Color.White
                                ),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("ترقية 🚀", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // 2. Daily Streak Check-in Card (Engagement & UX)
        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFFF5722).copy(alpha = 0.15f),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🔥", fontSize = 18.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                "سلسلة الحضور: $streakDays أيام متتالية",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                if (isDailyCheckInDone) "تم تسجيل حضورك اليوم بنجاح ✓ (+20 عملة)" else "سجل حضورك اليوم لكسب +20 عملة",
                                fontSize = 11.sp,
                                color = if (isDailyCheckInDone) Color(0xFF00875A) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (!isDailyCheckInDone) {
                        Button(
                            onClick = {
                                val success = viewModel.dailyCheckIn()
                                if (success) {
                                    Toast.makeText(context, "🎉 رائع! تم تسجيل حضورك اليوم وكسبت +20 عملة!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722)),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 5.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("تسجيل اليوم", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Surface(
                            color = Color(0xFFE8F5E9),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "مكتمل ✓",
                                color = Color(0xFF00875A),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // 3. Compact 3-in-1 Dashboard Overview (Screen Organization)
        item {
            val primaryApp = userApps.firstOrNull()
            val testedTodayCount = joinedTests.count { System.currentTimeMillis() - it.lastTestDate < 24 * 3600 * 1000 }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Widget 1: My App Readiness
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Apps, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("تطبيقي", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            if (primaryApp != null) "${primaryApp.currentTesters}/20" else "0",
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = if ((primaryApp?.currentTesters ?: 0) >= 20) Color(0xFF00875A) else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            if (primaryApp != null) "${primaryApp.completedDays}/14 يوماً" else "لم تسجل بعد",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Widget 2: Today's Tasks
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Science, contentDescription = null, tint = Color(0xFF00875A), modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("اختباراتي", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "${joinedTests.size}",
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = Color(0xFF00875A)
                        )
                        Text(
                            "$testedTodayCount فُحص اليوم",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Widget 3: Points Balance & Quick Earn
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = Color(0xFFFF8F00), modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("الرصيد", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "${currentUser.points}",
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = Color(0xFFFF8F00)
                        )
                        Text(
                            "عملة ذهبية 🪙",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // 4. Search & Filter Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("ابحث باسم التطبيق، المطور، أو الحزمة...", fontSize = 12.sp) },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "بحث",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { searchQuery = "" }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Clear, contentDescription = "مسح")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                )
            )
        }

        // 3. Category Filter Chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { category ->
                    val isSelected = selectedCategory == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = category },
                        label = {
                            Text(
                                if (category == "الكل") "الكل (${communityApps.size})" else category,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }
        }

        // 4. Dedicated "Featured Spotlight" Carousel
        if (featuredApps.isNotEmpty()) {
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFF8F00), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "تطبيقات مميزة في الصدارة ⭐",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Surface(
                            color = Color(0xFFFFB300).copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                "أولوية الاختبار",
                                color = Color(0xFFE65100),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(featuredApps, key = { it.id }) { app ->
                            FeaturedAppCard(
                                app = app,
                                onJoin = {
                                    viewModel.joinCommunityApp(app)
                                    Toast.makeText(context, "تم الانضمام لاختبار ${app.name} بنجاح!", Toast.LENGTH_SHORT).show()
                                },
                                onOpenOptIn = {
                                    val url = if (app.webOptInUrl.isNotBlank()) app.webOptInUrl else "https://play.google.com/apps/testing/${app.packageName}"
                                    try {
                                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                        context.startActivity(browserIntent)
                                    } catch (_: Exception) {
                                        Toast.makeText(context, "الرابط: $url", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        // 5. Main Queue Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "قائمة التطبيقات المتاحة للاختبار",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "${regularApps.size} تطبيق",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }

        // 6. Apps List
        if (regularApps.isEmpty()) {
            item {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Apps,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "لا توجد تطبيقات في هذا القسم حالياً",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "أضف تطبيقك من تبويب 'تطبيقاتي' ليظهر لمئات المطورين ويبدأ جمع الـ 20 مختبراً",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        } else {
            itemsIndexed(regularApps, key = { _, app -> app.id }) { _, app ->
                CleanCommunityAppCard(
                    app = app,
                    onJoin = {
                        viewModel.joinCommunityApp(app)
                        Toast.makeText(context, "تم الانضمام لاختبار ${app.name} بنجاح!", Toast.LENGTH_SHORT).show()
                    },
                    onOpenOptIn = {
                        val url = if (app.webOptInUrl.isNotBlank()) app.webOptInUrl else "https://play.google.com/apps/testing/${app.packageName}"
                        try {
                            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(browserIntent)
                        } catch (_: Exception) {
                            Toast.makeText(context, "الرابط: $url", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }
    }
}

/**
 * Compact, eye-catching card for the Featured Spotlight Carousel.
 */
@Composable
private fun FeaturedAppCard(
    app: CommunityApp,
    onJoin: () -> Unit,
    onOpenOptIn: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .border(
                width = 1.dp,
                color = Color(0xFFFFB300).copy(alpha = 0.6f),
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppIconImage(
                    iconUrl = app.iconUrl,
                    appName = app.name,
                    category = app.category,
                    modifier = Modifier.size(44.dp),
                    shape = RoundedCornerShape(12.dp)
                )
                Surface(
                    color = Color(0xFFFFB300).copy(alpha = 0.18f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "+${app.pointsReward} عملة",
                        color = Color(0xFFE65100),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                app.name,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                maxLines = 1,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                app.developerName,
                fontSize = 10.sp,
                maxLines = 1,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))
            val progressFraction = (app.currentTesters.toFloat() / maxOf(1, app.targetTesters)).coerceIn(0f, 1f)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("${app.currentTesters}/20 مختبر", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("${(progressFraction * 100).toInt()}%", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.height(3.dp))
            LinearProgressIndicator(
                progress = { progressFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = Color(0xFFFFB300),
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = onJoin,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                contentPadding = PaddingValues(vertical = 6.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("انضم الآن", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun CleanCommunityAppCard(
    app: CommunityApp,
    onJoin: () -> Unit,
    onOpenOptIn: () -> Unit
) {
    val isGold = app.isFeatured
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (isGold) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = if (isGold) 3.dp else 1.5.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Icon, Title, Points Badge
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
                        modifier = Modifier.size(46.dp),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                app.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                maxLines = 1,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (isGold) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = "مميز",
                                    tint = Color(0xFFFF8F00),
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                app.developerName,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (app.category.isNotBlank()) {
                                Text(
                                    " • ${app.category}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Surface(
                    color = Color(0xFFFFB300).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.MonetizationOn,
                            contentDescription = null,
                            tint = Color(0xFFFF8F00),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "+${app.pointsReward}",
                            color = Color(0xFFFF8F00),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Progress Bar & Info
            val progressFraction = (app.currentTesters.toFloat() / maxOf(1, app.targetTesters)).coerceIn(0f, 1f)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "المختبرون الحاليون: ${app.currentTesters}/${app.targetTesters}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "المتبقي: ${maxOf(0, app.targetTesters - app.currentTesters)} مختبر",
                    fontSize = 11.sp,
                    color = if (app.currentTesters >= app.targetTesters) Color(0xFF00875A) else MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.height(5.dp))
            LinearProgressIndicator(
                progress = { progressFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp),
                color = if (isGold) Color(0xFFFFB300) else MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    modifier = Modifier.weight(1.1f),
                    onClick = onJoin,
                    contentPadding = PaddingValues(vertical = 9.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("انضم للاختبار", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    modifier = Modifier.weight(0.9f),
                    onClick = onOpenOptIn,
                    contentPadding = PaddingValues(vertical = 9.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("رابط التحميل", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}
