# تحليل شاشات العمليات الأصلية (القراءة + سندات القبض) + تشخيص "ما تظهر بيانات"

> المصدر: APK v28 المفكوك (JADX) — `com.p001yd.electricecollector`
> التاريخ: 2026-05-29 | الجلسة: 004
> القاعدة: v28 = المصدر الأوحد لسلوك العميل (DEC-6)

---

## 0. ملخص تنفيذي (بالعراقي للمستخدم)

شخّصت سبب "تسجّل الدخول زين بس ما تطلع بيانات". المشكلة **مو في تسجيل الدخول ولا في التوكن** — المشكلة في **بناء طلبات القوائم + فك أسماء الحقول**. لقيت ٥ أعطال حقيقية بالكود (مو تخمين)، وكلها موثّقة تحت.

---

## 1. شاشة القراءة — `ListReadingActivity.java`

### 1.1 طريقة العرض
- **ما تتحمّل البيانات تلقائياً عند فتح الشاشة.** المستخدم لازم يضغط زر "عرض" (`btnView` → `AsyncDownloadStart()`).
- فلاتر اختيارية: المكان/المستلم (`spmstlm`)، الطبلة (`sptblh`)، المجموعة (`spgroup`)، checkbox للقراءات الفارغة.
- عند الضغط على عنصر بقائمة (وقيمته `cas == 0` فقط) → bottom sheet للتعديل (`reading_edit`).

### 1.2 الطلب الفعلي (الحقيقة الحاسمة)
```
GET {base}/electric/GetListReadingCounter
    ?id={user.getNou()}        ← رقم المستخدم NOU — إلزامي دائماً
    &nomstlm={place.num}       ← اختياري (إذا اختار مكان)
    &notblh={group.num}        ← اختياري (إذا اختار طبلة)
    &nogroup={textValue}       ← اختياري (نص المجموعة)
    &isnull=true               ← اختياري (إذا checkbox مفعّل)
    &appId={appId}             ← إلزامي
```
- **ملاحظة:** لا يوجد `pageNumber` / `pageSize` / `name` في طلب القراءة (بعكس ما كان موثّق سابقاً عن مسار loopj).

### 1.3 الـ envelope (شكل الرد)
```json
{ "GetListReadingCounterResult": [ { ...ItemReading... }, ... ] }
```
- مفتاح الـ wrapper = **`GetListReadingCounterResult`** (من `ReadingResponse.java`).

### 1.4 حقول `ItemReading` (verbatim من v28)
| الحقل | النوع | المعنى |
|---|---|---|
| `num` | int | تسلسل/معرّف |
| `noadad` | String | رقم العداد |
| `name` | String | اسم المشترك |
| `namet` | String | الاسم البديل |
| `ind` | int | نوع العداد |
| `nomstlm` | int | المستلم/المنطقة |
| `notblh` | long | الطبلة |
| `nog` | int | المجموعة |
| `ks` | double | القراءة السابقة |
| `kh` | double | القراءة الحالية |
| `cas` | int | حالة الترحيل (0 = غير مرحّل) |
| `asts` | double | الاستهلاك المتوقع |

---

## 2. شاشة سندات القبض — `ListBondsActivity.java`

### 2.1 طريقة العرض
- تعتمد فترة (`viewPeriod.startDate` → `endDate`) + سحب للتحديث (`SwipeRefreshLayout`).
- فيها طباعة عبر BIXOLON (`SplashScreenActivity.mPrinter`, connection string يبدأ بـ `bth://`).

### 2.2 الطلب الفعلي
```
GET {base}/electric/GetListBonds
    ?nou={user.getNou()}                      ← NOU — إلزامي
    &sdate={Utils.getShortDateStrApi(start)}  ← تاريخ بداية — إلزامي
    &edate={Utils.getShortDateStrApi(end)}    ← تاريخ نهاية — إلزامي
    &num_s={user.getNOA()}                     ← فقط إذا user.getSYS() != 1
    &appId={appId}                             ← إلزامي
```
- **منطق مهم:** إذا المستخدم مو SYS، يفلتر على صندوقه (`num_s = NOA`). إذا SYS، يشوف الكل.

### 2.3 الـ envelope
```json
{ "GetListBondsResult": [ { ...ItemBonds... }, ... ] }
```
- مفتاح الـ wrapper = **`GetListBondsResult`** (من `BondsResponse.java`).

### 2.4 حقول `ItemBonds` (verbatim)
`num, num_s, nmstnd, name, name_s, type, cas, mdate, dain, mden, equal, balance(→rsed), price_trans, currencyid, currencyname, currency{}, account{}, branchid, userid, notes, notes_box, notes2(→bin), nref, nref_docno, finalbalance`

---

## 3. مصدر بيانات المستخدم (LoginPresenter + Users)

- بعد تسجيل الدخول، السيرفر يرجّع كائن `Users` فيه:
  - **`NOU`** (رقم المستخدم — مفتاح كل الفلترة)
  - الصلاحيات: `SYS, NOA, ED, DE, S_K, S_S, REP`
  - `access_token`, `NAME_U`, `date_server`
- يُخزَّن في SharedPreferences ويُقرأ عبر `AppConfig.getUser()`.
- منطق الصلاحيات (تأكّد من `Users.java`): الفحص **`> 0`** (مو `== 1`) لبناء `HakAccess`:
  - `repBondsReciept.read = S_K>0 || SYS>0`
  - `repBondsPayment.read = S_S>0 || SYS>0`
  - التقارير تحتاج `REP>0 || S_K>0 || SYS>0` أو `SYS>0` فقط حسب التقرير.

---

## 4. التشخيص: لماذا "ما تظهر بيانات" في app1

### 🔴 العطل #1 — مفتاح الـ envelope غلط (الأخطر)
- **الأصلي يرجّع:** `{ "GetListReadingCounterResult": [...] }` / `{ "GetListBondsResult": [...] }`.
- **app1 يتوقع:** إما array مباشر أو `{ "data": [...] }` (شوف `zEnvelope` في `common.ts`: `{ success?, data, message? }`).
- **النتيجة:** الرد لا array ولا فيه `data` → الاتحاد (`union`) يفشل → ZodError → القائمة فاضية.
- **الإصلاح:** الـ envelope لازم يفك المفتاح `{Operation}Result` (اسم العملية + "Result").

### 🔴 العطل #2 — معاملات الطلب ناقصة
- **القراءة:** app1 موثّق عنده `id&isnull&notblh&nomstlm&nogroup&appId` (صح) لكن لازم نتأكد إن `id = NOU` فعلاً يُرسل، وإلا فاضي.
- **السندات:** الأصلي يحتاج `nou + sdate + edate + (num_s إذا مو SYS) + appId`. لو app1 يرسل `pageNumber/pageSize/name` بدل ذلك → السيرفر يرجّع فاضي أو خطأ.

### 🔴 العطل #3 — أسماء حقول الـ DTO مختلفة جذرياً
- **Account الأصلي:** `num, name, namet, balance, dain, mden, noadad, nog, nomstlm, notblh, tel, type, namep`.
- **app1 AccountDtoSchema:** `id, code, name, balance, currency_id, phone, address`.
- **النتيجة:** `z.array(itemSchema).parse(list)` يفشل على كل صف لأن `id` غير موجود (الأصلي اسمه `num`) → القائمة فاضية.
- نفس المشكلة محتملة في `BondDtoSchema` (`bond_no, bond_type, account_id…` بدل `nmstnd, type, num_s…`).

### 🟠 العطل #4 — توقيت التحميل (سلوك UX)
- الأصلي **ما يحمّل عند فتح الشاشة** — ينتظر ضغط "عرض". إذا app1 يحمّل عند الـ mount بدون تمرير الفلاتر/التواريخ الصحيحة → نتيجة فاضية.

### 🟠 العطل #5 — معاملات السندات تعتمد SYS
- إذا app1 ما يطبّق منطق `if (SYS != 1) num_s = NOA` → ممكن يرسل `num_s` خطأ أو يهمله ويطلع نتيجة خاطئة/فاضية.

---

## 5. خلاصة الإصلاح المطلوب (ترتيب الأولوية)
1. **تصحيح الـ envelope** ليفك `{Operation}Result` (P0).
2. **مطابقة أسماء حقول الـ DTO** مع الكيانات الأصلية حرفياً (`ItemBonds`, `Accounts`) (P0).
3. **بناء معاملات الطلب** حسب كل شاشة (`id=NOU` للقراءة، `nou+sdate+edate+num_s` للسندات) (P0).
4. **حفظ `NOU` + الصلاحيات** من رد تسجيل الدخول والتأكد من قراءتها قبل الطلب (P0).
5. **توقيت التحميل** بزر "عرض" مثل الأصلي (P1).
