package com.example.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.JoinedTest
import com.example.ui.components.AppIconImage
import com.example.ui.components.FeedbackDialog
import com.example.ui.viewmodel.TesterViewModel
import com.example.util.AdMobConfig

@Composable
fun MyTestingScreen(
    viewModel: TesterViewModel
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val joinedTests by viewModel.joinedTests.collectAsState()

    var selectedSubTab by remember { mutableStateOf("ACTIVE") }
    var lastTestedAppForDouble by remember { mutableStateOf<JoinedTest?>(null) }
    var appForFeedback by remember { mutableStateOf<JoinedTest?>(null) }

    val activeTests = remember(joinedTests) {
        joinedTests.filter { it.daysCompleted < 14 }
    }
    val completedTests = remember(joinedTests) {
        joinedTests.filter { it.daysCompleted >= 14 }
    }

    val displayList = if (selectedSubTab == "ACTIVE") activeTests else completedTests

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "التطبيقات التي أقوم باختبارها",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "افتح التطبيقات يومياً للحفاظ على نقاطك وسمعتك وتأهيل المطورين",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Sub Tabs Selector (Active vs Completed)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                FilterChip(
                    selected = selectedSubTab == "ACTIVE",
                    onClick = { selectedSubTab = "ACTIVE" },
                    label = {
                        Text(
                            "قيد الاختبار اليومي (${activeTests.size})",
                            fontSize = 12.sp,
                            fontWeight = if (selectedSubTab == "ACTIVE") FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    modifier = Modifier.weight(1f),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                    )
                )

                FilterChip(
                    selected = selectedSubTab == "COMPLETED",
                    onClick = { selectedSubTab = "COMPLETED" },
                    label = {
                        Text(
                            "المكتملة 14 يوماً (${completedTests.size}) ✓",
                            fontSize = 12.sp,
                            fontWeight = if (selectedSubTab == "COMPLETED") FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    modifier = Modifier.weight(1f),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF00875A),
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        // Double Rewards Banner (if an app was just tested)
        if (lastTestedAppForDouble != null) {
            val app = lastTestedAppForDouble!!
            item {
                Surface(
                    color = Color(0xFFFFF8E1),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFB300)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Celebration, contentDescription = null, tint = Color(0xFFE65100), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "🎉 أحسنت! كسبت +25 عملة لاختبار ${app.appName}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color(0xFFE65100)
                                )
                            }
                            IconButton(
                                onClick = { lastTestedAppForDouble = null },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = Color.Gray, modifier = Modifier.size(16.dp))
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    if (activity != null) {
                                        AdMobConfig.showRewardedAd(
                                            activity = activity,
                                            onRewardEarned = {
                                                viewModel.doubleTestReward(25)
                                                Toast.makeText(context, "🌟 رائع! تمت مضاعفة مكافأتك إلى 50 عملة!", Toast.LENGTH_LONG).show()
                                                lastTestedAppForDouble = null
                                            }
                                        )
                                    } else {
                                        viewModel.doubleTestReward(25)
                                        lastTestedAppForDouble = null
                                    }
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00875A)),
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                Icon(Icons.Default.PlayCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("ضاعفها إلى 50 عملة الآن (2x) 🎬", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = {
                                    appForFeedback = app
                                    lastTestedAppForDouble = null
                                },
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFF8F00), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("تقييم التطبيق (+15)", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // Empty state
        if (displayList.isEmpty()) {
            item {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            if (selectedSubTab == "ACTIVE") Icons.Default.HourglassEmpty else Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(44.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            if (selectedSubTab == "ACTIVE") "لا توجد تطبيقات قيد الاختبار حالياً" else "لم تكمل أي تطبيق بعد (14 يوماً)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            if (selectedSubTab == "ACTIVE") "تصفح شاشة المجتمع وانضم لاختبار تطبيقات المطورين لكسب العملات" else "استمر في فتح تطبيقاتك الحالية يومياً حتى تصل إلى 14 يوماً مكتملة",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(displayList, key = { it.id }) { test ->
                val isCompleted = test.daysCompleted >= 14
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
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
                                    iconUrl = test.iconUrl,
                                    appName = test.appName,
                                    packageName = test.packageName,
                                    modifier = Modifier.size(46.dp),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        test.appName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            if (isCompleted) "مكتمل 14 يوماً بنجاح ✓" else "اليوم ${test.daysCompleted} من 14",
                                            fontSize = 11.sp,
                                            color = if (isCompleted) Color(0xFF00875A) else MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            color = Color(0xFFFFB300).copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                "+25 عملة يومياً",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFFF8F00),
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            // Actions Row: 15-min QA Session Companion & Quick Launch
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        viewModel.openActiveTestSession(test)
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 7.dp)
                                ) {
                                    Icon(Icons.Default.HourglassBottom, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFF00875A))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("جلسة 15د (+50)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00875A))
                                }

                                Button(
                                    onClick = {
                                        viewModel.openAndTestApp(context, test) { earned ->
                                            lastTestedAppForDouble = test
                                        }
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 7.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isCompleted) Color(0xFF00875A) else MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (isCompleted) "إعادة الفتح" else "فتح واختبار", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Progress Bar (14 Days)
                        LinearProgressIndicator(
                            progress = { (test.daysCompleted.toFloat() / 14f).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (isCompleted) Color(0xFF00875A) else MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Bottom Actions (Review & Days Left)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                if (isCompleted) "أتممت مدة الاختبار المعتمدة لهذا التطبيق 🎉" else "متبقي ${maxOf(0, 14 - test.daysCompleted)} يوماً لاجتياز شرط Google Play",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            TextButton(
                                onClick = { appForFeedback = test },
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                                modifier = Modifier.height(26.dp)
                            ) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFF8F00), modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("كتابة تقييم (+15)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    if (appForFeedback != null) {
        val feedbackTarget = appForFeedback!!
        FeedbackDialog(
            appName = feedbackTarget.appName,
            iconUrl = feedbackTarget.iconUrl,
            onDismiss = { appForFeedback = null },
            onSubmit = { rating, comment ->
                viewModel.submitFeedback(feedbackTarget.packageName, rating, comment)
                Toast.makeText(context, "🌟 شكراً لك! تم إرسال تقييمك وإضافة +15 عملة لرصيدك!", Toast.LENGTH_LONG).show()
                appForFeedback = null
            }
        )
    }
}
