package com.p001yd.electricecollector.model;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.p001yd.electricecollector.HttpClientIntCallback;
import com.p001yd.electricecollector.entities.ItemBonds;
import com.p001yd.electricecollector.network.RestServiceHelper;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import cz.msebera.android.httpclient.Header;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes5.dex */
public class BondsRepository {
    private final String TAG = getClass().getSimpleName();
    private String _appId;
    private String _baseUrl;
    private String _token;

    public BondsRepository(String str, String str2, String str3) {
        this._baseUrl = str;
        this._token = str2;
        this._appId = str3;
    }

    public void delete(ItemBonds itemBonds, final HttpClientIntCallback<ItemBonds> httpClientIntCallback) throws JSONException {
        RestServiceHelper.delete(String.format("%s?appId=%s&id=%s", String.format("%s%s", this._baseUrl, "DeleteBond"), this._appId, itemBonds.getNmstnd()), this._token, new JsonHttpResponseHandler() { // from class: com.yd.electricecollector.model.BondsRepository.1
            @Override // com.loopj.android.http.JsonHttpResponseHandler, com.loopj.android.http.TextHttpResponseHandler
            public void onFailure(int i, Header[] headerArr, String str, Throwable th) {
                httpClientIntCallback.onError(th);
            }

            @Override // com.loopj.android.http.JsonHttpResponseHandler
            public void onFailure(int i, Header[] headerArr, Throwable th, JSONObject jSONObject) {
                httpClientIntCallback.onError(th);
            }

            @Override // com.loopj.android.http.JsonHttpResponseHandler
            public void onSuccess(int i, Header[] headerArr, JSONObject jSONObject) {
                try {
                    httpClientIntCallback.onSucceed(jSONObject.getJSONObject("DeleteBondResult").getString("Result"));
                } catch (Throwable th) {
                    httpClientIntCallback.onError(th);
                }
            }
        });
    }

    public void getAll(int i, int i2, int[] iArr, String str, final HttpClientIntCallback<ItemBonds> httpClientIntCallback) {
        if (str == null) {
            str = "";
        }
        RestServiceHelper.get(String.format("%s?appId=%s&pageNumber=%s&pageSize=%s&name=%s", String.format("%s%s", this._baseUrl, "GetListBonds"), this._appId, Integer.valueOf(i), Integer.valueOf(i2), str), this._token, new JsonHttpResponseHandler() { // from class: com.yd.electricecollector.model.BondsRepository.2
            @Override // com.loopj.android.http.JsonHttpResponseHandler, com.loopj.android.http.TextHttpResponseHandler
            public void onFailure(int i3, Header[] headerArr, String str2, Throwable th) {
                httpClientIntCallback.onError(th);
            }

            @Override // com.loopj.android.http.JsonHttpResponseHandler
            public void onFailure(int i3, Header[] headerArr, Throwable th, JSONObject jSONObject) {
                httpClientIntCallback.onError(th);
            }

            @Override // com.loopj.android.http.JsonHttpResponseHandler
            public void onSuccess(int i3, Header[] headerArr, JSONObject jSONObject) {
                try {
                    JsonAdapter adapter = new Moshi.Builder().build().adapter(ItemBonds.class);
                    ArrayList arrayList = new ArrayList();
                    JSONArray jSONArray = jSONObject.getJSONArray("GetListBondsResult");
                    for (int i4 = 0; i4 < jSONArray.length(); i4++) {
                        arrayList.add((ItemBonds) adapter.fromJson(jSONArray.getJSONObject(i4).toString()));
                    }
                    httpClientIntCallback.onSucceed(arrayList);
                } catch (Throwable th) {
                    httpClientIntCallback.onError(th);
                }
            }
        });
    }

    public void save(ItemBonds itemBonds, final HttpClientIntCallback<ItemBonds> httpClientIntCallback) throws JSONException {
        RestServiceHelper.post(String.format("%s?appId=%s", String.format("%s%s", this._baseUrl, "SaveBond"), this._appId), new Moshi.Builder().build().adapter(ItemBonds.class).toJson(itemBonds), this._token, new JsonHttpResponseHandler() { // from class: com.yd.electricecollector.model.BondsRepository.3
            @Override // com.loopj.android.http.JsonHttpResponseHandler, com.loopj.android.http.TextHttpResponseHandler
            public void onFailure(int i, Header[] headerArr, String str, Throwable th) {
                httpClientIntCallback.onError(th);
            }

            @Override // com.loopj.android.http.JsonHttpResponseHandler
            public void onFailure(int i, Header[] headerArr, Throwable th, JSONObject jSONObject) {
                httpClientIntCallback.onError(th);
            }

            @Override // com.loopj.android.http.JsonHttpResponseHandler
            public void onSuccess(int i, Header[] headerArr, JSONObject jSONObject) {
                try {
                    httpClientIntCallback.onSucceed(jSONObject.getJSONObject("SaveBondResult"));
                } catch (Throwable th) {
                    httpClientIntCallback.onError(th);
                }
            }
        });
    }

    public void update(ItemBonds itemBonds, final HttpClientIntCallback<ItemBonds> httpClientIntCallback) throws JSONException {
        RestServiceHelper.put(String.format("%s?appId=%s&id=%s", String.format("%s%s", this._baseUrl, "UpdateBond"), this._appId, itemBonds.getNmstnd()), new Moshi.Builder().build().adapter(ItemBonds.class).toJson(itemBonds), this._token, new JsonHttpResponseHandler() { // from class: com.yd.electricecollector.model.BondsRepository.4
            @Override // com.loopj.android.http.JsonHttpResponseHandler, com.loopj.android.http.TextHttpResponseHandler
            public void onFailure(int i, Header[] headerArr, String str, Throwable th) {
                httpClientIntCallback.onError(th);
            }

            @Override // com.loopj.android.http.JsonHttpResponseHandler
            public void onFailure(int i, Header[] headerArr, Throwable th, JSONObject jSONObject) {
                httpClientIntCallback.onError(th);
            }

            @Override // com.loopj.android.http.JsonHttpResponseHandler
            public void onSuccess(int i, Header[] headerArr, JSONObject jSONObject) {
                try {
                    httpClientIntCallback.onSucceed(jSONObject.getJSONObject("UpdateBondResult"));
                } catch (Throwable th) {
                    httpClientIntCallback.onError(th);
                }
            }
        });
    }
}
