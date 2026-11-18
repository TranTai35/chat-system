package com.chatsystem.user;

import com.chatsystem.common.ui.Theme;
import com.chatsystem.user.ui.LoginFrame;

import javax.swing.*;

/**
 * Điểm khởi chạy phân hệ người dùng.
 * Mở LoginFrame trước; bấm Đăng nhập thì mở UserMainFrame.
 */
public class UserApp {

    public static void main(String[] args) {
        Theme.setup();
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
