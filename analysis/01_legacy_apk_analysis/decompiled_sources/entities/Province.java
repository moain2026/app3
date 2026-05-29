package com.p001yd.electricecollector.entities;

import com.squareup.moshi.Json;

/* loaded from: classes14.dex */
public class Province {

    @Json(name = "name")
    String name;

    @Json(name = "num")
    String num;

    @Json(name = "type")
    String type;

    public String getname() {
        return this.name;
    }

    public String getnum() {
        return this.num;
    }

    public String gettype() {
        return this.type;
    }

    public void setname(String str) {
        this.name = str;
    }

    public void setnum(String str) {
        this.num = str;
    }

    public void settype(String str) {
        this.type = str;
    }
}
