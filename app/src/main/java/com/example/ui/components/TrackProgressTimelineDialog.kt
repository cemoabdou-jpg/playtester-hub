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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.UserApp
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DangerRed
import com.example.ui.theme.PlayGreen
import com.example.ui.theme.SuccessGreen

@Composable
fun TrackProgressTimelineDialog(
    app: UserApp,
    onDismiss: () -> Unit,
    onOpenReadinessReport: (UserApp) -> Unit = {}
) {
    val daysElapsed = app.daysElapsed.coerceIn(0, 14)
    val testersCount = app.testersCount
    val targetTesters = app.targetTesters.coerceAtLeast(20)
    val isComplete = daysElapsed >= 14 && testersCount >= targetTesters

    var selectedDayIndex by remember { mutableIntStateOf(daysElapsed.coerceAtLeast(1)) }
    var showHealthScoreDetails by remember { mutableStateOf(false) }

    // Health Score calculation (0 - 100)
    val testersScore = (testersCount.toFloat() / targetTesters * 40).coerceIn(0f, 40f)
    val daysScore = (daysElapsed.toFloat() / 14 * 40).coerceIn(0f, 40f)
    val linksScore = if (app.webOptInUrl.isNotBlank() && app.androidOptInUrl.isNotBlank()) 20f else 10f
    val totalHealthScore = (testersScore + daysScore + linksScore).toInt()

    val (gradeText, gradeColor) = when {
        totalHealthScore >= 90 -> "A+ (جاهزية ممتازة للإنتاج)" to SuccessGreen
        totalHealthScore >= 75 -> "A (أداء قوي ومستقر)" to PlayGreen
        totalHealthScore >= 50 -> "B (قيد التقدم المنتظم)" to AmberGold
        else -> "C (بحاجة لمزيد من المختبرين)" to DangerRed
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 16.dp)
                .testTag("dialog_track_progress_timeline"),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            border = BorderStroke(1.dp, PlayGreen.copy(alpha = 0.3f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Top Header with Close Icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = PlayGreen.copy(alpha = 0.15f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Timeline,
                                    contentDescription = null,
                                    tint = PlayGreen,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "مخطط مسار الـ 14 يوماً التفاعلي",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = app.name,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق")
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                // Release Health Grade Banner
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = gradeColor.copy(alpha = 0.1f)),
                    border = BorderStroke(1.dp, gradeColor.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "مؤشر صحة الإصدار والجاهزية",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = gradeText,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = gradeColor
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = gradeColor,
                                modifier = Modifier.size(48.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "$totalHealthScore%",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.Black
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        LinearProgressIndicator(
                            progress = { totalHealthScore / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape),
                            color = gradeColor,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            strokeCap = StrokeCap.Round
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("المختبرون: $testersCount/$targetTesters", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("الأيام: $daysElapsed/14 يوم", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("الروابط: مكتملة ✓", style = MaterialTheme.typography.labelSmall, color = SuccessGreen)
                        }
                    }
                }

                // Days Carousel Selector (Day 1 to 14)
                Text(
                    text = "سلسلة الأيام الـ 14 (اختر يوماً لمعرفة إرشاداته):",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    items(14) { index ->
                        val dayNum = index + 1
                        val isPast = dayNum < daysElapsed
                        val isCurrent = dayNum == daysElapsed
                        val isSelected = dayNum == selectedDayIndex

                        val dayBgColor = when {
                            isSelected -> PlayGreen
                            isPast -> SuccessGreen.copy(alpha = 0.2f)
                            isCurrent -> AmberGold.copy(alpha = 0.2f)
                            else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        }

                        val dayTextColor = when {
                            isSelected -> Color.Black
                            isPast -> SuccessGreen
                            isCurrent -> AmberGold
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = dayBgColor,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) PlayGreen else if (isCurrent) AmberGold else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                            ),
                            modifier = Modifier
                                .clickable { selectedDayIndex = dayNum }
                                .padding(vertical = 2.dp)
                                .testTag("day_chip_$dayNum")
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "يوم $dayNum",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = dayTextColor
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Icon(
                                    imageVector = when {
                                        isPast -> Icons.Default.CheckCircle
                                        isCurrent -> Icons.Default.HourglassTop
                                        else -> Icons.Default.Lock
                                    },
                                    contentDescription = null,
                                    tint = dayTextColor,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }

                // Selected Day Detailed Breakdown
                val (dayTitle, dayAction, dayTip) = getDayGuidance(selectedDayIndex)

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = PlayGreen.copy(alpha = 0.2f),
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "$selectedDayIndex",
                                            fontWeight = FontWeight.Bold,
                                            color = PlayGreen,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = dayTitle,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            if (selectedDayIndex <= daysElapsed) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = SuccessGreen.copy(alpha = 0.2f)
                                ) {
                                    Text("مكتمل ✓", color = SuccessGreen, fontWeight = FontWeight.Bold, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                        }

                        Text(
                            text = dayAction,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 18.sp
                        )

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = AmberGold.copy(alpha = 0.1f),
                            border = BorderStroke(1.dp, AmberGold.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.Top) {
                                Icon(Icons.Default.Lightbulb, contentDescription = null, tint = AmberGold, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = dayTip,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AmberGold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                // Action Buttons at Bottom
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("إغلاق")
                    }

                    Button(
                        onClick = {
                            onDismiss()
                            onOpenReadinessReport(app)
                        },
                        modifier = Modifier.weight(1.5f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = if (isComplete) SuccessGreen else PlayGreen)
                    ) {
                        Icon(
                            imageVector = if (isComplete) Icons.Default.CheckCircle else Icons.Default.RocketLaunch,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isComplete) "تصدير تقرير الإنتاج 🚀" else "فحص الجاهزية الكاملة",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

private fun getDayGuidance(day: Int): Triple<String, String, String> {
    return when (day) {
        1 -> Triple(
            "اليوم 1: انطلاق المسار المغلق وتفعيل الاشتراكات",
            "تأكد من قيام أول 5 مختبرين بالضغط على رابط الـ Opt-in وتثبيت التطبيق على هواتفهم وتشغيله للمرة الأولى.",
            "تأكد من حالة الإصدار في Google Play Console أنها Available وليست In review."
        )
        2, 3 -> Triple(
            "الأيام 2-3: زيادة وتيرة المختبرين وبلوغ الـ 20",
            "انشر رابط تطبيقك بالمنصة لضم ما لا يقل عن 22-25 مختبراً مسجلاً كحزام أمان ضد أي انسحاب مفاجئ.",
            "متابعة فتح التطبيق يومياً لمدة دقيقتين على الأقل لتسجيل التفاعل في Android Vitals."
        )
        4, 5 -> Triple(
            "الأيام 4-5: مراجعة التقييمات الأولية والأداء",
            "افحص التغذية الراجعة الواردة من المختبرين حول سرعة الواجهات والخطوط واستقرار تسجيل الدخول.",
            "قم بتسجيل أية مشاكل برمجية صغيرة لإصلاحها في التحديث القادم أثناء فترة الاختبار."
        )
        6, 7 -> Triple(
            "الأيام 6-7: إتمام الأسبوع الأول ورفع تحديث تحسيني (Update AAB)",
            "ارفع إصداراً جديداً (مثل v1.0.1) في المسار المغلق. تفضل جوجل رؤية المطور يتفاعل مع الملاحظات بتحديث فعلي.",
            "رفع تحديث أثناء الاختبار يرفع نسبة قبول الإنتاج لأكثر من 95% وفق معايير جوجل."
        )
        8, 9, 10 -> Triple(
            "الأيام 8-10: استقرار مستمر ومراقبة معدلات الانهيار",
            "تأكد من أن نسبة الانهيارات ANRs & Crashes تساوي 0.0% داخل تقارير Android Vitals.",
            "تذكير المختبرين بالاستمرار في فتح التطبيق للحفاظ على سلاسل التفاعل المتتالية."
        )
        11, 12, 13 -> Triple(
            "الأيام 11-13: التحضير لبيانات الحساب واستبيان الإنتاج",
            "راجع بيانات الحساب التجريبي (App Access Demo Credentials) وجهز نماذج إجابات استبيان الإنتاج المعتمدة من المنصة.",
            "تأكد من صحة رابط سياسة الخصوصية وأنه يفتح صفحة سارية دون أي خطأ 404."
        )
        14 -> Triple(
            "اليوم 14: اكتمال المدة وظهور زر التقديم للإنتاج 🎯",
            "مبارك! يظهر الآن زر Apply for Production في لوحة التحكم بكونسول جوجل. انسخ نماذج الإجابات وقدم طلبك بثقة.",
            "قم بتصدير تقرير الجاهزية الكامل لتوثيق أرقام المختبرين وتحديثاتك في الاستبيان."
        )
        else -> Triple("مرحلة الاختبار", "استمر في متابعة تفاعل المختبرين.", "حافظ على بقاء 20 مختبراً نشطاً.")
    }
}
