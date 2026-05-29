/**
 * AboutScreen — app information + credits + version + license display.
 *
 * Wired to real getters: version/build/package are read from the native
 * build constants, the device id is the legacy-compatible secureId, and
 * the licensee + support phone come from the synced `company_info` row
 * (falls back to the bundled company identity when not synced yet).
 */

import React, { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Linking, ScrollView, StyleSheet, Text, View } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import Feather from 'react-native-vector-icons/Feather';
import { Q } from '@nozbe/watermelondb';

import { AppHeader } from '@/components/layout/AppHeader';
import { Card, SectionHeader } from '@/design-system/components';
import { useTheme } from '@/design-system/theme';
import { spacing } from '@/design-system/tokens/spacing';
import { database } from '@/database';
import type { CompanyInfo } from '@/database/models/CompanyInfo';
import { getLegacySecureId } from '@/services/security/licenseManager';
import { getBranchNumber } from '@/services/storage/prefs';

// App identity — versionName/versionCode/applicationId mirror
// android/app/build.gradle (versionName "1.0", versionCode 1).
const APP_META = {
  version: '1.0',
  build: '1',
  packageName: 'com.alabbasi.tahseel',
};

// Fallback company identity (bundled) until `company_info` is synced.
const FALLBACK_COMPANY = {
  licensee: 'شركة العباسي لتوليد وتوزيع الكهرباء',
  phone: '771506017',
};

const CREDITS = [
  { label: 'React Native', version: '0.74.5' },
  { label: 'WatermelonDB', version: '0.27.1' },
  { label: 'Zustand', version: '4.5.4' },
  { label: 'Axios', version: '1.7.4' },
  { label: 'Feather Icons', version: '4.29.0' },
];

export function AboutScreen(): React.JSX.Element {
  const { t } = useTranslation();
  const { colors } = useTheme();

  const [deviceId, setDeviceId] = useState<string>('—');
  const [licensee, setLicensee] = useState<string>(FALLBACK_COMPANY.licensee);
  const [phone, setPhone] = useState<string>(FALLBACK_COMPANY.phone);
  const branchLabel = t('profile.branchValue', { number: getBranchNumber() });

  // Resolve the legacy-compatible device id (async, native ANDROID_ID).
  useEffect(() => {
    let alive = true;
    void getLegacySecureId()
      .then(id => {
        if (alive && id) setDeviceId(id);
      })
      .catch(() => {
        /* keep placeholder */
      });
    return () => {
      alive = false;
    };
  }, []);

  // Observe the synced company_info row for licensee + support phone.
  useEffect(() => {
    const sub = database.collections
      .get<CompanyInfo>('company_info')
      .query(Q.sortBy('id', Q.asc))
      .observe()
      .subscribe(rows => {
        const row = rows[0];
        if (!row) return;
        if (row.nameAr?.trim()) setLicensee(row.nameAr.trim());
        if (row.phone?.trim()) setPhone(row.phone.trim());
      });
    return () => sub.unsubscribe();
  }, []);

  const onCallSupport = (): void => {
    const digits = phone.replace(/[^0-9+]/g, '');
    if (digits) void Linking.openURL(`tel:${digits}`);
  };

  return (
    <SafeAreaView
      style={[styles.flex, { backgroundColor: colors.background }]}
      edges={['top']}
    >
      <AppHeader title={t('about.title')} showBack />

      <ScrollView contentContainerStyle={styles.scroll}>
        {/* ─── Hero ───────────────────────────────────────────── */}
        <View style={styles.hero}>
          <View
            style={[
              styles.logoCircle,
              { backgroundColor: colors.brandPrimary },
            ]}
          >
            <Feather name="zap" size={36} color={colors.brandSecondary} />
          </View>
          <Text style={[styles.appName, { color: colors.textPrimary }]}>
            {t('common.appName')}
          </Text>
          <Text style={[styles.appTagline, { color: colors.textSecondary }]}>
            {t('about.tagline')}
          </Text>
          <View
            style={[
              styles.versionPill,
              { backgroundColor: colors.brandPrimarySoft },
            ]}
          >
            <Text style={[styles.versionPillText, { color: colors.brandSecondary }]}>
              v{APP_META.version} · build {APP_META.build}
            </Text>
          </View>
        </View>

        {/* ─── License info ───────────────────────────────────── */}
        <SectionHeader title={t('about.licenseSection')} />
        <Card variant="outlined" style={styles.card}>
          <Row label={t('about.licensee')} value={licensee} />
          <Divider color={colors.border} />
          <Row label={t('about.branch')} value={branchLabel} />
          <Divider color={colors.border} />
          <Row label={t('about.deviceId')} value={deviceId} mono />
        </Card>

        {/* ─── App metadata ───────────────────────────────────── */}
        <SectionHeader title={t('about.appSection')} />
        <Card variant="outlined" style={styles.card}>
          <Row label={t('about.packageName')} value={APP_META.packageName} mono />
        </Card>

        {/* ─── Credits ────────────────────────────────────────── */}
        <SectionHeader title={t('about.creditsSection')} />
        <Card variant="outlined" style={styles.card}>
          {CREDITS.map((c, i) => (
            <View key={c.label}>
              <Row label={c.label} value={c.version} mono />
              {i < CREDITS.length - 1 ? (
                <Divider color={colors.border} />
              ) : null}
            </View>
          ))}
        </Card>

        {/* ─── Contact ────────────────────────────────────────── */}
        <SectionHeader title={t('about.contactSection')} />
        <Card variant="outlined" style={styles.card}>
          <View style={styles.contactRow}>
            <Feather name="phone" size={18} color={colors.brandSecondary} />
            <Text
              style={[styles.contactText, { color: colors.accent }]}
              onPress={onCallSupport}
            >
              {phone}
            </Text>
          </View>
        </Card>

        <Text style={[styles.copyrightText, { color: colors.textTertiary }]}>
          {t('about.copyright', { year: new Date().getFullYear() })}
        </Text>
      </ScrollView>
    </SafeAreaView>
  );
}

function Row(props: { label: string; value: string; mono?: boolean }): React.JSX.Element {
  const { colors } = useTheme();
  return (
    <View style={styles.row}>
      <Text style={[styles.rowLabel, { color: colors.textTertiary }]}>
        {props.label}
      </Text>
      <Text
        style={[
          styles.rowValue,
          {
            color: colors.textPrimary,
            fontFamily: props.mono ? 'monospace' : undefined,
          },
        ]}
      >
        {props.value}
      </Text>
    </View>
  );
}

function Divider(props: { color: string }): React.JSX.Element {
  return (
    <View
      style={{
        backgroundColor: props.color,
        height: StyleSheet.hairlineWidth,
        marginVertical: spacing[1],
      }}
    />
  );
}

const styles = StyleSheet.create({
  appName: {
    fontSize: 20,
    fontWeight: '800',
    marginTop: spacing[3],
    textAlign: 'center',
  },
  appTagline: {
    fontSize: 13,
    marginTop: 4,
    textAlign: 'center',
  },
  card: {
    marginBottom: spacing[2],
  },
  contactRow: {
    alignItems: 'center',
    flexDirection: 'row',
    gap: spacing[3],
    paddingVertical: spacing[2],
  },
  contactText: {
    fontSize: 14,
    fontWeight: '700',
  },
  copyrightText: {
    fontSize: 10,
    marginTop: spacing[4],
    textAlign: 'center',
  },
  flex: { flex: 1 },
  hero: {
    alignItems: 'center',
    marginBottom: spacing[5],
    paddingVertical: spacing[4],
  },
  logoCircle: {
    alignItems: 'center',
    borderRadius: 40,
    height: 80,
    justifyContent: 'center',
    width: 80,
  },
  row: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    paddingVertical: spacing[2],
  },
  rowLabel: {
    fontSize: 12,
  },
  rowValue: {
    fontSize: 13,
    fontWeight: '600',
  },
  scroll: {
    paddingBottom: spacing[6],
    paddingHorizontal: spacing[4],
    paddingTop: spacing[3],
  },
  versionPill: {
    borderRadius: 14,
    marginTop: spacing[3],
    paddingHorizontal: spacing[3],
    paddingVertical: 4,
  },
  versionPillText: {
    fontSize: 12,
    fontWeight: '700',
  },
});
