package com.p001yd.electricecollector.p002ui.send;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import com.p001yd.electricecollector.C1018R;

/* loaded from: classes14.dex */
public class SendFragment extends Fragment {
    private SendViewModel sendViewModel;

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.sendViewModel = (SendViewModel) ViewModelProviders.m49of(this).get(SendViewModel.class);
        View inflate = layoutInflater.inflate(C1018R.layout.fragment_send, viewGroup, false);
        final TextView textView = (TextView) inflate.findViewById(C1018R.id.text_send);
        this.sendViewModel.getText().observe(this, new Observer<String>() { // from class: com.yd.electricecollector.ui.send.SendFragment.1
            @Override // androidx.lifecycle.Observer
            public void onChanged(String str) {
                textView.setText(str);
            }
        });
        return inflate;
    }
}
