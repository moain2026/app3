package com.p001yd.electricecollector.printer.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes4.dex */
public final class RFComm {
    private static final int INVALID_SOCKET = -1;
    private String mBthAddr;
    private int mSock = -1;

    static {
        System.loadLibrary("RFComm");
    }

    public RFComm(String str) {
        this.mBthAddr = str;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public native int DatecsRFComm_available(int i);

    private native int DatecsRFComm_close(int i);

    private native int DatecsRFComm_connect(int i, String str);

    private static native int DatecsRFComm_getErrno();

    private static native String DatecsRFComm_queryName(String str) throws IOException;

    /* JADX INFO: Access modifiers changed from: private */
    public native int DatecsRFComm_read(int i, byte[] bArr, int i2, int i3);

    /* JADX INFO: Access modifiers changed from: private */
    public native int DatecsRFComm_readByte(int i);

    private static native String[] DatecsRFComm_scan() throws IOException;

    /* JADX INFO: Access modifiers changed from: private */
    public native int DatecsRFComm_write(int i, byte[] bArr, int i2, int i3);

    /* JADX INFO: Access modifiers changed from: private */
    public native int DatecsRFComm_writeByte(int i, byte b);

    static /* synthetic */ int access$600() {
        return DatecsRFComm_getErrno();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkStream() throws IOException {
        if (!isConnected()) {
            throw new IOException("The object is not connected");
        }
    }

    public static List<RFComm> scan() throws IOException {
        String[] DatecsRFComm_scan = DatecsRFComm_scan();
        ArrayList arrayList = new ArrayList(DatecsRFComm_scan.length);
        for (String str : DatecsRFComm_scan) {
            arrayList.add(new RFComm(str));
        }
        return arrayList;
    }

    public void close() throws IOException {
        if (this.mSock != -1) {
            if (DatecsRFComm_close(this.mSock) != 0) {
                throw new IOException("Error" + DatecsRFComm_getErrno());
            }
            this.mSock = -1;
        }
    }

    public void connect() throws IOException {
        if (this.mSock == -1) {
            this.mSock = DatecsRFComm_connect(1, this.mBthAddr);
            if (this.mSock == -1) {
                throw new IOException("Error " + DatecsRFComm_getErrno());
            }
        }
    }

    public String getAddress() {
        return this.mBthAddr;
    }

    public InputStream getInputStream() {
        return new InputStream() { // from class: com.yd.electricecollector.printer.bluetooth.RFComm.1
            @Override // java.io.InputStream
            public synchronized int available() throws IOException {
                RFComm.this.checkStream();
                return RFComm.this.DatecsRFComm_available(RFComm.this.mSock);
            }

            @Override // java.io.InputStream
            public synchronized int read() throws IOException {
                RFComm.this.checkStream();
                return RFComm.this.DatecsRFComm_readByte(RFComm.this.mSock);
            }

            @Override // java.io.InputStream
            public synchronized int read(byte[] bArr) throws IOException {
                if (bArr == null) {
                    throw new NullPointerException();
                }
                RFComm.this.checkStream();
                return RFComm.this.DatecsRFComm_read(RFComm.this.mSock, bArr, 0, bArr.length);
            }

            @Override // java.io.InputStream
            public synchronized int read(byte[] bArr, int i, int i2) throws IOException {
                if (bArr == null) {
                    throw new NullPointerException();
                }
                if (i >= 0 && i2 >= 0 && i2 <= bArr.length - i) {
                    RFComm.this.checkStream();
                }
                return RFComm.this.DatecsRFComm_read(RFComm.this.mSock, bArr, i, i2);
            }
        };
    }

    public String getName() throws IOException {
        return DatecsRFComm_queryName(this.mBthAddr);
    }

    public OutputStream getOutputStream() {
        return new OutputStream() { // from class: com.yd.electricecollector.printer.bluetooth.RFComm.2
            @Override // java.io.OutputStream
            public synchronized void write(int i) throws IOException {
                int DatecsRFComm_writeByte;
                do {
                    RFComm.this.checkStream();
                    DatecsRFComm_writeByte = RFComm.this.DatecsRFComm_writeByte(RFComm.this.mSock, (byte) (i & 255));
                    if (DatecsRFComm_writeByte < 0) {
                        throw new IOException("Error " + RFComm.access$600());
                    }
                } while (DatecsRFComm_writeByte != 1);
            }

            @Override // java.io.OutputStream
            public synchronized void write(byte[] bArr) throws IOException {
                int i = 0;
                try {
                    if (bArr == null) {
                        throw new NullPointerException();
                    }
                    do {
                        RFComm.this.checkStream();
                        int DatecsRFComm_write = RFComm.this.DatecsRFComm_write(RFComm.this.mSock, bArr, i, bArr.length - i);
                        if (DatecsRFComm_write < 0) {
                            throw new IOException("Error " + RFComm.access$600());
                        }
                        i += DatecsRFComm_write;
                    } while (i != bArr.length);
                } catch (Throwable th) {
                    throw th;
                }
            }

            @Override // java.io.OutputStream
            public synchronized void write(byte[] bArr, int i, int i2) throws IOException {
                int i3 = 0;
                if (bArr == null) {
                    throw new NullPointerException();
                }
                if (i >= 0 && i2 >= 0 && i + i2 <= bArr.length) {
                    do {
                        RFComm.this.checkStream();
                        int DatecsRFComm_write = RFComm.this.DatecsRFComm_write(RFComm.this.mSock, bArr, i3 + i, i2 - i3);
                        if (DatecsRFComm_write < 0) {
                            throw new IOException("Error " + RFComm.access$600());
                        }
                        i3 += DatecsRFComm_write;
                    } while (i3 != i2);
                }
                throw new IndexOutOfBoundsException();
            }
        };
    }

    public boolean isConnected() {
        return this.mSock != -1;
    }
}
