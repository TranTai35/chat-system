package com.chatsystem.user.ui;

import com.chatsystem.common.Session;
import com.chatsystem.common.ui.Async;
import com.chatsystem.common.ui.UiUtil;
import com.chatsystem.model.User;
import com.chatsystem.service.AuthService;

import javax.swing.*;

/**
 * User 1. Màn đăng nhập (wireframe user/04-login).
 * Đăng nhập đúng thì lưu vào {@link Session} rồi mở UserMainFrame.
 */
public class LoginFrame extends AuthFrame {

    private final AuthService authService = new AuthService();

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

        loginButton.addActionListener(e -> doLogin());
        getRootPane().setDefaultButton(loginButton);   // Enter = đăng nhập
    }

    private void doLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        if (username.trim().isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên đăng nhập và mật khẩu.",
                    "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }
        loginButton.setEnabled(false);
        Async.run(() -> authService.login(username, password, User.Role.USER),
                user -> {
                    Session.start(user);
                    switchTo(new UserMainFrame());
                },
                error -> {
                    loginButton.setEnabled(true);
                    passwordField.setText("");
                    Async.showError(error);
                });
    }

    // Getter để gắn sự kiện ở Phiên bản 2
    public JTextField getUsernameField() { return usernameField; }
    public JPasswordField getPasswordField() { return passwordField; }
    public JButton getLoginButton() { return loginButton; }
}
