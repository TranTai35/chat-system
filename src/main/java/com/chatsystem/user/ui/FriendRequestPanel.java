package com.chatsystem.user.ui;

import com.chatsystem.common.ui.BaseTablePanel;
import com.chatsystem.common.ui.UiUtil;

import javax.swing.*;

/**
 * Người dùng. Danh sách lời mời kết bạn đang chờ.
 * - Cột: tên đăng nhập, họ tên, thời gian gửi (yyyy-MM-dd HH:mm)
 * - Lọc theo tên; sắp xếp theo thời gian gửi
 * - Đồng ý (thêm 2 dòng friendships, xoá lời mời) / Từ chối (xoá lời mời)
 * Dữ liệu (Phiên bản 2): bảng friend_requests (receiver_id = mình) JOIN users.
 * Phiên bản 1: chỉ giao diện + dữ liệu giả, chưa xử lý sự kiện.
 */
public class FriendRequestPanel extends BaseTablePanel {

    // Thứ tự cột
    public static final int COL_USERNAME = 0;
    public static final int COL_FULLNAME = 1;
    public static final int COL_SENT_AT = 2;

    private final JButton rejectButton;
    private final JButton acceptButton;

    public FriendRequestPanel() {
        super("Lời mời kết bạn", new String[]{"Tên đăng nhập", "Họ tên", "Thời gian gửi"});

        // ----- Bộ lọc -----
        setSearchPlaceholder("Tìm theo tên");
        setSearchColumns(COL_USERNAME, COL_FULLNAME);

        // ----- Sắp xếp: mặc định lời mời mới nhất lên đầu -----
        addSortOption("Mới nhất", COL_SENT_AT, SortOrder.DESCENDING);
        addSortOption("Cũ nhất", COL_SENT_AT, SortOrder.ASCENDING);
        addSortOption("Tên (A-Z)", COL_FULLNAME, SortOrder.ASCENDING);

        // ----- Nút thao tác: nút nguy hiểm trước, nút chính ngoài cùng bên phải -----
        rejectButton = addAction(UiUtil.dangerButton("Từ chối"));
        acceptButton = addAction(UiUtil.primaryButton("Đồng ý"));

        setColumnWidth(COL_USERNAME, 180);
        setColumnWidth(COL_FULLNAME, 280);
        setColumnWidth(COL_SENT_AT, 160);

        loadMockData();
    }

    /** Dữ liệu giả để chụp hình báo cáo Phiên bản 1 (khớp wireframe). */
    private void loadMockData() {
        addRow("myltk", "Lê Thị Kiều My", "2026-11-16 20:10");
        addRow("phucvh", "Vũ Hoàng Phúc", "2026-11-15 08:44");
    }

    // Getter để gắn sự kiện ở Phiên bản 2
    public JButton getRejectButton() { return rejectButton; }
    public JButton getAcceptButton() { return acceptButton; }
}
