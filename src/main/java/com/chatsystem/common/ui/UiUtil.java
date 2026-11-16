package com.chatsystem.common.ui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/** Các hàm tạo nhanh component theo đúng style chung. */
public final class UiUtil {

    private UiUtil() {
    }

    public static JLabel title(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.FONT_TITLE);
        return l;
    }

    public static JLabel heading(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.FONT_HEADING);
        return l;
    }

    public static JLabel muted(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.FONT_SMALL);
        l.setForeground(Theme.MUTED_TEXT);
        return l;
    }

    /** Nút chính (nền xanh, chữ trắng). */
    public static JButton primaryButton(String text) {
        return coloredButton(text, Theme.PRIMARY);
    }

    /** Nút nguy hiểm: xoá, khoá tài khoản... */
    public static JButton dangerButton(String text) {
        return coloredButton(text, Theme.DANGER);
    }

    /** Nút thường. */
    public static JButton button(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private static JButton coloredButton(String text, Color bg) {
        JButton b = button(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(Theme.FONT_BOLD);
        return b;
    }

    /** Ô nhập có chữ gợi ý (placeholder) khi dùng FlatLaf. */
    public static JTextField textField(String placeholder, int columns) {
        JTextField f = new JTextField(columns);
        f.putClientProperty("JTextField.placeholderText", placeholder);
        return f;
    }

    public static JPasswordField passwordField(String placeholder, int columns) {
        JPasswordField f = new JPasswordField(columns);
        f.putClientProperty("JTextField.placeholderText", placeholder);
        f.putClientProperty("JPasswordField.showRevealButton", true);
        return f;
    }

    public static Border padding(int size) {
        return BorderFactory.createEmptyBorder(size, size, size, size);
    }

    /** Khung "thẻ": nền trắng, viền xám nhạt, có padding. */
    public static JPanel card(LayoutManager layout) {
        JPanel p = new JPanel(layout);
        p.setBackground(Theme.SURFACE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER),
                padding(Theme.PADDING)));
        return p;
    }
}
