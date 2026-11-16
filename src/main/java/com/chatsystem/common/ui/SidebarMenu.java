package com.chatsystem.common.ui;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Thanh menu dọc bên trái, đi kèm một vùng nội dung dùng CardLayout.
 * Bấm vào mục nào thì vùng nội dung chuyển sang màn hình tương ứng.
 *
 * <pre>
 * SidebarMenu menu = new SidebarMenu("Chat Admin");
 * menu.addItem("Quản lý người dùng", new UserManagementPanel());
 * menu.addItem("Danh sách nhóm chat", new GroupListPanel());
 * frame.add(menu, BorderLayout.WEST);
 * frame.add(menu.getContentPanel(), BorderLayout.CENTER);
 * </pre>
 */
public class SidebarMenu extends JPanel {

    private final JPanel itemsPanel = new JPanel();
    private final JPanel footerPanel = new JPanel();
    private final CardLayout cards = new CardLayout();
    private final JPanel content = new JPanel(cards);
    private final Map<String, JButton> buttons = new LinkedHashMap<>();
    private String activeKey;

    public SidebarMenu(String appName) {
        super(new BorderLayout());
        setBackground(Theme.SIDEBAR_BG);
        setPreferredSize(new Dimension(240, 0));

        JLabel name = new JLabel(appName);
        name.setFont(Theme.FONT_TITLE);
        name.setForeground(Color.WHITE);
        name.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setOpaque(false);
        footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS));
        footerPanel.setOpaque(false);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        JScrollPane scroll = new JScrollPane(itemsPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setViewportBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        add(name, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);

        content.setBackground(Theme.BACKGROUND);
    }

    /** Thêm 1 mục menu + màn hình của nó. Mục đầu tiên được chọn sẵn. */
    public void addItem(String label, JComponent screen) {
        addItem(label, screen, itemsPanel);
    }

    /** Mục nằm ở đáy menu (Tài khoản, Đăng xuất...). */
    public void addFooterItem(String label, JComponent screen) {
        addItem(label, screen, footerPanel);
    }

    /** Thêm một tiêu đề nhóm nhỏ giữa các mục menu. */
    public void addSection(String label) {
        JLabel l = new JLabel(label.toUpperCase());
        l.setFont(Theme.FONT_SMALL);
        l.setForeground(Theme.MUTED_TEXT);
        l.setBorder(BorderFactory.createEmptyBorder(14, 20, 6, 20));
        l.setAlignmentX(LEFT_ALIGNMENT);
        itemsPanel.add(l);
    }

    public void show(String label) {
        if (!buttons.containsKey(label)) {
            return;
        }
        if (activeKey != null) {
            style(buttons.get(activeKey), false);
        }
        activeKey = label;
        style(buttons.get(label), true);
        cards.show(content, label);
    }

    public JPanel getContentPanel() {
        return content;
    }

    private void addItem(String label, JComponent screen, JPanel target) {
        JButton b = new JButton(label);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setAlignmentX(LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        b.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setFont(Theme.FONT_BODY);
        style(b, false);
        b.addActionListener(e -> show(label));

        buttons.put(label, b);
        target.add(b);
        content.add(screen, label);
        if (activeKey == null) {
            show(label);
        }
    }

    private void style(JButton b, boolean active) {
        b.setBackground(active ? Theme.SIDEBAR_ACTIVE : Theme.SIDEBAR_BG);
        b.setForeground(active ? Color.WHITE : Theme.SIDEBAR_TEXT);
        b.setFont(active ? Theme.FONT_BOLD : Theme.FONT_BODY);
    }
}
