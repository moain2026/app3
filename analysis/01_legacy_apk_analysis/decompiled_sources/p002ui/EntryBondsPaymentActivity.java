package com.p001yd.electricecollector.p002ui;

import android.R;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.fragment.app.FragmentActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.p001yd.electricecollector.Adapter.AccounttSearchAdapter;
import com.p001yd.electricecollector.C1018R;
import com.p001yd.electricecollector.LoginActivity;
import com.p001yd.electricecollector.TokenManager;
import com.p001yd.electricecollector.Utils;
import com.p001yd.electricecollector.Validation;
import com.p001yd.electricecollector.common.AppConfig;
import com.p001yd.electricecollector.entities.AccountBalanceInfo;
import com.p001yd.electricecollector.entities.AccountBalanceResponse;
import com.p001yd.electricecollector.entities.Accounts;
import com.p001yd.electricecollector.entities.Currency;
import com.p001yd.electricecollector.entities.CurrencyResponse;
import com.p001yd.electricecollector.entities.ItemBonds;
import com.p001yd.electricecollector.entities.Users;
import com.p001yd.electricecollector.network.ApiService;
import com.p001yd.electricecollector.network.RetrofitBuilder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/* loaded from: classes12.dex */
public class EntryBondsPaymentActivity extends AppCompatActivity {
    Accounts _account;
    private String _appId;
    private String _baseUrl;
    Currency _currency;
    private String _mode;
    private int _position;
    private String _token;
    Users _user;
    private int _versionNumber;
    List<Accounts> aList;
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
    TextInputEditText edtNotes;
    ItemBonds itembond;
    private int mDay;
    private int mMonth;
    private int mYear;
    BondsPaymentPresenter presenter;
    LinearLayout progress;
    ApiService service;
    Spinner spCurrency;
    Spinner spnBoxes;
    private String tital;
    TokenManager tokenManager;
    TextView tvinfo1;
    TextView tvinfo2;
    AlertDialog.Builder window;
    private final String TAG = getClass().getSimpleName();
    final String[] options = {"عميل", "مورد"};
    int currencyId = 0;
    AutoCompleteTextView searchAccount = null;
    final Calendar myCalendar = Calendar.getInstance();
    Map<Integer, ArrayList<Accounts>> accountMap = new HashMap();
    private boolean _isUpdate = false;
    private int _typeCustomer = 0;
    BaseView<ItemBonds> baseViewAddEditHandler = new BaseView<ItemBonds>() { // from class: com.yd.electricecollector.ui.EntryBondsPaymentActivity.1
        @Override // com.p001yd.electricecollector.p002ui.BaseView
        public void onFailed(ItemBonds itemBonds) {
            EntryBondsPaymentActivity.this.progress.setVisibility(8);
            if (EntryBondsPaymentActivity.this._isUpdate) {
                Toast.makeText(EntryBondsPaymentActivity.this, "فشل عملية التعديل", 0).show();
            } else {
                Toast.makeText(EntryBondsPaymentActivity.this, "فشل عملية الاضافة", 0).show();
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
            EntryBondsPaymentActivity.this.progress.setVisibility(8);
            Intent intent = EntryBondsPaymentActivity.this.getIntent();
            intent.putExtra("data", itemBonds);
            intent.putExtra("mode", EntryBondsPaymentActivity.this._mode);
            if (EntryBondsPaymentActivity.this._isUpdate) {
                intent.putExtra("position", EntryBondsPaymentActivity.this._position);
                EntryBondsPaymentActivity.this.setResult(201, intent);
            } else {
                EntryBondsPaymentActivity.this.setResult(101, intent);
            }
            EntryBondsPaymentActivity.this.finish();
        }
    };
    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() { // from class: com.yd.electricecollector.ui.EntryBondsPaymentActivity.2
        @Override // android.app.DatePickerDialog.OnDateSetListener
        public void onDateSet(DatePicker datePicker, int i, int i2, int i3) {
            EntryBondsPaymentActivity.this.mYear = i;
            EntryBondsPaymentActivity.this.mMonth = i2;
            EntryBondsPaymentActivity.this.mDay = i3;
            EntryBondsPaymentActivity.this.myCalendar.set(1, i);
            EntryBondsPaymentActivity.this.myCalendar.set(2, i2);
            EntryBondsPaymentActivity.this.myCalendar.set(5, i3);
            EntryBondsPaymentActivity.this.edtDate.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(EntryBondsPaymentActivity.this.myCalendar.getTime()));
        }
    };

    private void GetAccountBalance() {
        if (this._account != null) {
            HashMap hashMap = new HashMap();
            hashMap.put("accountid", "" + this._account.getnum());
            hashMap.put("appid", this.appConfig.getAppId());
            this.service = (ApiService) RetrofitBuilder.createServiceWithAuth(ApiService.class, this.tokenManager, this._baseUrl);
            this.service.GetAccountBalanceInfo(hashMap).enqueue(new Callback<AccountBalanceResponse>() { // from class: com.yd.electricecollector.ui.EntryBondsPaymentActivity.14
                @Override // retrofit2.Callback
                public void onFailure(Call<AccountBalanceResponse> call, Throwable th) {
                }

                @Override // retrofit2.Callback
                public void onResponse(Call<AccountBalanceResponse> call, Response<AccountBalanceResponse> response) {
                    Log.w(EntryBondsPaymentActivity.this.TAG, "onResponse: " + response);
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
                        Utils.msgBox(str, EntryBondsPaymentActivity.this, new Object[0]);
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
            this.service.GetAccountBalance(hashMap).enqueue(new Callback<Object>() { // from class: com.yd.electricecollector.ui.EntryBondsPaymentActivity.13
                @Override // retrofit2.Callback
                public void onFailure(Call<Object> call, Throwable th) {
                }

                @Override // retrofit2.Callback
                public void onResponse(Call<Object> call, Response<Object> response) {
                    Log.w(EntryBondsPaymentActivity.this.TAG, "onResponse: " + response);
                    if (response.isSuccessful()) {
                        Object body = response.body();
                        String str = "0";
                        if (body != null) {
                            try {
                                str = String.valueOf(new JSONObject(body.toString()).getDouble("GetAccountBalanceResult"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Utils.msgBox(str, EntryBondsPaymentActivity.this, new Object[0]);
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
        this.service.GetBondPaymentRecordNext(hashMap).enqueue(new Callback<Object>() { // from class: com.yd.electricecollector.ui.EntryBondsPaymentActivity.11
            @Override // retrofit2.Callback
            public void onFailure(Call<Object> call, Throwable th) {
            }

            @Override // retrofit2.Callback
            public void onResponse(Call<Object> call, Response<Object> response) {
                Object body;
                if (!response.isSuccessful() || (body = response.body()) == null) {
                    return;
                }
                try {
                    EntryBondsPaymentActivity.this.edtDocNo.setText(String.valueOf(new JSONObject(body.toString()).getInt("GetBondPaymentRecordNextResult")));
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
        boolean z = Validation.hasText(this.edtDocNo);
        if (!Validation.hasText(this.searchAccount)) {
            z = false;
        }
        if (!Validation.hasText(this.edtAmount)) {
            z = false;
        }
        if (!Validation.hasText(this.edtExchange)) {
            z = false;
        }
        if (!Validation.hasText(this.edtEqual)) {
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
        this.searchAccount = (AutoCompleteTextView) findViewById(C1018R.id.autoCompleteTextView);
        this.edtDocNo = (TextInputEditText) findViewById(C1018R.id.edtDocNo);
        this.btnBal = (ImageView) findViewById(C1018R.id.edtAcctNo);
        this.edtDate = (TextView) findViewById(C1018R.id.edtDate);
        this.edtAmount = (TextInputEditText) findViewById(C1018R.id.edtAmount);
        this.edtEqual = (TextInputEditText) findViewById(C1018R.id.edtEqual);
        this.edtExchange = (TextInputEditText) findViewById(C1018R.id.edtExchange);
        this.edtNotes = (TextInputEditText) findViewById(C1018R.id.edtNotes);
        this.btnSave = (Button) findViewById(C1018R.id.btnSave);
        this.btnClose = (Button) findViewById(C1018R.id.btnClose);
        this.btnBal.setVisibility(0);
        this.btnClose.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.ui.EntryBondsPaymentActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                EntryBondsPaymentActivity.this.finish();
            }
        });
        this.btnSave.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.ui.EntryBondsPaymentActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                EntryBondsPaymentActivity.this.save();
            }
        });
        this.tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", 0));
        if (this.tokenManager.getToken() == null) {
            startActivity(new Intent(this, (Class<?>) LoginActivity.class));
            finish();
        }
        this.searchAccount.setOnTouchListener(new View.OnTouchListener() { // from class: com.yd.electricecollector.ui.EntryBondsPaymentActivity.5
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (1 != motionEvent.getAction()) {
                    return false;
                }
                Intent intent = new Intent(EntryBondsPaymentActivity.this, (Class<?>) LookupAccountsActivity.class);
                intent.putExtra(LookupAccountsActivity.EXTRA_PROVINSI_ID_VALUE, 1);
                if (EntryBondsPaymentActivity.this._account != null) {
                    intent.putExtra(LookupAccountsActivity.EXTRA_SELECTED_VALUE, EntryBondsPaymentActivity.this._account);
                }
                EntryBondsPaymentActivity.this.startActivityForResult(intent, 300);
                return false;
            }
        });
        this.btnBal.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.ui.EntryBondsPaymentActivity.6
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (EntryBondsPaymentActivity.this._account != null) {
                    String str = EntryBondsPaymentActivity.this._account.getBalance() > 0.0d ? "مدين/عليه" : "";
                    if (EntryBondsPaymentActivity.this._account.getBalance() < 0.0d) {
                        str = "دائن/لــه";
                    }
                    Utils.msgBox(" الرصيد الحالي:  " + Utils.numberToString(EntryBondsPaymentActivity.this._account.getBalance()) + " " + str, EntryBondsPaymentActivity.this, new Object[0]);
                }
            }
        });
        loadCurrency();
        this.spCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.yd.electricecollector.ui.EntryBondsPaymentActivity.7
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                EntryBondsPaymentActivity.this.currencyId = ((Currency) EntryBondsPaymentActivity.this.curr_List.get(i)).getNum();
                EntryBondsPaymentActivity.this._currency = (Currency) EntryBondsPaymentActivity.this.curr_List.get(i);
                EntryBondsPaymentActivity.this.edtExchange.setText(Utils.numberToString(EntryBondsPaymentActivity.this._currency.getSars()));
                if (EntryBondsPaymentActivity.this.edtAmount.getText().toString().length() != 0) {
                    EntryBondsPaymentActivity.this.edtEqual.setText(Utils.numberToString(Utils.stringToNumber(EntryBondsPaymentActivity.this.edtAmount.getText().toString()) * EntryBondsPaymentActivity.this._currency.getSars()));
                }
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        this.edtExchange.addTextChangedListener(new TextWatcher() { // from class: com.yd.electricecollector.ui.EntryBondsPaymentActivity.8
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
                if (EntryBondsPaymentActivity.this.edtAmount.getText().toString().length() != 0 && EntryBondsPaymentActivity.this.edtExchange.getText().toString().length() != 0) {
                    d = Utils.stringToNumber(EntryBondsPaymentActivity.this.edtAmount.getText().toString());
                    d2 = Utils.stringToNumber(EntryBondsPaymentActivity.this.edtExchange.getText().toString());
                }
                EntryBondsPaymentActivity.this.edtEqual.setText(Utils.numberToString(d * d2));
            }
        });
        this.edtAmount.addTextChangedListener(new TextWatcher() { // from class: com.yd.electricecollector.ui.EntryBondsPaymentActivity.9
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
                if (EntryBondsPaymentActivity.this.edtAmount.getText().toString().length() != 0 && EntryBondsPaymentActivity.this.edtExchange.getText().toString().length() != 0) {
                    d = Utils.stringToNumber(EntryBondsPaymentActivity.this.edtAmount.getText().toString());
                    d2 = Utils.stringToNumber(EntryBondsPaymentActivity.this.edtExchange.getText().toString());
                }
                EntryBondsPaymentActivity.this.edtEqual.setText(Utils.numberToString(d * d2));
            }
        });
        this.edtDate.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.ui.EntryBondsPaymentActivity.10
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                new DatePickerDialog(EntryBondsPaymentActivity.this, new DatePickerDialog.OnDateSetListener() { // from class: com.yd.electricecollector.ui.EntryBondsPaymentActivity.10.1
                    @Override // android.app.DatePickerDialog.OnDateSetListener
                    public void onDateSet(DatePicker datePicker, int i, int i2, int i3) {
                        Calendar calendar2 = Calendar.getInstance();
                        calendar2.set(i, i2, i3);
                        EntryBondsPaymentActivity.this.edtDate.setText(Utils.getShortDateStr((FragmentActivity) EntryBondsPaymentActivity.this, calendar2.getTime()));
                    }
                }, calendar.get(1), calendar.get(2), calendar.get(5)).show();
            }
        });
        this.edtDate.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Calendar.getInstance().getTime()));
    }

    private void loadCurrency() {
        this.service = (ApiService) RetrofitBuilder.createServiceWithAuth(ApiService.class, this.tokenManager, this._baseUrl);
        this.service.GetListCurrency().enqueue(new Callback<CurrencyResponse>() { // from class: com.yd.electricecollector.ui.EntryBondsPaymentActivity.12
            @Override // retrofit2.Callback
            public void onFailure(Call<CurrencyResponse> call, Throwable th) {
            }

            @Override // retrofit2.Callback
            public void onResponse(Call<CurrencyResponse> call, Response<CurrencyResponse> response) {
                Log.w(EntryBondsPaymentActivity.this.TAG, "onResponse: " + response);
                if (response.isSuccessful()) {
                    ArrayList arrayList = new ArrayList();
                    EntryBondsPaymentActivity.this.curr_List = (ArrayList) response.body().getData();
                    if (EntryBondsPaymentActivity.this.curr_List != null) {
                        Iterator it = EntryBondsPaymentActivity.this.curr_List.iterator();
                        while (it.hasNext()) {
                            arrayList.add(((Currency) it.next()).getName());
                        }
                    }
                    ArrayAdapter arrayAdapter = new ArrayAdapter(EntryBondsPaymentActivity.this, R.layout.simple_spinner_item, arrayList);
                    arrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                    EntryBondsPaymentActivity.this.spCurrency.setAdapter((SpinnerAdapter) arrayAdapter);
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
        if (i == 300 && i2 == -1 && intent.getParcelableExtra(LookupAccountsActivity.EXTRA_SELECTED_VALUE) != null) {
            this._account = (Accounts) intent.getParcelableExtra(LookupAccountsActivity.EXTRA_SELECTED_VALUE);
            this.searchAccount.setText(this._account.getname());
            ((TextInputLayout) findViewById(C1018R.id.txtInputedtAmount)).setHint(Utils.numberToString(this._account.getBalance()));
        }
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void onBackPressed() {
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
                this.searchAccount.setText(account.getname());
            } else {
                this.searchAccount.setText(this.itembond.getname());
            }
            this.edtExchange.setText(Utils.numberToString(this.itembond.getPriceTrans()));
            this.edtEqual.setText(Utils.numberToString(this.itembond.getMden()));
            this.edtAmount.setText(Utils.numberToString(this.itembond.getMden()));
            this.edtDocNo.setText(this.itembond.getNmstnd());
            this.edtDate.setText(Utils.getEnglishDate(this.itembond.getDate(), "yyyy-MM-dd"));
            this.edtNotes.setText(this.itembond.getBin());
            SetEnabled(itemBonds.getCas() == 0);
        }
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 16908332) {
            return super.onOptionsItemSelected(menuItem);
        }
        finish();
        return true;
    }

    @Override // android.app.Activity
    protected void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        this._user = (Users) bundle.getParcelable("user_info");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        this.appConfig = AppConfig.getInstance();
        this._baseUrl = this.appConfig.getBaseUrl();
        this._token = this.appConfig.getToken();
        this._appId = this.appConfig.getAppId();
        this._user = this.appConfig.getUser();
        super.onResume();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putParcelable("user_info", this.appConfig.getUser());
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
        if (!checkValidation()) {
            Toast.makeText(this, "خطا في ادخال البيانات", 0).show();
            return;
        }
        if (!this._isUpdate) {
            this.itembond = new ItemBonds();
        }
        this.itembond.setname(this.searchAccount.getText().toString());
        Accounts accounts = this._account;
        if (accounts != null) {
            this.itembond.setnum(accounts.getnum());
            this.itembond.setname(accounts.getname());
            this.itembond.setAccount(this._account);
        }
        Currency currency = this._currency;
        if (currency != null) {
            this.itembond.setCurrencyId(currency.getNum());
            this.itembond.setCurrencyName(currency.getName());
            this.itembond.setCurrency(this._currency);
        }
        this.itembond.setBin(this.edtNotes.getText().toString());
        this.itembond.setMden(Utils.stringToNumber(this.edtAmount.getText().toString()));
        this.itembond.setRsed(this.itembond.getMden());
        this.itembond.setEqual(Utils.stringToNumber(this.edtEqual.getText().toString()));
        this.itembond.setPriceTrans(Utils.stringToNumber(this.edtExchange.getText().toString()));
        this.itembond.setDate(Utils.getShortDateStrApi(this, Utils.getDateFromString(this.edtDate.getText().toString())));
        this.itembond.setNmstnd(this.edtDocNo.getText().toString());
        if (!this._isUpdate) {
            this.itembond.setnum_s(this._user.getNOA());
            this.itembond.setname_s(this._user.getname());
            this.itembond.setNotes(" عليكم مسلم من " + this._user.getname() + " " + this.itembond.getBin());
            this.itembond.setNotesBox("  لكم بيد   " + this._account.getname() + " " + this.itembond.getBin());
        }
        this.presenter = new BondsPaymentPresenter(this._baseUrl, this._token, this._appId, this.baseViewAddEditHandler);
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
