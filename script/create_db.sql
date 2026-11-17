-- =====================================================================
-- Chat System - Tạo cơ sở dữ liệu
-- Hệ quản trị: MySQL 8.0.16 trở lên (cần CHECK constraint)
-- Người phụ trách: Nguyễn Trần Trung Nhân (24127476)
--
-- Chạy:  mysql -u root -p < script/create_db.sql
-- CẢNH BÁO: script xoá CSDL chat_system cũ (nếu có) rồi tạo lại từ đầu.
--
-- Quy ước:
--   - Tên bảng / cột: tiếng Anh, snake_case. Giá trị ENUM viết hoa,
--     giao diện tự đổi sang tiếng Việt (MALE -> "Nam", PENDING -> "Chờ xử lý"...).
--   - Thời gian lưu DATETIME theo giờ máy chủ.
--   - Xoá user -> các dữ liệu "của riêng user đó" xoá theo (CASCADE).
--     Tin nhắn trong nhóm vẫn giữ lại, chỉ mất người gửi (SET NULL).
--   - MySQL không cho CHECK trên cột khoá ngoại có CASCADE/SET NULL, nên
--     các ràng buộc kiểu "không tự kết bạn với mình" kiểm tra ở tầng Java.
-- =====================================================================

DROP DATABASE IF EXISTS chat_system;
CREATE DATABASE chat_system
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;   -- tìm kiếm không phân biệt hoa thường / dấu
USE chat_system;

-- ---------------------------------------------------------------------
-- 1. Người dùng (cả user thường và admin)
-- ---------------------------------------------------------------------
CREATE TABLE users (
    id            INT          NOT NULL AUTO_INCREMENT,
    username      VARCHAR(50)  NOT NULL,
    password_hash VARCHAR(255) NOT NULL,                 -- BCrypt/SHA-256, không lưu mật khẩu gốc
    full_name     VARCHAR(100) NOT NULL,
    address       VARCHAR(255) NULL,
    dob           DATE         NULL,
    gender        ENUM('MALE', 'FEMALE', 'OTHER') NULL,
    email         VARCHAR(100) NOT NULL,                 -- dùng cho Quên mật khẩu
    role          ENUM('USER', 'ADMIN') NOT NULL DEFAULT 'USER',
    is_locked     BOOLEAN      NOT NULL DEFAULT FALSE,   -- admin khoá tài khoản
    is_online     BOOLEAN      NOT NULL DEFAULT FALSE,
    public_key    TEXT         NULL,                     -- khoá công khai (Base64), dùng cho nhóm mã hoá
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- ngày đăng ký
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uq_users_username UNIQUE (username),
    CONSTRAINT uq_users_email UNIQUE (email),
    INDEX idx_users_full_name (full_name),
    INDEX idx_users_created_at (created_at)              -- thống kê đăng ký mới theo tháng/năm
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- 2. Lịch sử đăng nhập (admin xem theo user, và DS đăng nhập toàn hệ thống)
-- ---------------------------------------------------------------------
CREATE TABLE login_history (
    id         BIGINT      NOT NULL AUTO_INCREMENT,
    user_id    INT         NOT NULL,
    login_at   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(45) NULL,                         -- đủ cho IPv6
    CONSTRAINT pk_login_history PRIMARY KEY (id),
    CONSTRAINT fk_login_history_user FOREIGN KEY (user_id)
        REFERENCES users (id) ON DELETE CASCADE,
    INDEX idx_login_history_user (user_id, login_at),
    INDEX idx_login_history_time (login_at)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- 3. Lượt mở ứng dụng (thống kê "người hoạt động", "số lần mở ứng dụng")
-- ---------------------------------------------------------------------
CREATE TABLE app_sessions (
    id        BIGINT   NOT NULL AUTO_INCREMENT,
    user_id   INT      NOT NULL,
    opened_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    closed_at DATETIME NULL,                             -- NULL = đang mở
    CONSTRAINT pk_app_sessions PRIMARY KEY (id),
    CONSTRAINT fk_app_sessions_user FOREIGN KEY (user_id)
        REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT ck_app_sessions_time CHECK (closed_at IS NULL OR closed_at >= opened_at),
    INDEX idx_app_sessions_user (user_id, opened_at),
    INDEX idx_app_sessions_time (opened_at)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- 4. Bạn bè
-- Mỗi quan hệ bạn bè lưu 2 dòng (A, B) và (B, A), thêm/xoá cùng lúc trong
-- 1 transaction. Nhờ vậy "bạn của A" chỉ cần WHERE user_id = A.
-- ---------------------------------------------------------------------
CREATE TABLE friendships (
    user_id    INT      NOT NULL,
    friend_id  INT      NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_friendships PRIMARY KEY (user_id, friend_id),
    CONSTRAINT fk_friendships_user FOREIGN KEY (user_id)
        REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_friendships_friend FOREIGN KEY (friend_id)
        REFERENCES users (id) ON DELETE CASCADE,
    INDEX idx_friendships_friend (friend_id)
) ENGINE = InnoDB;

-- Lời mời kết bạn đang chờ. Đồng ý -> thêm 2 dòng friendships rồi xoá lời mời;
-- từ chối -> xoá lời mời.
CREATE TABLE friend_requests (
    sender_id   INT      NOT NULL,
    receiver_id INT      NOT NULL,
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_friend_requests PRIMARY KEY (sender_id, receiver_id),
    CONSTRAINT fk_friend_requests_sender FOREIGN KEY (sender_id)
        REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_friend_requests_receiver FOREIGN KEY (receiver_id)
        REFERENCES users (id) ON DELETE CASCADE,
    INDEX idx_friend_requests_receiver (receiver_id, created_at)
) ENGINE = InnoDB;

-- Chặn: blocker không nhận tin nhắn / lời mời từ blocked. Block thì huỷ kết bạn.
CREATE TABLE blocks (
    blocker_id INT      NOT NULL,
    blocked_id INT      NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_blocks PRIMARY KEY (blocker_id, blocked_id),
    CONSTRAINT fk_blocks_blocker FOREIGN KEY (blocker_id)
        REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_blocks_blocked FOREIGN KEY (blocked_id)
        REFERENCES users (id) ON DELETE CASCADE,
    INDEX idx_blocks_blocked (blocked_id)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- 5. Hội thoại: chat riêng (PRIVATE, đúng 2 thành viên) và nhóm (GROUP)
-- ---------------------------------------------------------------------
CREATE TABLE conversations (
    id           INT          NOT NULL AUTO_INCREMENT,
    type         ENUM('PRIVATE', 'GROUP') NOT NULL,
    name         VARCHAR(100) NULL,                      -- chỉ nhóm mới có tên
    is_encrypted BOOLEAN      NOT NULL DEFAULT FALSE,    -- chỉ nhóm mới bật mã hoá
    key_version  INT          NOT NULL DEFAULT 0,        -- tăng mỗi lần đổi khoá nhóm (thêm/xoá thành viên)
    created_by   INT          NULL,
    created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_conversations PRIMARY KEY (id),
    CONSTRAINT fk_conversations_creator FOREIGN KEY (created_by)
        REFERENCES users (id) ON DELETE SET NULL,
    CONSTRAINT ck_conversations_group_name CHECK (type = 'PRIVATE' OR name IS NOT NULL),
    CONSTRAINT ck_conversations_private CHECK (type = 'GROUP' OR (name IS NULL AND is_encrypted = FALSE)),
    INDEX idx_conversations_type_name (type, name),     -- DS nhóm chat: lọc / sắp xếp theo tên
    INDEX idx_conversations_created_at (created_at)
) ENGINE = InnoDB;

CREATE TABLE conversation_members (
    conversation_id INT      NOT NULL,
    user_id         INT      NOT NULL,
    role            ENUM('ADMIN', 'MEMBER') NOT NULL DEFAULT 'MEMBER',  -- admin nhóm
    joined_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    cleared_at      DATETIME NULL,     -- "Xoá toàn bộ lịch sử" phía mình: ẩn tin gửi trước mốc này
    CONSTRAINT pk_conversation_members PRIMARY KEY (conversation_id, user_id),
    CONSTRAINT fk_conversation_members_conv FOREIGN KEY (conversation_id)
        REFERENCES conversations (id) ON DELETE CASCADE,
    CONSTRAINT fk_conversation_members_user FOREIGN KEY (user_id)
        REFERENCES users (id) ON DELETE CASCADE,
    INDEX idx_conversation_members_user (user_id)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- 6. Tin nhắn
-- Nhóm mã hoá: content là bản mã (Base64), iv + key_version để giải mã.
-- ---------------------------------------------------------------------
CREATE TABLE messages (
    id              BIGINT      NOT NULL AUTO_INCREMENT,
    conversation_id INT         NOT NULL,
    sender_id       INT         NULL,                    -- NULL nếu người gửi đã bị xoá
    content         TEXT        NOT NULL,
    iv              VARCHAR(32) NULL,                    -- chỉ có ở tin mã hoá
    key_version     INT         NULL,                    -- chỉ có ở tin mã hoá
    sent_at         DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),  -- mili giây để giữ đúng thứ tự
    CONSTRAINT pk_messages PRIMARY KEY (id),
    CONSTRAINT fk_messages_conv FOREIGN KEY (conversation_id)
        REFERENCES conversations (id) ON DELETE CASCADE,
    CONSTRAINT fk_messages_sender FOREIGN KEY (sender_id)
        REFERENCES users (id) ON DELETE SET NULL,
    INDEX idx_messages_conv_time (conversation_id, sent_at),  -- tải lịch sử 1 cuộc chat
    INDEX idx_messages_sender_time (sender_id, sent_at),      -- thống kê số người / nhóm đã chat
    FULLTEXT INDEX ft_messages_content (content)               -- tìm kiếm trong toàn bộ tin nhắn
) ENGINE = InnoDB;

-- "Xoá 1 tin nhắn" phía mình: chỉ ẩn với người xoá, người khác vẫn thấy.
CREATE TABLE message_hidden (
    user_id    INT      NOT NULL,
    message_id BIGINT   NOT NULL,
    hidden_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_message_hidden PRIMARY KEY (user_id, message_id),
    CONSTRAINT fk_message_hidden_user FOREIGN KEY (user_id)
        REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_message_hidden_message FOREIGN KEY (message_id)
        REFERENCES messages (id) ON DELETE CASCADE,
    INDEX idx_message_hidden_message (message_id)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- 7. Báo cáo spam (user báo cáo user khác, admin xem và khoá tài khoản)
-- ---------------------------------------------------------------------
CREATE TABLE spam_reports (
    id              INT          NOT NULL AUTO_INCREMENT,
    reporter_id     INT          NOT NULL,
    reported_id     INT          NOT NULL,
    conversation_id INT          NULL,                   -- cuộc chat nơi bấm báo cáo
    reason          VARCHAR(255) NULL,
    status          ENUM('PENDING', 'RESOLVED') NOT NULL DEFAULT 'PENDING',
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_spam_reports PRIMARY KEY (id),
    CONSTRAINT fk_spam_reports_reporter FOREIGN KEY (reporter_id)
        REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_spam_reports_reported FOREIGN KEY (reported_id)
        REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_spam_reports_conv FOREIGN KEY (conversation_id)
        REFERENCES conversations (id) ON DELETE SET NULL,
    INDEX idx_spam_reports_time (created_at),
    INDEX idx_spam_reports_reported (reported_id)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- 8. Khoá nhóm mã hoá
-- Khoá AES của nhóm được mã hoá bằng public_key của từng thành viên.
-- Thêm/xoá thành viên -> tăng conversations.key_version, tạo khoá mới.
-- Thành viên rời nhóm -> các khoá của người đó tự xoá theo.
-- ---------------------------------------------------------------------
CREATE TABLE group_keys (
    conversation_id INT      NOT NULL,
    user_id         INT      NOT NULL,
    key_version     INT      NOT NULL,
    encrypted_key   TEXT     NOT NULL,                   -- Base64
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_group_keys PRIMARY KEY (conversation_id, user_id, key_version),
    CONSTRAINT fk_group_keys_member FOREIGN KEY (conversation_id, user_id)
        REFERENCES conversation_members (conversation_id, user_id) ON DELETE CASCADE,
    INDEX idx_group_keys_user (user_id)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- 9. View thống kê cho admin: "DS người dùng và số lượng bạn bè"
--   direct_friends      : số bạn trực tiếp
--   friends_with_fof    : số người gồm bạn trực tiếp + bạn của bạn (không tính chính mình)
-- ---------------------------------------------------------------------
CREATE VIEW v_user_friend_counts AS
SELECT u.id,
       u.username,
       u.full_name,
       u.created_at,
       (SELECT COUNT(*) FROM friendships f WHERE f.user_id = u.id) AS direct_friends,
       (SELECT COUNT(DISTINCT x.person_id)
          FROM (SELECT f1.friend_id AS person_id, f1.user_id AS owner_id
                  FROM friendships f1
                UNION ALL
                SELECT f2.friend_id, f1.user_id
                  FROM friendships f1
                  JOIN friendships f2 ON f2.user_id = f1.friend_id) x
         WHERE x.owner_id = u.id AND x.person_id <> u.id) AS friends_with_fof
  FROM users u;
