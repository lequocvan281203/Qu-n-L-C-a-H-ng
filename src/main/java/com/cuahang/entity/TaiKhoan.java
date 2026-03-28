package com.cuahang.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "TaiKhoan")
@Getter
@Setter
/**
 * Thực thể Tài khoản (bảng TaiKhoan).
 * Dùng để đăng nhập hệ thống và phân quyền (Admin/User).
 */
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
