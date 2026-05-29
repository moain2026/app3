package com.p001yd.electricecollector.printer.bluetooth;

/* loaded from: classes4.dex */
public class PrinterFormatter {
    public static final int AnyPrinter = 0;
    public static final int DatecsDPP250 = 1;
    public static final int UnisystemUnsSp1B = 2;
    public int bPrintWidth = 32;
    public String boldText = "";
    public String center = "";
    public String left = "";
    private int printerType = 0;
    public String reset = "";
    public String right = "";
    public int sPrintWidth = 42;
    public String smallText = "";

    public PrinterFormatter(int i) {
        setPrinterType(i);
    }

    public PrinterFormatter(String str) {
        setPrinterType(str);
    }

    public static int getPrinterTypeFromModel(String str) {
        if (str.equalsIgnoreCase("DPP-250")) {
            return 1;
        }
        return str.equalsIgnoreCase("UNS-SP1B") ? 2 : 0;
    }

    public int getPrinterType() {
        return this.printerType;
    }

    public void setPrinterType(int i) {
        this.printerType = i;
        switch (this.printerType) {
            case 1:
                this.reset = "{reset}";
                this.left = "{left}";
                this.right = "{right}";
                this.center = "{center}";
                this.smallText = "";
                this.boldText = "{b}";
                this.sPrintWidth = 32;
                this.bPrintWidth = 32;
                return;
            case 2:
                this.reset = String.valueOf((char) 27) + '@' + String.valueOf((char) 27) + String.valueOf('!') + String.valueOf('P');
                this.left = "";
                this.right = "{right}";
                this.center = "";
                this.smallText = String.valueOf((char) 27) + "!" + String.valueOf('@');
                this.boldText = String.valueOf((char) 27) + "!" + String.valueOf('Q');
                this.sPrintWidth = 24;
                this.bPrintWidth = 24;
                return;
            default:
                this.reset = "";
                this.left = "";
                this.right = "";
                this.center = "";
                this.smallText = "";
                this.boldText = "";
                this.sPrintWidth = 42;
                this.bPrintWidth = 32;
                return;
        }
    }

    public void setPrinterType(String str) {
        setPrinterType(getPrinterTypeFromModel(str));
    }
}
