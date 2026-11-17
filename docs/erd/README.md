# Thiết kế cơ sở dữ liệu – Chat System

> **Bản nháp ERD** (T3 17/11). Chốt cùng `script/create_db.sql` trước 12:00 T4 18/11.
> Góp ý: comment vào Pull Request hoặc nhắn Nhân.

Phụ trách: Nguyễn Trần Trung Nhân (24127476)

![ERD](erd.png)

- Ảnh: [`erd.png`](erd.png) (dán vào báo cáo), [`erd.svg`](erd.svg) (bản vector)
- Mã nguồn sơ đồ: [`erd.mmd`](erd.mmd) (Mermaid). Sửa file này rồi xuất lại ảnh bằng [mermaid.live](https://mermaid.live).
- Script tạo CSDL: [`script/create_db.sql`](../../script/create_db.sql), đã chạy thử trên MySQL 8.4.

Schema thiết kế cho cả **Phiên bản 2** (mã hoá nhóm, thống kê, xoá lịch sử phía mình), để sau này không phải sửa bảng.

## Danh sách bảng

| # | Bảng | Lưu gì | Dùng cho màn hình |
| --- | --- | --- | --- |
| 1 | `users` | Tài khoản (cả user và admin): tên đăng nhập, mật khẩu đã băm, họ tên, địa chỉ, ngày sinh, giới tính, email, vai trò, trạng thái khoá, online, ngày đăng ký | Đăng ký, đăng nhập, tài khoản, Quản lý user, DS đăng ký mới, biểu đồ đăng ký |
| 2 | `login_history` | Mỗi lần đăng nhập thành công | Lịch sử đăng nhập của 1 user, DS đăng nhập (admin) |
| 3 | `app_sessions` | Mỗi lần mở ứng dụng (mở, đóng) | DS user hoạt động, biểu đồ số người hoạt động |
| 4 | `friendships` | Quan hệ bạn bè, **mỗi quan hệ 2 dòng** (A,B) và (B,A) | DS bạn bè, bạn online, DS user + số bạn bè |
| 5 | `friend_requests` | Lời mời kết bạn đang chờ | DS lời mời kết bạn, Tìm người |
| 6 | `blocks` | Ai chặn ai | Nút Chặn, lọc tin nhắn / lời mời |
| 7 | `conversations` | Hội thoại: `PRIVATE` (2 người) hoặc `GROUP` (tên, có mã hoá không, phiên bản khoá) | Khung chat, DS nhóm chat (admin) |
| 8 | `conversation_members` | Thành viên hội thoại, vai trò `ADMIN`/`MEMBER`, mốc xoá lịch sử | Tạo / quản lý nhóm, DS thành viên, DS admin nhóm |
| 9 | `messages` | Tin nhắn (bản rõ, hoặc bản mã nếu nhóm mã hoá) | Khung chat, tìm trong cuộc chat, tìm toàn bộ tin nhắn |
| 10 | `message_hidden` | Tin nhắn mà 1 người đã xoá phía mình | Xoá 1 tin nhắn |
| 11 | `spam_reports` | Báo cáo spam: người báo cáo, người bị báo cáo, lý do, trạng thái | Báo cáo spam, DS báo cáo spam (admin) |
| 12 | `group_keys` | Khoá AES của nhóm, mã hoá riêng cho từng thành viên | Nhóm mã hoá đầu cuối |
| – | `v_user_friend_counts` (view) | Số bạn trực tiếp và số người gồm cả bạn của bạn | DS user + số bạn bè (admin) |

## Các quyết định thiết kế

- **Bạn bè lưu 2 chiều.** Kết bạn thì thêm 2 dòng, huỷ thì xoá 2 dòng (trong 1 transaction). Truy vấn "bạn của A" chỉ cần `WHERE user_id = A`, và đếm bạn của bạn bằng 1 phép JOIN.
- **Lời mời chỉ lưu khi đang chờ.** Đồng ý thì thêm vào `friendships` rồi xoá lời mời, từ chối thì xoá lời mời.
- **Chat riêng và chat nhóm dùng chung** `conversations` + `messages`, phân biệt bằng `type`. Khung chat chỉ cần 1 màn hình.
- **Xoá tin phía mình không ảnh hưởng người khác:**
  - Xoá 1 tin: thêm 1 dòng `message_hidden`.
  - Xoá toàn bộ lịch sử: đặt `conversation_members.cleared_at = NOW()`, tin gửi trước mốc này bị ẩn với người đó.
- **Mã hoá nhóm.** Mỗi user có `public_key`. Nhóm có 1 khoá AES, mã hoá bằng public key của từng thành viên và lưu vào `group_keys`. Tin nhắn lưu bản mã kèm `iv` và `key_version`. Thêm hoặc xoá thành viên thì tăng `conversations.key_version` và tạo khoá mới. Thành viên bị xoá khỏi nhóm thì khoá của người đó tự xoá theo (khoá ngoại tới `conversation_members`).
- **Xoá user (admin).** Dữ liệu riêng của user xoá theo (CASCADE). Tin nhắn trong nhóm vẫn giữ lại, chỉ có `sender_id` thành `NULL` (hiển thị "Người dùng đã bị xoá").
- **Ràng buộc kiểm tra ở tầng Java:** không tự kết bạn, chặn hay báo cáo chính mình; chat riêng đúng 2 thành viên; nhóm có ít nhất 1 admin. Lý do: MySQL không cho `CHECK` trên cột khoá ngoại có `ON DELETE CASCADE / SET NULL`.
- **Giá trị ENUM viết tiếng Anh, viết hoa** (`MALE`, `PENDING`...). Giao diện đổi sang tiếng Việt khi hiển thị.
- **Collation `utf8mb4_0900_ai_ci`:** tìm "nhan" ra được "Nhân", không phân biệt hoa thường.

## Truy vấn mẫu cho các màn thống kê (đã chạy thử)

```sql
-- Số người đăng ký mới theo tháng trong năm
SELECT MONTH(created_at) AS thang, COUNT(*) AS so_luong
FROM users WHERE YEAR(created_at) = 2026 GROUP BY thang;

-- Số người hoạt động (có mở ứng dụng) theo tháng trong năm
SELECT MONTH(opened_at) AS thang, COUNT(DISTINCT user_id) AS so_luong
FROM app_sessions WHERE YEAR(opened_at) = 2026 GROUP BY thang;

-- DS user hoạt động trong khoảng thời gian: số lần mở app, số người đã chat, số nhóm đã chat
SELECT u.username,
  (SELECT COUNT(*) FROM app_sessions s
     WHERE s.user_id = u.id AND s.opened_at BETWEEN :tu AND :den) AS so_lan_mo_app,
  (SELECT COUNT(DISTINCT m.conversation_id) FROM messages m JOIN conversations c ON c.id = m.conversation_id
     WHERE m.sender_id = u.id AND c.type = 'PRIVATE' AND m.sent_at BETWEEN :tu AND :den) AS so_nguoi_da_chat,
  (SELECT COUNT(DISTINCT m.conversation_id) FROM messages m JOIN conversations c ON c.id = m.conversation_id
     WHERE m.sender_id = u.id AND c.type = 'GROUP' AND m.sent_at BETWEEN :tu AND :den) AS so_nhom_da_chat
FROM users u;

-- Tin nhắn user :me nhìn thấy trong 1 cuộc chat (đã trừ tin bị xoá phía mình)
SELECT m.* FROM messages m
JOIN conversation_members cm ON cm.conversation_id = m.conversation_id AND cm.user_id = :me
WHERE m.conversation_id = :conv
  AND (cm.cleared_at IS NULL OR m.sent_at > cm.cleared_at)
  AND NOT EXISTS (SELECT 1 FROM message_hidden h WHERE h.user_id = :me AND h.message_id = m.id)
ORDER BY m.sent_at;

-- DS user + số bạn bè
SELECT * FROM v_user_friend_counts WHERE direct_friends > 3;
```
