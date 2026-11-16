package com.chatsystem.common.ui;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Màn hình danh sách dùng chung: tiêu đề + thanh công cụ (ô tìm, bộ lọc, sắp xếp, nút)
 * + bảng dữ liệu. Dùng cho hầu hết màn hình của phân hệ admin.
 *
 * <pre>
 * BaseTablePanel p = new BaseTablePanel("Danh sách nhóm chat",
 *         new String[]{"Tên nhóm", "Số thành viên", "Ngày tạo"});
 * p.setSearchColumns(0);                         // ô tìm lọc theo cột "Tên nhóm"
 * p.addSortOption("Tên (A-Z)", 0, SortOrder.ASCENDING);
 * p.addSortOption("Mới tạo nhất", 2, SortOrder.DESCENDING);
 * p.addFilter("Từ ngày", new JTextField(8));     // bộ lọc riêng của màn hình
 * JButton members = p.addAction("Xem thành viên");
 * p.addRow("Nhóm Java", 5, "2026-11-01");
 * </pre>
 */
public class BaseTablePanel extends JPanel {

    private final DefaultTableModel model;
    private final JTable table;
    private final TableRowSorter<DefaultTableModel> sorter;
    private final JTextField searchField;
    private final JComboBox<SortOption> sortBox = new JComboBox<>();
    private final JPanel filterBar = new JPanel(new WrapLayout(FlowLayout.LEFT, Theme.GAP, 4));
    private final JPanel actionBar = new JPanel(new WrapLayout(FlowLayout.RIGHT, Theme.GAP, 4));
    private final JLabel countLabel = UiUtil.muted("");
    private int[] searchColumns = new int[0];

    /** Độ rộng mặc định mỗi cột; màn hình đổi riêng bằng setColumnWidth(). */
    public static final int DEFAULT_COLUMN_WIDTH = 100;

    public BaseTablePanel(String title, String[] columns) {
        super(new BorderLayout(0, Theme.PADDING));
        setBorder(UiUtil.padding(Theme.PADDING + 4));
        setBackground(Theme.BACKGROUND);

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                // để cột số sắp xếp đúng theo số, không theo chữ
                for (int r = 0; r < getRowCount(); r++) {
                    Object v = getValueAt(r, columnIndex);
                    if (v != null) {
                        return v.getClass();
                    }
                }
                return Object.class;
            }
        };
        table = new JTable(model) {
            // Cửa sổ rộng: bảng giãn hết chiều ngang. Cửa sổ hẹp: giữ độ rộng cột và hiện thanh cuộn ngang
            @Override
            public boolean getScrollableTracksViewportWidth() {
                return getPreferredSize().width < getParent().getWidth();
            }
        };
        table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        for (int i = 0; i < columns.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(DEFAULT_COLUMN_WIDTH);
        }
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setFont(Theme.FONT_BOLD);
        table.getTableHeader().setReorderingAllowed(false);
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        // ----- Hàng 1: tiêu đề (trái, giữ nguyên) + nút thao tác (chiếm phần còn lại,
        //        tự xuống dòng khi cửa sổ hẹp nên không bao giờ đè lên tiêu đề) -----
        actionBar.setOpaque(false);
        JPanel header = new JPanel(new BorderLayout(Theme.PADDING, 0));
        header.setOpaque(false);
        JLabel titleLabel = UiUtil.title(title);
        titleLabel.setVerticalAlignment(SwingConstants.TOP);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
        header.add(titleLabel, BorderLayout.WEST);
        header.add(actionBar, BorderLayout.CENTER);

        // ----- Hàng 2: ô tìm + bộ lọc (trái), sắp xếp (phải) -----
        searchField = UiUtil.textField("Tìm kiếm...", 20);
        searchField.putClientProperty("JTextField.showClearButton", true);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applySearch(); }
            public void removeUpdate(DocumentEvent e) { applySearch(); }
            public void changedUpdate(DocumentEvent e) { applySearch(); }
        });
        filterBar.setOpaque(false);
        filterBar.add(searchField);

        sortBox.setVisible(false);
        sortBox.addActionListener(e -> applySort());
        JLabel sortLabel = new JLabel("Sắp xếp:");
        sortLabel.setVisible(false);
        sortBox.putClientProperty("label", sortLabel);
        JPanel sortBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, Theme.GAP, 0));
        sortBar.setOpaque(false);
        sortBar.add(sortLabel);
        sortBar.add(sortBox);

        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);
        toolbar.add(filterBar, BorderLayout.CENTER);
        toolbar.add(sortBar, BorderLayout.EAST);

        JPanel top = new JPanel(new BorderLayout(0, Theme.GAP));
        top.setOpaque(false);
        top.add(header, BorderLayout.NORTH);
        top.add(toolbar, BorderLayout.SOUTH);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));

        add(top, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(countLabel, BorderLayout.SOUTH);  // "x / y dòng" dưới bảng
        updateCount();

        // Khi đổi kích thước cửa sổ, tính lại số hàng của thanh nút / bộ lọc
        java.awt.event.ComponentAdapter relayout = new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                // revalidate chính thanh đó để Swing bỏ kích thước cũ đã lưu và tính lại chiều cao
                SwingUtilities.invokeLater(() -> ((JComponent) e.getComponent()).revalidate());
            }
        };
        actionBar.addComponentListener(relayout);
        filterBar.addComponentListener(relayout);
    }

    // ================= API cho các màn hình =================

    /** Các cột mà ô "Tìm kiếm" sẽ lọc theo. Không gọi = lọc mọi cột. */
    public void setSearchColumns(int... columns) {
        this.searchColumns = columns;
    }

    public void setSearchPlaceholder(String text) {
        searchField.putClientProperty("JTextField.placeholderText", text);
    }

    /** Thêm 1 lựa chọn vào combobox "Sắp xếp". Lựa chọn đầu tiên được áp dụng ngay. */
    public BaseTablePanel addSortOption(String label, int column, SortOrder order) {
        sortBox.addItem(new SortOption(label, column, order));
        sortBox.setVisible(true);
        ((JLabel) sortBox.getClientProperty("label")).setVisible(true);
        return this;
    }

    /** Thêm một bộ lọc riêng (combobox trạng thái, ô ngày, ô số...) sau ô tìm kiếm. */
    public <T extends JComponent> T addFilter(String label, T component) {
        // nhãn + ô lọc gói chung 1 khối để khi xuống dòng không bị tách rời nhau
        JPanel group = new JPanel(new FlowLayout(FlowLayout.LEFT, Theme.GAP, 0));
        group.setOpaque(false);
        if (label != null && !label.isEmpty()) {
            group.add(new JLabel(label));
        }
        group.add(component);
        filterBar.add(group);
        return component;
    }

    /** Thêm nút ở góc phải thanh công cụ, trả về nút để gắn sự kiện sau. */
    public JButton addAction(String text) {
        return addAction(UiUtil.button(text));
    }

    public JButton addAction(JButton button) {
        actionBar.add(button);
        return button;
    }

    public void addRow(Object... values) {
        model.addRow(values);
        updateCount();
    }

    public void setRows(List<Object[]> rows) {
        model.setRowCount(0);
        for (Object[] r : rows) {
            model.addRow(r);
        }
        applySort();
        updateCount();
    }

    /** Dòng đang chọn (theo chỉ số trong model), -1 nếu chưa chọn. */
    public int getSelectedModelRow() {
        int viewRow = table.getSelectedRow();
        return viewRow < 0 ? -1 : table.convertRowIndexToModel(viewRow);
    }

    public void setColumnWidth(int column, int width) {
        table.getColumnModel().getColumn(column).setPreferredWidth(width);
    }

    public JTable getTable() { return table; }

    public DefaultTableModel getModel() { return model; }

    public JTextField getSearchField() { return searchField; }

    public TableRowSorter<DefaultTableModel> getSorter() { return sorter; }

    // ================= Nội bộ =================

    private void applySearch() {
        String text = searchField.getText().trim();
        if (text.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?iu)" + Pattern.quote(text), searchColumns));
        }
        updateCount();
    }

    private void applySort() {
        SortOption opt = (SortOption) sortBox.getSelectedItem();
        if (opt == null) {
            return;
        }
        List<RowSorter.SortKey> keys = new ArrayList<>();
        keys.add(new RowSorter.SortKey(opt.column, opt.order));
        sorter.setSortKeys(keys);
    }

    private void updateCount() {
        countLabel.setText(table.getRowCount() + " / " + model.getRowCount() + " dòng");
    }

    private record SortOption(String label, int column, SortOrder order) {
        @Override
        public String toString() {
            return label;
        }
    }
}
