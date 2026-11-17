package com.chatsystem.admin.ui;

import com.chatsystem.common.ui.BaseDialog;

import javax.swing.*;
import java.awt.*;

/**
 * Admin 1. Dialog xác nhận dùng chung cho Xoá, Khoá, Mở khoá người dùng.
 * Nút xác nhận ở bên phải (đỏ với hành động nguy hiểm), Huỷ ở bên trái.
 * Dùng các hàm tạo sẵn:
 * <pre>
 * ConfirmDialog.delete(SwingUtilities.getWindowAncestor(this), "taitp").setVisible(true);
 * ConfirmDialog.lock(owner, "minhlq").setVisible(true);
 * ConfirmDialog.unlock(owner, "minhlq").setVisible(true);
 * </pre>
 * Phiên bản 1: chỉ giao diện, chưa xử lý sự kiện.
 */
public class ConfirmDialog extends BaseDialog {

    public ConfirmDialog(Window owner, String title, String message, String confirmText, boolean danger) {
        super(owner, title, null);
        JLabel text = new JLabel("<html><body style='width: 360px'>" + message + "</body></html>");
        setContent(text);
        setPrimaryText(confirmText);
        if (danger) {
            setPrimaryDanger();
        }
        finish();
    }

    /** Xác nhận xoá người dùng (nút đỏ "Xoá"). */
    public static ConfirmDialog delete(Window owner, String username) {
        return new ConfirmDialog(owner, "Xoá người dùng",
                "Bạn có chắc muốn xoá tài khoản <b>" + username + "</b>? Hành động này không thể hoàn tác.",
                "Xoá", true);
    }

    /** Xác nhận khoá tài khoản (nút đỏ "Khoá"). */
    public static ConfirmDialog lock(Window owner, String username) {
        return new ConfirmDialog(owner, "Khoá tài khoản",
                "Tài khoản <b>" + username + "</b> sẽ không thể đăng nhập cho đến khi được mở khoá.",
                "Khoá", true);
    }

    /** Xác nhận mở khoá tài khoản (nút chính xanh "Mở khoá"). */
    public static ConfirmDialog unlock(Window owner, String username) {
        return new ConfirmDialog(owner, "Mở khoá tài khoản",
                "Cho phép tài khoản <b>" + username + "</b> đăng nhập lại?",
                "Mở khoá", false);
    }
}
