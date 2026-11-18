package com.chatsystem.admin.ui;

import com.chatsystem.common.ui.BaseTablePanel;

import javax.swing.*;
import java.text.Collator;
import java.util.Locale;

/**
 * Admin 3. Danh sách các nhóm chat.
 * - Sắp xếp theo tên / thời gian tạo, lọc theo tên nhóm
 * - Xem danh sách thành viên, danh sách admin của nhóm (mở GroupMembersDialog)
 * Cột khớp script/create_db.sql (đã chốt 18/11): conversations (type = 'GROUP')
 *   name, created_by, is_encrypted, created_at; số thành viên / admin đếm từ conversation_members.role.
 * Phiên bản 1: chỉ giao diện + dữ liệu giả, chưa xử lý sự kiện.
 */
public class GroupListPanel extends BaseTablePanel {

    public static final int COL_NAME = 0;
    public static final int COL_CREATOR = 1;     // conversations.created_by
    public static final int COL_MEMBERS = 2;
    public static final int COL_ADMINS = 3;
    public static final int COL_ENCRYPTED = 4;
    public static final int COL_CREATED = 5;

    private final JButton membersButton;
    private final JButton adminsButton;

    public GroupListPanel() {
        super("Danh sách nhóm chat", new String[]{
                "Tên nhóm", "Người tạo", "Số thành viên", "Số admin", "Mã hoá", "Ngày tạo"});

        // ----- Bộ lọc -----
        setSearchPlaceholder("Tìm theo tên nhóm");
        setSearchColumns(COL_NAME);

        // ----- Sắp xếp -----
        // So sánh theo tiếng Việt để "Ăn vặt", "Đá bóng", "Ôn thi" không bị dồn xuống cuối
        getSorter().setComparator(COL_NAME, Collator.getInstance(new Locale("vi", "VN")));
        addSortOption("Tên (A-Z)", COL_NAME, SortOrder.ASCENDING);
        addSortOption("Tên (Z-A)", COL_NAME, SortOrder.DESCENDING);
        addSortOption("Mới tạo nhất", COL_CREATED, SortOrder.DESCENDING);
        addSortOption("Cũ nhất", COL_CREATED, SortOrder.ASCENDING);

        // ----- Nút thao tác -----
        membersButton = addAction("Xem thành viên");
        adminsButton = addAction("Xem admin");

        setColumnWidth(COL_NAME, 240);
        setColumnWidth(COL_CREATOR, 120);
        setColumnWidth(COL_MEMBERS, 110);
        setColumnWidth(COL_ADMINS, 90);
        setColumnWidth(COL_ENCRYPTED, 90);
        setColumnWidth(COL_CREATED, 110);

        loadMockData();
    }

    /** Dữ liệu giả để chụp hình báo cáo Phiên bản 1. */
    private void loadMockData() {
        addRow("Nhóm đồ án Java", "taitp", 3, 1, "Có", "2026-11-16");
        addRow("Lớp 24CTT3", "anhht", 42, 2, "Không", "2026-09-05");
        addRow("CLB Guitar HCMUS", "baodq", 18, 2, "Không", "2026-09-12");
        addRow("Đá bóng thứ 7", "ducnm", 12, 1, "Không", "2026-09-28");
        addRow("Ôn thi Cấu trúc dữ liệu", "nhanntt", 9, 1, "Có", "2026-10-02");
        addRow("Phòng trọ Q5", "lanpt", 4, 1, "Không", "2026-10-07");
        addRow("Team Hackathon", "khangndh", 5, 2, "Có", "2026-10-15");
        addRow("Gia đình", "huongvt", 6, 1, "Không", "2026-10-21");
        addRow("Học tiếng Nhật N4", "myltk", 15, 1, "Không", "2026-10-30");
        addRow("Ăn vặt Nguyễn Văn Cừ", "phucvh", 23, 3, "Không", "2026-11-04");
        addRow("Mạng máy tính nhóm 2", "trangnh", 4, 1, "Có", "2026-11-09");
    }

    // Getter để gắn sự kiện ở Phiên bản 2
    public JButton getMembersButton() { return membersButton; }
    public JButton getAdminsButton() { return adminsButton; }
}
