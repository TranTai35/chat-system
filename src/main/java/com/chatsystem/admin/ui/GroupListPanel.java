package com.chatsystem.admin.ui;

import com.chatsystem.common.ui.BaseTablePanel;

import javax.swing.*;

/**
 * Admin 3. Danh sách các nhóm chat.
 * - Sắp xếp theo tên / thời gian tạo, lọc theo tên nhóm
 * - Xem danh sách thành viên, danh sách admin của nhóm (mở GroupMembersDialog)
 * Phiên bản 1: chỉ giao diện + dữ liệu giả, chưa xử lý sự kiện.
 */
public class GroupListPanel extends BaseTablePanel {

    public static final int COL_NAME = 0;
    public static final int COL_MEMBERS = 1;
    public static final int COL_ADMINS = 2;
    public static final int COL_ENCRYPTED = 3;
    public static final int COL_CREATED = 4;

    private final JButton membersButton;
    private final JButton adminsButton;

    public GroupListPanel() {
        super("Danh sách nhóm chat", new String[]{
                "Tên nhóm", "Số thành viên", "Số admin", "Mã hoá", "Ngày tạo"});

        // ----- Bộ lọc -----
        setSearchPlaceholder("Tìm theo tên nhóm");
        setSearchColumns(COL_NAME);

        // ----- Sắp xếp -----
        addSortOption("Tên (A-Z)", COL_NAME, SortOrder.ASCENDING);
        addSortOption("Tên (Z-A)", COL_NAME, SortOrder.DESCENDING);
        addSortOption("Mới tạo nhất", COL_CREATED, SortOrder.DESCENDING);
        addSortOption("Cũ nhất", COL_CREATED, SortOrder.ASCENDING);

        // ----- Nút thao tác -----
        membersButton = addAction("Xem thành viên");
        adminsButton = addAction("Xem admin");

                // Mở dialog thành viên / admin của nhóm đang chọn (giống cách Tài gắn nút ở UserManagementPanel)
        membersButton.addActionListener(e -> openMembersDialog(false));
        adminsButton.addActionListener(e -> openMembersDialog(true));

        setColumnWidth(COL_NAME, 240);
        setColumnWidth(COL_MEMBERS, 110);
        setColumnWidth(COL_ADMINS, 90);
        setColumnWidth(COL_ENCRYPTED, 90);
        setColumnWidth(COL_CREATED, 110);

        loadMockData();
    }

    /** Dữ liệu giả để chụp hình báo cáo Phiên bản 1. */
    private void loadMockData() {
        addRow("Nhóm đồ án Java", 3, 1, "Có", "2026-11-16");
        addRow("Lớp 24CTT3", 42, 2, "Không", "2026-09-05");
        addRow("CLB Guitar HCMUS", 18, 2, "Không", "2026-09-12");
        addRow("Đá bóng thứ 7", 12, 1, "Không", "2026-09-28");
        addRow("Ôn thi Cấu trúc dữ liệu", 9, 1, "Có", "2026-10-02");
        addRow("Phòng trọ Q5", 4, 1, "Không", "2026-10-07");
        addRow("Team Hackathon", 5, 2, "Có", "2026-10-15");
        addRow("Gia đình", 6, 1, "Không", "2026-10-21");
        addRow("Học tiếng Nhật N4", 15, 1, "Không", "2026-10-30");
        addRow("Ăn vặt Nguyễn Văn Cừ", 23, 3, "Không", "2026-11-04");
        addRow("Mạng máy tính nhóm 2", 4, 1, "Có", "2026-11-09");
    }

        /** Chưa chọn dòng nào thì mở theo nhóm đầu bảng, để luôn có dữ liệu mock. */
    private void openMembersDialog(boolean adminsOnly) {
        int row = getSelectedModelRow();
        String groupName = (String) getModel().getValueAt(row >= 0 ? row : 0, COL_NAME);
        new GroupMembersDialog(SwingUtilities.getWindowAncestor(this), groupName, adminsOnly).setVisible(true);
    }

    // Getter để gắn sự kiện ở Phiên bản 2
    public JButton getMembersButton() { return membersButton; }
    public JButton getAdminsButton() { return adminsButton; }
}
