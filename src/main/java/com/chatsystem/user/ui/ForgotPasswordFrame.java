package com.chatsystem.user.ui;

import com.chatsystem.common.ui.UiUtil;

import javax.swing.*;

/**
 * User 3. Màn quên mật khẩu (wireframe user/06-forgot-password).
 * Phiên bản 1: chỉ giao diện, chưa gửi email.
 */
public class ForgotPasswordFrame extends AuthFrame {

    private final JTextField accountField;
    private final JButton sendButton;

    public ForgotPasswordFrame() {
        super("Chat System - Quên mật khẩu");

        JPanel card = buildCard("Quên mật khẩu", "Mật khẩu mới sẽ được gửi về email của bạn");
        accountField = addField(card, "Tên đăng nhập hoặc email", UiUtil.textField("vd: khangndh", 20));
        sendButton = addPrimaryButton(card, "Gửi mật khẩu mới");
        addFooter(card, "", "← Quay lại đăng nhập", () -> switchTo(new LoginFrame()));

        getRootPane().setDefaultButton(sendButton);
    }

    // Getter để gắn sự kiện ở Phiên bản 2
    public JTextField getAccountField() { return accountField; }
    public JButton getSendButton() { return sendButton; }
}
