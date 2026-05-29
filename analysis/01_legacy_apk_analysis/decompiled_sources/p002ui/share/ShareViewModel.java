package com.p001yd.electricecollector.p002ui.share;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/* loaded from: classes9.dex */
public class ShareViewModel extends ViewModel {
    private MutableLiveData<String> mText = new MutableLiveData<>();

    public ShareViewModel() {
        this.mText.setValue("This is share fragment");
    }

    public LiveData<String> getText() {
        return this.mText;
    }
}
