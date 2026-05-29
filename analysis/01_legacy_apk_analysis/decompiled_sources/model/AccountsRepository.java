package com.p001yd.electricecollector.model;

import android.util.Log;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.p001yd.electricecollector.HttpClientIntCallback;
import com.p001yd.electricecollector.entities.Users;
import com.p001yd.electricecollector.network.RestServiceHelper;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes5.dex */
public class AccountsRepository {
    private final String TAG = getClass().getSimpleName();
    private String _appId;
    private String _baseUrl;

    public AccountsRepository(String str, String str2) {
        this._baseUrl = String.format("%s%s", str, "GetListAccounts");
        this._appId = str2;
        Log.w(this.TAG, "_baseUrl:" + this._baseUrl);
    }

    public void auth(Users users, HttpClientIntCallback<Users> httpClientIntCallback) throws JSONException {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("username", users.getname());
            jSONObject.put("password", users.getPASS());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RestServiceHelper.post(String.format("%s", this._baseUrl), jSONObject.toString(), new JsonHttpResponseHandler());
    }
}
