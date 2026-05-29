package com.p001yd.electricecollector.p002ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import com.p001yd.electricecollector.C1018R;

/* loaded from: classes11.dex */
public class GalleryFragment extends Fragment {
    private GalleryViewModel galleryViewModel;

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.galleryViewModel = (GalleryViewModel) ViewModelProviders.m49of(this).get(GalleryViewModel.class);
        View inflate = layoutInflater.inflate(C1018R.layout.fragment_gallery, viewGroup, false);
        final TextView textView = (TextView) inflate.findViewById(C1018R.id.text_gallery);
        this.galleryViewModel.getText().observe(this, new Observer<String>() { // from class: com.yd.electricecollector.ui.gallery.GalleryFragment.1
            @Override // androidx.lifecycle.Observer
            public void onChanged(String str) {
                textView.setText(str);
            }
        });
        return inflate;
    }
}
