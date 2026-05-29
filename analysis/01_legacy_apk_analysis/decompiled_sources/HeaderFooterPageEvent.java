package com.p001yd.electricecollector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import com.itextpdf.text.BadElementException;
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
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/* loaded from: classes6.dex */
public class HeaderFooterPageEvent extends PdfPageEventHelper {
    public static final String FONTss = "assets/dubai.ttf";
    public static Font fSupTitle1;

    /* renamed from: bf */
    BaseFont f604bf;
    Context context;
    PdfPTable header;
    private String report_name;

    /* renamed from: t */
    PdfTemplate f605t;
    Image total;

    /* renamed from: x */
    final int f606x = 12;
    float tableHeight = 0.0f;
    Font fonte = FontFactory.getFont("assets/dubai.ttf", BaseFont.IDENTITY_H, true, 10.0f);
    Font fontr = FontFactory.getFont("assets/dubai.ttf", BaseFont.IDENTITY_H, true, 12.0f, 4, BaseColor.BLUE);
    Font fTitle1 = FontFactory.getFont("assets/dubai.ttf", BaseFont.IDENTITY_H, true, 16.0f, 4, BaseColor.BLUE);

    public HeaderFooterPageEvent(Context context, String str) throws IOException, DocumentException {
        this.context = context;
        this.report_name = str;
        addHeader();
    }

    private void addFooter(PdfWriter pdfWriter, Document document) {
        PdfPTable pdfPTable = new PdfPTable(3);
        try {
            pdfPTable.setWidths(new int[]{1, 1, 1});
            pdfPTable.setTotalWidth(PageSize.f280A4.getWidth() - 30.0f);
            pdfPTable.setLockedWidth(true);
            pdfPTable.getDefaultCell().setBorder(1);
            pdfPTable.getDefaultCell().setBorderColor(BaseColor.LIGHT_GRAY);
            Font font = FontFactory.getFont("assets/dubai.ttf", BaseFont.IDENTITY_H, true, 10.0f, 4);
            PdfPCell pdfPCell = new PdfPCell();
            pdfPCell.setBorder(1);
            pdfPCell.setPaddingTop(4.1f);
            pdfPCell.setBorderColor(BaseColor.LIGHT_GRAY);
            Paragraph paragraph = new Paragraph(String.valueOf(Utils.getDateToStringNullTime(Utils.getCalendarSystem(this.context).getTime())), new Font(Font.FontFamily.HELVETICA, 8.0f));
            paragraph.setAlignment(0);
            pdfPCell.addElement(paragraph);
            pdfPTable.addCell(pdfPCell);
            PdfPTable pdfPTable2 = new PdfPTable(2);
            pdfPTable2.setWidths(new int[]{10, 10});
            pdfPTable2.getDefaultCell().setFixedHeight(40.0f);
            pdfPTable2.getDefaultCell().setBorderColor(BaseColor.LIGHT_GRAY);
            PdfPCell pdfPCell2 = new PdfPCell();
            pdfPCell2.setBorder(0);
            Paragraph paragraph2 = new Paragraph(new Phrase(String.format("%s / ", Integer.valueOf(pdfWriter.getPageNumber())), new Font(Font.FontFamily.HELVETICA, 8.0f)));
            paragraph2.setAlignment(2);
            pdfPCell2.addElement(paragraph2);
            pdfPTable2.addCell(pdfPCell2);
            PdfPCell pdfPCell3 = new PdfPCell(this.total);
            pdfPCell3.setPaddingTop(3.85f);
            pdfPCell3.setBorder(0);
            pdfPCell3.setBorderColor(BaseColor.LIGHT_GRAY);
            pdfPTable2.addCell(pdfPCell3);
            pdfPTable.addCell(pdfPTable2);
            PdfPCell pdfPCell4 = new PdfPCell();
            pdfPCell4.setBorder(1);
            pdfPCell4.setRowspan(1);
            pdfPCell4.setBorderColor(BaseColor.LIGHT_GRAY);
            pdfPCell4.setRunDirection(3);
            Paragraph paragraph3 = new Paragraph("YDsoft", font);
            paragraph3.setAlignment(0);
            pdfPCell4.addElement(paragraph3);
            pdfPTable.addCell(pdfPCell4);
            PdfContentByte directContent = pdfWriter.getDirectContent();
            directContent.beginMarkedContentSequence(PdfName.ARTIFACT);
            pdfPTable.writeSelectedRows(0, -1, 15.0f, 50.0f, directContent);
            directContent.endMarkedContentSequence();
        } catch (DocumentException e) {
            throw new ExceptionConverter(e);
        }
    }

    private void addHeader() throws DocumentException, IOException {
        Paragraph paragraph = new Paragraph();
        paragraph.setFont(this.fontr);
        String companyLogo = TAPreferences.getCompanyLogo(this.context);
        this.header = new PdfPTable(3);
        this.header.setRunDirection(3);
        this.header.setWidths(new int[]{1, 1, 1});
        this.header.setTotalWidth(PageSize.f280A4.getWidth() - 30.0f);
        this.header.setLockedWidth(true);
        PdfPTable pdfPTable = new PdfPTable(1);
        this.header.getDefaultCell().setBorder(0);
        pdfPTable.setRunDirection(3);
        pdfPTable.setWidthPercentage(100.0f);
        PdfPTable pdfPTable2 = new PdfPTable(1);
        pdfPTable2.setRunDirection(3);
        pdfPTable2.setWidthPercentage(100.0f);
        PdfPTable pdfPTable3 = new PdfPTable(1);
        pdfPTable3.setRunDirection(3);
        pdfPTable3.setWidthPercentage(100.0f);
        PdfPCell pdfPCell = new PdfPCell(new Phrase(TAPreferences.getCompanyName(this.context), this.fontr));
        pdfPCell.setHorizontalAlignment(3);
        pdfPCell.setBackgroundColor(BaseColor.WHITE);
        pdfPCell.setBorder(0);
        pdfPCell.setPaddingRight(5.0f);
        pdfPTable2.addCell(pdfPCell);
        PdfPCell pdfPCell2 = new PdfPCell(new Phrase(TAPreferences.getCompanyAddress(this.context), this.fontr));
        pdfPCell2.setHorizontalAlignment(3);
        pdfPCell2.setBackgroundColor(BaseColor.WHITE);
        pdfPCell2.setBorder(0);
        pdfPTable2.addCell(pdfPCell2);
        PdfPCell pdfPCell3 = new PdfPCell(new Phrase(TAPreferences.getCompanyPhone(this.context), this.fontr));
        pdfPCell3.setHorizontalAlignment(3);
        pdfPCell3.setBackgroundColor(BaseColor.WHITE);
        pdfPCell3.setBorder(0);
        pdfPTable2.addCell(pdfPCell3);
        Bitmap decodeStream = BitmapFactory.decodeStream(this.context.getAssets().open("ic_app-web.png"));
        if (companyLogo != null) {
            try {
                decodeStream = MediaStore.Images.Media.getBitmap(this.context.getContentResolver(), Uri.parse(companyLogo));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        decodeStream.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        Image image = Image.getInstance(byteArrayOutputStream.toByteArray());
        image.scaleAbsolute(60.0f, 60.0f);
        PdfPCell pdfPCell4 = new PdfPCell(image, false);
        pdfPCell4.setHorizontalAlignment(2);
        pdfPCell4.setBorder(0);
        pdfPCell4.setPadding(2.0f);
        pdfPTable.addCell(pdfPCell4);
        PdfPCell pdfPCell5 = new PdfPCell(new Phrase("", this.fontr));
        pdfPCell5.setHorizontalAlignment(1);
        pdfPCell5.setVerticalAlignment(0);
        pdfPCell5.setFixedHeight(20.0f);
        pdfPCell5.setBackgroundColor(BaseColor.WHITE);
        pdfPCell5.setBorder(0);
        pdfPTable3.addCell(pdfPCell5);
        PdfPCell pdfPCell6 = new PdfPCell(new Phrase(this.report_name, this.fontr));
        pdfPCell6.setHorizontalAlignment(0);
        pdfPCell6.setBackgroundColor(BaseColor.WHITE);
        pdfPCell6.setBorder(0);
        pdfPTable3.addCell(pdfPCell6);
        this.header.getDefaultCell().setBorder(2);
        this.header.addCell(pdfPTable2);
        this.header.addCell(pdfPTable3);
        this.header.addCell(pdfPTable);
        paragraph.add((Element) this.header);
        this.tableHeight = this.header.getTotalHeight();
    }

    private void addWaterMark(PdfWriter pdfWriter, Document document) {
        int length = String.valueOf(pdfWriter.getPageNumber()).length() * 5;
        String companyLogo = TAPreferences.getCompanyLogo(this.context);
        try {
            Bitmap decodeStream = BitmapFactory.decodeStream(this.context.getAssets().open("ic_app-web.png"));
            if (companyLogo != null) {
                try {
                    decodeStream = MediaStore.Images.Media.getBitmap(this.context.getContentResolver(), Uri.parse(companyLogo));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            decodeStream.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            try {
                Image image = Image.getInstance(byteArrayOutputStream.toByteArray());
                float width = document.getPageSize().getWidth();
                float height = document.getPageSize().getHeight();
                image.scaleToFit(width * 0.5f, 0.5f * height);
                image.setAbsolutePosition((width - image.getScaledWidth()) / 2.0f, (height - image.getScaledWidth()) / 2.0f);
                PdfContentByte directContentUnder = pdfWriter.getDirectContentUnder();
                directContentUnder.saveState();
                PdfGState pdfGState = new PdfGState();
                pdfGState.setFillOpacity(0.3f);
                directContentUnder.setGState(pdfGState);
                try {
                    directContentUnder.addImage(image);
                    directContentUnder.restoreState();
                } catch (DocumentException e2) {
                    throw new RuntimeException(e2);
                }
            } catch (BadElementException e3) {
                throw new RuntimeException(e3);
            } catch (IOException e4) {
                throw new RuntimeException(e4);
            }
        } catch (IOException e5) {
            throw new RuntimeException(e5);
        }
    }

    public float getTableHeight() {
        return this.tableHeight;
    }

    @Override // com.itextpdf.text.pdf.PdfPageEventHelper, com.itextpdf.text.pdf.PdfPageEvent
    public void onCloseDocument(PdfWriter pdfWriter, Document document) {
        ColumnText.showTextAligned(this.f605t, 2, new Phrase(String.valueOf(pdfWriter.getPageNumber() - 1), new Font(Font.FontFamily.HELVETICA, 8.0f)), String.valueOf(pdfWriter.getPageNumber()).length() * 5, 6.0f, 0.0f);
    }

    @Override // com.itextpdf.text.pdf.PdfPageEventHelper, com.itextpdf.text.pdf.PdfPageEvent
    public void onEndPage(PdfWriter pdfWriter, Document document) {
        this.header.writeSelectedRows(0, -1, document.left(), document.top() + ((document.topMargin() + this.tableHeight) / 2.0f), pdfWriter.getDirectContent());
        addFooter(pdfWriter, document);
        addWaterMark(pdfWriter, document);
    }

    @Override // com.itextpdf.text.pdf.PdfPageEventHelper, com.itextpdf.text.pdf.PdfPageEvent
    public void onOpenDocument(PdfWriter pdfWriter, Document document) {
        this.f605t = pdfWriter.getDirectContent().createTemplate(30.0f, 16.0f);
        try {
            this.total = Image.getInstance(this.f605t);
            this.total.setRole(PdfName.ARTIFACT);
        } catch (BadElementException e) {
            e.printStackTrace();
        }
    }

    @Override // com.itextpdf.text.pdf.PdfPageEventHelper, com.itextpdf.text.pdf.PdfPageEvent
    public void onStartPage(PdfWriter pdfWriter, Document document) {
    }
}
