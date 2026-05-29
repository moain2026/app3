package com.p001yd.electricecollector.p002ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.datecs.api.printer.Printer;
import com.datecs.api.printer.PrinterInformation;
import com.datecs.api.printer.ProtocolAdapter;
import com.ganesh.intermecarabic.Arabic864;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.p001yd.electricecollector.Adapter.ListBondsPaymentAdapter;
import com.p001yd.electricecollector.C1018R;
import com.p001yd.electricecollector.DialogHelper;
import com.p001yd.electricecollector.HakAccessHelper;
import com.p001yd.electricecollector.LoginActivity;
import com.p001yd.electricecollector.SplashScreenActivity;
import com.p001yd.electricecollector.TAPreferences;
import com.p001yd.electricecollector.TokenManager;
import com.p001yd.electricecollector.Utils;
import com.p001yd.electricecollector.ViewPeriod;
import com.p001yd.electricecollector.common.AppConfig;
import com.p001yd.electricecollector.entities.BalanceState;
import com.p001yd.electricecollector.entities.BondsPaymentResponse;
import com.p001yd.electricecollector.entities.HakAccess;
import com.p001yd.electricecollector.entities.ItemBonds;
import com.p001yd.electricecollector.entities.Users;
import com.p001yd.electricecollector.network.ApiService;
import com.p001yd.electricecollector.network.DialogCallback;
import com.p001yd.electricecollector.network.OnContextMenuItemClickCallback;
import com.p001yd.electricecollector.network.OnCreateContextMenuCallback;
import com.p001yd.electricecollector.network.OnItemClickCallback;
import com.p001yd.electricecollector.network.RetrofitBuilder;
import com.p001yd.electricecollector.printer.bluetooth.BluetoothConnector;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import org.json.JSONException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/* loaded from: classes12.dex */
public class ListBondsPaymentActivity extends AppCompatActivity implements BaseView<ItemBonds> {
    BalanceState BalanceStateHeader;
    private ListBondsPaymentAdapter _adapter;
    private String _appId;
    private String _baseUrl;
    private AlertDialog _dialog;
    private String _token;
    AppConfig appConfig;
    Call<BondsPaymentResponse> call;
    private String[] contextMenuArray;
    protected LinearLayout layoutOptions;
    ProgressBar loadingProgressBar;
    private ProgressDialog mProgressDialog;
    BondsPaymentPresenter presenter;
    LinearLayout progress;
    RecyclerView recyclerView;
    ApiService service;
    SwipeRefreshLayout swipeRefreshLayout;
    TokenManager tokenManager;
    TextView tvCase;
    TextView tvEndDate;
    TextView tvStartDate;
    TextView tvTitle;
    TextView tvTotal1;
    TextView tvTotal2;
    TextView tvTotal3;
    public ViewPeriod viewPeriod;
    private final String TAG = getClass().getSimpleName();
    private int _datePickerInput = 0;
    private boolean _isSetDate = false;
    private int _pageNumber = 1;
    private int _pageSize = 30;
    private int[] _pagesCount = new int[1];
    int posationDelete = -1;
    ItemBonds itemPrint = null;
    Users user = null;
    private final Handler mHandler = new Handler();
    DialogCallback dialogCallback = new DialogCallback() { // from class: com.yd.electricecollector.ui.ListBondsPaymentActivity.9
        @Override // com.p001yd.electricecollector.network.DialogCallback
        public void onCancel() {
        }

        /* JADX WARN: Type inference failed for: r0v2, types: [com.yd.electricecollector.ui.ListBondsPaymentActivity$9$1] */
        @Override // com.p001yd.electricecollector.network.DialogCallback
        public void onOk(Object obj) {
            if (ListBondsPaymentActivity.this.itemPrint != null) {
                new Thread() { // from class: com.yd.electricecollector.ui.ListBondsPaymentActivity.9.1
                    @Override // java.lang.Thread, java.lang.Runnable
                    public void run() {
                        try {
                            if (SplashScreenActivity.mPrinter == null) {
                                String setectedPrinterConnectionString = TAPreferences.getSetectedPrinterConnectionString(ListBondsPaymentActivity.this);
                                if (!setectedPrinterConnectionString.equalsIgnoreCase("")) {
                                    ListBondsPaymentActivity.this.showProgress(C1018R.string.connecting);
                                    if (!setectedPrinterConnectionString.startsWith("bth://")) {
                                        throw new IllegalArgumentException("Unsupported connection string");
                                    }
                                    ListBondsPaymentActivity.this.connectBth(setectedPrinterConnectionString.substring(6));
                                    ListBondsPaymentActivity.this.dismissProgress();
                                }
                            }
                            ListBondsPaymentActivity.this.showProgress(C1018R.string.printing_image);
                            ListBondsPaymentActivity.this.doPrint(ListBondsPaymentActivity.this.itemPrint);
                            ListBondsPaymentActivity.this.dismissProgress();
                        } catch (Exception e) {
                            ListBondsPaymentActivity.this.error(C1018R.drawable.bluetooth, e.getMessage());
                        }
                    }
                }.start();
            }
        }

        @Override // com.p001yd.electricecollector.network.DialogCallback
        public void onOk(Object obj, Object obj2) {
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    public void AsyncDownloadStart() {
        this.progress.setVisibility(0);
        HashMap hashMap = new HashMap();
        hashMap.put("appid", this.appConfig.getAppId());
        hashMap.put("sdate", Utils.getShortDateStrApi(this, this.viewPeriod.startDate));
        hashMap.put("edate", Utils.getShortDateStrApi(this, this.viewPeriod.endDate));
        hashMap.put("num_s", "" + this.appConfig.getUser().getNOA());
        Log.w(this.TAG, "_base_url: " + this.appConfig.getBaseUrl());
        this.service = (ApiService) RetrofitBuilder.createServiceWithAuth(ApiService.class, this.tokenManager, this.appConfig.getBaseUrl());
        this.call = this.service.GetListBondsPayment(hashMap);
        this.call.enqueue(new Callback<BondsPaymentResponse>() { // from class: com.yd.electricecollector.ui.ListBondsPaymentActivity.8
            @Override // retrofit2.Callback
            public void onFailure(Call<BondsPaymentResponse> call, Throwable th) {
                Log.w(ListBondsPaymentActivity.this.TAG, "onFailure: " + th.getMessage());
                ListBondsPaymentActivity.this.progress.setVisibility(8);
            }

            @Override // retrofit2.Callback
            public void onResponse(Call<BondsPaymentResponse> call, Response<BondsPaymentResponse> response) {
                Log.w(ListBondsPaymentActivity.this.TAG, "onResponse: " + response);
                if (response.isSuccessful()) {
                    if (response.body().getData() != null) {
                        ListBondsPaymentActivity.this._adapter.setItems(response.body().getData());
                    }
                    ListBondsPaymentActivity.this.progress.setVisibility(8);
                } else {
                    ListBondsPaymentActivity.this.tokenManager.deleteToken();
                    ListBondsPaymentActivity.this.startActivity(new Intent(ListBondsPaymentActivity.this, (Class<?>) LoginActivity.class));
                    ListBondsPaymentActivity.this.finish();
                }
                ListBondsPaymentActivity.this.progress.setVisibility(8);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dismissProgress() {
        this.mHandler.post(new Runnable() { // from class: com.yd.electricecollector.ui.ListBondsPaymentActivity.13
            @Override // java.lang.Runnable
            public void run() {
                ListBondsPaymentActivity.this.mProgressDialog.dismiss();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doPrint(ItemBonds itemBonds) {
        Arabic864 arabic864 = new Arabic864();
        try {
            try {
                try {
                    try {
                        SplashScreenActivity.mPrinter.reset();
                        SplashScreenActivity.mPrinter.printLogo();
                        SplashScreenActivity.mPrinter.printTaggedText("{reset}{center}{w}{h}");
                        SplashScreenActivity.mPrinter.printText(arabic864.Convert(TAPreferences.getCompanyName(this), false));
                        SplashScreenActivity.mPrinter.printTaggedText("{br}");
                        SplashScreenActivity.mPrinter.printTaggedText("{reset}{center}");
                        SplashScreenActivity.mPrinter.printText(arabic864.Convert(TAPreferences.getCompanyAddress(this), false));
                        SplashScreenActivity.mPrinter.printTaggedText("{br}");
                        SplashScreenActivity.mPrinter.printTaggedText("{reset}{center}");
                        SplashScreenActivity.mPrinter.printText(arabic864.Convert(TAPreferences.getCompanyPhone(this), false));
                        SplashScreenActivity.mPrinter.printTaggedText("{br}");
                        SplashScreenActivity.mPrinter.printTaggedText("{reset}{center}{w}{h}{b}");
                        SplashScreenActivity.mPrinter.printText(arabic864.Convert("سند صرف ", false));
                        SplashScreenActivity.mPrinter.printTaggedText("{br}");
                        SplashScreenActivity.mPrinter.printTaggedText("{br}*********************   ********************{br}");
                        SplashScreenActivity.mPrinter.printTaggedText("{br}");
                        SplashScreenActivity.mPrinter.printTaggedText("{reset}{right}{h}{b}      ");
                        SplashScreenActivity.mPrinter.printText(itemBonds.getNmstnd());
                        SplashScreenActivity.mPrinter.setLineSpace(20);
                        SplashScreenActivity.mPrinter.printTaggedText("{reset}{right}{h}");
                        SplashScreenActivity.mPrinter.printText(arabic864.Convert(" رقم السند   : ", false));
                        SplashScreenActivity.mPrinter.printTaggedText("{br}");
                        SplashScreenActivity.mPrinter.printTaggedText("{br}------------------------------------------------{br}\n");
                        SplashScreenActivity.mPrinter.printTaggedText("{reset}{right}{h}");
                        SplashScreenActivity.mPrinter.printText(itemBonds.getDate());
                        SplashScreenActivity.mPrinter.printTaggedText("{reset}{right}{h}");
                        SplashScreenActivity.mPrinter.printText(arabic864.Convert(" تاريخ السند : ", false));
                        SplashScreenActivity.mPrinter.printTaggedText("{br}");
                        SplashScreenActivity.mPrinter.printTaggedText("{br}------------------------------------------------{br}\n");
                        SplashScreenActivity.mPrinter.printTaggedText("{reset}{right}{h}");
                        SplashScreenActivity.mPrinter.printText(arabic864.Convert(itemBonds.getname(), false));
                        SplashScreenActivity.mPrinter.printTaggedText("{reset}{right}{h}");
                        SplashScreenActivity.mPrinter.printText(arabic864.Convert(" اسم الحساب : ", false));
                        SplashScreenActivity.mPrinter.printTaggedText("{br}");
                        SplashScreenActivity.mPrinter.printTaggedText("{br}------------------------------------------------{br}\n");
                        SplashScreenActivity.mPrinter.printTaggedText("{reset}{right}{h}         ");
                        SplashScreenActivity.mPrinter.printText(Utils.numberToString(itemBonds.getMden()));
                        SplashScreenActivity.mPrinter.printTaggedText("{reset}{right}{h}");
                        SplashScreenActivity.mPrinter.printText(arabic864.Convert(" المبلغ : ", false));
                        SplashScreenActivity.mPrinter.printTaggedText("{br}");
                        SplashScreenActivity.mPrinter.printTaggedText("{br}------------------------------------------------{br}\n");
                        SplashScreenActivity.mPrinter.printTaggedText("{reset}{right}{h}");
                        SplashScreenActivity.mPrinter.printText(arabic864.Convert(itemBonds.getname_s(), false));
                        SplashScreenActivity.mPrinter.printTaggedText("{reset}{right}{h}");
                        SplashScreenActivity.mPrinter.printText(arabic864.Convert(" الصندوق/البنك : ", false));
                        SplashScreenActivity.mPrinter.printTaggedText("{br}");
                        if (itemBonds.getBin() != null) {
                            SplashScreenActivity.mPrinter.printTaggedText("{br}------------------------------------------------{br}\n");
                            SplashScreenActivity.mPrinter.printTaggedText("{reset}{right}{h} ");
                            SplashScreenActivity.mPrinter.printText(arabic864.Convert(itemBonds.getBin(), false));
                            SplashScreenActivity.mPrinter.printTaggedText("{reset}{right}{h}");
                            SplashScreenActivity.mPrinter.printText(arabic864.Convert(" البيان : ", false));
                            SplashScreenActivity.mPrinter.printTaggedText("{br}");
                        }
                        SplashScreenActivity.mPrinter.printTaggedText("{br}------------------------------------------------{br}\n");
                        SplashScreenActivity.mPrinter.printTaggedText("{reset}{right} ");
                        SplashScreenActivity.mPrinter.printTaggedText(Utils.getCurrentDate());
                        SplashScreenActivity.mPrinter.printTaggedText("{reset}{right} ");
                        SplashScreenActivity.mPrinter.printText(arabic864.Convert(" تاريخ الطباعة: ", false));
                        SplashScreenActivity.mPrinter.printTaggedText("{br}------------------------------------------------{br}\n");
                        SplashScreenActivity.mPrinter.printTaggedText("{br}");
                        SplashScreenActivity.mPrinter.printTaggedText("{br}");
                        SplashScreenActivity.mPrinter.feedPaper(110);
                        SplashScreenActivity.mPrinter.flush();
                    } catch (Exception e) {
                        error(C1018R.drawable.bluetooth, e.getMessage());
                    }
                } catch (IOException e2) {
                    error(C1018R.drawable.text, getString(C1018R.string.failed_print_text) + ". " + e2.getMessage());
                }
            } catch (NullPointerException e3) {
                error(C1018R.drawable.bluetooth, getString(C1018R.string.printer_not_connected));
            }
        } catch (Exception e4) {
            error(C1018R.drawable.bluetooth, e4.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void error(final int i, final String str) {
        this.mHandler.post(new Runnable() { // from class: com.yd.electricecollector.ui.ListBondsPaymentActivity.12
            @Override // java.lang.Runnable
            public void run() {
                AlertDialog create = new AlertDialog.Builder(ListBondsPaymentActivity.this).setTitle("Error").setMessage(str).create();
                create.setIcon(i);
                create.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.yd.electricecollector.ui.ListBondsPaymentActivity.12.1
                    @Override // android.content.DialogInterface.OnDismissListener
                    public void onDismiss(DialogInterface dialogInterface) {
                    }
                });
                create.show();
            }
        });
    }

    private void initializeView() {
        this.recyclerView = (RecyclerView) findViewById(C1018R.id.recycleV);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.recyclerView.setHasFixedSize(true);
        this.loadingProgressBar = (ProgressBar) findViewById(C1018R.id.loading);
        this.progress = (LinearLayout) findViewById(C1018R.id.progressDownload);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void openViewPeriod() {
        Intent intent = new Intent(getApplicationContext(), (Class<?>) ViewPeriodActivity.class);
        ViewPeriod viewPeriod = this.viewPeriod;
        Bundle bundle = new Bundle();
        bundle.putSerializable("VIEW_PERIOD", viewPeriod);
        intent.putExtras(bundle);
        startActivityForResult(intent, 5);
    }

    private void setPrinterInfo(int i, String str) {
        this.mHandler.post(new Runnable() { // from class: com.yd.electricecollector.ui.ListBondsPaymentActivity.10
            @Override // java.lang.Runnable
            public void run() {
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showProgress(int i) {
        showProgress(getString(i));
    }

    private void showProgress(final String str) {
        this.mHandler.post(new Runnable() { // from class: com.yd.electricecollector.ui.ListBondsPaymentActivity.11
            @Override // java.lang.Runnable
            public void run() {
                ListBondsPaymentActivity.this.mProgressDialog = ProgressDialog.show(ListBondsPaymentActivity.this, ListBondsPaymentActivity.this.getString(C1018R.string.please_wait), str, true);
            }
        });
    }

    void connectBth(String str) {
        setPrinterInfo(C1018R.drawable.help, str);
        try {
            SplashScreenActivity.mBthConnector = BluetoothConnector.getConnector(this);
            SplashScreenActivity.mBthConnector.connect(str);
            SplashScreenActivity.mPrinter = getPrinter(SplashScreenActivity.mBthConnector.getInputStream(), SplashScreenActivity.mBthConnector.getOutputStream());
            SplashScreenActivity.mPrinterInfo = getPrinterInfo();
        } catch (IOException e) {
            error(C1018R.drawable.bluetooth, e.getMessage());
        }
    }

    Printer getPrinter(InputStream inputStream, OutputStream outputStream) throws IOException {
        ProtocolAdapter protocolAdapter = new ProtocolAdapter(inputStream, outputStream);
        if (!protocolAdapter.isProtocolEnabled()) {
            return new Printer(inputStream, outputStream);
        }
        ProtocolAdapter.Channel channel = protocolAdapter.getChannel(1);
        return new Printer(channel.getInputStream(), channel.getOutputStream());
    }

    PrinterInformation getPrinterInfo() {
        PrinterInformation printerInformation = null;
        try {
            printerInformation = SplashScreenActivity.mPrinter.getInformation();
            setPrinterInfo(C1018R.drawable.printer, printerInformation.getName());
            return printerInformation;
        } catch (IOException e) {
            e.printStackTrace();
            return printerInformation;
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i == 5 && i2 == -1) {
            try {
                ViewPeriod viewPeriod = (ViewPeriod) intent.getExtras().get("VIEW_PERIOD");
                if (viewPeriod != null) {
                    this.viewPeriod = viewPeriod;
                    AppConfig.getInstance().setViewPeriod(viewPeriod);
                    this.tvStartDate.setText(Utils.getShortDateStr((FragmentActivity) this, this.viewPeriod.startDate));
                    this.tvEndDate.setText(Utils.getShortDateStr((FragmentActivity) this, this.viewPeriod.endDate));
                    AsyncDownloadStart();
                }
            } catch (Exception e) {
            }
        }
        if (i == 100) {
            if (i2 == 101) {
                ItemBonds itemBonds = (ItemBonds) intent.getParcelableExtra("data");
                itemBonds.setDate(Utils.getConvertEnglishDate(itemBonds.getDate(), "yyyy/mm/dd"));
                this._adapter.setItem(itemBonds);
                this.recyclerView.smoothScrollToPosition(this._adapter.getItemCount() - 1);
                Toast.makeText(this, "تم حفظ السند", 0).show();
                DialogHelper.msgDialogConfirm("تم اضافة سند صرف برقم " + itemBonds.getNmstnd() + "\n مبلغ " + itemBonds.getMden() + "\n على حساب/ " + itemBonds.getname() + "\n\nهل تريد طباعة السند؟", this, this.dialogCallback).create().show();
                return;
            }
            return;
        }
        if (i == 200 && i2 == 201) {
            int intExtra = intent.getIntExtra("position", 0);
            ItemBonds itemBonds2 = (ItemBonds) intent.getParcelableExtra("data");
            itemBonds2.setDate(Utils.getConvertEnglishDate(itemBonds2.getDate(), "yyyy/mm/dd"));
            this._adapter.updateItem(itemBonds2, intExtra);
            this.recyclerView.smoothScrollToPosition(intExtra);
            Toast.makeText(this, "تم حفظ السند", 0).show();
            Utils.msgBox("تم تعديل سند صرف برقم " + itemBonds2.getNmstnd() + "\n مبلغ " + itemBonds2.getMden() + "\n على حساب/ " + itemBonds2.getname(), this, new Object[0]);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(C1018R.layout.list_bonds_report_activity);
        setTitle("سندات القبض   ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initializeView();
        ((FloatingActionButton) findViewById(C1018R.id.fab)).setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.ui.ListBondsPaymentActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Intent intent = new Intent(ListBondsPaymentActivity.this, (Class<?>) EntryBondsPaymentActivity.class);
                intent.putExtra("mode", "add");
                intent.putExtra("title", "سند صرف جديد");
                ListBondsPaymentActivity.this.startActivityForResult(intent, 100);
            }
        });
        this.tvStartDate = (TextView) findViewById(C1018R.id.txtStartDate);
        this.tvEndDate = (TextView) findViewById(C1018R.id.txtEndDate);
        this.layoutOptions = (LinearLayout) findViewById(C1018R.id.layoutOptions);
        this.layoutOptions.setVisibility(8);
        this.tvStartDate.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.ui.ListBondsPaymentActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ListBondsPaymentActivity.this.openViewPeriod();
            }
        });
        this.tvEndDate.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.ui.ListBondsPaymentActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ListBondsPaymentActivity.this.openViewPeriod();
            }
        });
        this.viewPeriod = AppConfig.getInstance().getViewPeriod();
        if (this.viewPeriod == null) {
            this.viewPeriod = new ViewPeriod(this);
        }
        this.tvStartDate.setText(Utils.getShortDateStr((FragmentActivity) this, this.viewPeriod.startDate));
        this.tvEndDate.setText(Utils.getShortDateStr((FragmentActivity) this, this.viewPeriod.endDate));
        this.BalanceStateHeader = (BalanceState) getIntent().getParcelableExtra("data");
        setTitle(" سند صرف  ");
        this.appConfig = AppConfig.getInstance();
        this.appConfig.getBaseUrl();
        this.appConfig.getToken();
        this.appConfig.getAppId();
        this.user = this.appConfig.getUser();
        this._pageSize = this.appConfig.getPageSize();
        this._adapter = new ListBondsPaymentAdapter();
        this.recyclerView.setAdapter(this._adapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, 1);
        this.swipeRefreshLayout = (SwipeRefreshLayout) findViewById(C1018R.id.swipeRefresh);
        this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() { // from class: com.yd.electricecollector.ui.ListBondsPaymentActivity.4
            @Override // androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
            public void onRefresh() {
                ListBondsPaymentActivity.this.AsyncDownloadStart();
                ListBondsPaymentActivity.this._adapter.notifyDataSetChanged();
                ListBondsPaymentActivity.this.swipeRefreshLayout.setRefreshing(false);
            }
        });
        this._adapter.setItemClickListener(new OnItemClickCallback<ItemBonds>() { // from class: com.yd.electricecollector.ui.ListBondsPaymentActivity.5
            @Override // com.p001yd.electricecollector.network.OnItemClickCallback
            public void onItemClicked(ItemBonds itemBonds) {
            }

            @Override // com.p001yd.electricecollector.network.OnItemClickCallback
            public void onItemClicked(ItemBonds itemBonds, int i) {
            }
        });
        this._adapter.setOnCreateContextMenu(new OnCreateContextMenuCallback() { // from class: com.yd.electricecollector.ui.ListBondsPaymentActivity.6
            @Override // com.p001yd.electricecollector.network.OnCreateContextMenuCallback
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo, int i, MenuItem.OnMenuItemClickListener onMenuItemClickListener) {
                contextMenu.setHeaderTitle("سند رقم: " + ListBondsPaymentActivity.this._adapter.getItems().get(i).getNmstnd());
                contextMenu.add(0, C1018R.id.menu_print, 0, C1018R.string.menu_print).setOnMenuItemClickListener(onMenuItemClickListener);
                contextMenu.add(0, C1018R.id.menu_edit, 0, C1018R.string.menu_edit).setOnMenuItemClickListener(onMenuItemClickListener);
                contextMenu.add(0, C1018R.id.menu_delete, 0, C1018R.string.menu_delete).setOnMenuItemClickListener(onMenuItemClickListener);
            }
        });
        this._adapter.setOnContextMenuItemClickCallback(new OnContextMenuItemClickCallback() { // from class: com.yd.electricecollector.ui.ListBondsPaymentActivity.7
            /* JADX WARN: Type inference failed for: r3v7, types: [com.yd.electricecollector.ui.ListBondsPaymentActivity$7$1] */
            @Override // com.p001yd.electricecollector.network.OnContextMenuItemClickCallback
            public void onContextMenuItemClick(MenuItem menuItem, final int i) {
                final ItemBonds itemBonds = ListBondsPaymentActivity.this._adapter.getItems().get(i);
                int itemId = menuItem.getItemId();
                HakAccess hakAkses = HakAccessHelper.getHakAkses("repBondsPayment", ListBondsPaymentActivity.this.appConfig.getListHakAccess());
                switch (itemId) {
                    case C1018R.id.menu_delete /* 2131362181 */:
                        if (hakAkses != null) {
                            if (!hakAkses.isDelete()) {
                                Utils.msgBox("ليس لديك صلاحية الحذف", ListBondsPaymentActivity.this, new Object[0]);
                                return;
                            } else if (itemBonds.getCas() == 0) {
                                Utils.msgBox(new DialogInterface.OnClickListener() { // from class: com.yd.electricecollector.ui.ListBondsPaymentActivity.7.2
                                    @Override // android.content.DialogInterface.OnClickListener
                                    public void onClick(DialogInterface dialogInterface, int i2) {
                                        ListBondsPaymentActivity.this.presenter = new BondsPaymentPresenter(ListBondsPaymentActivity.this._baseUrl, ListBondsPaymentActivity.this._token, ListBondsPaymentActivity.this._appId, ListBondsPaymentActivity.this);
                                        try {
                                            ListBondsPaymentActivity.this.progress.setVisibility(0);
                                            ListBondsPaymentActivity.this.posationDelete = i;
                                            ListBondsPaymentActivity.this.presenter.delete(itemBonds);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, "هل تريد حذف السند برقم " + itemBonds.getNmstnd(), ListBondsPaymentActivity.this, new Object[0]);
                                return;
                            } else {
                                Utils.msgBox("لايمكنك حذف السند المرحل", ListBondsPaymentActivity.this, new Object[0]);
                                return;
                            }
                        }
                        return;
                    case C1018R.id.menu_description /* 2131362182 */:
                    default:
                        return;
                    case C1018R.id.menu_edit /* 2131362183 */:
                        if (hakAkses != null) {
                            if (!hakAkses.isUpdate()) {
                                Utils.msgBox("ليس لديك صلاحية التعديل", ListBondsPaymentActivity.this, new Object[0]);
                                return;
                            }
                            Intent intent = new Intent(ListBondsPaymentActivity.this, (Class<?>) EntryBondsPaymentActivity.class);
                            intent.putExtra("mode", "update");
                            intent.putExtra("data", itemBonds);
                            intent.putExtra("position", i);
                            intent.putExtra("title", "تعديل سند صرف ");
                            ListBondsPaymentActivity.this.startActivityForResult(intent, 200);
                            return;
                        }
                        return;
                    case C1018R.id.menu_print /* 2131362184 */:
                        new Thread() { // from class: com.yd.electricecollector.ui.ListBondsPaymentActivity.7.1
                            @Override // java.lang.Thread, java.lang.Runnable
                            public void run() {
                                try {
                                    if (SplashScreenActivity.mPrinter == null) {
                                        String setectedPrinterConnectionString = TAPreferences.getSetectedPrinterConnectionString(ListBondsPaymentActivity.this);
                                        if (!setectedPrinterConnectionString.equalsIgnoreCase("")) {
                                            ListBondsPaymentActivity.this.showProgress(C1018R.string.connecting);
                                            if (!setectedPrinterConnectionString.startsWith("bth://")) {
                                                throw new IllegalArgumentException("Unsupported connection string");
                                            }
                                            ListBondsPaymentActivity.this.connectBth(setectedPrinterConnectionString.substring(6));
                                            ListBondsPaymentActivity.this.dismissProgress();
                                        }
                                    }
                                    ListBondsPaymentActivity.this.showProgress(C1018R.string.printing_image);
                                    ListBondsPaymentActivity.this.doPrint(itemBonds);
                                    ListBondsPaymentActivity.this.dismissProgress();
                                } catch (Exception e) {
                                    ListBondsPaymentActivity.this.error(C1018R.drawable.bluetooth, e.getMessage());
                                }
                            }
                        }.start();
                        return;
                }
            }
        });
        this.recyclerView.addItemDecoration(dividerItemDecoration);
        this.tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", 0));
        if (this.tokenManager.getToken() == null) {
            startActivity(new Intent(this, (Class<?>) LoginActivity.class));
            finish();
        }
        AsyncDownloadStart();
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(C1018R.menu.o_bonds, menu);
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
    public void onFailed(ItemBonds itemBonds) {
        this.progress.setVisibility(8);
        Toast.makeText(this, "فشل عملية الحذف", 0).show();
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseView
    public void onLoadDataFailure() {
        Toast.makeText(this, "فشل تحميل البيانات", 0).show();
        this.progress.setVisibility(8);
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseView
    public void onLoadDataSucceed(List<ItemBonds> list) {
        this.progress.setVisibility(8);
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            finish();
            return true;
        }
        switch (menuItem.getItemId()) {
            case C1018R.id.mnuItemNewBond /* 2131362188 */:
                Intent intent = new Intent(this, (Class<?>) EntryBondsPaymentActivity.class);
                intent.putExtra("mode", "add");
                intent.putExtra("title", "سند قبض جديد");
                startActivityForResult(intent, 100);
                return true;
            case C1018R.id.mnuItemOptionDropDown /* 2131362189 */:
                if (this.layoutOptions.getVisibility() == 0) {
                    this.layoutOptions.setVisibility(8);
                } else {
                    this.layoutOptions.setVisibility(0);
                }
                return true;
            case C1018R.id.mnuItemPrint /* 2131362190 */:
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override // android.app.Activity
    protected void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        this.user = (Users) bundle.getParcelable("user_info");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putParcelable("user_info", this.appConfig.getUser());
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseView
    public void onSucceed(ItemBonds itemBonds) {
        this.progress.setVisibility(8);
        this._adapter.removeItem(this.posationDelete);
        this.posationDelete = -1;
        Toast.makeText(this, "تم الحذف بنجاح", 0).show();
    }
}
