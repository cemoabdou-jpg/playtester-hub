package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.JoinedTest
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DangerRed
import com.example.ui.theme.PlayGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TestingHeatmapCard(
    test: JoinedTest,
    currentStreak: Int = 4,
    hasStreakShield: Boolean = true,
    modifier: Modifier = Modifier
) {
    val checkedDaysList = remember(test.checkedDaysCsv) {
        if (test.checkedDaysCsv.isBlank()) emptyList()
        else test.checkedDaysCsv.split(",").mapNotNull { it.trim().toIntOrNull() }
    }

    val todayStr = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
    val isCheckedToday = test.lastCheckInDate == todayStr

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("testing_heatmap_card_${test.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(PlayGreen.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = AmberGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "خريطة الـ 14 يوماً للإنتاج",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${test.daysCompleted} من 14 يوماً مكتملة (${(test.daysCompleted * 100) / 14}%)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Shield Badge
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (hasStreakShield) CyanAccent.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (hasStreakShield) CyanAccent.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = if (hasStreakShield) CyanAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (hasStreakShield) "درع الستريك 🛡️" else "بدون درع",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (hasStreakShield) CyanAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // 14-Day Heatmap Grid (2 rows of 7 blocks)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    (1..7).forEach { dayNum ->
                        HeatmapDayBlock(
                            dayNum = dayNum,
                            isCompleted = checkedDaysList.contains(dayNum),
                            isCurrentDay = (dayNum == test.daysCompleted + 1 && !isCheckedToday),
                            isMissed = (dayNum <= test.daysCompleted && !checkedDaysList.contains(dayNum))
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    (8..14).forEach { dayNum ->
                        HeatmapDayBlock(
                            dayNum = dayNum,
                            isCompleted = checkedDaysList.contains(dayNum),
                            isCurrentDay = (dayNum == test.daysCompleted + 1 && !isCheckedToday),
                            isMissed = (dayNum <= test.daysCompleted && !checkedDaysList.contains(dayNum))
                        )
                    }
                }
            }

            // Legend / Status Message
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(PlayGreen))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("مكتمل", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(AmberGold))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("يومك الحالي", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(DangerRed))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("محمي بالدرع", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp)
                    }
                }

                Text(
                    text = if (isCheckedToday) "✨ تمت تجربة اليوم!" else "⏳ بانتظار التجربة",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isCheckedToday) PlayGreen else AmberGold,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun HeatmapDayBlock(
    dayNum: Int,
    isCompleted: Boolean,
    isCurrentDay: Boolean,
    isMissed: Boolean
) {
    val (bgColor, borderColor, textColor, icon) = when {
        isCompleted -> Quadruple(PlayGreen, PlayGreen, Color.White, Icons.Default.Check)
        isCurrentDay -> Quadruple(AmberGold.copy(alpha = 0.25f), AmberGold, AmberGold, null)
        isMissed -> Quadruple(DangerRed.copy(alpha = 0.2f), DangerRed, DangerRed, Icons.Default.Close)
        else -> Quadruple(
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            null
        )
    }

    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(16.dp)
                )
            } else {
                Text(
                    text = "$dayNum",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (isCurrentDay) FontWeight.Bold else FontWeight.Medium,
                    color = textColor
                )
            }
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
