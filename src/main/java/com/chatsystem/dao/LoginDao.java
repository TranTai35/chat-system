package com.chatsystem.dao;

import com.chatsystem.common.db.Db;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Ghi nhận đăng nhập: bảng {@code login_history}, {@code app_sessions} và cờ {@code users.is_online}.
 * Chỉ chứa SQL. Các hàm có tham số {@link Connection} để service gom vào một giao dịch.
 */
public class LoginDao {

    /** Thêm một dòng lịch sử đăng nhập. */
    public void insertLoginHistory(Connection c, int userId) throws SQLException {
        Db.insert(c, "INSERT INTO login_history (user_id) VALUES (?)", ps -> ps.setInt(1, userId));
    }

    /** Đóng các lượt mở ứng dụng còn treo của người này (lần trước tắt đột ngột, chưa kịp ghi closed_at). */
    public int closeOpenSessions(Connection c, int userId) throws SQLException {
        return Db.update(c,
                "UPDATE app_sessions SET closed_at = GREATEST(opened_at, NOW()) "
                        + "WHERE user_id = ? AND closed_at IS NULL",
                ps -> ps.setInt(1, userId));
    }

    /** Mở một lượt dùng ứng dụng mới. */
    public void openSession(Connection c, int userId) throws SQLException {
        Db.insert(c, "INSERT INTO app_sessions (user_id) VALUES (?)", ps -> ps.setInt(1, userId));
    }

    public void setOnline(Connection c, int userId, boolean online) throws SQLException {
        Db.update(c, "UPDATE users SET is_online = ? WHERE id = ?", ps -> {
            ps.setBoolean(1, online);
            ps.setInt(2, userId);
        });
    }
}
