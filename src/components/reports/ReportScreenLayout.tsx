/**
 * ReportScreenLayout — shared layout shell for every report sub-screen.
 *
 * Mounts:
 *   • AppHeader with showBack
 *   • Filters card (period + optional extras)
 *   • Summary / KPI card (optional)
 *   • Body (the report table) with loading / error / empty states
 *   • Bottom action bar — "تصدير PDF" + "طباعة"
 *
 * The host screen owns the data-fetch (via `useReportData`) and feeds the
 * layout `loading` / `error` / `onRetry`. Period changes bubble up through
 * `onPeriodChange` so the host can re-query the server.
 */

import React, { useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
  ActivityIndicator,
  ScrollView,
  StyleSheet,
  Text,
  View,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

import { AppHeader } from '@/components/layout/AppHeader';
import { PeriodPicker, type PeriodValue } from '@/components/pickers';
import {
  Card,
  ErrorBanner,
  FormField,
  SecondaryButton,
} from '@/design-system/components';
import { useTheme } from '@/design-system/theme';
import { spacing } from '@/design-system/tokens/spacing';

export interface ReportScreenLayoutProps {
  title: string;
  /** Helper text displayed under the period picker. */
  subtitle?: string;
  /** Show the period picker (most reports use it; ReadingHeader does not). */
  showPeriod?: boolean;
  /** Optional summary header rendered above the main body (KPI cards). */
  summary?: React.ReactNode;
  /** Main body (the report table). */
  children: React.ReactNode;
  /** Notifies the host when the user picks a new period (to re-query). */
  onPeriodChange?: (period: PeriodValue) => void;
  /** True while the report request is in flight (renders a spinner). */
  loading?: boolean;
  /** Non-empty error message renders an ErrorBanner with a retry button. */
  error?: string | null;
  /** Retry handler wired to the ErrorBanner. */
  onRetry?: () => void;
}

const PERIOD_LABEL: Record<PeriodValue['preset'], string> = {
  today: 'اليوم',
  yesterday: 'أمس',
  thisWeek: 'هذا الأسبوع',
  thisMonth: 'هذا الشهر',
  last30Days: 'آخر 30 يوماً',
  custom: 'مخصص',
};

export function ReportScreenLayout(
  props: ReportScreenLayoutProps,
): React.JSX.Element {
  const {
    title,
    subtitle,
    showPeriod = true,
    summary,
    children,
    onPeriodChange,
    loading = false,
    error = null,
    onRetry,
  } = props;
  const { t } = useTranslation();
  const { colors } = useTheme();

  const [period, setPeriod] = useState<PeriodValue>({ preset: 'thisMonth' });
  const [periodOpen, setPeriodOpen] = useState(false);

  return (
    <SafeAreaView
      style={[styles.flex, { backgroundColor: colors.background }]}
      edges={['top']}
    >
      <AppHeader title={title} showBack />

      {error ? (
        <ErrorBanner
          message={error}
          variant="error"
          onRetry={onRetry}
          retryLabel={t('common.retry')}
        />
      ) : null}

      <ScrollView contentContainerStyle={styles.scroll}>
        {subtitle ? (
          <Text style={[styles.subtitle, { color: colors.textSecondary }]}>
            {subtitle}
          </Text>
        ) : null}

        {/* ─── Filters ────────────────────────────────────────── */}
        {showPeriod ? (
          <Card variant="outlined" style={styles.filtersCard}>
            <FormField
              label={t('pickers.period.title')}
              value={PERIOD_LABEL[period.preset]}
              readOnly
              onPress={() => setPeriodOpen(true)}
              leadingIcon="calendar"
            />
          </Card>
        ) : null}

        {/* ─── Optional summary KPIs ──────────────────────────── */}
        {summary}

        {/* ─── Body ───────────────────────────────────────────── */}
        {loading ? (
          <View style={styles.loadingBox}>
            <ActivityIndicator size="large" color={colors.brandPrimary} />
            <Text style={[styles.loadingText, { color: colors.textTertiary }]}>
              {t('common.loading')}
            </Text>
          </View>
        ) : (
          <View style={styles.body}>{children}</View>
        )}
      </ScrollView>

      {/* ─── Bottom action bar ──────────────────────────────── */}
      <View
        style={[
          styles.actionBar,
          {
            backgroundColor: colors.surface,
            borderTopColor: colors.border,
          },
        ]}
      >
        <SecondaryButton
          title={t('reports.actions.exportPdf')}
          icon="download"
          variant="outlined"
          onPress={() => {
            // TODO Wave 6-Β: implement PDF export via react-native-html-to-pdf.
          }}
          disabled
        />
        <SecondaryButton
          title={t('reports.actions.print')}
          icon="printer"
          variant="outlined"
          onPress={() => {
            // TODO Wave 6-Β: invoke PrinterService.printReport(...).
          }}
          disabled
        />
      </View>

      {showPeriod ? (
        <PeriodPicker
          visible={periodOpen}
          selectedPreset={period.preset}
          onSelect={(p) => {
            setPeriod(p);
            setPeriodOpen(false);
            onPeriodChange?.(p);
          }}
          onClose={() => setPeriodOpen(false)}
        />
      ) : null}
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  actionBar: {
    borderTopWidth: StyleSheet.hairlineWidth,
    flexDirection: 'row',
    gap: spacing[3],
    paddingHorizontal: spacing[4],
    paddingVertical: spacing[3],
  },
  body: {
    marginTop: spacing[3],
  },
  loadingBox: {
    alignItems: 'center',
    gap: spacing[2],
    paddingVertical: spacing[8],
  },
  loadingText: {
    fontSize: 12,
  },
  filtersCard: {
    marginBottom: spacing[2],
  },
  flex: { flex: 1 },
  scroll: {
    paddingBottom: spacing[6],
    paddingHorizontal: spacing[4],
    paddingTop: spacing[3],
  },
  subtitle: {
    fontSize: 13,
    marginBottom: spacing[3],
    textAlign: 'right',
  },
});
