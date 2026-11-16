package com.chatsystem.common.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Màu sắc, font và Look &amp; Feel dùng chung cho cả 2 phân hệ.
 * Mọi màn hình lấy màu/font từ đây, không tự đặt mã màu riêng.
 */
public final class Theme {

    private Theme() {
    }

    // ===== Màu =====
    public static final Color PRIMARY = new Color(0x2563EB);       // xanh dương chính
    public static final Color PRIMARY_DARK = new Color(0x1D4ED8);
    public static final Color DANGER = new Color(0xDC2626);        // nút xoá / khoá
    public static final Color SUCCESS = new Color(0x16A34A);       // online / mở khoá
    public static final Color MUTED_TEXT = new Color(0x6B7280);
    public static final Color BORDER = new Color(0xE5E7EB);
    public static final Color BACKGROUND = new Color(0xF9FAFB);
    public static final Color SURFACE = Color.WHITE;
    public static final Color SIDEBAR_BG = new Color(0x111827);
    public static final Color SIDEBAR_TEXT = new Color(0xD1D5DB);
    public static final Color SIDEBAR_ACTIVE = new Color(0x1F2937);
    public static final Color TEXT = new Color(0x111827);          // chữ chính
    public static final Color WARNING = new Color(0xD97706);       // cảnh báo, nhóm mã hoá

    // ===== Khung chat =====
    public static final Color BUBBLE_ME = PRIMARY;                 // tin của mình: nền xanh, chữ trắng
    public static final Color BUBBLE_ME_TEXT = Color.WHITE;
    public static final Color BUBBLE_OTHER = new Color(0xF3F4F6);  // tin người khác: nền xám nhạt, chữ đậm
    public static final Color BUBBLE_OTHER_TEXT = TEXT;
    public static final Color HIGHLIGHT = new Color(0xFEF08A);     // tô kết quả tìm kiếm trong lịch sử chat
    public static final Color ONLINE = SUCCESS;                    // chấm online
    public static final Color OFFLINE = new Color(0x9CA3AF);       // chấm offline

    // ===== Kích thước cửa sổ / dialog =====
    public static final Dimension MAIN_WINDOW = new Dimension(1280, 760);
    public static final Dimension AUTH_WINDOW = new Dimension(440, 560);
    public static final int DIALOG_WIDTH = 480;

    // ===== Font =====
    public static final String FONT_FAMILY = "Segoe UI";
    public static final Font FONT_BODY = new Font(FONT_FAMILY, Font.PLAIN, 14);
    public static final Font FONT_BOLD = new Font(FONT_FAMILY, Font.BOLD, 14);
    public static final Font FONT_SMALL = new Font(FONT_FAMILY, Font.PLAIN, 12);
    public static final Font FONT_TITLE = new Font(FONT_FAMILY, Font.BOLD, 20);
    public static final Font FONT_HEADING = new Font(FONT_FAMILY, Font.BOLD, 16);

    // ===== Kích thước =====
    public static final int GAP = 8;
    public static final int PADDING = 16;
    public static final int ROW_HEIGHT = 30;

    /**
     * Gọi 1 lần ở đầu main() của AdminApp / UserApp, trước khi tạo cửa sổ.
     * Dùng FlatLaf nếu có trong classpath, nếu không thì dùng Nimbus.
     */
    public static void setup() {
        try {
            Class<?> flat = Class.forName("com.formdev.flatlaf.FlatLightLaf");
            flat.getMethod("setup").invoke(null);
            UIManager.put("Component.arc", 8);
            UIManager.put("Button.arc", 8);
            UIManager.put("TextComponent.arc", 8);
            UIManager.put("ScrollBar.width", 10);
            UIManager.put("Table.showHorizontalLines", true);
            UIManager.put("Table.intercellSpacing", new Dimension(0, 1));
        } catch (Exception e) {
            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception ignored) {
                // giữ Look & Feel mặc định
            }
        }
        UIManager.put("defaultFont", FONT_BODY);
        UIManager.put("Table.rowHeight", ROW_HEIGHT);
    }
}
