package com.chatsystem.common.db;

import com.chatsystem.common.config.AppConfig;

/**
 * Chương trình nhỏ để thử kết nối CSDL, không mở giao diện.
 *
 * <pre>
 * mvn compile exec:java -Dexec.mainClass=com.chatsystem.common.db.DbCheck
 * </pre>
 * Chạy sau khi đã chạy create_db.sql và sample_data.sql.
 */
public final class DbCheck {

    private DbCheck() {
    }

    public static void main(String[] args) {
        System.out.println(AppConfig.describeSource());
        System.out.println("URL: " + Db.url());
        try {
            Db.ping();
            System.out.println("Kết nối thành công.");
            long users = Db.queryOne("SELECT COUNT(*) FROM users", null, rs -> rs.getLong(1)).orElse(0L);
            System.out.println("Số tài khoản trong bảng users: " + users);
        } catch (DbException e) {
            System.out.println("THẤT BẠI: " + e.getMessage());
            System.exit(1);
        }
    }
}
