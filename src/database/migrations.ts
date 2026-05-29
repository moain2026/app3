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
  ],
});
