package com.chatsystem.model;

import java.time.LocalDateTime;

/** Một thành viên của nhóm (bảng {@code conversation_members} JOIN {@code users}). */
public class GroupMember {

    public enum Role { ADMIN, MEMBER }

    private int userId;
    private String username;
    private String fullName;
    private Role role;
    private LocalDateTime joinedAt;

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public boolean isAdmin() { return role == Role.ADMIN; }

    public LocalDateTime getJoinedAt() { return joinedAt; }
    public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }
}
