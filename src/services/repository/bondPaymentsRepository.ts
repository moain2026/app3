/**
 * BondPayments Repository — العباسي تحصيل (Wave 6-Β)
 *
 * Read-only API around the `bond_payments` collection. Used by
 * `BondDetailScreen` to display the payment ledger for a single bond,
 * sorted oldest-first (matches how the user enters them).
 *
 * `createBondPayment` writes a local `bond_payments` row linked to its
 * parent bond and enqueues a `SaveBondPayment` push (POST /SaveBondPayment
 * is confirmed live on the WCF Help contract). Mirrors the legacy
 * `EntryBondsPaymentActivity.save()` flow.
 */

import { Q } from '@nozbe/watermelondb';
import type { Observable } from 'rxjs';
import { v4 as uuidv4 } from 'uuid';

import { database } from '@/database';
import type { Bond } from '@/database/models/Bond';
import type { BondPayment } from '@/database/models/BondPayment';
import { enqueueBondPaymentSave } from '@/services/sync/enqueueHelpers';
import { logger } from '@/utils/logger';

const log = logger.scope('BondPaymentsRepo');

/**
 * Reactive list of payments belonging to a single bond.
 *
 * `bondLocalId` is the WatermelonDB-local string id of the parent bond
 * (NOT the `local_uuid` legacy key). `BondDetailScreen` passes
 * `bond.id` which is exactly that.
 */
export function observePaymentsByBond(
  bondLocalId: string,
): Observable<BondPayment[]> {
  return database.collections
    .get<BondPayment>('bond_payments')
    .query(Q.where('bond_id', bondLocalId), Q.sortBy('payment_date', Q.asc))
    .observe();
}

/**
 * One-shot fetch — used by the receipt printer (Wave 6-Γ will read the
 * frozen payment list at print time).
 */
export async function fetchPaymentsByBond(
  bondLocalId: string,
): Promise<BondPayment[]> {
  return database.collections
    .get<BondPayment>('bond_payments')
    .query(Q.where('bond_id', bondLocalId), Q.sortBy('payment_date', Q.asc))
    .fetch();
}

// ─── Mutations (create) ───────────────────────────────────────────────────

/** User-facing input for adding a payment to an existing bond. */
export interface BondPaymentInput {
  /** WatermelonDB-local id of the parent bond (bond.id). */
  bondLocalId: string;
  /** Parent bond's human number (bond.bondNo). */
  bondNo: number;
  amount: number;
  paymentMethod?: string | null;
  referenceNo?: string | null;
  notes?: string | null;
}

/**
 * Create a payment row and enqueue a `SaveBondPayment` push.
 * Returns the persisted BondPayment model.
 */
export async function createBondPayment(
  input: BondPaymentInput,
): Promise<BondPayment> {
  const col = database.collections.get<BondPayment>('bond_payments');
  const localUuid = uuidv4();

  let created: BondPayment | null = null;
  await database.write(async () => {
    created = await col.create((row) => {
      row.localUuid = localUuid;
      row.bondId = input.bondLocalId;
      row.bondNo = input.bondNo;
      row.amount = input.amount;
      row.paymentMethod = input.paymentMethod ?? null;
      row.referenceNo = input.referenceNo ?? null;
      row.notes = input.notes ?? null;
      row.paymentDate = new Date();
      row.pushStatus = 'dirty';
      row.syncAttempts = 0;
    });
  });

  if (created == null) {
    throw new Error('createBondPayment: row was not persisted');
  }
  log.info('bond payment created', { uuid: localUuid, bondNo: input.bondNo });
  await enqueueBondPaymentSave(created);
  return created;
}

/**
 * Find a bond model by its `local_uuid` — used by the payment form to
 * resolve the parent bond's WMDB local id + headline values.
 */
export async function findBondForPayment(
  bondLocalUuid: string,
): Promise<Bond | null> {
  const rows = await database.collections
    .get<Bond>('bonds')
    .query(Q.where('local_uuid', bondLocalUuid))
    .fetch();
  return rows.length > 0 ? (rows[0] ?? null) : null;
}
