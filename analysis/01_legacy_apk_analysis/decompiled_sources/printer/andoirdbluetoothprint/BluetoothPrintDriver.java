package com.p001yd.electricecollector.printer.andoirdbluetoothprint;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import com.itextpdf.text.pdf.BidiOrder;
import com.itextpdf.text.pdf.ByteBuffer;
import cz.msebera.android.httpclient.message.TokenParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.UUID;
import kotlin.jvm.internal.ByteCompanionObject;

/* loaded from: classes11.dex */
public class BluetoothPrintDriver {
    public static final int Code128_B = 732;
    private static final int DEFAULT_CMD_BUFFER_LEN = 1048576;
    public static boolean TextPosWinStyle;
    private static int mIndex;
    private static BluetoothAdapter myBluetoothAdapter;
    private static BluetoothDevice myDevice;
    private static InputStream myInStream;
    private static OutputStream myOutStream;
    private static BluetoothSocket mySocket;
    public static String ErrorMessage = "No_Error_Message";
    private static byte[] mCmdBuffer = new byte[1048576];
    private static UUID applicationUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public static void AddAlignMode(byte b) {
        byte[] bArr = mCmdBuffer;
        int i = mIndex;
        int i2 = i + 1;
        mIndex = i2;
        bArr[i] = 27;
        int i3 = i2 + 1;
        mIndex = i3;
        bArr[i2] = 97;
        mIndex = i3 + 1;
        bArr[i3] = b;
    }

    public static void AddBold(byte b) {
        byte[] bArr = mCmdBuffer;
        int i = mIndex;
        int i2 = i + 1;
        mIndex = i2;
        bArr[i] = 27;
        int i3 = i2 + 1;
        mIndex = i3;
        bArr[i2] = 69;
        mIndex = i3 + 1;
        bArr[i3] = b;
    }

    public static void AddCodePrint(int i, String str) {
        if (i != 732) {
            return;
        }
        Code128_B(str);
    }

    public static void AddInverse(byte b) {
        byte[] bArr = mCmdBuffer;
        int i = mIndex;
        int i2 = i + 1;
        mIndex = i2;
        bArr[i] = 29;
        int i3 = i2 + 1;
        mIndex = i3;
        bArr[i2] = 66;
        mIndex = i3 + 1;
        bArr[i3] = b;
    }

    public static void Begin() throws IOException {
        WakeUpPritner();
        InitPrinter();
        ClearData();
    }

    public static void ClearData() {
        mIndex = 0;
    }

    public static void Code128_B(String str) {
        int length = str.length();
        byte[] bArr = mCmdBuffer;
        int i = mIndex;
        int i2 = i + 1;
        mIndex = i2;
        bArr[i] = 29;
        int i3 = i2 + 1;
        mIndex = i3;
        bArr[i2] = 107;
        int i4 = i3 + 1;
        mIndex = i4;
        bArr[i3] = 73;
        int i5 = i4 + 1;
        mIndex = i5;
        int i6 = i5 + 1;
        mIndex = i6;
        bArr[i5] = 123;
        mIndex = i6 + 1;
        bArr[i6] = 66;
        int i7 = 0;
        int i8 = 0;
        while (i8 < length) {
            byte[] bArr2 = bArr;
            int i9 = i;
            if (str.charAt(i8) > 127 || str.charAt(i8) < ' ') {
                return;
            }
            i8++;
            bArr = bArr2;
            i = i9;
        }
        if (length > 30) {
            return;
        }
        int i10 = 0;
        int i11 = 0;
        while (i10 < length) {
            byte[] bArr3 = bArr;
            int i12 = i;
            byte[] bArr4 = mCmdBuffer;
            int i13 = mIndex;
            mIndex = i13 + 1;
            bArr4[i13] = (byte) str.charAt(i10);
            if (str.charAt(i10) == '{') {
                byte[] bArr5 = mCmdBuffer;
                int i14 = mIndex;
                mIndex = i14 + 1;
                bArr5[i14] = (byte) str.charAt(i10);
                i11++;
            }
            i10++;
            bArr = bArr3;
            i = i12;
        }
        int i15 = 104;
        int i16 = 1;
        while (i7 < length) {
            i15 += (str.charAt(i7) - TokenParser.f713SP) * i16;
            i7++;
            i16++;
            bArr = bArr;
            i = i;
        }
        int i17 = i15 % 103;
        if (i17 >= 0 && i17 <= 95) {
            byte[] bArr6 = mCmdBuffer;
            int i18 = mIndex;
            mIndex = i18 + 1;
            bArr6[i18] = (byte) (i17 + 32);
            bArr6[i4] = (byte) (length + 3 + i11);
            return;
        }
        if (i17 == 96) {
            byte[] bArr7 = mCmdBuffer;
            int i19 = mIndex;
            int i20 = i19 + 1;
            mIndex = i20;
            bArr7[i19] = 123;
            mIndex = i20 + 1;
            bArr7[i20] = 51;
            bArr7[i4] = (byte) (i11 + length + 4);
            return;
        }
        if (i17 == 97) {
            byte[] bArr8 = mCmdBuffer;
            int i21 = mIndex;
            int i22 = i21 + 1;
            mIndex = i22;
            bArr8[i21] = 123;
            mIndex = i22 + 1;
            bArr8[i22] = 50;
            bArr8[i4] = (byte) (length + 4 + i11);
            return;
        }
        if (i17 == 98) {
            byte[] bArr9 = mCmdBuffer;
            int i23 = mIndex;
            int i24 = i23 + 1;
            mIndex = i24;
            bArr9[i23] = 123;
            mIndex = i24 + 1;
            bArr9[i24] = 83;
            bArr9[i4] = (byte) (length + 4 + i11);
            return;
        }
        if (i17 == 99) {
            byte[] bArr10 = mCmdBuffer;
            int i25 = mIndex;
            int i26 = i25 + 1;
            mIndex = i26;
            bArr10[i25] = 123;
            mIndex = i26 + 1;
            bArr10[i26] = 67;
            bArr10[i4] = (byte) (length + 4 + i11);
            return;
        }
        if (i17 == 100) {
            byte[] bArr11 = mCmdBuffer;
            int i27 = mIndex;
            int i28 = i27 + 1;
            mIndex = i28;
            bArr11[i27] = 123;
            mIndex = i28 + 1;
            bArr11[i28] = 52;
            bArr11[i4] = (byte) (length + 4 + i11);
            return;
        }
        if (i17 == 101) {
            byte[] bArr12 = mCmdBuffer;
            int i29 = mIndex;
            int i30 = i29 + 1;
            mIndex = i30;
            bArr12[i29] = 123;
            mIndex = i30 + 1;
            bArr12[i30] = 65;
            bArr12[i4] = (byte) (length + 4 + i11);
            return;
        }
        if (i17 == 102) {
            byte[] bArr13 = mCmdBuffer;
            int i31 = mIndex;
            int i32 = i31 + 1;
            mIndex = i32;
            bArr13[i31] = 123;
            mIndex = i32 + 1;
            bArr13[i32] = 49;
            bArr13[i4] = (byte) (length + 4 + i11);
        }
    }

    public static void ImportData(String str) {
        byte[] bArr;
        try {
            bArr = str.getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            bArr = null;
        }
        for (byte b : bArr) {
            byte[] bArr2 = mCmdBuffer;
            int i = mIndex;
            mIndex = i + 1;
            bArr2[i] = b;
        }
    }

    public static void ImportData(String str, boolean z) {
        throw new IllegalStateException("Decompilation failed");
    }

    public static void ImportData(byte[] bArr, int i) {
        for (int i2 = 0; i2 < i; i2++) {
            byte[] bArr2 = mCmdBuffer;
            int i3 = mIndex;
            mIndex = i3 + 1;
            bArr2[i3] = bArr[i2];
        }
    }

    public static boolean InitPrinter() throws IOException {
        byte[] bArr = {27, 64};
        OutputStream outputStream = myOutStream;
        if (outputStream == null) {
            return false;
        }
        outputStream.write(bArr);
        return true;
    }

    public static boolean IsNoConnection() {
        return myOutStream == null;
    }

    /* renamed from: LF */
    public static void m209LF() {
        byte[] bArr = mCmdBuffer;
        int i = mIndex;
        mIndex = i + 1;
        bArr[i] = 10;
    }

    public static boolean OpenPrinter(BluetoothSocket bluetoothSocket, OutputStream outputStream, BluetoothDevice bluetoothDevice, BluetoothAdapter bluetoothAdapter) {
        myBluetoothAdapter = bluetoothAdapter;
        myDevice = bluetoothDevice;
        myOutStream = outputStream;
        mySocket = bluetoothSocket;
        return true;
    }

    public static boolean OpenPrinter(String str) {
        if (str == "") {
            ErrorMessage = "There is no available printer";
            return false;
        }
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        myBluetoothAdapter = defaultAdapter;
        if (defaultAdapter == null) {
            ErrorMessage = "Bluetooth system error";
            return false;
        }
        BluetoothDevice remoteDevice = defaultAdapter.getRemoteDevice(str);
        myDevice = remoteDevice;
        if (remoteDevice != null) {
            return SPPOpen(myBluetoothAdapter, remoteDevice);
        }
        ErrorMessage = "Read Bluetooth device error";
        return false;
    }

    private static boolean SPPClose() {
        throw new IllegalStateException("Decompilation failed");
    }

    private static void SPPFlush() {
        throw new IllegalStateException("Decompilation failed");
    }

    private static boolean SPPOpen(BluetoothAdapter bluetoothAdapter, BluetoothDevice bluetoothDevice) {
        try {
            mySocket = bluetoothDevice.createRfcommSocketToServiceRecord(applicationUUID);
            bluetoothAdapter.cancelDiscovery();
            mySocket.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            myOutStream = mySocket.getOutputStream();
            return true;
        } catch (IOException e2) {
            e2.printStackTrace();
            return true;
        }
    }

    public static boolean SPPReadTimeout(byte[] bArr, int i, int i2) {
        return false;
    }

    public static boolean SPPWrite(byte[] bArr) {
        try {
            myOutStream.write(bArr);
            return true;
        } catch (IOException e) {
            ErrorMessage = "Failed to send Bluetooth data";
            return false;
        }
    }

    public static boolean SPPWrite(byte[] bArr, int i) {
        try {
            myOutStream.write(bArr, 0, i);
            return true;
        } catch (IOException e) {
            ErrorMessage = "Failed to send Bluetooth data";
            return false;
        }
    }

    public static void SetCharacterFont(byte b) {
        byte[] bArr = mCmdBuffer;
        int i = mIndex;
        int i2 = i + 1;
        mIndex = i2;
        bArr[i] = 27;
        int i3 = i2 + 1;
        mIndex = i3;
        bArr[i2] = 77;
        mIndex = i3 + 1;
        bArr[i3] = b;
    }

    public static void SetLineSpace(byte b) {
        byte[] bArr = mCmdBuffer;
        int i = mIndex;
        int i2 = i + 1;
        mIndex = i2;
        bArr[i] = 27;
        int i3 = i2 + 1;
        mIndex = i3;
        bArr[i2] = 51;
        mIndex = i3 + 1;
        bArr[i3] = 3;
    }

    public static void SetUnderline(byte b) {
        byte[] bArr = mCmdBuffer;
        int i = mIndex;
        int i2 = i + 1;
        mIndex = i2;
        bArr[i] = 27;
        int i3 = i2 + 1;
        mIndex = i3;
        bArr[i2] = 45;
        mIndex = i3 + 1;
        bArr[i3] = b;
    }

    public static void SetZoom(byte b) {
        byte[] bArr = mCmdBuffer;
        int i = mIndex;
        int i2 = i + 1;
        mIndex = i2;
        bArr[i] = 29;
        int i3 = i2 + 1;
        mIndex = i3;
        bArr[i2] = 33;
        mIndex = i3 + 1;
        bArr[i3] = b;
    }

    public static void WakeUpPritner() {
        try {
            myOutStream.write(new byte[3]);
            Thread.sleep(100L);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void close() {
        SPPClose();
    }

    public static boolean excute() {
        int i = mIndex;
        if (i > 0) {
            try {
                myOutStream.write(mCmdBuffer, 0, i);
                myOutStream.flush();
                mIndex = 0;
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean open(BluetoothAdapter bluetoothAdapter, BluetoothDevice bluetoothDevice) {
        return SPPOpen(bluetoothAdapter, bluetoothDevice);
    }

    public static void printByteData(byte[] bArr) {
        SPPWrite(bArr);
        SPPWrite(new byte[]{10});
    }

    public static void printImage() {
        printParameterSet(new byte[]{27, 64});
        printParameterSet(new byte[]{27, 33});
        printByteData(new byte[]{27, 64, 27, 74, 24, 29, 118, ByteBuffer.ZERO, 0, BidiOrder.f350S, 0, ByteCompanionObject.MIN_VALUE, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -9, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -13, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -15, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, ByteCompanionObject.MAX_VALUE, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, 63, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, 31, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, BidiOrder.f341B, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, 0, ByteCompanionObject.MAX_VALUE, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, 0, 63, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, 0, 31, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, 0, BidiOrder.f341B, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, 0, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, 0, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, 0, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, 8, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, BidiOrder.f343CS, 0, ByteCompanionObject.MAX_VALUE, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, BidiOrder.f342BN, 0, 63, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, BidiOrder.f341B, 0, 31, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -13, -1, -16, BidiOrder.f341B, ByteCompanionObject.MIN_VALUE, BidiOrder.f341B, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -31, -1, -16, BidiOrder.f341B, -64, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -64, -1, -16, BidiOrder.f341B, -32, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, ByteCompanionObject.MIN_VALUE, ByteCompanionObject.MAX_VALUE, -16, BidiOrder.f341B, -16, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 63, -16, BidiOrder.f341B, -8, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 31, -16, BidiOrder.f341B, -8, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, ByteCompanionObject.MIN_VALUE, BidiOrder.f341B, -16, BidiOrder.f341B, -16, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -64, 7, -16, BidiOrder.f341B, -32, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -32, 3, -16, BidiOrder.f341B, -64, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, 1, -16, BidiOrder.f341B, ByteCompanionObject.MIN_VALUE, BidiOrder.f341B, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -8, 0, -16, BidiOrder.f341B, 0, 31, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -4, 0, 112, BidiOrder.f342BN, 0, 63, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -2, 0, ByteBuffer.ZERO, BidiOrder.f343CS, 0, ByteCompanionObject.MAX_VALUE, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, BidiOrder.f350S, 8, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, ByteCompanionObject.MIN_VALUE, 0, 0, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -64, 0, 0, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -32, 0, 0, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, 0, 0, BidiOrder.f341B, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -8, 0, 0, 31, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -4, 0, 0, 63, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -2, 0, 0, ByteCompanionObject.MAX_VALUE, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, -1, -1, 
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, ByteCompanionObject.MIN_VALUE, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -64, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -64, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, ByteCompanionObject.MIN_VALUE, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -2, 0, 0, ByteCompanionObject.MAX_VALUE, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -4, 0, 0, 63, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -8, 0, 0, 31, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, 0, 0, BidiOrder.f341B, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -32, 0, 0, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -64, 0, 0, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, ByteCompanionObject.MIN_VALUE, 0, 0, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, BidiOrder.f350S, 8, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -2, 0, ByteBuffer.ZERO, BidiOrder.f343CS, 0, ByteCompanionObject.MAX_VALUE, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -4, 0, 112, BidiOrder.f342BN, 0, 63, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -8, 0, -16, BidiOrder.f341B, 0, 31, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, 1, -16, BidiOrder.f341B, ByteCompanionObject.MIN_VALUE, BidiOrder.f341B, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -32, 3, -16, BidiOrder.f341B, -64, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -64, 7, -16, BidiOrder.f341B, -32, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, ByteCompanionObject.MIN_VALUE, BidiOrder.f341B, -16, BidiOrder.f341B, -16, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 31, -16, BidiOrder.f341B, -8, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 63, -16, BidiOrder.f341B, -4, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, ByteCompanionObject.MIN_VALUE, ByteCompanionObject.MAX_VALUE, -16, BidiOrder.f341B, -8, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -64, -1, -16, BidiOrder.f341B, -16, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -31, -1, -16, BidiOrder.f341B, -32, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -13, -1, -16, BidiOrder.f341B, -64, BidiOrder.f341B, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, BidiOrder.f341B, ByteCompanionObject.MIN_VALUE, 31, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, BidiOrder.f341B, 0, 63, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, BidiOrder.f342BN, 0, ByteCompanionObject.MAX_VALUE, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, BidiOrder.f343CS, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, 8, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, 0, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, 0, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, 0, BidiOrder.f341B, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, 0, 31, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, 0, 63, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, 0, ByteCompanionObject.MAX_VALUE, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, BidiOrder.f341B, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, 31, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, 63, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, ByteCompanionObject.MAX_VALUE, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -15, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -13, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -9, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 10});
        printString("");
        printParameterSet(new byte[]{27, 64});
        printParameterSet(new byte[]{27, 97});
    }

    public static void printParameterSet(byte[] bArr) {
        SPPWrite(bArr);
    }

    public static void printString(String str) {
        try {
            SPPWrite(str.getBytes("GBK"));
            SPPWrite(new byte[]{10});
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static boolean zp_open(BluetoothAdapter bluetoothAdapter, BluetoothDevice bluetoothDevice) {
        return SPPOpen(bluetoothAdapter, bluetoothDevice);
    }
}
