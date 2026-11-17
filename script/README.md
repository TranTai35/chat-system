# Script CSDL

Nhân phụ trách:
- `create_db.sql`: tạo CSDL `chat_system`, bảng, khoá, ràng buộc, index, view (đã có)
- `sample_data.sql`: dữ liệu mẫu (làm T6 20/11)

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
```
