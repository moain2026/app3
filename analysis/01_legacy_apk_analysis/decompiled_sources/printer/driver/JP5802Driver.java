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
public class JP5802Driver extends AbstractPrinterDriver {
    private static BluetoothConnector blueToothConnector;
    private static MiniThermalPrinter printer;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes10.dex */
    public class MiniThermalPrinter extends Printer {
        private OutputStream mOStream;

        public MiniThermalPrinter(InputStream inputStream, OutputStream outputStream) throws IOException {
            super(inputStream, outputStream);
            this.mOStream = outputStream;
        }

        @Override // com.datecs.api.printer.Printer
        public void reset() throws IOException {
            byte[] bArr = {27, 64, 27, 50, 27, 33, 0, 29, 76, 0, 0, 28, 46, 27, 116, 23};
            synchronized (this) {
                this.mOStream.write(bArr);
            }
        }
    }

    public JP5802Driver(Context context) {
        super(context);
    }

    private MiniThermalPrinter getDatecsPrinter(InputStream inputStream, OutputStream outputStream) throws IOException {
        ProtocolAdapter protocolAdapter = new ProtocolAdapter(inputStream, outputStream);
        if (!protocolAdapter.isProtocolEnabled()) {
            return new MiniThermalPrinter(inputStream, outputStream);
        }
        ProtocolAdapter.Channel channel = protocolAdapter.getChannel(1);
        return new MiniThermalPrinter(channel.getInputStream(), channel.getOutputStream());
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
        return printer;
    }

    @Override // com.p001yd.electricecollector.printer.driver.AbstractPrinterDriver
    public boolean isPrinterConnected() {
        return printer != null;
    }

    @Override // com.p001yd.electricecollector.printer.driver.AbstractPrinterDriver
    protected void printInternal(String str) throws FailedPrintTextException, PrinterNotConnectedException {
        try {
            printer.reset();
            printer.printText(str);
            printer.feedPaper(110);
            printer.flush();
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
