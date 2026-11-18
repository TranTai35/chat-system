package com.chatsystem.admin.ui;

import com.chatsystem.common.ui.BaseTablePanel;
import com.chatsystem.common.ui.UiUtil;

import javax.swing.*;

/**
 * Admin 4. Danh sách báo cáo spam.
 * - Sắp xếp theo thời gian / tên đăng nhập
 * - Lọc theo khoảng thời gian, theo tên đăng nhập
 * - Khoá tài khoản người bị báo cáo
 * Cột khớp bảng spam_reports trong script/create_db.sql (đã chốt 18/11):
 *   created_at, reported_id, reporter_id, reason, status (PENDING/RESOLVED),
 *   cột cuối lấy từ users.is_locked của người bị báo cáo.
 * Phiên bản 1: chỉ giao diện + dữ liệu giả, chưa xử lý sự kiện.
 */
public class SpamReportListPanel extends BaseTablePanel {

    public static final int COL_TIME = 0;
    public static final int COL_REPORTED = 1;
    public static final int COL_REPORTED_NAME = 2;
    public static final int COL_REPORTER = 3;
    public static final int COL_REASON = 4;          // spam_reports.reason
    public static final int COL_REPORT_STATUS = 5;   // spam_reports.status
    public static final int COL_STATUS = 6;          // users.is_locked

    private final JTextField fromField;
    private final JTextField toField;
    private final JButton lockButton;

    public SpamReportListPanel() {
        super("Danh sách báo cáo spam", new String[]{
                "Thời gian", "Người bị báo cáo", "Họ tên", "Người báo cáo",
                "Lý do", "Xử lý", "Trạng thái tài khoản"});

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

        setColumnWidth(COL_TIME, 140);
        setColumnWidth(COL_REPORTED, 170);
        setColumnWidth(COL_REPORTED_NAME, 170);
        setColumnWidth(COL_REPORTER, 120);
        setColumnWidth(COL_REASON, 220);
        setColumnWidth(COL_REPORT_STATUS, 100);
        setColumnWidth(COL_STATUS, 150);

        loadMockData();
    }

    /** Dữ liệu giả để chụp hình báo cáo Phiên bản 1. */
    private void loadMockData() {
        addRow("2026-11-17 09:20", "minhlq", "Lê Quang Minh", "lanpt", "Gửi link quảng cáo liên tục", "Đã xử lý", "Bị khoá");
        addRow("2026-11-16 23:05", "trangnh", "Nguyễn Hà Trang", "huongvt", "Nhắn tin làm phiền lúc nửa đêm", "Đã xử lý", "Bị khoá");
        addRow("2026-11-16 15:42", "ducnm", "Ngô Minh Đức", "baodq", "Spam sticker trong nhóm", "Chờ xử lý", "Hoạt động");
        addRow("2026-11-14 20:11", "anhht", "Hoàng Tuấn Anh", "myltk", "Mời tham gia đa cấp", "Chờ xử lý", "Hoạt động");
        addRow("2026-11-12 08:37", "minhlq", "Lê Quang Minh", "taitp", "Gửi link lạ", "Đã xử lý", "Bị khoá");
        addRow("2026-11-08 13:26", "phucvh", "Vũ Hoàng Phúc", "nhanntt", "Gửi cùng 1 tin nhắn nhiều lần", "Đã xử lý", "Hoạt động");
        addRow("2026-11-02 17:50", "baodq", "Đỗ Quốc Bảo", "khangndh", "Quảng cáo bán hàng", "Chờ xử lý", "Hoạt động");
        addRow("2026-10-25 10:03", "trangnh", "Nguyễn Hà Trang", "lanpt", "Giả mạo tài khoản người khác", "Đã xử lý", "Bị khoá");
        addRow("2026-10-18 21:44", "ducnm", "Ngô Minh Đức", "phucvh", "Ngôn từ thiếu lịch sự", "Đã xử lý", "Hoạt động");
        addRow("2026-10-06 19:15", "minhlq", "Lê Quang Minh", "huongvt", "Gửi link quảng cáo", "Đã xử lý", "Bị khoá");
    }

    // Getter để gắn sự kiện ở Phiên bản 2
    public JTextField getFromField() { return fromField; }
    public JTextField getToField() { return toField; }
    public JButton getLockButton() { return lockButton; }
}
