package com.chatsystem.admin.ui;

import com.chatsystem.common.ui.BaseDialog;
import com.chatsystem.common.ui.BaseTablePanel;

import javax.swing.*;
import java.awt.*;

/**
 * Admin 1. Dialog xem danh sách bạn bè của một người dùng (chỉ để xem).
 * Cột: tên đăng nhập, họ tên, ngày kết bạn (bảng friendships).
 * Mở từ UserManagementPanel:
 * <pre>
 * new FriendListDialog(SwingUtilities.getWindowAncestor(this), "taitp", "Trần Phước Tài").setVisible(true);
 * </pre>
 * Phiên bản 1: chỉ giao diện + dữ liệu giả, chưa xử lý sự kiện.
 */
public class FriendListDialog extends BaseDialog {

    private final BaseTablePanel table;

    public FriendListDialog(Window owner, String username, String fullName) {
        super(owner, "Danh sách bạn bè", "Người dùng: " + fullName + " (" + username + ")");

        // Tiêu đề đã nằm ở đầu dialog nên bảng không cần tiêu đề riêng
        table = new BaseTablePanel("", new String[]{"Tên đăng nhập", "Họ tên", "Ngày kết bạn"});
        table.setSearchPlaceholder("Tìm theo tên");
        table.setSearchColumns(0, 1);
        table.addSortOption("Tên (A-Z)", 1, SortOrder.ASCENDING);
        table.addSortOption("Mới kết bạn", 2, SortOrder.DESCENDING);
        table.setBorder(BorderFactory.createEmptyBorder());   // dialog đã có lề
        table.setOpaque(false);
        table.setColumnWidth(1, 220);
        table.setPreferredSize(new Dimension(640, 380));

        loadMockData();

        setContent(table);
        setViewOnly();
        finish();
    }

    /** Dữ liệu giả để chụp hình báo cáo Phiên bản 1 (9 bạn trực tiếp, khớp màn Số lượng bạn bè). */
    private void loadMockData() {
        Object[][] rows = {
                {"khangndh", "Nguyễn Đặng Hoàng Khang", "2026-09-03"},
                {"nhanntt", "Nguyễn Trần Trung Nhân", "2026-09-03"},
                {"lanpt", "Phạm Thị Lan", "2026-09-16"},
                {"minhlq", "Lê Quang Minh", "2026-09-21"},
                {"huongvt", "Võ Thu Hương", "2026-10-04"},
                {"baodq", "Đỗ Quốc Bảo", "2026-10-12"},
                {"ducnm", "Ngô Minh Đức", "2026-10-30"},
                {"anhht", "Hoàng Tuấn Anh", "2026-11-06"},
                {"myltk", "Lê Thị Kiều My", "2026-11-11"},
        };
        for (Object[] r : rows) {
            table.addRow(r);
        }
    }

    public BaseTablePanel getTablePanel() { return table; }
}
