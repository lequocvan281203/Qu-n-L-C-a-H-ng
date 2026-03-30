package com.cuahang.service;

import com.cuahang.entity.KhachHang;
import com.cuahang.util.HibernateUtil;
import java.util.List;
import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class KhachHangService {
    public List<KhachHang> search(String keyword) {
        String kw = keyword == null ? "" : keyword.trim();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session
                .createQuery(
                    "from KhachHang kh where kh.maKH like :kw or kh.tenKH like :kw or kh.soDT like :kw order by kh.maKH",
                    KhachHang.class
                )
                .setParameter("kw", "%" + kw + "%")
                .getResultList();
        }
    }

    public Optional<KhachHang> findById(String maKH) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(KhachHang.class, maKH));
        }
    }

    public void saveOrUpdate(KhachHang kh) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(kh);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    public void deleteById(String maKH) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            KhachHang found = session.get(KhachHang.class, maKH);
            if (found != null) session.remove(found);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }
}

