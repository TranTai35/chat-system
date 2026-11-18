package com.chatsystem.user.ui;

import com.chatsystem.common.ui.UiUtil;

import javax.swing.*;

/**
 * User 2. Màn đăng ký (wireframe user/05-register).
 * Phiên bản 1: chỉ giao diện, chưa kiểm tra dữ liệu.
 */
public class RegisterFrame extends AuthFrame {

    private final JTextField usernameField;
    private final JTextField fullNameField;
    private final JTextField emailField;
    private final JPasswordField passwordField;
    private final JPasswordField confirmField;
    private final JButton registerButton;

    public RegisterFrame() {
        super("Chat System - Đăng ký");

        JPanel card = buildCard("Tạo tài khoản", "Điền thông tin để đăng ký");
        usernameField = addField(card, "Tên đăng nhập", UiUtil.textField("vd: khangndh", 20));
        fullNameField = addField(card, "Họ tên", UiUtil.textField("vd: Nguyễn Văn A", 20));
        emailField = addField(card, "Email", UiUtil.textField("vd: ban@example.com", 20));
        passwordField = addField(card, "Mật khẩu", UiUtil.passwordField("", 20));
        confirmField = addField(card, "Nhập lại mật khẩu", UiUtil.passwordField("", 20));
        registerButton = addPrimaryButton(card, "Đăng ký");
        addFooter(card, "Đã có tài khoản?", "Đăng nhập", () -> switchTo(new LoginFrame()));

        getRootPane().setDefaultButton(registerButton);
    }

    // Getter để gắn sự kiện ở Phiên bản 2
    public JTextField getUsernameField() { return usernameField; }
    public JTextField getFullNameField() { return fullNameField; }
    public JTextField getEmailField() { return emailField; }
    public JPasswordField getPasswordField() { return passwordField; }
    public JPasswordField getConfirmField() { return confirmField; }
    public JButton getRegisterButton() { return registerButton; }
}
