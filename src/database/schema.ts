/**
 * WatermelonDB Schema — العباسي تحصيل (Al-Abbasi Tahsil)
 *
 * ╔══════════════════════════════════════════════════════════════════════╗
 * ║  CRITICAL RULE — DO NOT VIOLATE                                      ║
 * ║                                                                      ║
 * ║  Column names below MIRROR the legacy backend JSON field names       ║
 * ║  exactly (`noadad`, `ks`, `kh`, `asts`, `cas`, `nomstlm`, `notblh`,  ║
 * ║  `nog`, `ind`). This is REQUIRED for transparent sync with the old   ║
 * ║  server which we cannot modify.                                      ║
 * ║                                                                      ║
 * ║  Translation to clean UI names (meterNumber, previousReading, etc.)  ║
 * ║  happens ONLY at the selector / hook layer — NOT here.               ║
 * ║                                                                      ║
 * ║  Reference: ItemReading.java (legacy app) lines 1-65.                ║
 * ╚══════════════════════════════════════════════════════════════════════╝
 *
 * Version history:
 *   v1 (2026-05-20): initial schema. 12 tables.
 *
 * To bump schema, increment `version` AND add a migration in `./migrations.ts`.
 */

import { appSchema, tableSchema } from '@nozbe/watermelondb';

export const SCHEMA_VERSION = 3;

export const schema = appSchema({
  version: SCHEMA_VERSION,
  tables: [
    /* ════════════════════════════════════════════════════════════════════
     * 1) readings — Core entity. Mirror of ItemReading.java (12 fields).
     * ════════════════════════════════════════════════════════════════════ */
    tableSchema({
      name: 'readings',
      columns: [
        // ─── Sync metadata (local-only) ───────────────────────────────────
        { name: 'local_uuid', type: 'string', isIndexed: true }, // idempotency key for sync
        { name: 'remote_id', type: 'number', isOptional: true, isIndexed: true },

        // ─── Original ItemReading fields — KEEP NAMES VERBATIM ────────────
        { name: 'num', type: 'number', isIndexed: true }, // sequence number
        { name: 'name', type: 'string' }, // customer name
        { name: 'namet', type: 'string', isOptional: true }, // customer alias
        { name: 'ind', type: 'number', isIndexed: true }, // meter type
        { name: 'nomstlm', type: 'number', isIndexed: true }, // receiver/area number
        { name: 'notblh', type: 'number', isIndexed: true }, // book/tabla number
        { name: 'noadad', type: 'string', isIndexed: true }, // meter number
        { name: 'nog', type: 'number', isIndexed: true }, // group number
        { name: 'ks', type: 'number' }, // previous reading
        { name: 'kh', type: 'number', isOptional: true }, // current reading (null = not yet read)
        { name: 'cas', type: 'number', isIndexed: true }, // posting status (0=unposted, !=0 = posted)
        { name: 'asts', type: 'number' }, // expected consumption

        // ─── Sync state (local-only) ──────────────────────────────────────
        {
          name: 'sync_status',
          type: 'string',
          isIndexed: true,
          // 'pristine' | 'dirty' | 'syncing' | 'synced' | 'failed'
        },
        { name: 'last_sync_attempt_at', type: 'number', isOptional: true },
        { name: 'last_error', type: 'string', isOptional: true },
        { name: 'sync_attempts', type: 'number' },

        // ─── Timestamps ───────────────────────────────────────────────────
        { name: 'reading_date', type: 'number', isOptional: true }, // when reading was taken
        { name: 'created_at', type: 'number' },
        { name: 'updated_at', type: 'number' },
      ],
    }),

    /* ════════════════════════════════════════════════════════════════════
     * 2) bonds — Receipts/vouchers (سندات)
     * ════════════════════════════════════════════════════════════════════ */
    tableSchema({
      name: 'bonds',
      // ⭐ Columns mirror entities/ItemBonds.java (v28) VERBATIM — ISS-12.
      //   num num_s nmstnd name name_s type cas mdate dain mden equal balance
      //   price_trans currencyid currencyname branchid userid notes notes_box
      //   notes2 nref nref_docno finalbalance
      columns: [
        { name: 'local_uuid', type: 'string', isIndexed: true },

        { name: 'num', type: 'number', isOptional: true, isIndexed: true }, // bond record id (remote)
        { name: 'num_s', type: 'number' }, // box/cashier number
        { name: 'nmstnd', type: 'string', isOptional: true, isIndexed: true }, // document/bond number (string)
        { name: 'name', type: 'string', isOptional: true }, // account name
        { name: 'name_s', type: 'string', isOptional: true }, // box/cashier name
        { name: 'type', type: 'number', isIndexed: true }, // bond type (1=receipt...)
        { name: 'cas', type: 'number', isIndexed: true }, // posting status (0=unposted)
        { name: 'mdate', type: 'string', isOptional: true }, // bond date (legacy string)
        { name: 'dain', type: 'number' }, // debit
        { name: 'mden', type: 'number' }, // credit
        { name: 'equal', type: 'number' }, // equivalent amount
        { name: 'balance', type: 'number' }, // running balance (rsed)
        { name: 'price_trans', type: 'number' }, // exchange/transfer price
        { name: 'currencyid', type: 'number' },
        { name: 'currencyname', type: 'string', isOptional: true },
        { name: 'branchid', type: 'string', isOptional: true },
        { name: 'userid', type: 'string', isOptional: true },
        { name: 'notes', type: 'string', isOptional: true },
        { name: 'notes_box', type: 'string', isOptional: true },
        { name: 'notes2', type: 'string', isOptional: true }, // bin
        { name: 'nref', type: 'string', isOptional: true },
        { name: 'nref_docno', type: 'string', isOptional: true },
        { name: 'finalbalance', type: 'number' },

        // sync
        { name: 'sync_status', type: 'string', isIndexed: true },
        { name: 'last_sync_attempt_at', type: 'number', isOptional: true },
        { name: 'last_error', type: 'string', isOptional: true },
        { name: 'sync_attempts', type: 'number' },

        { name: 'created_at', type: 'number' },
        { name: 'updated_at', type: 'number' },
      ],
    }),

    /* ════════════════════════════════════════════════════════════════════
     * 3) bond_payments — line items of a bond
     * ════════════════════════════════════════════════════════════════════ */
    tableSchema({
      name: 'bond_payments',
      columns: [
        { name: 'local_uuid', type: 'string', isIndexed: true },
        { name: 'remote_id', type: 'number', isOptional: true, isIndexed: true },

        { name: 'bond_id', type: 'string', isIndexed: true }, // FK → bonds.id (local id)
        { name: 'bond_no', type: 'number', isIndexed: true }, // duplicated for fast lookup
        { name: 'amount', type: 'number' },
        { name: 'payment_method', type: 'string', isOptional: true }, // cash / cheque / ...
        { name: 'reference_no', type: 'string', isOptional: true },
        { name: 'notes', type: 'string', isOptional: true },
        { name: 'payment_date', type: 'number' },

        // sync
        { name: 'sync_status', type: 'string', isIndexed: true },
        { name: 'last_sync_attempt_at', type: 'number', isOptional: true },
        { name: 'last_error', type: 'string', isOptional: true },
        { name: 'sync_attempts', type: 'number' },

        { name: 'created_at', type: 'number' },
        { name: 'updated_at', type: 'number' },
      ],
    }),

    /* ════════════════════════════════════════════════════════════════════
     * 4) accounts — قائمة الحسابات
     * ════════════════════════════════════════════════════════════════════ */
    tableSchema({
      name: 'accounts',
      // ⭐ Columns mirror entities/Accounts.java (v28) VERBATIM — ISS-12.
      //   num name namet namep noadad nog nomstlm notblh balance dain mden
      //   tel type
      columns: [
        { name: 'num', type: 'number', isIndexed: true }, // remote id / sequence
        { name: 'name', type: 'string', isIndexed: true }, // account name
        { name: 'namet', type: 'string', isOptional: true }, // alias name
        { name: 'namep', type: 'string', isOptional: true }, // parent/place name
        { name: 'noadad', type: 'string', isOptional: true, isIndexed: true }, // meter/account code
        { name: 'nog', type: 'number' }, // group number
        { name: 'nomstlm', type: 'number' }, // receiver/area number
        { name: 'notblh', type: 'number' }, // book/tabla number
        { name: 'balance', type: 'number' }, // running balance
        { name: 'dain', type: 'number' }, // debit total
        { name: 'mden', type: 'number' }, // credit total
        { name: 'tel', type: 'string', isOptional: true }, // phone
        { name: 'type', type: 'number' }, // account type

        // Read-only mirror (server is source of truth for accounts).
        { name: 'last_synced_at', type: 'number', isOptional: true },

        { name: 'created_at', type: 'number' },
        { name: 'updated_at', type: 'number' },
      ],
    }),

    /* ════════════════════════════════════════════════════════════════════
     * 5) places — المناطق (referenced by readings.nomstlm)
     * ════════════════════════════════════════════════════════════════════ */
    tableSchema({
      name: 'places',
      columns: [
        { name: 'remote_id', type: 'number', isIndexed: true }, // ↔ readings.nomstlm
        { name: 'code', type: 'string', isOptional: true },
        { name: 'name', type: 'string', isIndexed: true },
        { name: 'parent_id', type: 'number', isOptional: true },
        { name: 'last_synced_at', type: 'number', isOptional: true },
        { name: 'created_at', type: 'number' },
        { name: 'updated_at', type: 'number' },
      ],
    }),

    /* ════════════════════════════════════════════════════════════════════
     * 6) t_groups — المجموعات (referenced by readings.nog)
     * ════════════════════════════════════════════════════════════════════ */
    tableSchema({
      name: 't_groups',
      columns: [
        { name: 'remote_id', type: 'number', isIndexed: true }, // ↔ readings.nog
        { name: 'code', type: 'string', isOptional: true },
        { name: 'name', type: 'string', isIndexed: true },
        { name: 'place_id', type: 'number', isOptional: true, isIndexed: true },
        { name: 'last_synced_at', type: 'number', isOptional: true },
        { name: 'created_at', type: 'number' },
        { name: 'updated_at', type: 'number' },
      ],
    }),

    /* ════════════════════════════════════════════════════════════════════
     * 7) tblh — التابلات/الدفاتر (referenced by readings.notblh)
     * ════════════════════════════════════════════════════════════════════ */
    tableSchema({
      name: 'tblh',
      columns: [
        { name: 'remote_id', type: 'number', isIndexed: true }, // ↔ readings.notblh
        { name: 'code', type: 'string', isOptional: true },
        { name: 'name', type: 'string', isIndexed: true },
        { name: 'group_id', type: 'number', isOptional: true, isIndexed: true },
        { name: 'place_id', type: 'number', isOptional: true, isIndexed: true },
        { name: 'last_synced_at', type: 'number', isOptional: true },
        { name: 'created_at', type: 'number' },
        { name: 'updated_at', type: 'number' },
      ],
    }),

    /* ════════════════════════════════════════════════════════════════════
     * 8) currencies — العملات
     * ════════════════════════════════════════════════════════════════════ */
    tableSchema({
      name: 'currencies',
      columns: [
        { name: 'remote_id', type: 'number', isIndexed: true },
        { name: 'code', type: 'string', isIndexed: true },
        { name: 'name', type: 'string' },
        { name: 'symbol', type: 'string', isOptional: true },
        { name: 'exchange_rate', type: 'number' },
        { name: 'is_default', type: 'boolean' },
        { name: 'last_synced_at', type: 'number', isOptional: true },
        { name: 'created_at', type: 'number' },
        { name: 'updated_at', type: 'number' },
      ],
    }),

    /* ════════════════════════════════════════════════════════════════════
     * 9) users — Mirror of Users.java (permissions DE/ED/REP/S_K/S_S/SYS/NOA/NOU)
     * ════════════════════════════════════════════════════════════════════ */
    tableSchema({
      name: 'users',
      columns: [
        { name: 'remote_id', type: 'number', isIndexed: true },
        { name: 'username', type: 'string', isIndexed: true },
        { name: 'full_name', type: 'string', isOptional: true },
        { name: 'phone', type: 'string', isOptional: true },
        { name: 'email', type: 'string', isOptional: true },

        // ─── Permissions (preserve legacy field names) ────────────────────
        { name: 'de', type: 'boolean' }, // DE — delete permission
        { name: 'ed', type: 'boolean' }, // ED — edit permission
        { name: 'rep', type: 'boolean' }, // REP — reports access
        { name: 's_k', type: 'boolean' }, // S_K — see all readings
        { name: 's_s', type: 'boolean' }, // S_S — see all bonds
        { name: 'sys', type: 'boolean' }, // SYS — system admin
        { name: 'noa', type: 'number' }, // NOA — account scope
        { name: 'nou', type: 'number' }, // NOU — user scope

        { name: 'last_login_at', type: 'number', isOptional: true },
        { name: 'created_at', type: 'number' },
        { name: 'updated_at', type: 'number' },
      ],
    }),

    /* ════════════════════════════════════════════════════════════════════
     * 10) company_info — Used by print receipts (header)
     * ════════════════════════════════════════════════════════════════════ */
    tableSchema({
      name: 'company_info',
      columns: [
        { name: 'remote_id', type: 'number', isOptional: true, isIndexed: true },
        { name: 'name_ar', type: 'string' }, // شركة العباسي لتوليد الكهرباء التجارية
        { name: 'name_en', type: 'string', isOptional: true },
        { name: 'phone', type: 'string', isOptional: true },
        { name: 'address', type: 'string', isOptional: true },
        { name: 'logo_url', type: 'string', isOptional: true },
        { name: 'footer_text', type: 'string', isOptional: true },
        { name: 'last_synced_at', type: 'number', isOptional: true },
        { name: 'created_at', type: 'number' },
        { name: 'updated_at', type: 'number' },
      ],
    }),

    /* ════════════════════════════════════════════════════════════════════
     * 11) sync_queue — Outbound mutations awaiting upload
     * ════════════════════════════════════════════════════════════════════ */
    tableSchema({
      name: 'sync_queue',
      columns: [
        { name: 'entity_type', type: 'string', isIndexed: true }, // 'reading'|'bond'|'bond_payment'
        { name: 'entity_local_id', type: 'string', isIndexed: true }, // local WMDB id
        { name: 'entity_local_uuid', type: 'string', isIndexed: true }, // idempotency key
        { name: 'operation', type: 'string' }, // 'create'|'update'|'delete'
        { name: 'payload_json', type: 'string' }, // serialized DTO
        { name: 'status', type: 'string', isIndexed: true }, // 'pending'|'processing'|'failed'|'done'
        { name: 'priority', type: 'number' }, // higher = run first
        { name: 'attempts', type: 'number' },
        { name: 'last_error', type: 'string', isOptional: true },
        { name: 'next_run_at', type: 'number', isOptional: true }, // for backoff scheduling
        { name: 'created_at', type: 'number' },
        { name: 'updated_at', type: 'number' },
      ],
    }),

    /* ════════════════════════════════════════════════════════════════════
     * 12) sync_logs — Audit trail for sync operations (for the Sync Dashboard)
     * ════════════════════════════════════════════════════════════════════ */
    tableSchema({
      name: 'sync_logs',
      columns: [
        { name: 'entity_type', type: 'string', isIndexed: true },
        { name: 'operation', type: 'string' },
        { name: 'direction', type: 'string' }, // 'push'|'pull'
        { name: 'status', type: 'string', isIndexed: true }, // 'success'|'failure'
        { name: 'records_count', type: 'number' },
        { name: 'duration_ms', type: 'number' },
        { name: 'error_message', type: 'string', isOptional: true },
        { name: 'http_status', type: 'number', isOptional: true },
        { name: 'created_at', type: 'number' },
      ],
    }),
  ],
});
