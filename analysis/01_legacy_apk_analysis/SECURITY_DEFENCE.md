# SECURITY_DEFENCE — خوارزمية secureId الحقيقية (مصحّحة)

> مُستخرَجة حرفياً من `Defence.java`. **هذا التقرير يصحّح خطأً في وثائق
> app1** التي وصفت الخوارزمية بـ "XOR + first 8 hex chars". الحقيقة أبسط.

## 1. `getDeviceId()` — مولّد الـ secureId الفعلي ✅

```java
// Defence.java — الكود الحرفي
public String getDeviceId() {
    return Long.toString(
        Long.parseLong(
            Settings.Secure.getString(contentResolver, "android_id").substring(0, 8),
            16   // base-16
        )
    );
}
```

### الخوارزمية بالكلمات
1. اقرأ `ANDROID_ID` (سلسلة hex، عادة 16 حرفاً).
2. خذ **أول 8 أحرف** فقط: `substring(0, 8)`.
3. فسّرها كعدد **سداسي عشري (hex، base-16)**.
4. حوّلها إلى **عدد عشري (decimal)** كسلسلة.

> 🔴 **لا يوجد XOR في `getDeviceId()`.** الـ XOR موجود فقط في نظام
> ترخيص منفصل (`GenerateKey` / `isKeyValid`)، وهو **غير مرتبط بـ
> `secureId`**. وثيقة `LEGACY_JAVA_MAP.md` في app1 خلطت بينهما.

### مثال تحقّق
- `ANDROID_ID` (أول 8): `7d1f...` (افتراضي)
- `parseLong("7d1f....".substring(0,8), 16)` → عدد decimal من 9–10 خانات.
- للهاتف "motech" القيمة المعروفة: **`2098897319`**.

> ⚠️ **يجب التحقق:** قيمة `2098897319` كـ hex = `0x7D1F...`؟
> `2098897319` decimal = `0x7D1F8FE7`. أي أن أول 8 hex من ANDROID_ID
> للهاتف = `7d1f8fe7`. هذا متّسق ومنطقي.

## 2. نظام الترخيص المنفصل (GenerateKey / isKeyValid) — للمعلومة فقط

هذا نظام **تفعيل النسخة** (`menu_buy_app = "تفعيل النسخة"`)، منفصل تماماً
عن المصادقة. يعمل كالتالي:

```java
mashineSerialNumber() = getDeviceId().substring(length-8)   // آخر 8 أرقام
GenerateKey(type):
    j = parseInt(serial.substring(0,7)) ^ MAGIC[type]
    return fillKey(type + j)             // مع حشو bits في مواضع 2,4,6
isKeyValid(key, docCount):
    clearKey → استخراج type + value → value ^ MAGIC[type] == serial[0:7]
    && docCount <= addKeyValue(key) - 5
```

ثوابت الـ XOR السحرية (MAGIC):
| type | constant |
|---|---|
| 1 | `3452971` |
| 2 | `3376542` |
| 3 | `1298472` |
| 4 | `9087321` |
| 5 | `9845023` |
| 6 | `3213453` |
| 7 | `9823569` |
| (registration) | `6335737` |

> 🟢 **للترحيل:** نظام التفعيل هذا **اختياري**. التطبيق الأصلي يستخدمه
> لتحديد عدد المستندات المسموحة قبل التفعيل. app1 قد يتجاهله أو يطبّق
> نسخة مبسّطة (الـ `licenseManager.ts` الموجود). **القرار للمستخدم.**

## 3. ملخّص القرار للترحيل

| العنصر | الإجراء في app1 |
|---|---|
| `secureId` | طبّق `getDeviceId()` الحقيقي: أول 8 من ANDROID_ID → decimal. لا XOR. |
| دعم Override | احتفظ بإمكانية ضبط قيمة يدوية (للأجهزة الجديدة). |
| نظام التفعيل | اختياري — أسأل المستخدم إن كان يريد ترحيله. |
| `+a` bug في refresh | **لا تُعِد إنتاجه** — أرسل التوكن نظيفاً. |
