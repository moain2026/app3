# app3 — تطبيق التحصيل المالي (Abbasi Tahseel)

تطبيق تحصيل ميداني (قراءة عدّادات + سندات قبض + تقارير + طباعة إيصالات) لعمّال
**شركة العباسي لتوليد الكهرباء التجارية**، يُبنى كبديل حديث للتطبيق الأصلي
(Android/Java) مع الحفاظ الكامل على عقد الخادم (.NET WCF) المُستخرَج من الـ APK.

## الحالة الحالية
🔵 **مرحلة التحليل والتخطيط مكتملة.** كل الاكتشافات والخطة موثّقة في مجلد
[`analysis/`](./analysis/README.md). ابدأ من
[التقرير التنفيذي](./analysis/05_reports/EXECUTIVE_SUMMARY.md).

## المرجعية الأساسية (غير قابلة للتفاوض)
- **مصدر الحقيقة = الـ APK الأصلي المُفكَّك** (189 ملف Java مرجعي محفوظ في
  `analysis/01_legacy_apk_analysis/decompiled_sources/`).
- أي تعارض في الفهم يُحسَم لصالح الكود الأصلي.
- لا افتراضات ولا "هلوسة كود" — أي غموض يُسأل عنه.

## الحزمة التقنية (المخطّطة لـ app3)
React Native 0.74.5 (bare) · TypeScript strict · WatermelonDB (offline-first) ·
Zustand · Axios · Zod · i18next (RTL/عربي) · ESC/POS (Datecs DPP-250) ·
GitHub Actions + Expo EAS.

## الخطوات التالية
راجع [خطة المعمارية والترحيل](./analysis/04_architecture_plan/README.md) —
المراحل M0→M3 ونقاط القرار المطلوبة قبل بدء كتابة الكود.
