package com.cuahang.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "TaiKhoan")
public class TaiKhoan {
    @Id
    @Column(name = "Username", length = 50)
    private String username;

    @Column(name = "Password", nullable = false)
    private String password;

    @Column(name = "Quyen", nullable = false)
    private String quyen; // Admin hoặc User

    // Khóa ngoại nối về Nhân Viên
    @OneToOne
    @JoinColumn(name = "MaNV")
    private NhanVien nhanVien;
    
    public TaiKhoan() {}
}