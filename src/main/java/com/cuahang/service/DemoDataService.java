package com.cuahang.service;

import com.cuahang.entity.KhachHang;
import com.cuahang.entity.LoaiSanPham;
import com.cuahang.entity.NhaCungCap;
import com.cuahang.entity.NhanVien;
import com.cuahang.entity.SanPham;
import com.cuahang.util.HibernateUtil;
import java.util.Date;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class DemoDataService {
    public boolean ensureDemoData() {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            Long productCount = session.createQuery("select count(sp) from SanPham sp", Long.class).getSingleResult();
            if (productCount != null && productCount > 0) {
                tx.commit();
                return false;
            }

            LoaiSanPham loai1 = new LoaiSanPham();
            loai1.setMaLoai("L001");
            loai1.setTenLoai("Đồ uống");

            LoaiSanPham loai2 = new LoaiSanPham();
            loai2.setMaLoai("L002");
            loai2.setTenLoai("Bánh kẹo");

            NhaCungCap ncc1 = new NhaCungCap();
            ncc1.setMaNCC("NCC01");
            ncc1.setTenNCC("Nhà cung cấp Demo");
            ncc1.setDiaChi("TP.HCM");
            ncc1.setEmail("demo@ncc.local");

            session.persist(loai1);
            session.persist(loai2);
            session.persist(ncc1);

            SanPham sp1 = new SanPham();
            sp1.setMaSP("SP01");
            sp1.setTenSP("Nước suối 500ml");
            sp1.setGiaBan(8000);
            sp1.setDonViTinh("Chai");
            sp1.setSoLuongTon(50);
            sp1.setLoaiSanPham(loai1);
            sp1.setNhaCungCap(ncc1);

            SanPham sp2 = new SanPham();
            sp2.setMaSP("SP02");
            sp2.setTenSP("Bánh quy bơ");
            sp2.setGiaBan(15000);
            sp2.setDonViTinh("Gói");
            sp2.setSoLuongTon(40);
            sp2.setLoaiSanPham(loai2);
            sp2.setNhaCungCap(ncc1);

            session.persist(sp1);
            session.persist(sp2);

            KhachHang kh = new KhachHang("KH01", "Khách Demo", "0900000000", 0);
            session.persist(kh);

            NhanVien nv = new NhanVien();
            nv.setMaNV("NV01");
            nv.setHoTen("Nhân viên Demo");
            nv.setNgaySinh(new Date());
            nv.setSoDT("0911111111");
            nv.setChucVu("Thu ngân");
            session.persist(nv);

            tx.commit();
            return true;
        } catch (RuntimeException e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }
}

