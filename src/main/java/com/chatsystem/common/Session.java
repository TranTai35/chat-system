package com.chatsystem.common;

import com.chatsystem.model.User;

/**
 * Phiên đăng nhập hiện tại của ứng dụng: ai đang đăng nhập.
 * Mọi màn hình cần biết "người dùng hiện tại" thì gọi {@link #currentUser()}, không tự lưu riêng.
 * Mỗi ứng dụng (user, admin) là một tiến trình riêng nên mỗi bên có một Session riêng.
 */
public final class Session {

    private static volatile User current;

    private Session() {
    }

    /** Gọi sau khi đăng nhập thành công. */
    public static void start(User user) {
        current = user;
    }

    /** Gọi khi đăng xuất hoặc đóng ứng dụng. */
    public static void end() {
        current = null;
    }

    public static boolean isLoggedIn() {
        return current != null;
    }

    /** Người dùng đang đăng nhập, hoặc {@code null} nếu chưa đăng nhập. */
    public static User currentUser() {
        return current;
    }

    /** Id người dùng hiện tại; ném lỗi nếu chưa đăng nhập (lỗi lập trình, không phải lỗi của người dùng). */
    public static int currentUserId() {
        User u = current;
        if (u == null) {
            throw new IllegalStateException("Chưa đăng nhập");
        }
        return u.getId();
    }
}
