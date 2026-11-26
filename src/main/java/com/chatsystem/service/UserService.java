package com.chatsystem.service;

import com.chatsystem.common.db.DbException;
import com.chatsystem.dao.UserDao;
import com.chatsystem.model.User;

import java.time.LocalDate;
import java.util.regex.Pattern;

/**
 * Thông tin cá nhân của người dùng. Đổi mật khẩu nằm ở {@link AuthService#changePassword}.
 */
public class UserService {

    private static final Pattern EMAIL = Pattern.compile("[^@\\s]+@[^@\\s]+\\.[^@\\s]+");

    private final UserDao userDao;

    public UserService() {
        this(new UserDao());
    }

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    /** Đọc lại thông tin mới nhất của một tài khoản. */
    public User getById(int userId) {
        return userDao.findById(userId)
                .orElseThrow(() -> new ServiceException("Không tìm thấy tài khoản."));
    }

    /**
     * Người dùng tự cập nhật thông tin cá nhân (không đổi được tên đăng nhập).
     *
     * @return tài khoản sau khi cập nhật (đọc lại từ CSDL)
     * @throws ServiceException dữ liệu không hợp lệ hoặc email đã có người khác dùng
     */
    public User updateProfile(int userId, String fullName, String address, LocalDate dob,
                              User.Gender gender, String email) {
        fullName = trim(fullName);
        address = trim(address);
        email = trim(email);

        if (fullName.isEmpty()) {
            throw new ServiceException("Vui lòng nhập họ tên.");
        }
        if (!EMAIL.matcher(email).matches()) {
            throw new ServiceException("Email không hợp lệ.");
        }
        if (fullName.length() > 100 || email.length() > 100 || address.length() > 255) {
            throw new ServiceException("Họ tên và email tối đa 100 ký tự, địa chỉ tối đa 255 ký tự.");
        }
        if (dob != null && dob.isAfter(LocalDate.now())) {
            throw new ServiceException("Ngày sinh không được ở tương lai.");
        }
        if (userDao.existsByEmailOtherThan(email, userId)) {
            throw new ServiceException("Email đã được dùng cho tài khoản khác.");
        }

        User u = new User();
        u.setId(userId);
        u.setFullName(fullName);
        u.setAddress(address.isEmpty() ? null : address);
        u.setDob(dob);
        u.setGender(gender);
        u.setEmail(email);
        try {
            if (!userDao.updateProfile(u)) {
                throw new ServiceException("Không tìm thấy tài khoản.");
            }
        } catch (DbException e) {
            if (e.isDuplicateKey()) {
                throw new ServiceException("Email đã được dùng cho tài khoản khác.", e);
            }
            throw e;
        }
        return getById(userId);
    }

    private static String trim(String s) {
        return s == null ? "" : s.trim();
    }
}
