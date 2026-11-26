-- =====================================================================
-- Chat System - Dữ liệu mẫu
-- Người phụ trách: Nguyễn Trần Trung Nhân (24127476)
--
-- Chạy SAU create_db.sql, trên CSDL vừa tạo (bảng còn trống):
--   mysql -u root -p < script/create_db.sql
--   mysql -u root -p chat_system < script/sample_data.sql
--
-- Mật khẩu mẫu (băm SHA-256, cột password_hash):
--   - admin            : admin123
--   - các user còn lại : 123456
--   Nếu Phiên bản 2 đổi cách băm (BCrypt...) thì cập nhật lại cột này.
--
-- Nội dung khớp dữ liệu giả trên các màn hình Phiên bản 1:
--   - nhanntt có 3 bạn (lanpt, minhlq, taitp) và 2 lời mời (myltk, phucvh)
--   - taitp có 9 bạn; minhlq, trangnh bị khoá sau nhiều lần bị báo cáo spam
--   - Nhóm "Nhóm đồ án Java" đã mã hoá (taitp admin, khangndh, nhanntt)
-- Các trường hợp để thử chức năng:
--   - quynhnt: 0 bạn
--   - bạn của bạn: vd. nhanntt -> taitp -> khangndh, huongvt...
--   - 2 nhóm mã hoá (id 11, 15), 2 user bị khoá (minhlq, trangnh)
--   - minhlq bị báo cáo 3 lần; trangnh, ducnm 2 lần
--   - Đăng ký mới trải từ 11/2025 đến 11/2026; lịch sử đăng nhập và lượt mở
--     ứng dụng trải đều các tháng (sinh tự động ở mục 9)
-- =====================================================================

USE chat_system;

-- ---------------------------------------------------------------------
-- 1. Người dùng (22 tài khoản: 1 admin + 21 user)
-- ---------------------------------------------------------------------
INSERT INTO users (id, username, password_hash, full_name, address, dob, gender, email, role,
                   is_locked, is_online, created_at) VALUES
(1,  'admin',    SHA2('admin123', 256), 'Quản trị hệ thống',       '227 Nguyễn Văn Cừ, Q5, TP.HCM',     '1990-01-01', 'OTHER',  'admin@chatsystem.local', 'ADMIN', FALSE, FALSE, '2025-12-01 08:00:00'),
(2,  'taitp',    SHA2('123456', 256),   'Trần Phước Tài',          '227 Nguyễn Văn Cừ, Q5, TP.HCM',     '2006-05-03', 'MALE',   'tai@example.com',   'USER', FALSE, TRUE,  '2026-09-01 09:15:00'),
(3,  'khangndh', SHA2('123456', 256),   'Nguyễn Đặng Hoàng Khang', '12 Lê Lợi, Q1, TP.HCM',             '2006-02-14', 'MALE',   'khang@example.com', 'USER', FALSE, FALSE, '2026-09-02 10:20:00'),
(4,  'nhanntt',  SHA2('123456', 256),   'Nguyễn Trần Trung Nhân',  '45 Võ Văn Tần, Q3, TP.HCM',         '2006-08-21', 'MALE',   'nhan@example.com',  'USER', FALSE, TRUE,  '2026-09-02 20:05:00'),
(5,  'lanpt',    SHA2('123456', 256),   'Phạm Thị Lan',            '8 Trần Hưng Đạo, Q1, TP.HCM',       '2005-11-30', 'FEMALE', 'lan@example.com',   'USER', FALSE, TRUE,  '2026-09-15 14:30:00'),
(6,  'minhlq',   SHA2('123456', 256),   'Lê Quang Minh',           '90 Điện Biên Phủ, Bình Thạnh',      '2004-07-09', 'MALE',   'minh@example.com',  'USER', TRUE,  FALSE, '2026-09-20 11:00:00'),
(7,  'huongvt',  SHA2('123456', 256),   'Võ Thu Hương',            '33 Phan Xích Long, Phú Nhuận',      '2006-01-18', 'FEMALE', 'huong@example.com', 'USER', FALSE, FALSE, '2026-10-03 16:45:00'),
(8,  'baodq',    SHA2('123456', 256),   'Đỗ Quốc Bảo',             '102 Cách Mạng Tháng 8, Q10',        '2005-04-25', 'MALE',   'bao@example.com',   'USER', FALSE, FALSE, '2026-10-11 08:10:00'),
(9,  'trangnh',  SHA2('123456', 256),   'Nguyễn Hà Trang',         '5 Hoàng Văn Thụ, Tân Bình',         '2006-12-02', 'FEMALE', 'trang@example.com', 'USER', TRUE,  FALSE, '2026-10-18 19:25:00'),
(10, 'ducnm',    SHA2('123456', 256),   'Ngô Minh Đức',            '71 Nguyễn Thị Minh Khai, Q3',       '2005-03-12', 'MALE',   'duc@example.com',   'USER', FALSE, TRUE,  '2026-10-29 07:50:00'),
(11, 'anhht',    SHA2('123456', 256),   'Hoàng Tuấn Anh',          '16 Lý Thường Kiệt, Q11',            '2006-06-06', 'MALE',   'anh@example.com',   'USER', FALSE, FALSE, '2026-11-05 13:05:00'),
(12, 'myltk',    SHA2('123456', 256),   'Lê Thị Kiều My',          '28 Nguyễn Trãi, Q5',                '2006-09-27', 'FEMALE', 'my@example.com',    'USER', FALSE, TRUE,  '2026-11-10 21:40:00'),
(13, 'phucvh',   SHA2('123456', 256),   'Vũ Hoàng Phúc',           '64 Pasteur, Q1',                    '2004-10-15', 'MALE',   'phuc@example.com',  'USER', FALSE, FALSE, '2026-11-14 06:30:00'),
(14, 'thaonp',   SHA2('123456', 256),   'Nguyễn Phương Thảo',      '19 Nguyễn Huệ, Q1',                 '2006-03-08', 'FEMALE', 'thao@example.com',  'USER', FALSE, FALSE, '2026-01-12 10:00:00'),
(15, 'quanlm',   SHA2('123456', 256),   'Lâm Minh Quân',           '77 Lạc Long Quân, Q11',             '2005-08-19', 'MALE',   'quan@example.com',  'USER', FALSE, TRUE,  '2026-02-08 15:20:00'),
(16, 'vyhn',     SHA2('123456', 256),   'Huỳnh Ngọc Vy',           '210 Nguyễn Thị Thập, Q7',           '2006-11-11', 'FEMALE', 'vy@example.com',    'USER', FALSE, FALSE, '2026-03-03 09:40:00'),
(17, 'khoapd',   SHA2('123456', 256),   'Phan Đăng Khoa',          '3 Võ Thị Sáu, Q3',                  '2005-12-24', 'MALE',   'khoa@example.com',  'USER', FALSE, FALSE, '2026-04-21 18:15:00'),
(18, 'linhtt',   SHA2('123456', 256),   'Trần Thuỳ Linh',          '150 Hai Bà Trưng, Q1',              '2006-04-02', 'FEMALE', 'linh@example.com',  'USER', FALSE, TRUE,  '2026-05-16 12:00:00'),
(19, 'namvh',    SHA2('123456', 256),   'Võ Hải Nam',              '41 Cộng Hoà, Tân Bình',             '2004-02-29', 'MALE',   'nam@example.com',   'USER', FALSE, FALSE, '2026-06-09 17:35:00'),
(20, 'tuanpm',   SHA2('123456', 256),   'Phạm Minh Tuấn',          '88 Lê Văn Sỹ, Q3',                  '2005-05-20', 'MALE',   'tuan@example.com',  'USER', FALSE, FALSE, '2026-07-19 08:55:00'),
(21, 'quynhnt',  SHA2('123456', 256),   'Nguyễn Thị Quỳnh',        '9 Nguyễn Văn Linh, Q7',             '2007-01-15', 'FEMALE', 'quynh@example.com', 'USER', FALSE, FALSE, '2026-08-27 20:10:00'),
(22, 'hieult',   SHA2('123456', 256),   'Lê Trung Hiếu',           '60 Âu Cơ, Tân Phú',                 '2005-07-07', 'MALE',   'hieu@example.com',  'USER', FALSE, FALSE, '2025-11-20 09:30:00');

-- Khoá công khai / khoá bí mật đã mã hoá (giá trị minh hoạ) cho thành viên các nhóm mã hoá.
-- Phiên bản 2 sinh cặp khoá thật khi đăng ký.
UPDATE users
   SET public_key      = CONCAT('MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA_sample_', username),
       private_key_enc = CONCAT('c2FsdA==:aXZpdml2aXZpdg==:sample_private_key_', username)
 WHERE id IN (2, 3, 4, 14, 16, 18);

-- ---------------------------------------------------------------------
-- 2. Bạn bè: mỗi cặp lưu 2 chiều (A,B) và (B,A)
-- ---------------------------------------------------------------------
CREATE TEMPORARY TABLE tmp_pairs (a INT, b INT);
INSERT INTO tmp_pairs (a, b) VALUES
(2, 3), (2, 4), (2, 5), (2, 6), (2, 7), (2, 8), (2, 10), (2, 11), (2, 12),   -- taitp: 9 bạn
(3, 7), (3, 8),
(4, 5), (4, 6),                                                             -- nhanntt: lanpt, minhlq, taitp
(5, 7), (5, 12),
(7, 8),
(8, 10),
(9, 14),
(10, 11),
(11, 13),
(13, 15),
(14, 15), (14, 16),
(15, 16), (15, 17),
(16, 17), (16, 18),
(17, 19),
(18, 19), (18, 20),
(19, 22);
-- quynhnt (21) không có bạn

INSERT INTO friendships (user_id, friend_id, created_at)
SELECT p.a, p.b, GREATEST(ua.created_at, ub.created_at) + INTERVAL ((p.a + p.b) % 4 + 1) DAY
  FROM tmp_pairs p
  JOIN users ua ON ua.id = p.a
  JOIN users ub ON ub.id = p.b;

INSERT INTO friendships (user_id, friend_id, created_at)
SELECT friend_id, user_id, created_at FROM (SELECT * FROM friendships) f;

DROP TEMPORARY TABLE tmp_pairs;

-- Lời mời kết bạn đang chờ
INSERT INTO friend_requests (sender_id, receiver_id, created_at) VALUES
(12, 4,  '2026-11-16 20:10:00'),   -- myltk  -> nhanntt
(13, 4,  '2026-11-15 08:44:00'),   -- phucvh -> nhanntt
(21, 2,  '2026-11-18 10:00:00'),   -- quynhnt -> taitp
(20, 19, '2026-11-19 14:30:00'),
(22, 18, '2026-11-12 09:00:00');

-- Chặn (người chặn không còn là bạn của người bị chặn)
INSERT INTO blocks (blocker_id, blocked_id, created_at) VALUES
(7,  6, '2026-10-07 08:00:00'),    -- huongvt chặn minhlq
(12, 6, '2026-11-12 09:00:00'),    -- myltk chặn minhlq
(5,  9, '2026-10-26 07:30:00');    -- lanpt chặn trangnh

-- ---------------------------------------------------------------------
-- 3. Hội thoại: chat riêng có private_key = "<id nhỏ>_<id lớn>"
-- ---------------------------------------------------------------------
INSERT INTO conversations (id, type, name, private_key, is_encrypted, key_version, created_by, created_at) VALUES
(1,  'PRIVATE', NULL, '4_5',   FALSE, 0, 4,  '2026-09-16 19:55:00'),   -- nhanntt - lanpt
(2,  'PRIVATE', NULL, '2_4',   FALSE, 0, 2,  '2026-09-03 20:58:00'),   -- taitp - nhanntt
(3,  'PRIVATE', NULL, '4_6',   FALSE, 0, 6,  '2026-09-21 10:00:00'),   -- nhanntt - minhlq
(4,  'PRIVATE', NULL, '6_7',   FALSE, 0, 6,  '2026-10-04 18:50:00'),   -- minhlq spam huongvt
(5,  'PRIVATE', NULL, '5_6',   FALSE, 0, 6,  '2026-10-20 08:00:00'),   -- minhlq spam lanpt
(6,  'PRIVATE', NULL, '2_6',   FALSE, 0, 6,  '2026-11-11 08:20:00'),   -- minhlq spam taitp
(7,  'PRIVATE', NULL, '5_9',   FALSE, 0, 9,  '2026-10-24 09:45:00'),   -- trangnh spam lanpt
(8,  'PRIVATE', NULL, '7_9',   FALSE, 0, 9,  '2026-11-16 22:55:00'),   -- trangnh spam huongvt
(9,  'PRIVATE', NULL, '8_10',  FALSE, 0, 10, '2026-11-01 10:00:00'),   -- ducnm - baodq
(10, 'PRIVATE', NULL, '15_16', FALSE, 0, 15, '2026-03-05 18:55:00'),   -- quanlm - vyhn
(11, 'GROUP', 'Nhóm đồ án Java',         NULL, TRUE,  1, 2,  '2026-11-16 10:00:00'),
(12, 'GROUP', 'Lớp 24CTT3',              NULL, FALSE, 0, 2,  '2026-09-05 07:50:00'),
(13, 'GROUP', 'CLB Guitar HCMUS',        NULL, FALSE, 0, 16, '2026-03-10 17:50:00'),
(14, 'GROUP', 'Đá bóng thứ 7',           NULL, FALSE, 0, 8,  '2026-10-12 20:50:00'),
(15, 'GROUP', 'Ôn thi Cấu trúc dữ liệu', NULL, TRUE,  1, 14, '2026-04-25 20:30:00'),
(16, 'GROUP', 'Phòng trọ Q5',            NULL, FALSE, 0, 11, '2026-11-06 18:45:00');

-- Thành viên: joined_at = lúc tạo hội thoại, hoặc lúc đăng ký nếu đăng ký sau
CREATE TEMPORARY TABLE tmp_members (conv INT, uid INT, role VARCHAR(10));
INSERT INTO tmp_members (conv, uid, role) VALUES
(1, 4, 'MEMBER'), (1, 5, 'MEMBER'),
(2, 2, 'MEMBER'), (2, 4, 'MEMBER'),
(3, 4, 'MEMBER'), (3, 6, 'MEMBER'),
(4, 6, 'MEMBER'), (4, 7, 'MEMBER'),
(5, 5, 'MEMBER'), (5, 6, 'MEMBER'),
(6, 2, 'MEMBER'), (6, 6, 'MEMBER'),
(7, 5, 'MEMBER'), (7, 9, 'MEMBER'),
(8, 7, 'MEMBER'), (8, 9, 'MEMBER'),
(9, 8, 'MEMBER'), (9, 10, 'MEMBER'),
(10, 15, 'MEMBER'), (10, 16, 'MEMBER'),
(11, 2, 'ADMIN'), (11, 3, 'MEMBER'), (11, 4, 'MEMBER'),
(12, 2, 'ADMIN'), (12, 7, 'ADMIN'), (12, 3, 'MEMBER'), (12, 4, 'MEMBER'), (12, 5, 'MEMBER'),
(12, 8, 'MEMBER'), (12, 10, 'MEMBER'), (12, 11, 'MEMBER'), (12, 12, 'MEMBER'),
(13, 16, 'ADMIN'), (13, 14, 'MEMBER'), (13, 15, 'MEMBER'), (13, 17, 'MEMBER'), (13, 18, 'MEMBER'),
(14, 8, 'ADMIN'), (14, 3, 'MEMBER'), (14, 10, 'MEMBER'), (14, 11, 'MEMBER'), (14, 13, 'MEMBER'),
(15, 14, 'ADMIN'), (15, 16, 'MEMBER'), (15, 18, 'MEMBER'),
(16, 11, 'ADMIN'), (16, 10, 'MEMBER'), (16, 13, 'MEMBER');

INSERT INTO conversation_members (conversation_id, user_id, role, joined_at)
SELECT m.conv, m.uid, m.role, GREATEST(c.created_at, u.created_at)
  FROM tmp_members m
  JOIN conversations c ON c.id = m.conv
  JOIN users u ON u.id = m.uid;

DROP TEMPORARY TABLE tmp_members;

-- ---------------------------------------------------------------------
-- 4. Tin nhắn
-- Nhóm mã hoá (11, 15): content là bản mã minh hoạ (Base64) kèm iv, key_version;
-- bản rõ tương ứng ghi ở chú thích. Phiên bản 2 mã hoá thật ở máy người dùng.
-- ---------------------------------------------------------------------
INSERT INTO messages (conversation_id, sender_id, content, iv, key_version, sent_at) VALUES
-- nhanntt - lanpt
(1, 5, 'Chào Nhân, mình là Lan lớp 24CTT3 nè', NULL, NULL, '2026-09-16 20:00:00'),
(1, 4, 'Chào Lan, kết bạn vui nha', NULL, NULL, '2026-09-16 20:02:00'),
(1, 5, 'Phần kết nối CSDL bạn làm sao vậy?', NULL, NULL, '2026-11-10 09:05:00'),
(1, 4, 'Để mình gửi code mẫu nhé, dùng JDBC với file cấu hình', NULL, NULL, '2026-11-10 09:12:00'),
(1, 5, 'Nhân ơi, mai có học Java không?', NULL, NULL, '2026-11-19 21:40:00'),
(1, 4, 'Có nha, 7h30 phòng E202', NULL, NULL, '2026-11-19 21:42:00'),
(1, 4, 'Bạn có slide chương 5 không, gửi mình với', NULL, NULL, '2026-11-20 10:20:00'),
(1, 5, 'Ok bạn, mai gửi nhé', NULL, NULL, '2026-11-20 10:21:00'),
-- taitp - nhanntt
(2, 2, 'Nhân ơi, nhóm mình làm đồ án Chat System nha', NULL, NULL, '2026-09-03 21:00:00'),
(2, 4, 'Ok Tài, mình nhận phần CSDL', NULL, NULL, '2026-09-03 21:05:00'),
(2, 2, 'Nhớ tạo PR trước 16:00 thứ 6 nha', NULL, NULL, '2026-11-20 08:15:00'),
-- nhanntt - minhlq
(3, 6, 'Mình gửi link tài liệu ôn thi Mạng máy tính rồi đó', NULL, NULL, '2026-11-05 15:10:00'),
(3, 4, 'Cảm ơn nhé', NULL, NULL, '2026-11-05 15:12:00'),
-- spam của minhlq
(4, 6, 'Nhận quà miễn phí 500k, bấm link bit.ly/qua-tang-free ngay!!!', NULL, NULL, '2026-10-06 19:00:00'),
(4, 6, 'Bạn ơi bấm link chưa, sắp hết quà rồi', NULL, NULL, '2026-10-06 19:10:00'),
(5, 6, 'Tuyển cộng tác viên online, thu nhập 2 triệu/ngày, inbox ngay', NULL, NULL, '2026-11-17 09:00:00'),
(5, 6, 'Chỉ cần nạp 200k phí kích hoạt tài khoản', NULL, NULL, '2026-11-17 09:15:00'),
(6, 6, 'Đầu tư coin lãi 30%/tuần, tham gia nhóm VIP không?', NULL, NULL, '2026-11-12 08:30:00'),
-- spam của trangnh
(7, 9, 'Mua bán tài khoản game giá rẻ, liên hệ ngay', NULL, NULL, '2026-10-25 09:50:00'),
(8, 9, 'Like trang giúp mình để nhận quà nhé, bấm vào đây', NULL, NULL, '2026-11-16 22:58:00'),
-- ducnm - baodq
(9, 10, 'Cho mình mượn vở Toán rời rạc với', NULL, NULL, '2026-11-16 15:30:00'),
(9, 10, 'Nhanh lên nha, gửi ngay đi', NULL, NULL, '2026-11-16 15:35:00'),
(9, 8,  'Từ từ, mình đang bận', NULL, NULL, '2026-11-16 15:40:00'),
-- quanlm - vyhn
(10, 15, 'Tối nay tập guitar không Vy?', NULL, NULL, '2026-03-05 19:00:00'),
(10, 16, 'Có nha, 8h nhé', NULL, NULL, '2026-03-05 19:05:00'),
(10, 15, 'Mai học nhóm Cấu trúc dữ liệu nha', NULL, NULL, '2026-06-12 20:00:00'),
-- Nhóm đồ án Java (mã hoá)
(11, 2, 'k3Jx9QmW2vT8bLr1YpZc0eFh4uNs7AqD', 'Qm9uZ3JvdXBJVjAx', 1, '2026-11-19 20:58:00'),  -- Mọi người push code trước 22:00 nhé
(11, 4, 'Zt5Rk1PwN8qY3mLc6vBx0aSd2fGh9JuE', 'Qm9uZ3JvdXBJVjAy', 1, '2026-11-19 21:01:00'),  -- Ok, mình xong màn danh sách bạn bè rồi
(11, 3, 'Hq7Wn4XcV1bZ8kMa5sDf2gJl9pRt6YuE', 'Qm9uZ3JvdXBJVjAz', 1, '2026-11-19 21:03:00'),  -- Wireframe mình gửi lúc 18:00 rồi...
(11, 2, 'Pv2Lm8KxB5nQ1wEr7tYu4iOa0sDf3GhJ', 'Qm9uZ3JvdXBJVjA0', 1, '2026-11-20 09:02:00'),  -- Đã push code màn đăng nhập...
-- Lớp 24CTT3
(12, 2, 'Chào cả lớp, nhóm chat lớp 24CTT3 nhé', NULL, NULL, '2026-09-05 08:00:00'),
(12, 3, 'Thứ 2 nộp bài tập Java nha mọi người', NULL, NULL, '2026-09-22 19:00:00'),
(12, 7, 'Mai lớp nghỉ, thầy báo dời sang thứ 5', NULL, NULL, '2026-10-14 21:00:00'),
(12, 8, 'Ai có đề cương ôn thi giữa kỳ không?', NULL, NULL, '2026-10-28 20:15:00'),
(12, 5, 'Mình có, để mình gửi file', NULL, NULL, '2026-10-28 20:20:00'),
(12, 7, 'Có ai biết deadline đồ án Java chưa?', NULL, NULL, '2026-11-16 19:30:00'),
(12, 4, 'Deadline đồ án Java là thứ 7', NULL, NULL, '2026-11-16 19:32:00'),
-- CLB Guitar HCMUS (trải nhiều tháng cho thống kê hoạt động)
(13, 16, 'Chào mừng mọi người vào CLB Guitar HCMUS', NULL, NULL, '2026-03-10 18:00:00'),
(13, 14, 'Tuần này tập bài gì vậy?', NULL, NULL, '2026-04-02 19:00:00'),
(13, 17, 'Mình mới vào, cho hỏi lịch tập ạ', NULL, NULL, '2026-04-25 20:00:00'),
(13, 16, 'Thứ 4 và thứ 7, 18h ở sảnh nhà I', NULL, NULL, '2026-04-25 20:10:00'),
(13, 18, 'Cuối tháng có biểu diễn không mọi người?', NULL, NULL, '2026-05-20 21:00:00'),
(13, 15, 'Có nha, tập bài Phố cũ', NULL, NULL, '2026-06-18 19:30:00'),
(13, 14, 'Nghỉ hè tập online nhé', NULL, NULL, '2026-07-10 20:00:00'),
(13, 16, 'Tháng 8 họp mặt CLB nha', NULL, NULL, '2026-08-05 18:00:00'),
(13, 17, 'Đã đóng quỹ tháng 9', NULL, NULL, '2026-09-03 20:00:00'),
(13, 18, 'Ai mượn capo của mình chưa trả vậy?', NULL, NULL, '2026-10-07 21:00:00'),
(13, 15, 'Tối nay tập muộn 30 phút nha', NULL, NULL, '2026-11-11 17:30:00'),
-- Đá bóng thứ 7
(14, 8,  'Thứ 7 đá sân Phú Thọ 7h nhé', NULL, NULL, '2026-10-12 21:00:00'),
(14, 10, 'Ok, mình mang bóng', NULL, NULL, '2026-11-01 21:05:00'),
(14, 11, 'Tuần này ai đi?', NULL, NULL, '2026-11-06 20:00:00'),
(14, 13, 'Mình đi nha', NULL, NULL, '2026-11-15 20:10:00'),
(14, 3,  'Mình bận, tuần sau nhé', NULL, NULL, '2026-11-15 20:30:00'),
-- Ôn thi Cấu trúc dữ liệu (mã hoá)
(15, 14, 'Xa8Bc3Dd5Ee7Ff9Gg1Hh3Ii5Jj7Kk9Ll', 'T25UaGlDVERMSVYx', 1, '2026-04-25 21:00:00'),
(15, 16, 'Mm2Nn4Oo6Pp8Qq0Rr2Ss4Tt6Uu8Vv0Ww', 'T25UaGlDVERMSVYy', 1, '2026-05-12 20:00:00'),
(15, 18, 'Yy1Zz3Aa5Bb7Cc9Dd1Ee3Ff5Gg7Hh9Ii', 'T25UaGlDVERMSVYz', 1, '2026-06-20 19:00:00'),
(15, 14, 'Jj2Kk4Ll6Mm8Nn0Oo2Pp4Qq6Rr8Ss0Tt', 'T25UaGlDVERMSVY0', 1, '2026-09-15 21:00:00'),
-- Phòng trọ Q5
(16, 11, 'Tháng này tiền điện 1,2 triệu nha', NULL, NULL, '2026-11-06 19:00:00'),
(16, 10, 'Ok, mai mình chuyển', NULL, NULL, '2026-11-06 19:10:00'),
(16, 13, 'Mình mới vào phòng, chào mọi người', NULL, NULL, '2026-11-15 18:00:00');

-- Khoá nhóm cho từng thành viên của 2 nhóm mã hoá (giá trị minh hoạ)
INSERT INTO group_keys (conversation_id, user_id, key_version, encrypted_key, created_at)
SELECT cm.conversation_id, cm.user_id, 1,
       CONCAT('ZW5jcnlwdGVkX2dyb3VwX2tleQ==_', cm.conversation_id, '_', cm.user_id),
       cm.joined_at
  FROM conversation_members cm
 WHERE cm.conversation_id IN (11, 15);

-- Đã đọc / xoá lịch sử phía mình
UPDATE conversation_members SET last_read_at = '2026-11-20 10:20:30'      -- nhanntt chưa đọc tin 10:21 của lanpt
 WHERE conversation_id = 1 AND user_id = 4;
UPDATE conversation_members SET last_read_at = '2026-11-20 10:21:30'
 WHERE conversation_id = 1 AND user_id = 5;
UPDATE conversation_members SET cleared_at = '2026-10-06 19:20:00'        -- huongvt xoá toàn bộ tin spam phía mình
 WHERE conversation_id = 4 AND user_id = 7;

-- nhanntt xoá 1 tin trong nhóm lớp phía mình
INSERT INTO message_hidden (user_id, message_id, hidden_at)
SELECT 4, id, '2026-10-29 07:00:00' FROM messages
 WHERE conversation_id = 12 AND content = 'Ai có đề cương ôn thi giữa kỳ không?';

-- ---------------------------------------------------------------------
-- 5. Báo cáo spam (minhlq 3 lần, trangnh 2 lần -> đã bị khoá)
-- ---------------------------------------------------------------------
INSERT INTO spam_reports (reporter_id, reported_id, conversation_id, reason, status, created_at) VALUES
(7,  6,  4,    'Gửi tin rác',             'RESOLVED', '2026-10-06 19:15:00'),
(2,  6,  6,    'Lừa đảo',                 'RESOLVED', '2026-11-12 08:37:00'),
(5,  6,  5,    'Gửi tin rác',             'RESOLVED', '2026-11-17 09:20:00'),
(5,  9,  7,    'Quảng cáo',               'RESOLVED', '2026-10-25 10:03:00'),
(7,  9,  8,    'Gửi tin rác',             'RESOLVED', '2026-11-16 23:05:00'),
(8,  10, 9,    'Làm phiền',               'PENDING',  '2026-11-16 15:42:00'),
(13, 10, NULL, 'Làm phiền',               'PENDING',  '2026-11-18 21:44:00'),
(12, 11, NULL, 'Ngôn từ không phù hợp',   'PENDING',  '2026-11-14 20:11:00'),
(4,  13, NULL, 'Gửi tin rác',             'PENDING',  '2026-11-15 13:26:00'),
(3,  8,  NULL, 'Làm phiền',               'PENDING',  '2026-11-02 17:50:00');

-- ---------------------------------------------------------------------
-- 6. Lịch sử đăng nhập và lượt mở ứng dụng, trải đều từ 11/2025 đến 20/11/2026
-- Mỗi user mỗi tháng (từ lúc đăng ký) có 1-4 lượt; thời điểm tính theo công thức
-- cố định nên chạy lại vẫn ra cùng dữ liệu. User bị khoá không còn lượt sau ngày khoá.
-- ---------------------------------------------------------------------
CREATE TEMPORARY TABLE tmp_sessions (user_id INT, opened_at DATETIME, minutes INT);

INSERT INTO tmp_sessions (user_id, opened_at, minutes)
WITH RECURSIVE
    months (idx, start_at) AS (
        SELECT 0, CAST('2025-11-01 00:00:00' AS DATETIME)
        UNION ALL
        SELECT idx + 1, start_at + INTERVAL 1 MONTH FROM months WHERE idx < 12
    ),
    slots (n) AS (
        SELECT 1 UNION ALL SELECT n + 1 FROM slots WHERE n < 4
    ),
    candidates AS (
        SELECT u.id AS user_id, u.created_at, u.is_locked,
               m.start_at
                 + INTERVAL ((u.id * 7 + m.idx * 3 + s.n * 5) % 27) DAY
                 + INTERVAL (7 + (u.id + s.n * 3) % 15) HOUR
                 + INTERVAL ((u.id * 13 + s.n * 17) % 60) MINUTE AS t,
               15 + (u.id * s.n * 7) % 120 AS minutes
          FROM users u
          CROSS JOIN months m
          JOIN slots s ON s.n <= 1 + (u.id + m.idx) % 4
    )
SELECT user_id, t, minutes
  FROM candidates
 WHERE t >= created_at
   AND t < '2026-11-20 07:00:00'
   AND NOT (is_locked AND t > '2026-11-17 10:00:00');

-- Hôm nay: các user đang online có 1 lượt mở ứng dụng chưa đóng
INSERT INTO tmp_sessions (user_id, opened_at, minutes)
SELECT id, '2026-11-20 07:30:00' + INTERVAL (id * 11 % 120) MINUTE, NULL
  FROM users WHERE is_online = TRUE;

INSERT INTO login_history (user_id, login_at, ip_address)
SELECT user_id, opened_at, CONCAT('192.168.1.', 10 + user_id)
  FROM tmp_sessions
 ORDER BY opened_at;

-- Lượt mở ứng dụng chỉ tính phân hệ người dùng (admin dùng ứng dụng quản trị)
INSERT INTO app_sessions (user_id, opened_at, closed_at)
SELECT s.user_id, s.opened_at,
       CASE WHEN s.minutes IS NULL THEN NULL ELSE s.opened_at + INTERVAL s.minutes MINUTE END
  FROM tmp_sessions s
  JOIN users u ON u.id = s.user_id AND u.role = 'USER'
 ORDER BY s.opened_at;

DROP TEMPORARY TABLE tmp_sessions;

-- ---------------------------------------------------------------------
-- 7. Tín hiệu "còn sống" (users.last_seen)
-- Online khi is_online = 1 AND last_seen >= NOW() - INTERVAL 3 MINUTE.
-- ---------------------------------------------------------------------
-- Offline: lần cuối thấy là lúc đóng lượt mở ứng dụng gần nhất (admin: lần đăng nhập gần nhất)
UPDATE users u
   SET last_seen = (SELECT MAX(h.login_at) FROM login_history h WHERE h.user_id = u.id);
UPDATE users u
   SET last_seen = GREATEST(last_seen, (SELECT MAX(s.closed_at) FROM app_sessions s WHERE s.user_id = u.id))
 WHERE EXISTS (SELECT 1 FROM app_sessions s WHERE s.user_id = u.id AND s.closed_at IS NOT NULL);

-- Online: đang mở ứng dụng nên last_seen là lúc chạy script. Nếu không có ứng dụng thật
-- gửi tín hiệu thì sau 3 phút những người này thành offline, đúng như khi ứng dụng bị tắt ngang.
-- Muốn demo ai online thì đăng nhập bằng tài khoản đó.
UPDATE users SET last_seen = NOW(3) WHERE is_online = TRUE AND id <> 10;

-- ducnm: ứng dụng bị tắt ngang lúc 09:45 ngày 20/11 (is_online vẫn TRUE, lượt mở ứng dụng chưa đóng)
-- nhưng không còn tín hiệu nên tính là offline. Đăng nhập lại thì đóng lượt cũ tại last_seen.
UPDATE users SET last_seen = '2026-11-20 09:45:00.000' WHERE id = 10;
