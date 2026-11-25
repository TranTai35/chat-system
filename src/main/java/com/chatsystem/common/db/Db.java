package com.chatsystem.common.db;

import com.chatsystem.common.config.AppConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Cửa duy nhất để nói chuyện với MySQL. Mọi DAO đều đi qua lớp này, không ai tự mở kết nối riêng.
 *
 * <p>Cách dùng trong DAO (luôn dùng dấu ? và {@code Binder}, không cộng chuỗi SQL):
 * <pre>{@code
 * List<User> list = Db.query(
 *         "SELECT * FROM users WHERE full_name LIKE ?",
 *         ps -> ps.setString(1, "%" + keyword + "%"),
 *         rs -> mapUser(rs));
 * }</pre>
 *
 * <p>Mỗi lệnh mở một kết nối rồi đóng ngay. Với nhu cầu của đồ án (vài người dùng, khung chat
 * hỏi 1 giây một lần) là đủ. Nếu sau này cần connection pool thì chỉ sửa {@link #getConnection()},
 * các DAO không phải đổi.
 *
 * <p><b>Gọi từ giao diện:</b> truy vấn có thể mất vài chục mili giây, hãy chạy trong
 * {@code Async.run(...)} để cửa sổ không bị đứng.
 */
public final class Db {

    /** Gắn giá trị vào các dấu ? của câu SQL. */
    @FunctionalInterface
    public interface Binder {
        void bind(PreparedStatement ps) throws SQLException;
    }

    /** Đọc một dòng kết quả thành một đối tượng. Không gọi {@code rs.next()} trong này. */
    @FunctionalInterface
    public interface RowMapper<T> {
        T map(ResultSet rs) throws SQLException;
    }

    /** Một nhóm lệnh chạy trong cùng một transaction. */
    @FunctionalInterface
    public interface TxWork<T> {
        T run(Connection connection) throws SQLException;
    }

    private Db() {
    }

    // ------------------------------------------------------------------ kết nối

    /** URL kết nối, lấy từ db.host / db.port / db.name trong file cấu hình. */
    public static String url() {
        String host = AppConfig.get("db.host", "localhost");
        int port = AppConfig.getInt("db.port", 3306);
        String name = AppConfig.get("db.name", "chat_system");
        return "jdbc:mysql://" + host + ":" + port + "/" + name
                + "?useUnicode=true&characterEncoding=UTF-8&useSSL=false&allowPublicKeyRetrieval=true";
    }

    /** Mở một kết nối mới. Người gọi phải đóng (dùng try-with-resources). */
    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new DbException("Thiếu thư viện kết nối MySQL (mysql-connector-j).", e);
        }
        String user = AppConfig.get("db.user", "root");
        String password = AppConfig.get("db.password", "");
        try {
            return DriverManager.getConnection(url(), user, password);
        } catch (SQLException e) {
            throw new DbException("Không kết nối được CSDL (" + url() + "): " + e.getMessage()
                    + ". Hãy kiểm tra MySQL đang chạy và thông tin trong " + AppConfig.FILE_NAME
                    + ". " + AppConfig.describeSource(), e);
        }
    }

    /** Thử kết nối. Ném {@link DbException} nếu không được. */
    @SuppressWarnings("try")
    public static void ping() {
        try (Connection ignored = getConnection()) {
            // mở được là đủ
        } catch (SQLException e) {
            throw wrap(e);
        }
    }

    // ------------------------------------------------------------------ truy vấn

    public static <T> List<T> query(String sql, Binder binder, RowMapper<T> mapper) {
        try (Connection c = getConnection()) {
            return query(c, sql, binder, mapper);
        } catch (SQLException e) {
            throw wrap(e);
        }
    }

    /** Bản dùng chung kết nối, để chạy trong {@link #transaction}. */
    public static <T> List<T> query(Connection c, String sql, Binder binder, RowMapper<T> mapper)
            throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            if (binder != null) {
                binder.bind(ps);
            }
            try (ResultSet rs = ps.executeQuery()) {
                List<T> out = new ArrayList<>();
                while (rs.next()) {
                    out.add(mapper.map(rs));
                }
                return out;
            }
        }
    }

    /** Dòng đầu tiên của kết quả, hoặc rỗng nếu không có dòng nào. */
    public static <T> Optional<T> queryOne(String sql, Binder binder, RowMapper<T> mapper) {
        List<T> rows = query(sql, binder, mapper);
        return rows.isEmpty() ? Optional.empty() : Optional.of(rows.get(0));
    }

    // ------------------------------------------------------------------ thay đổi dữ liệu

    /** INSERT / UPDATE / DELETE. Trả về số dòng bị ảnh hưởng. */
    public static int update(String sql, Binder binder) {
        try (Connection c = getConnection()) {
            return update(c, sql, binder);
        } catch (SQLException e) {
            throw wrap(e);
        }
    }

    public static int update(Connection c, String sql, Binder binder) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            if (binder != null) {
                binder.bind(ps);
            }
            return ps.executeUpdate();
        }
    }

    /** INSERT và trả về khoá tự tăng (id) của dòng vừa thêm. */
    public static long insert(String sql, Binder binder) {
        try (Connection c = getConnection()) {
            return insert(c, sql, binder);
        } catch (SQLException e) {
            throw wrap(e);
        }
    }

    public static long insert(Connection c, String sql, Binder binder) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (binder != null) {
                binder.bind(ps);
            }
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
                throw new SQLException("Câu INSERT không trả về khoá tự tăng");
            }
        }
    }

    /**
     * Chạy nhiều lệnh trong 1 transaction: xong hết thì commit, lỗi thì rollback tất cả.
     * Dùng cho thao tác phải đi cùng nhau, ví dụ kết bạn (thêm 2 dòng friendships + xoá lời mời).
     */
    public static <T> T transaction(TxWork<T> work) {
        try (Connection c = getConnection()) {
            c.setAutoCommit(false);
            try {
                T result = work.run(c);
                c.commit();
                return result;
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw wrap(e);
        }
    }

    // ------------------------------------------------------------------ nội bộ

    private static DbException wrap(SQLException e) {
        return new DbException("Lỗi CSDL: " + e.getMessage(), e);
    }
}
