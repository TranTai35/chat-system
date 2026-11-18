package com.chatsystem.user.ui;

import com.chatsystem.common.ui.FormPanel;
import com.chatsystem.common.ui.Theme;
import com.chatsystem.common.ui.UiUtil;

import javax.swing.*;
import java.awt.*;

/**
 * User 5. Đổi mật khẩu (wireframe user/08-change-password):
 * mật khẩu hiện tại, mật khẩu mới, nhập lại; mật khẩu mới tối thiểu 6 ký tự.
 * Phiên bản 1: chỉ giao diện, chưa xử lý sự kiện.
 */
public class ChangePasswordPanel extends JPanel {

    private static final int FORM_WIDTH = 420;

    private final JPasswordField currentField;
    private final JPasswordField newField;
    private final JPasswordField confirmField;
    private final JButton saveButton;

    public ChangePasswordPanel() {
        super(new BorderLayout());
        setBackground(Theme.BACKGROUND);
        setBorder(UiUtil.padding(Theme.PADDING));

        FormPanel form = new FormPanel();
        currentField = form.addField("Mật khẩu hiện tại", UiUtil.passwordField("", 20));
        newField = form.addField("Mật khẩu mới", UiUtil.passwordField("", 20));
        confirmField = form.addField("Nhập lại", UiUtil.passwordField("", 20));
        saveButton = UiUtil.primaryButton("Lưu");
        form.addButtons(saveButton);

        JPanel head = new JPanel(new BorderLayout(0, 4));
        head.setOpaque(false);
        head.add(UiUtil.title("Đổi mật khẩu"), BorderLayout.NORTH);
        head.add(UiUtil.muted("Mật khẩu mới tối thiểu 6 ký tự"), BorderLayout.CENTER);

        JPanel left = new JPanel(new BorderLayout(0, Theme.PADDING));
        left.setOpaque(false);
        left.add(head, BorderLayout.NORTH);
        left.add(form, BorderLayout.CENTER);
        left.setPreferredSize(new Dimension(FORM_WIDTH, left.getPreferredSize().height));   // chỉ cố định chiều rộng

        JPanel box = new JPanel(new BorderLayout());
        box.setOpaque(false);
        box.add(left, BorderLayout.NORTH);
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(box, BorderLayout.WEST);
        add(top, BorderLayout.NORTH);
    }

    // Getter để gắn sự kiện ở Phiên bản 2
    public JPasswordField getCurrentField() { return currentField; }
    public JPasswordField getNewField() { return newField; }
    public JPasswordField getConfirmField() { return confirmField; }
    public JButton getSaveButton() { return saveButton; }
}
