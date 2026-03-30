# Phần mềm Quản lý Cửa hàng Tiện lợi (Java 22, Maven, Hibernate, MySQL) + AI sinh truy vấn SQL

## Thông tin đề tài
- Môn: Lập trình Windows
- Giảng viên: Ths. Nguyễn Minh Đạo
- Nhóm:
  - 21110862 Lê Quốc Văn (Leader)
  - 21110116 Cao Nguyễn Thàn An

## Mục tiêu
- Quản lý danh mục, khách hàng, nhân viên, hóa đơn, chi tiết hóa đơn.
- Tích hợp AI sinh truy vấn SQL từ ngôn ngữ tự nhiên (GGUF + Llama.cpp).

## Kiến trúc & Công nghệ
- Ngôn ngữ: Java 22
- Build: Maven
- ORM: Hibernate 6 + JPA
- CSDL: MySQL
- Giao diện: (định hướng) FlatLaf
- AI: (định hướng) Llama.cpp với mô hình GGUF

## Cấu trúc chính
- Dự án: Qu-n-L-C-a-H-ng
  - Nguồn: [src/main/java](file:///d:/desktop/ltr%20win/Quan-ly-cua-hang/Qu-n-L-C-a-H-ng/src/main/java)
    - Entry: [MainApp.java](file:///d:/desktop/ltr%20win/Quan-ly-cua-hang/Qu-n-L-C-a-H-ng/src/main/java/com/cuahang/main/MainApp.java#L1-L19)
    - Hibernate util: [HibernateUtil.java](file:///d:/desktop/ltr%20win/Quan-ly-cua-hang/Qu-n-L-C-a-H-ng/src/main/java/com/cuahang/util/HibernateUtil.java#L1-L26)
    - Entities: [com.cuahang.entity.*](file:///d:/desktop/ltr%20win/Quan-ly-cua-hang/Qu-n-L-C-a-H-ng/src/main/java/com/cuahang/entity)
  - Cấu hình: [hibernate.cfg.xml](file:///d:/desktop/ltr%20win/Quan-ly-cua-hang/Qu-n-L-C-a-H-ng/src/main/resources/hibernate.cfg.xml)
  - Maven: [pom.xml](file:///d:/desktop/ltr%20win/Quan-ly-cua-hang/Qu-n-L-C-a-H-ng/pom.xml)

## Cài đặt & Chạy (PowerShell - Windows)
1. Chuẩn bị:
   - JDK 22, MySQL (tạo DB quanlycuahang, tài khoản root/123456).
2. Build:
   - cd "d:\\desktop\\ltr win\\Quan-ly-cua-hang\\Qu-n-L-C-a-H-ng"
   - .\\mvnw.cmd clean compile
3. Chạy:
   - .\\mvnw.cmd -Dexec.mainClass=com.cuahang.main.MainApp exec:java

## Lưu ý DB & tài khoản demo
- Nếu MySQL user có quyền, app sẽ tự tạo database `quanlycuahang` khi chạy lần đầu.
- Nếu bảng `TaiKhoan` trống, app sẽ tự tạo tài khoản mặc định: `admin / 123456`.

## AI GGUF (Text-to-SQL)
- Model đang dùng: `qwen2.5-1.5b-instruct-q5_k_m.gguf`
- Đặt file vào thư mục dự án: `Qu-n-L-C-a-H-ng/models/qwen2.5-1.5b-instruct-q5_k_m.gguf`
- Link tải: `https://huggingface.co/Qwen/Qwen2.5-1.5B-Instruct-GGUF/tree/main?show_file_info=qwen2.5-1.5b-instruct-q5_k_m.gguf`
- Mặc định app sẽ tìm file `.gguf` trong `models/` (trong thư mục dự án). Ngoài ra vẫn hỗ trợ `../models` và `_workspace/models/`.
- Hoặc chỉ định đường dẫn model bằng system property khi chạy:
  - `.\\mvnw.cmd -Dexec.mainClass=com.cuahang.main.MainApp -Dcuahang.modelPath="D:\\path\\to\\model.gguf" exec:java`

## Kế hoạch triển khai
- File chi tiết: [_workspace/plan/Kế hoạch Code Chi tiết.md](file:///d:/desktop/ltr%20win/Quan-ly-cua-hang/_workspace/plan/K%E1%BA%BF%20ho%E1%BA%A1ch%20Code%20Chi%20ti%E1%BA%BFt.md)
- Bao gồm: DAO/Service, UI (FlatLaf), tích hợp AI Text-to-SQL, test & build.

## Seed dữ liệu
- Tệp tham chiếu: [_workspace/seed.sql](file:///d:/desktop/ltr%20win/Quan-ly-cua-hang/_workspace/seed.sql)
- Đã có dữ liệu mẫu cho hóa đơn và chi tiết hóa đơn; các bảng tham chiếu khác đã có sẵn.

## Quy ước làm việc
- _workspace: nơi soạn tài liệu/kế hoạch/dữ liệu cá nhân.
- Push GitHub từ thư mục Qu-n-L-C-a-H-ng. Với chức năng mới, tạo branch dạng feature/x; tài liệu cơ bản (README) có thể đẩy trực tiếp main theo yêu cầu GV.

## License
- Dự án học tập; giấy phép sẽ được cập nhật sau khi hoàn thiện.
