package com.chatsystem.admin.ui;

import com.chatsystem.common.ui.BaseDialog;
import com.chatsystem.common.ui.BaseTablePanel;

import javax.swing.*;
import java.awt.*;

/**
 * Admin 1. Dialog xem lịch sử đăng nhập của một người dùng (chỉ để xem).
 * Cột: thời gian, địa chỉ IP (bảng login_history).
 * Mở từ UserManagementPanel:
 * <pre>
 * new LoginHistoryDialog(SwingUtilities.getWindowAncestor(this), "taitp", "Trần Phước Tài").setVisible(true);
 * </pre>
 * Phiên bản 1: chỉ giao diện + dữ liệu giả, chưa xử lý sự kiện.
 */
public class LoginHistoryDialog extends BaseDialog {

    private final BaseTablePanel table;

    public LoginHistoryDialog(Window owner, String username, String fullName) {
        super(owner, "Lịch sử đăng nhập", "Người dùng: " + fullName + " (" + username + ")");

        // Tiêu đề đã nằm ở đầu dialog nên bảng không cần tiêu đề riêng
        table = new BaseTablePanel("", new String[]{"Thời gian", "Địa chỉ IP"});
        table.setSearchPlaceholder("Tìm theo thời gian / IP");
        table.setSearchColumns(0, 1);
        table.addSortOption("Mới nhất", 0, SortOrder.DESCENDING);
        table.addSortOption("Cũ nhất", 0, SortOrder.ASCENDING);
        table.setBorder(BorderFactory.createEmptyBorder());   // dialog đã có lề
        table.setOpaque(false);
        table.setColumnWidth(0, 200);
        table.setPreferredSize(new Dimension(520, 380));

        loadMockData();

        setContent(table);
        setViewOnly();
        finish();
    }

    /** Dữ liệu giả để chụp hình báo cáo Phiên bản 1. */
    private void loadMockData() {
        Object[][] rows = {
                {"2026-11-17 08:12", "113.161.24.10"},
                {"2026-11-15 21:40", "113.161.24.10"},
                {"2026-11-12 19:05", "192.168.1.15"},
                {"2026-11-03 07:48", "113.161.24.10"},
                {"2026-10-20 18:52", "113.161.24.10"},
                {"2026-10-08 22:15", "14.186.77.203"},
                {"2026-09-25 09:30", "113.161.24.10"},
                {"2026-09-01 14:02", "113.161.24.10"},
        };
        for (Object[] r : rows) {
            table.addRow(r);
        }
    }

    public BaseTablePanel getTablePanel() { return table; }
}
