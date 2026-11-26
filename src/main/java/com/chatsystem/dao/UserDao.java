package com.chatsystem.dao;

import com.chatsystem.common.db.Db;
import com.chatsystem.model.User;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

/**
 * DAO mẫu cho bảng {@code users}. Khang và Nhân viết DAO của mình theo đúng khuôn này:
 * <ul>
 *   <li>một lớp {@code XxxDao} không giữ trạng thái, đặt trong package {@code dao};</li>
 *   <li>mọi truy vấn đi qua {@link Db}, dùng dấu ? và {@code Binder}, không cộng chuỗi SQL;</li>
 *   <li>một hàm {@code map(ResultSet)} đổi một dòng kết quả thành đối tượng model;</li>
 *   <li>không có mã giao diện trong DAO (không JOptionPane, không Swing);</li>
 *   <li>không quyết định quy tắc của đề (ai được làm gì, dữ liệu hợp lệ hay không): việc đó ở lớp {@code service}.</li>
 * </ul>
 * Màn hình không gọi DAO trực tiếp: màn hình gọi service, service gọi DAO (xem {@code service/AuthService}).
 */
public class UserDao {

    /** Danh sách cột dùng chung cho mọi câu SELECT, để {@link #map} luôn khớp. */
    private static final String COLUMNS =
            "id, username, password_hash, full_name, address, dob, gender, email, role, "
                    + "is_locked, is_online, public_key, private_key_enc, created_at";

    public Optional<User> findById(int id) {
        return Db.queryOne("SELECT " + COLUMNS + " FROM users WHERE id = ?",
                ps -> ps.setInt(1, id), UserDao::map);
    }

    public Optional<User> findByUsername(String username) {
        return Db.queryOne("SELECT " + COLUMNS + " FROM users WHERE username = ?",
                ps -> ps.setString(1, username), UserDao::map);
    }

    public boolean existsByUsername(String username) {
        return Db.queryOne("SELECT 1 FROM users WHERE username = ?",
                ps -> ps.setString(1, username), rs -> 1).isPresent();
    }

    public boolean existsByEmail(String email) {
        return Db.queryOne("SELECT 1 FROM users WHERE email = ?",
                ps -> ps.setString(1, email), rs -> 1).isPresent();
    }

    /** Có tài khoản <b>khác</b> {@code userId} đang dùng email này không (dùng khi người dùng đổi email). */
    public boolean existsByEmailOtherThan(String email, int userId) {
        return Db.queryOne("SELECT 1 FROM users WHERE email = ? AND id <> ?",
                ps -> {
                    ps.setString(1, email);
                    ps.setInt(2, userId);
                }, rs -> 1).isPresent();
    }

    /** Tất cả tài khoản, mới đăng ký xếp trước. */
    public List<User> findAll() {
        return Db.query("SELECT " + COLUMNS + " FROM users ORDER BY created_at DESC, id DESC",
                null, UserDao::map);
    }

    /**
     * Thêm tài khoản mới và trả về id. {@code passwordHash} phải là kết quả của
     * {@code PasswordUtil.hash(...)}. Trùng tên đăng nhập hoặc email thì ném
     * {@code DbException} có {@code isDuplicateKey() == true}.
     */
    public int insert(User u) {
        long id = Db.insert(
                "INSERT INTO users (username, password_hash, full_name, address, dob, gender, email, role) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                ps -> {
                    ps.setString(1, u.getUsername());
                    ps.setString(2, u.getPasswordHash());
                    ps.setString(3, u.getFullName());
                    ps.setString(4, u.getAddress());
                    ps.setDate(5, u.getDob() == null ? null : Date.valueOf(u.getDob()));
                    ps.setString(6, u.getGender() == null ? null : u.getGender().name());
                    ps.setString(7, u.getEmail());
                    ps.setString(8, u.getRole().name());
                });
        return (int) id;
    }

    /**
     * Cập nhật thông tin cá nhân: họ tên, địa chỉ, ngày sinh, giới tính, email.
     * Không đụng tên đăng nhập, mật khẩu, quyền. Trùng email thì ném {@code DbException}
     * có {@code isDuplicateKey() == true}.
     */
    public boolean updateProfile(User u) {
        return Db.update("UPDATE users SET full_name = ?, address = ?, dob = ?, gender = ?, email = ? WHERE id = ?",
                ps -> {
                    ps.setString(1, u.getFullName());
                    ps.setString(2, u.getAddress());
                    ps.setDate(3, u.getDob() == null ? null : Date.valueOf(u.getDob()));
                    ps.setString(4, u.getGender() == null ? null : u.getGender().name());
                    ps.setString(5, u.getEmail());
                    ps.setInt(6, u.getId());
                }) > 0;
    }

    /** Đổi mật khẩu (đã băm). Trả về true nếu có tài khoản được cập nhật. */
    public boolean updatePassword(int userId, String newPasswordHash) {
        return Db.update("UPDATE users SET password_hash = ? WHERE id = ?", ps -> {
            ps.setString(1, newPasswordHash);
            ps.setInt(2, userId);
        }) > 0;
    }

    // ------------------------------------------------------------------ ánh xạ

    private static User map(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setUsername(rs.getString("username"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setFullName(rs.getString("full_name"));
        u.setAddress(rs.getString("address"));
        Date dob = rs.getDate("dob");
        u.setDob(dob == null ? null : dob.toLocalDate());
        String gender = rs.getString("gender");
        u.setGender(gender == null ? null : User.Gender.valueOf(gender));
        u.setEmail(rs.getString("email"));
        u.setRole(User.Role.valueOf(rs.getString("role")));
        u.setLocked(rs.getBoolean("is_locked"));
        u.setOnline(rs.getBoolean("is_online"));
        u.setPublicKey(rs.getString("public_key"));
        u.setPrivateKeyEnc(rs.getString("private_key_enc"));
        Timestamp created = rs.getTimestamp("created_at");
        u.setCreatedAt(created == null ? null : created.toLocalDateTime());
        return u;
    }
}
