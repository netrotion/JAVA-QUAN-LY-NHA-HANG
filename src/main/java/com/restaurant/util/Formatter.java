package com.restaurant.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Cac ham dinh dang tien te va ngay gio dung chung cho giao dien.
 */
public final class Formatter {

    private static final DecimalFormat MONEY_FORMAT;
    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator('.');
        MONEY_FORMAT = new DecimalFormat("#,##0", symbols);
    }

    private Formatter() {
    }

    /**
     * Dinh dang so tien: 150000 -> "150.000 d".
     * @param amount
     * @return 
     */
    public static String money(double amount) {
        return MONEY_FORMAT.format(amount) + " d";
    }

    /**
     * Dinh dang so tien khong co don vi: 150000 -> "150.000".
     * @param amount
     * @return 
     */
    public static String number(double amount) {
        return MONEY_FORMAT.format(amount);
    }

    /**
     * Dinh dang ngay gio: dd/MM/yyyy HH:mm:ss.
     * @param dateTime
     * @return 
     */
    public static String dateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(DATE_TIME_FORMAT);
    }
}
