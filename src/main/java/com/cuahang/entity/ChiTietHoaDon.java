package com.cuahang.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ChiTietHoaDon", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"MaHD", "MaSP"}) // Đảm bảo không trùng sản phẩm trong 1 hóa đơn
})
@Getter
@Setter
/**
 * Thực thể Chi tiết hóa đơn (bảng ChiTietHoaDon).
 * Mỗi dòng tương ứng 1 sản phẩm trong 1 hóa đơn, gồm số lượng và đơn giá.
 */
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
