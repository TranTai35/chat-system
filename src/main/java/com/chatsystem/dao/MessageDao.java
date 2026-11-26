package com.chatsystem.dao;

import com.chatsystem.common.db.Db;
import com.chatsystem.model.Message;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;
import java.util.Optional;

/**
 * Hội thoại và tin nhắn: bảng {@code conversations}, {@code conversation_members}, {@code messages},
 * {@code message_hidden}. Chỉ chứa SQL (câu đã chạy thử nằm trong {@code docs/notes/friends-chat-queries.md}).
 * Quy tắc (phải là thành viên mới được đọc / gửi, tin không rỗng...) nằm ở {@code ChatService}.
 */
public class MessageDao {

    private static final String COLUMNS = "m.id, m.conversation_id, m.sender_id, m.content, m.iv, m.key_version, m.sent_at";

    /** Id cuộc chat riêng theo {@code private_key} ("<id nhỏ>_<id lớn>"), rỗng nếu 2 người chưa chat lần nào. */
    public Optional<Integer> findPrivateConversationId(String privateKey) {
        return Db.queryOne("SELECT id FROM conversations WHERE type = 'PRIVATE' AND private_key = ?",
                ps -> ps.setString(1, privateKey), rs -> rs.getInt("id"));
    }

    public boolean isMember(int conversationId, int userId) {
        return Db.queryOne("SELECT 1 FROM conversation_members WHERE conversation_id = ? AND user_id = ?",
                ps -> {
                    ps.setInt(1, conversationId);
                    ps.setInt(2, userId);
                }, rs -> 1).isPresent();
    }

    /**
     * Tin của một cuộc chat mà {@code viewerId} được thấy, có {@code id > afterId}, theo thứ tự gửi.
     * {@code afterId = 0} để tải toàn bộ lịch sử; khung chat hỏi mỗi giây với id tin cuối đang có.
     * Bỏ tin người xem đã xoá phía mình ({@code message_hidden}) và tin trước mốc {@code cleared_at}.
     * Người không phải thành viên thì nhận danh sách rỗng.
     */
    public List<Message> findVisibleAfter(int conversationId, int viewerId, long afterId) {
        return Db.query(
                "SELECT " + COLUMNS + " FROM messages m "
                        + "JOIN conversation_members cm ON cm.conversation_id = m.conversation_id AND cm.user_id = ? "
                        + "WHERE m.conversation_id = ? AND m.id > ? "
                        + "AND (cm.cleared_at IS NULL OR m.sent_at > cm.cleared_at) "
                        + "AND NOT EXISTS (SELECT 1 FROM message_hidden h WHERE h.user_id = ? AND h.message_id = m.id) "
                        + "ORDER BY m.id",
                ps -> {
                    ps.setInt(1, viewerId);
                    ps.setInt(2, conversationId);
                    ps.setLong(3, afterId);
                    ps.setInt(4, viewerId);
                },
                MessageDao::map);
    }

    /** Thêm 1 tin và trả về id. Tin thường thì {@code iv}, {@code keyVersion} để {@code null}. */
    public long insert(Message m) {
        return Db.insert(
                "INSERT INTO messages (conversation_id, sender_id, content, iv, key_version) VALUES (?, ?, ?, ?, ?)",
                ps -> {
                    ps.setInt(1, m.getConversationId());
                    ps.setInt(2, m.getSenderId());
                    ps.setString(3, m.getContent());
                    ps.setString(4, m.getIv());
                    if (m.getKeyVersion() == null) {
                        ps.setNull(5, Types.INTEGER);
                    } else {
                        ps.setInt(5, m.getKeyVersion());
                    }
                });
    }

    private static Message map(ResultSet rs) throws SQLException {
        Message m = new Message();
        m.setId(rs.getLong("id"));
        m.setConversationId(rs.getInt("conversation_id"));
        int sender = rs.getInt("sender_id");
        m.setSenderId(rs.wasNull() ? null : sender);
        m.setContent(rs.getString("content"));
        m.setIv(rs.getString("iv"));
        int keyVersion = rs.getInt("key_version");
        m.setKeyVersion(rs.wasNull() ? null : keyVersion);
        Timestamp sent = rs.getTimestamp("sent_at");
        m.setSentAt(sent == null ? null : sent.toLocalDateTime());
        return m;
    }
}
