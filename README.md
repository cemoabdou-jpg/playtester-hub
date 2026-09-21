# PlayTester Hub 🚀
**منصة تبادل اختبار التطبيقات المغلق لمطوري أندرويد (Google Play 20 Testers / 14 Days Closed Testing)**

PlayTester Hub هو تطبيق أندرويد مفتوح المصدر تم تصميمه خصيصاً لمساعدة مطوري التطبيقات على تلبية شرط Google Play الإلزامي للحسابات الشخصية: **الحصول على 20 مختبراً نشطاً لمدة 14 يوماً متواصلة**.

---

## 🌟 المميزات الرئيسية (Key Features)

1. **نظام تبادل عادل (Peer-to-Peer Community):**
   - يمكن لكل مطور نشر تطبيقه ليختبره الآخرون، ومقابل ذلك يقوم باختبار تطبيقات زملائه المطورين.
   - كسب نقاط ومستوى ثقة (Trust Score) لكل اختبار يومي مكتمل.

2. **تحقق صارم من التثبيت الفعلي (Real Package Verification):**
   - فحص مباشر لحزمة التطبيق على الجهاز (`PackageManager`).
   - منع بدء العداد أو تسجيل الحضور اليومي دون تثبيت التطبيق الفعلي من Google Play.

3. **مؤقت الـ 30 ثانية الإجباري (30-Second Engagement Timer):**
   - تشغيل التطبيق المستهدف مباشرة وضمان بقاء المستخدم فيه لمدة لا تقل عن 30 ثانية لضمان المصداقية أمام خوارزميات فحص متجر Play.

4. **لوحة تحكم ذكية ومبسطة (Smart & Simplified Dashboard):**
   - متابعة العداد (0/14 يوماً) وتعداد المختبرين المنضمين (0/20).
   - واجهة تشغيل سريعة بضغطة زر.

5. **دليل Google Play Console الشامل (Play Guide):**
   - إرشادات تفصيلية خطوة بخطوة حول كيفية إنشاء المسار المغلق، إضافة قوائم الإيميلات أو مجموعات Google Groups، وطريقة الإجابة على أسئلة التقدم للإنتاج.

---

## 🛠 التقنيات المستخدمة (Tech Stack)

- **اللغة:** Kotlin
- **واجهة المستخدم:** Jetpack Compose (Material Design 3)
- **قاعدة البيانات المحلية:** Room Database + Kotlin Coroutines & StateFlow
- **معمارية المشروع:** MVVM (Model-View-ViewModel) + Clean Architecture
- **إصدار أندرويد:** `minSdk: 24` (Android 7.0) / `targetSdk: 36` (Android 15+)

---

## 📱 المتطلبات والتشغيل المحلي (Running Locally)

### المتطلبات:
- Android Studio Ladybug (2024.2.1) أو أحدث
- JDK 17 أو JDK 21
- جهاز أندرويد حقيقي أو محاكي (Android Emulator) يدعم Google Play Services

### خطوات التشغيل:
1. استنسخ المستودع (Clone repository):
   ```bash
   git clone https://github.com/YOUR_USERNAME/playtester-hub.git
   ```
2. افتح المشروع في Android Studio.
3. انتظر انتهاء مزامنة Gradle (`Sync Project with Gradle Files`).
4. اختر جهازك واضغط **Run (Shift + F10)**.

---

## 📦 تجهيز حزمة النشر لمتجر Google Play (.aab)

لتوليد حزمة تطبيق Android App Bundle موقعة للنشر:
1. من شريط قوائم Android Studio:
   `Build > Generate Signed Bundle / APK...`
2. اختر **Android App Bundle (.aab)** ثم اضغط Next.
3. اختر مفتاح التوقيع الخاص بك (Keystore) أو أنشئ مفتاحاً جديداً (Create new).
4. حدد مسار التصدير ونمط `release` ثم اضغط Create.
5. ستجد الملف الناتج بصيغة `.aab` جاهزاً للرفع المباشر على **Google Play Console**.

---

## 📄 التراخيص (License)
هذا المشروع مرخص تحت رخصة [MIT License](LICENSE).
