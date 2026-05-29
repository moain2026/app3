/**
 * ReadingsHistoryScreen — historical (multi-month) view of one subscriber's
 * readings.
 *
 * Reached from `navigation.navigate('ReadingsHistory', { num })`.
 *
 * Layout:
 *   • AppHeader (showBack) with subscriber name.
 *   • Header card: account num + name + alias + place/book/group meta.
 *   • Chart placeholder (Wave 6-Β: real Victory-Native line chart).
 *   • Table of past N months: month/ks/kh/cas/avg.
 *
 * Fully wired to WatermelonDB:
 *   • Subscriber lookup (`account`) subscribes to `observeAccountByCode(num)`.
 *   • The readings table subscribes to `observeReadingsByAccount(account.num)`
 *     and renders the actual reading rows the device holds for this
 *     subscriber (no fabricated multi-month history — the local store keeps
 *     one row per current period; a server-side rollup is out of scope).
 */

import { useRoute, type RouteProp } from '@react-navigation/native';
import React, { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { ScrollView, StyleSheet, Text, View } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { Subscription } from 'rxjs';
import Feather from 'react-native-vector-icons/Feather';

import { AppHeader } from '@/components/layout/AppHeader';
import { ReportTable } from '@/components/reports/ReportTable';
import { Card, EmptyState, SectionHeader } from '@/design-system/components';
import { useTheme } from '@/design-system/theme';
import { spacing } from '@/design-system/tokens/spacing';
import type { Account } from '@/database/models/Account';
import type { Reading } from '@/database/models/Reading';
import type { MockAccount } from '@/mocks/accounts';
import type { MainStackParamList } from '@/navigation/types';
import {
  observeAccountByCode,
  observeReadingsByAccount,
} from '@/services/repository';
import { observeReadingsByAccount } from '@/services/repository/readingsRepository';
import { toMockAccount } from '@/services/repository/viewModels';

export function ReadingsHistoryScreen(): React.JSX.Element {
  const { t } = useTranslation();
  const { colors } = useTheme();
  const route = useRoute<RouteProp<MainStackParamList, 'ReadingsHistory'>>();
  const num = route.params?.num ?? '0001';

  // undefined = loading, null = resolved not-found, MockAccount = resolved.
  const [account, setAccount] = useState<MockAccount | null | undefined>(
    undefined,
  );
  // Account record id (legacy num) used to query the readings table.
  const [accountNum, setAccountNum] = useState<number | null>(null);
  const [history, setHistory] = useState<Reading[]>([]);

  useEffect(() => {
    let sub: Subscription | null = null;
    sub = observeAccountByCode(num).subscribe({
      next: (row: Account | null) => {
        setAccount(row != null ? toMockAccount(row) : null);
        setAccountNum(row != null ? row.num : null);
      },
      error: () => {
        setAccount(null);
        setAccountNum(null);
      },
    });
    return () => {
      if (sub != null) sub.unsubscribe();
    };
  }, [num]);

  // Live readings for this subscriber.
  useEffect(() => {
    if (accountNum == null) {
      setHistory([]);
      return;
    }
    let sub: Subscription | null = null;
    sub = observeReadingsByAccount(accountNum).subscribe({
      next: (rows) => setHistory(rows),
      error: () => setHistory([]),
    });
    return () => {
      if (sub != null) sub.unsubscribe();
    };
  }, [accountNum]);

  // ─── Loading (waiting on first DB emission) ──────────────────
  if (account === undefined) {
    return (
      <SafeAreaView
        style={[styles.flex, { backgroundColor: colors.background }]}
        edges={['top']}
      >
        <AppHeader title={t('readings.history.title')} showBack />
      </SafeAreaView>
    );
  }

  // ─── Empty (subscriber not found) ────────────────────────────
  if (account === null) {
    return (
      <SafeAreaView
        style={[styles.flex, { backgroundColor: colors.background }]}
        edges={['top']}
      >
        <AppHeader title={t('readings.history.title')} showBack />
        <EmptyState
          icon="user-x"
          title={t('readings.history.notFoundTitle')}
          subtitle={t('readings.history.notFoundSubtitle')}
        />
      </SafeAreaView>
    );
  }

  // ─── Derive simple consumption stats ─────────────────────────
  const consumptions = history.map((h) => h.actualConsumption ?? 0);
  const avg =
    consumptions.length > 0
      ? Math.round(consumptions.reduce((s, c) => s + c, 0) / consumptions.length)
      : 0;
  const max = consumptions.length > 0 ? Math.max(...consumptions) : 0;
  const min = consumptions.length > 0 ? Math.min(...consumptions) : 0;

  return (
    <SafeAreaView
      style={[styles.flex, { backgroundColor: colors.background }]}
      edges={['top']}
    >
      <AppHeader title={t('readings.history.title')} showBack />

      <ScrollView contentContainerStyle={styles.scroll}>
        {/* ─── Subscriber header ──────────────────────────────── */}
        <Card variant="outlined" style={styles.subHeader}>
          <View style={styles.subHeaderTop}>
            <Feather name="user" size={20} color={colors.brandSecondary} />
            <View style={styles.subHeaderBody}>
              <Text
                style={[styles.subTitle, { color: colors.textPrimary }]}
              >
                {account.name}
              </Text>
              <Text
                style={[styles.subAlias, { color: colors.textTertiary }]}
              >
                {account.nameT ?? '—'}
              </Text>
            </View>
            <View
              style={[
                styles.numPill,
                { backgroundColor: colors.brandPrimarySoft },
              ]}
            >
              <Text style={[styles.numText, { color: colors.brandSecondary }]}>
                #{account.num}
              </Text>
            </View>
          </View>

          <View
            style={[styles.divider, { backgroundColor: colors.border }]}
          />

          <View style={styles.metaRow}>
            <Text style={[styles.metaLabel, { color: colors.textTertiary }]}>
              {t('readings.history.place')}
            </Text>
            <Text style={[styles.metaValue, { color: colors.textPrimary }]}>
              {account.placeName ?? '—'}
            </Text>
          </View>
        </Card>

        {/* ─── Consumption KPIs ───────────────────────────────── */}
        <View style={styles.kpiRow}>
          <Card variant="outlined" style={styles.kpi}>
            <Text style={[styles.kpiLabel, { color: colors.textTertiary }]}>
              {t('readings.history.avg')}
            </Text>
            <Text style={[styles.kpiValue, { color: colors.textPrimary }]}>
              {avg}
            </Text>
          </Card>
          <Card variant="outlined" style={styles.kpi}>
            <Text style={[styles.kpiLabel, { color: colors.textTertiary }]}>
              {t('readings.history.max')}
            </Text>
            <Text style={[styles.kpiValue, { color: colors.warning }]}>
              {max}
            </Text>
          </Card>
          <Card variant="outlined" style={styles.kpi}>
            <Text style={[styles.kpiLabel, { color: colors.textTertiary }]}>
              {t('readings.history.min')}
            </Text>
            <Text style={[styles.kpiValue, { color: colors.success }]}>
              {min}
            </Text>
          </Card>
        </View>

        {/* ─── Chart placeholder (Wave 6-Β) ───────────────────── */}
        <Card variant="outlined" style={styles.chartCard}>
          <View style={styles.chartPlaceholder}>
            <Feather name="trending-up" size={32} color={colors.textTertiary} />
            <Text style={[styles.chartHint, { color: colors.textTertiary }]}>
              {t('readings.history.chartPlaceholder')}
            </Text>
          </View>
        </Card>

        {/* ─── History table ──────────────────────────────────── */}
        <SectionHeader title={t('readings.history.tableTitle')} />

        {history.length === 0 ? (
          <EmptyState
            icon="inbox"
            title={t('readings.history.emptyTitle')}
            subtitle={t('readings.history.emptySubtitle')}
          />
        ) : (
          <ReportTable
            data={history}
            keyExtractor={(r) => r.localUuid}
            columns={[
              { key: 'date', label: t('readings.history.col.date'), width: 110, accessor: (r) => (r.readingDate ?? r.updatedAt).toISOString().slice(0, 10) },
              { key: 'ks', label: t('readings.history.col.previous'), width: 90, accessor: (r) => r.ks },
              { key: 'kh', label: t('readings.history.col.current'), width: 90, accessor: (r) => r.kh ?? '—' },
              {
                key: 'cas',
                label: t('readings.history.col.consumption'),
                width: 100,
                render: (r) => {
                  const cons = r.actualConsumption ?? 0;
                  return (
                    <Text
                      style={{
                        color: cons > r.asts ? colors.warning : colors.textPrimary,
                        fontSize: 12,
                        fontWeight: '700',
                      }}
                    >
                      {cons}
                    </Text>
                  );
                },
              },
              { key: 'asts', label: t('readings.history.col.avg'), width: 80, accessor: (r) => r.asts },
            ]}
          />
        )}
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  chartCard: {
    marginVertical: spacing[3],
  },
  chartHint: {
    fontSize: 11,
    textAlign: 'center',
  },
  chartPlaceholder: {
    alignItems: 'center',
    gap: spacing[2],
    paddingVertical: spacing[5],
  },
  divider: {
    height: StyleSheet.hairlineWidth,
    marginVertical: spacing[3],
  },
  flex: { flex: 1 },
  kpi: {
    flex: 1,
  },
  kpiLabel: {
    fontSize: 11,
    textAlign: 'right',
  },
  kpiRow: {
    flexDirection: 'row',
    gap: spacing[2],
    marginTop: spacing[2],
  },
  kpiValue: {
    fontSize: 20,
    fontWeight: '700',
    marginTop: 4,
    textAlign: 'right',
  },
  metaLabel: {
    fontSize: 12,
  },
  metaRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  metaValue: {
    fontSize: 13,
    fontWeight: '600',
  },
  numPill: {
    borderRadius: 6,
    paddingHorizontal: 8,
    paddingVertical: 3,
  },
  numText: {
    fontSize: 12,
    fontWeight: '700',
  },
  scroll: {
    paddingBottom: spacing[6],
    paddingHorizontal: spacing[4],
    paddingTop: spacing[3],
  },
  subAlias: {
    fontSize: 12,
    marginTop: 2,
    textAlign: 'right',
  },
  subHeader: {
    marginBottom: spacing[2],
  },
  subHeaderBody: {
    flex: 1,
  },
  subHeaderTop: {
    alignItems: 'center',
    flexDirection: 'row',
    gap: spacing[2],
  },
  subTitle: {
    fontSize: 15,
    fontWeight: '700',
    textAlign: 'right',
  },
});
