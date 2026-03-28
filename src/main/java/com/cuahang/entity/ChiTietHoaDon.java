package com.cuahang.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "ChiTietHoaDon", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"MaHD", "MaSP"}) // Đảm bảo không trùng sản phẩm trong 1 hóa đơn
})
public class ChiTietHoaDon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Tự động tăng (Auto-Increment)
    @Column(name = "MaCTHD")
    private int maCTHD;

    @Column(name = "SoLuong", nullable = false)
    private int soLuong;

    @Column(name = "DonGia", nullable = false)
    private double donGia;

    @ManyToOne
    @JoinColumn(name = "MaHD")
    private HoaDon hoaDon;

    @ManyToOne
    @JoinColumn(name = "MaSP")
    private SanPham sanPham;
    
    public ChiTietHoaDon() {}
}