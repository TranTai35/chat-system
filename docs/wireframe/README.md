# Wireframe

Wireframe thô (low-fi) của 2 phân hệ – Khang vẽ. Chỉ thể hiện khung, tên trường, vị trí nút; màu và font thật theo [`docs/DESIGN.md`](../DESIGN.md).

- Nguồn: [`wireframe.html`](wireframe.html) – mở bằng trình duyệt để xem tất cả trên 1 trang, sửa trực tiếp trong file.
- Ảnh PNG từng màn: `user/` và `admin/`.
- Quy ước: nút nền xám đậm = nút chính (`PRIMARY`), nút viền đỏ nét đứt = nút nguy hiểm (`DANGER`), khung vàng = ghi chú cho người làm màn hình.

## Phân hệ người dùng (`user/`)

| Ảnh | Màn hình | Người làm |
| --- | --- | --- |
| 01-chat | Cửa sổ chính + khung chat (tìm trong chat, xoá tin, menu ⋮, gợi ý LLM, nhóm mã hoá) | Nhân |
| 02-create-group-dialog | Dialog tạo nhóm | Tài |
| 03-manage-group-dialog | Dialog quản lý nhóm (đổi tên, thêm/xoá thành viên, gán admin, mã hoá) | Tài |
| 04-login, 05-register, 06-forgot-password | Đăng nhập, Đăng ký, Quên mật khẩu | Tài |
| 07-profile, 08-change-password | Thông tin tài khoản, Đổi mật khẩu | Tài |
| 09-friends, 10-friend-requests, 11-online-friends | Bạn bè, Lời mời kết bạn, Bạn bè online | Nhân |
| 12-find-people | Tìm người | Tài |
| 13-search-messages | Tìm trong toàn bộ tin nhắn | Nhân |
| 14-spam-report-dialog | Dialog báo cáo spam | Tài |

## Phân hệ quản trị (`admin/`)

`00-list-template` là mẫu chung của mọi màn danh sách (`BaseTablePanel`). Mỗi ảnh còn lại có khung ghi chú cột, bộ lọc và nút riêng của màn đó.

| Ảnh | Màn hình | Lớp | Người làm |
| --- | --- | --- | --- |
| 01-user-management | Quản lý người dùng + các dialog | `UserManagementPanel` | Tài |
| 02-login-list | Danh sách đăng nhập | `LoginListPanel` | Khang |
| 03-group-list, 03b-group-members-dialog | Danh sách nhóm chat + dialog thành viên / admin | `GroupListPanel`, `GroupMembersDialog` | Khang |
| 04-spam-report-list | Danh sách báo cáo spam | `SpamReportListPanel` | Khang |
| 05-new-user-list | Người dùng đăng ký mới | `NewUserListPanel` | Khang |
| 06-registration-chart | Biểu đồ đăng ký mới theo năm | `RegistrationChartPanel` | Khang |
| 07-friend-count-list | Người dùng + số lượng bạn bè | `FriendCountListPanel` | Khang |
| 08-active-user-list | Người dùng hoạt động | `ActiveUserListPanel` | Khang |
| 09-activity-chart | Biểu đồ hoạt động theo năm | `ActivityChartPanel` | Khang |
