/**
 * BondFormScreen — single shared form for both Create and Edit.
 *
 * The same component is mounted at two routes:
 *   • BondCreate(defaultType: 'receipt' | 'payment')
 *   • BondEdit(localUuid)
 *
 * Wired to REAL persistence (mirrors legacy `EntryBondsActivity.save()`):
 *   • Create  → bondsRepository.createBond → local `bonds` row + SaveBond push.
 *   • Edit    → bondsRepository.updateBond → local row update + UpdateBond push.
 *   • Account + currency pickers feed from the live WatermelonDB tables.
 */

import { useNavigation, useRoute, type RouteProp } from '@react-navigation/native';
import type { NativeStackNavigationProp } from '@react-navigation/native-stack';
import React, { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
  Alert,
  KeyboardAvoidingView,
  Platform,
  ScrollView,
  StyleSheet,
  View,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

import { AppHeader } from '@/components/layout/AppHeader';
import { PrimaryButton } from '@/components/forms/PrimaryButton';
import { AccountPicker, CurrencyPicker } from '@/components/pickers';
import { Chip, FormField, SectionHeader } from '@/design-system/components';
import { useTheme } from '@/design-system/theme';
import { spacing } from '@/design-system/tokens/spacing';
import type { MockAccount } from '@/mocks/accounts';
import { findMockCurrency, type MockCurrency } from '@/mocks/currencies';
import type { MainStackParamList } from '@/navigation/types';
import {
  createBond,
  findBondByUuid,
  updateBond,
  type BondInput,
} from '@/services/repository/bondsRepository';
import { AppError } from '@/utils/errors';

type Route =
  | RouteProp<MainStackParamList, 'BondCreate'>
  | RouteProp<MainStackParamList, 'BondEdit'>;

export function BondFormScreen(): React.JSX.Element {
  const { t } = useTranslation();
  const { colors } = useTheme();
  const route = useRoute<Route>();
  const navigation =
    useNavigation<NativeStackNavigationProp<MainStackParamList>>();

  const isEdit = route.name === 'BondEdit';
  const editUuid = isEdit
    ? (route.params as { localUuid: string }).localUuid
    : null;
  const defaultType =
    (!isEdit &&
      (route.params as { defaultType?: 'receipt' | 'payment' }).defaultType) ||
    'receipt';

  // Form state.
  const [bondType, setBondType] = useState<'receipt' | 'payment'>(defaultType);
  const [account, setAccount] = useState<MockAccount | null>(null);
  const [accountNum, setAccountNum] = useState<number | null>(null);
  const [accountLabel, setAccountLabel] = useState<string>('');
  const [currency, setCurrency] = useState<MockCurrency | null>(
    findMockCurrency(1) ?? null,
  );
  const [amount, setAmount] = useState<string>('');
  const [notes, setNotes] = useState<string>('');

  const [showAccountPicker, setShowAccountPicker] = useState(false);
  const [showCurrencyPicker, setShowCurrencyPicker] = useState(false);
  const [loadingExisting, setLoadingExisting] = useState<boolean>(isEdit);
  const [submitting, setSubmitting] = useState<boolean>(false);

  // ─── Hydrate from the real DB row when editing ──────────────────────────
  useEffect(() => {
    let cancelled = false;
    if (!editUuid) {
      setLoadingExisting(false);
      return;
    }
    void (async () => {
      const row = await findBondByUuid(editUuid);
      if (cancelled || row == null) {
        if (!cancelled) {
          setLoadingExisting(false);
        }
        return;
      }
      setBondType(row.bondType);
      setAccountLabel(row.name ?? '');
      setAccountNum(row.num ?? null);
      setCurrency(findMockCurrency(row.currencyid) ?? null);
      setAmount(String(row.amount));
      // notes2 holds the raw typed note (bin); fall back to notes.
      setNotes(row.notes2 ?? row.notes ?? '');
      setLoadingExisting(false);
    })();
    return () => {
      cancelled = true;
    };
  }, [editUuid]);

  const handleSubmit = async (): Promise<void> => {
    if (!accountLabel || !amount || !currency) {
      Alert.alert(t('bonds.form.validationTitle'), t('bonds.form.validationMsg'));
      return;
    }
    const numericAmount = Number(amount);
    if (!Number.isFinite(numericAmount) || numericAmount <= 0) {
      Alert.alert(t('bonds.form.validationTitle'), t('bonds.form.validationMsg'));
      return;
    }

    const input: BondInput = {
      bondType,
      accountNum: accountNum ?? account?.id ?? null,
      accountName: accountLabel,
      currencyId: currency.id,
      currencyName: currency.name,
      amount: numericAmount,
      notes: notes.trim() === '' ? null : notes.trim(),
    };

    setSubmitting(true);
    try {
      if (isEdit && editUuid) {
        await updateBond(editUuid, input);
      } else {
        await createBond(input);
      }
      Alert.alert(
        t('bonds.form.savedTitle'),
        isEdit ? t('bonds.form.savedEdit') : t('bonds.form.savedCreate'),
        [{ text: t('common.ok'), onPress: () => navigation.goBack() }],
      );
    } catch (e) {
      const msg =
        e instanceof AppError ? e.userMessage : t('bonds.form.saveFailed');
      Alert.alert(t('bonds.form.savedTitle'), msg);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <SafeAreaView
      style={[styles.flex, { backgroundColor: colors.background }]}
      edges={['top']}
    >
      <AppHeader
        title={isEdit ? t('bonds.form.editTitle') : t('bonds.form.createTitle')}
        showBack
      />

      <KeyboardAvoidingView
        style={styles.flex}
        behavior={Platform.OS === 'ios' ? 'padding' : undefined}
      >
        <ScrollView
          contentContainerStyle={styles.scroll}
          keyboardShouldPersistTaps="handled"
        >
          {/* TYPE SELECTOR */}
          <SectionHeader title={t('bonds.form.typeSection')} icon="repeat" />
          <View style={styles.typeRow}>
            <Chip
              label={t('bonds.types.receipt')}
              selected={bondType === 'receipt'}
              onPress={() => setBondType('receipt')}
            />
            <Chip
              label={t('bonds.types.payment')}
              selected={bondType === 'payment'}
              onPress={() => setBondType('payment')}
            />
          </View>

          {/* ACCOUNT */}
          <SectionHeader title={t('bonds.form.accountSection')} icon="user" />
          <FormField
            label={t('bonds.form.accountLabel')}
            value={accountLabel}
            placeholder={t('bonds.form.accountPlaceholder')}
            readOnly
            onPress={() => setShowAccountPicker(true)}
            trailingIcon="chevron-down"
            required
          />
          {account && account.balance !== 0 ? (
            <FormField
              label={t('bonds.form.currentBalance')}
              value={`${Math.abs(account.balance).toLocaleString('ar-EG')} ${
                currency?.symbol ?? ''
              }`}
              helperText={
                account.balance > 0
                  ? t('bonds.form.balanceDebtor')
                  : t('bonds.form.balanceCreditor')
              }
              disabled
            />
          ) : null}

          {/* AMOUNT */}
          <SectionHeader title={t('bonds.form.amountSection')} icon="dollar-sign" />
          <FormField
            label={t('bonds.form.currencyLabel')}
            value={currency ? `${currency.symbol} — ${currency.name}` : ''}
            placeholder={t('bonds.form.currencyPlaceholder')}
            readOnly
            onPress={() => setShowCurrencyPicker(true)}
            trailingIcon="chevron-down"
            required
          />
          <FormField
            label={t('bonds.form.amountLabel')}
            value={amount}
            onChangeText={setAmount}
            placeholder="0"
            keyboardType="numeric"
            suffix={currency?.symbol}
            required
          />

          {/* NOTES */}
          <SectionHeader title={t('bonds.form.notesSection')} icon="message-square" />
          <FormField
            label={t('bonds.form.notesLabel')}
            value={notes}
            onChangeText={setNotes}
            placeholder={t('bonds.form.notesPlaceholder')}
            multiline
            numberOfLines={3}
          />

          {/* SUBMIT */}
          <View style={styles.submitWrap}>
            <PrimaryButton
              title={isEdit ? t('common.save') : t('bonds.form.createCta')}
              onPress={() => {
                void handleSubmit();
              }}
              loading={submitting || loadingExisting}
              disabled={submitting || loadingExisting}
            />
          </View>
        </ScrollView>
      </KeyboardAvoidingView>

      {/* PICKERS */}
      <AccountPicker
        visible={showAccountPicker}
        onClose={() => setShowAccountPicker(false)}
        onSelect={(a) => {
          setAccount(a);
          setAccountNum(a.id);
          setAccountLabel(`${a.name} (${a.num})`);
          setShowAccountPicker(false);
        }}
      />
      <CurrencyPicker
        visible={showCurrencyPicker}
        onClose={() => setShowCurrencyPicker(false)}
        onSelect={(c) => {
          setCurrency(c);
          setShowCurrencyPicker(false);
        }}
      />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  flex: { flex: 1 },
  scroll: {
    paddingBottom: spacing[10],
    paddingHorizontal: spacing[4],
  },
  submitWrap: {
    marginTop: spacing[6],
  },
  typeRow: {
    flexDirection: 'row',
    gap: spacing[2],
  },
});
