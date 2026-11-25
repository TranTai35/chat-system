package com.chatsystem.user.ui;

import com.chatsystem.common.ui.Async;
import com.chatsystem.common.ui.Theme;
import com.chatsystem.common.ui.UiUtil;
import com.chatsystem.model.User;
import com.chatsystem.service.AuthService;
import com.chatsystem.service.ServiceException;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * User 2. Màn đăng ký (wireframe user/05-register).
 * Kiểm tra dữ liệu ở {@link AuthService#register}; màn hình chỉ thu thập và hiện thông báo.
 */
public class RegisterFrame extends AuthFrame {

    private final AuthService authService = new AuthService();

    private final JTextField usernameField;
    private final JTextField fullNameField;
    private final JTextField addressField;
    private final JTextField dobField;
    private final JComboBox<String> genderBox;
    private final JTextField emailField;
    private final JPasswordField passwordField;
    private final JPasswordField confirmField;
    private final JButton registerButton;

    public RegisterFrame() {
        super("Chat System - Đăng ký");
        setSize(new Dimension(Theme.AUTH_WINDOW.width, 800));
        setLocationRelativeTo(null);

        JPanel card = buildCard("Tạo tài khoản", "Điền thông tin để đăng ký");
        usernameField = addField(card, "Tên đăng nhập", UiUtil.textField("vd: khangndh", 20));
        fullNameField = addField(card, "Họ tên", UiUtil.textField("vd: Nguyễn Văn A", 20));
        addressField = addField(card, "Địa chỉ", UiUtil.textField("không bắt buộc", 20));
        dobField = addField(card, "Ngày sinh", UiUtil.textField("yyyy-MM-dd, ví dụ 2005-03-14", 20));
        genderBox = addField(card, "Giới tính", new JComboBox<>(new String[]{"Nam", "Nữ", "Khác"}));
        emailField = addField(card, "Email", UiUtil.textField("vd: ban@example.com", 20));
        passwordField = addField(card, "Mật khẩu", UiUtil.passwordField("", 20));
        confirmField = addField(card, "Nhập lại mật khẩu", UiUtil.passwordField("", 20));
        registerButton = addPrimaryButton(card, "Đăng ký");
        addFooter(card, "Đã có tài khoản?", "Đăng nhập", () -> switchTo(new LoginFrame()));

        registerButton.addActionListener(e -> doRegister());
        getRootPane().setDefaultButton(registerButton);
    }

    private void doRegister() {
        String password = new String(passwordField.getPassword());
        if (!password.equals(new String(confirmField.getPassword()))) {
            warn("Mật khẩu nhập lại không khớp.");
            return;
        }
        LocalDate dob = null;
        String dobText = dobField.getText().trim();
        if (!dobText.isEmpty()) {
            try {
                dob = LocalDate.parse(dobText);
            } catch (DateTimeParseException ex) {
                warn("Ngày sinh phải có dạng yyyy-MM-dd, ví dụ 2005-03-14.");
                return;
            }
        }
        User.Gender gender = switch (genderBox.getSelectedIndex()) {
            case 0 -> User.Gender.MALE;
            case 1 -> User.Gender.FEMALE;
            default -> User.Gender.OTHER;
        };

        String username = usernameField.getText();
        String fullName = fullNameField.getText();
        String address = addressField.getText();
        String email = emailField.getText();
        LocalDate finalDob = dob;
        registerButton.setEnabled(false);
        Async.run(() -> authService.register(username, password, fullName, address, finalDob, gender, email),
                user -> {
                    JOptionPane.showMessageDialog(this, "Đăng ký thành công. Hãy đăng nhập để tiếp tục.",
                            "Đăng ký", JOptionPane.INFORMATION_MESSAGE);
                    switchTo(new LoginFrame());
                },
                error -> {
                    registerButton.setEnabled(true);
                    if (error instanceof ServiceException) {
                        warn(error.getMessage());
                    } else {
                        Async.showError(error);
                    }
                });
    }

    private void warn(String message) {
        JOptionPane.showMessageDialog(this, message, "Không thể đăng ký", JOptionPane.WARNING_MESSAGE);
    }

    // Getter để gắn sự kiện ở Phiên bản 2
    public JTextField getUsernameField() { return usernameField; }
    public JTextField getFullNameField() { return fullNameField; }
    public JTextField getAddressField() { return addressField; }
    public JTextField getDobField() { return dobField; }
    public JComboBox<String> getGenderBox() { return genderBox; }
    public JTextField getEmailField() { return emailField; }
    public JPasswordField getPasswordField() { return passwordField; }
    public JPasswordField getConfirmField() { return confirmField; }
    public JButton getRegisterButton() { return registerButton; }
}
