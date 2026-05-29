/**
 * Bonds Repository — العباسي تحصيل (Wave 6-Β)
 *
 * Thin abstraction between the React layer (screens + components) and the
 * WatermelonDB `bonds` collection. Modelled after `readingsRepository.ts`:
 *
 *   • Builds reactive queries from user-facing filter objects.
 *   • Provides one-shot fetches for stats badges.
 *   • Exposes a `findByUuid` lookup for detail screens.
 *
 * MUTATION SCOPE
 *   `createBond` / `updateBond` write a local `bonds` row (mirroring the
 *   legacy `ItemBonds` columns exactly — see `EntryBondsActivity.save()`)
 *   and enqueue a `SaveBond` / `UpdateBond` push via `enqueueBondSave`.
 *   The legacy app DOES let collectors create receipt/payment bonds
 *   on-device (POST /SaveBond, PUT /UpdateBond — both confirmed live on
 *   the WCF Help contract), so the forms are wired to real persistence.
 *
 *   For READ paths (list + detail + pickers), `bonds` populated by either
 *   the server pull or the dev-bypass seeder behaves identically.
 */

import { Q } from '@nozbe/watermelondb';
import { Observable, map } from 'rxjs';
import { v4 as uuidv4 } from 'uuid';

import { database } from '@/database';
import type { Bond } from '@/database/models/Bond';
import { enqueueBondSave } from '@/services/sync/enqueueHelpers';
import { getCollectorNoa } from '@/services/storage/prefs';
import { useAuthStore } from '@/stores/authStore';
import { logger } from '@/utils/logger';

const log = logger.scope('BondsRepo');

// ─── Filter & sort types ──────────────────────────────────────────────────

/** Matches the chip filter on `BondsListScreen`. */
export type BondTypeFilter = 'all' | 'receipt' | 'payment';

export interface BondsQueryFilters {
  /** Free-text search across bond_no + account_name. Empty = no filter. */
  searchQuery: string;
  /** Type chip filter. */
  type: BondTypeFilter;
}

export interface BondsStats {
  total: number;
  receipt: number;
  payment: number;
  /** sync_status == 'synced' */
  synced: number;
  /** sync_status == 'dirty' */
  dirty: number;
  /** sync_status == 'failed' */
  failed: number;
}

// ─── Helpers ──────────────────────────────────────────────────────────────

function escapeForLike(value: string): string {
  // Q.like takes a SQL LIKE pattern; escape % and _ defensively.
  return value.replace(/[\\%_]/g, (m) => `\\${m}`);
}

/**
 * Build a stable query from a filter object. Sort is fixed at
 * `bond_date DESC, bond_no DESC` to mirror the legacy "newest first"
 * ordering used by `BondsListScreen`.
 */
function buildQuery(filters: BondsQueryFilters) {
  const conditions: Q.Clause[] = [];

  // Free-text search — `nmstnd` (bond number) is a string in the legacy
  // model; we search the textual `name` (account name) + `notes` columns
  // and leave numeric bond-number filtering to the JS post-pass below.
  const trimmed = filters.searchQuery.trim();
  if (trimmed.length > 0) {
    const pattern = `%${escapeForLike(trimmed)}%`;
    conditions.push(
      Q.or(
        Q.where('name', Q.like(pattern)),
        Q.where('nmstnd', Q.like(pattern)),
        Q.where('notes', Q.like(pattern)),
      ),
    );
  }

  // Legacy `type` is an int: 1 = receipt (قبض), otherwise payment (دفع).
  if (filters.type === 'receipt') {
    conditions.push(Q.where('type', 1));
  } else if (filters.type === 'payment') {
    conditions.push(Q.where('type', Q.notEq(1)));
  }

  return database.collections
    .get<Bond>('bonds')
    .query(
      ...conditions,
      // Legacy bonds carry `mdate` (string) + `num` (record id). Order by
      // the record id descending to mirror the legacy "newest first".
      Q.sortBy('num', Q.desc),
    );
}

// ─── Public API ───────────────────────────────────────────────────────────

/**
 * Reactive observable of bonds matching the filters. The returned
 * Observable emits a fresh array on every relevant DB change.
 *
 * Numeric-only search queries (e.g. user typing "1003") are handled
 * client-side by `applyNumericBondNoFilter` — `buildQuery` only filters
 * on text columns. Callers that want bond-no search should compose:
 *
 *   observeBonds(filters).pipe(
 *     map(rows => applyNumericBondNoFilter(rows, filters.searchQuery)),
 *   )
 *
 * `BondsListScreen` does exactly that.
 */
export function observeBonds(filters: BondsQueryFilters): Observable<Bond[]> {
  return buildQuery(filters).observe();
}

/**
 * Client-side numeric-bond-no filter. Returns the rows whose `bondNo`
 * matches the query, where "matches" is `String(bondNo).includes(query)`.
 *
 * Returns the input unchanged when the query is empty or non-numeric
 * — used in combination with the text search inside `buildQuery`.
 */
export function applyNumericBondNoFilter(rows: Bond[], query: string): Bond[] {
  const trimmed = query.trim();
  if (trimmed === '') return rows;
  if (!/^\d/.test(trimmed)) return rows;
  return rows.filter((b) => String(b.bondNo).includes(trimmed));
}

/**
 * One-shot fetch with the same filters. Used by stats and non-reactive
 * consumers (export, print).
 */
export async function fetchBonds(filters: BondsQueryFilters): Promise<Bond[]> {
  return buildQuery(filters).fetch();
}

/**
 * Find by local_uuid. Returns null if not found. Used by BondDetail.
 */
export async function findBondByUuid(localUuid: string): Promise<Bond | null> {
  const rows = await database.collections
    .get<Bond>('bonds')
    .query(Q.where('local_uuid', localUuid))
    .fetch();
  return rows.length > 0 ? (rows[0] ?? null) : null;
}

/**
 * Reactive single-bond observable for the detail screen. Emits the row
 * when it changes (e.g. payment added → amount_paid updated) and
 * completes with `null` if the row is destroyed.
 *
 * Implementation: we observe a 1-row query rather than `bond.observe()`
 * directly so the detail screen can subscribe BEFORE it has a reference
 * to the model row (i.e. from a navigation param, by uuid).
 */
export function observeBondByUuid(
  localUuid: string,
): Observable<Bond | null> {
  // We piggyback on the standard `.observe()` of a uuid-filtered query
  // and project [first | null]. WMDB will keep this hot as long as the
  // subscriber is attached. Using rxjs `map` keeps the typing clean —
  // we never hand-roll a new Observable.
  return database.collections
    .get<Bond>('bonds')
    .query(Q.where('local_uuid', localUuid))
    .observe()
    .pipe(map((rows: Bond[]) => (rows.length > 0 ? (rows[0] ?? null) : null)));
}

/**
 * Aggregate stats. Counts are computed against the UNFILTERED collection
 * so the chip badges always reflect global totals regardless of what the
 * user typed into the search box.
 */
export async function getBondStats(): Promise<BondsStats> {
  const collection = database.collections.get<Bond>('bonds');
  const [total, receipt, payment, synced, dirty, failed] = await Promise.all([
    collection.query().fetchCount(),
    collection.query(Q.where('type', 1)).fetchCount(),
    collection.query(Q.where('type', Q.notEq(1))).fetchCount(),
    collection.query(Q.where('sync_status', 'synced')).fetchCount(),
    collection.query(Q.where('sync_status', 'dirty')).fetchCount(),
    collection.query(Q.where('sync_status', 'failed')).fetchCount(),
  ]);
  return { total, receipt, payment, synced, dirty, failed };
}

/**
 * Live counts observable — convenience wrapper for chip badges. The
 * value re-emits on every `bonds` collection change. Implemented as a
 * one-shot fetch on each tick rather than a long-running aggregation
 * because the bonds collection is small (<10k rows realistically).
 */
export function observeBondStats(): Observable<BondsStats> {
  return database.collections
    .get<Bond>('bonds')
    .query()
    .observeWithColumns(['type', 'sync_status'])
    .pipe(
      map((rows: Bond[]): BondsStats => {
        const stats: BondsStats = {
          total: rows.length,
          receipt: 0,
          payment: 0,
          synced: 0,
          dirty: 0,
          failed: 0,
        };
        for (const r of rows) {
          if (r.bondType === 'receipt') stats.receipt += 1;
          else if (r.bondType === 'payment') stats.payment += 1;
          if (r.pushStatus === 'synced') stats.synced += 1;
          else if (r.pushStatus === 'dirty') stats.dirty += 1;
          else if (r.pushStatus === 'failed') stats.failed += 1;
        }
        return stats;
      }),
    );
}

// ─── Mutations (create / update) ──────────────────────────────────────────

/** User-facing input for creating/updating a bond. */
export interface BondInput {
  /** 'receipt' (قبض، type=1) or 'payment' (صرف، type=2). */
  bondType: 'receipt' | 'payment';
  /** Account record id (legacy num) — the subscriber/supplier. */
  accountNum?: number | null;
  /** Account display name (legacy name). */
  accountName: string;
  /** Human document/bond number (legacy nmstnd). */
  docNo?: string | null;
  /** Currency id (legacy currencyid). Defaults to 1 (base). */
  currencyId?: number;
  currencyName?: string | null;
  /** Headline amount. Mirrors legacy dain/mden/equal/rsed. */
  amount: number;
  /** Free-text note typed by the collector (legacy bin). */
  notes?: string | null;
}

/**
 * Build the auto-generated notes string EXACTLY like the legacy
 * `EntryBondsActivity.save()`:
 *   notes = " لكم مسلم بيد <collectorName> عبر الموبايل  <typedNote>"
 * We only attach the collector prefix for receipt bonds (قبض) to mirror
 * the legacy flow; payment bonds keep the typed note verbatim.
 */
function buildBondNotes(input: BondInput, collectorName: string): string {
  const typed = input.notes ?? '';
  if (input.bondType === 'receipt' && collectorName) {
    return ` لكم مسلم بيد ${collectorName} عبر الموبايل  ${typed}`;
  }
  return typed;
}

/** Map a BondInput onto the legacy ItemBonds columns of a `Bond` row. */
function applyInputToRow(
  row: Bond,
  input: BondInput,
  collectorName: string,
  collectorNoa: number,
): void {
  row.numS = collectorNoa; // box/cashier number (legacy num_s = NOA)
  row.nameS = collectorName || null; // box/cashier name (legacy name_s)
  row.nmstnd = input.docNo != null ? String(input.docNo) : null;
  row.name = input.accountName;
  row.type = input.bondType === 'receipt' ? 1 : 2;
  row.cas = 0; // unposted on creation
  row.mdate = new Date().toISOString();
  // Legacy: setDain(amount); setRsed(amount); setEqual(amount).
  if (input.bondType === 'receipt') {
    row.dain = input.amount;
    row.mden = 0;
  } else {
    row.dain = 0;
    row.mden = input.amount;
  }
  row.equal = input.amount;
  row.balance = input.amount; // rsed
  row.priceTrans = 1; // legacy setPriceTrans(1.0)
  row.currencyid = input.currencyId ?? 1;
  row.currencyname = input.currencyName ?? null;
  row.notes = buildBondNotes(input, collectorName);
  row.notesBox = null;
  row.notes2 = input.notes ?? null; // bin (the raw typed note)
  row.finalbalance = 0;
}

/**
 * Create a new bond locally and enqueue a `SaveBond` push.
 *
 * Mirrors `EntryBondsActivity.save()` (presenter.save):
 *   • Writes a fresh `bonds` row with the legacy ItemBonds field layout.
 *   • Marks it `dirty` and enqueues a `create` operation.
 *   • Fires a best-effort `pushOnly('after_write')`.
 *
 * Returns the persisted Bond model (with a stable `local_uuid`).
 */
export async function createBond(input: BondInput): Promise<Bond> {
  const user = useAuthStore.getState().user;
  const collectorName = user?.name ?? user?.username ?? '';
  const collectorNoa = getCollectorNoa() ?? user?.noa ?? 0;
  const localUuid = uuidv4();

  const bondCol = database.collections.get<Bond>('bonds');
  let created: Bond | null = null;
  await database.write(async () => {
    created = await bondCol.create((row) => {
      row.localUuid = localUuid;
      applyInputToRow(row, input, collectorName, collectorNoa);
      row.pushStatus = 'dirty';
      row.syncAttempts = 0;
    });
  });

  if (created == null) {
    throw new Error('createBond: row was not persisted');
  }
  log.info('bond created', { uuid: localUuid, type: input.bondType });
  await enqueueBondSave(created);
  return created;
}

/**
 * Update an existing bond (by local_uuid) and enqueue an `UpdateBond`
 * push. Throws if the bond cannot be found.
 */
export async function updateBond(
  localUuid: string,
  input: BondInput,
): Promise<Bond> {
  const target = await findBondByUuid(localUuid);
  if (target == null) {
    throw new Error(`updateBond: bond ${localUuid} not found`);
  }
  const user = useAuthStore.getState().user;
  const collectorName = user?.name ?? user?.username ?? '';
  const collectorNoa = getCollectorNoa() ?? user?.noa ?? target.numS ?? 0;

  await database.write(async () => {
    await target.update((row) => {
      applyInputToRow(row, input, collectorName, collectorNoa);
      row.pushStatus = 'dirty';
    });
  });

  log.info('bond updated', { uuid: localUuid, type: input.bondType });
  await enqueueBondSave(target);
  return target;
}
