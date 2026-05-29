# PROJECT_OVERVIEW — نظرة شاملة على المشروع

> ملف معرفة للوكلاء. يعطي الصورة الكبيرة بسرعة. للتفاصيل، اتبع الروابط.
> **آخر تحديث:** 2026-05-29 (بعد تكامل تحليل الباكِند B3).

---

## 1. الجهة والسياق التجاري

- **العميل:** شركة العباسي لتوليد الكهرباء التجارية (مزوّد كهرباء تجاري).
- **المستخدمون:** عمّال الشركة الميدانيون (جباة/قرّاء عدّادات).
- **الغرض:** قراءة عدّادات الكهرباء في الموقع، إصدار سندات قبض، طباعة إيصالات
  حرارية للزبائن، وعرض تقارير. كل ذلك **يعمل offline** ثم يتزامن مع الخادم.

---

## 2. الأنظمة الثلاثة

| النظام | الوصف | الدور |
|---|---|---|
| **التطبيق الأصلي** (Android/Java) | `com.yd.electricecollector`، اسم "كهرباء تحصيل" | **مصدر الحقيقة الوحيد** — منه نستخرج كل المنطق |
| **app1 / AbbasiTahseel** (React Native) | محاولة المستخدم السابقة لإعادة البناء (~65–70%) | **الأساس الذي نرحّله ونكمله** |
| **app3** (هذا المستودع) | التطبيق الجديد النهائي | **المُخرَج — كل سطر يُكتب هنا** |

> يوجد أيضاً **تطبيق سطح مكتب** للنظام نفسه (لدى المستخدم) — مصدر تحليل
> إضافي يمكن طلبه عند الحاجة لفهم منطق التقارير/الحسابات.

---

## 3. المعمارية التقنية (app3 = ترحيل app1)

```
React Native 0.74.5 (bare) + TypeScript strict
├── الشبكة:     Axios + interceptors (Bearer JWT/error/reauth-on-401) + Zod + mappers
├── التخزين:    WatermelonDB (offline-first, 13 model) + MMKV + Keychain
├── الحالة:     Zustand (auth/license/sync/readings/printer)
├── المزامنة:   offline-first queue + worker + coordinator + backoff + pull/push
├── الطباعة:    ESC/POS + cp1256 + Arabic shaper → BIXOLON SPP-R310 (Bluetooth)
├── i18n:       i18next (491 مفتاح عربي، RTL)
├── التصميم:    design-system (tokens + theme + 12 primitives)
└── البناء:     Expo EAS + GitHub Actions
```

---

## 4. عقد الخادم (الجوهر)

```
الخادم:   .NET Framework 4.5.1 WCF (self-hosted بـ OracleServiceMobile.exe)
          منطق الأعمال في MProgService.dll → قاعدة Oracle (ODP.NET)
العقد:    IServiceElect (الحديث، 33 عملية — الكلاينت يستخدمه) + IService1 (قديم)
العنوان:  http://{IP}:3000/electric/  (v28)  ← المنفذ والمسار ثابتان، IP متغيّر
          (v26 كانت بلا /electric/ — فرق نسخة)
الحيّ:    100.87.131.115:3000  (عبر Tailscale VPN فقط)
المصادقة: JWT Bearer (jose-jwt v5، HS256 غالباً، بلا exp — TTL على الخادم)
          v28: Authorization: Bearer <accessToken> + إعادة مصادقة على 401
الدخول:   POST /Login  body {User,Password,appId} (+secureId query)
          ← الرد DTO=Users يحوي access_token + error_no؛ نجاح: error_no===0
appId:    مُعرّف المستأجر/الفرع (افتراضي "1")، camelCase، يُرسَل على كل طلب
secureId: decimal(أول 8 hex من ANDROID_ID)  ← بلا XOR
الأخطاء: fault contract ServiceFault {Code, Description}
```

الكيانات الأساسية: `ItemReading` (القراءات → `DATA_M`)، `ItemBonds`
(السندات `SNDK_A` + سندات الدفع `SNDS_A`)، `Accounts` (`data_acc`)،
`Users` (`USER_R` + 7 أعلام صلاحيات Tier-A).

**الصلاحيات طبقتان:** Tier-A (7 أعلام على `USER_R`: `NOA,ED,DE,S_K,S_S,REP,SYS`
— UI-gating) + Tier-B (`USER_MNATK` ACL لكل مكان: `RED`/`SDAD` — row-filtering على الخادم).

**multi-tenant:** `appId` → الخادم يختار Oracle connection string لكل فرع
(`Dictionary<int,string>`). **هذا جواب سؤال «الفروع».**

التفصيل الكامل: `analysis/01_legacy_apk_analysis/` (الكلاينت v28) +
`/home/user/_analysis_src/B3/analysis/` (الخادم/Oracle/JWT — انظر AGENTS.md §4).

---

## 5. أهم الأخطاء التي كانت في app1 (تُصلَح في app3)

| الفجوة | الخطأ | الإصلاح |
|---|---|---|
| G-1 | صلاحيات مبسّطة (booleans) بلا نظام الطبقتين (Tier-A 7 أعلام + Tier-B per-place ACL) | وحدة صلاحيات مركزية تغطّي الطبقتين (قالب B3 permissions_matrix) |
| G-2 | عدم التأكد من فكّ غلاف `{Operation}Result` بكل mapper | أداة `unwrapResult` عامة |
| G-3 | عدم فحص `error_no===0` في الدخول | إضافة الفحص |
| G-4 | جدول `bonds` 8 حقول بدل 24 | توسيع schema |
| G-6 | `DeleteReading` يرسل `noadad` بدل `id={num}` | تصحيح المعرّف |
| G-7 | `TODO_SERVER` في bondPushHandler | حسمه (العمليات موجودة في الأصلي) |
| G-8 | شاشات التقارير الثمانية MOCK | ربطها بالـ API |

التفصيل: `analysis/03_gap_analysis/GAP_MATRIX.md`.

---

## 6. خطة العمل (المراحل)

| Milestone | المحتوى | الحالة |
|---|---|---|
| **M0** | ترحيل app1 → app3 + CI/CD + اخضرار البناء | ⏳ التالي |
| **M1** | تصحيح العقد (G-1/2/3: دخول + صلاحيات + أغلفة) | ⬜ |
| **M2** | ربط البيانات (CRUD + سندات 24 حقل + تقارير) | ⬜ |
| **M3** | تحسينات + اختبارات + اختبار طابعة BIXOLON ميداني | ⬜ |

التفصيل: `analysis/04_architecture_plan/README.md`.

---

## 7. حالة المنصّات الخارجية

| المنصّة | الحالة | التفاصيل |
|---|---|---|
| GitHub | ✅ جاهز | `moain2026/app3`، فرعا `main` + `genspark_ai_developer` |
| Expo / EAS | ✅ حساب مُنشأ | Project ID: `14fc4aef-f38c-474d-a63a-1a12c7c93730` |
| GitHub Secret | ✅ مُضاف | `EXPO_TOKEN` |
| Tailscale (للخادم) | بيد المستخدم | الخادم لا يُوصَل إلا عبره |

---

## 8. الأسئلة المفتوحة (تحتاج قرار المستخدم)

| السؤال | الأثر | الحالة |
|---|---|---|
| «الفروع»: معناها؟ | تصميم تسجيل الدخول + G-5 | ✅ مُجاب: `appId`=مُعرّف الفرع/المستأجر (B3) |
| كم فرعاً مستخدَم فعلاً؟ | هل نخزّن appId ثابتاً أم نختاره | ⏳ بانتظار تأكيد المستخدم |
| تطبيق سطح المكتب — هل نحلّله؟ | فهم إضافي للتقارير | 🎁 عرضه المستخدم |
