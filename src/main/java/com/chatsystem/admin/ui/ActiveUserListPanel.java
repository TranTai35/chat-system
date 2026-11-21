package com.chatsystem.admin.ui;

import com.chatsystem.common.ui.BaseTablePanel;
import com.chatsystem.common.ui.UiUtil;

import javax.swing.*;

/**
 * Admin 8. Danh sách người dùng hoạt động trong một khoảng thời gian.
 * - Cột: số lần mở ứng dụng, số người đã chat, số nhóm đã chat
 * - Sắp xếp theo tên / thời gian tạo
 * - Lọc theo tên, lọc theo số lượng hoạt động (=, <, > một số nhập vào)
 * Phiên bản 1: chỉ giao diện + dữ liệu giả, chưa xử lý sự kiện.
 *
 * Đối chiếu CSDL (script/create_db.sql), trong khoảng [Từ, Đến]:
 * - Mở ứng dụng: COUNT(*) FROM app_sessions WHERE user_id = ? AND opened_at BETWEEN ? AND ?
 * - Số người đã chat: COUNT(DISTINCT thành viên còn lại) của các conversations type PRIVATE
 *   mà user có messages (sender_id = ?, sent_at trong khoảng)
 * - Số nhóm đã chat: COUNT(DISTINCT m.conversation_id) với conversations type GROUP,
 *   m.sender_id = ?, m.sent_at trong khoảng
 * - Bộ lọc =, <, > áp lên cột "Mở ứng dụng".
 */
public class ActiveUserListPanel extends BaseTablePanel {

    public static final int COL_USERNAME = 0;
    public static final int COL_FULLNAME = 1;
    public static final int COL_CREATED = 2;
    public static final int COL_APP_OPENS = 3;
    public static final int COL_PEOPLE_CHATTED = 4;
    public static final int COL_GROUPS_CHATTED = 5;

    private final JTextField fromField;
    private final JTextField toField;
    private final JComboBox<String> operatorBox;
    private final JTextField countField;
    private final JButton viewButton;

    public ActiveUserListPanel() {
        super("Người dùng hoạt động", new String[]{
                "Tên đăng nhập", "Họ tên", "Ngày tạo", "Mở ứng dụng", "Số người đã chat", "Số nhóm đã chat"});

        // ----- Bộ lọc -----
        setSearchPlaceholder("Tìm theo tên");
        setSearchColumns(COL_USERNAME, COL_FULLNAME);
        fromField = addFilter("Từ:", UiUtil.textField("yyyy-MM-dd", 10));
        toField = addFilter("Đến:", UiUtil.textField("yyyy-MM-dd", 10));
        fromField.setText("2026-11-01");
        toField.setText("2026-11-20");
        operatorBox = addFilter("Số hoạt động:", new JComboBox<>(new String[]{"=", "<", ">"}));
        countField = addFilter(null, UiUtil.textField("Số", 5));

        // ----- Sắp xếp -----
        addSortOption("Tên (A-Z)", COL_FULLNAME, SortOrder.ASCENDING);
        addSortOption("Tên (Z-A)", COL_FULLNAME, SortOrder.DESCENDING);
        addSortOption("Mới tạo nhất", COL_CREATED, SortOrder.DESCENDING);
        addSortOption("Cũ nhất", COL_CREATED, SortOrder.ASCENDING);

        // ----- Nút thao tác -----
        viewButton = addAction(UiUtil.primaryButton("Xem"));

        setColumnWidth(COL_USERNAME, 120);
        setColumnWidth(COL_FULLNAME, 200);
        setColumnWidth(COL_CREATED, 100);
        setColumnWidth(COL_APP_OPENS, 110);
        setColumnWidth(COL_PEOPLE_CHATTED, 130);
        setColumnWidth(COL_GROUPS_CHATTED, 130);

        loadMockData();
    }

    /** Dữ liệu giả để chụp hình báo cáo Phiên bản 1. */
    private void loadMockData() {
        addRow("taitp", "Trần Phước Tài", "2026-09-01", 34, 8, 4);
        addRow("khangndh", "Nguyễn Đặng Hoàng Khang", "2026-09-02", 27, 6, 3);
        addRow("nhanntt", "Nguyễn Trần Trung Nhân", "2026-09-02", 30, 7, 3);
        addRow("lanpt", "Phạm Thị Lan", "2026-09-15", 18, 5, 2);
        addRow("huongvt", "Võ Thu Hương", "2026-10-03", 12, 4, 2);
        addRow("baodq", "Đỗ Quốc Bảo", "2026-10-11", 9, 3, 1);
        addRow("ducnm", "Ngô Minh Đức", "2026-10-29", 7, 2, 1);
        addRow("anhht", "Hoàng Tuấn Anh", "2026-11-05", 5, 2, 0);
        addRow("myltk", "Lê Thị Kiều My", "2026-11-10", 4, 1, 1);
        addRow("phucvh", "Vũ Hoàng Phúc", "2026-11-14", 2, 0, 0);
    }

    // Getter để gắn sự kiện ở Phiên bản 2
    public JTextField getFromField() { return fromField; }
    public JTextField getToField() { return toField; }
    public JComboBox<String> getOperatorBox() { return operatorBox; }
    public JTextField getCountField() { return countField; }
    public JButton getViewButton() { return viewButton; }
}
