package com.chatsystem.admin.ui;

import com.chatsystem.common.ui.BaseTablePanel;
import com.chatsystem.common.ui.UiUtil;

import javax.swing.*;

/**
 * Admin 2. Danh sách đăng nhập theo thứ tự thời gian.
 * Cột: thời gian, tên đăng nhập, họ tên.
 * Phiên bản 1: chỉ giao diện + dữ liệu giả, chưa xử lý sự kiện.
 */
public class LoginListPanel extends BaseTablePanel {

    public static final int COL_TIME = 0;
    public static final int COL_USERNAME = 1;
    public static final int COL_FULLNAME = 2;

    private final JTextField fromField;
    private final JTextField toField;
    private final JButton refreshButton;

    public LoginListPanel() {
        super("Danh sách đăng nhập", new String[]{"Thời gian", "Tên đăng nhập", "Họ tên"});

        // ----- Bộ lọc -----
        setSearchPlaceholder("Tìm theo tên / tên đăng nhập");
        setSearchColumns(COL_USERNAME, COL_FULLNAME);
        fromField = addFilter("Từ:", UiUtil.textField("yyyy-MM-dd", 10));
        toField = addFilter("Đến:", UiUtil.textField("yyyy-MM-dd", 10));

        // ----- Sắp xếp: mặc định mới nhất lên đầu -----
        addSortOption("Mới nhất", COL_TIME, SortOrder.DESCENDING);
        addSortOption("Cũ nhất", COL_TIME, SortOrder.ASCENDING);

        // ----- Nút thao tác -----
        refreshButton = addAction("Làm mới");

        setColumnWidth(COL_TIME, 160);
        setColumnWidth(COL_USERNAME, 140);
        setColumnWidth(COL_FULLNAME, 240);

        loadMockData();
    }

    /** Dữ liệu giả để chụp hình báo cáo Phiên bản 1. */
    private void loadMockData() {
        addRow("2026-11-17 08:12", "taitp", "Trần Phước Tài");
        addRow("2026-11-17 07:55", "lanpt", "Phạm Thị Lan");
        addRow("2026-11-16 22:41", "khangndh", "Nguyễn Đặng Hoàng Khang");
        addRow("2026-11-16 21:03", "nhanntt", "Nguyễn Trần Trung Nhân");
        addRow("2026-11-16 19:30", "huongvt", "Võ Thu Hương");
        addRow("2026-11-15 14:18", "baodq", "Đỗ Quốc Bảo");
        addRow("2026-11-14 09:47", "phucvh", "Vũ Hoàng Phúc");
        addRow("2026-11-12 20:26", "myltk", "Lê Thị Kiều My");
        addRow("2026-11-10 11:05", "anhht", "Hoàng Tuấn Anh");
        addRow("2026-11-05 16:39", "ducnm", "Ngô Minh Đức");
        addRow("2026-10-29 10:14", "trangnh", "Nguyễn Hà Trang");
        addRow("2026-10-20 18:52", "taitp", "Trần Phước Tài");
        addRow("2026-10-03 08:30", "minhlq", "Lê Quang Minh");
    }

    // Getter để gắn sự kiện ở Phiên bản 2
    public JTextField getFromField() { return fromField; }
    public JTextField getToField() { return toField; }
    public JButton getRefreshButton() { return refreshButton; }
}
