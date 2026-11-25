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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playtesterhub.app.R
import com.example.data.model.CommunityApp
import com.example.ui.components.AppIconImage
import com.example.ui.components.RealJoinTestDialog
import com.example.ui.util.L
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

    var selectedCategory by remember { mutableStateOf("ALL") }
    var searchQuery by remember { mutableStateOf("") }
    var sortOption by remember { mutableStateOf("FAIR_PLAY") } // FAIR_PLAY, NEED, REWARD
    var hideJoined by remember { mutableStateOf(false) }
    var onlyNeedsTesters by remember { mutableStateOf(false) }
    var appToJoinReal by remember { mutableStateOf<CommunityApp?>(null) }

    val categories = listOf("ALL", "MOST_NEEDED", "TOOLS", "GAMES", "PRODUCTIVITY", "EDUCATION", "SOCIAL")

    val featuredApps = remember(communityApps) {
        communityApps.filter { it.isFeatured }
    }

    val regularApps = remember(communityApps, joinedTests, selectedCategory, searchQuery, sortOption, hideJoined, onlyNeedsTesters) {
        communityApps.filter { app ->
            val isAlreadyJoined = joinedTests.any { it.packageName == app.packageName }
            val matchesJoinedFilter = !hideJoined || !isAlreadyJoined
            val matchesNeedFilter = !onlyNeedsTesters || app.currentTesters < 20
            val matchesCategory = when (selectedCategory) {
                "ALL" -> true
                "MOST_NEEDED" -> app.currentTesters < 20
                else -> L.isCategoryMatch(app.category, selectedCategory)
            }
            val matchesSearch = searchQuery.isBlank() ||
                    app.name.contains(searchQuery, ignoreCase = true) ||
                    app.packageName.contains(searchQuery, ignoreCase = true) ||
                    app.developerName.contains(searchQuery, ignoreCase = true)
            matchesJoinedFilter && matchesNeedFilter && matchesCategory && matchesSearch
        }.sortedWith(
            when (sortOption) {
                "NEED" -> compareBy<CommunityApp> { it.currentTesters }
                "REWARD" -> compareByDescending<CommunityApp> { it.pointsReward }
                else -> { // FAIR_PLAY: Featured first, then closest to 20 to finish testing cohorts efficiently
                    compareByDescending<CommunityApp> { it.isFeatured }
                        .thenBy { it.currentTesters >= 20 }
                        .thenByDescending { it.currentTesters }
                }
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
                        contentDescription = L.t("مجتمع المطورين", "Developers Community"),
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
                                        L.t("منصة معتمدة", "Verified Hub"),
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    L.t("اختبارات جوجل بلاي المغلقة لمدة 14 يوماً", "Google Play 14-Day Closed Testing"),
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                L.t("تبادل المختبرين الحقيقيين", "Real Tester Exchange"),
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 17.sp
                            )
                            Text(
                                L.t("اختبر تطبيقات المطورين واجمع 20 مختبراً لتطبيقك بنجاح", "Test developer apps and gather 20 real testers for your app"),
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
                                Text(L.t("عجلة الحظ 🎡", "Lucky Spin 🎡"), color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
                                Text(L.t("+50 عملة 🎬", "+50 Coins 🎬"), color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
                                Text(L.t("ترقية 🚀", "Upgrade 🚀"), fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
                                L.t("سلسلة الحضور: $streakDays أيام متتالية", "Attendance Streak: $streakDays days in a row"),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                if (isDailyCheckInDone)
                                    L.t("تم تسجيل حضورك اليوم بنجاح ✓ (+20 عملة)", "Checked in today ✓ (+20 coins)")
                                else
                                    L.t("سجل حضورك اليوم لكسب +20 عملة", "Check in today to earn +20 coins"),
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
                                    Toast.makeText(
                                        context,
                                        L.t("🎉 رائع! تم تسجيل حضورك اليوم وكسبت +20 عملة!", "🎉 Awesome! Checked in today and earned +20 coins!"),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        context,
                                        L.t("تم تسجيل حضورك اليوم مسبقاً ✓", "Already checked in today ✓"),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722)),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 5.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text(L.t("تسجيل اليوم", "Check In"), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Surface(
                            color = Color(0xFFE8F5E9),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                L.t("مكتمل ✓", "Done ✓"),
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
                            Text(L.t("تطبيقي", "My App"), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            if (primaryApp != null) "${primaryApp.currentTesters}/20" else "0",
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = if ((primaryApp?.currentTesters ?: 0) >= 20) Color(0xFF00875A) else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            if (primaryApp != null) L.t("${primaryApp.completedDays}/14 يوماً", "${primaryApp.completedDays}/14 days") else L.t("لم تسجل بعد", "Not added yet"),
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
                            Text(L.t("اختباراتي", "My Tests"), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "${joinedTests.size}",
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = Color(0xFF00875A)
                        )
                        Text(
                            L.t("$testedTodayCount فُحص اليوم", "$testedTodayCount checked today"),
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
                            Text(L.t("الرصيد", "Balance"), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "${currentUser.points}",
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = Color(0xFFFF8F00)
                        )
                        Text(
                            L.t("عملة ذهبية 🪙", "Gold Coins 🪙"),
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
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("community_search_input"),
                placeholder = { Text(L.t("ابحث باسم التطبيق، المطور، أو الحزمة...", "Search by app, developer, or package..."), fontSize = 12.sp) },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = L.t("بحث", "Search"),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { searchQuery = "" }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Clear, contentDescription = L.t("مسح", "Clear"))
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
                categories.forEach { categoryKey ->
                    val isSelected = selectedCategory == categoryKey
                    val chipLabel = when (categoryKey) {
                        "ALL" -> L.t("الكل (${communityApps.size})", "All (${communityApps.size})")
                        "MOST_NEEDED" -> L.t("الأكثر احتياجاً", "Most Needed")
                        "TOOLS" -> L.t("أدوات", "Tools")
                        "GAMES" -> L.t("ألعاب", "Games")
                        "PRODUCTIVITY" -> L.t("إنتاجية", "Productivity")
                        "EDUCATION" -> L.t("تعليم", "Education")
                        "SOCIAL" -> L.t("تواصل", "Social")
                        else -> categoryKey
                    }
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = categoryKey },
                        label = {
                            Text(
                                chipLabel,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }
        }

        // 3.5 Sort Mode & Quick Filters Selector
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(L.t("ترتيب:", "Sort:"), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    listOf(
                        "FAIR_PLAY" to L.t("المميزون والأقرب للاكتمال", "Featured & Closest to 20"),
                        "NEED" to L.t("الأكثر حاجة 🎯", "Most Needed 🎯"),
                        "REWARD" to L.t("الأعلى عملات 🪙", "Highest Coins 🪙")
                    ).forEach { (opt, label) ->
                        val isSelected = sortOption == opt
                        Surface(
                            onClick = { sortOption = opt },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Text(
                                label,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterChip(
                        selected = hideJoined,
                        onClick = { hideJoined = !hideJoined },
                        label = { Text(L.t("إخفاء المنضم لها", "Hide Joined"), fontSize = 11.sp) },
                        leadingIcon = if (hideJoined) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                        } else null,
                        modifier = Modifier.testTag("community_filter_hide_joined"),
                        shape = RoundedCornerShape(10.dp)
                    )
                    FilterChip(
                        selected = onlyNeedsTesters,
                        onClick = { onlyNeedsTesters = !onlyNeedsTesters },
                        label = { Text(L.t("تحتاج مختبرين (<20)", "Needs <20"), fontSize = 11.sp) },
                        leadingIcon = if (onlyNeedsTesters) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                        } else null,
                        modifier = Modifier.testTag("community_filter_needs_testers"),
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
                                L.t("تطبيقات مميزة في الصدارة ⭐", "Featured Spotlight Apps ⭐"),
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
                                L.t("أولوية الاختبار", "Priority Testing"),
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
                            val isJoined = joinedTests.any { it.packageName == app.packageName }
                            FeaturedAppCard(
                                app = app,
                                isJoined = isJoined,
                                onJoin = {
                                    if (isJoined) {
                                        viewModel.navigateToTab(1)
                                    } else {
                                        appToJoinReal = app
                                    }
                                },
                                onOpenGroup = {
                                    com.example.util.AppInstallVerifier.openGoogleGroup(context, app.googleGroupUrl)
                                },
                                onOpenOptIn = {
                                    val url = if (app.webOptInUrl.isNotBlank()) app.webOptInUrl else "https://play.google.com/apps/testing/${app.packageName}"
                                    try {
                                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                        context.startActivity(browserIntent)
                                    } catch (_: Exception) {
                                        Toast.makeText(context, L.t("الرابط: $url", "Link: $url"), Toast.LENGTH_SHORT).show()
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
                    L.t("قائمة التطبيقات المتاحة للاختبار", "Available Apps for Testing"),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        onClick = { viewModel.openDeveloperInbox() },
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Chat, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(L.t("محادثاتي 💬", "Chats 💬"), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            L.t("${regularApps.size} تطبيق", "${regularApps.size} apps"),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
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
                            L.t("لا توجد تطبيقات في هذا القسم حالياً", "No apps in this category right now"),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            L.t(
                                "أضف تطبيقك من تبويب 'تطبيقاتي' ليظهر لمئات المطورين ويبدأ جمع الـ 20 مختبراً",
                                "Add your app from the 'My Apps' tab so developers can join and help you reach 20 testers"
                            ),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        } else {
            itemsIndexed(regularApps, key = { _, app -> app.id }) { _, app ->
                val isJoined = joinedTests.any { it.packageName == app.packageName }
                CleanCommunityAppCard(
                    app = app,
                    isJoined = isJoined,
                    onJoin = {
                        if (isJoined) {
                            viewModel.navigateToTab(1)
                        } else {
                            appToJoinReal = app
                        }
                    },
                    onOpenGroup = {
                        com.example.util.AppInstallVerifier.openGoogleGroup(context, app.googleGroupUrl)
                    },
                    onOpenOptIn = {
                        val url = if (app.webOptInUrl.isNotBlank()) app.webOptInUrl else "https://play.google.com/apps/testing/${app.packageName}"
                        try {
                            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(browserIntent)
                        } catch (_: Exception) {
                            Toast.makeText(context, L.t("الرابط: $url", "Link: $url"), Toast.LENGTH_SHORT).show()
                        }
                    },
                    onOpenDeveloperProfile = {
                        val devEmail = if (app.developerEmail.isNotBlank()) app.developerEmail else "${app.developerName.lowercase().replace(" ", "").replace("[^a-zA-Z0-9]".toRegex(), "")}@gmail.com"
                        viewModel.openTesterProfile(devEmail, app.developerName)
                    },
                    onOpenChat = {
                        val devEmail = if (app.developerEmail.isNotBlank()) app.developerEmail else "${app.developerName.lowercase().replace(" ", "").replace("[^a-zA-Z0-9]".toRegex(), "")}@gmail.com"
                        viewModel.openDeveloperChat(devEmail, app.developerName, app.name)
                    }
                )
            }
        }
    }

    if (appToJoinReal != null) {
        val targetApp = appToJoinReal!!
        RealJoinTestDialog(
            app = targetApp,
            onDismiss = { appToJoinReal = null },
            onConfirmJoin = {
                val success = viewModel.joinCommunityApp(targetApp)
                if (success) {
                    Toast.makeText(
                        context,
                        L.t(
                            "🎉 تهانينا! أصبحت مختبراً رسمياً لـ ${targetApp.name} وتمت إضافته إلى تبويب 'اختباراتي' (+${targetApp.pointsReward} عملة)!",
                            "🎉 Congrats! You're now an official tester for ${targetApp.name} and it's added to 'My Tests' (+${targetApp.pointsReward} coins)!"
                        ),
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(context, L.t("أنت منضم بالفعل لهذا التطبيق مسبقاً!", "You already joined this app!"), Toast.LENGTH_SHORT).show()
                }
                appToJoinReal = null
            }
        )
    }
}

/**
 * Compact, eye-catching card for the Featured Spotlight Carousel.
 */
@Composable
private fun FeaturedAppCard(
    app: CommunityApp,
    isJoined: Boolean = false,
    onJoin: () -> Unit,
    onOpenGroup: () -> Unit = {},
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
                    packageName = app.packageName,
                    modifier = Modifier.size(44.dp),
                    shape = RoundedCornerShape(12.dp)
                )
                Surface(
                    color = Color(0xFFFFB300).copy(alpha = 0.18f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        L.t("+${app.pointsReward} عملة", "+${app.pointsReward} Coins"),
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
                L.translateKnown(app.developerName),
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
                Text(L.t("${app.currentTesters}/20 مختبر", "${app.currentTesters}/20 testers"), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = onJoin,
                    modifier = Modifier.weight(1.1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isJoined) Color(0xFF00875A) else MaterialTheme.colorScheme.primary
                    ),
                    contentPadding = PaddingValues(vertical = 6.dp)
                ) {
                    Icon(
                        if (isJoined) Icons.Default.CheckCircle else Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        if (isJoined) L.t("منضم ✓", "Joined ✓") else L.t("انضم", "Join"),
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                FilledTonalButton(
                    onClick = onOpenGroup,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = Color(0xFFE8F5E9),
                        contentColor = Color(0xFF1B5E20)
                    ),
                    contentPadding = PaddingValues(vertical = 6.dp, horizontal = 2.dp)
                ) {
                    Icon(Icons.Default.Groups, contentDescription = null, modifier = Modifier.size(13.dp), tint = Color(0xFF2E7D32))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(L.t("المجموعة 👥", "Group 👥"), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun CleanCommunityAppCard(
    app: CommunityApp,
    isJoined: Boolean = false,
    onJoin: () -> Unit,
    onOpenGroup: () -> Unit = {},
    onOpenOptIn: () -> Unit,
    onOpenDeveloperProfile: () -> Unit = {},
    onOpenChat: () -> Unit = {}
) {
    val isGold = app.isFeatured
    val context = LocalContext.current
    val isInstalled = remember(app.packageName) {
        com.example.util.AppInstallVerifier.isAppInstalled(context, app.packageName)
    }
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
                        packageName = app.packageName,
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
                                    contentDescription = L.t("مميز", "Featured"),
                                    tint = Color(0xFFFF8F00),
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { onOpenDeveloperProfile() }
                        ) {
                            Text(
                                L.translateKnown(app.developerName),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (app.category.isNotBlank()) {
                                Text(
                                    " • ${L.localizeCategory(app.category)}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                Icons.Default.AccountCircle,
                                contentDescription = L.t("ملف المطور", "Developer Profile"),
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                        // Installation & Google Group Status Micro-row
                        Row(
                            modifier = Modifier.padding(top = 2.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isInstalled) {
                                Surface(
                                    color = Color(0xFFE8F5E9),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        L.t("مثبت ✓", "Installed ✓"),
                                        fontSize = 9.sp,
                                        color = Color(0xFF2E7D32),
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Surface(
                                color = if (app.googleGroupUrl.isNotBlank()) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier.clickable { onOpenGroup() }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Groups,
                                        contentDescription = null,
                                        tint = if (app.googleGroupUrl.isNotBlank()) Color(0xFF2E7D32) else MaterialTheme.colorScheme.outline,
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        if (app.googleGroupUrl.isNotBlank()) L.t("المجموعة مخصصة 👥", "Custom Group 👥") else L.t("مجموعة عامة 👥", "Public Group 👥"),
                                        fontSize = 9.sp,
                                        color = if (app.googleGroupUrl.isNotBlank()) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
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
                    L.t("المختبرون الحاليون: ${app.currentTesters}/${app.targetTesters}", "Current Testers: ${app.currentTesters}/${app.targetTesters}"),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    L.t("المتبقي: ${maxOf(0, app.targetTesters - app.currentTesters)} مختبر", "Remaining: ${maxOf(0, app.targetTesters - app.currentTesters)} testers"),
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
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Row 1: Primary actions (Join Test & Join Google Group)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        modifier = Modifier
                            .weight(1.1f)
                            .heightIn(min = 48.dp)
                            .testTag("community_join_btn_${app.packageName}"),
                        onClick = onJoin,
                        contentPadding = PaddingValues(vertical = 9.dp, horizontal = 6.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isJoined) Color(0xFF00875A) else MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            if (isJoined) Icons.Default.CheckCircle else Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            if (isJoined) L.t("منضم ✓ — اذهب لاختباراتي", "Joined ✓ — My Tests") else L.t("انضم للاختبار", "Join Test"),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    FilledTonalButton(
                        modifier = Modifier.weight(1f),
                        onClick = onOpenGroup,
                        contentPadding = PaddingValues(vertical = 9.dp, horizontal = 4.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color(0xFFE8F5E9),
                            contentColor = Color(0xFF1B5E20)
                        )
                    ) {
                        Icon(
                            Icons.Default.Groups,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp),
                            tint = Color(0xFF2E7D32)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            L.t("انضم للمجموعة 👥", "Join Group 👥"),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B5E20)
                        )
                    }
                }

                // Row 2: Download / Launch & Developer Chat
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isInstalled) {
                        FilledTonalButton(
                            modifier = Modifier.weight(1f),
                            onClick = {
                                com.example.util.AppInstallVerifier.launchApp(context, app.packageName)
                            },
                            contentPadding = PaddingValues(vertical = 8.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Launch, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(L.t("تشغيل التطبيق المثبت", "Launch Installed App"), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        OutlinedButton(
                            modifier = Modifier.weight(1f),
                            onClick = onOpenOptIn,
                            contentPadding = PaddingValues(vertical = 8.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(L.t("رابط التحميل على جوجل بلاي", "Google Play Download Link"), fontSize = 10.5.sp, fontWeight = FontWeight.Medium)
                        }
                    }

                    OutlinedButton(
                        onClick = onOpenChat,
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            Icons.Default.Chat,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(L.t("محادثة", "Chat"), fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}
