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
        menu.addItem("Danh sách đăng nhập", new LoginListPanel());  // Khang
        menu.addItem("Người dùng mới", new NewUserListPanel());  // Khang
        menu.addItem("Số lượng bạn bè", new FriendCountListPanel());  // Khang
        menu.addItem("Người dùng hoạt động", new ActiveUserListPanel());  // Khang

        menu.addSection("Nhóm & báo cáo");
        menu.addItem("Nhóm chat", new GroupListPanel());  // Khang
        menu.addItem("Báo cáo spam", new SpamReportListPanel());  // Khang

        menu.addSection("Thống kê");
        menu.addItem("Biểu đồ đăng ký", new RegistrationChartPanel());  // Khang
        menu.addItem("Biểu đồ hoạt động", new ActivityChartPanel());  // Khang

        menu.addFooterItem("Đăng xuất", new PlaceholderPanel("Đăng xuất", "Khang"));

        getContentPane().setLayout(new BorderLayout());
        add(menu, BorderLayout.WEST);
        add(menu.getContentPanel(), BorderLayout.CENTER);
    }
}
