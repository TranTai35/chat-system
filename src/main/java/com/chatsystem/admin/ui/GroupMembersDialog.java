package com.chatsystem.admin.ui;

import com.chatsystem.common.ui.BaseDialog;
import com.chatsystem.common.ui.BaseTablePanel;

import javax.swing.*;
import java.awt.*;

/**
 * Admin 3. Dialog xem thành viên / xem admin của một nhóm chat (chỉ để xem).
 * adminsOnly = false: danh sách thành viên; true: danh sách admin.
 * Mở từ GroupListPanel:
 * <pre>
 * new GroupMembersDialog(SwingUtilities.getWindowAncestor(this), "Nhóm đồ án Java", false).setVisible(true);
 * </pre>
 */
public class GroupMembersDialog extends BaseDialog {

    private final BaseTablePanel table;

    public GroupMembersDialog(Window owner, String groupName, boolean adminsOnly) {
        super(owner,
                adminsOnly ? "Admin của nhóm" : "Thành viên nhóm",
                "Nhóm: " + groupName);

        // Tiêu đề đã nằm ở đầu dialog nên bảng không cần tiêu đề riêng
        table = new BaseTablePanel("", new String[]{"Tên đăng nhập", "Họ tên", "Vai trò", "Ngày tham gia"});
        table.setSearchPlaceholder("Tìm theo tên");
        table.setSearchColumns(0, 1);
        table.addSortOption("Tên (A-Z)", 1, SortOrder.ASCENDING);
        table.addSortOption("Mới tham gia", 3, SortOrder.DESCENDING);
        table.setBorder(BorderFactory.createEmptyBorder());   // dialog đã có lề
        table.setOpaque(false);
        table.setColumnWidth(1, 200);
        table.setPreferredSize(new Dimension(640, 380));

        loadMockData(adminsOnly);

        setContent(table);
        setViewOnly();
        finish();
    }

    /** Dữ liệu giả để chụp hình báo cáo Phiên bản 1. */
    private void loadMockData(boolean adminsOnly) {
        Object[][] rows = {
                {"taitp", "Trần Phước Tài", "Admin", "2026-11-16"},
                {"khangndh", "Nguyễn Đặng Hoàng Khang", "Thành viên", "2026-11-16"},
                {"nhanntt", "Nguyễn Trần Trung Nhân", "Thành viên", "2026-11-16"},
        };
        for (Object[] r : rows) {
            if (!adminsOnly || "Admin".equals(r[2])) {
                table.addRow(r);
            }
        }
    }

    public BaseTablePanel getTablePanel() { return table; }
}
