# KNOWN_DISCREPANCIES — التناقضات بين المصدر الأصلي ووثائق/كود app1

> هذا الملف يوثّق كل فرق اكتشفته بين الكود الأصلي المُفكَّك (مصدر الحقيقة)
> وبين ما هو موثّق أو مكتوب في app1. مُرتَّب حسب الخطورة.

## 🔴 خطير — يجب الإصلاح أو التحقق

### D-1: أنواع حقول `ItemReading` في وثيقة LEGACY_JAVA_MAP
- **الوثيقة (app1 `AGENT_CONTEXT/LEGACY_JAVA_MAP.md`):** وصفت
  `ks/kh/cas/asts` كلها `int`، و`nomstlm/notblh/nog` كـ `String`.
- **المصدر الأصلي (`ItemReading.java`):**
  `ks=double, kh=double, asts=double, cas=int`،
  `nomstlm=int, notblh=long, nog=int`.
- **كود app1 الفعلي (`schemas/reading.ts`):** ✅ **صحيح** — يستخدم
  `zNumberLoose` لـ ks/kh/asts و `zIntLoose` لـ int fields.
- **الحكم:** الكود سليم، الوثيقة النصية فقط غير دقيقة. **لا إصلاح كود
  مطلوب**، لكن يُنصح بتحديث الوثيقة لتجنّب تضليل وكلاء مستقبليين.

### D-2: codepage الطابعة — cp1251 vs cp1256 ⚠️
- **المصدر الأصلي (`strings.xml`):** `<string name="codepage">cp1251</string>`.
- **كود app1:** يستخدم **cp1256** (الصحيح للعربية).
- **الحكم:** ⚠️ **يحتاج تأكيد ميداني.** cp1251 سيريلي (روسي) — لا يدعم
  العربية. cp1256 عربي. الأرجح أن مورد `cp1251` خطأ قديم في التطبيق
  الأصلي (أو الطابعة تتجاهله). app1 على الأرجح **صحيح بـ cp1256**، لكن
  يجب اختبار طباعة فعلية للتأكيد. سؤال للمستخدم عند اختبار الطابعة.

### D-3: غلاف الاستجابة `{OperationName}Result`
- **المصدر الأصلي:** كل استجابة مُغلَّفة (`GetListReadingCounterResult`,
  `LoginResult`, `SaveReadingResult`, `DeleteBondResult.Result`...).
- **app1:** يجب التأكد أن كل mapper يفكّ الغلاف. **بند تحقق في مرحلة
  تقييم app1** (انظر `../03_gap_analysis/`).

## 🟡 متوسط — للتوثيق والانتباه

### D-4: خوارزمية secureId
- **الوثيقة:** "XOR + first 8 hex chars → decimal".
- **المصدر:** `getDeviceId()` = أول 8 hex من ANDROID_ID → decimal. **لا XOR.**
- الـ XOR في نظام ترخيص منفصل (`isKeyValid`). انظر `SECURITY_DEFENCE.md`.
- **الحكم:** يجب التأكد أن `licenseManager.ts` في app1 يطبّق
  الخوارزمية البسيطة الصحيحة (سيُفحص في تقييم app1).

### D-5: محرّك الشبكة الأصلي مزدوج
- التطبيق الأصلي يستخدم **loopj AsyncHttpClient** في الإنتاج (وليس
  Retrofit). app1 يستخدم **Axios** — وهو خيار حديث سليم. لا مشكلة، فقط
  يجب التأكد أن سلوك الـ headers/timeout/retry متوافق مع توقّعات الخادم.

### D-6: البورت والمسار ثابتان
- `getBaseUrl()` يضمّن `:3000/electric/` بشكل ثابت. فقط الـ IP متغيّر.
- app1 يطبّق هذا (`DEFAULT_BASE_URL`). ✅ متطابق.

## 🟢 منخفض — معلومة فقط

### D-7: مسار `/UserAuth` بـ `appid` lowercase
- موجود في الأصلي لكنه **غير مستخدم**. app1 لا يحتاجه. تجاهله.

### D-8: السيرفر الاحتياطي (HostingIP2)
- `127.0.0.1` إرث غير مفعّل. لا حاجة لترحيله.

### D-9: نظام تفعيل النسخة (License Key)
- موجود في الأصلي (`Defence.GenerateKey/isKeyValid`). اختياري للترحيل.
  القرار للمستخدم.

## ملاحظة منهجية
كل بند هنا قابل للتتبّع إلى ملف Java محدّد في `decompiled_sources/`.
عند أي شك، الكود المُفكَّك هو الحَكَم، ثم صفحة WCF Help الحيّة، ثم سؤال
المستخدم — بهذا الترتيب.
