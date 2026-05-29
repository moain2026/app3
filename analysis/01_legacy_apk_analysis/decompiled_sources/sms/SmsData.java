package com.p001yd.electricecollector.sms;

/* loaded from: classes13.dex */
public class SmsData {
    public static int NOTE = 0;
    public static int BOOKMARK = 1;
    public static int TRACK = 2;
    public int TYPE = 0;

    /* renamed from: x */
    public float f676x = 0.0f;

    /* renamed from: y */
    public float f677y = 0.0f;

    /* renamed from: z */
    public float f678z = Float.NaN;
    public String text = "";

    public String toSmsDataString() {
        StringBuilder sb = new StringBuilder();
        if (this.TYPE == NOTE) {
            sb.append("n:");
        } else if (this.TYPE == BOOKMARK) {
            sb.append("b:");
        }
        sb.append(this.f676x).append(",");
        sb.append(this.f677y).append(",");
        if (!Float.isNaN(this.f678z)) {
            sb.append(this.f678z).append(",");
        }
        sb.append(this.text);
        return sb.toString();
    }
}
