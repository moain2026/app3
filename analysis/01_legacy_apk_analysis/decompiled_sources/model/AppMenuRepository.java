package com.p001yd.electricecollector.model;

import android.content.Context;
import com.p001yd.electricecollector.AppMenu;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes5.dex */
public class AppMenuRepository {
    private List<AppMenu> _listOfMenuReports = new ArrayList();
    private List<AppMenu> _listOfMenuOprations = new ArrayList();

    public AppMenuRepository(Context context) {
        initMenu();
    }

    private void initMenu() {
        AppMenu appMenu = new AppMenu();
        appMenu.setMenuId("repBalanceHeader");
        appMenu.setMenuTitle("ارصــــدة الـحـسابـات");
        AppMenu appMenu2 = new AppMenu();
        appMenu2.setMenuId("repBalanceDetails");
        appMenu2.setMenuTitle("كـشف حـساب  تفصيلي");
        AppMenu appMenu3 = new AppMenu();
        appMenu3.setMenuId("repBondsReciept");
        appMenu3.setMenuTitle("سـندات الـقـبض ");
        AppMenu appMenu4 = new AppMenu();
        appMenu4.setMenuId("repBondsPayment");
        appMenu4.setMenuTitle(" الــــــقــــــــراءات ");
        AppMenu appMenu5 = new AppMenu();
        appMenu5.setMenuId("repCollectorMony");
        appMenu5.setMenuTitle("كشف اجمالي التحصيل ");
        AppMenu appMenu6 = new AppMenu();
        appMenu6.setMenuId("repListReading");
        appMenu6.setMenuTitle("تـقـريـرالاســـــتــــهــــلاك");
        AppMenu appMenu7 = new AppMenu();
        appMenu7.setMenuId("repBoxMoves");
        appMenu7.setMenuTitle("تقرير اجمالي حركة الصناديق");
        AppMenu appMenu8 = new AppMenu();
        appMenu8.setMenuId("repExpenses");
        appMenu8.setMenuTitle("تقرير المصروفات اليومية");
        this._listOfMenuReports.add(appMenu);
        this._listOfMenuReports.add(appMenu2);
        this._listOfMenuReports.add(appMenu5);
        this._listOfMenuReports.add(appMenu6);
        this._listOfMenuReports.add(appMenu7);
        this._listOfMenuReports.add(appMenu8);
        this._listOfMenuOprations.add(appMenu3);
        this._listOfMenuOprations.add(appMenu4);
    }

    public List<AppMenu> getMenuOprations() {
        return this._listOfMenuOprations;
    }

    public List<AppMenu> getMenuReports() {
        return this._listOfMenuReports;
    }
}
