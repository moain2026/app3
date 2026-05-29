package com.p001yd.electricecollector.printer.driver;

import android.content.Context;
import com.p001yd.electricecollector.TAPreferences;

/* loaded from: classes10.dex */
public class PrinterDriverFactory {
    public static AbstractPrinterDriver create(Context context) {
        switch (AbstractPrinterDriver.getPrinterTypeFromModel(TAPreferences.getSetectedPrinterModel(context))) {
            case 1:
                return new DatecsDpp250Driver(context);
            case 2:
                return new DatecsDpp250Driver(context);
            case 3:
                return new JP5802Driver(context);
            default:
                return new JP5802Driver(context);
        }
    }
}
