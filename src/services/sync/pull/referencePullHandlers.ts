/**
 * Reference Data Pull Handlers — العباسي تحصيل
 *
 * Reference data (accounts, places, groups, tablas, currencies, users,
 * company info) is server-owned. The collector never modifies it locally,
 * so the conflict resolution is simple: REPLACE the local snapshot with
 * the server's authoritative copy.
 *
 * Strategy: full refresh (small lists; total payload < 50KB on average).
 * For larger datasets (>10k rows) we'd switch to a delta-pull, but the
 * current legacy backend doesn't expose `?since=...` query params.
 *
 * Each entity gets its own handler so they can be invoked independently
 * from the Sync Dashboard.
 */

import { v4 as uuidv4 } from 'uuid';

import { database } from '../../../database';
import type { Account } from '../../../database/models/Account';
import type { Bond } from '../../../database/models/Bond';
import type { BondPayment } from '../../../database/models/BondPayment';
import type { CompanyInfo } from '../../../database/models/CompanyInfo';
import type { Currency } from '../../../database/models/Currency';
import type { Place } from '../../../database/models/Place';
import type { TGroup } from '../../../database/models/TGroup';
import type { Tblh } from '../../../database/models/Tblh';
import type { User } from '../../../database/models/User';
import { logger } from '../../../utils/logger';
import { api } from '../../api';
import {
  parseAccountList,
  parseBondList,
  parseBondPaymentList,
  parseCompanyInfo,
  parseCurrencyList,
  parsePlaceList,
  parseTGroupList,
  parseTblhList,
  parseUserList,
} from '../../api/mappers';
import { prefs } from '../../storage';
import type { PullHandler, PullResult } from '../types';
import { appIdParam, bondListParams } from './requestScope';

const log = logger.scope('Pull.Reference');

// Reference lists are scoped only by appId (branch). The legacy lookups
// (LookupPleasesActivity etc.) always append it; omitting it can yield an
// empty list on some deployments.
function refParams(): Record<string, string | number> {
  return { appId: appIdParam() };
}

// ─── Shared: delete-then-insert in a single transaction ───────────────────
/**
 * Server-owned tables can be safely replaced because the collector never
 * edits them. We delete every row, then insert the fresh payload. All inside
 * one `database.write()` so observers see a single atomic transition.
 */
async function clearTable(tableName: string): Promise<void> {
  const collection = database.collections.get(tableName);
  const existing = await collection.query().fetch();
  for (const row of existing) {
    await row.markAsDeleted();
  }
}

// ─── Accounts ─────────────────────────────────────────────────────────────
export const accountPullHandler: PullHandler = {
  entity: 'accounts',
  async run(): Promise<PullResult> {
    const startedAt = Date.now();
    const raw = await api.call('getListAccounts', { params: refParams() });
    const accounts = parseAccountList(raw);
    log.debug('accounts fetched', { count: accounts.length });

    const collection = database.collections.get<Account>('accounts');
    await database.write(async () => {
      await clearTable('accounts');
      for (const dto of accounts) {
        await collection.create(row => {
          row.num = dto.num;
          row.name = dto.name;
          row.namet = dto.namet;
          row.namep = dto.namep;
          row.noadad = dto.noadad;
          row.nog = dto.nog;
          row.nomstlm = dto.nomstlm;
          row.notblh = dto.notblh;
          row.balance = dto.balance;
          row.dain = dto.dain;
          row.mden = dto.mden;
          row.tel = dto.tel;
          row.type = dto.type;
          row.lastSyncedAt = new Date();
        });
      }
    });

    prefs.setLastSync('accounts');
    const durationMs = Date.now() - startedAt;
    log.info('accounts replaced', { count: accounts.length, durationMs });
    return { upserted: accounts.length, skipped: 0, durationMs };
  },
};

// ─── Places ───────────────────────────────────────────────────────────────
export const placePullHandler: PullHandler = {
  entity: 'places',
  async run(): Promise<PullResult> {
    const startedAt = Date.now();
    // For now we fetch the broad GetListPlaces. Permission-filtered
    // GetListUserPlaces will be wired up in Phase 5 once auth is in place.
    const raw = await api.call('getListPlaces', { params: refParams() });
    const places = parsePlaceList(raw);
    log.debug('places fetched', { count: places.length });

    const collection = database.collections.get<Place>('places');
    await database.write(async () => {
      await clearTable('places');
      for (const dto of places) {
        await collection.create(row => {
          row.remoteId = dto.remoteId;
          row.code = dto.code;
          row.name = dto.name;
          row.parentId = dto.parentId;
          row.lastSyncedAt = new Date();
        });
      }
    });

    prefs.setLastSync('places');
    const durationMs = Date.now() - startedAt;
    log.info('places replaced', { count: places.length, durationMs });
    return { upserted: places.length, skipped: 0, durationMs };
  },
};

// ─── Groups + Tablas (single endpoint returns both) ───────────────────────
export const groupPullHandler: PullHandler = {
  entity: 'groups',
  async run(): Promise<PullResult> {
    const startedAt = Date.now();
    const raw = await api.call('getListGroup', { params: refParams() });
    const groups = parseTGroupList(raw);
    const tblhs = parseTblhList(raw);
    log.info('groups + tblh fetched', { groups: groups.length, tblhs: tblhs.length });

    const groupCol = database.collections.get<TGroup>('t_groups');
    const tblhCol = database.collections.get<Tblh>('tblh');

    await database.write(async () => {
      await clearTable('t_groups');
      await clearTable('tblh');
      for (const g of groups) {
        await groupCol.create(row => {
          row.remoteId = g.remoteId;
          row.code = g.code;
          row.name = g.name;
          row.placeId = g.placeId;
          row.lastSyncedAt = new Date();
        });
      }
      for (const t of tblhs) {
        await tblhCol.create(row => {
          row.remoteId = t.remoteId;
          row.code = t.code;
          row.name = t.name;
          row.groupId = t.groupId;
          row.placeId = t.placeId;
          row.lastSyncedAt = new Date();
        });
      }
    });

    prefs.setLastSync('groups');
    prefs.setLastSync('tblh');
    const durationMs = Date.now() - startedAt;
    return { upserted: groups.length + tblhs.length, skipped: 0, durationMs };
  },
};

// ─── Currencies ───────────────────────────────────────────────────────────
export const currencyPullHandler: PullHandler = {
  entity: 'currencies',
  async run(): Promise<PullResult> {
    const startedAt = Date.now();
    const raw = await api.call('getListCurrency');
    const currencies = parseCurrencyList(raw);
    log.debug('currencies fetched', { count: currencies.length });

    const collection = database.collections.get<Currency>('currencies');
    await database.write(async () => {
      await clearTable('currencies');
      for (const dto of currencies) {
        await collection.create(row => {
          row.remoteId = dto.remoteId;
          row.code = dto.code;
          row.name = dto.name;
          row.symbol = dto.symbol;
          row.exchangeRate = dto.exchangeRate;
          row.isDefault = dto.isDefault;
          row.lastSyncedAt = new Date();
        });
      }
    });

    prefs.setLastSync('currencies');
    const durationMs = Date.now() - startedAt;
    log.info('currencies replaced', { count: currencies.length, durationMs });
    return { upserted: currencies.length, skipped: 0, durationMs };
  },
};

// ─── Users (read-only roster for admin views) ─────────────────────────────
export const userPullHandler: PullHandler = {
  entity: 'users',
  async run(): Promise<PullResult> {
    const startedAt = Date.now();
    const raw = await api.call('getListUsers', { params: refParams() });
    const users = parseUserList(raw);
    log.debug('users fetched', { count: users.length });

    const collection = database.collections.get<User>('users');
    await database.write(async () => {
      await clearTable('users');
      for (const dto of users) {
        await collection.create(row => {
          row.remoteId = dto.remoteId;
          row.username = dto.username;
          row.fullName = dto.fullName;
          row.phone = dto.phone;
          row.email = dto.email;
          row.de = dto.de;
          row.ed = dto.ed;
          row.rep = dto.rep;
          row.sK = dto.sK;
          row.sS = dto.sS;
          row.sys = dto.sys;
          row.noa = dto.noa;
          row.nou = dto.nou;
        });
      }
    });

    prefs.setLastSync('users');
    const durationMs = Date.now() - startedAt;
    log.info('users replaced', { count: users.length, durationMs });
    return { upserted: users.length, skipped: 0, durationMs };
  },
};

// ─── Company info (single row) ────────────────────────────────────────────
export const companyPullHandler: PullHandler = {
  entity: 'company',
  async run(): Promise<PullResult> {
    const startedAt = Date.now();
    const raw = await api.call('getCompanyData', { params: refParams() });
    const info = parseCompanyInfo(raw);

    const collection = database.collections.get<CompanyInfo>('company_info');
    await database.write(async () => {
      await clearTable('company_info');
      await collection.create(row => {
        row.remoteId = info.remoteId;
        row.nameAr = info.nameAr;
        row.nameEn = info.nameEn;
        row.phone = info.phone;
        row.address = info.address;
        row.logoUrl = info.logoUrl;
        row.footerText = info.footerText;
        row.lastSyncedAt = new Date();
      });
    });

    prefs.setLastSync('company');
    const durationMs = Date.now() - startedAt;
    log.info('company info pulled', { durationMs });
    return { upserted: 1, skipped: 0, durationMs };
  },
};

// ─── Bonds (LWW: skip rows the collector is mid-editing) ──────────────────
export const bondPullHandler: PullHandler = {
  entity: 'bonds',
  async run(): Promise<PullResult> {
    const startedAt = Date.now();
    const params = bondListParams();
    log.info('bonds pull params', params);
    const raw = await api.call('getListBonds', { params });
    const bonds = parseBondList(raw);

    const collection = database.collections.get<Bond>('bonds');
    const existing = await collection.query().fetch();
    // Match on legacy `num` (the bond record id).
    const byNum = new Map<number, Bond>();
    for (const b of existing) {
      if (b.num != null) {
        byNum.set(b.num, b);
      }
    }

    let upserted = 0;
    let skipped = 0;

    // Write legacy ItemBonds columns directly (model getters are read-only).
    const applyBondFields = (row: Bond, dto: (typeof bonds)[number]): void => {
      row.num = dto.num;
      row.numS = dto.numS;
      row.nmstnd = dto.nmstnd;
      row.name = dto.name;
      row.nameS = dto.nameS;
      row.type = dto.type;
      row.cas = dto.cas;
      row.mdate = dto.mdate;
      row.dain = dto.dain;
      row.mden = dto.mden;
      row.equal = dto.equal;
      row.balance = dto.balance;
      row.priceTrans = dto.priceTrans;
      row.currencyid = dto.currencyid;
      row.currencyname = dto.currencyname;
      row.branchid = dto.branchid;
      row.userid = dto.userid;
      row.notes = dto.notes;
      row.notesBox = dto.notesBox;
      row.notes2 = dto.notes2;
      row.nref = dto.nref;
      row.nrefDocNo = dto.nrefDocNo;
      row.finalbalance = dto.finalbalance;
    };

    await database.write(async () => {
      for (const dto of bonds) {
        const local = byNum.get(dto.num);
        if (local) {
          // LWW: skip if collector is mid-edit.
          if (
            local.pushStatus === 'dirty' ||
            local.pushStatus === 'syncing' ||
            local.pushStatus === 'failed'
          ) {
            skipped += 1;
            continue;
          }
          await local.update(row => {
            applyBondFields(row, dto);
            row.pushStatus = 'pristine';
            row.syncAttempts = 0;
            row.lastError = null;
          });
          upserted += 1;
        } else {
          await collection.create(row => {
            row.localUuid = uuidv4();
            applyBondFields(row, dto);
            row.pushStatus = 'pristine';
            row.syncAttempts = 0;
            row.lastError = null;
          });
          upserted += 1;
        }
      }
    });

    prefs.setLastSync('bonds');
    const durationMs = Date.now() - startedAt;
    log.info('bonds pulled', { upserted, skipped, durationMs });
    return { upserted, skipped, durationMs };
  },
};

// ─── Bond Payments ────────────────────────────────────────────────────────
export const bondPaymentPullHandler: PullHandler = {
  entity: 'bond_payments',
  async run(): Promise<PullResult> {
    const startedAt = Date.now();
    const params = bondListParams();
    log.info('bond payments pull params', params);
    const raw = await api.call('getListBondsPayment', { params });
    const payments = parseBondPaymentList(raw);

    const collection = database.collections.get<BondPayment>('bond_payments');
    const existing = await collection.query().fetch();
    const byRemoteId = new Map<number, BondPayment>();
    for (const p of existing) {
      if (p.remoteId != null) {
        byRemoteId.set(p.remoteId, p);
      }
    }

    let upserted = 0;
    let skipped = 0;

    // BondsPayment shares the ItemBonds wire shape. Map the legacy fields
    // onto the (still wave-α) bond_payments columns:
    //   num    → remote_id
    //   nmstnd → bond_no (parse string → number; fall back to num)
    //   equal/dain/mden → amount (dominant non-zero magnitude)
    //   mdate  → payment_date
    //   notes  → notes
    // NOTE: bond_payments table itself is aligned in Wave 3; this mapping
    // keeps the read path working today.
    const bondNoOf = (dto: (typeof payments)[number]): number => {
      if (dto.nmstnd && dto.nmstnd !== '') {
        const n = Number(dto.nmstnd);
        if (Number.isFinite(n)) return n;
      }
      return dto.num;
    };
    const amountOf = (dto: (typeof payments)[number]): number => {
      if (dto.equal && dto.equal !== 0) return Math.abs(dto.equal);
      return Math.abs(dto.dain || dto.mden || 0);
    };
    const dateOf = (dto: (typeof payments)[number]): Date | null => {
      if (!dto.mdate) return null;
      const normalized = dto.mdate.includes('T')
        ? dto.mdate
        : dto.mdate.replace(' ', 'T');
      const d = new Date(normalized);
      return Number.isNaN(d.getTime()) ? null : d;
    };

    await database.write(async () => {
      for (const dto of payments) {
        const local = byRemoteId.get(dto.num);
        if (local) {
          if (
            local.pushStatus === 'dirty' ||
            local.pushStatus === 'syncing' ||
            local.pushStatus === 'failed'
          ) {
            skipped += 1;
            continue;
          }
          await local.update(row => {
            row.bondNo = bondNoOf(dto);
            row.amount = amountOf(dto);
            row.paymentMethod = null;
            row.referenceNo = dto.nrefDocNo;
            row.notes = dto.notes;
            const d = dateOf(dto);
            if (d) {
              row.paymentDate = d;
            }
            row.pushStatus = 'pristine';
            row.syncAttempts = 0;
            row.lastError = null;
            row.remoteId = dto.num;
          });
          upserted += 1;
        } else {
          await collection.create(row => {
            row.localUuid = uuidv4();
            row.remoteId = dto.num;
            // bondId locally is the WatermelonDB id of the parent bond.
            // We don't have it from the wire payload; Phase 8 will run a
            // post-processor that joins on bond_no to fill it. For now we
            // store an empty placeholder.
            row.bondId = '';
            row.bondNo = bondNoOf(dto);
            row.amount = amountOf(dto);
            row.paymentMethod = null;
            row.referenceNo = dto.nrefDocNo;
            row.notes = dto.notes;
            row.paymentDate = dateOf(dto) ?? new Date();
            row.pushStatus = 'pristine';
            row.syncAttempts = 0;
            row.lastError = null;
          });
          upserted += 1;
        }
      }
    });

    // Share the timestamp with the parent bonds bucket.
    prefs.setLastSync('bonds');
    const durationMs = Date.now() - startedAt;
    log.info('bond payments pulled', { upserted, skipped, durationMs });
    return { upserted, skipped, durationMs };
  },
};
