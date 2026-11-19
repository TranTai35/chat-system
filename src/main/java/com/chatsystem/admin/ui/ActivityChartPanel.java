package com.chatsystem.admin.ui;

/**
 * Admin 9. Biểu đồ số lượng người hoạt động theo năm.
 * Chọn năm, trục hoành là tháng, trục tung là số người có mở ứng dụng trong tháng.
 * Phiên bản 1: chỉ giao diện + dữ liệu giả, chưa xử lý sự kiện.
 *
 * Đối chiếu CSDL (script/create_db.sql): "người hoạt động" là người có mở ứng dụng
 * trong tháng, lấy từ bảng app_sessions (opened_at). Một người mở nhiều lần chỉ đếm 1.
 * Phiên bản 2 dự kiến truy vấn (kết quả đổ vào setMonthlyData, tháng thiếu = 0):
 *   SELECT MONTH(opened_at) AS thang, COUNT(DISTINCT user_id) AS so_luong FROM app_sessions
 *   WHERE YEAR(opened_at) = ?   -- năm chọn ở yearBox
 *   GROUP BY MONTH(opened_at);
 */
public class ActivityChartPanel extends YearChartPanel {

    public ActivityChartPanel() {
        super("Biểu đồ hoạt động theo năm", "Số người hoạt động");
        loadMockData();
    }

    /** Dữ liệu giả để chụp hình báo cáo Phiên bản 1 (năm 2026, tới tháng 11). */
    private void loadMockData() {
        setMonthlyData(2026, new int[]{15, 18, 17, 22, 20, 19, 16, 24, 38, 45, 52, 0});
    }
}
