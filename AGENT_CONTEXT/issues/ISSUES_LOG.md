# ISSUES_LOG — سجل المشاكل والحلول

> كل مشكلة واجهناها وحلّها. الصيغة: المشكلة → السبب الجذري → الحل → الوقاية.
> هذا السجل يمنع الوكلاء من تكرار نفس الأخطاء أو إعادة اكتشاف نفس المطبّات.

---

## ISS-1 — الخادم لا يُوصَل إلا عبر Tailscale VPN
- **المشكلة:** نقاط النهاية والمفاتيح مخفية لأن الخادم خلف VPN خاص.
- **السبب الجذري:** البنية التحتية للعميل تتطلّب Tailscale؛ لا يمكن الوصول
  المباشر من بيئة التطوير.
- **الحل:** استخراج كل العقد (العنوان/المسارات/الحقول) **حرفياً من الـ APK**
  بالتفكيك (JADX)، بدل الاعتماد على فحص شبكة حيّ.
- **الوقاية:** اعتمد دائماً على المصدر المُفكَّك. للاختبار الحيّ، المستخدم وحده
  يملك وصول Tailscale.

---

## ISS-2 — تضارب في أنواع حقول الكيانات (وثائق app1 مقابل الكود)
- **المشكلة:** وثائق app1 النثرية وصفت `ks/kh/asts` كـ int، و`nomstlm/nog` كـ String.
- **السبب الجذري:** الوثائق كُتبت يدوياً ولم تُطابق الكود.
- **الحل:** اعتُمدت أنواع Java الفعلية: `ks/kh/asts=double, cas=int,
  nomstlm/nog=int, notblh=long`. كود app1 (`schemas/reading.ts`) كان **صحيحاً**؛
  الوثيقة فقط كانت خاطئة.
- **الوقاية:** الكود > الوثائق النثرية. تحقّق من `entities/*.java` مباشرة.
- **مرجع:** `KNOWN_DISCREPANCIES.md` D-1.

---

## ISS-3 — خوارزمية secureId موثّقة خطأً (XOR مزعوم)
- **المشكلة:** وثائق app1 قالت secureId = "XOR + first 8 hex → decimal".
- **السبب الجذري:** خلط بين `getDeviceId()` (بسيط) ونظام الترخيص المنفصل
  (`GenerateKey/isKeyValid` الذي يستخدم XOR).
- **الحل:** التحقق من `Defence.java`: `getDeviceId() = decimal(أول 8 hex من
  ANDROID_ID)` **بلا XOR**. الـ XOR لنظام الترخيص فقط.
- **الوقاية:** لا تخلط بين الأنظمة الفرعية. اقرأ الدالة المعنية بالضبط.
- **مرجع:** `SECURITY_DEFENCE.md`, `KNOWN_DISCREPANCIES.md` D-4.

---

## ISS-4 — codepage الطابعة (cp1251 في strings مقابل cp1256 الفعلي)
- **المشكلة:** مورد `<string name="codepage">cp1251</string>` يوحي بترميز سيريلي.
- **السبب الجذري:** المورد قديم/مضلِّل وغير مستخدم لترميز النص العربي.
- **الحل:** الكود الفعلي يستدعي `Charset.forName("Windows-1256")` صراحةً
  (`PrinterActivity.java:322`). الترميز الصحيح **cp1256**. app1 كان صحيحاً.
- **الوقاية:** لا تثق بموارد strings.xml للسلوك — تحقّق من الكود الذي يستخدمها.
- **مرجع:** `KNOWN_DISCREPANCIES.md` D-2.

---

## ISS-5 — `SaveBond/UpdateBond` غائبة عن Retrofit ApiService لكنها موجودة فعلاً
- **المشكلة:** بحث في `ApiService.java` (Retrofit) لم يُظهر بعض عمليات الكتابة،
  ما أوحى أنها غير موجودة (سبب `TODO_SERVER` في app1).
- **السبب الجذري:** محرّك الشبكة مزدوج — Retrofit **مُعرَّف لكن غير مستخدم**،
  والإنتاج يستخدم **loopj** في `model/*Repository.java`.
- **الحل:** العمليات موجودة في `BondsRepository.java` /
  `BondsPaymentRepository.java` عبر loopj. حُسم الـ TODO.
- **الوقاية:** عند البحث عن عملية، فتّش في **كلا** المحرّكين (loopj + Retrofit).
- **مرجع:** `GAP_MATRIX.md` G-7, `KNOWN_DISCREPANCIES.md` D-5.
- ⚠️ **تصحيح لاحق (انظر ISS-10):** «الإنتاج = loopj» غير دقيق لـ v28 — v28 في
  طور انتقال؛ مسارات auth/Lookups تستخدم Retrofit فعلاً. ابنِ app3 على نمط
  Retrofit/JWT الأحدث.

---

## ISS-6 — المستودع فارغ تماماً (لا فرع main على remote) عند أول دفع
- **المشكلة:** عند محاولة فتح PR، لم يكن هناك فرع `main` على remote كقاعدة.
- **السبب الجذري:** `app3` أُنشئ فارغاً بلا أي commit.
- **الحل:** أُنشئ فرع `main` بـ commit تهيئة (seed README)، ثم أُعيد بناء
  `genspark_ai_developer` فوقه ليتشاركا التاريخ، ثم فُتح/دُمج PR.
- **الوقاية:** في مستودع فارغ، أنشئ `main` أساسياً أولاً قبل فروع العمل.

---

## ISS-7 — تناقض baseUrl: `/electric/` مقابل بدونه (تحليلي مقابل B3)
- **المشكلة:** تحليلي وجد `http://{IP}:3000/electric/`؛ B3 وجد `http://192.168.0.100:3000/` (بلا `/electric/`).
- **السبب الجذري:** **فرق نسخة حقيقي.** أنا حللت `ElectricCollector28.apk` (v28)؛
  B3 حلل `ElectricCollector26.apk` (v26). v28 أضافت المسار `/electric/`.
- **الحل (مُتحقَّق من المصدر):** `AppConfig.java:44` في v28 صراحةً:
  `return "http://" + this.baseUrl + ":3000/electric/";`. والـ IP من
  `prefs.getString("HostingIP", "192.168.0.100")` (`:87`). **app3 يستخدم
  مسار v28 = `/electric/`** لأنها الأحدث.
- **الوقاية:** عند مقارنة مصدرين، تأكّد من **رقم النسخة** أولاً. النسخ تتطوّر.
- **مرجع:** `KNOWN_DISCREPANCIES.md` D-6، B3 `10_APK_V26_ANALYSIS.md §3`، AGENTS.md F-1.

---

## ISS-8 — تناقض المصادقة: لم أجد JWT أولاً، B3 أكّد Bearer JWT
- **المشكلة:** تحليلي المبكّر ركّز على طبقة loopj العالية ووجد `error_no===0`
  للدخول، ولم يُبرز خط JWT بوضوح. B3 أكّد خط أنابيب Bearer JWT كامل على الخادم.
- **السبب الجذري:** (1) v28 انتقلت إلى Retrofit وحقن التوكن يحدث في
  `RetrofitBuilder.createServiceWithAuth` (لم أصل إليه أول مرة). (2) B3 فكّك
  الخادم نفسه فكان أكثر سلطة على آلية المصادقة.
- **الحل (مُتحقَّق):** v28 `RetrofitBuilder.java` يحقن
  `addHeader("Authorization", "Bearer " + tokenManager.getToken().getAccessToken())`
  + `CustomAuthenticator` لإعادة المصادقة على 401. JWT **مؤكد قطعاً في v28**.
  B3 يفصّل: jose-jwt v5، HS256 (ثقة 60%)، بلا `exp` (TTL على الخادم) ⇒
  نتعامل مع التوكن كـ **opaque** + إعادة مصادقة على 401.
- **الوقاية:** للمصادقة/الباكِند، تحليل الخادم (B3) موثوق أكثر؛ للكلاينت، v28
  يحسم ما يُرسَل فعلاً. الاثنان متطابقان هنا (Bearer JWT).
- **مرجع:** `AUTH_FLOW.md`, AGENTS.md F-4/F-14، B3 `02_JWT_AUTHENTICATION.md`، `for_main_repo/jwt_interceptor.ts`.

---

## ISS-9 — نظام الصلاحيات أعمق مما وُثّق (6 أعلام → 7 أعلام + Tier-B)
- **المشكلة:** وثّقتُ 6 أعلام (`ED,DE,S_K,S_S,REP,SYS`) ومنطق `>0` و`SYS` يفتح كل شيء.
  B3 وثّق **7** أعلام (يضيف `NOA`) + **طبقة ثانية** (Tier-B per-place ACL).
- **السبب الجذري:** الكلاينت يخزّن Tier-A فقط (في `HakAccess`)، أما Tier-B
  (تصفية الصفوف لكل مكان) فمفروضة على الخادم عبر `USER_MNATK` ولا تظهر في
  منطق الكلاينت — لذا فاتتني من تحليل APK فقط.
- **الحل (مُتحقَّق من B3 + يحتاج تأكيد v28):**
  - **Tier-A** = 7 أعلام `int` على `USER_R` تأتي في DTO `Users`:
    `NOA, ED, DE, S_K, S_S, REP, SYS` (UI-gating). ملاحظة: `NOA` مزدوج
    (رقم صندوق/till + علم) — يجب فصلهما في app3.
  - **Tier-B** = جدول `USER_MNATK(NOU, no_mstlm, RED, SDAD, NAMEM)`:
    `RED`=قراءة، `SDAD`=كتابة لكل مكان (`no_mstlm`). يُفرَض عبر SQL subqueries
    `nvl(red,0)>0` / `nvl(sdad,0)>0` على الخادم (row-filtering).
  - منطق العلم: B3 يرجّح `==1` في C#، و`>0` لفلاتر Tier-B. **يجب التحقق من
    `entities/Users.java` + `HakAccessHelper.java` في v28 قبل كتابة منطق app3.**
- **الوقاية:** الصلاحيات على مستوى الصفوف قد تكون على الخادم فقط — راجع
  جداول الـ ACL في تحليل الباكِند، لا الكلاينت وحده.
- **مرجع:** AGENTS.md F-10، B3 `04_PERMISSIONS_SYSTEM.md` + `for_main_repo/permissions_matrix.md`.

---

## ISS-10 — تناقض محرّك الشبكة: loopj (v26/تحليلي) مقابل Retrofit (v28)
- **المشكلة:** ISS-5/F-13 السابقان قالا «الإنتاج = loopj، Retrofit مُعرَّف وغير
  مستخدم». لكن فحص v28 الدقيق أظهر أن `RetrofitBuilder.createServiceWithAuth`
  **مُستخدَم فعلاً** (تستدعيه شاشات Lookup* وغيرها).
- **السبب الجذري:** v28 في طور **انتقال** من loopj إلى Retrofit. بعض المسارات
  (سندات/قراءات قديمة) لا تزال loopj؛ مسارات أحدث (Lookups + auth) تستخدم
  Retrofit/OkHttp/Moshi. B3 حلل v26 (loopj بحت) فلم يرَ هذا.
- **الحل:** **لأغراض app3 الهجرة:** المرجع المعماري = **Retrofit/axios + Bearer
  JWT interceptor** (نمط v28 الأحدث + قالب B3 `jwt_interceptor.ts`). لا يهم أي
  محرّك في النسخة القديمة — المهم العقد (URL + params + JWT) وهو موحّد.
- **الوقاية:** «الإنتاج = X» قد يكون مؤقتاً أثناء انتقال؛ ابنِ على النمط الأحدث،
  وحقّق من توقيعات `network/ApiService.java` (Retrofit) لكل عملية.
- **مرجع:** AGENTS.md F-13، تحديث على ISS-5، `network/RetrofitBuilder.java`.

---

## (قالب لإدخال جديد — انسخه)
<!--
## ISS-N — العنوان
- **المشكلة:**
- **السبب الجذري:**
- **الحل:**
- **الوقاية:**
- **مرجع:**
-->
