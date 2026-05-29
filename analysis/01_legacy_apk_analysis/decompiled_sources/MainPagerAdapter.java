package com.p001yd.electricecollector;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.p001yd.electricecollector.menu.MenuOprationFragment;
import com.p001yd.electricecollector.menu.MenuReportFragment;

/* loaded from: classes6.dex */
public class MainPagerAdapter extends FragmentPagerAdapter {
    private static final int[] TAB_TITLES = {C1018R.string.tabopration, C1018R.string.tabreports};
    private final Context _context;

    public MainPagerAdapter(Context context, FragmentManager fragmentManager) {
        super(fragmentManager);
        this._context = context;
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public int getCount() {
        return TAB_TITLES.length;
    }

    @Override // androidx.fragment.app.FragmentPagerAdapter
    public Fragment getItem(int i) {
        if (i == 0) {
            return new MenuOprationFragment();
        }
        if (i == 1) {
            return new MenuReportFragment();
        }
        return null;
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public CharSequence getPageTitle(int i) {
        return this._context.getResources().getString(TAB_TITLES[i]);
    }
}
