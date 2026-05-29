# AUTH_FLOW — منطق المصادقة الكامل ونظام الصلاحيات

> مُستخرَج من `LoginActivity.java`, `LoginPresenter.java`,
> `model/AuthRepository.java`, `entities/Users.java`, `common/AppConfig.java`.

## 1. بناء الـ Base URL (مؤكَّد 100%)

```java
// common/AppConfig.java
public String getBaseUrl() {
    return "http://" + this.baseUrl + ":3000/electric/";
}
```

- **البورت `3000` ثابت (hardcoded).**
- **المسار `/electric/` ثابت (hardcoded).**
- **فقط الـ IP** (`this.baseUrl`) قابل للتغيير، يُقرأ من إعدادات المستخدم.
- القيمة الافتراضية للـ IP: `192.168.0.100` (من `TAPreferences.getHostingIP`).
- ⚠️ `TAPreferences.getHostingPort` الافتراضي = `9002` لكنه **غير مستخدم**
  في بناء URL الفعلي — `getBaseUrl()` يتجاهله ويستخدم 3000 ثابتاً.
- يوجد سيرفر احتياطي `HostingIP2 = 127.0.0.1` (إرث، غير مفعّل).

→ **في الإنتاج:** المستخدم يضع IP الـ Tailscale (`100.87.131.115`)،
فيصبح الـ URL `http://100.87.131.115:3000/electric/`. **هذا يطابق app1.**

## 2. تدفّق تسجيل الدخول (مؤكَّد من LoginActivity)

```
1. _baseUrl  = TAPreferences.getHostingIP()        → IP فقط
2. _appId    = prefs["appId"]  (افتراضي "1")        → رقم الفرع
3. secureId  = Utils.GetDeviceId() = Defence.getDeviceId()
4. authData  = { username, password, appId, secureId }
5. POST {base}Login   مع جسم JSON يدوي (AuthRepository)
6. الاستجابة: { "LoginResult": { ...Users... } }
7. منطق النجاح/الفشل (LoginPresenter):
      if (users.error_no != 0)  → onFailed(users)   ← الخطأ يأتي في error_no
      else                      → onSucceed(users)   ← نجاح
```

> 🔴 **قاعدة حاسمة:** النجاح/الفشل يُحدَّد بـ **`error_no`** داخل
> `LoginResult`، وليس بحالة HTTP. الخادم قد يردّ HTTP 200 مع
> `error_no != 0` (فشل منطقي).

## 3. نظام الصلاحيات (HakAccess) — مُستخرَج حرفياً من Users.java ⭐

عند نجاح الدخول، يُبنى 8 كائنات `HakAccess` من 6 أعلام رقمية في `Users`.
كل كائن له: `menuName`, `read`, `update`, `delete`. القاعدة:
**`SYS > 0` يفتح كل شيء.**

| menuName (التقرير) | read (قراءة) | update (تعديل) | delete (حذف) |
|---|---|---|---|
| `repBondsReciept` | `S_K>0 \|\| SYS>0` | `ED>0 \|\| SYS>0` | `DE>0 \|\| SYS>0` |
| `repBondsPayment` | `S_S>0 \|\| SYS>0` | `ED>0 \|\| SYS>0` | `DE>0 \|\| SYS>0` |
| `repBalanceHeader` | `REP>0 \|\| S_K>0 \|\| SYS>0` | `ED>0 \|\| SYS>0` | `DE>0 \|\| SYS>0` |
| `repBalanceDetails` | `REP>0 \|\| S_K>0 \|\| SYS>0` | `ED>0 \|\| SYS>0` | `DE>0 \|\| SYS>0` |
| `repCollectorMony` | `SYS>0` | `ED>0 \|\| SYS>0` | `DE>0 \|\| SYS>0` |
| `repListReading` | `SYS>0` | `ED>0 \|\| SYS>0` | `DE>0 \|\| SYS>0` |
| `repBoxMoves` | `SYS>0` | `ED>0 \|\| SYS>0` | `DE>0 \|\| SYS>0` |
| `repExpenses` | `SYS>0` | `ED>0 \|\| SYS>0` | `DE>0 \|\| SYS>0` |

### تفسير الأعلام
| العلم | المعنى |
|---|---|
| `SYS` | مدير النظام — يفتح كل القراءة/التعديل/الحذف |
| `ED` | صلاحية التعديل (Edit) عبر كل القوائم |
| `DE` | صلاحية الحذف (Delete) عبر كل القوائم |
| `S_K` | صلاحية قراءة سندات القبض + الموازين |
| `S_S` | صلاحية قراءة سندات الدفع |
| `REP` | صلاحية قراءة تقارير الموازين |

> 🟢 **للترحيل في app1:** يجب إنشاء طبقة `permissionsResolver` تأخذ
> كائن `Users` وتُنتج خريطة الصلاحيات الثمانية بهذه القواعد بالضبط.
> الشاشات تُخفي/تُعطّل أزرار التعديل/الحذف بناءً عليها.

## 4. تخزين الجلسة (SharedPreferences)

من `Users(SharedPreferences)` constructor — المفاتيح المخزّنة محلياً:
```
DE, ED, S_K, S_S, SYS, REP, NOU, NOA,
user_name, user_password, date_server, access_token
```
> القيم الافتراضية: كلها 0، `date_server="1980/01/01"`, `access_token=""`.

## 5. الـ Headers

| Header | القيمة |
|---|---|
| `Authorization` | `Bearer {access_token}` (في الطلبات المحمية) |
| `Content-Type` | `application/json` (UTF-8، POST/PUT) |
| `Accept` | `application/json` |

## 6. ما يجب تطبيقه في app1 (ملخص تنفيذي)

1. ✅ `appId` camelCase في جسم `/Login` (app1 يطبّقه).
2. 🔴 فكّ غلاف `LoginResult` ثم التحقق بـ `error_no === 0`.
3. 🔴 استخراج الصلاحيات الثمانية وتخزينها في `authStore`.
4. 🟡 `secureId` = خوارزمية `Defence.getDeviceId` (انظر `SECURITY_DEFENCE.md`).
5. 🟢 الـ Base URL = `http://{IP}:3000/electric/` (app1 يطبّقه).
