# Truy vấn bạn bè và chat (Phiên bản 2)

Phụ trách: Nguyễn Trần Trung Nhân (24127476). Issue #36.

Các câu SQL dưới đây đã chạy thử trên MySQL 8.4 với `script/create_db.sql` + `script/sample_data.sql` (bản có cột `last_seen`, PR #38). Người đang đăng nhập trong ví dụ là `nhanntt` (id 4). Câu có thay đổi dữ liệu được chạy trong `START TRANSACTION ... ROLLBACK` để đếm số dòng mà không làm hỏng dữ liệu mẫu.

Trong Java các câu này nằm ở `dao/FriendDao` và `dao/MessageDao` (dùng dấu `?`), quy tắc của đề nằm ở `service/FriendService` và `service/ChatService`.

## Bạn bè

### 1. Danh sách bạn bè, lọc theo tên
`FriendDao.findFriends(userId, keyword)`. Bạn bè lưu 2 chiều nên chỉ cần `WHERE f.user_id = ?`.

```sql
SELECT u.id, u.username, u.full_name, u.is_online
  FROM friendships f
  JOIN users u ON u.id = f.friend_id
 WHERE f.user_id = 4
   AND ('' = '' OR u.username LIKE '%lan%' OR u.full_name LIKE '%lan%')
 ORDER BY u.full_name, u.username;
```

| Thử | Kết quả |
| --- | --- |
| Không lọc | 3 dòng: `minhlq`, `lanpt`, `taitp` |
| Lọc `lan` | 1 dòng: `lanpt` |
| Lọc `pham thi` (không dấu) | 1 dòng: `lanpt` (nhờ collation `ai_ci`) |

Trạng thái online không tự tính ở đây: dùng hàm chung `UserDao.isOnline(...)` của Tài (điều kiện `is_online = 1 AND last_seen >= NOW() - INTERVAL 3 MINUTE`).

### 2. Lời mời đang chờ gửi tới mình
`FriendDao.findIncomingRequests(receiverId)`, mới nhất trước.

```sql
SELECT u.id, u.username, u.full_name, u.is_online, r.receiver_id, r.created_at
  FROM friend_requests r
  JOIN users u ON u.id = r.sender_id
 WHERE r.receiver_id = 4
 ORDER BY r.created_at DESC;
```
Kết quả: 2 dòng, `myltk` (16/11 20:10) rồi `phucvh` (15/11 08:44).

### 3. Gửi lời mời
Trước khi gửi, service kiểm tra 3 điều (mỗi câu trả về 1 dòng hoặc 0 dòng):

```sql
-- đã là bạn?
SELECT 1 FROM friendships WHERE user_id = 4 AND friend_id = 11;
-- có ai chặn ai không (chiều nào cũng tính)?
SELECT 1 FROM blocks WHERE (blocker_id = 4 AND blocked_id = 11) OR (blocker_id = 11 AND blocked_id = 4);
-- đã có lời mời đang chờ? (kiểm tra cả 2 chiều, mỗi chiều 1 câu)
SELECT 1 FROM friend_requests WHERE sender_id = 4 AND receiver_id = 11;

INSERT INTO friend_requests (sender_id, receiver_id) VALUES (4, 11);
```

| Thử | Kết quả |
| --- | --- |
| `nhanntt` → `anhht` (11) | 3 câu kiểm tra đều 0 dòng; `INSERT` thêm 1 dòng |
| `minhlq` (6) → `myltk` (12) | câu kiểm tra chặn ra 1 dòng (`myltk` đã chặn `minhlq`), service báo lỗi |

### 4. Đồng ý lời mời (1 transaction)
```sql
START TRANSACTION;
DELETE FROM friend_requests WHERE sender_id = 12 AND receiver_id = 4;      -- 1 dòng (0 = lời mời không còn, báo lỗi)
INSERT INTO friendships (user_id, friend_id) VALUES (4, 12), (12, 4);      -- 2 dòng
COMMIT;
```
Sau khi đồng ý, `nhanntt` có 4 bạn (trước đó 3).

### 5. Từ chối lời mời
```sql
DELETE FROM friend_requests WHERE sender_id = 13 AND receiver_id = 4;      -- 1 dòng
```

### 6. Huỷ kết bạn
```sql
DELETE FROM friendships
 WHERE (user_id = 4 AND friend_id = 5) OR (user_id = 5 AND friend_id = 4);  -- 2 dòng
```

### 7. Chặn (huỷ kết bạn luôn, 1 transaction)
```sql
START TRANSACTION;
INSERT IGNORE INTO blocks (blocker_id, blocked_id) VALUES (4, 2);                          -- 1 dòng
DELETE FROM friendships
 WHERE (user_id = 4 AND friend_id = 2) OR (user_id = 2 AND friend_id = 4);                 -- 2 dòng
DELETE FROM friend_requests
 WHERE (sender_id = 4 AND receiver_id = 2) OR (sender_id = 2 AND receiver_id = 4);         -- 0 dòng (không có lời mời)
COMMIT;
```
Sau khi chặn, câu kiểm tra chặn ở mục 3 ra 1 dòng nên `taitp` không gửi lại lời mời được.

## Chat

### 8. Tìm chat riêng giữa 2 người
Chat riêng có `private_key = "<id nhỏ>_<id lớn>"` (UNIQUE), xem `Conversation.privateKeyOf(...)`.

```sql
SELECT id FROM conversations WHERE type = 'PRIVATE' AND private_key = '4_5';
```
Kết quả: 1 dòng, id 1 (chat `nhanntt` – `lanpt`). `4_11` (chưa chat với `anhht`): 0 dòng. Tạo chat mới làm ở đợt 3.

### 9. Lấy tin nhắn (lịch sử và tin mới theo `id > id_cuối`)
`MessageDao.findVisibleAfter(conversationId, viewerId, afterId)`. Lần đầu mở cuộc chat truyền `afterId = 0`; sau đó khung chat cứ khoảng 1 giây hỏi lại với id tin cuối đang có. JOIN `conversation_members` để người không phải thành viên nhận 0 dòng; bỏ tin đã xoá phía mình và tin trước mốc "Xoá toàn bộ lịch sử".

```sql
SELECT m.id, m.conversation_id, m.sender_id, m.content, m.iv, m.key_version, m.sent_at
  FROM messages m
  JOIN conversation_members cm ON cm.conversation_id = m.conversation_id AND cm.user_id = 4
 WHERE m.conversation_id = 1 AND m.id > 0
   AND (cm.cleared_at IS NULL OR m.sent_at > cm.cleared_at)
   AND NOT EXISTS (SELECT 1 FROM message_hidden h WHERE h.user_id = 4 AND h.message_id = m.id)
 ORDER BY m.id;
```

| Thử | Kết quả |
| --- | --- |
| Chat 1, `id > 0` | 8 dòng (toàn bộ lịch sử) |
| Chat 1, `id > 7` | 1 dòng (tin id 8 "Ok bạn, mai gửi nhé") |
| Nhóm "Lớp 24CTT3" (12), `nhanntt` đã xoá 1 tin phía mình | 6 / 7 tin |
| Chat 4, `huongvt` (7) đã xoá toàn bộ lịch sử | 0 / 2 tin |
| Chat 1, người ngoài `khangndh` (3) | 0 dòng |

Lấy tin theo `id` (tự tăng) chứ không theo `sent_at` để không sót tin khi 2 tin gửi cùng một mili giây.

### 10. Gửi tin
```sql
INSERT INTO messages (conversation_id, sender_id, content) VALUES (1, 4, 'Tin thử');   -- 1 dòng, trả về id mới
```
Tin nhóm mã hoá thì thêm `iv` và `key_version` (đợt 5).

## Quy tắc kiểm tra ở service (không ở CSDL)

| Quy tắc | Chỗ kiểm tra |
| --- | --- |
| Không tự kết bạn, không tự chặn mình, không nhắn cho chính mình | `FriendService.sendRequest`, `block`; `ChatService.findPrivateConversation` |
| Không gửi lời mời khi đã là bạn, đã có lời mời (chiều nào cũng vậy), hoặc bị chặn | `FriendService.sendRequest` |
| Chặn thì huỷ kết bạn và xoá lời mời giữa 2 người, trong 1 transaction | `FriendService.block` |
| Đồng ý lời mời: xoá lời mời + thêm 2 dòng bạn bè trong 1 transaction | `FriendService.acceptRequest` |
| Chỉ thành viên mới được đọc và gửi tin; tin không rỗng, tối đa 2000 ký tự | `ChatService.loadMessages`, `sendText` |

Đã thử các quy tắc trên bằng một chương trình nhỏ gọi thẳng `FriendService`, `ChatService` với dữ liệu mẫu: 34/34 ca đúng (gồm cả các ca phải báo lỗi).
