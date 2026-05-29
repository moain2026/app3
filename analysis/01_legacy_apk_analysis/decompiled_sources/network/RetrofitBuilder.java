package com.p001yd.electricecollector.network;

import com.loopj.android.http.RequestParams;
import com.p001yd.electricecollector.TokenManager;
import cz.msebera.android.httpclient.HttpHeaders;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

/* loaded from: classes12.dex */
public class RetrofitBuilder {
    public static String BASE_URL = "http://192.168.0.100:3000/";
    private static final OkHttpClient client = buildClient();
    private static Retrofit retrofit = null;

    private static OkHttpClient buildClient() {
        return new OkHttpClient.Builder().connectTimeout(200L, TimeUnit.SECONDS).readTimeout(200L, TimeUnit.SECONDS).retryOnConnectionFailure(true).addInterceptor(new Interceptor() { // from class: com.yd.electricecollector.network.RetrofitBuilder.1
            @Override // okhttp3.Interceptor
            public Response intercept(Interceptor.Chain chain) throws IOException {
                return chain.proceed(chain.request().newBuilder().addHeader(HttpHeaders.ACCEPT, RequestParams.APPLICATION_JSON).build());
            }
        }).build();
    }

    private static Retrofit buildRetrofit(OkHttpClient okHttpClient, String str) {
        return new Retrofit.Builder().baseUrl(str).client(okHttpClient).addConverterFactory(MoshiConverterFactory.create().asLenient()).build();
    }

    public static <T> T createService(Class<T> cls, String str) {
        retrofit = buildRetrofit(client, str);
        return (T) retrofit.create(cls);
    }

    public static <T> T createServiceWithAuth(Class<T> cls, final TokenManager tokenManager, String str) {
        OkHttpClient build = client.newBuilder().addInterceptor(new Interceptor() { // from class: com.yd.electricecollector.network.RetrofitBuilder.2
            @Override // okhttp3.Interceptor
            public Response intercept(Interceptor.Chain chain) throws IOException {
                Request.Builder newBuilder = chain.request().newBuilder();
                if (TokenManager.this.getToken().getAccessToken() != null) {
                    newBuilder.addHeader("Authorization", "Bearer " + TokenManager.this.getToken().getAccessToken());
                }
                return chain.proceed(newBuilder.build());
            }
        }).authenticator(CustomAuthenticator.getInstance(tokenManager)).build();
        retrofit = buildRetrofit(client, str);
        return (T) retrofit.newBuilder().client(build).build().create(cls);
    }

    public static Retrofit getRetrofit() {
        return retrofit;
    }
}
