# 02 — تقييم التطبيق الجديد (app1 / AbbasiTahseel)

> **المصدر:** `moain2026/app1` → مجلد `AbbasiTahseel/`.
> **التقنية:** React Native 0.74.5 (bare) + TypeScript strict + WatermelonDB
> + Zustand + Axios + Zod + i18next + react-native-bluetooth-classic.
> **تاريخ التقييم:** 2026-05-29.

## 0. الحكم العام (الأهم)

> 🟢 **التطبيق `app1` ليس 40% — هو أقرب إلى 65–70% من حيث البنية، لكن
> ~35% من حيث "الإنتاج الفعلي" (مربوط بالخادم الحقيقي).**

البنية المعمارية **ممتازة ومحترفة** وتلتزم بـ Clean Code و SOLID. الطبقات
مفصولة بوضوح. التحدّي المتبقّي ليس بناء بنية جديدة، بل **ربط الشاشات
الموجودة من MOCK إلى الـ API/DB الحقيقي**، وإكمال منطق العمل المُستخرَج من
الأصلي (الصلاحيات، أغلفة WCF، حقول السندات الكاملة).

## 1. الإحصائيات

| المقياس | العدد |
|---|---|
| الشاشات (`.tsx`) | 32 |
| الـ Stores (Zustand) | 6 (auth, license, sync, readings, printer, + index) |
| خدمات (`services/*.ts`) | 72 |
| نماذج DB (WatermelonDB) | 13 |
| مفاتيح الترجمة العربية | 491 (17 قسماً) |
| نقاط النهاية المعرّفة | ~35 (تطابق المصدر الأصلي) |

## 2. طبقات معمارية ممتازة (مكتملة وجاهزة) ✅

1. **طبقة الشبكة (`services/api/`)** — Axios + interceptors
   (auth, error, refresh, retry) + سجلّ endpoints منظّم + Zod schemas +
   mappers. **بنية احترافية.**
2. **طبقة التخزين (`services/storage/`)** — MMKV (`prefs`) + Keychain
   (`secureStorage`). جاهزة.
3. **قاعدة البيانات (`database/`)** — WatermelonDB + 13 model + schema
   يحافظ على أسماء الأعمدة القديمة (`num/ks/kh/cas/asts...`) ✅ مطابق
   للمصدر + migrationRunner skeleton.
4. **محرّك المزامنة (`services/sync/`)** — هيكل offline-first كامل:
   queue + worker + coordinator + backoff + connectivity +
   pull/push handlers + background fetch + events. **بنية قوية.**
5. **الطباعة (`services/printer/`)** — cp1256 encoder + Arabic shaper +
   ESC/POS builder + PrinterManager + 3 receipt builders. ✅ مكتملة.
6. **نظام التصميم (`design-system/`)** — tokens + theme (dark/light) +
   12 primitive component. جاهز.
7. **التنقّل (`navigation/`)** — Auth/Main stacks + Drawer + Tabs. جاهز.
8. **i18n** — 491 مفتاح عربي، RTL. جاهز.

## 3. حالة الشاشات (MOCK vs مربوط)

### 🟢 مربوطة بـ DB/Repository (شبه جاهزة)
- `main/ReadingsScreen` — observable من `readingsRepository` ✅
- `main/ReadingDetailScreen` — مربوط + زر طباعة ✅
- `bonds/BondsListScreen` — مربوط جزئياً (observe + lookups) 🟡
- `bonds/BondDetailScreen` — مربوط ✅
- `readings/ReadingsHistoryScreen` — رأس مربوط، الجدول MOCK 🟡

### 🟡 مربوطة بـ MOCK (تحتاج ربط حقيقي) — أولوية عالية
- `bonds/BondFormScreen` — MOCK (يحتاج createBond/updateBond)
- `bonds/BondPaymentFormScreen` — MOCK (يحتاج saveBondPayment)
- `readings/ReadingsBulkScreen` — MOCK

### 🟡 شاشات التقارير الثمانية — كلها MOCK
- `reports/ReadingHeaderReportScreen` (GetRepReadingHeader)
- `reports/BondsHeaderReportScreen` (GetRepBondsHeader)
- `reports/BalanceHeaderReportScreen` (GetRepBalanceHeader)
- `reports/BalanceDetailsReportScreen` (GetRepBalanceDetails)
- `reports/BoxMoveReportScreen` (GetRepBoxMove)
- `reports/BoxMoveDetailsReportScreen` (GetRepBoxMoveDetails)
- `reports/ExpensesReportScreen` (GetRepExpenses)
- `reports/ReportsHubScreen` (موزّع)

### 🟡 شاشات الإعدادات والملف الشخصي — stub/جزئي
- `settings/CompanyInfoScreen` — stub (يحتاج GetCompanyData + نموذج)
- `settings/PermissionsScreen` — stub
- `main/ProfileScreen` — stub
- `main/ScannerScreen` — stub (كاميرا مؤجّلة)
- `main/HomeScreen` — يحتاج KPIs حقيقية

## 4. الفجوات المنطقية الحرجة (من مقارنة المصدر الأصلي)

> التفصيل الكامل في `../03_gap_analysis/GAP_MATRIX.md`.

| # | الفجوة | الخطورة |
|---|---|---|
| G-1 | نظام الصلاحيات مُبسّط (6 أعلام مسطّحة) بدل منطق الـ 8 قوائم (HakAccess) المركّب من الأصلي | 🔴 عالٍ |
| G-2 | فكّ غلاف `{OperationName}Result` — يجب التأكد أن كل mapper يطبّقه | 🔴 عالٍ |
| G-3 | منطق `error_no === 0` للنجاح في `/Login` — تأكيد التطبيق | 🔴 عالٍ |
| G-4 | حقول `bonds` في schema مبسّطة (8 حقول) مقابل `ItemBonds` (24 حقل) | 🟡 متوسط |
| G-5 | `appId` (رقم الفرع) لا يُحقن تلقائياً في طلبات pull/push | 🟡 متوسط (مؤجّل P3) |
| G-6 | `DeleteReading` يمرّر `noadad` بدل `id={num}` (المصدر يستخدم num) | 🟡 متوسط |
| G-7 | bondPushHandler به `TODO_SERVER` غير محسوم | 🟡 متوسط |
| G-8 | شاشات التقارير الثمانية بلا ربط API | 🟡 متوسط |
| G-9 | `secureId` — تأكيد مطابقة `getDeviceId` البسيط | 🟢 منخفض |

## 5. جودة الكود (مطابقة CODING_RULES)

- ✅ TypeScript strict، `noUncheckedIndexedAccess`، صفر `any` (مزعوم).
- ✅ Zustand فقط، WatermelonDB observables.
- ✅ أسماء أعمدة DB القديمة محفوظة.
- ✅ i18n لكل النصوص، Feather icons، RTL.
- ⚠️ لا يوجد إطار اختبار مفعّل فعلياً (jest مُعرَّف لكن لا اختبارات).

## 6. الخلاصة
البنية أقوى بكثير مما يوحي به وصف "40%". العمل المتبقّي هو **ترحيل
منطق العمل من الأصلي + ربط الشاشات** وليس إعادة هيكلة. الخطة الكاملة في
`../04_architecture_plan/`.
