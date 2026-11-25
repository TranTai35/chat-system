package com.chatsystem.model;

import java.time.LocalDateTime;

/** Một dòng của bảng {@code messages}. Tin của nhóm mã hoá thì {@code content} là bản mã (Base64). */
public class Message {

    private long id;
    private int conversationId;
    private Integer senderId;
    private String content;
    private String iv;
    private Integer keyVersion;
    private LocalDateTime sentAt;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public int getConversationId() { return conversationId; }
    public void setConversationId(int conversationId) { this.conversationId = conversationId; }

    /** Người gửi; {@code null} nếu người đó đã bị xoá khỏi hệ thống. */
    public Integer getSenderId() { return senderId; }
    public void setSenderId(Integer senderId) { this.senderId = senderId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    /** Vector khởi tạo AES (Base64); chỉ có ở tin mã hoá. */
    public String getIv() { return iv; }
    public void setIv(String iv) { this.iv = iv; }

    /** Phiên bản khoá nhóm đã dùng để mã hoá; chỉ có ở tin mã hoá. */
    public Integer getKeyVersion() { return keyVersion; }
    public void setKeyVersion(Integer keyVersion) { this.keyVersion = keyVersion; }

    /** Thời điểm gửi, chính xác đến mili giây (dùng để giữ đúng thứ tự tin). */
    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }

    public boolean isEncrypted() { return iv != null; }

    @Override
    public String toString() {
        return "Message{" + id + ", conv=" + conversationId + ", sender=" + senderId + "}";
    }
}
