package com.chatsystem.user;

import com.chatsystem.common.ui.Theme;
import com.chatsystem.user.ui.UserMainFrame;

import javax.swing.*;

/**
 * Điểm khởi chạy phân hệ người dùng.
 * Khi Tài làm xong màn hình Đăng nhập, đổi thành: mở LoginFrame trước, đăng nhập xong mới mở UserMainFrame.
 */
public class UserApp {

    public static void main(String[] args) {
        Theme.setup();
        SwingUtilities.invokeLater(() -> new UserMainFrame().setVisible(true));
    }
}
