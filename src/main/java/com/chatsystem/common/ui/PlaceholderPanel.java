package com.chatsystem.common.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Màn hình tạm, giữ chỗ trong menu cho tới khi người phụ trách làm xong.
 * Người phụ trách thay dòng new PlaceholderPanel(...) trong MainFrame bằng màn hình thật.
 */
public class PlaceholderPanel extends JPanel {

    public PlaceholderPanel(String screenName, String owner) {
        super(new GridBagLayout());
        setBackground(Theme.BACKGROUND);

        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setOpaque(false);

        JLabel title = UiUtil.title(screenName);
        title.setAlignmentX(CENTER_ALIGNMENT);
        JLabel sub = UiUtil.muted("Đang xây dựng - phụ trách: " + owner);
        sub.setAlignmentX(CENTER_ALIGNMENT);

        box.add(title);
        box.add(Box.createVerticalStrut(Theme.GAP));
        box.add(sub);
        add(box);
    }
}
