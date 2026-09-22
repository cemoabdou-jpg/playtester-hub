package com.example.ui.screens

import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.AuthUser
import com.example.data.model.CommunityApp
import com.example.data.model.JoinedTest
import com.example.ui.components.HeroBannerCard
import com.example.ui.components.TesterCard
import com.example.ui.theme.AmberGold
import com.example.ui.theme.PlayGreen

@Composable
fun CommunityScreen(
    communityApps: List<CommunityApp>,
    joinedTests: List<JoinedTest>,
    searchQuery: String,
    selectedCategory: String,
    currentUser: AuthUser? = null,
    onSearchChange: (String) -> Unit,
    onCategorySelect: (String) -> Unit,
    onJoinApp: (CommunityApp) -> Unit,
    onPublishClick: () -> Unit,
    onOpenAdmin: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val joinedAppIds = joinedTests.map { it.communityAppId }.toSet()
    val categories = listOf("الكل", "أدوات", "إنتاجية", "مالية", "ألعاب", "تعليم", "نمط حياة")

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("community_apps_list"),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Hero Banner
            item {
                HeroBannerCard(onPublishClick = onPublishClick)
            }

            // Search bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_field"),
                    placeholder = { Text("ابحث عن تطبيق أو مطور...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "بحث",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "مسح")
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            // Categories horizontal filter
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { cat ->
                        ElevatedFilterChip(
                            selected = selectedCategory == cat,
                            onClick = { onCategorySelect(cat) },
                            label = { Text(cat) },
                            modifier = Modifier.testTag("filter_chip_$cat")
                        )
                    }
                }
            }

            // Section Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "تطبيقات بحاجة لمختبرين (${communityApps.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (currentUser?.role == "ADMIN" && onOpenAdmin != null) {
                        Surface(
                            onClick = onOpenAdmin,
                            shape = RoundedCornerShape(8.dp),
                            color = AmberGold.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AmberGold),
                            modifier = Modifier.testTag("btn_open_admin_panel")
                        ) {
                            Text(
                                text = "👑 لوحة تحكم المالك",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = AmberGold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Empty state
            if (communityApps.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = if (searchQuery.isBlank() && selectedCategory == "الكل") Icons.Default.SwapHoriz else Icons.Default.Search,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (searchQuery.isBlank() && selectedCategory == "الكل")
                                    "لا توجد تطبيقات في المجتمع حالياً"
                                else
                                    "لا توجد نتائج مطابقة للبحث",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (searchQuery.isBlank() && selectedCategory == "الكل")
                                    "كن أول من ينشر تطبيقه للحصول على 20 مختبراً حقيقياً من المطورين!"
                                else
                                    "جرب تغيير كلمات البحث أو اختيار تصنيف آخر",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            if (searchQuery.isBlank() && selectedCategory == "الكل") {
                                Spacer(modifier = Modifier.height(14.dp))
                                Button(
                                    onClick = onPublishClick,
                                    colors = ButtonDefaults.buttonColors(containerColor = PlayGreen),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("أضف تطبيقك الآن", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Community App Cards
            items(communityApps, key = { it.id }) { app ->
                TesterCard(
                    app = app,
                    isAlreadyJoined = joinedAppIds.contains(app.id),
                    onJoinClick = { onJoinApp(app) }
                )
            }
        }

        // Floating Action Button to add app
        FloatingActionButton(
            onClick = onPublishClick,
            containerColor = PlayGreen,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("fab_publish_app")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Add, contentDescription = "أضف تطبيقك")
                Spacer(modifier = Modifier.size(6.dp))
                Text("أضف تطبيقك", fontWeight = FontWeight.Bold)
            }
        }
    }
}
