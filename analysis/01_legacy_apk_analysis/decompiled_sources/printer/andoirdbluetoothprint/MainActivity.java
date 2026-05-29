package com.p001yd.electricecollector.printer.andoirdbluetoothprint;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.ganesh.intermecarabic.Arabic864;
import com.p001yd.electricecollector.C1018R;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Set;
import java.util.UUID;

/* loaded from: classes11.dex */
public class MainActivity extends Activity implements Runnable {
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    protected static final String TAG = "TAG";
    int[] Ltk;
    int[] Ltp;
    PrinterFont PrinterText;
    BluetoothAdapter mBluetoothAdapter;
    private ProgressDialog mBluetoothConnectProgressDialog;
    BluetoothDevice mBluetoothDevice;
    private BluetoothSocket mBluetoothSocket;
    Button mDisc;
    Button mPrint;
    Button mScan;
    private UUID applicationUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private Handler mHandler = new Handler() { // from class: com.yd.electricecollector.printer.andoirdbluetoothprint.MainActivity.4
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            MainActivity.this.mBluetoothConnectProgressDialog.dismiss();
            Toast.makeText(MainActivity.this, "DeviceConnected", 0).show();
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    public void ListPairedDevices() {
        Set<BluetoothDevice> bondedDevices = this.mBluetoothAdapter.getBondedDevices();
        if (bondedDevices.size() > 0) {
            for (BluetoothDevice bluetoothDevice : bondedDevices) {
                Log.v(TAG, "PairedDevices: " + bluetoothDevice.getName() + "  " + bluetoothDevice.getAddress());
            }
        }
    }

    private void PrintArabicText(String str) {
        String[] words = getWords(str);
        int length = words.length - 1;
        int length2 = str.length();
        int i = 0;
        if (words.length >= 5) {
            i = 0;
            if (length2 > 30) {
                int i2 = 0;
                while (true) {
                    i = 0;
                    if (i2 >= words.length) {
                        break;
                    }
                    if (i2 > 5) {
                        words[i2] = "";
                    }
                    i2++;
                }
            }
        }
        while (i < words.length) {
            String str2 = words[length - i].toString();
            if (!str2.equals("")) {
                PrintArabicText2(str2 + " ");
            }
            i++;
        }
    }

    private void PrintArabicText2(String str) {
        this.PrinterText.Arabize(str, false);
        int[] ltk = this.PrinterText.getLtk();
        this.Ltk = ltk;
        this.Ltp = new int[ltk.length];
        int length = ltk.length - 1;
        int i = 0;
        while (true) {
            int[] iArr = this.Ltk;
            if (i >= iArr.length) {
                break;
            }
            this.Ltp[i] = iArr[length - i];
            i++;
        }
        int i2 = 0;
        while (true) {
            int[] iArr2 = this.Ltp;
            if (i2 >= iArr2.length) {
                return;
            }
            if (i2 <= 30) {
                BluetoothPrintDriver.ImportData(new byte[]{(byte) iArr2[i2]}, 1);
            }
            i2++;
        }
    }

    private void closeSocket(BluetoothSocket bluetoothSocket) {
        try {
            bluetoothSocket.close();
            Log.d(TAG, "SocketClosed");
        } catch (IOException e) {
            Log.d(TAG, "CouldNotCloseSocket");
        }
    }

    public static byte intToByteArray(int i) {
        byte[] array = ByteBuffer.allocate(4).putInt(i).array();
        for (int i2 = 0; i2 < array.length; i2++) {
            System.out.println("Selva  [" + i2 + "] = 0x" + UnicodeFormatter.byteToHex(array[i2]));
        }
        return array[3];
    }

    public boolean IsPrinterConnect() {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mBluetoothAdapter = defaultAdapter;
        if (defaultAdapter == null) {
            Toast.makeText(this, "Did not find the Bluetooth adapter", 1).show();
        }
        if (!this.mBluetoothAdapter.isEnabled()) {
            startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), 20);
            return true;
        }
        if (!BluetoothPrintDriver.OpenPrinter(this.mBluetoothDevice.getAddress())) {
            return false;
        }
        Toast.makeText(this, "لايوجد اتصال", 0).show();
        return true;
    }

    public String[] getWords(String str) {
        return str != null ? str.split("\\s") : new String[0];
    }

    @Override // android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        switch (i) {
            case 1:
                if (i2 == -1) {
                    String string = intent.getExtras().getString("DeviceAddress");
                    Log.v(TAG, "Coming incoming address " + string);
                    this.mBluetoothDevice = this.mBluetoothAdapter.getRemoteDevice(string);
                    this.mBluetoothConnectProgressDialog = ProgressDialog.show(this, "Connecting...", this.mBluetoothDevice.getName() + " : " + this.mBluetoothDevice.getAddress(), true, false);
                    new Thread(this).start();
                    return;
                }
                return;
            case 2:
                if (i2 != -1) {
                    Toast.makeText(this, "Message", 0).show();
                    return;
                } else {
                    ListPairedDevices();
                    startActivityForResult(new Intent(this, (Class<?>) DeviceListActivity.class), 1);
                    return;
                }
            default:
                return;
        }
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        try {
            if (this.mBluetoothSocket != null) {
                this.mBluetoothSocket.close();
            }
        } catch (Exception e) {
            Log.e("Tag", "Exe ", e);
        }
        setResult(0);
        finish();
    }

    @Override // android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(C1018R.layout.activity_prenter);
        this.mScan = (Button) findViewById(C1018R.id.Scan);
        this.PrinterText = new PrinterFont(this);
        this.mScan.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.printer.andoirdbluetoothprint.MainActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                MainActivity.this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (MainActivity.this.mBluetoothAdapter == null) {
                    Toast.makeText(MainActivity.this, "Message1", 0).show();
                    return;
                }
                if (!MainActivity.this.mBluetoothAdapter.isEnabled()) {
                    MainActivity.this.startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), 2);
                } else {
                    MainActivity.this.ListPairedDevices();
                    MainActivity.this.startActivityForResult(new Intent(MainActivity.this, (Class<?>) DeviceListActivity.class), 1);
                }
            }
        });
        this.mPrint = (Button) findViewById(C1018R.id.mPrint);
        this.mPrint.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.printer.andoirdbluetoothprint.MainActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                new Thread() { // from class: com.yd.electricecollector.printer.andoirdbluetoothprint.MainActivity.2.1
                    @Override // java.lang.Thread, java.lang.Runnable
                    public void run() {
                        try {
                            OutputStream outputStream = MainActivity.this.mBluetoothSocket.getOutputStream();
                            outputStream.write(new Arabic864().Convert("سند قبض ", false));
                            outputStream.write("\n------ ".getBytes());
                        } catch (Exception e) {
                            Log.e("MainActivity", "Exe ", e);
                        }
                    }
                }.start();
            }
        });
        this.mDisc = (Button) findViewById(C1018R.id.dis);
        this.mDisc.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.printer.andoirdbluetoothprint.MainActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (MainActivity.this.mBluetoothAdapter != null) {
                    MainActivity.this.mBluetoothAdapter.disable();
                }
            }
        });
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (this.mBluetoothSocket != null) {
                this.mBluetoothSocket.close();
            }
        } catch (Exception e) {
            Log.e("Tag", "Exe ", e);
        }
    }

    @Override // java.lang.Runnable
    public void run() {
        try {
            this.mBluetoothSocket = this.mBluetoothDevice.createRfcommSocketToServiceRecord(this.applicationUUID);
            this.mBluetoothAdapter.cancelDiscovery();
            this.mBluetoothSocket.connect();
            this.mHandler.sendEmptyMessage(0);
        } catch (IOException e) {
            Log.d(TAG, "CouldNotConnectToSocket", e);
            closeSocket(this.mBluetoothSocket);
        }
    }

    public byte[] sel(int i) {
        ByteBuffer allocate = ByteBuffer.allocate(2);
        allocate.putInt(i);
        allocate.flip();
        return allocate.array();
    }
}
