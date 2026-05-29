/**
 * BoxMoveDetailsReportScreen — "تفاصيل حركة الصندوق" for a date + account.
 *
 * Backed by the live GetRepBoxMoveDetails endpoint (RepBoxMovesDetals rows:
 * amount/name/nmstnd/notes/typems). Receives `date` (+ optional `num`) via
 * navigation params from the BoxMove report.
 */

import { useRoute, type RouteProp } from '@react-navigation/native';
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
import { repBoxMoveDetailsParams } from '@/services/sync/pull/requestScope';
import type { ReportRow } from '@/services/api/schemas/reports';
import type { MainStackParamList } from '@/navigation/types';

export function BoxMoveDetailsReportScreen(): React.JSX.Element {
  const { t } = useTranslation();
  const { colors } = useTheme();
  const route = useRoute<RouteProp<MainStackParamList, 'BoxMoveDetailsReport'>>();
  const date = route.params?.date ?? new Date().toISOString().slice(0, 10);
  const num = route.params?.num ?? '';

  const buildParams = useCallback(
    () => repBoxMoveDetailsParams(num, { startDate: date, endDate: date }),
    [num, date],
  );

  const { rows, loading, error, refetch } = useReportData(
    'getRepBoxMoveDetails',
    buildParams,
    [num, date],
  );

  const total = rows.reduce((s, r) => s + numField(r, 'amount'), 0);

  return (
    <ReportScreenLayout
      title={t('reports.entries.BoxMoveDetailsReport.title')}
      subtitle={t('reports.entries.BoxMoveDetailsReport.subtitle')}
      showPeriod={false}
      loading={loading}
      error={error}
      onRetry={refetch}
      summary={
        <Card variant="outlined" style={styles.kpi}>
          <View style={styles.dateRow}>
            <Text style={[styles.dateLabel, { color: colors.textTertiary }]}>
              {t('reports.kpi.date')}
            </Text>
            <Text style={[styles.dateValue, { color: colors.textPrimary }]}>
              {date}
            </Text>
          </View>
          <View style={styles.dateRow}>
            <Text style={[styles.dateLabel, { color: colors.textTertiary }]}>
              {t('reports.kpi.netMovement')}
            </Text>
            <Text
              style={[
                styles.dateValue,
                { color: total >= 0 ? colors.success : colors.danger },
              ]}
            >
              {total.toLocaleString('en-US')} ر.ي
            </Text>
          </View>
        </Card>
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
          keyExtractor={(r, i) => `${strField(r, 'name')}-${i}`}
          columns={[
            {
              key: 'type',
              label: t('reports.columns.type'),
              width: 90,
              accessor: (r) => strField(r, 'typems'),
            },
            {
              key: 'name',
              label: t('reports.columns.accountName'),
              width: 160,
              accessor: (r) => strField(r, 'name'),
            },
            {
              key: 'notes',
              label: t('reports.columns.description'),
              width: 150,
              accessor: (r) => strField(r, 'notes'),
            },
            {
              key: 'amt',
              label: t('reports.columns.amount'),
              width: 110,
              render: (r) => (
                <Text
                  style={{
                    color: colors.textPrimary,
                    fontSize: 12,
                    fontWeight: '700',
                  }}
                >
                  {numField(r, 'amount').toLocaleString('en-US')} ر.ي
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
  dateLabel: {
    fontSize: 12,
  },
  dateRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    paddingVertical: spacing[1],
  },
  dateValue: {
    fontSize: 14,
    fontWeight: '700',
  },
  kpi: {
    marginBottom: spacing[2],
  },
});
