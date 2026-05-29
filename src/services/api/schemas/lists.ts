/**
 * List/Reference Schemas — العباسي تحصيل
 *
 * Validates "list" endpoints that hydrate local reference tables:
 *   /GetCompanyData, /GetListAccounts, /GetListPlaces, /GetListGroup,
 *   /GetListUsers, /GetListCurrency, /GetListBonds, /GetListBondsPayment.
 */

import { z } from 'zod';

import {
  zBoolLoose,
  zEnvelope,
  zResultEnvelope,
  zIntLoose,
  zNumberLoose,
  zStringOrEmpty,
} from './common';

// ─── Company info (for receipt header) ────────────────────────────────────
export const CompanyInfoDtoSchema = z.object({
  id: zIntLoose.optional(),
  name_ar: zStringOrEmpty.optional(),
  name_en: zStringOrEmpty.optional(),
  phone: zStringOrEmpty.optional(),
  address: zStringOrEmpty.optional(),
  logo: zStringOrEmpty.optional(),
  footer: zStringOrEmpty.optional(),
});
export type CompanyInfoDto = z.infer<typeof CompanyInfoDtoSchema>;

// ─── Account ──────────────────────────────────────────────────────────────
// ⭐ Field names taken VERBATIM from entities/Accounts.java (v28) — ISS-12.
export const AccountDtoSchema = z.object({
  num: zIntLoose, // remote id / sequence
  name: zStringOrEmpty.default(''), // account name
  namet: zStringOrEmpty.optional(), // alias name
  namep: zStringOrEmpty.optional(), // parent/place name
  noadad: zStringOrEmpty.optional(), // meter/account code
  nog: zIntLoose.default(0), // group
  nomstlm: zIntLoose.default(0), // receiver/area
  notblh: zIntLoose.default(0), // book
  balance: zNumberLoose.default(0), // balance
  dain: zNumberLoose.default(0), // debit
  mden: zNumberLoose.default(0), // credit
  tel: zStringOrEmpty.optional(), // phone
  type: zIntLoose.default(0), // account type
});
export type AccountDto = z.infer<typeof AccountDtoSchema>;

// ─── Place / Group / Tblh share the same shape ────────────────────────────
export const PlaceDtoSchema = z.object({
  id: zIntLoose,
  code: zStringOrEmpty.optional(),
  name: zStringOrEmpty.default(''),
  parent_id: zIntLoose.optional(),
});
export type PlaceDto = z.infer<typeof PlaceDtoSchema>;

export const TGroupDtoSchema = z.object({
  id: zIntLoose,
  code: zStringOrEmpty.optional(),
  name: zStringOrEmpty.default(''),
  place_id: zIntLoose.optional(),
});
export type TGroupDto = z.infer<typeof TGroupDtoSchema>;

export const TblhDtoSchema = z.object({
  id: zIntLoose,
  code: zStringOrEmpty.optional(),
  name: zStringOrEmpty.default(''),
  group_id: zIntLoose.optional(),
  place_id: zIntLoose.optional(),
});
export type TblhDto = z.infer<typeof TblhDtoSchema>;

// ─── User (full profile from /GetListUsers) ───────────────────────────────
export const UserDtoSchema = z.object({
  id: zIntLoose,
  username: zStringOrEmpty.default(''),
  name: zStringOrEmpty.optional(),
  phone: zStringOrEmpty.optional(),
  email: zStringOrEmpty.optional(),
  DE: zBoolLoose.default(false),
  ED: zBoolLoose.default(false),
  REP: zBoolLoose.default(false),
  S_K: zBoolLoose.default(false),
  S_S: zBoolLoose.default(false),
  SYS: zBoolLoose.default(false),
  NOA: zIntLoose.default(0),
  NOU: zIntLoose.default(0),
});
export type UserDto = z.infer<typeof UserDtoSchema>;

// ─── Currency ─────────────────────────────────────────────────────────────
export const CurrencyDtoSchema = z.object({
  id: zIntLoose,
  code: zStringOrEmpty.default(''),
  name: zStringOrEmpty.default(''),
  symbol: zStringOrEmpty.optional(),
  exchange_rate: zNumberLoose.default(1),
  is_default: zBoolLoose.default(false),
});
export type CurrencyDto = z.infer<typeof CurrencyDtoSchema>;

// ─── Bond / Bond payment ──────────────────────────────────────────────────
export const BondDtoSchema = z.object({
  id: zIntLoose,
  bond_no: zIntLoose,
  bond_type: zStringOrEmpty.default(''),
  account_id: zIntLoose.optional(),
  account_name: zStringOrEmpty.optional(),
  currency_id: zIntLoose.optional(),
  amount: zNumberLoose.default(0),
  amount_paid: zNumberLoose.default(0),
  notes: zStringOrEmpty.optional(),
  bond_date: zStringOrEmpty.optional(), // legacy uses string; mapper parses
});
export type BondDto = z.infer<typeof BondDtoSchema>;

export const BondPaymentDtoSchema = z.object({
  id: zIntLoose,
  bond_id: zIntLoose,
  bond_no: zIntLoose,
  amount: zNumberLoose.default(0),
  payment_method: zStringOrEmpty.optional(),
  reference_no: zStringOrEmpty.optional(),
  notes: zStringOrEmpty.optional(),
  payment_date: zStringOrEmpty.optional(),
});
export type BondPaymentDto = z.infer<typeof BondPaymentDtoSchema>;

// ─── List response wrappers ───────────────────────────────────────────────
// The REAL WCF wire shape wraps every list under "{Operation}Result"
// (e.g. { "GetListAccountsResult": [...] }). Confirmed from *Response.java —
// see ISS-11. Priority: (1) *Result envelope → (2) { data } → (3) bare array.
const listOf = <T extends z.ZodTypeAny>(item: T) =>
  z.union([
    zResultEnvelope(z.array(item)),
    zEnvelope(z.array(item)),
    z.array(item),
  ]);

export const CompanyDataResponseSchema = z.union([
  zResultEnvelope(CompanyInfoDtoSchema),
  zEnvelope(CompanyInfoDtoSchema),
  CompanyInfoDtoSchema,
]);
export const AccountListResponseSchema = listOf(AccountDtoSchema);
export const PlaceListResponseSchema = listOf(PlaceDtoSchema);
export const TGroupListResponseSchema = listOf(TGroupDtoSchema);
export const TblhListResponseSchema = listOf(TblhDtoSchema);
export const UserListResponseSchema = listOf(UserDtoSchema);
export const CurrencyListResponseSchema = listOf(CurrencyDtoSchema);
export const BondListResponseSchema = listOf(BondDtoSchema);
export const BondPaymentListResponseSchema = listOf(BondPaymentDtoSchema);
