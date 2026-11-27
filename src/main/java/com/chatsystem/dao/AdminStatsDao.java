package com.chatsystem.dao;

import com.chatsystem.common.db.Db;
import com.chatsystem.model.GroupMember;
import com.chatsystem.model.GroupSummary;
import com.chatsystem.model.LoginRecord;
import com.chatsystem.model.SpamReport;
import com.chatsystem.model.UserStats;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Truy vấn cho các màn thống kê của admin (A2–A9), theo khuôn {@link UserDao}.
 * Câu SQL đã chạy thử trên sample_data.sql: xem docs/notes/admin-queries.md.
 *
 * <p>Quy ước khoảng ngày [from, to] (cả hai có thể {@code null} = không giới hạn):
 * {@code cột >= from 00:00} và {@code cột < to + 1 ngày}, để không mất dữ liệu trong ngày cuối.
 * Từ khoá tìm kiếm {@code null}/rỗng = không lọc; collation ai_ci nên không phân biệt hoa thường, dấu.
 *
 * <p>Chỉ đọc ghi dữ liệu. Kiểm tra dữ liệu nhập (từ ngày &gt; đến ngày, lọc =, &lt;, &gt;...) nằm ở
 * {@code service/StatsService}, {@code service/GroupService}. Màn hình không gọi lớp này trực tiếp.
 */
public class AdminStatsDao {

    // ------------------------------------------------------------------ A2 Danh sách đăng nhập

    /** Các lượt đăng nhập, mới nhất lên đầu. Lọc theo khoảng ngày và tên đăng nhập / họ tên. */
    public List<LoginRecord> findLogins(LocalDate from, LocalDate to, String keyword) {
        String sql = "SELECT lh.id, lh.user_id, lh.login_at, u.username, u.full_name "
                + "FROM login_history lh JOIN users u ON u.id = lh.user_id "
                + "WHERE (? IS NULL OR lh.login_at >= ?) AND (? IS NULL OR lh.login_at < ?) "
                + "AND (? IS NULL OR u.username LIKE ? OR u.full_name LIKE ?) "
                + "ORDER BY lh.login_at DESC, lh.id DESC";
        return Db.query(sql, ps -> {
            int i = bindRange(ps, 1, from, to);
            bindKeyword(ps, i, keyword);
        }, rs -> {
            LoginRecord r = new LoginRecord();
            r.setId(rs.getLong("id"));
            r.setUserId(rs.getInt("user_id"));
            r.setLoginAt(toTime(rs.getTimestamp("login_at")));
            r.setUsername(rs.getString("username"));
            r.setFullName(rs.getString("full_name"));
            return r;
        });
    }

    // ------------------------------------------------------------------ A3 Danh sách nhóm chat

    /** Tất cả nhóm chat kèm số thành viên, số admin, người tạo. Xếp theo tên (màn hình tự sắp xếp lại). */
    public List<GroupSummary> findGroups(String keyword) {
        String sql = "SELECT c.id, c.name, c.is_encrypted, c.created_at, cu.username AS creator, "
                + "(SELECT COUNT(*) FROM conversation_members m WHERE m.conversation_id = c.id) AS member_count, "
                + "(SELECT COUNT(*) FROM conversation_members m WHERE m.conversation_id = c.id AND m.role = 'ADMIN') AS admin_count "
                + "FROM conversations c LEFT JOIN users cu ON cu.id = c.created_by "
                + "WHERE c.type = 'GROUP' AND (? IS NULL OR c.name LIKE ?) "
                + "ORDER BY c.name, c.id";
        return Db.query(sql, ps -> {
            String like = like(keyword);
            ps.setString(1, like);
            ps.setString(2, like);
        }, rs -> {
            GroupSummary g = new GroupSummary();
            g.setId(rs.getInt("id"));
            g.setName(rs.getString("name"));
            g.setEncrypted(rs.getBoolean("is_encrypted"));
            g.setCreatedAt(toTime(rs.getTimestamp("created_at")));
            g.setCreatorUsername(rs.getString("creator"));
            g.setMemberCount(rs.getInt("member_count"));
            g.setAdminCount(rs.getInt("admin_count"));
            return g;
        });
    }

    /** Thành viên của một nhóm (admin trước), hoặc chỉ các admin nếu {@code adminsOnly}. */
    public List<GroupMember> findGroupMembers(int groupId, boolean adminsOnly) {
        String sql = "SELECT u.id, u.username, u.full_name, m.role, m.joined_at "
                + "FROM conversation_members m JOIN users u ON u.id = m.user_id "
                + "WHERE m.conversation_id = ? AND (? = FALSE OR m.role = 'ADMIN') "
                + "ORDER BY m.role, u.full_name";
        return Db.query(sql, ps -> {
            ps.setInt(1, groupId);
            ps.setBoolean(2, adminsOnly);
        }, rs -> {
            GroupMember gm = new GroupMember();
            gm.setUserId(rs.getInt("id"));
            gm.setUsername(rs.getString("username"));
            gm.setFullName(rs.getString("full_name"));
            gm.setRole(GroupMember.Role.valueOf(rs.getString("role")));
            gm.setJoinedAt(toTime(rs.getTimestamp("joined_at")));
            return gm;
        });
    }

    // ------------------------------------------------------------------ A4 Báo cáo spam

    /** Báo cáo spam mới nhất lên đầu. Lọc theo khoảng ngày và tên đăng nhập người bị báo cáo (theo đề). */
    public List<SpamReport> findSpamReports(LocalDate from, LocalDate to, String keyword) {
        String sql = "SELECT r.id, r.created_at, r.reason, r.status, "
                + "ru.id AS reported_id, ru.username AS reported_username, ru.full_name AS reported_full_name, "
                + "ru.is_locked AS reported_locked, rp.id AS reporter_id, rp.username AS reporter_username "
                + "FROM spam_reports r "
                + "JOIN users ru ON ru.id = r.reported_id "
                + "JOIN users rp ON rp.id = r.reporter_id "
                + "WHERE (? IS NULL OR r.created_at >= ?) AND (? IS NULL OR r.created_at < ?) "
                + "AND (? IS NULL OR ru.username LIKE ?) "
                + "ORDER BY r.created_at DESC, r.id DESC";
        return Db.query(sql, ps -> {
            int i = bindRange(ps, 1, from, to);
            String like = like(keyword);
            ps.setString(i, like);
            ps.setString(i + 1, like);
        }, rs -> {
            SpamReport r = new SpamReport();
            r.setId(rs.getInt("id"));
            r.setCreatedAt(toTime(rs.getTimestamp("created_at")));
            r.setReason(rs.getString("reason"));
            r.setStatus(SpamReport.Status.valueOf(rs.getString("status")));
            r.setReportedId(rs.getInt("reported_id"));
            r.setReportedUsername(rs.getString("reported_username"));
            r.setReportedFullName(rs.getString("reported_full_name"));
            r.setReportedLocked(rs.getBoolean("reported_locked"));
            r.setReporterId(rs.getInt("reporter_id"));
            r.setReporterUsername(rs.getString("reporter_username"));
            return r;
        });
    }

    /**
     * Khoá tài khoản người bị báo cáo và đánh dấu mọi báo cáo về người đó là đã xử lý.
     * Chỉ khoá tài khoản USER (không khoá admin). Trả về true nếu có tài khoản được khoá.
     */
    public boolean lockReportedUser(int userId) {
        return Db.transaction(c -> {
            int locked = Db.update(c, "UPDATE users SET is_locked = TRUE WHERE id = ? AND role = 'USER'",
                    ps -> ps.setInt(1, userId));
            Db.update(c, "UPDATE spam_reports SET status = 'RESOLVED' WHERE reported_id = ? AND status = 'PENDING'",
                    ps -> ps.setInt(1, userId));
            return locked > 0;
        });
    }

    // ------------------------------------------------------------------ A5 Đăng ký mới, A6 biểu đồ

    /** Người dùng (không tính admin) đăng ký trong khoảng ngày, mới nhất lên đầu. */
    public List<UserStats> findNewUsers(LocalDate from, LocalDate to, String keyword) {
        String sql = "SELECT u.id, u.username, u.full_name, u.email, u.created_at FROM users u "
                + "WHERE u.role = 'USER' AND (? IS NULL OR u.created_at >= ?) AND (? IS NULL OR u.created_at < ?) "
                + "AND (? IS NULL OR u.username LIKE ? OR u.full_name LIKE ?) "
                + "ORDER BY u.created_at DESC, u.id DESC";
        return Db.query(sql, ps -> {
            int i = bindRange(ps, 1, from, to);
            bindKeyword(ps, i, keyword);
        }, AdminStatsDao::mapUserBasic);
    }

    /** Các năm có người đăng ký, năm mới nhất trước (thay cho danh sách năm gắn cứng). */
    public List<Integer> findRegistrationYears() {
        return Db.query("SELECT DISTINCT YEAR(created_at) AS y FROM users WHERE role = 'USER' ORDER BY y DESC",
                null, rs -> rs.getInt("y"));
    }

    /** Số người đăng ký mới theo tháng của một năm. Phần tử [0] là tháng 1, tháng không có ai = 0. */
    public int[] countRegistrationsByMonth(int year) {
        return monthly("SELECT MONTH(created_at) AS m, COUNT(*) AS n FROM users "
                + "WHERE role = 'USER' AND YEAR(created_at) = ? GROUP BY MONTH(created_at)", year);
    }

    // ------------------------------------------------------------------ A7 Số lượng bạn bè

    /** Mọi người dùng (không tính admin) kèm số bạn trực tiếp và số bạn tính cả bạn của bạn. */
    public List<UserStats> findFriendCounts(String keyword) {
        String sql = "SELECT v.id, v.username, v.full_name, u.email, v.created_at, v.direct_friends, v.friends_with_fof "
                + "FROM v_user_friend_counts v JOIN users u ON u.id = v.id "
                + "WHERE u.role = 'USER' AND (? IS NULL OR v.username LIKE ? OR v.full_name LIKE ?) "
                + "ORDER BY v.full_name, v.id";
        return Db.query(sql, ps -> bindKeyword(ps, 1, keyword), rs -> {
            UserStats s = mapUserBasic(rs);
            s.setDirectFriends(rs.getInt("direct_friends"));
            s.setFriendsWithFof(rs.getInt("friends_with_fof"));
            return s;
        });
    }

    // ------------------------------------------------------------------ A8 Người dùng hoạt động, A9 biểu đồ

    /**
     * Mọi người dùng (không tính admin) kèm 3 số liệu trong khoảng ngày:
     * số lần mở ứng dụng ({@code app_sessions}), số người đã chat riêng và số nhóm đã chat
     * (đếm cuộc chat có tin do người đó gửi, chat riêng giữa 2 người là duy nhất nên 1 cuộc = 1 người).
     */
    public List<UserStats> findUserActivity(LocalDate from, LocalDate to, String keyword) {
        String sql = "SELECT u.id, u.username, u.full_name, u.email, u.created_at, "
                + "(SELECT COUNT(*) FROM app_sessions s WHERE s.user_id = u.id "
                + "   AND s.opened_at >= ? AND s.opened_at < ?) AS app_opens, "
                + "(SELECT COUNT(DISTINCT m.conversation_id) FROM messages m JOIN conversations c ON c.id = m.conversation_id "
                + "   WHERE m.sender_id = u.id AND c.type = 'PRIVATE' AND m.sent_at >= ? AND m.sent_at < ?) AS people_chatted, "
                + "(SELECT COUNT(DISTINCT m.conversation_id) FROM messages m JOIN conversations c ON c.id = m.conversation_id "
                + "   WHERE m.sender_id = u.id AND c.type = 'GROUP' AND m.sent_at >= ? AND m.sent_at < ?) AS groups_chatted "
                + "FROM users u WHERE u.role = 'USER' AND (? IS NULL OR u.username LIKE ? OR u.full_name LIKE ?) "
                + "ORDER BY u.full_name, u.id";
        // Không có ngày thì lấy toàn bộ: dùng mốc rất xa thay cho NULL để 3 truy vấn con gọn hơn.
        Timestamp start = Timestamp.valueOf((from == null ? LocalDate.of(1970, 1, 1) : from).atStartOfDay());
        Timestamp end = Timestamp.valueOf((to == null ? LocalDate.of(9999, 1, 1) : to.plusDays(1)).atStartOfDay());
        return Db.query(sql, ps -> {
            for (int i = 1; i <= 6; i += 2) {
                ps.setTimestamp(i, start);
                ps.setTimestamp(i + 1, end);
            }
            bindKeyword(ps, 7, keyword);
        }, rs -> {
            UserStats s = mapUserBasic(rs);
            s.setAppOpens(rs.getInt("app_opens"));
            s.setPeopleChatted(rs.getInt("people_chatted"));
            s.setGroupsChatted(rs.getInt("groups_chatted"));
            return s;
        });
    }

    /** Các năm có lượt mở ứng dụng, năm mới nhất trước. */
    public List<Integer> findActivityYears() {
        return Db.query("SELECT DISTINCT YEAR(opened_at) AS y FROM app_sessions ORDER BY y DESC",
                null, rs -> rs.getInt("y"));
    }

    /** Số người có mở ứng dụng theo tháng của một năm (mở nhiều lần chỉ đếm 1). [0] là tháng 1. */
    public int[] countActiveUsersByMonth(int year) {
        return monthly("SELECT MONTH(opened_at) AS m, COUNT(DISTINCT user_id) AS n FROM app_sessions "
                + "WHERE YEAR(opened_at) = ? GROUP BY MONTH(opened_at)", year);
    }

    // ------------------------------------------------------------------ nội bộ

    /** Chạy câu GROUP BY tháng (cột m, n) và đổ vào mảng 12 phần tử, tháng thiếu = 0. */
    private static int[] monthly(String sql, int year) {
        int[] months = new int[12];
        Db.query(sql, ps -> ps.setInt(1, year), rs -> {
            months[rs.getInt("m") - 1] = rs.getInt("n");
            return null;
        });
        return months;
    }

    /** Gắn 4 dấu ? của "(? IS NULL OR col >= ?) AND (? IS NULL OR col < ?)". Trả về vị trí ? kế tiếp. */
    private static int bindRange(PreparedStatement ps, int i, LocalDate from, LocalDate to) throws SQLException {
        Timestamp start = from == null ? null : Timestamp.valueOf(from.atStartOfDay());
        Timestamp end = to == null ? null : Timestamp.valueOf(to.plusDays(1).atStartOfDay());  // < ngày_đến + 1 ngày
        ps.setTimestamp(i, start);
        ps.setTimestamp(i + 1, start);
        ps.setTimestamp(i + 2, end);
        ps.setTimestamp(i + 3, end);
        return i + 4;
    }

    /** Gắn 3 dấu ? của "(? IS NULL OR a LIKE ? OR b LIKE ?)". */
    private static void bindKeyword(PreparedStatement ps, int i, String keyword) throws SQLException {
        String like = like(keyword);
        ps.setString(i, like);
        ps.setString(i + 1, like);
        ps.setString(i + 2, like);
    }

    /** "%từ khoá%" (đã thoát ký tự % _ \), hoặc null nếu không lọc. */
    private static String like(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }
        String escaped = keyword.trim().replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
        return "%" + escaped + "%";
    }

    private static UserStats mapUserBasic(java.sql.ResultSet rs) throws SQLException {
        UserStats s = new UserStats();
        s.setUserId(rs.getInt("id"));
        s.setUsername(rs.getString("username"));
        s.setFullName(rs.getString("full_name"));
        s.setEmail(rs.getString("email"));
        s.setCreatedAt(toTime(rs.getTimestamp("created_at")));
        return s;
    }

    private static LocalDateTime toTime(Timestamp t) {
        return t == null ? null : t.toLocalDateTime();
    }
}
