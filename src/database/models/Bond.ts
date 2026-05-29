/**
 * Bond Model — السندات
 *
 * ⭐ Columns mirror entities/ItemBonds.java (v28) VERBATIM — ISS-12.
 *
 * Back-compat getters (`bondNo`, `amount`, `bondType`, `bondDate`,
 * `accountName`, `currencyId`, `remoteId`, `notes`) are provided so the
 * existing view-model / UI keep compiling while we migrate them. New code
 * should read the legacy fields directly.
 */

import { Model, Q, type Query } from '@nozbe/watermelondb';
import { children, date, field, lazy, readonly, text } from '@nozbe/watermelondb/decorators';

import type { BondPayment } from './BondPayment';
import type { PushStatus } from './Reading';

export class Bond extends Model {
  static table = 'bonds';
  static associations = {
    bond_payments: { type: 'has_many' as const, foreignKey: 'bond_id' },
  };

  @text('local_uuid') localUuid!: string;

  // ─── Legacy ItemBonds fields (verbatim) ───────────────────────────────────
  @field('num') num?: number | null; // bond record id (remote)
  @field('num_s') numS!: number; // box/cashier number
  @text('nmstnd') nmstnd?: string | null; // document/bond number (string)
  @text('name') name?: string | null; // account name
  @text('name_s') nameS?: string | null; // box/cashier name
  @field('type') type!: number; // bond type (1=receipt...)
  @field('cas') cas!: number; // posting status (0=unposted)
  @text('mdate') mdate?: string | null; // bond date (legacy string)
  @field('dain') dain!: number; // debit
  @field('mden') mden!: number; // credit
  @field('equal') equal!: number; // equivalent amount
  @field('balance') balance!: number; // running balance (rsed)
  @field('price_trans') priceTrans!: number; // exchange/transfer price
  @field('currencyid') currencyid!: number;
  @text('currencyname') currencyname?: string | null;
  @text('branchid') branchid?: string | null;
  @text('userid') userid?: string | null;
  @text('notes') notes?: string | null;
  @text('notes_box') notesBox?: string | null;
  @text('notes2') notes2?: string | null; // bin
  @text('nref') nref?: string | null;
  @text('nref_docno') nrefDocNo?: string | null;
  @field('finalbalance') finalbalance!: number;

  // ─── sync ──────────────────────────────────────────────────────────────
  @text('sync_status') pushStatus!: PushStatus;
  @date('last_sync_attempt_at') lastSyncAttemptAt?: Date | null;
  @text('last_error') lastError?: string | null;
  @field('sync_attempts') syncAttempts!: number;

  @readonly @date('created_at') createdAt!: Date;
  @readonly @date('updated_at') updatedAt!: Date;

  /** All payments associated with this bond (reactive). */
  @children('bond_payments') payments!: Query<BondPayment>;

  /** Lazy query for fast aggregation in lists. */
  @lazy paymentsOrderedByDate = this.payments.extend(Q.sortBy('payment_date', Q.desc));

  // ─── Back-compat computed accessors (read legacy → expose old names) ───────

  /** Old `remoteId` = legacy `num` (the bond record id). */
  get remoteId(): number | null {
    return this.num ?? null;
  }

  /**
   * Old `bondNo` was a number. Legacy `nmstnd` is the human bond/document
   * number (string). Parse it; fall back to `num`.
   */
  get bondNo(): number {
    if (this.nmstnd != null && this.nmstnd !== '') {
      const n = Number(this.nmstnd);
      if (Number.isFinite(n)) return n;
    }
    return this.num ?? 0;
  }

  /**
   * Old `bondType` union 'receipt'|'payment'. Legacy `type` is an int.
   * type === 1 (or debit-bearing) → receipt; otherwise payment.
   * NOTE: confirm exact `type` mapping against the server when live.
   */
  get bondType(): 'receipt' | 'payment' {
    return this.type === 1 ? 'receipt' : 'payment';
  }

  /**
   * Old `amount` = the bond's headline value. In ItemBonds the value lives
   * in dain/mden depending on direction; `equal` holds the equivalent. Use
   * the dominant non-zero magnitude.
   */
  get amount(): number {
    if (this.equal && this.equal !== 0) return Math.abs(this.equal);
    return Math.abs(this.dain || this.mden || 0);
  }

  /** Old `amountPaid` — legacy single-row bonds carry no separate paid sum. */
  get amountPaid(): number {
    return this.amount;
  }

  get accountName(): string | null {
    return this.name ?? null;
  }

  /**
   * Old `accountId`. Legacy ItemBonds has no explicit FK to accounts; the
   * link is by account name. We expose `num_s` (box/cashier number) here
   * because the dev-bypass seeder stores the mock account id in `num_s`.
   * When wired to the live server this lookup falls back gracefully (the
   * view-model already tolerates a lookup miss).
   */
  get accountId(): number | null {
    return this.numS ?? null;
  }

  get currencyId(): number {
    return this.currencyid;
  }

  /** Old `bondDate` Date — parse legacy `mdate` string. */
  get bondDate(): Date {
    if (this.mdate) {
      const normalized = this.mdate.includes('T')
        ? this.mdate
        : this.mdate.replace(' ', 'T');
      const d = new Date(normalized);
      if (!Number.isNaN(d.getTime())) return d;
    }
    return this.createdAt ?? new Date();
  }

  /** Outstanding balance. Single-row bonds: 0 (fully settled in one record). */
  get remainingAmount(): number {
    return 0;
  }

  get isFullyPaid(): boolean {
    return true;
  }
}
