/**
 * CompanyInfoScreen — جلب البيانات / بيانات الشركة
 *
 * Mirrors the legacy "جلب البيانات" screen (see
 * AGENT_CONTEXT/app1_legacy_context/UI_GROUND_TRUTH.md §0/§2):
 *   • A "جلب البيانات من السيرفر" action that runs a FULL sync (pull all
 *     reference + collector data, then push the local queue) and reports the
 *     exact record count back to the operator.
 *   • A read-only company card: إسم المؤسسة / الشعار / العنوان / رقم الهاتف.
 *
 * The company row is the single `company_info` record fed by GetCompanyData
 * on every sync; we observe it live so a successful fetch updates the card
 * without a manual refresh.
 *
 * Reachable from the drawer menu under "بيانات الشركة" / "جلب البيانات".
 */

import { Q } from '@nozbe/watermelondb';
import React, { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
  ActivityIndicator,
  Image,
  Pressable,
  ScrollView,
  StyleSheet,
  Text,
  ToastAndroid,
  View,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import Feather from 'react-native-vector-icons/Feather';
import type { Subscription } from 'rxjs';

import { AppHeader } from '@/components/layout/AppHeader';
import { database } from '@/database';
import type { CompanyInfo } from '@/database/models/CompanyInfo';
import { useTheme } from '@/design-system/theme';
import { syncNow } from '@/services/sync/syncCoordinator';
import { getBranchNumber, getLastSync } from '@/services/storage/prefs';
import { logger } from '@/utils/logger';

const log = logger.scope('FetchDataScreen');

interface CompanyView {
  nameAr: string;
  address: string | null;
  phone: string | null;
  logoUrl: string | null;
}

function formatLastFetch(d: Date | null, never: string): string {
  if (!d) return never;
  const pad = (n: number): string => String(n).padStart(2, '0');
  return `${d.getFullYear()}/${pad(d.getMonth() + 1)}/${pad(d.getDate())} ${pad(
    d.getHours(),
  )}:${pad(d.getMinutes())}`;
}

export function CompanyInfoScreen(): React.JSX.Element {
  const { t } = useTranslation();
  const { colors } = useTheme();

  const [company, setCompany] = useState<CompanyView | null>(null);
  const [isFetching, setIsFetching] = useState<boolean>(false);
  const [lastFetch, setLastFetch] = useState<Date | null>(() =>
    getLastSync('company'),
  );

  // ── Observe the single company_info row live ───────────────────────────
  useEffect(() => {
    const sub: Subscription = database.collections
      .get<CompanyInfo>('company_info')
      .query(Q.sortBy('id', Q.asc))
      .observe()
      .subscribe((rows) => {
        const row = rows[0];
        if (row) {
          setCompany({
            nameAr: row.nameAr,
            address: row.address ?? null,
            phone: row.phone ?? null,
            logoUrl: row.logoUrl ?? null,
          });
        } else {
          setCompany(null);
        }
      });
    return () => sub.unsubscribe();
  }, []);

  const onFetch = async (): Promise<void> => {
    if (isFetching) return;
    setIsFetching(true);
    try {
      const result = await syncNow('manual');
      if (result.skipped) {
        const msg =
          result.skipReason === 'offline'
            ? t('company.fetchOffline')
            : result.skipReason === 'no_auth'
              ? t('company.fetchNoAuth')
              : t('company.fetching');
        ToastAndroid.show(msg, ToastAndroid.LONG);
        return;
      }
      const count = result.pulled.upserted;
      setLastFetch(getLastSync('company') ?? new Date());
      ToastAndroid.show(
        t('company.fetchSuccess', { count }),
        ToastAndroid.LONG,
      );
      log.info('manual fetch complete', {
        upserted: result.pulled.upserted,
        skipped: result.pulled.skipped,
        failed: result.pulled.failed,
      });
    } catch (err) {
      log.warn('manual fetch failed', err);
      ToastAndroid.show(t('company.fetchError'), ToastAndroid.LONG);
    } finally {
      setIsFetching(false);
    }
  };

  const Row = ({
    label,
    value,
  }: {
    label: string;
    value: string | null;
  }): React.JSX.Element => (
    <View
      style={[styles.row, { borderBottomColor: colors.border }]}
    >
      <Text style={[styles.rowLabel, { color: colors.textTertiary }]}>
        {label}
      </Text>
      <Text
        style={[styles.rowValue, { color: colors.textSecondary }]}
        selectable
      >
        {value && value.length > 0 ? value : '—'}
      </Text>
    </View>
  );

  return (
    <SafeAreaView
      style={[styles.flex, { backgroundColor: colors.background }]}
      edges={['bottom']}
    >
      <AppHeader title={t('company.fetchTitle')} showBack />

      <ScrollView contentContainerStyle={styles.scroll}>
        {/* Fetch action */}
        <Pressable
          accessibilityRole="button"
          accessibilityLabel={t('company.fetchFromServer')}
          onPress={() => void onFetch()}
          disabled={isFetching}
          style={[
            styles.fetchBar,
            {
              backgroundColor: colors.surface,
              borderColor: colors.border,
              opacity: isFetching ? 0.7 : 1,
            },
          ]}
        >
          {isFetching ? (
            <ActivityIndicator color={colors.brandPrimary} />
          ) : (
            <Feather name="refresh-cw" size={22} color={colors.success} />
          )}
          <Text style={[styles.fetchText, { color: colors.textPrimary }]}>
            {isFetching ? t('company.fetching') : t('company.fetchFromServer')}
          </Text>
        </Pressable>

        {/* Company card */}
        <View
          style={[
            styles.card,
            { backgroundColor: colors.surface, borderColor: colors.border },
          ]}
        >
          <Row
            label={t('company.orgName')}
            value={company?.nameAr ?? null}
          />

          {/* Logo */}
          <View style={[styles.row, { borderBottomColor: colors.border }]}>
            <Text style={[styles.rowLabel, { color: colors.textTertiary }]}>
              {t('company.logo')}
            </Text>
            {company?.logoUrl ? (
              <Image
                source={{ uri: company.logoUrl }}
                style={styles.logo}
                resizeMode="contain"
              />
            ) : (
              <View
                style={[styles.logoPlaceholder, { borderColor: colors.border }]}
              >
                <Feather name="image" size={20} color={colors.textTertiary} />
              </View>
            )}
          </View>

          <Row
            label={t('company.fields.address')}
            value={company?.address ?? null}
          />
          <Row
            label={t('company.fields.phone')}
            value={company?.phone ?? null}
          />
          <Row
            label={t('company.branch', { number: getBranchNumber() })}
            value={null}
          />
          <View style={styles.lastFetchRow}>
            <Text style={[styles.rowLabel, { color: colors.textTertiary }]}>
              {t('company.lastFetch')}
            </Text>
            <Text style={[styles.rowValue, { color: colors.textSecondary }]}>
              {formatLastFetch(lastFetch, t('company.never'))}
            </Text>
          </View>
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  card: {
    borderRadius: 14,
    borderWidth: 1,
    marginTop: 16,
    paddingHorizontal: 14,
  },
  fetchBar: {
    alignItems: 'center',
    borderRadius: 12,
    borderWidth: 1,
    flexDirection: 'row',
    gap: 14,
    justifyContent: 'flex-end',
    paddingHorizontal: 16,
    paddingVertical: 16,
  },
  fetchText: {
    fontSize: 16,
    fontWeight: '700',
  },
  flex: { flex: 1 },
  lastFetchRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    paddingVertical: 14,
  },
  logo: {
    borderRadius: 6,
    height: 40,
    width: 40,
  },
  logoPlaceholder: {
    alignItems: 'center',
    borderRadius: 6,
    borderWidth: 1,
    height: 40,
    justifyContent: 'center',
    width: 40,
  },
  row: {
    borderBottomWidth: StyleSheet.hairlineWidth,
    flexDirection: 'row',
    justifyContent: 'space-between',
    paddingVertical: 14,
  },
  rowLabel: {
    fontSize: 13,
    fontWeight: '600',
  },
  rowValue: {
    flexShrink: 1,
    fontSize: 14,
    maxWidth: '60%',
    textAlign: 'left',
  },
  scroll: {
    paddingBottom: 24,
    paddingHorizontal: 16,
    paddingTop: 8,
  },
});
