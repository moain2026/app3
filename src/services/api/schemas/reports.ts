/**
 * Report Schemas — العباسي تحصيل
 *
 * Validates payloads for the 8+ report endpoints:
 *   /GetRepBalanceHeader, /GetRepBalanceDetails, /GetRepBalanceDetailsByDate,
 *   /GetRepBondsHeader, /GetRepBoxMove, /GetRepBoxMoveDetails,
 *   /GetRepExpenses, /GetRepReadingHeader, /GetAccountBalance,
 *   /GetAccountBalanceInfo, /GetBondPaymentRecordNext, /GetBondRecieptRcordNext,
 *   /GetListUserPlaces, /report1.
 *
 * The legacy backend returns a free-form array of rows for most reports.
 * Rather than over-validate (and risk breaking when the backend evolves),
 * we accept any object with string-keyed scalar values — the report screens
 * render them as data tables.
 */

import { z } from 'zod';

import { zEnvelope, zResultEnvelope } from './common';

// A "row" is a flat object: { col1: scalar, col2: scalar, ... }
const ScalarSchema = z.union([z.string(), z.number(), z.boolean(), z.null()]);
export const ReportRowSchema = z.record(z.string(), ScalarSchema);
export type ReportRow = z.infer<typeof ReportRowSchema>;

// The live WCF backend wraps every report array in a `<Method>Result` key
// (e.g. { "GetRepBalanceHeaderResult": [...] }). zResultEnvelope unwraps it
// BEFORE validation; we also tolerate the legacy { data: [...] } envelope
// and a bare array.
export const ReportListResponseSchema = z.union([
  zResultEnvelope(z.array(ReportRowSchema)),
  zEnvelope(z.array(ReportRowSchema)),
  z.array(ReportRowSchema),
]);
export type ReportListResponse = z.infer<typeof ReportListResponseSchema>;

// Single balance payload (numeric/string fields tolerated)
export const AccountBalanceResponseSchema = z.union([
  zResultEnvelope(ReportRowSchema),
  zEnvelope(ReportRowSchema),
  ReportRowSchema,
]);
export type AccountBalanceResponse = z.infer<typeof AccountBalanceResponseSchema>;
