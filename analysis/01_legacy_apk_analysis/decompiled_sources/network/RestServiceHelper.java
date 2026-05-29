package com.p001yd.electricecollector.network;

import android.content.Context;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.ResponseHandlerInterface;
import cz.msebera.android.httpclient.entity.StringEntity;

/* loaded from: classes12.dex */
public class RestServiceHelper {
    private static final String CONTENT_TYPE = "application/json";
    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void delete(String str, ResponseHandlerInterface responseHandlerInterface) {
        client.delete((Context) null, str, responseHandlerInterface);
    }

    public static void delete(String str, String str2, ResponseHandlerInterface responseHandlerInterface) {
        client.addHeader("Authorization", "Bearer " + str2);
        client.delete((Context) null, str, responseHandlerInterface);
    }

    public static void get(String str, ResponseHandlerInterface responseHandlerInterface) {
        client.get(str, responseHandlerInterface);
    }

    public static void get(String str, String str2, ResponseHandlerInterface responseHandlerInterface) {
        client.addHeader("Authorization", "Bearer " + str2);
        client.get(str, responseHandlerInterface);
    }

    public static void post(String str, String str2, ResponseHandlerInterface responseHandlerInterface) {
        client.post(null, str, new StringEntity(str2, "UTF-8"), "application/json", responseHandlerInterface);
    }

    public static void post(String str, String str2, String str3, ResponseHandlerInterface responseHandlerInterface) {
        StringEntity stringEntity = new StringEntity(str2, "UTF-8");
        AsyncHttpClient asyncHttpClient = client;
        asyncHttpClient.setMaxRetriesAndTimeout(3, AsyncHttpClient.DEFAULT_RETRY_SLEEP_TIME_MILLIS);
        asyncHttpClient.setMaxRetriesAndTimeout(3, AsyncHttpClient.DEFAULT_RETRY_SLEEP_TIME_MILLIS);
        asyncHttpClient.addHeader("Authorization", "Bearer " + str3);
        client.post(null, str, stringEntity, "application/json", responseHandlerInterface);
    }

    public static void put(String str, String str2, ResponseHandlerInterface responseHandlerInterface) {
        client.put(null, str, new StringEntity(str2, "UTF-8"), "application/json", responseHandlerInterface);
    }

    public static void put(String str, String str2, String str3, ResponseHandlerInterface responseHandlerInterface) {
        StringEntity stringEntity = new StringEntity(str2, "UTF-8");
        client.addHeader("Authorization", "Bearer " + str3);
        client.put(null, str, stringEntity, "application/json", responseHandlerInterface);
    }
}
