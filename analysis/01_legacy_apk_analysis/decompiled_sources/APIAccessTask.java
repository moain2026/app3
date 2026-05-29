package com.p001yd.electricecollector;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import org.json.JSONException;

/* loaded from: classes6.dex */
class APIAccessTask extends AsyncTask<String, Void, APIResponseObject> {
    Context context;
    public OnCompleteListener delegate;
    List<Pair<String, String>> headerData;
    String method;
    List<Pair<String, String>> postData;
    URL requestUrl;
    int responseCode;
    HttpURLConnection urlConnection;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes6.dex */
    public static class APIResponseObject {
        String response;
        int responseCode;

        APIResponseObject(int i, String str) {
            this.responseCode = i;
            this.response = str;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes6.dex */
    public interface OnCompleteListener {
        void onComplete(APIResponseObject aPIResponseObject) throws JSONException;
    }

    APIAccessTask(Context context, String str, String str2, OnCompleteListener onCompleteListener) {
        this.responseCode = 200;
        this.delegate = null;
        this.context = context;
        this.delegate = onCompleteListener;
        this.method = str2;
        try {
            this.requestUrl = new URL(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    APIAccessTask(Context context, String str, String str2, List<Pair<String, String>> list, OnCompleteListener onCompleteListener) {
        this(context, str, str2, onCompleteListener);
        this.postData = list;
    }

    APIAccessTask(Context context, String str, String str2, List<Pair<String, String>> list, List<Pair<String, String>> list2, OnCompleteListener onCompleteListener) {
        this(context, str, str2, list, onCompleteListener);
        this.headerData = list2;
    }

    private String getPostDataString(List<Pair<String, String>> list) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        boolean z = true;
        for (Pair<String, String> pair : list) {
            if (z) {
                z = false;
            } else {
                sb.append("&");
            }
            sb.append(URLEncoder.encode((String) pair.first, "UTF-8"));
            sb.append("=");
            sb.append(URLEncoder.encode((String) pair.second, "UTF-8"));
        }
        return sb.toString();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.os.AsyncTask
    public APIResponseObject doInBackground(String... strArr) {
        Log.d(BuildConfig.BUILD_TYPE, "url = " + this.requestUrl);
        try {
            this.urlConnection = (HttpURLConnection) this.requestUrl.openConnection();
            if (this.headerData != null) {
                for (Pair<String, String> pair : this.headerData) {
                    this.urlConnection.setRequestProperty(pair.first.toString(), pair.second.toString());
                }
            }
            this.urlConnection.setDoInput(true);
            this.urlConnection.setChunkedStreamingMode(0);
            this.urlConnection.setRequestMethod(this.method);
            this.urlConnection.connect();
            StringBuilder sb = new StringBuilder();
            if (!this.method.equals("GET")) {
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(this.urlConnection.getOutputStream());
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(bufferedOutputStream, "UTF-8"));
                bufferedWriter.write(getPostDataString(this.postData));
                bufferedWriter.flush();
                bufferedWriter.close();
                bufferedOutputStream.close();
            }
            this.responseCode = this.urlConnection.getResponseCode();
            if (this.responseCode == 200) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new BufferedInputStream(this.urlConnection.getInputStream()), "UTF-8"));
                while (true) {
                    String readLine = bufferedReader.readLine();
                    if (readLine == null) {
                        break;
                    }
                    sb.append(readLine);
                }
            }
            return new APIResponseObject(this.responseCode, sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.os.AsyncTask
    public void onPostExecute(APIResponseObject aPIResponseObject) {
        try {
            this.delegate.onComplete(aPIResponseObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        super.onPostExecute((APIAccessTask) aPIResponseObject);
    }

    @Override // android.os.AsyncTask
    protected void onPreExecute() {
        super.onPreExecute();
    }
}
