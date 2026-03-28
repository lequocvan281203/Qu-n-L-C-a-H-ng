package com.cuahang.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "LoaiSanPham")
public class LoaiSanPham {
    @Id
    @Column(name = "MaLoai", length = 50)
    private String maLoai;

    @Column(name = "TenLoai", nullable = false)
    private String tenLoai;

    // 1 Loại Sản Phẩm có Nhiều Sản Phẩm
    @OneToMany(mappedBy = "loaiSanPham", cascade = CascadeType.ALL)
    private List<SanPham> danhSachSanPham;
    
    public LoaiSanPham() {}
}