package com.chatsystem.admin.ui;

import com.chatsystem.common.ui.BaseTablePanel;
import com.chatsystem.common.ui.UiUtil;

import javax.swing.*;

/**
 * Admin 7. Danh sách người dùng và số lượng bạn bè.
 * - Cột số bạn bè trực tiếp và số bạn bè tính cả bạn của bạn
 * - Sắp xếp theo tên / thời gian tạo
 * - Lọc theo tên, lọc theo số bạn trực tiếp (=, <, > một số nhập vào)
 * Phiên bản 1: chỉ giao diện + dữ liệu giả, chưa xử lý sự kiện.
 *
 * Đối chiếu CSDL (script/create_db.sql): mỗi cặp bạn lưu 2 dòng trong friendships
 * (user_id, friend_id), nên truy vấn Phiên bản 2 dự kiến:
 * - Số bạn trực tiếp: SELECT COUNT(*) FROM friendships WHERE user_id = ?
 * - Số bạn gồm bạn của bạn: đếm DISTINCT tập {bạn trực tiếp} ∪ {friend_id của các bạn đó},
 *   bỏ chính người đó:
 *   SELECT COUNT(DISTINCT x.id) FROM (
 *     SELECT f1.friend_id AS id FROM friendships f1 WHERE f1.user_id = ?
 *     UNION
 *     SELECT f2.friend_id FROM friendships f1 JOIN friendships f2 ON f2.user_id = f1.friend_id
 *     WHERE f1.user_id = ?) x WHERE x.id <> ?
 * - Bộ lọc =, <, > áp lên cột "Số bạn trực tiếp".
 */
public class FriendCountListPanel extends BaseTablePanel {

    public static final int COL_USERNAME = 0;
    public static final int COL_FULLNAME = 1;
    public static final int COL_CREATED = 2;
    public static final int COL_DIRECT = 3;
    public static final int COL_WITH_FOF = 4;

    private final JComboBox<String> operatorBox;
    private final JTextField countField;

    public FriendCountListPanel() {
        super("Người dùng và số lượng bạn bè", new String[]{
                "Tên đăng nhập", "Họ tên", "Ngày tạo", "Số bạn trực tiếp", "Số bạn (gồm bạn của bạn)"});

        // ----- Bộ lọc -----
        setSearchPlaceholder("Tìm theo tên");
        setSearchColumns(COL_USERNAME, COL_FULLNAME);
        operatorBox = addFilter("Số bạn:", new JComboBox<>(new String[]{"=", "<", ">"}));
        countField = addFilter(null, UiUtil.textField("Số", 5));

        // ----- Sắp xếp -----
        addSortOption("Tên (A-Z)", COL_FULLNAME, SortOrder.ASCENDING);
        addSortOption("Tên (Z-A)", COL_FULLNAME, SortOrder.DESCENDING);
        addSortOption("Mới tạo nhất", COL_CREATED, SortOrder.DESCENDING);
        addSortOption("Cũ nhất", COL_CREATED, SortOrder.ASCENDING);

        setColumnWidth(COL_USERNAME, 130);
        setColumnWidth(COL_FULLNAME, 220);
        setColumnWidth(COL_CREATED, 110);
        setColumnWidth(COL_DIRECT, 130);
        setColumnWidth(COL_WITH_FOF, 200);

        loadMockData();
    }

    /** Dữ liệu giả để chụp hình báo cáo Phiên bản 1. */
    private void loadMockData() {
        addRow("taitp", "Trần Phước Tài", "2026-09-01", 9, 24);
        addRow("khangndh", "Nguyễn Đặng Hoàng Khang", "2026-09-02", 7, 21);
        addRow("nhanntt", "Nguyễn Trần Trung Nhân", "2026-09-02", 8, 22);
        addRow("lanpt", "Phạm Thị Lan", "2026-09-15", 5, 17);
        addRow("minhlq", "Lê Quang Minh", "2026-09-20", 2, 9);
        addRow("huongvt", "Võ Thu Hương", "2026-10-03", 6, 18);
        addRow("baodq", "Đỗ Quốc Bảo", "2026-10-11", 4, 15);
        addRow("trangnh", "Nguyễn Hà Trang", "2026-10-18", 1, 6);
        addRow("ducnm", "Ngô Minh Đức", "2026-10-29", 3, 12);
        addRow("anhht", "Hoàng Tuấn Anh", "2026-11-05", 3, 11);
        addRow("myltk", "Lê Thị Kiều My", "2026-11-10", 2, 10);
        addRow("phucvh", "Vũ Hoàng Phúc", "2026-11-14", 0, 0);
    }

    // Getter để gắn sự kiện ở Phiên bản 2
    public JComboBox<String> getOperatorBox() { return operatorBox; }
    public JTextField getCountField() { return countField; }
}
