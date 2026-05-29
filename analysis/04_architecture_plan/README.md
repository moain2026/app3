# 04 — الخطة المعمارية وخطة الترحيل (Architecture & Migration Plan)

> **الغرض:** تحديد القرار المعماري لـ `app3`، واستراتيجية الترحيل من `app1`،
> وخطة العمل المرحلية لإكمال الـ~60% المتبقّية لتطبيق التحصيل المالي.
> **تاريخ:** 2026-05-29.
> **المراجع:** التقارير 01 (تحليل APK) + 02 (تقييم app1) + 03 (مصفوفة الفجوات).

---

## 1. القرار المعماري لـ `app3`

### 1.1 القرار: **ترحيل (Migrate) وليس إعادة بناء (Rebuild)**

> 🟢 **نُرحّل قاعدة `app1` كأساس لـ `app3`، ثم نُصلح الفجوات.**

**المبرّر (من التقرير 02):**
- بنية `app1` ممتازة وتلتزم Clean Code/SOLID (~65–70% بنيوياً).
- الطبقات السبع (network/storage/db/sync/printer/design-system/navigation) جاهزة.
- إعادة البناء من الصفر = هدر 65% من عمل صحيح + مخاطرة فقدان منطق مُتحقَّق منه.
- المتبقّي هو **منطق عمل + ربط شاشات**، وهو بالضبط ما تعالجه مصفوفة الفجوات.

### 1.2 الحزمة التقنية المعتمدة (مثبَّتة)
| الطبقة | التقنية | الحالة |
|---|---|---|
| الإطار | React Native 0.74.5 (bare) + TS strict | يُرحَّل كما هو |
| الحالة | Zustand | يُرحَّل |
| التخزين المحلي | WatermelonDB (offline-first) | يُرحَّل + توسيع schema |
| الشبكة | Axios + interceptors | يُرحَّل + envelope/appId |
| التحقق | Zod (`*Loose` coercers) | يُرحَّل (صحيح) |
| الترجمة | i18next (491 مفتاح، RTL) | يُرحَّل |
| الطباعة | ESC/POS + Bluetooth SPP، cp1256 | يُرحَّل (الطابعة الميدانية: **BIXOLON SPP-R310**) |
| البناء/النشر | **Expo EAS + GitHub Actions** | يُنشَأ جديداً |

> **ملاحظة EAS:** المشروع bare RN؛ سنستخدم EAS Build مع `expo-dev-client`
> (يدعم bare workflow) لأنه الأسرع لإنتاج APK/AAB دون إدارة توقيع يدوية.

---

## 2. عقد الخادم المُجمَّع (المرجع الموحَّد للتنفيذ)

```
BASE_URL = "http://" + IP + ":3000/electric/"     // المنفذ و /electric/ ثابتان
LIVE     = http://100.87.131.115:3000/electric/   // عبر Tailscale فقط
ENVELOPE = كل استجابة → { "<Operation>Result": {...} }
SUCCESS  = LoginResult.error_no === 0
APPID    = رقم الفرع (افتراضي "1")، camelCase: appId
SECUREID = decimal(first 8 hex of ANDROID_ID)     // بلا XOR
```

### نقاط النهاية الحرجة (مع معرّفاتها الصحيحة)
| العملية | الطريقة | المسار/المعاملات | المعرّف |
|---|---|---|---|
| Login | POST | `/Login` body `{username,password,appId,secureId}` | — |
| SaveReading | POST | `?appId={a}` body=ItemReading | — |
| UpdateReading | PUT | `?appId={a}&id={num}` | **num** (int) |
| DeleteReading | DELETE | `?appId={a}&id={num}` | **num** (int) |
| SaveBond | POST | `?appId={a}` body=ItemBonds | — |
| UpdateBond | PUT | `?appId={a}&id={nmstnd}` | **nmstnd** |
| DeleteBond | DELETE | `?appId={a}&id={nmstnd}` | **nmstnd** |
| SaveBondPayment | POST | `?appId={a}` body=ItemBonds | — |
| Update/DeleteBondPayment | PUT/DELETE | `?appId={a}&id={nmstnd}` | **nmstnd** |
| GetRep* (×8) | GET | `?appId={a}&...` للقراءة فقط | — |

---

## 3. خطة العمل المرحلية

### 🔴 المرحلة 0 — تجهيز `app3` (أساس)
1. ترحيل شجرة `app1/AbbasiTahseel` إلى جذر `app3` (مع تنظيف المراجع).
2. تثبيت التبعيات + التأكد من `tsc --noEmit` نظيف.
3. إعداد ESLint/Prettier + Husky pre-commit.
4. **CI/CD:** GitHub Actions (lint + typecheck + jest) + EAS build workflow.
5. هيكلة فروع: `main` (محمي) + `genspark_ai_developer` (التطوير).

### 🔴 المرحلة 1 — تصحيح العقد الأساسي (الفجوات الحرجة)
| المهمّة | الفجوة | الملفات |
|---|---|---|
| أداة `unwrapResult()` عامة + مراجعة كل mapper | G-2 | `api/mappers/_envelope.ts` + كل mapper |
| فحص `error_no === 0` في الدخول | G-3 | `auth.mapper.ts` / `authStore.ts` |
| وحدة صلاحيات HakAccess (`can(module,action)`, isAdmin override) | G-1 | `services/auth/permissions.ts` + guards |
| اختبارات وحدة لكلٍّ مما سبق | — | `__tests__/` |

**معيار القبول:** تسجيل دخول حقيقي ينجح/يفشل بدقّة، والقوائم تُفكّ أغلفتها،
والصلاحيات تتحكّم بالتنقّل.

### 🟡 المرحلة 2 — ربط البيانات الحقيقية
| المهمّة | الفجوة | الملفات |
|---|---|---|
| إصلاح `Delete/UpdateReading → id={num}` | G-6 | `readingPushHandler.ts` |
| إصلاح bonds: `id={nmstnd}` + حسم TODO_SERVER | G-7 | `bondPushHandler.ts`, `endpoints.ts` |
| توسيع جدول `bonds` إلى 24 حقلاً + migration | G-4 | `database/schema.ts`, models, mappers |
| ربط `BondFormScreen`/`BondPaymentFormScreen`/`ReadingsBulkScreen` | — | الشاشات + repositories |
| ربط التقارير الثمانية (schemas+mappers+hooks+UI) | G-8 | `reports/*`, `api/mappers/reports.*` |
| ربط `CompanyInfoScreen` (GetCompanyData) + Home KPIs | — | settings/main |

**معيار القبول:** دورة CRUD كاملة offline→sync→server لكل من القراءات
والسندات وسندات الدفع، والتقارير تعرض بيانات حقيقية.

### 🟢 المرحلة 3 — تحسينات وتأكيدات
| المهمّة | الفجوة |
|---|---|
| interceptor لحقن `appId` (دعم تعدّد الفروع) | G-5 |
| اختبار `secureId` (hex8→decimal، بلا XOR) | G-9 |
| اختبار طابعة ميداني: cp1251 vs cp1256 | D-2 |
| Scanner/الكاميرا (مؤجّل) | — |
| تغطية اختبارات شاملة + e2e أساسي | — |

---

## 4. CI/CD التفصيلي

### GitHub Actions — `ci.yml` (عند كل PR)
```
jobs: install → lint → typecheck (tsc --noEmit) → jest
```

### GitHub Actions — `eas-build.yml` (يدوي/عند tag)
```
expo/expo-github-action → eas build --platform android --profile preview
```
- ملفات الأسرار: `EXPO_TOKEN` في GitHub Secrets.
- ملفّات تعريف EAS: `preview` (APK داخلي) + `production` (AAB).

---

## 5. الترتيب الزمني المقترح (Milestones)

| Milestone | المحتوى | المخرَج |
|---|---|---|
| M0 | ترحيل + CI/CD + اخضرار البناء | `app3` يبني ويمرّ الـ checks |
| M1 | المرحلة 1 (G-1/2/3) | دخول وصلاحيات وأغلفة صحيحة |
| M2 | المرحلة 2 (CRUD + سندات + تقارير) | تطبيق وظيفي كامل end-to-end |
| M3 | المرحلة 3 (تحسينات + اختبارات + طابعة) | جاهز للإنتاج |

---

## 6. المبادئ الحاكمة أثناء التنفيذ (غير قابلة للتفاوض)

1. **مصدر الحقيقة = APK المُفكَّك.** أي تعارض يُحسَم لصالح Java الأصلي.
2. **لا هلوسة كود.** أي غموض → نتوقّف ونسأل المستخدم.
3. **أسماء أعمدة DB الأصلية محفوظة** (`num/ks/kh/cas/asts/nmstnd...`).
4. **Clean Code + SOLID** لكل سطر جديد.
5. **كل تغيير → commit فوري → PR محدَّث.**
6. **كل النصوص عبر i18n، RTL، Feather icons.**

---

## 7. ما يحتاج قراراً من المستخدم قبل البدء بالتنفيذ ⚠️

أطرح هذه النقاط الآن (التزاماً بقاعدة "إن غمض شيء توقّف واسأل") قبل كتابة كود `app3`:

1. **استراتيجية `app3`:** أؤكّد اعتماد **الترحيل من app1** (لا إعادة بناء). موافق؟
2. **EAS:** هل لديك حساب Expo/EAS وتوكن (`EXPO_TOKEN`)؟ أم أستخدم بناء Android
   تقليدي عبر Gradle في Actions حتى تجهّز الحساب؟
3. **الطابعة (D-2):** cp1251 (الأصلي) أم cp1256 (app1) — هل بإمكانك اختبار ميداني
   على Datecs DPP-250؟ (سأجعلها قابلة للتبديل بإعداد لحين الحسم.)
4. **تعدّد الفروع (`appId`):** هل النظام أحادي الفرع (appId="1") حالياً، أم نحتاج
   دعم فروع متعدّدة من الآن؟ (يؤثّر في أولوية G-5.)

> **ملاحظة:** كلمة "كمل/استمر" تعني المتابعة بالخطة كما هي دون انتظار إذن إضافي،
> وسأفترض الإجابات الافتراضية الآمنة (ترحيل / Gradle مؤقتاً / codepage قابل للتبديل /
> أحادي الفرع) إن قلت "استمر" دون تحديد.
