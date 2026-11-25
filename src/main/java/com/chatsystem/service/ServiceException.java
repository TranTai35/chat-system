package com.chatsystem.service;

/**
 * Lỗi nghiệp vụ: người dùng làm sai quy tắc của hệ thống (sai mật khẩu, tài khoản bị khoá,
 * trùng tên đăng nhập, không phải admin nhóm...). Thông báo đã viết bằng tiếng Việt,
 * màn hình hiện thẳng cho người dùng bằng hộp thoại.
 *
 * <p>Khác với {@code DbException} (hỏng kết nối, lỗi SQL): đây là lỗi mong đợi, không phải sự cố.
 */
public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
