package com.restaurant.util;

import java.util.List;

/**
 * Sinh id tu dong dang PREFIX + so thu tu, vi du: MON001, BAN010.
 */
public final class IDGenerator {

    private IDGenerator() {
    }

    /**
     * Sinh ma moi dua tren danh sach ma da co.
     *
     * @param prefix     tien to (vi du "MON")
     * @param existingIds danh sach ma hien tai
     * @param soChuSo    so chu so cua phan so (vi du 3 -> 001)
     * @return 
     */
    public static String generate(String prefix, List<String> existingIds, int soChuSo) {
        int max = 0;
        for (String id : existingIds) {
            if (id != null && id.startsWith(prefix)) {
                String soPhan = id.substring(prefix.length());
                try {
                    int value = Integer.parseInt(soPhan);
                    if (value > max) {
                        max = value;
                    }
                } catch (NumberFormatException ignored) {
                    // bo qua ma khong dung dinh dang
                }
            }
        }
        int next = max + 1;
        return prefix + String.format("%0" + soChuSo + "d", next);
    }
}
