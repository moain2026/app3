package com.p001yd.electricecollector.p002ui;

import android.R;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.NavUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.p001yd.electricecollector.Adapter.AccounttSearchAdapter;
import com.p001yd.electricecollector.C1018R;
import com.p001yd.electricecollector.DialogHelper;
import com.p001yd.electricecollector.LoginActivity;
import com.p001yd.electricecollector.TAPreferences;
import com.p001yd.electricecollector.TokenManager;
import com.p001yd.electricecollector.Utils;
import com.p001yd.electricecollector.Validation;
import com.p001yd.electricecollector.common.AppConfig;
import com.p001yd.electricecollector.entities.AccountBalanceInfo;
import com.p001yd.electricecollector.entities.AccountBalanceResponse;
import com.p001yd.electricecollector.entities.Accounts;
import com.p001yd.electricecollector.entities.AccountsResponse;
import com.p001yd.electricecollector.entities.Currency;
import com.p001yd.electricecollector.entities.CurrencyResponse;
import com.p001yd.electricecollector.entities.ItemBonds;
import com.p001yd.electricecollector.entities.Users;
import com.p001yd.electricecollector.network.ApiService;
import com.p001yd.electricecollector.network.DialogCallback;
import com.p001yd.electricecollector.network.RetrofitBuilder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/* loaded from: classes12.dex */
public class EntryBondsActivity extends AppCompatActivity {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static boolean retval;
    Accounts _account;
    private String _appId;
    private String _baseUrl;
    Currency _currency;
    private String _mode;
    private int _position;
    private String _token;
    Users _user;
    private int _versionNumber;
    AccounttSearchAdapter acAdp;
    private ArrayList<Accounts> acctList;
    private ArrayList<Accounts> acctListCust;
    private ArrayList<Accounts> acctListSupp;
    AppConfig appConfig;
    ImageView btnBal;
    Button btnClose;
    Button btnSave;
    private ArrayList<Currency> curr_List;
    TextInputEditText edtAmount;
    TextView edtDate;
    TextInputEditText edtDocNo;
    TextInputEditText edtEqual;
    TextInputEditText edtExchange;
    TextInputEditText edtNoConsomer;
    TextInputEditText edtNotes;
    ItemBonds itembond;
    private int mDay;
    private int mMonth;
    private int mYear;
    BondsPresenter presenter;
    LinearLayout progress;
    ApiService service;
    Spinner spCurrency;
    Spinner spnBoxes;
    private String tital;
    TokenManager tokenManager;
    TextView tvbal;
    TextView tvmstlm;
    TextView tvtblh;
    AlertDialog.Builder window;
    private final String TAG = getClass().getSimpleName();
    final String[] options = {"عميل", "مورد", "اخرى"};
    int currencyId = 0;
    TextInputEditText searchAccount = null;
    final Calendar myCalendar = Calendar.getInstance();
    Map<Integer, ArrayList<Accounts>> accountMap = new HashMap();
    private boolean _isUpdate = false;
    private int _typeCustomer = 0;
    BaseView<ItemBonds> baseViewAddEditHandler = new BaseView<ItemBonds>() { // from class: com.yd.electricecollector.ui.EntryBondsActivity.1
        @Override // com.p001yd.electricecollector.p002ui.BaseView
        public void onFailed(ItemBonds itemBonds) {
            EntryBondsActivity.this.progress.setVisibility(8);
            if (EntryBondsActivity.this._isUpdate) {
                Toast.makeText(EntryBondsActivity.this, "فشل عملية التعديل", 0).show();
            } else {
                Toast.makeText(EntryBondsActivity.this, "فشل عملية الاضافة", 0).show();
            }
        }

        @Override // com.p001yd.electricecollector.p002ui.BaseView
        public void onLoadDataFailure() {
        }

        @Override // com.p001yd.electricecollector.p002ui.BaseView
        public void onLoadDataSucceed(List<ItemBonds> list) {
        }

        @Override // com.p001yd.electricecollector.p002ui.BaseView
        public void onSucceed(ItemBonds itemBonds) {
            EntryBondsActivity.this.progress.setVisibility(8);
            Intent intent = EntryBondsActivity.this.getIntent();
            intent.putExtra("data", itemBonds);
            intent.putExtra("mode", EntryBondsActivity.this._mode);
            if (EntryBondsActivity.this._isUpdate) {
                intent.putExtra("position", EntryBondsActivity.this._position);
                EntryBondsActivity.this.setResult(201, intent);
            } else {
                EntryBondsActivity.this.setResult(101, intent);
            }
            EntryBondsActivity.this.finish();
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    public void CheckAmount() {
        if (!checkValidation()) {
            Toast.makeText(this, "خطا في ادخال البيانات", 0).show();
            return;
        }
        if (this._user.getNOA() == 0) {
            Toast.makeText(this, "لا يمكن حفظ العملية يجب تعيين حساب صندوق للمستخدم", 0).show();
            return;
        }
        if (!Utils.ComperDate(this._user.getDateServer(), Utils.getConvertEnglishDate(Utils.getShortDateStrApi(this, Utils.getDateFromString(this.edtDate.getText().toString())), "yyyy/mm/dd")).booleanValue()) {
            Toast.makeText(this, "تاريخ السند لا يساوي تاريخ السيرفر" + this._user.getDateServer(), 0).show();
        } else if (Utils.stringToNumber(this.edtAmount.getText().toString()) <= Utils.stringToNumber(this.tvbal.getText().toString()) || !TAPreferences.getNotfayAmount(this)) {
            save();
        } else {
            DialogHelper.msgDialogConfirm("المبلغ المقبوض اكبر من المديونية هل تريد المتابعة؟", this, new DialogCallback() { // from class: com.yd.electricecollector.ui.EntryBondsActivity.12
                @Override // com.p001yd.electricecollector.network.DialogCallback
                public void onCancel() {
                }

                @Override // com.p001yd.electricecollector.network.DialogCallback
                public void onOk(Object obj) {
                    EntryBondsActivity.this.save();
                }

                @Override // com.p001yd.electricecollector.network.DialogCallback
                public void onOk(Object obj, Object obj2) {
                }
            }).show();
        }
    }

    private void GetAccountBalance() {
        if (this._account != null) {
            HashMap hashMap = new HashMap();
            hashMap.put("accountid", "" + this._account.getnum());
            hashMap.put("appid", this.appConfig.getAppId());
            this.service = (ApiService) RetrofitBuilder.createServiceWithAuth(ApiService.class, this.tokenManager, this._baseUrl);
            this.service.GetAccountBalanceInfo(hashMap).enqueue(new Callback<AccountBalanceResponse>() { // from class: com.yd.electricecollector.ui.EntryBondsActivity.15
                @Override // retrofit2.Callback
                public void onFailure(Call<AccountBalanceResponse> call, Throwable th) {
                }

                @Override // retrofit2.Callback
                public void onResponse(Call<AccountBalanceResponse> call, Response<AccountBalanceResponse> response) {
                    Log.w(EntryBondsActivity.this.TAG, "onResponse: " + response);
                    if (response.isSuccessful()) {
                        List<AccountBalanceInfo> data = response.body().getData();
                        String str = "الرصيد: 0";
                        double d = 0.0d;
                        if (data != null) {
                            str = "";
                            for (AccountBalanceInfo accountBalanceInfo : data) {
                                str = str + " الرصيد:" + accountBalanceInfo.getBalance() + " العملة:" + accountBalanceInfo.getCurrencyName();
                                d += accountBalanceInfo.getBalanceLocal();
                            }
                            if (data.size() > 1) {
                                str = str + " الرصيد محلي:" + d;
                            }
                        }
                        Utils.msgBox(str, EntryBondsActivity.this, new Object[0]);
                    }
                }
            });
        }
    }

    private void GetAccountBalanceFinal() {
        if (this._account != null) {
            HashMap hashMap = new HashMap();
            hashMap.put("accountid", "" + this._account.getnum());
            hashMap.put("appid", this.appConfig.getAppId());
            this.service = (ApiService) RetrofitBuilder.createServiceWithAuth(ApiService.class, this.tokenManager, this._baseUrl);
            this.service.GetAccountBalance(hashMap).enqueue(new Callback<Object>() { // from class: com.yd.electricecollector.ui.EntryBondsActivity.14
                @Override // retrofit2.Callback
                public void onFailure(Call<Object> call, Throwable th) {
                }

                @Override // retrofit2.Callback
                public void onResponse(Call<Object> call, Response<Object> response) {
                    Log.w(EntryBondsActivity.this.TAG, "onResponse: " + response);
                    if (response.isSuccessful()) {
                        Object body = response.body();
                        String str = "0";
                        if (body != null) {
                            try {
                                str = String.valueOf(new JSONObject(body.toString()).getDouble("GetAccountBalanceResult"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Utils.msgBox(str, EntryBondsActivity.this, new Object[0]);
                        }
                    }
                }
            });
        }
    }

    private void GetItemBondNext() {
        HashMap hashMap = new HashMap();
        hashMap.put("appid", this.appConfig.getAppId());
        this.service = (ApiService) RetrofitBuilder.createServiceWithAuth(ApiService.class, this.tokenManager, this._baseUrl);
        this.service.GetBondRecieptRecordNext(hashMap).enqueue(new Callback<Object>() { // from class: com.yd.electricecollector.ui.EntryBondsActivity.11
            @Override // retrofit2.Callback
            public void onFailure(Call<Object> call, Throwable th) {
                Toast.makeText(EntryBondsActivity.this, "فشل تحميل رقم السند", 0).show();
            }

            @Override // retrofit2.Callback
            public void onResponse(Call<Object> call, Response<Object> response) {
                Object body;
                Log.w(EntryBondsActivity.this.TAG, "onResponse: " + response);
                if (!response.isSuccessful() || (body = response.body()) == null) {
                    return;
                }
                try {
                    String valueOf = String.valueOf(new JSONObject(body.toString()).getInt("GetBondRecieptRcordNextResult"));
                    EntryBondsActivity.this.edtDocNo.setText(valueOf);
                    EntryBondsActivity.this.edtDocNo.setText(valueOf);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void SetEnabled(boolean z) {
        this.edtAmount.setEnabled(z);
        this.edtDate.setEnabled(z);
        this.searchAccount.setEnabled(z);
        this.spCurrency.setEnabled(z);
        this.edtExchange.setEnabled(z);
        this.btnSave.setEnabled(z);
        this.btnBal.setEnabled(z);
    }

    private boolean checkValidation() {
        boolean z = Validation.hasText(this.searchAccount);
        if (!Validation.hasText(this.edtAmount)) {
            z = false;
        }
        if (this._user == null) {
            return false;
        }
        return z;
    }

    public static int getItemPosition(int i, ArrayList<Currency> arrayList) {
        if (arrayList == null) {
            return 0;
        }
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            if (arrayList.get(i2).getNum() == i) {
                return i2;
            }
        }
        return 0;
    }

    private void initializeView() {
        this.progress = (LinearLayout) findViewById(C1018R.id.progressDownload);
        this.spCurrency = (Spinner) findViewById(C1018R.id.spCurrency);
        this.searchAccount = (TextInputEditText) findViewById(C1018R.id.autoCompleteTextView);
        this.edtNoConsomer = (TextInputEditText) findViewById(C1018R.id.edtNoConsomer);
        this.edtDocNo = (TextInputEditText) findViewById(C1018R.id.edtDocNo);
        this.btnBal = (ImageView) findViewById(C1018R.id.edtAcctNo);
        this.edtDate = (TextView) findViewById(C1018R.id.edtDate);
        this.edtAmount = (TextInputEditText) findViewById(C1018R.id.edtAmount);
        this.edtEqual = (TextInputEditText) findViewById(C1018R.id.edtEqual);
        this.edtExchange = (TextInputEditText) findViewById(C1018R.id.edtExchange);
        this.edtNotes = (TextInputEditText) findViewById(C1018R.id.edtNotes);
        this.tvbal = (TextView) findViewById(C1018R.id.tvBal);
        this.tvtblh = (TextView) findViewById(C1018R.id.tvtblh);
        this.tvmstlm = (TextView) findViewById(C1018R.id.tvmstlm);
        this.btnSave = (Button) findViewById(C1018R.id.btnSave);
        this.btnClose = (Button) findViewById(C1018R.id.btnClose);
        this.btnBal.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.ui.EntryBondsActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (EntryBondsActivity.this.edtNoConsomer.length() <= 0) {
                    Utils.msgBox("ادخل رقم المشترك ", EntryBondsActivity.this, new Object[0]);
                    return;
                }
                EntryBondsActivity.this.progress.setVisibility(0);
                HashMap hashMap = new HashMap();
                hashMap.put("num", "" + EntryBondsActivity.this.appConfig.getUser().getNou());
                hashMap.put("acctid", "" + EntryBondsActivity.this.edtNoConsomer.getText().toString());
                hashMap.put("appid", EntryBondsActivity.this.appConfig.getAppId());
                EntryBondsActivity.this.service = (ApiService) RetrofitBuilder.createServiceWithAuth(ApiService.class, EntryBondsActivity.this.tokenManager, EntryBondsActivity.this.appConfig.getBaseUrl());
                EntryBondsActivity.this.service.GetListAccounts(hashMap).enqueue(new Callback<AccountsResponse>() { // from class: com.yd.electricecollector.ui.EntryBondsActivity.2.1
                    @Override // retrofit2.Callback
                    public void onFailure(Call<AccountsResponse> call, Throwable th) {
                        EntryBondsActivity.this.progress.setVisibility(8);
                        Log.w(EntryBondsActivity.this.TAG, "onFailure: " + th.getMessage());
                    }

                    @Override // retrofit2.Callback
                    public void onResponse(Call<AccountsResponse> call, Response<AccountsResponse> response) {
                        Log.w(EntryBondsActivity.this.TAG, "onResponse: " + response);
                        if (response.isSuccessful()) {
                            List<Accounts> data = response.body().getData();
                            if (data.size() > 0) {
                                EntryBondsActivity.this._account = data.get(0);
                                EntryBondsActivity.this.searchAccount.setText(EntryBondsActivity.this._account.getname());
                                EntryBondsActivity.this.tvbal.setText(Utils.numberToString(EntryBondsActivity.this._account.getBalance()));
                                EntryBondsActivity.this.tvtblh.setText(String.valueOf(EntryBondsActivity.this._account.getnamet()));
                                EntryBondsActivity.this.tvmstlm.setText(String.valueOf(EntryBondsActivity.this._account.getnamep()));
                                EntryBondsActivity.this.edtAmount.requestFocus();
                            } else {
                                Utils.msgBox("لايوجد بيانات  ", EntryBondsActivity.this, new Object[0]);
                            }
                        }
                        EntryBondsActivity.this.progress.setVisibility(8);
                    }
                });
            }
        });
        this.btnClose.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.ui.EntryBondsActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                EntryBondsActivity.this.finish();
            }
        });
        this.btnSave.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.ui.EntryBondsActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                EntryBondsActivity.this.CheckAmount();
            }
        });
        this.tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", 0));
        if (this.tokenManager.getToken() == null) {
            startActivity(new Intent(this, (Class<?>) LoginActivity.class));
            finish();
        }
        this.searchAccount.setOnTouchListener(new View.OnTouchListener() { // from class: com.yd.electricecollector.ui.EntryBondsActivity.5
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (1 == motionEvent.getAction()) {
                    Intent intent = new Intent(EntryBondsActivity.this, (Class<?>) LookupAccountsActivity.class);
                    intent.putExtra(LookupAccountsActivity.EXTRA_PROVINSI_ID_VALUE, 0);
                    if (EntryBondsActivity.this._account != null) {
                        intent.putExtra(LookupAccountsActivity.EXTRA_SELECTED_VALUE, EntryBondsActivity.this._account);
                    }
                    EntryBondsActivity.this.startActivityForResult(intent, 300);
                }
                return false;
            }
        });
        this.edtNoConsomer.setOnTouchListener(new View.OnTouchListener() { // from class: com.yd.electricecollector.ui.EntryBondsActivity.6
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                motionEvent.getAction();
                return false;
            }
        });
        loadCurrency();
        this.spCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.yd.electricecollector.ui.EntryBondsActivity.7
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                EntryBondsActivity.this.currencyId = ((Currency) EntryBondsActivity.this.curr_List.get(i)).getNum();
                EntryBondsActivity.this._currency = (Currency) EntryBondsActivity.this.curr_List.get(i);
                EntryBondsActivity.this.edtExchange.setText(Utils.numberToString(EntryBondsActivity.this._currency.getSars()));
                if (EntryBondsActivity.this.edtAmount.getText().toString().length() != 0) {
                    EntryBondsActivity.this.edtEqual.setText(Utils.numberToString(Utils.stringToNumber(EntryBondsActivity.this.edtAmount.getText().toString()) * EntryBondsActivity.this._currency.getSars()));
                }
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        this.edtExchange.addTextChangedListener(new TextWatcher() { // from class: com.yd.electricecollector.ui.EntryBondsActivity.8
            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
            }

            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                double d = 0.0d;
                double d2 = 0.0d;
                if (EntryBondsActivity.this.edtAmount.getText().toString().length() != 0 && EntryBondsActivity.this.edtExchange.getText().toString().length() != 0) {
                    d = Utils.stringToNumber(EntryBondsActivity.this.edtAmount.getText().toString());
                    d2 = Utils.stringToNumber(EntryBondsActivity.this.edtExchange.getText().toString());
                }
                EntryBondsActivity.this.edtEqual.setText(Utils.numberToString(d * d2));
            }
        });
        this.edtAmount.addTextChangedListener(new TextWatcher() { // from class: com.yd.electricecollector.ui.EntryBondsActivity.9
            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
            }

            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                double d = 0.0d;
                double d2 = 0.0d;
                if (EntryBondsActivity.this.edtAmount.getText().toString().length() != 0 && EntryBondsActivity.this.edtExchange.getText().toString().length() != 0) {
                    d = Utils.stringToNumber(EntryBondsActivity.this.edtAmount.getText().toString());
                    d2 = Utils.stringToNumber(EntryBondsActivity.this.edtExchange.getText().toString());
                }
                EntryBondsActivity.this.edtEqual.setText(Utils.numberToString(d * d2));
            }
        });
        this.edtDate.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.ui.EntryBondsActivity.10
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
            }
        });
        this.edtDate.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Calendar.getInstance().getTime()));
        ((CardView) findViewById(C1018R.id.cardview2)).setVisibility(8);
        this.edtEqual.setVisibility(8);
        this.edtDocNo.setVisibility(8);
        this.btnClose.setVisibility(8);
    }

    private void loadCurrency() {
        this.service = (ApiService) RetrofitBuilder.createServiceWithAuth(ApiService.class, this.tokenManager, this._baseUrl);
        this.service.GetListCurrency().enqueue(new Callback<CurrencyResponse>() { // from class: com.yd.electricecollector.ui.EntryBondsActivity.13
            @Override // retrofit2.Callback
            public void onFailure(Call<CurrencyResponse> call, Throwable th) {
            }

            @Override // retrofit2.Callback
            public void onResponse(Call<CurrencyResponse> call, Response<CurrencyResponse> response) {
                Log.w(EntryBondsActivity.this.TAG, "onResponse: " + response);
                if (response.isSuccessful()) {
                    ArrayList arrayList = new ArrayList();
                    EntryBondsActivity.this.curr_List = (ArrayList) response.body().getData();
                    if (EntryBondsActivity.this.curr_List != null) {
                        Iterator it = EntryBondsActivity.this.curr_List.iterator();
                        while (it.hasNext()) {
                            arrayList.add(((Currency) it.next()).getName());
                        }
                    }
                    ArrayAdapter arrayAdapter = new ArrayAdapter(EntryBondsActivity.this, R.layout.simple_spinner_item, arrayList);
                    arrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                    EntryBondsActivity.this.spCurrency.setAdapter((SpinnerAdapter) arrayAdapter);
                }
            }
        });
    }

    public Currency getItem(int i, ArrayList<Currency> arrayList) {
        if (arrayList == null) {
            return null;
        }
        Iterator<Currency> it = arrayList.iterator();
        while (it.hasNext()) {
            Currency next = it.next();
            if (next.getNum() == i) {
                return next;
            }
        }
        return null;
    }

    public ArrayList<Accounts> getListItems(int i, List<Accounts> list) {
        ArrayList<Accounts> arrayList = new ArrayList<>();
        ArrayList<Accounts> arrayList2 = new ArrayList<>();
        ArrayList<Accounts> arrayList3 = new ArrayList<>();
        if (list == null) {
            return null;
        }
        for (Accounts accounts : list) {
            if (accounts.getType() == 2) {
                arrayList.add(accounts);
            } else if (accounts.getType() == 3) {
                arrayList2.add(accounts);
            } else {
                arrayList3.add(accounts);
            }
        }
        this.accountMap.put(2, arrayList);
        this.accountMap.put(3, arrayList2);
        this.accountMap.put(0, arrayList3);
        return this.accountMap.get(Integer.valueOf(i));
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 300 && i2 == -1 && intent.getParcelableExtra(LookupAccountsActivity.EXTRA_SELECTED_VALUE) != null) {
            this._account = (Accounts) intent.getParcelableExtra(LookupAccountsActivity.EXTRA_SELECTED_VALUE);
            if (this._account == null) {
                throw new AssertionError();
            }
            this.searchAccount.setText(this._account.getname());
            this.edtNoConsomer.setText(String.valueOf(this._account.getnum()));
            this.tvbal.setText(Utils.numberToString(this._account.getBalance()));
            this.tvtblh.setText(String.valueOf(this._account.getnamet()));
            this.tvmstlm.setText(String.valueOf(this._account.getnamep()));
            this.edtAmount.requestFocus();
            getWindow().setSoftInputMode(4);
            ((TextInputLayout) findViewById(C1018R.id.txtInputedtAmount)).setHint(Utils.numberToString(this._account.getBalance()));
        }
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void onBackPressed() {
        super.onBackPressed();
        NavUtils.navigateUpFromSameTask(this);
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(C1018R.layout.entry_bond_activity);
        this.appConfig = AppConfig.getInstance();
        this._baseUrl = this.appConfig.getBaseUrl();
        this._appId = this.appConfig.getAppId();
        this._user = this.appConfig.getUser();
        Intent intent = getIntent();
        this._mode = intent.getStringExtra("mode");
        if (this._user != null) {
            getSupportActionBar().setTitle(((Object) " المستخدم/") + this._user.getname());
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initializeView();
        this._token = this.tokenManager.getToken().getAccessToken();
        this._isUpdate = this._mode.equals("update");
        if (!this._isUpdate) {
            GetItemBondNext();
            return;
        }
        this.itembond = (ItemBonds) intent.getParcelableExtra("data");
        this._position = intent.getIntExtra("position", 0);
        ItemBonds itemBonds = this.itembond;
        if (itemBonds != null) {
            this._currency = this.itembond.getCurrency();
            this.currencyId = this.itembond.getCurrencyId();
            if (this.currencyId > 0) {
                this.spCurrency.setSelection(getItemPosition(this.currencyId, this.curr_List));
            }
            Accounts account = this.itembond.getAccount();
            this._account = account;
            if (account != null) {
                this.acctList = this.accountMap.get(Integer.valueOf(account.getType()));
                this.searchAccount.setText(account.getname());
                this.edtNoConsomer.setText(String.valueOf(account.getnum()));
                this.tvmstlm.setText(String.valueOf(account.getNomstlm()));
                this.tvtblh.setText(String.valueOf(account.getNotblh()));
                this.tvbal.setText(String.valueOf(account.getBalance()));
            } else {
                this.searchAccount.setText(this.itembond.getname());
                this.edtNoConsomer.setText(String.valueOf(this.itembond.getnum()));
            }
            this.edtExchange.setText(Utils.numberToString(this.itembond.getPriceTrans()));
            this.edtEqual.setText(Utils.numberToString(this.itembond.getDain()));
            this.edtAmount.setText(Utils.numberToString(this.itembond.getDain()));
            this.edtDocNo.setText(this.itembond.getNmstnd());
            this.edtDate.setText(Utils.getEnglishDate(this.itembond.getDate(), "yyyy-MM-dd"));
            this.edtNotes.setText(this.itembond.getBin());
            SetEnabled(itemBonds.getCas() == 0);
        }
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(C1018R.menu.o_bonds_save, menu);
        return true;
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 16908332) {
            finish();
            return true;
        }
        if (itemId != C1018R.id.mnuItemSave) {
            return super.onOptionsItemSelected(menuItem);
        }
        CheckAmount();
        return true;
    }

    @Override // android.app.Activity
    protected void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        this._user = (Users) bundle.getParcelable("user_info");
        if (this._user != null) {
            getSupportActionBar().setTitle(((Object) " المستخدم/") + this._user.getname());
        }
        this._account = (Accounts) bundle.getParcelable("acct_info");
        if (this._account != null) {
            this.tvmstlm.setText(String.valueOf(this._account.getNomstlm()));
            this.tvtblh.setText(String.valueOf(this._account.getNotblh()));
            this.tvbal.setText(String.valueOf(this._account.getBalance()));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putParcelable("user_info", this.appConfig.getUser());
        bundle.putParcelable("acct_info", this._account);
    }

    public void reset_input_fields() {
        this.edtAmount.setText((CharSequence) null);
        this.edtEqual.setText((CharSequence) null);
        Calendar calendar = Calendar.getInstance();
        this.edtDate.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(calendar.getTime()));
        this.mYear = calendar.get(1);
        this.mMonth = calendar.get(2);
        this.mDay = calendar.get(5);
    }

    public void save() {
        if (!this._isUpdate) {
            this.itembond = new ItemBonds();
        }
        this.itembond.setname(((Editable) Objects.requireNonNull(this.searchAccount.getText())).toString());
        Accounts accounts = this._account;
        if (accounts != null) {
            this.itembond.setnum(accounts.getnum());
            this.itembond.setname(accounts.getname());
            this.itembond.setAccount(this._account);
            this.itembond.setType(this._account.getType());
        }
        Currency currency = this._currency;
        if (currency != null) {
            this.itembond.setCurrencyId(currency.getNum());
            this.itembond.setCurrencyName(currency.getName());
            this.itembond.setCurrency(this._currency);
        }
        this.itembond.setCurrencyId(1);
        this.itembond.setDain(Utils.stringToNumber(this.edtAmount.getText().toString()));
        this.itembond.setRsed(this.itembond.getDain());
        this.itembond.setEqual(this.itembond.getDain());
        this.itembond.setBin(this.edtNotes.getText().toString());
        this.itembond.setPriceTrans(1.0d);
        this.itembond.setDate(Utils.getShortDateStrApi(this, Utils.getDateFromString(this.edtDate.getText().toString())));
        this.itembond.setNmstnd(this.edtDocNo.getText().toString());
        if (!this._isUpdate) {
            this.itembond.setname_s(this._user.getname());
            this.itembond.setnum_s(this._user.getNOA());
            this.itembond.setNotes(" لكم مسلم بيد " + this._user.getname() + " عبر الموبايل  " + this.itembond.getBin());
            this.itembond.setNotesBox(" عليكم مسلم من   " + this._account.getname() + " " + this.itembond.getBin());
        }
        this.presenter = new BondsPresenter(this._baseUrl, this._token, this._appId, this.baseViewAddEditHandler);
        try {
            if (this._isUpdate) {
                this.presenter.update(this.itembond);
            } else {
                this.presenter.save(this.itembond);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.progress.setVisibility(0);
    }
}
