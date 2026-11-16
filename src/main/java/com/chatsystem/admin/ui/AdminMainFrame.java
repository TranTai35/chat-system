package com.chatsystem.admin.ui;

import com.chatsystem.common.ui.PlaceholderPanel;
import com.chatsystem.common.ui.SidebarMenu;
import com.chatsystem.common.ui.Theme;

import javax.swing.*;
import java.awt.*;

/**
 * Cửa sổ chính phân hệ quản trị: menu bên trái, màn hình bên phải.
 * Làm xong màn hình nào thì thay PlaceholderPanel tương ứng bằng màn hình thật.
 */
public class AdminMainFrame extends JFrame {

    public AdminMainFrame() {
        super("Chat System - Quản trị");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(Theme.MAIN_WINDOW);
        setMinimumSize(new Dimension(1024, 640));
        setLocationRelativeTo(null);

        SidebarMenu menu = new SidebarMenu("Chat Admin");

        menu.addSection("Người dùng");
        menu.addItem("Quản lý người dùng", new UserManagementPanel());                         // Tài
        menu.addItem("Lịch sử đăng nhập", new PlaceholderPanel("Danh sách đăng nhập", "Khang"));
        menu.addItem("Người dùng mới", new PlaceholderPanel("Người dùng đăng ký mới", "Khang"));
        menu.addItem("Số lượng bạn bè", new PlaceholderPanel("Người dùng và số lượng bạn bè", "Khang"));
        menu.addItem("Người dùng hoạt động", new PlaceholderPanel("Người dùng hoạt động", "Khang"));

        menu.addSection("Nhóm & báo cáo");
        menu.addItem("Nhóm chat", new PlaceholderPanel("Danh sách nhóm chat", "Khang"));
        menu.addItem("Báo cáo spam", new PlaceholderPanel("Danh sách báo cáo spam", "Khang"));

        menu.addSection("Thống kê");
        menu.addItem("Biểu đồ đăng ký", new PlaceholderPanel("Biểu đồ đăng ký mới theo năm", "Khang"));
        menu.addItem("Biểu đồ hoạt động", new PlaceholderPanel("Biểu đồ hoạt động theo năm", "Khang"));

        menu.addFooterItem("Đăng xuất", new PlaceholderPanel("Đăng xuất", "Khang"));

        getContentPane().setLayout(new BorderLayout());
        add(menu, BorderLayout.WEST);
        add(menu.getContentPanel(), BorderLayout.CENTER);
    }
}
