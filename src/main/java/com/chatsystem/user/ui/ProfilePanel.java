package com.chatsystem.user.ui;

import com.chatsystem.common.ui.FormPanel;
import com.chatsystem.common.ui.Theme;
import com.chatsystem.common.ui.UiUtil;

import javax.swing.*;
import java.awt.*;

/**
 * User 4. Thông tin tài khoản (wireframe user/07-profile).
 * Tên đăng nhập không sửa được; các trường còn lại chỉnh rồi bấm "Lưu".
 * Phiên bản 1: chỉ giao diện, điền sẵn dữ liệu mẫu, chưa xử lý sự kiện.
 */
public class ProfilePanel extends JPanel {

    private static final int FORM_WIDTH = 520;

    private final JTextField usernameField;
    private final JTextField fullNameField;
    private final JTextField addressField;
    private final JTextField dobField;
    private final JComboBox<String> genderBox;
    private final JTextField emailField;
    private final JButton saveButton;

    public ProfilePanel() {
        super(new BorderLayout());
        setBackground(Theme.BACKGROUND);
        setBorder(UiUtil.padding(Theme.PADDING));

        FormPanel form = new FormPanel();
        usernameField = form.addField("Tên đăng nhập", UiUtil.textField("", 20));
        fullNameField = form.addField("Họ tên", UiUtil.textField("vd: Nguyễn Văn A", 20));
        addressField = form.addField("Địa chỉ", UiUtil.textField("vd: 12 Lê Lợi, Q1", 20));
        dobField = form.addField("Ngày sinh", UiUtil.textField("yyyy-MM-dd", 20));
        genderBox = form.addField("Giới tính", new JComboBox<>(new String[]{"Nam", "Nữ", "Khác"}));
        emailField = form.addField("Email", UiUtil.textField("vd: ban@example.com", 20));
        saveButton = UiUtil.primaryButton("Lưu");
        form.addButtons(saveButton);

        usernameField.setEnabled(false);   // tên đăng nhập không sửa
        loadMockData();

        JPanel left = new JPanel(new BorderLayout(0, Theme.PADDING));
        left.setOpaque(false);
        left.add(UiUtil.title("Thông tin tài khoản"), BorderLayout.NORTH);
        left.add(form, BorderLayout.CENTER);
        left.setPreferredSize(new Dimension(FORM_WIDTH, left.getPreferredSize().height));   // chỉ cố định chiều rộng

        // Form nằm trên cùng, sát trái, rộng cố định
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JPanel box = new JPanel(new BorderLayout());
        box.setOpaque(false);
        box.add(left, BorderLayout.NORTH);
        top.add(box, BorderLayout.WEST);
        add(top, BorderLayout.NORTH);
    }

    private void loadMockData() {
        usernameField.setText("khangndh");
        fullNameField.setText("Nguyễn Đặng Hoàng Khang");
        addressField.setText("12 Lê Lợi, Q1");
        dobField.setText("2005-03-14");
        genderBox.setSelectedItem("Nam");
        emailField.setText("khang@example.com");
    }

    // Getter để gắn sự kiện ở Phiên bản 2
    public JTextField getUsernameField() { return usernameField; }
    public JTextField getFullNameField() { return fullNameField; }
    public JTextField getAddressField() { return addressField; }
    public JTextField getDobField() { return dobField; }
    public JComboBox<String> getGenderBox() { return genderBox; }
    public JTextField getEmailField() { return emailField; }
    public JButton getSaveButton() { return saveButton; }
}
