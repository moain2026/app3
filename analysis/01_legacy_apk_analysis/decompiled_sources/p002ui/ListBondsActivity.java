package com.p001yd.electricecollector.p002ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.ViewCompat;
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
import com.itextpdf.text.pdf.BidiOrder;
import com.p001yd.electricecollector.Adapter.ListBondsAdapter;
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
import com.p001yd.electricecollector.common.ErrorHandler;
import com.p001yd.electricecollector.entities.BondsResponse;
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
import com.p001yd.electricecollector.printer.driver.PrinterDriverFactory;
import com.p001yd.electricecollector.printer.driver.exceptions.BlueToothIsNotAvailableException;
import com.p001yd.electricecollector.printer.driver.exceptions.PrinterNotConnectedException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import org.json.JSONException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/* loaded from: classes12.dex */
public class ListBondsActivity extends AppCompatActivity implements BaseView<ItemBonds>, ErrorHandler.ErrorCallback {
    private ListBondsAdapter _adapter;
    private String _appId;
    private String _baseUrl;
    private AlertDialog _dialog;
    private String _token;
    AppConfig appConfig;
    Call<BondsResponse> call;
    private String[] contextMenuArray;
    protected LinearLayout layoutOptions;
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
    TextView tvEndDate;
    TextView tvStartDate;
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
    Users user = null;
    private final Handler mHandler = new Handler();
    DialogInterface.OnClickListener barcodeTerminalHostingPageListener = new DialogInterface.OnClickListener() { // from class: com.yd.electricecollector.ui.ListBondsActivity.1
        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
        }
    };
    private Handler errorHandler = new Handler() { // from class: com.yd.electricecollector.ui.ListBondsActivity.2
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            ListBondsActivity.this.showMessagePopup(ListBondsActivity.this, message.what);
        }
    };
    DialogCallback dialogCallback = new DialogCallback() { // from class: com.yd.electricecollector.ui.ListBondsActivity.11
        @Override // com.p001yd.electricecollector.network.DialogCallback
        public void onCancel() {
        }

        /* JADX WARN: Type inference failed for: r0v2, types: [com.yd.electricecollector.ui.ListBondsActivity$11$1] */
        @Override // com.p001yd.electricecollector.network.DialogCallback
        public void onOk(Object obj) {
            if (ListBondsActivity.this.itemPrint != null) {
                new Thread() { // from class: com.yd.electricecollector.ui.ListBondsActivity.11.1
                    @Override // java.lang.Thread, java.lang.Runnable
                    public void run() {
                        try {
                            if (SplashScreenActivity.mPrinter == null) {
                                String setectedPrinterConnectionString = TAPreferences.getSetectedPrinterConnectionString(ListBondsActivity.this);
                                if (!setectedPrinterConnectionString.equalsIgnoreCase("")) {
                                    ListBondsActivity.this.showProgress(C1018R.string.connecting);
                                    if (!setectedPrinterConnectionString.startsWith("bth://")) {
                                        throw new IllegalArgumentException("Unsupported connection string");
                                    }
                                    ListBondsActivity.this.connectBth(setectedPrinterConnectionString.substring(6));
                                    ListBondsActivity.this.dismissProgress();
                                }
                            }
                            ListBondsActivity.this.showProgress(C1018R.string.printing_image);
                            ListBondsActivity.this.doPrint2(ListBondsActivity.this.itemPrint);
                            ListBondsActivity.this.dismissProgress();
                        } catch (Exception e) {
                            ListBondsActivity.this.error(C1018R.drawable.bluetooth, e.getMessage());
                        }
                    }
                }.start();
            }
        }

        @Override // com.p001yd.electricecollector.network.DialogCallback
        public void onOk(Object obj, Object obj2) {
        }
    };
    private final Thread mConnectThread = new Thread() { // from class: com.yd.electricecollector.ui.ListBondsActivity.12
        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            String setectedPrinterConnectionString = TAPreferences.getSetectedPrinterConnectionString(ListBondsActivity.this);
            if (setectedPrinterConnectionString.equalsIgnoreCase("")) {
                return;
            }
            ListBondsActivity.this.showProgress(C1018R.string.connecting);
            if (!setectedPrinterConnectionString.startsWith("bth://")) {
                throw new IllegalArgumentException("Unsupported connection string");
            }
            ListBondsActivity.this.connectBth(setectedPrinterConnectionString.substring(6));
            ListBondsActivity.this.dismissProgress();
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    public void AsyncDownloadStart() {
        this.progress.setVisibility(0);
        HashMap hashMap = new HashMap();
        hashMap.put("nou", "" + this.appConfig.getUser().getNou());
        hashMap.put("sdate", Utils.getShortDateStrApi(this, this.viewPeriod.startDate));
        hashMap.put("edate", Utils.getShortDateStrApi(this, this.viewPeriod.endDate));
        if (this.appConfig.getUser().getSYS() != 1) {
            hashMap.put("num_s", "" + this.appConfig.getUser().getNOA());
        }
        Log.w(this.TAG, "_base_url: " + this.appConfig.getBaseUrl());
        hashMap.put("appId", this.appConfig.getAppId());
        this.service = (ApiService) RetrofitBuilder.createServiceWithAuth(ApiService.class, this.tokenManager, this.appConfig.getBaseUrl());
        this.call = this.service.GetListBonds(hashMap);
        this.call.enqueue(new Callback<BondsResponse>() { // from class: com.yd.electricecollector.ui.ListBondsActivity.18
            @Override // retrofit2.Callback
            public void onFailure(Call<BondsResponse> call, Throwable th) {
                Log.w(ListBondsActivity.this.TAG, "onFailure: " + th.getMessage());
                ListBondsActivity.this.progress.setVisibility(8);
                ErrorHandler.handleError(th, ListBondsActivity.this);
            }

            @Override // retrofit2.Callback
            public void onResponse(Call<BondsResponse> call, Response<BondsResponse> response) {
                Log.w(ListBondsActivity.this.TAG, "onResponse: " + response);
                if (response.isSuccessful()) {
                    if (response.body().getData() != null) {
                        ListBondsActivity.this._adapter.setItems(response.body().getData());
                    }
                    ListBondsActivity.this.progress.setVisibility(8);
                } else {
                    ListBondsActivity.this.handleResponseError(response);
                }
                ListBondsActivity.this.progress.setVisibility(8);
            }
        });
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [com.yd.electricecollector.ui.ListBondsActivity$13] */
    private void Print(final ItemBonds itemBonds) {
        new Thread() { // from class: com.yd.electricecollector.ui.ListBondsActivity.13
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                try {
                    AppConfig appConfig = ListBondsActivity.this.appConfig;
                    if (AppConfig.printerDriver == null) {
                        AppConfig appConfig2 = ListBondsActivity.this.appConfig;
                        AppConfig.printerDriver = PrinterDriverFactory.create(ListBondsActivity.this);
                    }
                    AppConfig appConfig3 = ListBondsActivity.this.appConfig;
                    if (!AppConfig.printerDriver.isPrinterConnected()) {
                        String setectedPrinterConnectionString = TAPreferences.getSetectedPrinterConnectionString(ListBondsActivity.this);
                        if (!setectedPrinterConnectionString.equalsIgnoreCase("")) {
                            ListBondsActivity.this.showProgress(C1018R.string.connecting);
                            if (!setectedPrinterConnectionString.startsWith("bth://")) {
                                throw new IllegalArgumentException("Unsupported connection string");
                            }
                            try {
                                try {
                                    try {
                                        AppConfig appConfig4 = ListBondsActivity.this.appConfig;
                                        AppConfig.printerDriver.connect(setectedPrinterConnectionString.substring(6));
                                    } catch (InterruptedException e) {
                                        ListBondsActivity.this.error(C1018R.drawable.bluetooth, e.getMessage());
                                    }
                                } catch (IOException e2) {
                                    ListBondsActivity.this.error(C1018R.drawable.bluetooth, e2.getMessage());
                                }
                            } catch (BlueToothIsNotAvailableException e3) {
                                ListBondsActivity.this.error(C1018R.drawable.bluetooth, e3.getMessage());
                            } catch (PrinterNotConnectedException e4) {
                                ListBondsActivity.this.error(C1018R.drawable.bluetooth, e4.getMessage());
                            }
                            ListBondsActivity.this.dismissProgress();
                        }
                    }
                    ListBondsActivity.this.showProgress(C1018R.string.printing_page);
                    ListBondsActivity.this.doPrint2(itemBonds);
                    ListBondsActivity.this.dismissProgress();
                } catch (Exception e5) {
                    ListBondsActivity.this.error(C1018R.drawable.bluetooth, e5.getMessage());
                }
            }
        }.start();
    }

    private void deleteBond() {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dismissProgress() {
        this.mHandler.post(new Runnable() { // from class: com.yd.electricecollector.ui.ListBondsActivity.17
            @Override // java.lang.Runnable
            public void run() {
                ListBondsActivity.this.mProgressDialog.dismiss();
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
                    SplashScreenActivity.mPrinter.printText(new byte[]{28, 46, 27, 119, 37, BidiOrder.NSM, 10});
                    SplashScreenActivity.mPrinter.printText(new byte[]{27, 116, 22});
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
                SplashScreenActivity.mPrinter.reset();
                SplashScreenActivity.mPrinter.printText(new byte[]{28, 46, 27, 119, 37, BidiOrder.NSM, 10});
                SplashScreenActivity.mPrinter.printText(new byte[]{27, 116, 22});
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
                SplashScreenActivity.mPrinter.printTaggedText("{br}***************   **************{br}");
                SplashScreenActivity.mPrinter.printTaggedText("{br}");
                SplashScreenActivity.mPrinter.printTaggedText("{reset}{b}      ");
                SplashScreenActivity.mPrinter.printText(itemBonds.getNmstnd());
                SplashScreenActivity.mPrinter.setLineSpace(20);
                SplashScreenActivity.mPrinter.printTaggedText("{reset}{right}");
                SplashScreenActivity.mPrinter.printText(arabic864.Convert(" رقم السند   : ", false));
                SplashScreenActivity.mPrinter.printTaggedText("{br}");
                SplashScreenActivity.mPrinter.printTaggedText("{br}--------------------------------{br}\n");
                SplashScreenActivity.mPrinter.printTaggedText("{reset}{right}");
                SplashScreenActivity.mPrinter.printText(itemBonds.getDate());
                SplashScreenActivity.mPrinter.printTaggedText("{reset}{right}");
                SplashScreenActivity.mPrinter.printText(arabic864.Convert(" تاريخ السند : ", false));
                SplashScreenActivity.mPrinter.printTaggedText("{br}");
                SplashScreenActivity.mPrinter.printTaggedText("{br}--------------------------------{br}\n");
                SplashScreenActivity.mPrinter.printTaggedText("{reset}{right}");
                SplashScreenActivity.mPrinter.printText(arabic864.Convert(" اسم المشترك : ", false));
                SplashScreenActivity.mPrinter.printTaggedText("{br}");
                SplashScreenActivity.mPrinter.printTaggedText("{reset}{right}");
                SplashScreenActivity.mPrinter.printText(arabic864.Convert(itemBonds.getname(), false));
                SplashScreenActivity.mPrinter.printTaggedText("{br}");
                if (itemBonds.getFinalBalance() != 0.0d) {
                    SplashScreenActivity.mPrinter.printTaggedText("{br}-----------------------------------{br}\n");
                    SplashScreenActivity.mPrinter.printTaggedText("{reset}{right}         ");
                    SplashScreenActivity.mPrinter.printText(Utils.numberToString(itemBonds.getFinalBalance() + itemBonds.getDain()));
                    SplashScreenActivity.mPrinter.printTaggedText("{reset}{right}");
                    SplashScreenActivity.mPrinter.printText(arabic864.Convert(" رصيد سابق : ", false));
                    SplashScreenActivity.mPrinter.printTaggedText("{br}");
                }
                SplashScreenActivity.mPrinter.printTaggedText("{br}--------------------------------{br}\n");
                SplashScreenActivity.mPrinter.printTaggedText("{reset}{right}         ");
                SplashScreenActivity.mPrinter.printText(Utils.numberToString(itemBonds.getDain()));
                SplashScreenActivity.mPrinter.printTaggedText("{reset}{right}");
                SplashScreenActivity.mPrinter.printText(arabic864.Convert(" المبلغ : ", false));
                SplashScreenActivity.mPrinter.printTaggedText("{br}");
                SplashScreenActivity.mPrinter.printTaggedText("{br}--------------------------------{br}\n");
                if (itemBonds.getFinalBalance() != 0.0d) {
                    SplashScreenActivity.mPrinter.printTaggedText("{reset}{right}         ");
                    SplashScreenActivity.mPrinter.printText(Utils.numberToString(itemBonds.getFinalBalance()));
                    SplashScreenActivity.mPrinter.printTaggedText("{reset}{right}");
                    SplashScreenActivity.mPrinter.printText(arabic864.Convert(" الرصيدالحالي : ", false));
                    SplashScreenActivity.mPrinter.printTaggedText("{br}");
                    SplashScreenActivity.mPrinter.printTaggedText("{br}--------------------------------{br}\n");
                }
                SplashScreenActivity.mPrinter.printTaggedText("{reset}{right}");
                SplashScreenActivity.mPrinter.printText(arabic864.Convert(itemBonds.getname_s(), false));
                SplashScreenActivity.mPrinter.printTaggedText("{reset}{right}");
                SplashScreenActivity.mPrinter.printText(arabic864.Convert(" الصندوق/البنك : ", false));
                SplashScreenActivity.mPrinter.printTaggedText("{br}");
                if (itemBonds.getBin().length() > 0) {
                    SplashScreenActivity.mPrinter.printTaggedText("{br}--------------------------------{br}\n");
                    SplashScreenActivity.mPrinter.printTaggedText("{reset}{right} ");
                    SplashScreenActivity.mPrinter.printText(arabic864.Convert(itemBonds.getBin(), false));
                    SplashScreenActivity.mPrinter.printTaggedText("{reset}{right}");
                    SplashScreenActivity.mPrinter.printText(arabic864.Convert(" البيان : ", false));
                    SplashScreenActivity.mPrinter.printTaggedText("{br}");
                }
                SplashScreenActivity.mPrinter.printTaggedText("{br}--------------------------------{br}\n");
                SplashScreenActivity.mPrinter.printTaggedText("{reset}{right} ");
                SplashScreenActivity.mPrinter.printTaggedText(Utils.getCurrentDate());
                SplashScreenActivity.mPrinter.printTaggedText("{reset}{right} ");
                SplashScreenActivity.mPrinter.printText(arabic864.Convert(" تاريخ الطباعة: ", false));
                SplashScreenActivity.mPrinter.printTaggedText("{br}      *******************      {br}");
                SplashScreenActivity.mPrinter.printTaggedText("{br}");
                SplashScreenActivity.mPrinter.printTaggedText("{br}");
                SplashScreenActivity.mPrinter.feedPaper(110);
                SplashScreenActivity.mPrinter.flush();
            } catch (Exception e) {
                error(C1018R.drawable.bluetooth, e.getMessage());
            }
        } catch (IOException e2) {
            error(C1018R.drawable.text, getString(C1018R.string.failed_print_text) + ". " + e2.getMessage());
        } catch (NullPointerException e3) {
            error(C1018R.drawable.bluetooth, getString(C1018R.string.printer_not_connected));
        } catch (Exception e4) {
            error(C1018R.drawable.bluetooth, e4.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doPrint3(ItemBonds itemBonds) {
        Bitmap createReceiptBitmap = createReceiptBitmap(itemBonds);
        int width = createReceiptBitmap.getWidth();
        int height = createReceiptBitmap.getHeight();
        int[] iArr = new int[width * height];
        createReceiptBitmap.getPixels(iArr, 0, width, 0, 0, width, height);
        createReceiptBitmap.recycle();
        try {
            SplashScreenActivity.mPrinter.reset();
            SplashScreenActivity.mPrinter.printImage(iArr, width, height, 1, true);
            SplashScreenActivity.mPrinter.feedPaper(110);
            SplashScreenActivity.mPrinter.flush();
        } catch (IOException e) {
            error(C1018R.drawable.text, getString(C1018R.string.failed_print_text) + ". " + e.getMessage());
        } catch (NullPointerException e2) {
            error(C1018R.drawable.bluetooth, getString(C1018R.string.printer_not_connected));
        } catch (Exception e3) {
            error(C1018R.drawable.bluetooth, e3.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void error(final int i, final String str) {
        this.mHandler.post(new Runnable() { // from class: com.yd.electricecollector.ui.ListBondsActivity.16
            @Override // java.lang.Runnable
            public void run() {
                AlertDialog create = new AlertDialog.Builder(ListBondsActivity.this).setTitle("Error").setMessage(str).create();
                create.setIcon(i);
                create.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.yd.electricecollector.ui.ListBondsActivity.16.1
                    @Override // android.content.DialogInterface.OnDismissListener
                    public void onDismiss(DialogInterface dialogInterface) {
                    }
                });
                create.show();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleResponseError(Response<?> response) {
        try {
            showErrorMessage("خطأ: " + response.code() + " - " + response.errorBody().string());
        } catch (IOException e) {
            showErrorMessage("خطأ في قراءة رسالة الخطأ");
        }
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
        this.mHandler.post(new Runnable() { // from class: com.yd.electricecollector.ui.ListBondsActivity.14
            @Override // java.lang.Runnable
            public void run() {
            }
        });
    }

    private void showErrorMessage(String str) {
        new AlertDialog.Builder(this).setTitle("خطأ").setMessage(str).setPositiveButton("حسناً", (DialogInterface.OnClickListener) null).show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showProgress(int i) {
        showProgress(getString(i));
    }

    private void showProgress(final String str) {
        this.mHandler.post(new Runnable() { // from class: com.yd.electricecollector.ui.ListBondsActivity.15
            @Override // java.lang.Runnable
            public void run() {
                ListBondsActivity.this.mProgressDialog = ProgressDialog.show(ListBondsActivity.this, ListBondsActivity.this.getString(C1018R.string.please_wait), str, true);
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

    public Bitmap createReceiptBitmap(ItemBonds itemBonds) {
        int i = 20;
        if (TAPreferences.getPaperSize(this) == 1) {
            i = 26;
        } else if (TAPreferences.getPaperSize(this) == 2) {
            i = 28;
        }
        if (TAPreferences.getPaperSize(this) == 3) {
            i = 32;
        }
        int i2 = TAPreferences.getPaperSize(this) == 1 ? 649 : 384;
        int i3 = itemBonds.getFinalBalance() != 0.0d ? 700 + 200 : 700;
        if (itemBonds.getBin().length() > 0) {
            i3 += 100;
        }
        Bitmap createBitmap = Bitmap.createBitmap(i2, i3, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        canvas.drawColor(-1);
        Paint paint = new Paint();
        paint.setColor(ViewCompat.MEASURED_STATE_MASK);
        paint.setTextSize(i + 4);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, 1));
        paint.setTextAlign(Paint.Align.CENTER);
        Paint paint2 = new Paint();
        paint2.setColor(ViewCompat.MEASURED_STATE_MASK);
        paint2.setStrokeWidth(2.0f);
        paint2.setPathEffect(new DashPathEffect(new float[]{10.0f, 5.0f}, 0.0f));
        int i4 = i2 - 10;
        canvas.drawText(TAPreferences.getCompanyName(this), i2 / 2, 40, paint);
        int i5 = 40 + 40;
        canvas.drawText(TAPreferences.getCompanyAddress(this), i2 / 2, i5, paint);
        int i6 = i5 + 40;
        canvas.drawText(TAPreferences.getCompanyPhone(this), i2 / 2, i6, paint);
        int i7 = i6 + 40;
        canvas.drawLine(40.0f, i7, i2 - 40, i7, paint2);
        int i8 = i7 + 5;
        canvas.drawLine(40.0f, i8, i2 - 40, i8, paint2);
        int i9 = i8 + 20;
        canvas.drawText("سند قبض نقدي", i2 / 2, i9, paint);
        int i10 = i9 + 20;
        paint.setTextSize(i);
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawLine(40.0f, i10, i2 - 40, i10, paint2);
        int i11 = i10 + 5;
        canvas.drawLine(40.0f, i11, i2 - 40, i11, paint2);
        int i12 = i11 + 30;
        canvas.drawText("رقم السند:" + itemBonds.getNmstnd(), i4, i12, paint);
        int i13 = i12 + 40;
        canvas.drawLine(10.0f, i13, i2 - 10, i13, paint2);
        int i14 = i13 + 30;
        canvas.drawText("التاريخ :" + itemBonds.getDate(), i4, i14, paint);
        int i15 = i14 + 40;
        canvas.drawLine(10.0f, i15, i2 - 10, i15, paint2);
        int i16 = i15 + 30;
        canvas.drawText("اسم المشترك :", i4, i16, paint);
        int i17 = i16 + 40;
        canvas.drawText(itemBonds.getname() + "", i4, i17, paint);
        int i18 = i17 + 40;
        canvas.drawLine(10.0f, i18, i2 - 10, i18, paint2);
        int i19 = i18 + 30;
        if (itemBonds.getFinalBalance() != 0.0d) {
            canvas.drawText("رصيد سابق :" + Utils.numberToString(itemBonds.getFinalBalance() - itemBonds.getDain()), i4, i19, paint);
            int i20 = i19 + 40;
            canvas.drawLine(10.0f, i20, i2 - 10, i20, paint2);
            i19 = i20 + 30;
        }
        canvas.drawText("المبلغ :" + itemBonds.getDain(), i4, i19, paint);
        int i21 = i19 + 40;
        canvas.drawLine(10.0f, i21, i2 - 10, i21, paint2);
        int i22 = i21 + 30;
        if (itemBonds.getFinalBalance() != 0.0d) {
            canvas.drawText(Utils.numberToString(itemBonds.getFinalBalance()) + "رصيد الحالي :", i4, i22, paint);
            int i23 = i22 + 40;
            canvas.drawLine(10.0f, i23, i2 - 10, i23, paint2);
            i22 = i23 + 30;
        }
        canvas.drawText("الصندوق/البنك:" + itemBonds.getname_s(), i4, i22, paint);
        int i24 = i22 + 40;
        canvas.drawLine(10.0f, i24, i2 - 10, i24, paint2);
        int i25 = i24 + 30;
        if (itemBonds.getBin().length() > 0) {
            canvas.drawText("البيان:" + itemBonds.getNotes(), i4, i25, paint);
            i25 += 40;
        }
        paint.setTextSize(i - 4);
        canvas.drawText("تاريخ الطباعة:" + Utils.getCurrentDate(), i4, i25, paint);
        return createBitmap;
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

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Type inference failed for: r3v8, types: [com.yd.electricecollector.ui.ListBondsActivity$9] */
    /* renamed from: lambda$onCreate$0$com-yd-electricecollector-ui-ListBondsActivity, reason: not valid java name */
    public /* synthetic */ void m511lambda$onCreate$0$comydelectricecollectoruiListBondsActivity(MenuItem menuItem, final int i) {
        final ItemBonds itemBonds = this._adapter.getItems().get(i);
        int itemId = menuItem.getItemId();
        HakAccess hakAkses = HakAccessHelper.getHakAkses("repBondsReciept", this.appConfig.getListHakAccess());
        switch (itemId) {
            case C1018R.id.menu_delete /* 2131362181 */:
                if (hakAkses != null) {
                    if (!hakAkses.isDelete()) {
                        Utils.msgBox("ليس لديك صلاحية الحذف", this, new Object[0]);
                        return;
                    } else if (itemBonds.getCas() == 0) {
                        Utils.msgBox(new DialogInterface.OnClickListener() { // from class: com.yd.electricecollector.ui.ListBondsActivity.10
                            @Override // android.content.DialogInterface.OnClickListener
                            public void onClick(DialogInterface dialogInterface, int i2) {
                                ListBondsActivity.this.presenter = new BondsPresenter(ListBondsActivity.this._baseUrl, ListBondsActivity.this._token, ListBondsActivity.this._appId, ListBondsActivity.this);
                                try {
                                    ListBondsActivity.this.progress.setVisibility(0);
                                    ListBondsActivity.this.posationDelete = i;
                                    ListBondsActivity.this.presenter.delete(itemBonds);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, "هل تريد حذف السند برقم " + itemBonds.getNmstnd(), this, new Object[0]);
                        return;
                    } else {
                        Utils.msgBox("لايمكنك حذف السند المرحل", this, new Object[0]);
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
                        Utils.msgBox("ليس لديك صلاحية التعديل", this, new Object[0]);
                        return;
                    }
                    if (itemBonds.getCas() != 0) {
                        Utils.msgBox("لايمكنك تعديل السند المرحل", this, new Object[0]);
                        return;
                    }
                    Intent intent = new Intent(this, (Class<?>) EntryBondsActivity.class);
                    intent.putExtra("mode", "update");
                    intent.putExtra("data", itemBonds);
                    intent.putExtra("position", i);
                    intent.putExtra("title", "تعديل سند قبض ");
                    startActivityForResult(intent, 200);
                    return;
                }
                return;
            case C1018R.id.menu_print /* 2131362184 */:
                new Thread() { // from class: com.yd.electricecollector.ui.ListBondsActivity.9
                    @Override // java.lang.Thread, java.lang.Runnable
                    public void run() {
                        try {
                            if (SplashScreenActivity.mPrinter == null) {
                                String setectedPrinterConnectionString = TAPreferences.getSetectedPrinterConnectionString(ListBondsActivity.this);
                                if (!setectedPrinterConnectionString.equalsIgnoreCase("")) {
                                    ListBondsActivity.this.showProgress(C1018R.string.connecting);
                                    if (!setectedPrinterConnectionString.startsWith("bth://")) {
                                        throw new IllegalArgumentException("Unsupported connection string");
                                    }
                                    ListBondsActivity.this.connectBth(setectedPrinterConnectionString.substring(6));
                                    ListBondsActivity.this.dismissProgress();
                                }
                            }
                            ListBondsActivity.this.showProgress(C1018R.string.printing_image);
                            if (TAPreferences.getPrinterRype(ListBondsActivity.this) != 1) {
                                ListBondsActivity.this.doPrint3(itemBonds);
                            } else if (TAPreferences.getPaperSize(ListBondsActivity.this) == 1) {
                                ListBondsActivity.this.doPrint(itemBonds);
                            } else {
                                ListBondsActivity.this.doPrint2(itemBonds);
                            }
                            ListBondsActivity.this.dismissProgress();
                        } catch (Exception e) {
                            ListBondsActivity.this.error(C1018R.drawable.bluetooth, e.getMessage());
                        }
                    }
                }.start();
                return;
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
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
        if (i != 100) {
            if (i == 200 && i2 == 201) {
                int intExtra = intent.getIntExtra("position", 0);
                ItemBonds itemBonds = (ItemBonds) intent.getParcelableExtra("data");
                itemBonds.setDate(Utils.getConvertEnglishDate(itemBonds.getDate(), "yyyy/mm/dd"));
                this._adapter.updateItem(itemBonds, intExtra);
                this.recyclerView.smoothScrollToPosition(intExtra);
                Toast.makeText(this, "تم حفظ السند", 0).show();
                Utils.msgBox("تم تعديل سند قبض برقم " + itemBonds.getNmstnd() + "\n مبلغ  " + itemBonds.getDain() + "\n الى حساب المشترك/ " + itemBonds.getname() + "\n رقم المشترك " + itemBonds.getnum(), this, new Object[0]);
                return;
            }
            return;
        }
        if (i2 == 101) {
            ItemBonds itemBonds2 = (ItemBonds) intent.getParcelableExtra("data");
            itemBonds2.setDate(Utils.getConvertEnglishDate(itemBonds2.getDate(), "yyyy/mm/dd"));
            this._adapter.setItem(itemBonds2);
            this.recyclerView.smoothScrollToPosition(this._adapter.getItemCount() - 1);
            Toast.makeText(this, "تم حفظ السند", 0).show();
            this.itemPrint = itemBonds2;
            DialogHelper.msgDialogConfirm("تم اضافة سند قبض برقم " + itemBonds2.getNmstnd() + "\n مبلغ " + Utils.numberToString(itemBonds2.getDain()) + "\n الى حساب المشترك/ " + itemBonds2.getname() + "\n رقم المشترك " + itemBonds2.getnum() + "\n\n الرصيدالحالي " + Utils.numberToString(itemBonds2.getFinalBalance()) + "\n\nهل تريد طباعة السند؟", this, this.dialogCallback).create().show();
            if (!TAPreferences.getSendSMS(this) || itemBonds2.getAccount().getTel() == null) {
                return;
            }
            Utils.openSendSmsSilent(itemBonds2.getAccount().getTel(), TAPreferences.getMessageText(this) + Utils.numberToString(itemBonds2.getDain()) + "\n " + TAPreferences.getMessageFooterText(this) + "\n " + TAPreferences.getCompanyName(this), this);
        }
    }

    @Override // com.yd.electricecollector.common.ErrorHandler.ErrorCallback
    public void onBadGateway(String str) {
        showErrorMessage(str);
    }

    @Override // com.yd.electricecollector.common.ErrorHandler.ErrorCallback
    public void onBadRequest(String str) {
        showErrorMessage(str);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(C1018R.layout.list_bonds_report_activity);
        setTitle("سندات القبض   ");
        ((ActionBar) Objects.requireNonNull(getSupportActionBar())).setDisplayHomeAsUpEnabled(true);
        AppCompatDelegate.setDefaultNightMode(1);
        initializeView();
        this.appConfig = AppConfig.getInstance();
        this._baseUrl = this.appConfig.getBaseUrl();
        this._token = this.appConfig.getToken();
        this._appId = this.appConfig.getAppId();
        this.tvStartDate = (TextView) findViewById(C1018R.id.txtStartDate);
        this.tvEndDate = (TextView) findViewById(C1018R.id.txtEndDate);
        this.layoutOptions = (LinearLayout) findViewById(C1018R.id.layoutOptions);
        this.layoutOptions.setVisibility(8);
        this.tvStartDate.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.ui.ListBondsActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ListBondsActivity.this.openViewPeriod();
            }
        });
        this.tvEndDate.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.ui.ListBondsActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ListBondsActivity.this.openViewPeriod();
            }
        });
        this.viewPeriod = AppConfig.getInstance().getViewPeriod();
        if (this.viewPeriod == null) {
            this.viewPeriod = new ViewPeriod(this);
        }
        this.tvStartDate.setText(Utils.getShortDateStr((FragmentActivity) this, this.viewPeriod.startDate));
        this.tvEndDate.setText(Utils.getShortDateStr((FragmentActivity) this, this.viewPeriod.endDate));
        setTitle(" سند قبض  ");
        this.appConfig = AppConfig.getInstance();
        this.appConfig.getBaseUrl();
        this.appConfig.getToken();
        this.appConfig.getAppId();
        this.user = this.appConfig.getUser();
        this._pageSize = this.appConfig.getPageSize();
        this._adapter = new ListBondsAdapter();
        this.recyclerView.setAdapter(this._adapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, 1);
        ((FloatingActionButton) findViewById(C1018R.id.fab)).setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.ui.ListBondsActivity.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Intent intent = new Intent(ListBondsActivity.this, (Class<?>) EntryBondsActivity.class);
                intent.putExtra("mode", "add");
                intent.putExtra("title", "سند قبض جديد");
                ListBondsActivity.this.startActivityForResult(intent, 100);
            }
        });
        this.swipeRefreshLayout = (SwipeRefreshLayout) findViewById(C1018R.id.swipeRefresh);
        this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() { // from class: com.yd.electricecollector.ui.ListBondsActivity.6
            @Override // androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
            public void onRefresh() {
                ListBondsActivity.this.AsyncDownloadStart();
                ListBondsActivity.this._adapter.notifyDataSetChanged();
                ListBondsActivity.this.swipeRefreshLayout.setRefreshing(false);
            }
        });
        this._adapter.setItemClickListener(new OnItemClickCallback<ItemBonds>() { // from class: com.yd.electricecollector.ui.ListBondsActivity.7
            @Override // com.p001yd.electricecollector.network.OnItemClickCallback
            public void onItemClicked(ItemBonds itemBonds) {
            }

            @Override // com.p001yd.electricecollector.network.OnItemClickCallback
            public void onItemClicked(ItemBonds itemBonds, int i) {
            }
        });
        this._adapter.setOnCreateContextMenu(new OnCreateContextMenuCallback() { // from class: com.yd.electricecollector.ui.ListBondsActivity.8
            @Override // com.p001yd.electricecollector.network.OnCreateContextMenuCallback
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo, int i, MenuItem.OnMenuItemClickListener onMenuItemClickListener) {
                HakAccessHelper.getHakAkses("repBondsReciept", ListBondsActivity.this.appConfig.getListHakAccess());
                contextMenu.setHeaderTitle("سند رقم: " + ListBondsActivity.this._adapter.getItems().get(i).getNmstnd());
                contextMenu.add(0, C1018R.id.menu_print, 0, C1018R.string.menu_print).setOnMenuItemClickListener(onMenuItemClickListener);
                if (ListBondsActivity.this.user == null || ListBondsActivity.this.user.getSYS() != 1) {
                    return;
                }
                contextMenu.add(0, C1018R.id.menu_edit, 0, C1018R.string.menu_edit).setOnMenuItemClickListener(onMenuItemClickListener);
            }
        });
        this._adapter.setOnContextMenuItemClickCallback(new OnContextMenuItemClickCallback() { // from class: com.yd.electricecollector.ui.ListBondsActivity$$ExternalSyntheticLambda0
            @Override // com.p001yd.electricecollector.network.OnContextMenuItemClickCallback
            public final void onContextMenuItemClick(MenuItem menuItem, int i) {
                ListBondsActivity.this.m511lambda$onCreate$0$comydelectricecollectoruiListBondsActivity(menuItem, i);
            }
        });
        this.recyclerView.addItemDecoration(dividerItemDecoration);
        this.tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", 0));
        if (this.tokenManager.getToken() == null) {
            Toast.makeText(this, "token not found", 0).show();
        }
        AsyncDownloadStart();
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(C1018R.menu.o_bonds, menu);
        menu.removeItem(C1018R.id.menu_print);
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

    @Override // com.yd.electricecollector.common.ErrorHandler.ErrorCallback
    public void onForbidden(String str) {
        showErrorMessage(str);
    }

    @Override // com.yd.electricecollector.common.ErrorHandler.ErrorCallback
    public void onHttpError(String str) {
        showErrorMessage(str);
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

    @Override // com.yd.electricecollector.common.ErrorHandler.ErrorCallback
    public void onNetworkError(String str) {
        showErrorMessage(str);
    }

    @Override // com.yd.electricecollector.common.ErrorHandler.ErrorCallback
    public void onNotFound(String str) {
        showErrorMessage(str);
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            finish();
            return true;
        }
        switch (menuItem.getItemId()) {
            case C1018R.id.mnuItemNewBond /* 2131362188 */:
                Intent intent = new Intent(this, (Class<?>) EntryBondsActivity.class);
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
        this.tvStartDate.setText(bundle.getString("tvStartDate"));
        this.tvEndDate.setText(bundle.getString("tvEndDate"));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putParcelable("user_info", this.appConfig.getUser());
        bundle.putString("tvStartDate", this.tvStartDate.getText().toString());
        bundle.putString("tvEndDate", this.tvStartDate.getText().toString());
    }

    @Override // com.yd.electricecollector.common.ErrorHandler.ErrorCallback
    public void onServerError(String str) {
        showErrorMessage("خطأ في الخادم: " + str);
    }

    @Override // com.yd.electricecollector.common.ErrorHandler.ErrorCallback
    public void onServiceUnavailable(String str) {
        showErrorMessage(str);
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

    @Override // com.yd.electricecollector.common.ErrorHandler.ErrorCallback
    public void onTimeoutError(String str) {
        showErrorMessage(str);
    }

    @Override // com.yd.electricecollector.common.ErrorHandler.ErrorCallback
    public void onUnauthorized(String str) {
        showErrorMessage(str);
        startActivity(new Intent(this, (Class<?>) LoginActivity.class));
        finish();
    }

    @Override // com.yd.electricecollector.common.ErrorHandler.ErrorCallback
    public void onUnknownError(String str) {
        showErrorMessage(str);
    }

    protected void showErrorPopup(Context context, int i) {
        new AlertDialog.Builder(context).setMessage(i).setTitle(C1018R.string.error).setPositiveButton(C1018R.string.btnOk, (DialogInterface.OnClickListener) null).setCancelable(true).create().show();
    }

    protected void showMessagePopup(Context context, int i) {
        new AlertDialog.Builder(context).setMessage(i).setTitle(C1018R.string.app_name).setPositiveButton(C1018R.string.txtYes, this.barcodeTerminalHostingPageListener).setNegativeButton(C1018R.string.txtNo, this.barcodeTerminalHostingPageListener).setCancelable(true).create().show();
    }
}
