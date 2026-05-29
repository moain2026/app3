/**
 * Request Scope Helpers — العباسي تحصيل
 *
 * The legacy WCF backend scopes every list request to the logged-in
 * collector. Omitting these query params makes the server return an EMPTY
 * list (the exact "no data shows" bug observed on-device despite a healthy
 * connection). These helpers reproduce the EXACT param sets the legacy Java
 * client builds:
 *
 *   • GetListReadingCounter (ListReadingActivity.java):
 *       id    = appConfig.getUser().getNou()     ← collector serial (NOU)
 *       appId = appConfig.getAppId()             ← branch number ("1")
 *       (optional: nomstlm, notblh, nogroup, isnull — UI filters)
 *
 *   • GetListBonds / GetListBondsPayment (ListBondsActivity.java):
 *       nou   = appConfig.getUser().getNou()
 *       sdate = dd/MM/yyyy  (period start)
 *       edate = dd/MM/yyyy  (period end)
 *       num_s = appConfig.getUser().getNOA()     ← ONLY when SYS !== 1
 *       appId = appConfig.getAppId()
 *
 * SYS users (admins) are NOT scoped by NOA — they see all boxes. We still
 * send `nou` for them (legacy does too); the backend ignores it for SYS.
 */

import { getAppId, getBranchNumber, getCollectorNou, getCollectorNoa, getCollectorSys } from '../../storage/prefs';

/** Branch/app id sent on EVERY request. Defaults to the branch number. */
export function appIdParam(): string {
  // Legacy `AppConfig.getAppId()` defaults to "1"; we mirror that by falling
  // back to the configured branch number (also "1" by default).
  return getAppId() ?? getBranchNumber();
}

/** dd/MM/yyyy — the exact wire format from Utils.getDateFormatApi(). */
export function toApiDate(d: Date): string {
  const dd = String(d.getDate()).padStart(2, '0');
  const mm = String(d.getMonth() + 1).padStart(2, '0');
  const yyyy = d.getFullYear();
  return `${dd}/${mm}/${yyyy}`;
}

/**
 * Params for GetListReadingCounter.
 * Mirrors ListReadingActivity: id=NOU + appId (UI filters omitted on sync).
 */
export function readingListParams(): Record<string, string | number> {
  const params: Record<string, string | number> = { appId: appIdParam() };
  const nou = getCollectorNou();
  if (nou != null) {
    params.id = nou;
  }
  return params;
}

/**
 * Params for GetListBonds / GetListBondsPayment.
 * Mirrors ListBondsActivity.AsyncDownloadStart():
 *   nou, sdate, edate, [num_s when SYS!=1], appId.
 *
 * The sync runs a FULL pull, so we use a wide default window (5 years back
 * → today) to capture every bond the collector can see. The on-device
 * report screens still narrow this with their own date pickers.
 */
export function bondListParams(): Record<string, string | number> {
  const params: Record<string, string | number> = { appId: appIdParam() };

  const nou = getCollectorNou();
  if (nou != null) {
    params.nou = nou;
  }

  // Wide window: 5 years back → end of today.
  const now = new Date();
  const start = new Date(now.getFullYear() - 5, 0, 1);
  const end = new Date(now.getFullYear(), now.getMonth(), now.getDate());
  params.sdate = toApiDate(start);
  params.edate = toApiDate(end);

  // num_s (NOA) is only sent for NON-SYS users (collectors), matching
  // `if (getUser().getSYS() != 1) hashMap.put("num_s", ...)`.
  const sys = getCollectorSys();
  const noa = getCollectorNoa();
  if (sys !== 1 && noa != null) {
    params.num_s = noa;
  }

  return params;
}

// ─── Report params ──────────────────────────────────────────────────────────
// IMPORTANT: the report endpoints take the branch as `appid` (lowercase),
// NOT `appId` — confirmed in *ReportActivity.java (hashMap.put("appid", ...)).

type ReportPeriod = { startDate?: string; endDate?: string };

/** Convert an ISO date string (or undefined) to dd/MM/yyyy, or '' if blank. */
function isoToApiDate(iso?: string): string {
  if (!iso) return '';
  const d = new Date(iso);
  if (Number.isNaN(d.getTime())) return '';
  return toApiDate(d);
}

/**
 * GetRepBalanceHeader (BalanceStateReportActivity):
 *   currency = places.num (filter, optional) — blank = all
 *   num      = tgroup.num (filter, optional)  — blank = all
 *   type     = group filter text (optional)   — blank = all
 *   appid    = AppConfig.getAppId()
 */
export function repBalanceHeaderParams(): Record<string, string | number> {
  return { currency: '', num: '', type: '', appid: appIdParam() };
}

/**
 * GetRepBondsHeader (BondsHeaderReportActivity):
 *   sdate, edate (dd/MM/yyyy), num (optional), currency (optional), appid.
 */
export function repBondsHeaderParams(
  period: ReportPeriod,
): Record<string, string | number> {
  return {
    sdate: isoToApiDate(period.startDate),
    edate: isoToApiDate(period.endDate),
    num: '',
    currency: '',
    appid: appIdParam(),
  };
}

/**
 * GetRepBoxMove (BoxMovesReportActivity): sdate + appid.
 */
export function repBoxMoveParams(
  period: ReportPeriod,
): Record<string, string | number> {
  return { sdate: isoToApiDate(period.startDate), appid: appIdParam() };
}

/**
 * GetRepExpenses (ExpensesReportActivity): sdate + appid.
 */
export function repExpensesParams(
  period: ReportPeriod,
): Record<string, string | number> {
  return { sdate: isoToApiDate(period.startDate), appid: appIdParam() };
}

/**
 * GetRepReadingHeader (ListReadingReportActivity): type (tab index) + appid.
 */
export function repReadingHeaderParams(
  type = 0,
): Record<string, string | number> {
  return { type, appid: appIdParam() };
}

/**
 * GetRepBalanceDetailsByDate (BalanceStateDetailsReportActivity):
 *   num (account), currency, sdate, edate, appid, id (=NOU).
 */
export function repBalanceDetailsParams(
  accountNum: string,
  period: ReportPeriod,
): Record<string, string | number> {
  const params: Record<string, string | number> = {
    num: accountNum,
    currency: '',
    sdate: isoToApiDate(period.startDate),
    edate: isoToApiDate(period.endDate),
    appid: appIdParam(),
  };
  const nou = getCollectorNou();
  if (nou != null) params.id = nou;
  return params;
}

/**
 * GetRepBoxMoveDetails (BoxMovesDetailsReportActivity): sdate, num, appid.
 */
export function repBoxMoveDetailsParams(
  accountNum: string,
  period: ReportPeriod,
): Record<string, string | number> {
  return {
    sdate: isoToApiDate(period.startDate),
    num: accountNum,
    appid: appIdParam(),
  };
}
