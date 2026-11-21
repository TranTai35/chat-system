# Nhật ký sử dụng AI – Đồ án Java, Hệ thống Chat (Phiên bản 1)

Nhóm 10, lớp 24KTPM2: Trần Phước Tài (24127119), Nguyễn Đặng Hoàng Khang (24127410), Nguyễn Trần Trung Nhân (24127476).

Mỗi thành viên tự ghi phần của mình. Phần của Khang và Nhân do chính hai bạn điền.

## 1. Trần Phước Tài (24127119)

**Công cụ:** Claude (Anthropic), dùng qua giao diện chat/agent. Không dùng công cụ AI nào khác cho đồ án.

### Việc đã dùng AI hỗ trợ

| Ngày | Việc | Cách dùng AI | Tài kiểm tra và quyết định |
| --- | --- | --- | --- |
| 16–17/11 | Khung Maven, component dùng chung (Theme, BaseTablePanel, BaseDialog, FormPanel, SidebarMenu), 5 dialog của màn Quản lý người dùng | Claude viết bản nháp mã và gợi ý cấu trúc | Tài chạy thử, chỉnh quy ước giao diện (`docs/DESIGN.md`) và tự commit |
| 18/11 | Màn Đăng nhập, Đăng ký, Quên mật khẩu; Thông tin tài khoản, Đổi mật khẩu | Claude viết mã giao diện | Tài chụp màn hình thấy bố cục lệch, yêu cầu sửa cho đối xứng; Claude sửa lại; Tài chạy thử rồi commit |
| 19/11 | Tìm người, dialog Báo cáo spam, Tạo nhóm, Quản lý nhóm; nối các dialog vào khung chat và màn Bạn bè đang online | Claude viết mã, đọc mã của Nhân để nối đúng chỗ | Tài chạy ứng dụng, phát hiện chỗ chưa mở được dialog, kiểm tra trước khi merge |
| 19–20/11 | Đối chiếu công việc của Khang và Nhân với kế hoạch, viết nội dung issue | Claude đọc nhánh và mô tả PR, soạn nội dung issue | Tài tự tạo issue, tự merge pull request |
| 20/11 | Chạy thử hai ứng dụng, sửa số liệu giả của 2 biểu đồ cho khớp `sample_data.sql` | Claude chạy kiểm thử tự động, đề xuất số liệu | Tài phát hiện biểu đồ không khớp danh sách người dùng mới; Tài kiểm tra bằng truy vấn trên MySQL rồi commit |
| 20–21/11 | Khung báo cáo và file Word | Claude dựng nội dung từ tài liệu của cả nhóm và dựng file theo mẫu | Tài đọc lại, sửa tên giảng viên, tên nhóm, tự chụp ảnh màn hình của mình |

### Cách kiểm soát

- Mọi commit và merge do Tài tự thực hiện trên máy của mình. Vì vậy lịch sử git không có dòng đồng tác giả của AI.
- Từ tối 20/11, repository được gắn vào phiên làm việc với AI với quyền đọc và ghi để AI đọc issue và pull request. AI chỉ đọc; không tạo, sửa hay đóng issue, không push, không merge. Mọi issue do Tài tự tạo và tự đóng trên GitHub.
- Mọi đoạn mã do AI viết đều được Tài chạy thử (`mvn compile exec:java`) trước khi commit.
- Các con số trong báo cáo (số bảng, số commit, số màn hình) được đối chiếu lại với repository.

### Giới hạn đã gặp

- AI từng nhận nhầm một số liệu khi tổng hợp (ví dụ báo sai số lần một người bị báo cáo spam); Tài đối chiếu với `sample_data.sql` và yêu cầu sửa.
- Ảnh chụp 9 màn hình của Tài trong báo cáo do Tài tự chụp từ ứng dụng đang chạy, không do AI tạo. Ảnh màn hình của Khang và Nhân xem phần ghi của hai bạn.

## 2. Nguyễn Đặng Hoàng Khang (24127410)

**Công cụ:** Claude (Anthropic), dùng qua dự án Claude trên claude.ai; Claude chạy trong môi trường đám mây, được đọc và ghi repository của nhóm qua tài khoản GitHub của Khang. Không dùng công cụ AI nào khác cho đồ án.

### Việc đã dùng AI hỗ trợ

| Ngày | Việc | Cách dùng AI | Khang kiểm tra và quyết định |
| --- | --- | --- | --- |
| 17/11 | Wireframe 2 phân hệ; 8 màn admin (A2–A9) và 2 dialog nhóm, gắn vào menu | Claude viết wireframe và mã giao diện theo `docs/DESIGN.md`, `BaseTablePanel`; tạo commit đứng tên Khang và mở PR #7 | Khang yêu cầu đặt tên nhánh `feature/...` theo quy ước nhóm (PR đầu bị đóng, mở lại thành PR #7); Tài review và merge |
| 18/11 | Đối chiếu A3, A4 với `create_db.sql` | Claude thêm cột Người tạo (A3), Lý do và Xử lý (A4) | Nhóm thống nhất giao diện phải bám wireframe, nên Khang tự revert 2 commit này trên máy mình, tự gắn nút Xem thành viên / Xem admin vào dialog và thêm ảnh màn hình, rồi push (PR #11) |
| 19/11 | Rà A5, A6, A9 với wireframe và CSDL | Claude kiểm tra, ghi chú truy vấn dự kiến cho Phiên bản 2 (Javadoc), tạo commit và PR #24 | Tài review và merge |
| 20–21/11 | Rà A7, A8; đổi tên menu "Lịch sử đăng nhập" thành "Danh sách đăng nhập" theo issue #28 | Claude soạn bản vá (patch) và hướng dẫn từng lệnh git | Khang tự áp bản vá, tự commit và push trên máy mình; Claude tạo PR #30, #31 giúp vì trình duyệt máy Khang chưa đăng nhập GitHub |
| 21/11 | Ảnh chụp 9 màn admin cho báo cáo | Ảnh được dựng bằng chương trình chạy chính mã giao diện của ứng dụng (FlatLaf) trên môi trường của Claude | Khang thay ảnh vào `docs/screens/admin/`, tự commit; ảnh git log do Khang tự chụp trên máy mình |
| 21/11 | Gom bài gửi Tài, rà báo cáo, điền nhật ký này | Claude gom ảnh, mô tả, link; đọc bản PDF báo cáo; soạn nội dung phần này | Khang đọc lại trước khi gửi Tài |

### Cách kiểm soát

- Lịch sử git cho thấy rõ phần nào do AI tạo commit: 12 commit ngày 17–19/11 dùng email `...@users.noreply.github.com`, trong đó 11 commit có dòng `Co-Authored-By: Claude`. 8 commit còn lại (18/11 và 21/11) dùng email `ndhkhang2429@clc.fitus.edu.vn`, do Khang tự commit trên máy mình.
- Merge vào `main` do Tài thực hiện sau khi review.
- Tên nhánh, việc bỏ các cột không có trong wireframe, và nội dung gửi Tài do Khang quyết định.

### Giới hạn đã gặp

- AI tự thêm cột cho khớp CSDL (18/11) nhưng làm giao diện lệch wireframe đã chốt; Khang phát hiện và revert.
- AI từng đặt tên nhánh `claude/...` không đúng quy ước nhóm; phải đóng PR và mở lại trên nhánh `feature/...`.
- Ảnh màn hình admin không phải chụp trực tiếp trên máy Khang mà do chương trình của AI dựng từ mã ứng dụng (xem dòng 21/11 ở trên).

## 3. Nguyễn Trần Trung Nhân (24127476)

**Công cụ:** Claude Code (Claude, Anthropic), chạy trong ứng dụng Claude trên máy của Nhân, được đọc và ghi thư mục repository, chạy lệnh và dùng GitHub CLI bằng tài khoản của Nhân. Không dùng công cụ AI nào khác cho đồ án.

### Việc đã dùng AI hỗ trợ

| Ngày | Việc | Cách dùng AI | Nhân kiểm tra và quyết định |
| --- | --- | --- | --- |
| 17/11 | Bản nháp ERD (Mermaid), `create_db.sql` | Claude đọc đề bài và kế hoạch nhóm, viết sơ đồ và script | Chạy thử script trên MySQL 8.4 (Docker). Thử nghiệm cho thấy FULLTEXT không tìm được chuỗi con nên đổi sang `LIKE`; thêm `last_read_at` để đếm tin chưa đọc |
| 18/11 | Màn Bạn bè, Lời mời kết bạn | Claude viết mã giao diện theo `docs/DESIGN.md` | Biên dịch, chạy thử, xem ảnh chụp màn hình rồi mới tạo PR |
| 19/11 | Màn Bạn bè đang online, khung chat (chat riêng, chat nhóm mã hoá) | Claude viết mã, tách renderer trạng thái dùng chung | Xem ảnh chụp, phát hiện bong bóng chat bị nền trắng và yêu cầu sửa trước khi tạo PR |
| 20/11 | Màn Tìm trong tin nhắn, `sample_data.sql`, xuất lại ảnh ERD | Claude viết mã và dữ liệu mẫu, viết truy vấn kiểm tra tính toàn vẹn | Chạy 2 script trên MySQL trống, các truy vấn kiểm tra trả về 0 lỗi; tin dài bị cắt nên yêu cầu hiện đoạn quanh từ khoá |
| 20/11 | Phần Thiết kế CSDL trong báo cáo, ảnh màn hình, ảnh git log | Claude soạn nội dung từ `create_db.sql` và `docs/erd/`; ảnh màn hình được dựng bằng chương trình chạy chính mã giao diện của ứng dụng (FlatLaf); ảnh git log dựng từ kết quả lệnh `git log` thật | Đối chiếu số bảng, số khoá ngoại, số commit với repository |
| 21/11 | Sửa 2 lỗi trong file Word theo issue #29 | Claude sửa file | Kiểm tra lại nội dung trước khi gửi Tài |

### Cách kiểm soát

- Mỗi việc đều bắt đầu từ issue Tài giao trên GitHub; Nhân giao việc cho AI và xem kết quả.
- Commit được tạo trên máy của Nhân, đứng tên Nhân, có thêm dòng `Co-Authored-By: Claude` để ghi rõ có AI hỗ trợ (cả 14 commit).
- Push, tạo pull request và comment trên GitHub đều được Nhân đồng ý trước khi AI thực hiện. Merge vào `main` do Tài làm.
- Mã được biên dịch và chạy thử; script SQL được chạy trên MySQL thật trước khi nộp.

### Giới hạn đã gặp

- Lúc đầu AI dùng FULLTEXT cho tìm kiếm; khi chạy thử thì không tìm được chuỗi con nên phải đổi sang `LIKE`.
- Ảnh ERD từng bị cũ sau khi Tài thêm cột `private_key`; phát hiện khi rà lại và đã xuất lại.
- Bản đầu của ảnh git log có một dòng dấu nhắc lệnh do AI tự thêm, không có trong kết quả thật; đã bỏ để ảnh đúng với kết quả lệnh.
