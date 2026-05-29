package com.p001yd.electricecollector.printer.bluetooth;

import com.p001yd.electricecollector.printer.bluetooth.BluetoothConnector;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/* loaded from: classes4.dex */
final class NDKConnector extends BluetoothConnector {
    private RFComm mRFComm;

    /* loaded from: classes4.dex */
    class DiscoveryRunnable implements Runnable {
        private BluetoothConnector.OnDiscoveryListener mListener;

        public DiscoveryRunnable(BluetoothConnector.OnDiscoveryListener onDiscoveryListener) {
            this.mListener = onDiscoveryListener;
        }

        @Override // java.lang.Runnable
        public void run() {
            List<RFComm> list;
            String str;
            this.mListener.onDiscoveryStarted();
            try {
                list = RFComm.scan();
            } catch (IOException e) {
                e.printStackTrace();
                this.mListener.onDiscoveryError(e.getMessage());
                list = null;
            }
            if (list != null) {
                for (RFComm rFComm : list) {
                    try {
                        str = rFComm.getName();
                    } catch (IOException e2) {
                        str = null;
                    }
                    this.mListener.onDeviceFound(str, rFComm.getAddress());
                }
            }
            this.mListener.onDiscoveryFinished();
        }
    }

    @Override // com.p001yd.electricecollector.printer.bluetooth.BluetoothConnector
    public void close() throws IOException {
        if (this.mRFComm != null) {
            this.mRFComm.close();
            this.mRFComm = null;
        }
    }

    @Override // com.p001yd.electricecollector.printer.bluetooth.BluetoothConnector
    public void connect(String str) throws IOException {
        this.mRFComm = new RFComm(str);
        this.mRFComm.connect();
    }

    @Override // com.p001yd.electricecollector.printer.bluetooth.BluetoothConnector
    public InputStream getInputStream() throws IOException {
        if (this.mRFComm != null) {
            return this.mRFComm.getInputStream();
        }
        throw new IOException("The stream is not connected");
    }

    @Override // com.p001yd.electricecollector.printer.bluetooth.BluetoothConnector
    public OutputStream getOutputStream() throws IOException {
        if (this.mRFComm != null) {
            return this.mRFComm.getOutputStream();
        }
        throw new IOException("The stream is not connected");
    }

    @Override // com.p001yd.electricecollector.printer.bluetooth.BluetoothConnector
    public void startDiscovery(BluetoothConnector.OnDiscoveryListener onDiscoveryListener) throws IOException {
        if (onDiscoveryListener == null) {
            throw new NullPointerException("The listener is a null");
        }
        new Thread(new DiscoveryRunnable(onDiscoveryListener)).start();
    }
}
