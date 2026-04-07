package com.cuahang.service;

import com.cuahang.entity.NhanVien;
import com.cuahang.entity.TaiKhoan;
import com.cuahang.util.HibernateUtil;
import java.util.Date;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class BootstrapService {
    /**
     * Đảm bảo có sẵn tài khoản demo để trình diễn phân quyền.
     *
     * @return true nếu có tạo mới ít nhất 1 tài khoản
     */
    public boolean ensureDefaultAdmin() {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            boolean created = false;

            TaiKhoan admin = session.get(TaiKhoan.class, "admin");
            if (admin == null) {
                NhanVien nv01 = session.get(NhanVien.class, "NV01");
                if (nv01 == null) {
                    nv01 = new NhanVien();
                    nv01.setMaNV("NV01");
                    nv01.setHoTen("Nguyễn Văn Admin");
                    nv01.setNgaySinh(new Date());
                    nv01.setSoDT("0901000001");
                    nv01.setChucVu("Quản lý");
                    session.persist(nv01);
                }

                admin = new TaiKhoan();
                admin.setUsername("admin");
                admin.setPassword("123456");
                admin.setQuyen("Admin");
                admin.setNhanVien(nv01);
                session.persist(admin);
                created = true;
            }

            TaiKhoan user = session.get(TaiKhoan.class, "user");
            if (user == null) {
                NhanVien nv02 = session.get(NhanVien.class, "NV02");
                if (nv02 == null) {
                    nv02 = new NhanVien();
                    nv02.setMaNV("NV02");
                    nv02.setHoTen("Trần Thị User");
                    nv02.setNgaySinh(new Date());
                    nv02.setSoDT("0902000002");
                    nv02.setChucVu("Thu ngân");
                    session.persist(nv02);
                }

                user = new TaiKhoan();
                user.setUsername("user");
                user.setPassword("123456");
                user.setQuyen("User");
                user.setNhanVien(nv02);
                session.persist(user);
                created = true;
            }

            tx.commit();
            return created;
        } catch (RuntimeException e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }
}
