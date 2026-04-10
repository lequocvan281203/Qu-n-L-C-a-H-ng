USE quanlycuahang;

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE ChiTietHoaDon;
TRUNCATE TABLE HoaDon;
TRUNCATE TABLE TaiKhoan;
TRUNCATE TABLE SanPham;
TRUNCATE TABLE NhanVien;
TRUNCATE TABLE KhachHang;
TRUNCATE TABLE NhaCungCap;
TRUNCATE TABLE LoaiSanPham;
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
('NV03', 'Lê Minh Kho', '2001-11-05', '0903000003', 'Thủ kho'),
('NV04', 'Phạm Thu Ngân', '2002-09-09', '0904000004', 'Thu ngân'),
('NV05', 'Đặng Minh Quản', '2000-02-02', '0905000005', 'Quản lý ca'),
('NV06', 'Ngô Hồng Bán', '2003-12-12', '0906000006', 'Bán hàng');

INSERT INTO TaiKhoan(Username, Password, Quyen, MaNV) VALUES
('admin', '123456', 'Admin', 'NV01'),
('admin2', '123456', 'Admin', 'NV05'),
('user', '123456', 'User', 'NV02'),
('user2', '123456', 'User', 'NV04'),
('user3', '123456', 'User', 'NV06');

INSERT INTO KhachHang(MaKH, TenKH, SoDT, DiemTichLuy) VALUES
('KH01', 'Khách Demo', '0900000000', 0),
('KH02', 'Phạm Anh', '0900000002', 15),
('KH03', 'Vũ Bình', '0900000003', 5),
('KH04', 'Hoàng Cúc', '0900000004', 30),
('KH05', 'Đỗ Duy', '0900000005', 0),
('KH06', 'Nguyễn Hà', '0900000006', 8),
('KH07', 'Lý Khang', '0900000007', 22),
('KH08', 'Trương Lan', '0900000008', 3),
('KH09', 'Tạ Minh', '0900000009', 12),
('KH10', 'Bùi My', '0900000010', 40);

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
('SP10', 'Khăn giấy bỏ túi', 7000, 'Gói', 150, 'L005', 'NCC01'),
('SP11', 'Nước tăng lực 250ml', 15000, 'Lon', 55, 'L001', 'NCC02'),
('SP12', 'Snack khoai tây', 11000, 'Gói', 65, 'L002', 'NCC03'),
('SP13', 'Socola đen', 22000, 'Thanh', 45, 'L002', 'NCC01'),
('SP14', 'Cơm cháy chà bông', 25000, 'Gói', 35, 'L003', 'NCC02'),
('SP15', 'Muối i-ốt', 8000, 'Gói', 90, 'L004', 'NCC01'),
('SP16', 'Nước rửa chén 500ml', 29000, 'Chai', 18, 'L005', 'NCC03'),
('SP17', 'Nước suối 1.5L', 14000, 'Chai', 50, 'L001', 'NCC01'),
('SP18', 'Nước ngọt cam 330ml', 12000, 'Lon', 90, 'L001', 'NCC02'),
('SP19', 'Nước ngọt chanh 330ml', 12000, 'Lon', 75, 'L001', 'NCC02'),
('SP20', 'Trà xanh 450ml', 13000, 'Chai', 60, 'L001', 'NCC03'),
('SP21', 'Sữa tươi 180ml', 9000, 'Hộp', 40, 'L001', 'NCC03'),
('SP22', 'Sữa đậu nành 200ml', 10000, 'Hộp', 55, 'L001', 'NCC03'),
('SP23', 'Nước tăng lực 330ml', 18000, 'Lon', 35, 'L001', 'NCC02'),
('SP24', 'Cà phê lon 240ml', 15000, 'Lon', 28, 'L001', 'NCC02'),
('SP25', 'Trà đào 500ml', 16000, 'Chai', 33, 'L001', 'NCC03'),
('SP26', 'Nước yến 190ml', 28000, 'Lon', 22, 'L001', 'NCC01'),
('SP27', 'Nước nha đam 500ml', 17000, 'Chai', 26, 'L001', 'NCC01'),
('SP28', 'Nước ép cam 1L', 42000, 'Hộp', 14, 'L001', 'NCC03'),
('SP29', 'Nước ép táo 1L', 42000, 'Hộp', 12, 'L001', 'NCC03'),
('SP30', 'Soda chanh 330ml', 14000, 'Lon', 10, 'L001', 'NCC02'),
('SP31', 'Trà sữa 350ml', 20000, 'Chai', 16, 'L001', 'NCC03'),
('SP32', 'Nước khoáng có ga 500ml', 11000, 'Chai', 18, 'L001', 'NCC01'),
('SP33', 'Nước uống vitamin C 330ml', 16000, 'Lon', 20, 'L001', 'NCC02'),
('SP34', 'Bánh gạo rong biển', 18000, 'Gói', 45, 'L002', 'NCC01'),
('SP35', 'Bánh bông lan mini', 20000, 'Hộp', 38, 'L002', 'NCC01'),
('SP36', 'Bánh quy socola', 17000, 'Gói', 42, 'L002', 'NCC01'),
('SP37', 'Kẹo dẻo trái cây', 12000, 'Gói', 50, 'L002', 'NCC02'),
('SP38', 'Kẹo sữa', 15000, 'Gói', 36, 'L002', 'NCC02'),
('SP39', 'Kẹo chocolate viên', 25000, 'Gói', 18, 'L002', 'NCC03'),
('SP40', 'Snack bắp rang', 10000, 'Gói', 60, 'L002', 'NCC03'),
('SP41', 'Snack tôm cay', 12000, 'Gói', 40, 'L002', 'NCC03'),
('SP42', 'Bánh tráng nướng', 16000, 'Gói', 25, 'L002', 'NCC02'),
('SP43', 'Hạt hướng dương', 22000, 'Gói', 28, 'L002', 'NCC01'),
('SP44', 'Hạt điều rang muối', 45000, 'Gói', 15, 'L002', 'NCC01'),
('SP45', 'Socola sữa', 24000, 'Thanh', 20, 'L002', 'NCC03'),
('SP46', 'Bánh que phô mai', 19000, 'Hộp', 30, 'L002', 'NCC02'),
('SP47', 'Kẹo the mát', 9000, 'Gói', 55, 'L002', 'NCC02'),
('SP48', 'Bánh pía', 28000, 'Hộp', 12, 'L002', 'NCC01'),
('SP49', 'Bánh mì ngọt', 12000, 'Cái', 22, 'L002', 'NCC03'),
('SP50', 'Bánh quy yến mạch', 26000, 'Gói', 14, 'L002', 'NCC01'),
('SP51', 'Mì gói tôm chua cay', 6500, 'Gói', 240, 'L003', 'NCC03'),
('SP52', 'Mì gói hải sản', 6500, 'Gói', 230, 'L003', 'NCC03'),
('SP53', 'Mì ly bò hầm', 17000, 'Ly', 85, 'L003', 'NCC02'),
('SP54', 'Cháo gói thịt bằm', 14000, 'Gói', 60, 'L003', 'NCC02'),
('SP55', 'Phở ăn liền', 18000, 'Gói', 55, 'L003', 'NCC03'),
('SP56', 'Bún ăn liền', 18000, 'Gói', 52, 'L003', 'NCC03'),
('SP57', 'Cơm hộp tự sưởi', 45000, 'Hộp', 20, 'L003', 'NCC01'),
('SP58', 'Xúc xích tiệt trùng', 12000, 'Cây', 90, 'L003', 'NCC01'),
('SP59', 'Chả cá viên', 26000, 'Gói', 35, 'L003', 'NCC02'),
('SP60', 'Cá hộp sốt cà', 32000, 'Hộp', 18, 'L003', 'NCC02'),
('SP61', 'Pate gan', 18000, 'Hộp', 25, 'L003', 'NCC01'),
('SP62', 'Mì ý sốt bò', 38000, 'Hộp', 12, 'L003', 'NCC03'),
('SP63', 'Bánh bao trứng cút', 17000, 'Cái', 30, 'L003', 'NCC02'),
('SP64', 'Cơm cháy nước mắm', 22000, 'Gói', 26, 'L003', 'NCC02'),
('SP65', 'Rong biển ăn liền', 16000, 'Gói', 40, 'L003', 'NCC01'),
('SP66', 'Ngũ cốc ăn sáng', 48000, 'Hộp', 9, 'L003', 'NCC03'),
('SP67', 'Bánh tráng cuốn', 18000, 'Gói', 16, 'L003', 'NCC01'),
('SP68', 'Đường cát 1kg', 28000, 'Gói', 40, 'L004', 'NCC01'),
('SP69', 'Bột ngọt 400g', 22000, 'Gói', 35, 'L004', 'NCC01'),
('SP70', 'Hạt nêm 400g', 26000, 'Gói', 28, 'L004', 'NCC02'),
('SP71', 'Nước tương 500ml', 24000, 'Chai', 22, 'L004', 'NCC02'),
('SP72', 'Tương ớt 250g', 18000, 'Chai', 26, 'L004', 'NCC03'),
('SP73', 'Tương cà 250g', 18000, 'Chai', 18, 'L004', 'NCC03'),
('SP74', 'Dầu hào 510g', 38000, 'Chai', 14, 'L004', 'NCC03'),
('SP75', 'Giấm gạo 500ml', 20000, 'Chai', 20, 'L004', 'NCC02'),
('SP76', 'Tiêu xay', 22000, 'Lọ', 15, 'L004', 'NCC01'),
('SP77', 'Ớt bột', 18000, 'Gói', 16, 'L004', 'NCC01'),
('SP78', 'Bột nghệ', 16000, 'Gói', 18, 'L004', 'NCC01'),
('SP79', 'Bột canh', 9000, 'Gói', 60, 'L004', 'NCC02'),
('SP80', 'Sa tế 90g', 15000, 'Hũ', 24, 'L004', 'NCC03'),
('SP81', 'Nước mắm 750ml', 52000, 'Chai', 10, 'L004', 'NCC03'),
('SP82', 'Dầu ăn 2L', 98000, 'Chai', 8, 'L004', 'NCC02'),
('SP83', 'Nước cốt dừa 400ml', 28000, 'Lon', 14, 'L004', 'NCC01'),
('SP84', 'Mắm tôm 350g', 32000, 'Hũ', 9, 'L004', 'NCC03'),
('SP85', 'Giấy vệ sinh cuộn', 26000, 'Lốc', 40, 'L005', 'NCC01'),
('SP86', 'Khăn ướt 20 tờ', 12000, 'Gói', 55, 'L005', 'NCC01'),
('SP87', 'Nước lau sàn 1L', 38000, 'Chai', 15, 'L005', 'NCC03'),
('SP88', 'Bột giặt 800g', 42000, 'Gói', 20, 'L005', 'NCC02'),
('SP89', 'Nước xả vải 1L', 52000, 'Chai', 12, 'L005', 'NCC02'),
('SP90', 'Kem đánh răng', 28000, 'Tuýp', 18, 'L005', 'NCC01'),
('SP91', 'Bàn chải đánh răng', 15000, 'Cái', 25, 'L005', 'NCC01'),
('SP92', 'Dao cạo râu', 22000, 'Cái', 14, 'L005', 'NCC02'),
('SP93', 'Dầu gội 180ml', 45000, 'Chai', 10, 'L005', 'NCC03'),
('SP94', 'Sữa tắm 180ml', 45000, 'Chai', 11, 'L005', 'NCC03'),
('SP95', 'Bông tẩy trang', 16000, 'Gói', 30, 'L005', 'NCC01'),
('SP96', 'Nước rửa tay 500ml', 35000, 'Chai', 13, 'L005', 'NCC03'),
('SP97', 'Nước rửa kính', 32000, 'Chai', 9, 'L005', 'NCC02'),
('SP98', 'Túi rác 1kg', 24000, 'Cuộn', 22, 'L005', 'NCC01'),
('SP99', 'Pin AA (vỉ 4)', 26000, 'Vỉ', 16, 'L005', 'NCC02'),
('SP100', 'Bật lửa', 7000, 'Cái', 35, 'L005', 'NCC01');

INSERT INTO HoaDon(MaHD, NgayLap, TongTien, TrangThai, MaKH, MaNV) VALUES
('HD001', NOW() - INTERVAL 3 DAY, 0, 'Complete', 'KH02', 'NV02'),
('HD002', NOW() - INTERVAL 2 DAY, 0, 'Complete', 'KH03', 'NV02'),
('HD003', NOW() - INTERVAL 1 DAY, 0, 'Pending',  'KH01', 'NV02'),
('HD004', NOW(), 0, 'Complete', 'KH04', 'NV02'),
('HD005', NOW() - INTERVAL 7 DAY, 0, 'Complete', 'KH06', 'NV04'),
('HD006', NOW() - INTERVAL 6 DAY, 0, 'Complete', 'KH07', 'NV04'),
('HD007', NOW() - INTERVAL 5 DAY, 0, 'Complete', 'KH08', 'NV06'),
('HD008', NOW() - INTERVAL 4 DAY, 0, 'Pending',  'KH09', 'NV06'),
('HD009', NOW() - INTERVAL 2 HOUR, 0, 'Complete', 'KH10', 'NV02'),
('HD010', NOW() - INTERVAL 10 DAY, 0, 'Complete', 'KH05', 'NV04'),
('HD011', NOW() - INTERVAL 9 DAY, 0, 'Complete', 'KH02', 'NV06'),
('HD012', NOW() - INTERVAL 8 DAY, 0, 'Complete', 'KH03', 'NV02'),
('HD013', NOW() - INTERVAL 7 DAY, 0, 'Complete', 'KH04', 'NV02'),
('HD014', NOW() - INTERVAL 6 DAY, 0, 'Complete', 'KH06', 'NV06'),
('HD015', NOW() - INTERVAL 5 DAY, 0, 'Complete', 'KH07', 'NV04'),
('HD016', NOW() - INTERVAL 4 DAY, 0, 'Complete', 'KH08', 'NV02'),
('HD017', NOW() - INTERVAL 3 DAY, 0, 'Complete', 'KH09', 'NV02'),
('HD018', NOW() - INTERVAL 2 DAY, 0, 'Pending',  'KH10', 'NV06'),
('HD019', NOW() - INTERVAL 1 DAY, 0, 'Complete', 'KH01', 'NV04'),
('HD020', NOW() - INTERVAL 12 HOUR, 0, 'Complete', 'KH02', 'NV02'),
('HD021', NOW() - INTERVAL 8 HOUR, 0, 'Complete', 'KH03', 'NV02'),
('HD022', NOW() - INTERVAL 6 HOUR, 0, 'Complete', 'KH04', 'NV06'),
('HD023', NOW() - INTERVAL 4 HOUR, 0, 'Pending',  'KH05', 'NV06'),
('HD024', NOW() - INTERVAL 2 HOUR, 0, 'Complete', 'KH06', 'NV04'),
('HD025', NOW() - INTERVAL 1 HOUR, 0, 'Complete', 'KH07', 'NV02');

INSERT INTO ChiTietHoaDon(MaHD, MaSP, SoLuong, DonGia) VALUES
('HD001', 'SP01', 2, 8000),
('HD001', 'SP04', 1, 15000),
('HD001', 'SP06', 2, 16000),
('HD002', 'SP02', 1, 12000),
('HD002', 'SP07', 5, 6000),
('HD003', 'SP10', 3, 7000),
('HD004', 'SP03', 1, 18000),
('HD004', 'SP08', 1, 35000),
('HD004', 'SP09', 1, 52000),
('HD005', 'SP11', 2, 15000),
('HD005', 'SP12', 3, 11000),
('HD005', 'SP15', 1, 8000),
('HD006', 'SP01', 1, 8000),
('HD006', 'SP06', 1, 16000),
('HD006', 'SP14', 2, 25000),
('HD007', 'SP13', 1, 22000),
('HD007', 'SP02', 2, 12000),
('HD008', 'SP16', 1, 29000),
('HD008', 'SP10', 2, 7000),
('HD009', 'SP07', 10, 6000),
('HD009', 'SP04', 2, 15000),
('HD010', 'SP17', 2, 14000),
('HD010', 'SP34', 1, 18000),
('HD010', 'SP85', 1, 26000),
('HD011', 'SP18', 1, 12000),
('HD011', 'SP40', 2, 10000),
('HD011', 'SP51', 6, 6500),
('HD012', 'SP20', 1, 13000),
('HD012', 'SP44', 1, 45000),
('HD012', 'SP68', 1, 28000),
('HD013', 'SP23', 2, 18000),
('HD013', 'SP57', 1, 45000),
('HD013', 'SP90', 1, 28000),
('HD014', 'SP30', 3, 14000),
('HD014', 'SP42', 1, 16000),
('HD014', 'SP74', 1, 38000),
('HD015', 'SP62', 1, 38000),
('HD015', 'SP72', 1, 18000),
('HD015', 'SP86', 2, 12000),
('HD016', 'SP53', 2, 17000),
('HD016', 'SP36', 1, 17000),
('HD016', 'SP96', 1, 35000),
('HD017', 'SP01', 1, 8000),
('HD017', 'SP07', 8, 6000),
('HD017', 'SP12', 2, 11000),
('HD018', 'SP66', 1, 48000),
('HD018', 'SP28', 1, 42000),
('HD018', 'SP97', 1, 32000),
('HD019', 'SP29', 1, 42000),
('HD019', 'SP48', 1, 28000),
('HD019', 'SP83', 1, 28000),
('HD020', 'SP07', 12, 6000),
('HD020', 'SP41', 2, 12000),
('HD020', 'SP90', 1, 28000),
('HD021', 'SP06', 2, 16000),
('HD021', 'SP55', 1, 18000),
('HD021', 'SP89', 1, 52000),
('HD022', 'SP11', 3, 15000),
('HD022', 'SP33', 2, 16000),
('HD022', 'SP95', 1, 16000),
('HD023', 'SP82', 1, 98000),
('HD023', 'SP84', 1, 32000),
('HD023', 'SP98', 1, 24000),
('HD024', 'SP16', 2, 29000),
('HD024', 'SP87', 1, 38000),
('HD024', 'SP91', 2, 15000),
('HD025', 'SP07', 15, 6000),
('HD025', 'SP04', 3, 15000),
('HD025', 'SP12', 4, 11000);

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
