/**
 * Common Zod Schemas — العباسي تحصيل
 *
 * Reusable building blocks. The legacy backend is fast-and-loose with types:
 *  • Numbers occasionally arrive as strings (e.g. "150" instead of 150).
 *  • Empty strings used in place of nulls.
 *  • Booleans sometimes encoded as "0"/"1" or "true"/"false".
 *
 * These coercion helpers absorb that messiness so the rest of the app sees
 * well-typed data.
 */

import { z } from 'zod';

/**
 * Coerce common server representations into a JS number.
 *  - numbers stay as-is
 *  - "123" → 123
 *  - "123.45" → 123.45
 *  - "" or "null" → undefined (then `.optional()` handles it)
 *  - true/false → 1/0
 */
export const zNumberLoose = z.preprocess(v => {
  if (v == null) return undefined;
  if (typeof v === 'number') return Number.isFinite(v) ? v : undefined;
  if (typeof v === 'boolean') return v ? 1 : 0;
  if (typeof v === 'string') {
    const trimmed = v.trim();
    if (trimmed === '' || trimmed.toLowerCase() === 'null') return undefined;
    const n = Number(trimmed);
    return Number.isFinite(n) ? n : undefined;
  }
  return undefined;
}, z.number());

/**
 * Loose integer — same as zNumberLoose but truncates to int.
 */
export const zIntLoose = z.preprocess(v => {
  if (v == null) return undefined;
  if (typeof v === 'number') return Number.isFinite(v) ? Math.trunc(v) : undefined;
  if (typeof v === 'boolean') return v ? 1 : 0;
  if (typeof v === 'string') {
    const trimmed = v.trim();
    if (trimmed === '' || trimmed.toLowerCase() === 'null') return undefined;
    const n = Number(trimmed);
    return Number.isFinite(n) ? Math.trunc(n) : undefined;
  }
  return undefined;
}, z.number().int());

/**
 * Loose boolean — accepts: true/false, "true"/"false", 1/0, "1"/"0", "yes"/"no".
 */
export const zBoolLoose = z.preprocess(v => {
  if (typeof v === 'boolean') return v;
  if (typeof v === 'number') return v !== 0;
  if (typeof v === 'string') {
    const s = v.trim().toLowerCase();
    if (s === 'true' || s === '1' || s === 'yes') return true;
    if (s === 'false' || s === '0' || s === 'no' || s === '') return false;
  }
  return false;
}, z.boolean());

/**
 * Empty string → undefined, then string. Useful for optional text fields.
 */
export const zStringOrEmpty = z.preprocess(v => {
  if (v == null) return undefined;
  if (typeof v === 'string' && v.trim() === '') return undefined;
  return v;
}, z.string());

/**
 * Date-ish: server uses 'YYYY-MM-DD HH:mm:ss' or ISO; we coerce to Date.
 */
export const zDateLoose = z.preprocess(v => {
  if (v == null) return undefined;
  if (v instanceof Date) return v;
  if (typeof v === 'string' && v.trim() !== '') {
    // Support both 'YYYY-MM-DD HH:mm:ss' (legacy) and ISO 8601.
    const normalized = v.includes('T') ? v : v.replace(' ', 'T');
    const d = new Date(normalized);
    return Number.isNaN(d.getTime()) ? undefined : d;
  }
  if (typeof v === 'number') {
    const d = new Date(v);
    return Number.isNaN(d.getTime()) ? undefined : d;
  }
  return undefined;
}, z.date());

/**
 * Generic envelope that legacy endpoints sometimes wrap responses in:
 *   { success: true, data: [...], message: "..." }
 * Some endpoints return the bare array — we handle both at the mapper layer.
 */
export const zEnvelope = <T extends z.ZodTypeAny>(inner: T) =>
  z.object({
    success: zBoolLoose.optional(),
    data: inner,
    message: zStringOrEmpty.optional(),
  });

/**
 * ⭐ WCF "{Operation}Result" envelope — THE REAL legacy wire shape.
 *
 * Confirmed from v28 decompiled source (`*Response.java` entities):
 *   • GetListReadingCounter → { "GetListReadingCounterResult": [...] }
 *   • GetListBonds          → { "GetListBondsResult": [...] }
 *   • GetListAccounts       → { "GetListAccountsResult": [...] }
 *   • DeleteReading         → { "DeleteReadingResult": { "Result": "true" } }
 *
 * The .NET WCF runtime auto-wraps every operation's payload under a key named
 * exactly "<OperationName>Result". This helper unwraps that key into `inner`.
 *
 * Use `zResultEnvelope(inner)` to accept ANY single `*Result` key, OR
 * `zResultEnvelopeNamed('GetListBondsResult', inner)` to pin the exact key.
 *
 * Reference: SCREEN_ANALYSIS_OPERATIONS.md §1.3/§2.3, ISS-11.
 */
export const zResultEnvelopeNamed = <T extends z.ZodTypeAny>(
  resultKey: string,
  inner: T,
) =>
  z.preprocess((v: unknown) => {
    if (v != null && typeof v === 'object' && !Array.isArray(v)) {
      const obj = v as Record<string, unknown>;
      if (resultKey in obj) return obj[resultKey];
    }
    return v;
  }, inner);

/**
 * Unwraps ANY single key ending in "Result" (case-sensitive) into `inner`.
 * Safest default when the exact operation name isn't pinned — picks the first
 * key matching /Result$/. Falls through untouched if no such key exists
 * (so bare arrays / already-unwrapped payloads still validate).
 */
export const zResultEnvelope = <T extends z.ZodTypeAny>(inner: T) =>
  z.preprocess((v: unknown) => {
    if (v != null && typeof v === 'object' && !Array.isArray(v)) {
      const obj = v as Record<string, unknown>;
      const resultKey = Object.keys(obj).find(k => /Result$/.test(k));
      if (resultKey) return obj[resultKey];
    }
    return v;
  }, inner);
