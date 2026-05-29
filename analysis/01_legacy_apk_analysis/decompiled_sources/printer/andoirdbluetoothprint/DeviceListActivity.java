package com.p001yd.electricecollector.printer.andoirdbluetoothprint;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.p001yd.electricecollector.C1018R;
import com.p001yd.electricecollector.TAPreferences;
import com.p001yd.electricecollector.printer.bluetooth.PrinterActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/* loaded from: classes11.dex */
public class DeviceListActivity extends Activity {
    private static final String ADDRESS = "address";
    public static final String BLUETOOTH_ADDR = "bluetooth_addr";
    public static final String BLUETOOTH_NAME = "bluetooth_name";
    private static final String DATABASE_NAME = "bluetooth_devices.db";
    private static final int DATABASE_VERSION = 1;
    private static final String DEVICES_TABLE = "bluetooth_devices";
    public static final String NAME = "name";
    private Button btnDisconnect;
    private BluetoothAdapter mBtAdapter;
    private DeviceAdapter mDevicesAdapter;
    private TextView txtDeviceAddress;
    private TextView txtDeviceName;
    private TextView txtDeviceNameCaption;
    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    public static String PREF_DEVICE_ADDRESS = "device_address";
    String dAddress = null;
    private DeviceNode activeDevice = null;
    private final AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() { // from class: com.yd.electricecollector.printer.andoirdbluetoothprint.DeviceListActivity.1
        @Override // android.widget.AdapterView.OnItemClickListener
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            DeviceListActivity.this.mBtAdapter.cancelDiscovery();
            DeviceNode deviceNode = (DeviceNode) DeviceListActivity.this.mDevicesAdapter.getItem(i);
            String address = deviceNode.getAddress();
            if (BluetoothAdapter.checkBluetoothAddress(address)) {
                DeviceListActivity.this.finishActivityWithResult(address, deviceNode.getName());
            }
        }
    };
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.yd.electricecollector.printer.andoirdbluetoothprint.DeviceListActivity.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (!"android.bluetooth.device.action.FOUND".equals(action)) {
                if ("android.bluetooth.adapter.action.DISCOVERY_FINISHED".equals(action)) {
                    DeviceListActivity.this.setProgressBarIndeterminateVisibility(false);
                    DeviceListActivity.this.setTitle(C1018R.string.title_select_device);
                    return;
                }
                return;
            }
            BluetoothDevice bluetoothDevice = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
            int i = bluetoothDevice.getBondState() == 12 ? C1018R.drawable.bluetooth_paired : C1018R.drawable.bluetooth;
            DeviceNode find = DeviceListActivity.this.mDevicesAdapter.find(bluetoothDevice.getAddress());
            if (find != null) {
                find.setName(bluetoothDevice.getName());
                find.setIcon(i);
            } else {
                DeviceListActivity.this.mDevicesAdapter.add(bluetoothDevice.getName(), bluetoothDevice.getAddress(), i);
                if (bluetoothDevice.getAddress().equals(DeviceListActivity.this.dAddress)) {
                    DeviceListActivity.this.setActiveDevice(find);
                }
            }
        }
    };

    /* loaded from: classes11.dex */
    private class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DeviceListActivity.DATABASE_NAME, (SQLiteDatabase.CursorFactory) null, 1);
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

    /* loaded from: classes11.dex */
    public class DeviceAdapter extends BaseAdapter {
        private List<DeviceNode> mNodeList = new ArrayList();

        public DeviceAdapter() {
        }

        public void add(DeviceNode deviceNode) {
            this.mNodeList.add(deviceNode);
        }

        public void add(String str, String str2, int i) {
            this.mNodeList.add(new DeviceNode(str, str2, i));
        }

        public void clear() {
            this.mNodeList.clear();
        }

        public DeviceNode find(String str) {
            for (DeviceNode deviceNode : this.mNodeList) {
                if (str.equals(deviceNode.getAddress())) {
                    return deviceNode;
                }
            }
            return null;
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return this.mNodeList.size();
        }

        @Override // android.widget.Adapter
        public Object getItem(int i) {
            return this.mNodeList.get(i);
        }

        @Override // android.widget.Adapter
        public long getItemId(int i) {
            return i;
        }

        @Override // android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            View view2 = view;
            if (view2 == null) {
                view2 = LayoutInflater.from(DeviceListActivity.this).inflate(C1018R.layout.device_node, (ViewGroup) null);
            }
            DeviceNode deviceNode = (DeviceNode) getItem(i);
            ((ImageView) view2.findViewById(C1018R.id.icon)).setImageResource(deviceNode.getIcon());
            ((TextView) view2.findViewById(C1018R.id.name)).setText(deviceNode.getName());
            ((TextView) view2.findViewById(C1018R.id.address)).setText(deviceNode.getAddress());
            return view2;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes11.dex */
    public class DeviceNode {
        private String mAddress;
        private int mIconResId;
        private String mName;

        public DeviceNode(String str, String str2, int i) {
            this.mName = str;
            this.mAddress = str2;
            this.mIconResId = i;
        }

        public String getAddress() {
            return this.mAddress;
        }

        public int getIcon() {
            return this.mIconResId;
        }

        public String getName() {
            return this.mName;
        }

        public void setIcon(int i) {
            this.mIconResId = i;
        }

        public void setName(String str) {
            this.mName = str;
        }
    }

    private void checkAndRequestPermissions() {
        int checkSelfPermission = ContextCompat.checkSelfPermission(this, "android.permission.BLUETOOTH");
        int checkSelfPermission2 = ContextCompat.checkSelfPermission(this, "android.permission.BLUETOOTH_ADMIN");
        int checkSelfPermission3 = ContextCompat.checkSelfPermission(this, "android.permission.BLUETOOTH_SCAN");
        int checkSelfPermission4 = ContextCompat.checkSelfPermission(this, "android.permission.BLUETOOTH_CONNECT");
        ArrayList arrayList = new ArrayList();
        if (checkSelfPermission != 0) {
            arrayList.add("android.permission.BLUETOOTH");
        }
        if (checkSelfPermission2 != 0) {
            arrayList.add("android.permission.BLUETOOTH_ADMIN");
        }
        if (checkSelfPermission3 != 0) {
            arrayList.add("android.permission.BLUETOOTH_SCAN");
        }
        if (checkSelfPermission4 != 0) {
            arrayList.add("android.permission.BLUETOOTH_CONNECT");
        }
        if (arrayList.isEmpty()) {
            return;
        }
        ActivityCompat.requestPermissions(this, (String[]) arrayList.toArray(new String[arrayList.size()]), 1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doDiscovery() {
        setProgressBarIndeterminateVisibility(true);
        setTitle(C1018R.string.title_scanning);
        if (this.mBtAdapter.isDiscovering()) {
            this.mBtAdapter.cancelDiscovery();
        }
        this.mBtAdapter.startDiscovery();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void finishActivityWithResult(String str, String str2) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DEVICE_ADDRESS, "bth://" + str);
        intent.putExtra("name", str2);
        setResult(-1, intent);
        finish();
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(C1018R.layout.device_list);
        checkAndRequestPermissions();
        this.dAddress = TAPreferences.getSetectedPrinterConnectionString(this);
        if (this.dAddress.contains("bth://")) {
            this.dAddress = this.dAddress.substring(6);
        }
        setResult(0);
        setFinishOnTouchOutside(false);
        this.mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mDevicesAdapter = new DeviceAdapter();
        PreferenceManager.getDefaultSharedPreferences(this);
        ListView listView = (ListView) findViewById(C1018R.id.devices_list);
        listView.setAdapter((ListAdapter) this.mDevicesAdapter);
        listView.setOnItemClickListener(this.mDeviceClickListener);
        this.txtDeviceNameCaption = (TextView) findViewById(C1018R.id.txtDeviceNameCaption);
        this.txtDeviceName = (TextView) findViewById(C1018R.id.txtDeviceName);
        this.txtDeviceAddress = (TextView) findViewById(C1018R.id.txtDeviceAddress);
        Button button = (Button) findViewById(C1018R.id.btnScan);
        ((Button) findViewById(C1018R.id.btnDisconnectPrinter)).setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.printer.andoirdbluetoothprint.DeviceListActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                DeviceListActivity.this.setActiveDevice(null);
                Intent intent = new Intent(DeviceListActivity.this, (Class<?>) DeviceListActivity.class);
                intent.putExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS, "");
                DeviceListActivity.this.setResult(-1, intent);
                DeviceListActivity.this.finish();
            }
        });
        button.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.printer.andoirdbluetoothprint.DeviceListActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                DeviceListActivity.this.doDiscovery();
            }
        });
        registerReceiver(this.mReceiver, new IntentFilter("android.bluetooth.device.action.FOUND"));
        registerReceiver(this.mReceiver, new IntentFilter("android.bluetooth.adapter.action.DISCOVERY_FINISHED"));
        if (this.mBtAdapter == null || !this.mBtAdapter.isEnabled()) {
            return;
        }
        Set<BluetoothDevice> bondedDevices = this.mBtAdapter.getBondedDevices();
        if (bondedDevices.size() > 0) {
            for (BluetoothDevice bluetoothDevice : bondedDevices) {
                this.mDevicesAdapter.add(bluetoothDevice.getName(), bluetoothDevice.getAddress(), C1018R.drawable.bluetooth_paired);
                this.mDevicesAdapter.notifyDataSetChanged();
                if (bluetoothDevice.getAddress().equals("68:AA:D2:01:1F:DF")) {
                    finishActivityWithResult(bluetoothDevice.getAddress(), bluetoothDevice.getName());
                }
            }
        }
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, C1018R.string.test_printer_menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        if (this.mBtAdapter != null) {
            this.mBtAdapter.cancelDiscovery();
        }
        unregisterReceiver(this.mReceiver);
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        if (i != 4) {
            return super.onKeyUp(i, keyEvent);
        }
        setResult(1);
        finish();
        return true;
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 1) {
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

    @Override // android.app.Activity
    protected void onStart() {
        super.onStart();
        setActiveDevice(null);
        SQLiteDatabase writableDatabase = new DatabaseHelper(this).getWritableDatabase();
        Cursor query = writableDatabase.query(DEVICES_TABLE, new String[]{"name", ADDRESS}, null, null, null, null, null);
        if (query != null && query.moveToFirst()) {
            this.mDevicesAdapter.clear();
            int columnIndex = query.getColumnIndex("name");
            int columnIndex2 = query.getColumnIndex(ADDRESS);
            do {
                String string = query.getString(columnIndex);
                String string2 = query.getString(columnIndex2);
                DeviceNode deviceNode = new DeviceNode(string, string2, C1018R.drawable.bluetooth_paired);
                if (string2.equals(this.dAddress)) {
                    setActiveDevice(deviceNode);
                }
                this.mDevicesAdapter.add(deviceNode);
            } while (query.moveToNext());
            query.close();
            this.mDevicesAdapter.notifyDataSetChanged();
        }
        writableDatabase.close();
    }

    @Override // android.app.Activity
    protected void onStop() {
        super.onStop();
        SQLiteDatabase writableDatabase = new DatabaseHelper(this).getWritableDatabase();
        try {
            writableDatabase.beginTransaction();
            writableDatabase.delete(DEVICES_TABLE, null, null);
            int count = this.mDevicesAdapter.getCount();
            for (int i = 0; i < count; i++) {
                DeviceNode deviceNode = (DeviceNode) this.mDevicesAdapter.getItem(i);
                ContentValues contentValues = new ContentValues();
                contentValues.put("name", deviceNode.getName());
                contentValues.put(ADDRESS, deviceNode.getAddress());
                writableDatabase.insert(DEVICES_TABLE, "name", contentValues);
            }
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
            writableDatabase.close();
        }
    }

    protected void setActiveDevice(DeviceNode deviceNode) {
        this.activeDevice = deviceNode;
        if (this.activeDevice == null) {
            this.txtDeviceNameCaption.setVisibility(4);
            this.txtDeviceName.setVisibility(4);
            this.txtDeviceAddress.setVisibility(4);
        } else {
            this.txtDeviceNameCaption.setVisibility(0);
            this.txtDeviceName.setVisibility(0);
            this.txtDeviceAddress.setVisibility(0);
            this.txtDeviceName.setText(deviceNode.mName);
            this.txtDeviceAddress.setText(deviceNode.mAddress);
        }
    }
}
