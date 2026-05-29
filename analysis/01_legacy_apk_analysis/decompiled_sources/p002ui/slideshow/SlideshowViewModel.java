package com.p001yd.electricecollector.p002ui.slideshow;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/* loaded from: classes8.dex */
public class SlideshowViewModel extends ViewModel {
    private MutableLiveData<String> mText = new MutableLiveData<>();

    public SlideshowViewModel() {
        this.mText.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return this.mText;
    }
}
