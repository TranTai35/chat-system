package com.chatsystem.common.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Khung dialog chuẩn: tiêu đề + mô tả ngắn + nội dung + hàng nút (Huỷ / nút chính) ở đáy.
 * Mọi dialog (thêm/sửa user, đổi mật khẩu, tạo nhóm, báo cáo spam...) kế thừa lớp này.
 *
 * <pre>
 * public class ChangePasswordDialog extends BaseDialog {
 *     public ChangePasswordDialog(Window owner) {
 *         super(owner, "Đổi mật khẩu", "Mật khẩu mới tối thiểu 6 ký tự");
 *         FormPanel form = new FormPanel();
 *         form.addField("Mật khẩu mới", UiUtil.passwordField("", 20));
 *         setContent(form);
 *         setPrimaryText("Lưu");
 *         finish();          // gọi cuối constructor: tính kích thước, căn giữa
 *     }
 * }
 * </pre>
 */
public class BaseDialog extends JDialog {

    private final JPanel body = new JPanel(new BorderLayout());
    private final JButton cancelButton = UiUtil.button("Huỷ");
    private final JButton primaryButton = UiUtil.primaryButton("Lưu");

    public BaseDialog(Window owner, String title, String subtitle) {
        super(owner, title, ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(0, Theme.PADDING));
        root.setBorder(UiUtil.padding(Theme.PADDING + 4));
        root.setBackground(Theme.SURFACE);

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);
        JLabel t = UiUtil.heading(title);
        t.setAlignmentX(LEFT_ALIGNMENT);
        header.add(t);
        if (subtitle != null && !subtitle.isEmpty()) {
            JLabel s = UiUtil.muted(subtitle);
            s.setAlignmentX(LEFT_ALIGNMENT);
            header.add(Box.createVerticalStrut(4));
            header.add(s);
        }

        body.setOpaque(false);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, Theme.GAP, 0));
        buttons.setOpaque(false);
        buttons.add(cancelButton);
        buttons.add(primaryButton);
        cancelButton.addActionListener(e -> dispose());   // Huỷ luôn đóng dialog
        getRootPane().setDefaultButton(primaryButton);     // Enter = nút chính

        root.add(header, BorderLayout.NORTH);
        root.add(body, BorderLayout.CENTER);
        root.add(buttons, BorderLayout.SOUTH);
        setContentPane(root);
    }

    /** Đặt nội dung chính (thường là FormPanel hoặc 1 BaseTablePanel nhỏ). */
    public void setContent(JComponent content) {
        body.removeAll();
        body.add(content, BorderLayout.CENTER);
    }

    public void setPrimaryText(String text) {
        primaryButton.setText(text);
    }

    /** Biến nút chính thành nút đỏ (Xoá, Khoá, Báo cáo...). */
    public void setPrimaryDanger() {
        primaryButton.setBackground(Theme.DANGER);
    }

    /** Dialog chỉ để xem (lịch sử đăng nhập, DS bạn bè): ẩn nút Huỷ, nút chính thành "Đóng". */
    public void setViewOnly() {
        cancelButton.setVisible(false);
        primaryButton.setText("Đóng");
        primaryButton.addActionListener(e -> dispose());
    }

    /** Gọi ở cuối constructor của lớp con. */
    protected void finish() {
        pack();
        setSize(Math.max(getWidth(), Theme.DIALOG_WIDTH), getHeight());
        setLocationRelativeTo(getOwner());
    }

    public JButton getPrimaryButton() { return primaryButton; }

    public JButton getCancelButton() { return cancelButton; }
}
