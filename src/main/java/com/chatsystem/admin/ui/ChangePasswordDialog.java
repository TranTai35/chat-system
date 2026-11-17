package com.chatsystem.admin.ui;

import com.chatsystem.common.ui.BaseDialog;
import com.chatsystem.common.ui.FormPanel;
import com.chatsystem.common.ui.UiUtil;

import javax.swing.*;
import java.awt.*;

/**
 * Admin 1. Dialog đặt mật khẩu mới cho một người dùng (2 ô: mật khẩu mới và nhập lại).
 * Mở từ UserManagementPanel:
 * <pre>
 * new ChangePasswordDialog(SwingUtilities.getWindowAncestor(this), "taitp").setVisible(true);
 * </pre>
 * Phiên bản 1: chỉ giao diện, chưa xử lý sự kiện.
 */
public class ChangePasswordDialog extends BaseDialog {

    private final JPasswordField newPasswordField;
    private final JPasswordField confirmField;

    public ChangePasswordDialog(Window owner, String username) {
        super(owner, "Đổi mật khẩu",
                "Đặt mật khẩu mới cho tài khoản " + username + " (tối thiểu 6 ký tự)");

        FormPanel form = new FormPanel();
        newPasswordField = form.addField("Mật khẩu mới", UiUtil.passwordField("", 20));
        confirmField = form.addField("Nhập lại", UiUtil.passwordField("", 20));

        setContent(form);
        setPrimaryText("Lưu");
        finish();
    }

    // Getter để gắn sự kiện ở Phiên bản 2
    public JPasswordField getNewPasswordField() { return newPasswordField; }
    public JPasswordField getConfirmField() { return confirmField; }
}
