package com.chatsystem.model;

import java.time.LocalDateTime;

/**
 * Một người dùng kèm số liệu thống kê, dùng chung cho 2 màn:
 * <ul>
 *   <li>A7 Số lượng bạn bè: {@code directFriends}, {@code friendsWithFof} (view {@code v_user_friend_counts});</li>
 *   <li>A8 Người dùng hoạt động: {@code appOpens}, {@code peopleChatted}, {@code groupsChatted}.</li>
 * </ul>
 * Màn nào không dùng số liệu nào thì số đó bằng 0.
 */
public class UserStats {

    private int userId;
    private String username;
    private String fullName;
    private String email;
    private LocalDateTime createdAt;

    private int directFriends;
    private int friendsWithFof;

    private int appOpens;
    private int peopleChatted;
    private int groupsChatted;

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public int getDirectFriends() { return directFriends; }
    public void setDirectFriends(int directFriends) { this.directFriends = directFriends; }

    public int getFriendsWithFof() { return friendsWithFof; }
    public void setFriendsWithFof(int friendsWithFof) { this.friendsWithFof = friendsWithFof; }

    public int getAppOpens() { return appOpens; }
    public void setAppOpens(int appOpens) { this.appOpens = appOpens; }

    public int getPeopleChatted() { return peopleChatted; }
    public void setPeopleChatted(int peopleChatted) { this.peopleChatted = peopleChatted; }

    public int getGroupsChatted() { return groupsChatted; }
    public void setGroupsChatted(int groupsChatted) { this.groupsChatted = groupsChatted; }
}
