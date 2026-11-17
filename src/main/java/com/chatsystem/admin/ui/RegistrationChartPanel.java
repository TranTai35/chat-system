package com.chatsystem.admin.ui;

/**
 * Admin 6. Biểu đồ số lượng người đăng ký mới theo năm.
 * Chọn năm, trục hoành là tháng, trục tung là số người đăng ký mới.
 * Phiên bản 1: chỉ giao diện + dữ liệu giả, chưa xử lý sự kiện.
 */
public class RegistrationChartPanel extends YearChartPanel {

    public RegistrationChartPanel() {
        super("Biểu đồ đăng ký mới theo năm", "Số người đăng ký");
        loadMockData();
    }

    /** Dữ liệu giả để chụp hình báo cáo Phiên bản 1 (năm 2026, tới tháng 11). */
    private void loadMockData() {
        setMonthlyData(2026, new int[]{4, 6, 3, 8, 5, 7, 9, 12, 18, 14, 11, 0});
    }
}
