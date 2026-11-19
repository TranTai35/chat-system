package com.chatsystem.admin.ui;

import com.chatsystem.common.ui.BaseTablePanel;
import com.chatsystem.common.ui.UiUtil;

import javax.swing.*;

/**
 * Admin 5. Danh sách người dùng đăng ký mới.
 * - Chọn khoảng thời gian (từ ngày - đến ngày)
 * - Sắp xếp theo tên / thời gian tạo, lọc theo tên
 * Phiên bản 1: chỉ giao diện + dữ liệu giả, chưa xử lý sự kiện.
 *
 * Đối chiếu CSDL (script/create_db.sql, bảng users): các cột lấy từ
 * username, full_name, email, created_at (ngày đăng ký, có index idx_users_created_at).
 * Phiên bản 2 dự kiến truy vấn:
 *   SELECT username, full_name, email, created_at FROM users
 *   WHERE role = 'USER' AND created_at BETWEEN ? AND ?   -- Từ, Đến
 *   ORDER BY created_at DESC;
 */
public class NewUserListPanel extends BaseTablePanel {

    public static final int COL_USERNAME = 0;
    public static final int COL_FULLNAME = 1;
    public static final int COL_EMAIL = 2;
    public static final int COL_CREATED = 3;

    private final JTextField fromField;
    private final JTextField toField;
    private final JButton viewButton;

    public NewUserListPanel() {
        super("Người dùng đăng ký mới", new String[]{"Tên đăng nhập", "Họ tên", "Email", "Ngày tạo"});

        // ----- Bộ lọc -----
        setSearchPlaceholder("Tìm theo tên");
        setSearchColumns(COL_USERNAME, COL_FULLNAME);
        fromField = addFilter("Từ:", UiUtil.textField("yyyy-MM-dd", 10));
        toField = addFilter("Đến:", UiUtil.textField("yyyy-MM-dd", 10));
        fromField.setText("2026-09-01");
        toField.setText("2026-11-30");

        // ----- Sắp xếp -----
        addSortOption("Mới tạo nhất", COL_CREATED, SortOrder.DESCENDING);
        addSortOption("Cũ nhất", COL_CREATED, SortOrder.ASCENDING);
        addSortOption("Tên (A-Z)", COL_FULLNAME, SortOrder.ASCENDING);
        addSortOption("Tên (Z-A)", COL_FULLNAME, SortOrder.DESCENDING);

        // ----- Nút thao tác -----
        viewButton = addAction(UiUtil.primaryButton("Xem"));

        setColumnWidth(COL_USERNAME, 140);
        setColumnWidth(COL_FULLNAME, 220);
        setColumnWidth(COL_EMAIL, 220);
        setColumnWidth(COL_CREATED, 120);

        loadMockData();
    }

    /** Dữ liệu giả để chụp hình báo cáo Phiên bản 1. */
    private void loadMockData() {
        addRow("taitp", "Trần Phước Tài", "tai@example.com", "2026-09-01");
        addRow("khangndh", "Nguyễn Đặng Hoàng Khang", "khang@example.com", "2026-09-02");
        addRow("nhanntt", "Nguyễn Trần Trung Nhân", "nhan@example.com", "2026-09-02");
        addRow("lanpt", "Phạm Thị Lan", "lan@example.com", "2026-09-15");
        addRow("minhlq", "Lê Quang Minh", "minh@example.com", "2026-09-20");
        addRow("huongvt", "Võ Thu Hương", "huong@example.com", "2026-10-03");
        addRow("baodq", "Đỗ Quốc Bảo", "bao@example.com", "2026-10-11");
        addRow("trangnh", "Nguyễn Hà Trang", "trang@example.com", "2026-10-18");
        addRow("ducnm", "Ngô Minh Đức", "duc@example.com", "2026-10-29");
        addRow("anhht", "Hoàng Tuấn Anh", "anh@example.com", "2026-11-05");
        addRow("myltk", "Lê Thị Kiều My", "my@example.com", "2026-11-10");
        addRow("phucvh", "Vũ Hoàng Phúc", "phuc@example.com", "2026-11-14");
    }

    // Getter để gắn sự kiện ở Phiên bản 2
    public JTextField getFromField() { return fromField; }
    public JTextField getToField() { return toField; }
    public JButton getViewButton() { return viewButton; }
}
