package com.chatsystem.admin;

import com.chatsystem.admin.ui.AdminMainFrame;
import com.chatsystem.common.ui.Theme;

import javax.swing.*;

/** Điểm khởi chạy phân hệ quản trị. */
public class AdminApp {

    public static void main(String[] args) {
        Theme.setup();
        SwingUtilities.invokeLater(() -> new AdminMainFrame().setVisible(true));
    }
}
