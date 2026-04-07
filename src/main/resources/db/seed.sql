USE quanlycuahang;

SET FOREIGN_KEY_CHECKS = 0;
DELETE FROM ChiTietHoaDon;
DELETE FROM HoaDon;
DELETE FROM TaiKhoan;
DELETE FROM SanPham;
DELETE FROM NhanVien;
DELETE FROM KhachHang;
DELETE FROM NhaCungCap;
DELETE FROM LoaiSanPham;
SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO LoaiSanPham(MaLoai, TenLoai) VALUES
('L001', 'Đồ uống'),
('L002', 'Bánh kẹo'),
('L003', 'Mì & Thực phẩm'),
('L004', 'Gia vị'),
('L005', 'Đồ dùng');

INSERT INTO NhaCungCap(MaNCC, TenNCC, DiaChi, Email) VALUES
('NCC01', 'Nhà cung cấp Demo', 'TP.HCM', 'demo@ncc.local'),
('NCC02', 'VietFood Supply', 'Hà Nội', 'contact@vietfood.local'),
('NCC03', 'SunMart Partners', 'Đà Nẵng', 'hello@sunmart.local');

INSERT INTO NhanVien(MaNV, HoTen, NgaySinh, SoDT, ChucVu) VALUES
('NV01', 'Nguyễn Văn Admin', '2002-01-15', '0901000001', 'Quản lý'),
('NV02', 'Trần Thị User', '2003-06-20', '0902000002', 'Thu ngân'),
('NV03', 'Lê Minh Kho', '2001-11-05', '0903000003', 'Thủ kho');

INSERT INTO TaiKhoan(Username, Password, Quyen, MaNV) VALUES
('admin', '123456', 'Admin', 'NV01'),
('user', '123456', 'User', 'NV02');

INSERT INTO KhachHang(MaKH, TenKH, SoDT, DiemTichLuy) VALUES
('KH01', 'Khách Demo', '0900000000', 0),
('KH02', 'Phạm Anh', '0900000002', 15),
('KH03', 'Vũ Bình', '0900000003', 5),
('KH04', 'Hoàng Cúc', '0900000004', 30),
('KH05', 'Đỗ Duy', '0900000005', 0);

INSERT INTO SanPham(MaSP, TenSP, GiaBan, DonViTinh, SoLuongTon, MaLoai, MaNCC) VALUES
('SP01', 'Nước suối 500ml', 8000, 'Chai', 120, 'L001', 'NCC01'),
('SP02', 'Nước ngọt cola 330ml', 12000, 'Lon', 80, 'L001', 'NCC02'),
('SP03', 'Trà sữa đóng chai', 18000, 'Chai', 60, 'L001', 'NCC03'),
('SP04', 'Bánh quy bơ', 15000, 'Gói', 70, 'L002', 'NCC01'),
('SP05', 'Kẹo bạc hà', 9000, 'Gói', 40, 'L002', 'NCC02'),
('SP06', 'Mì ly hải sản', 16000, 'Ly', 90, 'L003', 'NCC02'),
('SP07', 'Mì gói bò cay', 6000, 'Gói', 200, 'L003', 'NCC03'),
('SP08', 'Nước mắm 500ml', 35000, 'Chai', 30, 'L004', 'NCC03'),
('SP09', 'Dầu ăn 1L', 52000, 'Chai', 25, 'L004', 'NCC02'),
('SP10', 'Khăn giấy bỏ túi', 7000, 'Gói', 150, 'L005', 'NCC01');

INSERT INTO HoaDon(MaHD, NgayLap, TongTien, TrangThai, MaKH, MaNV) VALUES
('HD001', NOW() - INTERVAL 3 DAY, 0, 'Complete', 'KH02', 'NV02'),
('HD002', NOW() - INTERVAL 2 DAY, 0, 'Complete', 'KH03', 'NV02'),
('HD003', NOW() - INTERVAL 1 DAY, 0, 'Pending',  'KH01', 'NV02'),
('HD004', NOW(), 0, 'Complete', 'KH04', 'NV02');

INSERT INTO ChiTietHoaDon(MaHD, MaSP, SoLuong, DonGia) VALUES
('HD001', 'SP01', 2, 8000),
('HD001', 'SP04', 1, 15000),
('HD001', 'SP06', 2, 16000),
('HD002', 'SP02', 1, 12000),
('HD002', 'SP07', 5, 6000),
('HD003', 'SP10', 3, 7000),
('HD004', 'SP03', 1, 18000),
('HD004', 'SP08', 1, 35000),
('HD004', 'SP09', 1, 52000);

UPDATE HoaDon hd
JOIN (
  SELECT MaHD, SUM(SoLuong * DonGia) AS Total
  FROM ChiTietHoaDon
  GROUP BY MaHD
) t ON t.MaHD = hd.MaHD
SET hd.TongTien = t.Total;

UPDATE SanPham sp
JOIN (
  SELECT MaSP, SUM(SoLuong) AS Sold
  FROM ChiTietHoaDon ct
  JOIN HoaDon hd ON hd.MaHD = ct.MaHD
  WHERE hd.TrangThai IN ('Complete', 'Pending')
  GROUP BY MaSP
) x ON x.MaSP = sp.MaSP
SET sp.SoLuongTon = GREATEST(sp.SoLuongTon - x.Sold, 0);
