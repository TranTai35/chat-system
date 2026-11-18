package com.chatsystem.user.ui;

import com.chatsystem.common.ui.BaseTablePanel;
import com.chatsystem.common.ui.UiUtil;

import javax.swing.*;
import java.awt.*;

/**
 * User. Tìm người dùng khác theo tên đăng nhập hoặc họ tên (wireframe user/12-find-people).
 * - Cột: tên đăng nhập, họ tên, quan hệ (Chưa kết bạn / Bạn bè / Đã gửi lời mời)
 * - Nút: Nhắn tin, Chặn, Báo cáo spam, + Kết bạn (nút chính, ngoài cùng bên phải)
 * Dữ liệu (Phiên bản 2): bảng users, quan hệ lấy từ friendships và friend_requests.
 * Phiên bản 1: chỉ giao diện + dữ liệu giả. Riêng "Báo cáo spam" mở ReportSpamDialog cho dòng đang chọn.
 */
public class FindPeoplePanel extends BaseTablePanel {

    // Thứ tự cột
    public static final int COL_USERNAME = 0;
    public static final int COL_FULLNAME = 1;
    public static final int COL_RELATION = 2;

    public static final String NOT_FRIEND = "Chưa kết bạn";
    public static final String FRIEND = "Bạn bè";
    public static final String REQUEST_SENT = "Đã gửi lời mời";

    private final JButton searchButton;
    private final JButton chatButton;
    private final JButton blockButton;
    private final JButton reportButton;
    private final JButton addFriendButton;

    public FindPeoplePanel() {
        super("Tìm người", new String[]{"Tên đăng nhập", "Họ tên", "Quan hệ"});

        // ----- Ô tìm + nút Tìm -----
        setSearchPlaceholder("Nhập tên đăng nhập hoặc họ tên");
        setSearchColumns(COL_USERNAME, COL_FULLNAME);
        searchButton = addFilter(null, UiUtil.button("Tìm"));

        // ----- Nút thao tác: nút thường, nút nguy hiểm, nút chính ngoài cùng bên phải -----
        chatButton = addAction("Nhắn tin");
        blockButton = addAction(UiUtil.dangerButton("Chặn"));
        reportButton = addAction(UiUtil.dangerButton("Báo cáo spam"));
        addFriendButton = addAction(UiUtil.primaryButton("+ Kết bạn"));

        setColumnWidth(COL_USERNAME, 180);
        setColumnWidth(COL_FULLNAME, 280);
        setColumnWidth(COL_RELATION, 160);

        loadMockData();
        wireReportDialog();
    }

    /** Thêm 1 người vào kết quả tìm. */
    public void addPerson(String username, String fullName, String relation) {
        addRow(username, fullName, relation);
    }

    /** Dữ liệu giả để chụp hình báo cáo Phiên bản 1 (khớp wireframe). */
    private void loadMockData() {
        addPerson("huongvt", "Võ Thu Hương", NOT_FRIEND);
        addPerson("lanpt", "Phạm Thị Lan", FRIEND);
        addPerson("ducnm", "Ngô Minh Đức", REQUEST_SENT);
    }

    /** Duy nhất nút "Báo cáo spam" được nối sẵn để xem dialog; các nút còn lại để Phiên bản 2. */
    private void wireReportDialog() {
        reportButton.addActionListener(e -> {
            int row = getSelectedModelRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một người trong danh sách.");
                return;
            }
            new ReportSpamDialog(SwingUtilities.getWindowAncestor(this),
                    String.valueOf(getModel().getValueAt(row, COL_USERNAME)),
                    String.valueOf(getModel().getValueAt(row, COL_FULLNAME))).setVisible(true);
        });
    }

    // Getter để gắn sự kiện ở Phiên bản 2
    public JButton getSearchButton() { return searchButton; }
    public JButton getChatButton() { return chatButton; }
    public JButton getBlockButton() { return blockButton; }
    public JButton getReportButton() { return reportButton; }
    public JButton getAddFriendButton() { return addFriendButton; }
}
