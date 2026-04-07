SET FOREIGN_KEY_CHECKS = 0;

DELETE FROM ChiTietHoaDon;
DELETE FROM HoaDon;
DELETE FROM TaiKhoan;
DELETE FROM SanPham;
DELETE FROM LoaiSanPham;
DELETE FROM NhaCungCap;
DELETE FROM KhachHang;
DELETE FROM NhanVien;

SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO LoaiSanPham (MaLoai, TenLoai) VALUES
('L001', 'Đồ uống'),
('L002', 'Bánh kẹo'),
('L003', 'Gia dụng');

INSERT INTO NhaCungCap (MaNCC, TenNCC, DiaChi, Email) VALUES
('NCC01', 'Nhà cung cấp Demo', 'TP.HCM', 'demo@ncc.local'),
('NCC02', 'Nhà cung cấp Miền Bắc', 'Hà Nội', 'ncc2@ncc.local');

INSERT INTO NhanVien (MaNV, HoTen, NgaySinh, SoDT, ChucVu) VALUES
('NV01', 'Nhân viên Demo', '2000-01-01', '0911111111', 'Thu ngân'),
('NV02', 'Quản trị Demo', '1999-05-12', '0922222222', 'Quản lý');

INSERT INTO TaiKhoan (Username, Password, Quyen, MaNV) VALUES
('admin', '123456', 'Admin', NULL),
('user1', '123456', 'User', 'NV01');

INSERT INTO KhachHang (MaKH, TenKH, SoDT, DiemTichLuy) VALUES
('KH01', 'Khách Demo', '0900000000', 0),
('KH02', 'Khách VIP', '0900000002', 120),
('KH03', 'Khách lẻ', '0900000003', 5);

INSERT INTO SanPham (MaSP, TenSP, GiaBan, DonViTinh, SoLuongTon, MaLoai, MaNCC) VALUES
('SP01', 'Nước suối 500ml', 8000, 'Chai', 50, 'L001', 'NCC01'),
('SP02', 'Trà xanh 0 độ', 10000, 'Chai', 8, 'L001', 'NCC01'),
('SP03', 'Bánh quy bơ', 15000, 'Gói', 40, 'L002', 'NCC01'),
('SP04', 'Kẹo bạc hà', 12000, 'Gói', 3, 'L002', 'NCC02'),
('SP05', 'Nước rửa chén', 28000, 'Chai', 15, 'L003', 'NCC02');

INSERT INTO HoaDon (MaHD, NgayLap, TongTien, TrangThai, MaKH, MaNV) VALUES
('HD01', '2026-03-28 10:15:00', 0, 'Complete', 'KH01', 'NV01'),
('HD02', '2026-03-29 18:45:00', 0, 'Complete', 'KH02', 'NV01'),
('HD03', '2026-03-30 09:30:00', 0, 'Pending', NULL, 'NV02');

INSERT INTO ChiTietHoaDon (MaHD, MaSP, SoLuong, DonGia) VALUES
('HD01', 'SP01', 2, 8000),
('HD01', 'SP03', 1, 15000),
('HD02', 'SP02', 3, 10000),
('HD02', 'SP04', 2, 12000);

UPDATE HoaDon h
JOIN (
  SELECT MaHD, SUM(SoLuong * DonGia) AS Tong
  FROM ChiTietHoaDon
  GROUP BY MaHD
) t ON t.MaHD = h.MaHD
SET h.TongTien = t.Tong;

