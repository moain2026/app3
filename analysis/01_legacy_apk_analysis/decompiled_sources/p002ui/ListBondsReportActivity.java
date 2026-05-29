package com.p001yd.electricecollector.p002ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.datecs.api.printer.Printer;
import com.datecs.api.printer.PrinterInformation;
import com.datecs.api.printer.ProtocolAdapter;
import com.ganesh.intermecarabic.Arabic864;
import com.p001yd.electricecollector.Adapter.ListBondsReportAdapter;
import com.p001yd.electricecollector.C1018R;
import com.p001yd.electricecollector.LoginActivity;
import com.p001yd.electricecollector.SplashScreenActivity;
import com.p001yd.electricecollector.TAPreferences;
import com.p001yd.electricecollector.TokenManager;
import com.p001yd.electricecollector.Utils;
import com.p001yd.electricecollector.ViewPeriod;
import com.p001yd.electricecollector.common.AppConfig;
import com.p001yd.electricecollector.entities.BondsHeader;
import com.p001yd.electricecollector.entities.BondsResponse;
import com.p001yd.electricecollector.entities.ItemBonds;
import com.p001yd.electricecollector.entities.Users;
import com.p001yd.electricecollector.network.ApiService;
import com.p001yd.electricecollector.network.DialogCallback;
import com.p001yd.electricecollector.network.RetrofitBuilder;
import com.p001yd.electricecollector.printer.bluetooth.BluetoothConnector;
import com.p001yd.electricecollector.printer.driver.PrinterDriverFactory;
import com.p001yd.electricecollector.printer.driver.exceptions.BlueToothIsNotAvailableException;
import com.p001yd.electricecollector.printer.driver.exceptions.PrinterNotConnectedException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/* loaded from: classes12.dex */
public class ListBondsReportActivity extends AppCompatActivity implements BaseView<ItemBonds> {
    private ListBondsReportAdapter _adapter;
    private String _appId;
    private String _baseUrl;
    private AlertDialog _dialog;
    private String _token;
    AppConfig appConfig;
    BondsHeader bondsHeader;
    Call<BondsResponse> call;
    private String[] contextMenuArray;
    ProgressBar loadingProgressBar;
    private ProgressDialog mProgressDialog;
    BondsPresenter presenter;
    LinearLayout progress;
    RecyclerView recyclerView;
    ApiService service;
    String simpleTextPrintString;
    SwipeRefreshLayout swipeRefreshLayout;
    TokenManager tokenManager;
    TextView tvCase;
    TextView tvTitle;
    TextView tvTotal1;
    TextView tvTotal2;
    TextView tvTotal3;
    public ViewPeriod viewPeriod;
    private final String TAG = getClass().getSimpleName();
    int posationDelete = -1;
    private int _datePickerInput = 0;
    private boolean _isSetDate = false;
    private int _pageNumber = 1;
    private int _pageSize = 30;
    private int[] _pagesCount = new int[1];
    ItemBonds itemPrint = null;
    String num = null;
    String mdate = null;
    String name = null;
    Users user = null;
    private final Handler mHandler = new Handler();
    DialogInterface.OnClickListener barcodeTerminalHostingPageListener = new DialogInterface.OnClickListener() { // from class: com.yd.electricecollector.ui.ListBondsReportActivity.1
        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
        }
    };
    private Handler errorHandler = new Handler() { // from class: com.yd.electricecollector.ui.ListBondsReportActivity.2
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            ListBondsReportActivity.this.showMessagePopup(ListBondsReportActivity.this, message.what);
        }
    };
    DialogCallback dialogCallback = new DialogCallback() { // from class: com.yd.electricecollector.ui.ListBondsReportActivity.4
        @Override // com.p001yd.electricecollector.network.DialogCallback
        public void onCancel() {
        }

        /* JADX WARN: Type inference failed for: r0v2, types: [com.yd.electricecollector.ui.ListBondsReportActivity$4$1] */
        @Override // com.p001yd.electricecollector.network.DialogCallback
        public void onOk(Object obj) {
            if (ListBondsReportActivity.this.itemPrint != null) {
                new Thread() { // from class: com.yd.electricecollector.ui.ListBondsReportActivity.4.1
                    @Override // java.lang.Thread, java.lang.Runnable
                    public void run() {
                        try {
                            if (SplashScreenActivity.mPrinter == null) {
                                String setectedPrinterConnectionString = TAPreferences.getSetectedPrinterConnectionString(ListBondsReportActivity.this);
                                if (!setectedPrinterConnectionString.equalsIgnoreCase("")) {
                                    ListBondsReportActivity.this.showProgress(C1018R.string.connecting);
                                    if (!setectedPrinterConnectionString.startsWith("bth://")) {
                                        throw new IllegalArgumentException("Unsupported connection string");
                                    }
                                    ListBondsReportActivity.this.connectBth(setectedPrinterConnectionString.substring(6));
                                    ListBondsReportActivity.this.dismissProgress();
                                }
                            }
                            ListBondsReportActivity.this.showProgress(C1018R.string.printing_image);
                            ListBondsReportActivity.this.doPrint(ListBondsReportActivity.this.itemPrint);
                            ListBondsReportActivity.this.dismissProgress();
                        } catch (Exception e) {
                            ListBondsReportActivity.this.error(C1018R.drawable.bluetooth, e.getMessage());
                        }
                    }
                }.start();
            }
        }

        @Override // com.p001yd.electricecollector.network.DialogCallback
        public void onOk(Object obj, Object obj2) {
        }
    };
    private final Thread mConnectThread = new Thread() { // from class: com.yd.electricecollector.ui.ListBondsReportActivity.5
        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            String setectedPrinterConnectionString = TAPreferences.getSetectedPrinterConnectionString(ListBondsReportActivity.this);
            if (setectedPrinterConnectionString.equalsIgnoreCase("")) {
                return;
            }
            ListBondsReportActivity.this.showProgress(C1018R.string.connecting);
            if (!setectedPrinterConnectionString.startsWith("bth://")) {
                throw new IllegalArgumentException("Unsupported connection string");
            }
            ListBondsReportActivity.this.connectBth(setectedPrinterConnectionString.substring(6));
            ListBondsReportActivity.this.dismissProgress();
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    public void AsyncDownloadStart() {
        this.progress.setVisibility(0);
        HashMap hashMap = new HashMap();
        hashMap.put("nou", "" + this.appConfig.getUser().getNou());
        if (this.mdate != null) {
            hashMap.put("sdate", Utils.getShortDateStrApi(this, Utils.getDateFromString(Utils.getEnglishDate(this.bondsHeader.getDate(), "yyyy-MM-dd"))));
            hashMap.put("edate", Utils.getShortDateStrApi(this, Utils.getDateFromString(Utils.getEnglishDate(this.bondsHeader.getDate(), "yyyy-MM-dd"))));
        }
        if (this.num != null) {
            hashMap.put("num_s", "" + this.bondsHeader.getnum());
        }
        hashMap.put("appid", this.appConfig.getAppId());
        this.service = (ApiService) RetrofitBuilder.createServiceWithAuth(ApiService.class, this.tokenManager, this.appConfig.getBaseUrl());
        this.call = this.service.GetListBonds(hashMap);
        this.call.enqueue(new Callback<BondsResponse>() { // from class: com.yd.electricecollector.ui.ListBondsReportActivity.11
            @Override // retrofit2.Callback
            public void onFailure(Call<BondsResponse> call, Throwable th) {
                Log.w(ListBondsReportActivity.this.TAG, "onFailure: " + th.getMessage());
                ListBondsReportActivity.this.progress.setVisibility(8);
            }

            @Override // retrofit2.Callback
            public void onResponse(Call<BondsResponse> call, Response<BondsResponse> response) {
                Log.w(ListBondsReportActivity.this.TAG, "onResponse: " + response);
                if (response.isSuccessful()) {
                    if (response.body().getData() != null) {
                        ListBondsReportActivity.this._adapter.setItems(response.body().getData());
                    }
                    ListBondsReportActivity.this.progress.setVisibility(8);
                } else {
                    ListBondsReportActivity.this.tokenManager.deleteToken();
                    ListBondsReportActivity.this.startActivity(new Intent(ListBondsReportActivity.this, (Class<?>) LoginActivity.class));
                    ListBondsReportActivity.this.finish();
                }
                ListBondsReportActivity.this.progress.setVisibility(8);
            }
        });
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [com.yd.electricecollector.ui.ListBondsReportActivity$6] */
    private void Print(final ItemBonds itemBonds) {
        new Thread() { // from class: com.yd.electricecollector.ui.ListBondsReportActivity.6
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                try {
                    AppConfig appConfig = ListBondsReportActivity.this.appConfig;
                    if (AppConfig.printerDriver == null) {
                        AppConfig appConfig2 = ListBondsReportActivity.this.appConfig;
                        AppConfig.printerDriver = PrinterDriverFactory.create(ListBondsReportActivity.this);
                    }
                    AppConfig appConfig3 = ListBondsReportActivity.this.appConfig;
                    if (!AppConfig.printerDriver.isPrinterConnected()) {
                        String setectedPrinterConnectionString = TAPreferences.getSetectedPrinterConnectionString(ListBondsReportActivity.this);
                        if (!setectedPrinterConnectionString.equalsIgnoreCase("")) {
                            ListBondsReportActivity.this.showProgress(C1018R.string.connecting);
                            if (!setectedPrinterConnectionString.startsWith("bth://")) {
                                throw new IllegalArgumentException("Unsupported connection string");
                            }
                            try {
                                try {
                                    try {
                                        AppConfig appConfig4 = ListBondsReportActivity.this.appConfig;
                                        AppConfig.printerDriver.connect(setectedPrinterConnectionString.substring(6));
                                    } catch (InterruptedException e) {
                                        ListBondsReportActivity.this.error(C1018R.drawable.bluetooth, e.getMessage());
                                    }
                                } catch (IOException e2) {
                                    ListBondsReportActivity.this.error(C1018R.drawable.bluetooth, e2.getMessage());
                                }
                            } catch (BlueToothIsNotAvailableException e3) {
                                ListBondsReportActivity.this.error(C1018R.drawable.bluetooth, e3.getMessage());
                            } catch (PrinterNotConnectedException e4) {
                                ListBondsReportActivity.this.error(C1018R.drawable.bluetooth, e4.getMessage());
                            }
                            ListBondsReportActivity.this.dismissProgress();
                        }
                    }
                    ListBondsReportActivity.this.showProgress(C1018R.string.printing_page);
                    ListBondsReportActivity.this.doPrint2(itemBonds);
                    ListBondsReportActivity.this.dismissProgress();
                } catch (Exception e5) {
                    ListBondsReportActivity.this.error(C1018R.drawable.bluetooth, e5.getMessage());
                }
            }
        }.start();
    }

    private void deleteBond() {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dismissProgress() {
        this.mHandler.post(new Runnable() { // from class: com.yd.electricecollector.ui.ListBondsReportActivity.10
            @Override // java.lang.Runnable
            public void run() {
                ListBondsReportActivity.this.mProgressDialog.dismiss();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doPrint(ItemBonds itemBonds) {
        Arabic864 arabic864 = new Arabic864();
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
                    SplashScreenActivity.mPrinter.printText(arabic864.Convert("سند تحصيل ", false));
                    SplashScreenActivity.mPrinter.printTaggedText("{br}");
                    SplashScreenActivity.mPrinter.printTaggedText("{br}*********************   ********************{br}");
                    SplashScreenActivity.mPrinter.printTaggedText("{br}");
                    SplashScreenActivity.mPrinter.printTaggedText("{reset}{h}{b}      ");
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
                    SplashScreenActivity.mPrinter.printText(arabic864.Convert(" العميل : ", false));
                    SplashScreenActivity.mPrinter.printTaggedText("{br}");
                    if (itemBonds.getFinalBalance() != 0.0d) {
                        SplashScreenActivity.mPrinter.printTaggedText("{br}------------------------------------------------{br}\n");
                        SplashScreenActivity.mPrinter.printTaggedText("{reset}{right}{h}         ");
                        SplashScreenActivity.mPrinter.printText(Utils.numberToString(itemBonds.getFinalBalance() + itemBonds.getDain()));
                        SplashScreenActivity.mPrinter.printTaggedText("{reset}{right}{h}");
                        SplashScreenActivity.mPrinter.printText(arabic864.Convert(" رصيد سابق : ", false));
                        SplashScreenActivity.mPrinter.printTaggedText("{br}");
                    }
                    SplashScreenActivity.mPrinter.printTaggedText("{br}------------------------------------------------{br}\n");
                    SplashScreenActivity.mPrinter.printTaggedText("{reset}{right}{h}         ");
                    SplashScreenActivity.mPrinter.printText(Utils.numberToString(itemBonds.getDain()));
                    SplashScreenActivity.mPrinter.printTaggedText("{reset}{right}{h}");
                    SplashScreenActivity.mPrinter.printText(arabic864.Convert(" المبلغ : ", false));
                    SplashScreenActivity.mPrinter.printTaggedText("{br}");
                    SplashScreenActivity.mPrinter.printTaggedText("{br}------------------------------------------------{br}\n");
                    if (itemBonds.getFinalBalance() != 0.0d) {
                        SplashScreenActivity.mPrinter.printTaggedText("{reset}{right}{h}         ");
                        SplashScreenActivity.mPrinter.printText(Utils.numberToString(itemBonds.getFinalBalance()));
                        SplashScreenActivity.mPrinter.printTaggedText("{reset}{right}{h}");
                        SplashScreenActivity.mPrinter.printText(arabic864.Convert(" الرصيدالحالي : ", false));
                        SplashScreenActivity.mPrinter.printTaggedText("{br}");
                        SplashScreenActivity.mPrinter.printTaggedText("{br}------------------------------------------------{br}\n");
                    }
                    SplashScreenActivity.mPrinter.printTaggedText("{reset}{right}{h}");
                    SplashScreenActivity.mPrinter.printText(arabic864.Convert(itemBonds.getname_s(), false));
                    SplashScreenActivity.mPrinter.printTaggedText("{reset}{right}{h}");
                    SplashScreenActivity.mPrinter.printText(arabic864.Convert(" الصندوق/البنك : ", false));
                    SplashScreenActivity.mPrinter.printTaggedText("{br}");
                    if (itemBonds.getBin().length() > 0) {
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
                } catch (IOException e) {
                    error(C1018R.drawable.text, getString(C1018R.string.failed_print_text) + ". " + e.getMessage());
                }
            } catch (NullPointerException e2) {
                error(C1018R.drawable.bluetooth, getString(C1018R.string.printer_not_connected));
            } catch (Exception e3) {
                error(C1018R.drawable.bluetooth, e3.getMessage());
            }
        } catch (Exception e4) {
            error(C1018R.drawable.bluetooth, e4.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doPrint2(ItemBonds itemBonds) {
        Arabic864 arabic864 = new Arabic864();
        try {
            try {
                try {
                    try {
                        AppConfig.printerDriver.getPrinterInstance().reset();
                        AppConfig.printerDriver.getPrinterInstance().printTaggedText("{reset}{center}{w}{h}");
                        AppConfig.printerDriver.getPrinterInstance().printText(arabic864.Convert(TAPreferences.getCompanyName(this), false));
                        AppConfig.printerDriver.getPrinterInstance().printTaggedText("{br}------------------------------------------------{br}\n");
                        AppConfig.printerDriver.getPrinterInstance().printTaggedText("{reset}{center}{w}{h}");
                        AppConfig.printerDriver.getPrinterInstance().printText(arabic864.Convert("سند تحصيل نقدي", false));
                        AppConfig.printerDriver.getPrinterInstance().printArabicText(22, "سند تحصيل ");
                        AppConfig.printerDriver.getPrinterInstance().printTaggedText("{br}============================{br}");
                        AppConfig.printerDriver.getPrinterInstance().feedPaper(110);
                        AppConfig.printerDriver.getPrinterInstance().flush();
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
        this.mHandler.post(new Runnable() { // from class: com.yd.electricecollector.ui.ListBondsReportActivity.9
            @Override // java.lang.Runnable
            public void run() {
                AlertDialog create = new AlertDialog.Builder(ListBondsReportActivity.this).setTitle("Error").setMessage(str).create();
                create.setIcon(i);
                create.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.yd.electricecollector.ui.ListBondsReportActivity.9.1
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

    private void openViewPeriod() {
        Intent intent = new Intent(getApplicationContext(), (Class<?>) ViewPeriodActivity.class);
        ViewPeriod viewPeriod = this.viewPeriod;
        Bundle bundle = new Bundle();
        bundle.putSerializable("VIEW_PERIOD", viewPeriod);
        intent.putExtras(bundle);
        startActivityForResult(intent, 5);
    }

    private void setPrinterInfo(int i, String str) {
        this.mHandler.post(new Runnable() { // from class: com.yd.electricecollector.ui.ListBondsReportActivity.7
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
        this.mHandler.post(new Runnable() { // from class: com.yd.electricecollector.ui.ListBondsReportActivity.8
            @Override // java.lang.Runnable
            public void run() {
                ListBondsReportActivity.this.mProgressDialog = ProgressDialog.show(ListBondsReportActivity.this, ListBondsReportActivity.this.getString(C1018R.string.please_wait), str, true);
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

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(C1018R.layout.list_bonds_report_activity3);
        setTitle("سندات القبض   ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.num = getIntent().getStringExtra("num");
        this.mdate = getIntent().getStringExtra("mdate");
        this.name = getIntent().getStringExtra("name");
        initializeView();
        this.appConfig = AppConfig.getInstance();
        this._baseUrl = this.appConfig.getBaseUrl();
        this._token = this.appConfig.getToken();
        this._appId = this.appConfig.getAppId();
        this.bondsHeader = (BondsHeader) getIntent().getParcelableExtra("data");
        setTitle(((Object) "") + this.name + "/" + this.mdate);
        this.appConfig = AppConfig.getInstance();
        this.appConfig.getBaseUrl();
        this.appConfig.getToken();
        this.appConfig.getAppId();
        this.user = this.appConfig.getUser();
        this._pageSize = this.appConfig.getPageSize();
        this._adapter = new ListBondsReportAdapter();
        this.recyclerView.setAdapter(this._adapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, 1);
        this.swipeRefreshLayout = (SwipeRefreshLayout) findViewById(C1018R.id.swipeRefresh);
        this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() { // from class: com.yd.electricecollector.ui.ListBondsReportActivity.3
            @Override // androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
            public void onRefresh() {
                ListBondsReportActivity.this.AsyncDownloadStart();
                ListBondsReportActivity.this._adapter.notifyDataSetChanged();
                ListBondsReportActivity.this.swipeRefreshLayout.setRefreshing(false);
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
        menuItem.getItemId();
        return super.onOptionsItemSelected(menuItem);
    }

    @Override // android.app.Activity
    protected void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        this.user = (Users) bundle.getParcelable("user_info");
        this.mdate = bundle.getString("mdate");
        this.name = bundle.getString("name");
        this.num = bundle.getString("num");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putParcelable("user_info", this.appConfig.getUser());
        bundle.putString("name", this.name);
        bundle.putString("num", this.num);
        bundle.putString("mdate", this.mdate);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStart() {
        super.onStart();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStop() {
        super.onStop();
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseView
    public void onSucceed(ItemBonds itemBonds) {
        this.progress.setVisibility(8);
        this._adapter.removeItem(this.posationDelete);
        this.posationDelete = -1;
        Toast.makeText(this, "تم الحذف بنجاح", 0).show();
    }

    protected void showErrorPopup(Context context, int i) {
        new AlertDialog.Builder(context).setMessage(i).setTitle(C1018R.string.error).setPositiveButton(C1018R.string.btnOk, (DialogInterface.OnClickListener) null).setCancelable(true).create().show();
    }

    protected void showMessagePopup(Context context, int i) {
        new AlertDialog.Builder(context).setMessage(i).setTitle(C1018R.string.app_name).setPositiveButton(C1018R.string.txtYes, this.barcodeTerminalHostingPageListener).setNegativeButton(C1018R.string.txtNo, this.barcodeTerminalHostingPageListener).setCancelable(true).create().show();
    }
}
