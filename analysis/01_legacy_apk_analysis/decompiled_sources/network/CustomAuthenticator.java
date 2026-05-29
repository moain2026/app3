package com.p001yd.electricecollector.network;

import com.itextpdf.text.html.HtmlTags;
import com.p001yd.electricecollector.TokenManager;
import com.p001yd.electricecollector.entities.AccessToken;
import java.io.IOException;
import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

/* loaded from: classes12.dex */
public class CustomAuthenticator implements Authenticator {
    private static CustomAuthenticator INSTANCE;
    private TokenManager tokenManager;

    private CustomAuthenticator(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static synchronized CustomAuthenticator getInstance(TokenManager tokenManager) {
        CustomAuthenticator customAuthenticator;
        synchronized (CustomAuthenticator.class) {
            if (INSTANCE == null) {
                INSTANCE = new CustomAuthenticator(tokenManager);
            }
            customAuthenticator = INSTANCE;
        }
        return customAuthenticator;
    }

    private int responseCount(Response response) {
        int i = 1;
        while (true) {
            Response priorResponse = response.priorResponse();
            response = priorResponse;
            if (priorResponse == null) {
                return i;
            }
            i++;
        }
    }

    @Override // okhttp3.Authenticator
    public Request authenticate(Route route, Response response) throws IOException {
        if (responseCount(response) >= 3) {
            return null;
        }
        retrofit2.Response<AccessToken> execute = ((ApiService) RetrofitBuilder.createService(ApiService.class, "")).refresh(this.tokenManager.getToken().getRefreshToken() + HtmlTags.f298A).execute();
        if (!execute.isSuccessful()) {
            return null;
        }
        this.tokenManager.saveToken(execute.body());
        return response.request().newBuilder().header("Authorization", "Bearer " + execute.body().getAccessToken()).build();
    }
}
