package com.p001yd.electricecollector;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.p001yd.electricecollector.common.AppConfig;
import com.p001yd.electricecollector.entities.CompanyInfoResult;
import com.p001yd.electricecollector.printer.andoirdbluetoothprint.DeviceListActivity;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import cz.msebera.android.httpclient.Header;
import java.io.IOException;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes6.dex */
public class Preferences extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int PERMISSION_REQUEST_CODE_WRITE = 3;
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() { // from class: com.yd.electricecollector.Preferences.4
        @Override // android.preference.Preference.OnPreferenceChangeListener
        public boolean onPreferenceChange(Preference preference, Object obj) {
            preference.setSummary(obj.toString());
            return true;
        }
    };
    private EditTextPreference AgentId;
    private EditTextPreference CompanyAddressValue;
    private Preference CompanyDetailsScreen;
    private EditTextPreference CompanyNameValue;
    private EditTextPreference CompanyPhoneValue;
    private ListPreference ListFontSize;
    private ListPreference ListPaperSize;
    private ListPreference ListPrinterType;
    private Preference LockCustomValue;
    private Preference Logo;
    private EditTextPreference MessageFooterKey;
    private EditTextPreference MessageTextKey;
    private EditTextPreference PeriodCustomValue;
    AppConfig appConfig;
    private ListPreference listp;
    private Preference printerSelection;

    /* renamed from: ps */
    PreferenceScreen f611ps;
    SharedPreferences sharedPrefs;
    boolean result = false;
    boolean iscomplette = false;
    Preference.OnPreferenceChangeListener numberCheckListener = new Preference.OnPreferenceChangeListener() { // from class: com.yd.electricecollector.Preferences.1
        @Override // android.preference.Preference.OnPreferenceChangeListener
        public boolean onPreferenceChange(Preference preference, Object obj) {
            return Preferences.this.numberCheck(obj);
        }
    };
    Preference.OnPreferenceClickListener LogoClickListener = new Preference.OnPreferenceClickListener() { // from class: com.yd.electricecollector.Preferences.2
        @Override // android.preference.Preference.OnPreferenceClickListener
        public boolean onPreferenceClick(Preference preference) {
            Preferences.this.startActivityForResult(new Intent(Preferences.this, (Class<?>) LogoActivity.class), 7);
            return true;
        }
    };
    Preference.OnPreferenceClickListener printerSelectionClickListener = new Preference.OnPreferenceClickListener() { // from class: com.yd.electricecollector.Preferences.3
        @Override // android.preference.Preference.OnPreferenceClickListener
        public boolean onPreferenceClick(Preference preference) {
            Preferences.this.startActivityForResult(new Intent(Preferences.this, (Class<?>) DeviceListActivity.class), 6);
            return true;
        }
    };
    Preference.OnPreferenceChangeListener MessageFooterListener = new Preference.OnPreferenceChangeListener() { // from class: com.yd.electricecollector.Preferences.7
        @Override // android.preference.Preference.OnPreferenceChangeListener
        public boolean onPreferenceChange(Preference preference, Object obj) {
            preference.setSummary(obj.toString());
            return true;
        }
    };
    Preference.OnPreferenceClickListener PatternClickListener = new Preference.OnPreferenceClickListener() { // from class: com.yd.electricecollector.Preferences.8
        @Override // android.preference.Preference.OnPreferenceClickListener
        public boolean onPreferenceClick(Preference preference) {
            return true;
        }
    };
    Preference.OnPreferenceChangeListener lenthPassCheckListener = new Preference.OnPreferenceChangeListener() { // from class: com.yd.electricecollector.Preferences.9
        @Override // android.preference.Preference.OnPreferenceChangeListener
        public boolean onPreferenceChange(Preference preference, Object obj) {
            preference.setSummary(obj.toString());
            return Preferences.this.lenthPassCheck(obj);
        }
    };
    Preference.OnPreferenceClickListener passClickListener = new Preference.OnPreferenceClickListener() { // from class: com.yd.electricecollector.Preferences.10
        @Override // android.preference.Preference.OnPreferenceClickListener
        public boolean onPreferenceClick(Preference preference) {
            return Preferences.this.openDialogPassword();
        }
    };
    Preference.OnPreferenceChangeListener ListChangeListener = new Preference.OnPreferenceChangeListener() { // from class: com.yd.electricecollector.Preferences.11
        @Override // android.preference.Preference.OnPreferenceChangeListener
        public boolean onPreferenceChange(Preference preference, Object obj) {
            String obj2 = obj.toString();
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) Preferences.this.findPreference("LockType");
                int findIndexOfValue = listPreference.findIndexOfValue(obj2);
                preference.setSummary(findIndexOfValue >= 0 ? listPreference.getEntries()[findIndexOfValue] : null);
            }
            Preferences.this.LockTypeCustomValueEnabled(obj2);
            return true;
        }
    };
    Preference.OnPreferenceChangeListener ListChangeListener2 = new Preference.OnPreferenceChangeListener() { // from class: com.yd.electricecollector.Preferences.12
        @Override // android.preference.Preference.OnPreferenceChangeListener
        public boolean onPreferenceChange(Preference preference, Object obj) {
            String obj2 = obj.toString();
            if (!(preference instanceof ListPreference)) {
                return true;
            }
            ListPreference listPreference = (ListPreference) Preferences.this.findPreference("PaperSize");
            int findIndexOfValue = listPreference.findIndexOfValue(obj2);
            CharSequence[] entries = listPreference.getEntries();
            preference.setSummary(findIndexOfValue >= 0 ? entries[findIndexOfValue] : entries[0]);
            return true;
        }
    };
    Preference.OnPreferenceChangeListener ListChangeListener3 = new Preference.OnPreferenceChangeListener() { // from class: com.yd.electricecollector.Preferences.13
        @Override // android.preference.Preference.OnPreferenceChangeListener
        public boolean onPreferenceChange(Preference preference, Object obj) {
            String obj2 = obj.toString();
            if (!(preference instanceof ListPreference)) {
                return true;
            }
            ListPreference listPreference = (ListPreference) Preferences.this.findPreference("PrinterType");
            int findIndexOfValue = listPreference.findIndexOfValue(obj2);
            CharSequence[] entries = listPreference.getEntries();
            preference.setSummary(findIndexOfValue >= 0 ? entries[findIndexOfValue] : entries[0]);
            return true;
        }
    };
    Preference.OnPreferenceChangeListener ListChangeListener4 = new Preference.OnPreferenceChangeListener() { // from class: com.yd.electricecollector.Preferences.14
        @Override // android.preference.Preference.OnPreferenceChangeListener
        public boolean onPreferenceChange(Preference preference, Object obj) {
            String obj2 = obj.toString();
            if (!(preference instanceof ListPreference)) {
                return true;
            }
            ListPreference listPreference = (ListPreference) Preferences.this.findPreference("FontSize");
            int findIndexOfValue = listPreference.findIndexOfValue(obj2);
            CharSequence[] entries = listPreference.getEntries();
            preference.setSummary(findIndexOfValue >= 0 ? entries[findIndexOfValue] : entries[0]);
            return true;
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    public void LockTypeCustomValueEnabled(String str) {
        if (str.equals("1")) {
            this.LockCustomValue.setEnabled(true);
            SharedPreferences.Editor edit = this.sharedPrefs.edit();
            edit.putString("AccountPattern", "0");
            edit.apply();
            if (this.iscomplette) {
                openDialogPassword();
                return;
            }
            return;
        }
        if (str.equals("2")) {
            this.LockCustomValue.setEnabled(false);
            SharedPreferences.Editor edit2 = this.sharedPrefs.edit();
            edit2.putString("AccountPassword", "0");
            edit2.apply();
            this.LockCustomValue.setSummary("0");
            return;
        }
        this.LockCustomValue.setEnabled(false);
        SharedPreferences.Editor edit3 = this.sharedPrefs.edit();
        edit3.putString("AccountPassword", "0");
        edit3.apply();
        this.LockCustomValue.setSummary("0");
    }

    private static void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(), ""));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean lenthPassCheck(Object obj) {
        if (!obj.toString().equals("") && obj.toString().length() > 3) {
            return true;
        }
        Utils.msgBox("يجب الا يقل طول كلمة المرور عن 4 ", this, new Object[0]);
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean numberCheck(Object obj) {
        if (!obj.toString().equals("") && obj.toString().matches("\\d*")) {
            return true;
        }
        Utils.msgBox(C1018R.string.msgInvalidNumber, this, new Object[0]);
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean openDialogPassword() {
        this.result = false;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("ادخل كلمة المرور");
        final EditText editText = new EditText(this);
        editText.setGravity(4);
        builder.setView(editText);
        editText.setText(this.sharedPrefs.getString("AccountPassword", "0"));
        editText.setGravity(1);
        editText.setHint("ادخل كلمة المرور");
        editText.setFocusableInTouchMode(true);
        builder.setIcon(C1018R.drawable.ic_lock);
        builder.setPositiveButton("موافق", new DialogInterface.OnClickListener() { // from class: com.yd.electricecollector.Preferences.15
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                if (editText.length() < 4) {
                    editText.requestFocus();
                    editText.setError("يجب الا يقل طول كلمة المرور عن 4 ");
                    Utils.msgBox("يجب الا يقل طول كلمة المرور عن 4 ", Preferences.this, new Object[0]);
                } else {
                    SharedPreferences.Editor edit = Preferences.this.sharedPrefs.edit();
                    edit.putString("AccountPassword", editText.getText().toString());
                    edit.apply();
                    Preferences.this.LockCustomValue.setSummary("****");
                    dialogInterface.dismiss();
                    Preferences.this.result = true;
                }
            }
        });
        AlertDialog create = builder.create();
        create.setCancelable(false);
        create.setCanceledOnTouchOutside(false);
        create.show();
        return this.result;
    }

    private void periodCustomValueEnabled(String str) {
        if (str.equals("0")) {
            this.PeriodCustomValue.setEnabled(true);
        } else {
            this.PeriodCustomValue.setEnabled(false);
        }
    }

    @Override // android.preference.PreferenceActivity, android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i == 6 && i2 == -1) {
            Bundle extras = intent.getExtras();
            String string = extras.getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
            String string2 = extras.getString("name");
            if (string == null || string == "") {
                return;
            }
            SharedPreferences.Editor edit = this.sharedPrefs.edit();
            edit.putString("PrinterSelection", string);
            edit.commit();
            TAPreferences.setSelectedPrinterModel(this, string2);
        }
    }

    @Override // android.preference.PreferenceActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        this.sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        super.onCreate(bundle);
        addPreferencesFromResource(C1018R.xml.setting_preferences);
        this.appConfig = AppConfig.getInstance();
        this.CompanyDetailsScreen = getPreferenceScreen().findPreference("AsyncCompInfo");
        this.CompanyNameValue = (EditTextPreference) getPreferenceScreen().findPreference("CompanyName");
        this.CompanyNameValue.setEnabled(false);
        this.CompanyPhoneValue = (EditTextPreference) getPreferenceScreen().findPreference("Phone");
        this.CompanyPhoneValue.setEnabled(false);
        this.CompanyAddressValue = (EditTextPreference) getPreferenceScreen().findPreference("Address");
        this.CompanyAddressValue.setEnabled(false);
        this.CompanyNameValue.setSummary(this.sharedPrefs.getString("CompanyName", null));
        this.CompanyPhoneValue.setSummary(this.sharedPrefs.getString("Phone", null));
        this.CompanyAddressValue.setSummary(this.sharedPrefs.getString("Address", null));
        if (this.appConfig.getUser().getSYS() != 1) {
            this.CompanyNameValue.setEnabled(false);
            this.CompanyPhoneValue.setEnabled(false);
            this.CompanyAddressValue.setEnabled(false);
        }
        this.CompanyDetailsScreen.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.yd.electricecollector.Preferences.5
            @Override // android.preference.Preference.OnPreferenceClickListener
            public boolean onPreferenceClick(Preference preference) {
                ((PreferenceCategory) Preferences.this.findPreference("CompanyDetailsMain2")).addPreference(new LoadingPreference(Preferences.this));
                if (TokenManager.getInstance(Preferences.this.getSharedPreferences("prefs", 0)).getToken() == null) {
                    Preferences.this.startActivity(new Intent(Preferences.this, (Class<?>) LoginActivity.class));
                    Preferences.this.finish();
                }
                AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
                asyncHttpClient.addHeader("Authorization", "Bearer " + Preferences.this.appConfig.getToken());
                asyncHttpClient.get(new StringBuilder().append(Preferences.this.appConfig.getBaseUrl()).append("GetCompanyData").toString(), new JsonHttpResponseHandler() { // from class: com.yd.electricecollector.Preferences.5.1
                    static final /* synthetic */ boolean $assertionsDisabled = false;

                    @Override // com.loopj.android.http.JsonHttpResponseHandler
                    public void onFailure(int i, Header[] headerArr, Throwable th, JSONObject jSONObject) {
                        ((PreferenceCategory) Preferences.this.findPreference("CompanyDetailsMain2")).removeAll();
                    }

                    @Override // com.loopj.android.http.JsonHttpResponseHandler
                    public void onSuccess(int i, Header[] headerArr, JSONObject jSONObject) {
                        JsonAdapter adapter = new Moshi.Builder().build().adapter(CompanyInfoResult.class);
                        JSONObject jSONObject2 = null;
                        try {
                            jSONObject2 = jSONObject.getJSONObject("GetCompanyDataResult");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            if (jSONObject2 == null) {
                                throw new AssertionError();
                            }
                            CompanyInfoResult companyInfoResult = (CompanyInfoResult) adapter.fromJson(jSONObject2.toString());
                            if (companyInfoResult != null) {
                                SharedPreferences.Editor edit = Preferences.this.sharedPrefs.edit();
                                edit.putString("CompanyName", companyInfoResult.getCompanyName());
                                edit.putString("Phone", companyInfoResult.getTelephone());
                                edit.putString("Address", companyInfoResult.getAddress());
                                edit.apply();
                                Preferences.this.CompanyNameValue.setSummary(Preferences.this.sharedPrefs.getString("CompanyName", null));
                                Preferences.this.CompanyPhoneValue.setSummary(Preferences.this.sharedPrefs.getString("Phone", null));
                                Preferences.this.CompanyAddressValue.setSummary(Preferences.this.sharedPrefs.getString("Address", null));
                            }
                            ((PreferenceCategory) Preferences.this.findPreference("CompanyDetailsMain2")).removeAll();
                        } catch (IOException e2) {
                            e2.printStackTrace();
                        }
                    }
                });
                return false;
            }
        });
        this.f611ps = (PreferenceScreen) getPreferenceScreen().findPreference("SecuretyScreen");
        this.f611ps.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.yd.electricecollector.Preferences.6
            @Override // android.preference.Preference.OnPreferenceClickListener
            public boolean onPreferenceClick(Preference preference) {
                if (Integer.parseInt(Preferences.this.sharedPrefs.getString("LockType", "0")) == 0) {
                    return false;
                }
                Preferences.this.startActivityForResult(new Intent(Preferences.this, (Class<?>) EnterPasswordActivity.class), 9);
                return true;
            }
        });
        this.listp = (ListPreference) findPreference("LockType");
        this.listp.setValueIndex(Integer.parseInt(this.sharedPrefs.getString("LockType", "1")));
        this.listp.setSummary(this.listp.getEntry());
        this.listp.setOnPreferenceChangeListener(this.ListChangeListener);
        this.ListPaperSize = (ListPreference) findPreference("PaperSize");
        this.ListPaperSize.setValueIndex(Integer.parseInt(this.sharedPrefs.getString("PaperSize", "0")));
        this.ListPaperSize.setSummary(this.ListPaperSize.getEntry());
        this.ListPaperSize.setOnPreferenceChangeListener(this.ListChangeListener3);
        this.ListPrinterType = (ListPreference) findPreference("PrinterType");
        this.ListPrinterType.setValueIndex(Integer.parseInt(this.sharedPrefs.getString("PrinterType", "0")));
        this.ListPrinterType.setSummary(this.ListPrinterType.getEntry());
        this.ListPrinterType.setOnPreferenceChangeListener(this.ListChangeListener2);
        this.ListFontSize = (ListPreference) findPreference("FontSize");
        this.ListFontSize.setValueIndex(Integer.parseInt(this.sharedPrefs.getString("FontSize", "0")));
        this.ListFontSize.setSummary(this.ListFontSize.getEntry());
        this.ListFontSize.setOnPreferenceChangeListener(this.ListChangeListener4);
        this.LockCustomValue = getPreferenceScreen().findPreference("AccountPassword");
        this.PeriodCustomValue = (EditTextPreference) getPreferenceScreen().findPreference("PeriodCustomValue");
        periodCustomValueEnabled(this.sharedPrefs.getString("PeriodType", "0"));
        this.PeriodCustomValue.setOnPreferenceChangeListener(this.numberCheckListener);
        this.LockCustomValue.setSummary("****");
        this.LockCustomValue.setOnPreferenceClickListener(this.passClickListener);
        this.printerSelection = getPreferenceScreen().findPreference("PrinterSelection");
        this.printerSelection.setOnPreferenceClickListener(this.printerSelectionClickListener);
        this.Logo = getPreferenceScreen().findPreference("CompanyLogo");
        this.Logo.setOnPreferenceClickListener(this.LogoClickListener);
        this.MessageTextKey = (EditTextPreference) getPreferenceScreen().findPreference("message_text_key");
        this.MessageFooterKey = (EditTextPreference) getPreferenceScreen().findPreference("message_footer_key");
        this.MessageFooterKey.setOnPreferenceChangeListener(this.MessageFooterListener);
        this.MessageTextKey.setOnPreferenceChangeListener(this.MessageFooterListener);
        this.PeriodCustomValue.setSummary(this.sharedPrefs.getString("PeriodCustomValue", "0"));
        bindPreferenceSummaryToValue(this.PeriodCustomValue);
    }

    @Override // android.app.Activity
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override // android.content.SharedPreferences.OnSharedPreferenceChangeListener
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String str) {
        if (str.equals("PeriodType")) {
            periodCustomValueEnabled(sharedPreferences.getString(str, "0"));
        }
        str.equals("HostingIP");
        if (str.equals("CompanyName")) {
            this.CompanyNameValue.setSummary(this.CompanyNameValue.getText());
        }
        if (str.equals("Address")) {
            this.CompanyAddressValue.setSummary(this.CompanyAddressValue.getText());
        }
        if (str.equals("Phone")) {
            this.CompanyPhoneValue.setSummary(this.CompanyPhoneValue.getText());
        }
    }
}
