package com.p001yd.electricecollector.p002ui;

import java.util.List;

/* loaded from: classes12.dex */
public interface BaseView<T> {
    void onFailed(T t);

    void onLoadDataFailure();

    void onLoadDataSucceed(List<T> list);

    void onSucceed(T t);
}
