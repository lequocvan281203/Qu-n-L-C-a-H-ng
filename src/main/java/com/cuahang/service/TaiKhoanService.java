package com.cuahang.service;

import com.cuahang.entity.NhanVien;
import com.cuahang.entity.TaiKhoan;
import com.cuahang.util.HibernateUtil;
import java.util.List;
import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class TaiKhoanService {
    public List<TaiKhoan> search(String keyword) {
        String kw = keyword == null ? "" : keyword.trim();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session
                .createQuery(
                    "from TaiKhoan tk where tk.username like :kw or tk.quyen like :kw order by tk.username",
                    TaiKhoan.class
                )
                .setParameter("kw", "%" + kw + "%")
                .getResultList();
        }
    }

    public Optional<TaiKhoan> findById(String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(TaiKhoan.class, username));
        }
    }

    public List<NhanVien> listNhanVien() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from NhanVien nv order by nv.maNV", NhanVien.class).getResultList();
        }
    }

    public void saveOrUpdate(TaiKhoan tk) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(tk);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    public void deleteById(String username) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            TaiKhoan found = session.get(TaiKhoan.class, username);
            if (found != null) session.remove(found);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }
}

