package com.p001yd.electricecollector.entities;

/* loaded from: classes14.dex */
public final class MyTags {
    public static final MyTags ACCOUNTS = new MyTags();
    public static final MyTags ADD_ACCOUNT = new MyTags();
    public static final MyTags ADD_DEBIT = new MyTags();
    public static final MyTags ADD_CREDIT = new MyTags();
    public static final MyTags DELETE_ACCOUNT = new MyTags();
    public static final MyTags EXPORT_ACCOUNT = new MyTags();
    public static final MyTags PAY_BILLS = new MyTags();
    public static final MyTags CATCH_RECEIPT = new MyTags();
    public static final MyTags ADD_CHECK = new MyTags();
    public static final MyTags SEND_SMS = new MyTags();
    public static final MyTags SELL_BILL = new MyTags();
    private static final MyTags[] $VALUES = {ACCOUNTS, ADD_ACCOUNT, ADD_DEBIT, ADD_CREDIT, DELETE_ACCOUNT, EXPORT_ACCOUNT, PAY_BILLS, CATCH_RECEIPT, ADD_CHECK, SEND_SMS, SELL_BILL};

    public static MyTags[] values() {
        return (MyTags[]) $VALUES.clone();
    }
}
