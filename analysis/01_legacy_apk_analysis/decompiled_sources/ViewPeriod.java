package com.p001yd.electricecollector;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/* loaded from: classes6.dex */
public class ViewPeriod implements Serializable {
    private static final long serialVersionUID = -5610074477068411084L;
    private int periodType;
    public Date endDate = new Date();
    public Date startDate = new Date();

    public ViewPeriod(Context context) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        setPeriodType(Integer.parseInt(defaultSharedPreferences.getString("PeriodType", "0")));
        setPeriodCustomValue(Integer.parseInt(defaultSharedPreferences.getString("PeriodCustomValue", "90")));
    }

    public Date getEndDate() {
        return this.endDate;
    }

    public int getPeriodCustomValue() {
        return DateUtils.daysBetween(getStartDate(), getEndDate());
    }

    public int getPeriodType() {
        return this.periodType;
    }

    public Date getStartDate() {
        return this.startDate;
    }

    public void setEndDate(Date date) {
        if (getPeriodType() == 0) {
            if (DateUtils.daysBetween(this.startDate, date) < 0) {
                this.endDate.setTime(this.startDate.getTime());
            } else {
                this.endDate = date;
            }
        }
    }

    public void setPeriodCustomValue(int i) {
        if (getPeriodType() == 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(this.endDate);
            for (int i2 = 1; i2 <= i; i2++) {
                calendar.add(5, -1);
            }
            this.startDate = calendar.getTime();
        }
    }

    public void setPeriodType(int i) {
        this.periodType = i;
        Calendar calendar = Calendar.getInstance();
        switch (i) {
            case 0:
                setPeriodCustomValue(getPeriodCustomValue());
                return;
            case 1:
                this.startDate = new Date();
                this.endDate = new Date();
                return;
            case 2:
                calendar.add(5, -1);
                this.startDate = calendar.getTime();
                this.endDate = calendar.getTime();
                return;
            case 3:
                for (int i2 = calendar.get(7); i2 != 1; i2 = calendar.get(7)) {
                    calendar.add(5, -1);
                }
                this.startDate = calendar.getTime();
                this.endDate = new Date();
                return;
            case 4:
                calendar.set(5, 1);
                this.startDate = calendar.getTime();
                this.endDate = new Date();
                return;
            default:
                return;
        }
    }

    public void setStartDate(Date date) {
        if (getPeriodType() == 0) {
            if (DateUtils.daysBetween(date, this.endDate) < 0) {
                this.startDate.setTime(this.endDate.getTime());
            } else {
                this.startDate = date;
            }
        }
    }
}
