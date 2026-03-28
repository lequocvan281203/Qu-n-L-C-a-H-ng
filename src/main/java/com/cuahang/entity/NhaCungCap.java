package com.cuahang.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "NhaCungCap")
public class NhaCungCap {
    @Id
    @Column(name = "MaNCC", length = 50)
    private String maNCC;

    @Column(name = "TenNCC", nullable = false)
    private String tenNCC;

    @Column(name = "DiaChi")
    private String diaChi;

    @Column(name = "Email")
    private String email;

    // 1 Nhà Cung Cấp có Nhiều Sản Phẩm
    @OneToMany(mappedBy = "nhaCungCap", cascade = CascadeType.ALL)
    private List<SanPham> danhSachSanPham;
    
    public NhaCungCap() {}
}