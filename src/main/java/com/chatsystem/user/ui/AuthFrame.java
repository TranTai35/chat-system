package com.chatsystem.user.ui;

import com.chatsystem.common.ui.Theme;
import com.chatsystem.common.ui.UiUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Khung chung cho 3 màn xác thực (đăng nhập, đăng ký, quên mật khẩu):
 * nền BACKGROUND, một thẻ trắng ở giữa, kích thước Theme.AUTH_WINDOW, không cho resize.
 * Nhãn nằm trên ô nhập (theo docs/DESIGN.md mục 5.3).
 * Mỗi hàng là một panel rộng đúng bằng thẻ nên mọi thành phần thẳng lề, đối xứng hai bên.
 */
public abstract class AuthFrame extends JFrame {

    private static final int CONTENT_WIDTH = 340;
    private static final int FIELD_HEIGHT = 36;

    protected AuthFrame(String title) {
        super(title);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(Theme.AUTH_WINDOW);
        setResizable(false);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(Theme.BACKGROUND);
        setContentPane(root);
    }

    /** Một hàng rộng bằng thẻ, cao theo nội dung. */
    private static JPanel row(LayoutManager layout) {
        JPanel p = new JPanel(layout) {
            @Override
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                return new Dimension(CONTENT_WIDTH, d.height);
            }

            @Override
            public Dimension getMaximumSize() {
                return new Dimension(CONTENT_WIDTH, getPreferredSize().height);
            }
        };
        p.setOpaque(false);
        p.setAlignmentX(LEFT_ALIGNMENT);
        return p;
    }

    private static void gap(JPanel card, int h) {
        card.add(Box.createVerticalStrut(h));
    }

    /** Tạo thẻ trắng có tiêu đề và mô tả (căn giữa), đặt ở giữa cửa sổ. */
    protected JPanel buildCard(String heading, String subtitle) {
        JPanel card = UiUtil.card(null);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JPanel t = row(new FlowLayout(FlowLayout.CENTER, 0, 0));
        t.add(UiUtil.title(heading));
        card.add(t);
        gap(card, 4);
        JPanel s = row(new FlowLayout(FlowLayout.CENTER, 0, 0));
        s.add(UiUtil.muted(subtitle));
        card.add(s);
        gap(card, 20);

        ((JPanel) getContentPane()).add(card, new GridBagConstraints());
        return card;
    }

    /** Thêm nhãn (trên) và ô nhập kéo giãn hết chiều ngang thẻ. */
    protected <T extends JComponent> T addField(JPanel card, String label, T input) {
        JPanel l = row(new BorderLayout());
        l.add(new JLabel(label), BorderLayout.WEST);
        card.add(l);
        gap(card, 4);

        input.setPreferredSize(new Dimension(CONTENT_WIDTH, FIELD_HEIGHT));
        JPanel f = row(new BorderLayout());
        f.add(input, BorderLayout.CENTER);
        card.add(f);
        gap(card, 12);
        return input;
    }

    /** Nút chính rộng hết thẻ. */
    protected JButton addPrimaryButton(JPanel card, String text) {
        JButton b = UiUtil.primaryButton(text);
        b.setPreferredSize(new Dimension(CONTENT_WIDTH, FIELD_HEIGHT));
        JPanel r = row(new BorderLayout());
        r.add(b, BorderLayout.CENTER);
        card.add(r);
        gap(card, 12);
        return b;
    }

    /** Dòng link chữ xanh sát lề phải ô nhập (ví dụ "Quên mật khẩu?"). */
    protected void addRightLink(JPanel card, String text, Runnable action) {
        JPanel r = row(new BorderLayout());
        r.add(link(text, action), BorderLayout.EAST);
        card.add(r);
        gap(card, 12);
    }

    /** Dòng chữ thường + link, căn giữa (ví dụ "Chưa có tài khoản? Đăng ký"). */
    protected void addFooter(JPanel card, String plain, String linkText, Runnable action) {
        JPanel r = row(new FlowLayout(FlowLayout.CENTER, 4, 0));
        if (!plain.isEmpty()) {
            r.add(UiUtil.muted(plain));
        }
        r.add(link(linkText, action));
        card.add(r);
    }

    /** Link: chữ PRIMARY, con trỏ hình bàn tay, bấm thì chạy action. */
    protected JLabel link(String text, Runnable action) {
        JLabel l = new JLabel(text);
        l.setForeground(Theme.PRIMARY);
        l.setFont(Theme.FONT_BOLD);
        l.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        l.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                action.run();
            }
        });
        return l;
    }

    /** Mở một màn xác thực khác rồi đóng màn hiện tại. */
    protected void switchTo(JFrame next) {
        next.setVisible(true);
        dispose();
    }
}
