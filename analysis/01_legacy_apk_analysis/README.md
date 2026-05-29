# 01 — تحليل التطبيق الأصلي (Legacy APK) — مصدر الحقيقة

> **المصدر:** `ElectricCollector28.apk` (12 MB) من مستودع
> `moain2026/ElectricCollectorAnalysis`.
> **أداة فك التشفير:** JADX 1.5.0 (`--no-debug-info --deobf`).
> **عدد ملفات Java المستخرجة:** 189 ملف داخل الحزمة `com.yd.electricecollector`.
> **تاريخ التحليل:** 2026-05-29.
> **حالة التحقق:** كل ما في هذا التقرير مُستخرَج **حرفياً** من الكود
> المُفكَّك، وليس مفترَضاً. أي بند غير مؤكد مُعَلَّم صراحةً بـ ⚠️.

---

## 0. الهوية العامة للتطبيق

| الخاصية | القيمة (من `AndroidManifest.xml` + `strings.xml`) |
|---|---|
| اسم الحزمة | `com.yd.electricecollector` |
| اسم التطبيق (عربي) | **كهرباء تحصيل** |
| `versionCode / versionName` | `1` / `1.0` |
| `minSdkVersion` | 24 (Android 7.0) |
| `targetSdkVersion / compileSdk` | 34 (Android 14) |
| `usesCleartextTraffic` | **`true`** (يسمح HTTP غير مشفّر — مطلوب للسيرفر) |
| `debuggable` | `true` (نسخة debug) |
| `supportsRtl` | `true` |
| الـ Launcher Activity | `SplashScreenActivity` |
| محرك الشبكة | Retrofit2 + OkHttp + **loopj AsyncHttpClient** (مزدوج!) |
| محرك JSON | **Moshi** (وليس Gson في مسار الشبكة) |
| محرك PDF | iText (AGPL) |

### ملاحظة معمارية مهمة (مكتشفة)
التطبيق الأصلي يستخدم **محرّكي شبكة في آنٍ واحد**:
1. **Retrofit2 + OkHttp** (`ApiService.java` / `RetrofitBuilder.java`) — مُعرَّف لكن
   يبدو أنه غير مستخدم في المسارات الحرجة (login/reading/bonds).
2. **loopj `AsyncHttpClient`** (`RestServiceHelper.java`) — **هذا هو المحرك
   المستخدم فعلياً** في `AuthRepository` و `ReadingRepository` و
   `BondsRepository`. كل عمليات الإنتاج تمرّ عبره.

هذا يفسّر سبب التضارب في تعريفات تسجيل الدخول (انظر §3).

---

## 1. فهرس ملفات التقرير

| الملف | المحتوى |
|---|---|
| `endpoints/API_CONTRACT.md` | كل نقاط النهاية (30+) بمعاملاتها وأغلفة استجاباتها |
| `entities/ENTITIES.md` | كل الكيانات (DTOs) بحقولها الدقيقة وأنواعها |
| `AUTH_FLOW.md` | منطق المصادقة الكامل + نظام الصلاحيات (HakAccess) |
| `SECURITY_DEFENCE.md` | خوارزمية `secureId` و `Defence` الحقيقية (مصحّحة) |
| `decompiled_sources/` | 189 ملف Java مرجعي (للقراءة فقط) |
| `resources/` | AndroidManifest + strings (ar + default) |

---

## 2. البنية الوظيفية (الوحدات الرئيسية)

من تحليل الـ Activities في `AndroidManifest.xml` وحزمة `p002ui`:

1. **المصادقة والإعدادات**
   - `SplashScreenActivity` → `LoginActivity` → `MainActivity`
   - `Preferences` (إعدادات الاتصال: IP + رقم الفرع)
   - `ChangePasswordActivity` / `EnterPasswordActivity`
   - `AboutActivity`, `LogoActivity`

2. **القراءات (Readings)**
   - `ListReadingActivity` — قائمة قراءات العدادات + بحث + ترقيم صفحات
   - `ViewPeriodActivity` — عرض الفترة

3. **السندات (Bonds / المدفوعات)**
   - `ListBondsActivity` / `EntryBondsActivity` — سندات القبض
   - `ListBondsPaymentActivity` / `EntryBondsPaymentActivity` — سندات الدفع
   - أدوات بحث: `LookupAccountsActivity`, `LookupPleasesActivity`,
     `LookupGroupsActivity`

4. **التقارير (Reports) — 8 تقارير**
   - `ListReadingReportActivity` (repListReading)
   - `BondsHeaderReportActivity` (repBondsReciept)
   - `ListBondsReportActivity` (repBondsPayment)
   - `BalanceStateReportActivity` (repBalanceHeader)
   - `BalanceStateDetailsReportActivity` (repBalanceDetails)
   - `BoxMovesReportActivity` (repBoxMoves)
   - `BoxMovesDetailsReportActivity`
   - `ExpensesReportActivity` (repExpenses)
   - (+ repCollectorMony — صلاحية فقط)

5. **الطباعة (Printer)** — نظامان بلوتوث:
   - `printer/bluetooth/*` (RFComm + NDK)
   - `printer/andoirdbluetoothprint/*`
   - `printer/driver/DatecsDpp250Driver.java` + `JP5802Driver.java`
   - `codepage = cp1251` ⚠️ (المورد يقول cp1251، لكن طابعات Datecs العربية
     تستخدم cp1256 — يحتاج تأكيد، انظر `KNOWN_DISCREPANCIES.md`)

6. **SMS** — `ActivitySMS`, `sms/SmsData.java` (إرسال إشعارات؟)

---

## 3. التناقضات والاكتشافات الحرجة (مختصر — التفصيل في الملفات الفرعية)

1. **`appId` دائماً camelCase في المسار الفعلي.** `AuthRepository.java`
   (المسار المستخدم) يبني JSON بـ `put("appId", ...)`. الـ DTO
   `AuthData.java` يستخدم `appid` lowercase لكنه **يُستخدم فقط مع
   `/UserAuth`** (مسار غير مستخدم). → الوكلاء السابقون كانوا محقّين.

2. **استجابة `/Login` مُغلَّفة في `LoginResult`.** الخادم يرد:
   `{ "LoginResult": { ...Users... } }`. هذا نمط WCF.

3. **كل استجابات القوائم مغلَّفة بـ `{EndpointName}Result`.** مثال:
   `GetListReadingCounterResult`, `DeleteBondResult.Result`.

4. **خوارزمية `secureId` الحقيقية أبسط مما وُثِّق سابقاً** — لا يوجد XOR
   في `getDeviceId()`. التفصيل في `SECURITY_DEFENCE.md`.

5. **نظام الصلاحيات (HakAccess)** مُشتق من 6 أعلام في كائن `Users`
   (`ED, DE, S_K, S_S, REP, SYS`). التفصيل في `AUTH_FLOW.md`.

6. **`SaveBond` / `UpdateBond` / `UpdateReading` موجودة فعلاً** في
   الـ Repositories (loopj) لكنها **غير معرّفة في واجهة Retrofit
   `ApiService.java`**. تأكَّد بعد فحص أعمق:
   `BondsRepository` يحوي `GetListBonds + SaveBond + UpdateBond +
   DeleteBond`. → تعريف app1 لها **صحيح ومطابق**. التفصيل في
   `endpoints/API_CONTRACT.md` §ج/§د.

7. **`BASE_URL` المضمّن** = `http://192.168.0.100:3000/` (في
   `RetrofitBuilder`) و `geturl` = `192.168.0.100` (في strings). الـ IP
   الفعلي للإنتاج (`100.87.131.115` عبر Tailscale) يُحقَن في وقت التشغيل
   من إعدادات المستخدم، وليس مضمّناً.
