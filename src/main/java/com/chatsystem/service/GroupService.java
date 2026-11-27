package com.chatsystem.service;

import com.chatsystem.dao.AdminStatsDao;
import com.chatsystem.model.GroupMember;
import com.chatsystem.model.GroupSummary;

import java.util.List;

/**
 * Nhóm chat.
 * <ul>
 *   <li>Đợt 1 (A3, admin): danh sách nhóm, xem thành viên, xem admin của nhóm (đã có).</li>
 *   <li>Đợt 4 (người dùng): tạo nhóm (tên + ít nhất 1 thành viên, người tạo là admin), đổi tên,
 *       thêm thành viên, gán admin, xoá thành viên (chỉ admin nhóm, kiểm tra ở đây), gọi
 *       {@code GroupKeyService.rotateKey(...)} khi đổi thành viên nhóm mã hoá. Sẽ thêm {@code GroupDao} lúc đó.</li>
 * </ul>
 * Màn hình chỉ gọi lớp này, không gọi DAO trực tiếp (xem {@link AuthService}).
 */
public class GroupService {

    private final AdminStatsDao statsDao;

    public GroupService() {
        this(new AdminStatsDao());
    }

    public GroupService(AdminStatsDao statsDao) {
        this.statsDao = statsDao;
    }

    /** A3: tất cả nhóm chat, lọc theo tên nhóm (để trống = tất cả). */
    public List<GroupSummary> listGroups(String keyword) {
        return statsDao.findGroups(keyword);
    }

    /** A3: thành viên của nhóm ({@code adminsOnly = false}) hoặc chỉ admin của nhóm ({@code true}). */
    public List<GroupMember> listMembers(int groupId, boolean adminsOnly) {
        if (groupId <= 0) {
            throw new ServiceException("Vui lòng chọn một nhóm.");
        }
        return statsDao.findGroupMembers(groupId, adminsOnly);
    }
}
