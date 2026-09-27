package com.example.ui.components

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TesterFeedback
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DangerRed
import com.example.ui.theme.PlayGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class FeedbackCategory(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color,
    val keywords: List<String>
) {
    BUG("🐛 أخطاء تقنية", Icons.Default.BugReport, DangerRed, listOf("خطأ", "توقف", "عطل", "bug", "crash", "error", "مشكلة")),
    UI_UX("🎨 تجربة المستخدم", Icons.Default.Palette, CyanAccent, listOf("تصميم", "واجهة", "لون", "زر", "خط", "ui", "ux", "شاشة")),
    PERFORMANCE("⚡ الأداء والسرعة", Icons.Default.Speed, AmberGold, listOf("بطء", "بطيء", "تعليق", "حرارة", "ذاكرة", "أداء", "speed", "lag")),
    POSITIVE("⭐ إشادات وتجارب", Icons.Default.Star, PlayGreen, listOf("ممتاز", "رائع", "جيد", "سلس", "جميل", "اعجبني", "good", "great", "nice"))
}

object FeedbackClassifierUtil {
    fun classify(content: String, rating: Int, feedbackType: String): FeedbackCategory {
        if (feedbackType.contains("BUG", ignoreCase = true) || rating <= 2) {
            return FeedbackCategory.BUG
        }
        val lower = content.lowercase()
        return when {
            FeedbackCategory.BUG.keywords.any { lower.contains(it) } -> FeedbackCategory.BUG
            FeedbackCategory.PERFORMANCE.keywords.any { lower.contains(it) } -> FeedbackCategory.PERFORMANCE
            FeedbackCategory.UI_UX.keywords.any { lower.contains(it) } -> FeedbackCategory.UI_UX
            else -> FeedbackCategory.POSITIVE
        }
    }
}

@Composable
fun SmartFeedbackClassifierCard(
    feedbacks: List<TesterFeedback>,
    onDeleteFeedback: ((Int) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var selectedCategoryFilter by remember { mutableStateOf<FeedbackCategory?>(null) }

    val classifiedList = remember(feedbacks) {
        feedbacks.map { fb ->
            fb to FeedbackClassifierUtil.classify(fb.content, fb.rating, fb.feedbackType)
        }
    }

    val totalCount = feedbacks.size
    val bugsCount = classifiedList.count { it.second == FeedbackCategory.BUG }
    val uiCount = classifiedList.count { it.second == FeedbackCategory.UI_UX }
    val perfCount = classifiedList.count { it.second == FeedbackCategory.PERFORMANCE }
    val posCount = classifiedList.count { it.second == FeedbackCategory.POSITIVE }

    val filteredList = remember(classifiedList, selectedCategoryFilter) {
        if (selectedCategoryFilter == null) classifiedList
        else classifiedList.filter { it.second == selectedCategoryFilter }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("smart_feedback_classifier_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(CyanAccent.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Category,
                            contentDescription = null,
                            tint = CyanAccent,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "تصنيف وفرز الملاحظات والبلاغات",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "فرز وتصنيف الملاحظات المجمعة لتقرير جوجل بلاي",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = PlayGreen.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "$totalCount ملاحظة",
                        style = MaterialTheme.typography.labelSmall,
                        color = PlayGreen,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Stats breakdown bar
            if (totalCount > 0) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FeedbackStatPill("🐛 أخطاء ($bugsCount)", DangerRed, (bugsCount * 100f) / totalCount)
                        FeedbackStatPill("🎨 واجهة ($uiCount)", CyanAccent, (uiCount * 100f) / totalCount)
                        FeedbackStatPill("⚡ أداء ($perfCount)", AmberGold, (perfCount * 100f) / totalCount)
                        FeedbackStatPill("⭐ إيجابي ($posCount)", PlayGreen, (posCount * 100f) / totalCount)
                    }
                }
            }

            // Category Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = selectedCategoryFilter == null,
                    onClick = { selectedCategoryFilter = null },
                    label = { Text("الكل", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PlayGreen,
                        selectedLabelColor = Color.White
                    )
                )

                FeedbackCategory.values().forEach { category ->
                    val isSel = selectedCategoryFilter == category
                    FilterChip(
                        selected = isSel,
                        onClick = {
                            selectedCategoryFilter = if (isSel) null else category
                        },
                        label = { Text(category.title, fontSize = 11.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = category.icon,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = category.color,
                            selectedLabelColor = Color.Black
                        )
                    )
                }
            }

            // Feedback Items List
            if (filteredList.isEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                ) {
                    Text(
                        text = "لا توجد ملاحظات مسجلة في هذا التصنيف حالياً.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filteredList.take(5).forEach { (fb, cat) ->
                        ClassifiedFeedbackItemCard(
                            feedback = fb,
                            category = cat,
                            onDelete = if (onDeleteFeedback != null) { { onDeleteFeedback(fb.id) } } else null
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FeedbackStatPill(
    label: String,
    color: Color,
    percentage: Float
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f)),
        modifier = Modifier.padding(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = color
            )
        }
    }
}

@Composable
private fun ClassifiedFeedbackItemCard(
    feedback: TesterFeedback,
    category: FeedbackCategory,
    onDelete: (() -> Unit)?
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(category.color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = category.icon,
                    contentDescription = null,
                    tint = category.color,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = feedback.testerEmail,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = AmberGold,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${feedback.rating}/5",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = feedback.content,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                val formattedDate = remember(feedback.submittedAt) {
                    SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(feedback.submittedAt))
                }

                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }

            if (onDelete != null) {
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "حذف الملاحظة",
                        tint = DangerRed.copy(alpha = 0.7f),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}
