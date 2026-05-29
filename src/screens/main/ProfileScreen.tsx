/**
 * ProfileScreen — current operator's profile.
 *
 * Wired to the real authStore (Zustand) + prefs. Shows the logged-in
 * collector's name / username / branch / NOU-NOA scope and performs a
 * real logout (clears tokens + collector identity; the root navigator
 * reacts to the auth state change and resets to the Auth stack).
 */

import React from 'react';
import { useTranslation } from 'react-i18next';
import { Alert, ScrollView, StyleSheet, Text, View } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import Feather from 'react-native-vector-icons/Feather';

import { AppHeader } from '@/components/layout/AppHeader';
import {
  Card,
  SecondaryButton,
  SectionHeader,
} from '@/design-system/components';
import { useTheme } from '@/design-system/theme';
import { spacing } from '@/design-system/tokens/spacing';
import { useAuthStore } from '@/stores/authStore';
import { getBranchNumber } from '@/services/storage/prefs';

/** Derive 1–2 initials from the operator's display name. */
function deriveInitials(name: string): string {
  const parts = name.trim().split(/\s+/).filter(Boolean);
  if (parts.length === 0) return '؟';
  if (parts.length === 1) return parts[0].slice(0, 2);
  return parts[0].charAt(0) + parts[1].charAt(0);
}

export function ProfileScreen(): React.JSX.Element {
  const { t } = useTranslation();
  const { colors } = useTheme();

  const user = useAuthStore(s => s.user);
  const logout = useAuthStore(s => s.logout);

  const displayName = user?.name?.trim() || user?.username || '—';
  const username = user?.username ?? '—';
  const branchNumber = getBranchNumber();
  const branchLabel = t('profile.branchValue', { number: branchNumber });
  const initials = deriveInitials(displayName);
  // SYS=1 means an admin/supervisor scope; otherwise a field collector.
  const roleLabel =
    user?.sys === 1 ? t('profile.roleAdmin') : t('profile.roleCollector');

  const onLogout = (): void => {
    Alert.alert(
      t('profile.logout'),
      t('profile.logoutConfirm'),
      [
        { text: t('common.cancel'), style: 'cancel' },
        {
          text: t('profile.logout'),
          style: 'destructive',
          onPress: () => {
            void logout();
          },
        },
      ],
      { cancelable: true },
    );
  };

  return (
    <SafeAreaView
      style={[styles.flex, { backgroundColor: colors.background }]}
      edges={['top']}
    >
      <AppHeader title={t('profile.title')} showBack />

      <ScrollView contentContainerStyle={styles.scroll}>
        {/* ─── Hero ───────────────────────────────────────────── */}
        <View style={styles.hero}>
          <View
            style={[
              styles.avatar,
              { backgroundColor: colors.brandPrimary },
            ]}
          >
            <Text
              style={[styles.avatarText, { color: colors.brandSecondary }]}
            >
              {initials}
            </Text>
          </View>
          <Text style={[styles.userName, { color: colors.textPrimary }]}>
            {displayName}
          </Text>
          <Text style={[styles.userRole, { color: colors.textSecondary }]}>
            {roleLabel} · {branchLabel}
          </Text>
        </View>

        {/* ─── Account info ───────────────────────────────────── */}
        <SectionHeader title={t('profile.accountSection')} />
        <Card variant="outlined" style={styles.card}>
          <Row
            icon="user"
            label={t('profile.username')}
            value={username}
          />
          <Divider color={colors.border} />
          <Row
            icon="briefcase"
            label={t('profile.branch')}
            value={branchLabel}
          />
          <Divider color={colors.border} />
          <Row
            icon="hash"
            label={t('profile.scope')}
            value={t('profile.scopeValue', {
              nou: user?.nou ?? 0,
              noa: user?.noa ?? 0,
            })}
          />
        </Card>

        {/* ─── Actions ────────────────────────────────────────── */}
        <SectionHeader title={t('profile.actionsSection')} />
        <Card variant="outlined" style={styles.card}>
          <View style={styles.actionsList}>
            <SecondaryButton
              title={t('profile.logout')}
              icon="log-out"
              variant="danger"
              onPress={onLogout}
            />
          </View>
        </Card>
      </ScrollView>
    </SafeAreaView>
  );
}

function Row(props: {
  icon: string;
  label: string;
  value: string;
}): React.JSX.Element {
  const { colors } = useTheme();
  return (
    <View style={styles.row}>
      <Feather name={props.icon} size={16} color={colors.textTertiary} />
      <View style={styles.rowBody}>
        <Text style={[styles.rowLabel, { color: colors.textTertiary }]}>
          {props.label}
        </Text>
        <Text style={[styles.rowValue, { color: colors.textPrimary }]}>
          {props.value}
        </Text>
      </View>
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
  actionsList: {
    gap: spacing[2],
  },
  avatar: {
    alignItems: 'center',
    borderRadius: 40,
    height: 80,
    justifyContent: 'center',
    width: 80,
  },
  avatarText: {
    fontSize: 28,
    fontWeight: '800',
  },
  card: {
    marginBottom: spacing[2],
  },
  flex: { flex: 1 },
  footerHint: {
    alignItems: 'center',
    flexDirection: 'row',
    gap: spacing[2],
    marginTop: spacing[3],
  },
  footerHintText: {
    fontSize: 11,
  },
  hero: {
    alignItems: 'center',
    marginBottom: spacing[4],
    paddingVertical: spacing[4],
  },
  row: {
    alignItems: 'center',
    flexDirection: 'row',
    gap: spacing[3],
    paddingVertical: spacing[2],
  },
  rowBody: {
    flex: 1,
  },
  rowLabel: {
    fontSize: 11,
    textAlign: 'right',
  },
  rowValue: {
    fontSize: 13,
    fontWeight: '600',
    marginTop: 2,
    textAlign: 'right',
  },
  scroll: {
    paddingBottom: spacing[6],
    paddingHorizontal: spacing[4],
    paddingTop: spacing[3],
  },
  userName: {
    fontSize: 18,
    fontWeight: '800',
    marginTop: spacing[3],
  },
  userRole: {
    fontSize: 13,
    marginTop: 4,
  },
});
