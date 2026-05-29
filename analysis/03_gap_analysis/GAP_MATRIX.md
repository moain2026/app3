# 03 — مصفوفة الفجوات التفصيلية (GAP MATRIX)

> **الغرض:** مقارنة سطر-بسطر بين منطق العمل في التطبيق الأصلي (مصدر الحقيقة
> الوحيد — APK المُفكَّك) وبين ما هو منفّذ فعلياً في `app1/AbbasiTahseel`،
> مع تحديد بالضبط ما هو **خطأ**، وما هو **ناقص**، وما هو **صحيح لكن غير مربوط**.
> **تاريخ:** 2026-05-29.
> **القاعدة:** كل ادّعاء هنا مُسنَد إلى ملف Java حقيقي (سطر) + ملف app1 (سطر).
> لا توجد افتراضات؛ ما لم يُتحقَّق منه يُوسَم ⚠️.

---

## 0. ملخّص تنفيذي

| التصنيف | العدد | الدلالة |
|---|---|---|
| 🔴 فجوات حرجة (تمنع التشغيل الصحيح) | 3 | G-1, G-2, G-3 |
| 🟡 فجوات متوسطة (تسبّب خطأ منطقي/بيانات ناقصة) | 5 | G-4, G-5, G-6, G-7, G-8 |
| 🟢 فجوات منخفضة (تأكيد/تحسين) | 1 | G-9 |

**الحكم:** لا توجد فجوة بنيوية معمارية. كل الفجوات هي **منطق عمل** قابلة
للإصلاح ضمن الطبقات الموجودة دون إعادة هيكلة.

---

## G-1 🔴 — نظام الصلاحيات: تبسيط مُفرِط يكسر منطق HakAccess الأصلي

> ⚠️ **مُحدَّث بعد تحليل الباكِند (B3):** النظام **طبقتان** وليس طبقة واحدة.
> انظر `01_legacy_apk_analysis/KNOWN_DISCREPANCIES.md` D-12 وقالب B3
> `for_main_repo/permissions_matrix.md`.

### ما يفعله الأصلي (مصدر الحقيقة)

**Tier-A — أعلام على مستوى المستخدم (UI-gating):**
- الخادم (`USER_R`) + DTO `Users` يحملان **7 أعلام `int`**:
  `NOA, ED, DE, S_K, S_S, REP, SYS` (B3 `04_PERMISSIONS_SYSTEM.md`). الكلاينت
  الأصلي يخزّنها في `entities/HakAccess.java` عبر `HakAccessHelper`.
- `NOA` **مزدوج**: رقم صندوق/till (`no_box`) **و** علم قدرة — **افصلهما في app3**
  (`tillAccountId: number` و `canListAccounts: boolean`).
- `SYS` (مدير النظام) يتجاوز الباقي.
- **منطق الفحص (⚠️ يجب التحقق):** B3 يرجّح `Convert.ToInt32(row["FLAG"]) == 1`
  في C# الخادم. تحليلي السابق (من الكلاينت) رأى `> 0`. **اقرأ
  `entities/Users.java` + `HakAccessHelper.java` في v28 لحسم المنطق قبل الكتابة.**

**Tier-B — ACL لكل مكان (row-filtering على الخادم):**
- جدول `USER_MNATK(NOU, no_mstlm, RED, SDAD, NAMEM)`: `RED`=قراءة،
  `SDAD`=كتابة لكل مكان (`no_mstlm`). الخادم يفرضها عبر SQL subqueries
  (`nvl(red,0)>0` / `nvl(sdad,0)>0`). **الكلاينت لا يحتاج تطبيقها** — يصله
  الخادم مصفّى؛ لكن endpoint `GetListUserPlaces` يعيد Tier-B لعرض الأماكن المسموحة.

### ما يفعله app1 (خطأ)
`src/stores/authStore.ts` (الأسطر 139–145):
```ts
permissions: {
  canDelete: raw.DE === true,        // ← يعتمد على preprocessing في الـ schema
  canEdit: raw.ED === true,
  canViewReports: raw.REP === true,
  canViewAllReadings: raw.S_K === true,
  canViewAllSubscribers: raw.S_S === true,
  isAdmin: raw.SYS === true,
}
```
- يحوّل الأعلام الـ6 إلى **6 booleans مسطّحة** ويفقد تفريعها إلى 8 قوائم.
- يعتمد على `zBoolLoose` في الـ schema لتحويل `int !== 0 → true` — **هذا صحيح
  جزئياً** (يحافظ على دلالة `> 0`)، لكنه يفقد طبقة "القائمة" (read/update/delete
  لكل وحدة) ولا يطبّق قاعدة **`isAdmin` يتجاوز الكل**.

### الأثر
- مستخدم بصلاحية `SYS` فقط (مدير) لن تظهر له شاشات إن لم تكن أعلامه الأخرى مرفوعة،
  لأن `canEdit/canDelete...` لا ترث من `isAdmin`.
- لا يوجد فصل بين "أرى السندات" و"أعدّل السندات" و"أحذف السندات".

### الإصلاح المطلوب
إنشاء وحدة `permissions` مركزية تعيد بناء منطق HakAccess (استرشد بقالب B3
`for_main_repo/permissions_matrix.md` ودالة `can(me, p)`):
```ts
// 1) Tier-A: 7 أعلام (NOA,ED,DE,S_K,S_S,REP,SYS) — المنطق حسب v28 (>0 أو ==1: تحقّق)
// 2) isAdmin (SYS) يتجاوز كل شيء (Tier-B لا ينطبق على SYS)
// 3) افصل NOA: tillAccountId (رقم) عن canListAccounts (bool)
// 4) دالة can(action, module) تشتقّ القوائم من الأعلام (خريطة endpoint↔flag)
function can(p: Permissions, module: Module, action: Action): boolean {
  if (p.isAdmin) return true;                 // SYS يفتح الكل
  // خريطة (module, action) → flag وفق permissions_matrix.md / AUTH_FLOW.md
}
// Tier-B: لا تُطبَّق في الكلاينت (الخادم يصفّي)؛ فقط اعرض الأماكن من GetListUserPlaces.
```
**ملفات الإصلاح:** `src/stores/authStore.ts` + ملف جديد
`src/services/auth/permissions.ts` + استخدامه في الحرّاس (navigation guards).

---

## G-2 🔴 — فكّ غلاف `{OperationName}Result` (WCF envelope)

### ما يفعله الأصلي
كل استجابات خدمة WCF مغلَّفة باسم العملية + `Result`. أمثلة مؤكَّدة من Java:
- `ReadingRepository.java:43` → `jSONObject.getJSONObject("DeleteReadingResult").getString("Result")`
- `AuthRepository` → `jSONObject2.getJSONObject("LoginResult")`
- النمط العام: `GetReading → {"GetReadingResult": {...}}`،
  `SaveBond → {"SaveBondResult": {...}}`، إلخ.

### ما يجب التحقق منه في app1
يجب أن **كل mapper** في `src/services/api/mappers/*` يفكّ الغلاف المطابق لاسم العملية
قبل تمرير البيانات إلى Zod. الخطر: mapper يقرأ الجذر مباشرة فيحصل على `undefined`.

### الإصلاح المطلوب
- إنشاء أداة عامة `unwrapResult(operationName, payload)` في
  `src/services/api/mappers/_envelope.ts`.
- مراجعة كل mapper والتأكد أنه يستدعيها (auth, reading, bonds, reports, company).
- **⚠️ يتطلّب مراجعة فعلية لكل ملف mapper** — مُدرَج كمهمّة في الخطة (المرحلة 1).

---

## G-3 🔴 — منطق نجاح تسجيل الدخول `error_no === 0`

### ما يفعله الأصلي
النجاح يُحدَّد بـ `error_no === 0` داخل `LoginResult` (وليس HTTP 200 وحده).
الحقل `error_no` يأتي ضمن الكيان `Users`/الاستجابة.

### ما يجب في app1
يجب أن يفحص `authStore.login()`/mapper قيمة `error_no` ويرفض الدخول إن كانت `!= 0`
مع عرض `error_msg`. الخطر: قبول استجابة تحتوي `error_no != 0` كنجاح.

### الإصلاح
في `auth.mapper.ts` (أو store): `if (loginResult.error_no !== 0) throw AuthError(loginResult.error_msg)`.
**⚠️ يتطلّب التأكد من تنفيذه فعلياً.**

---

## G-4 🟡 — جدول `bonds`: 8 حقول بدل 24 (`ItemBonds`)

### الأصلي
`entities/ItemBonds.java` = **24 حقلاً** (مُوثَّقة كاملةً في
`01_legacy_apk_analysis/entities/ENTITIES.md`). نفس الكيان `ItemBonds`
يُستخدم لـ **السندات** و**سندات الدفع** معاً.

### app1
`src/database/schema.ts` (حول السطر 77) يعرّف جدول `bonds` بحقول مبسّطة فقط.

### الأثر
- فقدان حقول ضرورية للطباعة/التقارير/المزامنة الكاملة.
- `nmstnd` (معرّف السند، يُستخدم في delete/update) يجب أن يكون موجوداً وفهرساً.

### الإصلاح
توسيع جدول `bonds` ليطابق الـ24 حقلاً (مع migration)، والإبقاء على أسماء
الأعمدة الأصلية، وفصل bondPayment منطقياً عن bond رغم اشتراكهما بالكيان.

---

## G-5 🟡 — حقن `appId` (رقم الفرع) تلقائياً

### الأصلي
كل طلب CRUD يحمل `?appId={branch}`؛ القيمة من `_appId` (افتراضي "1").
أمثلة: `SaveReading?appId=1`، `DeleteBond?appId=1&id={nmstnd}`.

### app1
`appId` غير محقون تلقائياً في طبقة الـ pull/push؛ يُمرَّر يدوياً أو غائب.

### الإصلاح
interceptor في `services/api` يحقن `appId` من `authStore.branchId` لكل طلب
يتطلّبه. **أولوية P3 (مؤجّل)** — لا يكسر التشغيل أحادي الفرع (appId="1").

> ⚠️ **مُحدَّث بعد B3:** `appId` ليس مجرّد «رقم فرع» تجميلي — بل **مفتاح
> المستأجر (tenant key)** الذي يختار به الخادم **قاعدة Oracle المناسبة**
> (`Dictionary<int,string> ConnetionStrings`). إن كانت الشركة تُشغّل **عدة فروع**
> (قواعد بيانات منفصلة)، ترتفع أولوية G-5. **تأكيد عدد الفروع بيد المستخدم.**
> انظر B3 `07_MULTI_TENANT.md`.

---

## G-6 🟡 — `DeleteReading`/`UpdateReading` تستخدم المعرّف الخطأ

### الأصلي (تأكيد قاطع من Java)
`model/ReadingRepository.java:29`:
```java
RestServiceHelper.delete(String.format("%s?appId=%s&id=%s", ...,
    "DeleteReading", this._appId, Integer.valueOf(itemReading.getnum())) ...
// id = getnum()  → int num (وليس noadad)
```
`UpdateReading` (السطر 107) كذلك: `id = getnum()`.
> `noadad` في `ItemReading` هو **String** (رقم العدّاد/المشترك)، أمّا `num` فهو
> **int** المعرّف الأساسي للقراءة على الخادم.

### app1 (خطأ)
`src/services/sync/push/readingPushHandler.ts:72`:
```ts
params: { noadad: payload.dto.noadad },   // ❌ يجب id = num
```

### الأثر
- الخادم يتوقّع `id={num}` → الحذف/التعديل سيفشل أو يصيب صفاً خاطئاً.

### الإصلاح
```ts
// DeleteReading
params: { id: payload.dto.num }           // ✅ مطابق ReadingRepository.java:29
// UpdateReading
params: { id: payload.dto.num }           // ✅ مطابق ReadingRepository.java:107
```
ونفس المبدأ للسندات: `DeleteBond/UpdateBond → id = nmstnd`
(`BondsRepository.java:29,107`).

---

## G-7 🟡 — `bondPushHandler` يحتوي `TODO_SERVER` غير محسوم

### app1
`src/services/sync/push/bondPushHandler.ts`:
- السطر 48: `// TODO_SERVER: replace with explicit Endpoints.saveBond / updateBond`
- السطر 114: نفس الـ TODO لسندات الدفع.

### الحقيقة من الأصلي (تحسم الـ TODO)
العمليات موجودة فعلاً في الأصلي (loopj) حتى لو غابت عن Retrofit `ApiService.java`:
- `BondsRepository.java` → `SaveBond` / `UpdateBond?appId=&id={nmstnd}` / `DeleteBond`
- `BondsPaymentRepository.java` → `SaveBondPayment` / `UpdateBondPayment?id={nmstnd}` / `DeleteBondPayment`
- التسلسل JSON عبر Moshi لكيان `ItemBonds` كاملاً.

### الإصلاح
- إضافة `saveBond/updateBond/deleteBond/saveBondPayment/...` صراحةً إلى
  `endpoints.ts` (إن لم تكن مكتملة) واستخدامها بدل الـ TODO.
- `update/delete` تمرّر `id = nmstnd`.

---

## G-8 🟡 — شاشات التقارير الثمانية بلا ربط API

### الأصلي
8 تقارير عبر نقاط `GetRep*`:
`GetRepReadingHeader, GetRepBondsHeader, GetRepBalanceHeader, GetRepBalanceDetails,
GetRepBoxMove, GetRepBoxMoveDetails, GetRepExpenses` (+ موزّع).
كلها استجابات مغلَّفة بـ `{...Result}`.

### app1
الشاشات الثماني موجودة (UI) لكنها MOCK — لا تستدعي API ولا تعرض بيانات حقيقية.

### الإصلاح
- mappers + schemas لكل تقرير (مع `unwrapResult`).
- ربط كل شاشة بـ hook بيانات + حالات تحميل/خطأ/فارغ.
- التقارير للقراءة فقط (لا offline write) → لا تحتاج طابور مزامنة.

---

## G-9 🟢 — تأكيد خوارزمية `secureId`

### الأصلي (مؤكَّد)
`getDeviceId()` = أوّل 8 خانات hex من `ANDROID_ID` → تحويل إلى عدد عشري.
**لا يوجد XOR** (وثائق app1 النثرية كانت تقول XOR — خطأ، الكود لا يفعل).
> نظام الترخيص (`GenerateKey/isKeyValid`) منفصل تماماً وله ثوابت XOR خاصّة —
> لا علاقة له بـ `secureId`.

### app1
يجب التأكد أن `getDeviceId` يطبّق نفس البساطة (hex8 → decimal، بلا XOR).

### الإصلاح
مراجعة بسيطة + اختبار وحدة يثبّت الخوارزمية.

---

## ملاحظات تناقض إضافية (من KNOWN_DISCREPANCIES)

| الرمز | الوصف | الحالة |
|---|---|---|
| D-2 | الطابعة: الكود الفعلي يرمّز العربي بـ **Windows-1256** (`PrinterActivity.java:322`)؛ مورد `cp1251` في strings.xml غير مستخدم | ✅ محسوم — **app1 صحيح بـ cp1256** |
| D-2b | موديل الطابعة الفعلي = **BIXOLON SPP-R310** (ESC/POS عبر Bluetooth SPP) — متوافق مع تصميم app1 | ✅ محسوم — تصميم عام ESC/POS |
| D-1 | أنواع الحقول في الوثائق النثرية كانت غير دقيقة — **الكود صحيح** | محسوم (الكود مرجع) |

---

## خريطة الإصلاح حسب الأولوية

| المرحلة | الفجوات | السبب |
|---|---|---|
| **P1 (حرج — قبل أي ربط)** | G-2, G-3, G-1 | تصحّح العقد الأساسي (الدخول/الصلاحيات/الأغلفة) |
| **P2 (ربط البيانات)** | G-6, G-7, G-4, G-8 | تصحيح CRUD + توسيع السندات + ربط التقارير |
| **P3 (تحسينات/تأجيل)** | G-5, G-9 | متعدّد الفروع + تأكيدات + اختبار طابعة BIXOLON SPP-R310 ميداني |

> التنفيذ التفصيلي لكل مرحلة في `../04_architecture_plan/`.
