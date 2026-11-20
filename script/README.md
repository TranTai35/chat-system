# Script CSDL

Nhân phụ trách:
- `create_db.sql`: tạo CSDL `chat_system`, bảng, khoá, ràng buộc, index, view (đã có)
- `sample_data.sql`: dữ liệu mẫu, 22 tài khoản (1 admin), bạn bè, lời mời, chặn, 16 hội thoại
  (2 nhóm mã hoá), 60 tin nhắn, báo cáo spam, lịch sử đăng nhập và lượt mở ứng dụng từ 11/2025 đến 11/2026

## Tài khoản mẫu

| Tên đăng nhập | Mật khẩu | Ghi chú |
| --- | --- | --- |
| `admin` | `admin123` | Phân hệ quản trị |
| `nhanntt`, `taitp`, `khangndh`, `lanpt`... | `123456` | User thường |
| `minhlq`, `trangnh` | `123456` | Đã bị khoá (bị báo cáo spam nhiều lần) |
| `quynhnt` | `123456` | Chưa có bạn bè |

Mật khẩu lưu dạng SHA-256 (`SHA2('123456', 256)`). Nếu Phiên bản 2 đổi cách băm thì cập nhật lại `sample_data.sql`.

Thiết kế và ý nghĩa từng bảng: [`docs/erd/README.md`](../docs/erd/README.md).

## Chạy

Cần MySQL **8.0.16 trở lên**. Script **xoá CSDL `chat_system` cũ** rồi tạo lại.

```
mysql -u root -p < script/create_db.sql
mysql -u root -p chat_system < script/sample_data.sql
```

Dùng MySQL Workbench: File → Open SQL Script → chọn file → bấm ⚡ (Execute).

Không cài MySQL mà có Docker:
```
docker run -d --name chat-mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=root mysql:8.4
docker exec -i chat-mysql mysql -uroot -proot < script/create_db.sql
docker exec -i chat-mysql mysql -uroot -proot chat_system < script/sample_data.sql
```
