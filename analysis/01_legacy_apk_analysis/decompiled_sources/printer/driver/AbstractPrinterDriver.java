package com.p001yd.electricecollector.printer.driver;

import android.content.Context;
import com.datecs.api.printer.Printer;
import com.p001yd.electricecollector.printer.driver.exceptions.BlueToothIsNotAvailableException;
import com.p001yd.electricecollector.printer.driver.exceptions.FailedPrintTextException;
import com.p001yd.electricecollector.printer.driver.exceptions.PrinterNotConnectedException;
import java.io.IOException;

/* loaded from: classes10.dex */
public abstract class AbstractPrinterDriver {
    public static final int AnyPrinter = 0;
    public static final int DatecsDPP250 = 1;
    public static final int JP5802Printer = 3;
    public static final int UnisystemUnsSp1B = 2;
    protected Context context;
    protected String deviceAddress;

    public AbstractPrinterDriver(Context context) {
        this.context = context;
    }

    public static int getPrinterTypeFromModel(String str) {
        if (str.equalsIgnoreCase("DPP-250")) {
            return 1;
        }
        if (str.equalsIgnoreCase("UNS-SP1B")) {
            return 2;
        }
        return str.equalsIgnoreCase("BlueTooth Printer") ? 3 : 0;
    }

    public abstract void closePrinter();

    public void connect(String str) throws IOException, BlueToothIsNotAvailableException, InterruptedException, PrinterNotConnectedException {
        this.deviceAddress = str;
    }

    public Context getContext() {
        return this.context;
    }

    public String getDeviceAddress() {
        return this.deviceAddress;
    }

    public abstract Printer getPrinterInstance();

    public abstract boolean isPrinterConnected();

    public void print(String str) throws FailedPrintTextException, PrinterNotConnectedException {
        try {
            printInternal(str);
        } catch (Exception e) {
            closePrinter();
            throw e;
        }
    }

    public void print(byte[] bArr) throws FailedPrintTextException, PrinterNotConnectedException {
        try {
            printInternal(bArr);
        } catch (Exception e) {
            closePrinter();
            throw e;
        }
    }

    protected abstract void printInternal(String str) throws FailedPrintTextException, PrinterNotConnectedException;

    protected abstract void printInternal(byte[] bArr) throws FailedPrintTextException, PrinterNotConnectedException;
}
