package com.p001yd.electricecollector.p002ui;

import android.R;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.xml.xmp.DublinCoreProperties;
import com.p001yd.electricecollector.Adapter.AccounttSearchAdapter;
import com.p001yd.electricecollector.Adapter.BalanceStateAdapter;
import com.p001yd.electricecollector.C1018R;
import com.p001yd.electricecollector.LoginActivity;
import com.p001yd.electricecollector.Pdf_temp;
import com.p001yd.electricecollector.TokenManager;
import com.p001yd.electricecollector.Utils;
import com.p001yd.electricecollector.common.AppConfig;
import com.p001yd.electricecollector.entities.Accounts;
import com.p001yd.electricecollector.entities.BalanceState;
import com.p001yd.electricecollector.entities.Currency;
import com.p001yd.electricecollector.entities.Places;
import com.p001yd.electricecollector.entities.PostResponse;
import com.p001yd.electricecollector.entities.TGroup;
import com.p001yd.electricecollector.network.ApiService;
import com.p001yd.electricecollector.network.OnItemClickCallback;
import com.p001yd.electricecollector.network.RetrofitBuilder;
import com.p001yd.electricecollector.p002ui.tools.LookupGroupsActivity;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/* loaded from: classes12.dex */
public class BalanceStateReportActivity extends AppCompatActivity implements BaseView<BalanceState> {
    List<BalanceState> ListBalance;
    private BalanceStateAdapter _adapter;
    private AlertDialog _dialog;
    List<Accounts> aList;
    AccounttSearchAdapter acAdp;
    private ArrayList<Accounts> acctList;
    AppConfig appConfig;
    ImageView btnTblh;
    Button btnView;
    ImageView btnmstlm;
    Call<PostResponse> call;
    private ArrayList<Currency> curr_List;
    String dirpath;
    EditText edtGroupFilter;
    protected LinearLayout layoutOptions;
    Pdf_temp pdf_temp;
    LinearLayout progress;
    RecyclerView recyclerView;
    LinearLayout relativeLayout;
    ApiService service;
    private TextView spmstlm;
    private TextView sptblh;
    SwipeRefreshLayout swipeRefreshLayout;
    TokenManager tokenManager;
    TextView tvCase;
    TextView tvTitle;
    TextView tvTotal1;
    private final String TAG = getClass().getSimpleName();
    Map<Integer, ArrayList<Accounts>> accountMap = new HashMap();
    private int _datePickerInput = 0;
    private boolean _isSetDate = false;
    private int _pageNumber = 1;
    private int _pageSize = 30;
    private int[] _pagesCount = new int[1];
    Places _places = null;
    TGroup _tgroup = null;

    /* JADX INFO: Access modifiers changed from: private */
    public void AsyncDownloadStart() {
        HashMap hashMap = new HashMap();
        hashMap.put(DublinCoreProperties.DATE, "" + this.appConfig.getUser().getNou());
        if (this._places != null) {
            hashMap.put("currency", "" + this._places.getnum());
        }
        if (this._tgroup != null) {
            hashMap.put("num", this._tgroup.getnum());
        }
        if (this.edtGroupFilter.getText().toString().length() > 0) {
            hashMap.put("type", this.edtGroupFilter.getText().toString());
        }
        if (hashMap.size() > 0) {
            hashMap.put("appid", this.appConfig.getAppId());
            this.progress.setVisibility(0);
            Log.w(this.TAG, "_base_url: " + this.appConfig.getBaseUrl());
            if (this.appConfig.getBaseUrl() != null) {
                this.service = (ApiService) RetrofitBuilder.createServiceWithAuth(ApiService.class, this.tokenManager, this.appConfig.getBaseUrl());
                this.call = this.service.posts(hashMap);
                this.call.enqueue(new Callback<PostResponse>() { // from class: com.yd.electricecollector.ui.BalanceStateReportActivity.9
                    @Override // retrofit2.Callback
                    public void onFailure(Call<PostResponse> call, Throwable th) {
                        Log.w(BalanceStateReportActivity.this.TAG, "onFailure: " + th.getMessage());
                        BalanceStateReportActivity.this.progress.setVisibility(8);
                    }

                    @Override // retrofit2.Callback
                    public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                        Log.w(BalanceStateReportActivity.this.TAG, "onResponse: " + response);
                        BalanceStateReportActivity.this.ListBalance = new ArrayList();
                        if (response.isSuccessful()) {
                            BalanceStateReportActivity.this.ListBalance = response.body().getData();
                            if (BalanceStateReportActivity.this.ListBalance != null) {
                                BalanceStateReportActivity.this._adapter.setItems(response.body().getData());
                                BalanceStateReportActivity.this._adapter.setItemsFilter(BalanceStateReportActivity.this._adapter.getItems());
                                BalanceStateReportActivity.this.getTotals(BalanceStateReportActivity.this.ListBalance);
                            }
                            BalanceStateReportActivity.this.progress.setVisibility(8);
                        } else {
                            response.code();
                        }
                        BalanceStateReportActivity.this.progress.setVisibility(8);
                    }
                });
            }
        }
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

    /* JADX INFO: Access modifiers changed from: private */
    public double getTotals(List<BalanceState> list) {
        double d = 0.0d;
        for (BalanceState balanceState : list) {
            d += balanceState.getMden() - Math.abs(balanceState.getDain());
        }
        this.tvTotal1.setText(Utils.numberToString(d));
        if (d > 0.0d) {
            this.tvTotal1.setTextColor(Color.parseColor("#D81B60"));
        } else if (d < 0.0d) {
            this.tvTotal1.setTextColor(Color.parseColor("#008577"));
        }
        this.tvCase.setText(d > 0.0d ? "مدين" : "دائن");
        return 0.0d - d;
    }

    private void initializeView() {
        this.recyclerView = (RecyclerView) findViewById(C1018R.id.recycleV);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.recyclerView.setHasFixedSize(true);
        this.progress = (LinearLayout) findViewById(C1018R.id.progressDownload);
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
        if (list == null) {
            return null;
        }
        for (Accounts accounts : list) {
            if (accounts.getType() == 2) {
                arrayList.add(accounts);
            } else if (accounts.getType() == 3) {
                arrayList2.add(accounts);
            }
        }
        this.accountMap.put(2, arrayList);
        this.accountMap.put(3, arrayList2);
        return this.accountMap.get(Integer.valueOf(i));
    }

    public void imageToPDF() throws FileNotFoundException {
        try {
            Document document = new Document();
            this.dirpath = Environment.getExternalStorageDirectory().toString();
            PdfWriter.getInstance(document, new FileOutputStream(this.dirpath + "/NewPDF.pdf"));
            document.open();
            Image image = Image.getInstance(Environment.getExternalStorageDirectory() + File.separator + "image.jpg");
            image.scalePercent(((((document.getPageSize().getWidth() - document.leftMargin()) - document.rightMargin()) - 0.0f) / image.getWidth()) * 100.0f);
            image.setAlignment(5);
            document.add(image);
            document.close();
            Toast.makeText(this, "PDF Generated successfully!..", 0).show();
        } catch (Exception e) {
        }
    }

    public void layoutToImage(View view) {
        this.relativeLayout = (LinearLayout) view.findViewById(C1018R.id.linarprint);
        this.relativeLayout.setDrawingCacheEnabled(true);
        this.relativeLayout.buildDrawingCache();
        Bitmap drawingCache = this.relativeLayout.getDrawingCache();
        new Intent("android.intent.action.SEND").setType("image/jpeg");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        drawingCache.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "image.jpg");
        try {
            file.createNewFile();
            new FileOutputStream(file).write(byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 500 && i2 == -1) {
            this._places = (Places) intent.getExtras().get(LookupPleasesActivity.EXTRA_SELECTED_VALUE);
            this.spmstlm.setText(this._places.getname());
        }
        if (i == 600 && i2 == -1) {
            this._tgroup = (TGroup) intent.getExtras().get(LookupGroupsActivity.EXTRA_SELECTED_VALUE);
            this.sptblh.setText(this._tgroup.getname());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(C1018R.layout.list_balance_report_activity);
        setTitle("ارصدة الحسابات  ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.appConfig = AppConfig.getInstance();
        this.appConfig.getBaseUrl();
        this.appConfig.getToken();
        this.appConfig.getAppId();
        initializeView();
        this.layoutOptions = (LinearLayout) findViewById(C1018R.id.layoutOptions);
        this.btnView = (Button) findViewById(C1018R.id.btnView);
        this.btnTblh = (ImageView) findViewById(C1018R.id.btnTblh);
        this.btnmstlm = (ImageView) findViewById(C1018R.id.btnmstlm);
        this.sptblh = (TextView) findViewById(C1018R.id.spTblhFilter);
        this.spmstlm = (TextView) findViewById(C1018R.id.spPlacesFilter);
        this.btnTblh.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.ui.BalanceStateReportActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                BalanceStateReportActivity.this.sptblh.setText("");
                BalanceStateReportActivity.this._tgroup = null;
            }
        });
        this.btnmstlm.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.ui.BalanceStateReportActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                BalanceStateReportActivity.this.spmstlm.setText("");
                BalanceStateReportActivity.this._places = null;
                BalanceStateReportActivity.this.sptblh.setText("");
                BalanceStateReportActivity.this._tgroup = null;
            }
        });
        this.tvTotal1 = (TextView) findViewById(C1018R.id.tvTotal1);
        this.tvTitle = (TextView) findViewById(C1018R.id.tvTitle);
        this.tvCase = (TextView) findViewById(C1018R.id.tvCase);
        this.edtGroupFilter = (EditText) findViewById(C1018R.id.spGroupFilter);
        this.tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", 0));
        if (this.tokenManager.getToken() == null) {
            startActivity(new Intent(this, (Class<?>) LoginActivity.class));
        }
        this.sptblh.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.ui.BalanceStateReportActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Intent intent = new Intent(BalanceStateReportActivity.this, (Class<?>) LookupGroupsActivity.class);
                intent.putExtra(LookupGroupsActivity.EXTRA_SELECTED_VALUE, BalanceStateReportActivity.this._places);
                BalanceStateReportActivity.this.startActivityForResult(intent, 600);
            }
        });
        this.spmstlm.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.ui.BalanceStateReportActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Intent intent = new Intent(BalanceStateReportActivity.this, (Class<?>) LookupPleasesActivity.class);
                intent.putExtra(LookupGroupsActivity.EXTRA_PROVINSI_ID_VALUE, 1);
                BalanceStateReportActivity.this.startActivityForResult(intent, 500);
            }
        });
        AsyncDownloadStart();
        this.btnView.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.ui.BalanceStateReportActivity.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                BalanceStateReportActivity.this.AsyncDownloadStart();
            }
        });
        this._pageSize = this.appConfig.getPageSize();
        this._adapter = new BalanceStateAdapter();
        this.swipeRefreshLayout = (SwipeRefreshLayout) findViewById(C1018R.id.swipeRefresh);
        this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() { // from class: com.yd.electricecollector.ui.BalanceStateReportActivity.6
            @Override // androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
            public void onRefresh() {
                BalanceStateReportActivity.this.layoutOptions.setVisibility(8);
                BalanceStateReportActivity.this.AsyncDownloadStart();
                BalanceStateReportActivity.this._adapter.notifyDataSetChanged();
                BalanceStateReportActivity.this.swipeRefreshLayout.setRefreshing(false);
            }
        });
        this._adapter.setItemClickListener(new OnItemClickCallback<BalanceState>() { // from class: com.yd.electricecollector.ui.BalanceStateReportActivity.7
            @Override // com.p001yd.electricecollector.network.OnItemClickCallback
            public void onItemClicked(BalanceState balanceState) {
                Intent intent = new Intent(BalanceStateReportActivity.this, (Class<?>) BalanceStateDetailsReportActivity.class);
                intent.putExtra("data", balanceState);
                BalanceStateReportActivity.this.startActivity(intent);
            }

            @Override // com.p001yd.electricecollector.network.OnItemClickCallback
            public void onItemClicked(BalanceState balanceState, int i) {
            }
        });
        this.recyclerView.setAdapter(this._adapter);
        getWindow().setSoftInputMode(2);
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(C1018R.menu.o_report, menu);
        MenuItem findItem = menu.findItem(C1018R.id.search);
        SearchManager searchManager = (SearchManager) getSystemService("search");
        SearchView searchView = (SearchView) findItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("بحث");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() { // from class: com.yd.electricecollector.ui.BalanceStateReportActivity.8
            @Override // android.widget.SearchView.OnQueryTextListener
            public boolean onQueryTextChange(String str) {
                BalanceStateReportActivity.this.onQueryTextChange2(str);
                return false;
            }

            @Override // android.widget.SearchView.OnQueryTextListener
            public boolean onQueryTextSubmit(String str) {
                return false;
            }
        });
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        if (this.call != null) {
            this.call.cancel();
            this.call = null;
        }
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseView
    public void onFailed(BalanceState balanceState) {
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseView
    public void onLoadDataFailure() {
        Toast.makeText(this, "فشل تحميل البيانات", 0).show();
        this.progress.setVisibility(8);
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseView
    public void onLoadDataSucceed(List<BalanceState> list) {
        this._adapter.setItems(list);
        this.progress.setVisibility(8);
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.home:
                finish();
                return true;
            case C1018R.id.mnuItemOptionDropDown /* 2131362189 */:
                if (this.layoutOptions.getVisibility() == 0) {
                    this.layoutOptions.setVisibility(8);
                } else {
                    this.layoutOptions.setVisibility(0);
                }
                return true;
            case C1018R.id.mnuItemPrint /* 2131362190 */:
                try {
                    pdfF();
                } catch (DocumentException e) {
                } catch (IOException e2) {
                    e2.printStackTrace();
                } catch (ParseException e3) {
                    e3.printStackTrace();
                }
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    void onQueryTextChange2(String str) {
        this._adapter.getFilter().filter(str);
    }

    @Override // android.app.Activity
    protected void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStart() {
        super.onStart();
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseView
    public void onSucceed(BalanceState balanceState) {
    }

    /* JADX WARN: Type inference failed for: r4v3, types: [com.yd.electricecollector.ui.BalanceStateReportActivity$10] */
    public void pdfF() throws DocumentException, IOException, ParseException {
        final ArrayList arrayList = new ArrayList();
        new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        final StringBuilder sb = new StringBuilder();
        sb.append("الارصدة/ " + getTitle().toString());
        final List<BalanceState> items = this._adapter.getItems();
        new AsyncTask<String, Integer, String>() { // from class: com.yd.electricecollector.ui.BalanceStateReportActivity.10
            ProgressDialog progressD;

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public String doInBackground(String... strArr) {
                BalanceStateReportActivity.this.pdf_temp = new Pdf_temp(BalanceStateReportActivity.this.getApplicationContext(), "ارصدة الحسابات", 2);
                BalanceStateReportActivity.this.pdf_temp.setReportName(sb.toString());
                BalanceStateReportActivity.this.pdf_temp.openDocument();
                BalanceStateReportActivity.this.pdf_temp.setReportName("" + BalanceStateReportActivity.this.getTitle().toString());
                BalanceStateReportActivity.this.pdf_temp.addTtle("تقرير", "تقرير جميع السجلات", "6/1/2017");
                String[] strArr2 = {"الرصيد", "الحالة", "الحساب", "التاريخ"};
                double d = 0.0d;
                double d2 = 0.0d;
                for (BalanceState balanceState : items) {
                    d += balanceState.getDain();
                    d2 += balanceState.getMden();
                    arrayList.add(new String[]{NumberFormat.getInstance(Locale.ENGLISH).format(balanceState.getMden() - Math.abs(balanceState.getDain())), balanceState.getMden() - Math.abs(balanceState.getDain()) > 0.0d ? "مدين" : "دائن", balanceState.getname(), balanceState.getDate()});
                }
                try {
                    BalanceStateReportActivity.this.pdf_temp.createTable5(strArr2, arrayList, "  " + BalanceStateReportActivity.this.getTitle().toString() + sb.toString(), d, d2, d2 - Math.abs(d));
                } catch (DocumentException e) {
                    e.printStackTrace();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
                BalanceStateReportActivity.this.pdf_temp.closDocument();
                return null;
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(String str) {
                super.onPostExecute((AsyncTaskC114210) str);
                BalanceStateReportActivity.this.pdf_temp.viewPdf();
                this.progressD.dismiss();
            }

            @Override // android.os.AsyncTask
            protected void onPreExecute() {
                this.progressD = new ProgressDialog(BalanceStateReportActivity.this);
                this.progressD = new ProgressDialog(BalanceStateReportActivity.this);
                this.progressD.setTitle("يتم إعداد التقرير..");
                this.progressD.setMessage("يرجى الانتظار...");
                this.progressD.setCancelable(false);
                try {
                    this.progressD.show();
                } catch (Exception e) {
                    this.progressD.dismiss();
                }
                super.onPreExecute();
            }
        }.execute(new String[0]);
    }
}
