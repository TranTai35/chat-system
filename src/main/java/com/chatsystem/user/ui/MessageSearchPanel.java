package com.chatsystem.user.ui;

import com.chatsystem.common.ui.BaseTablePanel;
import com.chatsystem.common.ui.Theme;
import com.chatsystem.common.ui.UiUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Locale;
import java.util.function.BiConsumer;

/**
 * Người dùng. Tìm chuỗi trong lịch sử chat với tất cả mọi người.
 * - Ô từ khoá (lọc ngay khi gõ), chọn phạm vi "Trong:" 1 hội thoại hoặc tất cả
 * - Kết quả: hội thoại, người gửi, nội dung (tô vàng từ khoá), thời gian; sắp xếp mới nhất / cũ nhất
 * - Bấm đúp 1 dòng hoặc nút "Mở đoạn chat": mở khung chat và nhảy tới tin nhắn đó
 * Dữ liệu (Phiên bản 2): messages JOIN conversation_members (của mình) WHERE content LIKE '%từ khoá%',
 * bỏ tin trong message_hidden và trước cleared_at (xem docs/erd/README.md).
 * Phiên bản 1: giao diện + dữ liệu giả (cùng nội dung với khung chat).
 */
public class MessageSearchPanel extends BaseTablePanel {

    // Thứ tự cột
    public static final int COL_CONVERSATION = 0;
    public static final int COL_SENDER = 1;
    public static final int COL_CONTENT = 2;
    public static final int COL_TIME = 3;

    private static final String ALL = "Tất cả hội thoại";
    /** Số ký tự tối đa hiện ở cột nội dung; dài hơn thì cắt quanh từ khoá. */
    private static final int SNIPPET_LENGTH = 60;

    private final JComboBox<String> scopeFilter;
    private final JButton openButton;
    private BiConsumer<String, String> openChatHandler;

    public MessageSearchPanel() {
        super("Tìm trong tin nhắn", new String[]{"Hội thoại", "Người gửi", "Nội dung", "Thời gian"});

        // ----- Bộ lọc: ô tìm kiếm chính là ô từ khoá -----
        setSearchPlaceholder("Nhập từ khoá");
        setSearchColumns(COL_CONTENT);
        scopeFilter = addFilter("Trong:", new JComboBox<>(new String[]{
                ALL, "Phạm Thị Lan", "Nhóm đồ án Java", "Trần Phước Tài", "Lê Quang Minh", "Lớp 24CTT3"}));

        // ----- Sắp xếp -----
        addSortOption("Mới nhất", COL_TIME, SortOrder.DESCENDING);
        addSortOption("Cũ nhất", COL_TIME, SortOrder.ASCENDING);

        // ----- Nút thao tác -----
        openButton = addAction(UiUtil.primaryButton("Mở đoạn chat"));

        getTable().getColumnModel().getColumn(COL_CONTENT).setCellRenderer(new KeywordRenderer());
        getTable().setToolTipText("Bấm đúp 1 dòng để mở khung chat và nhảy đến tin nhắn đó");
        setColumnWidth(COL_CONVERSATION, 180);
        setColumnWidth(COL_SENDER, 110);
        setColumnWidth(COL_CONTENT, 460);
        setColumnWidth(COL_TIME, 140);

        // Bấm đúp hoặc bấm "Mở đoạn chat" -> mở khung chat (thao tác giao diện)
        getTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    openSelected();
                }
            }
        });
        openButton.addActionListener(e -> openSelected());
        // Gõ từ khoá -> vẽ lại để cột nội dung tô đúng từ mới
        getSearchField().getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { getTable().repaint(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { getTable().repaint(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { getTable().repaint(); }
        });

        loadMockData();
    }

    /**
     * Việc cần làm khi mở 1 kết quả: nhận (tên hội thoại, từ khoá).
     * UserMainFrame dùng để chuyển sang màn "Tin nhắn" và nhảy tới tin nhắn.
     */
    public void setOpenChatHandler(BiConsumer<String, String> handler) {
        this.openChatHandler = handler;
    }

    private void openSelected() {
        int row = getSelectedModelRow();
        if (row < 0 || openChatHandler == null) {
            return;
        }
        String conversation = (String) getModel().getValueAt(row, COL_CONVERSATION);
        openChatHandler.accept(conversation, getSearchField().getText().trim());
    }

    /** Dữ liệu giả: các tin trong khung chat mock; điền sẵn từ khoá "code" như wireframe. */
    private void loadMockData() {
        addRow("Nhóm đồ án Java", "Tài", "Mọi người push code trước 22:00 nhé", "2026-11-19 20:58");
        addRow("Nhóm đồ án Java", "Bạn", "Ok, mình xong màn danh sách bạn bè rồi", "2026-11-19 21:01");
        addRow("Nhóm đồ án Java", "Khang", "Wireframe mình gửi lúc 18:00 rồi, mọi người xem giúp nha", "2026-11-19 21:03");
        addRow("Nhóm đồ án Java", "Tài", "Đã push code màn đăng nhập, mọi người pull về chạy thử rồi góp ý trước 16:00 thứ Sáu nhé. "
                + "Ai thấy lỗi giao diện thì tạo issue trên GitHub giúp mình.", "2026-11-20 09:02");
        addRow("Phạm Thị Lan", "Phạm Thị Lan", "Phần kết nối CSDL bạn làm sao vậy?", "2026-11-10 09:05");
        addRow("Phạm Thị Lan", "Bạn", "Để mình gửi code mẫu nhé, dùng JDBC với file cấu hình", "2026-11-10 09:12");
        addRow("Phạm Thị Lan", "Phạm Thị Lan", "Nhân ơi, mai có học Java không?", "2026-11-19 21:40");
        addRow("Phạm Thị Lan", "Bạn", "Có nha, 7h30 phòng E202", "2026-11-19 21:42");
        addRow("Phạm Thị Lan", "Bạn", "Bạn có slide chương 5 không, gửi mình với", "2026-11-20 10:20");
        addRow("Phạm Thị Lan", "Phạm Thị Lan", "Ok bạn, mai gửi nhé", "2026-11-20 10:21");
        addRow("Trần Phước Tài", "Trần Phước Tài", "Nhớ tạo PR trước 16:00 thứ 6 nha", "2026-11-20 08:15");
        addRow("Lê Quang Minh", "Lê Quang Minh", "Mình gửi link tài liệu ôn thi Mạng máy tính rồi đó", "2026-11-19 15:10");
        addRow("Lê Quang Minh", "Bạn", "Cảm ơn nhé", "2026-11-19 15:12");
        addRow("Lớp 24CTT3", "Hương", "Có ai biết deadline đồ án Java chưa?", "2026-11-16 19:30");
        addRow("Lớp 24CTT3", "Bạn", "Deadline đồ án Java là thứ 7", "2026-11-16 19:32");
        getSearchField().setText("code");
    }

    // Getter để gắn sự kiện ở Phiên bản 2
    public JComboBox<String> getScopeFilter() { return scopeFilter; }
    public JButton getOpenButton() { return openButton; }

    /** Cột nội dung: tô nền Theme.HIGHLIGHT các chỗ khớp từ khoá (không phân biệt hoa thường). */
    private class KeywordRenderer extends DefaultTableCellRenderer {
        private final String highlightHex = hex(Theme.HIGHLIGHT);
        private final String textHex = hex(Theme.TEXT);

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean selected,
                                                       boolean focused, int row, int column) {
            String text = value == null ? "" : value.toString();
            String keyword = getSearchField().getText().trim();
            super.getTableCellRendererComponent(table, highlight(snippet(text, keyword), keyword),
                    selected, focused, row, column);
            setToolTipText(text);   // rê chuột để xem đủ nội dung
            return this;
        }

        /** Tin dài: chỉ hiện đoạn quanh chỗ khớp đầu tiên, "…" ở chỗ bị cắt (giống wireframe). */
        private String snippet(String text, String keyword) {
            if (text.length() <= SNIPPET_LENGTH) {
                return text;
            }
            int hit = keyword.isEmpty() ? -1 : text.toLowerCase(Locale.ROOT).indexOf(keyword.toLowerCase(Locale.ROOT));
            int start = hit < 0 ? 0 : Math.max(0, hit - SNIPPET_LENGTH / 3);
            int end = Math.min(text.length(), start + SNIPPET_LENGTH);
            return (start > 0 ? "…" : "") + text.substring(start, end).trim() + (end < text.length() ? "…" : "");
        }

        private String highlight(String text, String keyword) {
            if (keyword.isEmpty()) {
                return text;
            }
            String lower = text.toLowerCase(Locale.ROOT);
            String needle = keyword.toLowerCase(Locale.ROOT);
            StringBuilder html = new StringBuilder("<html><nobr>");   // nobr: giữ 1 dòng, dài thì xem tooltip
            int from = 0;
            for (int i = lower.indexOf(needle); i >= 0; i = lower.indexOf(needle, from)) {
                html.append(escape(text.substring(from, i)))
                        .append("<span style='background:").append(highlightHex).append(";color:").append(textHex).append("'>")
                        .append(escape(text.substring(i, i + needle.length())))
                        .append("</span>");
                from = i + needle.length();
            }
            return html.append(escape(text.substring(from))).append("</nobr></html>").toString();
        }

        private static String hex(Color c) {
            return String.format("#%06X", c.getRGB() & 0xFFFFFF);
        }

        private String escape(String s) {
            return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        }
    }
}
