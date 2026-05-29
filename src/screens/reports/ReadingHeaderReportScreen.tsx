/**
 * ReadingHeaderReportScreen — "تقرير القراءات".
 *
 * Backed by the live GetRepReadingHeader endpoint (RepReading rows:
 * num/name/ast). `ast` = consumption value. The legacy report is scoped by
 * `type` (tab index) + appid; we default to type=0 (all). Shows total
 * consumption + reading count, then a num/name/consumption table.
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
import { repReadingHeaderParams } from '@/services/sync/pull/requestScope';
import type { ReportRow } from '@/services/api/schemas/reports';

export function ReadingHeaderReportScreen(): React.JSX.Element {
  const { t } = useTranslation();
  const { colors } = useTheme();

  const buildParams = useCallback(() => repReadingHeaderParams(0), []);
  const { rows, loading, error, refetch } = useReportData(
    'getRepReadingHeader',
    buildParams,
    [],
  );

  const totalKwh = rows.reduce((s, r) => s + numField(r, 'ast'), 0);
  const count = rows.length;

  return (
    <ReportScreenLayout
      title={t('reports.entries.ReadingHeaderReport.title')}
      subtitle={t('reports.entries.ReadingHeaderReport.subtitle')}
      showPeriod={false}
      loading={loading}
      error={error}
      onRetry={refetch}
      summary={
        <View style={styles.kpiRow}>
          <Card variant="outlined" style={styles.kpi}>
            <Text style={[styles.kpiLabel, { color: colors.textTertiary }]}>
              {t('reports.kpi.totalConsumption')}
            </Text>
            <Text style={[styles.kpiValue, { color: colors.brandSecondary }]}>
              {totalKwh.toLocaleString('en-US')} {t('reports.kpi.kwhUnit')}
            </Text>
          </Card>
          <Card variant="outlined" style={styles.kpi}>
            <Text style={[styles.kpiLabel, { color: colors.textTertiary }]}>
              {t('reports.kpi.totalSubscribers')}
            </Text>
            <Text style={[styles.kpiValue, { color: colors.textPrimary }]}>
              {count}
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
              width: 80,
              accessor: (r) => strField(r, 'num'),
            },
            {
              key: 'name',
              label: t('reports.columns.accountName'),
              width: 200,
              accessor: (r) => strField(r, 'name'),
            },
            {
              key: 'kwh',
              label: t('reports.columns.consumption'),
              width: 110,
              render: (r) => (
                <Text style={{ color: colors.textPrimary, fontSize: 12, fontWeight: '600' }}>
                  {numField(r, 'ast').toLocaleString('en-US')}
                </Text>
              ),
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
  kpiRow: {
    flexDirection: 'row',
    gap: spacing[2],
    marginBottom: spacing[2],
  },
  kpiValue: {
    fontSize: 18,
    fontWeight: '700',
    textAlign: 'right',
  },
});
