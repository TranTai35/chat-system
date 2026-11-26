package com.chatsystem.model;

import java.time.LocalDateTime;

/** Một lời mời kết bạn đang chờ (bảng {@code friend_requests}), kèm thông tin người gửi để hiện lên bảng. */
public class FriendRequest {

    private User sender;
    private int receiverId;
    private LocalDateTime createdAt;

    /** Người gửi lời mời (có id, tên đăng nhập, họ tên). */
    public User getSender() { return sender; }
    public void setSender(User sender) { this.sender = sender; }

    public int getReceiverId() { return receiverId; }
    public void setReceiverId(int receiverId) { this.receiverId = receiverId; }

    /** Thời điểm gửi lời mời. */
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "FriendRequest{" + (sender == null ? "?" : sender.getUsername()) + " -> " + receiverId + "}";
    }
}
