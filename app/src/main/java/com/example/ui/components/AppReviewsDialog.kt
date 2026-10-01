package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.TesterFeedback
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DangerRed
import com.example.ui.theme.PlayGreen
import com.example.ui.util.AppLanguage
import com.example.ui.util.LocalAppLanguage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AppReviewsDialog(
    appName: String,
    packageName: String,
    feedbacks: List<TesterFeedback>,
    isDeveloperOrAdmin: Boolean = false,
    onWriteReviewClick: () -> Unit,
    onReplyToReview: (feedbackId: Int, replyText: String) -> Unit,
    onDeleteReview: (feedbackId: Int) -> Unit = {},
    onDismiss: () -> Unit
) {
    val language = LocalAppLanguage.current
    val isArabic = language == AppLanguage.AR

    var selectedFilter by remember { mutableStateOf("ALL") } // "ALL", "5", "4", "3", "BUGS"
    var replyingToFeedbackId by remember { mutableStateOf<Int?>(null) }
    var replyText by remember { mutableStateOf("") }

    val appReviews = remember(feedbacks, packageName) {
        if (packageName.isBlank()) feedbacks else feedbacks.filter { it.packageName == packageName }
    }

    val totalCount = appReviews.size
    val averageRating = if (totalCount > 0) appReviews.map { it.rating }.average() else 5.0

    val count5 = appReviews.count { it.rating == 5 }
    val count4 = appReviews.count { it.rating == 4 }
    val count3 = appReviews.count { it.rating == 3 }
    val count2 = appReviews.count { it.rating == 2 }
    val count1 = appReviews.count { it.rating == 1 }
    val bugsCount = appReviews.count { it.feedbackType == "BUG_REPORT" }

    val filteredReviews = when (selectedFilter) {
        "5" -> appReviews.filter { it.rating == 5 }
        "4" -> appReviews.filter { it.rating == 4 }
        "3" -> appReviews.filter { it.rating in 1..3 }
        "BUGS" -> appReviews.filter { it.feedbackType == "BUG_REPORT" }
        else -> appReviews
    }

    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", if (isArabic) Locale("ar") else Locale.ENGLISH) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 16.dp)
                .testTag("app_reviews_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
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
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(AmberGold.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = AmberGold,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isArabic) "تقييمات ومراجعات المختبرين" else "Tester Ratings & Reviews",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (appName.isNotBlank()) appName else if (isArabic) "كافة التطبيقات" else "All Apps",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("btn_close_reviews_dialog")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = if (isArabic) "إغلاق" else "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Rating Score & Distribution Banner
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Big average score
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.width(90.dp)
                        ) {
                            Text(
                                text = String.format(Locale.US, "%.1f", averageRating),
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = AmberGold
                            )
                            Row {
                                (1..5).forEach { star ->
                                    Icon(
                                        imageVector = if (star <= averageRating.toInt()) Icons.Default.Star else Icons.Default.StarOutline,
                                        contentDescription = null,
                                        tint = AmberGold,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isArabic) "$totalCount تقييم" else "$totalCount reviews",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Distribution bars
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            RatingBarRow(starNum = 5, count = count5, total = totalCount, color = PlayGreen)
                            RatingBarRow(starNum = 4, count = count4, total = totalCount, color = CyanAccent)
                            RatingBarRow(starNum = 3, count = count3, total = totalCount, color = AmberGold)
                            RatingBarRow(starNum = 2, count = count2, total = totalCount, color = Color(0xFFFF9800))
                            RatingBarRow(starNum = 1, count = count1, total = totalCount, color = DangerRed)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Action Bar: Filter Chips & Write Review Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        FilterChip(
                            selected = selectedFilter == "ALL",
                            onClick = { selectedFilter = "ALL" },
                            label = { Text(if (isArabic) "الكل ($totalCount)" else "All ($totalCount)", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PlayGreen,
                                selectedLabelColor = Color.White
                            )
                        )
                        FilterChip(
                            selected = selectedFilter == "5",
                            onClick = { selectedFilter = "5" },
                            label = { Text("5★ ($count5)", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AmberGold,
                                selectedLabelColor = Color.Black
                            )
                        )
                        FilterChip(
                            selected = selectedFilter == "BUGS",
                            onClick = { selectedFilter = "BUGS" },
                            label = { Text(if (isArabic) "أعطال ($bugsCount)" else "Bugs ($bugsCount)", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = DangerRed,
                                selectedLabelColor = Color.White
                            )
                        )
                    }

                    Button(
                        onClick = onWriteReviewClick,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PlayGreen, contentColor = Color.White),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("btn_write_review_dialog")
                    ) {
                        Icon(imageVector = Icons.Default.RateReview, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isArabic) "أضف تقييمك ⭐" else "Rate App ⭐", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Reviews List
                if (filteredReviews.isEmpty()) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.RateReview,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (isArabic) "لا توجد مراجعات في هذا التصنيف حالياً" else "No reviews in this filter yet",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isArabic) "كن أول من يكتب تقييماً وملاحظات للمطور ويكسب +25 نقطة!" else "Be the first to leave a review and earn +25 points!",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false)
                            .height(340.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredReviews, key = { it.id }) { review ->
                            ReviewItemCard(
                                review = review,
                                isArabic = isArabic,
                                dateFormat = dateFormat,
                                isDeveloperOrAdmin = isDeveloperOrAdmin,
                                isReplying = replyingToFeedbackId == review.id,
                                replyText = replyText,
                                onReplyTextChange = { replyText = it },
                                onStartReply = {
                                    replyingToFeedbackId = review.id
                                    replyText = review.developerReply ?: ""
                                },
                                onCancelReply = {
                                    replyingToFeedbackId = null
                                    replyText = ""
                                },
                                onSubmitReply = {
                                    onReplyToReview(review.id, replyText)
                                    replyingToFeedbackId = null
                                    replyText = ""
                                },
                                onDelete = { onDeleteReview(review.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RatingBarRow(
    starNum: Int,
    count: Int,
    total: Int,
    color: Color
) {
    val progress = if (total > 0) count.toFloat() / total else 0f
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "$starNum★",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(22.dp)
        )
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            strokeCap = StrokeCap.Round
        )
        Text(
            text = "$count",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(18.dp)
        )
    }
}

@Composable
private fun ReviewItemCard(
    review: TesterFeedback,
    isArabic: Boolean,
    dateFormat: SimpleDateFormat,
    isDeveloperOrAdmin: Boolean,
    isReplying: Boolean,
    replyText: String,
    onReplyTextChange: (String) -> Unit,
    onStartReply: () -> Unit,
    onCancelReply: () -> Unit,
    onSubmitReply: () -> Unit,
    onDelete: () -> Unit
) {
    val initial = review.testerName.firstOrNull()?.uppercase() ?: "T"
    val dateString = remember(review.submittedAt) { dateFormat.format(Date(review.submittedAt)) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Reviewer Top Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initial,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontSize = 13.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = review.testerName,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            if (review.isVerifiedTester) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = PlayGreen.copy(alpha = 0.15f)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PlayGreen, modifier = Modifier.size(10.dp))
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(if (isArabic) "مختبر موثق" else "Verified", fontSize = 9.sp, color = PlayGreen, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                        Text(
                            text = dateString,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Stars
                Row {
                    (1..5).forEach { star ->
                        Icon(
                            imageVector = if (star <= review.rating) Icons.Default.Star else Icons.Default.StarOutline,
                            contentDescription = null,
                            tint = AmberGold,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Category tag
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                when (review.feedbackType) {
                    "BUG_REPORT" -> {
                        Surface(shape = RoundedCornerShape(6.dp), color = DangerRed.copy(alpha = 0.12f)) {
                            Row(modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.BugReport, contentDescription = null, tint = DangerRed, modifier = Modifier.size(11.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(if (isArabic) "تقرير عطل / Bug" else "Bug Report", fontSize = 10.sp, color = DangerRed, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                    "UI_UX" -> {
                        Surface(shape = RoundedCornerShape(6.dp), color = CyanAccent.copy(alpha = 0.12f)) {
                            Row(modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Palette, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(11.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(if (isArabic) "تصميم وواجهة" else "UI/UX", fontSize = 10.sp, color = CyanAccent, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                    "PERFORMANCE" -> {
                        Surface(shape = RoundedCornerShape(6.dp), color = PlayGreen.copy(alpha = 0.12f)) {
                            Row(modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Speed, contentDescription = null, tint = PlayGreen, modifier = Modifier.size(11.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(if (isArabic) "سرعة وأداء" else "Performance", fontSize = 10.sp, color = PlayGreen, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                    else -> {
                        Surface(shape = RoundedCornerShape(6.dp), color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)) {
                            Row(modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(11.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(if (isArabic) "تقييم عام" else "Review", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }

                if (review.deviceModel.isNotBlank()) {
                    Surface(shape = RoundedCornerShape(6.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                        Row(modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Devices, contentDescription = null, modifier = Modifier.size(11.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(review.deviceModel, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Review text content
            Text(
                text = review.content,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 18.sp
            )

            // Developer Reply Box (if present)
            if (!review.developerReply.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = PlayGreen.copy(alpha = 0.08f),
                    border = BorderStroke(1.dp, PlayGreen.copy(alpha = 0.25f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.AutoMirrored.Filled.Reply, contentDescription = null, tint = PlayGreen, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isArabic) "رد مطور التطبيق 👨‍💻" else "Developer Response 👨‍💻",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = PlayGreen
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = review.developerReply,
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Developer Reply Input (when active)
            AnimatedVisibility(visible = isReplying) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedTextField(
                        value = replyText,
                        onValueChange = onReplyTextChange,
                        label = { Text(if (isArabic) "اكتب ردك على المختبر..." else "Write response to tester...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 4
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = onCancelReply,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(if (isArabic) "إلغاء" else "Cancel", fontSize = 11.sp)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Button(
                            onClick = onSubmitReply,
                            colors = ButtonDefaults.buttonColors(containerColor = PlayGreen),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                        ) {
                            Text(if (isArabic) "إرسال الرد" else "Send Reply", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Reply Action Button for developer / Admin
            if (isDeveloperOrAdmin && !isReplying) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (review.developerReply.isNullOrBlank()) {
                            if (isArabic) "💬 الرد على هذه المراجعة" else "💬 Reply to review"
                        } else {
                            if (isArabic) "✏️ تعديل الرد" else "✏️ Edit reply"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { onStartReply() }
                            .padding(vertical = 4.dp)
                    )

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = DangerRed.copy(alpha = 0.6f), modifier = Modifier.size(14.dp))
                    }
                }
            }
        }
    }
}
