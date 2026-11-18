package com.chatsystem.user.ui;

import com.chatsystem.common.ui.BaseTablePanel;
import com.chatsystem.common.ui.UiUtil;

import javax.swing.*;

/**
 * Người dùng. Danh sách bạn bè.
 * - Cột: chấm trạng thái, tên đăng nhập, họ tên, trạng thái (Online / Offline)
 * - Lọc theo tên, lọc theo trạng thái; sắp xếp theo tên
 * - Nhắn tin, huỷ kết bạn, chặn (chặn thì huỷ kết bạn luôn, người đó không gửi lời mời lại được)
 * Dữ liệu (Phiên bản 2): bảng friendships JOIN users (is_online).
 * Phiên bản 1: chỉ giao diện + dữ liệu giả, chưa xử lý sự kiện.
 */
public class FriendListPanel extends BaseTablePanel {

    // Thứ tự cột
    public static final int COL_DOT = 0;
    public static final int COL_USERNAME = 1;
    public static final int COL_FULLNAME = 2;
    public static final int COL_STATUS = 3;

    public static final String ONLINE = StatusCellRenderer.ONLINE;
    public static final String OFFLINE = StatusCellRenderer.OFFLINE;

    private final JComboBox<String> statusFilter;
    private final JButton chatButton;
    private final JButton unfriendButton;
    private final JButton blockButton;

    public FriendListPanel() {
        super("Bạn bè", new String[]{"", "Tên đăng nhập", "Họ tên", "Trạng thái"});

        // ----- Bộ lọc -----
        setSearchPlaceholder("Tìm theo tên");
        setSearchColumns(COL_USERNAME, COL_FULLNAME);
        statusFilter = addFilter("Trạng thái:", new JComboBox<>(new String[]{"Tất cả", ONLINE, OFFLINE}));

        // ----- Sắp xếp -----
        addSortOption("Tên (A-Z)", COL_FULLNAME, SortOrder.ASCENDING);
        addSortOption("Tên (Z-A)", COL_FULLNAME, SortOrder.DESCENDING);

        // ----- Nút thao tác: nút thường trước, nút nguy hiểm sau -----
        chatButton = addAction("Nhắn tin");
        unfriendButton = addAction(UiUtil.dangerButton("Huỷ kết bạn"));
        blockButton = addAction(UiUtil.dangerButton("Chặn"));

        // ----- Cột trạng thái: chấm màu + chữ màu -----
        StatusCellRenderer.installDotColumn(getTable(), COL_DOT);
        getTable().getColumnModel().getColumn(COL_STATUS).setCellRenderer(StatusCellRenderer.text());

        setColumnWidth(COL_USERNAME, 180);
        setColumnWidth(COL_FULLNAME, 280);
        setColumnWidth(COL_STATUS, 120);

        loadMockData();
    }

    /** Thêm 1 người bạn: cột chấm và cột trạng thái cùng lấy từ online. */
    public void addFriend(String username, String fullName, boolean online) {
        String status = StatusCellRenderer.of(online);
        addRow(status, username, fullName, status);
    }

    /** Dữ liệu giả để chụp hình báo cáo Phiên bản 1 (khớp wireframe). */
    private void loadMockData() {
        addFriend("lanpt", "Phạm Thị Lan", true);
        addFriend("minhlq", "Lê Quang Minh", false);
        addFriend("taitp", "Trần Phước Tài", true);
    }

    // Getter để gắn sự kiện ở Phiên bản 2
    public JComboBox<String> getStatusFilter() { return statusFilter; }
    public JButton getChatButton() { return chatButton; }
    public JButton getUnfriendButton() { return unfriendButton; }
    public JButton getBlockButton() { return blockButton; }
}
