package com.cuahang.service;

import com.cuahang.entity.TaiKhoan;
import com.cuahang.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class BootstrapService {
    public boolean ensureDefaultAdmin() {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            Long count = session.createQuery("select count(tk) from TaiKhoan tk", Long.class).getSingleResult();
            if (count != null && count > 0) {
                tx.commit();
                return false;
            }

            TaiKhoan admin = new TaiKhoan();
            admin.setUsername("admin");
            admin.setPassword("123456");
            admin.setQuyen("Admin");
            admin.setNhanVien(null);

            session.persist(admin);
            tx.commit();
            return true;
        } catch (RuntimeException e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }
}

