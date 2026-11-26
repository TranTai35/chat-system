package com.chatsystem.dao;

import com.chatsystem.common.db.Db;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Ghi nhận đăng nhập: bảng {@code login_history}, {@code app_sessions}, cờ {@code users.is_online}
 * và tín hiệu "còn sống" {@code users.last_seen}.
 * Chỉ chứa SQL. Các hàm có tham số {@link Connection} để service gom vào một giao dịch.
 */
public class LoginDao {

    /** Thêm một dòng lịch sử đăng nhập. */
    public void insertLoginHistory(Connection c, int userId) throws SQLException {
        Db.insert(c, "INSERT INTO login_history (user_id) VALUES (?)", ps -> ps.setInt(1, userId));
    }

    /**
     * Lúc đăng nhập: đóng các lượt mở ứng dụng còn treo (lần trước tắt ngang, chưa kịp ghi closed_at)
     * tại tín hiệu cuối cùng {@code last_seen}, không phải giờ hiện tại, để thống kê hoạt động không bị kéo dài.
     * Phải gọi <b>trước</b> khi cập nhật last_seen mới.
     */
    public int closeStaleSessions(Connection c, int userId) throws SQLException {
        return Db.update(c,
                "UPDATE app_sessions s JOIN users u ON u.id = s.user_id "
                        + "SET s.closed_at = GREATEST(s.opened_at, COALESCE(u.last_seen, s.opened_at)) "
                        + "WHERE s.user_id = ? AND s.closed_at IS NULL",
                ps -> ps.setInt(1, userId));
    }

    /** Lúc đăng xuất / đóng ứng dụng: đóng các lượt đang mở tại giờ hiện tại. */
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

    /** Đặt cờ online và ghi tín hiệu "còn sống" = bây giờ (cả khi đăng nhập lẫn đăng xuất). */
    public void setOnline(Connection c, int userId, boolean online) throws SQLException {
        Db.update(c, "UPDATE users SET is_online = ?, last_seen = NOW(3) WHERE id = ?", ps -> {
            ps.setBoolean(1, online);
            ps.setInt(2, userId);
        });
    }

    /**
     * Tín hiệu "còn sống", gọi mỗi 30 giây khi ứng dụng đang mở. Chỉ cập nhật khi người này vẫn đang
     * đăng nhập (is_online = 1), để một tín hiệu đến muộn sau khi đăng xuất không làm họ online lại.
     *
     * @return false nếu không có dòng nào được cập nhật (đã đăng xuất ở nơi khác)
     */
    public boolean touchLastSeen(int userId) {
        return Db.update("UPDATE users SET last_seen = NOW(3) WHERE id = ? AND is_online = 1",
                ps -> ps.setInt(1, userId)) > 0;
    }
}
