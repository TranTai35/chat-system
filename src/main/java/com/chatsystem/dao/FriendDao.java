package com.chatsystem.dao;

import com.chatsystem.common.db.Db;
import com.chatsystem.model.FriendRequest;
import com.chatsystem.model.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

/**
 * Bạn bè, lời mời kết bạn, chặn: bảng {@code friendships}, {@code friend_requests}, {@code blocks}.
 * Chỉ chứa SQL (câu đã chạy thử nằm trong {@code docs/notes/friends-chat-queries.md}).
 * Quy tắc (không tự kết bạn, không gửi lời mời khi bị chặn...) nằm ở {@code FriendService}.
 *
 * <p>Bạn bè lưu 2 chiều: A là bạn B thì có cả dòng (A, B) và (B, A). Các hàm có tham số
 * {@link Connection} để service gom nhiều lệnh vào một transaction.
 */
public class FriendDao {

    /** Cột người dùng cần cho các màn bạn bè. Trạng thái online thật tính bằng hàm chung ở UserDao. */
    private static final String USER_COLUMNS = "u.id, u.username, u.full_name, u.is_online";

    // ------------------------------------------------------------------ đọc

    /**
     * Bạn bè của một người, xếp theo họ tên. {@code keyword} rỗng thì lấy tất cả,
     * có thì lọc theo tên đăng nhập hoặc họ tên (chứa chuỗi, không phân biệt hoa thường và dấu).
     */
    public List<User> findFriends(int userId, String keyword) {
        String kw = keyword == null ? "" : keyword.trim();
        return Db.query(
                "SELECT " + USER_COLUMNS + " FROM friendships f JOIN users u ON u.id = f.friend_id "
                        + "WHERE f.user_id = ? AND (? = '' OR u.username LIKE ? OR u.full_name LIKE ?) "
                        + "ORDER BY u.full_name, u.username",
                ps -> {
                    ps.setInt(1, userId);
                    ps.setString(2, kw);
                    ps.setString(3, "%" + kw + "%");
                    ps.setString(4, "%" + kw + "%");
                },
                FriendDao::mapUser);
    }

    /** Lời mời đang chờ gửi tới {@code receiverId}, mới nhất trước. */
    public List<FriendRequest> findIncomingRequests(int receiverId) {
        return Db.query(
                "SELECT " + USER_COLUMNS + ", r.receiver_id, r.created_at "
                        + "FROM friend_requests r JOIN users u ON u.id = r.sender_id "
                        + "WHERE r.receiver_id = ? ORDER BY r.created_at DESC",
                ps -> ps.setInt(1, receiverId),
                rs -> {
                    FriendRequest r = new FriendRequest();
                    r.setSender(mapUser(rs));
                    r.setReceiverId(rs.getInt("receiver_id"));
                    Timestamp t = rs.getTimestamp("created_at");
                    r.setCreatedAt(t == null ? null : t.toLocalDateTime());
                    return r;
                });
    }

    public boolean areFriends(int userId, int otherId) {
        return Db.queryOne("SELECT 1 FROM friendships WHERE user_id = ? AND friend_id = ?",
                ps -> {
                    ps.setInt(1, userId);
                    ps.setInt(2, otherId);
                }, rs -> 1).isPresent();
    }

    /** Có ai chặn ai giữa 2 người này không (chiều nào cũng tính). */
    public boolean isBlockedEitherWay(int a, int b) {
        return Db.queryOne(
                "SELECT 1 FROM blocks WHERE (blocker_id = ? AND blocked_id = ?) OR (blocker_id = ? AND blocked_id = ?)",
                ps -> bindPair(ps, a, b), rs -> 1).isPresent();
    }

    /** {@code senderId} đã gửi lời mời cho {@code receiverId} và lời mời còn đang chờ. */
    public boolean hasPendingRequest(int senderId, int receiverId) {
        return Db.queryOne("SELECT 1 FROM friend_requests WHERE sender_id = ? AND receiver_id = ?",
                ps -> {
                    ps.setInt(1, senderId);
                    ps.setInt(2, receiverId);
                }, rs -> 1).isPresent();
    }

    // ------------------------------------------------------------------ ghi

    /** Thêm lời mời. Trùng (đã gửi rồi) thì ném {@code DbException} có {@code isDuplicateKey()}. */
    public void insertRequest(int senderId, int receiverId) {
        Db.update("INSERT INTO friend_requests (sender_id, receiver_id) VALUES (?, ?)", ps -> {
            ps.setInt(1, senderId);
            ps.setInt(2, receiverId);
        });
    }

    /** Xoá 1 lời mời (đồng ý hoặc từ chối). Trả về số dòng đã xoá: 0 nghĩa là lời mời không còn. */
    public int deleteRequest(Connection c, int senderId, int receiverId) throws SQLException {
        return Db.update(c, "DELETE FROM friend_requests WHERE sender_id = ? AND receiver_id = ?", ps -> {
            ps.setInt(1, senderId);
            ps.setInt(2, receiverId);
        });
    }

    /** Xoá mọi lời mời giữa 2 người, chiều nào cũng xoá (dùng khi chặn). */
    public int deleteRequestsBetween(Connection c, int a, int b) throws SQLException {
        return Db.update(c,
                "DELETE FROM friend_requests WHERE (sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?)",
                ps -> bindPair(ps, a, b));
    }

    /** Kết bạn: thêm 2 dòng (a, b) và (b, a). */
    public void insertFriendship(Connection c, int a, int b) throws SQLException {
        Db.update(c, "INSERT INTO friendships (user_id, friend_id) VALUES (?, ?), (?, ?)", ps -> {
            ps.setInt(1, a);
            ps.setInt(2, b);
            ps.setInt(3, b);
            ps.setInt(4, a);
        });
    }

    /** Huỷ kết bạn: xoá cả 2 chiều. Trả về số dòng đã xoá (2 nếu đang là bạn, 0 nếu không). */
    public int deleteFriendship(Connection c, int a, int b) throws SQLException {
        return Db.update(c,
                "DELETE FROM friendships WHERE (user_id = ? AND friend_id = ?) OR (user_id = ? AND friend_id = ?)",
                ps -> bindPair(ps, a, b));
    }

    /** {@code blockerId} chặn {@code blockedId}. Đã chặn rồi thì bỏ qua. */
    public void insertBlock(Connection c, int blockerId, int blockedId) throws SQLException {
        Db.update(c, "INSERT IGNORE INTO blocks (blocker_id, blocked_id) VALUES (?, ?)", ps -> {
            ps.setInt(1, blockerId);
            ps.setInt(2, blockedId);
        });
    }

    // ------------------------------------------------------------------ nội bộ

    /** Gắn (a, b, b, a) cho các câu "giữa 2 người, chiều nào cũng tính". */
    private static void bindPair(java.sql.PreparedStatement ps, int a, int b) throws SQLException {
        ps.setInt(1, a);
        ps.setInt(2, b);
        ps.setInt(3, b);
        ps.setInt(4, a);
    }

    private static User mapUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setUsername(rs.getString("username"));
        u.setFullName(rs.getString("full_name"));
        u.setOnline(rs.getBoolean("is_online"));
        return u;
    }
}
