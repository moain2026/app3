package com.p001yd.electricecollector.printer.bluetooth;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.datecs.api.card.FinancialCard;
import com.datecs.api.printer.Printer;
import com.datecs.api.printer.PrinterInformation;
import com.datecs.api.printer.ProtocolAdapter;
import com.ganesh.intermecarabic.Arabic864;
import com.itextpdf.text.pdf.codec.TIFFConstants;
import com.loopj.android.http.AsyncHttpClient;
import com.p001yd.electricecollector.C1018R;
import com.p001yd.electricecollector.TAPreferences;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

/* loaded from: classes4.dex */
public class PrinterActivity extends AppCompatActivity {
    public static final String CONNECTION_STRING = "connection_string";
    private BluetoothConnector mBthConnector;
    private final Thread mConnectThread = new Thread() { // from class: com.yd.electricecollector.printer.bluetooth.PrinterActivity.1
        void connectBth(String str) {
            PrinterActivity.this.setPrinterInfo(C1018R.drawable.help, str);
            try {
                PrinterActivity.this.mBthConnector = BluetoothConnector.getConnector(PrinterActivity.this);
                PrinterActivity.this.mBthConnector.connect(str);
                PrinterActivity.this.mPrinter = getPrinter(PrinterActivity.this.mBthConnector.getInputStream(), PrinterActivity.this.mBthConnector.getOutputStream());
                PrinterActivity.this.mPrinterInfo = getPrinterInfo();
            } catch (IOException e) {
                PrinterActivity.this.error(C1018R.drawable.bluetooth, e.getMessage());
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
                printerInformation = PrinterActivity.this.mPrinter.getInformation();
                PrinterActivity.this.setPrinterInfo(C1018R.drawable.printer, printerInformation.getName());
                return printerInformation;
            } catch (IOException e) {
                e.printStackTrace();
                return printerInformation;
            }
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            String setectedPrinterConnectionString = TAPreferences.getSetectedPrinterConnectionString(PrinterActivity.this);
            PrinterActivity.this.showProgress(C1018R.string.connecting);
            if (!setectedPrinterConnectionString.startsWith("bth://")) {
                throw new IllegalArgumentException("Unsupported connection string");
            }
            connectBth(setectedPrinterConnectionString.substring(6));
            PrinterActivity.this.dismissProgress();
        }
    };
    private final Handler mHandler = new Handler();
    private Printer mPrinter;
    private PrinterInformation mPrinterInfo;
    private ProgressDialog mProgressDialog;

    private void dialog(final int i, final String str, final String str2) {
        this.mHandler.post(new Runnable() { // from class: com.yd.electricecollector.printer.bluetooth.PrinterActivity.11
            @Override // java.lang.Runnable
            public void run() {
                AlertDialog create = new AlertDialog.Builder(PrinterActivity.this).setTitle(str).setMessage(str2).create();
                create.setIcon(i);
                create.show();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dismissProgress() {
        this.mHandler.post(new Runnable() { // from class: com.yd.electricecollector.printer.bluetooth.PrinterActivity.10
            @Override // java.lang.Runnable
            public void run() {
                PrinterActivity.this.mProgressDialog.dismiss();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doPrintBarcode() {
        try {
            this.mPrinter.reset();
            this.mPrinter.setBarcode(1, false, 2, 2, 100);
            this.mPrinter.printBarcode(67, "123456789012");
            this.mPrinter.feedPaper(38);
            this.mPrinter.setBarcode(1, false, 2, 3, 100);
            this.mPrinter.printBarcode(73, "ABCDEF123456");
            this.mPrinter.feedPaper(38);
            this.mPrinter.setBarcode(1, false, 2, 0, 100);
            this.mPrinter.printBarcode(74, "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
            this.mPrinter.feedPaper(38);
            this.mPrinter.setBarcode(1, false, 2, 0, 100);
            this.mPrinter.printQRCode(4, 3, "http://www.datecs.bg");
            this.mPrinter.feedPaper(38);
            this.mPrinter.feedPaper(110);
        } catch (IOException e) {
            error(C1018R.drawable.barcode, getString(C1018R.string.failed_print_barcode) + ". " + e.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doPrintImage() {
        Bitmap decodeResource = BitmapFactory.decodeResource(getResources(), C1018R.drawable.sample);
        int width = decodeResource.getWidth();
        int height = decodeResource.getHeight();
        int[] iArr = new int[width * height];
        decodeResource.getPixels(iArr, 0, width, 0, 0, width, height);
        try {
            this.mPrinter.reset();
            this.mPrinter.printImage(iArr, width, height, 0, true);
            this.mPrinter.feedPaper(110);
        } catch (IOException e) {
            error(C1018R.drawable.image, getString(C1018R.string.failed_print_image) + ". " + e.getMessage());
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doPrintPage() {
        try {
            this.mPrinter.reset();
            this.mPrinter.selectPageMode();
            this.mPrinter.setPageRegion(0, 0, 160, TIFFConstants.TIFFTAG_COLORMAP, 0);
            this.mPrinter.setPageXY(0, 4);
            this.mPrinter.printTaggedText("{reset}{center}{b}PARAGRAPH I{br}");
            this.mPrinter.drawPageRectangle(0, 0, 160, 32, 2);
            this.mPrinter.setPageXY(0, 34);
            this.mPrinter.printTaggedText("{reset}Text printed from left to right, feed to bottom. Starting point in left top corner of the page.{br}");
            this.mPrinter.drawPageFrame(0, 0, 160, TIFFConstants.TIFFTAG_COLORMAP, 1, 1);
            this.mPrinter.setPageRegion(160, 0, 160, TIFFConstants.TIFFTAG_COLORMAP, 3);
            this.mPrinter.setPageXY(0, 4);
            this.mPrinter.printTaggedText("{reset}{center}{b}PARAGRAPH II{br}");
            this.mPrinter.drawPageRectangle(128, 0, 32, TIFFConstants.TIFFTAG_COLORMAP, 2);
            this.mPrinter.setPageXY(0, 34);
            this.mPrinter.printTaggedText("{reset}Text printed from top to bottom, feed to left. Starting point in right top corner of the page.{br}");
            this.mPrinter.drawPageFrame(0, 0, 160, TIFFConstants.TIFFTAG_COLORMAP, 1, 1);
            this.mPrinter.setPageRegion(160, TIFFConstants.TIFFTAG_COLORMAP, 160, TIFFConstants.TIFFTAG_COLORMAP, 2);
            this.mPrinter.setPageXY(0, 4);
            this.mPrinter.printTaggedText("{reset}{center}{b}PARAGRAPH III{br}");
            this.mPrinter.drawPageRectangle(0, TIFFConstants.TIFFTAG_FREEOFFSETS, 160, 32, 2);
            this.mPrinter.setPageXY(0, 34);
            this.mPrinter.printTaggedText("{reset}Text printed from right to left, feed to top. Starting point in right bottom corner of the page.{br}");
            this.mPrinter.drawPageFrame(0, 0, 160, TIFFConstants.TIFFTAG_COLORMAP, 1, 1);
            this.mPrinter.setPageRegion(0, TIFFConstants.TIFFTAG_COLORMAP, 160, TIFFConstants.TIFFTAG_COLORMAP, 1);
            this.mPrinter.setPageXY(0, 4);
            this.mPrinter.printTaggedText("{reset}{center}{b}PARAGRAPH IV{br}");
            this.mPrinter.drawPageRectangle(0, 0, 32, TIFFConstants.TIFFTAG_COLORMAP, 2);
            this.mPrinter.setPageXY(0, 34);
            this.mPrinter.printTaggedText("{reset}Text printed from bottom to top, feed to right. Starting point in left bottom corner of the page.{br}");
            this.mPrinter.drawPageFrame(0, 0, 160, TIFFConstants.TIFFTAG_COLORMAP, 1, 1);
            this.mPrinter.printPage();
            this.mPrinter.selectStandardMode();
            this.mPrinter.feedPaper(110);
        } catch (IOException e) {
            error(C1018R.drawable.page, getString(C1018R.string.failed_print_page) + ". " + e.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doPrintSelfTest() {
        try {
            this.mPrinter.printSelfTest();
        } catch (IOException e) {
            error(C1018R.drawable.selftest, getString(C1018R.string.failed_print_self_test) + ". " + e.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doPrintText() {
        Arabic864 arabic864 = new Arabic864();
        try {
            this.mPrinter.reset();
            this.mPrinter.printTaggedText("{reset}{center}{w}{h}");
            this.mPrinter.printText(arabic864.Convert("سند قبض ", false));
            this.mPrinter.printTaggedText("{br}");
            this.mPrinter.printTaggedText("{br}------------------------{br}\n");
            this.mPrinter.printTaggedText("{reset}1. {b}");
            this.mPrinter.printText(arabic864.Convert("رقم السند ", false));
            this.mPrinter.printTaggedText("{br}");
            this.mPrinter.printTaggedText("{reset}{right}{h}$0.50 A{br}");
            this.mPrinter.printTaggedText("{br}{br}");
            this.mPrinter.feedPaper(110);
            this.mPrinter.flush();
        } catch (IOException e) {
            error(C1018R.drawable.text, getString(C1018R.string.failed_print_text) + ". " + e.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doReadBarcode() {
        String str = null;
        try {
            str = this.mPrinter.readBarcode(AsyncHttpClient.DEFAULT_SOCKET_TIMEOUT);
        } catch (IOException e) {
            error(C1018R.drawable.readbarcode, getString(C1018R.string.read_barcode) + ". " + e.getMessage());
        }
        if (str != null) {
            dialog(C1018R.drawable.readbarcode, getString(C1018R.string.barcode), str);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doReadMagstripe() {
        String[] readCard;
        FinancialCard financialCard = null;
        try {
            if ((this.mPrinterInfo == null || !this.mPrinterInfo.getName().startsWith("CMP-10")) && (readCard = this.mPrinter.readCard(true, true, true, 15000)) != null) {
                StringBuffer stringBuffer = new StringBuffer();
                if (readCard[0] != null && readCard[1] == null && readCard[2] == null) {
                    stringBuffer.append(getString(C1018R.string.no_card_read));
                } else {
                    if (readCard[0] != null) {
                        financialCard = new FinancialCard(readCard[0]);
                    } else if (readCard[1] != null) {
                        financialCard = new FinancialCard(readCard[1]);
                    }
                    if (financialCard != null) {
                        stringBuffer.append(getString(C1018R.string.card_no) + ": " + financialCard.getNumber());
                        stringBuffer.append("\n");
                        stringBuffer.append(getString(C1018R.string.holder) + ": " + financialCard.getName());
                        stringBuffer.append("\n");
                        stringBuffer.append(getString(C1018R.string.exp_date) + ": " + String.format("%02d/%02d", Integer.valueOf(financialCard.getExpiryMonth()), Integer.valueOf(financialCard.getExpiryYear())));
                        stringBuffer.append("\n");
                    }
                    if (readCard[0] != null) {
                        stringBuffer.append("\n");
                        stringBuffer.append(readCard[0]);
                    }
                    if (readCard[1] != null) {
                        stringBuffer.append("\n");
                        stringBuffer.append(readCard[1]);
                    }
                    if (readCard[2] != null) {
                        stringBuffer.append("\n");
                        stringBuffer.append(readCard[2]);
                    }
                }
                dialog(C1018R.drawable.card, getString(C1018R.string.card_info), stringBuffer.toString());
            }
            String[] readCard2 = this.mPrinter.readCard(true, true, false, 15000);
            if (readCard2 != null) {
                StringBuffer stringBuffer2 = new StringBuffer();
                String str = readCard2[0];
                if (readCard2[0] != null) {
                    financialCard = new FinancialCard(readCard2[0]);
                } else if (readCard2[1] != null) {
                    financialCard = new FinancialCard(readCard2[1]);
                }
                if (financialCard != null) {
                    stringBuffer2.append(getString(C1018R.string.card_no) + ": " + financialCard.getNumber());
                    stringBuffer2.append("\n");
                    stringBuffer2.append(getString(C1018R.string.holder) + ": " + financialCard.getName());
                    stringBuffer2.append("\n");
                    stringBuffer2.append(getString(C1018R.string.exp_date) + ": " + String.format("%02d/%02d", Integer.valueOf(financialCard.getExpiryMonth()), Integer.valueOf(financialCard.getExpiryYear())));
                    stringBuffer2.append("\n");
                }
                if (readCard2[0] != null) {
                    stringBuffer2.append("\n");
                    stringBuffer2.append(readCard2[0]);
                }
                if (readCard2[1] != null) {
                    stringBuffer2.append("\n");
                    stringBuffer2.append(readCard2[1]);
                }
                if (readCard2[2] != null) {
                    stringBuffer2.append("\n");
                    stringBuffer2.append(readCard2[2]);
                }
                dialog(C1018R.drawable.card, getString(C1018R.string.card_info), stringBuffer2.toString());
            }
        } catch (IOException e) {
            error(C1018R.drawable.card, getString(C1018R.string.read_card) + ". " + e.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void error(final int i, final String str) {
        this.mHandler.post(new Runnable() { // from class: com.yd.electricecollector.printer.bluetooth.PrinterActivity.12
            @Override // java.lang.Runnable
            public void run() {
                AlertDialog create = new AlertDialog.Builder(PrinterActivity.this).setTitle("Error").setMessage(str).create();
                create.setIcon(i);
                create.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.yd.electricecollector.printer.bluetooth.PrinterActivity.12.1
                    @Override // android.content.DialogInterface.OnDismissListener
                    public void onDismiss(DialogInterface dialogInterface) {
                        PrinterActivity.this.finish();
                    }
                });
                create.show();
            }
        });
    }

    private void printText() throws IOException {
        byte[] Convert = new Arabic864().Convert("اللغة العربية", false);
        this.mPrinter.reset();
        byte[] bytes = " السلام عليكم".getBytes(Charset.forName("Windows-1256"));
        String str = new String(new byte[]{27, 116, 22});
        this.mPrinter.printText(Convert);
        this.mPrinter.printText(str + bytes);
        this.mPrinter.feedPaper(100);
        this.mPrinter.flush();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setPrinterInfo(final int i, final String str) {
        this.mHandler.post(new Runnable() { // from class: com.yd.electricecollector.printer.bluetooth.PrinterActivity.13
            @Override // java.lang.Runnable
            public void run() {
                ((ImageView) PrinterActivity.this.findViewById(C1018R.id.icon)).setImageResource(i);
                ((TextView) PrinterActivity.this.findViewById(C1018R.id.name)).setText(str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showProgress(int i) {
        showProgress(getString(i));
    }

    private void showProgress(final String str) {
        this.mHandler.post(new Runnable() { // from class: com.yd.electricecollector.printer.bluetooth.PrinterActivity.9
            @Override // java.lang.Runnable
            public void run() {
                PrinterActivity.this.mProgressDialog = ProgressDialog.show(PrinterActivity.this, PrinterActivity.this.getString(C1018R.string.please_wait), str, true);
            }
        });
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(C1018R.layout.printer);
        findViewById(C1018R.id.print_self_test).setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.printer.bluetooth.PrinterActivity.2
            /* JADX WARN: Type inference failed for: r0v0, types: [com.yd.electricecollector.printer.bluetooth.PrinterActivity$2$1] */
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                new Thread() { // from class: com.yd.electricecollector.printer.bluetooth.PrinterActivity.2.1
                    @Override // java.lang.Thread, java.lang.Runnable
                    public void run() {
                        PrinterActivity.this.showProgress(C1018R.string.printing_self_test);
                        PrinterActivity.this.doPrintSelfTest();
                        PrinterActivity.this.dismissProgress();
                    }
                }.start();
            }
        });
        findViewById(C1018R.id.print_text).setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.printer.bluetooth.PrinterActivity.3
            /* JADX WARN: Type inference failed for: r0v0, types: [com.yd.electricecollector.printer.bluetooth.PrinterActivity$3$1] */
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                new Thread() { // from class: com.yd.electricecollector.printer.bluetooth.PrinterActivity.3.1
                    @Override // java.lang.Thread, java.lang.Runnable
                    public void run() {
                        PrinterActivity.this.showProgress(C1018R.string.printing_text);
                        PrinterActivity.this.doPrintText();
                        PrinterActivity.this.dismissProgress();
                    }
                }.start();
            }
        });
        findViewById(C1018R.id.print_image).setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.printer.bluetooth.PrinterActivity.4
            /* JADX WARN: Type inference failed for: r0v0, types: [com.yd.electricecollector.printer.bluetooth.PrinterActivity$4$1] */
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                new Thread() { // from class: com.yd.electricecollector.printer.bluetooth.PrinterActivity.4.1
                    @Override // java.lang.Thread, java.lang.Runnable
                    public void run() {
                        PrinterActivity.this.showProgress(C1018R.string.printing_image);
                        PrinterActivity.this.doPrintImage();
                        PrinterActivity.this.dismissProgress();
                    }
                }.start();
            }
        });
        findViewById(C1018R.id.print_page).setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.printer.bluetooth.PrinterActivity.5
            /* JADX WARN: Type inference failed for: r0v0, types: [com.yd.electricecollector.printer.bluetooth.PrinterActivity$5$1] */
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                new Thread() { // from class: com.yd.electricecollector.printer.bluetooth.PrinterActivity.5.1
                    @Override // java.lang.Thread, java.lang.Runnable
                    public void run() {
                        PrinterActivity.this.showProgress(C1018R.string.printing_page);
                        PrinterActivity.this.doPrintPage();
                        PrinterActivity.this.dismissProgress();
                    }
                }.start();
            }
        });
        findViewById(C1018R.id.print_barcode).setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.printer.bluetooth.PrinterActivity.6
            /* JADX WARN: Type inference failed for: r0v0, types: [com.yd.electricecollector.printer.bluetooth.PrinterActivity$6$1] */
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                new Thread() { // from class: com.yd.electricecollector.printer.bluetooth.PrinterActivity.6.1
                    @Override // java.lang.Thread, java.lang.Runnable
                    public void run() {
                        PrinterActivity.this.showProgress(C1018R.string.printing_barcode);
                        PrinterActivity.this.doPrintBarcode();
                        PrinterActivity.this.dismissProgress();
                    }
                }.start();
            }
        });
        findViewById(C1018R.id.read_card).setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.printer.bluetooth.PrinterActivity.7
            /* JADX WARN: Type inference failed for: r0v0, types: [com.yd.electricecollector.printer.bluetooth.PrinterActivity$7$1] */
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                new Thread() { // from class: com.yd.electricecollector.printer.bluetooth.PrinterActivity.7.1
                    @Override // java.lang.Thread, java.lang.Runnable
                    public void run() {
                        PrinterActivity.this.showProgress(C1018R.string.reading_card);
                        PrinterActivity.this.doReadMagstripe();
                        PrinterActivity.this.dismissProgress();
                    }
                }.start();
            }
        });
        findViewById(C1018R.id.read_barcode).setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.printer.bluetooth.PrinterActivity.8
            /* JADX WARN: Type inference failed for: r0v0, types: [com.yd.electricecollector.printer.bluetooth.PrinterActivity$8$1] */
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                new Thread() { // from class: com.yd.electricecollector.printer.bluetooth.PrinterActivity.8.1
                    @Override // java.lang.Thread, java.lang.Runnable
                    public void run() {
                        PrinterActivity.this.showProgress(C1018R.string.read_barcode);
                        PrinterActivity.this.doReadBarcode();
                        PrinterActivity.this.dismissProgress();
                    }
                }.start();
            }
        });
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onStart() {
        super.onStart();
        if (this.mBthConnector == null) {
            this.mConnectThread.start();
        }
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onStop() {
        super.onStop();
        if (this.mBthConnector != null) {
            try {
                this.mBthConnector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
