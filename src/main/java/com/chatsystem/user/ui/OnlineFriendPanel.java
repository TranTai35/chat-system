package com.chatsystem.user.ui;

import com.chatsystem.common.ui.BaseTablePanel;
import com.chatsystem.common.ui.UiUtil;

import javax.swing.*;

/**
 * Người dùng. Danh sách bạn bè đang online.
 * - Cột: chấm online, tên đăng nhập, họ tên
 * - Lọc theo tên
 * - Nhắn tin (mở khung chat riêng), + Tạo nhóm (mở dialog tạo nhóm với người đang chọn)
 * Dữ liệu (Phiên bản 2): friendships JOIN users WHERE is_online = TRUE.
 * Phiên bản 1: chỉ giao diện + dữ liệu giả, chưa xử lý sự kiện.
 */
public class OnlineFriendPanel extends BaseTablePanel {

    // Thứ tự cột
    public static final int COL_DOT = 0;
    public static final int COL_USERNAME = 1;
    public static final int COL_FULLNAME = 2;

    private final JButton createGroupButton;
    private final JButton chatButton;

    public OnlineFriendPanel() {
        super("Bạn bè đang online", new String[]{"", "Tên đăng nhập", "Họ tên"});

        // ----- Bộ lọc -----
        setSearchPlaceholder("Tìm theo tên");
        setSearchColumns(COL_USERNAME, COL_FULLNAME);

        // ----- Nút thao tác: nút chính "Nhắn tin" ngoài cùng bên phải -----
        createGroupButton = addAction("+ Tạo nhóm");
        chatButton = addAction(UiUtil.primaryButton("Nhắn tin"));

        StatusCellRenderer.installDotColumn(getTable(), COL_DOT);
        setColumnWidth(COL_USERNAME, 180);
        setColumnWidth(COL_FULLNAME, 280);

        loadMockData();
    }

    /** Thêm 1 người bạn đang online. */
    public void addOnlineFriend(String username, String fullName) {
        addRow(StatusCellRenderer.ONLINE, username, fullName);
    }

    /** Dữ liệu giả để chụp hình báo cáo Phiên bản 1 (khớp wireframe và màn Bạn bè). */
    private void loadMockData() {
        addOnlineFriend("lanpt", "Phạm Thị Lan");
        addOnlineFriend("taitp", "Trần Phước Tài");
    }

    // Getter để gắn sự kiện ở Phiên bản 2
    public JButton getCreateGroupButton() { return createGroupButton; }
    public JButton getChatButton() { return chatButton; }
}
