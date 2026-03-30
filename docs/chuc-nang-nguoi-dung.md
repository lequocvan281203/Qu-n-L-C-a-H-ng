# Chức năng người dùng (phục vụ viết luận)

Tài liệu này liệt kê chức năng theo **đối tượng người dùng** (role/actor) trong hệ thống quản lý cửa hàng.

## 1) Đối tượng người dùng trong hệ thống

### 1.1 Người dùng đăng nhập (User Accounts)
- **Admin (Quản trị)**: quản lý dữ liệu, cấu hình, thống kê, giám sát AI.
- **User (Nhân viên/Thu ngân)**: bán hàng, tra cứu, dùng AI để hỏi dữ liệu.

### 1.2 Người dùng nghiệp vụ (không đăng nhập)
- **Khách hàng (KhachHang)**: là đối tượng phát sinh hóa đơn và tích điểm; không có tài khoản đăng nhập trong phiên bản hiện tại.

## 2) Chức năng theo vai trò

### 2.1 Admin (Quản trị)

#### A. Chức năng hệ thống
- Đăng nhập/đăng xuất.
- Xem thông tin tài khoản đang đăng nhập (username/quyền).
- Tạo dữ liệu demo khi DB trống (sản phẩm/khách hàng/nhân viên).
- (Tuỳ môi trường) tự tạo database khi khởi động nếu chưa tồn tại.

#### B. Quản lý dữ liệu (CRUD)
- **Sản phẩm**: thêm/sửa/xóa/tìm kiếm theo mã hoặc tên; chỉnh tồn kho; gán loại sản phẩm/nhà cung cấp.
- **Khách hàng**: thêm/sửa/xóa/tìm kiếm theo mã/tên/số điện thoại; xem điểm tích lũy.
- **Loại sản phẩm**: thêm/sửa/xóa (mở rộng).
- **Nhà cung cấp**: thêm/sửa/xóa (mở rộng).
- **Nhân viên**: thêm/sửa/xóa (mở rộng).
- **Tài khoản**: tạo tài khoản, reset mật khẩu, phân quyền Admin/User, gán nhân viên (mở rộng).

#### C. Bán hàng / Hóa đơn
- Tạo hóa đơn bán hàng (POS): chọn sản phẩm + số lượng.
- Tự động trừ tồn kho sau khi thanh toán.
- Lưu hóa đơn và chi tiết hóa đơn.
- Tra cứu hóa đơn theo mã (mở rộng) và xem chi tiết.

#### D. Thống kê (JPQL)
- Thống kê tồn kho thấp (ngưỡng nhập).
- Thống kê top sản phẩm bán chạy (Top N).
- Thống kê doanh thu theo ngày (từ ngày – đến ngày).

#### E. AI (Text-to-SQL)
- Nhập câu hỏi tiếng Việt → hệ thống sinh SQL (chỉ cho phép SELECT/WITH) → thực thi và hiển thị kết quả dạng bảng.
- Tự động chặn các câu lệnh nguy hiểm (INSERT/UPDATE/DELETE/DROP...).
- Hỗ trợ tải model GGUF lần đầu (theo xác nhận của người dùng) hoặc chỉ định đường dẫn model.
- Hiển thị SQL sinh ra để kiểm tra minh bạch (phục vụ demo và kiểm soát).

### 2.2 User (Nhân viên/Thu ngân)

#### A. Chức năng cơ bản
- Đăng nhập/đăng xuất.
- **Bán hàng (POS)**:
  - Xem danh sách sản phẩm.
  - Chọn sản phẩm theo mã, nhập số lượng, thêm vào giỏ.
  - Tạo hóa đơn, tự tính tổng tiền.
  - Tự trừ tồn kho sau khi tạo hóa đơn.
- **Tra cứu**:
  - Tìm sản phẩm theo mã/tên (tìm kiếm).
  - Tra cứu hóa đơn theo mã (mở rộng) hoặc theo thời gian (mở rộng).

#### B. Chức năng AI (phục vụ tra cứu)
- Hỏi đáp nhanh dữ liệu cửa hàng bằng tiếng Việt (đọc dữ liệu):
  - “Số lượng sản phẩm trong kho”
  - “Danh sách sản phẩm sắp hết hàng”
  - “Top sản phẩm bán chạy”
  - “Doanh thu hôm nay/tuần này”
- AI trả kết quả dưới dạng bảng; chỉ chạy truy vấn SELECT nên không làm thay đổi dữ liệu.

### 2.3 Khách hàng (không đăng nhập)
- Xuất hiện trong dữ liệu hóa đơn: hóa đơn có thể gắn mã khách hàng.
- Có điểm tích lũy (phục vụ thống kê/CSKH, có thể mở rộng).

## 3) Liên kết với giao diện (MainForm/SubForm)
- Hệ thống ưu tiên mô hình **MainForm/SubForm**:
  - `MainForm`: cửa sổ chính (menu điều hướng).
  - `SubForm`: các form con cho từng nghiệp vụ (POS, AI, CRUD, thống kê...).

## 4) Gợi ý trình bày trong luận
- Mô tả các vai trò: Admin, User, Khách hàng.
- Với mỗi vai trò: nhóm chức năng (CRUD, POS, Thống kê, AI) và luồng thao tác.
- Nhấn mạnh “AI chỉ tạo SELECT” + cơ chế an toàn, giải thích cách AI hỗ trợ người dùng tra cứu.

