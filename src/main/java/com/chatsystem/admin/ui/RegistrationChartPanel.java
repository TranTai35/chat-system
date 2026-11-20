package com.chatsystem.admin.ui;

/**
 * Admin 6. Biểu đồ số lượng người đăng ký mới theo năm.
 * Chọn năm, trục hoành là tháng, trục tung là số người đăng ký mới.
 * Phiên bản 1: chỉ giao diện + dữ liệu giả, chưa xử lý sự kiện.
 *
 * Đối chiếu CSDL (script/create_db.sql): đếm theo users.created_at.
 * Phiên bản 2 dự kiến truy vấn (kết quả đổ vào setMonthlyData, tháng thiếu = 0):
 *   SELECT MONTH(created_at) AS thang, COUNT(*) AS so_luong FROM users
 *   WHERE role = 'USER' AND YEAR(created_at) = ?   -- năm chọn ở yearBox
 *   GROUP BY MONTH(created_at);
 */
public class RegistrationChartPanel extends YearChartPanel {

    public RegistrationChartPanel() {
        super("Biểu đồ đăng ký mới theo năm", "Số người đăng ký");
        loadMockData();
    }

    /** Dữ liệu giả để chụp hình báo cáo Phiên bản 1 (năm 2026, tới tháng 11, khớp script/sample_data.sql). */
    private void loadMockData() {
        setMonthlyData(2026, new int[]{1, 1, 1, 1, 1, 1, 1, 1, 5, 4, 3, 0});
    }
}
