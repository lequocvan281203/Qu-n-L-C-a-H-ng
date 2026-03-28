package com.cuahang.entity;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "NhanVien")
@Getter
@Setter
/**
 * Thực thể Nhân viên (bảng NhanVien).
 * Lưu thông tin nhân viên, có thể gắn 1 tài khoản đăng nhập và phát sinh nhiều hóa đơn.
 */
public class NhanVien {
    @Id
    @Column(name = "MaNV", length = 50)
    private String maNV;

    @Column(name = "HoTen", nullable = false)
    private String hoTen;

    @Column(name = "NgaySinh")
    @Temporal(TemporalType.DATE)
    private Date ngaySinh;

    @Column(name = "SoDT", length = 20)
    private String soDT;

    @Column(name = "ChucVu")
    private String chucVu;

    // Mối quan hệ 1-1 với Tài Khoản
    @OneToOne(mappedBy = "nhanVien", cascade = CascadeType.ALL)
    private TaiKhoan taiKhoan;

    // 1 Nhân Viên tạo Nhiều Hóa Đơn
    @OneToMany(mappedBy = "nhanVien")
    private List<HoaDon> danhSachHoaDon;
    
    public NhanVien() {}
}
