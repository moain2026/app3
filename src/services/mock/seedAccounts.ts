/**
 * Seed accounts — Wave 6-Β
 *
 * Mirrors `MOCK_ACCOUNTS` into the `accounts` WatermelonDB table for
 * dev-bypass sessions. Idempotent: existing rows (matched by remote_id)
 * are skipped on subsequent runs.
 *
 * NOTE on shape adaptation:
 *   `MockAccount` carries fields the `Account` model doesn't have
 *   (placeId, placeName, groupId, groupName, active). These are PURELY
 *   presentation conveniences — the live WCF `accounts` payload returns
 *   them as JOINs against `places`/`tblh`. We drop them here because:
 *
 *   (a) The picker reads `placeName` / `groupName` via the `MockAccount`
 *       fixture path TODAY, which Wave 6-Β does NOT migrate (out of
 *       scope — would require a JOIN query in WMDB). The picker keeps
 *       reading from `MOCK_ACCOUNTS` for the *labels* while the
 *       *primary list* comes from `observeAccounts()`.
 *
 *   (b) `active` is omitted from the local mirror until the WCF help
 *       page confirms whether it exists on the server payload.
 */

import { Q } from '@nozbe/watermelondb';

import { database } from '@/database';
import type { Account } from '@/database/models/Account';
import { useAuthStore } from '@/stores/authStore';
import { logger } from '@/utils/logger';

import { MOCK_ACCOUNTS } from '../../mocks/accounts';

const log = logger.scope('SeedAccounts');

let promise: Promise<void> | null = null;

export async function seedAccountsIfDevBypass(): Promise<void> {
  if (!useAuthStore.getState().isDevBypass) {
    return;
  }
  if (promise) {
    return promise;
  }
  promise = doSeed().finally(() => {
    promise = null;
  });
  return promise;
}

async function doSeed(): Promise<void> {
  const collection = database.collections.get<Account>('accounts');

  // Fast path — if the first mock id is already in DB, skip the whole batch.
  const sentinel = MOCK_ACCOUNTS[0];
  if (!sentinel) {
    log.warn('seedAccounts: no mock rows — skip');
    return;
  }
  const existing = await collection
    .query(Q.where('num', sentinel.id))
    .fetch();
  if (existing.length > 0) {
    log.debug('seedAccounts: already populated, skip');
    return;
  }

  const startedAt = Date.now();
  try {
    await database.write(async () => {
      const creates = MOCK_ACCOUNTS.map((mock) =>
        collection.prepareCreate((row) => {
          row.num = mock.id; // legacy `num` = the account record id
          row.noadad = mock.num; // visible meter/account code
          row.name = mock.name;
          row.namet = mock.nameT ?? null;
          row.namep = mock.placeName ?? null;
          row.nog = mock.groupId ?? 0;
          row.nomstlm = mock.placeId ?? 0;
          row.notblh = 0;
          row.balance = mock.balance;
          row.dain = mock.balance > 0 ? mock.balance : 0;
          row.mden = mock.balance < 0 ? -mock.balance : 0;
          row.tel = mock.phone ?? null;
          row.type = 0;
          row.lastSyncedAt = new Date();
        }),
      );
      await database.batch(...creates);
    });
    log.info('seedAccounts: populated', {
      rows: MOCK_ACCOUNTS.length,
      durationMs: Date.now() - startedAt,
    });
  } catch (err) {
    log.error('seedAccounts: failed', {
      message: err instanceof Error ? err.message : String(err),
    });
  }
}
