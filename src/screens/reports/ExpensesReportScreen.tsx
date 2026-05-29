/**
 * ExpensesReportScreen — "المصروفات اليومية".
 *
 * Backed by the live GetRepExpenses endpoint (RepBoxMovesDetals rows:
 * amount/name/nmstnd/notes/typems). `typems` = expense category, `name` =
 * party/name, `notes` = description, `amount` = value. Scoped by sdate.
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
import { repExpensesParams } from '@/services/sync/pull/requestScope';
import type { ReportRow } from '@/services/api/schemas/reports';

const CATEGORY_COLOR: Record<string, string> = {
  وقود: '#BF360C',
  صيانة: '#0D47A1',
  مكتبية: '#33691E',
  أخرى: '#4A148C',
};

export function ExpensesReportScreen(): React.JSX.Element {
  const { t } = useTranslation();
  const { colors } = useTheme();

  const [period, setPeriod] = useState<PeriodValue>({ preset: 'today' });

  const buildParams = useCallback(() => {
    const range = periodToRange(period);
    return repExpensesParams(range);
  }, [period]);

  const { rows, loading, error, refetch } = useReportData(
    'getRepExpenses',
    buildParams,
    [period],
  );

  const total = rows.reduce((s, r) => s + numField(r, 'amount'), 0);
  const byCategory = rows.reduce<Record<string, number>>((acc, r) => {
    const cat = strField(r, 'typems') || 'أخرى';
    acc[cat] = (acc[cat] ?? 0) + numField(r, 'amount');
    return acc;
  }, {});

  return (
    <ReportScreenLayout
      title={t('reports.entries.ExpensesReport.title')}
      subtitle={t('reports.entries.ExpensesReport.subtitle')}
      loading={loading}
      error={error}
      onRetry={refetch}
      onPeriodChange={setPeriod}
      summary={
        <View>
          <Card variant="outlined" style={styles.totalCard}>
            <Text style={[styles.totalLabel, { color: colors.textTertiary }]}>
              {t('reports.kpi.totalExpenses')}
            </Text>
            <Text style={[styles.totalValue, { color: colors.danger }]}>
              {total.toLocaleString('en-US')} ر.ي
            </Text>
          </Card>
          <View style={styles.chips}>
            {Object.entries(byCategory).map(([cat, sum]) => (
              <View
                key={cat}
                style={[
                  styles.chip,
                  {
                    backgroundColor: colors.surface,
                    borderColor: CATEGORY_COLOR[cat] ?? colors.border,
                  },
                ]}
              >
                <Text
                  style={{
                    color: CATEGORY_COLOR[cat] ?? colors.textPrimary,
                    fontSize: 11,
                    fontWeight: '700',
                  }}
                >
                  {cat}: {sum.toLocaleString('en-US')}
                </Text>
              </View>
            ))}
          </View>
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
          keyExtractor={(r, i) => `${strField(r, 'name')}-${i}`}
          columns={[
            {
              key: 'cat',
              label: t('reports.columns.category'),
              width: 90,
              render: (r) => {
                const cat = strField(r, 'typems');
                return (
                  <Text
                    style={{
                      color: CATEGORY_COLOR[cat] ?? colors.textPrimary,
                      fontSize: 12,
                      fontWeight: '700',
                    }}
                  >
                    {cat}
                  </Text>
                );
              },
            },
            {
              key: 'name',
              label: t('reports.columns.accountName'),
              width: 140,
              accessor: (r) => strField(r, 'name'),
            },
            {
              key: 'desc',
              label: t('reports.columns.description'),
              width: 160,
              accessor: (r) => strField(r, 'notes'),
            },
            {
              key: 'amt',
              label: t('reports.columns.amount'),
              width: 110,
              render: (r) => (
                <Text style={{ color: colors.danger, fontSize: 12, fontWeight: '700' }}>
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
  chip: {
    borderRadius: 14,
    borderWidth: 1,
    paddingHorizontal: spacing[2] + 2,
    paddingVertical: 4,
  },
  chips: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: spacing[2],
    marginBottom: spacing[2],
  },
  totalCard: {
    marginBottom: spacing[2],
  },
  totalLabel: {
    fontSize: 11,
    marginBottom: 2,
    textAlign: 'right',
  },
  totalValue: {
    fontSize: 22,
    fontWeight: '800',
    textAlign: 'right',
  },
});
