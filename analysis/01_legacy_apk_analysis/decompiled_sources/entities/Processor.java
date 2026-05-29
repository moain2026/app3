package com.p001yd.electricecollector.entities;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

/* loaded from: classes14.dex */
public class Processor {
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Reports> jsona = this.moshi.adapter(Reports.class);
}
