package com.chatsystem.service;

import com.chatsystem.common.PasswordUtil;
import com.chatsystem.common.db.Db;
import com.chatsystem.common.db.DbException;
import com.chatsystem.dao.LoginDao;
import com.chatsystem.dao.UserDao;
import com.chatsystem.model.User;

import java.time.LocalDate;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Service mẫu: đăng ký, đăng nhập, đổi mật khẩu. Khang và Nhân viết service của mình theo khuôn này:
 * <ul>
 *   <li>Màn hình chỉ gọi service, <b>không gọi DAO trực tiếp</b>. Service gọi DAO.</li>
 *   <li>Mọi <b>quy tắc của đề</b> nằm ở service (kiểm tra dữ liệu, quyền, điều kiện), không nằm trong
 *       màn hình và không nằm trong DAO. DAO chỉ đọc ghi dữ liệu.</li>
 *   <li>Vi phạm quy tắc thì ném {@link ServiceException} với thông báo tiếng Việt; màn hình bắt và hiện ra.</li>
 *   <li>Không có mã giao diện trong service (không JOptionPane, không Swing).</li>
 *   <li>Nhận DAO qua constructor để dễ thay bản giả khi thử, constructor không tham số dùng DAO thật.</li>
 *   <li>Việc cần nhiều bước phải thành công cùng nhau thì gói vào {@code Db.transaction(...)}.</li>
 * </ul>
 * Gọi từ màn hình, trong luồng nền:
 * <pre>{@code
 * Async.run(() -> authService.login(user, pass, User.Role.USER), u -> openMainFrame(u));
 * }</pre>
 * {@code login} (phân hệ người dùng) còn ghi {@code login_history}, mở {@code app_sessions} và bật
 * {@code is_online}; {@code logout} làm ngược lại.
 */
public class AuthService {

    public static final int MIN_PASSWORD_LENGTH = 6;

    private static final Pattern USERNAME = Pattern.compile("[A-Za-z0-9_.]{3,50}");
    private static final Pattern EMAIL = Pattern.compile("[^@\\s]+@[^@\\s]+\\.[^@\\s]+");

    private final UserDao userDao;
    private final LoginDao loginDao;

    public AuthService() {
        this(new UserDao(), new LoginDao());
    }

    public AuthService(UserDao userDao) {
        this(userDao, new LoginDao());
    }

    public AuthService(UserDao userDao, LoginDao loginDao) {
        this.userDao = userDao;
        this.loginDao = loginDao;
    }

    /**
     * Đăng ký tài khoản người dùng thường.
     *
     * @return tài khoản vừa tạo (đã có id)
     * @throws ServiceException dữ liệu không hợp lệ, hoặc trùng tên đăng nhập / email
     */
    public User register(String username, String rawPassword, String fullName, String address,
                         LocalDate dob, User.Gender gender, String email) {
        username = trim(username);
        fullName = trim(fullName);
        email = trim(email);

        if (!USERNAME.matcher(username).matches()) {
            throw new ServiceException("Tên đăng nhập gồm 3–50 ký tự: chữ không dấu, số, dấu chấm hoặc gạch dưới.");
        }
        checkNewPassword(rawPassword);
        if (fullName.isEmpty()) {
            throw new ServiceException("Vui lòng nhập họ tên.");
        }
        if (!EMAIL.matcher(email).matches()) {
            throw new ServiceException("Email không hợp lệ.");
        }
        if (fullName.length() > 100 || email.length() > 100 || trim(address).length() > 255) {
            throw new ServiceException("Họ tên và email tối đa 100 ký tự, địa chỉ tối đa 255 ký tự.");
        }
        if (dob != null && dob.isAfter(LocalDate.now())) {
            throw new ServiceException("Ngày sinh không được ở tương lai.");
        }
        if (userDao.existsByUsername(username)) {
            throw new ServiceException("Tên đăng nhập đã có người dùng.");
        }
        if (userDao.existsByEmail(email)) {
            throw new ServiceException("Email đã được dùng cho tài khoản khác.");
        }

        User u = new User();
        u.setUsername(username);
        u.setPasswordHash(PasswordUtil.hash(rawPassword));
        u.setFullName(fullName);
        u.setAddress(trim(address).isEmpty() ? null : trim(address));
        u.setDob(dob);
        u.setGender(gender);
        u.setEmail(email);
        u.setRole(User.Role.USER);
        try {
            u.setId(userDao.insert(u));
        } catch (DbException e) {
            // Hai người đăng ký trùng tên cùng lúc: bước kiểm tra ở trên lọt qua, UNIQUE của CSDL chặn lại.
            if (e.isDuplicateKey()) {
                throw new ServiceException("Tên đăng nhập hoặc email đã có người dùng.", e);
            }
            throw e;
        }
        return u;
    }

    /**
     * Kiểm tra đăng nhập.
     *
     * @param requiredRole {@code USER} khi gọi từ phân hệ người dùng, {@code ADMIN} từ phân hệ quản trị
     * @throws ServiceException sai thông tin, tài khoản bị khoá hoặc không đúng phân hệ
     */
    public User login(String username, String rawPassword, User.Role requiredRole) {
        Optional<User> found = userDao.findByUsername(trim(username));
        // Sai tên hay sai mật khẩu đều báo chung một câu, để người lạ không dò được tên nào tồn tại.
        User u = found.filter(x -> PasswordUtil.matches(rawPassword == null ? "" : rawPassword, x.getPasswordHash()))
                .orElseThrow(() -> new ServiceException("Tên đăng nhập hoặc mật khẩu không đúng."));
        if (u.isLocked()) {
            throw new ServiceException("Tài khoản đã bị khoá. Vui lòng liên hệ quản trị viên.");
        }
        if (u.getRole() != requiredRole) {
            throw new ServiceException(requiredRole == User.Role.ADMIN
                    ? "Tài khoản này không có quyền quản trị."
                    : "Tài khoản quản trị chỉ đăng nhập ở phân hệ quản trị.");
        }
        if (requiredRole == User.Role.USER) {
            recordLogin(u.getId());
        }
        return u;
    }

    /** Ghi lịch sử đăng nhập, mở lượt dùng ứng dụng và đặt trạng thái online, cùng một giao dịch. */
    private void recordLogin(int userId) {
        Db.transaction(c -> {
            loginDao.closeOpenSessions(c, userId);   // lần trước tắt đột ngột thì đóng nốt
            loginDao.insertLoginHistory(c, userId);
            loginDao.openSession(c, userId);
            loginDao.setOnline(c, userId, true);
            return null;
        });
    }

    /** Đăng xuất hoặc đóng ứng dụng: đóng lượt dùng ứng dụng và đặt offline. */
    public void logout(int userId) {
        Db.transaction(c -> {
            loginDao.closeOpenSessions(c, userId);
            loginDao.setOnline(c, userId, false);
            return null;
        });
    }

    /**
     * Người dùng tự đổi mật khẩu: phải nhập đúng mật khẩu cũ.
     *
     * @throws ServiceException mật khẩu cũ sai hoặc mật khẩu mới không hợp lệ
     */
    public void changePassword(int userId, String oldRaw, String newRaw) {
        User u = userDao.findById(userId)
                .orElseThrow(() -> new ServiceException("Không tìm thấy tài khoản."));
        if (!PasswordUtil.matches(oldRaw == null ? "" : oldRaw, u.getPasswordHash())) {
            throw new ServiceException("Mật khẩu cũ không đúng.");
        }
        checkNewPassword(newRaw);
        if (PasswordUtil.matches(newRaw, u.getPasswordHash())) {
            throw new ServiceException("Mật khẩu mới phải khác mật khẩu cũ.");
        }
        userDao.updatePassword(userId, PasswordUtil.hash(newRaw));
    }

    private static void checkNewPassword(String rawPassword) {
        if (rawPassword == null || rawPassword.length() < MIN_PASSWORD_LENGTH) {
            throw new ServiceException("Mật khẩu phải có ít nhất " + MIN_PASSWORD_LENGTH + " ký tự.");
        }
    }

    private static String trim(String s) {
        return s == null ? "" : s.trim();
    }
}
