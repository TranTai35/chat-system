package com.chatsystem.common.db;

import java.sql.SQLException;

/**
 * Lỗi khi làm việc với CSDL. Là lỗi không bắt buộc bắt ({@link RuntimeException})
 * để các DAO không phải khai báo {@code throws SQLException}. Thông báo đã viết
 * bằng tiếng Việt, có thể hiện thẳng cho người dùng.
 */
public class DbException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private static final int MYSQL_DUPLICATE_ENTRY = 1062;

    public DbException(String message, Throwable cause) {
        super(message, cause);
    }

    /** Vi phạm UNIQUE (trùng tên đăng nhập, trùng email, trùng chat riêng...). */
    public boolean isDuplicateKey() {
        return getCause() instanceof SQLException e && e.getErrorCode() == MYSQL_DUPLICATE_ENTRY;
    }
}
