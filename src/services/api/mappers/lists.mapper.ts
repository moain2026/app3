/**
 * List Mappers — العباسي تحصيل
 *
 * Convert reference-data DTOs into clean domain models.
 * Used by the sync engine when pulling from the server into WatermelonDB.
 */

import { z } from 'zod';

import {
  AccountDtoSchema,
  AccountListResponseSchema,
  BondDtoSchema,
  BondListResponseSchema,
  BondPaymentDtoSchema,
  BondPaymentListResponseSchema,
  CompanyDataResponseSchema,
  CompanyInfoDtoSchema,
  CurrencyDtoSchema,
  CurrencyListResponseSchema,
  PlaceDtoSchema,
  PlaceListResponseSchema,
  TGroupDtoSchema,
  TGroupListResponseSchema,
  TblhDtoSchema,
  TblhListResponseSchema,
  UserDtoSchema,
  UserListResponseSchema,
} from '../schemas/lists';

// ─── Generic envelope-unwrapper ───────────────────────────────────────────

/**
 * Unwrap either a bare array or a `{ data: [...] }` envelope.
 * `itemSchema` re-validates each item defensively.
 */
function unwrapList<T extends z.ZodTypeAny>(
  responseSchema: z.ZodTypeAny,
  itemSchema: T,
  raw: unknown,
): z.infer<T>[] {
  const parsed = responseSchema.parse(raw) as unknown;
  const list = Array.isArray(parsed)
    ? parsed
    : (parsed as { data: unknown }).data;
  return z.array(itemSchema).parse(list);
}

// ═══════════════════════════════════════════════════════════════════════════
// Company info
// ═══════════════════════════════════════════════════════════════════════════

export interface CompanyInfoDomain {
  remoteId: number | null;
  nameAr: string;
  nameEn: string | null;
  phone: string | null;
  address: string | null;
  logoUrl: string | null;
  footerText: string | null;
}

export function parseCompanyInfo(raw: unknown): CompanyInfoDomain {
  const parsed = CompanyDataResponseSchema.parse(raw);
  const dto =
    'data' in (parsed as object) && (parsed as { data: unknown }).data
      ? CompanyInfoDtoSchema.parse((parsed as { data: unknown }).data)
      : CompanyInfoDtoSchema.parse(parsed);
  return {
    remoteId: dto.id ?? null,
    nameAr: dto.name_ar ?? 'شركة العباسي لتوليد الكهرباء التجارية',
    nameEn: dto.name_en ?? null,
    phone: dto.phone ?? null,
    address: dto.address ?? null,
    logoUrl: dto.logo ?? null,
    footerText: dto.footer ?? null,
  };
}

// ═══════════════════════════════════════════════════════════════════════════
// Account
// ═══════════════════════════════════════════════════════════════════════════

export interface AccountDomain {
  /** Backend `num`. */
  num: number;
  /** Backend `name`. */
  name: string;
  /** Backend `namet` — alias. */
  namet: string | null;
  /** Backend `namep` — parent/place name. */
  namep: string | null;
  /** Backend `noadad` — meter/account code. */
  noadad: string | null;
  /** Backend `nog`. */
  nog: number;
  /** Backend `nomstlm`. */
  nomstlm: number;
  /** Backend `notblh`. */
  notblh: number;
  /** Backend `balance`. */
  balance: number;
  /** Backend `dain` — debit. */
  dain: number;
  /** Backend `mden` — credit. */
  mden: number;
  /** Backend `tel` — phone. */
  tel: string | null;
  /** Backend `type`. */
  type: number;
}

export function parseAccountList(raw: unknown): AccountDomain[] {
  return unwrapList(AccountListResponseSchema, AccountDtoSchema, raw).map(
    (dto): AccountDomain => ({
      num: dto.num,
      name: dto.name,
      namet: dto.namet ?? null,
      namep: dto.namep ?? null,
      noadad: dto.noadad ?? null,
      nog: dto.nog,
      nomstlm: dto.nomstlm,
      notblh: dto.notblh,
      balance: dto.balance,
      dain: dto.dain,
      mden: dto.mden,
      tel: dto.tel ?? null,
      type: dto.type,
    }),
  );
}

// ═══════════════════════════════════════════════════════════════════════════
// Place
// ═══════════════════════════════════════════════════════════════════════════

export interface PlaceDomain {
  remoteId: number;
  code: string | null;
  name: string;
  parentId: number | null;
}

export function parsePlaceList(raw: unknown): PlaceDomain[] {
  return unwrapList(PlaceListResponseSchema, PlaceDtoSchema, raw).map(
    (dto): PlaceDomain => ({
      remoteId: dto.id,
      code: dto.code ?? null,
      name: dto.name,
      parentId: dto.parent_id ?? null,
    }),
  );
}

// ═══════════════════════════════════════════════════════════════════════════
// TGroup
// ═══════════════════════════════════════════════════════════════════════════

export interface TGroupDomain {
  remoteId: number;
  code: string | null;
  name: string;
  placeId: number | null;
}

export function parseTGroupList(raw: unknown): TGroupDomain[] {
  return unwrapList(TGroupListResponseSchema, TGroupDtoSchema, raw).map(
    (dto): TGroupDomain => ({
      remoteId: dto.id,
      code: dto.code ?? null,
      name: dto.name,
      placeId: dto.place_id ?? null,
    }),
  );
}

// ═══════════════════════════════════════════════════════════════════════════
// Tblh
// ═══════════════════════════════════════════════════════════════════════════

export interface TblhDomain {
  remoteId: number;
  code: string | null;
  name: string;
  groupId: number | null;
  placeId: number | null;
}

export function parseTblhList(raw: unknown): TblhDomain[] {
  return unwrapList(TblhListResponseSchema, TblhDtoSchema, raw).map(
    (dto): TblhDomain => ({
      remoteId: dto.id,
      code: dto.code ?? null,
      name: dto.name,
      groupId: dto.group_id ?? null,
      placeId: dto.place_id ?? null,
    }),
  );
}

// ═══════════════════════════════════════════════════════════════════════════
// User
// ═══════════════════════════════════════════════════════════════════════════

export interface UserListDomain {
  remoteId: number;
  username: string;
  fullName: string | null;
  phone: string | null;
  email: string | null;
  de: boolean;
  ed: boolean;
  rep: boolean;
  sK: boolean;
  sS: boolean;
  sys: boolean;
  noa: number;
  nou: number;
}

export function parseUserList(raw: unknown): UserListDomain[] {
  return unwrapList(UserListResponseSchema, UserDtoSchema, raw).map(
    (dto): UserListDomain => ({
      remoteId: dto.id,
      username: dto.username,
      fullName: dto.name ?? null,
      phone: dto.phone ?? null,
      email: dto.email ?? null,
      de: dto.DE,
      ed: dto.ED,
      rep: dto.REP,
      sK: dto.S_K,
      sS: dto.S_S,
      sys: dto.SYS,
      noa: dto.NOA,
      nou: dto.NOU,
    }),
  );
}

// ═══════════════════════════════════════════════════════════════════════════
// Currency
// ═══════════════════════════════════════════════════════════════════════════

export interface CurrencyDomain {
  remoteId: number;
  code: string;
  name: string;
  symbol: string | null;
  exchangeRate: number;
  isDefault: boolean;
}

export function parseCurrencyList(raw: unknown): CurrencyDomain[] {
  return unwrapList(CurrencyListResponseSchema, CurrencyDtoSchema, raw).map(
    (dto): CurrencyDomain => ({
      remoteId: dto.id,
      code: dto.code,
      name: dto.name,
      symbol: dto.symbol ?? null,
      exchangeRate: dto.exchange_rate,
      isDefault: dto.is_default,
    }),
  );
}

// ═══════════════════════════════════════════════════════════════════════════
// Bond
// ═══════════════════════════════════════════════════════════════════════════

// ⭐ Field names mirror entities/ItemBonds.java (v28) VERBATIM — ISS-12.
export interface BondDomain {
  /** Backend `num` — bond record id (remote). */
  num: number;
  /** Backend `num_s` — box/cashier number. */
  numS: number;
  /** Backend `nmstnd` — document/bond number (string). */
  nmstnd: string | null;
  /** Backend `name` — account name. */
  name: string | null;
  /** Backend `name_s` — box/cashier name. */
  nameS: string | null;
  /** Backend `type` — bond type (1=receipt). */
  type: number;
  /** Backend `cas` — posting status. */
  cas: number;
  /** Backend `mdate` — bond date (legacy string). */
  mdate: string | null;
  /** Backend `dain` — debit. */
  dain: number;
  /** Backend `mden` — credit. */
  mden: number;
  /** Backend `equal` — equivalent amount. */
  equal: number;
  /** Backend `balance` — running balance (rsed). */
  balance: number;
  /** Backend `price_trans`. */
  priceTrans: number;
  /** Backend `currencyid`. */
  currencyid: number;
  /** Backend `currencyname`. */
  currencyname: string | null;
  /** Backend `branchid`. */
  branchid: string | null;
  /** Backend `userid`. */
  userid: string | null;
  /** Backend `notes`. */
  notes: string | null;
  /** Backend `notes_box`. */
  notesBox: string | null;
  /** Backend `notes2` — bin. */
  notes2: string | null;
  /** Backend `nref`. */
  nref: string | null;
  /** Backend `nref_docno`. */
  nrefDocNo: string | null;
  /** Backend `finalbalance`. */
  finalbalance: number;
}

export function parseBondList(raw: unknown): BondDomain[] {
  return unwrapList(BondListResponseSchema, BondDtoSchema, raw).map(
    (dto): BondDomain => ({
      num: dto.num,
      numS: dto.num_s,
      nmstnd: dto.nmstnd ?? null,
      name: dto.name,
      nameS: dto.name_s ?? null,
      type: dto.type,
      cas: dto.cas,
      mdate: dto.mdate ?? null,
      dain: dto.dain,
      mden: dto.mden,
      equal: dto.equal,
      balance: dto.balance,
      priceTrans: dto.price_trans,
      currencyid: dto.currencyid,
      currencyname: dto.currencyname ?? null,
      branchid: dto.branchid ?? null,
      userid: dto.userid ?? null,
      notes: dto.notes ?? null,
      notesBox: dto.notes_box ?? null,
      notes2: dto.notes2 ?? null,
      nref: dto.nref ?? null,
      nrefDocNo: dto.nref_docno ?? null,
      finalbalance: dto.finalbalance,
    }),
  );
}

// ═══════════════════════════════════════════════════════════════════════════
// Bond payment
// ═══════════════════════════════════════════════════════════════════════════

// Bond payment shares the ItemBonds wire shape (GetListBondsPayment →
// BondsPaymentResponse). Same domain as BondDomain; `type` distinguishes
// payment vs receipt.
export type BondPaymentDomain = BondDomain;

export function parseBondPaymentList(raw: unknown): BondPaymentDomain[] {
  return unwrapList(
    BondPaymentListResponseSchema,
    BondPaymentDtoSchema,
    raw,
  ).map(
    (dto): BondPaymentDomain => ({
      num: dto.num,
      numS: dto.num_s,
      nmstnd: dto.nmstnd ?? null,
      name: dto.name,
      nameS: dto.name_s ?? null,
      type: dto.type,
      cas: dto.cas,
      mdate: dto.mdate ?? null,
      dain: dto.dain,
      mden: dto.mden,
      equal: dto.equal,
      balance: dto.balance,
      priceTrans: dto.price_trans,
      currencyid: dto.currencyid,
      currencyname: dto.currencyname ?? null,
      branchid: dto.branchid ?? null,
      userid: dto.userid ?? null,
      notes: dto.notes ?? null,
      notesBox: dto.notes_box ?? null,
      notes2: dto.notes2 ?? null,
      nref: dto.nref ?? null,
      nrefDocNo: dto.nref_docno ?? null,
      finalbalance: dto.finalbalance,
    }),
  );
}
