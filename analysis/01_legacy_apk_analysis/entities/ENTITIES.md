# ENTITIES — الكيانات (DTOs) بحقولها الدقيقة

> مستخرَجة حرفياً من `entities/*.java` (Moshi `@Json` annotations).
> **هذه الأسماء مقدّسة** — الخادم يعتمد عليها حرفياً في الطلبات
> والاستجابات. لا تُغيَّر أبداً في طبقة الـ wire.

## ItemReading — جسم `SaveReading` / `UpdateReading` ⭐

| الحقل (`@Json`) | النوع Java | الوصف |
|---|---|---|
| `num` | `int` | رقم العداد / المشترك (= `id` في URL) |
| `name` | `String` | اسم المشترك (عربي) |
| `namet` | `String` | الاسم المُترجَم/اللاتيني |
| `ind` | `int` | نوع/مؤشر القراءة (getter اسمه `getType`) |
| `nomstlm` | **`int`** | رقم المستلم ⚠️ (int وليس String) |
| `notblh` | **`long`** | رمز التابلة/المجموعة ⚠️ (long) |
| `noadad` | `String` | العدد |
| `nog` | **`int`** | رمز المجموعة الفرعية ⚠️ (int وليس String) |
| `ks` | **`double`** | القراءة السابقة ⚠️ (double وليس int) |
| `kh` | **`double`** | الاستهلاك ⚠️ (double) |
| `cas` | `int` | المبلغ المستحق |
| `asts` | **`double`** | علم الحالة ⚠️ (double) |

> 🔴 **تصحيح حرج لوثائق app1:** أنواع `ks/kh/asts` هي `double`،
> و`nomstlm/nog` هي `int`، و`notblh` هي `long`. وثيقة
> `LEGACY_JAVA_MAP.md` في app1 وصفت `ks/kh/cas/asts` كلها كـ `int`
> و`nomstlm/notblh/nog` كـ `String` — **هذا غير دقيق**. يجب مطابقة
> schema القراءات في app1 مع هذه الأنواع الفعلية.

## ItemBonds — جسم `SaveBond`/`UpdateBond`/`SaveBondPayment`/`UpdateBondPayment` ⭐

| الحقل (`@Json`) | النوع | ملاحظة |
|---|---|---|
| `account` | `Accounts` | كائن متداخل |
| `currency` | `Currency` | كائن متداخل |
| `notes2` (→ `bin`) | `String` | الاسم الداخلي للحقل `bin` |
| `branchid` | `String` | |
| `cas` | `int` | |
| `currencyid` | `int` | |
| `currencyname` | `String` | |
| `dain` | `double` | مدين |
| `equal` | `double` | المعادل |
| `finalbalance` | `double` | (بلا `@Json` — اسم الحقل كما هو) |
| `mdate` | `String` | تاريخ |
| `mden` | `double` | دائن |
| `name` | `String` | |
| `name_s` | `String` | |
| `nmstnd` | `String` | **رقم السند (= `id` في URL للتحديث/الحذف)** |
| `notes` | `String` | البيان |
| `notes_box` | `String` | |
| `nref` | `String` | |
| `nref_docno` | `String` | |
| `num` | `int` | |
| `num_s` | `int` | |
| `price_trans` | `double` | سعر الصرف |
| `balance` (→ `rsed`) | `double` | الرصيد |
| `type` | `int` | |
| `userid` | `String` | |

## Users — استجابة `/Login` (داخل `LoginResult`) ⭐

| الحقل (`@Json`) | النوع | الوصف |
|---|---|---|
| `NAME_U` | `String` | اسم المستخدم |
| `PASS` | `String` | كلمة المرور (مُعادة من الخادم!) |
| `NOU` | `int` | رقم المستخدم |
| `NOA` | `int` | رقم الحساب |
| `access_token` | `String` | توكن الوصول |
| `date_server` | `String` | تاريخ الخادم (افتراضي `1980/01/01`) |
| `error_no` | `int` | رقم الخطأ (0 = نجاح) |
| `error_msg` | `String` | رسالة الخطأ |
| `ED` | `int` | علم صلاحية: تعديل (Edit) |
| `DE` | `int` | علم صلاحية: حذف (Delete) |
| `S_K` | `int` | علم صلاحية: السندات (قبض) |
| `S_S` | `int` | علم صلاحية: السندات (دفع) |
| `REP` | `int` | علم صلاحية: التقارير |
| `SYS` | `int` | علم صلاحية: مدير النظام (يفتح كل شيء) |

> نظام الصلاحيات الكامل في `../AUTH_FLOW.md`.

## AccessToken — استجابة `/UserAuth` و `/refresh`

| الحقل | النوع |
|---|---|
| `access_token` | `String` |
| `expires_in` | `int` |
| `refresh_token` | `String` |
| `token_type` | `String` |

## AuthData — جسم `/UserAuth` فقط (⚠️ lowercase appid)

| الحقل (`@Json`) | النوع |
|---|---|
| `username` | `String` |
| `password` | `String` |
| `appid` | `String` ← **lowercase، هذا المسار فقط** |
| `secureId` | `String` |

## Currency — `GetListCurrency`

| الحقل | النوع | الوصف |
|---|---|---|
| `num` | `int` | المعرّف |
| `name` | `String` | اسم العملة |
| `fls` | `String` | الفئة/الرمز |
| `sars` | `double` | سعر الصرف |

## Accounts — `GetListAccounts` (وكائن متداخل في ItemBonds)

| الحقل | النوع | | الحقل | النوع |
|---|---|---|---|---|
| `num` | `int` | | `balance` | `double` |
| `name` | `String` | | `dain` | `double` |
| `namet` | `String` | | `mden` | `double` |
| `namep` | `String` | | `nog` | `int` |
| `tel` | `String` | | `nomstlm` | `int` |
| `type` | `int` | | `notblh` | `int` |
| `noadad` | `String` | | | |

## Places — `GetListPlaces`
| `num` | `String` | `name` | `String` |
(ملاحظة: `num` هنا String وليس int)

## TGroup — `GetListGroup`
| `num` | `String` | `name` | `String` | `nomk2` | `String` |

## Tblh — `GetListGroup` (نفس الـ endpoint، تحويل مختلف)
| `num` | `int` | `name` | `String` |

## CompanyInfoResult — `GetCompanyData` (للطباعة)

| الحقل (`@Json`) | الاسم Java | الوصف |
|---|---|---|
| `compname` | `compname` | اسم الشركة |
| `compaddress` | `address` | العنوان |
| `comptelephone` | `telephone` | الهاتف |
| `compactive` | `active` | حالة التفعيل |

## كيانات التقارير (Reports)

### RepReading (تقرير القراءات)
| `num` String | `name` String | `ast` String |

### BalanceState (ميزان الحسابات)
| `num` String | `name` String | `type` int | `mdate` String |
| `dain` double | `mden` double | `dain2` double | `mden2` double | `balance` double |

### BondsHeader (تقرير السندات)
| `num` String | `name` String | `type` int | `mdate` String |
| `dain` double | `mden` double | `balance` double |
| `currencyid` String | `currencyname` String |

### RepBoxMoves (حركة الصندوق)
| `num` String | `name` String | `mdate` String |
| `dain` double | `mden` double | `balance` double | `fbalance` double |
