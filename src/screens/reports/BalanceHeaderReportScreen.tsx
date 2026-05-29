/**
 * BalanceHeaderReportScreen — "ميزان عام".
 *
 * Lists every account with its current balance. Backed by the live
 * GetRepBalanceHeader endpoint (BalanceState rows: num/name/balance/
 * dain/mden/type). No period filter — the legacy report shows the
 * current balance snapshot, scoped only by branch (appid).
 *
 * Sign convention (confirmed from owner screenshots + balances PDF):
 *   balance > 0  → عليه / مدين  (customer owes)  → RED
 *   balance < 0  → له / دائن    (credit)         → BLUE
 */

import React, { useCallback } from 'react';
import { useTranslation } from 'react-i18next';
import { StyleSheet, Text, View } from 'react-native';

import { ReportScreenLayout } from '@/components/reports/ReportScreenLayout';
import { ReportTable } from '@/components/reports/ReportTable';
import { Card, EmptyState } from '@/design-system/components';
import { useTheme } from '@/design-system/theme';
import { spacing } from '@/design-system/tokens/spacing';
import {
  useReportData,
  numField,
  strField,
} from '@/hooks/useReportData';
import { repBalanceHeaderParams } from '@/services/sync/pull/requestScope';
import type { ReportRow } from '@/services/api/schemas/reports';

export function BalanceHeaderReportScreen(): React.JSX.Element {
  const { t } = useTranslation();
  const { colors } = useTheme();

  const buildParams = useCallback(() => repBalanceHeaderParams(), []);
  const { rows, loading, error, refetch } = useReportData(
    'getRepBalanceHeader',
    buildParams,
    [],
  );

  // ─── Derive summary KPIs from the live dataset ───────────────
  const totalAccounts = rows.length;
  const totalBalance = rows.reduce(
    (s, r) => s + numField(r, 'balance'),
    0,
  );

  return (
    <ReportScreenLayout
      title={t('reports.entries.BalanceHeaderReport.title')}
      subtitle={t('reports.entries.BalanceHeaderReport.subtitle')}
      showPeriod={false}
      loading={loading}
      error={error}
      onRetry={refetch}
      summary={
        <View style={styles.summaryRow}>
          <Card variant="outlined" style={styles.kpi}>
            <Text style={[styles.kpiLabel, { color: colors.textTertiary }]}>
              {t('reports.kpi.accounts')}
            </Text>
            <Text style={[styles.kpiValue, { color: colors.textPrimary }]}>
              {totalAccounts}
            </Text>
          </Card>
          <Card variant="outlined" style={styles.kpi}>
            <Text style={[styles.kpiLabel, { color: colors.textTertiary }]}>
              {t('reports.kpi.totalBalance')}
            </Text>
            <Text
              style={[
                styles.kpiValue,
                {
                  color: totalBalance >= 0 ? colors.danger : colors.accent,
                },
              ]}
            >
              {totalBalance.toLocaleString('en-US')} ر.ي
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
              label: t('reports.columns.accountNum'),
              width: 70,
              accessor: (r) => strField(r, 'num'),
            },
            {
              key: 'name',
              label: t('reports.columns.accountName'),
              width: 200,
              accessor: (r) => strField(r, 'name'),
            },
            {
              key: 'balance',
              label: t('reports.columns.balance'),
              width: 130,
              render: (r) => {
                const bal = numField(r, 'balance');
                return (
                  <Text
                    style={{
                      color: bal >= 0 ? colors.danger : colors.accent,
                      fontSize: 12,
                      fontWeight: '700',
                    }}
                  >
                    {bal.toLocaleString('en-US')} ر.ي
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
  kpiLabel: {
    fontSize: 11,
    marginBottom: 2,
    textAlign: 'right',
  },
  kpiValue: {
    fontSize: 18,
    fontWeight: '700',
    textAlign: 'right',
  },
  summaryRow: {
    flexDirection: 'row',
    gap: spacing[2],
    marginBottom: spacing[2],
  },
});
