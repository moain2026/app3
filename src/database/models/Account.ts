/**
 * Account Model — قائمة الحسابات
 *
 * Read-only mirror (server is source of truth). Local edits are not supported.
 */

import { Model } from '@nozbe/watermelondb';
import { date, field, readonly, text } from '@nozbe/watermelondb/decorators';

/**
 * Account Model — قائمة الحسابات
 *
 * Read-only mirror (server is source of truth). Columns mirror
 * entities/Accounts.java (v28) VERBATIM — see ISS-12.
 */
export class Account extends Model {
  static table = 'accounts';

  @field('num') num!: number; // remote id / sequence
  @text('name') name!: string; // account name
  @text('namet') namet?: string | null; // alias name
  @text('namep') namep?: string | null; // parent/place name
  @text('noadad') noadad?: string | null; // meter/account code
  @field('nog') nog!: number; // group number
  @field('nomstlm') nomstlm!: number; // receiver/area number
  @field('notblh') notblh!: number; // book/tabla number
  @field('balance') balance!: number; // running balance
  @field('dain') dain!: number; // debit total
  @field('mden') mden!: number; // credit total
  @text('tel') tel?: string | null; // phone
  @field('type') type!: number; // account type

  @date('last_synced_at') lastSyncedAt?: Date | null;

  @readonly @date('created_at') createdAt!: Date;
  @readonly @date('updated_at') updatedAt!: Date;

  /**
   * Back-compat alias. The legacy `num` IS the account record id (what the
   * old code called `remoteId`). Kept so bond view-models that build a
   * `remoteId → Account` index keep compiling until the bonds wave realigns
   * them. New code should read `num` directly.
   */
  get remoteId(): number {
    return this.num;
  }
}
