package com.cuahang.entity;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "HoaDon")
public class HoaDon {
    @Id
    @Column(name = "MaHD", length = 50)
    private String maHD;

    @Column(name = "NgayLap")
    @Temporal(TemporalType.TIMESTAMP)
    private Date ngayLap = new Date(); // Mặc định lấy giờ hiện tại

    @Column(name = "TongTien")
    private double tongTien = 0.0;

    @Column(name = "TrangThai", length = 50)
    private String trangThai = "Pending"; // Sẽ đổi thành 'Complete' khi hoàn tất

    @ManyToOne
    @JoinColumn(name = "MaKH")
    private KhachHang khachHang;

    @ManyToOne
    @JoinColumn(name = "MaNV")
    private NhanVien nhanVien;

    // 1 Hóa Đơn có Nhiều Chi Tiết Hóa Đơn
    @OneToMany(mappedBy = "hoaDon", cascade = CascadeType.ALL)
    private List<ChiTietHoaDon> danhSachChiTiet;
    
    public HoaDon() {}
}