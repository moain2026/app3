package com.p001yd.electricecollector.p002ui.tools;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/* loaded from: classes9.dex */
public class ToolsViewModel extends ViewModel {
    private MutableLiveData<String> mText = new MutableLiveData<>();

    public ToolsViewModel() {
        this.mText.setValue("This is tools fragment");
    }

    public LiveData<String> getText() {
        return this.mText;
    }
}
