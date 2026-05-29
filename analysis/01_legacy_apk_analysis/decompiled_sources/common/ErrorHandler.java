package com.p001yd.electricecollector.common;

import cz.msebera.android.httpclient.HttpStatus;
import java.io.IOException;
import java.net.SocketTimeoutException;
import retrofit2.HttpException;

/* loaded from: classes15.dex */
public class ErrorHandler {

    /* loaded from: classes15.dex */
    public interface ErrorCallback {
        void onBadGateway(String str);

        void onBadRequest(String str);

        void onForbidden(String str);

        void onHttpError(String str);

        void onNetworkError(String str);

        void onNotFound(String str);

        void onServerError(String str);

        void onServiceUnavailable(String str);

        void onTimeoutError(String str);

        void onUnauthorized(String str);

        void onUnknownError(String str);
    }

    public static void handleError(Throwable th, ErrorCallback errorCallback) {
        if (th instanceof IOException) {
            errorCallback.onNetworkError("خطأ في الاتصال بالإنترنت. يرجى التحقق من اتصالك");
            return;
        }
        if (th instanceof HttpException) {
            handleHttpException((HttpException) th, errorCallback);
        } else if (th instanceof SocketTimeoutException) {
            errorCallback.onTimeoutError("انتهى وقت الانتظار. يرجى المحاولة مرة أخرى");
        } else {
            errorCallback.onUnknownError("حدث خطأ غير متوقع: " + th.getMessage());
        }
    }

    private static void handleHttpException(HttpException httpException, ErrorCallback errorCallback) {
        switch (httpException.code()) {
            case 400:
                errorCallback.onBadRequest("طلب غير صالح");
                return;
            case 401:
                errorCallback.onUnauthorized("غير مصرح بالوصول. يرجى تسجيل الدخول مرة أخرى");
                return;
            case 403:
                errorCallback.onForbidden("غير مسموح بالوصول إلى هذا المورد");
                return;
            case HttpStatus.SC_NOT_FOUND /* 404 */:
                errorCallback.onNotFound("لم يتم العثور على المورد المطلوب");
                return;
            case 500:
                errorCallback.onServerError("خطأ في الخادم الداخلي");
                return;
            case 502:
                errorCallback.onBadGateway("خطأ في البوابة");
                return;
            case 503:
                errorCallback.onServiceUnavailable("الخدمة غير متوفرة حالياً");
                return;
            default:
                errorCallback.onHttpError("خطأ HTTP: " + httpException.code());
                return;
        }
    }
}
