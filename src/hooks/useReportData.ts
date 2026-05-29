/**
 * useReportData — generic report fetch hook.
 *
 * Every report screen (ميزان عام / السندات / حركة الصندوق / المصروفات / القراءات)
 * calls one of the GetRep* endpoints, which all return a free-form array of
 * rows wrapped in the WCF `<Method>Result` envelope. This hook centralizes:
 *
 *   • building the request params (caller-supplied, recomputed on `deps`),
 *   • calling `api.call(endpoint, { params })`,
 *   • parsing through `parseReportList` (unwraps the envelope + validates),
 *   • exposing `{ rows, loading, error, refetch }`.
 *
 * The host screen owns the period picker; whenever the period (or any other
 * filter) changes it bumps `deps`, and the hook re-fetches. A manual
 * `refetch()` is also returned for the ErrorBanner retry button.
 */

import { useCallback, useEffect, useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';

import { api } from '@/services/api';
import type { EndpointKey } from '@/services/api/endpoints';
import { parseReportList } from '@/services/api/mappers/reports.mapper';
import type { ReportRow } from '@/services/api/schemas/reports';
import { AppError } from '@/utils/errors';

export interface UseReportDataResult {
  rows: ReportRow[];
  loading: boolean;
  error: string | null;
  refetch: () => void;
}

type ParamMap = Record<string, string | number | boolean | undefined>;

export function useReportData(
  endpoint: EndpointKey,
  buildParams: () => ParamMap,
  deps: ReadonlyArray<unknown> = [],
): UseReportDataResult {
  const { t } = useTranslation();
  const [rows, setRows] = useState<ReportRow[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  // Guard against setting state after unmount / out-of-order responses.
  const reqIdRef = useRef(0);

  const run = useCallback(async () => {
    const myReq = ++reqIdRef.current;
    setLoading(true);
    setError(null);
    try {
      const params = buildParams();
      const raw = await api.call(endpoint, { params });
      const list = parseReportList(raw);
      if (reqIdRef.current !== myReq) return; // stale
      setRows(list);
    } catch (e) {
      if (reqIdRef.current !== myReq) return; // stale
      const msg =
        e instanceof AppError
          ? e.userMessage
          : t('reports.errors.loadFailed');
      setError(msg);
      setRows([]);
    } finally {
      if (reqIdRef.current === myReq) setLoading(false);
    }
    // buildParams is intentionally re-read on every run; deps drive re-fetch.
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [endpoint, t]);

  useEffect(() => {
    void run();
    return () => {
      // Invalidate any in-flight request on unmount / deps change.
      reqIdRef.current++;
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, deps);

  const refetch = useCallback(() => {
    void run();
  }, [run]);

  return { rows, loading, error, refetch };
}

/** Convenience: pull a numeric field from a loosely-typed report row. */
export function numField(row: ReportRow, ...keys: string[]): number {
  for (const k of keys) {
    const v = row[k];
    if (typeof v === 'number') return v;
    if (typeof v === 'string' && v.trim() !== '' && !Number.isNaN(Number(v))) {
      return Number(v);
    }
  }
  return 0;
}

/** Convenience: pull a string field from a loosely-typed report row. */
export function strField(row: ReportRow, ...keys: string[]): string {
  for (const k of keys) {
    const v = row[k];
    if (v != null && v !== '') return String(v);
  }
  return '';
}

// ─── Period preset → ISO date range ───────────────────────────────────────
export type PeriodPreset =
  | 'today'
  | 'yesterday'
  | 'thisWeek'
  | 'thisMonth'
  | 'last30Days'
  | 'custom';

export interface DateRange {
  startDate: string; // ISO yyyy-mm-dd
  endDate: string; // ISO yyyy-mm-dd
}

function iso(d: Date): string {
  const yyyy = d.getFullYear();
  const mm = String(d.getMonth() + 1).padStart(2, '0');
  const dd = String(d.getDate()).padStart(2, '0');
  return `${yyyy}-${mm}-${dd}`;
}

/**
 * Resolve a period (preset + optional custom ISO dates) into a concrete
 * start/end ISO range. Mirrors the presets offered by PeriodPicker.
 */
export function periodToRange(period: {
  preset: PeriodPreset;
  startDate?: string;
  endDate?: string;
}): DateRange {
  const now = new Date();
  const endOfToday = new Date(now.getFullYear(), now.getMonth(), now.getDate());

  switch (period.preset) {
    case 'today':
      return { startDate: iso(endOfToday), endDate: iso(endOfToday) };
    case 'yesterday': {
      const y = new Date(endOfToday);
      y.setDate(y.getDate() - 1);
      return { startDate: iso(y), endDate: iso(y) };
    }
    case 'thisWeek': {
      const s = new Date(endOfToday);
      s.setDate(s.getDate() - s.getDay()); // Sunday as week start
      return { startDate: iso(s), endDate: iso(endOfToday) };
    }
    case 'thisMonth': {
      const s = new Date(now.getFullYear(), now.getMonth(), 1);
      return { startDate: iso(s), endDate: iso(endOfToday) };
    }
    case 'last30Days': {
      const s = new Date(endOfToday);
      s.setDate(s.getDate() - 29);
      return { startDate: iso(s), endDate: iso(endOfToday) };
    }
    case 'custom':
      return {
        startDate: period.startDate ?? iso(endOfToday),
        endDate: period.endDate ?? iso(endOfToday),
      };
    default:
      return { startDate: iso(endOfToday), endDate: iso(endOfToday) };
  }
}
