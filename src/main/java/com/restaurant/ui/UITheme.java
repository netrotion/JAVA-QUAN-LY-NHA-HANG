package com.restaurant.ui;

import java.awt.Color;
import java.awt.Font;

/**
 * Mau sac va font dung chung cho toan bo giao dien.
 */
public final class UITheme {

    public static final Color PRIMARY = new Color(0x2E, 0x7D, 0x32);     // xanh la dam
    public static final Color PRIMARY_DARK = new Color(0x1B, 0x5E, 0x20);
    public static final Color ACCENT = new Color(0xFF, 0x98, 0x00);      // cam
    public static final Color DANGER = new Color(0xC6, 0x28, 0x28);      // do
    public static final Color BAN_TRONG = new Color(0x43, 0xA0, 0x47);   // xanh - ban trong
    public static final Color BAN_PHUC_VU = new Color(0xE5, 0x39, 0x35); // do - dang phuc vu
    public static final Color SIDEBAR_BG = new Color(0x26, 0x32, 0x38);
    public static final Color SIDEBAR_FG = new Color(0xEC, 0xEF, 0xF1);

    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_NORMAL = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 14);

    private UITheme() {
    }
}
