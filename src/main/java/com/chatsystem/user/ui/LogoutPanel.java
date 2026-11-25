package com.chatsystem.user.ui;

import com.chatsystem.common.ui.Theme;
import com.chatsystem.common.ui.UiUtil;

import javax.swing.*;
import java.awt.*;

/** Mục "Đăng xuất" ở menu: hỏi lại cho chắc, đồng ý thì gọi {@code onLogout}. */
public class LogoutPanel extends JPanel {

    public LogoutPanel(Runnable onLogout) {
        super(new GridBagLayout());
        setBackground(Theme.BACKGROUND);

        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setOpaque(false);

        JLabel title = UiUtil.title("Đăng xuất");
        title.setAlignmentX(CENTER_ALIGNMENT);
        JLabel sub = UiUtil.muted("Bạn sẽ quay lại màn hình đăng nhập.");
        sub.setAlignmentX(CENTER_ALIGNMENT);
        JButton button = UiUtil.dangerButton("Đăng xuất");
        button.setAlignmentX(CENTER_ALIGNMENT);
        button.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this, "Bạn muốn đăng xuất?", "Đăng xuất",
                    JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                onLogout.run();
            }
        });

        box.add(title);
        box.add(Box.createVerticalStrut(Theme.GAP));
        box.add(sub);
        box.add(Box.createVerticalStrut(Theme.GAP * 2));
        box.add(button);
        add(box);
    }
}
