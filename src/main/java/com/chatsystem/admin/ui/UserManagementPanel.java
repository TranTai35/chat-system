package com.chatsystem.admin.ui;

import com.chatsystem.common.ui.BaseTablePanel;
import com.chatsystem.common.ui.UiUtil;

import javax.swing.*;
import java.awt.Window;

/**
 * Admin 1. Quản lý danh sách người dùng.
 * - Lọc theo tên / tên đăng nhập / trạng thái, sắp xếp theo tên / ngày tạo
 * - Thêm / cập nhật / xoá, khoá / mở khoá, cập nhật mật khẩu
 * - Xem lịch sử đăng nhập, danh sách bạn bè
 * Phiên bản 1: chỉ giao diện + dữ liệu giả, chưa xử lý sự kiện.
 */
public class UserManagementPanel extends BaseTablePanel {

    // Thứ tự cột
    public static final int COL_USERNAME = 0;
    public static final int COL_FULLNAME = 1;
    public static final int COL_ADDRESS = 2;
    public static final int COL_DOB = 3;
    public static final int COL_GENDER = 4;
    public static final int COL_EMAIL = 5;
    public static final int COL_STATUS = 6;
    public static final int COL_CREATED = 7;

    private final JComboBox<String> statusFilter;
    private final JButton addButton;
    private final JButton editButton;
    private final JButton deleteButton;
    private final JButton lockButton;
    private final JButton passwordButton;
    private final JButton loginHistoryButton;
    private final JButton friendsButton;

    public UserManagementPanel() {
        super("Quản lý người dùng", new String[]{
                "Tên đăng nhập", "Họ tên", "Địa chỉ", "Ngày sinh",
                "Giới tính", "Email", "Trạng thái", "Ngày tạo"});

        // ----- Bộ lọc -----
        setSearchPlaceholder("Tìm theo tên / tên đăng nhập");
        setSearchColumns(COL_USERNAME, COL_FULLNAME);
        statusFilter = addFilter("Trạng thái:",
                new JComboBox<>(new String[]{"Tất cả", "Hoạt động", "Bị khoá"}));

        // ----- Sắp xếp -----
        addSortOption("Tên (A-Z)", COL_FULLNAME, SortOrder.ASCENDING);
        addSortOption("Tên (Z-A)", COL_FULLNAME, SortOrder.DESCENDING);
        addSortOption("Mới tạo nhất", COL_CREATED, SortOrder.DESCENDING);
        addSortOption("Cũ nhất", COL_CREATED, SortOrder.ASCENDING);

        // ----- Nút thao tác -----
        loginHistoryButton = addAction("Lịch sử đăng nhập");
        friendsButton = addAction("Bạn bè");
        passwordButton = addAction("Đổi mật khẩu");
        lockButton = addAction("Khoá / Mở khoá");
        deleteButton = addAction(UiUtil.dangerButton("Xoá"));
        editButton = addAction("Sửa");
        addButton = addAction(UiUtil.primaryButton("+ Thêm"));

        setColumnWidth(COL_USERNAME, 105);
        setColumnWidth(COL_FULLNAME, 150);
        setColumnWidth(COL_ADDRESS, 180);
        setColumnWidth(COL_EMAIL, 170);
        setColumnWidth(COL_DOB, 95);
        setColumnWidth(COL_GENDER, 70);
        setColumnWidth(COL_STATUS, 90);
        setColumnWidth(COL_CREATED, 95);

        loadMockData();
        wireDialogs();
    }

    /** Dữ liệu giả để chụp hình báo cáo Phiên bản 1. */
    private void loadMockData() {
        addRow("taitp", "Trần Phước Tài", "227 Nguyễn Văn Cừ, Q5, TP.HCM", "2006-05-03", "Nam", "tai@example.com", "Hoạt động", "2026-09-01");
        addRow("khangndh", "Nguyễn Đặng Hoàng Khang", "12 Lê Lợi, Q1, TP.HCM", "2006-02-14", "Nam", "khang@example.com", "Hoạt động", "2026-09-02");
        addRow("nhanntt", "Nguyễn Trần Trung Nhân", "45 Võ Văn Tần, Q3, TP.HCM", "2006-08-21", "Nam", "nhan@example.com", "Hoạt động", "2026-09-02");
        addRow("lanpt", "Phạm Thị Lan", "8 Trần Hưng Đạo, Q1, TP.HCM", "2005-11-30", "Nữ", "lan@example.com", "Hoạt động", "2026-09-15");
        addRow("minhlq", "Lê Quang Minh", "90 Điện Biên Phủ, Bình Thạnh", "2004-07-09", "Nam", "minh@example.com", "Bị khoá", "2026-09-20");
        addRow("huongvt", "Võ Thu Hương", "33 Phan Xích Long, Phú Nhuận", "2006-01-18", "Nữ", "huong@example.com", "Hoạt động", "2026-10-03");
        addRow("baodq", "Đỗ Quốc Bảo", "102 Cách Mạng Tháng 8, Q10", "2005-04-25", "Nam", "bao@example.com", "Hoạt động", "2026-10-11");
        addRow("trangnh", "Nguyễn Hà Trang", "5 Hoàng Văn Thụ, Tân Bình", "2006-12-02", "Nữ", "trang@example.com", "Bị khoá", "2026-10-18");
        addRow("ducnm", "Ngô Minh Đức", "71 Nguyễn Thị Minh Khai, Q3", "2005-03-12", "Nam", "duc@example.com", "Hoạt động", "2026-10-29");
        addRow("anhht", "Hoàng Tuấn Anh", "16 Lý Thường Kiệt, Q11", "2006-06-06", "Nam", "anh@example.com", "Hoạt động", "2026-11-05");
        addRow("myltk", "Lê Thị Kiều My", "28 Nguyễn Trãi, Q5", "2006-09-27", "Nữ", "my@example.com", "Hoạt động", "2026-11-10");
        addRow("phucvh", "Vũ Hoàng Phúc", "64 Pasteur, Q1", "2004-10-15", "Nam", "phuc@example.com", "Hoạt động", "2026-11-14");
    }

    /**
     * Mở các dialog khi bấm nút (để xem và chụp hình giao diện).
     * Phiên bản 2 sẽ thay phần thân của các listener này bằng xử lý thật.
     */
    private void wireDialogs() {
        addButton.addActionListener(e -> new UserFormDialog(owner()).setVisible(true));
        editButton.addActionListener(e -> withSelectedRow(row ->
                new UserFormDialog(owner(), cell(row, COL_USERNAME), cell(row, COL_FULLNAME),
                        cell(row, COL_ADDRESS), cell(row, COL_DOB), cell(row, COL_GENDER),
                        cell(row, COL_EMAIL)).setVisible(true)));
        deleteButton.addActionListener(e -> withSelectedRow(row ->
                ConfirmDialog.delete(owner(), cell(row, COL_USERNAME)).setVisible(true)));
        lockButton.addActionListener(e -> withSelectedRow(row -> {
            String username = cell(row, COL_USERNAME);
            boolean locked = "Bị khoá".equals(cell(row, COL_STATUS));
            (locked ? ConfirmDialog.unlock(owner(), username) : ConfirmDialog.lock(owner(), username))
                    .setVisible(true);
        }));
        passwordButton.addActionListener(e -> withSelectedRow(row ->
                new ChangePasswordDialog(owner(), cell(row, COL_USERNAME)).setVisible(true)));
        loginHistoryButton.addActionListener(e -> withSelectedRow(row ->
                new LoginHistoryDialog(owner(), cell(row, COL_USERNAME), cell(row, COL_FULLNAME)).setVisible(true)));
        friendsButton.addActionListener(e -> withSelectedRow(row ->
                new FriendListDialog(owner(), cell(row, COL_USERNAME), cell(row, COL_FULLNAME)).setVisible(true)));
    }

    private Window owner() {
        return SwingUtilities.getWindowAncestor(this);
    }

    private String cell(int modelRow, int column) {
        return String.valueOf(getModel().getValueAt(modelRow, column));
    }

    /** Chạy action với dòng đang chọn; chưa chọn dòng nào thì nhắc người dùng. */
    private void withSelectedRow(java.util.function.IntConsumer action) {
        int row = getSelectedModelRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(owner(), "Vui lòng chọn một người dùng trong bảng.");
            return;
        }
        action.accept(row);
    }

    // Getter để gắn sự kiện ở Phiên bản 2
    public JComboBox<String> getStatusFilter() { return statusFilter; }
    public JButton getAddButton() { return addButton; }
    public JButton getEditButton() { return editButton; }
    public JButton getDeleteButton() { return deleteButton; }
    public JButton getLockButton() { return lockButton; }
    public JButton getPasswordButton() { return passwordButton; }
    public JButton getLoginHistoryButton() { return loginHistoryButton; }
    public JButton getFriendsButton() { return friendsButton; }
}
