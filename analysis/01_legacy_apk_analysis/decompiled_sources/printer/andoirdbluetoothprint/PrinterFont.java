package com.p001yd.electricecollector.printer.andoirdbluetoothprint;

import android.content.Context;
import androidx.recyclerview.widget.ItemTouchHelper;
import com.itextpdf.text.Jpeg;
import com.itextpdf.text.pdf.codec.wmf.MetaDo;
import cz.msebera.android.httpclient.HttpStatus;
import cz.msebera.android.httpclient.message.TokenParser;
import java.util.ArrayList;

/* loaded from: classes11.dex */
public class PrinterFont {
    int[] IsArabicLatter;
    int[] Ltk;
    int[] Ltp;

    /* renamed from: a */
    public ArrayList<Letter>[] f674a;

    /* renamed from: c */
    Context f675c;
    private int var16_4;
    private String var17_5;
    private int var18_6;
    private int var20_8;
    private int var21_9;
    private int var23_10;
    private int var24_11;
    private int var25_12;
    private boolean var27_13;
    private boolean var28_14;
    private int var29_15;
    private int var3_3;
    private String var5_7;
    int ISO_CHARACTERS = 12;
    int Link_CHARACTERS = 24;
    int N_DISTINCT_CHARACTERS = 36;
    int N_DISTINCT_CHARACTERS2 = 4;
    int _CH = 0;

    /* renamed from: _I */
    Integer f673_I = 0;
    String _Out = "";
    String _sIn = "";
    long[] theSet1 = new long[24];
    long[] theSet2 = new long[12];

    /* renamed from: Lt */
    Letter[] f672Lt = new Letter[this.N_DISTINCT_CHARACTERS];
    Letter2[] Lt2 = new Letter2[this.N_DISTINCT_CHARACTERS2];

    /* loaded from: classes11.dex */
    public class Letter {
        public int Character;
        public int endGlyph;
        public int iniGlyph;
        public int isoGlyph;
        public int midGlyph;

        public Letter(int i, int i2, int i3, int i4, int i5) {
            this.Character = i;
            this.endGlyph = i2;
            this.iniGlyph = i3;
            this.midGlyph = i4;
            this.isoGlyph = i5;
        }
    }

    /* loaded from: classes11.dex */
    public class Letter2 {
        public int Character;
        public int endGlyph;
        public int isoGlyph;

        public Letter2(int i, int i2, int i3) {
            this.Character = i;
            this.endGlyph = i2;
            this.isoGlyph = i3;
        }
    }

    public PrinterFont(Context context) {
        this.f675c = context;
        InitialArabic();
    }

    private String MakeLines(String str) {
        int i = 1;
        while (i <= str.length()) {
            int i2 = i + 20;
            if (i2 < str.length()) {
                int i3 = i - 1;
                int i4 = i2 - 1;
                String substring = str.substring(i3, ((i3 + i4) - i) + 1);
                int indexOf = substring.indexOf("\r\n") + 1;
                if (indexOf == 0) {
                    int SearchFor = SearchFor(substring, " ");
                    if (SearchFor == substring.length()) {
                        substring = substring.substring(0, substring.length() - 1) + "\r\n";
                    } else if (SearchFor < substring.length() / 2) {
                        int SearchFor2 = SearchFor(substring, ".");
                        if (SearchFor2 == 0) {
                            SearchFor2 = SearchFor(substring, "?");
                        }
                        if (SearchFor2 == 0) {
                            SearchFor2 = SearchFor(substring, "!");
                        }
                        if (SearchFor2 == 0) {
                            SearchFor2 = SearchFor(substring, "-");
                        }
                        if (SearchFor2 == 0) {
                            SearchFor2 = SearchFor(substring, "+");
                        }
                        if (SearchFor2 == 0) {
                            SearchFor2 = SearchFor(substring, "/");
                        }
                        if (SearchFor2 == 0) {
                            SearchFor2 = SearchFor(substring, "\\");
                        }
                        if (SearchFor2 == 0) {
                            SearchFor2 = SearchFor(substring, "*");
                        }
                        if (SearchFor2 == 0) {
                            SearchFor2 = SearchFor(substring, ",");
                        }
                        if (SearchFor2 != 0) {
                            substring = substring.substring(0, SearchFor2) + "\r\n" + substring.substring(SearchFor2);
                        }
                    } else {
                        substring = substring.substring(0, SearchFor - 1) + "\r\n" + substring.substring(SearchFor);
                    }
                    str = str.substring(0, i3) + substring + str.substring(i4);
                } else {
                    i = ((i + indexOf) + 2) - 20;
                }
            }
            i += 20;
        }
        return str;
    }

    private String MakeReverse(String str) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (true) {
            int indexOf = str.indexOf("\r\n", i);
            if (indexOf == -1) {
                sb.append(MakeReverse2(str.substring(i)));
                return sb.toString();
            }
            sb.append(MakeReverse(str.substring(i, indexOf))).append("zr\n");
            i = indexOf + 2;
        }
    }

    private String MakeReverse2(String str) {
        String str2 = "";
        for (int length = str == null ? 0 : str.length(); length >= 1; length--) {
            StringBuilder sb = new StringBuilder();
            sb.append(str2);
            int i = length - 1;
            sb.append(str.substring(i, i + 1));
            str2 = sb.toString();
        }
        return str2;
    }

    private String MakeReverseold(String str) {
        String str2 = "";
        int i = 0;
        while (true) {
            int indexOf = str.indexOf("\r\n", i) + 1;
            if (indexOf != 0) {
                if (i > 0) {
                    i++;
                }
                String MakeReverse2 = MakeReverse2(str.substring(i, indexOf - 1));
                if (str2 == null || str2.length() == 0) {
                    str2 = MakeReverse2;
                } else {
                    str2 = str2 + "\r\n" + MakeReverse2;
                }
            }
            if (indexOf == 0) {
                break;
            }
            i = indexOf;
        }
        if (i > 0) {
            i++;
        }
        String MakeReverse22 = MakeReverse2(str.substring(i));
        if (str2 == null || str2.length() == 0) {
            return MakeReverse22;
        }
        return str2 + "\r\n" + MakeReverse22;
    }

    private int SearchFor(String str, String str2) {
        for (int length = str.length(); length >= 1; length--) {
            int i = length - 1;
            if (str.substring(i, i + 1).equals(str2)) {
                return length;
            }
        }
        return 0;
    }

    private void SetAt(String str, int i, int i2) {
        this._Out = str.substring(0, i - 1) + ((char) i2) + str.substring(i);
    }

    private void SetAt2(String str, String str2, Integer num, int i, boolean z) {
        char c = i != 18 ? i == 33 ? (char) 1 : i == 35 ? (char) 2 : (char) 0 : (char) 3;
        int i2 = z ? this.f672Lt[c].endGlyph : this.Lt2[c].isoGlyph;
        this._CH = i2;
        this._Out = str.substring(0, num.intValue() - 2) + ((char) i2) + str.substring(num.intValue());
        this._sIn = str2.substring(0, num.intValue() - 2) + str2.substring(num.intValue() - 1);
        this.f673_I = Integer.valueOf(num.intValue() - 1);
    }

    public final String ArabicReverse(String str) {
        MakeReverse(str);
        int i = 1;
        String str2 = "";
        while (true) {
            if (i > (str == null ? 0 : str.length())) {
                return str2;
            }
            int i2 = i - 1;
            String substring = str.substring(i2, i2 + 1);
            if ((substring.compareTo("0") < 0 || substring.compareTo("9") > 0) && !((substring.compareTo("A") >= 0 && substring.compareTo("z") <= 0) || substring.equals(".") || substring.equals(String.valueOf('\n')) || substring.equals(String.valueOf(TokenParser.f710CR)))) {
                str2 = str2 + substring;
                i++;
            } else {
                String str3 = "";
                while (true) {
                    if ((substring.compareTo("0") >= 0 && substring.compareTo("9") <= 0) || ((substring.compareTo("A") >= 0 && substring.compareTo("z") <= 0) || substring.equals(".") || substring.equals(String.valueOf('\n')) || substring.equals(String.valueOf(TokenParser.f710CR)))) {
                        str3 = str3 + substring;
                        i++;
                        if (i > (str == null ? 0 : str.length())) {
                            break;
                        }
                        int i3 = i - 1;
                        substring = str.substring(i3, i3 + 1);
                    } else {
                        break;
                    }
                }
                MakeReverse2(str3);
                str2 = str2 + str3;
            }
        }
    }

    public String Arabize(String str, boolean z) {
        this.var3_3 = 1;
        this.var5_7 = "";
        try {
            if (str.trim().equals("")) {
                return str;
            }
            if (z) {
                str = MakeLines(str);
            }
            if (str == null) {
                return str;
            }
            this.var16_4 = str.length();
            this.Ltk = new int[this.var16_4];
            this.IsArabicLatter = new int[this.var16_4];
            this.var17_5 = str;
            this.var18_6 = 1;
            try {
                this.var16_4 = str.length();
                this.Ltk = new int[this.var16_4];
                this.IsArabicLatter = new int[this.var16_4];
                this.var17_5 = str;
                this.var18_6 = 1;
                while (this.var18_6 <= this.var16_4) {
                    this.var20_8 = this.var18_6 - 1;
                    try {
                        this.var21_9 = str.charAt(this.var20_8);
                        if (this.var21_9 == 13 || this.var21_9 == 32) {
                            this.Ltk[this.var20_8] = this.var21_9;
                        }
                        this.var5_7 = "2";
                    } catch (Exception e) {
                        this.var3_3 = this.var18_6;
                        this.var5_7 = "Error processing character: " + e.getMessage();
                        e.printStackTrace();
                    }
                    if (this.var21_9 >= 1569 && this.var21_9 <= 1610) {
                        this.var5_7 = "3";
                        this.IsArabicLatter[this.var20_8] = this.var3_3;
                        this.var23_10 = 0;
                        while (true) {
                            if (this.var23_10 >= this.N_DISTINCT_CHARACTERS) {
                                break;
                            }
                            this.var5_7 = "4";
                            if (this.f672Lt[this.var23_10].Character == this.var21_9) {
                                this.var5_7 = "6";
                                break;
                            }
                            this.var23_10++;
                        }
                        this.var24_11 = str.length();
                        this.var5_7 = "7";
                        this.var27_13 = this.var18_6 != this.var24_11 && (isFromTheSet1((long) str.charAt(this.var18_6)) || isFromTheSet2((long) str.charAt(this.var18_6)));
                        this.var28_14 = this.var18_6 == this.var3_3 ? false : isFromTheSet1(str.charAt(this.var18_6 - 2));
                        if (this.var28_14 && this.var27_13) {
                            this.var5_7 = "48";
                            SetAt(this.var17_5, this.var18_6, this.f672Lt[this.var23_10].midGlyph);
                            this.var5_7 = "49";
                            this.var17_5 = this._Out;
                            this.var5_7 = "50";
                            this.Ltk[this.var18_6 - 1] = this.f672Lt[this.var23_10].midGlyph;
                        }
                        this.var18_6++;
                    }
                    this.Ltk[this.var20_8] = this.var21_9;
                    this.IsArabicLatter[this.var20_8] = 0;
                    this.var18_6++;
                }
                this.var5_7 = "1";
                return str;
            } catch (Exception e2) {
                this.var5_7 = "Error setting up arrays: " + e2.getMessage();
                e2.printStackTrace();
                return str;
            }
        } catch (Exception e3) {
            this.var5_7 = "Error in initial setup: " + e3.getMessage();
            this.var3_3 = 0;
            e3.printStackTrace();
            return str;
        }
    }

    public final void InitialArabic() {
        long[] jArr = this.theSet1;
        jArr[0] = 1580;
        jArr[1] = 1581;
        jArr[2] = 1582;
        jArr[3] = 1607;
        jArr[4] = 1593;
        jArr[5] = 1594;
        jArr[6] = 1601;
        jArr[7] = 1602;
        jArr[8] = 1579;
        jArr[9] = 1589;
        jArr[10] = 1590;
        jArr[11] = 1591;
        jArr[12] = 1603;
        jArr[13] = 1605;
        jArr[14] = 1606;
        jArr[15] = 1578;
        jArr[16] = 1604;
        jArr[17] = 1576;
        jArr[18] = 1610;
        jArr[19] = 1587;
        jArr[20] = 1588;
        jArr[21] = 1592;
        jArr[22] = 1600;
        jArr[23] = 1574;
        long[] jArr2 = this.theSet2;
        jArr2[0] = 1575;
        jArr2[1] = 1571;
        jArr2[2] = 1573;
        jArr2[3] = 1570;
        jArr2[4] = 1583;
        jArr2[5] = 1584;
        jArr2[6] = 1585;
        jArr2[7] = 1586;
        jArr2[8] = 1608;
        jArr2[9] = 1572;
        jArr2[10] = 1577;
        jArr2[11] = 1609;
        this.Lt2[0] = new Letter2(1570, ItemTouchHelper.Callback.DEFAULT_SWIPE_ANIMATION_DURATION, 249);
        this.Lt2[1] = new Letter2(1571, 154, 153);
        this.Lt2[2] = new Letter2(1573, 158, 157);
        this.Lt2[3] = new Letter2(1575, 158, 157);
        this.f672Lt[0] = new Letter(1584, 208, 208, 208, 208);
        this.f672Lt[1] = new Letter(1583, HttpStatus.SC_MULTI_STATUS, HttpStatus.SC_MULTI_STATUS, HttpStatus.SC_MULTI_STATUS, HttpStatus.SC_MULTI_STATUS);
        this.f672Lt[2] = new Letter(1580, 173, 204, 204, 173);
        this.f672Lt[3] = new Letter(1581, 174, HttpStatus.SC_RESET_CONTENT, HttpStatus.SC_RESET_CONTENT, 174);
        this.f672Lt[4] = new Letter(1582, 175, HttpStatus.SC_PARTIAL_CONTENT, HttpStatus.SC_PARTIAL_CONTENT, 175);
        this.f672Lt[5] = new Letter(1607, 243, 231, 244, 243);
        this.f672Lt[6] = new Letter(1593, 197, 217, 236, 223);
        this.f672Lt[7] = new Letter(1594, Jpeg.M_APPD, 218, MetaDo.META_CREATEPALETTE, Jpeg.M_APPE);
        this.f672Lt[8] = new Letter(1601, 186, 225, 225, 186);
        this.f672Lt[9] = new Letter(1602, 248, Jpeg.M_APP2, Jpeg.M_APP2, 248);
        this.f672Lt[10] = new Letter(1579, 171, 203, 203, 171);
        this.f672Lt[11] = new Letter(1589, 190, 213, 213, 190);
        this.f672Lt[12] = new Letter(1590, 235, 214, 214, 235);
        this.f672Lt[13] = new Letter(1591, 215, 215, 215, 215);
        this.f672Lt[14] = new Letter(1603, 252, 227, 227, 252);
        this.f672Lt[15] = new Letter(1605, 239, 229, 229, 239);
        this.f672Lt[16] = new Letter(1606, 242, 230, 230, 242);
        this.f672Lt[17] = new Letter(1578, 170, 202, 202, 170);
        this.f672Lt[18] = new Letter(1575, 168, 199, 168, 199);
        this.f672Lt[19] = new Letter(1604, 251, 228, 228, 251);
        this.f672Lt[20] = new Letter(1576, 169, 200, 200, 169);
        this.f672Lt[21] = new Letter(1610, 246, 234, 234, 253);
        this.f672Lt[22] = new Letter(1587, 188, 211, 211, 188);
        this.f672Lt[23] = new Letter(1588, 189, 212, 212, 189);
        this.f672Lt[24] = new Letter(1592, 216, 216, 216, 216);
        this.f672Lt[25] = new Letter(1586, 210, 210, 210, 210);
        this.f672Lt[26] = new Letter(1608, 232, 232, 232, 232);
        this.f672Lt[27] = new Letter(1577, 201, 201, 201, 201);
        this.f672Lt[28] = new Letter(1609, 245, 233, 233, 233);
        this.f672Lt[29] = new Letter(1585, 209, 209, 209, 209);
        this.f672Lt[30] = new Letter(1572, 196, 196, 196, 196);
        this.f672Lt[31] = new Letter(1569, 193, 193, 193, 193);
        this.f672Lt[32] = new Letter(MetaDo.META_ESCAPE, 245, 198, 198, 233);
        this.f672Lt[33] = new Letter(1571, 165, 195, 195, 195);
        this.f672Lt[34] = new Letter(1570, 162, 194, 194, 194);
        this.f672Lt[35] = new Letter(1573, 168, 199, 199, 199);
    }

    public int[] getLtk() {
        return this.Ltk;
    }

    public final boolean isFromTheSet1(long j) {
        for (int i = 0; i < this.Link_CHARACTERS; i++) {
            if (j == this.theSet1[i]) {
                return true;
            }
        }
        return false;
    }

    public final boolean isFromTheSet2(long j) {
        for (int i = 0; i < this.ISO_CHARACTERS; i++) {
            if (j == this.theSet2[i]) {
                return true;
            }
        }
        return false;
    }
}
