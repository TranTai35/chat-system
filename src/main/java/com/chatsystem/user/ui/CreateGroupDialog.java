package com.chatsystem.user.ui;

import com.chatsystem.common.ui.BaseDialog;
import com.chatsystem.common.ui.FormPanel;
import com.chatsystem.common.ui.Theme;
import com.chatsystem.common.ui.UiUtil;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.regex.Pattern;

/**
 * User. Dialog tạo nhóm chat (wireframe user/02-create-group-dialog).
 * Nhập tên nhóm, tìm bạn bè, tích chọn tối thiểu 1 thành viên rồi bấm "Tạo nhóm".
 * Mở từ màn Bạn bè đang online (nút "+ Tạo nhóm") và khung chat:
 * <pre>
 * new CreateGroupDialog(SwingUtilities.getWindowAncestor(this)).setVisible(true);
 * </pre>
 * Phiên bản 1: chỉ giao diện + bạn bè giả; ô tích chọn và ô tìm chạy được để xem, "Tạo nhóm" chưa xử lý.
 */
public class CreateGroupDialog extends BaseDialog {

    private static final int COL_CHECK = 0;
    private static final int COL_USERNAME = 1;
    private static final int COL_FULLNAME = 2;

    private final JTextField groupNameField;
    private final JTextField searchField;
    private final DefaultTableModel model;
    private final JTable table;
    private final JLabel selectedLabel = UiUtil.muted("");

    public CreateGroupDialog(Window owner) {
        super(owner, "Tạo nhóm chat", "Chọn tối thiểu 1 thành viên");

        FormPanel form = new FormPanel();
        groupNameField = form.addField("Tên nhóm", UiUtil.textField("vd: Nhóm đồ án Java", 20));
        searchField = form.addField("Tìm bạn bè", UiUtil.textField("Tìm theo tên", 20));

        // Bảng chọn thành viên: cột đầu là ô tích (sửa được), 2 cột còn lại chỉ để xem
        model = new DefaultTableModel(new Object[]{"", "Tên đăng nhập", "Họ tên"}, 0) {
            @Override
            public Class<?> getColumnClass(int c) {
                return c == COL_CHECK ? Boolean.class : String.class;
            }

            @Override
            public boolean isCellEditable(int r, int c) {
                return c == COL_CHECK;
            }
        };
        table = new JTable(model);
        table.setRowHeight(Theme.ROW_HEIGHT);
        table.getTableHeader().setFont(Theme.FONT_BOLD);
        table.getTableHeader().setReorderingAllowed(false);
        table.setFillsViewportHeight(true);
        table.getColumnModel().getColumn(COL_CHECK).setMaxWidth(40);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filter(sorter); }
            public void removeUpdate(DocumentEvent e) { filter(sorter); }
            public void changedUpdate(DocumentEvent e) { filter(sorter); }
        });
        model.addTableModelListener(e -> updateSelectedLabel());

        loadMockData();
        updateSelectedLabel();

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        scroll.setPreferredSize(new Dimension(Theme.DIALOG_WIDTH, 170));

        JPanel content = new JPanel(new BorderLayout(0, Theme.GAP));
        content.setOpaque(false);
        content.add(form, BorderLayout.NORTH);
        content.add(scroll, BorderLayout.CENTER);
        content.add(selectedLabel, BorderLayout.SOUTH);

        setContent(content);
        setPrimaryText("Tạo nhóm");
        finish();
    }

    /** Thêm 1 người bạn vào danh sách chọn. */
    public void addFriend(boolean selected, String username, String fullName) {
        model.addRow(new Object[]{selected, username, fullName});
    }

    /** Dữ liệu giả khớp wireframe: 2 người đã chọn, 2 người chưa. */
    private void loadMockData() {
        addFriend(true, "lanpt", "Phạm Thị Lan");
        addFriend(true, "nhanntt", "Nguyễn Trần Trung Nhân");
        addFriend(false, "minhlq", "Lê Quang Minh");
        addFriend(false, "huongvt", "Võ Thu Hương");
    }

    private void filter(TableRowSorter<DefaultTableModel> sorter) {
        String text = searchField.getText().trim();
        sorter.setRowFilter(text.isEmpty() ? null
                : RowFilter.regexFilter("(?iu)" + Pattern.quote(text), COL_USERNAME, COL_FULLNAME));
    }

    private void updateSelectedLabel() {
        int n = 0;
        for (int r = 0; r < model.getRowCount(); r++) {
            if (Boolean.TRUE.equals(model.getValueAt(r, COL_CHECK))) {
                n++;
            }
        }
        selectedLabel.setText("Đã chọn " + n + " thành viên");
    }

    // Getter để gắn sự kiện ở Phiên bản 2
    public JTextField getGroupNameField() { return groupNameField; }
    public JTextField getSearchField() { return searchField; }
    public JTable getTable() { return table; }
}
