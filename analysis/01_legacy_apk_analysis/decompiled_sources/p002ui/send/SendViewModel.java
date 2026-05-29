package com.p001yd.electricecollector.p002ui.send;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/* loaded from: classes14.dex */
public class SendViewModel extends ViewModel {
    private MutableLiveData<String> mText = new MutableLiveData<>();

    public SendViewModel() {
        this.mText.setValue("This is send fragment");
    }

    public LiveData<String> getText() {
        return this.mText;
    }
}
