package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
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
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.PlayGreen
import com.example.ui.theme.SuccessGreen

data class GuideStep(
    val title: String,
    val summary: String,
    val details: String
)

@Composable
fun GuideScreen(
    onOpenOnboarding: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val steps = remember {
        listOf(
            GuideStep(
                title = "1. إنشاء مجموعة Google Groups للمختبرين",
                summary = "مجموعة جوجل هي أفضل وأسهل طريقة لإدارة المختبرين العشرين",
                details = "توجه إلى groups.google.com وأنشئ مجموعة جديدة. في إعدادات الوصول (Access Settings)، اختر أن يتمكن أي شخص لديه الرابط من الانضمام (Anyone can ask/join). هذا الرابط هو الذي ستشاركه مع المختبرين."
            ),
            GuideStep(
                title = "2. إنشاء مسار الاختبار المغلق (Closed Track)",
                summary = "في Google Play Console، اختر تطبيقك ثم 'الاختبار المغلق'",
                details = "داخل كونسول المطور، اذهب إلى: اختبار (Testing) > اختبار مغلق (Closed testing) > إنشاء مسار (Create track). سمّ المسار Alpha أو Closed Beta."
            ),
            GuideStep(
                title = "3. ربط المجموعة وتفعيل رابط الاشتراك (Opt-in Link)",
                summary = "أضف عنوان البريد الخاص بمجموعتك إلى قائمة المختبرين",
                details = "في قسم 'المختبرون' (Testers) داخل المسار، اختر Google Groups وأدخل بريد مجموعتك. بعد الحفظ، ستجد رابطين في الأسفل: Join on the web و Join on Android. انسخهما."
            ),
            GuideStep(
                title = "4. رفع حزمة التطبيق الموقعة (AAB) للمراجعة",
                summary = "يجب أن توافق جوجل على الإصدار أولاً قبل أن يتمكن المختبرون من تحميله",
                details = "ارفع ملف .aab موقع بمفتاح الرفع، واكتب ملاحظات الإصدار ثم اضغط 'إرسال للمراجعة'. تستغرق الموافقة الأولية للمسار المغلق عادة من يوم إلى 3 أيام عمل."
            ),
            GuideStep(
                title = "5. جمع 20 مختبراً على الأقل بالتبادل",
                summary = "استخدم PlayTester Hub لتبادل الاختبار مع مطورين آخرين",
                details = "يجب أن ينضم 20 شخصاً على الأقل للمجموعة ويضغطوا على رابط الاشتراك (Opt-in) ثم يحملوا التطبيق على أجهزتهم. يُفضل الوصول إلى 22-25 مختبراً احتياطاً لمن يحذف التطبيق."
            ),
            GuideStep(
                title = "6. استمرار التفاعل وفتح التطبيق لـ 14 يوماً متواصلاً",
                summary = "الشرط الأساسي: 14 يوماً متتالياً دون انقطاع وتفاعل فعلي",
                details = "تقوم جوجل بمراقبة فتح التطبيقات ومعدل البقاء. يساعدك متتبع PlayTester Hub بتذكيرك بتسجيل فتح التطبيق يومياً لمدة 14 يوماً وتقديم ملاحظات bugs أو أفكار تحسين."
            ),
            GuideStep(
                title = "7. التقديم على الإنتاج (Apply for Production)",
                summary = "بعد اكتمال 14 يوماً، يظهر زر 'التقدم بطلب لنشر التطبيق'",
                details = "ستظهر أسئلة حول: كيف جمعت المختبرين؟ ما هي الملاحظات التي تلقيتها؟ ما هي التحديثات التي قمت بها أثناء الاختبار؟ أجب بصدق واذكر الملاحظات الواقعية التي تم جمعها لضمان القبول الفوري."
            )
        )
    }

    val checkedSteps = remember { mutableStateMapOf<Int, Boolean>() }
    var expandedStepIndex by remember { mutableStateOf<Int?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("guide_screen_list"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Title Banner
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = PlayGreen,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "دليل اجتياز شرط الـ 14 يوماً لجوجل بلاي",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "قائمة مرجعية خطوة بخطوة للمطورين لضمان قبول تطبيقك في متجر Google Play الرسمي دون رفض.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    androidx.compose.material3.Button(
                        onClick = onOpenOnboarding,
                        shape = RoundedCornerShape(10.dp),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = PlayGreen),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_reopen_onboarding")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("عرض الجولة الترحيبية وشرح الـ 14 يوماً 🚀", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Checklist Items
        itemsIndexed(steps) { index, step ->
            val isChecked = checkedSteps[index] == true
            val isExpanded = expandedStepIndex == index

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("guide_step_$index"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(
                    1.dp,
                    if (isChecked) SuccessGreen.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { checkedSteps[index] = it },
                            modifier = Modifier.testTag("step_checkbox_$index")
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    expandedStepIndex = if (isExpanded) null else index
                                }
                        ) {
                            Text(
                                text = step.title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isChecked) SuccessGreen else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = step.summary,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.clickable {
                                expandedStepIndex = if (isExpanded) null else index
                            }
                        )
                    }

                    AnimatedVisibility(visible = isExpanded) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp, start = 44.dp, end = 8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = step.details,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // FAQs / Common Pitfalls
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.WarningAmber,
                            contentDescription = null,
                            tint = AmberGold,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "أسباب شائعة لرفض طلب الإنتاج وكيف تتفاداها:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "1️⃣ قلة التفاعل: لا يكفي أن يقوم المختبر بالاشتراك فقط، يجب فتح التطبيق لدقائق خلال الأسبوعين.\n" +
                                "2️⃣ انخفاض عدد المختبرين عن 20: إذا قام شخص بحذف التطبيق أو مغادرة المجموعة، يتوقف العداد.\n" +
                                "3️⃣ عدم وجود ملاحظات: تأكد من أن المختبرين يرسلون ملاحظات عبر متجر بلاي أو من خلال التطبيق لتوثيق التحسينات.\n" +
                                "4️⃣ إجابات سطحية في الاستبيان: اشرح بدقة ما تعلمته من الاختبار وكيف أصلحت الأخطاء.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
