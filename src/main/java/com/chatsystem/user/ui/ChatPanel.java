package com.chatsystem.user.ui;

import com.chatsystem.common.ui.Theme;
import com.chatsystem.common.ui.UiUtil;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Người dùng. Màn "Tin nhắn": danh sách hội thoại bên trái + khung chat bên phải.
 * Dùng chung cho chat riêng và chat nhóm (xem docs/DESIGN.md mục 5.4).
 * <ul>
 *   <li>Trái (300px): tìm hội thoại, danh sách hội thoại (chấm online / biểu tượng nhóm / ổ khoá nhóm mã hoá),
 *       nút + Tạo nhóm</li>
 *   <li>Header: tên, trạng thái hoặc "Đã mã hoá" + số thành viên, nút tìm trong cuộc chat, menu ⋮
 *       (Xoá toàn bộ lịch sử, Báo cáo spam, Chặn với chat riêng / Quản lý nhóm với chat nhóm)</li>
 *   <li>Thanh tìm trong cuộc chat: tô vàng kết quả, nút ↑ ↓ chuyển kết quả</li>
 *   <li>Tin nhắn: bong bóng bo góc, của mình bên phải (xanh), người khác bên trái (xám);
 *       chuột phải 1 tin -> "Xoá tin nhắn"</li>
 *   <li>Ô nhập: nút Gợi ý (LLM), ô nhập tin, nút Gửi</li>
 * </ul>
 * Dữ liệu (Phiên bản 2): conversations, conversation_members, messages (trừ message_hidden và trước cleared_at).
 * Phiên bản 1: giao diện + dữ liệu giả. Chỉ có các thao tác giao diện (chọn hội thoại, mở/đóng thanh tìm,
 * mở menu ⋮); gửi tin, xoá, chặn... gắn sự kiện ở Phiên bản 2 qua các getter.
 */
public class ChatPanel extends JPanel {

    public static final int LIST_WIDTH = 300;
    public static final int HEADER_HEIGHT = 56;
    /** Bong bóng tin nhắn rộng tối đa 60% khung chat. */
    public static final double BUBBLE_MAX_RATIO = 0.6;

    private static final int ICON_SIZE = 16;

    // ----- Cột trái: danh sách hội thoại -----
    private final JTextField conversationSearchField = UiUtil.textField("Tìm hội thoại", 20);
    private final DefaultListModel<Conversation> conversationModel = new DefaultListModel<>();
    private final JList<Conversation> conversationList = new JList<>(conversationModel);
    private final JButton createGroupButton = UiUtil.button("+ Tạo nhóm");

    // ----- Header cuộc chat -----
    private final JLabel nameLabel = UiUtil.heading("");
    private final JLabel statusLabel = new JLabel();
    private final JLabel membersLabel = UiUtil.muted("");
    private final JButton searchButton = iconButton(ChatIcons.search(ICON_SIZE, Theme.TEXT), "Tìm trong cuộc chat");
    private final JButton moreButton = iconButton(ChatIcons.more(ICON_SIZE, Theme.TEXT), "Tuỳ chọn");

    // ----- Thanh tìm trong cuộc chat -----
    private final JPanel searchBar = new JPanel(new BorderLayout(Theme.GAP, 0));
    private final JTextField searchField = UiUtil.textField("Tìm trong cuộc chat", 20);
    private final JLabel searchCountLabel = UiUtil.muted("");
    private final JButton prevResultButton = UiUtil.button("↑");
    private final JButton nextResultButton = UiUtil.button("↓");
    private final JButton closeSearchButton = UiUtil.button("×");

    // ----- Tin nhắn -----
    private final MessageListPanel messageList = new MessageListPanel();
    private final JScrollPane messageScroll = new JScrollPane(messageList);

    // ----- Ô nhập -----
    private final JButton suggestButton = UiUtil.button("Gợi ý");
    private final JTextField messageField = UiUtil.textField("Nhập tin nhắn...", 30);
    private final JButton sendButton = UiUtil.primaryButton("Gửi");

    // ----- Menu -----
    private final JPopupMenu messageMenu = new JPopupMenu();
    private final JMenuItem deleteMessageItem = dangerItem("Xoá tin nhắn");
    private final JPopupMenu moreMenu = new JPopupMenu();
    private final JMenuItem clearHistoryItem = dangerItem("Xoá toàn bộ lịch sử");
    private final JMenuItem reportSpamItem = dangerItem("Báo cáo spam");
    private final JMenuItem blockItem = dangerItem("Chặn");
    private final JMenuItem manageGroupItem = new JMenuItem("Quản lý nhóm");

    /** Tin nhắn giả của từng hội thoại (Phiên bản 2 thay bằng truy vấn bảng messages). */
    private final Map<String, List<Message>> mockMessages = new LinkedHashMap<>();

    public ChatPanel() {
        super(new BorderLayout());
        setBackground(Theme.SURFACE);

        messageMenu.add(deleteMessageItem);

        add(buildConversationPane(), BorderLayout.WEST);
        add(buildChatPane(), BorderLayout.CENTER);

        // Thao tác giao diện (không đụng dữ liệu): chọn hội thoại, mở/đóng thanh tìm, menu ⋮
        conversationList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && conversationList.getSelectedValue() != null) {
                showConversation(conversationList.getSelectedValue());
            }
        });
        searchButton.addActionListener(e -> setSearchVisible(!searchBar.isVisible()));
        closeSearchButton.addActionListener(e -> setSearchVisible(false));
        moreButton.addActionListener(e -> moreMenu.show(moreButton, 0, moreButton.getHeight()));

        // Mở các dialog (Phiên bản 1: chỉ để xem giao diện, các nút trong dialog chưa xử lý)
        createGroupButton.addActionListener(e -> new CreateGroupDialog(owner()).setVisible(true));
        manageGroupItem.addActionListener(e -> {
            Conversation c = conversationList.getSelectedValue();
            if (c != null) {
                new ManageGroupDialog(owner(), c.name()).setVisible(true);
            }
        });
        reportSpamItem.addActionListener(e -> {
            Conversation c = conversationList.getSelectedValue();
            if (c != null) {
                new ReportSpamDialog(owner(), mockUsername(c.name()), c.name()).setVisible(true);
            }
        });
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applySearch(); }
            public void removeUpdate(DocumentEvent e) { applySearch(); }
            public void changedUpdate(DocumentEvent e) { applySearch(); }
        });

        loadMockData();
    }

    // ================= Dựng giao diện =================

    private JComponent buildConversationPane() {
        JPanel pane = new JPanel(new BorderLayout());
        pane.setBackground(Theme.SURFACE);
        pane.setPreferredSize(new Dimension(LIST_WIDTH, 0));
        pane.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Theme.BORDER));

        conversationSearchField.putClientProperty("JTextField.showClearButton", true);
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.setBorder(UiUtil.padding(Theme.GAP + 4));
        top.add(conversationSearchField);

        conversationList.setCellRenderer(new ConversationRenderer());
        conversationList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        conversationList.setBackground(Theme.SURFACE);
        JScrollPane scroll = new JScrollPane(conversationList);
        scroll.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Theme.BORDER));

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.setBorder(UiUtil.padding(Theme.GAP + 4));
        bottom.add(createGroupButton);

        pane.add(top, BorderLayout.NORTH);
        pane.add(scroll, BorderLayout.CENTER);
        pane.add(bottom, BorderLayout.SOUTH);
        return pane;
    }

    private JComponent buildChatPane() {
        JPanel pane = new JPanel(new BorderLayout());
        pane.setBackground(Theme.SURFACE);

        JPanel north = new JPanel(new BorderLayout());
        north.setOpaque(false);
        north.add(buildHeader(), BorderLayout.NORTH);
        north.add(buildSearchBar(), BorderLayout.SOUTH);

        messageScroll.setBorder(BorderFactory.createEmptyBorder());
        messageScroll.getViewport().setBackground(Theme.SURFACE);
        messageScroll.getVerticalScrollBar().setUnitIncrement(Theme.PADDING);
        messageScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        pane.add(north, BorderLayout.NORTH);
        pane.add(messageScroll, BorderLayout.CENTER);
        pane.add(buildInputBar(), BorderLayout.SOUTH);
        return pane;
    }

    private JComponent buildHeader() {
        JPanel header = new JPanel(new BorderLayout(Theme.GAP, 0));
        header.setBackground(Theme.SURFACE);
        header.setPreferredSize(new Dimension(0, HEADER_HEIGHT));
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER),
                BorderFactory.createEmptyBorder(0, Theme.PADDING + 4, 0, Theme.PADDING)));

        statusLabel.setFont(Theme.FONT_SMALL);
        statusLabel.setIconTextGap(4);

        header.add(centeredRow(nameLabel, statusLabel, membersLabel), BorderLayout.WEST);
        header.add(centeredRow(searchButton, moreButton), BorderLayout.EAST);
        return header;
    }

    private JComponent buildSearchBar() {
        searchBar.setBackground(Theme.BACKGROUND);
        searchBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER),
                BorderFactory.createEmptyBorder(Theme.GAP, Theme.PADDING + 4, Theme.GAP, Theme.PADDING)));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, Theme.GAP, 0));
        buttons.setOpaque(false);
        buttons.add(searchCountLabel);
        buttons.add(prevResultButton);
        buttons.add(nextResultButton);
        buttons.add(closeSearchButton);
        prevResultButton.setToolTipText("Kết quả trước");
        nextResultButton.setToolTipText("Kết quả sau");
        closeSearchButton.setToolTipText("Đóng");

        searchBar.add(searchField, BorderLayout.CENTER);
        searchBar.add(buttons, BorderLayout.EAST);
        return searchBar;
    }

    private JComponent buildInputBar() {
        JPanel bar = new JPanel(new BorderLayout(Theme.GAP, 0));
        bar.setBackground(Theme.SURFACE);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.BORDER),
                BorderFactory.createEmptyBorder(Theme.GAP + 4, Theme.PADDING + 4, Theme.GAP + 4, Theme.PADDING)));

        suggestButton.setIcon(ChatIcons.sparkle(ICON_SIZE, Theme.PRIMARY));
        suggestButton.setToolTipText("Nhờ LLM gợi ý hoặc chỉnh câu trả lời, điền vào ô nhập");

        bar.add(suggestButton, BorderLayout.WEST);
        bar.add(messageField, BorderLayout.CENTER);
        bar.add(sendButton, BorderLayout.EAST);
        return bar;
    }

    // ================= Hiển thị hội thoại =================

    /** Đổi header, menu ⋮ và tin nhắn theo hội thoại được chọn. */
    private void showConversation(Conversation c) {
        nameLabel.setText(c.name());
        if (c.group()) {
            statusLabel.setVisible(c.encrypted());
            statusLabel.setIcon(ChatIcons.lock(14, Theme.WARNING));
            statusLabel.setText("Đã mã hoá");
            statusLabel.setForeground(Theme.WARNING);
            membersLabel.setText(c.members() + " thành viên");
            membersLabel.setVisible(true);
        } else {
            statusLabel.setVisible(true);
            statusLabel.setIcon(null);
            statusLabel.setText("● " + StatusCellRenderer.of(c.online()));
            statusLabel.setForeground(c.online() ? Theme.ONLINE : Theme.OFFLINE);
            membersLabel.setVisible(false);
        }

        // Menu ⋮: chat nhóm có Quản lý nhóm; chat riêng có Báo cáo spam và Chặn
        moreMenu.removeAll();
        if (c.group()) {
            moreMenu.add(manageGroupItem);
            moreMenu.addSeparator();
            moreMenu.add(clearHistoryItem);
        } else {
            moreMenu.add(clearHistoryItem);
            moreMenu.add(reportSpamItem);
            moreMenu.add(blockItem);
        }

        messageList.setMessages(mockMessages.getOrDefault(c.name(), List.of()), c.group());
        applySearch();
        scrollToBottom();
    }

    private void setSearchVisible(boolean visible) {
        searchBar.setVisible(visible);
        if (visible) {
            searchField.requestFocusInWindow();
        }
        applySearch();
    }

    /** Tô vàng chuỗi cần tìm trong các bong bóng và cập nhật số kết quả. */
    private void applySearch() {
        String query = searchBar.isVisible() ? searchField.getText().trim() : "";
        int total = messageList.highlight(query);
        searchCountLabel.setText(query.isEmpty() ? "" : (total == 0 ? "0/0" : "1/" + total));
    }

    /**
     * Mở 1 hội thoại theo tên, bật thanh tìm với từ khoá và cuộn tới tin khớp đầu tiên.
     * Dùng khi bấm 1 kết quả ở màn "Tìm trong tin nhắn".
     */
    public void openConversation(String name, String keyword) {
        for (int i = 0; i < conversationModel.size(); i++) {
            if (conversationModel.get(i).name().equals(name)) {
                jumpToMatch = true;
                searchField.setText(keyword == null ? "" : keyword);
                searchBar.setVisible(true);
                if (conversationList.getSelectedIndex() == i) {
                    showConversation(conversationModel.get(i));
                } else {
                    conversationList.setSelectedIndex(i);   // gọi showConversation qua listener
                }
                conversationList.ensureIndexIsVisible(i);
                return;
            }
        }
    }

    /** true: lần cuộn tới sẽ nhảy tới tin khớp từ khoá thay vì cuối cuộc chat. */
    private boolean jumpToMatch;

    private void scrollToBottom() {
        SwingUtilities.invokeLater(() -> {
            messageList.revalidate();
            SwingUtilities.invokeLater(() -> {
                Bubble target = jumpToMatch ? messageList.firstMatch() : null;
                jumpToMatch = false;
                if (target != null) {
                    Rectangle r = SwingUtilities.convertRectangle(target.getParent(), target.getBounds(), messageList);
                    r.grow(0, Theme.PADDING * 2);   // chừa chỗ cho tên người gửi và giờ
                    messageList.scrollRectToVisible(r);
                } else {
                    JScrollBar bar = messageScroll.getVerticalScrollBar();
                    bar.setValue(bar.getMaximum());
                }
            });
        });
    }

    // ================= Dữ liệu giả =================

    /** Dữ liệu giả để chụp hình báo cáo Phiên bản 1 (người đang đăng nhập là Nhân). */
    private void loadMockData() {
        addConversation(Conversation.privateChat("Phạm Thị Lan", true, "Ok bạn, mai gửi nhé", "10:21"),
                Message.day("2026-11-10"),
                Message.other(null, "Phần kết nối CSDL bạn làm sao vậy?", "09:05"),
                Message.mine("Để mình gửi code mẫu nhé, dùng JDBC với file cấu hình", "09:12"),
                Message.day("Hôm qua"),
                Message.other(null, "Nhân ơi, mai có học Java không?", "21:40"),
                Message.mine("Có nha, 7h30 phòng E202", "21:42"),
                Message.day("Hôm nay"),
                Message.mine("Bạn có slide chương 5 không, gửi mình với", "10:20"),
                Message.other(null, "Ok bạn, mai gửi nhé", "10:21"));

        addConversation(Conversation.groupChat("Nhóm đồ án Java", true, 3, "Tài: Đã push code màn đăng nhập", "09:02"),
                Message.day("Hôm qua"),
                Message.other("Tài", "Mọi người push code trước 22:00 nhé", "20:58"),
                Message.mine("Ok, mình xong màn danh sách bạn bè rồi", "21:01"),
                Message.other("Khang", "Wireframe mình gửi lúc 18:00 rồi, mọi người xem giúp nha", "21:03"),
                Message.day("Hôm nay"),
                Message.other("Tài", "Đã push code màn đăng nhập, mọi người pull về chạy thử rồi góp ý trước 16:00 thứ Sáu nhé. "
                        + "Ai thấy lỗi giao diện thì tạo issue trên GitHub giúp mình.", "09:02"));

        addConversation(Conversation.privateChat("Trần Phước Tài", true, "Nhớ tạo PR trước 16:00 thứ 6 nha", "08:15"),
                Message.day("Hôm nay"),
                Message.other(null, "Nhớ tạo PR trước 16:00 thứ 6 nha", "08:15"));

        addConversation(Conversation.privateChat("Lê Quang Minh", false, "Bạn: Cảm ơn nhé", "Hôm qua"),
                Message.day("Hôm qua"),
                Message.other(null, "Mình gửi link tài liệu ôn thi Mạng máy tính rồi đó", "15:10"),
                Message.mine("Cảm ơn nhé", "15:12"));

        addConversation(Conversation.groupChat("Lớp 24CTT3", false, 42, "Bạn: Deadline đồ án Java là thứ 7", "T2"),
                Message.day("Thứ Hai"),
                Message.other("Hương", "Có ai biết deadline đồ án Java chưa?", "19:30"),
                Message.mine("Deadline đồ án Java là thứ 7", "19:32"));

        // Mở sẵn chat nhóm mã hoá và thanh tìm kiếm như wireframe
        searchField.setText("push");
        conversationList.setSelectedIndex(1);
    }

    private void addConversation(Conversation c, Message... messages) {
        conversationModel.addElement(c);
        mockMessages.put(c.name(), List.of(messages));
    }

    // ================= Tiện ích =================

    private static JButton iconButton(Icon icon, String tooltip) {
        JButton b = UiUtil.button("");
        b.setIcon(icon);
        b.setToolTipText(tooltip);
        return b;
    }

    private static JMenuItem dangerItem(String text) {
        JMenuItem item = new JMenuItem(text);
        item.setForeground(Theme.DANGER);
        return item;
    }

    /** Hàng ngang các component, căn giữa theo chiều dọc. */
    private static JPanel centeredRow(JComponent... items) {
        JPanel row = new JPanel(new GridBagLayout());
        row.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(0, 0, 0, Theme.GAP);
        for (JComponent item : items) {
            row.add(item, c);
        }
        return row;
    }

    /** Cửa sổ chứa khung chat, làm chủ cho các dialog. */
    private Window owner() {
        return SwingUtilities.getWindowAncestor(this);
    }

    /** Tên đăng nhập giả ứng với họ tên trong dữ liệu mock (Phiên bản 2 lấy từ CSDL). */
    private static String mockUsername(String fullName) {
        switch (fullName) {
            case "Phạm Thị Lan": return "lanpt";
            case "Trần Phước Tài": return "taitp";
            case "Lê Quang Minh": return "minhlq";
            default: return fullName;
        }
    }

    // ================= Getter để gắn sự kiện ở Phiên bản 2 =================

    public JTextField getConversationSearchField() { return conversationSearchField; }
    public JList<Conversation> getConversationList() { return conversationList; }
    public JButton getCreateGroupButton() { return createGroupButton; }
    public JButton getSearchButton() { return searchButton; }
    public JButton getMoreButton() { return moreButton; }
    public JTextField getSearchField() { return searchField; }
    public JButton getPrevResultButton() { return prevResultButton; }
    public JButton getNextResultButton() { return nextResultButton; }
    public JButton getSuggestButton() { return suggestButton; }
    public JTextField getMessageField() { return messageField; }
    public JButton getSendButton() { return sendButton; }
    public JMenuItem getDeleteMessageItem() { return deleteMessageItem; }
    public JMenuItem getClearHistoryItem() { return clearHistoryItem; }
    public JMenuItem getReportSpamItem() { return reportSpamItem; }
    public JMenuItem getBlockItem() { return blockItem; }
    public JMenuItem getManageGroupItem() { return manageGroupItem; }

    // ================= Kiểu dữ liệu hiển thị =================

    /** 1 dòng trong danh sách hội thoại. */
    public record Conversation(String name, boolean group, boolean online, boolean encrypted,
                               int members, String preview, String time) {
        static Conversation privateChat(String name, boolean online, String preview, String time) {
            return new Conversation(name, false, online, false, 2, preview, time);
        }

        static Conversation groupChat(String name, boolean encrypted, int members, String preview, String time) {
            return new Conversation(name, true, false, encrypted, members, preview, time);
        }
    }

    /** 1 tin nhắn, hoặc 1 dòng ngăn cách ngày ("Hôm nay"). */
    public record Message(String sender, String text, String time, boolean mine, boolean daySeparator) {
        static Message mine(String text, String time) {
            return new Message(null, text, time, true, false);
        }

        static Message other(String sender, String text, String time) {
            return new Message(sender, text, time, false, false);
        }

        static Message day(String label) {
            return new Message(null, label, null, false, true);
        }
    }

    // ================= Danh sách hội thoại =================

    private static class ConversationRenderer extends JPanel implements ListCellRenderer<Conversation> {
        private final JLabel icon = new JLabel();
        private final JLabel name = new JLabel();
        private final JLabel time = UiUtil.muted("");
        private final JLabel preview = UiUtil.muted("");

        ConversationRenderer() {
            super(new BorderLayout(Theme.GAP, 0));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER),
                    BorderFactory.createEmptyBorder(Theme.GAP + 4, Theme.GAP + 4, Theme.GAP + 4, Theme.GAP + 4)));
            name.setFont(Theme.FONT_BOLD);
            icon.setHorizontalAlignment(SwingConstants.CENTER);
            icon.setVerticalAlignment(SwingConstants.TOP);
            icon.setPreferredSize(new Dimension(ICON_SIZE + 4, ICON_SIZE + 4));

            JPanel firstLine = new JPanel(new BorderLayout(Theme.GAP, 0));
            firstLine.setOpaque(false);
            firstLine.add(name, BorderLayout.CENTER);
            firstLine.add(time, BorderLayout.EAST);

            JPanel text = new JPanel(new GridLayout(2, 1, 0, 4));
            text.setOpaque(false);
            text.add(firstLine);
            text.add(preview);

            add(icon, BorderLayout.WEST);
            add(text, BorderLayout.CENTER);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Conversation> list, Conversation c,
                                                      int index, boolean selected, boolean focused) {
            if (c.group()) {
                icon.setText(null);
                icon.setIcon(c.encrypted() ? ChatIcons.lock(ICON_SIZE, Theme.WARNING)
                        : ChatIcons.group(ICON_SIZE, Theme.MUTED_TEXT));
            } else {
                icon.setIcon(null);
                icon.setText("●");
                icon.setForeground(c.online() ? Theme.ONLINE : Theme.OFFLINE);
            }
            name.setText(c.name());
            name.setForeground(selected ? Theme.PRIMARY : Theme.TEXT);
            time.setText(c.time());
            preview.setText(c.preview());
            setBackground(selected ? Theme.BUBBLE_OTHER : Theme.SURFACE);
            return this;
        }
    }

    // ================= Danh sách tin nhắn =================

    /** Cột tin nhắn: tin nằm sát đáy khi ít, cuộn được khi nhiều; bề ngang luôn bằng khung chat. */
    private class MessageListPanel extends JPanel implements Scrollable {
        private final List<Bubble> bubbles = new ArrayList<>();

        MessageListPanel() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBackground(Theme.SURFACE);
            setBorder(UiUtil.padding(Theme.PADDING + 4));
            // Khung chat đổi bề ngang -> tính lại chiều cao các bong bóng
            addComponentListener(new java.awt.event.ComponentAdapter() {
                private int lastWidth = -1;

                @Override
                public void componentResized(java.awt.event.ComponentEvent e) {
                    if (getWidth() != lastWidth) {
                        lastWidth = getWidth();
                        SwingUtilities.invokeLater(MessageListPanel.this::revalidate);
                    }
                }
            });
        }

        void setMessages(List<Message> messages, boolean showSender) {
            removeAll();
            bubbles.clear();
            add(Box.createVerticalGlue());
            for (Message m : messages) {
                if (m.daySeparator()) {
                    add(new DayRow(m.text()));
                } else {
                    MessageRow row = new MessageRow(m, showSender && !m.mine());
                    bubbles.add(row.bubble);
                    add(row);
                }
                add(Box.createVerticalStrut(Theme.GAP + 4));
            }
            revalidate();
            repaint();
        }

        /** Tô chuỗi query trong mọi bong bóng, trả về tổng số chỗ khớp. */
        int highlight(String query) {
            int total = 0;
            firstMatch = null;
            for (Bubble b : bubbles) {
                int n = b.highlight(query);
                if (n > 0 && firstMatch == null) {
                    firstMatch = b;
                }
                total += n;
            }
            return total;
        }

        private Bubble firstMatch;

        /** Bong bóng đầu tiên có chỗ khớp ở lần tô gần nhất, null nếu không có. */
        Bubble firstMatch() {
            return firstMatch;
        }

        /** Bề ngang dành cho tin nhắn (trừ lề). */
        int contentWidth() {
            Insets in = getInsets();
            int w = getWidth() - in.left - in.right;
            return w > 0 ? w : 640;
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }

        @Override
        public int getScrollableUnitIncrement(Rectangle r, int orientation, int direction) { return Theme.PADDING; }

        @Override
        public int getScrollableBlockIncrement(Rectangle r, int orientation, int direction) {
            return orientation == SwingConstants.VERTICAL ? r.height : r.width;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() { return true; }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            // ít tin: kéo cao bằng khung để glue đẩy tin xuống đáy
            return getParent() != null && getParent().getHeight() > getPreferredSize().height;
        }
    }

    /** Dòng "Hôm nay", "Hôm qua"... ở giữa khung chat. */
    private static class DayRow extends JPanel {
        DayRow(String label) {
            super(new BorderLayout());
            setOpaque(false);
            setAlignmentX(LEFT_ALIGNMENT);
            JLabel l = UiUtil.muted(label);
            l.setHorizontalAlignment(SwingConstants.CENTER);
            add(l);
        }

        @Override
        public Dimension getMaximumSize() {
            return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
        }
    }

    /** 1 tin nhắn: tên người gửi (nhóm), bong bóng, giờ gửi; tin của mình nằm bên phải. */
    private class MessageRow extends JPanel {
        final Bubble bubble;

        MessageRow(Message m, boolean showSender) {
            super(new BorderLayout());
            setOpaque(false);
            setAlignmentX(LEFT_ALIGNMENT);

            float align = m.mine() ? RIGHT_ALIGNMENT : LEFT_ALIGNMENT;
            JPanel column = new JPanel();
            column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));
            column.setOpaque(false);

            if (showSender && m.sender() != null) {
                JLabel sender = UiUtil.muted(m.sender());
                sender.setAlignmentX(align);
                column.add(sender);
                column.add(Box.createVerticalStrut(4));
            }
            bubble = new Bubble(m.text(), m.mine());
            bubble.setAlignmentX(align);
            column.add(bubble);
            column.add(Box.createVerticalStrut(4));
            JLabel time = UiUtil.muted(m.time());
            time.setAlignmentX(align);
            column.add(time);

            add(column, m.mine() ? BorderLayout.EAST : BorderLayout.WEST);
        }

        @Override
        public Dimension getMaximumSize() {
            return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
        }
    }

    /** Bong bóng bo góc 12px, padding 8×12, rộng theo nội dung nhưng không quá 60% khung chat. */
    private class Bubble extends JPanel {
        private static final int ARC = 24;   // bo góc 12px

        private final String text;
        private final Color textColor;
        /**
         * Ô chữ trong bong bóng. Dùng BasicTextPaneUI vì UI của một số Look &amp; Feel (Nimbus...)
         * luôn tô nền trắng cho ô chữ, đè lên màu bong bóng dù đã setOpaque(false).
         */
        private final JTextPane pane = new JTextPane() {
            @Override
            public void updateUI() {
                setUI(new javax.swing.plaf.basic.BasicTextPaneUI());
            }
        };

        Bubble(String text, boolean mine) {
            super(new BorderLayout());
            this.text = text;
            this.textColor = mine ? Theme.BUBBLE_ME_TEXT : Theme.BUBBLE_OTHER_TEXT;
            setOpaque(false);
            setBackground(mine ? Theme.BUBBLE_ME : Theme.BUBBLE_OTHER);
            setBorder(BorderFactory.createEmptyBorder(Theme.GAP, Theme.GAP + 4, Theme.GAP, Theme.GAP + 4));

            pane.setText(text);
            pane.setFont(Theme.FONT_BODY);
            pane.setEditable(false);
            pane.setFocusable(false);
            pane.setOpaque(false);
            pane.setBorder(BorderFactory.createEmptyBorder());
            pane.setMargin(new Insets(0, 0, 0, 0));
            pane.setComponentPopupMenu(messageMenu);   // chuột phải -> "Xoá tin nhắn"
            setComponentPopupMenu(messageMenu);
            add(pane, BorderLayout.CENTER);
            highlight("");
        }

        /** Tô vàng mọi chỗ khớp query (không phân biệt hoa thường), trả về số chỗ khớp. */
        int highlight(String query) {
            StyledDocument doc = pane.getStyledDocument();
            SimpleAttributeSet normal = new SimpleAttributeSet();
            StyleConstants.setForeground(normal, textColor);
            doc.setCharacterAttributes(0, doc.getLength(), normal, true);
            if (query == null || query.isEmpty()) {
                return 0;
            }
            SimpleAttributeSet found = new SimpleAttributeSet();
            StyleConstants.setBackground(found, Theme.HIGHLIGHT);
            StyleConstants.setForeground(found, Theme.TEXT);
            String haystack = text.toLowerCase(Locale.ROOT);
            String needle = query.toLowerCase(Locale.ROOT);
            int count = 0;
            for (int i = haystack.indexOf(needle); i >= 0; i = haystack.indexOf(needle, i + needle.length())) {
                doc.setCharacterAttributes(i, needle.length(), found, true);
                count++;
            }
            return count;
        }

        @Override
        public Dimension getPreferredSize() {
            Insets in = getInsets();
            int padX = in.left + in.right;
            int max = (int) (messageList.contentWidth() * BUBBLE_MAX_RATIO) - padX;
            FontMetrics fm = pane.getFontMetrics(pane.getFont());
            int natural = 0;
            for (String line : text.split("\n")) {
                natural = Math.max(natural, fm.stringWidth(line));
            }
            int innerW = Math.max(Theme.PADDING * 2, Math.min(natural + 4, max));
            pane.setSize(innerW, Short.MAX_VALUE);   // đặt bề ngang để JTextPane tính chiều cao khi xuống dòng
            int innerH = pane.getPreferredSize().height;
            return new Dimension(innerW + padX, innerH + in.top + in.bottom);
        }

        @Override
        public Dimension getMaximumSize() { return getPreferredSize(); }

        @Override
        public Dimension getMinimumSize() { return getPreferredSize(); }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), ARC, ARC);
            g2.dispose();
        }
    }
}
