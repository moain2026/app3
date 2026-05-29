package com.p001yd.electricecollector;

import java.util.List;

/* loaded from: classes6.dex */
public interface HttpClientIntCallback<T> {
    void onDataloaded(List<T> list);

    void onError(Throwable th);

    void onSucceed(Object obj);
}
