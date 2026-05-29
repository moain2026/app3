/**
 * WatermelonDB Migrations — العباسي تحصيل
 *
 * Add a new entry every time `SCHEMA_VERSION` (in ./schema.ts) is incremented.
 *
 * Pattern:
 *   {
 *     toVersion: 2,
 *     steps: [
 *       addColumns({ table: 'readings', columns: [{ name: 'new_col', type: 'string', isOptional: true }] }),
 *       createTable({ name: 'new_table', columns: [...] }),
 *     ],
 *   }
 */

import {
  schemaMigrations,
  unsafeExecuteSql,
} from '@nozbe/watermelondb/Schema/migrations';

export const migrations = schemaMigrations({
  migrations: [
    // ─── v1 → v2: realign `accounts` table to entities/Accounts.java ───────
    // The old `accounts` columns (code/name_en/currency_id/phone/address) were
    // an imagined model that never matched the server (ISS-12). We rebuild the
    // table with the legacy field names (num/namet/namep/noadad/dain/mden/tel/
    // type/nomstlm/notblh/nog). Accounts are a read-only mirror, so dropping &
    // recreating is safe — the next pull repopulates from the server.
    {
      toVersion: 2,
      steps: [
        unsafeExecuteSql('DROP TABLE IF EXISTS "accounts";'),
        unsafeExecuteSql(
          'CREATE TABLE "accounts" (' +
            '"id" TEXT PRIMARY KEY NOT NULL, ' +
            '"_changed" TEXT, ' +
            '"_status" TEXT, ' +
            '"num" INTEGER, ' +
            '"name" TEXT, ' +
            '"namet" TEXT, ' +
            '"namep" TEXT, ' +
            '"noadad" TEXT, ' +
            '"nog" INTEGER, ' +
            '"nomstlm" INTEGER, ' +
            '"notblh" INTEGER, ' +
            '"balance" INTEGER, ' +
            '"dain" INTEGER, ' +
            '"mden" INTEGER, ' +
            '"tel" TEXT, ' +
            '"type" INTEGER, ' +
            '"last_synced_at" INTEGER, ' +
            '"created_at" INTEGER, ' +
            '"updated_at" INTEGER);',
        ),
        unsafeExecuteSql(
          'CREATE INDEX IF NOT EXISTS "accounts_num" ON "accounts" ("num");',
        ),
        unsafeExecuteSql(
          'CREATE INDEX IF NOT EXISTS "accounts_name" ON "accounts" ("name");',
        ),
        unsafeExecuteSql(
          'CREATE INDEX IF NOT EXISTS "accounts_noadad" ON "accounts" ("noadad");',
        ),
      ],
    },

    // ─── v2 → v3: realign `bonds` table to entities/ItemBonds.java ─────────
    // Replaces the imagined bond model (bond_no/bond_type/amount/amount_paid/
    // account_id) with the legacy server shape (num/num_s/nmstnd/name/type/cas/
    // dain/mden/equal/balance/currencyid...). Bonds pulled from the server are
    // a mirror; locally-created (dirty) bonds are rare in dev — recreating the
    // table is acceptable for this migration stage (ISS-12).
    {
      toVersion: 3,
      steps: [
        unsafeExecuteSql('DROP TABLE IF EXISTS "bonds";'),
        unsafeExecuteSql(
          'CREATE TABLE "bonds" (' +
            '"id" TEXT PRIMARY KEY NOT NULL, ' +
            '"_changed" TEXT, ' +
            '"_status" TEXT, ' +
            '"local_uuid" TEXT, ' +
            '"num" INTEGER, ' +
            '"num_s" INTEGER, ' +
            '"nmstnd" TEXT, ' +
            '"name" TEXT, ' +
            '"name_s" TEXT, ' +
            '"type" INTEGER, ' +
            '"cas" INTEGER, ' +
            '"mdate" TEXT, ' +
            '"dain" INTEGER, ' +
            '"mden" INTEGER, ' +
            '"equal" INTEGER, ' +
            '"balance" INTEGER, ' +
            '"price_trans" INTEGER, ' +
            '"currencyid" INTEGER, ' +
            '"currencyname" TEXT, ' +
            '"branchid" TEXT, ' +
            '"userid" TEXT, ' +
            '"notes" TEXT, ' +
            '"notes_box" TEXT, ' +
            '"notes2" TEXT, ' +
            '"nref" TEXT, ' +
            '"nref_docno" TEXT, ' +
            '"finalbalance" INTEGER, ' +
            '"sync_status" TEXT, ' +
            '"last_sync_attempt_at" INTEGER, ' +
            '"last_error" TEXT, ' +
            '"sync_attempts" INTEGER, ' +
            '"created_at" INTEGER, ' +
            '"updated_at" INTEGER);',
        ),
        unsafeExecuteSql(
          'CREATE INDEX IF NOT EXISTS "bonds_local_uuid" ON "bonds" ("local_uuid");',
        ),
        unsafeExecuteSql(
          'CREATE INDEX IF NOT EXISTS "bonds_num" ON "bonds" ("num");',
        ),
        unsafeExecuteSql(
          'CREATE INDEX IF NOT EXISTS "bonds_nmstnd" ON "bonds" ("nmstnd");',
        ),
        unsafeExecuteSql(
          'CREATE INDEX IF NOT EXISTS "bonds_type" ON "bonds" ("type");',
        ),
        unsafeExecuteSql(
          'CREATE INDEX IF NOT EXISTS "bonds_cas" ON "bonds" ("cas");',
        ),
        unsafeExecuteSql(
          'CREATE INDEX IF NOT EXISTS "bonds_sync_status" ON "bonds" ("sync_status");',
        ),
      ],
    },
  ],
});
