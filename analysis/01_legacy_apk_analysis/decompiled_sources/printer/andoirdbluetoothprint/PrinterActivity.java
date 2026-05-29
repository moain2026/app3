package com.p001yd.electricecollector.printer.andoirdbluetoothprint;

import android.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.adpa.printer.wrapper.DatecsStyle;
import com.adpa.printer.wrapper.DatecsWrapper;
import com.datecs.api.biometric.TouchChip;
import com.datecs.api.biometric.TouchChipException;
import com.datecs.api.card.FinancialCard;
import com.datecs.api.emsr.EMSR;
import com.datecs.api.printer.Printer;
import com.datecs.api.printer.PrinterInformation;
import com.datecs.api.printer.ProtocolAdapter;
import com.datecs.api.rfid.ContactlessCard;
import com.datecs.api.rfid.RC663;
import com.datecs.api.universalreader.UniversalReader;
import com.ganesh.intermecarabic.Arabic864;
import com.itextpdf.text.pdf.codec.TIFFConstants;
import com.p001yd.electricecollector.C1018R;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/* loaded from: classes11.dex */
public class PrinterActivity extends Activity {
    public static final int CONNECTION_STRING = 0;
    private static final int DEFAULT_NETWORK_PORT = 9100;
    private static final String LOG_TAG = "PrinterSample";
    private static final int REQUEST_GET_DEVICE = 0;
    private BluetoothSocket mBtSocket;
    private EMSR mEMSR;
    private Socket mNetSocket;
    private Printer mPrinter;
    private ProtocolAdapter.Channel mPrinterChannel;
    private ProtocolAdapter mProtocolAdapter;
    private RC663 mRC663;
    private ProtocolAdapter.Channel mUniversalChannel;
    private String m_Text = "";

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes11.dex */
    public interface PrinterRunnable {
        void run(ProgressDialog progressDialog, Printer printer) throws IOException;
    }

    private synchronized void closeActiveConnection() {
        closePrinterConnection();
        closeBluetoothConnection();
    }

    private synchronized void closeBluetoothConnection() {
        BluetoothSocket bluetoothSocket = this.mBtSocket;
        this.mBtSocket = null;
        if (bluetoothSocket != null) {
            Log.d(LOG_TAG, "Close Bluetooth socket");
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void closePrinterConnection() {
        if (this.mRC663 != null) {
            try {
                this.mRC663.disable();
            } catch (IOException e) {
            }
            this.mRC663.close();
        }
        if (this.mEMSR != null) {
            this.mEMSR.close();
        }
        if (this.mPrinter != null) {
            this.mPrinter.close();
        }
        if (this.mProtocolAdapter != null) {
            this.mProtocolAdapter.close();
        }
    }

    private void deleteAllIdentities() {
        Log.d(LOG_TAG, "Delete all identities");
        runTask(new PrinterRunnable() { // from class: com.yd.electricecollector.printer.andoirdbluetoothprint.PrinterActivity.33
            @Override // com.yd.electricecollector.printer.andoirdbluetoothprint.PrinterActivity.PrinterRunnable
            public void run(ProgressDialog progressDialog, Printer printer) throws IOException {
                TouchChip touchChip = printer.getTouchChip();
                try {
                    for (int i : touchChip.listSlots()) {
                        touchChip.deleteIdentity(i);
                    }
                } catch (TouchChipException e) {
                    PrinterActivity.this.error("Failed to delete fingerprints: " + e.getMessage());
                }
            }
        }, C1018R.string.print_barcode);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dialog(final int i, final String str, final String str2) {
        runOnUiThread(new Runnable() { // from class: com.yd.electricecollector.printer.andoirdbluetoothprint.PrinterActivity.11
            @Override // java.lang.Runnable
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(PrinterActivity.this);
                builder.setIcon(i);
                builder.setTitle(str);
                builder.setMessage(str2);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() { // from class: com.yd.electricecollector.printer.andoirdbluetoothprint.PrinterActivity.11.1
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialogInterface, int i2) {
                        dialogInterface.dismiss();
                    }
                });
                builder.create().show();
            }
        });
    }

    private void enrolNewIdentity(final String str) {
        Log.d(LOG_TAG, "Enrol new identity");
        runTask(new PrinterRunnable() { // from class: com.yd.electricecollector.printer.andoirdbluetoothprint.PrinterActivity.32
            @Override // com.yd.electricecollector.printer.andoirdbluetoothprint.PrinterActivity.PrinterRunnable
            public void run(ProgressDialog progressDialog, Printer printer) throws IOException {
                try {
                    printer.getTouchChip().enrolIdentity(str);
                } catch (TouchChipException e) {
                    PrinterActivity.this.error("Failed to enrol identity: " + e.getMessage());
                }
            }
        }, C1018R.string.print_barcode);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void error(final String str) {
        Log.w(LOG_TAG, str);
        runOnUiThread(new Runnable() { // from class: com.yd.electricecollector.printer.andoirdbluetoothprint.PrinterActivity.10
            @Override // java.lang.Runnable
            public void run() {
                Toast.makeText(PrinterActivity.this.getApplicationContext(), str, 1).show();
            }
        });
    }

    private void establishBluetoothConnection(final String str) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(C1018R.string.welcome));
        progressDialog.setMessage(getString(C1018R.string.connecting));
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        final BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        new Thread(new Runnable() { // from class: com.yd.electricecollector.printer.andoirdbluetoothprint.PrinterActivity.19
            @Override // java.lang.Runnable
            public void run() {
                Log.d(PrinterActivity.LOG_TAG, "Connecting to " + str + "...");
                defaultAdapter.cancelDiscovery();
                try {
                    BluetoothSocket createRfcommSocketToServiceRecord = defaultAdapter.getRemoteDevice(str).createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                    createRfcommSocketToServiceRecord.connect();
                    PrinterActivity.this.mBtSocket = createRfcommSocketToServiceRecord;
                    PrinterActivity.this.initPrinter(PrinterActivity.this.mBtSocket.getInputStream(), PrinterActivity.this.mBtSocket.getOutputStream());
                } catch (IOException e) {
                    PrinterActivity.this.error("FAILED to connect: " + e.getMessage());
                    PrinterActivity.this.waitForConnection();
                } catch (IOException e2) {
                    PrinterActivity.this.error("FAILED to initiallize: " + e2.getMessage());
                } finally {
                    progressDialog.dismiss();
                }
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void printBarcode() {
        Log.d(LOG_TAG, "Print Barcode");
        runTask(new PrinterRunnable() { // from class: com.yd.electricecollector.printer.andoirdbluetoothprint.PrinterActivity.28
            @Override // com.yd.electricecollector.printer.andoirdbluetoothprint.PrinterActivity.PrinterRunnable
            public void run(ProgressDialog progressDialog, Printer printer) throws IOException {
                printer.reset();
                printer.setBarcode(1, false, 2, 2, 100);
                printer.printBarcode(75, "123456789012345678901234");
                printer.feedPaper(38);
                printer.setBarcode(1, false, 2, 2, 100);
                printer.printBarcode(67, "123456789012");
                printer.feedPaper(38);
                printer.setBarcode(1, false, 2, 3, 100);
                printer.printBarcode(73, "ABCDEF123456");
                printer.feedPaper(38);
                printer.setBarcode(1, false, 2, 0, 100);
                printer.printBarcode(74, "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
                printer.feedPaper(38);
                printer.setBarcode(1, false, 2, 0, 100);
                printer.printQRCode(4, 3, "http://www.datecs.bg");
                printer.feedPaper(38);
                printer.feedPaper(110);
                printer.flush();
            }
        }, C1018R.string.print_barcode);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void printCustomText(String str) {
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
        }
    }

    private void printCustomText1(List<String> list) {
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
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void printImage() {
        Log.d(LOG_TAG, "Print Image");
        runTask(new PrinterRunnable() { // from class: com.yd.electricecollector.printer.andoirdbluetoothprint.PrinterActivity.26
            @Override // com.yd.electricecollector.printer.andoirdbluetoothprint.PrinterActivity.PrinterRunnable
            public void run(ProgressDialog progressDialog, Printer printer) throws IOException {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inScaled = false;
                Bitmap decodeStream = BitmapFactory.decodeStream(PrinterActivity.this.getApplicationContext().getAssets().open("sample.png"), null, options);
                int width = decodeStream.getWidth();
                int height = decodeStream.getHeight();
                int[] iArr = new int[width * height];
                decodeStream.getPixels(iArr, 0, width, 0, 0, width, height);
                decodeStream.recycle();
                printer.reset();
                printer.printCompressedImage(iArr, width, height, 1, true);
                printer.feedPaper(110);
                printer.flush();
            }
        }, C1018R.string.print_image);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void printPage() {
        Log.d(LOG_TAG, "Print Page");
        runTask(new PrinterRunnable() { // from class: com.yd.electricecollector.printer.andoirdbluetoothprint.PrinterActivity.27
            @Override // com.yd.electricecollector.printer.andoirdbluetoothprint.PrinterActivity.PrinterRunnable
            public void run(ProgressDialog progressDialog, Printer printer) throws IOException {
                if (!printer.getInformation().isPageSupported()) {
                    PrinterActivity.this.dialog(C1018R.drawable.ic_setting_d_icon, PrinterActivity.this.getString(C1018R.string.warning), PrinterActivity.this.getString(C1018R.string.print_page));
                    return;
                }
                printer.reset();
                printer.selectPageMode();
                printer.setPageRegion(0, 0, 160, TIFFConstants.TIFFTAG_COLORMAP, 0);
                printer.setPageXY(0, 4);
                printer.printTaggedText("{reset}{center}{b}PARAGRAPH I{br}");
                printer.drawPageRectangle(0, 0, 160, 32, 2);
                printer.setPageXY(0, 34);
                printer.printTaggedText("{reset}Text printed from left to right, feed to bottom. Starting point in left top corner of the page.{br}");
                printer.drawPageFrame(0, 0, 160, TIFFConstants.TIFFTAG_COLORMAP, 1, 1);
                printer.setPageRegion(160, 0, 160, TIFFConstants.TIFFTAG_COLORMAP, 3);
                printer.setPageXY(0, 4);
                printer.printTaggedText("{reset}{center}{b}PARAGRAPH II{br}");
                printer.drawPageRectangle(128, 0, 32, TIFFConstants.TIFFTAG_COLORMAP, 2);
                printer.setPageXY(0, 34);
                printer.printTaggedText("{reset}Text printed from top to bottom, feed to left. Starting point in right top corner of the page.{br}");
                printer.drawPageFrame(0, 0, 160, TIFFConstants.TIFFTAG_COLORMAP, 1, 1);
                printer.setPageRegion(160, TIFFConstants.TIFFTAG_COLORMAP, 160, TIFFConstants.TIFFTAG_COLORMAP, 2);
                printer.setPageXY(0, 4);
                printer.printTaggedText("{reset}{center}{b}PARAGRAPH III{br}");
                printer.drawPageRectangle(0, TIFFConstants.TIFFTAG_FREEOFFSETS, 160, 32, 2);
                printer.setPageXY(0, 34);
                printer.printTaggedText("{reset}Text printed from right to left, feed to top. Starting point in right bottom corner of the page.{br}");
                printer.drawPageFrame(0, 0, 160, TIFFConstants.TIFFTAG_COLORMAP, 1, 1);
                printer.setPageRegion(0, TIFFConstants.TIFFTAG_COLORMAP, 160, TIFFConstants.TIFFTAG_COLORMAP, 1);
                printer.setPageXY(0, 4);
                printer.printTaggedText("{reset}{center}{b}PARAGRAPH IV{br}");
                printer.drawPageRectangle(0, 0, 32, TIFFConstants.TIFFTAG_COLORMAP, 2);
                printer.setPageXY(0, 34);
                printer.printTaggedText("{reset}Text printed from bottom to top, feed to right. Starting point in left bottom corner of the page.{br}");
                printer.drawPageFrame(0, 0, 160, TIFFConstants.TIFFTAG_COLORMAP, 1, 1);
                printer.printPage();
                printer.selectStandardMode();
                printer.feedPaper(110);
                printer.flush();
            }
        }, C1018R.string.print_page);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void printSelfTest() {
        Log.d(LOG_TAG, "Print Self Test");
        runTask(new PrinterRunnable() { // from class: com.yd.electricecollector.printer.andoirdbluetoothprint.PrinterActivity.21
            @Override // com.yd.electricecollector.printer.andoirdbluetoothprint.PrinterActivity.PrinterRunnable
            public void run(ProgressDialog progressDialog, Printer printer) throws IOException {
                printer.printSelfTest();
                printer.flush();
            }
        }, C1018R.string.failed_print_self_test);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void printText() {
        Log.d(LOG_TAG, "Print Text");
        runTask(new PrinterRunnable() { // from class: com.yd.electricecollector.printer.andoirdbluetoothprint.PrinterActivity.23
            @Override // com.yd.electricecollector.printer.andoirdbluetoothprint.PrinterActivity.PrinterRunnable
            public void run(ProgressDialog progressDialog, Printer printer) throws IOException {
                DatecsWrapper datecsWrapper = new DatecsWrapper(printer, DatecsWrapper.PrinterType.DPP450);
                printer.reset();
                ArrayList arrayList = new ArrayList();
                arrayList.add(DatecsStyle.RESET);
                arrayList.add(DatecsStyle.CENTER);
                arrayList.add(DatecsStyle.BOLD);
                datecsWrapper.setPrinterStyle(arrayList);
                datecsWrapper.printPersianText("شرکت پخش سایه سمن (سهامی خاص)");
                arrayList.clear();
                arrayList.add(DatecsStyle.BREAK);
                arrayList.add(DatecsStyle.RESET);
                arrayList.add(DatecsStyle.CENTER);
                arrayList.add(DatecsStyle.BOLD);
                datecsWrapper.setPrinterStyle(arrayList);
                datecsWrapper.printPersianText("نماینده انحصاری عالی فرد، شیوا، نستله و ردبول");
                arrayList.clear();
                datecsWrapper.setPrinterStyle("{br}{br}{reset}{center}{b}");
                datecsWrapper.printPersianText("شماره درخواست: 101033");
                datecsWrapper.setPrinterStyle("{reset}{br}{b}");
                datecsWrapper.printPersianText("----------------------------------------------------");
                datecsWrapper.setPrinterStyle("{br}");
                printer.feedPaper(100);
                printer.flush();
            }
        }, C1018R.string.print_text);
    }

    private void printText1() {
        runTask(new PrinterRunnable() { // from class: com.yd.electricecollector.printer.andoirdbluetoothprint.PrinterActivity.25
            @Override // com.yd.electricecollector.printer.andoirdbluetoothprint.PrinterActivity.PrinterRunnable
            public void run(ProgressDialog progressDialog, Printer printer) throws IOException {
                DatecsWrapper datecsWrapper = new DatecsWrapper(printer, DatecsWrapper.PrinterType.DPP250);
                new String();
                printer.reset();
                ArrayList arrayList = new ArrayList();
                arrayList.add(DatecsStyle.BOLD);
                datecsWrapper.setPrinterStyle(arrayList);
                datecsWrapper.printPersianText("آدرس دفتر و پخش مستقیم هورکا");
                arrayList.clear();
                datecsWrapper.setPrinterStyle("{br}{s}");
                datecsWrapper.setPrinterStyle(arrayList);
                datecsWrapper.printPersianText("آدرس دفتر و پخش مستقیم هورکا");
                printer.feedPaper(70);
                printer.flush();
            }
        }, C1018R.string.print_text);
    }

    private void printText2() {
        runTask(new PrinterRunnable() { // from class: com.yd.electricecollector.printer.andoirdbluetoothprint.PrinterActivity.24
            @Override // com.yd.electricecollector.printer.andoirdbluetoothprint.PrinterActivity.PrinterRunnable
            public void run(ProgressDialog progressDialog, Printer printer) throws IOException {
                DatecsWrapper datecsWrapper = new DatecsWrapper(printer, DatecsWrapper.PrinterType.DPP450);
                new String();
                printer.reset();
                ArrayList arrayList = new ArrayList();
                arrayList.add("امروزه افراد زیادی از واژه پرداز محبوب شرکت مایکروسافت یعنی “واژه پرداز مایکروسافت ورد” استفاده میکنند، این نرم\u200cافزار در میان کاربران فارسی زبان نیز از اهمیت ویژه\u200cای برخوردار است و خیلی از کاربران ویندوز اعم از کاربران حرفه\u200cای و آماتور از این برنامه برای ایجاد و ویرایش اسناد متنی خود استفاده میکند، این ابزار با امکانات و ویژگی\u200cهای منحصر به فرد خود کاربران زیادی را جذب کرده است و امکان ویرایش متون به اکثر زبان\u200cهای رایج دنیا در آن وجود دارد که زبان فارسی نیز یکی از آن\u200cهاست، در ادامه با همیار آی\u200cتی همراه باشید تا با هم نحوه نگارش صحیح جملات فارسی را در نرم\u200cافزار مایکروسافت ورد مرور کنیم.");
                arrayList.add("توجه: این کار تمام جملات شما را به صورت راست\u200cچین نمایش میدهد، برای ارائه یک متن مرتب بهتر است Alignment را در حالت Justify قرار دهید.");
                arrayList.add("یکی از راه\u200cهای تنظیم فونت استفاده از منوی Font در تب Home میباشد ولی اگر بخواهید فونت خاصی را به کل نوشته اختصاص دهید (به صورت مجزا برای کلمات فارسی و انگلیسی) باید طبق روش زیر عمل کنید.\nمطابق تصویر زیر بر روی علامت کوچکی که در گوشه\u200cی منوی Font قرار دارد کلیک کنید.");
                arrayList.add("شما میتوانید با هر نوع فونتی که تمایل داشته باشید متن خود را بنویسید ولی اگر از یک فونت مناسب که برای زبان فارسی بهینه شده استفاده کنید زیبایی و خوانایی متن شما دو چندان خواهد شد، پس همواره این نکته را به یاد داشته باشید، معمولا فونت\u200cهایی که با حروفی مانند B- یا IR- و Fa- آغاز میشوند مناسب نگارش فارسی هستند (البته استثنائاتی نیز در اینجا وجود دارد)");
                arrayList.add("سعی کنید همیشه و در محل مناسب از علائم نگارشی استفاده کنید تا متن شما راحت\u200cتر قابل خواندن باشد، اجزای اصلی علائم نگارشی در زبان پارسی شامل [\u200c؟\u200c!\u200c. ،: ؛ ” ” () … ] میشوند، البته نحوه استفاده از آن\u200cها نیز اصول خاصی دارد که در ادامه به آن\u200cها می\u200cپردازیم.");
                arrayList.add("پس از اینکه جمله\u200cی مورد نظر خود را نوشتید در انتهای جمله و بدون درج هیچ\u200cگونه فاصله\u200cای علامت مناسب را قرار داده و سپس یک فاصله (Space) درج کنید.\nالگوی صحیح استفاده از این علائم به این صورت است:\n… [عبارت] [علائم نگارشی] [فاصله] [عبارت] …");
                arrayList.add("شیوه نادرست:\nعبارت قبل( عبارت میان پرانتز  )عبارت بعد\n[  عبارت داخل کروشه  ]عبارت بعد\nعبارت”  داخل گیومه  “");
                arrayList.add("شیوه صحیح:\nعبارت قبل (عبارت میان پرانتز) عبارت بعد\n[عبارت داخل کروشه] عبارت بعد\nعبارت “داخل گیومه”");
                arrayList.add("استفاده از علائم ساده\u200cی ریاضی\nیکی دیگر از نشانه\u200cهایی که به استفاده از آن\u200cها احتیاج پیدا خواهید کرد علامت\u200cهای ساده\u200cی ریاضی مانند: + – *  / ٪ هستند، به یاد داشته باشید علامت درصد باید به عدد قبلی خود چسبیده باشد (بدون فاصله) ولی قبل و بعد از علامت\u200cهای +، – و … فاصله ایجاد کنید.\nنحوه\u200cی صحیح به این صورت است:\n۸۵٪\n۲ + ۳\nShift + Enter\nرعایت نکات بالا باعث میشود جملات تایپ شده در رایانه به صورت صحیح در تمام صفحات نمایش داده شوند (چراکه کامپیوتر با هر فاصله\u200cای کلمه\u200cی قبل و بعد را دو کلمه\u200cی واحد و مجزا به حساب می\u200cآورد و این امکان وجود دارد که بخشی از کلمه یا علامت آن به سطر بعدی منتقل شود، همچنین توجه داشته باشید که نیم\u200cفاصله نیز به همین صورت عمل میکند، اگر با نیم فاصله آشنا نیستید این آموزش را ببینید: آموزش تصویری درج نیم\u200cفاصله در مایکروسافت ورد)");
                arrayList.add("برای وارد كردن اعداد كسری مانند ¼، ½ و ¾ كه در كیبوردهای كامپیوتر دیده نمی شوند می توان به ترتیب كدهای Alt0188, Alt0189, Alt0190 را به كار برد. برای تایپ اعداد حتماً باید از قسمت Numeric Pad كه در سمت راست كیبورد قرار گرفته است استفاده شود.");
                arrayList.add("13) جابجایی متن: با ترفند زیر به راه حلی راحت و سریع برای كپی و یا جابجایی قسمتی از متن دست خواهید یافت: ابتدا متن و یا گرافیكی را كه تصمیم به حركت آن دارید علامت گذاری كنید. صفحه تصویر را با كمك حاشیه\u200cهای آن كه در سمت راست و گوشه قابل حركتند. به قدری جابجا كنید كه مكانی را كه می\u200cخواهید عنصر مربوطه به آن اضافه شود كاملا در دید قرار بگیرد. سپس برای جابجایی دكمه <Ctrl> و برای كپی كردن تركیب دكمه\u200cهای <Ctrl>-<Shift> را كلیك كرده و با دكمه راست موش بر روی مقصد كلیك كنید.");
                arrayList.add("15) بایگانی فایل : اینكه نرم\u200cافزار Word چه پوشه\u200cای را به صورت استاندارد در زمان استفاده از گزینه\u200cهای File->Open یا File->Save نشان می\u200cدهد می\u200cتوانید شخصا تعیین كنید تنظیمات مربوطه را می\u200cتوانید در Tools->Options->File locations->Documents بیابید. اینكه فایل\u200cهایی را كه شخصا درست كرده و به صورت الگو در آورده\u200cاید در كجا ثبت می\u200cشوند، نیز در همان بخش، قسمت User templates قابل مشاهده\u200cاند. نرم\u200cافزار Word هنگامی كه شما برای ذخیره نوع فایلی DOT را انتخاب كنید از این دایركتوری استفاده می\u200cكند.\nتوسط دكمه Modify در همان بخش می\u200cتوانید تنظیمات مربوطه را تغییر داده و تعیین كنیدكه برای مثال\u200c فایل\u200cهای DOT و DOC در كجا ذخیره شوند. \n");
                arrayList.add(" نوشته شده توسط تيم توسعه سيفا\n دسته: main-contents\n منتشر شده در 05 شهریور 1394\n بازدید: 1145");
                arrayList.add("جهت ارائه هرگونه پیشنهادات و انتقادات در مورد این سایت و کدهای خدماتی  118 - 20119 - 191 - 192 - 20126   میتوانید با شماره تلفن 20196 تماس بگیرید .");
                arrayList.add("1. تاریخ را به فرمت تاریخ میلادی بنویسیم ولی در قسمت format cell>custom فرمت ان را به صورت yy/mm/ddنوشت در این حالت تاریخ 21/05/1987 به صورت 21/05/87دیده میشوددراین حالت تاریخها به راحتی مقایسه میشوند و فرمت اطلاعات هم تاریخ هست و لی از نظر محاسبات روی تاریخ مشکل داریم مثلا ما تاریخ 31/03/87در تاریخ شمسی داریم ولی در تاریخ میلادی این تاریخ وجود ندارد چون ماه 3 انها 31 روزه نمیباشد و مشکل دوم اگر از توابع فاصله بین 2 تاریخ استفاده کنیم جواب با خطائ روبرو میشود.");
                arrayList.add("یک باگ جالب در مورد فرمول dayweek هست. این فرمول قرار است روز هفته یک تاریخ مشخص را برگرداند. طبق بررسی من، از امروز به عقب تا تاریخ 1380/10/11 (سه شنبه) این فرمول درست کار می کند ولی روز قبلش را اشتباه نشان می دهد");
                arrayList.add("شماره درخواست: 101033");
                arrayList.add("کیلومتر 20 جاده قدیم کرج-شهر قدس-بلوار انقلاب-خ رازی-پ 40-کدپستی 6816877889");
                arrayList.add("تلفن: 021-46467878 ، 021-98743200");
                arrayList.add("فکس: 021-09097676");
                arrayList.add("شماره ثبت/شماره ملی: 115361");
                arrayList.add("کد پستی 10 رقمی: 1399837711");
                arrayList.add("تلفن:88800892(تلفن شکایات و پیشنهادات: 88917049)");
                arrayList.add("تاریخ ویزیت: 1395/07/06              نوع پرداخت : نقدی");
                arrayList.add("نام خریدار: پوراحمدی، (مسجد سجاد) کد خریدار: 2011574");
                arrayList.add("نام فروشنده: 20725- بهمن حسنی");
                arrayList.add("ردیف |     شرح کالا      |    تعداد   |   بهای کل    |   تخفیفات | اضافات |");
                arrayList.add("1  |   شربت پرتقال14   |   2   |  1392528  |  0)0.0(  | 125326 |");
                arrayList.add("============================================================================");
                arrayList.add("جمع کل: 278560056 جمع اضافات: 250652 جمع تخفیفات: 0.0");
                arrayList.add("2 || شربت پرتقال13      2 || 139538  || 0(12.346%)  || 125326");
                StringBuffer stringBuffer = new StringBuffer();
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    stringBuffer.append((String) it.next());
                }
                datecsWrapper.printPersianText(stringBuffer.toString());
                printer.feedPaper(20);
                printer.flush();
            }
        }, C1018R.string.print_text);
    }

    private void printTextCopy() {
        Log.d(LOG_TAG, "Print Text");
        runTask(new PrinterRunnable() { // from class: com.yd.electricecollector.printer.andoirdbluetoothprint.PrinterActivity.22
            @Override // com.yd.electricecollector.printer.andoirdbluetoothprint.PrinterActivity.PrinterRunnable
            public void run(ProgressDialog progressDialog, Printer printer) throws IOException {
                printer.reset();
                printer.printTaggedText("{reset}{center}{b}");
                printer.printArabicText(24, "شرکت آرمان پرداز آرنا");
                printer.printTaggedText("{br}{br}{reset}{right}{b}");
                printer.printArabicText(24, "ارائه دهنده:");
                printer.printTaggedText("{br}{br}{right}");
                printer.printArabicText(24, "1. چاپگرهاي حرارتي");
                printer.printTaggedText("{br}{s}{i}{right}");
                printer.printArabicText(24, "2. صندوق هاي فروشگاهي هوشمند");
                printer.printTaggedText("{br}------------------------{br}");
                printer.printArabicText(24, "ا آ ب پ ت ث ج چ ح خ د ذ ر ز ژ س ش ص ض ط ظ ع غ ف ق ک گ ل م ن و ه ي  ِ  ْ ّ ُ ً َ ـ » « ، ؛ ؟ هٔ ي أ ؤ ئ ء ٪ ٫ ٬ 0 1 2 3 4 5 6 7 8 9 ي ي | ئ ئ  | ي ي )(");
                printer.printTaggedText("{br}{b}------------------------{br}");
                printer.printArabicText(24, "آب است، ولي کم است)در مصرف آب صرفه جويي کنيم(");
                printer.printTaggedText("{br}");
                printer.printArabicText(24, " با استفاده از چابگرهاي حرارتي داتــــــکس قابل حمل تجربه سرويس دهي سريع را تجربه کنيد.");
                printer.printTaggedText("{br}{center}");
                printer.printArabicText(24, "]5931-دي ماه[");
                printer.printTaggedText("{br}{b}************************{br}{center}{b}{u}");
                printer.printArabicText(24, "با سپـــــــاس");
                printer.printTaggedText("{br}{br}");
                printer.feedPaper(110);
                printer.flush();
            }
        }, C1018R.string.print_text);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void readBarcode(final int i) {
        Log.d(LOG_TAG, "Read Barcode");
        runTask(new PrinterRunnable() { // from class: com.yd.electricecollector.printer.andoirdbluetoothprint.PrinterActivity.31
            @Override // com.yd.electricecollector.printer.andoirdbluetoothprint.PrinterActivity.PrinterRunnable
            public void run(ProgressDialog progressDialog, Printer printer) throws IOException {
                String readBarcode = printer.readBarcode(i);
                if (readBarcode != null) {
                    PrinterActivity.this.dialog(C1018R.drawable.ic_delete, PrinterActivity.this.getString(C1018R.string.barcode), readBarcode);
                }
            }
        }, C1018R.string.print_barcode);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void readCard() {
        Log.d(LOG_TAG, "Read card");
        runTask(new PrinterRunnable() { // from class: com.yd.electricecollector.printer.andoirdbluetoothprint.PrinterActivity.29
            @Override // com.yd.electricecollector.printer.andoirdbluetoothprint.PrinterActivity.PrinterRunnable
            public void run(ProgressDialog progressDialog, Printer printer) throws IOException {
                PrinterInformation information = printer.getInformation();
                FinancialCard financialCard = null;
                Printer.setDebug(true);
                String[] readCard = information.getName().startsWith("CMP-10") ? printer.readCard(true, true, false, 15000) : printer.readCard(true, true, true, 15000);
                if (readCard != null) {
                    StringBuffer stringBuffer = new StringBuffer();
                    if (readCard[0] == null && readCard[1] == null && readCard[2] == null) {
                        stringBuffer.append(PrinterActivity.this.getString(C1018R.string.no_card_read));
                    } else {
                        if (readCard[0] != null) {
                            financialCard = new FinancialCard(readCard[0]);
                        } else if (readCard[1] != null) {
                            financialCard = new FinancialCard(readCard[1]);
                        }
                        if (financialCard != null) {
                            stringBuffer.append(PrinterActivity.this.getString(C1018R.string.card_no) + ": " + financialCard.getNumber());
                            stringBuffer.append("\n");
                            stringBuffer.append(PrinterActivity.this.getString(C1018R.string.holder) + ": " + financialCard.getName());
                            stringBuffer.append("\n");
                            stringBuffer.append(PrinterActivity.this.getString(C1018R.string.exp_date) + ": " + String.format("%02d/%02d", Integer.valueOf(financialCard.getExpiryMonth()), Integer.valueOf(financialCard.getExpiryYear())));
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
                    PrinterActivity.this.dialog(C1018R.drawable.ic_delete, PrinterActivity.this.getString(C1018R.string.card_info), stringBuffer.toString());
                }
            }
        }, C1018R.string.print_barcode);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void readCardEncrypted() {
        Log.d(LOG_TAG, "Read card encrypted");
        runTask(new PrinterRunnable() { // from class: com.yd.electricecollector.printer.andoirdbluetoothprint.PrinterActivity.30
            @Override // com.yd.electricecollector.printer.andoirdbluetoothprint.PrinterActivity.PrinterRunnable
            public void run(ProgressDialog progressDialog, Printer printer) throws IOException {
                byte[] readCardData = PrinterActivity.this.mEMSR.readCardData(135);
                StringBuffer stringBuffer = new StringBuffer();
                int i = readCardData[0] >>> 3;
                byte[] bArr = new byte[readCardData.length - 1];
                System.arraycopy(readCardData, 1, bArr, 0, bArr.length);
                if (i == 0 || i == 5) {
                    String[] strArr = null;
                    try {
                        stringBuffer.append("Track2: " + strArr[0]);
                        stringBuffer.append("\n");
                    } catch (Exception e) {
                        PrinterActivity.this.error("Failed to decrypt RSA data: " + e.getMessage());
                        return;
                    }
                } else if (i == 2) {
                    String[] strArr2 = null;
                    try {
                        stringBuffer.append("Random data: " + strArr2[0]);
                        stringBuffer.append("\n");
                        stringBuffer.append("Serial number: " + strArr2[1]);
                        stringBuffer.append("\n");
                        String str = strArr2[2];
                        String str2 = strArr2[3];
                        String str3 = strArr2[4];
                    } catch (Exception e2) {
                        PrinterActivity.this.error("Failed to decrypt AES data: " + e2.getMessage());
                        return;
                    }
                } else if (i == 3) {
                    String[] strArr3 = null;
                    try {
                        stringBuffer.append("Card type: " + strArr3[0]);
                        stringBuffer.append("\n");
                        String str4 = strArr3[1];
                        String str5 = strArr3[2];
                        String str6 = strArr3[3];
                    } catch (Exception e3) {
                        PrinterActivity.this.error("Failed to decrypt IDTECH data: " + e3.getMessage());
                        return;
                    }
                } else {
                    stringBuffer.append("\n");
                }
                PrinterActivity.this.dialog(C1018R.drawable.ic_setting_icon, PrinterActivity.this.getString(C1018R.string.card_info), stringBuffer.toString());
            }
        }, C1018R.string.print_barcode);
    }

    private void readInformation() {
        Log.d(LOG_TAG, "Read information");
        runTask(new PrinterRunnable() { // from class: com.yd.electricecollector.printer.andoirdbluetoothprint.PrinterActivity.20
            @Override // com.yd.electricecollector.printer.andoirdbluetoothprint.PrinterActivity.PrinterRunnable
            public void run(ProgressDialog progressDialog, Printer printer) throws IOException {
                StringBuffer stringBuffer = new StringBuffer();
                PrinterInformation information = printer.getInformation();
                stringBuffer.append("PRINTER:");
                stringBuffer.append("\n");
                stringBuffer.append("Name: " + information.getName());
                stringBuffer.append("\n");
                stringBuffer.append("Version: " + information.getFirmwareVersionString());
                stringBuffer.append("\n");
                stringBuffer.append("\n");
                if (PrinterActivity.this.mEMSR != null) {
                    EMSR.EMSRInformation information2 = PrinterActivity.this.mEMSR.getInformation();
                    EMSR.EMSRKeyInformation keyInformation = PrinterActivity.this.mEMSR.getKeyInformation(2);
                    EMSR.EMSRKeyInformation keyInformation2 = PrinterActivity.this.mEMSR.getKeyInformation(1);
                    EMSR.EMSRKeyInformation keyInformation3 = PrinterActivity.this.mEMSR.getKeyInformation(32);
                    stringBuffer.append("ENCRYPTED MAGNETIC HEAD:");
                    stringBuffer.append("\n");
                    stringBuffer.append("Name: " + information2.name);
                    stringBuffer.append("\n");
                    stringBuffer.append("Serial: " + information2.serial);
                    stringBuffer.append("\n");
                    stringBuffer.append("Version: " + information2.version);
                    stringBuffer.append("\n");
                    stringBuffer.append("KEK Version: " + (keyInformation.tampered ? "Tampered" : Integer.valueOf(keyInformation.version)));
                    stringBuffer.append("\n");
                    stringBuffer.append("AES Version: " + (keyInformation2.tampered ? "Tampered" : Integer.valueOf(keyInformation2.version)));
                    stringBuffer.append("\n");
                    stringBuffer.append("DUKPT Version: " + (keyInformation3.tampered ? "Tampered" : Integer.valueOf(keyInformation3.version)));
                }
                PrinterActivity.this.dialog(C1018R.drawable.ic_logout_d_icon, "hhhhgggg", stringBuffer.toString());
            }
        }, C1018R.string.app_name);
    }

    private void runTask(final PrinterRunnable printerRunnable, int i) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(C1018R.string.welcome));
        progressDialog.setMessage(getString(i));
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        new Thread(new Runnable() { // from class: com.yd.electricecollector.printer.andoirdbluetoothprint.PrinterActivity.13
            @Override // java.lang.Runnable
            public void run() {
                try {
                    try {
                        try {
                            printerRunnable.run(progressDialog, PrinterActivity.this.mPrinter);
                        } catch (Exception e) {
                            e.printStackTrace();
                            PrinterActivity.this.error("Critical error occurs: " + e.getMessage());
                            PrinterActivity.this.finish();
                        }
                    } catch (IOException e2) {
                        e2.printStackTrace();
                        PrinterActivity.this.error("I/O error occurs: " + e2.getMessage());
                    }
                } finally {
                    progressDialog.dismiss();
                }
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void status(String str) {
        runOnUiThread(new Runnable() { // from class: com.yd.electricecollector.printer.andoirdbluetoothprint.PrinterActivity.12
            @Override // java.lang.Runnable
            public void run() {
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void toast(final String str) {
        Log.d(LOG_TAG, str);
        runOnUiThread(new Runnable() { // from class: com.yd.electricecollector.printer.andoirdbluetoothprint.PrinterActivity.9
            @Override // java.lang.Runnable
            public void run() {
                Toast.makeText(PrinterActivity.this.getApplicationContext(), str, 0).show();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void waitForConnection() {
        status(null);
        closeActiveConnection();
        startActivityForResult(new Intent(this, (Class<?>) DeviceListActivity.class), 0);
    }

    protected void initPrinter(InputStream inputStream, OutputStream outputStream) throws IOException {
        Log.d(LOG_TAG, "Initialize printer...");
        Printer.setDebug(true);
        EMSR.setDebug(true);
        this.mProtocolAdapter = new ProtocolAdapter(inputStream, outputStream);
        if (this.mProtocolAdapter.isProtocolEnabled()) {
            Log.d(LOG_TAG, "Protocol mode is enabled");
            this.mProtocolAdapter.setPrinterListener(new ProtocolAdapter.PrinterListener() { // from class: com.yd.electricecollector.printer.andoirdbluetoothprint.PrinterActivity.14
                @Override // com.datecs.api.printer.ProtocolAdapter.PrinterListener
                public void onBatteryStateChanged(boolean z) {
                    if (!z) {
                        PrinterActivity.this.status(null);
                    } else {
                        Log.d(PrinterActivity.LOG_TAG, "Low battery");
                        PrinterActivity.this.status("LOW BATTERY");
                    }
                }

                @Override // com.datecs.api.printer.ProtocolAdapter.PrinterListener
                public void onPaperStateChanged(boolean z) {
                    if (!z) {
                        PrinterActivity.this.status(null);
                    } else {
                        Log.d(PrinterActivity.LOG_TAG, "Event: Paper out");
                        PrinterActivity.this.status("PAPER OUT");
                    }
                }

                @Override // com.datecs.api.printer.ProtocolAdapter.PrinterListener
                public void onThermalHeadStateChanged(boolean z) {
                    if (!z) {
                        PrinterActivity.this.status(null);
                    } else {
                        Log.d(PrinterActivity.LOG_TAG, "Thermal head is overheated");
                        PrinterActivity.this.status("OVERHEATED");
                    }
                }
            });
            this.mProtocolAdapter.setBarcodeListener(new ProtocolAdapter.BarcodeListener() { // from class: com.yd.electricecollector.printer.andoirdbluetoothprint.PrinterActivity.15
                @Override // com.datecs.api.printer.ProtocolAdapter.BarcodeListener
                public void onReadBarcode() {
                    Log.d(PrinterActivity.LOG_TAG, "On read barcode");
                    PrinterActivity.this.runOnUiThread(new Runnable() { // from class: com.yd.electricecollector.printer.andoirdbluetoothprint.PrinterActivity.15.1
                        @Override // java.lang.Runnable
                        public void run() {
                            PrinterActivity.this.readBarcode(0);
                        }
                    });
                }
            });
            this.mProtocolAdapter.setCardListener(new ProtocolAdapter.CardListener() { // from class: com.yd.electricecollector.printer.andoirdbluetoothprint.PrinterActivity.16
                @Override // com.datecs.api.printer.ProtocolAdapter.CardListener
                public void onReadCard(boolean z) {
                    Log.d(PrinterActivity.LOG_TAG, "On read card(entrypted=" + z + ")");
                    if (z) {
                        PrinterActivity.this.runOnUiThread(new Runnable() { // from class: com.yd.electricecollector.printer.andoirdbluetoothprint.PrinterActivity.16.1
                            @Override // java.lang.Runnable
                            public void run() {
                                PrinterActivity.this.readCardEncrypted();
                            }
                        });
                    } else {
                        PrinterActivity.this.runOnUiThread(new Runnable() { // from class: com.yd.electricecollector.printer.andoirdbluetoothprint.PrinterActivity.16.2
                            @Override // java.lang.Runnable
                            public void run() {
                                PrinterActivity.this.readCard();
                            }
                        });
                    }
                }
            });
            this.mPrinterChannel = this.mProtocolAdapter.getChannel(1);
            this.mPrinter = new Printer(this.mPrinterChannel.getInputStream(), this.mPrinterChannel.getOutputStream());
            ProtocolAdapter.Channel channel = this.mProtocolAdapter.getChannel(15);
            try {
                channel.close();
            } catch (IOException e) {
            }
            try {
                channel.open();
                Log.d(LOG_TAG, "Encrypted magnetic stripe reader is available");
            } catch (IOException e2) {
                if (this.mEMSR != null) {
                    this.mEMSR.close();
                    this.mEMSR = null;
                }
            }
            ProtocolAdapter.Channel channel2 = this.mProtocolAdapter.getChannel(13);
            try {
                channel2.close();
            } catch (IOException e3) {
            }
            try {
                channel2.open();
                this.mRC663 = new RC663(channel2.getInputStream(), channel2.getOutputStream());
                this.mRC663.setCardListener(new RC663.CardListener() { // from class: com.yd.electricecollector.printer.andoirdbluetoothprint.PrinterActivity.17
                    @Override // com.datecs.api.rfid.RC663.CardListener
                    public void onCardDetect(ContactlessCard contactlessCard) {
                    }
                });
                this.mRC663.enable();
                Log.d(LOG_TAG, "RC663 reader is available");
            } catch (IOException e4) {
                if (this.mRC663 != null) {
                    this.mRC663.close();
                    this.mRC663 = null;
                }
            }
            this.mUniversalChannel = this.mProtocolAdapter.getChannel(16);
            new UniversalReader(this.mUniversalChannel.getInputStream(), this.mUniversalChannel.getOutputStream());
        } else {
            Log.d(LOG_TAG, "Protocol mode is disabled");
            this.mPrinter = new Printer(this.mProtocolAdapter.getRawInputStream(), this.mProtocolAdapter.getRawOutputStream());
        }
        this.mPrinter.setConnectionListener(new Printer.ConnectionListener() { // from class: com.yd.electricecollector.printer.andoirdbluetoothprint.PrinterActivity.18
            @Override // com.datecs.api.printer.Printer.ConnectionListener
            public void onDisconnect() {
                PrinterActivity.this.toast("Printer is disconnected");
                PrinterActivity.this.runOnUiThread(new Runnable() { // from class: com.yd.electricecollector.printer.andoirdbluetoothprint.PrinterActivity.18.1
                    @Override // java.lang.Runnable
                    public void run() {
                        if (PrinterActivity.this.isFinishing()) {
                            return;
                        }
                        PrinterActivity.this.waitForConnection();
                    }
                });
            }
        });
    }

    @Override // android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) {
        if (i == 0) {
            if (i2 != -1) {
                finish();
                return;
            }
            String stringExtra = intent.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
            PreferenceManager.getDefaultSharedPreferences(this).edit().putString("PrinterSelection", "bth://" + stringExtra).commit();
            establishBluetoothConnection(stringExtra);
        }
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override // android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(C1018R.layout.printer);
        findViewById(C1018R.id.print_self_test).setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.printer.andoirdbluetoothprint.PrinterActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                PrinterActivity.this.printSelfTest();
            }
        });
        findViewById(C1018R.id.print_text).setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.printer.andoirdbluetoothprint.PrinterActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                PrinterActivity.this.printText();
            }
        });
        findViewById(C1018R.id.btn_print_custom).setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.printer.andoirdbluetoothprint.PrinterActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PrinterActivity.this);
                builder.setTitle("CodeTable 24");
                final EditText editText = new EditText(PrinterActivity.this);
                editText.setInputType(1);
                builder.setView(editText);
                builder.setPositiveButton("تاييد", new DialogInterface.OnClickListener() { // from class: com.yd.electricecollector.printer.andoirdbluetoothprint.PrinterActivity.3.1
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialogInterface, int i) {
                        PrinterActivity.this.m_Text = editText.getText().toString();
                        PrinterActivity.this.printCustomText(PrinterActivity.this.m_Text);
                    }
                });
                builder.setNegativeButton("لغو", new DialogInterface.OnClickListener() { // from class: com.yd.electricecollector.printer.andoirdbluetoothprint.PrinterActivity.3.2
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.show();
            }
        });
        findViewById(C1018R.id.print_image).setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.printer.andoirdbluetoothprint.PrinterActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                PrinterActivity.this.printImage();
            }
        });
        findViewById(C1018R.id.print_page).setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.printer.andoirdbluetoothprint.PrinterActivity.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                PrinterActivity.this.printPage();
            }
        });
        findViewById(C1018R.id.print_barcode).setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.printer.andoirdbluetoothprint.PrinterActivity.6
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                PrinterActivity.this.printBarcode();
            }
        });
        findViewById(C1018R.id.read_card).setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.printer.andoirdbluetoothprint.PrinterActivity.7
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                PrinterActivity.this.readCard();
            }
        });
        findViewById(C1018R.id.read_barcode).setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.printer.andoirdbluetoothprint.PrinterActivity.8
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                PrinterActivity.this.readBarcode(10);
            }
        });
        waitForConnection();
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        closeActiveConnection();
    }
}
