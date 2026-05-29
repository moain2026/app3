/**
 * PermissionsScreen — Android runtime permission inventory.
 *
 * Lists every permission the app uses, its current grant state, and a
 * direct CTA to open the system settings page if denied. Lets the user
 * inspect at-a-glance why a feature isn't working (e.g. Bluetooth
 * disabled blocks printer pairing).
 *
 * Wired to the real Android runtime-permission bridge:
 *   • `PermissionsAndroid.check()` reads the current grant state on mount.
 *   • `PermissionsAndroid.request()` prompts the user; if the result is
 *     `never_ask_again` we flip the row to "blocked".
 *   • `Linking.openSettings()` deep-links to the app's system settings page
 *     for blocked permissions.
 *   • An AppState foreground listener re-checks after the user returns from
 *     system settings, so the UI reflects changes made there.
 */

import React, { useCallback, useEffect, useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
  AppState,
  type AppStateStatus,
  Linking,
  Permission,
  PermissionsAndroid,
  Platform,
  ScrollView,
  StyleSheet,
  Text,
  View,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import Feather from 'react-native-vector-icons/Feather';

import { AppHeader } from '@/components/layout/AppHeader';
import { Card, SecondaryButton, SectionHeader } from '@/design-system/components';
import { useTheme } from '@/design-system/theme';
import { spacing } from '@/design-system/tokens/spacing';

type PermissionStatus = 'granted' | 'denied' | 'blocked';

interface PermissionEntry {
  key: string;
  icon: string;
  /** Underlying Android permission(s). Empty = informational only. */
  androidPerms: Permission[];
}

// Resolve the Android permission constants that exist on the current SDK.
// We pick from PermissionsAndroid.PERMISSIONS defensively (some keys are
// undefined on older SDK levels) so a missing constant never crashes.
const P = PermissionsAndroid.PERMISSIONS;

function perms(...keys: string[]): Permission[] {
  return keys
    .map((k) => (P as Record<string, Permission | undefined>)[k])
    .filter((v): v is Permission => v != null);
}

const ENTRIES: PermissionEntry[] = [
  { key: 'camera', icon: 'camera', androidPerms: perms('CAMERA') },
  {
    key: 'bluetooth',
    icon: 'bluetooth',
    androidPerms: perms('BLUETOOTH_CONNECT', 'BLUETOOTH_SCAN'),
  },
  {
    key: 'location',
    icon: 'map-pin',
    androidPerms: perms('ACCESS_FINE_LOCATION'),
  },
  {
    key: 'storage',
    icon: 'hard-drive',
    androidPerms: perms('WRITE_EXTERNAL_STORAGE', 'READ_EXTERNAL_STORAGE'),
  },
  { key: 'phone', icon: 'phone', androidPerms: perms('CALL_PHONE') },
];

export function PermissionsScreen(): React.JSX.Element {
  const { t } = useTranslation();
  const { colors } = useTheme();

  const [statuses, setStatuses] = useState<Record<string, PermissionStatus>>(
    () => Object.fromEntries(ENTRIES.map((e) => [e.key, 'granted'])),
  );
  // Track which rows hit "never_ask_again" so we can show "blocked".
  const blockedRef = useRef<Set<string>>(new Set());

  const refreshStatuses = useCallback(async (): Promise<void> => {
    if (Platform.OS !== 'android') {
      return;
    }
    const next: Record<string, PermissionStatus> = {};
    for (const entry of ENTRIES) {
      if (entry.androidPerms.length === 0) {
        next[entry.key] = 'granted';
        continue;
      }
      const checks = await Promise.all(
        entry.androidPerms.map((p) => PermissionsAndroid.check(p)),
      );
      const allGranted = checks.every(Boolean);
      if (allGranted) {
        next[entry.key] = 'granted';
        blockedRef.current.delete(entry.key);
      } else {
        next[entry.key] = blockedRef.current.has(entry.key)
          ? 'blocked'
          : 'denied';
      }
    }
    setStatuses(next);
  }, []);

  useEffect(() => {
    void refreshStatuses();
    const sub = AppState.addEventListener(
      'change',
      (s: AppStateStatus) => {
        if (s === 'active') {
          void refreshStatuses();
        }
      },
    );
    return () => sub.remove();
  }, [refreshStatuses]);

  const requestPermission = (key: string): void => {
    const entry = ENTRIES.find((e) => e.key === key);
    if (entry == null || entry.androidPerms.length === 0) {
      return;
    }
    void (async () => {
      const result = await PermissionsAndroid.requestMultiple(
        entry.androidPerms,
      );
      const values = Object.values(result);
      const allGranted = values.every(
        (v) => v === PermissionsAndroid.RESULTS.GRANTED,
      );
      const anyBlocked = values.some(
        (v) => v === PermissionsAndroid.RESULTS.NEVER_ASK_AGAIN,
      );
      if (anyBlocked && !allGranted) {
        blockedRef.current.add(key);
      }
      await refreshStatuses();
    })();
  };

  const openSystemSettings = (): void => {
    void Linking.openSettings();
  };

  const statusColor = (s: PermissionStatus): string =>
    s === 'granted'
      ? colors.success
      : s === 'denied'
      ? colors.warning
      : colors.danger;

  return (
    <SafeAreaView
      style={[styles.flex, { backgroundColor: colors.background }]}
      edges={['top']}
    >
      <AppHeader title={t('settings.permissions.title')} showBack />

      <ScrollView contentContainerStyle={styles.scroll}>
        <Text style={[styles.subtitle, { color: colors.textSecondary }]}>
          {t('settings.permissions.subtitle')}
        </Text>

        <SectionHeader title={t('settings.permissions.runtimeSection')} />

        <Card variant="outlined" padding={0} style={styles.listCard}>
          {ENTRIES.map((entry, i) => {
            const status = statuses[entry.key] ?? 'granted';
            return (
              <View
                key={entry.key}
                style={[
                  styles.row,
                  i < ENTRIES.length - 1 && {
                    borderBottomColor: colors.border,
                    borderBottomWidth: StyleSheet.hairlineWidth,
                  },
                ]}
              >
                <View style={styles.rowHead}>
                  <Feather
                    name={entry.icon}
                    size={20}
                    color={colors.textSecondary}
                  />
                  <View style={styles.rowBody}>
                    <Text
                      style={[styles.rowTitle, { color: colors.textPrimary }]}
                    >
                      {t(`settings.permissions.${entry.key}.title`)}
                    </Text>
                    <Text
                      style={[
                        styles.rowDesc,
                        { color: colors.textTertiary },
                      ]}
                    >
                      {t(`settings.permissions.${entry.key}.desc`)}
                    </Text>
                  </View>
                  <View
                    style={[
                      styles.statusPill,
                      { backgroundColor: statusColor(status) },
                    ]}
                  >
                    <Text style={styles.statusText}>
                      {t(`settings.permissions.status.${status}`)}
                    </Text>
                  </View>
                </View>

                {status !== 'granted' ? (
                  <View style={styles.actionRow}>
                    <SecondaryButton
                      title={t(
                        status === 'blocked'
                          ? 'settings.permissions.openSettings'
                          : 'settings.permissions.request',
                      )}
                      icon={status === 'blocked' ? 'external-link' : 'unlock'}
                      variant={status === 'blocked' ? 'outlined' : 'outlined'}
                      onPress={() =>
                        status === 'blocked'
                          ? openSystemSettings()
                          : requestPermission(entry.key)
                      }
                    />
                  </View>
                ) : null}
              </View>
            );
          })}
        </Card>

        <Card variant="outlined" style={styles.helpCard}>
          <View style={styles.helpHead}>
            <Feather name="help-circle" size={16} color={colors.brandSecondary} />
            <Text style={[styles.helpTitle, { color: colors.textPrimary }]}>
              {t('settings.permissions.helpTitle')}
            </Text>
          </View>
          <Text style={[styles.helpBody, { color: colors.textSecondary }]}>
            {t('settings.permissions.helpBody')}
          </Text>
        </Card>
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  actionRow: {
    flexDirection: 'row',
    justifyContent: 'flex-end',
    marginTop: spacing[2],
  },
  flex: { flex: 1 },
  helpBody: {
    fontSize: 12,
    lineHeight: 18,
    textAlign: 'right',
  },
  helpCard: {
    marginTop: spacing[3],
  },
  helpHead: {
    alignItems: 'center',
    flexDirection: 'row',
    gap: spacing[2],
    marginBottom: spacing[2],
  },
  helpTitle: {
    fontSize: 13,
    fontWeight: '700',
  },
  listCard: {
    marginBottom: spacing[2],
  },
  row: {
    padding: spacing[3],
  },
  rowBody: {
    flex: 1,
  },
  rowDesc: {
    fontSize: 12,
    marginTop: 2,
    textAlign: 'right',
  },
  rowHead: {
    alignItems: 'center',
    flexDirection: 'row',
    gap: spacing[3],
  },
  rowTitle: {
    fontSize: 14,
    fontWeight: '700',
    textAlign: 'right',
  },
  scroll: {
    paddingBottom: spacing[6],
    paddingHorizontal: spacing[4],
    paddingTop: spacing[3],
  },
  statusPill: {
    borderRadius: 4,
    paddingHorizontal: 8,
    paddingVertical: 3,
  },
  statusText: {
    color: '#FFFFFF',
    fontSize: 10,
    fontWeight: '700',
  },
  subtitle: {
    fontSize: 13,
    marginBottom: spacing[3],
    textAlign: 'right',
  },
});
