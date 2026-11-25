package com.chatsystem.model;

import java.time.LocalDateTime;

/** Một dòng của bảng {@code conversations}: chat riêng (PRIVATE) hoặc nhóm (GROUP). */
public class Conversation {

    public enum Type { PRIVATE, GROUP }

    private int id;
    private Type type;
    private String name;
    private String privateKey;
    private boolean encrypted;
    private int keyVersion;
    private Integer createdBy;
    private LocalDateTime createdAt;

    /**
     * Khoá của chat riêng giữa 2 người: {@code "<id nhỏ>_<id lớn>"}, ví dụ "3_7".
     * Cột private_key là UNIQUE nên 2 người chỉ có đúng 1 cuộc chat riêng.
     */
    public static String privateKeyOf(int userA, int userB) {
        return Math.min(userA, userB) + "_" + Math.max(userA, userB);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }

    /** Tên nhóm; {@code null} nếu là chat riêng. */
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPrivateKey() { return privateKey; }
    public void setPrivateKey(String privateKey) { this.privateKey = privateKey; }

    public boolean isEncrypted() { return encrypted; }
    public void setEncrypted(boolean encrypted) { this.encrypted = encrypted; }

    public int getKeyVersion() { return keyVersion; }
    public void setKeyVersion(int keyVersion) { this.keyVersion = keyVersion; }

    /** Người tạo; {@code null} nếu người đó đã bị xoá. */
    public Integer getCreatedBy() { return createdBy; }
    public void setCreatedBy(Integer createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public boolean isGroup() { return type == Type.GROUP; }

    @Override
    public String toString() {
        return "Conversation{" + id + ", " + type + (name != null ? ", " + name : "") + "}";
    }
}
