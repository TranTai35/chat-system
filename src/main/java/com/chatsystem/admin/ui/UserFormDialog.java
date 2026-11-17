package com.chatsystem.admin.ui;

import com.chatsystem.common.ui.BaseDialog;
import com.chatsystem.common.ui.FormPanel;
import com.chatsystem.common.ui.UiUtil;

import javax.swing.*;
import java.awt.*;

/**
 * Admin 1. Dialog thêm / sửa người dùng.
 * - Thêm: new UserFormDialog(owner)
 * - Sửa:  new UserFormDialog(owner, username, họ tên, địa chỉ, ngày sinh, giới tính, email) (tên đăng nhập không sửa được)
 * Mở từ UserManagementPanel:
 * <pre>
 * new UserFormDialog(SwingUtilities.getWindowAncestor(this)).setVisible(true);
 * </pre>
 * Phiên bản 1: chỉ giao diện, chưa xử lý sự kiện.
 */
public class UserFormDialog extends BaseDialog {

    private final JTextField usernameField;
    private final JTextField fullNameField;
    private final JTextField addressField;
    private final JTextField dobField;
    private final JComboBox<String> genderBox;
    private final JTextField emailField;

    /** Dialog thêm người dùng mới. */
    public UserFormDialog(Window owner) {
        this(owner, false);
    }

    /** Dialog sửa người dùng: điền sẵn thông tin hiện tại. */
    public UserFormDialog(Window owner, String username, String fullName, String address,
                          String dob, String gender, String email) {
        this(owner, true);
        usernameField.setText(username);
        fullNameField.setText(fullName);
        addressField.setText(address);
        dobField.setText(dob);
        genderBox.setSelectedItem(gender);
        emailField.setText(email);
    }

    private UserFormDialog(Window owner, boolean edit) {
        super(owner,
                edit ? "Sửa người dùng" : "Thêm người dùng",
                edit ? "Cập nhật thông tin tài khoản" : "Nhập thông tin tài khoản mới");

        FormPanel form = new FormPanel();
        usernameField = form.addField("Tên đăng nhập", UiUtil.textField("vd: taitp", 20));
        fullNameField = form.addField("Họ tên", UiUtil.textField("vd: Trần Phước Tài", 20));
        addressField = form.addField("Địa chỉ", UiUtil.textField("vd: 227 Nguyễn Văn Cừ, Q5", 20));
        dobField = form.addField("Ngày sinh", UiUtil.textField("yyyy-MM-dd", 20));
        genderBox = form.addField("Giới tính", new JComboBox<>(new String[]{"Nam", "Nữ", "Khác"}));
        emailField = form.addField("Email", UiUtil.textField("vd: ban@example.com", 20));

        usernameField.setEnabled(!edit);   // tên đăng nhập chỉ nhập khi thêm mới

        setContent(form);
        setPrimaryText(edit ? "Lưu" : "Thêm");
        finish();
    }

    // Getter để gắn sự kiện ở Phiên bản 2
    public JTextField getUsernameField() { return usernameField; }
    public JTextField getFullNameField() { return fullNameField; }
    public JTextField getAddressField() { return addressField; }
    public JTextField getDobField() { return dobField; }
    public JComboBox<String> getGenderBox() { return genderBox; }
    public JTextField getEmailField() { return emailField; }
}
