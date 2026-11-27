package com.chatsystem.model;

import java.time.LocalDateTime;

/** Một nhóm chat kèm số thành viên và số admin, dùng cho màn A3 Danh sách nhóm chat. */
public class GroupSummary {

    private int id;
    private String name;
    private boolean encrypted;
    private LocalDateTime createdAt;
    private String creatorUsername;
    private int memberCount;
    private int adminCount;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean isEncrypted() { return encrypted; }
    public void setEncrypted(boolean encrypted) { this.encrypted = encrypted; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    /** Tên đăng nhập người tạo; {@code null} nếu người tạo đã bị xoá. */
    public String getCreatorUsername() { return creatorUsername; }
    public void setCreatorUsername(String creatorUsername) { this.creatorUsername = creatorUsername; }

    public int getMemberCount() { return memberCount; }
    public void setMemberCount(int memberCount) { this.memberCount = memberCount; }

    public int getAdminCount() { return adminCount; }
    public void setAdminCount(int adminCount) { this.adminCount = adminCount; }
}
