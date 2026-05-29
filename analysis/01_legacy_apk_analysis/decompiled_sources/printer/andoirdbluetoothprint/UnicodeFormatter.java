package com.p001yd.electricecollector.printer.andoirdbluetoothprint;

import com.itextpdf.text.pdf.Barcode128;
import com.itextpdf.text.pdf.BidiOrder;
import com.itextpdf.text.pdf.PdfWriter;

/* loaded from: classes11.dex */
public class UnicodeFormatter {
    public static String byteToHex(byte b) {
        char[] cArr = {'0', '1', PdfWriter.VERSION_1_2, PdfWriter.VERSION_1_3, PdfWriter.VERSION_1_4, PdfWriter.VERSION_1_5, PdfWriter.VERSION_1_6, PdfWriter.VERSION_1_7, '8', '9', 'a', 'b', Barcode128.CODE_AB_TO_C, Barcode128.CODE_AC_TO_B, Barcode128.CODE_BC_TO_A, Barcode128.FNC1_INDEX};
        return new String(new char[]{cArr[(b >> 4) & 15], cArr[b & BidiOrder.f341B]});
    }

    public static String charToHex(char c) {
        return byteToHex((byte) (c >>> '\b')) + byteToHex((byte) (c & 255));
    }
}
