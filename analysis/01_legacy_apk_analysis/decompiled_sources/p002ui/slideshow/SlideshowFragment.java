package com.p001yd.electricecollector.p002ui.slideshow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import com.p001yd.electricecollector.C1018R;

/* loaded from: classes8.dex */
public class SlideshowFragment extends Fragment {
    private SlideshowViewModel slideshowViewModel;

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.slideshowViewModel = (SlideshowViewModel) ViewModelProviders.m49of(this).get(SlideshowViewModel.class);
        View inflate = layoutInflater.inflate(C1018R.layout.fragment_slideshow, viewGroup, false);
        final TextView textView = (TextView) inflate.findViewById(C1018R.id.text_slideshow);
        this.slideshowViewModel.getText().observe(this, new Observer<String>() { // from class: com.yd.electricecollector.ui.slideshow.SlideshowFragment.1
            @Override // androidx.lifecycle.Observer
            public void onChanged(String str) {
                textView.setText(str);
            }
        });
        return inflate;
    }
}
