/**
 * BalanceDetailsReportScreen — "كشف حساب تفصيلي".
 *
 * Date-ordered ledger for ONE account. Backed by the live
 * GetRepBalanceDetailsByDate endpoint (BalanceStateDetails rows:
 * mdate/nref/name/dain/mden/rsed). `dain` = دائن/له (credit),
 * `mden` = مدين/عليه (debit), `rsed` = running balance, `nref` = doc ref.
 *
 * The user must pick an account; until then we show an empty prompt.
 */

import React, { useCallback, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { StyleSheet, Text, View } from 'react-native';

import { AccountPicker } from '@/components/pickers';
import type { PeriodValue } from '@/components/pickers';
import { ReportScreenLayout } from '@/components/reports/ReportScreenLayout';
import { ReportTable } from '@/components/reports/ReportTable';
import { Card, EmptyState, FormField } from '@/design-system/components';
import { useTheme } from '@/design-system/theme';
import { spacing } from '@/design-system/tokens/spacing';
import {
  useReportData,
  numField,
  strField,
  periodToRange,
} from '@/hooks/useReportData';
import { repBalanceDetailsParams } from '@/services/sync/pull/requestScope';
import type { ReportRow } from '@/services/api/schemas/reports';
import type { MockAccount } from '@/mocks/accounts';

export function BalanceDetailsReportScreen(): React.JSX.Element {
  const { t } = useTranslation();
  const { colors } = useTheme();

  const [account, setAccount] = useState<MockAccount | null>(null);
  const [pickerOpen, setPickerOpen] = useState(false);
  const [period, setPeriod] = useState<PeriodValue>({ preset: 'thisMonth' });

  const accountNum = account?.num ?? '';

  const buildParams = useCallback(() => {
    const range = periodToRange(period);
    return repBalanceDetailsParams(accountNum, range);
  }, [accountNum, period]);

  // Only fetch once an account is chosen.
  const enabled = accountNum !== '';
  const { rows, loading, error, refetch } = useReportData(
    'getRepBalanceDetailsByDate',
    buildParams,
    [accountNum, period, enabled],
    { enabled },
  );

  const showTable = enabled && (loading || rows.length > 0);

  return (
    <ReportScreenLayout
      title={t('reports.entries.BalanceDetailsReport.title')}
      subtitle={t('reports.entries.BalanceDetailsReport.subtitle')}
      loading={enabled ? loading : false}
      error={enabled ? error : null}
      onRetry={refetch}
      onPeriodChange={setPeriod}
      summary={
        <Card variant="outlined" style={styles.accountCard}>
          <FormField
            label={t('reports.fields.account')}
            value={account ? `${account.num} — ${account.name}` : ''}
            placeholder={t('pickers.account.searchPlaceholder')}
            readOnly
            onPress={() => setPickerOpen(true)}
            leadingIcon="user"
          />
          {account ? (
            <View style={styles.balanceLine}>
              <Text style={[styles.balanceLabel, { color: colors.textTertiary }]}>
                {t('reports.kpi.currentBalance')}
              </Text>
              <Text
                style={[
                  styles.balanceValue,
                  {
                    color:
                      account.balance >= 0 ? colors.danger : colors.accent,
                  },
                ]}
              >
                {account.balance.toLocaleString('en-US')} ر.ي
              </Text>
            </View>
          ) : null}
        </Card>
      }
    >
      {!enabled ? (
        <EmptyState
          icon="user"
          title={t('reports.fields.account')}
          subtitle={t('pickers.account.searchPlaceholder')}
        />
      ) : !loading && rows.length === 0 ? (
        <EmptyState
          icon="inbox"
          title={t('reports.empty.title')}
          subtitle={t('reports.empty.subtitle')}
        />
      ) : showTable ? (
        <ReportTable<ReportRow>
          data={rows}
          keyExtractor={(r, i) => `${strField(r, 'nref')}-${i}`}
          columns={[
            {
              key: 'date',
              label: t('reports.columns.date'),
              width: 100,
              accessor: (r) => strField(r, 'mdate'),
            },
            {
              key: 'doc',
              label: t('reports.columns.docNo'),
              width: 90,
              accessor: (r) => strField(r, 'nref'),
            },
            {
              key: 'desc',
              label: t('reports.columns.description'),
              width: 150,
              accessor: (r) => strField(r, 'name'),
            },
            {
              key: 'debit',
              label: t('reports.columns.debit'),
              width: 90,
              render: (r) => {
                const v = numField(r, 'mden');
                return (
                  <Text style={{ color: colors.danger, fontSize: 12, fontWeight: '600' }}>
                    {v > 0 ? v.toLocaleString('en-US') : '—'}
                  </Text>
                );
              },
            },
            {
              key: 'credit',
              label: t('reports.columns.credit'),
              width: 90,
              render: (r) => {
                const v = numField(r, 'dain');
                return (
                  <Text style={{ color: colors.accent, fontSize: 12, fontWeight: '600' }}>
                    {v > 0 ? v.toLocaleString('en-US') : '—'}
                  </Text>
                );
              },
            },
            {
              key: 'bal',
              label: t('reports.columns.balance'),
              width: 100,
              render: (r) => (
                <Text style={{ color: colors.textPrimary, fontSize: 12, fontWeight: '700' }}>
                  {numField(r, 'rsed').toLocaleString('en-US')}
                </Text>
              ),
            },
          ]}
        />
      ) : null}

      <AccountPicker
        visible={pickerOpen}
        onClose={() => setPickerOpen(false)}
        onSelect={(a) => {
          setAccount(a);
          setPickerOpen(false);
        }}
      />
    </ReportScreenLayout>
  );
}

const styles = StyleSheet.create({
  accountCard: {
    marginBottom: spacing[2],
  },
  balanceLabel: {
    fontSize: 12,
  },
  balanceLine: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginTop: spacing[2],
  },
  balanceValue: {
    fontSize: 14,
    fontWeight: '700',
  },
});
