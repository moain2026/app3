/**
 * BondPaymentFormScreen — add a payment to an existing bond.
 *
 * Reachable via `navigation.navigate('BondPaymentCreate', { bondLocalUuid })`.
 *
 * Wired to REAL persistence: loads the parent bond from WatermelonDB,
 * then `createBondPayment` writes a local `bond_payments` row and enqueues
 * a `SaveBondPayment` push (POST /SaveBondPayment — confirmed live).
 */

import { useNavigation, useRoute, type RouteProp } from '@react-navigation/native';
import type { NativeStackNavigationProp } from '@react-navigation/native-stack';
import React, { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
  ActivityIndicator,
  Alert,
  KeyboardAvoidingView,
  Platform,
  ScrollView,
  StyleSheet,
  Text,
  View,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

import { AppHeader } from '@/components/layout/AppHeader';
import { PrimaryButton } from '@/components/forms/PrimaryButton';
import type { Bond } from '@/database/models/Bond';
import { Card, ErrorBanner, FormField, SectionHeader } from '@/design-system/components';
import { useTheme } from '@/design-system/theme';
import { spacing } from '@/design-system/tokens/spacing';
import { findMockCurrency } from '@/mocks/currencies';
import type { MainStackParamList } from '@/navigation/types';
import {
  createBondPayment,
  findBondForPayment,
} from '@/services/repository/bondPaymentsRepository';
import { AppError } from '@/utils/errors';

type Route = RouteProp<MainStackParamList, 'BondPaymentCreate'>;

export function BondPaymentFormScreen(): React.JSX.Element {
  const { t } = useTranslation();
  const { colors } = useTheme();
  const route = useRoute<Route>();
  const navigation =
    useNavigation<NativeStackNavigationProp<MainStackParamList>>();

  const [bond, setBond] = useState<Bond | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [amount, setAmount] = useState<string>('');
  const [notes, setNotes] = useState<string>('');
  const [submitting, setSubmitting] = useState<boolean>(false);

  useEffect(() => {
    let cancelled = false;
    void (async () => {
      const row = await findBondForPayment(route.params.bondLocalUuid);
      if (cancelled) {
        return;
      }
      setBond(row);
      setLoading(false);
    })();
    return () => {
      cancelled = true;
    };
  }, [route.params.bondLocalUuid]);

  const currencySymbol = bond
    ? findMockCurrency(bond.currencyid)?.symbol ?? ''
    : '';

  const handleSubmit = async (): Promise<void> => {
    if (bond == null) {
      return;
    }
    const value = Number(amount);
    if (!value || value <= 0) {
      Alert.alert(
        t('bonds.payments.invalidTitle'),
        t('bonds.payments.invalidMsg'),
      );
      return;
    }

    setSubmitting(true);
    try {
      await createBondPayment({
        bondLocalId: bond.id,
        bondNo: bond.bondNo,
        amount: value,
        notes: notes.trim() === '' ? null : notes.trim(),
      });
      Alert.alert(
        t('bonds.payments.savedTitle'),
        t('bonds.payments.savedMsg'),
        [{ text: t('common.ok'), onPress: () => navigation.goBack() }],
      );
    } catch (e) {
      const msg =
        e instanceof AppError ? e.userMessage : t('bonds.form.saveFailed');
      Alert.alert(t('bonds.payments.savedTitle'), msg);
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return (
      <SafeAreaView
        style={[styles.flex, { backgroundColor: colors.background }]}
        edges={['top']}
      >
        <AppHeader title={t('bonds.payments.formTitle')} showBack />
        <View style={styles.center}>
          <ActivityIndicator color={colors.accent} />
        </View>
      </SafeAreaView>
    );
  }

  if (!bond) {
    return (
      <SafeAreaView
        style={[styles.flex, { backgroundColor: colors.background }]}
        edges={['top']}
      >
        <AppHeader title={t('bonds.payments.formTitle')} showBack />
        <ErrorBanner message={t('bonds.detail.notFound')} variant="error" />
      </SafeAreaView>
    );
  }

  return (
    <SafeAreaView
      style={[styles.flex, { backgroundColor: colors.background }]}
      edges={['top']}
    >
      <AppHeader title={t('bonds.payments.formTitle')} showBack />

      <KeyboardAvoidingView
        style={styles.flex}
        behavior={Platform.OS === 'ios' ? 'padding' : undefined}
      >
        <ScrollView
          contentContainerStyle={styles.scroll}
          keyboardShouldPersistTaps="handled"
        >
          {/* BOND SUMMARY */}
          <SectionHeader
            title={t('bonds.payments.bondSummary')}
            icon="file-text"
          />
          <Card>
            <Text style={[styles.bondTitle, { color: colors.textPrimary }]}>
              {t('bonds.detail.bondNo', { no: bond.bondNo })} —{' '}
              {bond.accountName ?? ''}
            </Text>
            <View style={styles.summaryRow}>
              <Text style={[styles.summaryLabel, { color: colors.textTertiary }]}>
                {t('bonds.detail.amount')}
              </Text>
              <Text style={[styles.summaryValue, { color: colors.textPrimary }]}>
                {bond.amount.toLocaleString('ar-EG')} {currencySymbol}
              </Text>
            </View>
          </Card>

          {/* AMOUNT */}
          <SectionHeader
            title={t('bonds.payments.amountSection')}
            icon="dollar-sign"
          />
          <FormField
            label={t('bonds.payments.amountLabel')}
            value={amount}
            onChangeText={setAmount}
            placeholder="0"
            keyboardType="numeric"
            suffix={currencySymbol}
            required
          />

          {/* NOTES */}
          <SectionHeader
            title={t('bonds.payments.notesSection')}
            icon="message-square"
          />
          <FormField
            label={t('bonds.payments.notesLabel')}
            value={notes}
            onChangeText={setNotes}
            placeholder={t('bonds.payments.notesPlaceholder')}
            multiline
            numberOfLines={3}
          />

          <View style={styles.submitWrap}>
            <PrimaryButton
              title={t('bonds.payments.saveCta')}
              onPress={() => {
                void handleSubmit();
              }}
              loading={submitting}
              disabled={submitting}
            />
          </View>
        </ScrollView>
      </KeyboardAvoidingView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  bondTitle: {
    fontSize: 15,
    fontWeight: '700',
    marginBottom: spacing[3],
    textAlign: 'right',
  },
  center: { alignItems: 'center', flex: 1, justifyContent: 'center' },
  flex: { flex: 1 },
  scroll: {
    paddingBottom: spacing[10],
    paddingHorizontal: spacing[4],
  },
  submitWrap: {
    marginTop: spacing[6],
  },
  summaryLabel: {
    fontSize: 13,
    fontWeight: '600',
  },
  summaryRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    paddingVertical: spacing[1] + 2,
  },
  summaryValue: {
    fontSize: 14,
    fontWeight: '700',
  },
});
