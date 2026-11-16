# Hướng dẫn thiết kế giao diện – Chat System

Đọc file này trước khi làm màn hình mới. Mục tiêu: 3 người làm, nhưng ghép lại trông như **một người làm**.

**Quy tắc vàng:** không tự đặt màu, font, kích thước hay khoảng cách trong màn hình. Mọi thứ lấy từ `Theme`, và dựng màn hình bằng các lớp có sẵn trong `common.ui`. Thiếu thứ gì thì báo Tài để bổ sung vào `common.ui`, đừng tự viết riêng.

Màn hình mẫu để copy theo: `admin/ui/UserManagementPanel.java`.

---

## 1. Màu sắc (`Theme`)

| Hằng số | Mã màu | Dùng cho |
| --- | --- | --- |
| `PRIMARY` | `#2563EB` | Nút chính, link, tin nhắn của mình, mục đang chọn |
| `PRIMARY_DARK` | `#1D4ED8` | Viền/hover của nút chính |
| `DANGER` | `#DC2626` | Nút Xoá, Khoá, Block, Báo cáo spam; chữ "Bị khoá" |
| `SUCCESS` / `ONLINE` | `#16A34A` | Chấm online, chữ "Hoạt động", thông báo thành công |
| `WARNING` | `#D97706` | Cảnh báo, biểu tượng nhóm đã mã hoá |
| `OFFLINE` | `#9CA3AF` | Chấm offline |
| `TEXT` | `#111827` | Chữ chính |
| `MUTED_TEXT` | `#6B7280` | Chữ phụ: mô tả, thời gian, ghi chú, số dòng |
| `BORDER` | `#E5E7EB` | Viền bảng, viền thẻ, đường kẻ |
| `BACKGROUND` | `#F9FAFB` | Nền vùng nội dung |
| `SURFACE` | `#FFFFFF` | Nền thẻ, dialog, ô chat |
| `SIDEBAR_BG` / `SIDEBAR_ACTIVE` | `#111827` / `#1F2937` | Menu bên trái (đã có trong `SidebarMenu`) |
| `BUBBLE_ME` + `BUBBLE_ME_TEXT` | `#2563EB` + trắng | Bong bóng tin nhắn của mình |
| `BUBBLE_OTHER` + `BUBBLE_OTHER_TEXT` | `#F3F4F6` + `#111827` | Bong bóng tin nhắn người khác |
| `HIGHLIGHT` | `#FEF08A` | Tô vàng kết quả tìm kiếm trong lịch sử chat |

Chỉ có **1 màu nhấn** là xanh `PRIMARY`. Đỏ chỉ dùng cho hành động nguy hiểm, xanh lá chỉ cho trạng thái tốt. Không thêm màu mới.

## 2. Chữ (`Theme`)

Font chung: **Segoe UI**.

| Hằng số | Cỡ | Dùng cho | Tạo nhanh |
| --- | --- | --- | --- |
| `FONT_TITLE` | 20 đậm | Tiêu đề màn hình (1 cái mỗi màn) | `UiUtil.title("...")` |
| `FONT_HEADING` | 16 đậm | Tiêu đề dialog, tiêu đề khối trong màn | `UiUtil.heading("...")` |
| `FONT_BODY` | 14 | Chữ thường, ô nhập, bảng | mặc định |
| `FONT_BOLD` | 14 đậm | Tên người trong danh sách, header bảng | |
| `FONT_SMALL` | 12 | Mô tả, thời gian tin nhắn, ghi chú | `UiUtil.muted("...")` |

Cách viết chữ trên giao diện:
- Tiếng Việt có dấu, viết hoa chữ đầu câu: "Quản lý người dùng", không viết "QUẢN LÝ NGƯỜI DÙNG" hay "Quản Lý Người Dùng".
- Nút là động từ ngắn: **Thêm, Sửa, Xoá, Lưu, Huỷ, Gửi, Kết bạn, Chặn**. Nút thêm mới có dấu cộng: "+ Thêm", "+ Tạo nhóm".
- Thống nhất chữ: dùng **"Xoá"** (không phải "Xóa"), **"Khoá"**, **"Huỷ"**; **"Tên đăng nhập"** (không phải "Username"); **"Họ tên"**; **"Ngày tạo"**.
- Ngày hiển thị dạng `yyyy-MM-dd`, ngày giờ dạng `yyyy-MM-dd HH:mm`.

## 3. Khoảng cách và kích thước

| Hằng số | Giá trị | Dùng cho |
| --- | --- | --- |
| `GAP` | 8 px | Giữa các nút, giữa nhãn và ô nhập |
| `PADDING` | 16 px | Khoảng cách giữa các khối; padding thẻ |
| `PADDING + 4` | 20 px | Lề ngoài của màn hình và dialog |
| `ROW_HEIGHT` | 30 px | Chiều cao dòng bảng (đã đặt sẵn) |
| `MAIN_WINDOW` | 1280 × 760 | Cửa sổ chính của cả 2 phân hệ |
| `AUTH_WINDOW` | 440 × 560 | Cửa sổ đăng nhập / đăng ký / quên mật khẩu |
| `DIALOG_WIDTH` | 480 px | Chiều rộng tối thiểu của dialog |

Chỉ dùng các bội số 4/8/16/20. Không dùng 5, 10, 15, 25...

## 4. Các khối có sẵn trong `common.ui`

| Lớp | Khi nào dùng |
| --- | --- |
| `BaseTablePanel` | **Mọi màn hình dạng danh sách** (gần hết phân hệ admin, DS bạn bè, lời mời). Kế thừa lớp này. |
| `BaseDialog` | **Mọi dialog**: thêm/sửa, đổi mật khẩu, tạo nhóm, quản lý nhóm, báo cáo spam, xem lịch sử... |
| `FormPanel` | Mọi form nhãn / ô nhập (đặt trong dialog hoặc màn đăng ký/profile). |
| `SidebarMenu` | Menu trái của 2 cửa sổ chính. Thành viên không cần đụng tới. |
| `UiUtil` | Tạo tiêu đề, nút, ô nhập có placeholder, thẻ trắng có viền. |
| `PlaceholderPanel` | Màn hình tạm trong menu. Làm xong thì thay bằng màn thật. |

### Nút

| Loại | Tạo bằng | Dùng khi | Số lượng |
| --- | --- | --- | --- |
| Nút chính | `UiUtil.primaryButton("Lưu")` | Hành động chính của màn/dialog | **Tối đa 1** mỗi màn/dialog |
| Nút nguy hiểm | `UiUtil.dangerButton("Xoá")` | Xoá, khoá, chặn, báo cáo | Khi cần |
| Nút thường | `UiUtil.button("Sửa")` | Mọi hành động còn lại | Không giới hạn |

Thứ tự nút trên thanh công cụ (từ trái sang phải): nút thường → nút nguy hiểm → **nút chính ở ngoài cùng bên phải**. Trong dialog: **Huỷ** bên trái, **nút chính** bên phải (`BaseDialog` đã làm sẵn).

### Ô nhập

- Luôn dùng `UiUtil.textField("gợi ý", cột)` / `UiUtil.passwordField(...)` để có chữ gợi ý (placeholder).
- Lựa chọn cố định (giới tính, trạng thái, toán tử =, <, >) dùng `JComboBox`.
- Chọn ngày: tạm dùng `UiUtil.textField("yyyy-MM-dd", 10)`.

## 5. Bố cục từng loại màn hình

### 5.1 Màn danh sách (admin, DS bạn bè, lời mời)

```
┌──────────────────────────────────────────────────────────────┐
│ Tiêu đề màn hình                 [Nút] [Nút] [Xoá] [+ Thêm]  │  ← hàng 1
│ [Tìm kiếm...] Bộ lọc: [▾]  [ô số]           Sắp xếp: [▾]     │  ← hàng 2
│ ┌──────────────────────────────────────────────────────────┐ │
│ │ Cột 1      │ Cột 2       │ Cột 3     │ ...               │ │
│ │ ...                                                       │ │
│ └──────────────────────────────────────────────────────────┘ │
│ 12 / 40 dòng                                                 │
└──────────────────────────────────────────────────────────────┘
```

```java
public class GroupListPanel extends BaseTablePanel {
    public GroupListPanel() {
        super("Danh sách nhóm chat", new String[]{"Tên nhóm", "Số thành viên", "Ngày tạo"});
        setSearchPlaceholder("Tìm theo tên nhóm");
        setSearchColumns(0);
        addSortOption("Tên (A-Z)", 0, SortOrder.ASCENDING);
        addSortOption("Mới tạo nhất", 2, SortOrder.DESCENDING);
        membersButton = addAction("Xem thành viên");
        adminsButton = addAction("Xem admin");
        addRow("Nhóm Java", 5, "2026-11-01");   // dữ liệu giả
    }
}
```

- Bộ lọc theo **khoảng thời gian**: 2 ô `textField("yyyy-MM-dd", 10)` với nhãn "Từ:" và "Đến:".
- Bộ lọc theo **số lượng** (=, <, >): `JComboBox` gồm `"=", "<", ">"` + `textField("Số", 5)`, nhãn "Số bạn:" hoặc "Số hoạt động:".
- Cột trạng thái ghi chữ: "Hoạt động" / "Bị khoá" / "Online" / "Offline".
- Mỗi bảng có **8–15 dòng dữ liệu giả** có dấu tiếng Việt, ngày trải đều nhiều tháng.

### 5.2 Dialog

```
┌──────────────────────────────────┐
│ Tiêu đề dialog                   │
│ Mô tả ngắn (chữ xám)             │
│                                  │
│ Nhãn 1        [ô nhập.........]  │
│ Nhãn 2        [ô nhập.........]  │
│                                  │
│                    [Huỷ] [Lưu]   │
└──────────────────────────────────┘
```

```java
public class UserFormDialog extends BaseDialog {
    public UserFormDialog(Window owner) {
        super(owner, "Thêm người dùng", "Nhập thông tin tài khoản mới");
        FormPanel form = new FormPanel();
        usernameField = form.addField("Tên đăng nhập", UiUtil.textField("vd: taitp", 20));
        genderBox = form.addField("Giới tính", new JComboBox<>(new String[]{"Nam", "Nữ", "Khác"}));
        setContent(form);
        setPrimaryText("Thêm");
        finish();
    }
}
```

- Dialog chỉ để xem (lịch sử đăng nhập, DS bạn bè, DS thành viên): đặt `BaseTablePanel` vào `setContent(...)`, rồi gọi `setViewOnly()`.
- Dialog xác nhận hành động nguy hiểm (xoá, khoá, chặn): gọi `setPrimaryDanger()` và đổi chữ nút thành đúng hành động, ví dụ "Xoá".
- Mở dialog: `new UserFormDialog(SwingUtilities.getWindowAncestor(this)).setVisible(true);`

### 5.3 Màn đăng nhập / đăng ký / quên mật khẩu

```
           ┌───────────────────────────┐
           │      Chat System          │  ← UiUtil.title, căn giữa
           │  Đăng nhập để tiếp tục    │  ← UiUtil.muted
           │                           │
           │  Tên đăng nhập            │
           │  [.....................]  │
           │  Mật khẩu                 │
           │  [.....................]  │
           │           Quên mật khẩu?  │  ← link chữ xanh
           │  [      Đăng nhập      ]  │  ← primaryButton, rộng hết khung
           │  Chưa có tài khoản? Đăng ký│
           └───────────────────────────┘
```

- `JFrame` kích thước `Theme.AUTH_WINDOW`, không cho resize, nền `BACKGROUND`.
- Ở giữa là 1 `UiUtil.card(...)` trắng, nhãn nằm **trên** ô nhập (không đặt bên trái như dialog).
- Link ("Quên mật khẩu?", "Đăng ký") là `JLabel` chữ `PRIMARY`, con trỏ hình bàn tay.

### 5.4 Màn chat

```
┌───────────────┬──────────────────────────────────────────────┐
│ [Tìm hội thoại]│ Tên người / nhóm   ● Online   [🔍] [⋮]      │ ← header 56px
│ ● Lan     10:21│──────────────────────────────────────────────│
│   Ok bạn...    │  ┌──────────────┐                            │
│ ○ Minh  Hôm qua│  │ tin người khác│  (BUBBLE_OTHER, bên trái) │
│ 🔒 Nhóm Java   │  └──────────────┘                            │
│               │                  ┌────────────────┐          │
│               │                  │ tin của mình   │ (BUBBLE_ME, phải)
│               │                  └────────────────┘ 10:22    │
│               │──────────────────────────────────────────────│
│ [+ Tạo nhóm]   │ [✨ Gợi ý] [ Nhập tin nhắn...      ] [Gửi]  │
└───────────────┴──────────────────────────────────────────────┘
   300px                     phần còn lại
```

- Danh sách hội thoại rộng 300 px. Mỗi dòng gồm: chấm `ONLINE`/`OFFLINE` (nhóm thì là biểu tượng nhóm, nhóm mã hoá thêm 🔒 màu `WARNING`), tên (`FONT_BOLD`), tin cuối và giờ (`FONT_SMALL`, `MUTED_TEXT`).
- Bong bóng tin nhắn bo góc 12 px, rộng tối đa 60% khung chat, padding 8×12. Giờ gửi dùng `FONT_SMALL` nằm dưới bong bóng. Trong nhóm, tên người gửi nằm trên bong bóng.
- Thanh tìm kiếm trong cuộc chat mở ngay dưới header, có nút ↑ ↓ để chuyển kết quả, tô kết quả bằng `HIGHLIGHT`.
- Nút LLM ("✨ Gợi ý") đặt bên trái ô nhập tin nhắn.
- Chuột phải vào tin nhắn hiện menu "Xoá tin nhắn". Menu ⋮ ở header gồm: Xoá toàn bộ lịch sử, Báo cáo spam, Chặn (chat riêng); Quản lý nhóm (chat nhóm).

### 5.5 Biểu đồ (admin)

- Dùng JFreeChart, kiểu **cột** (bar), trục hoành là 12 tháng (T1…T12), trục tung là số lượng.
- Cột màu `PRIMARY`, nền trắng, không hiệu ứng 3D, không gradient, không chú thích (legend) khi chỉ có 1 dãy số.
- Phía trên biểu đồ: tiêu đề màn + `JComboBox` chọn năm + nút chính "Xem".

## 6. Quy ước code giao diện

- Tên lớp theo chức năng + hậu tố: `...Panel` (màn trong menu), `...Dialog`, `...Frame` (cửa sổ riêng). Ví dụ: `GroupListPanel`, `CreateGroupDialog`, `LoginFrame`.
- Đặt đúng package: màn admin vào `admin.ui`, màn người dùng vào `user.ui`. Component dùng chung cho cả 2 phân hệ vào `common.ui` (báo nhóm trước khi thêm).
- Nút và ô nhập mà Phiên bản 2 cần xử lý sự kiện thì lưu thành **field + getter** (xem `UserManagementPanel`), chưa cần viết `addActionListener`.
- Dữ liệu giả để trong hàm `loadMockData()` riêng ở cuối lớp, để Phiên bản 2 thay bằng dữ liệu từ CSDL.
- Không dùng GUI builder (NetBeans form designer) vì sinh code khó đọc và khó merge. Dùng layout có sẵn: `BorderLayout`, `FlowLayout`, `BoxLayout`, `GridBagLayout` (qua `FormPanel`).
- Không dùng `setBounds` / `null` layout.

## 7. Checklist trước khi tạo Pull Request

- [ ] Không có `new Color(...)` hay `new Font(...)` nào trong màn hình của mình (tìm bằng Ctrl+Shift+F)
- [ ] Màn danh sách kế thừa `BaseTablePanel`, dialog kế thừa `BaseDialog`
- [ ] Chỉ có 1 nút chính (xanh) mỗi màn / dialog, nằm ngoài cùng bên phải
- [ ] Chữ trên giao diện có dấu, đúng các từ thống nhất ở mục 2
- [ ] Có dữ liệu giả đủ để chụp hình báo cáo
- [ ] Đã thay `PlaceholderPanel` tương ứng trong `AdminMainFrame` / `UserMainFrame`
- [ ] Chạy thử, kéo giãn cửa sổ không bị vỡ bố cục
- [ ] Chụp màn hình lưu sẵn vào thư mục riêng để làm báo cáo
