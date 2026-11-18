package com.chatsystem.user;

import com.chatsystem.common.ui.Theme;
import com.chatsystem.common.ui.UiUtil;
import com.chatsystem.user.ui.CreateGroupDialog;
import com.chatsystem.user.ui.ManageGroupDialog;
import com.chatsystem.user.ui.ReportSpamDialog;

import javax.swing.*;
import java.awt.*;

/**
 * Cửa sổ xem trước các dialog của người dùng, để chụp hình báo cáo khi các màn mở chúng
 * (Bạn bè đang online, khung chat) chưa ghép vào. Không dùng trong app chính.
 * Chạy: mvn compile exec:java -Dexec.mainClass=com.chatsystem.user.UserDialogPreview
 */
public class UserDialogPreview {

    public static void main(String[] args) {
        Theme.setup();
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Xem trước dialog người dùng");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JPanel p = new JPanel(new GridLayout(0, 1, 0, Theme.GAP));
            p.setBorder(UiUtil.padding(Theme.PADDING + 4));
            p.setBackground(Theme.BACKGROUND);

            JButton create = UiUtil.button("Tạo nhóm");
            create.addActionListener(e -> new CreateGroupDialog(f).setVisible(true));
            JButton manage = UiUtil.button("Quản lý nhóm");
            manage.addActionListener(e -> new ManageGroupDialog(f, "Nhóm đồ án Java").setVisible(true));
            JButton spam = UiUtil.button("Báo cáo spam");
            spam.addActionListener(e -> new ReportSpamDialog(f, "minhlq", "Lê Quang Minh").setVisible(true));

            p.add(create);
            p.add(manage);
            p.add(spam);
            f.setContentPane(p);
            f.setSize(280, 190);
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }
}
