# Chat System – Đồ án cuối kỳ Lập trình ứng dụng Java

| MSSV | Họ tên |
| --- | --- |
| 24127119 | Trần Phước Tài |
| 24127410 | Nguyễn Đặng Hoàng Khang |
| 24127476 | Nguyễn Trần Trung Nhân |

## Công nghệ
Java 17+, Swing + FlatLaf, Maven, MySQL, JFreeChart.

## Chạy thử
Mở project bằng IntelliJ / NetBeans / VS Code (mở thư mục có `pom.xml`), rồi chạy:
- Phân hệ quản trị: `com.chatsystem.admin.AdminApp`
- Phân hệ người dùng: `com.chatsystem.user.UserApp`

Hoặc dùng dòng lệnh:
```
mvn compile exec:java                                              # người dùng
mvn compile exec:java -Dexec.mainClass=com.chatsystem.admin.AdminApp   # quản trị
```

## Cấu trúc thư mục
```
src/main/java/com/chatsystem/
├── admin/            AdminApp (main) + ui/ các màn hình quản trị
├── user/             UserApp (main)  + ui/ các màn hình người dùng
├── common/ui/        component dùng chung (Theme, BaseTablePanel, SidebarMenu, FormPanel, UiUtil, Async)
├── common/db/        Db (kết nối + truy vấn MySQL), DbException, DbCheck
├── common/config/    AppConfig (đọc config.properties)
├── service/          nghiệp vụ: quy tắc của đề (AuthService mẫu, ServiceException)
├── dao/              truy cập CSDL: SQL (UserDao mẫu)
└── model/            lớp dữ liệu (User, Conversation, Message, ...)
script/               script tạo CSDL (create_db.sql, sample_data.sql)
docs/wireframe/       wireframe các màn hình
git/                  ảnh chụp commit của từng thành viên (lúc nộp bài)
```

## Component dùng chung (`common.ui`)
| Lớp | Dùng để |
| --- | --- |
| `Theme` | Màu, font, kích thước chung. Gọi `Theme.setup()` đầu `main()`. |
| `UiUtil` | Tạo nhanh tiêu đề, nút chính/nút xoá, ô nhập có placeholder, khung thẻ. |
| `BaseTablePanel` | Màn hình danh sách: tiêu đề + ô tìm + bộ lọc + sắp xếp + nút + bảng. Kế thừa lớp này cho các màn danh sách. Xem `admin/ui/UserManagementPanel` làm mẫu. |
| `SidebarMenu` | Menu dọc + vùng nội dung CardLayout, dùng trong 2 MainFrame. |
| `FormPanel` | Form 2 cột nhãn / ô nhập cho dialog và màn đăng ký, profile. |
| `PlaceholderPanel` | Màn hình tạm giữ chỗ trong menu. |

## Hướng dẫn thiết kế
**Đọc [`docs/DESIGN.md`](docs/DESIGN.md) trước khi làm màn hình mới.** File này quy định màu, font, khoảng cách, bố cục từng loại màn hình và checklist trước khi tạo PR.

## Quy ước làm việc
- Mỗi người làm trên branch `feature/<ten>-<man-hinh>`, tạo Pull Request vào `main`. Merge mỗi tối trước 22:00.
- Mỗi màn hình là 1 lớp `JPanel` (hoặc `JDialog`) riêng, đặt tên theo chức năng: `GroupListPanel`, `CreateGroupDialog`...
- Làm xong màn hình thì thay `PlaceholderPanel` tương ứng trong `AdminMainFrame` / `UserMainFrame`.
- Không tự đặt mã màu / font trong màn hình, lấy từ `Theme`.
- Phiên bản 1 chỉ cần giao diện + dữ liệu giả. Nút nào cần xử lý sự kiện ở Phiên bản 2 thì tạo getter cho nút đó.
- Commit ít nhất 1 lần mỗi ngày.

## Phiên bản 2: kết nối CSDL, cấu hình và đóng gói
**Cài CSDL** (một lần, cần MySQL 8.0.16 trở lên):
```
mysql -u root -p --default-character-set=utf8mb4 -e "source script/create_db.sql"
mysql -u root -p --default-character-set=utf8mb4 chat_system -e "source script/sample_data.sql"
```

**Cấu hình:** sửa `config.properties` (host, port, user MySQL...). Mật khẩu MySQL của riêng bạn ghi vào `config.local.properties` (cùng thư mục, đã nằm trong `.gitignore`), ví dụ một dòng `db.password=mat_khau_cua_ban`. File local ghi đè file chung.
Thử kết nối: `mvn compile exec:java -Dexec.mainClass=com.chatsystem.common.db.DbCheck`

**Kiến trúc 3 lớp (Phiên bản 2).** Mỗi lớp chỉ gọi lớp ngay bên dưới:
```
Màn hình (admin.ui, user.ui)  ->  Service (service)  ->  DAO (dao)  ->  Db  ->  MySQL
                         dùng chung: model (User, Conversation, Message)
```
| Lớp | Làm gì | Không được làm |
| --- | --- | --- |
| Màn hình | Hiện dữ liệu, bắt sự kiện bấm nút, hiện thông báo lỗi | Gọi DAO trực tiếp, viết SQL, tự kiểm tra quy tắc của đề |
| Service | **Quy tắc của đề**: kiểm tra dữ liệu, quyền, điều kiện; ghép nhiều lời gọi DAO; ném `ServiceException` (tiếng Việt) khi vi phạm | Dùng Swing (JOptionPane, JPanel...), viết SQL |
| DAO | Đọc ghi CSDL bằng SQL (`PreparedStatement`) qua `Db`, đổi `ResultSet` thành model | Quyết định quy tắc nghiệp vụ, dùng Swing |

Ví dụ quy tắc nằm ở service: chỉ admin nhóm mới được xoá thành viên; người đã chặn mình không hiện trong Tìm người; chặn một người thì huỷ kết bạn luôn (trong 1 transaction); tài khoản bị khoá không đăng nhập được.
Màn chỉ hiển thị danh sách mà không có quy tắc thì service chỉ chuyển lời gọi sang DAO, vẫn đi qua service để thống nhất.
Mẫu: `service/AuthService` (kèm chú thích cách viết), `dao/UserDao`. Mỗi người đặt tên service theo mảng của mình: Tài `AuthService`, `UserService`; Khang `GroupService`, `StatsService`, `LlmService`; Nhân `FriendService`, `ChatService`, `GroupKeyService`.

**Viết code truy vấn CSDL** (xem `UserDao` làm mẫu):
- Mọi truy vấn đi qua `common.db.Db`, viết trong lớp `XxxDao` ở package `dao`. Không viết SQL trong lớp màn hình hoặc service, không tự mở kết nối.
- Luôn dùng dấu `?` và `PreparedStatement`, không cộng chuỗi SQL.
- Từ màn hình, gọi **service** (không gọi DAO) qua `common.ui.Async.run(...)` để cửa sổ không bị đứng:
  ```java
  Async.run(() -> authService.login(name, pass, User.Role.USER), user -> { /* mở màn chính */ },
            err -> { /* hiện err.getMessage() */ });
  ```
  Không truyền lambda thứ ba thì lỗi (`ServiceException`, `DbException`) tự hiện thành hộp thoại.
- Lớp model đặt trong `model` (`User`, `Conversation`, `Message`). Thiếu model thì thêm cùng kiểu.
- Cột `id` mới thêm hoặc thêm bảng thì nói với Nhân để cập nhật `create_db.sql` và ERD.

**Đóng gói bản nộp:** `mvn clean package` tạo `release/chat-user.jar`, `release/chat-admin.jar` và chép `config.properties` vào `release/`. Chạy: `java -jar release/chat-user.jar` (cần `config.properties` cạnh file jar). Các file `.jar` không đưa lên GitHub.
