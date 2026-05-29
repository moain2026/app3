package com.p001yd.electricecollector;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import androidx.core.content.FileProvider;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

/* loaded from: classes6.dex */
public class Pdf_temp {
    public static final String FONTss = "assets/dubai.ttf";
    public static Font fSupTitle1 = FontFactory.getFont("assets/dubai.ttf", BaseFont.IDENTITY_H, true);

    /* renamed from: x */
    private static final int f607x = 12;

    /* renamed from: bf */
    BaseFont f608bf;
    private Context context;
    private Document documented;
    Font fTitle1;
    Font fonte;
    Font fonteBlue;
    Font fonteRed;
    Font fontr;
    private String namef;
    private Paragraph paragraph;
    private File pdfFile;
    private PdfWriter pdfWriter;

    /* renamed from: ps */
    private Paragraph f610ps;
    private int type;
    private Uri uri;
    float[] columnWidths = {16.0f, 16.0f, 16.0f, 36.0f, 16.0f};
    private String reportname = null;

    /* renamed from: os */
    private OutputStream f609os = null;

    /* loaded from: classes6.dex */
    public class HeaderFooter extends PdfPageEventHelper {
        Font font1 = new Font(Font.FontFamily.TIMES_ROMAN, 11.0f);
        Font font2 = new Font(Font.FontFamily.TIMES_ROMAN, 11.0f, 1);
        Font font3 = new Font(Font.FontFamily.TIMES_ROMAN, 10.5f);
        Font font4 = new Font(Font.FontFamily.TIMES_ROMAN, 10.5f, 1);
        Font font5 = new Font(Font.FontFamily.TIMES_ROMAN, 7.0f);
        Font font6 = new Font(Font.FontFamily.TIMES_ROMAN, 7.0f, 1);
        Paragraph footer;
        Paragraph header;
        PdfTemplate total;

        public HeaderFooter() {
        }

        @Override // com.itextpdf.text.pdf.PdfPageEventHelper, com.itextpdf.text.pdf.PdfPageEvent
        public void onCloseDocument(PdfWriter pdfWriter, Document document) {
            ColumnText.showTextAligned(this.total, 0, new Phrase(String.valueOf(pdfWriter.getPageNumber() - 1), this.font5), 2.0f, 7.0f, 0.0f);
        }

        @Override // com.itextpdf.text.pdf.PdfPageEventHelper, com.itextpdf.text.pdf.PdfPageEvent
        public void onEndPage(PdfWriter pdfWriter, Document document) {
            PdfPTable pdfPTable = new PdfPTable(1);
            PdfPTable pdfPTable2 = new PdfPTable(1);
            PdfPTable pdfPTable3 = new PdfPTable(1);
            try {
                pdfPTable.setWidths(new int[]{24});
                pdfPTable.setTotalWidth(150.0f);
                pdfPTable.setLockedWidth(true);
                pdfPTable.getDefaultCell().setFixedHeight(130.0f);
                pdfPTable.getDefaultCell().setLeading(0.0f, 1.1f);
                pdfPTable.getDefaultCell().setBorder(2);
                pdfPTable.getDefaultCell().setHorizontalAlignment(0);
                pdfPTable.addCell(this.header);
                pdfPTable.writeSelectedRows(0, -1, 10.0f, pdfPTable.getTotalHeight() + 590.0f, pdfWriter.getDirectContent());
                pdfPTable2.setWidths(new int[]{24});
                pdfPTable2.setTotalWidth(590.0f);
                pdfPTable2.setLockedWidth(true);
                pdfPTable2.getDefaultCell().setFixedHeight(40.0f);
                pdfPTable2.getDefaultCell().setLeading(0.0f, 1.2f);
                pdfPTable2.getDefaultCell().setBorder(1);
                pdfPTable2.getDefaultCell().setHorizontalAlignment(1);
                pdfPTable2.addCell(this.footer);
                pdfPTable2.writeSelectedRows(0, -1, 10.0f, pdfPTable2.getTotalHeight() + 70.0f, pdfWriter.getDirectContent());
                pdfPTable3.setWidths(new int[]{103, 97});
                pdfPTable3.setTotalWidth(590.0f);
                pdfPTable3.setLockedWidth(true);
                pdfPTable3.getDefaultCell().setFixedHeight(20.0f);
                pdfPTable3.getDefaultCell().setBorder(1);
                pdfPTable3.getDefaultCell().setHorizontalAlignment(2);
                pdfPTable3.addCell(new Paragraph(String.format("Page %d of ", Integer.valueOf(pdfWriter.getPageNumber())), this.font5));
                PdfPCell pdfPCell = new PdfPCell(Image.getInstance(this.total));
                pdfPCell.setBorder(1);
                pdfPTable3.addCell(pdfPCell).setHorizontalAlignment(0);
                pdfPTable3.writeSelectedRows(0, -1, 10.0f, pdfPTable3.getTotalHeight(), pdfWriter.getDirectContent());
            } catch (DocumentException e) {
                throw new ExceptionConverter(e);
            }
        }

        @Override // com.itextpdf.text.pdf.PdfPageEventHelper, com.itextpdf.text.pdf.PdfPageEvent
        public void onOpenDocument(PdfWriter pdfWriter, Document document) {
            this.total = pdfWriter.getDirectContent().createTemplate(30.0f, 16.0f);
        }

        public void setFooter(Paragraph paragraph) {
            this.footer = paragraph;
        }

        public void setHeader(Paragraph paragraph) {
            this.header = paragraph;
        }
    }

    public Pdf_temp(Context context, String str, int i) {
        try {
            this.f608bf = BaseFont.createFont("assets/dubai.ttf", "Cp1251", true);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        this.fonte = FontFactory.getFont("assets/dubai.ttf", BaseFont.IDENTITY_H, true, 10.0f);
        this.fonteRed = FontFactory.getFont("assets/dubai.ttf", BaseFont.IDENTITY_H, true, 10.0f, 0, BaseColor.RED);
        this.fonteBlue = FontFactory.getFont("assets/dubai.ttf", BaseFont.IDENTITY_H, true, 10.0f, 0, BaseColor.BLUE);
        this.fontr = FontFactory.getFont("assets/dubai.ttf", BaseFont.IDENTITY_H, true, 12.0f, 4, BaseColor.BLUE);
        this.fTitle1 = FontFactory.getFont("assets/dubai.ttf", BaseFont.IDENTITY_H, true, 16.0f, 4, BaseColor.BLUE);
        this.fTitle1.setSize(16.0f);
        this.fTitle1.setColor(BaseColor.BLUE);
        this.fonte.setSize(12.0f);
        this.context = context;
        this.namef = str;
        this.type = i;
    }

    private static void add(PdfPTable pdfPTable, String str, BaseColor baseColor, int i, Font font) {
        PdfPCell pdfPCell = new PdfPCell(new Phrase(str, font));
        pdfPCell.setBackgroundColor(baseColor);
        pdfPCell.setHorizontalAlignment(1);
        pdfPCell.setFixedHeight(20.0f);
        pdfPTable.addCell(pdfPCell);
    }

    private void addChildP(Paragraph paragraph) {
        paragraph.setAlignment(2);
        this.paragraph.add((Element) paragraph);
    }

    private void addEmptyLine(Paragraph paragraph, int i) {
        for (int i2 = 0; i2 < i; i2++) {
            paragraph.add((Element) new Paragraph(" "));
        }
    }

    private static void addPadding(PdfPTable pdfPTable) {
        PdfPCell pdfPCell = new PdfPCell();
        pdfPCell.setFixedHeight(2.0f);
        pdfPCell.setBorder(0);
        pdfPCell.setColspan(pdfPTable.getNumberOfColumns());
        pdfPTable.addCell(pdfPCell);
    }

    private boolean checkExistPhoto(String str) {
        return new File(Uri.parse(str).getPath()).exists();
    }

    private void createFile() {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Electric_Collector/");
        if (!file.exists()) {
            file.mkdirs();
        }
        this.pdfFile = new File(file, this.namef + ".pdf");
    }

    public static void previewPdf(File file, Context context) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setFlags(268435456);
        Uri uriForFile = FileProvider.getUriForFile(context.getApplicationContext(), "com.yd.electricecollector.fileprovider", file);
        intent.setFlags(2);
        intent.setFlags(1);
        intent.setDataAndType(uriForFile, "application/pdf");
        Intent createChooser = Intent.createChooser(intent, "Open File");
        createChooser.addFlags(268435456);
        try {
            context.getApplicationContext().startActivity(createChooser);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void previewPdfFromUri(Uri uri, Context context) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(uri, "application/pdf");
        intent.setFlags(268435456);
        intent.setFlags(1);
        Intent createChooser = Intent.createChooser(intent, "Open File");
        createChooser.addFlags(268435456);
        try {
            context.getApplicationContext().startActivity(createChooser);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void SplitLastRow() {
        try {
            Document document = new Document();
            document.setPageSize(PageSize.LETTER);
            document.setMargins(16.0f, 14.0f, 14.0f, 14.0f);
            PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Accountant_Book/SplitLastRow.pdf"));
            document.open();
            document.setPageSize(PageSize.f280A4);
            document.setMargins(16.0f, 14.0f, 42.0f, 38.0f);
            for (int i = 1; i < 20; i++) {
                PdfPTable pdfPTable = new PdfPTable(1);
                pdfPTable.setSpacingAfter(0.0f);
                pdfPTable.setSpacingBefore(0.0f);
                pdfPTable.setTotalWidth(document.right() - document.left());
                pdfPTable.setLockedWidth(true);
                pdfPTable.setHeaderRows(1);
                int i2 = 0 + 1;
                add(pdfPTable, "Header Row continued " + i, BaseColor.LIGHT_GRAY, 0, this.fonte);
                int i3 = i2 + 1;
                add(pdfPTable, "Header Row normal " + i, BaseColor.LIGHT_GRAY, i2, this.fonte);
                int i4 = i3 + 1;
                add(pdfPTable, "Text Row 1 ", BaseColor.WHITE, i3, this.fonte);
                int i5 = i4 + 1;
                add(pdfPTable, "Text Row 2 ", BaseColor.WHITE, i4, this.fonte);
                int i6 = i5 + 1;
                add(pdfPTable, "Text Row 3 ", BaseColor.WHITE, i5, this.fonte);
                addPadding(pdfPTable);
                if ((pdfWriter.getVerticalPosition(true) - pdfPTable.getRowHeight(0)) - pdfPTable.getRowHeight(1) < document.bottom()) {
                    document.newPage();
                }
                document.add(pdfPTable);
            }
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addTtle(String str, String str2, String str3) {
        createFile();
    }

    public void addTtleTable(String str, String str2, String str3) {
        createFile();
        try {
            this.paragraph = new Paragraph();
            PdfPTable pdfPTable = new PdfPTable(2);
            pdfPTable.setRunDirection(3);
            pdfPTable.setWidthPercentage(100.0f);
            PdfPTable pdfPTable2 = new PdfPTable(1);
            pdfPTable2.setRunDirection(3);
            pdfPTable2.setWidthPercentage(100.0f);
            PdfPTable pdfPTable3 = new PdfPTable(1);
            pdfPTable3.setRunDirection(3);
            pdfPTable3.setWidthPercentage(100.0f);
            PdfPCell pdfPCell = new PdfPCell(new Phrase(str, this.fTitle1));
            pdfPCell.setHorizontalAlignment(3);
            pdfPCell.setBackgroundColor(BaseColor.WHITE);
            pdfPTable3.addCell(pdfPCell);
            PdfPCell pdfPCell2 = new PdfPCell(new Phrase(str2, this.fontr));
            pdfPCell2.setHorizontalAlignment(3);
            pdfPCell2.setBackgroundColor(BaseColor.WHITE);
            pdfPTable3.addCell(pdfPCell2);
            PdfPCell pdfPCell3 = new PdfPCell(new Phrase(str3, this.fontr));
            pdfPCell3.setHorizontalAlignment(3);
            pdfPCell3.setBackgroundColor(BaseColor.WHITE);
            pdfPTable3.addCell(pdfPCell3);
            pdfPTable.addCell(pdfPTable3);
            pdfPTable.addCell(pdfPTable2);
            this.paragraph.add((Element) pdfPTable);
            this.paragraph.setSpacingAfter(30.0f);
            this.documented.add(this.paragraph);
        } catch (Exception e) {
            Log.e("addParagragh", e.toString());
        }
    }

    public void addmetaData(String str, String str2, String str3) {
        this.documented.addTitle(str);
        this.documented.addSubject(str2);
        this.documented.addAuthor(str3);
    }

    public void addparagragh(String str) {
        createFile();
        try {
            this.paragraph = new Paragraph(str, this.fonte);
            this.paragraph.setFont(this.fontr);
            this.paragraph.setAlignment(3);
            this.paragraph.setSpacingAfter(5.0f);
            this.paragraph.setSpacingBefore(5.0f);
            this.documented.add(this.paragraph);
        } catch (Exception e) {
            Log.e("addParagragh", e.toString());
        }
    }

    public void closDocument() {
        this.documented.close();
    }

    public Uri createAndSavePdfWithMediaStore(Context context, String str) {
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put("_display_name", str);
        contentValues.put("mime_type", "application/pdf");
        if (Build.VERSION.SDK_INT >= 29) {
            contentValues.put("relative_path", Environment.DIRECTORY_DOWNLOADS + File.separator + "Electric_Collector");
        } else {
            contentValues.put("relative_path", Environment.DIRECTORY_DOCUMENTS + File.separator + "Electric_Collector");
        }
        try {
            Uri insert = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
            if (insert == null) {
                return null;
            }
            this.f609os = contentResolver.openOutputStream(insert);
            return insert;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void createFooterTable(float f, float[] fArr, String str, double d, double d2, double d3) throws DocumentException, IOException {
        PdfPTable pdfPTable = new PdfPTable(2);
        pdfPTable.setRunDirection(3);
        pdfPTable.setWidthPercentage(100.0f);
        pdfPTable.setWidths(new float[]{f, 100.0f - f});
        PdfPTable pdfPTable2 = new PdfPTable(fArr);
        pdfPTable2.setRunDirection(3);
        pdfPTable2.setWidthPercentage(100.0f);
        pdfPTable2.setWidths(this.columnWidths);
        pdfPTable2.setHorizontalAlignment(2);
        PdfPCell pdfPCell = new PdfPCell(new Phrase("", this.fonte));
        pdfPCell.setHorizontalAlignment(1);
        pdfPCell.setFixedHeight(20.0f);
        pdfPTable2.addCell(pdfPCell);
        PdfPCell pdfPCell2 = new PdfPCell(new Phrase(NumberFormat.getInstance(Locale.ENGLISH).format(d), this.fontr));
        pdfPCell2.setHorizontalAlignment(1);
        pdfPCell2.setBackgroundColor(new BaseColor(229, 229, 229));
        pdfPTable2.addCell(pdfPCell2);
        PdfPCell pdfPCell3 = new PdfPCell(new Phrase(NumberFormat.getInstance(Locale.ENGLISH).format(d2), this.fontr));
        pdfPCell3.setHorizontalAlignment(1);
        pdfPCell3.setVerticalAlignment(1);
        pdfPCell3.setBackgroundColor(new BaseColor(229, 229, 229));
        pdfPCell3.setFixedHeight(25.0f);
        pdfPTable2.addCell(pdfPCell3);
        PdfPCell pdfPCell4 = new PdfPCell(new Phrase("اجمالي العمليات :", this.fonte));
        pdfPCell4.setHorizontalAlignment(1);
        pdfPCell4.setFixedHeight(20.0f);
        pdfPCell4.setBackgroundColor(new BaseColor(229, 229, 229));
        pdfPTable2.addCell(pdfPCell4);
        this.paragraph.add((Element) pdfPTable2);
        StringBuilder sb = new StringBuilder();
        sb.append(TAPreferences.getCreditDebitCaption(this.context, d3 > 0.0d ? -1 : 1) + "  : ");
        sb.append(NumberFormat.getInstance(Locale.ENGLISH).format(d3));
        PdfPCell pdfPCell5 = new PdfPCell(new Phrase("اجمالي الرصيد : ", this.fontr));
        pdfPCell5.setHorizontalAlignment(3);
        pdfPCell5.setBackgroundColor(BaseColor.WHITE);
        pdfPTable.addCell(pdfPCell5);
        PdfPCell pdfPCell6 = new PdfPCell(new Phrase(sb.toString(), this.fontr));
        pdfPCell6.setHorizontalAlignment(1);
        pdfPCell6.setBackgroundColor(BaseColor.WHITE);
        pdfPCell6.setFixedHeight(20.0f);
        pdfPCell6.setBackgroundColor(new BaseColor(229, 229, 229));
        PdfPTable pdfPTable3 = new PdfPTable(1);
        pdfPTable3.setRunDirection(3);
        pdfPTable3.setWidthPercentage(100.0f);
        pdfPTable.addCell(pdfPCell6);
        this.paragraph.add((Element) pdfPTable);
        this.paragraph.add((Element) pdfPTable3);
        this.documented.add(this.paragraph);
    }

    public void createTable(String[] strArr, float[] fArr, ArrayList<String[]> arrayList, String str, double d, double d2, double d3) throws DocumentException, IOException {
        String[] strArr2 = strArr;
        Object obj = null;
        boolean z = false;
        Object obj2 = null;
        this.columnWidths = fArr;
        this.paragraph = new Paragraph();
        this.paragraph.setFont(this.fontr);
        PdfPTable pdfPTable = new PdfPTable(1);
        pdfPTable.setRunDirection(3);
        pdfPTable.setWidthPercentage(100.0f);
        PdfPTable pdfPTable2 = new PdfPTable(2);
        pdfPTable2.setRunDirection(3);
        pdfPTable2.setWidthPercentage(100.0f);
        pdfPTable2.setWidths(new float[]{25.0f, 75.0f});
        PdfPTable pdfPTable3 = new PdfPTable(strArr2.length);
        pdfPTable3.setRunDirection(3);
        pdfPTable3.setWidthPercentage(100.0f);
        pdfPTable3.setWidths(this.columnWidths);
        pdfPTable3.setHorizontalAlignment(2);
        PdfPTable pdfPTable4 = new PdfPTable(strArr2.length);
        pdfPTable4.setRunDirection(3);
        pdfPTable4.setWidthPercentage(100.0f);
        pdfPTable4.setWidths(this.columnWidths);
        PdfPCell pdfPCell = new PdfPCell(new Phrase("", this.fontr));
        pdfPCell.setHorizontalAlignment(1);
        pdfPCell.setBackgroundColor(BaseColor.WHITE);
        pdfPCell.setBorder(0);
        pdfPCell.setFixedHeight(30.0f);
        pdfPTable.addCell(pdfPCell);
        PdfPTable pdfPTable5 = new PdfPTable(1);
        pdfPTable5.setRunDirection(3);
        pdfPTable5.setWidthPercentage(100.0f);
        this.paragraph.add((Element) pdfPTable);
        int i = 0;
        while (true) {
            Object obj3 = obj;
            boolean z2 = z;
            if (i >= strArr2.length) {
                break;
            }
            PdfPCell pdfPCell2 = new PdfPCell(new Phrase(strArr2[i], this.fontr));
            pdfPCell2.setHorizontalAlignment(1);
            pdfPCell2.setVerticalAlignment(1);
            pdfPCell2.setFixedHeight(25.0f);
            pdfPCell2.setBackgroundColor(new BaseColor(229, 229, 229));
            pdfPTable4.addCell(pdfPCell2);
            i++;
            strArr2 = strArr;
            obj = obj3;
            z = z2;
            obj2 = obj2;
        }
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            int i3 = 0;
            String[] strArr3 = arrayList.get(i2);
            while (i3 < strArr3.length) {
                PdfPCell pdfPCell3 = new PdfPCell(new Phrase(strArr3[i3] + "", this.fonte));
                pdfPCell3.setHorizontalAlignment(1);
                pdfPCell3.setFixedHeight(20.0f);
                pdfPTable4.addCell(pdfPCell3);
                i3++;
                strArr3 = strArr3;
                pdfPTable = pdfPTable;
            }
        }
        this.paragraph.add((Element) pdfPTable4);
        StringBuilder sb = new StringBuilder();
        sb.append(TAPreferences.getCreditDebitCaption(this.context, d3 > 0.0d ? -1 : 1) + "  : ");
        sb.append(NumberFormat.getInstance(Locale.ENGLISH).format(d3));
        PdfPCell pdfPCell4 = new PdfPCell(new Phrase("اجمالي الرصيد : ", this.fontr));
        pdfPCell4.setHorizontalAlignment(3);
        pdfPCell4.setBackgroundColor(BaseColor.WHITE);
        pdfPTable2.addCell(pdfPCell4);
        PdfPCell pdfPCell5 = new PdfPCell(new Phrase(sb.toString(), this.fontr));
        pdfPCell5.setHorizontalAlignment(1);
        pdfPCell5.setBackgroundColor(BaseColor.WHITE);
        pdfPCell5.setFixedHeight(20.0f);
        pdfPCell5.setBackgroundColor(new BaseColor(229, 229, 229));
        pdfPTable2.addCell(pdfPCell5);
        this.paragraph.add((Element) pdfPTable2);
        this.paragraph.add((Element) pdfPTable5);
        this.documented.add(this.paragraph);
    }

    public void createTable2(String[] strArr, float[] fArr, ArrayList<String[]> arrayList, String str, double d, double d2, double d3) throws DocumentException, IOException {
        String[] strArr2 = strArr;
        Object obj = null;
        boolean z = false;
        Object obj2 = null;
        this.columnWidths = fArr;
        this.paragraph = new Paragraph();
        this.paragraph.setFont(this.fontr);
        PdfPTable pdfPTable = new PdfPTable(1);
        pdfPTable.setRunDirection(3);
        pdfPTable.setWidthPercentage(100.0f);
        PdfPTable pdfPTable2 = new PdfPTable(2);
        pdfPTable2.setRunDirection(3);
        pdfPTable2.setWidthPercentage(100.0f);
        pdfPTable2.setWidths(new float[]{51.0f, 48.0f});
        PdfPTable pdfPTable3 = new PdfPTable(strArr2.length);
        pdfPTable3.setRunDirection(3);
        pdfPTable3.setWidthPercentage(100.0f);
        pdfPTable3.setWidths(this.columnWidths);
        pdfPTable3.setHorizontalAlignment(2);
        PdfPTable pdfPTable4 = new PdfPTable(strArr2.length);
        pdfPTable4.setRunDirection(3);
        pdfPTable4.setWidthPercentage(100.0f);
        pdfPTable4.setWidths(this.columnWidths);
        PdfPCell pdfPCell = new PdfPCell(new Phrase("", this.fontr));
        pdfPCell.setHorizontalAlignment(1);
        pdfPCell.setBackgroundColor(BaseColor.WHITE);
        pdfPCell.setBorder(0);
        pdfPCell.setFixedHeight(30.0f);
        pdfPTable.addCell(pdfPCell);
        PdfPTable pdfPTable5 = new PdfPTable(1);
        pdfPTable5.setRunDirection(3);
        pdfPTable5.setWidthPercentage(100.0f);
        this.paragraph.add((Element) pdfPTable);
        int i = 0;
        while (true) {
            Object obj3 = obj;
            boolean z2 = z;
            if (i >= strArr2.length) {
                break;
            }
            PdfPCell pdfPCell2 = new PdfPCell(new Phrase(strArr2[i], this.fontr));
            pdfPCell2.setHorizontalAlignment(1);
            pdfPCell2.setVerticalAlignment(1);
            pdfPCell2.setFixedHeight(25.0f);
            pdfPCell2.setBackgroundColor(new BaseColor(229, 229, 229));
            pdfPTable4.addCell(pdfPCell2);
            i++;
            strArr2 = strArr;
            obj = obj3;
            z = z2;
            obj2 = obj2;
        }
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            int i3 = 0;
            String[] strArr3 = arrayList.get(i2);
            while (i3 < strArr3.length) {
                PdfPCell pdfPCell3 = new PdfPCell(new Phrase(strArr3[i3] + "", this.fonte));
                pdfPCell3.setHorizontalAlignment(1);
                pdfPCell3.setFixedHeight(20.0f);
                pdfPTable4.addCell(pdfPCell3);
                i3++;
                strArr3 = strArr3;
                pdfPTable = pdfPTable;
            }
        }
        this.paragraph.add((Element) pdfPTable4);
        PdfPCell pdfPCell4 = new PdfPCell(new Phrase("اجمالي العمليات :", this.fontr));
        pdfPCell4.setHorizontalAlignment(3);
        pdfPCell4.setFixedHeight(20.0f);
        pdfPTable3.addCell(pdfPCell4);
        PdfPCell pdfPCell5 = new PdfPCell(new Phrase(NumberFormat.getInstance(Locale.ENGLISH).format(d), this.fontr));
        pdfPCell5.setHorizontalAlignment(1);
        pdfPCell5.setBackgroundColor(new BaseColor(229, 229, 229));
        pdfPTable3.addCell(pdfPCell5);
        PdfPCell pdfPCell6 = new PdfPCell(new Phrase(NumberFormat.getInstance(Locale.ENGLISH).format(d2), this.fontr));
        pdfPCell6.setHorizontalAlignment(1);
        pdfPCell6.setVerticalAlignment(1);
        pdfPCell6.setBackgroundColor(new BaseColor(229, 229, 229));
        pdfPCell6.setFixedHeight(25.0f);
        pdfPTable3.addCell(pdfPCell6);
        PdfPCell pdfPCell7 = new PdfPCell(new Phrase("", this.fonte));
        pdfPCell7.setHorizontalAlignment(1);
        pdfPCell7.setFixedHeight(20.0f);
        pdfPCell7.setBackgroundColor(new BaseColor(229, 229, 229));
        pdfPTable3.addCell(pdfPCell7);
        this.paragraph.add((Element) pdfPTable3);
        StringBuilder sb = new StringBuilder();
        sb.append(TAPreferences.getCreditDebitCaption(this.context, d3 > 0.0d ? -1 : 1) + "  : ");
        sb.append(NumberFormat.getInstance(Locale.ENGLISH).format(d3));
        PdfPCell pdfPCell8 = new PdfPCell(new Phrase("اجمالي الرصيد : ", this.fontr));
        pdfPCell8.setHorizontalAlignment(3);
        pdfPCell8.setBackgroundColor(BaseColor.WHITE);
        pdfPTable2.addCell(pdfPCell8);
        PdfPCell pdfPCell9 = new PdfPCell(new Phrase(sb.toString(), this.fontr));
        pdfPCell9.setHorizontalAlignment(1);
        pdfPCell9.setBackgroundColor(BaseColor.WHITE);
        pdfPCell9.setFixedHeight(20.0f);
        pdfPCell9.setBackgroundColor(new BaseColor(229, 229, 229));
        pdfPTable2.addCell(pdfPCell9);
        this.paragraph.add((Element) pdfPTable2);
        this.paragraph.add((Element) pdfPTable5);
        this.documented.add(this.paragraph);
    }

    public void createTable3(String[] strArr, ArrayList<String[]> arrayList, String str, double d, double d2, double d3) throws DocumentException, IOException {
        String[] strArr2 = strArr;
        Object obj = null;
        boolean z = false;
        Object obj2 = null;
        this.paragraph = new Paragraph();
        this.paragraph.setFont(this.fontr);
        PdfPTable pdfPTable = new PdfPTable(1);
        pdfPTable.setRunDirection(3);
        pdfPTable.setWidthPercentage(100.0f);
        PdfPTable pdfPTable2 = new PdfPTable(2);
        pdfPTable2.setRunDirection(3);
        pdfPTable2.setWidthPercentage(100.0f);
        pdfPTable2.setWidths(new float[]{48.0f, 52.0f});
        PdfPTable pdfPTable3 = new PdfPTable(4);
        pdfPTable3.setRunDirection(3);
        pdfPTable3.setWidthPercentage(100.0f);
        pdfPTable3.setWidths(new float[]{16.0f, 16.0f, 16.0f, 52.0f});
        pdfPTable3.setHorizontalAlignment(2);
        PdfPTable pdfPTable4 = new PdfPTable(strArr2.length);
        pdfPTable4.setRunDirection(3);
        pdfPTable4.setWidthPercentage(100.0f);
        pdfPTable4.setWidths(this.columnWidths);
        PdfPCell pdfPCell = new PdfPCell(new Phrase("", this.fontr));
        pdfPCell.setHorizontalAlignment(1);
        pdfPCell.setBackgroundColor(BaseColor.WHITE);
        pdfPCell.setBorder(0);
        pdfPCell.setFixedHeight(30.0f);
        pdfPTable.addCell(pdfPCell);
        PdfPTable pdfPTable5 = new PdfPTable(1);
        pdfPTable5.setRunDirection(3);
        pdfPTable5.setWidthPercentage(100.0f);
        this.paragraph.add((Element) pdfPTable);
        int i = 0;
        while (i < strArr2.length) {
            PdfPCell pdfPCell2 = new PdfPCell(new Phrase(strArr2[i], this.fontr));
            pdfPCell2.setHorizontalAlignment(1);
            pdfPCell2.setVerticalAlignment(1);
            pdfPCell2.setFixedHeight(25.0f);
            pdfPCell2.setNoWrap(true);
            pdfPCell2.setBackgroundColor(new BaseColor(229, 229, 229));
            pdfPTable4.addCell(pdfPCell2);
            i++;
            strArr2 = strArr;
            obj = obj;
        }
        int i2 = 0;
        while (i2 < arrayList.size()) {
            int i3 = 0;
            String[] strArr3 = arrayList.get(i2);
            while (i3 < strArr3.length) {
                StringBuilder sb = new StringBuilder();
                boolean z2 = z;
                sb.append(strArr3[i3]);
                sb.append("");
                Object obj3 = obj2;
                PdfPTable pdfPTable6 = pdfPTable;
                PdfPCell pdfPCell3 = new PdfPCell(new Phrase(sb.toString(), this.fonte));
                if (i3 == 1) {
                    pdfPCell3.setHorizontalAlignment(3);
                    pdfPCell3.setPaddingRight(1.0f);
                    pdfPCell3.setPaddingRight(1.0f);
                } else {
                    pdfPCell3.setHorizontalAlignment(1);
                }
                pdfPCell3.setFixedHeight(30.0f);
                pdfPTable4.addCell(pdfPCell3);
                i3++;
                z = z2;
                obj2 = obj3;
                pdfPTable = pdfPTable6;
            }
            i2++;
            obj2 = obj2;
            pdfPTable = pdfPTable;
        }
        this.paragraph.add((Element) pdfPTable4);
        PdfPCell pdfPCell4 = new PdfPCell(new Phrase("إجمالي العمليات :", this.fonte));
        pdfPCell4.setHorizontalAlignment(0);
        pdfPCell4.setFixedHeight(20.0f);
        pdfPTable3.addCell(pdfPCell4);
        PdfPCell pdfPCell5 = new PdfPCell(new Phrase(NumberFormat.getInstance(Locale.ENGLISH).format(d), this.fontr));
        pdfPCell5.setHorizontalAlignment(1);
        pdfPCell5.setBackgroundColor(new BaseColor(229, 229, 229));
        pdfPTable3.addCell(pdfPCell5);
        PdfPCell pdfPCell6 = new PdfPCell(new Phrase(NumberFormat.getInstance(Locale.ENGLISH).format(d2), this.fontr));
        pdfPCell6.setHorizontalAlignment(1);
        pdfPCell6.setVerticalAlignment(1);
        pdfPCell6.setBackgroundColor(new BaseColor(229, 229, 229));
        pdfPCell6.setFixedHeight(20.0f);
        pdfPTable3.addCell(pdfPCell6);
        PdfPCell pdfPCell7 = new PdfPCell(new Phrase("", this.fonte));
        pdfPCell7.setHorizontalAlignment(1);
        pdfPCell7.setFixedHeight(20.0f);
        pdfPCell7.setBackgroundColor(new BaseColor(229, 229, 229));
        pdfPTable3.addCell(pdfPCell7);
        this.paragraph.add((Element) pdfPTable3);
        StringBuilder sb2 = new StringBuilder();
        sb2.append(TAPreferences.getCreditDebitCaption(this.context, d3 > 0.0d ? 1 : -1) + "  : ");
        sb2.append(NumberFormat.getInstance(Locale.ENGLISH).format(Math.abs(d3)));
        PdfPCell pdfPCell8 = new PdfPCell(new Phrase("اجمالي الرصيد : ", this.fontr));
        pdfPCell8.setHorizontalAlignment(0);
        pdfPCell8.setBackgroundColor(BaseColor.WHITE);
        pdfPTable2.addCell(pdfPCell8);
        PdfPCell pdfPCell9 = new PdfPCell(new Phrase(sb2.toString(), this.fontr));
        pdfPCell9.setHorizontalAlignment(1);
        pdfPCell9.setBackgroundColor(new BaseColor(229, 229, 229));
        pdfPCell9.setFixedHeight(20.0f);
        pdfPTable2.addCell(pdfPCell9);
        this.paragraph.add((Element) pdfPTable2);
        this.paragraph.add((Element) pdfPTable5);
        this.documented.add(this.paragraph);
    }

    public void createTable4(String[] strArr, float[] fArr, ArrayList<String[]> arrayList, String str, double d, double d2, double d3) throws DocumentException, IOException {
        String[] strArr2 = strArr;
        Object obj = null;
        boolean z = false;
        Object obj2 = null;
        this.columnWidths = fArr;
        this.paragraph = new Paragraph();
        this.paragraph.setFont(this.fontr);
        PdfPTable pdfPTable = new PdfPTable(1);
        pdfPTable.setRunDirection(3);
        pdfPTable.setWidthPercentage(100.0f);
        PdfPTable pdfPTable2 = new PdfPTable(2);
        pdfPTable2.setRunDirection(3);
        pdfPTable2.setWidthPercentage(100.0f);
        pdfPTable2.setWidths(new float[]{34.0f, 66.0f});
        PdfPTable pdfPTable3 = new PdfPTable(3);
        pdfPTable3.setRunDirection(3);
        pdfPTable3.setWidthPercentage(100.0f);
        pdfPTable3.setWidths(new float[]{17.0f, 17.0f, 66.0f});
        pdfPTable3.setHorizontalAlignment(2);
        PdfPTable pdfPTable4 = new PdfPTable(strArr2.length);
        pdfPTable4.setRunDirection(3);
        pdfPTable4.setWidthPercentage(100.0f);
        pdfPTable4.setWidths(this.columnWidths);
        PdfPCell pdfPCell = new PdfPCell(new Phrase("", this.fontr));
        pdfPCell.setHorizontalAlignment(1);
        pdfPCell.setBackgroundColor(BaseColor.WHITE);
        pdfPCell.setBorder(0);
        pdfPCell.setFixedHeight(30.0f);
        pdfPTable.addCell(pdfPCell);
        PdfPTable pdfPTable5 = new PdfPTable(1);
        pdfPTable5.setRunDirection(3);
        pdfPTable5.setWidthPercentage(100.0f);
        this.paragraph.add((Element) pdfPTable);
        int i = 0;
        while (true) {
            Object obj3 = obj;
            boolean z2 = z;
            if (i >= strArr2.length) {
                break;
            }
            PdfPCell pdfPCell2 = new PdfPCell(new Phrase(strArr2[i], this.fontr));
            pdfPCell2.setHorizontalAlignment(1);
            pdfPCell2.setVerticalAlignment(1);
            pdfPCell2.setFixedHeight(25.0f);
            pdfPCell2.setBackgroundColor(new BaseColor(229, 229, 229));
            pdfPTable4.addCell(pdfPCell2);
            i++;
            strArr2 = strArr;
            obj = obj3;
            z = z2;
            obj2 = obj2;
        }
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            int i3 = 0;
            String[] strArr3 = arrayList.get(i2);
            while (i3 < strArr3.length) {
                PdfPCell pdfPCell3 = new PdfPCell(new Phrase(strArr3[i3] + "", this.fonte));
                pdfPCell3.setHorizontalAlignment(1);
                pdfPCell3.setFixedHeight(20.0f);
                pdfPTable4.addCell(pdfPCell3);
                i3++;
                strArr3 = strArr3;
                pdfPTable = pdfPTable;
            }
        }
        this.paragraph.add((Element) pdfPTable4);
        PdfPTable pdfPTable6 = new PdfPTable(3);
        pdfPTable6.setWidthPercentage(66.0f);
        pdfPTable6.setRunDirection(3);
        pdfPTable6.setHorizontalAlignment(2);
        PdfPCell pdfPCell4 = new PdfPCell(new Phrase("اجمالي العمليات:", this.fontr));
        pdfPCell4.setHorizontalAlignment(0);
        pdfPCell4.setFixedHeight(20.0f);
        pdfPCell4.setColspan(3);
        pdfPTable6.addCell(pdfPCell4);
        PdfPCell pdfPCell5 = new PdfPCell(new Phrase("", this.fonte));
        pdfPCell5.setHorizontalAlignment(0);
        pdfPCell5.setFixedHeight(20.0f);
        pdfPTable6.addCell(pdfPCell5);
        PdfPCell pdfPCell6 = new PdfPCell(new Phrase("", this.fonte));
        pdfPCell6.setHorizontalAlignment(0);
        pdfPCell6.setFixedHeight(20.0f);
        pdfPCell6.getColspan();
        pdfPTable6.addCell(pdfPCell6);
        pdfPTable3.addCell(pdfPTable6);
        PdfPCell pdfPCell7 = new PdfPCell(new Phrase(NumberFormat.getInstance(Locale.ENGLISH).format(d), this.fontr));
        pdfPCell7.setHorizontalAlignment(1);
        pdfPCell7.setBackgroundColor(new BaseColor(229, 229, 229));
        pdfPTable3.addCell(pdfPCell7);
        PdfPCell pdfPCell8 = new PdfPCell(new Phrase(NumberFormat.getInstance(Locale.ENGLISH).format(d2), this.fontr));
        pdfPCell8.setHorizontalAlignment(1);
        pdfPCell8.setVerticalAlignment(1);
        pdfPCell8.setBackgroundColor(new BaseColor(229, 229, 229));
        pdfPCell8.setFixedHeight(20.0f);
        pdfPTable3.addCell(pdfPCell8);
        this.paragraph.add((Element) pdfPTable3);
        StringBuilder sb = new StringBuilder();
        sb.append(TAPreferences.getCreditDebitCaption(this.context, d3 > 0.0d ? -1 : 1) + "  : ");
        sb.append(NumberFormat.getInstance(Locale.ENGLISH).format(d3));
        PdfPCell pdfPCell9 = new PdfPCell(new Phrase("اجمالي الرصيد : ", this.fontr));
        pdfPCell9.setHorizontalAlignment(3);
        pdfPCell9.setBackgroundColor(BaseColor.WHITE);
        pdfPTable2.addCell(pdfPCell9);
        PdfPCell pdfPCell10 = new PdfPCell(new Phrase(sb.toString(), this.fontr));
        pdfPCell10.setHorizontalAlignment(1);
        pdfPCell10.setBackgroundColor(BaseColor.WHITE);
        pdfPCell10.setFixedHeight(20.0f);
        pdfPCell10.setBackgroundColor(new BaseColor(229, 229, 229));
        pdfPTable2.addCell(pdfPCell10);
        this.paragraph.add((Element) pdfPTable2);
        this.paragraph.add((Element) pdfPTable5);
        this.documented.add(this.paragraph);
    }

    public void createTable5(String[] strArr, ArrayList<String[]> arrayList, String str, double d, double d2, double d3) throws DocumentException, IOException {
        String[] strArr2 = strArr;
        float[] fArr = {17.0f, 49.0f, 17.0f, 17.0f};
        Object obj = null;
        boolean z = false;
        Object obj2 = null;
        this.paragraph = new Paragraph();
        this.paragraph.setFont(this.fontr);
        PdfPTable pdfPTable = new PdfPTable(1);
        pdfPTable.setRunDirection(3);
        pdfPTable.setWidthPercentage(100.0f);
        PdfPTable pdfPTable2 = new PdfPTable(2);
        pdfPTable2.setRunDirection(3);
        pdfPTable2.setWidthPercentage(100.0f);
        pdfPTable2.setWidths(new float[]{51.0f, 49.0f});
        PdfPTable pdfPTable3 = new PdfPTable(4);
        pdfPTable3.setRunDirection(3);
        pdfPTable3.setWidthPercentage(100.0f);
        pdfPTable3.setWidths(new float[]{17.0f, 17.0f, 17.0f, 49.0f});
        pdfPTable3.setHorizontalAlignment(2);
        PdfPTable pdfPTable4 = new PdfPTable(strArr2.length);
        pdfPTable4.setRunDirection(3);
        pdfPTable4.setWidthPercentage(100.0f);
        pdfPTable4.setWidths(fArr);
        PdfPCell pdfPCell = new PdfPCell(new Phrase("", this.fontr));
        pdfPCell.setHorizontalAlignment(1);
        pdfPCell.setBackgroundColor(BaseColor.WHITE);
        pdfPCell.setBorder(0);
        pdfPCell.setFixedHeight(30.0f);
        pdfPTable.addCell(pdfPCell);
        PdfPTable pdfPTable5 = new PdfPTable(1);
        pdfPTable5.setRunDirection(3);
        pdfPTable5.setWidthPercentage(100.0f);
        this.paragraph.add((Element) pdfPTable);
        int i = 0;
        while (true) {
            float[] fArr2 = fArr;
            Object obj3 = obj;
            if (i >= strArr2.length) {
                break;
            }
            PdfPCell pdfPCell2 = new PdfPCell(new Phrase(strArr2[i], this.fontr));
            pdfPCell2.setHorizontalAlignment(1);
            pdfPCell2.setVerticalAlignment(1);
            pdfPCell2.setFixedHeight(25.0f);
            pdfPCell2.setBackgroundColor(new BaseColor(229, 229, 229));
            pdfPTable4.addCell(pdfPCell2);
            i++;
            strArr2 = strArr;
            fArr = fArr2;
            obj = obj3;
            z = z;
            obj2 = obj2;
        }
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            int i3 = 0;
            String[] strArr3 = arrayList.get(i2);
            while (i3 < strArr3.length) {
                PdfPCell pdfPCell3 = new PdfPCell(new Phrase(strArr3[i3] + "", this.fonte));
                pdfPCell3.setHorizontalAlignment(1);
                pdfPCell3.setFixedHeight(20.0f);
                pdfPTable4.addCell(pdfPCell3);
                i3++;
                strArr3 = strArr3;
                pdfPTable = pdfPTable;
            }
        }
        this.paragraph.add((Element) pdfPTable4);
        StringBuilder sb = new StringBuilder();
        sb.append(TAPreferences.getCreditDebitCaption(this.context, d3 > 0.0d ? 1 : -1) + "  : ");
        sb.append(NumberFormat.getInstance(Locale.ENGLISH).format(d3));
        PdfPCell pdfPCell4 = new PdfPCell(new Phrase("اجمالي الرصيد : ", this.fontr));
        pdfPCell4.setHorizontalAlignment(0);
        pdfPCell4.setBackgroundColor(BaseColor.WHITE);
        pdfPTable2.addCell(pdfPCell4);
        PdfPCell pdfPCell5 = new PdfPCell(new Phrase(sb.toString(), this.fontr));
        pdfPCell5.setHorizontalAlignment(1);
        pdfPCell5.setBackgroundColor(new BaseColor(229, 229, 229));
        pdfPCell5.setFixedHeight(20.0f);
        pdfPTable2.addCell(pdfPCell5);
        this.paragraph.add((Element) pdfPTable2);
        this.paragraph.add((Element) pdfPTable5);
        this.documented.add(this.paragraph);
    }

    public void openDocument() {
        if (Build.VERSION.SDK_INT >= 29) {
            this.uri = createAndSavePdfWithMediaStore(this.context, this.namef);
        } else {
            createFile();
            try {
                this.f609os = new FileOutputStream(this.pdfFile);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        if (this.f609os != null) {
            try {
                HeaderFooterPageEvent headerFooterPageEvent = new HeaderFooterPageEvent(this.context, this.reportname);
                this.documented = new Document(PageSize.f280A4);
                this.documented.setMargins(15.0f, 15.0f, headerFooterPageEvent.getTableHeight() + 20.0f, 45.0f);
                this.pdfWriter = PdfWriter.getInstance(this.documented, this.f609os);
                this.pdfWriter.setPageEvent(headerFooterPageEvent);
                this.documented.open();
            } catch (Exception e2) {
                Log.e("addParagragh", e2.toString());
            }
        }
    }

    public File savePdfToAppInternalStorage(Context context, String str) {
        return new File(context.getFilesDir(), str + ".pdf");
    }

    public void setReportName(String str) {
        this.reportname = str;
    }

    public void testChangingMargins() throws IOException, DocumentException {
        StringBuilder sb = new StringBuilder("test");
        for (int i = 0; i < 100; i++) {
            sb.append(" test");
        }
        String sb2 = sb.toString();
        FileOutputStream fileOutputStream = new FileOutputStream(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Accountant_Book/", "ChangingMargins.pdf"));
        try {
            Document document = new Document(PageSize.f280A4.rotate(), 0.0f, 0.0f, 0.0f, 0.0f);
            PdfWriter.getInstance(document, fileOutputStream);
            document.open();
            for (int i2 = 0; i2 < document.getPageSize().getWidth() / 2.0f && i2 < document.getPageSize().getHeight() / 2.0f; i2 += 100) {
                document.setMargins(i2, i2, i2, i2);
                document.newPage();
                document.add(new Paragraph(sb2));
            }
            document.close();
            fileOutputStream.close();
        } catch (Throwable th) {
            try {
                fileOutputStream.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    public void viewPdf() {
        if (Build.VERSION.SDK_INT >= 29) {
            previewPdfFromUri(this.uri, this.context);
        } else {
            previewPdf(this.pdfFile, this.context);
        }
    }
}
