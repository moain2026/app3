package com.p001yd.electricecollector.entities;

import java.util.List;
import java.util.Map;

/* loaded from: classes14.dex */
public class ApiError {
    Map<String, List<String>> errors;
    String message;

    public Map<String, List<String>> getErrors() {
        return this.errors;
    }

    public String getMessage() {
        return this.message;
    }
}
