package com.p001yd.electricecollector;

import com.squareup.moshi.Moshi;
import java.io.IOException;
import org.json.JSONObject;

/* loaded from: classes6.dex */
public class GsonHelper {
    public static <T> String toJson(T t) {
        return "";
    }

    public static <T> T toObject(JSONObject jSONObject, Class<T> cls) throws IOException {
        return new Moshi.Builder().build().adapter((Class) cls).fromJson(jSONObject.toString());
    }
}
