package com.p001yd.electricecollector.printer.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.p001yd.electricecollector.printer.bluetooth.BluetoothConnector;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* loaded from: classes4.dex */
final class APIConnector extends BluetoothConnector {
    private BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
    private Context mContext;
    private BluetoothSocket mSocket;

    /* loaded from: classes4.dex */
    private class DiscoveryReceiver extends BroadcastReceiver {
        public BluetoothConnector.OnDiscoveryListener mListener;

        public DiscoveryReceiver(BluetoothConnector.OnDiscoveryListener onDiscoveryListener) {
            this.mListener = onDiscoveryListener;
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("android.bluetooth.adapter.action.DISCOVERY_STARTED")) {
                this.mListener.onDiscoveryStarted();
                return;
            }
            if (action.equals("android.bluetooth.adapter.action.DISCOVERY_FINISHED")) {
                this.mListener.onDiscoveryFinished();
                APIConnector.this.mContext.unregisterReceiver(this);
            } else if (action.equals("android.bluetooth.device.action.FOUND")) {
                BluetoothDevice bluetoothDevice = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                this.mListener.onDeviceFound(bluetoothDevice.getName(), bluetoothDevice.getAddress());
            }
        }
    }

    public APIConnector(Context context) throws IOException {
        this.mContext = context;
        if (this.mAdapter == null) {
            throw new IOException("Bluetooth is not supported on this hardware platform.");
        }
    }

    @Override // com.p001yd.electricecollector.printer.bluetooth.BluetoothConnector
    public void close() throws IOException {
        if (this.mSocket != null) {
            this.mSocket.close();
            this.mSocket = null;
        }
    }

    @Override // com.p001yd.electricecollector.printer.bluetooth.BluetoothConnector
    public void connect(String str) throws IOException {
        BluetoothDevice remoteDevice = this.mAdapter.getRemoteDevice(str);
        this.mAdapter.cancelDiscovery();
        try {
            this.mSocket = (BluetoothSocket) remoteDevice.getClass().getMethod("createRfcommSocket", Integer.TYPE).invoke(remoteDevice, 1);
            this.mSocket.connect();
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    @Override // com.p001yd.electricecollector.printer.bluetooth.BluetoothConnector
    public InputStream getInputStream() throws IOException {
        if (this.mSocket != null) {
            return this.mSocket.getInputStream();
        }
        throw new IOException("The stream is not connected");
    }

    @Override // com.p001yd.electricecollector.printer.bluetooth.BluetoothConnector
    public OutputStream getOutputStream() throws IOException {
        if (this.mSocket != null) {
            return this.mSocket.getOutputStream();
        }
        throw new IOException("The stream is not connected");
    }

    @Override // com.p001yd.electricecollector.printer.bluetooth.BluetoothConnector
    public void startDiscovery(BluetoothConnector.OnDiscoveryListener onDiscoveryListener) throws IOException {
        if (onDiscoveryListener == null) {
            throw new NullPointerException("The listener is a null");
        }
        if (!this.mAdapter.isEnabled()) {
            throw new IOException("Bluetooth is not enabled");
        }
        DiscoveryReceiver discoveryReceiver = new DiscoveryReceiver(onDiscoveryListener);
        this.mContext.registerReceiver(discoveryReceiver, new IntentFilter("android.bluetooth.adapter.action.DISCOVERY_STARTED"));
        this.mContext.registerReceiver(discoveryReceiver, new IntentFilter("android.bluetooth.adapter.action.DISCOVERY_FINISHED"));
        this.mContext.registerReceiver(discoveryReceiver, new IntentFilter("android.bluetooth.device.action.FOUND"));
        if (this.mAdapter.startDiscovery()) {
            return;
        }
        this.mContext.unregisterReceiver(discoveryReceiver);
    }
}
