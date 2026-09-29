package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.AmberGold
import com.example.ui.theme.PlayGreen
import com.example.ui.util.AppLanguage
import com.example.ui.util.LocalAppLanguage

data class ApprovalSecretTip(
    val title: String,
    val category: String,
    val description: String,
    val goldenRule: String,
    val isCrucial: Boolean = false
)

@Composable
fun PlayApprovalGuideDialog(
    onDismiss: () -> Unit
) {
    val language = LocalAppLanguage.current
    val isArabic = language == AppLanguage.AR
    var expandedIndex by remember { mutableStateOf<Int?>(0) }

    val tips = remember(isArabic) {
        if (isArabic) {
            listOf(
                ApprovalSecretTip(
                    title = "1. سر استبيان الإنتاج: الابتعاد عن الإجابات الجاهزة العامة",
                    category = "استبيان الإنتاج",
                    description = "فريق مراجعة جوجل يدقق بشدة في إجابات استبيان الـ 14 يوماً. كتابة عبارات مثل 'Everything was good' أو 'No bugs were found' تؤدي لرفض فوري تقريباً للاشتباه في غياب اختبار حقيقي.",
                    goldenRule = "القاعدة الذهبية: اذكر دائماً 2 إلى 3 ملاحظات واقعية تم إصلاحها في تطبيقك (مثل: ضبط تباعد الأزرار، تحسين استهلاك الذاكرة، تسريع تسجيل الدخول).",
                    isCrucial = true
                ),
                ApprovalSecretTip(
                    title = "2. رفع تحديث جديد (Release Update) أثناء الـ 14 يوماً",
                    category = "سجل التحديثات",
                    description = "تقارير المراجعة تظهر أن المطورين الذين يرفعون إصداراً محدثاً (مثل من v1.0 إلى v1.1) أثناء فترة الأسبوعين تزيد نسبة قبولهم في أول محاولة عن 95%.",
                    goldenRule = "القاعدة الذهبية: احرص على رفع حزمة AAB جديدة في المسار المغلق خلال الأسبوع الأول مصحوبة بملاحظات إصدار واضحة تترجم ملاحظات المختبرين.",
                    isCrucial = true
                ),
                ApprovalSecretTip(
                    title = "3. تقديم بيانات دخول تجريبية واضحة (App Access Credentials)",
                    category = "الوصول للتطبيق",
                    description = "إذا كان تطبيقك يتطلب حساباً، فإن عدم توفير اسم مستخدم وكلمة مرور لفريق مراجعة جوجل يؤدي لرفض تلقائي للمراجعة لتعذر فحص المحتوى الداخلي.",
                    goldenRule = "القاعدة الذهبية: اذهب إلى App Access في كونسول جوجل وأضف بيانات حساب تجريبي مجهز ومسبق التفعيل دون التحقق برقم هاتف.",
                    isCrucial = true
                ),
                ApprovalSecretTip(
                    title = "4. الحفاظ على معدل انهيار 0.0% في Android Vitals",
                    category = "استقرار الكود",
                    description = "تراقب خوارزميات جوجل معدل الانهيار اليومي (Crash rate) والـ ANRs أثناء فترة الاختبار المغلق. أي معدل أعلى من 1.09% قد يمنع صدور الموافقة.",
                    goldenRule = "القاعدة الذهبية: افحص صفحة Android Vitals أسبوعياً وتأكد من خلو الإصدار من أية استثناءات غير معالجة.",
                    isCrucial = false
                ),
                ApprovalSecretTip(
                    title = "5. التعامل مع رسالة «المراجعة تستغرق وقتاً أطول من المعتاد»",
                    category = "مدة المراجعة",
                    description = "تستغوق المراجعة الأولى للمسار المغلق عادة من 1 إلى 4 أيام عمل. قد تتأخر الحسابات الجديدة قليلاً دون وجود أي خطأ برمجيات.",
                    goldenRule = "القاعدة الذهبية: لا تقم بإلغاء الإصدار أو تعديله أثناء وجوده في مرحلة In review حتى لا تعود لنهاية طابور الفحص من الصفر.",
                    isCrucial = false
                )
            )
        } else {
            listOf(
                ApprovalSecretTip(
                    title = "1. Production Questionnaire: Avoid Generic Responses",
                    category = "Questionnaire",
                    description = "Google review teams heavily scrutinize questionnaire answers. Writing clichés like 'Everything was great' or 'No bugs found' triggers near-instant rejection due to suspected simulated testing.",
                    goldenRule = "Golden Rule: Always detail 2 to 3 genuine user feedback items that you fixed (e.g. button padding, memory optimization, login speed).",
                    isCrucial = true
                ),
                ApprovalSecretTip(
                    title = "2. Deploy an Updated Release (AAB) During the 14 Days",
                    category = "Releases",
                    description = "Telemetry shows developers who release an incremental update (e.g., v1.0 to v1.1) during the 14-day test track have over a 95% first-attempt approval rate.",
                    goldenRule = "Golden Rule: Upload a refreshed AAB bundle during testing with clear release notes reflecting tester suggestions.",
                    isCrucial = true
                ),
                ApprovalSecretTip(
                    title = "3. Provide Working Test Credentials (App Access)",
                    category = "App Access",
                    description = "If your app requires account authentication and demo credentials are not configured, your submission will be rejected due to inaccessible content.",
                    goldenRule = "Golden Rule: In Play Console go to 'App Access' and provide active pre-verified demo credentials without SMS 2FA.",
                    isCrucial = true
                ),
                ApprovalSecretTip(
                    title = "4. Maintain 0.0% Crash Rate & Zero ANRs in Android Vitals",
                    category = "Stability",
                    description = "Google algorithms monitor crash rates and ANRs throughout the closed track. Any crash rate above 1.09% halts production eligibility.",
                    goldenRule = "Golden Rule: Review Android Vitals weekly and ensure all runtime exceptions and background leaks are resolved.",
                    isCrucial = false
                ),
                ApprovalSecretTip(
                    title = "5. Handling 'Review is taking longer than usual'",
                    category = "Review Times",
                    description = "Initial closed testing reviews typically take 1 to 4 business days. Newer developer accounts may take slightly longer without any policy violation.",
                    goldenRule = "Golden Rule: Do not discard or edit the track while 'In review', or you will reset your position in the review queue.",
                    isCrucial = false
                )
            )
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .height(620.dp)
                .padding(vertical = 16.dp)
                .testTag("dialog_play_approval_guide"),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            border = BorderStroke(1.dp, PlayGreen.copy(alpha = 0.3f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                // Header
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
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = PlayGreen,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isArabic) "أسرار وقواعد القبول في Google Play" else "Google Play Approval Secrets & Rules",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isArabic) "دليل تجارب المطورين للقبول من أول مراجعة" else "Field-tested developer guide for 1st-try approval",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = if (isArabic) "إغلاق" else "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                Spacer(modifier = Modifier.height(10.dp))

                // Tips List
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    itemsIndexed(tips) { index, tip ->
                        val isExpanded = expandedIndex == index

                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
                            border = BorderStroke(
                                1.dp,
                                if (tip.isCrucial) AmberGold.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateContentSize()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { expandedIndex = if (isExpanded) null else index },
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                        Icon(
                                            imageVector = if (tip.isCrucial) Icons.Default.WarningAmber else Icons.Default.Lightbulb,
                                            contentDescription = null,
                                            tint = if (tip.isCrucial) AmberGold else PlayGreen,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = tip.title,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Icon(
                                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                AnimatedVisibility(visible = isExpanded) {
                                    Column(modifier = Modifier.padding(top = 10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text(
                                            text = tip.description,
                                            style = MaterialTheme.typography.bodySmall,
                                            lineHeight = 20.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )

                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (tip.isCrucial) AmberGold.copy(alpha = 0.12f) else PlayGreen.copy(alpha = 0.12f),
                                            border = BorderStroke(1.dp, if (tip.isCrucial) AmberGold.copy(alpha = 0.3f) else PlayGreen.copy(alpha = 0.3f)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.Top) {
                                                Icon(
                                                    imageVector = Icons.Default.Star,
                                                    contentDescription = null,
                                                    tint = if (tip.isCrucial) AmberGold else PlayGreen,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = tip.goldenRule,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (tip.isCrucial) AmberGold else PlayGreen,
                                                    lineHeight = 18.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PlayGreen)
                ) {
                    Text(if (isArabic) "فهمت ذلك، شكراً!" else "Got it, thank you!", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
