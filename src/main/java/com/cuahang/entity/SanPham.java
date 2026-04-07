package com.cuahang.entity;

import jakarta.persistence.*;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "SanPham")
@Getter
@Setter
/**
 * Thực thể Sản phẩm (bảng SanPham).
 * Lưu thông tin sản phẩm, giá bán, đơn vị tính, tồn kho và liên kết đến loại sản phẩm/nhà cung cấp.
 */
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
