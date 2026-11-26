package com.chatsystem.user.ui;

import com.chatsystem.common.Session;
import com.chatsystem.common.ui.Async;
import com.chatsystem.common.ui.SidebarMenu;
import com.chatsystem.common.ui.Theme;
import com.chatsystem.service.AuthService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Cửa sổ chính phân hệ người dùng (sau khi đăng nhập): menu bên trái, nội dung bên phải.
 * Làm xong màn hình nào thì thay PlaceholderPanel tương ứng bằng màn hình thật.
 * Các dialog (tạo nhóm, quản lý nhóm, báo cáo spam...) mở từ trong màn hình, không nằm trong menu.
 */
public class UserMainFrame extends JFrame {

    private final AuthService authService = new AuthService();

    /** Gửi tín hiệu "còn sống" định kỳ để bạn bè thấy mình online; dừng khi đăng xuất. */
    private final Timer heartbeatTimer = new Timer(AuthService.HEARTBEAT_SECONDS * 1000, e -> sendHeartbeat());

    public UserMainFrame() {
        super("Chat System");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);   // tự xử lý ở dưới: ghi đăng xuất rồi mới thoát
        setSize(Theme.MAIN_WINDOW);
        setMinimumSize(new Dimension(960, 600));
        setLocationRelativeTo(null);

        SidebarMenu menu = new SidebarMenu("Chat System");

        ChatPanel chatPanel = new ChatPanel();
        MessageSearchPanel messageSearchPanel = new MessageSearchPanel();

        menu.addItem("Tin nhắn", chatPanel);
        menu.addItem("Bạn bè", new FriendListPanel());
        menu.addItem("Lời mời kết bạn", new FriendRequestPanel());
        menu.addItem("Bạn bè đang online", new OnlineFriendPanel());
        menu.addItem("Tìm người", new FindPeoplePanel());
        menu.addItem("Tìm trong tin nhắn", messageSearchPanel);

        menu.addFooterItem("Tài khoản", new ProfilePanel());
        menu.addFooterItem("Đổi mật khẩu", new ChangePasswordPanel());
        menu.addFooterItem("Đăng xuất", new LogoutPanel(this::logout));

        getContentPane().setLayout(new BorderLayout());
        add(menu, BorderLayout.WEST);
        add(menu.getContentPanel(), BorderLayout.CENTER);

        heartbeatTimer.start();

        // Đóng cửa sổ = đăng xuất: ghi lại để bạn bè thấy mình offline
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                endSession(() -> System.exit(0));
            }
        });

        // Bấm 1 kết quả tìm kiếm -> chuyển sang "Tin nhắn", mở hội thoại và nhảy tới tin khớp từ khoá
        messageSearchPanel.setOpenChatHandler((conversation, keyword) -> {
            menu.show("Tin nhắn");
            chatPanel.openConversation(conversation, keyword);
        });
    }

    /** Đăng xuất: ghi vào CSDL, xoá phiên rồi quay về màn đăng nhập. */
    private void logout() {
        endSession(() -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
    }

    private void sendHeartbeat() {
        if (!Session.isLoggedIn()) {
            return;
        }
        int userId = Session.currentUserId();
        // Lỗi CSDL thoáng qua thì bỏ qua, lần sau gửi lại; không hiện hộp thoại mỗi 30 giây.
        Async.run(() -> authService.heartbeat(userId), ok -> { }, error -> { });
    }

    /** Ghi đăng xuất vào CSDL (trong luồng nền), xong thì chạy {@code then}. Lỗi mạng/CSDL không chặn việc thoát. */
    private void endSession(Runnable then) {
        heartbeatTimer.stop();
        if (!Session.isLoggedIn()) {
            then.run();
            return;
        }
        int userId = Session.currentUserId();
        Async.run(() -> {
            authService.logout(userId);
            return null;
        }, ok -> {
            Session.end();
            then.run();
        }, error -> {
            Session.end();
            then.run();
        });
    }
}
