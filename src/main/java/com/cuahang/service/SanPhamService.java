package com.cuahang.service;

import com.cuahang.entity.LoaiSanPham;
import com.cuahang.entity.NhaCungCap;
import com.cuahang.entity.SanPham;
import com.cuahang.util.HibernateUtil;
import java.util.List;
import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class SanPhamService {
    public List<SanPham> search(String keyword) {
        String kw = keyword == null ? "" : keyword.trim();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session
                .createQuery(
                    "from SanPham sp where sp.maSP like :kw or sp.tenSP like :kw order by sp.maSP",
                    SanPham.class
                )
                .setParameter("kw", "%" + kw + "%")
                .getResultList();
        }
    }

    public Optional<SanPham> findById(String maSP) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(SanPham.class, maSP));
        }
    }

    public List<LoaiSanPham> listLoai() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from LoaiSanPham l order by l.maLoai", LoaiSanPham.class).getResultList();
        }
    }

    public List<NhaCungCap> listNcc() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from NhaCungCap n order by n.maNCC", NhaCungCap.class).getResultList();
        }
    }

    public void saveOrUpdate(SanPham sp) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(sp);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    public void deleteById(String maSP) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            SanPham found = session.get(SanPham.class, maSP);
            if (found != null) session.remove(found);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }
}

