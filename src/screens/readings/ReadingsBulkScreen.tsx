/**
 * ReadingsBulkScreen — fast-entry grid for posting many readings at once.
 *
 * Workflow:
 *   1. User filters by place (top filter card).
 *   2. Pending subscribers (cas == 0) appear in a scrollable list with an
 *      inline numeric input per row.
 *   3. Bottom bar shows totals + a single "post all" CTA that writes every
 *      draft via `updateLocalReading` (local write + enqueue SaveReading).
 *
 * Wired to live WatermelonDB: `observeReadings({ status: 'pending' })`.
 */

import React, { useEffect, useMemo, useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
  Alert,
  StyleSheet,
  Text,
  TextInput,
  View,
} from 'react-native';
import { FlashList } from '@shopify/flash-list';
import { SafeAreaView } from 'react-native-safe-area-context';
import { Subscription } from 'rxjs';
import Feather from 'react-native-vector-icons/Feather';

import { AppHeader } from '@/components/layout/AppHeader';
import { PlacePicker } from '@/components/pickers';
import { Card, EmptyState, FormField, SectionHeader } from '@/design-system/components';
import { PrimaryButton } from '@/components/forms/PrimaryButton';
import { useTheme } from '@/design-system/theme';
import { spacing } from '@/design-system/tokens/spacing';
import type { Reading } from '@/database/models/Reading';
import type { MockPlace } from '@/mocks';
import {
  observeReadings,
  updateLocalReading,
  type ReadingsQueryFilters,
} from '@/services/repository/readingsRepository';

const ROW_HEIGHT = 78;

const BASE_FILTERS: ReadingsQueryFilters = {
  searchQuery: '',
  area: null,
  book: null,
  group: null,
  status: 'pending',
  sortBy: 'num',
  sortOrder: 'asc',
};

export function ReadingsBulkScreen(): React.JSX.Element {
  const { t } = useTranslation();
  const { colors } = useTheme();

  const [place, setPlace] = useState<MockPlace | null>(null);
  const [placeOpen, setPlaceOpen] = useState(false);
  const [entries, setEntries] = useState<Record<string, string>>({});
  const [readings, setReadings] = useState<Reading[]>([]);
  const [saving, setSaving] = useState(false);

  // Live pending readings, re-filtered by selected place (nomstlm).
  useEffect(() => {
    const filters: ReadingsQueryFilters = {
      ...BASE_FILTERS,
      area: place && place.id !== 0 ? place.id : null,
    };
    let sub: Subscription | null = null;
    sub = observeReadings(filters).subscribe({
      next: (rows) => setReadings(rows),
      error: () => setReadings([]),
    });
    return () => {
      sub?.unsubscribe();
    };
  }, [place]);

  const draftCount = useMemo(
    () => Object.values(entries).filter((v) => v.trim() !== '').length,
    [entries],
  );

  const handleChange = (key: string, value: string): void => {
    const cleaned = value.replace(/[^0-9]/g, '');
    setEntries((prev) => ({ ...prev, [key]: cleaned }));
  };

  const handleSaveAll = async (): Promise<void> => {
    const drafts = readings
      .map((r) => ({ reading: r, raw: entries[r.localUuid] ?? '' }))
      .filter((d) => d.raw.trim() !== '');
    if (drafts.length === 0) {
      return;
    }
    setSaving(true);
    let ok = 0;
    let failed = 0;
    for (const d of drafts) {
      const newKh = Number(d.raw);
      // Skip invalid / non-increasing readings (must be >= previous).
      if (!Number.isFinite(newKh) || newKh < d.reading.ks) {
        failed += 1;
        continue;
      }
      try {
        await updateLocalReading(d.reading, newKh);
        ok += 1;
      } catch {
        failed += 1;
      }
    }
    setSaving(false);
    setEntries({});
    Alert.alert(
      t('readings.bulk.savedTitle'),
      t('readings.bulk.savedMsg', { ok, failed }),
    );
  };

  const renderItem = ({ item }: { item: Reading }): React.JSX.Element => {
    const draft = entries[item.localUuid] ?? '';
    return (
      <View style={[styles.row, { borderBottomColor: colors.border }]}>
        <View style={styles.rowInfo}>
          <Text
            style={[styles.rowName, { color: colors.textPrimary }]}
            numberOfLines={1}
          >
            {item.name}
          </Text>
          <Text style={[styles.rowMeta, { color: colors.textTertiary }]}>
            #{item.num} · {t('readings.bulk.previous')}: {item.ks}
          </Text>
        </View>
        <TextInput
          style={[
            styles.input,
            {
              backgroundColor: colors.inputBg,
              borderColor: draft !== '' ? colors.accent : colors.inputBorder,
              color: colors.textPrimary,
            },
          ]}
          placeholder={t('readings.bulk.placeholder')}
          placeholderTextColor={colors.inputPlaceholder}
          keyboardType="numeric"
          value={draft}
          onChangeText={(v) => handleChange(item.localUuid, v)}
          maxLength={9}
        />
      </View>
    );
  };

  return (
    <SafeAreaView
      style={[styles.flex, { backgroundColor: colors.background }]}
      edges={['top']}
    >
      <AppHeader title={t('readings.bulk.title')} showBack />

      <View style={styles.filterArea}>
        <Card variant="outlined">
          <FormField
            label={t('readings.bulk.placeFilter')}
            value={place && place.id !== 0 ? place.name : ''}
            placeholder={t('readings.bulk.allPlaces')}
            readOnly
            onPress={() => setPlaceOpen(true)}
            leadingIcon="map-pin"
            trailingIcon={place && place.id !== 0 ? 'x' : 'chevron-down'}
            onTrailingPress={
              place && place.id !== 0 ? () => setPlace(null) : undefined
            }
          />
        </Card>
      </View>

      <SectionHeader
        title={`${t('readings.bulk.section')} (${readings.length})`}
      />

      {readings.length === 0 ? (
        <EmptyState
          icon="check-circle"
          title={t('readings.bulk.emptyTitle')}
          subtitle={t('readings.bulk.emptySubtitle')}
        />
      ) : (
        <FlashList
          data={readings}
          renderItem={renderItem}
          estimatedItemSize={ROW_HEIGHT}
          keyExtractor={(r) => r.localUuid}
          keyboardShouldPersistTaps="handled"
          contentContainerStyle={{ paddingBottom: 120 }}
        />
      )}

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
        <View style={styles.actionBarInfo}>
          <Feather name="edit-3" size={16} color={colors.accent} />
          <Text style={[styles.actionBarText, { color: colors.textPrimary }]}>
            {t('readings.bulk.draftCount', { count: draftCount })}
          </Text>
        </View>
        <View style={styles.actionBarButton}>
          <PrimaryButton
            title={t('readings.bulk.saveAll')}
            disabled={draftCount === 0 || saving}
            loading={saving}
            onPress={() => {
              void handleSaveAll();
            }}
          />
        </View>
      </View>

      <PlacePicker
        visible={placeOpen}
        onClose={() => setPlaceOpen(false)}
        onSelect={(p) => {
          setPlace(p);
          setPlaceOpen(false);
        }}
      />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  actionBar: {
    borderTopWidth: StyleSheet.hairlineWidth,
    flexDirection: 'row',
    gap: spacing[3],
    paddingBottom: spacing[3],
    paddingHorizontal: spacing[4],
    paddingTop: spacing[3],
  },
  actionBarButton: {
    flex: 1,
  },
  actionBarInfo: {
    alignItems: 'center',
    flexDirection: 'row',
    gap: spacing[2],
  },
  actionBarText: {
    fontSize: 12,
    fontWeight: '700',
  },
  filterArea: {
    paddingHorizontal: spacing[4],
    paddingVertical: spacing[3],
  },
  flex: { flex: 1 },
  input: {
    borderRadius: 8,
    borderWidth: 1,
    fontSize: 16,
    fontWeight: '700',
    height: 44,
    paddingHorizontal: spacing[3],
    textAlign: 'center',
    width: 110,
  },
  row: {
    alignItems: 'center',
    borderBottomWidth: StyleSheet.hairlineWidth,
    flexDirection: 'row',
    gap: spacing[3],
    paddingHorizontal: spacing[4],
    paddingVertical: spacing[3],
  },
  rowInfo: {
    flex: 1,
  },
  rowMeta: {
    fontSize: 11,
    marginTop: 2,
    textAlign: 'right',
  },
  rowName: {
    fontSize: 14,
    fontWeight: '700',
    textAlign: 'right',
  },
});
