# UI Ground Truth — Legacy App (v28) Real Screenshots + Real Data

> Source: official screenshots + a real "ارصدة الحسابات" PDF (42 pages, 2026-05-29)
> provided by the owner (معين العباسي / شركة العباسي - الحديدة).
> This is the **single source of truth** for migrating app3 screen-by-screen.

---

## 0. Company / Tenant identity (from "جلب البيانات" + drawer)

| Field | Value |
|-------|-------|
| إسم المؤسسة (org name) | شركة العباسي |
| العنوان (address) | لتوليد وتوزيع الكهرباء - الحديدة |
| رقم الهاتف (phone) | وتساب 771506017 |
| المالك (owner, drawer) | شركة العباسي |
| الشعار (logo) | present (green placeholder when not loaded) |

Report header/footer (PDF):
- Header: `شركة العباسي / الحديدة - لتوليد وتوزيع الكهرباء / واتساب 771506017`
- Footer left: `YDsoft`  |  Footer center: `1 / 42`  |  Date: `2026-05-29`

---

## 1. Connection Settings ("اعدادات الاتصال")

| Label (RTL) | Value | Notes |
|-------------|-------|-------|
| عنوان السيرقر (server) | `100.87.131.115` | matches DEFAULT_HOSTING_IP in app3 prefs |
| رقم الفرع (branch) | `1` | == appId |
| الرقم التسلسلي (serial) | `2098897319` | device-derived secureId (read-only label) |
| Buttons | تراجع (cancel) / موافق (ok) | side-by-side at top |

> CONFIRM: app3 ServerSettingsScreen must show these 3 fields, serial read-only.

---

## 2. Home / Main Menu — TWO TABS

Header: power-off icon · gear icon · title `المستخدم/ معين ال...` · hamburger menu.
Tabs: **عمليات (Operations)** | **تقارير (Reports)**.

### Tab "عمليات" (Operations) — order:
1. سندات القبض (Receipt Vouchers)
2. القراءات (Readings)

### Tab "تقارير" (Reports) — order:
1. ارصدة الحسابات (Account Balances)
2. كشف حساب تفصيلي (Detailed Account Statement)
3. كشف اجمالي التحصيل (Total Collection Statement)
4. تقرير الاستهلاك (Consumption Report)
5. تقرير اجمالي حركة الصناديق (Total Box Movement Report)
6. تقرير المصروفات اليومية (Daily Expenses Report)

Each item = white card, **orange lightning bolt** circular icon on the RIGHT.

> NOTE: The legacy Home has NO "today counters" cards. It's a 2-tab launcher.
> app3 HomeScreen currently shows today-count cards — that's an app3 invention,
> not in the original. Migration should consider matching the 2-tab launcher.

---

## 3. Side Drawer (hamburger)

Top: logo square + `المالك :` + blue bar `شركة العباسي`.
Items (top→bottom), each with icon:
1. عن النظام (About) — info icon
2. اعدادات (Settings) — gear icon
3. تغيير كلمة المرور (Change Password) — key icon
4. تسجيل خروج (Logout) — exit-door icon
5. انهاء (Exit/Quit) — power-circle icon

---

## 4. Readings Entry ("ادخال القراءات")

Top bar: back arrow · title · search icon · sort (up/down) icon.
Filters:
- المنطقة (region) — underlined dropdown + X clear
- الطبلة (feeder)  — underlined dropdown + X clear
- المجموعة (group) — **bordered box**, placeholder `رقم المجموعة`
- بدون قراءة (no-reading) — checkbox
- عرض (View) — tall blue button on the left

### Reading CARD layout (per subscriber):
- Blue header bar = `<full name> <serial-ish number> <region-name>`
  e.g. `ابراهيم سعيد احمد 687 غليل`
- Three columns: **العداد (meter)** | **الطبلة (feeder)** | **المنطقة (region)**
- Two reading rows:
  - `ق سابقة` (previous) — **gray** text
  - `ق حالية` (current) — **green** text

### REAL DATA captured (region=1, feeder=غير معروف for all):
| name | trailing# | region-name | meter (العداد) | ق سابقة | ق حالية |
|------|-----------|-------------|----------------|---------|---------|
| ابراهيم سعيد احمد | 687 | غليل | 1212584 | 3745.0 | 3745.0 |
| فارع عامر محمد احمد سالم | 213 | غليل | 2022870003191 | 27.0 | 27.0 |
| يوسف ابراهيم علي عبده | 69 | غليل | 10029850 | 8287.0 | 8287.0 |
| منصر احمد صالح | 750 | غليل | 1000892 | 3860.0 | 3860.0 |
| حياة محمد حسن حرازي | 793 | غليل | 8721693 | 5040.0 | 5040.0 |
| محمد احمد علي غلاب | 713 | غليل | 10026266 | 3825.0 | 3825.0 |
| حسني احمد علي شريف محمد | 339 | غليل | 10029768 | 700.0 | 700.0 |

> KEY: `ق حالية` defaults to `ق سابقة` until edited (so previous==current on fresh load).
> Meter serials vary wildly in length (7–13 digits) → store as STRING not int.

---

## 5. Receipt Voucher — ENTRY form ("سند قبض" add)

Header: save(disk) icon · `المستخدم/معين العباس..` · back arrow. Date banner `2026-05-29`.
Sections (dark-blue header bars), top→bottom:
- **بيانات المشترك** (subscriber data):
  - رقم المشترك (subscriber no.) + info(i) icon
  - اسم المشترك (subscriber name)
  - اسم المنطقة (region) | اسم الطبله (feeder)  ← two columns
  - المديونية (debt/arrears)
- **المبلغ المسدد** (paid amount): input `ادخل مبلغ التسديد`
- **البيان** (note): input

---

## 6. Receipt Vouchers — LIST ("سند قبض", landscape)

Top: back · title · `MNUITEMFILTERDATA` button · sort icon · `+` add. Plus a FAB `+`.
Columns (RTL): **التاريخ | المبلغ | الحساب | الرقم | البيان**(blank).

### REAL DATA:
| التاريخ | المبلغ | الحساب | الرقم |
|---------|--------|--------|-------|
| 2026/2/28 | 2,000 | مازن محمد صغير عبدالله ضحوي | 3122 |
| 2026/2/28 | 3,500 | عبدالخالق عبده احمد الدقم | 3123 |
| 2026/2/28 | 3,500 | عبد السلام حسن بن حسن بط | 3124 |
| 2026/2/28 | 14,300 | مناجي علي عبدالحميد النجار | 3125 |
| 2026/2/28 | 3,000 | احمد كليب علي حمود | 3126 |

> `الرقم` is a sequential voucher id (3122..3126). Date format displayed `yyyy/M/d`.

---

## 7. Account Balances report ("ارصدة الحسابات")

Top: back · title · search · sort · **orange PDF icon** (exports the 42-page PDF).
Filters identical to readings (المنطقة/الطبلة/المجموعة + عرض).
Columns (RTL): **الرصيد | الحالة | الحساب**.
- الحالة = `عليه` (debit/owes) OR `له` (credit/owed-to)
- الرصيد color: **red** when عليه (positive), **blue** when له (negative, shown as `940-`)
- Bottom summary: `الرصيد = -1,748,065` (blue) , `دائن` label on left.

### REAL DATA (screen + PDF agree):
| الحساب | الحالة | الرصيد |
|--------|--------|--------|
| قاسم علي محمد جحبي | عليه/مدين | 4,819 |
| ابتسام حسن سالم احمد | له/دائن | -940 |
| ابراهيم ابراهيم هادي مكين | عليه/مدين | 1,580 |
| ابراهيم ابو الغيث احمد مينه | له/دائن | -1,400 |
| ابراهيم احمد علي قاسم | عليه/مدين | 2,400 |
| ابراهيم احمد محمد زايد | له/دائن | -131 |
| ابراهيم احمد يحي علي | عليه/مدين | 3,479 |
| ابراهيم السيد يحي القاسمي | مدين | 160 |
| ابراهيم حامد قاسم احمد | دائن | -3,440 |
| ابراهيم حسن محمدجزاز | دائن | -6,880 |

> Report-side terminology mapping (from Settings):
> - `دائن` (creditor)  ↔ displayed as `له`
> - `مدين` (debtor)    ↔ displayed as `عليه`
> PDF total: **إجمالي الرصيد : -1,748,065** over **42 pages**.
> Sign convention: POSITIVE = عليه/مدين (customer owes), NEGATIVE = له/دائن.

---

## 8. Consumption Report ("تقرير الاستهلاك") — TWO TABS

Tabs: **حسب الطبلة (by feeder)** | **حسب المنطقة (by region)**.
Columns (RTL): **الاستهلاك | الوصف | الرقم**.

### "حسب المنطقة" REAL DATA:
| الرقم | الوصف | الاستهلاك |
|-------|-------|-----------|
| 1 | الدهمية | 10951 |
| 2 | الصبالية | 7728 |
| 4 | جمال | 7077 |
| 3 | غليل | 17646 |
| | **اجمالي** | **43,402** |

> Region master (from "المناطق" picker): 1=الدهمية, 2=الصبالية, 6=تجريب,
> 4=جمال, 3=غليل, 5=مشتركين اساسي.

---

## 9. Settings ("اعدادات") — full list (pink section headers)

- بيانات المؤسسة (org data)
- اعدادات الامان (security) — padlock
- **إعدادات القائمة (list settings)**
  - فترة عرض المستندات — `ضبط عرض المستندات لفترة من قائمة السندات`
    - عدد الايام (days count)
- **تسمية حالة الحساب في التقارير (status naming)**
  - دائن → `له`
  - مدين → `عليه`
- **تنبيه المقبوض اكبر من المديونية** — toggle `تفعيل` = OFF
- **الرسائل (SMS/messages)** — toggle `تفعيل` = ON
  - نص الرسالة = `لكم سند قبض بمبلغ :`
  - نهاية الرسالة = `مع جزيل الشكر`
- **الطباعة (printing)**
  - تحديد الطابعة (select printer)
  - حجم الورق = `58mm`
  - حجم الخط = `عادي`
  - نوع الطابعة = `XPrinter`

> IMPORTANT: legacy printer setting shows **XPrinter / 58mm**, NOT BIXOLON.
> Reconcile with the stated target printer (BIXOLON SPP-R310, cp1256):
> printer TYPE is user-selectable; default in this tenant = XPrinter 58mm.

---

## 10. Migration gaps surfaced by these screenshots

1. **Home model mismatch**: legacy = 2-tab launcher (عمليات/تقارير), no today-counters.
2. **Account status sign convention**: positive=عليه(مدين), negative=له(دائن). Reports must
   color red/blue + map label via Settings (دائن=له, مدين=عليه).
3. **Meter serial = STRING** (variable length up to 13 digits).
4. **Reading current defaults to previous** until edited.
5. **Voucher list columns**: التاريخ/المبلغ/الحساب/الرقم/البيان; id sequential.
6. **Settings**: messages text + footer, doc-display-days, status naming, printer
   (XPrinter/58mm/عادي) are all server/local config that must exist in app3 Settings.
7. **PDF export** from the balances screen (orange PDF icon) → 42-page report with the
   exact header/footer + `YDsoft` credit.
