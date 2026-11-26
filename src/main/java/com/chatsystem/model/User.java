package com.chatsystem.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Một dòng của bảng {@code users} (cả người dùng thường và admin). */
public class User {

    public enum Role { USER, ADMIN }

    public enum Gender { MALE, FEMALE, OTHER }

    private int id;
    private String username;
    private String passwordHash;
    private String fullName;
    private String address;
    private LocalDate dob;
    private Gender gender;
    private String email;
    private Role role = Role.USER;
    private boolean locked;
    private boolean online;
    private LocalDateTime lastSeen;
    private String publicKey;
    private String privateKeyEnc;
    private LocalDateTime createdAt;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }

    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public boolean isLocked() { return locked; }
    public void setLocked(boolean locked) { this.locked = locked; }

    /**
     * Đang online hay không, đã tính theo tín hiệu "còn sống" (is_online = 1 và last_seen trong 3 phút)
     * khi đọc từ {@code UserDao}. Đây là trạng thái lúc truy vấn; cần giá trị mới thì đọc lại.
     */
    public boolean isOnline() { return online; }
    public void setOnline(boolean online) { this.online = online; }

    /** Lần cuối ứng dụng của người này gửi tín hiệu "còn sống" ({@code users.last_seen}). */
    public LocalDateTime getLastSeen() { return lastSeen; }
    public void setLastSeen(LocalDateTime lastSeen) { this.lastSeen = lastSeen; }

    public String getPublicKey() { return publicKey; }
    public void setPublicKey(String publicKey) { this.publicKey = publicKey; }

    public String getPrivateKeyEnc() { return privateKeyEnc; }
    public void setPrivateKeyEnc(String privateKeyEnc) { this.privateKeyEnc = privateKeyEnc; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public boolean isAdmin() { return role == Role.ADMIN; }

    @Override
    public String toString() {
        return "User{" + id + ", " + username + "}";
    }
}
