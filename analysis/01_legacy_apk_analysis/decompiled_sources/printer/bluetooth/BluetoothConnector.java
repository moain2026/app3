package com.p001yd.electricecollector.printer.bluetooth;

import android.content.Context;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* loaded from: classes4.dex */
public abstract class BluetoothConnector {
    static boolean sExpectNDK;

    /* loaded from: classes4.dex */
    public interface OnDiscoveryListener {
        void onDeviceFound(String str, String str2);

        void onDiscoveryError(String str);

        void onDiscoveryFinished();

        void onDiscoveryStarted();
    }

    static {
        sExpectNDK = false;
        try {
            Class.forName("android.bluetooth.BluetoothAdapter");
        } catch (ClassNotFoundException e) {
            sExpectNDK = true;
        }
    }

    public static BluetoothConnector getConnector(Context context) throws IOException {
        return sExpectNDK ? new NDKConnector() : new APIConnector(context);
    }

    public abstract void close() throws IOException;

    public abstract void connect(String str) throws IOException;

    public abstract InputStream getInputStream() throws IOException;

    public abstract OutputStream getOutputStream() throws IOException;

    public abstract void startDiscovery(OnDiscoveryListener onDiscoveryListener) throws IOException;
}
