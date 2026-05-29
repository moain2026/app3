# API_CONTRACT — عقد الـ API الكامل (مستخرَج حرفياً من الـ APK)

> كل ما هنا مستخرَج من `network/ApiService.java` (واجهة Retrofit)
> ومن `model/*Repository.java` (الاستدعاءات الفعلية عبر loopj
> `RestServiceHelper`). حيث يختلفان، **مسار loopj في الـ Repository هو
> المعتمد** لأنه المُستخدم فعلياً في الإنتاج.

## القواعد العامة للعقد (Wire Conventions)

| القاعدة | التفصيل |
|---|---|
| Base URL وقت التشغيل | `http://{IP}:3000/electric/` (يُحقَن من إعدادات المستخدم) |
| Base URL مضمّن (افتراضي) | `http://192.168.0.100:3000/` (`RetrofitBuilder.BASE_URL`) |
| المصادقة | `Authorization: Bearer {access_token}` (في كل طلب محمي) |
| Content-Type (POST/PUT) | `application/json` (UTF-8) |
| Accept | `application/json` |
| Timeout | connect=200s, read=200s, retries=3 (loopj DEFAULT_RETRY_SLEEP) |
| **`appId`** | **camelCase دائماً**، في الـ query string (`?appId={branch}`) |
| غلاف الاستجابة | **`{OperationName}Result`** (نمط WCF DataContract) |
| محرّك التسلسل | **Moshi** (`.asLenient()`) |

> ⚠️ **`appId` = رقم الفرع** (`branchNumber`)، القيمة الافتراضية `"1"`.
> هذا ليس معرّف تطبيق ثابتاً بل قيمة قابلة للتغيير من الإعدادات.

---

## أ) المصادقة (Authentication)

### 1. `POST Login` — مسار تسجيل الدخول الفعلي ✅
**المصدر:** `model/AuthRepository.java` (loopj، المسار المُستخدم).

```
POST {base}Login          ← Capital L، بلا query params
Content-Type: application/json
Body (JSONObject مبني يدوياً):
{
  "username": "<اسم المستخدم>",
  "password": "<كلمة المرور>",
  "appId":    "<رقم الفرع>",     ← camelCase
  "secureId": "<معرّف الجهاز>"
}
```
**الاستجابة (نجاح):**
```json
{ "LoginResult": { ...كائن Users كامل... } }
```
انظر `../AUTH_FLOW.md` لتفاصيل `Users` ونظام الصلاحيات.

### 2. `POST UserAuth` — مسار بديل (غير مستخدم في الإنتاج) ⚠️
**المصدر:** `ApiService.java` (Retrofit) + `entities/AuthData.java`.
```
POST {base}UserAuth
Content-Type: application/json
Body (Moshi AuthData):  { "appid", "secureId", "username", "password" }
                          ↑ lowercase appid (فرق مهم — هذا المسار فقط)
الاستجابة: AccessToken { access_token, expires_in, refresh_token, token_type }
```
> هذا هو مصدر الخلط التاريخي. **لا تستخدم `appid` lowercase في `/Login`.**

### 3. `POST refresh` — تجديد التوكن
```
POST {base}refresh   (FormUrlEncoded)
Field: refresh_token={token}
الاستجابة: AccessToken
```
⚠️ **bug قديم:** `CustomAuthenticator` يلحق الحرف `"a"` بنهاية الـ
refresh token. **لا نُعيد إنتاج هذا الخطأ.**

### 4. `POST register` (FormUrlEncoded: name, email, password) — غير مستخدم.
### 5. `POST ReSetPassword` — إعادة تعيين كلمة المرور (موجود على WCF).

---

## ب) القراءات (Readings) — `model/ReadingRepository.java`

| العملية | Method | URL + Query | Body | غلاف الاستجابة |
|---|---|---|---|---|
| جلب القائمة | `GET` | `GetListReadingCounter?appId={a}&pageNumber={n}&pageSize={s}&name={q}` | — | `GetListReadingCounterResult[]` (مصفوفة `ItemReading`) |
| حفظ جديد | `POST` | `SaveReading?appId={a}` | `ItemReading` (Moshi JSON) | `SaveReadingResult` |
| تحديث | `PUT` | `UpdateReading?appId={a}&id={num}` | `ItemReading` (Moshi JSON) | `UpdateReadingResult` |
| حذف | `DELETE` | `DeleteReading?appId={a}&id={num}` | — | `DeleteReadingResult.Result` |

> `id` في التحديث/الحذف = `ItemReading.num` (رقم العداد، int).
> حقول `ItemReading` كاملة في `../entities/ENTITIES.md`.

---

## ج) السندات (Bonds — سندات القبض) — `model/BondsRepository.java`

| العملية | Method | URL + Query | Body | غلاف الاستجابة |
|---|---|---|---|---|
| جلب القائمة | `GET` | `GetListBonds?appId={a}&pageNumber={n}&pageSize={s}&name={q}` | — | `GetListBondsResult[]` (`ItemBonds`) |
| حفظ جديد | `POST` | `SaveBond?appId={a}` | `ItemBonds` (Moshi JSON) | `SaveBondResult` |
| تحديث | `PUT` | `UpdateBond?appId={a}&id={nmstnd}` | `ItemBonds` (Moshi JSON) | `UpdateBondResult` |
| حذف | `DELETE` | `DeleteBond?appId={a}&id={nmstnd}` | — | `DeleteBondResult.Result` |

> `id` في التحديث/الحذف = `ItemBonds.nmstnd` (String، رقم السند).

---

## د) مدفوعات السندات (Bond Payments — سندات الدفع) — `model/BondsPaymentRepository.java`

| العملية | Method | URL + Query | Body | غلاف الاستجابة |
|---|---|---|---|---|
| جلب القائمة | `GET` | `GetListBondsPayment?appId={a}&pageNumber={n}&pageSize={s}&name={q}` | — | `GetListBondsPaymentResult[]` |
| حفظ جديد | `POST` | `SaveBondPayment?appId={a}` | `ItemBonds` (نفس النوع!) | `SaveBondPaymentResult` |
| تحديث | `PUT` | `UpdateBondPayment?appId={a}&id={nmstnd}` | `ItemBonds` | `UpdateBondPaymentResult` |
| حذف | `DELETE` | `DeleteBondPayment?appId={a}&id={nmstnd}` | — | `DeleteBondPaymentResult` |

> ⚠️ **اكتشاف:** سندات الدفع تستخدم **نفس كيان `ItemBonds`** للحفظ
> والتحديث (وليس كياناً منفصلاً). الفرق فقط في الـ endpoint.

---

## هـ) القوائم المرجعية (Reference Lists) — `ApiService.java`

| Endpoint | Method | Query | الاستجابة |
|---|---|---|---|
| `GetCompanyData` | GET | — | `CompanyInfoResult` |
| `GetListAccounts` | GET | `?appId=` (QueryMap) | `AccountsResponse` |
| `GetListPlaces` | GET | QueryMap | `PlacesResponse` |
| `GetListGroup` | GET | QueryMap | `TGroipResponse` / `TblhResponse` |
| `GetListCurrency` | GET | — | `CurrencyResponse` |
| `GetListUsers` | GET | `?id=&appId=` | `UserResponse` |
| `GetListUserPlaces` | GET | — | `UserPlacesResponse` |

---

## و) التقارير (Reports) — `ApiService.java`

| Endpoint | Method | الاستجابة | الشاشة المقابلة |
|---|---|---|---|
| `GetListReadingCounter` (إعادة استخدام) | GET | `ReadingResponse` | تقرير القراءات |
| `GetRepReadingHeader` | GET | `RepReadingResponse` | repListReading |
| `GetRepBondsHeader` | GET | `BondsHeaderResponse` | repBondsReciept |
| `GetRepBalanceHeader` | GET | `GetRepBalanceHeaderResult` | repBalanceHeader |
| `GetRepBalanceDetails` | GET | `BalanceStateDetailsRespons` | repBalanceDetails |
| `GetRepBalanceDetailsByDate` | GET | `BalanceStateDetailsRespons` | repBalanceDetails (بتاريخ) |
| `GetRepBoxMove` | GET | `RepBoxMovesResponse` | repBoxMoves |
| `GetRepBoxMoveDetails` | GET | `RepBoxMovesDetailsResponse` | تفاصيل الصندوق |
| `GetRepExpenses` | GET | `RepExpensesResponse` | repExpenses |
| `GetAccountBalance` | GET | `Object` | رصيد حساب |
| `GetAccountBalanceInfo` | GET | `AccountBalanceResponse` | تفاصيل رصيد |
| `GetBondRecieptRcordNext` | GET | `Object` | الرقم التالي لسند القبض |
| `GetBondPaymentRecordNext` | GET | `Object` | الرقم التالي لسند الدفع |
| `report1` | GET | `List<Reports>` | تقرير عام |

---

## ز) ملخّص نمط الاستجابة (WCF Envelope)

كل عملية WCF تُرجع كائناً جذرياً اسمه `{OperationName}Result`:

```
GET  GetListReadingCounter → { "GetListReadingCounterResult": [ ... ] }
POST SaveReading           → { "SaveReadingResult": { ... } }
DELETE DeleteReading       → { "DeleteReadingResult": { "Result": "..." } }
POST Login                 → { "LoginResult": { ... Users ... } }
```

> **قاعدة عامة للترحيل:** كل mapper في app1 يجب أن يفكّ الغلاف
> `{OperationName}Result` أولاً قبل التحقق بـ Zod.
