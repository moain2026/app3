package com.p001yd.electricecollector.entities;

/* loaded from: classes14.dex */
public class MainAdd {
    private float cardEv;
    private int color;
    private int iconTint;
    private int image;
    private MyTags names;
    private int title;

    public MainAdd(int i, int i2, int i3, MyTags myTags, float f) {
        this.title = i;
        this.image = i2;
        this.color = i3;
        this.names = myTags;
        this.cardEv = f;
    }

    public MainAdd(int i, int i2, int i3, MyTags myTags, int i4, float f) {
        this.title = i;
        this.image = i2;
        this.color = i3;
        this.names = myTags;
        this.iconTint = i4;
        this.cardEv = f;
    }

    public float getCardEv() {
        return this.cardEv;
    }

    public int getColor() {
        return this.color;
    }

    public int getIconTint() {
        return this.iconTint;
    }

    public int getImage() {
        return this.image;
    }

    public MyTags getNames() {
        return this.names;
    }

    public int getTitle() {
        return this.title;
    }
}
