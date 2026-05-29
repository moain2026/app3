package com.p001yd.electricecollector.model;

import android.util.Log;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.p001yd.electricecollector.HttpClientIntCallback;
import com.p001yd.electricecollector.entities.AuthData;
import com.p001yd.electricecollector.entities.Users;
import com.p001yd.electricecollector.network.RestServiceHelper;
import cz.msebera.android.httpclient.Header;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes5.dex */
public class AuthRepository {
    private final String TAG = getClass().getSimpleName();
    private String _appId;
    private String _baseUrl;

    public AuthRepository(String str, String str2) {
        this._baseUrl = String.format("%s%s", str, "Login");
        this._appId = str2;
        Log.w(this.TAG, "_baseUrl:" + this._baseUrl);
    }

    public void auth(AuthData authData, final HttpClientIntCallback<Users> httpClientIntCallback) throws JSONException {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("username", authData.getUserName());
            jSONObject.put("password", authData.getPassword());
            jSONObject.put("appId", authData.getAppId());
            jSONObject.put("secureId", authData.getSecurId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RestServiceHelper.post(String.format("%s", this._baseUrl), jSONObject.toString(), new JsonHttpResponseHandler() { // from class: com.yd.electricecollector.model.AuthRepository.1
            @Override // com.loopj.android.http.JsonHttpResponseHandler, com.loopj.android.http.TextHttpResponseHandler
            public void onFailure(int i, Header[] headerArr, String str, Throwable th) {
                httpClientIntCallback.onError(th);
            }

            @Override // com.loopj.android.http.JsonHttpResponseHandler
            public void onFailure(int i, Header[] headerArr, Throwable th, JSONObject jSONObject2) {
                httpClientIntCallback.onError(th);
            }

            /* JADX WARN: Removed duplicated region for block: B:101:0x01a6  */
            /* JADX WARN: Removed duplicated region for block: B:104:0x01b2  */
            /* JADX WARN: Removed duplicated region for block: B:107:0x01cb  */
            /* JADX WARN: Removed duplicated region for block: B:110:0x01d7  */
            /* JADX WARN: Removed duplicated region for block: B:113:0x01e3  */
            /* JADX WARN: Removed duplicated region for block: B:116:0x01fc  */
            /* JADX WARN: Removed duplicated region for block: B:119:0x0208  */
            /* JADX WARN: Removed duplicated region for block: B:122:0x0214  */
            /* JADX WARN: Removed duplicated region for block: B:135:0x0216  */
            /* JADX WARN: Removed duplicated region for block: B:136:0x020a  */
            /* JADX WARN: Removed duplicated region for block: B:137:0x01fe  */
            /* JADX WARN: Removed duplicated region for block: B:138:0x01e5  */
            /* JADX WARN: Removed duplicated region for block: B:139:0x01d9  */
            /* JADX WARN: Removed duplicated region for block: B:140:0x01cd  */
            /* JADX WARN: Removed duplicated region for block: B:141:0x01b4  */
            /* JADX WARN: Removed duplicated region for block: B:142:0x01a8  */
            /* JADX WARN: Removed duplicated region for block: B:143:0x019c  */
            /* JADX WARN: Removed duplicated region for block: B:144:0x0183  */
            /* JADX WARN: Removed duplicated region for block: B:19:0x005d A[Catch: IOException | JSONException -> 0x022c, IOException -> 0x022e, TryCatch #4 {IOException | JSONException -> 0x022c, blocks: (B:5:0x0013, B:7:0x0037, B:11:0x0041, B:13:0x004a, B:17:0x0054, B:19:0x005d, B:23:0x0067, B:25:0x007d, B:29:0x0087, B:31:0x0090, B:35:0x009a, B:37:0x00a3, B:41:0x00ad, B:43:0x00c3, B:47:0x00cd, B:49:0x00d6, B:53:0x00e0, B:55:0x00e9, B:57:0x00ef, B:61:0x00f9, B:63:0x010f, B:67:0x0119, B:69:0x0122, B:73:0x012c, B:75:0x0135, B:77:0x013b, B:81:0x0145, B:83:0x015b, B:87:0x0165, B:89:0x016e, B:93:0x0178, B:96:0x0184, B:99:0x019d, B:102:0x01a9, B:105:0x01b5, B:108:0x01ce, B:111:0x01da, B:114:0x01e6, B:117:0x01ff, B:120:0x020b, B:123:0x0217), top: B:4:0x0013 }] */
            /* JADX WARN: Removed duplicated region for block: B:25:0x007d A[Catch: IOException | JSONException -> 0x022c, IOException -> 0x022e, TryCatch #4 {IOException | JSONException -> 0x022c, blocks: (B:5:0x0013, B:7:0x0037, B:11:0x0041, B:13:0x004a, B:17:0x0054, B:19:0x005d, B:23:0x0067, B:25:0x007d, B:29:0x0087, B:31:0x0090, B:35:0x009a, B:37:0x00a3, B:41:0x00ad, B:43:0x00c3, B:47:0x00cd, B:49:0x00d6, B:53:0x00e0, B:55:0x00e9, B:57:0x00ef, B:61:0x00f9, B:63:0x010f, B:67:0x0119, B:69:0x0122, B:73:0x012c, B:75:0x0135, B:77:0x013b, B:81:0x0145, B:83:0x015b, B:87:0x0165, B:89:0x016e, B:93:0x0178, B:96:0x0184, B:99:0x019d, B:102:0x01a9, B:105:0x01b5, B:108:0x01ce, B:111:0x01da, B:114:0x01e6, B:117:0x01ff, B:120:0x020b, B:123:0x0217), top: B:4:0x0013 }] */
            /* JADX WARN: Removed duplicated region for block: B:31:0x0090 A[Catch: IOException | JSONException -> 0x022c, IOException -> 0x022e, TryCatch #4 {IOException | JSONException -> 0x022c, blocks: (B:5:0x0013, B:7:0x0037, B:11:0x0041, B:13:0x004a, B:17:0x0054, B:19:0x005d, B:23:0x0067, B:25:0x007d, B:29:0x0087, B:31:0x0090, B:35:0x009a, B:37:0x00a3, B:41:0x00ad, B:43:0x00c3, B:47:0x00cd, B:49:0x00d6, B:53:0x00e0, B:55:0x00e9, B:57:0x00ef, B:61:0x00f9, B:63:0x010f, B:67:0x0119, B:69:0x0122, B:73:0x012c, B:75:0x0135, B:77:0x013b, B:81:0x0145, B:83:0x015b, B:87:0x0165, B:89:0x016e, B:93:0x0178, B:96:0x0184, B:99:0x019d, B:102:0x01a9, B:105:0x01b5, B:108:0x01ce, B:111:0x01da, B:114:0x01e6, B:117:0x01ff, B:120:0x020b, B:123:0x0217), top: B:4:0x0013 }] */
            /* JADX WARN: Removed duplicated region for block: B:37:0x00a3 A[Catch: IOException | JSONException -> 0x022c, IOException -> 0x022e, TryCatch #4 {IOException | JSONException -> 0x022c, blocks: (B:5:0x0013, B:7:0x0037, B:11:0x0041, B:13:0x004a, B:17:0x0054, B:19:0x005d, B:23:0x0067, B:25:0x007d, B:29:0x0087, B:31:0x0090, B:35:0x009a, B:37:0x00a3, B:41:0x00ad, B:43:0x00c3, B:47:0x00cd, B:49:0x00d6, B:53:0x00e0, B:55:0x00e9, B:57:0x00ef, B:61:0x00f9, B:63:0x010f, B:67:0x0119, B:69:0x0122, B:73:0x012c, B:75:0x0135, B:77:0x013b, B:81:0x0145, B:83:0x015b, B:87:0x0165, B:89:0x016e, B:93:0x0178, B:96:0x0184, B:99:0x019d, B:102:0x01a9, B:105:0x01b5, B:108:0x01ce, B:111:0x01da, B:114:0x01e6, B:117:0x01ff, B:120:0x020b, B:123:0x0217), top: B:4:0x0013 }] */
            /* JADX WARN: Removed duplicated region for block: B:43:0x00c3 A[Catch: IOException | JSONException -> 0x022c, IOException -> 0x022e, TryCatch #4 {IOException | JSONException -> 0x022c, blocks: (B:5:0x0013, B:7:0x0037, B:11:0x0041, B:13:0x004a, B:17:0x0054, B:19:0x005d, B:23:0x0067, B:25:0x007d, B:29:0x0087, B:31:0x0090, B:35:0x009a, B:37:0x00a3, B:41:0x00ad, B:43:0x00c3, B:47:0x00cd, B:49:0x00d6, B:53:0x00e0, B:55:0x00e9, B:57:0x00ef, B:61:0x00f9, B:63:0x010f, B:67:0x0119, B:69:0x0122, B:73:0x012c, B:75:0x0135, B:77:0x013b, B:81:0x0145, B:83:0x015b, B:87:0x0165, B:89:0x016e, B:93:0x0178, B:96:0x0184, B:99:0x019d, B:102:0x01a9, B:105:0x01b5, B:108:0x01ce, B:111:0x01da, B:114:0x01e6, B:117:0x01ff, B:120:0x020b, B:123:0x0217), top: B:4:0x0013 }] */
            /* JADX WARN: Removed duplicated region for block: B:49:0x00d6 A[Catch: IOException | JSONException -> 0x022c, IOException -> 0x022e, TryCatch #4 {IOException | JSONException -> 0x022c, blocks: (B:5:0x0013, B:7:0x0037, B:11:0x0041, B:13:0x004a, B:17:0x0054, B:19:0x005d, B:23:0x0067, B:25:0x007d, B:29:0x0087, B:31:0x0090, B:35:0x009a, B:37:0x00a3, B:41:0x00ad, B:43:0x00c3, B:47:0x00cd, B:49:0x00d6, B:53:0x00e0, B:55:0x00e9, B:57:0x00ef, B:61:0x00f9, B:63:0x010f, B:67:0x0119, B:69:0x0122, B:73:0x012c, B:75:0x0135, B:77:0x013b, B:81:0x0145, B:83:0x015b, B:87:0x0165, B:89:0x016e, B:93:0x0178, B:96:0x0184, B:99:0x019d, B:102:0x01a9, B:105:0x01b5, B:108:0x01ce, B:111:0x01da, B:114:0x01e6, B:117:0x01ff, B:120:0x020b, B:123:0x0217), top: B:4:0x0013 }] */
            /* JADX WARN: Removed duplicated region for block: B:55:0x00e9 A[Catch: IOException | JSONException -> 0x022c, IOException -> 0x022e, TryCatch #4 {IOException | JSONException -> 0x022c, blocks: (B:5:0x0013, B:7:0x0037, B:11:0x0041, B:13:0x004a, B:17:0x0054, B:19:0x005d, B:23:0x0067, B:25:0x007d, B:29:0x0087, B:31:0x0090, B:35:0x009a, B:37:0x00a3, B:41:0x00ad, B:43:0x00c3, B:47:0x00cd, B:49:0x00d6, B:53:0x00e0, B:55:0x00e9, B:57:0x00ef, B:61:0x00f9, B:63:0x010f, B:67:0x0119, B:69:0x0122, B:73:0x012c, B:75:0x0135, B:77:0x013b, B:81:0x0145, B:83:0x015b, B:87:0x0165, B:89:0x016e, B:93:0x0178, B:96:0x0184, B:99:0x019d, B:102:0x01a9, B:105:0x01b5, B:108:0x01ce, B:111:0x01da, B:114:0x01e6, B:117:0x01ff, B:120:0x020b, B:123:0x0217), top: B:4:0x0013 }] */
            /* JADX WARN: Removed duplicated region for block: B:63:0x010f A[Catch: IOException | JSONException -> 0x022c, IOException -> 0x022e, TryCatch #4 {IOException | JSONException -> 0x022c, blocks: (B:5:0x0013, B:7:0x0037, B:11:0x0041, B:13:0x004a, B:17:0x0054, B:19:0x005d, B:23:0x0067, B:25:0x007d, B:29:0x0087, B:31:0x0090, B:35:0x009a, B:37:0x00a3, B:41:0x00ad, B:43:0x00c3, B:47:0x00cd, B:49:0x00d6, B:53:0x00e0, B:55:0x00e9, B:57:0x00ef, B:61:0x00f9, B:63:0x010f, B:67:0x0119, B:69:0x0122, B:73:0x012c, B:75:0x0135, B:77:0x013b, B:81:0x0145, B:83:0x015b, B:87:0x0165, B:89:0x016e, B:93:0x0178, B:96:0x0184, B:99:0x019d, B:102:0x01a9, B:105:0x01b5, B:108:0x01ce, B:111:0x01da, B:114:0x01e6, B:117:0x01ff, B:120:0x020b, B:123:0x0217), top: B:4:0x0013 }] */
            /* JADX WARN: Removed duplicated region for block: B:69:0x0122 A[Catch: IOException | JSONException -> 0x022c, IOException -> 0x022e, TryCatch #4 {IOException | JSONException -> 0x022c, blocks: (B:5:0x0013, B:7:0x0037, B:11:0x0041, B:13:0x004a, B:17:0x0054, B:19:0x005d, B:23:0x0067, B:25:0x007d, B:29:0x0087, B:31:0x0090, B:35:0x009a, B:37:0x00a3, B:41:0x00ad, B:43:0x00c3, B:47:0x00cd, B:49:0x00d6, B:53:0x00e0, B:55:0x00e9, B:57:0x00ef, B:61:0x00f9, B:63:0x010f, B:67:0x0119, B:69:0x0122, B:73:0x012c, B:75:0x0135, B:77:0x013b, B:81:0x0145, B:83:0x015b, B:87:0x0165, B:89:0x016e, B:93:0x0178, B:96:0x0184, B:99:0x019d, B:102:0x01a9, B:105:0x01b5, B:108:0x01ce, B:111:0x01da, B:114:0x01e6, B:117:0x01ff, B:120:0x020b, B:123:0x0217), top: B:4:0x0013 }] */
            /* JADX WARN: Removed duplicated region for block: B:75:0x0135 A[Catch: IOException | JSONException -> 0x022c, IOException -> 0x022e, TryCatch #4 {IOException | JSONException -> 0x022c, blocks: (B:5:0x0013, B:7:0x0037, B:11:0x0041, B:13:0x004a, B:17:0x0054, B:19:0x005d, B:23:0x0067, B:25:0x007d, B:29:0x0087, B:31:0x0090, B:35:0x009a, B:37:0x00a3, B:41:0x00ad, B:43:0x00c3, B:47:0x00cd, B:49:0x00d6, B:53:0x00e0, B:55:0x00e9, B:57:0x00ef, B:61:0x00f9, B:63:0x010f, B:67:0x0119, B:69:0x0122, B:73:0x012c, B:75:0x0135, B:77:0x013b, B:81:0x0145, B:83:0x015b, B:87:0x0165, B:89:0x016e, B:93:0x0178, B:96:0x0184, B:99:0x019d, B:102:0x01a9, B:105:0x01b5, B:108:0x01ce, B:111:0x01da, B:114:0x01e6, B:117:0x01ff, B:120:0x020b, B:123:0x0217), top: B:4:0x0013 }] */
            /* JADX WARN: Removed duplicated region for block: B:83:0x015b A[Catch: IOException | JSONException -> 0x022c, IOException -> 0x022e, TryCatch #4 {IOException | JSONException -> 0x022c, blocks: (B:5:0x0013, B:7:0x0037, B:11:0x0041, B:13:0x004a, B:17:0x0054, B:19:0x005d, B:23:0x0067, B:25:0x007d, B:29:0x0087, B:31:0x0090, B:35:0x009a, B:37:0x00a3, B:41:0x00ad, B:43:0x00c3, B:47:0x00cd, B:49:0x00d6, B:53:0x00e0, B:55:0x00e9, B:57:0x00ef, B:61:0x00f9, B:63:0x010f, B:67:0x0119, B:69:0x0122, B:73:0x012c, B:75:0x0135, B:77:0x013b, B:81:0x0145, B:83:0x015b, B:87:0x0165, B:89:0x016e, B:93:0x0178, B:96:0x0184, B:99:0x019d, B:102:0x01a9, B:105:0x01b5, B:108:0x01ce, B:111:0x01da, B:114:0x01e6, B:117:0x01ff, B:120:0x020b, B:123:0x0217), top: B:4:0x0013 }] */
            /* JADX WARN: Removed duplicated region for block: B:89:0x016e A[Catch: IOException | JSONException -> 0x022c, IOException -> 0x022e, TryCatch #4 {IOException | JSONException -> 0x022c, blocks: (B:5:0x0013, B:7:0x0037, B:11:0x0041, B:13:0x004a, B:17:0x0054, B:19:0x005d, B:23:0x0067, B:25:0x007d, B:29:0x0087, B:31:0x0090, B:35:0x009a, B:37:0x00a3, B:41:0x00ad, B:43:0x00c3, B:47:0x00cd, B:49:0x00d6, B:53:0x00e0, B:55:0x00e9, B:57:0x00ef, B:61:0x00f9, B:63:0x010f, B:67:0x0119, B:69:0x0122, B:73:0x012c, B:75:0x0135, B:77:0x013b, B:81:0x0145, B:83:0x015b, B:87:0x0165, B:89:0x016e, B:93:0x0178, B:96:0x0184, B:99:0x019d, B:102:0x01a9, B:105:0x01b5, B:108:0x01ce, B:111:0x01da, B:114:0x01e6, B:117:0x01ff, B:120:0x020b, B:123:0x0217), top: B:4:0x0013 }] */
            /* JADX WARN: Removed duplicated region for block: B:95:0x0181  */
            /* JADX WARN: Removed duplicated region for block: B:98:0x019a  */
            @Override // com.loopj.android.http.JsonHttpResponseHandler
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct add '--show-bad-code' argument
            */
            public void onSuccess(int r17, cz.msebera.android.httpclient.Header[] r18, org.json.JSONObject r19) {
                /*
                    Method dump skipped, instructions count: 573
                    To view this dump add '--comments-level debug' option
                */
                throw new UnsupportedOperationException("Method not decompiled: com.p001yd.electricecollector.model.AuthRepository.C10491.onSuccess(int, cz.msebera.android.httpclient.Header[], org.json.JSONObject):void");
            }
        });
    }
}
