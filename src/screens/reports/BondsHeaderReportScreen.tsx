/**
 * BondsHeaderReportScreen — "تقرير السندات".
 *
 * Lists all bonds within a date range. Backed by the live GetRepBondsHeader
 * endpoint (BondsHeader rows: num/name/balance/dain/mden/mdate/type/
 * currencyname). `dain` = دائن/قبض (receipt), `mden` = مدين/صرف (payment).
 *
 * The period picker drives sdate/edate; changing it re-queries the server.
 */

import React, { useCallback, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { StyleSheet, Text, View } from 'react-native';

import { ReportScreenLayout } from '@/components/reports/ReportScreenLayout';
import { ReportTable } from '@/components/reports/ReportTable';
import { Card, EmptyState } from '@/design-system/components';
import type { PeriodValue } from '@/components/pickers';
import { useTheme } from '@/design-system/theme';
import { spacing } from '@/design-system/tokens/spacing';
import {
  useReportData,
  numField,
  strField,
  periodToRange,
} from '@/hooks/useReportData';
import { repBondsHeaderParams } from '@/services/sync/pull/requestScope';
import type { ReportRow } from '@/services/api/schemas/reports';

export function BondsHeaderReportScreen(): React.JSX.Element {
  const { t } = useTranslation();
  const { colors } = useTheme();

  const [period, setPeriod] = useState<PeriodValue>({ preset: 'thisMonth' });

  const buildParams = useCallback(() => {
    const range = periodToRange(period);
    return repBondsHeaderParams(range);
  }, [period]);

  const { rows, loading, error, refetch } = useReportData(
    'getRepBondsHeader',
    buildParams,
    [period],
  );

  // dain = receipt amount, mden = payment amount.
  const totalReceipts = rows.reduce((s, r) => s + numField(r, 'dain'), 0);
  const totalPayments = rows.reduce((s, r) => s + numField(r, 'mden'), 0);
  const receiptCount = rows.filter((r) => numField(r, 'dain') > 0).length;
  const paymentCount = rows.filter((r) => numField(r, 'mden') > 0).length;

  return (
    <ReportScreenLayout
      title={t('reports.entries.BondsHeaderReport.title')}
      subtitle={t('reports.entries.BondsHeaderReport.subtitle')}
      loading={loading}
      error={error}
      onRetry={refetch}
      onPeriodChange={setPeriod}
      summary={
        <View style={styles.summaryRow}>
          <Card variant="outlined" style={styles.kpi}>
            <Text style={[styles.kpiLabel, { color: colors.textTertiary }]}>
              {t('reports.kpi.totalReceipts')}
            </Text>
            <Text style={[styles.kpiValue, { color: colors.success }]}>
              {totalReceipts.toLocaleString('en-US')} ر.ي
            </Text>
            <Text style={[styles.kpiHint, { color: colors.textTertiary }]}>
              {receiptCount} {t('reports.kpi.bondCount')}
            </Text>
          </Card>
          <Card variant="outlined" style={styles.kpi}>
            <Text style={[styles.kpiLabel, { color: colors.textTertiary }]}>
              {t('reports.kpi.totalPayments')}
            </Text>
            <Text style={[styles.kpiValue, { color: colors.danger }]}>
              {totalPayments.toLocaleString('en-US')} ر.ي
            </Text>
            <Text style={[styles.kpiHint, { color: colors.textTertiary }]}>
              {paymentCount} {t('reports.kpi.bondCount')}
            </Text>
          </Card>
        </View>
      }
    >
      {!loading && rows.length === 0 ? (
        <EmptyState
          icon="inbox"
          title={t('reports.empty.title')}
          subtitle={t('reports.empty.subtitle')}
        />
      ) : (
        <ReportTable<ReportRow>
          data={rows}
          keyExtractor={(r, i) => `${strField(r, 'num')}-${i}`}
          columns={[
            {
              key: 'num',
              label: t('reports.columns.bondNo'),
              width: 80,
              accessor: (r) => strField(r, 'num'),
            },
            {
              key: 'date',
              label: t('reports.columns.date'),
              width: 110,
              accessor: (r) => strField(r, 'mdate'),
            },
            {
              key: 'name',
              label: t('reports.columns.accountName'),
              width: 180,
              accessor: (r) => strField(r, 'name'),
            },
            {
              key: 'amount',
              label: t('reports.columns.amount'),
              width: 120,
              render: (r) => {
                const dain = numField(r, 'dain');
                const mden = numField(r, 'mden');
                const isReceipt = dain >= mden;
                const amount = isReceipt ? dain : mden;
                return (
                  <Text
                    style={{
                      color: isReceipt ? colors.success : colors.danger,
                      fontSize: 12,
                      fontWeight: '700',
                    }}
                  >
                    {amount.toLocaleString('en-US')} ر.ي
                  </Text>
                );
              },
            },
          ]}
        />
      )}
    </ReportScreenLayout>
  );
}

const styles = StyleSheet.create({
  kpi: {
    flex: 1,
  },
  kpiHint: {
    fontSize: 11,
    marginTop: 2,
    textAlign: 'right',
  },
  kpiLabel: {
    fontSize: 11,
    marginBottom: 2,
    textAlign: 'right',
  },
  kpiValue: {
    fontSize: 16,
    fontWeight: '700',
    textAlign: 'right',
  },
  summaryRow: {
    flexDirection: 'row',
    gap: spacing[2],
    marginBottom: spacing[2],
  },
});
