package com.cuahang.service;

import com.cuahang.entity.NhaCungCap;
import com.cuahang.util.HibernateUtil;
import java.util.List;
import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class NhaCungCapService {
    public List<NhaCungCap> search(String keyword) {
        String kw = keyword == null ? "" : keyword.trim();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session
                .createQuery(
                    "from NhaCungCap n where n.maNCC like :kw or n.tenNCC like :kw or n.diaChi like :kw or n.email like :kw order by n.maNCC",
                    NhaCungCap.class
                )
                .setParameter("kw", "%" + kw + "%")
                .getResultList();
        }
    }

    public Optional<NhaCungCap> findById(String maNCC) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(NhaCungCap.class, maNCC));
        }
    }

    public void saveOrUpdate(NhaCungCap ncc) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(ncc);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    public void deleteById(String maNCC) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            NhaCungCap found = session.get(NhaCungCap.class, maNCC);
            if (found != null) session.remove(found);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }
}

