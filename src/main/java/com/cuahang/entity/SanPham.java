package com.cuahang.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "SanPham")
public class SanPham {
    @Id
    @Column(name = "MaSP", length = 50)
    private String maSP;

    @Column(name = "TenSP", nullable = false)
    private String tenSP;

    @Column(name = "GiaBan")
    private double giaBan;

    @Column(name = "DonViTinh", length = 50)
    private String donViTinh;

    @Column(name = "SoLuongTon")
    private int soLuongTon = 0;

    // Khóa ngoại nối về Loại Sản Phẩm
    @ManyToOne
    @JoinColumn(name = "MaLoai")
    private LoaiSanPham loaiSanPham;

    // Khóa ngoại nối về Nhà Cung Cấp
    @ManyToOne
    @JoinColumn(name = "MaNCC")
    private NhaCungCap nhaCungCap;
    
    public SanPham() {}
}