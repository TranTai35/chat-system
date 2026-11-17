package com.chatsystem.admin.ui;

import com.chatsystem.common.ui.BaseTablePanel;
import com.chatsystem.common.ui.UiUtil;

import javax.swing.*;

/**
 * Admin 4. Danh sách báo cáo spam.
 * - Sắp xếp theo thời gian / tên đăng nhập
 * - Lọc theo khoảng thời gian, theo tên đăng nhập
 * - Khoá tài khoản người bị báo cáo
 * Phiên bản 1: chỉ giao diện + dữ liệu giả, chưa xử lý sự kiện.
 */
public class SpamReportListPanel extends BaseTablePanel {

    public static final int COL_TIME = 0;
    public static final int COL_REPORTED = 1;
    public static final int COL_REPORTED_NAME = 2;
    public static final int COL_REPORTER = 3;
    public static final int COL_STATUS = 4;

    private final JTextField fromField;
    private final JTextField toField;
    private final JButton lockButton;

    public SpamReportListPanel() {
        super("Danh sách báo cáo spam", new String[]{
                "Thời gian", "Tên đăng nhập bị báo cáo", "Họ tên", "Người báo cáo", "Trạng thái tài khoản"});

        // ----- Bộ lọc -----
        setSearchPlaceholder("Tìm theo tên đăng nhập");
        setSearchColumns(COL_REPORTED, COL_REPORTER);
        fromField = addFilter("Từ:", UiUtil.textField("yyyy-MM-dd", 10));
        toField = addFilter("Đến:", UiUtil.textField("yyyy-MM-dd", 10));

        // ----- Sắp xếp -----
        addSortOption("Mới nhất", COL_TIME, SortOrder.DESCENDING);
        addSortOption("Cũ nhất", COL_TIME, SortOrder.ASCENDING);
        addSortOption("Tên đăng nhập (A-Z)", COL_REPORTED, SortOrder.ASCENDING);
        addSortOption("Tên đăng nhập (Z-A)", COL_REPORTED, SortOrder.DESCENDING);

        // ----- Nút thao tác -----
        lockButton = addAction(UiUtil.dangerButton("Khoá tài khoản"));

        setColumnWidth(COL_TIME, 150);
        setColumnWidth(COL_REPORTED, 190);
        setColumnWidth(COL_REPORTED_NAME, 190);
        setColumnWidth(COL_REPORTER, 140);
        setColumnWidth(COL_STATUS, 150);

        loadMockData();
    }

    /** Dữ liệu giả để chụp hình báo cáo Phiên bản 1. */
    private void loadMockData() {
        addRow("2026-11-17 09:20", "minhlq", "Lê Quang Minh", "lanpt", "Bị khoá");
        addRow("2026-11-16 23:05", "trangnh", "Nguyễn Hà Trang", "huongvt", "Bị khoá");
        addRow("2026-11-16 15:42", "ducnm", "Ngô Minh Đức", "baodq", "Hoạt động");
        addRow("2026-11-14 20:11", "anhht", "Hoàng Tuấn Anh", "myltk", "Hoạt động");
        addRow("2026-11-12 08:37", "minhlq", "Lê Quang Minh", "taitp", "Bị khoá");
        addRow("2026-11-08 13:26", "phucvh", "Vũ Hoàng Phúc", "nhanntt", "Hoạt động");
        addRow("2026-11-02 17:50", "baodq", "Đỗ Quốc Bảo", "khangndh", "Hoạt động");
        addRow("2026-10-25 10:03", "trangnh", "Nguyễn Hà Trang", "lanpt", "Bị khoá");
        addRow("2026-10-18 21:44", "ducnm", "Ngô Minh Đức", "phucvh", "Hoạt động");
        addRow("2026-10-06 19:15", "minhlq", "Lê Quang Minh", "huongvt", "Bị khoá");
    }

    // Getter để gắn sự kiện ở Phiên bản 2
    public JTextField getFromField() { return fromField; }
    public JTextField getToField() { return toField; }
    public JButton getLockButton() { return lockButton; }
}
