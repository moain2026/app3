package com.p001yd.electricecollector.p002ui.tools;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import com.p001yd.electricecollector.C1018R;

/* loaded from: classes9.dex */
public class ToolsFragment extends Fragment {
    private ToolsViewModel toolsViewModel;

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.toolsViewModel = (ToolsViewModel) ViewModelProviders.m49of(this).get(ToolsViewModel.class);
        View inflate = layoutInflater.inflate(C1018R.layout.fragment_tools, viewGroup, false);
        final TextView textView = (TextView) inflate.findViewById(C1018R.id.text_tools);
        this.toolsViewModel.getText().observe(this, new Observer<String>() { // from class: com.yd.electricecollector.ui.tools.ToolsFragment.1
            @Override // androidx.lifecycle.Observer
            public void onChanged(String str) {
                textView.setText(str);
            }
        });
        return inflate;
    }
}
