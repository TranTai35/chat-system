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
├── common/ui/        component dùng chung (Theme, BaseTablePanel, SidebarMenu, FormPanel, UiUtil)
└── model/            lớp dữ liệu (User, Message, ...)
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
