package com.cuahang.service;

import com.cuahang.entity.ChiTietHoaDon;
import com.cuahang.entity.HoaDon;
import com.cuahang.entity.SanPham;
import com.cuahang.util.HibernateUtil;
import java.util.List;
import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class ChiTietHoaDonService {
    private final HoaDonService hoaDonService = new HoaDonService();

    public List<ChiTietHoaDon> searchByMaHD(String maHD) {
        String kw = maHD == null ? "" : maHD.trim();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session
                .createQuery(
                    "from ChiTietHoaDon ct where ct.hoaDon.maHD like :kw order by ct.maCTHD",
                    ChiTietHoaDon.class
                )
                .setParameter("kw", "%" + kw + "%")
                .getResultList();
        }
    }

    public Optional<ChiTietHoaDon> findById(int maCTHD) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(ChiTietHoaDon.class, maCTHD));
        }
    }

    public List<HoaDon> listHoaDon() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from HoaDon hd order by hd.ngayLap desc", HoaDon.class).getResultList();
        }
    }

    public List<SanPham> listSanPham() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from SanPham sp order by sp.maSP", SanPham.class).getResultList();
        }
    }

    public void create(ChiTietHoaDon ct) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(ct);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null) tx.rollback();
            throw e;
        }
        if (ct.getHoaDon() != null) hoaDonService.recalcTongTien(ct.getHoaDon().getMaHD());
    }

    public void update(ChiTietHoaDon ct) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(ct);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null) tx.rollback();
            throw e;
        }
        if (ct.getHoaDon() != null) hoaDonService.recalcTongTien(ct.getHoaDon().getMaHD());
    }

    public void deleteById(int maCTHD) {
        String maHD = null;
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            ChiTietHoaDon found = session.get(ChiTietHoaDon.class, maCTHD);
            if (found != null) {
                maHD = found.getHoaDon() != null ? found.getHoaDon().getMaHD() : null;
                session.remove(found);
            }
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null) tx.rollback();
            throw e;
        }
        if (maHD != null) hoaDonService.recalcTongTien(maHD);
    }
}

