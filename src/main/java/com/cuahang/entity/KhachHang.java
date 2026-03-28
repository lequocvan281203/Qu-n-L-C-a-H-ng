package com.cuahang.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;

@Entity
@Table(name = "KhachHang") // Tên bảng sẽ được tạo trong MySQL
public class KhachHang {

    @Id // Đánh dấu đây là Khóa chính (Primary Key)
    @Column(name = "MaKH", length = 50)
    private String maKH;

    @Column(name = "TenKH", nullable = false, length = 255)
    private String tenKH;

    @Column(name = "SoDT", length = 20)
    private String soDT;

    @Column(name = "DiemTichLuy", columnDefinition = "INT DEFAULT 0")
    private int diemTichLuy = 0;

    // --- MỐI QUAN HỆ: 1 Khách Hàng có Nhiều Hóa Đơn ---
    @OneToMany(mappedBy = "khachHang", cascade = CascadeType.ALL)
    private List<HoaDon> danhSachHoaDon;

    // --- Constructor Không Tham Số (Bắt buộc phải có cho Hibernate) ---
    public KhachHang() {
    }

    // --- Constructor Có Tham Số (Để dễ dàng tạo đối tượng sau này) ---
    public KhachHang(String maKH, String tenKH, String soDT, int diemTichLuy) {
        this.maKH = maKH;
        this.tenKH = tenKH;
        this.soDT = soDT;
        this.diemTichLuy = diemTichLuy;
    }

    // --- Getter và Setter ---
    public String getMaKH() {
        return maKH;
    }

    public void setMaKH(String maKH) {
        this.maKH = maKH;
    }

    public String getTenKH() {
        return tenKH;
    }

    public void setTenKH(String tenKH) {
        this.tenKH = tenKH;
    }

    public String getSoDT() {
        return soDT;
    }

    public void setSoDT(String soDT) {
        this.soDT = soDT;
    }

    public int getDiemTichLuy() {
        return diemTichLuy;
    }

    public void setDiemTichLuy(int diemTichLuy) {
        this.diemTichLuy = diemTichLuy;
    }

    public List<HoaDon> getDanhSachHoaDon() {
        return danhSachHoaDon;
    }

    public void setDanhSachHoaDon(List<HoaDon> danhSachHoaDon) {
        this.danhSachHoaDon = danhSachHoaDon;
    }
}