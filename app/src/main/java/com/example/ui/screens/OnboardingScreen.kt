package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.PlayGreen
import kotlinx.coroutines.launch

data class OnboardingPageData(
    val title: String,
    val subtitle: String,
    val badgeText: String,
    val badgeIcon: ImageVector,
    val badgeColor: Color,
    val imageResId: Int,
    val mainDescription: String,
    val keyPoints: List<Pair<ImageVector, String>>,
    val highlightNote: String
)

@Composable
fun OnboardingScreen(
    onFinishOnboarding: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pages = listOf(
        OnboardingPageData(
            title = "شرط الـ 14 يوماً الإلزامي من Google Play",
            subtitle = "متطلب أساسي وحاسم لنشر التطبيقات للمطورين الجدد",
            badgeText = "شروط Google Play الرسمية 🎯",
            badgeIcon = Icons.Default.Security,
            badgeColor = PlayGreen,
            imageResId = R.drawable.promo_step1_track_1789982321864,
            mainDescription = "تفرض شركة Google على جميع حسابات المطورين الشخصية المنشأة بعد نوفمبر 2023 تجربة التطبيق مع 20 مختبراً نشطاً لمدة 14 يوماً متواصلة على الأقل قبل الموافقة على طرحه للعامة في متجر Play.",
            keyPoints = listOf(
                Icons.Default.Groups to "توفير 20 مختبراً حقيقياً مسجلين في قائمة الاختبار المغلق.",
                Icons.AutoMirrored.Filled.FactCheck to "التزام المختبرين بفتح التطبيق وتجربته طوال الـ 14 يوماً دون انقطاع.",
                Icons.Default.Star to "جمع تقارير أداء وملاحظات واقعية لتحسين التطبيق وتفادي الرفض."
            ),
            highlightNote = "تطبيق PlayTester Hub صُمم خصيصاً ليحل هذه العقبة للمطورين العرب بمجتمع متكامل."
        ),
        OnboardingPageData(
            title = "نظام التبادل التشاركي الذكي",
            subtitle = "ساعد المطورين الآخرين واكسب مختبرين لتطبيقك مجاناً",
            badgeText = "مجتمع المطورين التفاعلي 🤝",
            badgeIcon = Icons.Default.Groups,
            badgeColor = CyanAccent,
            imageResId = R.drawable.promo_step2_testers_1789982333758,
            mainDescription = "يقوم النظام على مبدأ التكافل والتبادل العادل: عندما تنضم لاختبار تطبيقات زملائك وتفحصها يومياً، تكسب نقاطاً وشارات تُمكّنك من نشر تطبيقاتك وجلب 20 مختبراً موثوقاً بسرعة فائقة.",
            keyPoints = listOf(
                Icons.Default.RocketLaunch to "أضف تطبيقك برابط Google Play أو ملف APK ورمز الانضمام.",
                Icons.Default.EmojiEvents to "اكسب نقاطاً ومكافآت يومية عند فتح وتجربة تطبيقات المجتمع.",
                Icons.Default.CheckCircle to "دعوة المختبرين بنقرة واحدة وإدارة بريد مجموعات Google Groups."
            ),
            highlightNote = "كلما كنت مختبراً نشطاً وموثوقاً، ارتفعت أولوية ظهور تطبيقك في مقدمة القائمة!"
        ),
        OnboardingPageData(
            title = "الفحص اليومي، التذكيرات والملاحظات",
            subtitle = "متابعة دقيقة تضمن عدم انقطاع عداد الـ 14 يوماً",
            badgeText = "التزام ويقظة مستمرة ⏰⚡",
            badgeIcon = Icons.Default.Alarm,
            badgeColor = AmberGold,
            imageResId = R.drawable.app_screen_checklist_1789985928642,
            mainDescription = "نظام ذكي متكامل يتابع جلسات الفحص اليومية ويرسل لك إشعارات وتنبيهات في شريط الهاتف لتذكيرك بالأيام المتبقية وتأكيد فتح التطبيقات وإرسال الملاحظات للمطورين.",
            keyPoints = listOf(
                Icons.Default.Alarm to "إشعارات تذكير ذكية في شريط الهاتف مع لوجو التطبيق.",
                Icons.Default.AutoAwesome to "تسجيل مدة الفحص وكتابة ملاحظات الجودة وتقييم الاستقرار.",
                Icons.Default.EmojiEvents to "حفظ سلاسل الأيام المتتالية (Streaks) وفتح شارات الشرف."
            ),
            highlightNote = "تأكيدك اليومي يحمي المطور من إعادة فترة الـ 14 يوماً من الصفر!"
        ),
        OnboardingPageData(
            title = "الجاهزية للنشر والإنتاج في Google Play",
            subtitle = "أكمل رحلتك وحوّل فكرتك إلى تطبيق عالمي متاح للجميع",
            badgeText = "الوصول للإنتاج الكامل 🚀",
            badgeIcon = Icons.Default.RocketLaunch,
            badgeColor = PlayGreen,
            imageResId = R.drawable.promo_step3_publish_1789982347173,
            mainDescription = "بمجرد إتمام فترة الاختبار، يوفر لك التطبيق تقريراً شاملاً بكافة إحصائيات المختبرين وتفاعلهم لتعبئة استبيان Google Play Console والإجابة على أسئلة الوصول للإنتاج بنجاح وقبول فوري.",
            keyPoints = listOf(
                Icons.Default.CheckCircle to "تصدير قائمة المختبرين والتقييمات بصيغة واضحة.",
                Icons.Default.Security to "دليل إرشادي تفصيلي للإجابة على أسئلة الوصول للإنتاج بدون رفض.",
                Icons.Default.RocketLaunch to "نشر تطبيقك على Google Play بثقة تامة واستقرار مضمون."
            ),
            highlightNote = "أنت الآن جاهز للانضمام لمجتمع المطورين والمختبرين الأقوى والأكثر فاعلية!"
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(MaterialTheme.colorScheme.background)
            .testTag("onboarding_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            // Top Bar: Brand + Skip Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = PlayGreen.copy(alpha = 0.15f),
                        modifier = Modifier.size(34.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.RocketLaunch,
                                contentDescription = null,
                                tint = PlayGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "PlayTester Hub",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                if (pagerState.currentPage < pages.size - 1) {
                    TextButton(
                        onClick = onFinishOnboarding,
                        modifier = Modifier.testTag("btn_onboarding_skip")
                    ) {
                        Text(
                            text = "تخطي ⏩",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Pager Container
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { pageIndex ->
                val page = pages[pageIndex]
                OnboardingPageContent(page = page)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bottom Navigation Controls
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Dot Indicators
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 12.dp)
                ) {
                    repeat(pages.size) { index ->
                        val isSelected = pagerState.currentPage == index
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .height(8.dp)
                                .width(if (isSelected) 24.dp else 8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) PlayGreen else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                                )
                                .clickable {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Bottom Buttons (Back / Next or Finish)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (pagerState.currentPage > 0) {
                        OutlinedButton(
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(0.8f)
                                .height(50.dp)
                                .testTag("btn_onboarding_prev")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("السابق", fontWeight = FontWeight.Bold)
                        }
                    }

                    val isLastPage = pagerState.currentPage == pages.size - 1
                    Button(
                        onClick = {
                            if (isLastPage) {
                                onFinishOnboarding()
                            } else {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isLastPage) PlayGreen else MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1.2f)
                            .height(50.dp)
                            .testTag("btn_onboarding_next_or_finish")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isLastPage) "ابدأ رحلة الاختبار الآن 🚀" else "التالي",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isLastPage) Color.Black else MaterialTheme.colorScheme.onPrimary
                            )
                            if (!isLastPage) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OnboardingPageContent(
    page: OnboardingPageData,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(top = 4.dp, bottom = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Hero Image Card
        Card(
            shape = RoundedCornerShape(18.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = page.imageResId),
                    contentDescription = page.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.7f)
                                )
                            )
                        )
                )

                // Category / Step Tag on image
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = page.badgeColor.copy(alpha = 0.9f),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = page.badgeIcon,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = page.badgeText,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Titles
        Text(
            text = page.title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = page.subtitle,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = page.badgeColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Main Description Card
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = page.mainDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 22.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Key Points Checklist
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                page.keyPoints.forEachIndexed { index, (icon, text) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = page.badgeColor.copy(alpha = 0.15f),
                            modifier = Modifier
                                .size(26.dp)
                                .padding(top = 2.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = page.badgeColor,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = text,
                            style = MaterialTheme.typography.bodySmall,
                            lineHeight = 19.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (index < page.keyPoints.size - 1) {
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Highlight Info Banner
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = AmberGold.copy(alpha = 0.12f),
            border = androidx.compose.foundation.BorderStroke(1.dp, AmberGold.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = AmberGold,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = page.highlightNote,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = AmberGold,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
