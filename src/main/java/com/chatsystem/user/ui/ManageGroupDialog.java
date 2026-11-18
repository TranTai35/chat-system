package com.chatsystem.user.ui;

import com.chatsystem.common.ui.BaseDialog;
import com.chatsystem.common.ui.BaseTablePanel;
import com.chatsystem.common.ui.FormPanel;
import com.chatsystem.common.ui.Theme;
import com.chatsystem.common.ui.UiUtil;

import javax.swing.*;
import java.awt.*;

/**
 * User. Dialog quản lý nhóm chat (wireframe user/03-manage-group-dialog), chỉ admin của nhóm được sửa.
 * Đổi tên nhóm, bật mã hoá đầu cuối, thêm / xoá thành viên, gán admin.
 * Mở từ khung chat:
 * <pre>
 * new ManageGroupDialog(SwingUtilities.getWindowAncestor(this), "Nhóm đồ án Java").setVisible(true);
 * </pre>
 * Phiên bản 1: chỉ giao diện + thành viên giả, chưa xử lý sự kiện.
 */
public class ManageGroupDialog extends BaseDialog {

    // Thứ tự cột
    public static final int COL_USERNAME = 0;
    public static final int COL_FULLNAME = 1;
    public static final int COL_ROLE = 2;

    public static final String ADMIN = "Admin";
    public static final String MEMBER = "Thành viên";

    private final JTextField groupNameField;
    private final JButton renameButton;
    private final JCheckBox encryptBox;
    private final BaseTablePanel members;
    private final JButton addMemberButton;
    private final JButton makeAdminButton;
    private final JButton removeButton;

    public ManageGroupDialog(Window owner, String groupName) {
        super(owner, "Quản lý nhóm", "Chỉ admin của nhóm được sửa");

        // ----- Tên nhóm (kèm nút Đổi tên) và Mã hoá -----
        FormPanel form = new FormPanel();
        groupNameField = UiUtil.textField("", 20);
        groupNameField.setText(groupName);
        renameButton = UiUtil.button("Đổi tên");
        JPanel nameRow = new JPanel(new BorderLayout(Theme.GAP, 0));
        nameRow.setOpaque(false);
        nameRow.add(groupNameField, BorderLayout.CENTER);
        nameRow.add(renameButton, BorderLayout.EAST);
        form.addField("Tên nhóm", nameRow);

        encryptBox = new JCheckBox("Bật mã hoá đầu cuối cho nhóm", true);
        encryptBox.setOpaque(false);
        JPanel encryptRow = new JPanel(new FlowLayout(FlowLayout.LEFT, Theme.GAP, 0));
        encryptRow.setOpaque(false);
        encryptRow.add(encryptBox);
        encryptRow.add(UiUtil.muted("(biểu tượng khoá hiện ở danh sách hội thoại)"));
        form.addField("Mã hoá", encryptRow);

        // ----- Bảng thành viên: tiêu đề và các nút cùng hàng, nút nguy hiểm sau cùng -----
        members = new BaseTablePanel("Thành viên (3)", new String[]{"Tên đăng nhập", "Họ tên", "Vai trò"});
        members.setSearchPlaceholder("Tìm theo tên");
        members.setSearchColumns(COL_USERNAME, COL_FULLNAME);
        members.setBorder(BorderFactory.createEmptyBorder());   // dialog đã có lề
        members.setOpaque(false);
        members.setColumnWidth(COL_FULLNAME, 220);
        addMemberButton = members.addAction("+ Thêm thành viên");
        makeAdminButton = members.addAction("Gán admin");
        removeButton = members.addAction(UiUtil.dangerButton("Xoá khỏi nhóm"));
        members.setPreferredSize(new Dimension(640, 270));
        loadMockData();

        JPanel content = new JPanel(new BorderLayout(0, Theme.GAP));
        content.setOpaque(false);
        content.add(form, BorderLayout.NORTH);
        content.add(members, BorderLayout.CENTER);

        setContent(content);
        setPrimaryText("Lưu");
        finish();
    }

    /** Thêm 1 thành viên vào bảng. */
    public void addMember(String username, String fullName, String role) {
        members.addRow(username, fullName, role);
    }

    /** Dữ liệu giả khớp wireframe: 3 thành viên, 1 admin. */
    private void loadMockData() {
        addMember("taitp", "Trần Phước Tài", ADMIN);
        addMember("khangndh", "Nguyễn Đặng Hoàng Khang", MEMBER);
        addMember("nhanntt", "Nguyễn Trần Trung Nhân", MEMBER);
    }

    // Getter để gắn sự kiện ở Phiên bản 2
    public JTextField getGroupNameField() { return groupNameField; }
    public JButton getRenameButton() { return renameButton; }
    public JCheckBox getEncryptBox() { return encryptBox; }
    public BaseTablePanel getMembersPanel() { return members; }
    public JButton getAddMemberButton() { return addMemberButton; }
    public JButton getMakeAdminButton() { return makeAdminButton; }
    public JButton getRemoveButton() { return removeButton; }
}
