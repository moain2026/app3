/**
 * BoxMoveReportScreen — "حركة الصندوق" (cashbox movements for a day).
 *
 * Backed by the live GetRepBoxMove endpoint (RepBoxMoves rows:
 * num/name/balance/dain/mden/fbalance/mdate). `dain` = قبض (receipts),
 * `mden` = صرف (payments), net = dain − mden. Scoped by sdate (period).
 */

import { useNavigation } from '@react-navigation/native';
import type { NativeStackNavigationProp } from '@react-navigation/native-stack';
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
import { repBoxMoveParams } from '@/services/sync/pull/requestScope';
import type { ReportRow } from '@/services/api/schemas/reports';
import type { MainStackParamList } from '@/navigation/types';

export function BoxMoveReportScreen(): React.JSX.Element {
  const { t } = useTranslation();
  const { colors } = useTheme();
  const navigation =
    useNavigation<NativeStackNavigationProp<MainStackParamList>>();

  const [period, setPeriod] = useState<PeriodValue>({ preset: 'today' });

  const buildParams = useCallback(() => {
    const range = periodToRange(period);
    return repBoxMoveParams(range);
  }, [period]);

  const { rows, loading, error, refetch } = useReportData(
    'getRepBoxMove',
    buildParams,
    [period],
  );

  const totalNet = rows.reduce(
    (s, r) => s + (numField(r, 'dain') - numField(r, 'mden')),
    0,
  );
  const sdate = periodToRange(period).startDate;

  return (
    <ReportScreenLayout
      title={t('reports.entries.BoxMoveReport.title')}
      subtitle={t('reports.entries.BoxMoveReport.subtitle')}
      loading={loading}
      error={error}
      onRetry={refetch}
      onPeriodChange={setPeriod}
      summary={
        <Card variant="outlined" style={styles.kpi}>
          <Text style={[styles.kpiLabel, { color: colors.textTertiary }]}>
            {t('reports.kpi.netMovement')}
          </Text>
          <Text
            style={[
              styles.kpiValue,
              { color: totalNet >= 0 ? colors.success : colors.danger },
            ]}
          >
            {totalNet.toLocaleString('en-US')} ر.ي
          </Text>
          <Text style={[styles.kpiHint, { color: colors.textTertiary }]}>
            {rows.length} {t('reports.kpi.daysCovered')}
          </Text>
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
        <>
          <ReportTable<ReportRow>
            data={rows}
            keyExtractor={(r, i) => `${strField(r, 'num')}-${i}`}
            columns={[
              {
                key: 'name',
                label: t('reports.columns.accountName'),
                width: 160,
                render: (r) => {
                  const num = strField(r, 'num');
                  return (
                    <Text
                      style={{
                        color: colors.accent,
                        fontSize: 12,
                        fontWeight: '700',
                        textDecorationLine: 'underline',
                      }}
                      onPress={() =>
                        navigation.navigate('BoxMoveDetailsReport', {
                          date: sdate,
                          num,
                        })
                      }
                    >
                      {strField(r, 'name') || num}
                    </Text>
                  );
                },
              },
              {
                key: 'rec',
                label: t('reports.columns.receipts'),
                width: 110,
                render: (r) => (
                  <Text style={{ color: colors.success, fontSize: 12, fontWeight: '600' }}>
                    {numField(r, 'dain').toLocaleString('en-US')}
                  </Text>
                ),
              },
              {
                key: 'pay',
                label: t('reports.columns.payments'),
                width: 110,
                render: (r) => (
                  <Text style={{ color: colors.danger, fontSize: 12, fontWeight: '600' }}>
                    {numField(r, 'mden').toLocaleString('en-US')}
                  </Text>
                ),
              },
              {
                key: 'net',
                label: t('reports.columns.net'),
                width: 110,
                render: (r) => {
                  const net = numField(r, 'dain') - numField(r, 'mden');
                  return (
                    <Text
                      style={{
                        color: net >= 0 ? colors.success : colors.danger,
                        fontSize: 12,
                        fontWeight: '700',
                      }}
                    >
                      {net.toLocaleString('en-US')}
                    </Text>
                  );
                },
              },
            ]}
          />

          <View style={styles.hint}>
            <Text style={[styles.hintText, { color: colors.textTertiary }]}>
              {t('reports.hints.tapDate')}
            </Text>
          </View>
        </>
      )}
    </ReportScreenLayout>
  );
}

const styles = StyleSheet.create({
  hint: {
    marginTop: spacing[2],
  },
  hintText: {
    fontSize: 11,
    fontStyle: 'italic',
    textAlign: 'right',
  },
  kpi: {
    marginBottom: spacing[2],
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
    fontSize: 18,
    fontWeight: '700',
    textAlign: 'right',
  },
});
