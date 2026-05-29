package com.p001yd.electricecollector.p002ui.home;

import android.util.Pair;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.p001yd.electricecollector.entities.AccessToken;

/* loaded from: classes15.dex */
public class HomeViewModel extends ViewModel {
    private MutableLiveData<Pair<Boolean, String>> _messages;
    private MutableLiveData<Boolean> _socketStatus;
    private MutableLiveData<AccessToken> mText = new MutableLiveData<>();

    public HomeViewModel() {
        this.mText.setValue(new AccessToken());
    }

    public void addMessage(LiveData<Pair<Boolean, String>> liveData) {
    }

    public LiveData<AccessToken> getText() {
        return this.mText;
    }

    public LiveData<AccessToken> setStatus() {
        return this.mText;
    }
}
