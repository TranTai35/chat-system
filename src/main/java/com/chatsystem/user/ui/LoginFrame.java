package com.chatsystem.user.ui;

import com.chatsystem.common.ui.UiUtil;

import javax.swing.*;

/**
 * User 1. Màn đăng nhập (wireframe user/04-login).
 * Phiên bản 1: chỉ giao diện. Bấm "Đăng nhập" mở UserMainFrame, chưa kiểm tra tài khoản.
 */
public class LoginFrame extends AuthFrame {

    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JButton loginButton;

    public LoginFrame() {
        super("Chat System - Đăng nhập");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel card = buildCard("Chat System", "Đăng nhập để tiếp tục");
        usernameField = addField(card, "Tên đăng nhập", UiUtil.textField("vd: taitp", 20));
        passwordField = addField(card, "Mật khẩu", UiUtil.passwordField("", 20));
        addRightLink(card, "Quên mật khẩu?", () -> switchTo(new ForgotPasswordFrame()));
        loginButton = addPrimaryButton(card, "Đăng nhập");
        addFooter(card, "Chưa có tài khoản?", "Đăng ký", () -> switchTo(new RegisterFrame()));

        loginButton.addActionListener(e -> switchTo(new UserMainFrame()));
        getRootPane().setDefaultButton(loginButton);   // Enter = đăng nhập
    }

    // Getter để gắn sự kiện ở Phiên bản 2
    public JTextField getUsernameField() { return usernameField; }
    public JPasswordField getPasswordField() { return passwordField; }
    public JButton getLoginButton() { return loginButton; }
}
