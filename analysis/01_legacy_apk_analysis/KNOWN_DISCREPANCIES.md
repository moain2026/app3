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

### D-2: codepage الطابعة — cp1251 vs cp1256 ✅ محسوم
- **المصدر الأصلي (`strings.xml`):** `<string name="codepage">cp1251</string>`
  — **مورد مضلِّل/غير مستخدم للنص العربي.**
- **الكود الفعلي (`printer/bluetooth/PrinterActivity.java:322`):**
  ```java
  byte[] bytes = " السلام عليكم".getBytes(Charset.forName("Windows-1256"));
  ```
- **الحكم:** ✅ **النص العربي يُرمَّز فعلياً بـ Windows-1256 (= cp1256).**
  مورد `cp1251` في strings.xml لا يُستخدم لترميز العربي (الكود يستدعي
  `Charset.forName("Windows-1256")` صراحةً). **app1 كان صحيحاً بـ cp1256.**
  لا حاجة لتغيير، لكن نُبقي الترميز قابلاً للضبط احتياطاً.

### D-2b: موديل الطابعة الفعلي — BIXOLON SPP-R310 🆕
- **المصدر الأصلي (`printer/driver/PrinterDriverFactory.java`):** مصنع
  drivers يختار حسب الموديل المخزّن (`TAPreferences.getSetectedPrinterModel`):
  - `"DPP-250"` → `DatecsDpp250Driver` (type 1)
  - `"UNS-SP1B"` → `DatecsDpp250Driver` (type 2)
  - `"BlueTooth Printer"` → `JP5802Driver` (type 3)
  - default → `JP5802Driver`
- **الواقع الميداني (أكّده المستخدم):** الطابعة المستخدمة هي
  **BIXOLON SPP-R310** (طابعة حرارية محمولة، ESC/POS، Bluetooth).
- **الحكم:** SPP-R310 طابعة ESC/POS قياسية تدعم cp1256 عربي. التطبيق الأصلي
  لا يحوي driver باسمها صراحةً، فالأرجح كانت تُستخدم عبر مسار
  `"BlueTooth Printer"` العام (JP5802/ESC-POS عبر RFCOMM). **التصميم في app3:**
  مجرّد طابعة ESC/POS عبر Bluetooth SPP + cp1256 + Arabic shaper — متوافق
  تماماً مع SPP-R310. يُختبر ميدانياً عند أول طباعة.

### D-3: غلاف الاستجابة `{OperationName}Result` (⚠️ مُراجَع بعد B3)
- **المصدر الأصلي (loopj/v26):** ظهرت أغلفة `{Operation}Result`
  (`GetListReadingCounterResult`, `LoginResult`, `DeleteBondResult.Result`...).
- **مراجعة B3 (الخادم):** على مستوى الخادم، الأنواع الراجعة الفعلية هي
  `Users` / `ResultPost {Note,Status,ErrorMessage,ErrorCode}` / `List<X>`
  مع fault contract `ServiceFault {Code, Description}`. أي «الغلاف»
  `{Operation}Result` غالباً طبقة WCF/الـ proxy في الكلاينت القديم، لا شكل
  JSON ثابت من الخادم.
- **v28 (Retrofit/Moshi):** يفكّ JSON مباشرةً إلى DTOs عبر
  `MoshiConverterFactory`. **الحَكَم النهائي = توقيعات `network/ApiService.java`
  في v28** (نوع الإرجاع لكل دالة) + DTOs الخادم في B3 `dtos.ts`.
- **app1/app3:** لا تفترض غلاف `Result` لكل عملية تلقائياً؛ اشتقّ شكل كل رد
  من توقيع Retrofit المطابق + DTO الخادم. أداة `unwrapResult` تبقى مفيدة
  للمسارات القديمة فقط. **بند تحقق في M1.**

## 🟡 متوسط — للتوثيق والانتباه

### D-4: خوارزمية secureId
- **الوثيقة:** "XOR + first 8 hex chars → decimal".
- **المصدر:** `getDeviceId()` = أول 8 hex من ANDROID_ID → decimal. **لا XOR.**
- الـ XOR في نظام ترخيص منفصل (`isKeyValid`). انظر `SECURITY_DEFENCE.md`.
- **الحكم:** يجب التأكد أن `licenseManager.ts` في app1 يطبّق
  الخوارزمية البسيطة الصحيحة (سيُفحص في تقييم app1).

### D-5: محرّك الشبكة الأصلي مزدوج (⚠️ مُصحَّح بعد B3)
- **مُصحَّح:** v28 تستخدم **Retrofit/OkHttp/Moshi فعلاً** في مسارات auth و
  Lookups عبر `RetrofitBuilder.createServiceWithAuth` (مع حقن
  `Authorization: Bearer <token>` + `CustomAuthenticator` على 401). بعض
  المسارات القديمة لا تزال loopj — v28 في طور انتقال. (B3 حلّل v26 = loopj بحت.)
- app1/app3 يستخدمان **Axios** — مكافئ حديث لـ Retrofit. سليم.
- **الأثر:** ابنِ طبقة الشبكة على نمط Retrofit/JWT الأحدث + قالب B3
  `jwt_interceptor.ts`. انظر `AGENT_CONTEXT/issues/ISSUES_LOG.md` ISS-10.

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

## 🆕 تناقضات تكامل تحليل الباكِند (ريبو B3 — v26 مقابل v28)

> B3 فكّك **الخادم نفسه** من نسخة **v26**؛ نحن نبني على كلاينت **v28**.
> التناقضات أدناه ليست أخطاء بل **فرق نسخة**، وقد صُولِحت. التفصيل في
> `AGENT_CONTEXT/issues/ISSUES_LOG.md` ISS-7..ISS-10 و`decisions/DECISION_LOG.md` DEC-6.

### D-10: baseUrl — `/electric/` (v28) مقابل بدونه (v26)
- **B3 (v26):** `http://192.168.0.100:3000/`.
- **v28 (المصدر لدينا):** `AppConfig.java:44` → `http://{IP}:3000/electric/`.
- **الحكم:** app3 يستخدم **`/electric/`** (نسختنا الأحدث). فرق نسخة مؤكد.

### D-11: المصادقة JWT Bearer (مؤكدة قطعاً في v28)
- **B3:** خط أنابيب Bearer JWT كامل على الخادم (jose-jwt v5، HS256 ثقة 60%،
  **بلا `exp`** — TTL على الخادم).
- **v28:** `RetrofitBuilder.createServiceWithAuth` يحقن
  `Authorization: Bearer <accessToken>`؛ `TokenManager` يخزّن التوكن؛
  `CustomAuthenticator` يعيد المصادقة على 401. **متطابق مع B3.**
- **الحكم:** عامل التوكن كـ **opaque** (لا تفكّه بالكلاينت) + أعِد المصادقة
  على 401. استخدم قالب B3 `for_main_repo/jwt_interceptor.ts`.

### D-12: نظام الصلاحيات طبقتان (Tier-A 7 أعلام + Tier-B ACL)
- **B3:** **Tier-A** = 7 أعلام `int` على `USER_R` في DTO `Users`
  (`NOA, ED, DE, S_K, S_S, REP, SYS` — UI-gating)؛ **Tier-B** = جدول
  `USER_MNATK(NOU, no_mstlm, RED, SDAD, NAMEM)` لتصفية الصفوف لكل مكان على الخادم.
- **تحليلي السابق:** Tier-A فقط (6 أعلام)؛ فاتني Tier-B لأنه مفروض على الخادم.
- **الحكم:** app3 يطبّق **بوّابات Tier-A بالواجهة** + يثق بتصفية Tier-B من الخادم.
  `NOA` مزدوج (رقم صندوق + علم) → افصلهما. **تحقّق من منطق العلم (`>0` vs `==1`)
  من `entities/Users.java` + `HakAccessHelper.java` في v28 قبل الكتابة.**

### D-13: قاعدة Oracle + نتائج أمنية P0 (من B3)
- **B3 كشف:** الخادم يستخدم **Oracle** (ODP.NET)، 12 جدول، ~75 قالب SQL.
  multi-tenant: `appId` → connection string لكل فرع.
- **نتائج أمنية P0 (عيوب خادم — لا تُحَلّ من الكلاينت):** (1) حقن SQL على
  `/Login` و`/ChangePassword`. (2) اعتماد Oracle مضمّن بالنص في الـ binaries.
  (3) HTTP صريح (لا TLS).
- **الحكم:** تُوثَّق وتُبلَّغ للمستخدم؛ app3 يضيف TLS/pinning في مساره ويتجنّب
  IMEI. الإصلاح الجذري لحقن SQL = على الخادم (.NET) وليس الكلاينت.

## ملاحظة منهجية
كل بند هنا قابل للتتبّع إلى ملف Java محدّد في `decompiled_sources/` (للكلاينت)
أو إلى ملف B3 محدّد (للخادم). عند أي شك، **الترتيب:**
1. سلوك الكلاينت (URL/params/headers) → كود **v28** المُفكَّك يحسم.
2. سلوك الخادم (JWT/DTOs/SQL/الصلاحيات) → تحليل **B3** يحسم.
3. صفحة WCF Help الحيّة (عبر Tailscale، بيد المستخدم).
4. سؤال المستخدم.
— بهذا الترتيب.
