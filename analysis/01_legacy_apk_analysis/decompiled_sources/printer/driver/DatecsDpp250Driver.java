package com.p001yd.electricecollector.printer.driver;

import android.content.Context;
import com.datecs.api.printer.Printer;
import com.datecs.api.printer.ProtocolAdapter;
import com.p001yd.electricecollector.C1018R;
import com.p001yd.electricecollector.printer.bluetooth.BluetoothConnector;
import com.p001yd.electricecollector.printer.driver.exceptions.BlueToothIsNotAvailableException;
import com.p001yd.electricecollector.printer.driver.exceptions.FailedPrintTextException;
import com.p001yd.electricecollector.printer.driver.exceptions.PrinterNotConnectedException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* loaded from: classes10.dex */
public class DatecsDpp250Driver extends AbstractPrinterDriver {
    public static BluetoothConnector blueToothConnector;
    public static Printer printer;

    public DatecsDpp250Driver(Context context) {
        super(context);
    }

    private Printer getDatecsPrinter(InputStream inputStream, OutputStream outputStream) throws IOException {
        ProtocolAdapter protocolAdapter = new ProtocolAdapter(inputStream, outputStream);
        if (!protocolAdapter.isProtocolEnabled()) {
            return new Printer(inputStream, outputStream);
        }
        ProtocolAdapter.Channel channel = protocolAdapter.getChannel(1);
        return new Printer(channel.getInputStream(), channel.getOutputStream());
    }

    @Override // com.p001yd.electricecollector.printer.driver.AbstractPrinterDriver
    public void closePrinter() {
        printer = null;
        if (blueToothConnector != null) {
            try {
                blueToothConnector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override // com.p001yd.electricecollector.printer.driver.AbstractPrinterDriver
    public void connect(String str) throws IOException, BlueToothIsNotAvailableException, InterruptedException, PrinterNotConnectedException {
        super.connect(str);
        blueToothConnector = BluetoothConnector.getConnector(this.context);
        blueToothConnector.connect(str);
        printer = getDatecsPrinter(blueToothConnector.getInputStream(), blueToothConnector.getOutputStream());
    }

    @Override // com.p001yd.electricecollector.printer.driver.AbstractPrinterDriver
    public Printer getPrinterInstance() {
        return null;
    }

    @Override // com.p001yd.electricecollector.printer.driver.AbstractPrinterDriver
    public boolean isPrinterConnected() {
        return printer != null;
    }

    @Override // com.p001yd.electricecollector.printer.driver.AbstractPrinterDriver
    protected void printInternal(String str) throws FailedPrintTextException, PrinterNotConnectedException {
        try {
            printer.reset();
            printer.printTaggedText(str, this.context.getString(C1018R.string.codepage));
            printer.feedPaper(110);
        } catch (IOException e) {
            throw new FailedPrintTextException(this.context.getString(C1018R.string.failed_print_text) + ". " + e.getMessage());
        } catch (NullPointerException e2) {
            throw new PrinterNotConnectedException(this.context.getString(C1018R.string.printer_not_connected));
        }
    }

    @Override // com.p001yd.electricecollector.printer.driver.AbstractPrinterDriver
    protected void printInternal(byte[] bArr) throws FailedPrintTextException, PrinterNotConnectedException {
        try {
            printer.reset();
            printer.printText(bArr);
            printer.feedPaper(110);
            printer.flush();
        } catch (IOException e) {
            throw new FailedPrintTextException(this.context.getString(C1018R.string.failed_print_text) + ". " + e.getMessage());
        } catch (NullPointerException e2) {
            throw new PrinterNotConnectedException(this.context.getString(C1018R.string.printer_not_connected));
        }
    }
}
