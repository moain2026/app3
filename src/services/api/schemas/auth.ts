/**
 * Auth Schemas — العباسي تحصيل
 *
 * Validates payloads for /login, /UserAuth, /refresh, /register.
 *
 * Reference: AuthData.java, AccessToken.java, Users.java in the legacy app.
 */

import { z } from 'zod';

import { zBoolLoose, zIntLoose, zStringOrEmpty } from './common';

// ─── Request payloads ─────────────────────────────────────────────────────

/**
 * /Authenticate — JSON `{ "Password", "User", "appId" }`.
 *
 * ⚠️ Wire-field casing matters — this is the EXACT shape documented by the
 * live WCF Service Explorer at:
 *     http://<host>:3000/electric/Authenticate
 *
 * The DataContract is:
 *     <Credentials xmlns="http://schemas.datacontract.org/2004/07/MProgService.models">
 *       <Password>...</Password>
 *       <User>...</User>
 *       <appId>...</appId>
 *     </Credentials>
 *
 * Note that ALL other WCF endpoints use `appId` (camelCase) in their query
 * strings — e.g. `GetCompanyInfo?appId={APPID}` — so this naming is
 * consistent with the rest of the surface.
 *
 * There is NO secureId field in this contract.
 */
export const AuthenticateRequestSchema = z.object({
  Password: z.string().min(1),
  User: z.string().min(1),
  appId: z.string().min(1),
});

/**
 * /login — form-urlencoded { username, password, appid? }
 */
export const LoginRequestSchema = z.object({
  username: z.string().min(1),
  password: z.string().min(1),
  appid: z.string().optional(),
});

/**
 * /UserAuth — JSON body { username, password, appid, secureId }
 * `secureId` is generated SILENTLY by the new app (replaces Defence XOR per ADR-004).
 */
export const UserAuthRequestSchema = z.object({
  username: z.string().min(1),
  password: z.string().min(1),
  appid: z.string(),
  secureId: z.string(),
});

/**
 * /refresh — form-urlencoded { refresh_token }
 * NOTE: We send refresh_token as-is. The legacy app erroneously appended "a"
 * (HtmlTags.A from iText) — see CustomAuthenticator.java line 41. We do NOT
 * reproduce that bug.
 */
export const RefreshRequestSchema = z.object({
  refresh_token: z.string().min(1),
});

/**
 * /register — form-urlencoded { name, email, password }
 */
export const RegisterRequestSchema = z.object({
  name: z.string().min(1),
  email: z.string().email(),
  password: z.string().min(6),
});

// ─── Response payloads ────────────────────────────────────────────────────

/**
 * /Authenticate response.
 *
 * Per the WCF Help page, the body is a JSON **string** literal — i.e. the
 * raw HTTP body is `"some-token"` (with quotes, no surrounding object).
 *
 * Axios decodes this into a JavaScript string. An empty string ("") is
 * treated as a failed authentication (server returns an empty quoted
 * string when credentials do not match — typical .NET WCF pattern).
 *
 * ⚠️ REAL-WORLD VARIANTS observed in the field — we normalise ALL of them
 * down to the bare token string so the login flow never rejects a valid
 * token on a cosmetic wrapper:
 *
 *   1. Bare string literal      → `"bUqox…=="`                (axios → string)
 *   2. Raw text (no quotes)     → `bUqox…==`                  (axios → string,
 *                                  base64 isn't valid JSON so axios keeps it raw)
 *   3. WCF *Result envelope     → `{ "AuthenticateResult": "bUqox…==" }`
 *   4. AccessToken object       → `{ "access_token": "bUqox…==" }`
 *
 * `z.preprocess` unwraps (3)/(4)/extracts the inner string, trims stray
 * whitespace + surrounding quotes, then the inner `z.string().min(1)`
 * guarantees a non-empty token. An empty/blank token ⇒ failed auth.
 */
export const AuthenticateResponseSchema = z.preprocess((raw) => {
  // Unwrap object envelopes first.
  if (raw != null && typeof raw === 'object' && !Array.isArray(raw)) {
    const obj = raw as Record<string, unknown>;
    // (3) any key ending in `Result` (WCF) — e.g. AuthenticateResult.
    const resultKey = Object.keys(obj).find((k) => /Result$/i.test(k));
    if (resultKey != null && typeof obj[resultKey] === 'string') {
      raw = obj[resultKey];
    } else if (typeof obj.access_token === 'string') {
      // (4) AccessToken-style payload.
      raw = obj.access_token;
    } else if (typeof obj.token === 'string') {
      raw = obj.token;
    }
  }
  if (typeof raw === 'string') {
    // Trim whitespace + a single pair of wrapping double-quotes (which can
    // survive when the server double-encodes the JSON string literal).
    let s = raw.trim();
    if (s.length >= 2 && s.startsWith('"') && s.endsWith('"')) {
      s = s.slice(1, -1).trim();
    }
    return s;
  }
  return raw;
}, z.string().min(1, 'auth.authenticate.emptyToken'));

/**
 * AccessToken response from /UserAuth, /refresh, /register.
 *  { access_token, refresh_token, token_type?, expires_in? }
 */
export const AccessTokenResponseSchema = z.object({
  access_token: z.string().min(1),
  refresh_token: z.string().min(1),
  token_type: zStringOrEmpty.optional(),
  expires_in: zIntLoose.optional(),
});

/**
 * /login response — legacy returns the Users object directly (NOT wrapped).
 * Mirrors Users.java fields including permission flags.
 *
 * Tokens are nested under the Users entity in some legacy variants — we
 * accept both shapes.
 */
export const LoginUserResponseSchema = z.object({
  // Identity
  id: zIntLoose.optional(),
  username: zStringOrEmpty.optional(),
  name: zStringOrEmpty.optional(),
  email: zStringOrEmpty.optional(),
  phone: zStringOrEmpty.optional(),

  // Permission flags (legacy field names preserved 1:1)
  DE: zBoolLoose.optional(),
  ED: zBoolLoose.optional(),
  REP: zBoolLoose.optional(),
  S_K: zBoolLoose.optional(),
  S_S: zBoolLoose.optional(),
  SYS: zBoolLoose.optional(),
  NOA: zIntLoose.optional(),
  NOU: zIntLoose.optional(),

  // Embedded tokens (when the server returns them on /login)
  access_token: zStringOrEmpty.optional(),
  refresh_token: zStringOrEmpty.optional(),
});

export type AuthenticateRequest = z.infer<typeof AuthenticateRequestSchema>;
export type AuthenticateResponse = z.infer<typeof AuthenticateResponseSchema>;
export type LoginRequest = z.infer<typeof LoginRequestSchema>;
export type UserAuthRequest = z.infer<typeof UserAuthRequestSchema>;
export type RefreshRequest = z.infer<typeof RefreshRequestSchema>;
export type RegisterRequest = z.infer<typeof RegisterRequestSchema>;
export type AccessTokenResponse = z.infer<typeof AccessTokenResponseSchema>;
export type LoginUserResponse = z.infer<typeof LoginUserResponseSchema>;
