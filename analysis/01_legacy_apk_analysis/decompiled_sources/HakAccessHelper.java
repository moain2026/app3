package com.p001yd.electricecollector;

import com.p001yd.electricecollector.entities.HakAccess;
import java.util.List;

/* loaded from: classes6.dex */
public class HakAccessHelper {
    public static HakAccess getHakAkses(String str, List<HakAccess> list) {
        for (HakAccess hakAccess : list) {
            if (hakAccess.getMenuName().equals(str)) {
                return hakAccess;
            }
        }
        return null;
    }
}
