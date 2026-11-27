package com.chatsystem.service;

import com.chatsystem.dao.AdminStatsDao;
import com.chatsystem.model.LoginRecord;
import com.chatsystem.model.SpamReport;
import com.chatsystem.model.UserStats;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

/**
 * Các màn thống kê của admin: A2 Danh sách đăng nhập, A4 Báo cáo spam, A5 Đăng ký mới, A6 biểu đồ đăng ký,
 * A7 Số lượng bạn bè, A8 Người dùng hoạt động, A9 biểu đồ hoạt động. Nhóm chat (A3) ở {@link GroupService}.
 *
 * <p>Theo khuôn {@link AuthService}: màn hình chỉ gọi lớp này (qua {@code Async.run}), lớp này gọi
 * {@link AdminStatsDao}. Quy tắc kiểm tra dữ liệu nhập nằm ở đây:
 * <ul>
 *   <li>ngày nhập theo dạng yyyy-MM-dd, ô trống = không giới hạn; "Từ" không được sau "Đến";</li>
 *   <li>bộ lọc số lượng chỉ nhận "=", "&lt;", "&gt;" và số nguyên không âm.</li>
 * </ul>
 * <pre>{@code
 * Async.run(() -> statsService.listLogins(fromField.getText(), toField.getText(), keyword),
 *           rows -> rows.forEach(r -> addRow(r.getLoginAt(), r.getUsername(), r.getFullName())));
 * }</pre>
 */
public class StatsService {

    private static final DateTimeFormatter DATE =
            DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);

    private final AdminStatsDao dao;

    public StatsService() {
        this(new AdminStatsDao());
    }

    public StatsService(AdminStatsDao dao) {
        this.dao = dao;
    }

    // ------------------------------------------------------------------ A2

    /** Danh sách đăng nhập, mới nhất lên đầu. Ngày dạng yyyy-MM-dd, để trống = không giới hạn. */
    public List<LoginRecord> listLogins(String from, String to, String keyword) {
        LocalDate[] range = parseRange(from, to);
        return dao.findLogins(range[0], range[1], keyword);
    }

    // ------------------------------------------------------------------ A4

    /** Danh sách báo cáo spam, lọc theo khoảng ngày và tên đăng nhập người bị báo cáo. */
    public List<SpamReport> listSpamReports(String from, String to, String keyword) {
        LocalDate[] range = parseRange(from, to);
        return dao.findSpamReports(range[0], range[1], keyword);
    }

    /**
     * Nút "Khoá tài khoản" của màn A4: đặt users.is_locked và đánh dấu các báo cáo về người này là đã xử lý.
     *
     * @throws ServiceException tài khoản đã bị khoá sẵn, hoặc không khoá được (không tồn tại / là admin)
     */
    public void lockReportedUser(SpamReport report) {
        if (report == null) {
            throw new ServiceException("Vui lòng chọn một báo cáo.");
        }
        if (report.isReportedLocked()) {
            throw new ServiceException("Tài khoản " + report.getReportedUsername() + " đã bị khoá.");
        }
        if (!dao.lockReportedUser(report.getReportedId())) {
            throw new ServiceException("Không khoá được tài khoản " + report.getReportedUsername() + ".");
        }
    }

    // ------------------------------------------------------------------ A5, A6

    /** Người dùng đăng ký mới trong khoảng ngày (bắt buộc nhập đủ Từ và Đến). */
    public List<UserStats> listNewUsers(String from, String to, String keyword) {
        LocalDate[] range = parseRange(from, to);
        if (range[0] == null || range[1] == null) {
            throw new ServiceException("Vui lòng nhập đủ ngày Từ và Đến.");
        }
        return dao.findNewUsers(range[0], range[1], keyword);
    }

    /** Các năm cho ô chọn năm của biểu đồ đăng ký mới. Chưa có dữ liệu thì trả về năm hiện tại. */
    public List<Integer> registrationYears() {
        return orThisYear(dao.findRegistrationYears());
    }

    /** 12 số liệu (tháng 1 → 12) cho {@code YearChartPanel.setMonthlyData}. */
    public int[] registrationsByMonth(int year) {
        return dao.countRegistrationsByMonth(year);
    }

    // ------------------------------------------------------------------ A7

    /**
     * Người dùng và số lượng bạn bè. Lọc theo cột "Số bạn trực tiếp".
     *
     * @param operator "=", "&lt;" hoặc "&gt;"
     * @param count    ô số; để trống = không lọc
     */
    public List<UserStats> listFriendCounts(String keyword, String operator, String count) {
        return filterByCount(dao.findFriendCounts(keyword), UserStats::getDirectFriends, operator, count);
    }

    // ------------------------------------------------------------------ A8, A9

    /** Người dùng hoạt động trong khoảng ngày, lọc theo cột "Mở ứng dụng". */
    public List<UserStats> listActiveUsers(String from, String to, String keyword, String operator, String count) {
        LocalDate[] range = parseRange(from, to);
        if (range[0] == null || range[1] == null) {
            throw new ServiceException("Vui lòng nhập đủ ngày Từ và Đến.");
        }
        return filterByCount(dao.findUserActivity(range[0], range[1], keyword), UserStats::getAppOpens, operator, count);
    }

    /** Các năm cho ô chọn năm của biểu đồ hoạt động. */
    public List<Integer> activityYears() {
        return orThisYear(dao.findActivityYears());
    }

    /** 12 số liệu (tháng 1 → 12): số người có mở ứng dụng trong tháng. */
    public int[] activeUsersByMonth(int year) {
        return dao.countActiveUsersByMonth(year);
    }

    // ------------------------------------------------------------------ quy tắc dùng chung

    /** Đọc 2 ô ngày; trống = null. Sai định dạng hoặc Từ sau Đến thì báo lỗi. */
    static LocalDate[] parseRange(String from, String to) {
        LocalDate f = parseDate(from, "Từ");
        LocalDate t = parseDate(to, "Đến");
        if (f != null && t != null && f.isAfter(t)) {
            throw new ServiceException("Ngày \"Từ\" không được sau ngày \"Đến\".");
        }
        return new LocalDate[]{f, t};
    }

    private static LocalDate parseDate(String text, String label) {
        if (text == null || text.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(text.trim(), DATE);
        } catch (DateTimeParseException e) {
            throw new ServiceException("Ngày \"" + label + "\" phải có dạng yyyy-MM-dd, ví dụ 2026-11-27.");
        }
    }

    /** Lọc =, <, > một số nhập vào trên một cột số. Ô số trống thì không lọc. */
    static List<UserStats> filterByCount(List<UserStats> rows, ToIntFunction<UserStats> column,
                                         String operator, String count) {
        if (count == null || count.isBlank()) {
            return rows;
        }
        int n;
        try {
            n = Integer.parseInt(count.trim());
        } catch (NumberFormatException e) {
            throw new ServiceException("Số lượng phải là số nguyên.");
        }
        if (n < 0) {
            throw new ServiceException("Số lượng không được âm.");
        }
        String op = operator == null ? "=" : operator.trim();
        return rows.stream().filter(r -> {
            int v = column.applyAsInt(r);
            switch (op) {
                case "=": return v == n;
                case "<": return v < n;
                case ">": return v > n;
                default: throw new ServiceException("Phép so sánh chỉ được là =, < hoặc >.");
            }
        }).collect(Collectors.toList());
    }

    private static List<Integer> orThisYear(List<Integer> years) {
        return years.isEmpty() ? List.of(LocalDate.now().getYear()) : years;
    }
}
