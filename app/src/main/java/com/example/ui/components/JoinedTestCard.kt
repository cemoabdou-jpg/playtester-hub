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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.JoinedTest
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DangerRed
import com.example.ui.theme.PlayGreen
import com.example.ui.theme.SuccessGreen
import coil.compose.AsyncImage
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun JoinedTestCard(
    test: JoinedTest,
    onCheckInClick: () -> Unit,
    onFeedbackClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onRecordOpened: (packageName: String, appName: String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val todayStr = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()) }
    val isCheckedToday = test.lastCheckInDate == todayStr
    val checkedDaysList = remember(test.checkedDaysCsv) {
        test.checkedDaysCsv.split(",").filter { it.isNotEmpty() }.mapNotNull { it.toIntOrNull() }.toSet()
    }
    val progress = (test.daysCompleted.toFloat() / 14f).coerceIn(0f, 1f)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("joined_test_card_${test.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            1.dp,
            if (test.isCompleted) PlayGreen.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Status badge & Delete icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (test.isCompleted) SuccessGreen.copy(alpha = 0.15f) else CyanAccent.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (test.isCompleted) Icons.Default.CheckCircle else Icons.Default.Schedule,
                            contentDescription = null,
                            tint = if (test.isCompleted) SuccessGreen else CyanAccent,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (test.isCompleted) "اكتمل الاختبار (14 يوماً)" else "جاري الاختبار المغلق",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (test.isCompleted) SuccessGreen else CyanAccent
                        )
                    }
                }

                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("delete_test_${test.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "حذف الاختبار",
                        tint = DangerRed.copy(alpha = 0.7f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // App row: Clickable icon + title & package
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable {
                            launchOrOpenApp(
                                context = context,
                                packageName = test.packageName,
                                fallbackUrl = test.androidOptInUrl,
                                appName = test.appName,
                                onRecordOpened = onRecordOpened
                            )
                        }
                        .testTag("joined_app_icon_${test.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    if (test.iconUrl.isNotBlank()) {
                        AsyncImage(
                            model = if (test.iconUrl.startsWith("/")) File(test.iconUrl) else test.iconUrl,
                            contentDescription = "أيقونة ${test.appName}",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                    } else {
                        Text(
                            text = test.appName.take(2).uppercase(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = PlayGreen
                        )
                    }

                    Surface(
                        shape = CircleShape,
                        color = PlayGreen.copy(alpha = 0.85f),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "فتح",
                            tint = Color.White,
                            modifier = Modifier.size(10.dp).padding(1.dp)
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = test.appName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = test.packageName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "⚡ اضغط على الأيقونة لفتح التطبيق وتوثيق اليوم",
                        style = MaterialTheme.typography.labelSmall,
                        color = PlayGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Progress Summary Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "سجل الأيام الـ 14 المطلوبة:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "اليوم ${test.daysCompleted} / 14 (${(progress * 100).toInt()}%)",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (test.isCompleted) PlayGreen else MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (test.isCompleted) PlayGreen else CyanAccent,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 14-Day Calendar Circles Grid (1..14)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                maxItemsInEachRow = 7
            ) {
                for (day in 1..14) {
                    val isChecked = checkedDaysList.contains(day)
                    val isCurrentDay = day == test.daysCompleted + 1 && !isCheckedToday && !test.isCompleted

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isChecked -> PlayGreen
                                    isCurrentDay -> AmberGold.copy(alpha = 0.2f)
                                    else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                }
                            )
                            .then(
                                if (isCurrentDay) {
                                    Modifier.background(
                                        color = Color.Transparent,
                                        shape = CircleShape
                                    )
                                } else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isChecked) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "يوم $day مكتمل",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        } else {
                            Text(
                                text = "$day",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isCurrentDay) FontWeight.Bold else FontWeight.Normal,
                                color = if (isCurrentDay) AmberGold else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Daily Check-in CTA Button
            if (!test.isCompleted) {
                Button(
                    onClick = onCheckInClick,
                    enabled = !isCheckedToday,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("check_in_btn_${test.id}"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PlayGreen,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isCheckedToday) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "تم تأكيد تجربة اليوم",
                            tint = PlayGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "تم توثيق وتأكيد تجربة اليوم بنجاح ✓",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    } else {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.FactCheck,
                            contentDescription = "تأكيد التجربة اليومية",
                            modifier = Modifier.size(20.dp),
                            tint = Color.Black
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "تأكيد التجربة اليومية (+25 نقطة)",
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            // Bottom Action Row: Launch Installed App, Feedback
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Launch or open in store
                OutlinedButton(
                    onClick = {
                        launchOrOpenApp(
                            context = context,
                            packageName = test.packageName,
                            fallbackUrl = test.androidOptInUrl,
                            appName = test.appName,
                            onRecordOpened = onRecordOpened
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("launch_app_btn_${test.id}"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = "فتح التطبيق",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("فتح التطبيق", maxLines = 1)
                }

                // Send feedback button
                OutlinedButton(
                    onClick = onFeedbackClick,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("feedback_btn_${test.id}"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Chat,
                        contentDescription = "تقديم ملاحظات",
                        tint = if (test.feedbackSent) SuccessGreen else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (test.feedbackSent) "تم التقييم ✓" else "تقييم وملاحظة",
                        maxLines = 1
                    )
                }
            }
        }
    }
}

fun launchOrOpenApp(
    context: Context,
    packageName: String,
    fallbackUrl: String,
    appName: String = "",
    onRecordOpened: (String, String) -> Unit = { _, _ -> }
): Boolean {
    val pm = context.packageManager
    val launchIntent = pm.getLaunchIntentForPackage(packageName)
    return if (launchIntent != null) {
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(launchIntent)
        Toast.makeText(context, "جاري فتح التطبيق وتسجيل مشاركتك...", Toast.LENGTH_SHORT).show()
        onRecordOpened(packageName, appName.ifBlank { packageName })
        true
    } else {
        // Not installed, open Play Store test link
        val url = if (fallbackUrl.isNotBlank()) fallbackUrl else "https://play.google.com/store/apps/details?id=$packageName"
        openUrl(context, url)
        Toast.makeText(context, "جاري فتح صفحة التثبيت والاختبار...", Toast.LENGTH_SHORT).show()
        onRecordOpened(packageName, appName.ifBlank { packageName })
        false
    }
}
