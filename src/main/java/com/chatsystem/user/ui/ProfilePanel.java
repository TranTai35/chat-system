package com.chatsystem.user.ui;

import com.chatsystem.common.Session;
import com.chatsystem.common.ui.Async;
import com.chatsystem.common.ui.FormPanel;
import com.chatsystem.common.ui.Theme;
import com.chatsystem.common.ui.UiUtil;
import com.chatsystem.model.User;
import com.chatsystem.service.ServiceException;
import com.chatsystem.service.UserService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * User 4. Thông tin tài khoản (wireframe user/07-profile).
 * Tên đăng nhập không sửa được; các trường còn lại chỉnh rồi bấm "Lưu".
 * Điền từ người đang đăng nhập ({@link Session}); "Lưu" ghi vào CSDL qua {@link UserService}.
 */
public class ProfilePanel extends JPanel {

    private static final int FORM_WIDTH = 520;

    private final UserService userService = new UserService();

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
        loadFromSession();
        saveButton.addActionListener(e -> save());

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

    /** Điền các ô từ người đang đăng nhập. Chưa đăng nhập (ví dụ chạy xem thử riêng màn này) thì để trống. */
    private void loadFromSession() {
        User u = Session.currentUser();
        if (u == null) {
            saveButton.setEnabled(false);
            return;
        }
        usernameField.setText(u.getUsername());
        fullNameField.setText(u.getFullName());
        addressField.setText(u.getAddress() == null ? "" : u.getAddress());
        dobField.setText(u.getDob() == null ? "" : u.getDob().toString());
        genderBox.setSelectedIndex(u.getGender() == User.Gender.FEMALE ? 1
                : u.getGender() == User.Gender.OTHER ? 2 : 0);
        emailField.setText(u.getEmail());
    }

    private void save() {
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
        int userId = Session.currentUserId();
        String fullName = fullNameField.getText();
        String address = addressField.getText();
        String email = emailField.getText();
        LocalDate finalDob = dob;

        saveButton.setEnabled(false);
        Async.run(() -> userService.updateProfile(userId, fullName, address, finalDob, gender, email),
                updated -> {
                    saveButton.setEnabled(true);
                    Session.start(updated);   // các màn khác thấy thông tin mới
                    loadFromSession();
                    JOptionPane.showMessageDialog(this, "Đã lưu thông tin tài khoản.",
                            "Tài khoản", JOptionPane.INFORMATION_MESSAGE);
                },
                error -> {
                    saveButton.setEnabled(true);
                    if (error instanceof ServiceException) {
                        warn(error.getMessage());
                    } else {
                        Async.showError(error);
                    }
                });
    }

    private void warn(String message) {
        JOptionPane.showMessageDialog(this, message, "Không thể lưu", JOptionPane.WARNING_MESSAGE);
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
