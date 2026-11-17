package com.chatsystem.admin.ui;

import com.chatsystem.common.ui.Theme;
import com.chatsystem.common.ui.UiUtil;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;

/**
 * Khung chung cho 2 biểu đồ theo năm của admin (đăng ký mới, hoạt động).
 * Bố cục theo docs/DESIGN.md mục 5.5: tiêu đề + chọn năm + nút "Xem", bên dưới là biểu đồ cột 12 tháng.
 * Lớp con chỉ cần gọi setMonthlyData(...) với 12 số liệu.
 */
public abstract class YearChartPanel extends JPanel {

    private static final String SERIES = "Số lượng";

    private final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    private final JComboBox<Integer> yearBox = new JComboBox<>(new Integer[]{2026, 2025, 2024});
    private final JButton viewButton = UiUtil.primaryButton("Xem");
    private final JLabel totalLabel = UiUtil.muted("");

    protected YearChartPanel(String title, String valueAxisLabel) {
        super(new BorderLayout(0, Theme.PADDING));
        setBorder(UiUtil.padding(Theme.PADDING + 4));
        setBackground(Theme.BACKGROUND);

        // ----- Hàng tiêu đề: tiêu đề (trái), chọn năm + nút Xem (phải) -----
        JPanel header = new JPanel(new BorderLayout(Theme.PADDING, 0));
        header.setOpaque(false);
        header.add(UiUtil.title(title), BorderLayout.WEST);
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, Theme.GAP, 0));
        controls.setOpaque(false);
        controls.add(new JLabel("Năm:"));
        controls.add(yearBox);
        controls.add(viewButton);
        header.add(controls, BorderLayout.EAST);

        // ----- Biểu đồ cột: trục hoành T1..T12, trục tung số lượng -----
        JFreeChart chart = ChartFactory.createBarChart(null, "Tháng", valueAxisLabel, dataset,
                PlotOrientation.VERTICAL, false, true, false);
        chart.setBackgroundPaint(Theme.SURFACE);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Theme.SURFACE);
        plot.setOutlineVisible(false);
        plot.setRangeGridlinePaint(Theme.BORDER);
        plot.getRangeAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        plot.getDomainAxis().setTickLabelFont(Theme.FONT_SMALL);
        plot.getDomainAxis().setLabelFont(Theme.FONT_BODY);
        plot.getRangeAxis().setTickLabelFont(Theme.FONT_SMALL);
        plot.getRangeAxis().setLabelFont(Theme.FONT_BODY);
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setBarPainter(new StandardBarPainter());   // không gradient
        renderer.setShadowVisible(false);
        renderer.setSeriesPaint(0, Theme.PRIMARY);
        renderer.setMaximumBarWidth(0.06);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPopupMenu(null);
        chartPanel.setMouseZoomable(false);
        JPanel card = UiUtil.card(new BorderLayout());
        card.add(chartPanel, BorderLayout.CENTER);

        add(header, BorderLayout.NORTH);
        add(card, BorderLayout.CENTER);
        add(totalLabel, BorderLayout.SOUTH);
    }

    /** Đổ 12 số liệu (tháng 1 → 12) vào biểu đồ. */
    public void setMonthlyData(int year, int[] values) {
        dataset.clear();
        int total = 0;
        for (int m = 0; m < 12; m++) {
            int v = m < values.length ? values[m] : 0;
            dataset.addValue(v, SERIES, "T" + (m + 1));
            total += v;
        }
        yearBox.setSelectedItem(year);
        totalLabel.setText("Tổng năm " + year + ": " + total);
    }

    // Getter để gắn sự kiện ở Phiên bản 2
    public JComboBox<Integer> getYearBox() { return yearBox; }
    public JButton getViewButton() { return viewButton; }
}
