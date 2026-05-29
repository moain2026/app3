package com.p001yd.electricecollector;

import com.loopj.android.http.RequestParams;
import cz.msebera.android.httpclient.HttpHeaders;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

/* loaded from: classes6.dex */
class APIClient {
    private static Retrofit retrofit = null;

    APIClient() {
    }

    private static OkHttpClient buildClient() {
        return new OkHttpClient.Builder().connectTimeout(60L, TimeUnit.SECONDS).readTimeout(80L, TimeUnit.SECONDS).addInterceptor(new Interceptor() { // from class: com.yd.electricecollector.APIClient.2
            @Override // okhttp3.Interceptor
            public Response intercept(Interceptor.Chain chain) throws IOException {
                return chain.proceed(chain.request().newBuilder().addHeader(HttpHeaders.ACCEPT, RequestParams.APPLICATION_JSON).addHeader("Connection", "close").build());
            }
        }).build();
    }

    static Retrofit getClient() {
        new Interceptor() { // from class: com.yd.electricecollector.APIClient.1
            @Override // okhttp3.Interceptor
            public Response intercept(Interceptor.Chain chain) throws IOException {
                return null;
            }
        };
        buildClient();
        retrofit = new Retrofit.Builder().baseUrl("http://192.168.1.3:8088/").addConverterFactory(MoshiConverterFactory.create()).build();
        return retrofit;
    }
}
