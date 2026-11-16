package com.chatsystem.user.ui;

import com.chatsystem.common.ui.PlaceholderPanel;
import com.chatsystem.common.ui.SidebarMenu;
import com.chatsystem.common.ui.Theme;

import javax.swing.*;
import java.awt.*;

/**
 * Cửa sổ chính phân hệ người dùng (sau khi đăng nhập): menu bên trái, nội dung bên phải.
 * Làm xong màn hình nào thì thay PlaceholderPanel tương ứng bằng màn hình thật.
 * Các dialog (tạo nhóm, quản lý nhóm, báo cáo spam...) mở từ trong màn hình, không nằm trong menu.
 */
public class UserMainFrame extends JFrame {

    public UserMainFrame() {
        super("Chat System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(Theme.MAIN_WINDOW);
        setMinimumSize(new Dimension(960, 600));
        setLocationRelativeTo(null);

        SidebarMenu menu = new SidebarMenu("Chat System");

        menu.addItem("Tin nhắn", new PlaceholderPanel("Tin nhắn (danh sách hội thoại + khung chat)", "Nhân"));
        menu.addItem("Bạn bè", new PlaceholderPanel("Danh sách bạn bè", "Nhân"));
        menu.addItem("Lời mời kết bạn", new PlaceholderPanel("Lời mời kết bạn", "Nhân"));
        menu.addItem("Bạn bè đang online", new PlaceholderPanel("Bạn bè đang online", "Nhân"));
        menu.addItem("Tìm người", new PlaceholderPanel("Tìm người", "Tài"));
        menu.addItem("Tìm trong tin nhắn", new PlaceholderPanel("Tìm kiếm trong toàn bộ tin nhắn", "Nhân"));

        menu.addFooterItem("Tài khoản", new PlaceholderPanel("Thông tin tài khoản", "Tài"));
        menu.addFooterItem("Đổi mật khẩu", new PlaceholderPanel("Đổi mật khẩu", "Tài"));
        menu.addFooterItem("Đăng xuất", new PlaceholderPanel("Đăng xuất", "Tài"));

        getContentPane().setLayout(new BorderLayout());
        add(menu, BorderLayout.WEST);
        add(menu.getContentPanel(), BorderLayout.CENTER);
    }
}
