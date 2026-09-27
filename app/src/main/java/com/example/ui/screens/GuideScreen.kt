package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DangerRed
import com.example.ui.theme.IndigoAccent
import com.example.ui.theme.PlayGreen
import com.example.ui.theme.PlayGreenLight
import com.example.ui.theme.SuccessGreen

enum class GuideTab(val title: String, val icon: ImageVector) {
    ALL("الكل", Icons.Default.FilterList),
    CHECKLIST("خطوات الـ 14 يوماً", Icons.Default.CheckCircle),
    PRODUCTION_QA("نماذج إجابات الإنتاج", Icons.Default.QuestionAnswer),
    PITFALLS("تفادي الرفض", Icons.Default.WarningAmber),
    FAQ("الأسئلة الشائعة", Icons.AutoMirrored.Filled.HelpOutline),
    RESOURCES("أدوات ومصادر", Icons.Default.Link)
}

data class GuideStepItem(
    val id: Int,
    val title: String,
    val summary: String,
    val details: String,
    val importantTip: String? = null,
    val tag: String
)

data class ProductionQuestion(
    val title: String,
    val subtitle: String,
    val arabicTemplate: String,
    val englishTemplate: String,
    val keyTips: List<String>
)

data class PitfallItem(
    val title: String,
    val severity: String,
    val reason: String,
    val solution: String
)

data class FaqItem(
    val question: String,
    val answer: String
)

@Composable
fun GuideScreen(
    onOpenOnboarding: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var selectedTab by remember { mutableStateOf(GuideTab.ALL) }
    var searchQuery by remember { mutableStateOf("") }

    val checkedSteps = remember { mutableStateMapOf<Int, Boolean>() }
    var expandedStepId by remember { mutableStateOf<Int?>(null) }
    var expandedQaIndex by remember { mutableStateOf<Int?>(null) }
    var expandedPitfallIndex by remember { mutableStateOf<Int?>(null) }
    var expandedFaqIndex by remember { mutableStateOf<Int?>(null) }

    val officialEmail = "player-test-hup@googlegroups.com"

    val steps = remember {
        listOf(
            GuideStepItem(
                id = 1,
                title = "1. إضافة المختبرين في مسار الاختبار المغلق (إيميلات مخصصة أو مجموعة)",
                summary = "يمكن للمطور إضافة قائمة إيميلات المختبرين مباشرة، أو استخدام مجموعة Google خاصة أو مشتركة",
                details = "في Google Play Console داخل مسار الاختبار المغلق (Closed testing)، يدعم النظام طريقتين معتمدتين لإضافة المختبرين: إما بإضافة قائمة بريدية مخصصة (Email List) تحتوي على إيميلات المختبرين المحددة من طرفك مباشرة، أو تحديد مجموعة Google Groups (مجموعتك الخاصة أو بريد المجموعة المشتركة $officialEmail). يمنحك ذلك حرية وتحكماً كاملاً.",
                importantTip = "إمكانية إضافة إيميلات المختبرين مباشرة من طرفك تضمن التحكم الدقيق في قائمة المدعوين دون إلزام الجميع بالانضمام لمجموعة محددة.",
                tag = "إدارة المختبرين"
            ),
            GuideStepItem(
                id = 2,
                title = "2. إنشاء مسار الاختبار المغلق (Closed Track)",
                summary = "في Google Play Console، اختر تطبيقك ثم 'الاختبار المغلق' (Closed testing)",
                details = "داخل كونسول المطور، اذهب إلى: اختبار (Testing) > اختبار مغلق (Closed testing) > إنشاء مسار (Create track). سمّ المسار Alpha أو Closed Beta وتأكد من حفظ الإعدادات.",
                importantTip = "تأكد من ضبط الدولة والمناطق المتاحة لتشمل كافة الدول أو دول مجتمع المنصة.",
                tag = "Google Play Console"
            ),
            GuideStepItem(
                id = 3,
                title = "3. اعتماد المختبرين واستخراج روابط الاشتراك (Opt-in Links)",
                summary = "أضف قائمة الإيميلات أو المجموعة واحفظ التغييرات لنسخ رابطي الويب والأندرويد",
                details = "في قسم 'المختبرون' (Testers) بالمسار المغلق، اختر قوائم البريد الإلكتروني (Email lists) لإضافة إيميلات المختبرين، أو اختر Google Groups. بعد حفظ التغييرات، ستجد رابطين في الأسفل: Join on the web و Join on Android. انسخهما وضعهما عند نشر تطبيقك في المنصة.",
                importantTip = "روابط Opt-in هي المفتاح الرسمي الذي يضغط عليه المختبر المعتمد (سواء المضاف بالإيميل أو عبر المجموعة) لبدء احتساب الـ 14 يوماً.",
                tag = "روابط الاشتراك"
            ),
            GuideStepItem(
                id = 4,
                title = "4. رفع حزمة التطبيق الموقعة (AAB) للمراجعة الأولية",
                summary = "يجب أن توافق جوجل على الإصدار أولاً قبل أن يتمكن المختبرون من تحميله وتجربته",
                details = "ارفع ملف .aab موقع بمفتاح الرفع، واكتب ملاحظات الإصدار (Release notes) ثم اضغط 'إرسال للمراجعة'. تستغرق الموافقة الأولية للمسار المغلق عادة من يوم إلى 3 أيام عمل.",
                importantTip = "لا يمكن للمختبرين تثبيت التطبيق حتى تصبح حالة الإصدار 'Available on Google Play' في المسار المغلق.",
                tag = "حزم النشر AAB"
            ),
            GuideStepItem(
                id = 5,
                title = "5. جمع 20 مختبراً على الأقل وتفعيل اشتراكهم بالتبادل",
                summary = "انشر تطبيقك في PlayTester Hub لجمع 20+ مختبراً موثوقاً وتأكيد مشاركتهم",
                details = "يجب أن يقوم 20 مختبراً على الأقل (من المضافين في قائمتك البريدية أو مجموعتك) بالضغط على رابط الاشتراك (Opt-in) وتثبيت التطبيق على أجهزتهم الفعلية والتفاعل معه. يُفضل الوصول إلى 22-25 مختبراً احتياطاً لمن يحذف التطبيق أو يغير جهازه.",
                importantTip = "شارك في اختبار تطبيقات زملائك المطورين لتحصل على نقاط وتضع تطبيقك في صدارة التطبيقات المطلوبة للاختبار.",
                tag = "مجتمع المختبرين"
            ),
            GuideStepItem(
                id = 6,
                title = "6. استمرار التفاعل وفتح التطبيق لـ 14 يوماً متواصلاً",
                summary = "الشرط الحاسم: 14 يوماً متتالياً دون انقطاع وتفاعل فعلي مع تقديم ملاحظات",
                details = "تقوم جوجل بمراقبة فتح التطبيقات ومعدل البقاء وأجهزة المختبرين. يساعدك متتبع PlayTester Hub بتذكيرك بتسجيل فتح التطبيق يومياً لمدة 14 يوماً وتقديم ملاحظات bugs أو أفكار تحسين وحفظ سلاسل الأيام المتتالية.",
                importantTip = "احرص على ألا ينخفض عدد المختبرين النشطين عن 20 خلال أي يوم من الـ 14 يوماً لتجنب إعادة العداد.",
                tag = "فترة الـ 14 يوماً"
            ),
            GuideStepItem(
                id = 7,
                title = "7. التقديم على الإنتاج (Apply for Production) وتعبئة الاستبيان",
                summary = "بعد اكتمال 14 يوماً، يظهر زر 'التقدم بطلب لنشر التطبيق في الإنتاج'",
                details = "ستظهر أسئلة حول: كيف جمعت المختبرين؟ ما هي الملاحظات التي تلقيتها؟ ما هي التحديثات التي قمت بها أثناء الاختبار؟ استخدم نماذج الإجابات المعتمدة في هذا الدليل للإجابة بدقة وشفافية لضمان القبول الفوري.",
                importantTip = "قم بتصدير تقرير الجاهزية من المنصة للاستعانة بالإحصائيات والتحسينات الموثقة أثناء تعبئة الاستبيان.",
                tag = "النشر للإنتاج"
            )
        )
    }

    val productionQuestions = remember {
        listOf(
            ProductionQuestion(
                title = "سؤال 1: كيف قمت بتجنيد وتوظيف المختبرين؟",
                subtitle = "How did you recruit testers for your closed test?",
                arabicTemplate = "تم تجنيد المختبرين من خلال شبكة ومجتمع مطوري أندرويد المعتمد (PlayTester Hub) وقوائم البريد الإلكتروني المخصصة ومجموعات Google Groups المتخصصة في اختبار الجودة. قمنا بضم واعتماد أكثر من 20 مختبراً حقيقياً يمتلكون أجهزة أندرويد متنوعة بمستويات API وإصدارات شاشات مختلفة، مع توجيههم لاتباع سيناريوهات استخدام واقعية وتجربة كافة وظائف التطبيق طوال فترة الـ 14 يوماً.",
                englishTemplate = "Testers were recruited through an active Android developer community (PlayTester Hub), dedicated email tester lists, and Google Groups for quality assurance. We onboarded 20+ verified real testers with diverse Android devices across different OS versions and screen sizes, guiding them through realistic test scenarios and ensuring active daily testing throughout the 14-day closed testing track.",
                keyTips = listOf(
                    "أكد أن المختبرين مستخدمون حقيقيون ولديهم أجهزة فعلية وليسوا برامج روبوت أو محاكيات.",
                    "وضح التنوع في إصدارات أندرويد والأجهزة وقنوات ضم المختبرين (قوائم الإيميلات والمجموعات)."
                )
            ),
            ProductionQuestion(
                title = "سؤال 2: ما هي التغذية الراجعة والملاحظات التي تلقيتها؟",
                subtitle = "What feedback did you receive from testers during closed testing?",
                arabicTemplate = "تلقينا ملاحظات قيمة شملت: تحسين تجربة تسجيل الدخول وجعل الخطوات أسرع، إصلاح تباعد بعض العناصر في الشاشات الصغيرة، تحسين استجابة الرسوم المتحركة في القوائم، والتأكد من دعم الوضع الداكن بشكل متناسق. كما أشاد المختبرون باستقرار التطبيق وسهولة تصفح الواجهات.",
                englishTemplate = "We gathered actionable feedback including: streamlining the authentication flow for faster onboarding, optimizing UI padding and text scaling on smaller phone screens, improving animation smoothness in list rendering, and ensuring full dark mode contrast consistency. Testers also confirmed overall stability and intuitive navigation.",
                keyTips = listOf(
                    "اذكر تفاصيل محددة (مثل UI, أداء, دعم شاشات, سهولة الاستخدام) بدلاً من كتابة 'كل شيء كان ممتازاً'.",
                    "جوجل تفضل دائماً رؤية ملاحظات حقيقية تشير إلى أن التطبيق خضع لاختبار واقعي."
                )
            ),
            ProductionQuestion(
                title = "سؤال 3: ما هي التعديلات والتحديثات التي قمت بها أثناء الاختبار؟",
                subtitle = "What changes did you make to your app based on tester feedback?",
                arabicTemplate = "بناءً على التغذية الراجعة، قمنا بإصدار تحديث جديد أثناء فترة الاختبار تضمن: معالجة تسريب طفيف في الذاكرة لتقليل استهلاك البطارية، إعادة تصميم بعض الأزرار لتوافق معايير إمكانية الوصول (Accessibility 48dp)، وإضافة رسائل خطأ توضيحية عند انقطاع الاتصال بالإنترنت.",
                englishTemplate = "Based on feedback, we rolled out an updated release during the testing track: resolved minor memory overhead to improve battery efficiency, enlarged interactive touch targets to satisfy Material 3 accessibility standards (48dp), and implemented friendly offline error states with automatic retry handling.",
                keyTips = listOf(
                    "يُحبذ رفع إصدار جديد (New Release AAB) في المسار المغلق أثناء الأسبوعين كدليل على تفاعلك مع الملاحظات.",
                    "بين كيف عززت التحديثات تجربة المستخدم واستقرار التطبيق."
                )
            ),
            ProductionQuestion(
                title = "سؤال 4: كيف تأكدت من أن تطبيقك جاهز للطرح في الإنتاج العام؟",
                subtitle = "How did you decide that your app is ready for production?",
                arabicTemplate = "تأكدنا من الجاهزية بعد: إكمال 14 يوماً متواصلاً دون تسجيل أي أعطال برمجية حرجة (Crashes / ANRs = 0%)، تأكيد جميع المختبرين لسلاسة رحلة المستخدم الأساسية، استيفاء سياسات الخصوصية والأمان لمتجر Google Play، وإجراء اختبارات أداء على شبكات بيانات مختلفة.",
                englishTemplate = "Production readiness was verified after: achieving 14 consecutive testing days with zero critical crashes or ANRs, unanimous tester confirmation of smooth primary user workflows, full compliance with Google Play Developer Policies and Privacy Guidelines, and robust stress-testing across diverse network conditions.",
                keyTips = listOf(
                    "أشر إلى مقاييس الاستقرار (Zero crashes / Zero ANRs).",
                    "أكد التوافق مع سياسات الخصوصية والأمان الرسمية لـ Google Play."
                )
            )
        )
    }

    val pitfalls = remember {
        listOf(
            PitfallItem(
                title = "انخفاض عدد المختبرين النشطين عن 20",
                severity = "حرج جداً 🛑",
                reason = "إذا قام أحد المختبرين بإلغاء التثبيت أو حذفه من القائمة وانخفض عدد المختبرين النشطين عن 20، يتوقف عداد الـ 14 يوماً فوراً.",
                solution = "حافظ دائماً على 22 إلى 25 مختبراً مسجلاً ومضافاً في مسارك المغلق كحزام أمان، وتابع تقرير الجاهزية بالمنصة بانتظام."
            ),
            PitfallItem(
                title = "انعدام التفاعل الفعلي وعدم فتح التطبيق",
                severity = "شائع ومسبب للرفض ⚠️",
                reason = "جوجل تراقب ما إذا كان المختبرون يفتحون التطبيق ويتفاعلون معه فعلياً وليس فقط الضغط على زر الاشتراك.",
                solution = "استخدم نظام التذكيرات اليومية وسلاسل الأيام بالمنصة لحث المختبرين على فتح التطبيق لدقائق وتقديم تقييمات واقعية."
            ),
            PitfallItem(
                title = "إجابات سطحية أو منسوخة بحرفية في استبيان الإنتاج",
                severity = "مسبب للرفض ⚠️",
                reason = "كتابة جمل عامة مثل 'Everything was great' أو 'No feedback' تُشعر فريق المراجعة بعدم وجود اختبار حقيقي.",
                solution = "خصص الإجابات لتطبيقك، واذكر ميزات محددة وأمثلة واقعية على الملاحظات التي تم استلامها والتحديثات المطبقة."
            ),
            PitfallItem(
                title = "إغفال بيانات الحساب التجريبي (Demo Credentials)",
                severity = "مسبب للرفض ⚠️",
                reason = "إذا كان تطبيقك يتطلب تسجيل دخول ولم تقدم لفريق مراجعة جوجل بيانات حساب تجريبي، سيتم رفض المراجعة لتعذر الوصول للمحتوى.",
                solution = "في كونسول المطور: اذهب إلى 'الوصول إلى التطبيقات' (App access) وأضف اسم مستخدم وكلمة مرور تجريبية مع إرشادات واضحة."
            ),
            PitfallItem(
                title = "ارتفاع معدلات الانهيار (Crashes & ANRs)",
                severity = "عالي 🛑",
                reason = "تجاوز معدل الانهيار للحد المقبول لجوجل (أعلى من 1.09%) يعيق الموافقة على طرح التطبيق في الإنتاج.",
                solution = "افحص تقرير Android Vitals في Play Console بانتظام وأصلح أي استثناءات أو تجمد في الواجهة قبل التقديم للإنتاج."
            ),
            PitfallItem(
                title = "عدم مطابقة سياسة الخصوصية وأذونات التطبيق",
                severity = "عالي 🛑",
                reason = "طلب أذونات حساسة دون تبرير استخدامها أو وضع رابط سياسة خصوصية غير صالح يؤدي لرفض فوري.",
                solution = "تأكد من وجود رابط سياسة خصوصية نشط وواضح في صفحة المتجر، واستخدم Android Photo Picker بدلاً من أذونات التخزين الشاملة."
            )
        )
    }

    val faqs = remember {
        listOf(
            FaqItem(
                question = "هل يمكنني إضافة إيميل المختبر مباشرة أم يجب استخدام Google Group؟",
                answer = "يمكنك استخدام الطريقتين بحرية كاملة! في Google Play Console يمكنك إضافة قائمة بريد إلكتروني (Email list) وإدخال إيميلات المختبرين من طرفك، أو استخدام مجموعة Google Groups (مجموعتك الخاصة أو المجموعة المشتركة). كلا الخيارين معتمدان رسمياً."
            ),
            FaqItem(
                question = "هل ينطبق شرط الـ 20 مختبراً و14 يوماً على جميع حسابات المطورين؟",
                answer = "ينطبق هذا الشرط على جميع حسابات المطورين الشخصية (Personal Developer Accounts) التي تم إنشاؤها بعد 13 نوفمبر 2023. أما حسابات المؤسسات (Organization Accounts) وحسابات الأفراد القديمة فلا تخضع لهذا الشرط الإلزامي."
            ),
            FaqItem(
                question = "ماذا يحدث إذا تخلف أحد المختبرين عن فتح التطبيق يوماً واحداً؟",
                answer = "لا يتم إلغاء الاختبار مباشرة طالما أن هناك 20 مختبراً على الأقل مسجلين ومستمرين في التفاعل. وجود 22-25 مختبراً يحميك تماماً من أي انقطاع عارض لأحد الأعضاء."
            ),
            FaqItem(
                question = "متى يظهر زر التقديم على الإنتاج (Apply for Production) في Google Play Console؟",
                answer = "يظهر الزر تلقائياً داخل صفحة لوحة التحكم (Dashboard) في كونسول المطور بمجرد أن يسجل نظام جوجل اكتمال 14 يوماً متواصلة مع 20 مختبراً نشطاً في المسار المغلق."
            ),
            FaqItem(
                question = "هل يُسمح برفع تحديثات جديدة أثناء سريان فترة الـ 14 يوماً؟",
                answer = "نعم بكل تأكيد! بل إن رفع إصدارات محسنة أثناء الاختبار يُعتبر مؤشراً إيجابياً قوياً لفريق جوجل على تفاعلك مع الملاحظات وجدية تحسين تجربة المستخدم."
            ),
            FaqItem(
                question = "كيف يضمن PlayTester Hub التزام المختبرين بالتبادل العادل؟",
                answer = "يعتمد التطبيق على نظام المكافآت والنقاط وسلاسل الأيام (Streaks). يحصل المطور على نقاط وشارات عند فحص تطبيقات زملائه، وتُخصم نقاط أو تتراجع أولوية الظهور في حال عدم الالتزام."
            )
        )
    }

    val completedCount = checkedSteps.values.count { it }
    val progressPercent = if (steps.isNotEmpty()) completedCount.toFloat() / steps.size else 0f

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("guide_screen_list"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // --- 1. Top Header Banner ---
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)),
                border = BorderStroke(1.dp, PlayGreen.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = PlayGreen.copy(alpha = 0.15f),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                        contentDescription = null,
                                        tint = PlayGreen,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "دليل اجتياز شروط Google Play",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "خارطة طريق كاملة للإنتاج والقبول الرسمي",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PlayGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = PlayGreen.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "إصدار 2.3",
                                style = MaterialTheme.typography.labelSmall,
                                color = PlayGreen,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "مرجع شامل ومفصل لمطوري تطبيقات أندرويد لاجتياز شرط الـ 14 يوماً و20 مختبراً وتعبئة استبيان الإنتاج بنماذج معتمدة تضمن القبول وتفادي الرفض.",
                        style = MaterialTheme.typography.bodySmall,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Reopen onboarding action
                    Button(
                        onClick = onOpenOnboarding,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PlayGreen),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("btn_reopen_onboarding")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.RocketLaunch, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("عرض الجولة الترحيبية المصورة 🚀", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }



        // --- 2. Flexible Tester Addition Options Card ---
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = CyanAccent.copy(alpha = 0.08f),
                border = BorderStroke(1.dp, CyanAccent.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Groups, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "خيارات إضافة المختبرين في Google Play Console",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "يمكنك في المسار المغلق اختيار الطريقة الأنسب لك:\n" +
                                "1️⃣ إضافة قائمة بريدية مخصصة (Email List) وإدخال إيميلات المختبرين مباشرة.\n" +
                                "2️⃣ أو استخدام مجموعة Google Groups (مجموعتك الخاصة، أو الاستعانة ببريد المجموعة المشتركة الجاهزة):",
                        style = MaterialTheme.typography.bodySmall,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = officialEmail,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = CyanAccent
                                    )
                                    Text(
                                        text = "بريد المجموعة المشتركة (اختياري كبديل جاهز)",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            OutlinedButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(officialEmail))
                                    Toast.makeText(context, "تم نسخ البريد بنجاح!", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("btn_copy_official_email")
                            ) {
                                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("نسخ", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // --- 3. Interactive Search Bar ---
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("ابحث في الخطوات، نماذج الإجابات، الأسئلة الشائعة...") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "مسح البحث")
                        }
                    }
                },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PlayGreen,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_guide_search")
            )
        }

        // --- 4. Category Filter Tabs ---
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(GuideTab.values()) { tab ->
                    val isSelected = selectedTab == tab
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedTab = tab },
                        label = { Text(tab.title, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium) },
                        leadingIcon = {
                            Icon(imageVector = tab.icon, contentDescription = null, modifier = Modifier.size(16.dp))
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PlayGreen.copy(alpha = 0.2f),
                            selectedLabelColor = PlayGreen,
                            selectedLeadingIconColor = PlayGreen
                        ),
                        modifier = Modifier.testTag("chip_tab_${tab.name}")
                    )
                }
            }
        }

        // --- 5. Checklist Progress Indicator (When Checklist or ALL is active) ---
        if (selectedTab == GuideTab.ALL || selectedTab == GuideTab.CHECKLIST) {
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "معدل إنجاز متطلبات النشر:",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "$completedCount من ${steps.size} (${(progressPercent * 100).toInt()}%)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (completedCount == steps.size) SuccessGreen else PlayGreen
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = { progressPercent },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape),
                            color = if (completedCount == steps.size) SuccessGreen else PlayGreen,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            strokeCap = StrokeCap.Round
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            if (completedCount < steps.size) {
                                TextButton(
                                    onClick = { steps.forEach { checkedSteps[it.id] = true } },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.DoneAll, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("تحديد الكل", fontSize = 12.sp)
                                }
                            } else {
                                TextButton(
                                    onClick = { checkedSteps.clear() },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.LockReset, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("إعادة ضبط", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- 6. SECTION: 7-Step Checklist Items ---
        if (selectedTab == GuideTab.ALL || selectedTab == GuideTab.CHECKLIST) {
            val filteredSteps = steps.filter {
                searchQuery.isEmpty() ||
                        it.title.contains(searchQuery, ignoreCase = true) ||
                        it.summary.contains(searchQuery, ignoreCase = true) ||
                        it.details.contains(searchQuery, ignoreCase = true) ||
                        it.tag.contains(searchQuery, ignoreCase = true)
            }

            if (filteredSteps.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "قائمة الخطوات الـ 7 لاجتياز الاختبار المغلق",
                        icon = Icons.Default.CheckCircle,
                        tint = PlayGreen
                    )
                }

                itemsIndexed(filteredSteps) { _, step ->
                    val isChecked = checkedSteps[step.id] == true
                    val isExpanded = expandedStepId == step.id

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("guide_step_${step.id}"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isChecked) SuccessGreen.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (isChecked) SuccessGreen.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateContentSize()
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { checkedSteps[step.id] = it },
                                    colors = CheckboxDefaults.colors(checkedColor = SuccessGreen),
                                    modifier = Modifier.testTag("step_checkbox_${step.id}")
                                )

                                Spacer(modifier = Modifier.width(6.dp))

                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            expandedStepId = if (isExpanded) null else step.id
                                        }
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = step.title,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isChecked) SuccessGreen else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = step.summary,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                IconButton(
                                    onClick = { expandedStepId = if (isExpanded) null else step.id },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                        contentDescription = if (isExpanded) "إخفاء التفاصيل" else "عرض التفاصيل",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            AnimatedVisibility(visible = isExpanded) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 10.dp, start = 38.dp, end = 6.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Text(
                                                text = step.details,
                                                style = MaterialTheme.typography.bodySmall,
                                                lineHeight = 20.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )

                                            if (step.importantTip != null) {
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Surface(
                                                    shape = RoundedCornerShape(8.dp),
                                                    color = AmberGold.copy(alpha = 0.12f),
                                                    border = BorderStroke(1.dp, AmberGold.copy(alpha = 0.3f)),
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Row(
                                                        modifier = Modifier.padding(8.dp),
                                                        verticalAlignment = Alignment.Top
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Lightbulb,
                                                            contentDescription = null,
                                                            tint = AmberGold,
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Text(
                                                            text = step.importantTip,
                                                            style = MaterialTheme.typography.labelSmall,
                                                            fontWeight = FontWeight.Medium,
                                                            color = AmberGold
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- 7. SECTION: Production Questions & Ready-to-Copy Answers ---
        if (selectedTab == GuideTab.ALL || selectedTab == GuideTab.PRODUCTION_QA) {
            val filteredQa = productionQuestions.filter {
                searchQuery.isEmpty() ||
                        it.title.contains(searchQuery, ignoreCase = true) ||
                        it.subtitle.contains(searchQuery, ignoreCase = true) ||
                        it.arabicTemplate.contains(searchQuery, ignoreCase = true) ||
                        it.englishTemplate.contains(searchQuery, ignoreCase = true)
            }

            if (filteredQa.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "نماذج إجابات استبيان الإنتاج في Google Play Console",
                        icon = Icons.Default.QuestionAnswer,
                        tint = IndigoAccent
                    )
                }

                itemsIndexed(filteredQa) { index, qa ->
                    val isExpanded = expandedQaIndex == index

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("production_qa_card_$index"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, IndigoAccent.copy(alpha = 0.3f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateContentSize()
                                .padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { expandedQaIndex = if (isExpanded) null else index },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Surface(
                                        shape = CircleShape,
                                        color = IndigoAccent.copy(alpha = 0.15f),
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "${index + 1}",
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = IndigoAccent
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = qa.title,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = qa.subtitle,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            AnimatedVisibility(visible = isExpanded) {
                                Column(modifier = Modifier.padding(top = 12.dp)) {
                                    // Arabic Template
                                    Text(
                                        text = "النموذج العربي المقترح:",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Text(
                                                text = qa.arabicTemplate,
                                                style = MaterialTheme.typography.bodySmall,
                                                lineHeight = 20.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.End
                                            ) {
                                                OutlinedButton(
                                                    onClick = {
                                                        clipboardManager.setText(AnnotatedString(qa.arabicTemplate))
                                                        Toast.makeText(context, "تم نسخ النموذج العربي!", Toast.LENGTH_SHORT).show()
                                                    },
                                                    shape = RoundedCornerShape(8.dp),
                                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                                    modifier = Modifier.testTag("btn_copy_qa_arabic_$index")
                                                ) {
                                                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("نسخ النص العربي", fontSize = 11.sp)
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // English Template
                                    Text(
                                        text = "النموذج الإنجليزي المقترح (English):",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = IndigoAccent
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Text(
                                                text = qa.englishTemplate,
                                                style = MaterialTheme.typography.bodySmall,
                                                lineHeight = 18.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.End
                                            ) {
                                                OutlinedButton(
                                                    onClick = {
                                                        clipboardManager.setText(AnnotatedString(qa.englishTemplate))
                                                        Toast.makeText(context, "تم نسخ النموذج الإنجليزي!", Toast.LENGTH_SHORT).show()
                                                    },
                                                    shape = RoundedCornerShape(8.dp),
                                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                                    modifier = Modifier.testTag("btn_copy_qa_english_$index")
                                                ) {
                                                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("نسخ النص الإنجليزي", fontSize = 11.sp)
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Key Tips
                                    qa.keyTips.forEach { tip ->
                                        Row(
                                            modifier = Modifier.padding(vertical = 2.dp),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Lightbulb,
                                                contentDescription = null,
                                                tint = AmberGold,
                                                modifier = Modifier
                                                    .size(14.dp)
                                                    .padding(top = 2.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = tip,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- 8. SECTION: Pitfalls & Avoid Rejection ---
        if (selectedTab == GuideTab.ALL || selectedTab == GuideTab.PITFALLS) {
            val filteredPitfalls = pitfalls.filter {
                searchQuery.isEmpty() ||
                        it.title.contains(searchQuery, ignoreCase = true) ||
                        it.reason.contains(searchQuery, ignoreCase = true) ||
                        it.solution.contains(searchQuery, ignoreCase = true)
            }

            if (filteredPitfalls.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "أشهر أسباب رفض طلب الإنتاج وكيف تتفاداها",
                        icon = Icons.Default.WarningAmber,
                        tint = AmberGold
                    )
                }

                itemsIndexed(filteredPitfalls) { index, pitfall ->
                    val isExpanded = expandedPitfallIndex == index

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("pitfall_card_$index"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, AmberGold.copy(alpha = 0.35f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateContentSize()
                                .padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { expandedPitfallIndex = if (isExpanded) null else index },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Icon(
                                        imageVector = Icons.Default.WarningAmber,
                                        contentDescription = null,
                                        tint = AmberGold,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = pitfall.title,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "مستوى الخطورة: ${pitfall.severity}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = AmberGold,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            AnimatedVisibility(visible = isExpanded) {
                                Column(modifier = Modifier.padding(top = 10.dp)) {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Text(
                                                text = "سبب المشكلة:",
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = DangerRed
                                            )
                                            Text(
                                                text = pitfall.reason,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )

                                            Spacer(modifier = Modifier.height(8.dp))

                                            Text(
                                                text = "طريقة الحل والتفادي:",
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = SuccessGreen
                                            )
                                            Text(
                                                text = pitfall.solution,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- 9. SECTION: Frequently Asked Questions (FAQ) ---
        if (selectedTab == GuideTab.ALL || selectedTab == GuideTab.FAQ) {
            val filteredFaqs = faqs.filter {
                searchQuery.isEmpty() ||
                        it.question.contains(searchQuery, ignoreCase = true) ||
                        it.answer.contains(searchQuery, ignoreCase = true)
            }

            if (filteredFaqs.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "الأسئلة الأكثر شيوعاً للمطورين",
                        icon = Icons.AutoMirrored.Filled.HelpOutline,
                        tint = CyanAccent
                    )
                }

                itemsIndexed(filteredFaqs) { index, faq ->
                    val isExpanded = expandedFaqIndex == index

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("faq_card_$index"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, CyanAccent.copy(alpha = 0.25f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateContentSize()
                                .padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { expandedFaqIndex = if (isExpanded) null else index },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Icon(
                                        imageVector = Icons.Default.Forum,
                                        contentDescription = null,
                                        tint = CyanAccent,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = faq.question,
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
                                Column(modifier = Modifier.padding(top = 10.dp)) {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = faq.answer,
                                            style = MaterialTheme.typography.bodySmall,
                                            lineHeight = 20.sp,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.padding(12.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- 10. SECTION: Official Resources and Quick Tools ---
        if (selectedTab == GuideTab.ALL || selectedTab == GuideTab.RESOURCES) {
            item {
                SectionHeader(
                    title = "روابط وأدوات رسمية مفيدة",
                    icon = Icons.Default.Link,
                    tint = PlayGreen
                )
            }

            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ResourceLinkRow(
                            title = "Google Play Console",
                            subtitle = "لوحة تحكم كونسول المطور الرسمية",
                            icon = Icons.Default.RocketLaunch,
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/console"))
                                context.startActivity(intent)
                            }
                        )

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                        ResourceLinkRow(
                            title = "دليل Google الرسمي للاختبار المغلق",
                            subtitle = "شروط حسابات الأفراد و20 مختبراً",
                            icon = Icons.Default.Security,
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://support.google.com/googleplay/android-developer/answer/9845334"))
                                context.startActivity(intent)
                            }
                        )

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                        ResourceLinkRow(
                            title = "سياسات مطوري Google Play الرسمية",
                            subtitle = "متطلبات الخصوصية والأمان وحماية البيانات",
                            icon = Icons.Default.Info,
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/about/developer-content-policy/"))
                                context.startActivity(intent)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(top = 8.dp, bottom = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun ResourceLinkRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = PlayGreen.copy(alpha = 0.12f),
                modifier = Modifier.size(32.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = null, tint = PlayGreen, modifier = Modifier.size(18.dp))
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
            contentDescription = "فتح الرابط",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
        )
    }
}
