# Truy vấn các màn admin (A2–A9), Phiên bản 2

Người viết: Nguyễn Đặng Hoàng Khang (24127410). Issue #37.

Các câu dưới đây đã chạy đúng trên MySQL 8.0 với `script/create_db.sql` + `script/sample_data.sql` (CSDL trống, import lại từ đầu).
Trong code, các câu này nằm ở `dao/AdminStatsDao.java` (dấu `?` thay cho giá trị); màn hình gọi `service/StatsService` hoặc `service/GroupService`, không gọi DAO trực tiếp.

Quy ước chung:

- Lọc khoảng ngày: `cột >= :tu` và `cột < :den + INTERVAL 1 DAY`, để không mất dữ liệu trong ngày cuối (ví dụ đăng nhập lúc 2026-11-20 09:20 vẫn nằm trong "Đến 2026-11-20"). Ô ngày để trống thì không giới hạn.
- Tìm theo tên: `LIKE CONCAT('%', :tu_khoa, '%')`. Collation `utf8mb4_0900_ai_ci` nên không phân biệt hoa thường và dấu ("tai" khớp "Tài").
- Các màn thống kê người dùng (A5–A8) chỉ tính `role = 'USER'`, không tính tài khoản admin.
- Kiểm tra dữ liệu nhập (sai định dạng ngày, "Từ" sau "Đến", ô số không phải số) nằm ở `StatsService` và báo lỗi tiếng Việt.

Lưu ý khi import trên máy: chạy `mysql` với `--default-character-set=utf8mb4` (hoặc dùng MySQL Workbench), nếu không tiếng Việt có thể bị lỗi font và sắp xếp theo tên sai.

## A2. Danh sách đăng nhập

```sql
SELECT lh.id, lh.user_id, lh.login_at, u.username, u.full_name
FROM login_history lh JOIN users u ON u.id = lh.user_id
WHERE lh.login_at >= :tu AND lh.login_at < :den + INTERVAL 1 DAY
  AND (u.username LIKE CONCAT('%', :tu_khoa, '%') OR u.full_name LIKE CONCAT('%', :tu_khoa, '%'))
ORDER BY lh.login_at DESC, lh.id DESC;
```

| Thử với | Số dòng |
| --- | --- |
| Không lọc | 247 |
| Từ 2026-11-01, Đến 2026-11-17, từ khoá `tai` | 3 |
| Từ 2026-11-20, Đến 2026-11-20 (một ngày) | 7 |

## A3. Danh sách nhóm chat, thành viên và admin của nhóm

```sql
SELECT c.id, c.name, c.is_encrypted, c.created_at, cu.username AS creator,
       (SELECT COUNT(*) FROM conversation_members m WHERE m.conversation_id = c.id) AS member_count,
       (SELECT COUNT(*) FROM conversation_members m WHERE m.conversation_id = c.id AND m.role = 'ADMIN') AS admin_count
FROM conversations c LEFT JOIN users cu ON cu.id = c.created_by
WHERE c.type = 'GROUP' AND c.name LIKE CONCAT('%', :tu_khoa, '%')
ORDER BY c.name, c.id;

-- Dialog thành viên (bỏ điều kiện role) / dialog admin (giữ điều kiện role)
SELECT u.id, u.username, u.full_name, m.role, m.joined_at
FROM conversation_members m JOIN users u ON u.id = m.user_id
WHERE m.conversation_id = :nhom AND m.role = 'ADMIN'
ORDER BY m.role, u.full_name;
```

| Thử với | Kết quả |
| --- | --- |
| Tất cả nhóm | 6 dòng |
| Từ khoá `nhom` | 1 dòng (Nhóm đồ án Java: 3 thành viên, 1 admin, mã hoá) |
| Thành viên nhóm 11 | 3 dòng (taitp là ADMIN) |
| Admin nhóm 12 (Lớp 24CTT3) | 2 dòng (nhóm có 9 thành viên) |

Sắp xếp theo tên / ngày tạo làm ở bảng của màn hình (BaseTablePanel), không cần truy vấn lại.

## A4. Danh sách báo cáo spam, khoá tài khoản

```sql
SELECT r.id, r.created_at, r.reason, r.status,
       ru.id AS reported_id, ru.username AS reported_username, ru.full_name AS reported_full_name,
       ru.is_locked AS reported_locked, rp.id AS reporter_id, rp.username AS reporter_username
FROM spam_reports r
JOIN users ru ON ru.id = r.reported_id
JOIN users rp ON rp.id = r.reporter_id
WHERE r.created_at >= :tu AND r.created_at < :den + INTERVAL 1 DAY
  AND ru.username LIKE CONCAT('%', :tu_khoa, '%')
ORDER BY r.created_at DESC, r.id DESC;

-- Nút "Khoá tài khoản" (1 transaction)
UPDATE users SET is_locked = TRUE WHERE id = :reported_id AND role = 'USER';
UPDATE spam_reports SET status = 'RESOLVED' WHERE reported_id = :reported_id AND status = 'PENDING';
```

| Thử với | Kết quả |
| --- | --- |
| Không lọc | 10 dòng (minhlq, trangnh đã bị khoá) |
| Từ 2026-10-01, Đến 2026-11-30, tên đăng nhập `minh` | 3 dòng |
| Khoá `ducnm` (id 10) | `users.is_locked = 1`, 2 báo cáo về ducnm chuyển RESOLVED; bấm khoá lần nữa thì báo "Tài khoản ducnm đã bị khoá." |

## A5. Người dùng đăng ký mới

```sql
SELECT u.id, u.username, u.full_name, u.email, u.created_at FROM users u
WHERE u.role = 'USER' AND u.created_at >= :tu AND u.created_at < :den + INTERVAL 1 DAY
ORDER BY u.created_at DESC, u.id DESC;
```

| Thử với | Số dòng |
| --- | --- |
| Từ 2026-11-01, Đến 2026-11-30 | 3 |
| Từ 2025-01-01, Đến 2026-12-31 | 21 (toàn bộ user, không tính admin) |

## A6. Biểu đồ đăng ký mới theo năm

```sql
-- Danh sách năm cho ô chọn năm (thay cho 2026/2025/2024 gắn cứng)
SELECT DISTINCT YEAR(created_at) AS y FROM users WHERE role = 'USER' ORDER BY y DESC;

SELECT MONTH(created_at) AS m, COUNT(*) AS n FROM users
WHERE role = 'USER' AND YEAR(created_at) = :nam
GROUP BY MONTH(created_at);
```

Kết quả: năm `2026, 2025`. Năm 2026 theo tháng 1 → 12: `1, 1, 1, 1, 1, 1, 1, 1, 5, 4, 3, 0` (tháng không có ai đăng ký tự điền 0 trong Java).

## A7. Người dùng và số lượng bạn bè

```sql
SELECT v.id, v.username, v.full_name, u.email, v.created_at, v.direct_friends, v.friends_with_fof
FROM v_user_friend_counts v JOIN users u ON u.id = v.id
WHERE u.role = 'USER'
ORDER BY v.full_name, v.id;
```

Lọc `=`, `<`, `>` trên cột "Số bạn trực tiếp" làm ở `StatsService.listFriendCounts` (quy tắc ở service).

| Thử với | Số dòng |
| --- | --- |
| Không lọc | 21 |
| Số bạn > 3 | 6 |
| Số bạn = 0 | 1 (quynhnt) |
| Số bạn < 2 | 4 |

Ví dụ: taitp có 9 bạn trực tiếp, 10 người khi tính cả bạn của bạn; nhanntt 3 và 9.

## A8. Người dùng hoạt động

```sql
SELECT u.id, u.username, u.full_name, u.email, u.created_at,
  (SELECT COUNT(*) FROM app_sessions s
     WHERE s.user_id = u.id AND s.opened_at >= :tu AND s.opened_at < :den + INTERVAL 1 DAY) AS app_opens,
  (SELECT COUNT(DISTINCT m.conversation_id) FROM messages m JOIN conversations c ON c.id = m.conversation_id
     WHERE m.sender_id = u.id AND c.type = 'PRIVATE'
       AND m.sent_at >= :tu AND m.sent_at < :den + INTERVAL 1 DAY) AS people_chatted,
  (SELECT COUNT(DISTINCT m.conversation_id) FROM messages m JOIN conversations c ON c.id = m.conversation_id
     WHERE m.sender_id = u.id AND c.type = 'GROUP'
       AND m.sent_at >= :tu AND m.sent_at < :den + INTERVAL 1 DAY) AS groups_chatted
FROM users u WHERE u.role = 'USER'
ORDER BY u.full_name, u.id;
```

"Số người đã chat" đếm số cuộc chat riêng có tin do người đó gửi; vì 2 người chỉ có đúng 1 cuộc chat riêng (`private_key`), 1 cuộc = 1 người. Lọc `=`, `<`, `>` trên cột "Mở ứng dụng" làm ở `StatsService.listActiveUsers`.

| Thử với (Từ 2026-11-01, Đến 2026-11-30) | Kết quả |
| --- | --- |
| Không lọc số | 21 dòng |
| Mở ứng dụng > 2 | 9 dòng |
| taitp | mở 4 lần, chat 1 người, 1 nhóm |
| nhanntt | mở 2 lần, chat 2 người, 2 nhóm |

Ghi chú dữ liệu mẫu: phucvh có 0 lần mở ứng dụng trong 11/2026 nhưng có tin nhắn trong 2 nhóm. Không sai truy vấn, chỉ là dữ liệu mẫu sinh độc lập; có thể nhờ Nhân bổ sung lượt mở ứng dụng cho khớp.

## A9. Biểu đồ người hoạt động theo năm

```sql
SELECT DISTINCT YEAR(opened_at) AS y FROM app_sessions ORDER BY y DESC;

SELECT MONTH(opened_at) AS m, COUNT(DISTINCT user_id) AS n FROM app_sessions
WHERE YEAR(opened_at) = :nam
GROUP BY MONTH(opened_at);
```

Kết quả: năm `2026, 2025`. Năm 2026 theo tháng 1 → 12: `1, 3, 4, 4, 5, 7, 7, 8, 14, 17, 19, 0`. Một người mở nhiều lần trong tháng chỉ đếm 1.
