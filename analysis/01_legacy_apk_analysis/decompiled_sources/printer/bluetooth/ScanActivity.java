package com.p001yd.electricecollector.printer.bluetooth;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.p001yd.electricecollector.C1018R;
import com.p001yd.electricecollector.TAPreferences;
import com.p001yd.electricecollector.printer.andoirdbluetoothprint.MainActivity;
import com.p001yd.electricecollector.printer.bluetooth.BluetoothConnector;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes4.dex */
public class ScanActivity extends AppCompatActivity implements BluetoothConnector.OnDiscoveryListener {
    private static final String ADDRESS = "address";
    public static final String BLUETOOTH_ADDR = "bluetooth_addr";
    public static final String BLUETOOTH_NAME = "bluetooth_name";
    private static final String DATABASE_NAME = "bluetooth_devices.db";
    private static final int DATABASE_VERSION = 1;
    private static final String DEVICES_TABLE = "bluetooth_devices";
    public static final String NAME = "name";
    private Button btnDisconnect;
    private Button btnScan;
    private BluetoothConnector mConnector;
    private ListView mDeviceView;
    private ProgressDialog mProgress;
    private TextView txtDeviceAddress;
    private TextView txtDeviceName;
    private TextView txtDeviceNameCaption;
    private Device activeDevice = null;
    String dAddress = null;
    private final DeviceAdapter mAdapter = new DeviceAdapter() { // from class: com.yd.electricecollector.printer.bluetooth.ScanActivity.1
        @Override // android.widget.Adapter
        public int getCount() {
            return getContainer().size();
        }

        @Override // android.widget.Adapter
        public Object getItem(int i) {
            return getContainer().get(i);
        }

        @Override // android.widget.Adapter
        public long getItemId(int i) {
            return i;
        }

        @Override // android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            View view2 = view;
            if (view2 == null) {
                view2 = LayoutInflater.from(ScanActivity.this).inflate(C1018R.layout.device, (ViewGroup) null);
            }
            Device device = getContainer().get(i);
            ((TextView) view2.findViewById(C1018R.id.name)).setText(device.getName());
            ((TextView) view2.findViewById(C1018R.id.address)).setText(device.getAddress());
            return view2;
        }
    };
    private final Handler mHandler = new Handler();

    /* loaded from: classes4.dex */
    private class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, ScanActivity.DATABASE_NAME, (SQLiteDatabase.CursorFactory) null, 1);
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onCreate(SQLiteDatabase sQLiteDatabase) {
            sQLiteDatabase.execSQL("create table bluetooth_devices (name text, address text);");
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
            sQLiteDatabase.execSQL("DROP TABLE  bluetooth_devices");
            onCreate(sQLiteDatabase);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class Device {
        private String mAddress;
        private String mName;

        public Device(String str, String str2) {
            this.mName = str;
            this.mAddress = str2;
        }

        public String getAddress() {
            return this.mAddress;
        }

        public String getName() {
            return this.mName;
        }
    }

    /* loaded from: classes4.dex */
    private abstract class DeviceAdapter extends BaseAdapter {
        private ArrayList<Device> mDevices;

        private DeviceAdapter() {
            this.mDevices = new ArrayList<>();
        }

        public void add(Device device) {
            this.mDevices.add(device);
        }

        public void clear() {
            this.mDevices.clear();
        }

        public List<Device> getContainer() {
            return this.mDevices;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startDiscovery() {
        try {
            this.mConnector.startDiscovery(this);
        } catch (IOException e) {
            notifyUser(e.getMessage());
        }
    }

    protected void notifyUser(final String str) {
        this.mHandler.post(new Runnable() { // from class: com.yd.electricecollector.printer.bluetooth.ScanActivity.5
            @Override // java.lang.Runnable
            public void run() {
                new AlertDialog.Builder(ScanActivity.this).setTitle(C1018R.string.scan_error).setMessage(str).create().show();
            }
        });
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(C1018R.layout.devices);
        this.dAddress = TAPreferences.getSetectedPrinterConnectionString(this);
        if (this.dAddress.contains("bth://")) {
            this.dAddress = this.dAddress.substring(6);
        }
        this.txtDeviceNameCaption = (TextView) findViewById(C1018R.id.txtDeviceNameCaption);
        this.txtDeviceName = (TextView) findViewById(C1018R.id.txtDeviceName);
        this.txtDeviceAddress = (TextView) findViewById(C1018R.id.txtDeviceAddress);
        this.btnScan = (Button) findViewById(C1018R.id.btnScan);
        this.btnScan.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.printer.bluetooth.ScanActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ScanActivity.this.startDiscovery();
            }
        });
        this.btnDisconnect = (Button) findViewById(C1018R.id.btnDisconnectPrinter);
        this.btnDisconnect.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.printer.bluetooth.ScanActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ScanActivity.this.setActiveDevice(null);
                Intent intent = new Intent(ScanActivity.this, (Class<?>) ScanActivity.class);
                intent.putExtra(PrinterActivity.CONNECTION_STRING, "");
                ScanActivity.this.setResult(-1, intent);
                ScanActivity.this.finish();
            }
        });
        this.mDeviceView = (ListView) findViewById(C1018R.id.devices);
        this.mDeviceView.setAdapter((ListAdapter) this.mAdapter);
        this.mDeviceView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.yd.electricecollector.printer.bluetooth.ScanActivity.4
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                Device device = (Device) ScanActivity.this.mAdapter.getItem(i);
                Intent intent = new Intent(ScanActivity.this, (Class<?>) ScanActivity.class);
                intent.putExtra(PrinterActivity.CONNECTION_STRING, "bth://" + device.getAddress());
                intent.putExtra("name", device.getName());
                ScanActivity.this.setResult(-1, intent);
                ScanActivity.this.finish();
            }
        });
        try {
            this.mConnector = BluetoothConnector.getConnector(this);
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), 0);
            finish();
        }
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, C1018R.string.scan_menu);
        menu.add(0, 2, 0, C1018R.string.test_printer_menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
    }

    @Override // com.yd.electricecollector.printer.bluetooth.BluetoothConnector.OnDiscoveryListener
    public void onDeviceFound(final String str, final String str2) {
        this.mHandler.post(new Runnable() { // from class: com.yd.electricecollector.printer.bluetooth.ScanActivity.6
            @Override // java.lang.Runnable
            public void run() {
                new Device(str, str2);
                Device device = str == null ? new Device(ScanActivity.this.getString(C1018R.string.scan_unknown_device), str2) : new Device(str, str2);
                if (str2.equals(ScanActivity.this.dAddress)) {
                    ScanActivity.this.setActiveDevice(device);
                }
                ScanActivity.this.mAdapter.add(device);
                ScanActivity.this.mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override // com.yd.electricecollector.printer.bluetooth.BluetoothConnector.OnDiscoveryListener
    public void onDiscoveryError(String str) {
        notifyUser(str);
    }

    @Override // com.yd.electricecollector.printer.bluetooth.BluetoothConnector.OnDiscoveryListener
    public void onDiscoveryFinished() {
        this.mHandler.post(new Runnable() { // from class: com.yd.electricecollector.printer.bluetooth.ScanActivity.7
            @Override // java.lang.Runnable
            public void run() {
                ScanActivity.this.mProgress.dismiss();
            }
        });
    }

    @Override // com.yd.electricecollector.printer.bluetooth.BluetoothConnector.OnDiscoveryListener
    public void onDiscoveryStarted() {
        this.mHandler.post(new Runnable() { // from class: com.yd.electricecollector.printer.bluetooth.ScanActivity.8
            @Override // java.lang.Runnable
            public void run() {
                ScanActivity.this.mAdapter.clear();
                ScanActivity.this.mAdapter.notifyDataSetChanged();
                ScanActivity.this.mProgress = ProgressDialog.show(ScanActivity.this, ScanActivity.this.getString(C1018R.string.scan_title), ScanActivity.this.getString(C1018R.string.scan_text), true);
            }
        });
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 1) {
            startDiscovery();
        }
        if (menuItem.getItemId() == 2) {
            if (this.txtDeviceAddress.getText().length() == 0) {
                new AlertDialog.Builder(this).setTitle(C1018R.string.scan_error).setMessage(" يجب تحديد الطابعة").create().show();
                return false;
            }
            Intent intent = new Intent(this, (Class<?>) PrinterActivity.class);
            intent.putExtra(PrinterActivity.CONNECTION_STRING, this.txtDeviceAddress.getText().toString());
            startActivity(intent);
        }
        if (menuItem.getItemId() == 3) {
            startActivity(new Intent(this, (Class<?>) MainActivity.class));
        }
        return super.onOptionsItemSelected(menuItem);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStart() {
        super.onStart();
        setActiveDevice(null);
        SQLiteDatabase writableDatabase = new DatabaseHelper(this).getWritableDatabase();
        Cursor query = writableDatabase.query(DEVICES_TABLE, new String[]{"name", ADDRESS}, null, null, null, null, null);
        if (query != null && query.moveToFirst()) {
            this.mAdapter.clear();
            int columnIndex = query.getColumnIndex("name");
            int columnIndex2 = query.getColumnIndex(ADDRESS);
            do {
                String string = query.getString(columnIndex);
                String string2 = query.getString(columnIndex2);
                Device device = new Device(string, string2);
                if (string2.equals(this.dAddress)) {
                    setActiveDevice(device);
                }
                this.mAdapter.add(device);
            } while (query.moveToNext());
            query.close();
            this.mAdapter.notifyDataSetChanged();
        }
        writableDatabase.close();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStop() {
        super.onStop();
        SQLiteDatabase writableDatabase = new DatabaseHelper(this).getWritableDatabase();
        try {
            writableDatabase.beginTransaction();
            writableDatabase.delete(DEVICES_TABLE, null, null);
            int count = this.mAdapter.getCount();
            for (int i = 0; i < count; i++) {
                Device device = (Device) this.mAdapter.getItem(i);
                ContentValues contentValues = new ContentValues();
                contentValues.put("name", device.getName());
                contentValues.put(ADDRESS, device.getAddress());
                writableDatabase.insert(DEVICES_TABLE, "name", contentValues);
            }
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
            writableDatabase.close();
        }
    }

    protected void setActiveDevice(Device device) {
        this.activeDevice = device;
        if (this.activeDevice == null) {
            this.txtDeviceNameCaption.setVisibility(4);
            this.txtDeviceName.setVisibility(4);
            this.txtDeviceAddress.setVisibility(4);
        } else {
            this.txtDeviceNameCaption.setVisibility(0);
            this.txtDeviceName.setVisibility(0);
            this.txtDeviceAddress.setVisibility(0);
            this.txtDeviceName.setText(device.mName);
            this.txtDeviceAddress.setText(device.mAddress);
        }
    }
}
