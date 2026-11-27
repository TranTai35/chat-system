package com.chatsystem.model;

import java.time.LocalDateTime;

/** Một lượt đăng nhập (bảng {@code login_history} JOIN {@code users}), dùng cho màn A2 Danh sách đăng nhập. */
public class LoginRecord {

    private long id;
    private int userId;
    private String username;
    private String fullName;
    private LocalDateTime loginAt;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public LocalDateTime getLoginAt() { return loginAt; }
    public void setLoginAt(LocalDateTime loginAt) { this.loginAt = loginAt; }
}
