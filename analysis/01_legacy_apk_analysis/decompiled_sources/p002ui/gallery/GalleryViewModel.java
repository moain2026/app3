package com.p001yd.electricecollector.p002ui.gallery;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/* loaded from: classes11.dex */
public class GalleryViewModel extends ViewModel {
    private MutableLiveData<String> mText = new MutableLiveData<>();

    public GalleryViewModel() {
        this.mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return this.mText;
    }
}
